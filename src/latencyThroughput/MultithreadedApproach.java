package latencyThroughput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultithreadedApproach {

    private static final String SOURCE_FILE = "./out/many-flowers.jpg";
    private static final String DESTINATION_FILE = "./out/image/many-flowers.jpg";

    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red-green) < 30 && Math.abs(red - blue) < 30
                && Math.abs(green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0X0000FF00) >> 8;
    }

    public static int getBlue(int rgb) { return rgb & 0x000000FF; }

    public static void recolorPixel(BufferedImage orgImage, BufferedImage res,
                                    int x, int y) {
        int rgb = orgImage.getRGB(x, y);

        int green = getGreen(rgb);
        int red = getRed(rgb);
        int blue = getBlue(rgb);

        if (isShadeOfGray(red, green, blue)) {
            int newRed = Math.min(255, red+10);
            int newGreen = Math.max(0, rgb - 80);
            int newBlue = Math.max(0, rgb - 20);
            int newRgb = createRGBFromColors(newRed, newGreen, newBlue);
            setRGB(res, x, y, newRgb);
        }

    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster()
                .setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    public static void recolorImage(BufferedImage ogImage, BufferedImage rgImage, int leftCorner, int topCorner,
                                    int height, int width) {
        for (int x = leftCorner; x < leftCorner + width && x < ogImage.getWidth(); x++) {
            for (int y= topCorner; y < topCorner + height && y < ogImage.getHeight(); y++) {
                recolorPixel(ogImage, rgImage, x, y);
            }
        }
    }

    public static void recolorMultiThreaded(BufferedImage ogImage, BufferedImage rgImage, int numberOfThreads) {
        List<Thread> threads = new ArrayList<>();
        int width = ogImage.getWidth();
        int height = ogImage.getHeight() / numberOfThreads;

        for (int i=0; i<numberOfThreads; i++) {
            final int threadMultiplier = i;

            Thread thread = new Thread(() -> {
                int leftCorner = 0;
                int topCorner = height * threadMultiplier;

                recolorImage(ogImage, rgImage, leftCorner, topCorner, ogImage.getHeight(), ogImage.getWidth());
            });
            threads.add(thread);
        }

        for (Thread thread: threads) {
            thread.start();
        }

        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_RGB)  ;

        int numberOfThreads = 2;
        long start = System.currentTimeMillis();

        recolorMultiThreaded(image, resultImage, numberOfThreads);
        long end = System.currentTimeMillis();

        System.out.println("Time Taken "+(end-start));

        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(resultImage, "jpg", outputFile);
    }
}