package project;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Lab extends JFrame {
    JButton jButton = new JButton("이미지 바꾸기");
    JLabel jLabel = new JLabel(), jl = new JLabel("안녕!하세요");
    HashMap<Integer, String> hashMap = new HashMap<>();

    public Lab() {
        setSize(500, 500);
        setDefaultCloseOperation(2);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        ImageIcon img = new ImageIcon(getToolkit().getImage("ChessImage/Pawn_Black.png").getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        ImageIcon img2 = new ImageIcon(getToolkit().getImage("ChessImage/Rook_Black.png").getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        add(jLabel);
        add(jl, "North");
        add(jButton, "South");

        hashMap.put(1, "A");
        hashMap.put(2, "B");
        hashMap.put(3, "C");

        for (int i = 0; i < 10; i++) {
            if(i % 2 == 0) continue;
            System.out.println(i+"");
        }

        jLabel.setIcon(img);
        System.out.println(hashMap);
        jButton.addActionListener(e->{
            jLabel.setIcon(img2);
            String str = jl.getText().replaceAll("!.*","");
            jl.setText(str);
            hashMap.replace(1,"D");
            System.out.println(hashMap);
            System.out.println(hashMap.containsValue("5"));
            System.out.println(hashMap.containsValue("B"));
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new Lab();
    }
}
