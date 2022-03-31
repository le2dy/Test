package othello;

import javax.sound.sampled.Line;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class WaitingRoom extends JFrame {
    JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
    JPanel player1Panel = new JPanel(new BorderLayout());
    JPanel player2Panel = new JPanel(new BorderLayout());
    JPanel player1ButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JPanel player2ButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JLabel player1NameLabel = new JLabel("Player1", SwingConstants.CENTER);
    JLabel player2NameLabel = new JLabel("Player2", SwingConstants.CENTER);
    JLabel[] readyStatusLabel = new JLabel[2];
    JButton[] player1ReadyButton = {new JButton("준비"), new JButton("취소")};
    JButton[] player2ReadyButton = {new JButton("준비"), new JButton("취소")};
    Client client = new Client();

    public WaitingRoom() {
        formSetting();

        addAction();

        client.setWaitingRoom(this);
        client.connect();
    }

    void formSetting() {
        setSize(800, 500);
        setTitle("대기실");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("대기실", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));

        JPanel[] panels = {player1Panel, player2Panel};
        JPanel[] buttonPanels = {player1ButtonPanel, player2ButtonPanel};
        JLabel[] labels = {player1NameLabel, player2NameLabel};
        JButton[][] buttons = {player1ReadyButton, player2ReadyButton};
        String[] color = {"Black", "White"};

        add(centerPanel);

        for (int i = 0; i < panels.length; i++) {
            centerPanel.add(panels[i]);
            panels[i].add(labels[i], "North");
            labels[i].setName("Player" + (i + 1));
            panels[i].setBorder(new LineBorder(Color.BLACK));

            JPanel labelPanel = new JPanel(new GridLayout(2, 1));

            panels[i].add(labelPanel);
            labelPanel.add(readyStatusLabel[i] = new JLabel("Waiting...", SwingConstants.CENTER));
            JLabel colorLabel = new JLabel(color[i], SwingConstants.CENTER);
            labelPanel.add(colorLabel);
            colorLabel.setFont(new Font("", Font.PLAIN, 40));

            readyStatusLabel[i].setFont(new Font("", Font.ITALIC, 50));
            readyStatusLabel[i].setForeground(Color.RED);
            panels[i].add(buttonPanels[i], "South");
            labels[i].setFont(new Font("", Font.PLAIN, 20));

            buttonPanels[i].add(buttons[i][0]);
            buttonPanels[i].add(buttons[i][1]);

            int j = i;
            buttons[i][0].addActionListener(actionEvent -> {
                if(client.order.equals("Player" +(j + 1))) {
                    readyStatusLabel[j].setText("Ready");
                    readyStatusLabel[j].setForeground(Color.GREEN);
                    client.sendMessage("Ready");
                }
            });
            buttons[i][1].addActionListener(actionEvent -> {
                if(client.order.equals("Player" +(j + 1))) {
                    readyStatusLabel[j].setText("Waiting...");
                    readyStatusLabel[j].setForeground(Color.RED);
                    client.sendMessage("Waiting");
                }
            });
        }

        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        add(titleLabel, "North");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    void addAction() {
        JLabel[] nameLabels = {player1NameLabel, player2NameLabel};

        for (JLabel nameLabel : nameLabels) {
            nameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (((JLabel) e.getSource()).getName().equals(client.order)) setName(((JLabel) e.getSource()));
                }
            });
        }
    }

    void setName(JLabel label) {
        if(!(label.getText().equals("Player1") || label.getText().equals("Player2"))) {
            JOptionPane.showMessageDialog(null, "이름은 한 번만 변경할 수 있습니다.", "경고", JOptionPane.ERROR_MESSAGE);
            return;
        };
        String name = JOptionPane.showInputDialog(null, "이름을 입력해주세요.", "플레이어 이름 입력", JOptionPane.PLAIN_MESSAGE);

        if (name == null) {
            label.setText(label.getText());
        } else if (name.isBlank() || name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "이름은 한 글자 이상 입력해야 합니다.", "경고", JOptionPane.ERROR_MESSAGE);
            setName(label);
        } else {
            label.setText(name);
            client.sendMessage(name);
        }
    }

    void setOtherPlayer() {
        String[] names = client.NF.split(",");
        if(client.order.equals("Player1")) {
            if(names.length == 3) player2NameLabel.setText(names[1]);
        } else {
            player1NameLabel.setText(names[2]);
        }
    }

    public static void main(String[] args) {
        new WaitingRoom();
    }
}

class Client extends JFrame {
    WaitingRoom waitingRoom;
    String order;

    private DataOutputStream out;
    Socket socket;
    String NF = "";

    public void connect() {
        try {

            socket = new Socket("127.0.0.1", 7777);
            System.out.println("서버 연결됨.");

            out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            String msg = in.readUTF();
            order = msg.replace("⇄", "");

            while (in != null) {
                msg = in.readUTF();
                if(msg.contains("NF")) {
                    NF = msg;
                    waitingRoom.setOtherPlayer();
                } else if(msg.contains("Ready")) {
                    if(msg.equals("Player1: Ready")) {
                        waitingRoom.readyStatusLabel[0].setText("Ready");
                        waitingRoom.readyStatusLabel[0].setForeground(Color.GREEN);
                    } else {
                        waitingRoom.readyStatusLabel[1].setText("Ready");
                        waitingRoom.readyStatusLabel[1].setForeground(Color.GREEN);
                    }
                    if(waitingRoom.readyStatusLabel[0].getText().equals("Ready") && waitingRoom.readyStatusLabel[1].getText().equals("Ready")) {
                        waitingRoom.dispose();
                        new Othello(socket, order);
                        break;
                    }
                } else if(msg.contains("Waiting")) {
                    if(msg.equals("Player1: Waiting")) {
                        waitingRoom.readyStatusLabel[0].setText("Waiting...");
                        waitingRoom.readyStatusLabel[0].setForeground(Color.RED);
                    } else {
                        waitingRoom.readyStatusLabel[1].setText("Waiting...");
                        waitingRoom.readyStatusLabel[1].setForeground(Color.RED);
                    }
                } else {
                    System.out.println(msg);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setWaitingRoom(WaitingRoom waitingRoom) {
        this.waitingRoom = waitingRoom;
    }

    public void sendMessage(String msg2) {
        try {
            out.writeUTF(order + ": "+ msg2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
