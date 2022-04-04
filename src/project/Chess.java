package project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class Chess extends JFrame {
    JPanel board = new JPanel(new GridLayout(8, 8));
    JPanel alphabetNorth = new JPanel(new GridLayout(0, 8));
    JPanel alphabetSouth = new JPanel(new GridLayout(0, 8));
    JPanel numberWest = new JPanel(new GridLayout(8, 0));
    JPanel numberEast = new JPanel(new GridLayout(8, 0));
    static JPanel[][] block = new JPanel[8][8];
    JLabel[] alphabet = new JLabel[16];
    JLabel[] number = new JLabel[16];
    static ChessPieces[] blackPieces = {new Pawn(), new Pawn(), new Pawn(), new Pawn(), new Pawn(), new Pawn(), new Pawn(), new Pawn(), new Rook(), new Knight(), new Bishop(), new Queen(), new King(), new Bishop(), new Knight(), new Rook()};
    static ChessPieces[] whitePieces = {new Pawn(), new Pawn(), new Pawn(), new Pawn(), new Pawn(), new Pawn(), new Pawn(), new Pawn(), new Rook(), new Knight(), new Bishop(), new Queen(), new King(), new Bishop(), new Knight(), new Rook()};
    String knight = "Knight";
    String bishop = "Bishop";
    static String queen = "Queen";
    static String blackText = "Black";
    static String whiteText = "White";
    String[] chessPieces = {"Pawn", "Pawn", "Pawn", "Pawn", "Pawn", "Pawn", "Pawn", "Pawn", "Rook", knight, bishop, queen, "King", bishop, knight, "Rook"};
    static HashMap<Integer, String> blackSets = new HashMap<>(); //1~8: pawns   9,16: rooks   10,15: knights   11,14: bishops   12: queen   13: king
    static HashMap<Integer, String> whiteSets = new HashMap<>(); //1~8: pawns   9,16: rooks   10,15: knights   11,14: bishops   12: queen   13: king
    static ArrayList<String> graveYard = new ArrayList<>();
    static final Color white = new Color(255, 206, 158);
    static final Color black = new Color(209, 139, 71);
    static final Color selectedBlack = new Color(232, 201, 33);
    static final Color selectedWhite = new Color(253, 232, 73);
    static final Color attackBlack = new Color(122, 185, 62);
    static final Color attackWhite = new Color(145, 219, 106);
    static ChessPieces selectedPieces = null;
    static String turn = whiteText;
    String path = "ChessImage/";
    static int attacker = -1;
    static int blocker = - 1;
    static boolean isThreat = false;

    public Chess() {
        defaultSetting();

        addComponents();

        setPieces();

        getContentPane().setBackground(Color.WHITE);
        setVisible(true);
    }

    void defaultSetting() {
        setSize(1000, 1000);
        setTitle("Chess");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(board);
        add(alphabetNorth, "North");
        add(alphabetSouth, "South");
        add(numberWest, "West");
        add(numberEast, "East");

        board.setOpaque(false);

        JComponent[] components = {alphabetNorth, alphabetSouth, numberEast, numberWest};
        IntStream.range(0, 4).forEach(i -> {
            setSize(components[i], i < 2 ? 0 : 30, i < 2 ? 30 : 0);
            components[i].setOpaque(false);
        });

        IntStream.range(0, alphabet.length).forEach(i -> {
            char alpha = (char) (i % 8 + 97);
            alphabet[i] = new JLabel(alpha + "", SwingConstants.CENTER);
            alphabet[i].setOpaque(false);
            number[i] = new JLabel(i % 8 + 1 + "", SwingConstants.CENTER);
            number[i].setOpaque(false);
        });
    }

    void addComponents() {
        for (int i = 0; i < block.length; i++) {
            alphabetNorth.add(alphabet[i]);
            alphabetSouth.add(alphabet[i + 8]);
            numberEast.add(number[i]);
            numberWest.add(number[i + 8]);

            for (int j = 0; j < block[i].length; j++) {
                block[i][j] = new JPanel(new GridBagLayout());
                board.add(block[i][j]);
                block[i][j].setOpaque(true);
                block[i][j].setBackground((j + i % 2) % 2 == 0 ? white : black);

                block[i][j].setToolTipText("asd");
                block[i][j].setEnabled(false);
                block[i][j].setName(j + "," + i);
                block[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            JPanel selBlock = ((JPanel) e.getSource());
                            if (!(selBlock.getBackground().equals(selectedBlack) || selBlock.getBackground().equals(selectedWhite)))
                                return;
                            pieceMove(selBlock);

                        } catch (NullPointerException ignored) {
                            Logger.getGlobal().fine("Null");
                        } finally {
                            reset();
                            selectedPieces = null;
                            repaint();
                        }
                    }
                });
            }
        }
    }

    void setPieces() {
        IntStream.range(0, chessPieces.length).forEach(i -> {
            ImageIcon imageIconBlack = new ImageIcon(getToolkit().getImage(path + chessPieces[i] + "_Black.png").getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            ImageIcon imageIconWhite = new ImageIcon(getToolkit().getImage(path + chessPieces[i] + "_White.png").getScaledInstance(100, 100, Image.SCALE_SMOOTH));
            blackPieces[i].setImageIcon(imageIconBlack);
            whitePieces[i].setImageIcon(imageIconWhite);
            blackPieces[i].color = blackText;
            whitePieces[i].color = whiteText;
            blackPieces[i].curX = whitePieces[i].curX = i % 8;
            blackPieces[i].setName(i + "");
            whitePieces[i].setName(i + "");
        });

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                blackSets.put(i * 8 + j, j + "," + (1 - i));
                whiteSets.put(i * 8 + j, j + "," + (i + 6));
                if (i == 0) {
                    block[i][j].add(blackPieces[j + 8]);
                    block[i + 6][j].add(whitePieces[j]);

                    blackPieces[j + 8].curY = 0;
                    whitePieces[j].curY = 6;

                } else {
                    block[i][j].add(blackPieces[j]);
                    block[i + 6][j].add(whitePieces[j + 8]);

                    blackPieces[j].curY = 1;
                    whitePieces[j + 8].curY = 7;
                }
            }
        }
    }

    static void pieceMove(JPanel selBlock) {
        String[] coordinate = selBlock.getName().split(",");
        int x = toInt(coordinate[0]);
        int y = toInt(coordinate[1]);
        int curX = selectedPieces.curX;
        int curY = selectedPieces.curY;
        block[curY][curX].removeAll();
        block[curY][curX].repaint();
        block[y][x].add(selectedPieces);
        selectedPieces.curX = x;
        selectedPieces.curY = y;

        int pieceNum = toInt(selectedPieces.getName());

        if (selectedPieces.color.equals(blackText)) blackSets.replace(pieceNum, curX + "," + curY, x + "," + y);
        else whiteSets.replace(pieceNum, curX + "," + curY, x + "," + y);

        if (selectedPieces.type.equals("Pawn")) selectedPieces.maxRange = 1;

        if (turn.equals(whiteText)) turn = blackText;
        else turn = whiteText;

        detectCheck();
    }

