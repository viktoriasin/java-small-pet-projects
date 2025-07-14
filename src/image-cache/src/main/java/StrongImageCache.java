import java.util.LinkedHashMap;
import java.util.Map;

public class StrongImageCache<T> implements ImageCache<T> {

    private final LinkedHashMap<Object, T> cache;
    private final int capacity;

    public StrongImageCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity + 1, 1.1f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, T> eldest) {
                return size() > capacity;
            }
        };
    }

    @Override
    public T get(Object key) {
        return cache.get(key);
    }

    @Override
    public boolean put(Object key, T value) {
        return false;
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
