package othello;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;

public class WaitingRoom extends JFrame {
    String p1 = "Player1";
    String player = "Player";
    String ready = "Ready";
    String wait = "Waiting...";
    JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
    JPanel player1Panel = new JPanel(new BorderLayout());
    JPanel player2Panel = new JPanel(new BorderLayout());
    JPanel player1ButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JPanel player2ButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JLabel player1NameLabel = new JLabel(p1, SwingConstants.CENTER);
    JLabel player2NameLabel = new JLabel("Player2", SwingConstants.CENTER);
    JLabel[] readyStatusLabel = new JLabel[2];
    JButton[] player1ReadyButton = {new JButton("준비"), new JButton("취소")};
    JButton[] player2ReadyButton = {new JButton("준비"), new JButton("취소")};
    DatagramSocket socket;
    String ip;
    int port;
    JLabel order;

    public WaitingRoom(String ip, int port) {
        this.port = port;
        this.ip = ip;

        formSetting();

        addAction();


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sendMessage(port + ":Disconnect");
            }
        });


        setVisible(true);

    }


    void formSetting() {
        setSize(800, 500);
        setTitle("대기실-"+port);
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
            labels[i].setName(player + (i + 1));
            panels[i].setBorder(new LineBorder(Color.BLACK));

            JPanel labelPanel = new JPanel(new GridLayout(2, 1));

            panels[i].add(labelPanel);
            readyStatusLabel[i] = new JLabel(wait, SwingConstants.CENTER);
            labelPanel.add(readyStatusLabel[i]);
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
                if (order.equals(labels[j])) {
                    readyStatusLabel[j].setText(ready);
                    readyStatusLabel[j].setForeground(Color.GREEN);
                    sendMessage("Notice:"+ready+":"+port);
                }
            });
            buttons[i][1].addActionListener(actionEvent -> {
                if (order.equals(labels[j])) {
                    readyStatusLabel[j].setText(wait);
                    readyStatusLabel[j].setForeground(Color.RED);
                    sendMessage("Notice:Waiting:"+port);
                }
            });
        }

        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        add(titleLabel, "North");

        JButton testButton = new JButton("접속");
        add(testButton,"South");

        testButton.addActionListener(actionEvent -> new Thread(() -> {
            String myIp = "";
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress("google.com", 80));
                myIp = s.getLocalAddress().toString().replace("/", "");

            } catch (IOException e) {
                e.printStackTrace();
            }
            sendMessage("User:Player:" + port+":"+myIp);
            receive();
        }).start());

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
                    if (((JLabel) e.getSource()).getName().equals(order.getName())) setName(((JLabel) e.getSource()));
                }
            });
        }
    }

    void setName(JLabel label) {
        String name = JOptionPane.showInputDialog(null, "이름을 입력해주세요.", "이름 입력", JOptionPane.PLAIN_MESSAGE, null, null, order.getText()).toString();

        if (name == null) {
            label.setText(label.getText());
        } else if (name.isBlank() || name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "이름은 한 글자 이상 입력해야 합니다.", "경고", JOptionPane.ERROR_MESSAGE);
            setName(label);
        } else {
            label.setText(name);
            sendMessage("User:" + name + ":" + port);
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

    void receive() {
        byte[] bytes = new byte[512];
        try {
            socket = new DatagramSocket(port);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

            while (true) {
                socket.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());
                String[] partedData = data.split(":");

                if (data.getBytes().length == 1) {
                    order = data.equals("1") ? player1NameLabel : player2NameLabel;
                    setName(order);
                } else if(data.equals("Move to game")) {
                        dispose();
                        new Othello(order.getName(), ip, socket);
                } else if(partedData[0].equals("Rival")) {
                     JLabel label = order.equals(player1NameLabel) ? player2NameLabel : player1NameLabel;
                     label.setText(partedData[1]);
                } else if(port != Integer.parseInt(partedData[2])) {
                    ignoreMyMessage(partedData);
                } else if(partedData[1].equals("Server Shutdown")) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void ignoreMyMessage(String[] partedData) {
        if (partedData[0].equals("User")) {
            if (order.equals(player1NameLabel)) player2NameLabel.setText(partedData[1]);
            else player1NameLabel.setText(partedData[1]);
        } else if(partedData[0].equals("Notice")) {
            if(Integer.parseInt(partedData[2]) == 3000) {
                setStatus(readyStatusLabel[0], partedData[1]);
            } else {
                setStatus(readyStatusLabel[1], partedData[1]);
            }
        }
    }

    void setStatus(JLabel statusLabel, String status) {
        Color color = status.equals(ready) ? Color.GREEN : Color.RED;

        statusLabel.setForeground(color);
        statusLabel.setText(status);

        if(readyStatusLabel[0].getText().equals(ready) && readyStatusLabel[1].getText().equals(ready)) {
            sendMessage("Move to game");
        }
    }

    public static void main(String[] args) {
        new WaitingRoom("192.168.45.99", 3000);
    }
}

