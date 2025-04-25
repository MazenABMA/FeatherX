package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class LogProcessor {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(5001), 0);
        server.createContext("/process", new LogHandler());
        server.setExecutor(null);
        System.out.println("🚀 Log Processor running on port 5001...");
        server.start();
    }

    static class LogHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }

                String logMessage = requestBody.toString();
                System.out.println("📥 Received log: " + logMessage);

                // Extract log details
                String timestamp = extractTimestamp(logMessage);
                String level = extractLogLevel(logMessage);
                String message = extractLogMessage(logMessage);

                // Check log level and forward if needed
                if (level.equals("ERROR") || level.equals("CRITICAL") || level.equals("FATAL") || level.equals("WARNING")) {
                    System.out.println("⚠️ Important log detected! Forwarding to Elasticsearch...");
                    sendToElasticsearch(timestamp, level, message);
                } else {
                    System.out.println("✅ Log ignored (not an error or critical issue).");
                }

                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().close();
            }
        }

        // Extract timestamp from log or generate a new one
        private String extractTimestamp(String log) {
            String extractedTimestamp = log.replaceAll(".*\\[(.*?)\\].*", "$1");
            return extractedTimestamp.equals(log) ? getCurrentTimestamp() : extractedTimestamp;
        }

        // Extract log level (INFO, ERROR, WARN, etc.)
        private String extractLogLevel(String log) {
            String extractedLevel = log.replaceAll(".*\\] (.*?) - .*", "$1");
            return extractedLevel.equals(log) ? "INFO" : extractedLevel;
        }

        // Extract actual log message
        private String extractLogMessage(String log) {
            String extractedMessage = log.replaceAll(".* - (.*)", "$1");
            return extractedMessage.equals(log) ? "Unknown log message" : extractedMessage;
        }

        // Send structured log data to Elasticsearch
        private void sendToElasticsearch(String timestamp, String level, String message) {
            try {
                String jsonLog = "{"
                        + "\"timestamp\": \"" + timestamp + "\","
                        + "\"level\": \"" + level + "\","
                        + "\"message\": \"" + message + "\""
                        + "}";

                URL url = new URL("http://localhost:9200/logs/_doc");  // Elasticsearch index: "logs"
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                os.write(jsonLog.getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                System.out.println("📤 Sent to Elasticsearch! Response: " + responseCode);

                connection.disconnect();
            } catch (Exception e) {
                System.out.println("❌ Error sending log to Elasticsearch: " + e.getMessage());
            }
        }

        // Generate ISO 8601 formatted timestamp (UTC)
        private String getCurrentTimestamp() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(new Date());
        }
    }
}
