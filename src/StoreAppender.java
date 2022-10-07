import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;

public class StoreAppender implements iExcerptAppender, Closeable{
    private final iWriteLock iWriteLock;
    private File path;
    private File currentFile = null;
    private boolean closed;
    private long lastIndex = Long.MIN_VALUE;

    StoreAppender(SingleChronicleQueue queue) {
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
            try {
                RandomAccessFile raf = new RandomAccessFile(currentFile, "rw");
                int header = lastIndex > 0 ? (int) lastIndex : 0;
                raf.seek(header);
                raf.write("1".getBytes(), 0, 1);
                raf.close();
                lastIndex = header + bytes.length;
            } catch (IOException e) {
                e.printStackTrace();
            }
            iWriteLock.unlock();
        }
    }

    private File currentFile(File path) throws IOException {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        String fileName = "data-" + date + ".cq4";
        Path currentPath = Paths.get(path + "/" + fileName);
        if (!Files.exists(currentPath)) {
            Files.createFile(currentPath);
            currentFile = currentPath.toFile();
            System.err.println("Current file is newly created");
        } else {
            System.err.println("Current file already exists");
            currentFile = currentPath.toFile();
            lastIndex = Files.size(currentPath);
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
    public void close() throws IOException {
        // System.out.println("Closing appender");
        if (this.closed) 
            return;
        try {
            iWriteLock.unlock();
        } catch (Exception e) {
            System.err.println("Error closing appender");
        } finally {
            this.closed = true;
            System.out.println("Appender closed");
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
