import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class StoreAppender implements iExcerptAppender, Closeable{
    private final SingleChronicleQueue queue;
    private final iWriteLock iWriteLock;
    private File path;
    private File currentFile = null;
    SingleChronicleQueueStore store;
    private boolean closed;

    long lastPosition;
    private long positionOfHeader = 0;
    private long lastIndex = Long.MIN_VALUE;
    private int count = 0;

    StoreAppender(SingleChronicleQueue queue) {
        this.queue = queue;
        this.iWriteLock = queue.writeLock();
        this.path = queue.path;
    }

    @Override
    public void writeBytes(byte[] bytes) {
        throwExceptionIfClosed();
        iWriteLock.lock();
        try {
            currentFile(path);
           Files.write(currentFile.toPath(), bytes, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            iWriteLock.unlock();
        }
    }
    private File currentFile(File path) {
        if (currentFile == null) {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            currentFile = new File(path, "data-" + date + ".cq4");
        }
        return currentFile;
    }

    public void writeText(String text) {
        throwExceptionIfClosed();
        int len = text.length();
        String bs = Integer.toBinaryString(len * 8);    // convert text length to binary
        String first32 = padding(bs, 32);
        StringBuilder sb = new StringBuilder();
        sb.append(first32);
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            String binary = Integer.toBinaryString(c);
            sb.append(padding(binary, 8));
        }
        sb.append("\n");
        writeBytes(sb.toString().getBytes());
    }

    private String padding(String str, int len) {
        return String.format(String.format("%%%ds", len), str).replaceAll(" ", "0");
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
