import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.Point;
import javax.imageio.ImageIO;

import java.util.Objects;
import java.util.Scanner;

public class SeamCarving {

    private class EandMap {
        public double[] currentEnergy;
        public int[][] map;

        public EandMap(double[] energy, int[][] path) {
            this.currentEnergy = energy;
            this.map = path;
        }
    }

    BufferedImage rawImage;
    BufferedImage currentImage;
    int width;
    int height;
    int[][] R;
    int[][] G;
    int[][] B;
    int[] seamMap_w;
    int[] seamMap_h;
    int constant = 1000;
    public int ChangeWidth;
    public int ChangeHeight;

    public double WidthRatio;
    public double HeightRatio;

    private Point topLeft;
    private Point bottomRight;

    public String MODE;

    // 选取左上角
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入 0 0 0 0 a 正常SeamCarving;输入 左上右下xy坐标 b 区域避免 输入 左上右下xy坐标 c 区域强化");
        int topLeftX = scanner.nextInt();
        int topLeftY = scanner.nextInt();
        int bottomRightX = scanner.nextInt();
        int bottomRightY = scanner.nextInt();
        String mode = scanner.next();
        SeamCarving sc = null; // 在 if-else 外部声明

        // 输入 0 0 0 0 a 正常SeamCarving;输入 左上右下xy坐标 b 区域避免 输入 左上右下xy坐标 c 区域强化
        if (Objects.equals(mode, "a")) {
            sc = new SeamCarving("Code/img/eng.jpg");
        } else if (Objects.equals(mode, "b")) {
            sc = new SeamCarving("Code/img/eng.jpg", new Point(topLeftX, topLeftY),
                    new Point(bottomRightX, bottomRightY));
        } else if (Objects.equals(mode, "c")) {
            sc = new SeamCarving("Code/img/eng.jpg", new Point(topLeftX, topLeftY),
                    new Point(bottomRightX, bottomRightY), 1);
        }

