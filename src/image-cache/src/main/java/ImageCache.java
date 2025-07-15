public interface ImageCache<T> {
    T get(Object key);
    boolean put(Object key, T value); // TODO: boolean?

    void remove(Object key);
    int size();
    void clear();
}
