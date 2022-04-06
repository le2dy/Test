package notepad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Note extends JFrame {
    JTextArea jTextArea = new JTextArea();

    public Note(String fileName, Choose choose) {
        setSize(1000, 1000);
        setTitle(fileName);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(0,1));

        JScrollPane scrollPane = new JScrollPane(jTextArea);
        add(scrollPane);

        try(InputStream inputStream = new FileInputStream("/home/leedongyun/Desktop/notes/" + fileName)) {
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            StringBuilder text = new StringBuilder();
            int cur;
            while((cur = reader.read()) != -1){
                text.append((char) cur);
            }
            jTextArea.setText(text.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        jTextArea.setLineWrap(true);
        jTextArea.setFont(new Font("", Font.PLAIN, 20));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try(OutputStream outputStream = new FileOutputStream("/home/leedongyun/Desktop/notes/" + fileName)) {
                    String text = jTextArea.getText();
                    byte[] bytes = text.getBytes();
                    outputStream.write(bytes);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                choose.setVisible(true);
            }
        });

        setVisible(true);
    }

}