//    static void detectCheck() {
//        ChessPieces king = turn.equals(whiteText) ? whitePieces[12] : blackPieces[12];
//        int y = king.curY;
//        int x = king.curX;
//        String checkMessage = turn + " king is Check";
//
//        for (int i = 1; i < 8; i++) {
//            if(y - i >= 0 && block[y - i][x].getComponentCount() == 1 && !((ChessPieces)block[y - i][x].getComponent(0)).color.equals(turn))
//                Logger.getGlobal().warning(checkMessage);
//
//            if(y + i < block.length && block[y + i][x].getComponentCount() == 1 && !((ChessPieces)block[y + i][x].getComponent(0)).color.equals(turn) && (((ChessPieces)block[y + i][x].getComponent(0)).type.equals("Rook") || ((ChessPieces)block[y + i][x].getComponent(0)).type.equals(Chess.queen)))
//                Logger.getGlobal().warning(checkMessage);
//
//            if(x - i >= 0 && block[y][x - i].getComponentCount() == 1 && !((ChessPieces)block[y][x - i].getComponent(0)).color.equals(turn) && (((ChessPieces)block[y][x - i].getComponent(0)).type.equals("Rook") || ((ChessPieces)block[y][x - i].getComponent(0)).type.equals(Chess.queen)))
//                Logger.getGlobal().warning(checkMessage);
//
//            if(x + i < block.length && block[y][x + i].getComponentCount() == 1 && !((ChessPieces)block[y][x + i].getComponent(0)).color.equals(turn) && (((ChessPieces)block[y][x + i].getComponent(0)).type.equals("Rook") || ((ChessPieces)block[y][x + i].getComponent(0)).type.equals(Chess.queen)))
//                Logger.getGlobal().warning(checkMessage);
//        }
//
//        if(x + 1 < block.length && x - 1 >= 0 && y + 1 < block.length && y - 1 >= 0 && !((ChessPieces)block[y + 1][x + 1].getComponent(0)).color.equals(turn) && ((ChessPieces)block[y + 1][x + 1].getComponent(0)).type.equals("Pawn"))
//            Logger.getGlobal().warning(checkMessage);
//        if(x + 1 < block.length && x - 1 >= 0 && y + 1 < block.length && y - 1 >= 0 && !((ChessPieces)block[y + 1][x - 1].getComponent(0)).color.equals(turn) && ((ChessPieces)block[y + 1][x - 1].getComponent(0)).type.equals("Pawn"))
//            Logger.getGlobal().warning(checkMessage);
//    }

    static void detectCheck() {
        String[] kingCoordinate;
        int kingX;
        int kingY;
        int range;
        HashMap<Integer, String> enemyPieces;
        ChessPieces[] myPieces = null;

        if (turn.equals(whiteText)) {
            myPieces = whitePieces;
            enemyPieces = blackSets;
        } else {
            myPieces = blackPieces;
            enemyPieces = whiteSets;
        }

        kingCoordinate = enemyPieces.get(12).split(",");
        kingX = toInt(kingCoordinate[0]);
        kingY = toInt(kingCoordinate[1]);
        for (ChessPieces piece : myPieces) {
            range = piece.maxRange;
            String[] directions = piece.direction.split(",");

            if (piece.type.equals("King")) continue;

            for (String direction : directions) {
                switch (direction) {
                    case "Pawn":
                        if ((piece.color.equals(blackText) && (kingX == piece.curX - 1 || kingX == piece.curX + 1) && kingY == piece.curY + range) || (piece.color.equals(whiteText) && (kingX == piece.curX - 1 || kingX == piece.curX + 1) && kingY == piece.curY - range)) {
                            isBlock("Pawn", piece, kingY);
                        }
                        break;
                    case "vertical":
                        if (kingX == piece.curX && (kingY >= piece.curY - range || kingY <= piece.curY + range)) {
                            isBlock("vertical", piece, kingY);
                        }
                        break;
                    case "horizontal":
                        if (kingY == piece.curY && (kingX <= piece.curX + range || kingX >= piece.curX - range)) {
                            isBlock("horizontal", piece, kingY);
                        }
                        break;
                    case "knight":
                        jousting(piece.curX, piece.curY, kingX, kingY, piece.getName());
                        break;
                    default:
                        boolean defeat = false;
                        for (int i = 1; i <= range; i++) {
                            if ((kingX == piece.curX + i || kingX == piece.curX - i) && (kingY == piece.curY + i || kingY == piece.curY - i)) {
                                defeat = true;
                                break;
                            }
                        }
                        if (defeat) isBlock("diagonal", piece, kingY);
                }
            }
        }
    }

    static void isBlock(String direction, ChessPieces pieces, int kingY) {
        boolean check = false;
        int plusX = 0;
        int plusY = 0;
        int dir = 0;
        int dirX = 0;
        int dirY = 0;
        if (direction.equals("Pawn")) {
            check = true;
        } else {
            for (int i = 1; i <= pieces.maxRange; i++) {
                if (direction.equals("vertical")) {
                    plusY = i;
                } else if (direction.equals("horizontal")) {
                    plusX = i;
                } else if (direction.equals("diagonal")) {
                    plusX = plusY = i;
                }
                if (pieces.curY < kingY) {
                    if (pieces.curY + plusY < block.length && pieces.curX + plusX < block.length && block[pieces.curY + plusY][pieces.curX + plusX].getComponentCount() == 1 && !((ChessPieces) (block[pieces.curY + plusY][pieces.curX + plusX].getComponent(0))).color.equals(turn)) {
                        dir = 1;
                        check = true;
                        break;
                    }
                } else {
                    if (pieces.curY - plusY >= 0 && pieces.curX - plusX >= 0 && block[pieces.curY - plusY][pieces.curX - plusX].getComponentCount() == 1 && !((ChessPieces) (block[pieces.curY - plusY][pieces.curX - plusX].getComponent(0))).color.equals(turn)) {
                        dir = -1;
                        check = true;
                        break;
                    }
                }

                if(direction.equals("diagonal")) {
                    if (pieces.curY - plusY >= 0 && pieces.curY - plusY < block.length && pieces.curX + plusX < block.length && block[pieces.curY - plusY][pieces.curX + plusX].getComponentCount() == 1 && !((ChessPieces) (block[pieces.curY - plusY][pieces.curX + plusX].getComponent(0))).color.equals(turn)) {
                        dir = 2;
                        dirY = -1;
                        dirX = 1;
                        check = true;
                        break;
                    }
                    if (pieces.curY + plusY < block.length && pieces.curX - plusX >= 0 &&  pieces.curX - plusX < block.length && block[pieces.curY + plusY][pieces.curX - plusX].getComponentCount() == 1 && !((ChessPieces) (block[pieces.curY + plusY][pieces.curX - plusX].getComponent(0))).color.equals(turn)) {
                        dir = 2;
                        dirY = 1;
                        dirX = -1;
                        check = true;
                        break;
                    }
                }
            }

        }

        if (check) {
            if(Math.abs(dir) == 1) {
                if(!block[pieces.curY + plusY * dir][pieces.curX + plusX * dir].getComponent(0).getName().equals("12")){
                    blocker = toInt(block[pieces.curY + plusY * dir][pieces.curX + plusX * dir].getComponent(0).getName());
                    isThreat = true;
                    attacker = toInt(pieces.getName());
                    Logger.getGlobal().warning("Danger");
                    return;
                }
            } else {
                if(!block[pieces.curY + plusY * dirY][pieces.curX + plusX * dirX].getComponent(0).getName().equals("12")){
                    blocker = toInt(block[pieces.curY + plusY * dirY][pieces.curX + plusX * dirX].getComponent(0).getName());
                    isThreat = true;
                    attacker = toInt(pieces.getName());
                    Logger.getGlobal().warning("Danger");
                    return;
                }
            }

            blocker = -1;
            isThreat = false;
            attacker = toInt(pieces.getName());
            Logger.getGlobal().warning("Check!");
        }
    }

    static void jousting(int curX, int curY, int kingX, int kingY, String number) {
        boolean defeat = false;

        int dir = 1;
        for (int i = 0; i < 4; i++) {
            int x = curX + (i < 2 ? 2 : 1) * dir;
            if (x < 0 || x >= Chess.block.length || curY - (i < 2 ? 1 : 2) < 0 || curY + (i < 2 ? 1 : 2) >= block.length)
                continue;
            if (kingX == x && (kingY == curY + (i < 2 ? 1 : 2) || kingY == curY - (i < 2 ? 1 : 2))) defeat = true;
            dir = -dir;
        }

        if (defeat) {
            attacker = toInt(number);
            Logger.getGlobal().warning("Check!");
        }
    }

    void setSize(JComponent c, int w, int h) {
        c.setPreferredSize(new Dimension(w, h));
    }

    static int toInt(String str) {
        return Integer.parseInt(str);
    }

    static void reset() {
        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block.length; j++) {
                block[i][j].setBackground((j + i % 2) % 2 == 0 ? white : black);
            }
        }
    }

    public static void main(String[] args) {
        new Chess();
    }
}

