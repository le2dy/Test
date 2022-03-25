package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainF extends JFrame {
    Player p = new Player();
    Ground g = new Ground();
    JPanel back = new JPanel(null);

    public MainF() {
        setTitle("Moving");
        setSize(1000, 630);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout());

        add(back);

        addComp(p, p.x, p.y, p.width, p.height);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                    p.left();
                }else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    p.right();
                }else if(e.getKeyCode() == KeyEvent.VK_UP) {
//                    p.up();
                    p.jump(MainF.this.g);
                }else if(e.getKeyCode() == KeyEvent.VK_DOWN && p.y + p.height < g.getY()) {
                    p.down();
                }
            }
        });

        addComp(g,0,400,g.getWidth(), g.getHeight());

        setVisible(true);
    }

    void addComp(JComponent p, int x, int y, int w, int h){
        back.add(p);
        p.setBounds(x, y, w, h);
    }

    public static void main(String[] args) {
        new MainF();
    }
}

class Ground extends JPanel {

    public Ground() {
        setSize(1000, 250);
        //setBackground(new Color(66,33,00));
        setOpaque(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(500, 39, Color.GREEN, 500, 40, new Color(66, 33, 0));

        g2.drawRect(0,0,1000,250);
        g2.setPaint(gp);
        g2.fillRect(0, 0, 1000, 250);
    }
}

class Player extends JPanel {
    int x = 475;
    int y = 350;
    int width = 50;
    int height = 50;

    public Player() {
        setLayout(new GridLayout());
        setSize(width, height);
        setBackground(Color.RED);
    }

    void jump(Ground g) {
        int bottom = y + height;

        new Thread(() -> {
            try {
                while(y > bottom - height - 20){
                        Player.this.setLocation(x, y--);
                        Thread.sleep(20);
                }
                while(this.y + height != g.getY()){
                    Player.this.setLocation(x, y++);
                    Thread.sleep(20);
                }
                Thread.interrupted();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    void up() {
        y -= 5;
        this.setLocation(x, y);
    }

    void down() {
        y += 5;
        this.setLocation(x, y);
    }

    void left() {
        x -= 5;
        this.setLocation(x, y);
    }

    void right() {
        x += 5;
        this.setLocation(x, y);
    }


}
