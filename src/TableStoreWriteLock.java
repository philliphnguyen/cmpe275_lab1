import java.io.File;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;

public class TableStoreWriteLock implements iWriteLock{
    public static final String APPEND_LOCK_KEY = "chronicle.append.lock";
    private final long timeout;
    private Thread lockedByThread = null;
    protected static final long PID = getProcessId();
    public static final long UNLOCKED = 1L << 63;
    // check more about the AtomicLong class: https://tarunjain07.medium.com/notes-compare-and-swap-atomiclong-atomic-reference-5ac1815c1f64#:~:text=Compare%20and%20swap%20is%20a,variable%20for%20a%20new%20variable.
    protected final AtomicLong lock = new AtomicLong(UNLOCKED);
    protected final File path;

    public TableStoreWriteLock(long timeout, File path) {
        this.timeout = timeout;
        this.path = path;
    }

    @Override
    public void lock() {
        assert checkNotAlreadyLocked();
        long currentLockValue = 0;
        try {
            currentLockValue = lock.get();
            while (!lock.compareAndSet(UNLOCKED, PID)) {
                if (Thread.currentThread().isInterrupted())
                    throw new TimeoutException("Interrupted for the lock file:" + path);
                currentLockValue = lock.get();
            }

            //noinspection ConstantConditions,AssertWithSideEffects
            assert (lockedByThread = Thread.currentThread()) != null;

            // success
        } catch (TimeoutException e) {
            final String lockedBy = getLockedBy(currentLockValue);
            final String warningMsg = "Couldn't acquire write lock " +
                    "after " + timeout + " ms " +
                    "for the lock file:" + path + ". " +
                    "Lock was held by " + lockedBy;
            forceUnlock(currentLockValue);
            lock();
            throw new IllegalStateException(warningMsg, e);
        }
    }

    protected String getLockedBy(long value) {
        return value == Long.MIN_VALUE ? "unknown" :
                value == PID ? "me"
                        : Long.toString((int) value);
    }

    protected static long getProcessId() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }

    protected void forceUnlock(long value) {
        boolean unlocked = lock.compareAndSet(value, UNLOCKED);
        final String warningMsg = "Forced unlock for the " +
                        "lock file:" + path + ", " +
                        "unlocked: " + unlocked;
        System.err.println(warningMsg);
    }

    @Override
    public void unlock() {
        if (!lock.compareAndSet(PID, UNLOCKED)) {
            long value = lock.get();
            if (value == UNLOCKED)
            System.err.println("Write lock was already unlocked. For the path" + path);
            else
            System.err.println("Write lock was locked by someone else! For the path" + path + " " +
                        "by PID: " + getLockedBy(value));
        }
        lockedByThread = null;
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public boolean forceUnlockIfProcessIsDead() {
        return false;
    }

    @Override
    public boolean isLockedByCurrentProcess(LongConsumer notCurrentProcessConsumer) {
        final long pid = this.lock.get();
        // mask off thread (if used)
        int realPid = (int) pid;
        if (realPid == PID)
            return true;
        notCurrentProcessConsumer.accept(pid);
        return false;
    }

    private boolean checkNotAlreadyLocked() {
        if (!locked())
            return true;
        if (lockedByThread == null)
            return true;
        if (lockedByThread == Thread.currentThread())
            throw new AssertionError("Lock is already acquired by current thread.");
        return true;
    }
}
