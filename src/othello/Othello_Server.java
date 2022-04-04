package othello;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Othello_Server extends JFrame {

    /** XXX 03. 세번째 중요한것. 사용자들의 정보를 저장하는 맵입니다. */
    final Map<String, DataOutputStream> clientsMap = new HashMap<>();
    int clientsCount = 0;
    String ip;

    JPanel boardPanel = new JPanel();
    JLabel jLabel = new JLabel();
    JButton shutdownButton = new JButton("Server Shutdown");

    public Othello_Server() {
        setSize(300, 300);
        setTitle("사용자 목록");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        add(boardPanel);
        boardPanel.setLayout(new BoxLayout(boardPanel, BoxLayout.Y_AXIS));
        boardPanel.add(jLabel);

        add(shutdownButton, "South");

        shutdownButton.addActionListener(actionEvent -> {
            System.exit(0);
        });

        setVisible(true);
    }

    public void setting() throws IOException {
        Collections.synchronizedMap(clientsMap); // 이걸 교통정리 해줍니다^^

        try (ServerSocket serverSocket = new ServerSocket(7777); Socket s = new Socket()) {
            s.connect(new InetSocketAddress("google.com", 80));

            ip = s.getLocalAddress().toString().replace("/", "");

            jLabel.setText("사용자 목록: 0명(" + ip + ":7777)");

            while (clientsMap.size() <= 5) {
                /** XXX 01. 첫번째. 서버가 할일 분담. 계속 접속받는것. */
                System.out.println("서버 대기중...");
                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress() + "에서 접속했습니다.");
                Receiver receiver = new Receiver(socket);
                receiver.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 맵의내용(클라이언트) 저장과 삭제
    public void addClient(String nick, DataOutputStream out) {
        clientsMap.put(nick, out);
        clientsCount = clientsMap.size();
        sendMessage(nick + "님이 접속하셨습니다.");

        boardPanel.add(new JLabel(nick));
        clientsCount = clientsMap.size();
        jLabel.setText("사용자 목록: "+clientsCount + "명");

        sendNickname(clientsMap);
    }

    public void sendNickname(Map<String, DataOutputStream> map) {
        final String[] str = {"NF"};
        List<Object> arrays = Arrays.asList(map.keySet().toArray());

        arrays.stream().forEach(key -> str[0] += key.toString().replaceAll(".*: ", ","));

        sendMessage(str[0].trim());
    }

    public void removeClient(String nick) {
        clientsMap.remove(nick);
        clientsCount = clientsMap.size();
        sendMessage(nick + "님이 나가셨습니다.");
    }

    // 메시지 내용 전파
    public void sendMessage(String msg) {
        Iterator<String> it = clientsMap.keySet().iterator();
        String key;

        while (it.hasNext()) {
            key = it.next();
            try {
                clientsMap.get(key).writeUTF(msg);
//                System.out.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Othello_Server().setting();
    }

    // -----------------------------------------------------------------------------
    class Receiver extends Thread {
        private final DataInputStream in;
        private final String nick;

        /** XXX 2. 리시버가 한일은 자기 혼자서 네트워크 처리 계속..듣기.. 처리해주는 것. */
        public Receiver(Socket socket) throws IOException {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            out.writeUTF("⇄Player"+ (clientsCount + 1));
            nick = in.readUTF();
            addClient(nick, out);
        }

        @Override
        public void run() {
            try {// 계속 듣기만!!
                while (in != null) {
                    String msg = in.readUTF();
                    sendMessage(msg);

                    if(msg.endsWith("exit")) removeClient(nick);
                }
            } catch (IOException e) {
                // 사용접속종료시 여기서 에러 발생. 그럼나간거에요.. 여기서 리무브 클라이언트 처리 해줍니다.
                removeClient(nick);
            }
        }
    }
}