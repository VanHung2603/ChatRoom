package firebase_rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseDB {

    private static final String DB_URL = "https://chatroomfinal-af2cf-default-rtdb.firebaseio.com";

    private static String request(String method, String endpoint, String jsonBody) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");

        if (jsonBody != null) {
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(jsonBody.getBytes());
            os.flush();
        }

        InputStream is = conn.getInputStream();
        return new String(is.readAllBytes());
    }

    // -------------------- SAVE MESSAGE --------------------
    public static void saveMessage(String room, String username, String text) {
        String endpoint = DB_URL + "/chatrooms/" + room + "/messages.json";

        String body = "{"
                + "\"username\":\"" + username + "\","
                + "\"text\":\"" + text + "\","
                + "\"timestamp\":" + System.currentTimeMillis()
                + "}";

        try {
            request("POST", endpoint, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------- LOAD MESSAGE HISTORY --------------------
    public static String loadMessages(String room) {
        String endpoint = DB_URL + "/chatrooms/" + room + "/messages.json";

        try {
            return request("GET", endpoint, null);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
