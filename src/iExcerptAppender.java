
public interface iExcerptAppender extends iExcerptCommon<iExcerptAppender> {
    // use byte[] type instead of BytesStore
    void writeBytes(byte[] bytes);

    long lastWrittenIndex();

    // return the cycle this appender is on
    int cycle();

    // do we need to implement this to further improve the write performance?
    // default void preTouch() {
    // }
}
