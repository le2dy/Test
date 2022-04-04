package othello;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Setting extends JFrame {
    JButton switchColor = new JButton("⇄");
    JButton okButton = new JButton("확인");
    JButton cancelButton = new JButton("취소");
    JPanel centerPanel = new JPanel(new GridBagLayout());
    JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JPanel player = new JPanel(new BorderLayout());
    JPanel cpu = new JPanel(new BorderLayout());
    JLabel player1;
    JLabel player2;
    JLabel color1;
    JLabel color2;
    String p1;
    String p2;

    public Setting(String p1, String p2) {
        this.p1 = p1;
        this.p2 = p2;

        defaultSetting();

        addAction();

        setScreen();
    }

    void addAction() {
        switchColor.addActionListener(actionEvent -> {
            String change = color1.getText();
            color1.setText(color2.getText());
            color2.setText(change);
        });

        okButton.addActionListener(actionEvent -> {
            if(p2.equals("CPU")) {
                dispose();
                Othello othello = new Othello();

                othello.withBot(color2.getText());
            } else {
                dispose();
                new Othello();
            }
        });

        cancelButton.addActionListener(actionEvent -> {
            dispose();
            new Othello_Options();
        });
    }

    void setScreen() {
        add(centerPanel);
        add(southPanel, "South");

        centerPanel.add(player);
        centerPanel.add(switchColor);
        centerPanel.add(cpu);

        player1 = new JLabel(p1, SwingConstants.CENTER);
        player2 = new JLabel(p2, SwingConstants.CENTER);

        color1 = new JLabel("Black", SwingConstants.CENTER);
        color2 = new JLabel("White", SwingConstants.CENTER);

        player1.setFont(new Font("", Font.PLAIN, 20));
        player2.setFont(new Font("", Font.PLAIN, 20));

        color1.setFont(new Font("", Font.PLAIN, 40));
        color2.setFont(new Font("", Font.PLAIN, 40));

        player.add(player1, "North");
        player.add(color1);

        cpu.add(player2, "North");
        cpu.add(color2);

        southPanel.add(okButton);
        southPanel.add(cancelButton);

        player.setPreferredSize(new Dimension(200, 200));
        cpu.setPreferredSize(new Dimension(200, 200));
    }

    void defaultSetting() {
        setSize(500, 300);
        setTitle("othello");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Othello_Options();
            }
        });
        setVisible(true);
    }
}
