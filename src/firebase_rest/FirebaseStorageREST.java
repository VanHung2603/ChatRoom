package firebase_rest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;

public class FirebaseStorageREST {

    private static final String BUCKET = "chatroomfinal-af2cf-default-rtdb.appspot.com";

    public static String uploadAvatar(File file, String username) throws Exception {

        String uploadUrl =
                "https://firebasestorage.googleapis.com/v0/b/" +
                        BUCKET + "/o?uploadType=media&name=avatars/" +
                        username + "_" + file.getName();

        HttpURLConnection conn = (HttpURLConnection) new URL(uploadUrl).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "image/png");

        // upload bytes
        byte[] bytes = Files.readAllBytes(file.toPath());
        conn.getOutputStream().write(bytes);

        // read response
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);

        JsonObject obj = JsonParser.parseString(sb.toString()).getAsJsonObject();

        // file path on server
        String name = obj.get("name").getAsString();
        String token = obj.get("downloadTokens").getAsString();

        String encoded = URLEncoder.encode(name, "UTF-8");

        return "https://firebasestorage.googleapis.com/v0/b/" +
                BUCKET + "/o/" + encoded + "?alt=media&token=" + token;
    }
}
