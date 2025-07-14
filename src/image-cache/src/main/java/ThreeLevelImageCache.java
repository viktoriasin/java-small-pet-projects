public class ThreeLevelImageCache<T> implements ImageCache<T> {
    private final StrongImageCache<T> strongImageCache;
    private final SoftImageCache<T> softImageCache;
    private final WeakImageCache<T> weakImageCache;


    public ThreeLevelImageCache(int initialCapacity, int maxStrongCacheSize, int maxSoftCacheSize) {
        weakImageCache = new WeakImageCache<>(initialCapacity);
        softImageCache = new SoftImageCache<>(initialCapacity,  maxSoftCacheSize, weakImageCache);
        strongImageCache = new StrongImageCache<>(initialCapacity, maxStrongCacheSize, softImageCache);
    }

    @Override
    public T get(Object key) {
        if (strongImageCache.get(key) != null) {
            return strongImageCache.get(key);
        }

        if (softImageCache.get(key) != null) {
            T value = softImageCache.get(key);
            strongImageCache.put(key, value);
            softImageCache.remove(key);
            return value;
        }

        if (weakImageCache.get(key) != null) {
            T value = weakImageCache.get(key);
            strongImageCache.put(key, value);
            weakImageCache.remove(key);
            return value;
        }

        T loadedImage = (T) ImageLoader.load(key);
        put(key, loadedImage);
        return loadedImage;
    }

    @Override
    public boolean put(Object key, T value) {
        return strongImageCache.put(key, value);
    }

    @Override
    public boolean remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return strongImageCache.size() + softImageCache.size() + weakImageCache.size();
    }

    public int strongCacheSize() {
        return strongImageCache.size();
    }

    public int softCacheSize() {
        return softImageCache.size();
    }

    public int weakCacheSize() {
        return weakImageCache.size();
    }

    @Override
    public void clear() {
        strongImageCache.clear();
        softImageCache.clear();
        weakImageCache.clear();
    }
}
