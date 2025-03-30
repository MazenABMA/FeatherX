//     package com.example;

// import java.io.*;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import java.util.Scanner;
// import com.jcraft.jsch.*;
// import com.jcraft.jsch.JSch;
// import com.jcraft.jsch.Session;
// import com.jcraft.jsch.ChannelExec;
// public class LogCollector {
//     public static void main(String[] args) {
//         Scanner scanner = new Scanner(System.in);

//         System.out.println("🔍 Choose Log Source:");
//         System.out.println("1️⃣ Windows Event Logs");
//         System.out.println("2️⃣ Linux System Logs");
//         System.out.println("3️⃣ Remote Server (SSH)");
//         System.out.println("4️⃣ Web API Logs");
//         System.out.print("Enter your choice: ");

//         int choice = scanner.nextInt();
//         scanner.nextLine(); // Consume newline

//         switch (choice) {
//             case 1:
//                 collectWindowsEventLogs();
//                 break;
//             case 2:
//                 collectLinuxSystemLogs();
//                 break;
//             case 3:
//                 collectRemoteLogs();
//                 break;
//             case 4:
//                 collectAPILogs();
//                 break;
//             default:
//                 System.out.println("❌ Invalid choice! Exiting...");
//         }

//         scanner.close();
//     }

//     // 📌 1. Collect Windows Event Logs (PowerShell)
//     public static void collectWindowsEventLogs() {
//         System.out.println("🖥️ Collecting Windows Event Logs...");
//         try {po
//             Process process = Runtime.getRuntime().exec("powershell Get-EventLog -LogName System -Newest 10");

//             BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//             String line;
//             while ((line = reader.readLine()) != null) {
//                 System.out.println("📥 Log: " + line);
//             }
//         } catch (Exception e) {
//             System.out.println("❌ Error: " + e.getMessage());
//         }
//     }

//     // 📌 2. Collect Linux System Logs (`/var/log/syslog`)
//     public static void collectLinuxSystemLogs() {
//         System.out.println("🐧 Collecting Linux System Logs in real-time...");
    
//         try {
//             ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "tail -f /var/log/syslog");
//             processBuilder.redirectErrorStream(true); // Redirect errors to standard output
//             Process process = processBuilder.start();
    
//             BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//             String line;
            
//             // Keep reading the output continuously
//             while ((line = reader.readLine()) != null) {
//                 System.out.println("📥 Log: " + line);
//             }
    
//         } catch (Exception e) {
//             System.out.println("❌ Error: " + e.getMessage());
//         }
//     }

//     // 📌 3. Collect Logs from a Remote Server (via SSH)
//     public static void collectRemoteLogs() {
//         Scanner scanner = new Scanner(System.in);
//         System.out.print("🌐 Enter Remote Server IP: ");
//         String host = scanner.nextLine();
//         System.out.print("👤 Enter Username: ");
//         String user = scanner.nextLine();
//         System.out.print("🔑 Enter Password: ");
//         String password = scanner.nextLine();
//        String command = "journalctl -f -u ssh";

//         System.out.println("🔗 Connecting to " + host + "...");
//         try {
//             JSch jsch = new JSch();
//             Session session = jsch.getSession(user, host, 22);
//             session.setPassword(password);
//             session.setConfig("StrictHostKeyChecking", "no");
//             session.connect();

//             ChannelExec channel = (ChannelExec) session.openChannel("exec");
//             channel.setCommand(command);
//             channel.setInputStream(null);
//             channel.setErrStream(System.err);

//             BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
//             channel.connect();

//             String line;
//             while ((line = reader.readLine()) != null) {
//                 System.out.println("📥 Remote Log: " + line);
//             }

//             channel.disconnect();
//             session.disconnect();
//         } catch (Exception e) {
//             System.out.println("❌ Error: " + e.getMessage());
//         }
//     }

//     // 📌 4. Collect Logs from a Web API
//     public static void collectAPILogs() {
//         Scanner scanner = new Scanner(System.in);
//         System.out.print("🌐 Enter API URL: ");
//         String logAPI = scanner.nextLine();

//         System.out.println("🔍 Fetching logs from API...");
//         try {
//             URL url = new URL(logAPI);
//             HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//             conn.setRequestMethod("GET");

//             BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//             String line;
//             while ((line = reader.readLine()) != null) {
//                 System.out.println("📥 API Log: " + line);
//             }

//             conn.disconnect();
//         } catch (Exception e) {
//             System.out.println("❌ Error: " + e.getMessage());
//         }
//     }
// }
package com.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.jcraft.jsch.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Memory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LogCollector {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\uD83D\uDD0D Choose Log Source:");
        System.out.println("1\uFE0F\u20E3 Windows Event Logs");
        System.out.println("2\uFE0F\u20E3 Linux System Logs");
        System.out.println("3\uFE0F\u20E3 Remote Server (SSH)");
        System.out.println("4\uFE0F\u20E3 Web API Logs");
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
        System.out.println("🖥️ Collecting Windows Event Logs...");

        WinNT.HANDLE hEventLog = Advapi32.INSTANCE.OpenEventLog(null, "System");
        if (hEventLog == null) {
            System.out.println("❌ Failed to open event log");
            return;
        }

        byte[] buffer = new byte[4096];
        Memory memory = new Memory(buffer.length);
        memory.write(0, buffer, 0, buffer.length);

        IntByReference bytesRead = new IntByReference();
        IntByReference minBytesNeeded = new IntByReference();

        while (Advapi32.INSTANCE.ReadEventLog(hEventLog, 
                WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_FORWARDS_READ, 
                0, memory, (int) memory.size(), bytesRead, minBytesNeeded)) {

            ByteBuffer byteBuffer = memory.getByteBuffer(0, bytesRead.getValue()).order(ByteOrder.LITTLE_ENDIAN);
            int eventID = byteBuffer.getInt(8);
            System.out.println("📥 Event ID: " + eventID);
        }

        Advapi32.INSTANCE.CloseEventLog(hEventLog);
    }

    // Linux System Logs
    public static void collectLinuxSystemLogs() {
        System.out.println("🐧 Collecting Linux System Logs...");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "tail -n 10 /var/log/syslog");
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

    // Remote Server Logs
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
