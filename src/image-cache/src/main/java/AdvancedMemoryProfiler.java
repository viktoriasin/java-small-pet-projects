import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.text.DecimalFormat;
import java.util.List;

public class AdvancedMemoryProfiler {

    public static void profileMemory() {
        DecimalFormat df = new DecimalFormat("#,###.##");

        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean pool : pools) {
            // Для целей нашей задачи интереснее всего посмотреть на G1 Old Gen и G1 Eden Space
            if (pool.getName().equals("G1 Old Gen") || pool.getName().equals("G1 Eden Space")) {
                long used = pool.getUsage().getUsed();
                long committed = pool.getUsage().getCommitted();
                long max = pool.getUsage().getMax();

                System.out.println("Тип области памяти: " + pool.getName());
                System.out.println("  Тип памяти: " + pool.getType());
                System.out.println("  Объем занятой памяти: " + df.format(used / (float) 1024 / 1024) + " МБ");
                System.out.println("  Выделено памяти: " + df.format(committed / (float) 1024 / 1024) + " МБ");
                System.out.println("  Максимальная разрешенная память: " + df.format(max / (float) 1024 / 1024) + " МБ");
            }
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
