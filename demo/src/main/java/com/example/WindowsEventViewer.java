
package com.example;

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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the log name to collect (e.g., Security, System, Application): ");
        String logName = scanner.nextLine().trim();

        System.out.print("Enter how many days ago (0 for today, 1 for yesterday, etc.): ");
        int daysAgo = Integer.parseInt(scanner.nextLine().trim());

        scanner.close();
        collectWindowsEventLogs(logName, daysAgo);
    }

    public static void collectWindowsEventLogs(String logName, int daysAgo) {
        long cutoffTimestamp = getTimestampStartOfDay(daysAgo);
        WinNT.HANDLE hEventLog = Advapi32.INSTANCE.OpenEventLog(null, logName);
        if (hEventLog == null) {
            System.out.println("❌ Failed to open event log: " + logName);
            return;
        }

        int bufferSize = 4096;
        Memory memory = new Memory(bufferSize);
        IntByReference bytesRead = new IntByReference();
        IntByReference minBytesNeeded = new IntByReference();

        while (Advapi32.INSTANCE.ReadEventLog(hEventLog,
                WinNT.EVENTLOG_SEQUENTIAL_READ | WinNT.EVENTLOG_FORWARDS_READ,
                0, memory, (int) memory.size(), bytesRead, minBytesNeeded)) {
            
            ByteBuffer byteBuffer = memory.getByteBuffer(0, bytesRead.getValue()).order(ByteOrder.LITTLE_ENDIAN);
            
            while (byteBuffer.remaining() > 0) {
                int startPosition = byteBuffer.position();
                if (byteBuffer.remaining() < 4) break;
                int recordLength = byteBuffer.getInt();
                if (recordLength < 12 || byteBuffer.remaining() < recordLength - 4) break;

                byteBuffer.position(startPosition + 8);
                int eventID = byteBuffer.getInt() & 0xFFFF;
                long timeGenerated = Integer.toUnsignedLong(byteBuffer.getInt()) * 1000L;

                if (timeGenerated < cutoffTimestamp) {
                    byteBuffer.position(startPosition + recordLength);
                    continue;
                }

                String message = extractEventMessage(byteBuffer, startPosition, recordLength);
                JSONObject logEntry = new JSONObject();
                logEntry.put("event_id", eventID);
                logEntry.put("log_name", logName);
                logEntry.put("timestamp", dateFormat.format(new Date(timeGenerated)));
                logEntry.put("message", message);

                LogProcessor.processLog(logEntry);
                byteBuffer.position(startPosition + recordLength);
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

    private static long getTimestampStartOfDay(int daysAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}

class LogProcessor {
    public static void processLog(JSONObject logEntry) {
        String message = logEntry.getString("message");
        String severity = classifyLog(message);
        logEntry.put("severity", severity);
        System.out.println("🔍 Processed Log: " + logEntry.toString(4));
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
}
