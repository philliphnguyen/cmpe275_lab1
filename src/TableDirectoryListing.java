import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableDirectoryListing implements iDirectoryListing {
    private final File queueDir;
    private final List<String> fileNames;
    private int maxCycle;

    public TableDirectoryListing(File queueDir, List<String> fileNames) {
        this.queueDir = queueDir;
        this.fileNames = fileNames;
        this.maxCycle = fileNames.size();
    }

    @Override
    public void refresh(boolean force) {
        if (!force) {
            return;
        }

        String[] files = queueDir.list();
        Arrays.sort(files);

        fileNames.clear();

        if (files != null) {
            for (String fileName : files) {
                if (fileName.endsWith(SingleChronicleQueue.SUFFIX)) {
                    fileNames.add(fileName);
                }
            }
        }

        maxCycle = fileNames.size();
    }

    @Override
    public int maxCycle() {
        return maxCycle;
    }

    @Override
    public List<String> getList() {
        return fileNames;
    }
}