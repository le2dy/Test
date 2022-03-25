package project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Test extends JFrame {
    static Player p = new Player();
    Thread th;
    int dirX = 0;
    int dirY = 0;
    static float speed = 2;
    static int jumpHeight = 100;
    boolean isJump = false;
    boolean isDrop = false;
    OptionPanel op = new OptionPanel();

    public Test() {
        setSize(1000, 1000);
        setTitle("Moving test");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(null);

        add(p);
        p.setBounds(475, 475, 50, 50);

        JLabel jl = new JLabel("", SwingConstants.CENTER);
        add(jl);
        jl.setBounds(0, 0, 100, 100);


        th = new Thread(() -> {
            while (true) {
                move();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    th.interrupt();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    dirX = -1;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    dirX = 1;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && e.isControlDown() /*&& !isDrop*/) {
                    dirY = 1;
//                    drop();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && !isJump) {
                    jump();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    dirX = 0;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    dirY = 0;
                }
            }
        });

        th.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });

        op.setVisible(true);
        setVisible(true);
    }

    void jump() {
        int bottom = p.y + p.height;

        new Thread(() -> {
            try {
                isJump = true;
                while (p.y > bottom - p.height - jumpHeight) {
                    dirY = -1;
                    move();
                    Thread.sleep(20);
                }
                while (p.y + p.height <= bottom) {
                    dirY = 1;
                    move();
                    Thread.sleep(20);
                }
                dirY = 0;
                isJump = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }).start();
    }

    void drop() {
        int bottom = p.y + p.height;

        new Thread(() -> {
            try {
                isDrop = true;
                while (p.y < bottom + jumpHeight) {
                    dirY = 1;
                    move();
                    Thread.sleep(20);
                }
                dirY = 0;
                isDrop = false;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }).start();
    }

    void move() {

        p.x += dirX * speed;
        p.y += dirY * speed;

        p.setLocation(p.x, p.y);
    }

    public static void main(String[] args) {
        new Test();
    }
}

class OptionPanel extends JDialog {
    JPanel centerP = new JPanel(new GridLayout(0, 1, 0, 10));
    JPanel southP = new JPanel(new GridLayout(1, 0, 10, 0));
    JSlider[] slider = new JSlider[]{
            new JSlider(1, 10, 2),
            new JSlider(1, 10, 2),
            new JSlider(1, 10, 2)
    };
    JButton save = new JButton("Save");
    JButton reset = new JButton("Reset");
    String[] optionName =
            {"Run Speed", "Jump Height", "etc"};

    public OptionPanel() {
        setTitle("Options");
        setSize(300, 300);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(2);

        add(centerP);
        add(southP, "South");

        for (int i = 0; i < slider.length; i++) {
            JPanel temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel j = new JLabel();

            temp.add(j);
            temp.add(slider[i]);
            slider[i].setPaintLabels(true);
            slider[i].setPaintTicks(true);
            slider[i].setMajorTickSpacing(1);

            j.setText(optionName[i]);
            j.setPreferredSize(new Dimension(80, 30));

            centerP.add(temp);
        }

        southP.add(save);
        southP.add(reset);

        save.addActionListener(e -> {
            Test.speed = slider[0].getValue();
            Test.jumpHeight = slider[1].getValue() * 10;
        });

        reset.addActionListener(e -> {
            Test.speed = 2;
            Test.jumpHeight = 100;
            Test.p.setBounds(475, 475, 50, 50);

            for (int i = 0; i < 3; i++) {
                slider[i].setValue(2);
            }

            repaint();
        });

        ((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
        setFocusable(false);
    }
}