public interface ImageCache<T> {
    T get(Object key);
    boolean put(Object key, T value);
    int size();
    void clear();
}
