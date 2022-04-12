package udp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BaseClient extends JFrame {
    JPanel messagePanel = new JPanel();
    JScrollPane jScrollPane = new JScrollPane(messagePanel);
    JTextField jTextField = new JTextField();
    JButton sendButton = new JButton("전송");
    JPanel textPanel = new JPanel(new BorderLayout());
    String serverIP;
    String name;
    int port;

    public BaseClient(int port, String serverIP) {
        setDesign();

        this.port = port;
        this.serverIP = serverIP;

        name = JOptionPane.showInputDialog(null, "이름을 입력해주세요.", "이름 입력", JOptionPane.INFORMATION_MESSAGE, null, null, new NameGenerator().generateName()).toString();

        sendMessage("User:" + name + ":" + port);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sendMessage(port + ":Disconnect");
            }
        });

        setVisible(true);

        receive();
    }

    void setDesign() {
        setSize(400, 500);
        setTitle("Chatting Program");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        textPanel.add(jTextField);
        textPanel.add(sendButton, "East");

        add(jScrollPane);
        add(textPanel, "South");

        addAction();
    }

    void addAction() {
        sendButton.addActionListener(actionEvent -> {
            String msg = jTextField.getText();
            sendMessage(port + ":" + msg);

            addMessage(msg, "me");

            jTextField.setText("");
        });
    }

    void addMessage(String msg, String distinct) {
        Color color = null;

        if(distinct.equals("me")) {
            color = Color.YELLOW;
        } else if(distinct.equals("other")) {
            color = Color.WHITE;
        } else if(distinct.equals("server")) {
            color = Color.LIGHT_GRAY;
        }

        JLabel message = new JLabel(msg, SwingConstants.RIGHT);
        message.setOpaque(true);
        message.setBackground(color);

        messagePanel.add(message);

        repaint();
        revalidate();
    }

    void sendMessage(String str) {
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket sendPacket = new DatagramPacket(str.getBytes(), str.getBytes().length,
                    InetAddress.getByName(serverIP), 2500);

            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void receive() {
        byte[] bytes = new byte[512];
        try (DatagramSocket server = new DatagramSocket(port)) {
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

            while (true) {
                server.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());
                String[] partedData = data.split(":");

                System.out.println(data);

                if (data.contains("#Notice")) {
                    addMessage(data.replace("#Notice", ""), "server");
                } else if (!data.startsWith("User")) {
                    if (!partedData[0].equals(name)) addMessage(data, "other");
                    if (data.split(":")[1].equals("Server Shutdown")) break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
