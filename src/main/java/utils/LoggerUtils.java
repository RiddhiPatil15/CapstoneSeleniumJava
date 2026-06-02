package utils;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;

public class LoggerUtils {

    private static final String FOLDER = "UserLogs";
    private static FileWriter writer;
    private static String currentUser;

    public static void initUser(String email) {
        try {
            currentUser = email;
            new File(FOLDER).mkdirs();
            writer = new FileWriter(FOLDER + "/" + email + ".txt", true);

            log("===== NEW TEST RUN =====");
            log("User: " + email);
            log("Time: " + LocalDateTime.now());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void log(String message) {
        try {
            if (writer == null) return;
            writer.write(message + "\n");
            writer.flush();
        } catch (Exception ignored) {}
    }
    public static void info(String message) {
        log("[INFO] " + message);
    }
    public static void warn(String message) {
        log("[WARN] " + message);
    }
    public static void error(String message) {
        log("[ERROR] " + message);
    }


    public static void close() {
        try {
            if (writer != null) {
                log("===== END RUN =====");
                log("========================\n");
                writer.close();
            }
        } catch (Exception ignored) {}
    }
}