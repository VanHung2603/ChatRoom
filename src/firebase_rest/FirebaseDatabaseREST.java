package firebase_rest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseREST {
    private static final String USERS = "/users/";
    private static final String DB_URL =
            "https://chatroomfinal-af2cf-default-rtdb.firebaseio.com";
    public static void saveAvatar(String username, String base64) throws Exception {
        URL url = new URL(DB_URL + "/avatars/" + username + ".json");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setDoOutput(true);

        String json = "\"" + base64 + "\"";

        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes());
        }

        con.getInputStream().close();
    }

    public static String getAvatar(String username) throws Exception {
        URL url = new URL(DB_URL + "/avatars/" + username + ".json");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String resp = br.readLine();
            if (resp == null || resp.equals("null")) return null;
            return resp.replace("\"", "");
        }
    }
    private static String GET(String path) throws Exception {
        URL url = new URL(DB_URL + path + ".json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    private static void PUT(String path, String json) throws Exception {
        URL url = new URL(DB_URL + path + ".json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        con.setDoOutput(true);

        OutputStream os = con.getOutputStream();
        os.write(json.getBytes());
        os.close();

        con.getInputStream().close();
    }

    private static void DELETE(String path) throws Exception {
        URL url = new URL(DB_URL + path + ".json");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.getInputStream().close();
    }

    public static Map<String, Map<String, Object>> getAllUsers() throws Exception {
        String json = GET(USERS);
        if (json.equals("null")) return new HashMap<>();

        return new com.google.gson.Gson().fromJson(
                json,
                Map.class
        );
    }

    public static void addUser(String username, String email, String role) throws Exception {
        String json = "{"
                + "\"email\":\"" + email + "\","
                + "\"role\":\"" + role + "\","
                + "\"banned\":false"
                + "}";

        PUT(USERS + username, json);
    }

    public static void updateUser(String username, String email, String role, boolean banned) throws Exception {
        String json = "{"
                + "\"email\":\"" + email + "\","
                + "\"role\":\"" + role + "\","
                + "\"banned\":" + banned
                + "}";

        PUT(USERS + username, json);
    }

    public static void deleteUser(String username) throws Exception {
        DELETE(USERS + username);
    }

    public static void setBanned(String username, boolean banned) throws Exception {
        PUT(USERS + username + "/banned", banned ? "true" : "false");
    }
}
