public class StrongImageCache<T> implements ImageCache<T> {
    @Override
    public T get(Object key) {
        return null;
    }

    @Override
    public boolean put(Object key, T value) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {

    }
}
