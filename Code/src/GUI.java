import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame{
    private JPanel operateArea;
    public JButton Shrink;
    public JButton Expand;
    public JButton Import;
    public JButton SelectKeep;
    public JButton SelectRemove;
    public JButton Retry;
    public JButton Download;
    private final int WIDTH;
    private final int HEIGHT;
    public int ChangeWidth;
    public int ChangeHeight;
    public BufferedImage TargetImage;


    public GUI(int width, int height){
        WIDTH = width;
        HEIGHT = height;
        setTitle("2024 CS203b Project Demo");
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //程序右上方的叉关闭
        setLayout(null);
        //Operate Area
        operateArea = new JPanel();
        operateArea.setLayout(null);
        operateArea.setBackground(Color.LIGHT_GRAY);
        operateArea.setBounds(30, 40, 500, 500); // 设置矩形区域的位置和大小
        add(operateArea);
        //Button
        addSelectKeepButton();
        addSelectRemoveButton();
        addExpandButton();
        addShrinkButton();
        addImportButton();
        addRetryButton();
        addDownloadButton();
        this.setVisible(true);
    }

    private void addShrinkButton(){
        Shrink = new JButton("Shrink");
        Shrink.setSize(120,50);
        Shrink.setLocation(WIDTH * 4 / 5, HEIGHT / 10 + 70);

        Shrink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建一个对话框
                JPanel panel = new JPanel();
                JTextField widthField = new JTextField(5);
                JTextField heightField = new JTextField(5);

                panel.add(new JLabel("Width:"));
                panel.add(widthField);
                panel.add(Box.createHorizontalStrut(15)); // 添加间隔
                panel.add(new JLabel("Height:"));
                panel.add(heightField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Enter Width and Height", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        // 获取用户输入的宽度和高度
                        ChangeWidth = Integer.parseInt(widthField.getText());
                        ChangeHeight = Integer.parseInt(heightField.getText());
                        // 可以在这里进行一些额外的验证
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter valid numbers for Width and Height.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        add(Shrink);
        setVisible(true);
    }

    private void addExpandButton(){
        Expand = new JButton("Expand");
        Expand.setSize(120,50);
        Expand.setLocation(WIDTH * 4 / 5, HEIGHT / 10 + 140);

        Expand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建一个对话框
                JPanel panel = new JPanel();
                JTextField widthField = new JTextField(5);
                JTextField heightField = new JTextField(5);

                panel.add(new JLabel("Width:"));
                panel.add(widthField);
                panel.add(Box.createHorizontalStrut(15)); // 添加间隔
                panel.add(new JLabel("Height:"));
                panel.add(heightField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Enter Width and Height", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        // 获取用户输入的宽度和高度
                        ChangeWidth = Integer.parseInt(widthField.getText());
                        ChangeHeight = Integer.parseInt(heightField.getText());
                        // 可以在这里进行一些额外的验证
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Please enter valid numbers for Width and Height.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        add(Expand);
        setVisible(true);
    }

    private void addImportButton(){
        Import= new JButton("Import");
        Import.setSize(120,50);
        Import.setLocation(WIDTH * 4 / 5, HEIGHT / 10);

        Import.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        TargetImage = ImageIO.read(selectedFile);
                        int maxWidth = operateArea.getWidth(); // 获取 OperateArea 的宽度
                        int maxHeight = operateArea.getHeight(); // 获取 OperateArea 的高度
                        double widthRatio = (double) maxWidth / TargetImage.getWidth();
                        double heightRatio = (double) maxHeight / TargetImage.getHeight();
                        double ratio = Math.min(widthRatio, heightRatio)-0.05; // 取较小的缩放比例

                        int newWidth = (int) (TargetImage.getWidth() * ratio);
                        int newHeight = (int) (TargetImage.getHeight() * ratio);

                        int x = (500 - newWidth) / 2;
                        int y = (500 - newHeight) / 2;

                        Image scaledImage = TargetImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                        ImageIcon icon = new ImageIcon(scaledImage);
                        JLabel imageLabel = new JLabel(icon);
                        imageLabel.setBounds(x, y, newWidth, newHeight);
                        operateArea.removeAll(); // 清空 OperateArea 区域
                        operateArea.add(imageLabel); // 添加缩放后的图片
                        operateArea.revalidate(); // 重新布局
                        operateArea.repaint(); // 刷新显示
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        add(Import);
        setVisible(true);
    }
    private void addSelectKeepButton(){
        SelectKeep= new JButton("Select to Keep");
        SelectKeep.setSize(120,50);
        SelectKeep.setLocation(WIDTH * 4 / 5, HEIGHT / 10 + 210);
        add(SelectKeep);
        setVisible(true);
    }

    private void addSelectRemoveButton(){
        SelectRemove= new JButton("Select to Remove");
        SelectRemove.setSize(120,50);
        SelectRemove.setLocation(WIDTH * 4 / 5, HEIGHT / 10 + 280);
        add(SelectRemove);
        setVisible(true);
    }
    /*
    Retry: 用来在Shrink或者Expand后重新显示操作前原图。
     */
    private void addRetryButton(){
        Retry= new JButton("Retry");
        Retry.setSize(120,50);
        Retry.setLocation(WIDTH * 4 / 5, HEIGHT / 10 + 350);

        Retry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int maxWidth = operateArea.getWidth(); // 获取 OperateArea 的宽度
                    int maxHeight = operateArea.getHeight(); // 获取 OperateArea 的高度
                    double widthRatio = (double) maxWidth / TargetImage.getWidth();
                    double heightRatio = (double) maxHeight / TargetImage.getHeight();
                    double ratio = Math.min(widthRatio, heightRatio)-0.05; // 取较小的缩放比例

                    int newWidth = (int) (TargetImage.getWidth() * ratio);
                    int newHeight = (int) (TargetImage.getHeight() * ratio);

                    int x = (500 - newWidth) / 2;
                    int y = (500 - newHeight) / 2;

                    Image scaledImage = TargetImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaledImage);
                    JLabel imageLabel = new JLabel(icon);
                    imageLabel.setBounds(x, y, newWidth, newHeight);
                    operateArea.removeAll(); // 清空 OperateArea 区域
                    operateArea.add(imageLabel); // 添加缩放后的图片
                    operateArea.revalidate(); // 重新布局
                    operateArea.repaint(); // 刷新显示
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Please import an image first.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(Retry);
        setVisible(true);
    }

    private void addDownloadButton(){
        Download = new JButton("Download");
        Download.setSize(120,50);
        Download.setLocation(WIDTH * 4 / 5, HEIGHT / 10 + 420);

        Download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (TargetImage != null) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFolder = fileChooser.getSelectedFile();
                        String fileName = "downloaded_image.jpg"; // 定义文件名
                        File outputFile = new File(selectedFolder, fileName); // 创建新文件
                        try {
                            // 将图像写入新文件
                            ImageIO.write(TargetImage, "jpg", outputFile);
                            JOptionPane.showMessageDialog(null, "Image downloaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Failed to download image.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please import an image first.", "Error", JOptionPane.ERROR_MESSAGE);
                }
        }
        });

        add(Download);
        setVisible(true);
    }

    public static void main(String[] args) {
        GUI gui = new GUI(800,600);
    }
}
