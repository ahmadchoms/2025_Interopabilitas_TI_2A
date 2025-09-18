import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class RPCClientGUI extends JFrame {
    private JTextField ipField;
    private JTextField portField;
    private JTextField num1Field;
    private JTextField num2Field;
    private JTextArea responseArea;
    private JButton connectButton;
    private JButton[] operationButtons;
    private Socket rpcClient;
    private PrintStream printStream;
    private boolean isConnected = false;

    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color SECONDARY_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color DARK_GRAY = new Color(52, 73, 94);
    private final Color LIGHT_GRAY = new Color(236, 240, 241);
    private final Color WHITE = new Color(245, 245, 245); // Warna input lebih lembut

    public RPCClientGUI() {
        setTitle("RPC Calculator Client - Premium Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 700);
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
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillRoundRect(4, 4, 24, 24, 8, 8);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "C";
        int x = (32 - fm.stringWidth(text)) / 2;
        int y = (32 + fm.getAscent()) / 2;
        g2d.drawString(text, x, y);
        g2d.dispose();
        return icon;
    }

    private void initializeComponents() {
        ipField = createStyledTextField("127.0.0.1");
        portField = createStyledTextField("3000");

        num1Field = createStyledTextField("");
        num2Field = createStyledTextField("");

        responseArea = new JTextArea(15, 50);
        responseArea.setEditable(false);
        responseArea.setBackground(LIGHT_GRAY);
        responseArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        responseArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        connectButton = createStyledButton("Connect to Server", PRIMARY_COLOR);

        String[] operations = { "Add (+)", "Subtract (-)", "Multiply (×)", "Divide (÷)", "Modulo (%)" };
        String[] operationCodes = { "add", "sub", "mul", "div", "mod" };
        operationButtons = new JButton[operations.length];

        for (int i = 0; i < operations.length; i++) {
            operationButtons[i] = createStyledButton(operations[i], SECONDARY_COLOR);
            operationButtons[i].setActionCommand(operationCodes[i]);
            operationButtons[i].setEnabled(false);
        }
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(149, 165, 166), 1), // Border lebih lembut
                new EmptyBorder(8, 12, 8, 12)));
        field.setBackground(WHITE);
        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = color;

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker().darker()); // Efek hover lebih kontras
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

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(DARK_GRAY);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        JLabel titleLabel = new JLabel("RPC Calculator Client");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        JPanel connectionPanel = new JPanel(new GridBagLayout());
        connectionPanel.setBackground(Color.WHITE);
        connectionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                "Server Connection",
                0, 0, new Font("Segoe UI", Font.BOLD, 16), PRIMARY_COLOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        connectionPanel.add(createStyledLabel("Server IP:"), gbc);
        gbc.gridx = 1;
        connectionPanel.add(ipField, gbc);

        gbc.gridx = 2;
        connectionPanel.add(createStyledLabel("Port:"), gbc);
        gbc.gridx = 3;
        connectionPanel.add(portField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        connectionPanel.add(connectButton, gbc);

        JPanel calculatorPanel = new JPanel(new GridBagLayout());
        calculatorPanel.setBackground(Color.WHITE);
        calculatorPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                "Calculator Operations",
                0, 0, new Font("Segoe UI", Font.BOLD, 16), SECONDARY_COLOR));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        gbc.gridx = 0;
        gbc.gridy = 0;
        calculatorPanel.add(createStyledLabel("First Number:"), gbc);
        gbc.gridx = 1;
        num1Field.setPreferredSize(new Dimension(100, 30));
        calculatorPanel.add(num1Field, gbc);

        gbc.gridx = 2;
        calculatorPanel.add(createStyledLabel("Second Number:"), gbc);
        gbc.gridx = 3;
        num2Field.setPreferredSize(new Dimension(100, 30));
        calculatorPanel.add(num2Field, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        for (int i = 0; i < operationButtons.length; i++) {
            gbc.gridx = i;
            calculatorPanel.add(operationButtons[i], gbc);
        }

        JPanel responsePanel = new JPanel(new BorderLayout());
        responsePanel.setBackground(Color.WHITE);
        responsePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(DARK_GRAY, 2),
                "Server Responses",
                0, 0, new Font("Segoe UI", Font.BOLD, 16), DARK_GRAY));

        JScrollPane scrollPane = new JScrollPane(responseArea);
        scrollPane.setPreferredSize(new Dimension(750, 200));
        responsePanel.add(scrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_GRAY);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(connectionPanel, BorderLayout.NORTH);
        topPanel.add(calculatorPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(responsePanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(DARK_GRAY);
        JLabel statusLabel = new JLabel("Disconnected");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void addEventListeners() {
        connectButton.addActionListener(new ConnectActionListener());

        for (JButton button : operationButtons) {
            button.addActionListener(new OperationActionListener());
        }
    }

    private class ConnectActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isConnected) {
                connectToServer();
            } else {
                disconnectFromServer();
            }
        }
    }

    private class OperationActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isConnected) {
                showError("Not connected to server!");
                return;
            }

            try {
                int num1 = Integer.parseInt(num1Field.getText().trim());
                int num2 = Integer.parseInt(num2Field.getText().trim());
                String operation = e.getActionCommand();

                String message = operation + ":" + num1 + ":" + num2;
                printStream.println(message);

                responseArea.append("Sent: " + message + "\n");
                responseArea.setCaretPosition(responseArea.getDocument().getLength());

            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers!");
            }
        }
    }

    private void connectToServer() {
        try {
            String ip = ipField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());

            rpcClient = new Socket(ip, port);
            printStream = new PrintStream(rpcClient.getOutputStream(), true);

            new Thread(new ResponseListener()).start();

            isConnected = true;
            connectButton.setText("Disconnect");
            connectButton.setBackground(DANGER_COLOR);

            for (JButton button : operationButtons) {
                button.setEnabled(true);
            }

            ipField.setEnabled(false);
            portField.setEnabled(false);

            responseArea.append("Connected to server " + ip + ":" + port + "\n");
            responseArea.append("Ready to perform calculations!\n\n");
            responseArea.setCaretPosition(responseArea.getDocument().getLength());

            JPanel statusBar = (JPanel) ((BorderLayout) getContentPane().getLayout())
                    .getLayoutComponent(BorderLayout.SOUTH);
            JLabel statusLabel = (JLabel) statusBar.getComponent(0);
            statusLabel.setText("Connected to " + ip + ":" + port);

            showSuccess("Connected to server successfully!");

        } catch (Exception ex) {
            showError("Failed to connect: " + ex.getMessage());
        }
    }

    private void disconnectFromServer() {
        try {
            if (rpcClient != null && !rpcClient.isClosed()) {
                rpcClient.close();
            }

            isConnected = false;
            connectButton.setText("Connect to Server");
            connectButton.setBackground(PRIMARY_COLOR);

            for (JButton button : operationButtons) {
                button.setEnabled(false);
            }

            ipField.setEnabled(true);
            portField.setEnabled(true);

            responseArea.append("Disconnected from server\n\n");
            responseArea.setCaretPosition(responseArea.getDocument().getLength());

            JPanel statusBar = (JPanel) ((BorderLayout) getContentPane().getLayout())
                    .getLayoutComponent(BorderLayout.SOUTH);
            JLabel statusLabel = (JLabel) statusBar.getComponent(0);
            statusLabel.setText("Disconnected");

        } catch (IOException ex) {
            showError("Error during disconnection: " + ex.getMessage());
        }
    }

    private class ResponseListener implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(rpcClient.getInputStream()));
                String response;

                while ((response = reader.readLine()) != null && isConnected) {
                    final String finalResponse = response;
                    SwingUtilities.invokeLater(() -> {
                        responseArea.append("Server: " + finalResponse + "\n");
                        responseArea.setCaretPosition(responseArea.getDocument().getLength());
                    });
                }
            } catch (IOException ex) {
                if (isConnected) {
                    SwingUtilities.invokeLater(() -> {
                        showError("Connection lost!");
                        disconnectFromServer();
                    });
                }
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RPCClientGUI();
        });
    }
}