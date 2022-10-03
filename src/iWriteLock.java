import java.io.Closeable;
import java.util.function.LongConsumer;

// taken from the original source code
public interface iWriteLock extends Closeable {

    iWriteLock NO_OP = new iWriteLock() {

        @Override
        public void lock() {
        }

        @Override
        public void unlock() {
        }

        @Override
        public void close() {
        }

        @Override
        public boolean forceUnlockIfProcessIsDead() {
            return true;
        }

        @Override
        public boolean isLockedByCurrentProcess(LongConsumer notCurrentProcessConsumer) {
            return true;
        }
    };

    void lock();
    void unlock();
    void close();
    
    default boolean locked() {
        return false;
    }
    boolean forceUnlockIfProcessIsDead();
    boolean isLockedByCurrentProcess(LongConsumer notCurrentProcessConsumer);
}