package othello;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class Othello extends JFrame {
    JPanel numberWest = new JPanel(new GridLayout(8, 0));
    JPanel numberEast = new JPanel(new GridLayout(8, 0));
    JPanel alphabetNorth = new JPanel(new GridLayout(0, 8));
    JPanel alphabetSouth = new JPanel(new GridLayout(0, 8));
    JPanel board = new JPanel(new GridLayout(8, 8));
    JLabel[][] alpha = new JLabel[2][8];
    static Block[][] block = new Block[8][8];
    int[][] directions = {{-1, 0}, {1, 1}, {0, 1}, {-1, 1}, {1, 0}, {-1, -1}, {0, -1}, {1, -1}};
    int blackCount = 2;
    int whiteCount = 2;
    String black = "Black";
    String white = "White";
    String turn = black;
    String blockText = "Block";
    String blackTrans = "Black_trans";
    String whiteTrans = "White_trans";
    String searchColor = "";
    BulletinBoard bulletinBoard = new BulletinBoard();
    Bot bot1 = new Bot();
    Bot bot2 = new Bot();
    boolean isEnd = false;
    String userName;
    String ip = null;
    DatagramSocket socket;

    public Othello() {
        setSize(1000, 1000);
        setTitle("othello");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        ToolTipManager.sharedInstance().setEnabled(false);

        setBoard();

        bulletinBoard.setVisible(true);

        showPossibility();

        getContentPane().setBackground(Color.WHITE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
                bulletinBoard.dispose();
            }
        });

        setVisible(true);
    }

    public Othello(String userName, String ip, DatagramSocket socket) {
        this.userName = userName.equals("Player1") ? black : white;
        this.socket = socket;
        this.ip = ip;

        setSize(1000, 1000);
        setTitle("othello-" + socket.getLocalPort());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        ToolTipManager.sharedInstance().setEnabled(false);

        setBoard();

        bulletinBoard.setVisible(true);

        showPossibility();

        getContentPane().setBackground(Color.WHITE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                bulletinBoard.dispose();
            }
        });

        setVisible(true);

        receiveMessage();
    }

    void receiveMessage() {
        byte[] bytes = new byte[512];
        try {
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

            while (!isEnd) {
                socket.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());
                String[] partedData = data.split(": ");
                String[] coordinate = partedData[1].split(",");
                int x = Integer.parseInt(coordinate[0]);
                int y = Integer.parseInt(coordinate[1]);

                if (!partedData[0].equals(userName)) blockAction(block[y][x], x, y);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMessage(String str) {
        try (DatagramSocket server = new DatagramSocket()) {
            DatagramPacket sendPacket = new DatagramPacket(str.getBytes(), str.getBytes().length,
                    InetAddress.getByName(ip), 2500);

            server.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void withBot(String color) {
        bot1.setColor(color);
    }

    void useBot() {

        bot1.setColor(black);
        bot1.enemy = bot2;

        bot2.setColor(white);
        bot2.enemy = bot1;

        bot1.scanningBoard();
    }

    void showPossibility() {
        reset();

        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[i].length; j++) {
                if (block[i][j].getName().equals(blockText)) {
                    find8Ways(j, i);
                }
            }
        }
    }

    void reset() {
        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[i].length; j++) {
                if (block[j][i].getName().equals(blackTrans) || block[j][i].getName().equals(whiteTrans)) {
                    block[j][i].setColor(blockText);
                    block[j][i].setName(blockText);
                    block[j][i].setText("");
                }
            }
        }
    }

    void find8Ways(int curX, int curY) {
        int x;
        int y;
        for (int[] direction : directions) {
            x = curX + direction[1];
            y = curY + direction[0];

            if (!(x < 0 || x >= directions.length || y < 0 || y >= directions.length)) {
                finding(block[y][x].getName(), curX, curY, direction[1], direction[0]);
            }
        }
    }

    void finding(String blockName, int curX, int curY, int dirX, int dirY) {
        if (!blockName.equals(turn) && !(blockName.equals(blockText) || blockName.equals(blackTrans) || blockName.equals(whiteTrans))) {
            searchColor = turn;
            search(curX, curY, dirX, dirY);
        }
    }

    void search(int dirX, int dirY, int wayX, int wayY) {
        for (int i = 1; i <= block.length; i++) {
            if (dirY + i * wayY >= 0 && dirY + i * wayY < block.length && dirX + i * wayX >= 0 && dirX + i * wayX < block.length) {
                if (block[dirY + i * wayY][dirX + i * wayX].getName().equals(blockText)) return;
                if (block[dirY + i * wayY][dirX + i * wayX].getName().equals(searchColor)) {
                    block[dirY][dirX].setColor(turn + "_trans");
                    String way = block[dirY][dirX].getText();
                    block[dirY][dirX].setText(way + "!" + wayX + "," + wayY);
                }
            }
        }
    }

    void check(String color, int curX, int curY) {
        String[] direction = Arrays.stream(color.split("!")).skip(1).toArray(String[]::new);

        for (String s : direction) {
            String[] xy = s.split(",");
            int dirX = toInt(xy[0]);
            int dirY = toInt(xy[1]);

            int sum = 1;
            while (!block[curY + sum * dirY][curX + sum * dirX].getName().equals(turn)) {
                block[curY + sum * dirY][curX + sum * dirX].setText("");
                block[curY + sum * dirY][curX + sum * dirX].setColor(turn);

                sum++;
            }
        }

        block[curY][curX].setColor(turn);
        if (turn.equals(black)) {
            turn = white;
        } else {
            turn = black;
        }

        countingScore();

        bulletinBoard.teamLabel.setText(turn);
        bulletinBoard.whiteScore.setText(whiteCount + "");
        bulletinBoard.blackScore.setText(blackCount + "");
    }

    void countingScore() {
        whiteCount = 0;
        blackCount = 0;

        for (Block[] blocks : block) {
            for (Block b : blocks) {
                if (b.getName().equals(white)) whiteCount++;
                if (b.getName().equals(black)) blackCount++;
            }
        }
    }

    int toInt(String str) {
        return Integer.parseInt(str);
    }

    void isFinish() {
        int emptyCount = 0;
        int transCount = 0;

        for (Block[] blocks : block) {
            for (Block b : blocks) {
                emptyCount += emptyCount(b.getName());
                transCount += transCount(b.getName());
            }
        }

        if ((emptyCount == 0 && transCount == 0) || (emptyCount != 0 && transCount == 0)) {
            String winner;

            if (whiteCount > blackCount) winner = white;
            else if (blackCount > whiteCount) winner = black;
            else winner = "Draw";

            isEnd = true;

            String winText = winner.equals("Draw") ? "Draw!" : winner + " is the winner!";

            JOptionPane.showMessageDialog(this, winText, "Black " + blackCount + " : " + whiteCount + " White", JOptionPane.INFORMATION_MESSAGE);
            int yes = JOptionPane.showConfirmDialog(this, "Play again?", "Replay", JOptionPane.YES_NO_OPTION);
            if (yes == JOptionPane.YES_OPTION) {
                bulletinBoard.dispose();
                dispose();
                new Othello();
            }

            bulletinBoard.dispose();
            dispose();
        }
    }

    int emptyCount(String blockName) {
        return blockName.equals(blockText) ? 1 : 0;
    }

    int transCount(String blockName) {
        return (blockName.equals(whiteTrans) || blockName.equals(blackTrans)) ? 1 : 0;
    }

    void setBoard() {
        add(alphabetNorth, "North");
        add(alphabetSouth, "South");
        add(board);
        add(numberEast, "East");
        add(numberWest, "West");

        for (int i = 1; i < 9; i++) {
            char alphabet = (char) (i + 96);
            alpha[0][i - 1] = new JLabel(alphabet + "", SwingConstants.CENTER);
            alpha[1][i - 1] = new JLabel(alphabet + "", SwingConstants.CENTER);

            alphabetNorth.add(alpha[0][i - 1]);
            alphabetSouth.add(alpha[1][i - 1]);

            alpha[0][i - 1].setOpaque(false);
            alpha[0][i - 1].setFont(new Font("", Font.PLAIN, 20));
            alpha[0][i - 1].setPreferredSize(new Dimension(0, 50));

            alpha[1][i - 1].setOpaque(false);
            alpha[1][i - 1].setFont(new Font("", Font.PLAIN, 20));
            alpha[1][i - 1].setPreferredSize(new Dimension(0, 50));

            numberWest.add(new JLabel(i + "", SwingConstants.CENTER));
            numberEast.add(new JLabel(i + "", SwingConstants.CENTER));

            JLabel jLabel = (JLabel) numberWest.getComponent(i - 1);
            jLabel.setOpaque(false);
            jLabel.setFont(new Font("", Font.PLAIN, 20));
            jLabel.setPreferredSize(new Dimension(50, 0));

            JLabel jLabel2 = (JLabel) numberEast.getComponent(i - 1);
            jLabel2.setOpaque(false);
            jLabel2.setFont(new Font("", Font.PLAIN, 20));
            jLabel2.setPreferredSize(new Dimension(50, 0));
        }

        alphabetSouth.setBorder(new EmptyBorder(0, 50, 0, 50));
        alphabetNorth.setBorder(new EmptyBorder(0, 50, 0, 50));

        JComponent[] panels = {alphabetNorth, alphabetSouth, numberEast, numberWest};
        IntStream.range(0, 4).forEach(i -> panels[i].setOpaque(false));

        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block.length; j++) {
                block[i][j] = new Block();
                board.add(block[i][j]);
                block[i][j].setOpaque(true);
                block[i][j].setToolTipText(j + "," + i);

                int x = j;
                int y = i;

                block[i][j].addActionListener(actionEvent -> {
                    Block block1 = (Block) actionEvent.getSource();
                    if (ip != null) {
                        onOnlineGame(block1, x, y);
                    } else {
                        onOfflineGame(block1, x, y);
                    }
                });
            }
        }
        block[3][4].setColor(black);
        block[4][3].setColor(black);
        block[3][3].setColor(white);
        block[4][4].setColor(white);

        repaint();

        board.setOpaque(false);
    }

    void onOfflineGame(Block block1, int x, int y) {
        if (block1.getName().equals(blackTrans) || block1.getName().equals(whiteTrans)) {
            blockAction(block1, x, y);
        }

        if (bot1.enemy == null && turn.equals(bot1.color)) {
            bot1.scanningBoard();
        }
    }

    void onOnlineGame(Block block1, int x, int y) {
        if (turn.equals(userName)) {
            if (block1.getName().equals(blackTrans) || block1.getName().equals(whiteTrans)) {
                blockAction(block1, x, y);
                sendMessage(userName + ": " + x + "," + y);
            }
        } else {
            JOptionPane.showMessageDialog(null, "자신의 차례가 아닙니다.", "경고", JOptionPane.ERROR_MESSAGE);
        }
    }

    void blockAction(Block block, int x, int y) {
        check(block.getText(), x, y);
        showPossibility();
        isFinish();
    }

    public static void main(String[] args) {
        try {
            new Othello("Player1", "192.168.45.99", new DatagramSocket(3000));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    static class BulletinBoard extends JDialog {
        String black = "Black";
        JPanel scoreBoard = new JPanel(new GridLayout(1, 0));
        JPanel turnDisplay = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel whiteScore = new JLabel("2", SwingConstants.CENTER);
        JLabel blackScore = new JLabel("2", SwingConstants.CENTER);
        JLabel teamLabel = new JLabel(black, SwingConstants.CENTER);
        JLabel[] jLabel = {blackScore, whiteScore};
        String[] teamColor = {black, "White"};

        public BulletinBoard() {
            setSize(300, 200);
            setTitle("Scoreboard");
            setLayout(new BorderLayout());
            setDefaultCloseOperation(2);
            setLayout(new BorderLayout());

            add(scoreBoard);
            add(turnDisplay, "South");

            for (int i = 0; i < 2; i++) {
                JPanel teamBoard = new JPanel(new BorderLayout());
                teamBoard.add(new JLabel(teamColor[i], SwingConstants.CENTER), "North");
                teamBoard.add(jLabel[i]);

                jLabel[i].setFont(new Font("", Font.BOLD, 25));

                scoreBoard.add(teamBoard);
            }

            turnDisplay.add(teamLabel);
            teamLabel.setFont(new Font("", Font.BOLD, 25));

            getContentPane().setBackground(Color.WHITE);
        }
    }

    static class Bot implements Serializable {
        ArrayList<String> board = new ArrayList<>();
        String color;
        Bot enemy;

        void setColor(String color) {
            this.color = color;
        }

        void scanningBoard() {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.getGlobal().warning("Interrupted");
                    Thread.currentThread().interrupt();
                }

                board.clear();

                for (Block[] blocks : block) {
                    for (Block b : blocks) {
                        if (b.getName().equals(color + "_trans")) {
                            board.add(b.getToolTipText());
                        }
                    }
                }

                selectBlock();
            }).start();
        }

        void selectBlock() {
            Random random;
            try {
                random = SecureRandom.getInstanceStrong();
                int randomBlock = random.nextInt(board.size());
                String[] xy = board.get(randomBlock).split(",");
                int x = Integer.parseInt(xy[0]);
                int y = Integer.parseInt(xy[1]);
                block[y][x].doClick();
            } catch (NoSuchAlgorithmException | IllegalArgumentException e) {
                e.printStackTrace();
            }

            if (enemy != null) {
                nextTurn(enemy);
            }
        }

        void nextTurn(Bot enemy) {
            enemy.scanningBoard();
        }
    }

    static class Block extends JButton {

        public Block() {
            setIcon(new ImageIcon(getToolkit().getImage("./Othello/Block.png").getScaledInstance(115, 110, Image.SCALE_SMOOTH)));
            setName("Block");
            setSize(115, 110);

            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFont(new Font("", Font.PLAIN, 0));
        }

        void setColor(String color) {
            setIcon(new ImageIcon(getToolkit().getImage("./Othello/" + color + ".png").getScaledInstance(115, 110, Image.SCALE_SMOOTH)));
            setName(color);
            repaint();
        }
    }
}