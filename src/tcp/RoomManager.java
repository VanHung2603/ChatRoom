package tcp;

import java.util.*;
import tcp.ClientHandler;
public class RoomManager {

    // roomName -> set client
    private static final Map<String, Set<ClientHandler>> rooms = new HashMap<>();

    public static synchronized void joinRoom(String room, ClientHandler client) {
        rooms.putIfAbsent(room, new HashSet<>());
        rooms.get(room).add(client);
    }

    public static synchronized void leaveRoom(String room, ClientHandler client) {
        if (room == null) return;
        Set<ClientHandler> set = rooms.get(room);
        if (set != null) {
            set.remove(client);
        }
    }

    public static synchronized void leaveAllRooms(ClientHandler client) {
        for (Set<ClientHandler> set : rooms.values()) {
            set.remove(client);
        }
    }

    public static synchronized Set<ClientHandler> getRoomMembers(String room) {
        Set<ClientHandler> set = rooms.get(room);
        if (set == null) return Collections.emptySet();
        return new HashSet<>(set);
    }
    public static synchronized Set<String> getAllRooms() {
        return rooms.keySet();
    }

}
