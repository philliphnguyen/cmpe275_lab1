import java.io.File;

// should we include the impelementation of the classes in this project?

// import net.openhft.chronicle.core.io.Closeable;
// import net.openhft.chronicle.core.io.SingleThreadedChecked;

// this is the generic interface for iExcerptCommonAppender and iExcerptCommonTailer
public interface iExcerptCommon<E extends iExcerptCommon<E>>  {
    default File currentFile() {
        return null;
    }

    default void sync() {
    }
}