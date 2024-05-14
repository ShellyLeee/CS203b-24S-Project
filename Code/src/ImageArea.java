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
    private int offsetX = 0;
    private int offsetY = 0;
    private Point startPoint; // 框选的起始点
    private Point endPoint; // 框选的结束点

    public ImageArea() {
        //loadImage();
        setPreferredSize(new Dimension(800, 800)); // 设置开始大小
        setBounds(30, 40, 450, 450);
        setLayout(null);

        // 添加鼠标监听器
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint(); // 记录鼠标按下的坐标作为框选的起始点
                endPoint = startPoint;
                repaint();
                System.out.println(startPoint);
            }

            public void mouseDragged(MouseEvent e) {
                endPoint = e.getPoint();
                repaint(); // 重新绘制界面
            }

            public void mouseReleased(MouseEvent e) {
                endPoint = e.getPoint(); // 记录鼠标释放的坐标作为框选的结束点
                System.out.println(endPoint);
                // 计算框选区域在图片中的相对坐标
                if (startPoint != null && endPoint != null) {
                    int x1 = (int) ((startPoint.getX() - offsetX) / scale);
                    int y1 = (int) ((startPoint.getY() - offsetY) / scale);
                    int x2 = (int) ((endPoint.getX() - offsetX) / scale);
                    int y2 = (int) ((endPoint.getY() - offsetY) / scale);
                    System.out.println("框选区域在图片中的相对坐标：(" + x1 + ", " + y1 + ") - (" + x2 + ", " + y2 + ")");
                }
                repaint();
            }

            public void mouseClicked(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mouseMoved(MouseEvent e) {}

        });
    }

    void loadImage(File file) {
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setScale(double scale) {
        this.scale = scale;
        revalidate();
        repaint();
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
}
