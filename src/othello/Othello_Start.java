package othello;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Othello_Start extends JFrame {
    JButton localPlayButton = new JButton("로컬 플레이");
    JButton onlinePlayButton = new JButton("온라인 플레이");
    JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
    JLabel mainTitleLabel = new JLabel("오셀로 Othello", SwingConstants.CENTER);

    public Othello_Start() {
        defaultSetting();

        setScreen();

        addActionToButton();

        setVisible(true);
    }

    void defaultSetting() {
        setSize(500, 300);
        setTitle("othello");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    void setScreen() {
        add(mainTitleLabel, "North");
        add(buttonPanel);

        mainTitleLabel.setFont(new Font("", Font.BOLD + Font.ITALIC, 35));

        buttonPanel.add(localPlayButton);
        buttonPanel.add(onlinePlayButton);

        buttonPanel.setBorder(new EmptyBorder(30, 30, 10, 30));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 20, 20, 20));
    }

    void addActionToButton() {
        localPlayButton.addActionListener(actionEvent -> {
            dispose();
            new Othello_Options();
        });

        onlinePlayButton.addActionListener(actionEvent -> showServerPanel());
    }

    void showServerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel label = new JLabel("서버 IP ");

        AtomicInteger port = new AtomicInteger(3000);

        ButtonGroup buttonGroup = new ButtonGroup();

        JRadioButton[] radioButton = {
                new JRadioButton("3000"),
                new JRadioButton("3100"),
                new JRadioButton("3200"),
                new JRadioButton("3300"),
        };

        panel.add(centerPanel);
        panel.add(southPanel, "South");

        centerPanel.add(label);

        for (JRadioButton radio : radioButton) {
            southPanel.add(radio);
            buttonGroup.add(radio);

            radio.addActionListener(actionEvent -> {
                if (radio.isSelected()) port.set(Integer.parseInt(radio.getText()));
            });
        }
        radioButton[0].setSelected(true);

        String ip = JOptionPane.showInputDialog(null, panel, "서버 정보 입력", JOptionPane.PLAIN_MESSAGE, null, null, "127.0.0.1").toString();

        dispose();
        new WaitingRoom(ip, port.get());
    }

    public static void main(String[] args) {
        new Othello_Start();
    }
}
