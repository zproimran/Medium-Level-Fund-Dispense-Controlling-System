package smarthrms;

import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginLauncher extends JFrame implements ActionListener {
    private JButton fingerprintBtn, traditionalBtn, exitBtn, deviceStatusBtn;
    private JLabel titleLabel, deviceStatusLabel;
    private FingerprintUtil fingerprintUtil;

    public LoginLauncher() {
        fingerprintUtil = new FingerprintUtil();
        initializeUI();
        startDeviceStatusChecker();
    }

    private void initializeUI() {
        setTitle("HR Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titleLabel = new JLabel("HR MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(6, 1, 15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Welcome label
        JLabel welcomeLabel = new JLabel("Choose Login Method", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.DARK_GRAY);
        mainPanel.add(welcomeLabel);

        // Device Status
        JPanel statusPanel = new JPanel(new FlowLayout());
        statusPanel.setBackground(new Color(240, 240, 240));
        deviceStatusLabel = new JLabel("🔴 Checking device status...");
        deviceStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        deviceStatusLabel.setForeground(Color.RED);
        
        deviceStatusBtn = new JButton("Refresh Status");
        deviceStatusBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        deviceStatusBtn.setBackground(new Color(149, 165, 166));
        deviceStatusBtn.setForeground(Color.WHITE);
        deviceStatusBtn.setFocusPainted(false);
        deviceStatusBtn.addActionListener(e -> checkDeviceStatus());
        
        statusPanel.add(deviceStatusLabel);
        statusPanel.add(deviceStatusBtn);
        mainPanel.add(statusPanel);

        // Traditional Login Button
        traditionalBtn = new JButton("👤 Traditional Login (Username/Password)");
        styleButton(traditionalBtn, new Color(52, 152, 219));
        mainPanel.add(traditionalBtn);

        // Fingerprint Login Button
        fingerprintBtn = new JButton("🔒 Fingerprint Login");
        styleButton(fingerprintBtn, new Color(46, 204, 113));
        mainPanel.add(fingerprintBtn);

//        // Fingerprint Enrollment Button
//        JButton enrollmentBtn = new JButton("📝 Fingerprint Enrollment");
//        styleButton(enrollmentBtn, new Color(155, 89, 182));
//        enrollmentBtn.addActionListener(e -> openFingerprintEnrollment());
//        mainPanel.add(enrollmentBtn);

        // Exit Button
        exitBtn = new JButton("❌ Exit System");
        styleButton(exitBtn, new Color(231, 76, 60));
        mainPanel.add(exitBtn);

        add(mainPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("Secure Access System - v2.0 | Device Status: Real-time Monitoring", JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(footerLabel, BorderLayout.SOUTH);

        // Add action listeners
        fingerprintBtn.addActionListener(this);
        traditionalBtn.addActionListener(this);
        exitBtn.addActionListener(this);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }

    private void startDeviceStatusChecker() {
        // Check device status every 3 seconds
        Timer timer = new Timer(3000, e -> checkDeviceStatus());
        timer.start();
    }

    private void checkDeviceStatus() {
        new Thread(() -> {
            try {
                boolean deviceAvailable = checkFingerprintDeviceAvailable();
                
                SwingUtilities.invokeLater(() -> {
                    if (deviceAvailable) {
                        deviceStatusLabel.setText("🟢 Fingerprint Device: Connected & Ready");
                        deviceStatusLabel.setForeground(new Color(39, 174, 96));
                        fingerprintBtn.setEnabled(true);
                    } else {
                        deviceStatusLabel.setText("🔴 Fingerprint Device: Not Connected");
                        deviceStatusLabel.setForeground(Color.RED);
                        fingerprintBtn.setEnabled(true); // Still allow but show warning
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    deviceStatusLabel.setText("⚠️ Device Status: Check Failed");
                    deviceStatusLabel.setForeground(Color.ORANGE);
                });
            }
        }).start();
    }

    private boolean checkFingerprintDeviceAvailable() {
        try {
            // Try to initialize ZKTeco library
            if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init()) {
                return false;
            }
            
            // Check for connected devices
            int deviceCount = FingerprintSensorEx.GetDeviceCount();
            if (deviceCount <= 0) {
                FingerprintSensorEx.Terminate();
                return false;
            }
            
            // Try to open device
            long deviceHandle = FingerprintSensorEx.OpenDevice(0);
            if (deviceHandle == 0) {
                FingerprintSensorEx.Terminate();
                return false;
            }
            
            // Close device and terminate
            FingerprintSensorEx.CloseDevice(deviceHandle);
            FingerprintSensorEx.Terminate();
            return true;
            
        } catch (Exception e) {
            System.out.println("Device check error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == fingerprintBtn) {
            openFingerprintLogin();
        } else if (e.getSource() == traditionalBtn) {
            openTraditionalLogin();
        } else if (e.getSource() == exitBtn) {
            fingerprintUtil.close();
            System.exit(0);
        }
    }

    private void openTraditionalLogin() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            new TraditionalLogin().setVisible(true);
        });
    }

    private void openFingerprintLogin() {
        // Check device status before opening fingerprint login
        if (!checkFingerprintDeviceAvailable()) {
            int choice = JOptionPane.showConfirmDialog(this,
                "Fingerprint device is not detected or not ready.\n\n" +
                "Please ensure:\n" +
                "• Fingerprint scanner is connected via USB\n" +
                "• Drivers are properly installed\n" +
                "• Device is powered on\n\n" +
                "Do you want to continue anyway?",
                "Device Not Detected",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        dispose();
        SwingUtilities.invokeLater(() -> {
            new FingerprintLogin().setVisible(true);
        });
    }

    private void openFingerprintEnrollment() {
        // Check device status before opening enrollment
        if (!checkFingerprintDeviceAvailable()) {
            JOptionPane.showMessageDialog(this,
                "Cannot open fingerprint enrollment.\n\n" +
                "Fingerprint device is not detected.\n" +
                "Please connect your fingerprint scanner and try again.",
                "Device Required",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        dispose();
        SwingUtilities.invokeLater(() -> {
            FingerprintEnrollment.main(new String[]{});
        });
    }

    public static void main(String[] args) {
//        // Set system look and feel
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
//        } catch (Exception e) {
//            // Use default look and feel
//        }

        SwingUtilities.invokeLater(() -> {
            LoginLauncher loginLauncher = new LoginLauncher();
            loginLauncher.setVisible(true);
        });
    }
}