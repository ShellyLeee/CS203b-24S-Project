import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame{
    private JLabel dimensionLabel;
    private String imgPath;
    private String initialImagePath;

    private final ImageArea imageArea;

    public JButton Shrink;
    public JButton Expand;
    public JButton Import;
    public JButton SelectKeep;
    public JButton SelectRemove;
    public JButton Retry;
    public JButton Download;
    public JButton Help;
    public JLabel statusLabel;

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
        zoomSlider.setBounds(60, 510, 400, 50);
        zoomSlider.setPreferredSize(new Dimension(zoomSlider.getPreferredSize().width, zoomSlider.getPreferredSize().height / 2));
        zoomSlider.setMajorTickSpacing(50);
        zoomSlider.setMinorTickSpacing(10);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setPaintLabels(true);
        JLabel valueLabel = new JLabel("Size: 100%");
        valueLabel.setSize(200,20);
        valueLabel.setLocation(470, 515);
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
        addStatusLabel();

        //Button
        addSelectKeepButton();
        addSelectRemoveButton();
        addExpandButton();
        addShrinkButton();
        addImportButton();
        addRetryButton();
        addDownloadButton();
        addHelpButton();
        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    //图片大小标签
    private void addDimensionLabel(){
        dimensionLabel = new JLabel("Dimension:");
        dimensionLabel.setSize(200,20);
        dimensionLabel.setLocation(550, 20);
        add(dimensionLabel);
        dimensionLabel.setVisible(true);
    }

    private void addStatusLabel(){
        statusLabel = new JLabel("Picture status:");
        statusLabel.setSize(200,50);
        statusLabel.setLocation(550, 20);
        add(statusLabel);
        statusLabel.setVisible(true);
    }

    private void addHelpButton(){
        Help = new JButton("Help");
        Help.setSize(50,40);
        Help.setLocation(8, 8);

        Help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 创建一个 JTextArea，用于展示内容
                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setText("在操作前请参考我们的说明文档。" + System.lineSeparator() +""+ System.lineSeparator() +
                        "我们的界面布局为：左侧灰色区域为图片操作区，灰色区域右侧的Dimension代表图片目前的尺寸（宽*高），下方进度条可灵活调节图片大小方便观察（size代表放缩程度），右侧有7个按钮可支持图片相关的操作。"+ System.lineSeparator() + ""+ System.lineSeparator() +
                "按钮使用方法：" + System.lineSeparator() + "1. Import：导入图片。请选择相关图片路径导入图片"+ System.lineSeparator() + "2. Shrink：不选定相关区域，缩小图片"+ System.lineSeparator() + "3. Expand：不选定相关区域，放大图片"+ System.lineSeparator() + "4. Shrink: SelectKeep：选定相关区域以保持不变，缩小图片" + System.lineSeparator() + "5. Shrink: SelectRemove：选定相关区域以增强删除，缩小图片"+ System.lineSeparator() + "6. Retry：重新加载最初版本import的图片"+ System.lineSeparator() + "7. Download：下载现在的图片。请选择相关图片路径导出图片（jpg格式）" +System.lineSeparator()+ ""+ System.lineSeparator() +
                        "注意事项：" + System.lineSeparator() + "1. 在Shrink、Expand、SelectKeep、SelectRemove时，点击后若出现Mode Selection文本框，请选择依据比例（ratio）或依据数值（value）进行图片的修改操作。"+ System.lineSeparator() +"如果选择比例，选择缩小模式，您输入的ratio必须位于(0,1)间；选择放大模式，您输入的ratio必须大于1。"+ System.lineSeparator() +"如果选择数值，您输入的value必须合乎逻辑：缩小模式下，输入的value要小于原数值；放大模式下，输入的value要大于原数值。" + System.lineSeparator() +""+ System.lineSeparator() + "2. 您可以在导入图片后长按图片中某一点拖出一个红色矩形框，这个框即为选择区域（Selected Area）。如果要使用SelectKeep或SelectRemove模式，请确保您在点击SelectKeep、SelectRemove按钮前已经选定相应区域，再进行后续操作。");

                // 将 JTextArea 放入 JScrollPane 中
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 500));

                // 创建一个展示对话框
                JOptionPane.showMessageDialog(null, scrollPane, "Help", JOptionPane.PLAIN_MESSAGE);
            }
        });

        add(Help);
        setVisible(true);
    }

    //导入
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

    //缩小
    private void addShrinkButton(){
        Shrink = new JButton("Shrink");
        Shrink.setSize(120,50);
        Shrink.setLocation(WIDTH * 4 / 5, HEIGHT / 10 + 70);

        Shrink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Picture status:");
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
                int choice = JOptionPane.showOptionDialog(null, "Shrink: Please select your mode", "Mode Selection", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, new Object[] { "value", "ratio" }, "value");
                // 处理用户选择
                if (choice == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "Please enter the value which is SMALLER than original size.", "Alert", JOptionPane.INFORMATION_MESSAGE);
                    int result = JOptionPane.showConfirmDialog(null, panel, "Shrink: Enter Width and Height", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            // 获取用户输入的宽度和高度
                            sc.ChangeWidth = imageArea.image.getWidth() - Integer.parseInt(widthField.getText());
                            sc.ChangeHeight = imageArea.image.getHeight() - Integer.parseInt(heightField.getText());
                            // 可以在这里进行一些额外的验证
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Please enter valid numbers for Width and Height.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        // Logic
                        sc.cutWidth(sc.ChangeWidth);
                        sc.cutHeight(sc.ChangeHeight);//这个方法会在img文件夹生成new_img.jpg
                        statusLabel.setText("Picture status: Updated");
                        imgPath = "Code/img/new_image.jpg";
                    }
                } else if (choice == JOptionPane.NO_OPTION) {
                    JOptionPane.showMessageDialog(null, "Please enter the ratio which is SMALLER than 1.", "Alert", JOptionPane.INFORMATION_MESSAGE);
                    int result = JOptionPane.showConfirmDialog(null, panel, "Shrink: Enter Width Ratio and Height Ratio", JOptionPane.OK_CANCEL_OPTION);
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
                        statusLabel.setText("Picture status: Updated");
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

    //扩大
    private void addExpandButton(){
        Expand = new JButton("Expand");
        Expand.setSize(120,50);
        Expand.setLocation(WIDTH * 4 / 5, HEIGHT / 10 + 140);

        Expand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Picture status:");
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
                int choice = JOptionPane.showOptionDialog(null, "Expand: Please select your mode", "Mode Selection", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, new Object[] { "value", "ratio" }, "value");
                // 处理用户选择
                if (choice == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "Please enter the value which is BIGGER than original size.", "Alert", JOptionPane.INFORMATION_MESSAGE);
                    int result = JOptionPane.showConfirmDialog(null, panel, "Expand: Enter Width and Height", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            // 获取用户输入的宽度和高度
                            sc.ChangeWidth = Integer.parseInt(widthField.getText()) - imageArea.image.getWidth();
                            sc.ChangeHeight = Integer.parseInt(heightField.getText()) - imageArea.image.getHeight();
                            // 可以在这里进行一些额外的验证
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Please enter valid numbers for Width and Height.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        // Logic
                        sc.expandWidth(sc.ChangeWidth);
                        sc.expandHeight(sc.ChangeHeight);//这个方法会在img文件夹生成new_img.jpg
                        statusLabel.setText("Picture status: Updated");
                        imgPath = "Code/img/new_image.jpg";
                    }
                } else if (choice == JOptionPane.NO_OPTION) {
                    JOptionPane.showMessageDialog(null, "Please enter the ratio which is BIGGER than 1.", "Alert", JOptionPane.INFORMATION_MESSAGE);
                    int result = JOptionPane.showConfirmDialog(null, panel, "Expand: Enter Width Ratio and Height Ratio", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            // 获取用户输入的ratio
                            sc.WidthRatio = Double.parseDouble(widthField.getText()) - 1;
                            sc.HeightRatio = Double.parseDouble(heightField.getText()) - 1;
                            // 可以在这里进行一些额外的验证
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Please enter valid numbers for Ratio.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        // Logic
                        sc.expandWidth(sc.WidthRatio);
                        sc.expandHeight(sc.HeightRatio);//这个方法会在img文件夹生成new_img.jpg
                        statusLabel.setText("Picture status: Updated");
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
        SelectKeep= new JButton("Shrink: SelectKeep");
        SelectKeep.setSize(160,50);
        SelectKeep.setLocation(WIDTH * 4 / 5 - 15, HEIGHT / 10 + 210);

        SelectKeep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Picture status:");
                // 创建一个对话框
                JPanel panel = new JPanel();
                JTextField widthField = new JTextField(5);
                JTextField heightField = new JTextField(5);

                panel.add(new JLabel("Width:"));
                panel.add(widthField);
                panel.add(Box.createHorizontalStrut(15)); // 添加间隔
                panel.add(new JLabel("Height:"));
                panel.add(heightField);

                SeamCarving sc = null;
                int confirm = JOptionPane.showConfirmDialog(null, "Use the current selected area?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
                if (confirm == JOptionPane.OK_OPTION) {
                    sc = new SeamCarving(imgPath, new Point(imageArea.newBoxX1,imageArea.newBoxY1), new Point(imageArea.newBoxX4,imageArea.newBoxY4));
                    int choice = JOptionPane.showOptionDialog(null, "Select to Keep: Please select your mode", "Mode Selection", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, new Object[] { "value", "ratio" }, "value");
                    // 处理用户选择
                    if (choice == JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(null, "Please enter the value which is SMALLER than original size.", "Alert", JOptionPane.INFORMATION_MESSAGE);
                        int result = JOptionPane.showConfirmDialog(null, panel, "Select to Keep: Enter Width and Height", JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            try {
                                // 获取用户输入的宽度和高度
                                sc.ChangeWidth = imageArea.image.getWidth() - Integer.parseInt(widthField.getText());
                                sc.ChangeHeight = imageArea.image.getHeight() - Integer.parseInt(heightField.getText());
                                // 可以在这里进行一些额外的验证
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, "Please enter valid numbers for Width and Height.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            // Logic
                            sc.cutWidth(sc.ChangeWidth);
                            sc.cutHeight(sc.ChangeHeight);//这个方法会在img文件夹生成new_img.jpg
                            statusLabel.setText("Picture status: Updated");
                            imgPath = "Code/img/new_image.jpg";
                        }
                    } else if (choice == JOptionPane.NO_OPTION) {
                        JOptionPane.showMessageDialog(null, "Please enter the value which is SMALLER than 1.", "Alert", JOptionPane.INFORMATION_MESSAGE);
                        int result = JOptionPane.showConfirmDialog(null, panel, "Select to Keep: Enter Width Ratio and Height Ratio", JOptionPane.OK_CANCEL_OPTION);
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
                            statusLabel.setText("Picture status: Updated");
                            imgPath = "Code/img/new_image.jpg";
                        }
                    }
                    imageArea.removeAll();
                    imageArea.startPoint = null;
                    imageArea.endPoint = null;
                    imageArea.repaint();
                    imageArea.loadImage(new File(imgPath));
                    dimensionLabel.setText("Dimension: "+ imageArea.image.getWidth()+"x"+ imageArea.image.getHeight());
                }
            }
        });

        add(SelectKeep);
        setVisible(true);
    }

    private void addSelectRemoveButton(){
        SelectRemove= new JButton("Shrink: SelectRemove");
        SelectRemove.setSize(160,50);
        SelectRemove.setLocation(WIDTH * 4 / 5 - 15, HEIGHT / 10 + 280);

        SelectRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Picture status:");
                // 创建一个对话框
                JPanel panel = new JPanel();
                JTextField widthField = new JTextField(5);
                JTextField heightField = new JTextField(5);
                panel.add(new JLabel("Width:"));
                panel.add(widthField);
                panel.add(Box.createHorizontalStrut(15)); // 添加间隔
                panel.add(new JLabel("Height:"));
                panel.add(heightField);

                SeamCarving sc = null;
                int confirm = JOptionPane.showConfirmDialog(null, "Use the current selected area?", "Confirmation", JOptionPane.OK_CANCEL_OPTION);
                if (confirm == JOptionPane.OK_OPTION) {
                    sc = new SeamCarving(imgPath, new Point(imageArea.newBoxX1,imageArea.newBoxY1), new Point(imageArea.newBoxX4,imageArea.newBoxY4),1);
                    int choice = JOptionPane.showOptionDialog(null, "Select to Remove: Please select your mode", "Mode Selection", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, new Object[] { "value", "ratio" }, "value");
                    // 处理用户选择
                    if (choice == JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(null, "Please enter the value which is SMALLER than original size.", "Alert", JOptionPane.INFORMATION_MESSAGE);
                        int result = JOptionPane.showConfirmDialog(null, panel, "Select to Remove: Enter Width and Height", JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            try {
                                // 获取用户输入的宽度和高度
                                sc.ChangeWidth = imageArea.image.getWidth() - Integer.parseInt(widthField.getText());
                                sc.ChangeHeight = imageArea.image.getHeight() - Integer.parseInt(heightField.getText());
                                // 可以在这里进行一些额外的验证
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(null, "Please enter valid numbers for Width and Height.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            // Logic
                            sc.cutWidth(sc.ChangeWidth);
                            sc.cutHeight(sc.ChangeHeight);//这个方法会在img文件夹生成new_img.jpg
                            statusLabel.setText("Picture status: Updated");
                            imgPath = "Code/img/new_image.jpg";
                        }
                    } else if (choice == JOptionPane.NO_OPTION) {
                        JOptionPane.showMessageDialog(null, "Please enter the ratio which is SMALLER than 1.", "Alert", JOptionPane.INFORMATION_MESSAGE);
                        int result = JOptionPane.showConfirmDialog(null, panel, "Select to Keep: Enter Width Ratio and Height Ratio", JOptionPane.OK_CANCEL_OPTION);
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
                            statusLabel.setText("Picture status: Updated");
                            imgPath = "Code/img/new_image.jpg";
                        }
                    }
                    imageArea.removeAll();
                    imageArea.startPoint = null;
                    imageArea.endPoint = null;
                    imageArea.repaint();
                    imageArea.loadImage(new File(imgPath));
                    dimensionLabel.setText("Dimension: "+ imageArea.image.getWidth()+"x"+ imageArea.image.getHeight());
                }
            }
        });

        add(SelectRemove);
        setVisible(true);
    }

    //Retry: 用来在Shrink或者Expand后重新显示操作前原图。
    private void addRetryButton(){
        statusLabel.setText("Picture status:");
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

    //下载目前显示的图片
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
