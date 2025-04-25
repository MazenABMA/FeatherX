
package com.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.jcraft.jsch.*;
import java.util.List;
import java.util.ArrayList;

public class LogCollector {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\uD83D\uDD0D Choose Log Source:");
        System.out.println("1️⃣ Windows Event Logs");
        System.out.println("2️⃣ Linux System Logs");
        System.out.println("3️⃣ Remote Server (SSH)");
        System.out.println("4️⃣ Web API Logs");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                collectWindowsEventLogs();
                break;
            case 2:
                collectLinuxSystemLogs();
                break;
            case 3:
                collectRemoteLogs();
                break;
            case 4:
                collectAPILogs();
                break;
            default:
                System.out.println("❌ Invalid choice! Exiting...");
        }

        scanner.close();
    }

    // Windows Event Logs
    public static void collectWindowsEventLogs() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\uD83D\uDD0D Choose Log Category:");
        System.out.println("1️⃣ Application");
        System.out.println("2️⃣ System");
        System.out.println("3️⃣ Security");
        System.out.print("Enter your choice: ");
        
        int categoryChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String logCategory;
        switch (categoryChoice) {
            case 1:
                logCategory = "Application";
                break;
            case 2:
                logCategory = "System";
                break;
            case 3:
                logCategory = "Security";
                break;
            default:
                System.out.println("❌ Invalid choice! Exiting...");
                return;
        }

        System.out.println("\uD83D\uDD0D Choose Event Log Type:");
        System.out.println("1️⃣ Error");
        System.out.println("2️⃣ Information");
        System.out.println("3️⃣ Warning");
        System.out.print("Enter your choice: ");
        
        int logTypeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String logType;
        switch (logTypeChoice) {
            case 1:
                logType = "Error";
                break;
            case 2:
                logType = "Information";
                break;
            case 3:
                logType = "Warning";
                break;
            default:
                System.out.println("❌ Invalid choice! Exiting...");
                return;
        }

        System.out.println("🪟 Collecting Windows Event Logs (" + logCategory + " - " + logType + ")...");
        try {
            List<String> eventLogs = readWindowsEventLogs(logCategory, logType);
            for (String log : eventLogs) {
                System.out.println("📥 Log: " + log);
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    public static List<String> readWindowsEventLogs(String logCategory, String logType) throws IOException {
        List<String> logs = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "Get-EventLog", "-LogName", logCategory, "-EntryType", logType, "-Newest", "10");
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            logs.add(line);
        }
        return logs;
    }

    // Linux System Logs
    public static void collectLinuxSystemLogs() {
        System.out.println("🐧 Collecting Linux System Logs...");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "tail -n 10 /var/log/");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("📥 Log: " + line);
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // Remote Server Logs (SSH)
    public static void collectRemoteLogs() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("🌐 Enter Remote Server IP: ");
        String host = scanner.nextLine();
        System.out.print("👤 Enter Username: ");
        String user = scanner.nextLine();
        System.out.print("🔑 Enter Password: ");
        String password = scanner.nextLine();
        String command = "journalctl -n 10";

        System.out.println("🔗 Connecting to " + host + "...");
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);

            BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("📥 Remote Log: " + line);
            }

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // API Logs
    public static void collectAPILogs() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("🌐 Enter API URL: ");
        String logAPI = scanner.nextLine();

        System.out.println("🔍 Fetching logs from API...");
        try {
            URL url = new URL(logAPI);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("📥 API Log: " + line);
            }

            conn.disconnect();
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}