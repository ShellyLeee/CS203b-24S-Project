import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
仅用于测试: 选择区域
 */


public class ImageDrawing extends JFrame implements MouseListener, MouseMotionListener {
    private Point start; // 鼠标按下的坐标
    private Point end; // 鼠标拖动时的坐标

    public ImageDrawing() {
        setTitle("Image Drawing");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

// 在这里添加显示图片的代码

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (start != null && end != null) {
            int x = Math.min(start.x, end.x);
            int y = Math.min(start.y, end.y);
            int width = Math.abs(start.x - end.x);
            int height = Math.abs(start.y - end.y);
            g.drawRect(x, y, width, height); // 绘制矩形
        }
    }

    public void mousePressed(MouseEvent e) {
        start = e.getPoint();
        end = start; // 初始化 end 坐标为 start 坐标
        repaint(); // 重新绘制界面
    }

    public void mouseDragged(MouseEvent e) {
        end = e.getPoint();
        repaint(); // 重新绘制界面
    }

    public void mouseReleased(MouseEvent e) {
        end = e.getPoint();
        repaint(); // 重新绘制界面
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ImageDrawing imageDrawing = new ImageDrawing();
                imageDrawing.setVisible(true);
            }
        });
    }
}