class ChessPieces extends JLabel {
    int maxRange = 0;
    int curX = -1;
    int curY = -1;
    String direction = "";
    String color;
    String type;
    private ChessPieces sel;
    private ChessPieces other;

    public ChessPieces() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sel = (ChessPieces) e.getSource();

                if (Chess.selectedPieces == null) {
                    if (sel.color.equals(Chess.turn)) select();
                } else if (Chess.selectedPieces != sel && !Chess.selectedPieces.color.equals(sel.color)) {
                    other = (ChessPieces) e.getSource();
                    isAttack(other.curX, other.curY);
                } else {
                    select();
                }
            }
        });
    }

    void checkKillIsPossible() {
        Logger.getGlobal().info("HI");
    }

    void checkAvoidanceIsPossible() {
        Logger.getGlobal().info("Hello");
    }


    void checkBlockIsPossible() {
        ChessPieces[] myPieces;

        if (Chess.turn.equals(Chess.blackText)) myPieces = Chess.blackPieces;
        else myPieces = Chess.whitePieces;

        for (ChessPieces piece : myPieces) {
            piece.getName();
        }

    }

    void select() {
        Chess.selectedPieces = sel;

        if(Chess.isThreat && Integer.parseInt(sel.getName())== Chess.blocker) {
            Logger.getGlobal().warning("Your blocker");
            checkKillIsPossible();
            checkBlockIsPossible();
            checkAvoidanceIsPossible();
        }

        Chess.reset();
        showPath();
    }

    int toInt(String str) {
        return Integer.parseInt(str);
    }

    void isAttack(int x, int y) {
        if (Chess.block[y][x].getBackground().equals(Chess.attackWhite) || Chess.block[y][x].getBackground().equals(Chess.attackBlack)) {
            if (Chess.turn.equals(Chess.whiteText)) {
                Chess.blackSets.remove(toInt(other.getName()));
            } else {
                Chess.whiteSets.remove(toInt(other.getName()));
            }

            other.curX = -99;
            other.curY = -99;
            Chess.graveYard.add(other.getName() + "," + other.type);
            Chess.block[y][x].removeAll();
            Chess.pieceMove(Chess.block[y][x]);

            Chess.selectedPieces = null;
        }

        Chess.reset();
        repaint();
    }

    void setImageIcon(ImageIcon img) {
        setIcon(img);
    }

    void showPath() {
        String[] directions = direction.split(",");
        for (String str : directions) {
            switch (str) {
                case "vertical":
                    drawUp();
                    drawDown();
                    break;
                case "horizontal":
                    drawLeft();
                    drawRight();
                    break;
                case "diagonal":
                    drawDiagonal();
                    break;
                case "knight":
                    drawKnight();
                    break;
                default:
                    drawPawnsPath();
            }
        }
    }

    boolean isExists(int ix, int iy) {
        return Chess.block[curY + iy][curX + ix].getComponentCount() >= 1;
    }

    void draw(int ix, int iy) {
        if (Chess.block[curY + iy][curX + ix].getBackground().equals(Chess.black)) {
            Chess.block[curY + iy][curX + ix].setBackground(Chess.selectedBlack);
        } else {
            Chess.block[curY + iy][curX + ix].setBackground(Chess.selectedWhite);
        }
        repaint();
    }

    void drawAbleAttack(int ix, int iy) {
        if (Chess.selectedPieces.color.equals(Chess.blackText)) {
            if (Chess.whiteSets.containsValue((curX + ix) + "," + (curY + iy))) {
                fillGreen(ix, iy);
            }
        } else {
            if (Chess.blackSets.containsValue((curX + ix) + "," + (curY + iy))) {
                fillGreen(ix, iy);
            }
        }
    }

    void fillGreen(int ix, int iy) {
        if (Chess.block[curY + iy][curX + ix].getBackground().equals(Chess.black)) {
            Chess.block[curY + iy][curX + ix].setBackground(Chess.attackBlack);
        } else {
            Chess.block[curY + iy][curX + ix].setBackground(Chess.attackWhite);
        }
    }

    void drawLeft() {
        for (int i = -1; i >= -maxRange; i--) {
            if (curX + i < 0) break;

            if (isExists(i, 0)) {
                drawAbleAttack(i, 0);
                break;
            }

            draw(i, 0);
        }
    }

    void drawRight() {
        for (int i = 1; i <= maxRange; i++) {
            if (curX + i > Chess.block.length - 1) break;

            if (isExists(i, 0)) {
                drawAbleAttack(i, 0);
                break;
            }

            draw(i, 0);
        }
    }

    void drawUp() {
        for (int i = -1; i >= -maxRange; i--) {
            if (curY + i < 0) break;

            if (isExists(0, i)) {
                drawAbleAttack(0, i);
                break;
            }

            draw(0, i);
        }
    }

    void drawDown() {
        for (int i = 1; i <= maxRange; i++) {
            if (curY + i > Chess.block.length - 1) break;

            if (isExists(0, i)) {
                drawAbleAttack(0, i);
                break;
            }

            draw(0, i);
        }
    }

    void drawDiagonal() {
        int dmaxRange;
        int x = Chess.selectedPieces.curX;
        int y = Chess.selectedPieces.curY;
        String pieceType = Chess.selectedPieces.type;

        //왼쪽위: x와 y 둘 중 더 작은 쪽  오른쪽위: 7 - x 와 y 둘 중 더 짧은 쪽  왼쪽아래: x와 7 - y 둘 중 더 작은 쪽  오른쪽아래: 7 - x 와 7 - y 둘 중 더 짧은 쪽

        dmaxRange = Math.min(x, y);
        if (pieceType.equals("King")) dmaxRange = dmaxRange >= 1 ? 1 : 0;

        for (int i = -1; i >= -dmaxRange; i--) {
            if (isExists(i, i)) {
                drawAbleAttack(i, i);
                break;
            }

            draw(i, i);
        }

        dmaxRange = Math.min(x, 7 - y);
        if (pieceType.equals("King")) dmaxRange = dmaxRange >= 1 ? 1 : 0;

        for (int i = -1; i >= -dmaxRange; i--) {
            if (isExists(i, -i)) {
                drawAbleAttack(i, -i);
                break;
            }

            draw(i, -i);
        }

        dmaxRange = Math.min(7 - x, 7 - y);
        if (pieceType.equals("King")) dmaxRange = dmaxRange >= 1 ? 1 : 0;

        for (int i = 1; i <= dmaxRange; i++) {
            if (isExists(i, i)) {
                drawAbleAttack(i, i);
                break;
            }

            draw(i, i);
        }
        dmaxRange = Math.min(7 - x, y);
        if (pieceType.equals("King")) dmaxRange = dmaxRange >= 1 ? 1 : 0;

        for (int i = 1; i <= dmaxRange; i++) {
            if (isExists(i, -i)) {
                drawAbleAttack(i, -i);
                break;
            }

            draw(i, -i);
        }
    }

    void drawPawnsPath() {
        int dir;
        if (Chess.selectedPieces.color.equals(Chess.whiteText)) dir = -1;
        else dir = 1;

        for (int i = 1; i <= maxRange; i++) {
            if (curY + i * dir > Chess.block.length - 1 || curY + i * dir < 0 || isExists(0, i * dir)) break;

            draw(0, i * dir);
        }

        if (curX - 1 > 0 && isExists(-1, dir)) {
            drawAbleAttack(-1, dir);
        }

        if ((curX + 1 < Chess.block.length) && isExists(1, dir)) {
            drawAbleAttack(1, dir);
        }
    }

    void drawKnight() {
        boolean[] conditions = {curY - 1 >= 0, curY + 1 < Chess.block.length};
        boolean[] conditions2 = {curY - 2 >= 0, curY + 2 < Chess.block.length};
        int dir = 1;

        for (int i = 0; i < conditions.length; i++) {
            if (curX + 2 < Chess.block.length && conditions[i]) {
                if (isExists(2, -dir)) drawAbleAttack(2, -dir);
                else draw(2, -dir);
            }

            if (curX - 2 >= 0 && conditions[i]) {
                if (isExists(-2, -dir)) drawAbleAttack(-2, -dir);
                else draw(-2, -dir);
            }

            if (curX - 1 >= 0 && conditions2[i]) {
                if (isExists(-1, -2 * dir)) {
                    drawAbleAttack(-1, -2 * dir);
                } else draw(-1, -2 * dir);
            }

            if (curX + 1 < Chess.block.length && conditions2[i]) {
                if (isExists(1, -2 * dir)) drawAbleAttack(1, -2 * dir);
                else draw(1, -2 * dir);
            }

            dir = -dir;
        }
    }
}

class Pawn extends ChessPieces {
    public Pawn() {
        type = "Pawn";
        maxRange = 2;
        direction = "Pawn";
    }
}

class Rook extends ChessPieces {
    public Rook() {
        type = "Rook";
        maxRange = 7;
        direction = "vertical,horizontal";
    }
}

class Knight extends ChessPieces {
    public Knight() {
        type = "Knight";
        maxRange = 2;
        direction = "knight";
    }
}

class Bishop extends ChessPieces {
    public Bishop() {
        type = "Bishop";
        maxRange = 7;
        direction = "diagonal";
    }
}

class King extends ChessPieces {
    public King() {
        type = "King";
        maxRange = 1;
        direction = "vertical,horizontal,diagonal";
    }
}

class Queen extends ChessPieces {
    public Queen() {
        type = "Queen";
        maxRange = 7;
        direction = "vertical,horizontal,diagonal";
    }
}