package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

public class Client extends JFrame implements Serializable {
    private DataOutputStream out;
    private ClientGui gui;
    Socket socket;

    public final void setGui(ClientGui gui) {
        this.gui = gui;
    }

    public void connect() {
        try {
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("서버 연결됨.");

            out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            while (in != null) {
                String msg = in.readUTF();
                if(msg.startsWith("⇄")) System.out.println("print count");
                gui.appendMsg(msg);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg2) {
        try {
            out.writeUTF(msg2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class ClientGui extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private final JTextArea jta = new JTextArea(40, 25);
    private final JTextField jtf = new JTextField(25);
    final Client client = new Client();
    JToggleButton[][] jButtons = new JToggleButton[3][3];

    public ClientGui(String name) {
        JPanel northPanel = new JPanel(new GridLayout(3, 3));

        add(northPanel, "North");
        add(jta, BorderLayout.CENTER);
        add(jtf, BorderLayout.SOUTH);
        jtf.addActionListener(this);

        for (int i = 0; i < jButtons.length; i++) {
            for (int j = 0; j < jButtons[i].length; j++) {
                northPanel.add(jButtons[j][i] = new JToggleButton(j+","+i));
                jButtons[j][i].addActionListener(changeEvent -> {
                    String msg = name + " :" + ((JToggleButton)changeEvent.getSource()).getText() + "\n";
                    client.sendMessage(msg);
                    jtf.setText("");
                });
            }
        }

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setBounds(800, 100, 400, 600);
        setTitle("클라이언트");

        client.setGui(this);
        client.connect();
    }

    public static void main(String[] args) {
        new ClientGui("Client1");
    }

    @Override
    // 말치면 보내는 부분
    public void actionPerformed(ActionEvent e) {
        String msg = "Client :" + jtf.getText() + "\n";
        client.sendMessage(msg);
        jtf.setText("");
    }

    public void appendMsg(String msg) {
        String[] button = msg.replaceAll(".*:", "").replaceAll("\n","").split(",");
        if (button.length == 2) {
            int x = Integer.parseInt(button[0]);
            int y = Integer.parseInt(button[1]);
            jButtons[x][y].setText("Selected");
        }

        jta.append(msg);
    }


}