package tcp;

import firebase_rest.FirebaseDatabaseREST;
import udp.UDPNotificationServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {

    private final int port;

    // All TCP clients (online)
    public final Set<ClientHandler> clients = new HashSet<>();

    // Rooms
    public final Map<String, Set<ClientHandler>> rooms = new HashMap<>();

    // UDP server
    public final UDPNotificationServer udp;
    public Map<String, String> avatarCache = new HashMap<>();
    public ChatServer(int port) {
        this.port = port;

        rooms.put("global", new HashSet<>());
        rooms.put("1", new HashSet<>());
        rooms.put("2", new HashSet<>());
        rooms.put("3", new HashSet<>());

        udp = new UDPNotificationServer(6000);
        new Thread(udp).start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("=== Chat Server running on port " + port + " ===");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());

                ClientHandler handler = new ClientHandler(socket, this);
                new Thread(handler).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==========================================================
    // UNIFIED ONLINE/OFFLINE NOTIFY → ONLY THESE ARE USED
    // ==========================================================
    public synchronized void notifyOnline(String name) {
        for (ClientHandler c : clients) {
            c.send("ONLINE|" + name);
        }
    }

    public synchronized void notifyOffline(String name) {
        for (ClientHandler c : clients) {
            c.send("OFFLINE|" + name);
        }
    }

    // ==========================================================
    // ROOM MESSAGE
    // ==========================================================
    public void sendRoom(String room, String sender, String msg) {
        if (!rooms.containsKey(room)) return;

        try {
            String base64 = avatarCache.get(sender);
            if (base64 == null) {
                base64 = FirebaseDatabaseREST.getAvatar(sender);
                avatarCache.put(sender, base64);
            }

            String packet =
                    "ROOM|" + room + "|" + sender + "|" +
                            (base64 == null ? "" : base64) + "|" +
                            msg;

            for (ClientHandler c : rooms.get(room)) {
                c.send(packet);
            }

            System.out.println("DEBUG SEND = " + packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // ==========================================================
    // JOIN ROOM
    // ==========================================================
    public synchronized void joinRoom(String room, ClientHandler c) {
        if (!rooms.containsKey(room)) rooms.put(room, new HashSet<>());

        // remove old
        if (c.currentRoom != null)
            rooms.get(c.currentRoom).remove(c);

        // add new
        rooms.get(room).add(c);
        c.currentRoom = room;

        c.send("Joined room: " + room);
    }

    // ==========================================================
    // REMOVE CLIENT
    // ==========================================================
    public synchronized void removeClient(ClientHandler c) {
        clients.remove(c);

        for (Set<ClientHandler> set : rooms.values()) {
            set.remove(c);
        }

        notifyOffline(c.name);

        udp.notifyAllClients("USER_OFFLINE|" + c.name);
    }

    public static void main(String[] args) {
        new ChatServer(5000).start();
    }
}
