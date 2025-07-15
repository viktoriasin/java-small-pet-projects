import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    /**
     * Класс для сравнения производительности трехуровневого кеша и обычного кеша.
     * Сравнение производилось путем замера времени работы кешей, а также объему потребляемой памяти.
     *
     * Чтобы сравнение было более достоверным, сначала выполним прогрев JVM.
     */

    private static final int IMAGES_COUNT = 5000;
    private static final int WARM_UP_ITERATIONS = 1000;
    private static final int MEASUREMENT_ITERATIONS = 10_000;
    private static final int STRONG_CACHE_SIZE = 1000;
    private static final int SOFT_CACHE_SIZE = 500;
    private static final int INITIAL_CAPACITY = 11000;
    private static final float LOAD_FACTOR = .75f;
    private static final float TEMPORARY_IMAGE_REFERENCE_HOLD_COUNT = 50; // регулирует, сколько мы будем держать сильных ссылок, чтобы сымитировать работу реального приложения
    private static final int IMAGES_COUNT_IN_SIMPLE_CACHE = 3000;
    private static final int NEW_IMAGE_TO_LOAD = 500; // параметр, регулирующий, когда следует грузить новое изображение

    public static void main(String[] args) {
        System.out.println("======= Исходное состояние памяти ======");
        AdvancedMemoryProfiler.profileMemory();

        warmUp();
        testThreeLevelCache();
        testSimpleCache();

        System.out.println("======= Итоговое состояние памяти ======");
        AdvancedMemoryProfiler.profileMemory();
    }

    private static void warmUp() {
        // Прогреваем виртуальную машину и JIT-компилятор
        ThreeLevelImageCache<byte[]> cache = new ThreeLevelImageCache<>(INITIAL_CAPACITY, STRONG_CACHE_SIZE, SOFT_CACHE_SIZE);

        // Выполняем прогрев через цикл простых операций
        for (int i = 0; i < IMAGES_COUNT; i++) {
            cache.put(i, (byte[]) ImageLoader.load(i));
        }

        for (int i = 0; i < WARM_UP_ITERATIONS; i++) {
            cache.get(randomImageIndex()); // Выполняем хаотичные обращения
        }

        cache.clear();

        // Завершаем прогон принудительной сборкой мусора
        System.gc();

        System.out.println("======= Состояние памяти после прогрева ======");
        AdvancedMemoryProfiler.profileMemory();
    }

    private static void testThreeLevelCache() {

        HashMap<Object, byte[]> imageTemporaryStrongReferenceHolder = new LinkedHashMap<>(INITIAL_CAPACITY + 1, LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, byte[]> eldest) {
                return size() > TEMPORARY_IMAGE_REFERENCE_HOLD_COUNT;
            }
        };


        ThreeLevelImageCache<byte[]> threeLevelImageCache = new ThreeLevelImageCache<>(INITIAL_CAPACITY, STRONG_CACHE_SIZE, SOFT_CACHE_SIZE);

        for (int i = 0; i < IMAGES_COUNT; i++) {
            threeLevelImageCache.put(i, (byte[]) ImageLoader.load(i));
        }

        double[] durationsIterations = new double[10];
        // Повторный доступ к изображениям
        for (int iteration = 0; iteration < 10; iteration++) {

            double[] durationsMeasurements = new double[MEASUREMENT_ITERATIONS];
            for (int i = 0; i < MEASUREMENT_ITERATIONS; i += 10) {
                long startTime = System.nanoTime();
                int index;
                if (i % NEW_IMAGE_TO_LOAD == 0) {
                    // получаем индекс изображения, которое еще не было в кеше
                    index = randomNewImageToLoadIndex();
                } else {
                    // получаем индекс изображения, которое ранее мы добавляли сами в кеш (при этом, оттуда оно могло уже удалиться)
                    index = randomImageIndex();
                }
                byte[] bytes = threeLevelImageCache.get(index);
                imageTemporaryStrongReferenceHolder.put(index, bytes);
                long endTime = System.nanoTime();
                double durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
                durationsMeasurements[i] = durationMs;
            }
            durationsIterations[iteration] = Arrays.stream(durationsMeasurements).average().orElse(0);
        }


        System.out.println("\n===> Трёхуровневый кэш:");

        System.out.printf("Среднее время работы операции get: %.2f мс\n", Arrays.stream(durationsIterations).average().orElse(0));
        System.out.println("Количество непустых изображений в кеше с мягкими ссылками: " + threeLevelImageCache.softCacheNotNullValueCount());
        System.out.println("Количество непустых изображений в кеше со слабыми ссылками: " + threeLevelImageCache.weakCacheNotNullValueCount());

        System.out.println("======= Состояние памяти после работы  трёхуровневый кеша ======");
        AdvancedMemoryProfiler.profileMemory();

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
                return size() > IMAGES_COUNT_IN_SIMPLE_CACHE;
            }
        };

        for (int i = 0; i < IMAGES_COUNT; i++) {
            singleLevelCache.put(i, (byte[]) ImageLoader.load(i));
        }

        double[] durationsIterations = new double[10];
        // Повторный доступ к изображениям
        for (int iteration = 0; iteration < 10; iteration++) {
            double[] durationsMeasurements = new double[MEASUREMENT_ITERATIONS];
            for (int i = 0; i < MEASUREMENT_ITERATIONS; i += 10) {
                long startTime = System.nanoTime();
                int index;
                if (i % NEW_IMAGE_TO_LOAD == 0) {
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

                long endTime = System.nanoTime();
                double durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
                durationsMeasurements[i] = durationMs;
            }
            durationsIterations[iteration] = Arrays.stream(durationsMeasurements).average().orElse(0);
        }

        System.out.println("\n===> Одноуровневый кэш:");
        System.out.printf("Среднее время работы операции get: %.2f мс\n", Arrays.stream(durationsIterations).average().orElse(0));

        System.out.println("======= Состояние памяти после работы обычного кеша ======");
        AdvancedMemoryProfiler.profileMemory();
    }

    private static int randomImageIndex() {
        return new Random().nextInt(IMAGES_COUNT);
    }

    private static int randomNewImageToLoadIndex() {
        return new Random().nextInt(IMAGES_COUNT + 1, IMAGES_COUNT * 2);
    }

}