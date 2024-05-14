import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame{
    private JLabel dimensionLabel;
    private String imgPath;
    private String initialImagePath;

    public JPanel operateArea;
    private ImageArea imageArea;

    public JButton Shrink;
    public JButton Expand;
    public JButton Import;
    public JButton SelectKeep;
    public JButton SelectRemove;
    public JButton Retry;
    public JButton Download;

    private final int WIDTH;
    private final int HEIGHT;


    public GUI(int width, int height){
        WIDTH = width;
        HEIGHT = height;
        setTitle("2024 CS203b Project Demo");
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null); // Center the window.
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //程序右上方的叉关闭
        setLayout(null);

        //Slider
        JSlider zoomSlider = new JSlider(JSlider.HORIZONTAL, 0, 200, 100);
        zoomSlider.setBounds(50, 510, 400, 50);
        zoomSlider.setPreferredSize(new Dimension(zoomSlider.getPreferredSize().width, zoomSlider.getPreferredSize().height / 2));
        zoomSlider.setMajorTickSpacing(50);
        zoomSlider.setMinorTickSpacing(10);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setPaintLabels(true);
        JLabel valueLabel = new JLabel("Size: 100%");
        valueLabel.setSize(200,20);
        valueLabel.setLocation(460, 515);
        add(valueLabel);
        zoomSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = zoomSlider.getValue();
                imageArea.setScale(value / 100.0);
                valueLabel.setText("Size: " + value + "%");
            }
        });
        add(zoomSlider);

        imageArea = new ImageArea();
        add(imageArea);

        //Dimension
        addDimensionLabel();

        //Button
        addSelectKeepButton();
        addSelectRemoveButton();
        addExpandButton();
        addShrinkButton();
        addImportButton();
        addRetryButton();
        addDownloadButton();

        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void addDimensionLabel(){
        dimensionLabel = new JLabel("Dimension:");
        dimensionLabel.setSize(200,20);
        dimensionLabel.setLocation(550, 20);
        add(dimensionLabel);
        dimensionLabel.setVisible(true);
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
                    imgPath = selectedFile.getAbsolutePath();
                    initialImagePath = imgPath;
                    imageArea.loadImage(selectedFile);
                    imageArea.copyImage = imageArea.image;
                    repaint();
                    dimensionLabel.setText("Dimension: "+imageArea.image.getWidth()+"x"+imageArea.image.getHeight());

                }
            }
        });

        add(Import);
        setVisible(true);
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

                SeamCarving sc = new SeamCarving(imgPath);
                int choice = JOptionPane.showOptionDialog(null, "Please select your mode", "Selection", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, new Object[] { "value", "ratio" }, "value");
                // 处理用户选择
                if (choice == JOptionPane.YES_OPTION) {
                    int result = JOptionPane.showConfirmDialog(null, panel, "Enter Width and Height", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            // 获取用户输入的宽度和高度改——————————————————————————！！！
                            sc.ChangeWidth = imageArea.image.getWidth() - Integer.parseInt(widthField.getText());
                            sc.ChangeHeight = imageArea.image.getHeight() - Integer.parseInt(heightField.getText());
                            // 可以在这里进行一些额外的验证
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Please enter valid numbers for Width and Height.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        // Logic
                        sc.cutWidth(sc.ChangeWidth);
                        sc.cutHeight(sc.ChangeHeight);//这个方法会在img文件夹生成new_img.jpg
                        imgPath = "Code/img/new_image.jpg";
                    }
                } else if (choice == JOptionPane.NO_OPTION) {
                    int result = JOptionPane.showConfirmDialog(null, panel, "Enter Width Ratio and Height Ratio", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            // 获取用户输入的ratio
                            sc.WidthRatio = 1-Double.parseDouble(widthField.getText());
                            sc.HeightRatio = 1-Double.parseDouble(heightField.getText());
                            // 可以在这里进行一些额外的验证
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Please enter valid numbers for Ratio.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        // Logic
                        sc.cutWidth(sc.WidthRatio);
                        sc.cutHeight(sc.HeightRatio);//这个方法会在img文件夹生成new_img.jpg
                        imgPath = "Code/img/new_image.jpg";
                    }
                }
                imageArea.removeAll();
                imageArea.repaint();
                imageArea.loadImage(new File(imgPath));
                dimensionLabel.setText("Dimension: "+ imageArea.image.getWidth()+"x"+ imageArea.image.getHeight());
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

                SeamCarving sc = new SeamCarving(imgPath);
                int choice = JOptionPane.showOptionDialog(null, "Please select your mode", "Selection", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, new Object[] { "value", "ratio" }, "value");
                // 处理用户选择
                if (choice == JOptionPane.YES_OPTION) {
                    int result = JOptionPane.showConfirmDialog(null, panel, "Enter Width and Height", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            // 获取用户输入的宽度和高度改——————————————————————————！！！
                            sc.ChangeWidth = Integer.parseInt(widthField.getText()) - imageArea.image.getWidth();
                            sc.ChangeHeight = Integer.parseInt(heightField.getText()) - imageArea.image.getHeight();
                            // 可以在这里进行一些额外的验证
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Please enter valid numbers for Width and Height.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        // Logic
                        sc.expandWidth(sc.ChangeWidth);
                        sc.expandHeight(sc.ChangeHeight);//这个方法会在img文件夹生成new_img.jpg
                        imgPath = "Code/img/new_image.jpg";
                    }
                } else if (choice == JOptionPane.NO_OPTION) {
                    int result = JOptionPane.showConfirmDialog(null, panel, "Enter Width Ratio and Height Ratio", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            // 获取用户输入的ratio
                            sc.WidthRatio = 1-Double.parseDouble(widthField.getText());
                            sc.HeightRatio = 1-Double.parseDouble(heightField.getText());
                            // 可以在这里进行一些额外的验证
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Please enter valid numbers for Ratio.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        // Logic
                        sc.expandWidth(sc.WidthRatio);
                        sc.expandHeight(sc.HeightRatio);//这个方法会在img文件夹生成new_img.jpg
                        imgPath = "Code/img/new_image.jpg";
                    }
                }
                imageArea.removeAll();
                imageArea.repaint();
                imageArea.loadImage(new File(imgPath));
                dimensionLabel.setText("Dimension: "+ imageArea.image.getWidth()+"x"+ imageArea.image.getHeight());
            }
        });

        add(Expand);
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
                imageArea.image = imageArea.copyImage;
                imgPath = initialImagePath;
                repaint();
                dimensionLabel.setText("Dimension: "+imageArea.image.getWidth()+"x"+imageArea.image.getHeight());
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
                if (imageArea.image != null) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFolder = fileChooser.getSelectedFile();
                        String fileName = "downloaded_image.jpg"; // 定义文件名
                        File outputFile = new File(selectedFolder, fileName); // 创建新文件
                        try {
                            // 将图像写入新文件
                            ImageIO.write(imageArea.image, "jpg", outputFile);
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

}
