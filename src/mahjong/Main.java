package mahjong;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Main extends JFrame {

    public Main() {
        setSize(500, 300);
        setTitle("Mahjong");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        JLabel titleLabel = new JLabel("마작 Mahjong", SwingConstants.CENTER);
        JButton startButton = new JButton("Start");

        titleLabel.setFont(new Font("", Font.BOLD + Font.ITALIC, 35));
        titleLabel.setVerticalAlignment(SwingConstants.TOP);

        startButton.setBackground(Color.WHITE);
        startButton.setBorder(new LineBorder(Color.BLACK));
        startButton.setFont(new Font("", Font.PLAIN, 25));

        add(titleLabel);
        add(startButton);

        ((JPanel)getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }
}
