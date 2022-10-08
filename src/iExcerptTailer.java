public interface iExcerptTailer extends iExcerptCommon<iExcerptTailer> {
    long lastReadIndex();

    int cycle();
}
