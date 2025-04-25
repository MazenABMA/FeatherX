
// package com.example;

// import com.sun.jna.platform.win32.*;
// import com.sun.jna.ptr.IntByReference;
// import com.sun.jna.Memory;
// import java.nio.ByteBuffer;
// import java.nio.ByteOrder;
// import java.text.SimpleDateFormat;
// import java.util.*;
// import org.json.JSONObject;

// public class WindowsEventViewer {
//     private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//     public static void collectWindowsEventLogs(String logName, int daysAgo) {
//         System.out.println("\n🖥️ Collecting Windows Event Logs from: " + logName);
//         long cutoffTimestamp = getTimestampStartOfDay(daysAgo);
//         System.out.println("📅 Showing events from timestamp: " + cutoffTimestamp + " (" + dateFormat.format(new Date(cutoffTimestamp)) + ")");

//         WinNT.HANDLE hEventLog = Advapi32.INSTANCE.OpenEventLog(null, logName);
//         if (hEventLog == null) {
//             System.out.println("❌ Failed to open event log: " + logName);
//             return;
//         }

//         int bufferSize = 4096;
//         Memory memory = new Memory(bufferSize);
//         IntByReference bytesRead = new IntByReference();
//         IntByReference minBytesNeeded = new IntByReference();

//         boolean foundEvents = false;

//         while (Advapi32.INSTANCE.ReadEventLog(hEventLog,
//                 WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_FORWARDS_READ,
//                 0, memory, (int) memory.size(), bytesRead, minBytesNeeded)) {
            
//             ByteBuffer byteBuffer = memory.getByteBuffer(0, bytesRead.getValue()).order(ByteOrder.LITTLE_ENDIAN);
            
//             while (byteBuffer.remaining() > 0) {
//                 int startPosition = byteBuffer.position();

//                 // Ensure we can read the record length
//                 if (byteBuffer.remaining() < 4) break;
//                 int recordLength = byteBuffer.getInt();

//                 // Ensure we have enough data for the full record
//                 if (recordLength < 12 || byteBuffer.remaining() < recordLength - 4) {
//                     System.out.println("⚠️ Skipping incomplete event record (expected " + recordLength + " bytes, but found " + byteBuffer.remaining() + ").");
//                     break;
//                 }

//                 byteBuffer.position(startPosition + 8); // Move to Event ID
//                 int eventID = byteBuffer.getInt() & 0xFFFF;

//                 // Read timestamp
//                 if (byteBuffer.remaining() < 4) break;
//                 long timeGenerated = Integer.toUnsignedLong(byteBuffer.getInt()) * 1000L;

//                 // ✅ Filter logs based on timestamp
//                 if (timeGenerated < cutoffTimestamp) {
//                     byteBuffer.position(startPosition + recordLength);
//                     continue;
//                 }

//                 foundEvents = true;

//                 // ✅ Extract Event Message
//                 String message = extractEventMessage(byteBuffer, startPosition, recordLength);

//                 // ✅ JSON Output
//                 JSONObject logEntry = new JSONObject();
//                 logEntry.put("event_id", eventID);
//                 logEntry.put("log_name", logName);
//                 logEntry.put("timestamp", dateFormat.format(new Date(timeGenerated)));
//                 logEntry.put("message", message);

//                 System.out.println("✅ Event: " + logEntry.toString(4));

//                 byteBuffer.position(startPosition + recordLength);
//             }
//         }
//         Advapi32.INSTANCE.CloseEventLog(hEventLog);

//         if (!foundEvents) {
//             System.out.println("⚠️ No events found matching the criteria.");
//         }
//     }

//     // ✅ Extract event message from buffer
//     private static String extractEventMessage(ByteBuffer byteBuffer, int start, int length) {
//         int stringOffset = start + 56; // Event message starts here (approximate)
//         if (stringOffset >= start + length) return "No message available";

//         byteBuffer.position(stringOffset);

//         StringBuilder message = new StringBuilder();
//         while (byteBuffer.position() < start + length) {
//             char c = byteBuffer.getChar();
//             if (c == '\0') break; // Stop at null terminator
//             message.append(c);
//         }

