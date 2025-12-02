package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

public class UDPNotificationServer implements Runnable {

    private final int port;

    // name -> udpPort
    public static ConcurrentHashMap<String, Integer> clientPorts = new ConcurrentHashMap<>();

    public UDPNotificationServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("[UDP] Notification server started on port " + port);

            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Send notify to all clients
    public void notifyAllClients(String msg) {
        try {
            byte[] data = msg.getBytes();

            for (String user : clientPorts.keySet()) {
                int port = clientPorts.get(user);

                DatagramPacket packet = new DatagramPacket(
                        data,
                        data.length,
                        InetAddress.getByName("localhost"),
                        port
                );

                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.send(packet);
                }
            }

        } catch (Exception e) {
            System.out.println("[UDP] Error sending: " + e.getMessage());
        }
    }

    public void registerPort(String user, int port) {
        clientPorts.put(user, port);
    }

    public void registerClient(String name, int port) {
        clientPorts.put(name, port);
    }

    public void unregisterClient(String name) {
        clientPorts.remove(name);
    }
}
