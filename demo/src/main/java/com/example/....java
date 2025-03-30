
// import java.io.*;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import java.nio.charset.StandardCharsets;
// import java.nio.file.*;

// public class WWW {
//     public static void main(String[] args) {
//         // Define the folder where logs are stored
//         String logFolderPath = "C:\\Logs";  // Change this to your log folder
//         String logstashURL = "http://localhost:5001/process";  // Log Processor (Logstash-like)

//         System.out.println("🚀 Log Collector Started...");

//         File logFolder = new File(logFolderPath);
//         if (!logFolder.exists() || !logFolder.isDirectory()) {
//             System.out.println("❌ Log folder not found: " + logFolderPath);
//             return;
//         }

//         // Get all .log files in the folder
//         File[] logFiles = logFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".log"));

//         if (logFiles == null || logFiles.length == 0) {
//             System.out.println("⚠️ No .log files found in " + logFolderPath);
//             return;
//         }

//         // Read each log file and send logs
//         for (File logFile : logFiles) {
//             System.out.println("📂 Processing log file: " + logFile.getName());
//             processLogFile(logFile, logstashURL);
//         }
//     }

//     private static void processLogFile(File logFile, String serverURL) {
//         try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
//             String line;
//             while ((line = reader.readLine()) != null) {
//                 System.out.println("📤 Sending log to Logstash: " + line);
//                 sendLog(line, serverURL);
//             }
//         } catch (IOException e) {
//             System.out.println("❌ Error reading log file: " + logFile.getName() + " - " + e.getMessage());
//         }
//     }

//     private static void sendLog(String log, String serverURL) {
//         try {
//             URL url = new URL(serverURL);
//             HttpURLConnection connection = (HttpURLConnection) url.openConnection();

//             connection.setRequestMethod("POST");
//             connection.setRequestProperty("Content-Type", "application/json");
//             connection.setDoOutput(true);

//             // Convert log message to JSON format
//             String jsonLog = "{\"log\": \"" + log.replace("\"", "\\\"") + "\"}";

//             connection.getOutputStream().write(jsonLog.getBytes(StandardCharsets.UTF_8));

//             int responseCode = connection.getResponseCode();
//             if (responseCode == 200) {
//                 System.out.println("✅ Log sent successfully!");
//             } else {
//                 System.out.println("⚠️ Failed to send log. Response: " + responseCode);
//             }

//             connection.disconnect();
//         } catch (Exception e) {
//             System.out.println("❌ Error sending log: " + e.getMessage());
//         }
//     }
// }
