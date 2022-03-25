package project;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.SecureRandom;
import java.util.*;

public class Snake extends JFrame {
    JLabel[] jl = new JLabel[100];
    static Queue<String> present = new LinkedList<>();
    static Queue<String> next = new LinkedList<>();
    String[][] map = new String [10][10];
    Thread th;
    int dirX = 0;
    int dirY = 0;
    int saveX = dirX;
    int saveY = dirY;

    public Snake() {
        setTitle("Snake");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(10, 10));

        present.offer("1,0");
        present.offer("0,0");

        for (int i = 0;i < jl.length;i++) {
            jl[i] = new JLabel("", SwingConstants.CENTER);
            add(jl[i]);
            jl[i].setBorder(new LineBorder(Color.black));
            jl[i].setOpaque(true);
            jl[i].setForeground(Color.BLACK);
        }

//        clear();
        Arrays.stream(map).forEach(str -> Arrays.fill(str, ""));

        mkSnake();
        mkApple();

        th = new Thread(()->{
            while(true){
                try {
                    move();
                    saveX = dirX;
                    saveY = dirY;
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    th.interrupt();
                    e.printStackTrace();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(dirX ==0 && dirY == 0) {
                    if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                        dirX = 0;
                        dirY = 1;
                    }else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        dirX = 1;
                        dirY = 0;
                    }
                }
                switch (e.getKeyCode()){
                    case KeyEvent.VK_RIGHT:
                        dirX = 1;
                        dirY = 0;
                        break;
                    case KeyEvent.VK_LEFT:
                        dirX = -1;
                        dirY = 0;
                        break;
                    case KeyEvent.VK_UP:
                        dirX = 0;
                        dirY = -1;
                        break;
                    case KeyEvent.VK_DOWN:
                        dirX = 0;
                        dirY = 1;
                        break;
                    default:
                        break;
                }
            }
        });

        th.start();

        setVisible(true);
    }

    void move() {
        assert present.peek() != null;
        String[] coordinate = present.peek().split(",");
        int x = toInt(coordinate[0]) + dirX;
        int y = toInt(coordinate[1]) + dirY;

        if(present.contains(x+","+y)) {
            ArrayList<String> arrayList = new ArrayList<>(present);
            if(arrayList.indexOf(x+","+y) == 1){
                dirY = saveY;
                dirX = saveX;
                return;
            }else if(arrayList.indexOf(x+","+y) == 0){
                return;
            } else {
                gameOver();
            }
        }

        if(x < 0 || x >= map.length || y < 0 || y >= map.length) {
            gameOver();
        }

        next.offer(x+","+y);


        for (int i = present.size() - 1; i > 0; i--) {
            next.offer(present.poll());
        }

        coordinate = Objects.requireNonNull(present.poll()).split(",");
        x = toInt(coordinate[0]);
        y = toInt(coordinate[1]);

        map[y][x] = "";
        jl[y * 10 + x].setText("");
        jl[y * 10 + x].setBackground(null
        );

        present.clear();
        present.addAll(next);
        next.clear();

        isEat();
        isCollide();
        mkSnake();
    }

    void isCollide() {
        ArrayList<String> snake = new ArrayList<>(present);

        if(snake.stream().filter(s-> {
            assert present.peek() != null;
            return s.contains(present.peek());
        }).count() >= 2){
            gameOver();
        }
    }

    void isEat() {
        assert present.peek() != null;
        String[] cord = present.peek().split(",");
        int x = toInt(cord[0]);
        int y = toInt(cord[1]);

        if(map[y][x].equals("A")){
            ArrayList<String> snake = new ArrayList<>(present);
            String[] coordinate1 = snake.get(snake.size() - 1).split(",");
            String[] coordinate2 = snake.get(snake.size() - 2).split(",");

            int x1 = toInt(coordinate1[0]);
            int y1 = toInt(coordinate1[1]);
            int x2 = toInt(coordinate2[0]);
            int y2 = toInt(coordinate2[1]);

            x = -(x2 - x1) + x1;
            y = -(y2 - y1) + y1;

            if(x < 0 || x >= map.length || y < 0 || y >= map.length){
                assert present.peek() != null;
                String head = present.peek().split(",")[1];
                int hY;
                hY = toInt(head);

                x = x2;
                if(hY > y1){
                    y = y2 - 1;
                }else{
                    y = y2 + 1;
                }
            }

            present.offer(x+","+y);

            mkApple();
        }

    }

    void gameOver() {
        JOptionPane.showMessageDialog(this, "END", "끝", JOptionPane.ERROR_MESSAGE);
        th.interrupt();
        System.exit(0);
    }

    int toInt(String str) {
        return Integer.parseInt(str);
    }

    void mkSnake() {
//        clear();

        ArrayList<String> snake = new ArrayList<>(present);

        for (String s : snake) {
            String[] coordinate = s.split(",");
            int x = toInt(coordinate[0]);
            int y = toInt(coordinate[1]);

            map[y][x] = "S";
            jl[y * 10 + x].setText("S");
            jl[y * 10 + x].setBackground(Color.GREEN.darker());
        }
    }

    void mkApple() {
        Random random = new SecureRandom();

        int x = random.nextInt(10);
        int y = random.nextInt(10);

        if (map[y][x].equals("S")) {
            mkApple();
        }else {
            map[y][x] = "A";
            jl[y * 10 + x].setText("A");
            jl[y * 10 + x].setBackground(Color.RED.darker());
        }
    }

    public static void main(String[] args) {
        new Snake();
    }
}
/*    void clear() {
        for (int i = 0; i < jl.length; i++) {
            if(jl[i].getText().equals("A")) continue;
            jl[i].setText("");
            jl[i].setBackground(Color.WHITE);
            int x = i % 10;
            int y = i / 10;
            map[y][x] = "";
        }

        repaint();
    }  */