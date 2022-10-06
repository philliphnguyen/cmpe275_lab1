import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SingleChronicleQueue implements Closeable {
    public static final String SUFFIX = ".cq4";

    File path;
    private final iWriteLock writeLock;
    private Long timeoutMS; // 10 seconds.
    private final Set<StoreAppender> closers = Collections.newSetFromMap(new IdentityHashMap<>());
    private final iDirectoryListing directoryListing;
    
    public SingleChronicleQueue(SingleChronicleQueueBuilder builder) {
        //Construction process from builder design pattern
        this.path = builder.path();
        this.writeLock = new TableStoreWriteLock(timeoutMS() * 3 / 2, path);
        this.directoryListing = new FileSystemDirectoryListing(path, getFileNames());
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

    public List<String> getFileNames() {
        List<String> fileNames = new ArrayList<String>();
        String[] files = path.list();
        Arrays.sort(files);

        for (String fileName: files) {
            if (fileName.endsWith(SingleChronicleQueue.SUFFIX)) {
                fileNames.add(fileName);
            }
        }

        return fileNames;
    }

    public int lastCycle() {
        return directoryListing.maxCycle();
    }

    public String toFileName(int cycle) {
        return directoryListing.getList().get(cycle);
    }
}
