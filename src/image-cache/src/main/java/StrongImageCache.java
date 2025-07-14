import java.util.LinkedHashMap;
import java.util.Map;

public class StrongImageCache<T> implements ImageCache<T> {

    private final LinkedHashMap<Object, T> cache;

    public StrongImageCache(int capacity, int maxCacheSize, SoftImageCache<T> softImageCache) {

        this.cache = new LinkedHashMap<>(capacity + 1, 1.1f, true) {

            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, T> eldest) {
                if (size() > maxCacheSize) {
                    softImageCache.put(eldest.getKey(), eldest.getValue());
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public T get(Object key) {
        return cache.get(key);
    }

    @Override
    public boolean put(Object key, T value) {
        cache.put(key, value);
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
    public boolean remove(Object key) {
        cache.remove(key);
        return true;
    }
}
