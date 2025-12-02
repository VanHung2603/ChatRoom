package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("UserService", new UserServiceImpl());

            System.out.println("RMI Server running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
