import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SeamCarving {

    BufferedImage raw_image;
    int width;
    int height;
    int[][] R;
    int[][] G;
    int[][] B;
    int[] seamMap_w;
    int[] seamMap_h;
    int constant = 1000;

    public static void main(String[] args) {
        SeamCarving sc = new SeamCarving("image_1.jpg");
        long startTime = System.currentTimeMillis(); 
        sc.cutHeight(200);
        sc.cutWidth(100);
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to seam-carve: " + (endTime - startTime) + "ms");
        sc.showSeamMap("height");
    }

    private SeamCarving(String imagePath) {
        readImage(imagePath);
    }

    public void showSeamMap(String mode) {
        if (mode == "width") {
            showSeamMap(seamMap_w, mode);
        } else if (mode == "height") {
            showSeamMap(seamMap_h, mode);
        }
    }

    public void cutWidth(int n) {
        BufferedImage newImage = raw_image;
        for (int i = 0; i < n; i++) {
            long startTime = System.currentTimeMillis();
            int[] seamMap = calculateRowEnergy(newImage, width, height);
            newImage = new BufferedImage(width--, height, BufferedImage.TYPE_INT_RGB);
            newImage = removeWidthSeam(newImage, seamMap, width, height);
            long endTime = System.currentTimeMillis();
            System.out.println(
                    "Time taken to remove width seam " + String.valueOf(i + 1) + " : " + (endTime - startTime) + "ms");
        }
    }

    public void cutHeight(int n) {
        BufferedImage newImage = raw_image;
        for (int i = 0; i < n; i++) {
            long startTime = System.currentTimeMillis();
            int[] seamMap = calculateColumnEnergy(newImage, width, height);
            newImage = new BufferedImage(width, height--, BufferedImage.TYPE_INT_RGB);
            newImage = removeColumnSeam(newImage, seamMap, width, height);
            long endTime = System.currentTimeMillis();
            System.out.println(
                    "Time taken to remove height seam " + String.valueOf(i + 1) + " : " + (endTime - startTime) + "ms");
        }
    }

    public void readImage(String path) {
        try {
            // Read the image file
            File imageFile = new File(path);
            FileInputStream fis = new FileInputStream(imageFile);
            BufferedImage image = ImageIO.read(fis);
            raw_image = image;
            fis.close();

            // Get the width and height of the image
            width = image.getWidth();
            height = image.getHeight();
            System.out.println("Image width: " + width);
            System.out.println("Image height: " + height);

            // Create arrays to store the RGB values
            R = new int[height][width];
            G = new int[height][width];
            B = new int[height][width];

            // Iterate over each pixel and store the RGB values
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = new Color(image.getRGB(x, y));
                    R[y][x] = color.getRed();
                    G[y][x] = color.getGreen();
                    B[y][x] = color.getBlue();
                }
            }
            System.out.println("Image read successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] calculateRowEnergy(BufferedImage image, int w, int h) {
        // Create a 2D array to store the energy values
        double[] current_energy = new double[w];
        double[] pre_energy = new double[w];
        int[][] seamingMap = new int[h][w];
        // Calculate the energy of each pixel
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (y == 0) {
                    current_energy[x] = constant;
                    seamingMap[y][x] = x;
                    continue;
                } else if (x == 0) {
                    double[] pre_energy_part = { pre_energy[x], pre_energy[x + 1] };
                    int[] indexes = { x, x + 1 };
                    double[] best = selectPath(pre_energy_part, indexes);
                    current_energy[x] = constant + best[0];
                    seamingMap[y][x] = (int) best[1];
                    continue;
                } else if (x == w - 1) {
                    double[] pre_energy_part = { pre_energy[x - 1], pre_energy[x] };
                    int[] indexes = { x - 1, x };
                    double[] best = selectPath(pre_energy_part, indexes);
                    current_energy[x] = constant + best[0];
                    seamingMap[y][x] = (int) best[1];
                    continue;
                } else if (y == h - 1) {
                    double[] pre_energy_part = { pre_energy[x - 1], pre_energy[x], pre_energy[x + 1] };
                    int[] indexes = { x - 1, x, x + 1 };
                    double[] best = selectPath(pre_energy_part, indexes);
                    current_energy[x] = constant + best[0];
                    seamingMap[y][x] = (int) best[1];
                    continue;
                }

                // select minimus energy path from the last row
                double[] pre_energy_part = { pre_energy[x - 1], pre_energy[x], pre_energy[x + 1] };
                int[] indexes = { x - 1, x, x + 1 };
                double[] best = selectPath(pre_energy_part, indexes);

                // current energy
                int Rx = R[y][x + 1] - R[y][x - 1];
                int Gx = G[y][x + 1] - G[y][x - 1];
                int Bx = B[y][x + 1] - B[y][x - 1];
                long delta_x = Rx * Rx + Gx * Gx + Bx * Bx;

                int Ry = R[y + 1][x] - R[y - 1][x];
                int Gy = G[y + 1][x] - G[y - 1][x];
                int By = B[y + 1][x] - B[y - 1][x];
                long delta_y = Ry * Ry + Gy * Gy + By * By;

                current_energy[x] = Math.sqrt(delta_x + delta_y) + best[0];
                seamingMap[y][x] = (int) best[1];
            }
            pre_energy = current_energy.clone();
        }

        // System.out.println("Energy calculated successfully");

        // Return the seam path
        seamMap_w = new int[h];
        int minIndex = (int) selectPath(pre_energy, seamingMap[0])[1];
        for (int i = h - 1; i >= 0; i--) {
            seamMap_w[i] = minIndex;
            minIndex = seamingMap[i][minIndex];
        }
        return seamMap_w;
    }

    private int[] calculateColumnEnergy(BufferedImage image, int w, int h) {

        // Create a 2D array to store the energy values
        double[] current_energy = new double[h];
        double[] pre_energy = new double[h];
        int[][] seamingMap = new int[w][h];
        // Calculate the energy of each pixel
        for (int y = 0; y < w; y++) {
            for (int x = 0; x < h; x++) {
                if (y == 0) {
                    current_energy[x] = constant;
                    seamingMap[y][x] = x;
                    continue;
                } else if (x == 0) {
                    double[] pre_energy_part = { pre_energy[x], pre_energy[x + 1] };
                    int[] indexes = { x, x + 1 };
                    double[] best = selectPath(pre_energy_part, indexes);
                    current_energy[x] = constant + best[0];
                    seamingMap[y][x] = (int) best[1];
                    continue;
                } else if (x == h - 1) {
                    double[] pre_energy_part = { pre_energy[x - 1], pre_energy[x] };
                    int[] indexes = { x - 1, x };
                    double[] best = selectPath(pre_energy_part, indexes);
                    current_energy[x] = constant + best[0];
                    seamingMap[y][x] = (int) best[1];
                    continue;
                } else if (y == w - 1) {
                    double[] pre_energy_part = { pre_energy[x - 1], pre_energy[x], pre_energy[x + 1] };
                    int[] indexes = { x - 1, x, x + 1 };
                    double[] best = selectPath(pre_energy_part, indexes);
                    current_energy[x] = constant + best[0];
                    seamingMap[y][x] = (int) best[1];
                    continue;
                }

                // select minimus energy path from the last row
                double[] pre_energy_part = { pre_energy[x - 1], pre_energy[x], pre_energy[x + 1] };
                int[] indexes = { x - 1, x, x + 1 };
                double[] best = selectPath(pre_energy_part, indexes);

                // current energy
                int Rx = R[x][y + 1] - R[x][y - 1];
                int Gx = G[x][y + 1] - G[x][y - 1];
                int Bx = B[x][y + 1] - B[x][y - 1];
                long delta_x = Rx * Rx + Gx * Gx + Bx * Bx;

                int Ry = R[x + 1][y] - R[x - 1][y];
                int Gy = G[x + 1][y] - G[x - 1][y];
                int By = B[x + 1][y] - B[x - 1][y];
                long delta_y = Ry * Ry + Gy * Gy + By * By;

                current_energy[x] = Math.sqrt(delta_x + delta_y) + best[0];
                seamingMap[y][x] = (int) best[1];
            }
            pre_energy = current_energy.clone();
        }

        // System.out.println("Energy calculated successfully");

        // Return the seam path
        seamMap_h = new int[w];
        int minIndex = (int) selectPath(pre_energy, seamingMap[0])[1];
        for (int i = w - 1; i >= 0; i--) {
            seamMap_h[i] = minIndex;
            minIndex = seamingMap[i][minIndex];
        }
        return seamMap_h;
    }

    private double[] selectPath(double[] pre_energy, int[] indexes) {
        double[] best = new double[2];
        int minIndex = 0;
        double minValue = pre_energy[0];
        for (int i = 1; i < pre_energy.length; i++) {
            if (pre_energy[i] < minValue) {
                minValue = pre_energy[i];
                minIndex = i;
            }
        }
        best[0] = minValue;
        best[1] = indexes[minIndex];
        return best;
    }

    private void showSeamMap(int[] seamMap, String mode) {
        // Create a new image to store the seam map
        BufferedImage seamImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        seamImage = raw_image;

        if (mode == "width") {
            // Iterate over each pixel and set the color based on the seam map
            for (int y = height - 1; y >= 0; y--) {
                int end = seamMap[y];
                seamImage.setRGB(end, y, Color.RED.getRGB());

            }
        } else if (mode == "height") {
            for (int y = width - 1; y >= 0; y--) {
                int end = seamMap[y];
                seamImage.setRGB(y, end, Color.RED.getRGB());

            }
        }

        // Save the seam map image
        try {
            File seamFile = new File("seam_map.jpg");
            ImageIO.write(seamImage, "jpg", seamFile);
            // System.out.println("Seam map saved successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage removeWidthSeam(BufferedImage newImage, int[] seamMap, int width, int height) {

        R = new int[height][width];
        G = new int[height][width];
        B = new int[height][width];
        // Iterate over each pixel and set the color based on the seam map
        for (int y = 0; y < height; y++) {
            int end = seamMap[y];
            for (int x = 0; x < width + 1; x++) {
                Color color = new Color(raw_image.getRGB(x, y));
                if (x < end) {
                    R[y][x] = color.getRed();
                    G[y][x] = color.getGreen();
                    B[y][x] = color.getBlue();
                    newImage.setRGB(x, y, color.getRGB());
                } else if (x > end) {
                    R[y][x - 1] = color.getRed();
                    G[y][x - 1] = color.getGreen();
                    B[y][x - 1] = color.getBlue();
                    newImage.setRGB(x - 1, y, color.getRGB());
                }
            }
        }

        // Save the seam map image
        try {
            File seamFile = new File("new_image.jpg");
            ImageIO.write(newImage, "jpg", seamFile);
            // System.out.println("Seam removed successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
        raw_image = newImage;
        return newImage;
    }

    public BufferedImage removeColumnSeam(BufferedImage newImage, int[] seamMap, int width, int height) {

        R = new int[height][width];
        G = new int[height][width];
        B = new int[height][width];
        // Iterate over each pixel and set the color based on the seam map
        for (int y = 0; y < width; y++) {
            int end = seamMap[y];
            for (int x = 0; x < height + 1; x++) {
                Color color = new Color(raw_image.getRGB(y, x));
                if (x < end) {
                    R[x][y] = color.getRed();
                    G[x][y] = color.getGreen();
                    B[x][y] = color.getBlue();
                    newImage.setRGB(y, x, color.getRGB());
                } else if (x > end) {
                    R[x - 1][y] = color.getRed();
                    G[x - 1][y] = color.getGreen();
                    B[x - 1][y] = color.getBlue();
                    newImage.setRGB(y, x - 1, color.getRGB());
                }
            }
        }

        // Save the seam map image
        try {
            File seamFile = new File("new_image.jpg");
            ImageIO.write(newImage, "jpg", seamFile);
            // System.out.println("Seam removed successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
        raw_image = newImage;
        return newImage;
    }

}
