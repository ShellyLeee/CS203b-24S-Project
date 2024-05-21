import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageArea extends JPanel {
    BufferedImage image;
    BufferedImage copyImage;

    private double scale = 1.0;
    Point startPoint; // 框选的起始点
    Point endPoint; // 框选的结束点

    int topLeftX;
    int topLeftY;
    int topRightX;
    int topRightY;
    int bottomLeftX;
    int bottomLeftY;
    int bottomRightX;
    int bottomRightY;

    int BoxX1;
    int BoxY1;
    int BoxX2;
    int BoxY2;
    int BoxX3;
    int BoxY3;
    int BoxX4;
    int BoxY4;

    int newBoxX1;
    int newBoxY1;
    int newBoxX4;
    int newBoxY4;

    Boolean Selecting = false;

    public ImageArea() {
        //loadImage();
        setPreferredSize(new Dimension(800, 800)); // 设置开始大小
        setBounds(60, 50, 450, 450);
        setLayout(null);

        // 添加鼠标监听器
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint(); // 记录鼠标按下的坐标作为框选的起始点
                endPoint = startPoint;
                repaint();
            }

            public void mouseDragged(MouseEvent e) {
                endPoint = e.getPoint();
                repaint(); // 重新绘制界面
            }

            public void mouseReleased(MouseEvent e) {
                endPoint = e.getPoint(); // 记录鼠标释放的坐标作为框选的结束点

                // 计算框选区域在图片中的相对坐标
                if (startPoint != null && endPoint != null) {
                    BoxX1 = (int) startPoint.getX();
                    BoxY1 = (int) startPoint.getY();
                    BoxX2 = (int) endPoint.getX();
                    BoxY2 = (int) startPoint.getY();
                    BoxX3 = (int) startPoint.getX();
                    BoxY3 = (int) endPoint.getY();
                    BoxX4 = (int) endPoint.getX();
                    BoxY4 = (int) endPoint.getY();

                    TransformBoxImage();
                    System.out.println(newBoxX1);
                    System.out.println(newBoxY1);
                    System.out.println(newBoxX4);
                    System.out.println(newBoxY4);
                }
                repaint();
            }

        });
    }

    void loadImage(File file) {
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ImageCoordinates();
    }

    public void setScale(double scale) {
        this.scale = scale;
        revalidate();
        repaint();
        ImageCoordinates();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int width = (int) (image.getWidth(this) * scale);
            int height = (int) (image.getHeight(this) * scale);
            int x = (getWidth() - width) / 2;
            int y = (getHeight() - height) / 2;
            g.drawImage(image, x, y, width, height, this);

            // 绘制选框
            if (startPoint != null && endPoint != null) {
                int x1 = Math.min(startPoint.x, endPoint.x);
                int y1 = Math.min(startPoint.y, endPoint.y);
                int x2 = Math.max(startPoint.x, endPoint.x);
                int y2 = Math.max(startPoint.y, endPoint.y);
                g.setColor(Color.RED);
                g.drawRect(x1, y1, x2 - x1, y2 - y1);
            }
        }
    }

    void ImageCoordinates() {
        if (image != null) {
            int width = (int) (image.getWidth(this) * scale);
            int height = (int) (image.getHeight(this) * scale);
            int x = (getWidth() - width) / 2;
            int y = (getHeight() - height) / 2;
            // 四角坐标
            topLeftX = x;
            topLeftY = y;
            topRightX = x + width;
            topRightY = y;
            bottomLeftX = x;
            bottomLeftY = y + height;
            bottomRightX = x + width;
            bottomRightY = y + height;
            System.out.println(topLeftX);
            System.out.println(topLeftY);
            System.out.println(bottomRightX);
            System.out.println(bottomRightY);
        }
    }
    public void TransformBoxImage(){
        // 平移+伸缩：针对box
        double x = image.getWidth();
        double w = (double) topRightX - topLeftX;
        double ratio = w / x;
        int bx1 = BoxX1 - topLeftX;
        int by1 = BoxY1 - topLeftY;//平移框左上角点

        int width1 = bx1;
        int height1 = by1;
        int width2 = BoxX2 - BoxX1;
        int height2 = BoxY4 - BoxY2;
        int CWidth1 = (int) (width1 / ratio);
        int CWidth2 = (int) (width2 / ratio);
        int CHeight1 = (int) (height1 / ratio);
        int CHeight2 = (int) (height2 / ratio);

        //左上右下坐标
        newBoxX1 = CWidth1;
        newBoxY1 = CHeight1;

        newBoxX4 = CWidth1 + CWidth2;
        newBoxY4 = CHeight1 + CHeight2;

    }
}
