package db;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ChatHistoryStore {

    private static final Path ROOT = Paths.get("db", "rooms");

    static {
        try {
            Files.createDirectories(ROOT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ghi thêm 1 dòng lịch sử vào file room.log
    public static synchronized void append(String room, String line) {
        Path file = ROOT.resolve(room + ".log");
        try (BufferedWriter w = Files.newBufferedWriter(
                file,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        )) {
            w.write(line);
            w.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static synchronized List<String> load(String room, int maxLines) {
        Path file = ROOT.resolve(room + ".log");
        List<String> result = new ArrayList<>();
        if (!Files.exists(file)) return result;

        try (BufferedReader r = Files.newBufferedReader(file)) {
            Deque<String> buffer = new ArrayDeque<>();
            String line;
            while ((line = r.readLine()) != null) {
                buffer.addLast(line);
                if (buffer.size() > maxLines) {
                    buffer.removeFirst();
                }
            }
            result.addAll(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
