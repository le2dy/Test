package server;

import othello.Othello_Server;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Server extends JFrame {
    private GUI gui;

    /** XXX 03. 세번째 중요한것. 사용자들의 정보를 저장하는 맵입니다. */
    private final Map<String, DataOutputStream> clientsMap = new HashMap<>();
    public int clientsCount = 0;

    public void setting() throws IOException {
        Collections.synchronizedMap(clientsMap); // 이걸 교통정리 해줍니다^^
        ServerSocket serverSocket = new ServerSocket(7777);
        while (clientsMap.size() <= 1) {
            /** XXX 01. 첫번째. 서버가 할일 분담. 계속 접속받는것. */
            System.out.println("서버 대기중...");
            Socket socket = serverSocket.accept();
            System.out.println(socket.getInetAddress() + "에서 접속했습니다.");
            Receiver receiver = new Receiver(socket);
            receiver.start();
        }

        serverSocket.close();
    }

    void setGui(GUI gui) {
        this.gui = gui;
    }

    // 맵의내용(클라이언트) 저장과 삭제
    public void addClient(String nick, DataOutputStream out) {
        sendMessage(nick + "님이 접속하셨습니다.");
        clientsMap.put(nick, out);
        gui.boardPanel.add(new JLabel(nick));
        clientsCount = clientsMap.size();
        gui.jLabel.setText("사용자 목록: "+clientsCount + "명");
        sendMessage("⇄"+clientsCount);
    }

    public void removeClient(String nick) {
        sendMessage(nick + "님이 나가셨습니다.");
        clientsMap.remove(nick);
    }

    // 메시지 내용 전파
    public void sendMessage(String msg) {
        Othello_Server.getMessage(msg, clientsMap);
    }

    public static void main(String[] args) {
        new GUI();
    }

    // -----------------------------------------------------------------------------
    class Receiver extends Thread {
        private final DataInputStream in;
        private final String nick;

        /** XXX 2. 리시버가 한일은 자기 혼자서 네트워크 처리 계속..듣기.. 처리해주는 것. */
        public Receiver(Socket socket) throws IOException {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            nick = in.readUTF();
            addClient(nick, out);
        }

        @Override
        public void run() {
            try {// 계속 듣기만!!
                while (in != null) {
                    String msg = in.readUTF();
                    sendMessage(msg);
                }
            } catch (IOException e) {
                // 사용접속종료시 여기서 에러 발생. 그럼나간거에요.. 여기서 리무브 클라이언트 처리 해줍니다.
                removeClient(nick);
            }
        }
    }
}

class GUI extends JFrame {
    JPanel boardPanel = new JPanel();
    JLabel jLabel = new JLabel("사용자 목록: 0명");
    private final Server server = new Server();

    public GUI() {
        try {
            formSetting();

            server.setting();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void formSetting() {
        setSize(300, 300);
        setTitle("사용자 목록");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        add(boardPanel);
        boardPanel.setLayout(new BoxLayout(boardPanel, BoxLayout.Y_AXIS));
        boardPanel.add(jLabel);

        server.setGui(this);

        setVisible(true);
    }
}