import java.util.Random;

public class ImageLoader {
    // Имитирует загрузку изображения из базы
    public static Object load(Object key) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Random rand = new Random();
        byte[] data = new byte[rand.nextInt(1024)];
        rand.nextBytes(data);
        return data;
    }
}
