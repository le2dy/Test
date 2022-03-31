package server;

import othello.WaitingRoom;

import javax.swing.*;

public class Clients extends JFrame {

    public Clients() {
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JButton btn = new JButton("클라이언트 실행");
        add(btn);

        btn.addActionListener(actionEvent -> {
            dispose();
            new ClientGui("Client2");
        });

        setVisible(true);
    }

    public static void main(String[] args) {
//        new Clients();
        new ClientGui("Client2");
    }
}
