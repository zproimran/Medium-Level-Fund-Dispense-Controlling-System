package smarthrms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class TraditionalLogin extends JFrame implements ActionListener {
    JButton login, cancel, showPasswordButton;
    JTextField uNamefield;
    JPasswordField pField;
    JLabel label, cardLabel, pinLabel, titleLabel;
    JCheckBox rememberMe;

    public TraditionalLogin() {
        setUndecorated(true);
        setLayout(null);
        
        ImageIcon appIcon = new ImageIcon(ClassLoader.getSystemResource("icons/appIcon.png"));
        setIconImage(appIcon.getImage());
        getContentPane().setBackground(Color.WHITE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        
        // Create rounded corners
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        initializeComponents();
        setVisible(true);
    }

    private void initializeComponents() {
        // Main panel with background
        label = new JLabel();
        label.setBounds(0, 0, 500, 500);
        label.setLayout(null);
        add(label);

        // Background with gradient effect
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(70, 130, 180);
                Color color2 = new Color(176, 224, 230);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setBounds(0, 0, 500, 500);
        backgroundPanel.setLayout(null);
        label.add(backgroundPanel);

        // Title
        titleLabel = new JLabel("TRADITIONAL LOGIN");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 50, 500, 40);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 24));
        backgroundPanel.add(titleLabel);

        // No device required message
        JLabel noDeviceLabel = new JLabel("No fingerprint device required", SwingConstants.CENTER);
        noDeviceLabel.setForeground(Color.WHITE);
        noDeviceLabel.setBounds(0, 90, 500, 20);
        noDeviceLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        backgroundPanel.add(noDeviceLabel);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setBounds(50, 120, 400, 300);
        formPanel.setBackground(new Color(255, 255, 255, 200));
        formPanel.setLayout(null);
        formPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        backgroundPanel.add(formPanel);

        // Username label and field
        cardLabel = new JLabel("Username:");
        cardLabel.setForeground(Color.DARK_GRAY);
        cardLabel.setBounds(50, 50, 100, 30);
        cardLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(cardLabel);

        uNamefield = new JTextField();
        uNamefield.setFont(new Font("Arial", Font.PLAIN, 16));
        uNamefield.setBounds(50, 80, 300, 40);
        uNamefield.setBackground(Color.WHITE);
        uNamefield.setForeground(Color.DARK_GRAY);
        uNamefield.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(uNamefield);

        // Password label and field
        pinLabel = new JLabel("Password:");
        pinLabel.setForeground(Color.DARK_GRAY);
        pinLabel.setBounds(50, 140, 100, 30);
        pinLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(pinLabel);

        pField = new JPasswordField();
        pField.setFont(new Font("Arial", Font.PLAIN, 16));
        pField.setBounds(50, 170, 300, 40);
        pField.setBackground(Color.WHITE);
        pField.setForeground(Color.DARK_GRAY);
        pField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(pField);

        // Show password button
        showPasswordButton = new JButton("👁");
        showPasswordButton.setBounds(355, 170, 45, 40);
        showPasswordButton.setBackground(new Color(240, 240, 240));
        showPasswordButton.setForeground(Color.DARK_GRAY);
        showPasswordButton.setFocusPainted(false);
        showPasswordButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        showPasswordButton.addActionListener(this);
        formPanel.add(showPasswordButton);

        // Remember me checkbox
        rememberMe = new JCheckBox("Remember me");
        rememberMe.setBounds(50, 220, 150, 25);
        rememberMe.setBackground(new Color(255, 255, 255, 0));
        rememberMe.setForeground(Color.DARK_GRAY);
        rememberMe.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(rememberMe);

        // Login button
        login = new JButton("LOGIN");
        login.setBounds(50, 260, 140, 40);
        login.setBackground(new Color(46, 204, 113));
        login.setForeground(Color.WHITE);
        login.setFont(new Font("Arial", Font.BOLD, 14));
        login.setFocusPainted(false);
        login.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        login.addActionListener(this);
        login.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(login);

        // Cancel button
        cancel = new JButton("CANCEL");
        cancel.setBounds(210, 260, 140, 40);
        cancel.setBackground(new Color(231, 76, 60));
        cancel.setForeground(Color.WHITE);
        cancel.setFont(new Font("Arial", Font.BOLD, 14));
        cancel.setFocusPainted(false);
        cancel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cancel.addActionListener(this);
        cancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(cancel);

        // Back to launcher button
        JButton backButton = new JButton("← Back to Main Menu");
        backButton.setBounds(150, 320, 200, 30);
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        backButton.addActionListener(e -> {
            dispose();
            new LoginLauncher().setVisible(true);
        });
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(backButton);

        // Set login as default button
        getRootPane().setDefaultButton(login);

        // Add document listener to enable/disable login button
        javax.swing.event.DocumentListener documentListener = new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                checkFields();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                checkFields();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                checkFields();
            }

            private void checkFields() {
                String username = uNamefield.getText().trim();
                String password = new String(pField.getPassword()).trim();
                login.setEnabled(!username.isEmpty() && !password.isEmpty());
            }
        };

        uNamefield.getDocument().addDocumentListener(documentListener);
        pField.getDocument().addDocumentListener(documentListener);

        // Initially disable login button
        login.setEnabled(false);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == login) {
            loginUser();
        } else if (ae.getSource() == cancel) {
            dispose();
            new LoginLauncher().setVisible(true);
        } else if (ae.getSource() == showPasswordButton) {
            togglePasswordVisibility();
        }
    }

    private void loginUser() {
        String username = uNamefield.getText().trim();
        String password = new String(pField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show loading
        login.setEnabled(false);
        login.setText("AUTHENTICATING...");

        new Thread(() -> {
            try {
                Connecting connects = new Connecting();
                String sql = "SELECT * FROM members WHERE username=? AND status='Active'";
                java.sql.PreparedStatement pst = connects.con.prepareStatement(sql);
                pst.setString(1, username.toLowerCase());
                java.sql.ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    String role = rs.getString("role");
                    String fullName = rs.getString("full_name");

                    // Verify password using BCrypt
                    if (PasswordUtils.checkPassword(password, storedHash)) {
                        if ("Admin".equals(role) || "Doctor".equals(role) || "Midwifer".equals(role)) {
                            SwingUtilities.invokeLater(() -> {
                                dispose();
                                JOptionPane.showMessageDialog(this, 
                                    "Login successful!\nWelcome " + fullName + " (" + role + ")", 
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                                
                                // Launch main application
                                launchMainApplication(username);
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this, "Access denied. Invalid role.", 
                                    "Authorization Error", JOptionPane.ERROR_MESSAGE);
                                resetLoginButton();
                            });
                        }
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "Invalid username or password.", 
                                "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                            resetLoginButton();
                        });
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Invalid username or password.", 
                            "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                        resetLoginButton();
                    });
                }

                rs.close();
                pst.close();
                connects.con.close();

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), 
                        "System Error", JOptionPane.ERROR_MESSAGE);
                    resetLoginButton();
                });
            }
        }).start();
    }

    private void resetLoginButton() {
        login.setEnabled(true);
        login.setText("LOGIN");
    }

    private void togglePasswordVisibility() {
        if (pField.getEchoChar() == '*') {
            pField.setEchoChar((char) 0);
            showPasswordButton.setText("🙈");
        } else {
            pField.setEchoChar('*');
            showPasswordButton.setText("👁");
        }
    }

   private void launchMainApplication(String username) {
    // Close all login windows first
    dispose();
    
    // Close any other open login windows
    java.awt.Window[] windows = java.awt.Window.getWindows();
    for (java.awt.Window window : windows) {
        if (window instanceof javax.swing.JFrame) {
            window.dispose();
        }
    }
    
    // Launch SmartHRMS using the proper method
    SmartHRMS.launchFromLogin(username);
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TraditionalLogin().setVisible(true));
    }
}