package othello;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Othello_Server_New extends JFrame {
    HashMap<Integer, String> clientsMap = new HashMap<>();
    HashMap<Integer, String> clientsIPMap = new HashMap<>();
    String ip;
    int port = 2500;
    boolean isShutdown = false;
    String message = "message";

    //server 2500
    //client 3000, 3100, 3200, 3300

    JPanel boardPanel = new JPanel();
    JLabel jLabel = new JLabel("서버 접속 유저(0명) " + ip);
    JButton shutdownButton = new JButton("Server Shutdown");
    DatagramSocket socket;

    public Othello_Server_New() throws SocketException {
        setSize(300, 300);
        setTitle("사용자 목록");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());


        add(boardPanel);
        boardPanel.setLayout(new BoxLayout(boardPanel, BoxLayout.Y_AXIS));

        boardPanel.add(jLabel);
        jLabel.setName("title");

        add(shutdownButton, "South");

        shutdownButton.addActionListener(actionEvent -> System.exit(0));

        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress("google.com", 80));
            ip = s.getLocalAddress().toString().replace("/", "");

            jLabel.setText("사용자 목록: 0명(" + ip + ":2500)");
            socket = new DatagramSocket(new InetSocketAddress(ip, port));
        } catch (IOException e) {
            e.printStackTrace();
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isShutdown = !isShutdown;
                System.exit(0);
            }
        });

        setVisible(true);

        receive();
    }

    void receive() {
        byte[] bytes = new byte[512];

        try {
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

            while (!isShutdown) {
                socket.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());
                String[] partedData = data.split(":");

                if (partedData.length == 1) {
                    sendMessageToClients(message, data);
                } else if (partedData[0].equals("Black") || partedData[0].equals("White")) {
                    sendMessageToClients(message, data);
                } else if (partedData[0].equals("Notice")) {
                    sendMessageToClients(message, data);
                } else if (partedData[1].equals("Disconnect")) {
                    checkDisconnect(partedData);
                } else if (partedData[0].equals("User")) {
                    setPlayersName(partedData[2]);
                    checkUser(partedData);
                    sendMessageToClients(message, data);
                } else {
                    sendMessageToClients(message, data.replace(partedData[0], clientsMap.get(Integer.parseInt(partedData[0]))));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setPlayersName(String port) {
        if (clientsMap.size() >= 2) {
            for (Map.Entry<Integer, String> entry : clientsMap.entrySet()) {
                int key = entry.getKey();
                if (key != Integer.parseInt(port)) {
                    sendMessage("Rival:" + clientsMap.get(key) + ":" + key, Integer.parseInt(port), clientsIPMap.get(key));
                }
            }
        }
    }

    void checkUser(String[] partedData) {
        int key = Integer.parseInt(partedData[2]);
        if (clientsMap.containsKey(key)) {
            String name = clientsMap.get(key);
            clientsMap.replace(key, name, partedData[1]);

            Component[] comp = boardPanel.getComponents();
            for (int i = 1; i < comp.length; i++) {
                for (Component label : ((JPanel) comp[i]).getComponents()) {
                    if (label.getName().equals(key + "")) {
                        ((JLabel) label).setText(partedData[1]);
                    }
                }
            }
        } else {
            addUser(key, partedData);
        }

        repaint();
        revalidate();
    }

    void addUser(int key, String[] partedData) {
        clientsMap.put(key, partedData[1]);
        clientsIPMap.put(key, partedData[3]);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setName("Just panel.");

        JLabel numberLabel = new JLabel("Player" + clientsMap.size() + ": ");
        JLabel label = new JLabel(partedData[1]);

        label.setName(partedData[2]);
        numberLabel.setName("Normal label.");

        namePanel.add(numberLabel);
        namePanel.add(label);

        boardPanel.add(namePanel);
        namePanel.setPreferredSize(new Dimension(300, 20));

        jLabel.setText("사용자 목록: " + clientsMap.size() + "명(" + ip + ":2500)");
        sendMessage(clientsMap.size() + "", key, partedData[3]);
    }

    void checkDisconnect(String[] data) {
        String msg = clientsMap.get(Integer.parseInt(data[0])) + " has Left.";

        for (Component c : boardPanel.getComponents()) {
            if (c.getName().equals(data[0])) boardPanel.remove(c);
        }
        clientsMap.remove(Integer.parseInt(data[0]));

        sendMessageToClients("notice", msg);
        repaint();
        revalidate();
    }

    void sendMessageToClients(String status, String message) {
        for (Map.Entry<Integer, String> entry : clientsMap.entrySet()) {
            int clientPort = entry.getKey();
            String clientIp = clientsIPMap.get(clientPort);

            if (status.equals(this.message)) {
                sendMessage(message, clientPort, clientIp);
            } else if (status.equals("notice")) {
                sendMessage("#Notice" + clientsMap.get(clientPort) + " has left.", clientPort, clientIp);
            }
        }

    }

    void sendMessage(String str, int port, String clientIp) {
        try {
            socket.connect(new InetSocketAddress(InetAddress.getByName(clientIp), port));

            DatagramPacket sendPacket = new DatagramPacket(str.getBytes(), str.getBytes().length,
                    socket.getInetAddress(), port);
            socket.send(sendPacket);

            socket.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SocketException {
        new Othello_Server_New();
    }
}
