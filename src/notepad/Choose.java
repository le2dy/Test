package notepad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Choose extends JFrame {
    static final JPanel centerPanel = new JPanel(new GridLayout(1, 1));
    static final JPanel jPanel = new JPanel();
    static final JScrollPane jScrollPane = new JScrollPane(jPanel);
    static final JButton jButton = new JButton("새 파일 생성");

    public Choose() {
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(centerPanel);
        add(jButton, "South");
        centerPanel.add(jScrollPane);

        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        loadFiles();

        jPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setUnselectLabel();
            }
        });

        jButton.addActionListener(actionEvent -> {
            try {
                String name = JOptionPane.showInputDialog(null, "파일 이름을 입력해주세요.","", JOptionPane.PLAIN_MESSAGE);

                File file1 = new File("/home/leedongyun/Desktop/notes/" + name + ".txt");
                if(file1.exists()) {
                    JOptionPane.showMessageDialog(null, "이미 파일이 존재합니다.", "", JOptionPane.ERROR_MESSAGE);
                }else {
                    String text = "";
                    FileOutputStream fileOutputStream = new FileOutputStream("/home/leedongyun/Desktop/notes/" + name + ".txt");
                    fileOutputStream.write(text.getBytes());
                    loadFiles();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        setVisible(true);
    }

    void setUnselectLabel() {
        Component[] labels = jPanel.getComponents();

        for (Component label: labels) {
            label.setBackground(null);
            label.setForeground(Color.BLACK);
        }
    }

    void loadFiles() {
        jPanel.removeAll();

        String pathDir = "/";
        String filePath = "home/leedongyun/Desktop/notes/";
        filePath = pathDir.concat(filePath);

        File file = new File(filePath);
        File[] files = file.listFiles();

        for (int i = 0; i < Objects.requireNonNull(file.listFiles()).length; i++) {
            assert files != null;
            JLabel jLabel = new JLabel(files[i].getName());
            jPanel.add(jLabel);
            jLabel.setOpaque(true);

            jLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    setUnselectLabel();

                    jLabel.setBackground(new Color(61, 174, 233));
                    jLabel.setForeground(Color.WHITE);
                    repaint();
                    revalidate();
                    if(e.getClickCount() == 2) {
                        new Note(jLabel.getText());
                        dispose();
                    }
                }
            });
        }

        repaint();
        revalidate();
    }

    public static void main(String[] args) {
        new Choose();
    }
}