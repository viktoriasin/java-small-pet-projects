import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class SoftImageCache<T> implements ImageCache<T> {

    private final LinkedHashMap<Object, SoftReference<T>> cache;

    public SoftImageCache(int capacity, int maxCacheSize, WeakImageCache<T> weakImageCache) {
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, SoftReference<T>> eldest) {
                if (size() > maxCacheSize) {
                    weakImageCache.put(eldest.getKey(), eldest.getValue().get());
                    return true;
                }
                return false;
            }
        };
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
        cache.put(key, new SoftReference<>(value));
        return true;
    }

    @Override
    public void remove(Object key) {
        cache.remove(key);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void clear() {
        cache.clear();
    }

    public Collection<SoftReference<T>> values() {
        return cache.values();
    }
}
