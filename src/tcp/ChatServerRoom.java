//package tcp;
//
//import firebase_rest.FirebaseDB;
//import udp.UDPNotificationServer;
//
//import java.util.*;
//
//public class ChatServerRoom {
//
//    private static final Map<String, List<ClientHandler>> rooms = new HashMap<>();
//    private static final UDPNotificationServer udp = new UDPNotificationServer();
//
//    // Join room
//    public static synchronized void joinRoom(String room, ClientHandler client) {
//        rooms.putIfAbsent(room, new ArrayList<>());
//        rooms.get(room).add(client);
//
//        // Load history
//        String history = FirebaseDB.loadMessages(room);
//        if (history != null) {
//            client.send("[HISTORY]\n" + history);
//        }
//
//        broadcast(room, "[SYSTEM] " + client.getName() + " has joined the room.");
//
//        // Notify via UDP
//        udp.notifyAllClients("JOIN_ROOM|" + client.getName() + "|" + room);
//    }
//
//    // Leave room
//    public static synchronized void leaveRoom(String room, ClientHandler client) {
//        if (rooms.containsKey(room)) {
//            rooms.get(room).remove(client);
//
//            broadcast(room,
//                    "[SYSTEM] " + client.getName() + " left the room.");
//
//            udp.notifyAllClients("LEAVE_ROOM|" + client.getName() + "|" + room);
//        }
//    }
//
//    // Broadcast message
//    public static synchronized void broadcast(String room, String message) {
//        if (!rooms.containsKey(room)) return;
//
//        // Save to firebase
//        FirebaseDB.saveMessage(room, "SYSTEM", message);
//
//        for (ClientHandler client : rooms.get(room)) {
//            client.send(message);
//        }
//
//        // Notify
//        udp.notifyAllClients("NEW_MESSAGE|" + room + "|" + message);
//    }
//}
