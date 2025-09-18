import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class RPCServerGUI extends JFrame {
    private JTextField portField;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea logArea;
    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel clientCountLabel;
    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private AtomicInteger clientCounter = new AtomicInteger(0);

    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DARK_GRAY = new Color(52, 73, 94);
    private final Color LIGHT_GRAY = new Color(236, 240, 241);
    private final Color WHITE = Color.WHITE;

    public RPCServerGUI() {
        setTitle("RPC Calculator Server - Professional Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeComponents();
        layoutComponents();
        addEventListeners();

        setIconImage(createIcon());

        setVisible(true);
    }

    private Image createIcon() {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(SUCCESS_COLOR);
        g2d.fillRoundRect(4, 4, 24, 24, 8, 8);
        g2d.setColor(WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "S";
        int x = (32 - fm.stringWidth(text)) / 2;
        int y = (32 + fm.getAscent()) / 2;
        g2d.drawString(text, x, y);
        g2d.dispose();
        return icon;
    }

    private void initializeComponents() {
        portField = createStyledTextField("3000");

        startButton = createStyledButton("Start Server", DANGER_COLOR);
        stopButton = createStyledButton("Stop Server", DANGER_COLOR);
        stopButton.setEnabled(false);

        logArea = new JTextArea(20, 50);
        logArea.setEditable(false);
        logArea.setBackground(DARK_GRAY);
        logArea.setForeground(WHITE);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columnNames = { "Client ID", "IP Address", "Port", "Connected Time", "Status" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        clientTable = new JTable(tableModel);
        clientTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clientTable.setRowHeight(25);
        clientTable.setGridColor(LIGHT_GRAY);
        clientTable.getTableHeader().setBackground(PRIMARY_COLOR);
        clientTable.getTableHeader().setForeground(PRIMARY_COLOR);
        clientTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        statusLabel = new JLabel("Server Stopped");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(DANGER_COLOR);

        clientCountLabel = new JLabel("Connected Clients: 0");
        clientCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clientCountLabel.setForeground(DARK_GRAY);
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DARK_GRAY, 1),
                new EmptyBorder(8, 12, 8, 12)));
        field.setBackground(WHITE);
        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(DARK_GRAY);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = color;

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(DARK_GRAY);
        return label;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DARK_GRAY);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("RPC Calculator Server Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(WHITE);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(DARK_GRAY);
        statusPanel.add(clientCountLabel);
        statusPanel.add(Box.createHorizontalStrut(20));
        statusPanel.add(statusLabel);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusPanel, BorderLayout.EAST);

        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(WHITE);
        controlPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                "Server Controls",
                0, 0, new Font("Segoe UI", Font.BOLD, 16), PRIMARY_COLOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(createStyledLabel("Port:"), gbc);

        gbc.gridx = 1;
        controlPanel.add(portField, gbc);

        gbc.gridx = 2;
        controlPanel.add(startButton, gbc);

        gbc.gridx = 3;
        controlPanel.add(stopButton, gbc);

        JPanel clientPanel = new JPanel(new BorderLayout());
        clientPanel.setBackground(WHITE);
        clientPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(WARNING_COLOR, 2),
                "Connected Clients",
                0, 0, new Font("Segoe UI", Font.BOLD, 16), WARNING_COLOR));

        JScrollPane clientScrollPane = new JScrollPane(clientTable);
        clientScrollPane.setPreferredSize(new Dimension(950, 200));
        clientPanel.add(clientScrollPane, BorderLayout.CENTER);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(WHITE);
        logPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DARK_GRAY, 2),
                "Server Logs",
                0, 0, new Font("Segoe UI", Font.BOLD, 16), DARK_GRAY));

        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(950, 300));
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(clientPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(logPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(LIGHT_GRAY);
        JLabel footerLabel = new JLabel("RPC Calculator Server v2.0 - Professional Edition");
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        footerLabel.setForeground(DARK_GRAY);
        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void addEventListeners() {
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
    }

    private void startServer() {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            serverSocket = new ServerSocket(port);
            isRunning = true;

            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            portField.setEnabled(false);
            statusLabel.setText("Server Running");
            statusLabel.setForeground(SUCCESS_COLOR);

            String localIP = InetAddress.getLocalHost().getHostAddress();
            appendLog("Server started on " + localIP + ":" + port);
            appendLog("Waiting for client connections...");

            new Thread(this::acceptClients).start();

        } catch (Exception ex) {
            showError("Failed to start server: " + ex.getMessage());
        }
    }

    private void stopServer() {
        try {
            isRunning = false;

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            portField.setEnabled(true);
            statusLabel.setText("Server Stopped");
            statusLabel.setForeground(DANGER_COLOR);

            tableModel.setRowCount(0);
            clientCounter.set(0);
            updateClientCount();

            appendLog("Server stopped");

        } catch (IOException ex) {
            showError("Error stopping server: " + ex.getMessage());
        }
    }

    private void acceptClients() {
        while (!serverSocket.isClosed() && isRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                int clientId = clientCounter.incrementAndGet();

                String clientAddress = clientSocket.getRemoteSocketAddress().toString();
                String clientIP = clientSocket.getInetAddress().getHostAddress();
                int clientPort = clientSocket.getPort();
                String connectedTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

                SwingUtilities.invokeLater(() -> {
                    tableModel.addRow(new Object[] {
                            "Client-" + clientId,
                            clientIP,
                            clientPort,
                            connectedTime,
                            "Connected"
                    });
                    updateClientCount();
                });

                appendLog("New client connected: " + clientAddress + " (ID: Client-" + clientId + ")");

                new Thread(() -> handleClient(clientSocket, clientId)).start();

            } catch (IOException ex) {
                if (isRunning) {
                    appendLog("Error accepting client: " + ex.getMessage());
                }
            }
        }
    }

    private void handleClient(Socket clientSocket, int clientId) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintStream printStream = new PrintStream(clientSocket.getOutputStream(), true)) {

            String line;
            while ((line = reader.readLine()) != null && isRunning) {
                appendLog("Client-" + clientId + " request: " + line);

                String[] commands = line.split(":", 3);

                if (commands.length < 3) {
                    String errorMsg = "Error: Invalid command format. Use 'operation:operand1:operand2'";
                    printStream.println(errorMsg);
                    appendLog("Sent to Client-" + clientId + ": " + errorMsg);
                    continue;
                }

                String operation = commands[0];
                int operand1, operand2;

                try {
                    operand1 = Integer.parseInt(commands[1]);
                    operand2 = Integer.parseInt(commands[2]);
                } catch (NumberFormatException e) {
                    String errorMsg = "Error: Operands must be valid integers.";
                    printStream.println(errorMsg);
                    appendLog("Sent to Client-" + clientId + ": " + errorMsg);
                    continue;
                }

                String message = processOperation(operation, operand1, operand2);
                printStream.println(message);
                appendLog("Sent to Client-" + clientId + ": " + message);
            }

        } catch (IOException ex) {
            appendLog("Client-" + clientId + " disconnected");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                appendLog("Error closing client socket: " + e.getMessage());
            }

            SwingUtilities.invokeLater(() -> {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (("Client-" + clientId).equals(tableModel.getValueAt(i, 0))) {
                        tableModel.setValueAt("Disconnected", i, 4);
                        break;
                    }
                }
                updateClientCount();
            });
        }
    }

    private String processOperation(String operation, int operand1, int operand2) {
        String message;
        int result;

        switch (operation) {
            case "add":
                result = (operand1 + operand2);
                message = String.format("%d + %d = %d", operand1, operand2, result);
                break;
            case "sub":
                result = (operand1 - operand2);
                message = String.format("%d - %d = %d", operand1, operand2, result);
                break;
            case "mul":
                result = (operand1 * operand2);
                message = String.format("%d × %d = %d", operand1, operand2, result);
                break;
            case "div":
                if (operand2 == 0) {
                    message = "Error: Division by zero is not allowed.";
                } else {
                    result = (operand1 / operand2);
                    message = String.format("%d ÷ %d = %d", operand1, operand2, result);
                }
                break;
            case "mod":
                if (operand2 == 0) {
                    message = "Error: Modulo by zero is not allowed.";
                } else {
                    result = (operand1 % operand2);
                    message = String.format("%d %% %d = %d", operand1, operand2, result);
                }
                break;
            default:
                message = "Error: Unknown operation '" + operation + "'. Supported operations: add, sub, mul, div, mod";
                break;
        }

        return message;
    }

    private void updateClientCount() {
        int connectedCount = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ("Connected".equals(tableModel.getValueAt(i, 4))) {
                connectedCount++;
            }
        }
        clientCountLabel.setText("Connected Clients: " + connectedCount);
        clientCountLabel.setForeground(connectedCount > 0 ? SUCCESS_COLOR : DARK_GRAY);
    }

    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new RPCServerGUI();
        });
    }
}