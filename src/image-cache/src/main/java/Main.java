import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    /**
     * Класс для сравнения производительности трехуровневого кеша и обычного кеша.
     * Сравнение производилось путем замера времени работы кешей, а также объему потребляемой памяти.
     *
     * Чтобы сравнение было более достоверным, сначала выполним прогрев JVM.
     */

    private static final int IMAGES_COUNT = 1000;
    private static final int WARM_UP_ITERATIONS = 1000;
    private static final int MEASUREMENT_ITERATIONS = 1000;
    private static final int STRONG_CACHE_SIZE = 100;
    private static final int SOFT_CACHE_SIZE = 300;
    private static final int INITIAL_CAPACITY = 1000;
    private static final float LOAD_FACTOR = 1.1f;
    private static final float TEMPORARY_IMAGE_REFERENCE_HOLD_COUNT = 400; // регулирует, сколько мы будем держать сильных ссылок, чтобы сымитировать работу реального приложения

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
        ThreeLevelImageCache<byte[]> cache = new ThreeLevelImageCache<>(INITIAL_CAPACITY + 1, STRONG_CACHE_SIZE, SOFT_CACHE_SIZE);

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

        long startTime = System.nanoTime();

        ThreeLevelImageCache<byte[]> threeLevelImageCache = new ThreeLevelImageCache<>(INITIAL_CAPACITY, STRONG_CACHE_SIZE, SOFT_CACHE_SIZE);

        for (int i = 0; i < IMAGES_COUNT; i++) {
            threeLevelImageCache.put(i, (byte[]) ImageLoader.load(i));
        }

        // Повторный доступ к изображениям
        for (int iteration = 0; iteration < 10; iteration++) {
            for (int i = 0; i < MEASUREMENT_ITERATIONS; i += 10) {
                int index;
                if (i % 500 == 0) {
                    // получаем индекс изображения, которое еще не было в кеше
                    index = randomNewImageToLoadIndex();
                } else {
                    // получаем индекс изображения, которое ранее мы добавляли сами в кеш (при этом, оттуда оно могло уже удалиться)
                    index = randomImageIndex();
                }
                System.gc();
                byte[] bytes = threeLevelImageCache.get(index);
                imageTemporaryStrongReferenceHolder.put(index, bytes);
            }
        }

        long endTime = System.nanoTime();
        double durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("\n===> Трёхуровневый кэш:");

        System.out.printf("Трёхуровневый кэш: %.2f мс\n", durationMs);
        System.out.println("Размер кеша общий: " + threeLevelImageCache.size());
        System.out.println("Размер кеша strong: " + threeLevelImageCache.strongCacheSize());
        System.out.println("Размер кеша soft: " + threeLevelImageCache.softCacheSize());
        System.out.println("Размер кеша weak: " + threeLevelImageCache.weakCacheSize());

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
                return size() > IMAGES_COUNT;
            }
        };

        long startTime = System.nanoTime();


        for (int i = 0; i < IMAGES_COUNT; i++) {
            singleLevelCache.put(i, (byte[]) ImageLoader.load(i));
        }

        // Повторный доступ к изображениям
        for (int iteration = 0; iteration < 10; iteration++) {
            for (int i = 0; i < MEASUREMENT_ITERATIONS; i += 10) {
                int index;
                if (i % 500 == 0) {
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
        System.out.println("\n===> Одноуровневый кэш:");
        System.out.printf("Одноуровневый кэш: %.2f мс\n", durationMs);

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


class AdvancedMemoryProfiler {

    public static void profileMemory() {
        DecimalFormat df = new DecimalFormat("#,###.##");

        // Информация о кучах (heap)
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean pool : pools) {
            long used = pool.getUsage().getUsed();
            long committed = pool.getUsage().getCommitted();
            long max = pool.getUsage().getMax();

            System.out.println("Тип области памяти: " + pool.getName());
            System.out.println("  Тип памяти: " + pool.getType());
            System.out.println("  Объем занятой памяти: " + df.format(used / (float) 1024 / 1024) + " МБ");
            System.out.println("  Выделено памяти: " + df.format(committed / (float) 1024 / 1024) + " МБ");
            System.out.println("  Максимальная разрешенная память: " + df.format(max / (float) 1024 / 1024) + " МБ");
        }

        // Информация о сборщике мусора
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean bean : gcBeans) {
            System.out.println("Сборщик мусора: " + bean.getName());
            System.out.println("  Кол-во выполненных сборок: " + bean.getCollectionCount());
            System.out.println("  Суммарное время работы: " + bean.getCollectionTime() + " мс");
        }
    }
}