        long startTime = System.currentTimeMillis();
        sc.cutWidth(200);
        sc.cutHeight(100);
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to seam-carve: " + (endTime - startTime) + "ms");
    }

    public SeamCarving(String imagePath) {
        readImage(imagePath);
    }

    public SeamCarving(String imagePath, Point topLeft, Point bottomRight) {
        readImage(imagePath);
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.MODE = "b";
    }

    public SeamCarving(String imagePath, Point topLeft, Point bottomRight, int a) {
        readImage(imagePath);
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.MODE = "c";
    }

    public void showSeamMap(String mode) {
        if (mode == "width") {
            showSeamMap(seamMap_w, mode);
        } else if (mode == "height") {
            showSeamMap(seamMap_h, mode);
        }
    }

    public void cutWidth(int n) {
        ChangeWidth = n;
        BufferedImage newImage = currentImage;
        for (int i = 0; i < n; i++) {
            long startTime = System.currentTimeMillis();
            EandMap result = calculateRowEnergy(newImage, width, height);
            seamMap_w = findMinPath(result.currentEnergy, result.map, height);
            newImage = new BufferedImage(width--, height, BufferedImage.TYPE_INT_RGB);
            newImage = removeWidthSeam(newImage, seamMap_w, width, height);
            long endTime = System.currentTimeMillis();


//              System.out.println(
//              "Time taken to remove width seam " + String.valueOf(i + 1) + " : " + (endTime
//              - startTime) + "ms");


        }
        currentImage = newImage;

        try {
            File seamFile = new File("Code/img/new_image.jpg");
            ImageIO.write(currentImage, "jpg", seamFile);
            // System.out.println("Seam removed successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cutWidth(double ratio) {
        WidthRatio = ratio;
        int n = (int) (width * ratio);
        cutWidth(n);
    }

    public void cutHeight(int n) {
        ChangeHeight = n;
        BufferedImage newImage = currentImage;
        for (int i = 0; i < n; i++) {
            long startTime = System.currentTimeMillis();
            EandMap result = calculateColumnEnergy(newImage, width, height);
            seamMap_h = findMinPath(result.currentEnergy, result.map, width);
            newImage = new BufferedImage(width, height--, BufferedImage.TYPE_INT_RGB);
            newImage = removeColumnSeam(newImage, seamMap_h, width, height);
            long endTime = System.currentTimeMillis();
            // Save the seam map image

//              System.out.println(
//              "Time taken to remove height seam " + String.valueOf(i + 1) + " : " +
//              (endTime - startTime) + "ms");


        }
        currentImage = newImage;

        try {
            File seamFile = new File("Code/img/new_image.jpg");
            ImageIO.write(newImage, "jpg", seamFile);
            // System.out.println("Seam removed successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cutHeight(double ratio) {
        HeightRatio = ratio;
        int n = (int) (height * ratio);
        cutHeight(n);
    }

    public void expandWidth(int n) {
        EandMap result = calculateRowEnergy(currentImage, width, height);
        int[] indexes = myTool.sortIndex(result.currentEnergy);
        int[][] seamMaps = findNMinPath(indexes, result.map, n, height);
        showSeamMap(seamMaps, n, "width");
        BufferedImage newImage = new BufferedImage(width + n, height, BufferedImage.TYPE_INT_RGB);
        newImage = addRowSeam(newImage, seamMaps, n);

        currentImage = newImage;
        width = width + n;
        try {
            File seamFile = new File("Code/img/new_image.jpg");
            ImageIO.write(currentImage, "jpg", seamFile);
            // System.out.println("Seam removed successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void expandWidth(double ratio) {
        int n = (int) (width * ratio);
        expandWidth(n);
    }

    public void expandHeight(int n) {
        EandMap result = calculateColumnEnergy(currentImage, width, height);
        int[] indexes = myTool.sortIndex(result.currentEnergy);
        int[][] seamMaps = findNMinPath(indexes, result.map, n, width);
        showSeamMap(seamMaps, n, "height");
        BufferedImage newImage = new BufferedImage(width, height + n, BufferedImage.TYPE_INT_RGB);
        newImage = addColumnSeam(newImage, seamMaps, n);

        currentImage = newImage;
        height = height + n;
        try {
            File seamFile = new File("Code/img/new_image.jpg");
            ImageIO.write(currentImage, "jpg", seamFile);
            // System.out.println("Seam removed successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void expandHeight(double ratio) {
        int n = (int) (height * ratio);
        expandHeight(n);
    }

    public void readImage(String path) {
        try {
            // Read the image file
            File imageFile = new File(path);
            FileInputStream fis = new FileInputStream(imageFile);
            BufferedImage image = ImageIO.read(fis);
            rawImage = image;
            currentImage = image;
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

    // calcukateRow 形成竖向的线
    private EandMap calculateRowEnergy(BufferedImage image, int w, int h) {
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

                if (Objects.equals(MODE, "b")) {
                    if (topLeft.x <= x && bottomRight.x >= x && topLeft.y <= y && bottomRight.y >= y) {
                        pre_energy[x] = 10 * pre_energy[x] + 100000000;
                    }
                    if (topLeft.x <= x - 1 && bottomRight.x >= x - 1 && topLeft.y <= y && bottomRight.y >= y) {
                        pre_energy[x - 1] = 10 * pre_energy[x - 1] + 100000000;
                    }
                    if (topLeft.x <= x + 1 && bottomRight.x >= x + 1 && topLeft.y <= y && bottomRight.y >= y) {
                        pre_energy[x + 1] = 10 * pre_energy[x + 1] + 100000000;
                    }
                }

                if (Objects.equals(MODE, "c")) {
                    int centerX = (topLeft.x + bottomRight.x) / 2;
                    int centerY = (topLeft.y + bottomRight.y) / 2;

// 最大距离，用于归一化衰减因子
                    double maxDistance = Math.sqrt(Math.pow(centerX - topLeft.x, 2) + Math.pow(centerY - topLeft.y, 2));
                    if (topLeft.x <= x && bottomRight.x >= x && topLeft.y <= y && bottomRight.y >= y) {
                        // 遍历每个点，计算衰减因子并应用

                                // 计算当前点到中心点的距离
                                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));

                                // 计算衰减因子（距离越远，因子越小）
                        double decayFactor = Math.exp(-(distance / maxDistance));

                                // 应用衰减因子到能量上
                                pre_energy[x] = pre_energy[x] * decayFactor;

                    }
                    if (topLeft.x <= x - 1 && bottomRight.x >= x - 1 && topLeft.y <= y && bottomRight.y >= y) {

                                // 计算当前点到中心点的距离
                                double distance = Math.sqrt(Math.pow(x-1 - centerX, 2) + Math.pow(y - centerY, 2));

                                // 计算衰减因子（距离越远，因子越小）
                        double decayFactor = Math.exp(-(distance / maxDistance));

                                // 应用衰减因子到能量上
                                pre_energy[x-1] = pre_energy[x-1] * decayFactor;

                    }
                    if (topLeft.x <= x + 1 && bottomRight.x >= x + 1 && topLeft.y <= y && bottomRight.y >= y) {

                                // 计算当前点到中心点的距离
                                double distance = Math.sqrt(Math.pow(x+1 - centerX, 2) + Math.pow(y - centerY, 2));

                                // 计算衰减因子（距离越远，因子越小）
                        double decayFactor = Math.exp(-(distance / maxDistance));

                                // 应用衰减因子到能量上
                                pre_energy[x+1] = pre_energy[x+1] * decayFactor;
                            }

                }

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
        EandMap result = new EandMap(current_energy, seamingMap);
        return result;
        // Return the seam path
        /*
         * seamMap_w = new int[h];
         * int minIndex = (int) selectPath(pre_energy, seamingMap[0])[1];
         * for (int i = h - 1; i >= 0; i--) {
         * seamMap_w[i] = minIndex;
         * minIndex = seamingMap[i][minIndex];
         * }
         * return seamMap_w;
         */

    }

    // calculateColumn 形成横向的线
    private EandMap calculateColumnEnergy(BufferedImage image, int w, int h) {

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

                if (Objects.equals(MODE, "b")) {
                    if (topLeft.x <= y && bottomRight.x >= y && topLeft.y <= x && bottomRight.y >= x) {
                        pre_energy[x] = 10 * pre_energy[x] + 100000000;
                    }
                    if (topLeft.x <= y && bottomRight.x >= y && topLeft.y <= x - 1 && bottomRight.y >= x - 1) {
                        pre_energy[x - 1] = 10 * pre_energy[x - 1] + 100000000;
                    }
                    if (topLeft.x <= y && bottomRight.x >= y && topLeft.y <= x + 1 && bottomRight.y >= x + 1) {
                        pre_energy[x + 1] = 10 * pre_energy[x + 1] + 100000000;
                    }
                }

                if (Objects.equals(MODE, "c")) {
                    // 计算中心点坐标
                    int centerX = (topLeft.x + bottomRight.x) / 2;
                    int centerY = (topLeft.y + bottomRight.y) / 2;

// 最大距离，用于归一化衰减因子
                    double maxDistance = Math.sqrt(Math.pow(centerX - topLeft.x, 2) + Math.pow(centerY - topLeft.y, 2));
                    if (topLeft.x <= y && bottomRight.x >= y && topLeft.y <= x && bottomRight.y >= x) {
                        // 遍历每个点，计算衰减因子并应用

                                // 计算当前点到中心点的距离
                                double distance = Math.sqrt(Math.pow(y - centerX, 2) + Math.pow(x - centerY, 2));

                                // 计算衰减因子（距离越远，因子越小）
                        double decayFactor = Math.exp(-(distance / maxDistance));

                                // 应用衰减因子到能量上
                                pre_energy[x] = pre_energy[x] * decayFactor;

                    }
                    if (topLeft.x <= y && bottomRight.x >= y && topLeft.y <= x - 1 && bottomRight.y >= x - 1) {
                        // 遍历每个点，计算衰减因子并应用

                                // 计算当前点到中心点的距离
                                double distance = Math.sqrt(Math.pow(y - centerX, 2) + Math.pow(x-1 - centerY, 2));

                                // 计算衰减因子（距离越远，因子越小）
                        double decayFactor = Math.exp(-(distance / maxDistance));

                                // 应用衰减因子到能量上
                                pre_energy[x-1] = pre_energy[x-1] * decayFactor;

                    }
                    if (topLeft.x <= y && bottomRight.x >= y && topLeft.y <= x + 1 && bottomRight.y >= x + 1) {
                        // 遍历每个点，计算衰减因子并应用

                                // 计算当前点到中心点的距离
                                double distance = Math.sqrt(Math.pow(y - centerX, 2) + Math.pow(x+1 - centerY, 2));

                                // 计算衰减因子（距离越远，因子越小）
                        double decayFactor = Math.exp(-(distance / maxDistance));

                                // 应用衰减因子到能量上
                                pre_energy[x+1] = pre_energy[x+1] * decayFactor;

                    }
                }

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
        EandMap result = new EandMap(current_energy, seamingMap);
        return result;
        // Return the seam path
        /*
         * seamMap_h = new int[w];
         * int minIndex = (int) selectPath(pre_energy, seamingMap[0])[1];
         * for (int i = w - 1; i >= 0; i--) {
         * seamMap_h[i] = minIndex;
         * minIndex = seamingMap[i][minIndex];
         * }
         * return seamMap_h;
         */

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

    private int[] findMinPath(double[] energy, int[][] map, int length) {
        int[] seamMap = new int[length];
        int minIndex = (int) selectPath(energy, map[0])[1];
        for (int i = length - 1; i >= 0; i--) {
            seamMap[i] = minIndex;
            minIndex = map[i][minIndex];
        }
        return seamMap;
    }

    private int[][] findNMinPath(int[] indexes, int[][] map, int n, int length) {
        int[][] seamMaps = new int[length][n];
        for (int i = 0; i < n; i++) {
            int minIndex = indexes[i];
            for (int j = length - 1; j >= 0; j--) {
                seamMaps[j][i] = minIndex;
                minIndex = map[j][minIndex];
            }
        }
        return seamMaps;
    }

    private void showSeamMap(int[] seamMap, String mode) {
        // Create a new image to store the seam map
        BufferedImage seamImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        seamImage = rawImage;

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
            File seamFile = new File("Code/img/seam_map.jpg");
            ImageIO.write(seamImage, "jpg", seamFile);
            // System.out.println("Seam map saved successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSeamMap(int[][] seamMaps, int n, String mode) {
        // Create a new image to store the seam map
        BufferedImage seamImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                seamImage.setRGB(x, y, rawImage.getRGB(x, y));
            }
        }

        if (mode == "width") {
            for (int i = 0; i < n; i++) {
                // Iterate over each pixel and set the color based on the seam map
                for (int y = height - 1; y >= 0; y--) {
                    int end = seamMaps[y][i];
                    seamImage.setRGB(end, y, Color.RED.getRGB());
                }
            }
        } else if (mode == "height") {
            for (int i = 0; i < n; i++) {
                // Iterate over each pixel and set the color based on the seam map
                for (int y = width - 1; y >= 0; y--) {
                    int end = seamMaps[y][i];
                    seamImage.setRGB(y, end, Color.RED.getRGB());
                }
            }

        }

        // Save the seam map image
        try {
            File seamFile = new File("Code/img/seam_map.jpg");
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
                Color color = new Color(rawImage.getRGB(x, y));
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

        //
        if (topLeft != null && bottomRight != null) {
            int selectedY = (topLeft.y + bottomRight.y) / 2;
            if (seamMap[selectedY] < topLeft.x + 1) {
                topLeft.x--;
                bottomRight.x--;
            }
        }
        rawImage = newImage;
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
                Color color = new Color(rawImage.getRGB(y, x));
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
        //
        if (topLeft != null && bottomRight != null) {
            int selectedX = (topLeft.x + bottomRight.x) / 2;
            if (seamMap[selectedX] < topLeft.y + 1) {
                topLeft.y--;
                bottomRight.y--;
            }
        }

        rawImage = newImage;
        return newImage;
    }

    public BufferedImage addRowSeam(BufferedImage newImage, int[][] seamMaps, int n) {
        R = new int[height][width + n];
        G = new int[height][width + n];
        B = new int[height][width + n];
        // Iterate over each pixel and set the color based on the seam map

        for (int y = 0; y < height; y++) {
            int[] ends = myTool.insertionSort(seamMaps[y]);
            int i = 0;
            for (int x = 0; x < width; x++) {
                Color color = new Color(rawImage.getRGB(x, y));
                if (x < ends[i]) {
                    R[y][x + i] = color.getRed();
                    G[y][x + i] = color.getGreen();
                    B[y][x + i] = color.getBlue();
                    newImage.setRGB(x + i, y, color.getRGB());
                } else if (x == ends[i]) {
                    R[y][x + i] = color.getRed();
                    G[y][x + i] = color.getGreen();
                    B[y][x + i] = color.getBlue();
                    newImage.setRGB(x + i, y, color.getRGB());
                    R[y][x + i + 1] = color.getRed();
                    G[y][x + i + 1] = color.getGreen();
                    B[y][x + i + 1] = color.getBlue();
                    newImage.setRGB(x + i + 1, y, color.getRGB());
                    i++;
                    if (i < n) {
                        while (ends[i] == ends[i - 1]) {
                            R[y][x + i + 1] = color.getRed();
                            G[y][x + i + 1] = color.getGreen();
                            B[y][x + i + 1] = color.getBlue();
                            newImage.setRGB(x + i + 1, y, color.getRGB());
                            i++;
                            if (i == n) {
                                i--;
                                break;
                            }
                        }
                    } else {
                        i--;
                    }

                } else {
                    R[y][x + n] = color.getRed();
                    G[y][x + n] = color.getGreen();
                    B[y][x + n] = color.getBlue();
                    newImage.setRGB(x + n, y, color.getRGB());
                }
            }
        }

        rawImage = newImage;
        return newImage;

    }

    public BufferedImage addColumnSeam(BufferedImage newImage, int[][] seamMaps, int n) {
        R = new int[height + n][width];
        G = new int[height + n][width];
        B = new int[height + n][width];
        // Iterate over each pixel and set the color based on the seam map

        for (int y = 0; y < width; y++) {
            int[] ends = myTool.insertionSort(seamMaps[y]);
            int i = 0;
            for (int x = 0; x < height; x++) {
                Color color = new Color(rawImage.getRGB(y, x));
                if (x < ends[i]) {
                    R[x + i][y] = color.getRed();
                    G[x + i][y] = color.getGreen();
                    B[x + i][y] = color.getBlue();
                    newImage.setRGB(y, x + i, color.getRGB());
                } else if (x == ends[i]) {
                    R[x + i][y] = color.getRed();
                    G[x + i][y] = color.getGreen();
                    B[x + i][y] = color.getBlue();
                    newImage.setRGB(y, x + i, color.getRGB());
                    R[x + i + 1][y] = color.getRed();
                    G[x + i + 1][y] = color.getGreen();
                    B[x + i + 1][y] = color.getBlue();
                    newImage.setRGB(y, x + i + 1, color.getRGB());
                    i++;
                    if (i < n) {
                        while (ends[i] == ends[i - 1]) {
                            R[x + i + 1][y] = color.getRed();
                            G[x + i + 1][y] = color.getGreen();
                            B[x + i + 1][y] = color.getBlue();
                            newImage.setRGB(y, x + i + 1, color.getRGB());
                            i++;
                            if (i == n) {
                                i--;
                                break;
                            }
                        }
                    } else {
                        i--;
                    }

                } else {
                    R[x + n][y] = color.getRed();
                    G[x + n][y] = color.getGreen();
                    B[x + n][y] = color.getBlue();
                    newImage.setRGB(y, x + n, color.getRGB());
                }
            }
        }

        rawImage = newImage;
        return newImage;
    }
    // public class NonTouchableArea {
    // private Point topLeft;
    // private Point bottomRight;
    //
    // public NonTouchableArea(Point topLeft, Point bottomRight) {
    // this.topLeft = topLeft;
    // this.bottomRight = bottomRight;
    // }

    // public boolean intersects(int x, int y) {
    // return y >= topLeft.y && y <= bottomRight.y && x >= topLeft.x && x <=
    // bottomRight.x;
    // }
    // // 检查缝隙是否与水平不可触碰区域相交
    // private boolean intersectsHorizontalNonTouchableArea(int[] seamMap) {
    // if (nonTouchableArea == null) {
    // return false;
    // }
    //
    // for (int y = 0; y < seamMap.length; y++) {
    // if (nonTouchableArea.intersects(seamMap[y],y)) {
    // return true;
    // }
    // }
    // return false;
    // }
    //
    // // 检查缝隙是否与垂直不可触碰区域相交
    // private boolean intersectsVerticalNonTouchableArea(int[] seamMap) {
    // if (nonTouchableArea == null) {
    // return false;
    // }
    //
    // for (int x = 0; x < seamMap.length; x++) {
    // if (nonTouchableArea.intersects(x,seamMap[x])) {
    // return true;
    // }
    // }
    // return false;
    // }

}
