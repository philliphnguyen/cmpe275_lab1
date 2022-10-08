import java.util.List;

public interface iDirectoryListing {
    void refresh(boolean force);

    int maxCycle();

    List<String> getList();
}
