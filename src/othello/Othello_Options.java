package othello;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Othello_Options extends JFrame {
    JButton versusHuman = new JButton("Player VS Player");
    JButton versusCPU = new JButton("Player VS CPU");
    JButton simulate = new JButton("CPU VS CPU");
    String player = "Player";

    public Othello_Options() {
        defaultSetting();

        setScreen();

        addActionToButton();
    }

    void addActionToButton() {
        versusHuman.addActionListener(actionEvent -> {
            dispose();
            new Setting(player, player);
        });

        versusCPU.addActionListener(actionEvent -> {
            dispose();
            new Setting(player, "CPU");
        });

        simulate.addActionListener(actionEvent -> {
            dispose();
            new Othello().useBot();
        });
    }

    void setScreen() {
        add(versusHuman);
        add(versusCPU);
        add(simulate);
    }

    void defaultSetting() {
        setSize(500, 300);
        setTitle("othello");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 0, 10));

        ((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Othello_Start();
            }
        });

        setVisible(true);
    }
}
