import java.io.Closeable;
import java.io.IOException;

public class StoreAppender implements iExcerptAppender, Closeable{
    private final SingleChronicleQueue queue;
    private final iWriteLock iWriteLock;
    SingleChronicleQueueStore store;
    private boolean closed;

    long lastPosition;
    private long positionOfHeader = 0;
    private long lastIndex = Long.MIN_VALUE;
    private int count = 0;

    StoreAppender(SingleChronicleQueue queue) {
        this.queue = queue;
        this.iWriteLock = queue.writeLock();
    }

    @Override
    public void writeBytes(byte[] bytes) {
        throwExceptionIfClosed();
        iWriteLock.lock();
        try {
           // TODO: to be implemented
        } finally {
            iWriteLock.unlock();
        }
    }

    @Override
    public long lastWrittenIndex() {
        if (lastIndex != Long.MIN_VALUE)
            return lastIndex;

        if (lastPosition == Long.MIN_VALUE) {
            throw new IllegalStateException("nothing has been appended, so there is no last index");
        }

        try {
            long index = store.sequenceForPosition(this, lastPosition, true);
            lastIndex(index);
            return index;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastIndex;
    }

    void lastIndex(long index) {
        this.lastIndex = index;
    }

    @Override
    public void close() throws IOException {
        try {
            this.close();
            this.closed = true;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public void throwExceptionIfClosed() {
        if (closed) {
            throw new IllegalStateException("This appender has been closed");
        }
    }
}
