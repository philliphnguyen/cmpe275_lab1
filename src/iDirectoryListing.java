import java.util.List;

public interface iDirectoryListing {
    void refresh();

    int maxCycle();

    List<String> getList();
}
