package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.function.Consumer;

public class UDPClientListener implements Runnable {

    private int port;
    private Consumer<String> handler;

    public UDPClientListener(int port, Consumer<String> handler) {
        this.port = port;
        this.handler = handler;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] buffer = new byte[2048];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength(), "UTF-8");

                if (handler != null) {
                    handler.accept(msg);
                }
            }

        } catch (Exception e) {
            System.out.println("UDP Listener stopped.");
        }
    }
}