//         return message.toString().trim();
//     }

//     // ✅ Get timestamp for the start of "days ago"
//     private static long getTimestampStartOfDay(int daysAgo) {
//         Calendar calendar = Calendar.getInstance();
//         calendar.add(Calendar.DAY_OF_YEAR, -daysAgo);
//         calendar.set(Calendar.HOUR_OF_DAY, 0);
//         calendar.set(Calendar.MINUTE, 0);
//         calendar.set(Calendar.SECOND, 0);
//         calendar.set(Calendar.MILLISECOND, 0);
//         return calendar.getTimeInMillis();
//     }

//     public static void main(String[] args) {
//         Scanner scanner = new Scanner(System.in);

//         // Get log name
//         System.out.print("Enter the log name to collect (e.g., Security, System, Application): ");
//         String logName = scanner.nextLine().trim();

//         // Get date filter (days ago)
//         System.out.print("Enter how many days ago (0 for today, 1 for yesterday, etc.): ");
//         int daysAgo = Integer.parseInt(scanner.nextLine().trim());

//         scanner.close();
//         collectWindowsEventLogs(logName, daysAgo);
//     }
// }
package com.example;

import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Memory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.JSONObject;

public class WindowsEventViewer {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static boolean collectVerboseLogs = false;
    private static long cutoffTimestamp = 0;
    private static boolean realTime = true;
    private static final int TIMEOUT_SECONDS = 60; // Timeout period for real-time log collection

    public static void collectLogs(String logName) {
        WinNT.HANDLE hEventLog = Advapi32.INSTANCE.OpenEventLog(null, logName);
        if (hEventLog == null) {
            System.out.println("❌ Failed to open event log: " + logName);
            return;
        }

        int bufferSize = 4096;
        Memory memory = new Memory(bufferSize);
        IntByReference bytesRead = new IntByReference();
        IntByReference minBytesNeeded = new IntByReference();

        System.out.println("🖥️ Collecting Windows Event Logs from: " + logName);
        System.out.println("📅 Cutoff timestamp: " + dateFormat.format(new Date(cutoffTimestamp)));

        int readFlags = realTime ? WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_BACKWARDS_READ :
                                    WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_FORWARDS_READ;

        long lastLogTime = System.currentTimeMillis();
        
        while (true) {
            System.out.println("🔄 Reading event log...");
            boolean success = Advapi32.INSTANCE.ReadEventLog(hEventLog, readFlags, 0, memory, (int) memory.size(), bytesRead, minBytesNeeded);

            if (!success) {
                int errorCode = Kernel32.INSTANCE.GetLastError();
                if (errorCode == WinError.ERROR_HANDLE_EOF) {
                    if (realTime) {
                        long currentTime = System.currentTimeMillis();
                        if ((currentTime - lastLogTime) / 1000 > TIMEOUT_SECONDS) {
                            System.out.println("⏳ Timeout reached, no new logs found. Exiting...");
                            break;
                        }
                        // No more records, wait for new events
                        System.out.println("⏳ No more records, waiting for new events...");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    } else {
                        break;
                    }
                } else if (errorCode == WinError.ERROR_INSUFFICIENT_BUFFER) {
                    System.out.println("⚠️ Buffer too small, increasing size to " + minBytesNeeded.getValue() + " bytes.");
                    bufferSize = minBytesNeeded.getValue();
                    memory = new Memory(bufferSize);
                    continue;
                } else {
                    System.out.println("❌ Error reading event log: " + errorCode);
                    break;
                }
            }

            ByteBuffer byteBuffer = memory.getByteBuffer(0, bytesRead.getValue()).order(ByteOrder.LITTLE_ENDIAN);
            boolean logFound = false;
            while (byteBuffer.remaining() > 0) {
                int startPosition = byteBuffer.position();
                if (byteBuffer.remaining() < 4) break;
                int recordLength = byteBuffer.getInt();
                if (recordLength < 12 || byteBuffer.remaining() < recordLength - 4) break;

                byteBuffer.position(startPosition + 8);
                int eventID = byteBuffer.getInt() & 0xFFFF;
                long timeGenerated = Integer.toUnsignedLong(byteBuffer.getInt()) * 1000L;

                if (!realTime && timeGenerated < cutoffTimestamp) {
                    byteBuffer.position(startPosition + recordLength);
                    continue;
                }

                String message = extractEventMessage(byteBuffer, startPosition, recordLength);
                JSONObject logEntry = new JSONObject();
                logEntry.put("event_id", eventID);
                logEntry.put("log_name", logName);
                logEntry.put("timestamp", dateFormat.format(new Date(timeGenerated)));
                logEntry.put("message", message);

                if (collectVerboseLogs || !classifyLog(message).equals("Verbose")) {
                    System.out.println("🔍 Processed Log: " + logEntry.toString(4));
                    logFound = true;
                    lastLogTime = System.currentTimeMillis();
                } else {
                    System.out.println("🚫 Skipping verbose log: " + logEntry.toString(4));
                }
                byteBuffer.position(startPosition + recordLength);
            }

            if (!logFound) {
                System.out.println("ℹ️ No logs found for the specified time interval.");
                if (realTime) {
                    long currentTime = System.currentTimeMillis();
                    if ((currentTime - lastLogTime) / 1000 > TIMEOUT_SECONDS) {
                        System.out.println("⏳ Timeout reached, no new logs found. Exiting...");
                        break;
                    }
                }
            }

            if (!realTime) {
                break;
            }
        }

        Advapi32.INSTANCE.CloseEventLog(hEventLog);
    }

