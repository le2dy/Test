package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Server {
    HashMap<Integer, String> clientsMap = new HashMap<>();
    int port = 2500;
    //server 2500
    //client 3000 3100 3200 3300

    public Server() {
        byte[] bytes = new byte[512];

        try (DatagramSocket server = new DatagramSocket(port)) {
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

            while (true) {
                server.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());
                String[] partedData = data.split(":");

                if (partedData[1].equals("Disconnect")) {
                    String msg = clientsMap.get(Integer.parseInt(partedData[0])) + " has Left.";
                    clientsMap.remove(Integer.parseInt(partedData[0]));
                    sendMessageToClients("notice", msg);
                } else if (partedData[1].equals("Server Shutdown")) {
                    break;
                } else {
                    if (data.startsWith("User")) {
                        clientsMap.put(Integer.parseInt(partedData[2]), partedData[1]);
                    } else {
                        sendMessageToClients("message", data.replace(partedData[0], clientsMap.get(Integer.parseInt(partedData[0]))));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMessageToClients(String status, String message) {
        for (Map.Entry<Integer, String> entry : clientsMap.entrySet()) {
            int clientPort = entry.getKey();
            if (status.equals("message")) {
                sendMessage(message, clientPort);
            } else if (status.equals("notice")) {
                sendMessage("#Notice" + clientsMap.get(clientPort) + " has left.", clientPort);
            }
        }

    }

    void sendMessage(String str, int port) {
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket sendPacket = new DatagramPacket(str.getBytes(), str.getBytes().length,
                    InetAddress.getByName("127.0.0.1"), port);

            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
