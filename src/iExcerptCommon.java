import java.io.File;

// this is the generic interface for iExcerptCommonAppender and iExcerptCommonTailer
public interface iExcerptCommon<E extends iExcerptCommon<E>>  {
    default File currentFile() {
        return null;
    }

    default void sync() {
    }
}