    private static String extractEventMessage(ByteBuffer byteBuffer, int start, int length) {
        int stringOffset = start + 56;
        if (stringOffset >= start + length) return "No message available";
        byteBuffer.position(stringOffset);
        StringBuilder message = new StringBuilder();
        while (byteBuffer.position() < start + length) {
            char c = byteBuffer.getChar();
            if (c == '\0') break;
            message.append(c);
        }
        return message.toString().trim();
    }

    private static String classifyLog(String message) {
        if (message.contains("critical") || message.contains("fatal")) {
            return "Critical";
        } else if (message.contains("error") || message.contains("failed")) {
            return "Error";
        } else if (message.contains("warning") || message.contains("alert")) {
            return "Warning";
        } else if (message.contains("info") || message.contains("started")) {
            return "Information";
        } else {
            return "Verbose";
        }
    }

    private static long getCutoffTimestamp(String interval) {
        long currentTime = System.currentTimeMillis();
        switch (interval) {
            case "30s":
                return currentTime - 30 * 1000;
            case "10m":
                return currentTime - 10 * 60 * 1000;
            case "30m":
                return currentTime - 30 * 60 * 1000;
            case "1h":
                return currentTime - 60 * 60 * 1000;
            case "2h":
                return currentTime - 2 * 60 * 60 * 1000;
            default:
                return 0;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Select the log name to collect (1: Security, 2: System, 3: Application): ");
        int logOption = Integer.parseInt(scanner.nextLine().trim());
        String logName = "";
        switch (logOption) {
            case 1:
                logName = "Security";
                break;
            case 2:
                logName = "System";
                break;
            case 3:
                logName = "Application";
                break;
            default:
                System.out.println("Invalid option. Exiting.");
                return;
        }

        System.out.print("Enable verbose logging? (true/false): ");
        collectVerboseLogs = Boolean.parseBoolean(scanner.nextLine().trim());

        System.out.print("Collect logs in real-time? (true/false): ");
        realTime = Boolean.parseBoolean(scanner.nextLine().trim());

        if (!realTime) {
            System.out.print("Enter the time interval for collecting logs (30s, 10m, 30m, 1h, 2h): ");
            String interval = scanner.nextLine().trim();
            cutoffTimestamp = getCutoffTimestamp(interval);
        } else {
            cutoffTimestamp = System.currentTimeMillis();
        }

        scanner.close();
        collectLogs(logName);
    }
}