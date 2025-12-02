package client;

import udp.UDPClientListener;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientAPI {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Thread tcpThread;
    private Thread udpThread;

    private Consumer<String> onTcpMessage;
    private Consumer<String> onUdpMessage;

    private String username;
    private int udpPort;

    public ClientAPI() {}

    // ==========================
    // CONNECT
    // ==========================
    public boolean connect(String host, int port, String username, int udpPort) {
        try {
            this.username = username;
            this.udpPort = udpPort;

            socket = new Socket(host, port);

            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Read greeting
            System.out.println(in.readLine()); // === Chat Server ===
            System.out.println("Sending username...");
            out.println(username);

            System.out.println("Sending UDP port...");
            out.println(udpPort);

            // TCP listener
            tcpThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (onTcpMessage != null) onTcpMessage.accept(line);
                    }
                } catch (Exception ignored) {}
            });
            tcpThread.setDaemon(true);
            tcpThread.start();

            // UDP listener
            udpThread = new Thread(new UDPClientListener(udpPort, (msg) -> {
                if (onUdpMessage != null) onUdpMessage.accept(msg);
            }));
            udpThread.setDaemon(true);
            udpThread.start();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================
    // SEND MESSAGE
    // ==========================
    public void sendMessage(String msg) {
        if (out != null) out.println(msg);
    }

    // ==========================
    // LISTENERS
    // ==========================
    public void setOnTcpMessage(Consumer<String> handler) {
        this.onTcpMessage = handler;
    }

    public void setOnUdpMessage(Consumer<String> handler) {
        this.onUdpMessage = handler;
    }

    // ==========================
    // DISCONNECT
    // ==========================
    public void disconnect() {
        try {
            if (out != null) out.println("/quit");
            if (socket != null) socket.close();
        } catch (Exception ignored) {}
    }
}
