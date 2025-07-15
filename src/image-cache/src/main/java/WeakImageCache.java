import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;

public class WeakImageCache<T> implements ImageCache<T> {
    private final HashMap<Object, WeakReference<T>> cache;

    public WeakImageCache(final int capacity) {
        this.cache = new HashMap<>(capacity, 0.75f);
    }

    @Override
    public T get(Object key) {
        if (cache.get(key) != null) {
            return cache.get(key).get();
        }
        return null;
    }

    @Override
    public boolean put(Object key, T value) {
        cache.put(key, new WeakReference<>(value));
        return true;
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public void remove(Object key) {
        cache.remove(key);
    }

    public Collection<WeakReference<T>> values() {
        return cache.values();
    }
}
