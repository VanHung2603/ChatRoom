package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class RMIClientTest {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            UserService userService = (UserService) registry.lookup("UserService");

            // Register test
//            String reg = userService.register("alice", "123");
//            System.out.println("Register: " + reg);

            // Login test
            String login = userService.login("alice", "123");
            System.out.println("Login: " + login);

            // Friend list
            List<String> friends = userService.getFriends("alice");
            System.out.println("Friends: " + friends);

            // Room list
            List<String> rooms = userService.getRooms();
            System.out.println("Rooms: " + rooms);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
