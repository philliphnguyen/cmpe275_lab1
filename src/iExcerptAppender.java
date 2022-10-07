
public interface iExcerptAppender extends iExcerptCommon<iExcerptAppender> {
    // use byte[] type instead of BytesStore
    void writeBytes(byte[] bytes);
}
