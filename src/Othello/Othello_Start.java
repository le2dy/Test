package Othello;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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
        setTitle("Othello");
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
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 20, 20, 20));
    }

    void addActionToButton() {
        localPlayButton.addActionListener(actionEvent -> {
            new Othello_Options();
        });

        onlinePlayButton.addActionListener(actionEvent -> {
            JOptionPane.showMessageDialog(this, "아직 안됨.", "온라인 플레이", JOptionPane.ERROR_MESSAGE);
        });
    }

    public static void main(String[] args) {
        new Othello_Start();
    }
}
