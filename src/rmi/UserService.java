package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UserService extends Remote {

    String register(String email, String password, String displayName) throws RemoteException;

    String login(String username, String password) throws RemoteException;

    List<String> getFriends(String username) throws RemoteException;

    List<String> getRooms() throws RemoteException;
}
