package tcp;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;

    private BufferedReader in;
    private PrintWriter out;

    public String name = "Unknown";
    public String currentRoom = "global";
    public int udpPort = -1;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // ==========================================
            // READ 2 INIT LINES
            // ==========================================
            String loginLine = in.readLine();
            String udpLine   = in.readLine();

            if (loginLine == null) return;
            if (udpLine == null)  return;

            if (loginLine.startsWith("LOGIN|")) {
                name = loginLine.substring(6);
            }

            if (udpLine.startsWith("UDP|")) {
                udpPort = Integer.parseInt(udpLine.substring(4));
                server.udp.registerClient(name, udpPort);
            }

            System.out.println("LOGIN OK => " + name);

            // ==========================================
            // IMPORTANT: ADD CLIENT FIRST
            // ==========================================
            synchronized (server.clients) {
                server.clients.add(this);
            }

            // ==========================================
            // SEND ALL CURRENT ONLINE USERS TO NEW USER
            // ==========================================
            for (ClientHandler c : server.clients) {
                if (c != this) {
                    this.send("ONLINE|" + c.name);
                }
            }

            // ==========================================
            // BROADCAST THIS USER ONLINE
            // ==========================================
            server.notifyOnline(name);

            // ==========================================
            out.println("Welcome " + name + "!");

            // JOIN DEFAULT ROOM
            server.joinRoom("global", this);

            // ==========================================
            // MAIN LOOP
            // ==========================================
            String line;
            while ((line = in.readLine()) != null) {

                if (line.startsWith("/join ")) {
                    String room = line.substring(6).trim();
                    server.joinRoom(room, this);
                    continue;
                }

                server.sendRoom(currentRoom, name, line);
            }

        } catch (Exception e) {
            System.out.println("Client error: " + name + " => " + e.getMessage());

        } finally {

            server.notifyOffline(name);
            server.udp.unregisterClient(name);
            server.removeClient(this);

            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public void send(String msg) {
        out.println(msg);
    }
}
