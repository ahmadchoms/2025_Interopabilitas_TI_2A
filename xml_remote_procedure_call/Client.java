import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.net.MalformedURLException;
import java.util.Vector;
import org.apache.xmlrpc.XmlRpcClient;

public class Client extends JFrame {
    private XmlRpcClient xmlRpcClient;
    private JTextField xField, yField, resultField;
    private JComboBox<String> operationCombo;
    private JButton calculateButton, clearButton;

    // Modern color palette
    private final Color PRIMARY_BG = new Color(15, 23, 42); // Dark slate
    private final Color SECONDARY_BG = new Color(30, 41, 59); // Slate 700
    private final Color CARD_BG = new Color(51, 65, 85); // Slate 600
    private final Color ACCENT_BLUE = new Color(59, 130, 246); // Blue 500
    private final Color TEXT_PRIMARY = new Color(248, 250, 252); // Slate 50
    private final Color TEXT_SECONDARY = new Color(148, 163, 184); // Slate 400
    private final Color SUCCESS_GREEN = new Color(34, 197, 94); // Green 500
    private final Color ERROR_RED = new Color(239, 68, 68); // Red 500

    public Client() throws MalformedURLException {
        String serverUrl = "http://172.16.167.159:1717/RPC2";
        xmlRpcClient = new XmlRpcClient(serverUrl);
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Modern Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);

        // Create main panel with rounded corners
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create rounded rectangle background
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(PRIMARY_BG);
                g2d.fill(roundedRectangle);

                // Add subtle gradient overlay
                GradientPaint gradient = new GradientPaint(0, 0, new Color(59, 130, 246, 10),
                        getWidth(), getHeight(), new Color(139, 92, 246, 10));
                g2d.setPaint(gradient);
                g2d.fill(roundedRectangle);
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Create components
        JPanel headerPanel = createHeaderPanel();
        JPanel contentPanel = createContentPanel();
        JPanel buttonPanel = createButtonPanel();
        JPanel closePanel = createClosePanel();

        mainPanel.add(closePanel, BorderLayout.NORTH);
        mainPanel.add(headerPanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createClosePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JButton closeBtn = new JButton("×");
        closeBtn.setPreferredSize(new Dimension(30, 30));
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        closeBtn.setForeground(TEXT_SECONDARY);
        closeBtn.setBackground(null);
        closeBtn.setBorder(null);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> System.exit(0));

        closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeBtn.setForeground(ERROR_RED);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeBtn.setForeground(TEXT_SECONDARY);
            }
        });

        panel.add(closeBtn);
        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("Calculator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Simple & Modern", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(5, 0, 0, 0));

        panel.add(titleLabel);
        panel.add(subtitleLabel);
        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Operation selection
        JPanel opPanel = createInputPanel("Operation", null);
        String[] operations = { "Add (+)", "Subtract (-)", "Multiply (×)", "Divide (÷)" };
        operationCombo = new JComboBox<>(operations);
        styleComboBox(operationCombo);
        opPanel.add(Box.createVerticalStrut(8));
        opPanel.add(operationCombo);

        // Input fields
        JPanel xPanel = createInputPanel("First Number", null);
        xField = createStyledTextField();
        xPanel.add(Box.createVerticalStrut(8));
        xPanel.add(xField);

        JPanel yPanel = createInputPanel("Second Number", null);
        yField = createStyledTextField();
        yPanel.add(Box.createVerticalStrut(8));
        yPanel.add(yField);

        // Result field
        JPanel resultPanel = createInputPanel("Result", null);
        resultField = createStyledTextField();
        resultField.setEditable(false);
        resultField.setBackground(new Color(30, 41, 59));
        resultField.setFont(new Font("JetBrains Mono", Font.BOLD, 16));
        resultField.setForeground(SUCCESS_GREEN);
        resultPanel.add(Box.createVerticalStrut(8));
        resultPanel.add(resultField);

        panel.add(opPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(xPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(yPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(resultPanel);

        return panel;
    }

    private JPanel createInputPanel(String labelText, String placeholder) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Inter", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setOpaque(false);

        calculateButton = createModernButton("Calculate", ACCENT_BLUE, true);
        clearButton = createModernButton("Clear", SECONDARY_BG, false);

        calculateButton.addActionListener(new CalculateAction());
        clearButton.addActionListener(e -> clearFields());

        panel.add(calculateButton);
        panel.add(clearButton);

        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(350, 45));
        field.setMaximumSize(new Dimension(350, 45));
        field.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(CARD_BG);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(75, 85, 99), 1),
                new EmptyBorder(10, 15, 10, 15)));

        // Focus effects
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT_BLUE, 2),
                        new EmptyBorder(10, 15, 10, 15)));
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(75, 85, 99), 1),
                        new EmptyBorder(10, 15, 10, 15)));
            }
        });

        return field;
    }

    private JButton createModernButton(String text, Color color, boolean isPrimary) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create rounded rectangle
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(getBackground());
                g2d.fill(roundedRectangle);

                // Paint text
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }
        };

        button.setPreferredSize(new Dimension(140, 45));
        button.setFont(new Font("Inter", Font.BOLD, 14));
        button.setForeground(isPrimary ? Color.WHITE : TEXT_PRIMARY);
        button.setBackground(color);
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);

        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = color;

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(isPrimary ? color.brighter() : CARD_BG);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setPreferredSize(new Dimension(350, 45));
        combo.setMaximumSize(new Dimension(350, 45));
        combo.setFont(new Font("Inter", Font.PLAIN, 14));
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(CARD_BG);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(75, 85, 99), 1),
                new EmptyBorder(5, 15, 5, 15)));

        // Style the dropdown arrow
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("⌄");
                button.setForeground(TEXT_SECONDARY);
                button.setBackground(CARD_BG);
                button.setBorder(null);
                return button;
            }
        });
    }

    private void clearFields() {
        xField.setText("");
        yField.setText("");
        resultField.setText("");
        resultField.setForeground(SUCCESS_GREEN);
        operationCombo.setSelectedIndex(0);
        xField.requestFocus();
    }

    private class CalculateAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String xText = xField.getText().trim();
                String yText = yField.getText().trim();

                if (xText.isEmpty() || yText.isEmpty()) {
                    showErrorDialog("Please enter both values!");
                    return;
                }

                int x = Integer.parseInt(xText);
                int y = Integer.parseInt(yText);

                Vector<Integer> vector = new Vector<>();
                vector.addElement(x);
                vector.addElement(y);

                String selectedOperation = (String) operationCombo.getSelectedItem();
                String method = getMethodName(selectedOperation);

                if (method.equals("server.hitungPembagian") && y == 0) {
                    showErrorDialog("Division by zero is not allowed!");
                    return;
                }

                // Show loading state
                calculateButton.setText("Loading...");
                calculateButton.setEnabled(false);

                SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
                    @Override
                    protected Integer doInBackground() throws Exception {
                        return (Integer) xmlRpcClient.execute(method, vector);
                    }

                    @Override
                    protected void done() {
                        try {
                            int result = get();
                            resultField.setText(String.valueOf(result));
                            resultField.setForeground(SUCCESS_GREEN);
                        } catch (Exception ex) {
                            showErrorDialog("Error: " + ex.getMessage());
                            resultField.setText("Error");
                            resultField.setForeground(ERROR_RED);
                        } finally {
                            calculateButton.setText("Calculate");
                            calculateButton.setEnabled(true);
                        }
                    }
                };
                worker.execute();

            } catch (NumberFormatException ex) {
                showErrorDialog("Please enter valid numbers!");
                resultField.setText("Invalid Input");
                resultField.setForeground(ERROR_RED);
            }
        }
    }

    private String getMethodName(String operation) {
        switch (operation) {
            case "Add (+)":
                return "server.hitungPenjumlahan";
            case "Subtract (-)":
                return "server.hitungPengurangan";
            case "Multiply (×)":
                return "server.hitungPerkalian";
            case "Divide (÷)":
                return "server.hitungPembagian";
            default:
                return "server.hitungPenjumlahan";
        }
    }

    private void showErrorDialog(String message) {
        // Custom error dialog
        JDialog dialog = new JDialog(this, "Error", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(CARD_BG);
                g2d.fill(roundedRectangle);
                g2d.setColor(ERROR_RED);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(roundedRectangle);
                g2d.dispose();
            }
        };

        JLabel msgLabel = new JLabel(message, SwingConstants.CENTER);
        msgLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        msgLabel.setForeground(TEXT_PRIMARY);
        msgLabel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton okButton = createModernButton("OK", ERROR_RED, true);
        okButton.setPreferredSize(new Dimension(80, 35));
        okButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        panel.add(msgLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new Client().setVisible(true);
            } catch (MalformedURLException e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        });
    }
}