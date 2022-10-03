import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class SingleChronicleQueue implements Closeable {
    File path;
    private final iWriteLock writeLock;
    private Long timeoutMS; // 10 seconds.
    private final Set<StoreAppender> closers = Collections.newSetFromMap(new IdentityHashMap<>());
    
    public SingleChronicleQueue(SingleChronicleQueueBuilder builder) {
        //Construction process from builder design pattern
        this.path = builder.path();
        this.writeLock = new TableStoreWriteLock(timeoutMS() * 3 / 2);
    }

    public long timeoutMS() {
        return timeoutMS == null ? 10_000L : timeoutMS;
    }

    iWriteLock writeLock() {
        return writeLock;
    }

    public <T> void addCloseListener(StoreAppender key) {
        synchronized (closers) {
            if (!closers.isEmpty())
                closers.removeIf(StoreAppender::isClosed);
            closers.add(key);
        }
    }

    protected void performClose() throws IOException {
        synchronized (closers) {
            closers.forEach(t -> {
                try {
                    t.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            closers.clear();

            try {
                this.close();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        performClose();
    }

}
