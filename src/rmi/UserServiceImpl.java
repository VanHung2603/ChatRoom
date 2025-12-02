package rmi;

import firebase_rest.FirebaseAuthREST;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Arrays;
import java.util.List;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {

    public UserServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String register(String email, String password, String displayName) throws RemoteException {
        try {
            // FirebaseAuthREST đã hỗ trợ displayName trong hàm register()
            String resp = String.valueOf(FirebaseAuthREST.register(email, password, displayName));

            if (resp.contains("displayName") || resp.contains("idToken")) {
                return "OK";
            }

            return "ERR|" + resp;

        } catch (Exception e) {
            return "ERR|" + e.getMessage();
        }
    }

    @Override
    public String login(String email, String password) throws RemoteException {
        try {
            String loginJson = FirebaseAuthREST.login(email, password);

            JsonObject obj = JsonParser.parseString(loginJson).getAsJsonObject();

            if (!obj.has("idToken")) {
                return "ERR|" + loginJson;
            }

            return loginJson;

        } catch (Exception e) {
            return "ERR|" + e.getMessage();
        }
    }

    @Override
    public List<String> getFriends(String username) throws RemoteException {
        return Arrays.asList("alice", "bob", "charlie");
    }

    @Override
    public List<String> getRooms() throws RemoteException {
        return Arrays.asList("global", "1", "2", "3");
    }
}
