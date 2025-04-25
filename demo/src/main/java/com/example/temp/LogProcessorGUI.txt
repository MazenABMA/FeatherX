
package com.example;

 import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LogProcessorGUI {
    private JFrame frame;
    private JTextArea logArea;
    private JButton sendLogButton;
    private JTextField logInput;

    public LogProcessorGUI() {
        frame = new JFrame("Log Processor GUI");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        logInput = new JTextField(40);
        sendLogButton = new JButton("Send Log");

        sendLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String log = logInput.getText();
                if (!log.isEmpty()) {
                    sendLogToProcessor(log);
                    logArea.append("📤 Sent log: " + log + "\n");
                    logInput.setText("");
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Log:"));
        panel.add(logInput);
        panel.add(sendLogButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void sendLogToProcessor(String log) {
        try {
            URL url = new URL("http://localhost:5001/process");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            os.write(log.getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            logArea.append("✅ Log sent! Response: " + responseCode + "\n");

            connection.disconnect();
        } catch (Exception ex) {
            logArea.append("❌ Error: " + ex.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LogProcessorGUI());
    }
}