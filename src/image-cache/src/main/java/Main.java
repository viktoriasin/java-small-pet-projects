import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    /**
     * Класс для сравнения производительности трехуровневого кеша и обычного кеша.
     * Сравнение производилось путем замера времени работы кешей, а также объему потребляемой памяти.
     *
     * Чтобы сравнение было более достоверным, сначала выполним прогрев JVM.
     */

    private static final List<byte[]> images = new ArrayList<>();

    private static final int IMAGES_COUNT = 200;
    private static final int WARM_UP_ITERATIONS = 1000;
    private static final int MEASUREMENT_ITERATIONS = 1000;
    private static final int STRONG_CACHE_SIZE = 20;
    private static final int SOFT_CACHE_SIZE = 40;
    private static final int INITIAL_CAPACITY = 10;
    public static final float LOAD_FACTOR = 1.1f;
    public static final float TEMPORARY_IMAGE_REFERENCE_HOLD_COUNT = 10; // регулирует, сколько мы будем держать сильных ссылок, чтобы сымитировать работу реального приложения


    public static void main(String[] args) {
        prepareImages();
        warmUp();
        testThreeLevelCache();
        testSimpleCache();
    }

    private static void prepareImages() {
        Random random = new Random();
        for (int i = 0; i < IMAGES_COUNT; i++) {
            byte[] img = new byte[random.nextInt(1024)];
            random.nextBytes(img);
            images.add(img);
        }
    }

    private static void warmUp() {
        // Прогреваем виртуальную машину и JIT-компилятор
        ThreeLevelImageCache<byte[]> cache = new ThreeLevelImageCache<>(INITIAL_CAPACITY + 1, STRONG_CACHE_SIZE, SOFT_CACHE_SIZE);

        // Выполняем прогрев через цикл простых операций
        for (int i = 0; i < IMAGES_COUNT; i++) {
            cache.put(i, images.get(i));
        }

        for (int i = 0; i < WARM_UP_ITERATIONS; i++) {
            cache.get(images.get(randomImageIndex())); // Выполняем хаотичные обращения
        }

        // Завершаем прогон принудительной сборкой мусора
        System.gc();
    }

    private static void testThreeLevelCache() {

        HashMap<Object, byte[]> imageTemporaryStrongReferenceHolder = new LinkedHashMap<>(INITIAL_CAPACITY + 1, LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, byte[]> eldest) {
                return size() > TEMPORARY_IMAGE_REFERENCE_HOLD_COUNT;
            }
        };

        long startTime = System.nanoTime();

        ThreeLevelImageCache<byte[]> threeLevelImageCache = new ThreeLevelImageCache<>(INITIAL_CAPACITY, STRONG_CACHE_SIZE, SOFT_CACHE_SIZE);

        for (int i = 0; i < IMAGES_COUNT; i++) {
            threeLevelImageCache.put(i, images.get(i));
        }

        // Повторный доступ к изображениям
        for (int iteration = 0; iteration < 10; iteration++) {
            for (int i = 0; i < MEASUREMENT_ITERATIONS; i += 10) {
                int index;
                if (i % 7 == 0) {
                    // получаем индекс изображения, которое еще не было в кеше
                    index = randomNewImageToLoadIndex();
                } else {
                    // получаем индекс изображения, которое ранее мы добавляли сами в кеш (при этом, оттуда оно могло уже удалиться)
                    index = randomImageIndex();
                }

                byte[] bytes = threeLevelImageCache.get(index);
                imageTemporaryStrongReferenceHolder.put(index, bytes);
            }
        }

        long endTime = System.nanoTime();
        double durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.printf("Трихуровневый кэш: %.2f мс\n", durationMs);

    }

    private static void testSimpleCache() {

        HashMap<Object, byte[]> imageHolder = new LinkedHashMap<>(INITIAL_CAPACITY + 1, LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, byte[]> eldest) {
                return size() > TEMPORARY_IMAGE_REFERENCE_HOLD_COUNT;
            }
        };

        LinkedHashMap<Integer, byte[]> singleLevelCache = new LinkedHashMap<>(INITIAL_CAPACITY + 1, LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, byte[]> eldest) {
                return size() > IMAGES_COUNT;
            }
        };

        long startTime = System.nanoTime();


        for (int i = 0; i < IMAGES_COUNT; i++) {
            singleLevelCache.put(i, images.get(i));
        }

        // Повторный доступ к изображениям
        for (int iteration = 0; iteration < 10; iteration++) {
            for (int i = 0; i < MEASUREMENT_ITERATIONS; i += 10) {
                int index;
                if (i % 7 == 0) {
                    index = randomNewImageToLoadIndex();
                    Object load = ImageLoader.load(index);
                    singleLevelCache.put(index, (byte[]) load);
                    imageHolder.put(index, (byte[]) load); // только для того, чтобы условия сравнения были одинаковыми
                } else {
                    index = randomImageIndex();
                    byte[] image = singleLevelCache.get(index);
                    if (image == null) {
                        Object load = ImageLoader.load(index);
                        singleLevelCache.put(index, (byte[]) load);
                        imageHolder.put(index, (byte[]) load);
                    } else {
                        imageHolder.put(index, image);
                    }
                }

            }
        }

        long endTime = System.nanoTime();
        double durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.printf("Одноуровневый кэш: %.2f мс\n", durationMs);
    }

    private static int randomImageIndex() {
        return new Random().nextInt(IMAGES_COUNT);
    }

    private static int randomNewImageToLoadIndex() {
        return new Random().nextInt(IMAGES_COUNT + 1, IMAGES_COUNT * 2);
    }
}
