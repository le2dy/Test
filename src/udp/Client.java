package udp;

import javax.swing.*;

public class Client {
    public static void main(String[] args) {
        String IP = JOptionPane.showInputDialog(null, "서버 IP를 입력해주세요.", "서버 접속", JOptionPane.PLAIN_MESSAGE, null, null, "127.0.0.1").toString();
        new BaseClient(3000, IP);
    }
}
