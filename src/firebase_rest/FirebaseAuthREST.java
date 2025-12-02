package firebase_rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;

public class FirebaseAuthREST {

    private static final String API_KEY = "AIzaSyCCNzIHR6RvdiSurW94AJxZ7GS7BivjHXA";

    private static final String SIGN_IN_URL =
            "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;

    private static final String SIGN_UP_URL =
            "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY;

    private static final String UPDATE_URL =
            "https://identitytoolkit.googleapis.com/v1/accounts:update?key=" + API_KEY;


    // ============================================================
    // REGISTER WITH DISPLAY NAME  (3 PARAMETERS)
    // ============================================================
//    public static String register(String email, String password, String displayName) throws Exception {
//
//        // Step 1: Sign Up
//        URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY);
//        String body = "{"
//                + "\"email\":\"" + email + "\","
//                + "\"password\":\"" + password + "\","
//                + "\"returnSecureToken\":true"
//                + "}";
//
//        String signUpResponse = sendPOST(String.valueOf(url), body);
//
//        JsonObject obj = JsonParser.parseString(signUpResponse).getAsJsonObject();
//        String idToken = obj.get("idToken").getAsString();
//
//        // Step 2: Update displayName
//        return updateProfile(idToken, displayName);
//    }
    public static JsonObject register(String email, String password, String displayName) throws Exception {

        // 1) SIGN UP (email + password)
        URL url = new URL(SIGN_UP_URL);
        String body = "{"
                + "\"email\":\"" + email + "\","
                + "\"password\":\"" + password + "\","
                + "\"returnSecureToken\":true"
                + "}";

        String signUpResponse = sendPOST(String.valueOf(url), body);
        JsonObject obj = JsonParser.parseString(signUpResponse).getAsJsonObject();

        if (!obj.has("idToken")) {
            // đăng ký thất bại
            return obj;
        }

        String idToken = obj.get("idToken").getAsString();

        // 2) UPDATE displayName
        String result = updateProfile(idToken, displayName);
        JsonObject updated = JsonParser.parseString(result).getAsJsonObject();

        updated.addProperty("idToken", idToken);

        return updated;
    }

    public static String updateProfile(String idToken, String displayName) throws Exception {
        URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:update?key=" + API_KEY);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        String json = "{"
                + "\"idToken\":\"" + idToken + "\","
                + "\"displayName\":\"" + displayName + "\","
                + "\"returnSecureToken\":true"
                + "}";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();

        return sb.toString();
    }
    public static String uploadAvatar(File file, String idToken) throws Exception {

        String bucket = "chatroomfinal.appspot.com";

        String uploadUrl =
                "https://firebasestorage.googleapis.com/v0/b/" + bucket +
                        "/o?uploadType=media&name=avatars/" + file.getName();

        HttpURLConnection conn = (HttpURLConnection) new URL(uploadUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "image/png");
        conn.setRequestProperty("Authorization", "Bearer " + idToken);

        OutputStream os = conn.getOutputStream();
        os.write(Files.readAllBytes(file.toPath()));
        os.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();

        JsonObject obj = JsonParser.parseString(sb.toString()).getAsJsonObject();

        String name = obj.get("name").getAsString();
        String token = obj.get("downloadTokens").getAsString();

        String encodedName = URLEncoder.encode(name, "UTF-8");

        return "https://firebasestorage.googleapis.com/v0/b/" +
                bucket + "/o/" + encodedName +
                "?alt=media&token=" + token;
    }



    // ============================================================
    // LOGIN → return displayName
    // ============================================================
    public static String login(String email, String password) throws Exception {
        URL url = new URL(SIGN_IN_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        String json = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\", \"returnSecureToken\": true }";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }

        int code = conn.getResponseCode();

        BufferedReader br;
        if (code == 200) {
            // login OK
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            // login FAILED → THROW ERROR
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

            StringBuilder error = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) error.append(line);

            throw new Exception("Login failed: " + error);
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);

        JsonObject obj = JsonParser.parseString(sb.toString()).getAsJsonObject();

        return obj.has("displayName") ? obj.get("displayName").getAsString() : email;
    }



    // ============================================================
    // POST helper
    // ============================================================
    private static String sendPOST(String urlStr, String json) throws Exception {

        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes());
        }

        BufferedReader br;

        if (con.getResponseCode() == 200)
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        else
            br = new BufferedReader(new InputStreamReader(con.getErrorStream()));

        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null)
            sb.append(line);

        return sb.toString();
    }
}
