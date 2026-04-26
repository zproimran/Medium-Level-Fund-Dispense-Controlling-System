package smarthrms;

import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FingerprintLogin extends JFrame implements ActionListener {
    JButton fingerprintBtn, traditionalLoginBtn, cancelBtn, checkDeviceBtn;
    JLabel label, titleLabel, statusLabel, instructionLabel, deviceStatusLabel, scanStatusLabel, fingerprintIcon;
    JProgressBar progressBar;
    FingerprintUtil fingerprintUtil;
    private boolean isAuthenticated = false;
    private boolean deviceOpen = false;
    private boolean isScanning = false;
    
    // Animation variables
    private Timer pulseTimer;
    private Timer scanAnimationTimer;
    private float pulseAlpha = 0.0f;
    private boolean pulseIncreasing = true;
    private int scanAnimationStep = 0;
    private JPanel fingerprintPanel;
    
    // ZKTeco device variables
    private long mhDevice = 0;
    private long mhDB = 0;
    private boolean mbStop = true;
    private int fpWidth = 0;
    private int fpHeight = 0;
    private byte[] imgbuf = null;
    private byte[] template = new byte[2048];
    private int[] templateLen = new int[1];
    private ScheduledExecutorService sensorExecutor;

    public FingerprintLogin() {
        setUndecorated(true);
        setLayout(null);
        
        ImageIcon appIcon = new ImageIcon(ClassLoader.getSystemResource("icons/appIcon.png"));
        setIconImage(appIcon.getImage());
        getContentPane().setBackground(Color.WHITE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        fingerprintUtil = new FingerprintUtil();
        initializeComponents();
        startAnimations();
        checkDeviceStatus();
        setVisible(true);
    }

    private void initializeComponents() {
        // Main panel with background
        label = new JLabel();
        label.setBounds(0, 0, 500, 600);
        label.setLayout(null);
        add(label);

        // Background with gradient effect
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Gradient background
                Color color1 = new Color(46, 204, 113);
                Color color2 = new Color(39, 174, 96);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle pattern
                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < getWidth(); i += 20) {
                    for (int j = 0; j < getHeight(); j += 20) {
                        g2d.fillOval(i, j, 2, 2);
                    }
                }
            }
        };
        backgroundPanel.setBounds(0, 0, 500, 600);
        backgroundPanel.setLayout(null);
        label.add(backgroundPanel);

        // Logo at top left
        try {
            ImageIcon logoIcon = new ImageIcon(ClassLoader.getSystemResource("icons/appIcon.png"));
            Image logoImage = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
            logoLabel.setBounds(20, 10, 80, 80);
            backgroundPanel.add(logoLabel);
        } catch (Exception e) {
            // If logo not found, show placeholder text
            JLabel logoLabel = new JLabel("LOGO");
            logoLabel.setBounds(20, 10, 80, 80);
            logoLabel.setFont(new Font("Arial", Font.BOLD, 14));
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setOpaque(true);
            logoLabel.setBackground(new Color(255, 255, 255, 30));
            logoLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 2));
            backgroundPanel.add(logoLabel);
        }

        // 3D "FINANCE INFORMATION SYSTEM" text - Moved to top right
        JPanel financeTextPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                String text = "FINANCE INFORMATION SYSTEM";
                Font font = new Font("Arial Black", Font.BOLD, 16);
                g2d.setFont(font);
                
                // Get text dimensions
                FontMetrics metrics = g2d.getFontMetrics(font);
                int x = (getWidth() - metrics.stringWidth(text)) / 2;
                int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                
                // Draw 3D effect - shadow layers
                for (int i = 3; i >= 1; i--) {
                    g2d.setColor(new Color(0, 100, 50, 80 - i*20)); // Dark green shadow
                    g2d.drawString(text, x + i, y + i);
                }
                
                // Draw main text
                g2d.setColor(new Color(255, 215, 0)); // Gold color for main text
                g2d.drawString(text, x, y);
                
                // Draw highlight
                g2d.setColor(new Color(255, 255, 255, 150)); // White highlight
                g2d.drawString(text, x - 1, y - 1);
            }
        };
        financeTextPanel.setBounds(110, 20, 370, 50);
        financeTextPanel.setOpaque(false);
        backgroundPanel.add(financeTextPanel);

        // Title with animation - Moved down
        titleLabel = new JLabel("FINGERPRINT LOGIN");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 70, 500, 40);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial Black", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        backgroundPanel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Secure Biometric Authentication");
        subtitleLabel.setForeground(new Color(240, 240, 240));
        subtitleLabel.setBounds(0, 105, 500, 20);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        backgroundPanel.add(subtitleLabel);

        // Form panel with animation - Adjusted position
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle with shadow
                g2d.setColor(new Color(255, 255, 255, 240));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw border
                g2d.setColor(new Color(200, 200, 200, 100));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                
                g2d.dispose();
            }
        };
        formPanel.setBounds(50, 130, 400, 420); // Adjusted height and position
        formPanel.setLayout(null);
        formPanel.setOpaque(false);
        backgroundPanel.add(formPanel);

        // Device Status
        deviceStatusLabel = new JLabel("🔴 Checking device...", SwingConstants.CENTER);
        deviceStatusLabel.setForeground(Color.RED);
        deviceStatusLabel.setBounds(50, 10, 300, 25); // Adjusted position
        deviceStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(deviceStatusLabel);

        checkDeviceBtn = new JButton("Check Device");
        checkDeviceBtn.setBounds(150, 40, 100, 25);
        checkDeviceBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        checkDeviceBtn.setBackground(new Color(52, 152, 219));
        checkDeviceBtn.setForeground(Color.WHITE);
        checkDeviceBtn.setFocusPainted(false);
        checkDeviceBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        checkDeviceBtn.addActionListener(e -> checkDeviceStatus());
        formPanel.add(checkDeviceBtn);

        // Fingerprint visualization panel
        fingerprintPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = Math.min(getWidth(), getHeight()) / 2 - 10;
                
                // Draw outer circle
                GradientPaint gp = new GradientPaint(0, 0, new Color(220, 220, 220), 
                                                    getWidth(), getHeight(), new Color(180, 180, 180));
                g2d.setPaint(gp);
                g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                
                // Draw pulse effect
                if (isScanning) {
                    g2d.setColor(new Color(52, 152, 219, (int)(pulseAlpha * 255)));
                    g2d.setStroke(new BasicStroke(3f));
                    g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                }
                
                // Draw fingerprint pattern
                drawFingerprintPattern(g2d, centerX, centerY, radius - 15);
                
                // Draw scan animation
                if (isScanning) {
                    drawScanAnimation(g2d, centerX, centerY, radius - 15);
                }
            }
            
            private void drawFingerprintPattern(Graphics2D g2d, int centerX, int centerY, int radius) {
                g2d.setColor(new Color(100, 100, 100));
                g2d.setStroke(new BasicStroke(2f));
                
                // Draw concentric circles for fingerprint
                for (int i = 0; i < 5; i++) {
                    int r = radius - i * 8;
                    if (r > 10) {
                        g2d.drawOval(centerX - r, centerY - r, r * 2, r * 2);
                    }
                }
                
                // Draw fingerprint ridges
                g2d.setStroke(new BasicStroke(1.5f));
                for (int i = 0; i < 12; i++) {
                    double angle = Math.toRadians(i * 30);
                    int x1 = centerX + (int)((radius - 20) * Math.cos(angle));
                    int y1 = centerY + (int)((radius - 20) * Math.sin(angle));
                    int x2 = centerX + (int)((radius - 5) * Math.cos(angle));
                    int y2 = centerY + (int)((radius - 5) * Math.sin(angle));
                    g2d.drawLine(x1, y1, x2, y2);
                }
            }
            
            private void drawScanAnimation(Graphics2D g2d, int centerX, int centerY, int radius) {
                g2d.setColor(new Color(46, 204, 113, 150));
                g2d.setStroke(new BasicStroke(2f));
                
                int scanRadius = (scanAnimationStep % 20) * radius / 20;
                if (scanRadius > 0) {
                    g2d.drawOval(centerX - scanRadius, centerY - scanRadius, scanRadius * 2, scanRadius * 2);
                }
            }
        };
        fingerprintPanel.setBounds(150, 75, 100, 100); // Adjusted position
        fingerprintPanel.setOpaque(false);
        formPanel.add(fingerprintPanel);

        // Fingerprint icon (fallback)
        fingerprintIcon = new JLabel("", SwingConstants.CENTER);
        fingerprintIcon.setBounds(150, 75, 100, 100); // Adjusted position
        fingerprintIcon.setFont(new Font("Serif", Font.PLAIN, 60));
        formPanel.add(fingerprintIcon);

        // Scan status indicator
        scanStatusLabel = new JLabel("", SwingConstants.CENTER);
        scanStatusLabel.setForeground(Color.GRAY);
        scanStatusLabel.setBounds(50, 180, 300, 20); // Adjusted position
        scanStatusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(scanStatusLabel);

        // Instructions
        instructionLabel = new JLabel("Click 'Start Scan' to begin", SwingConstants.CENTER);
        instructionLabel.setForeground(Color.DARK_GRAY);
        instructionLabel.setBounds(50, 205, 300, 25); // Adjusted position
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(instructionLabel);

        // Progress bar with animation
        progressBar = new JProgressBar();
        progressBar.setBounds(50, 235, 300, 20); // Adjusted position
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setForeground(new Color(46, 204, 113));
        progressBar.setBackground(new Color(230, 230, 230));
        formPanel.add(progressBar);

        // Status label
        statusLabel = new JLabel("Device ready - Waiting for scan", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(52, 152, 219));
        statusLabel.setBounds(50, 260, 300, 30); // Adjusted position
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(statusLabel);

        // Fingerprint login button with animation
        fingerprintBtn = new JButton("START SCAN");
        fingerprintBtn.setBounds(100, 300, 200, 40); // Adjusted position
        fingerprintBtn.setFont(new Font("Arial", Font.BOLD, 14));
        fingerprintBtn.setBackground(new Color(52, 152, 219));
        fingerprintBtn.setForeground(Color.WHITE);
        fingerprintBtn.setFocusPainted(false);
        fingerprintBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        fingerprintBtn.addActionListener(this);
        fingerprintBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        fingerprintBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (fingerprintBtn.isEnabled()) {
                    fingerprintBtn.setBackground(new Color(41, 128, 185));
                    fingerprintBtn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isScanning) {
                    fingerprintBtn.setBackground(new Color(231, 76, 60));
                } else {
                    fingerprintBtn.setBackground(new Color(52, 152, 219));
                }
                fingerprintBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            }
        });
        formPanel.add(fingerprintBtn);

        // Traditional login button - Commented out as requested
        /*
        traditionalLoginBtn = new JButton("Use Traditional Login");
        traditionalLoginBtn.setBounds(100, 350, 200, 35);
        traditionalLoginBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        traditionalLoginBtn.setBackground(new Color(149, 165, 166));
        traditionalLoginBtn.setForeground(Color.WHITE);
        traditionalLoginBtn.setFocusPainted(false);
        traditionalLoginBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        traditionalLoginBtn.addActionListener(this);
        traditionalLoginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        traditionalLoginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                traditionalLoginBtn.setBackground(new Color(127, 140, 141));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                traditionalLoginBtn.setBackground(new Color(149, 165, 166));
            }
        });
        formPanel.add(traditionalLoginBtn);
        */

        // Cancel button
        cancelBtn = new JButton("Cancel");
        cancelBtn.setBounds(150, 350, 150, 30); // Adjusted position
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        cancelBtn.setBackground(new Color(231, 76, 60));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        cancelBtn.addActionListener(this);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cancelBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cancelBtn.setBackground(new Color(192, 57, 43));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                cancelBtn.setBackground(new Color(231, 76, 60));
            }
        });
        formPanel.add(cancelBtn);
    }

    private void startAnimations() {
        // Pulse animation for the fingerprint visualization
        pulseTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pulseIncreasing) {
                    pulseAlpha += 0.05f;
                    if (pulseAlpha >= 1.0f) {
                        pulseAlpha = 1.0f;
                        pulseIncreasing = false;
                    }
                } else {
                    pulseAlpha -= 0.05f;
                    if (pulseAlpha <= 0.0f) {
                        pulseAlpha = 0.0f;
                        pulseIncreasing = true;
                    }
                }
                fingerprintPanel.repaint();
            }
        });
        
        // Scan animation for when scanning is active
        scanAnimationTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isScanning) {
                    scanAnimationStep = (scanAnimationStep + 1) % 40;
                    fingerprintPanel.repaint();
                }
            }
        });
        
        pulseTimer.start();
        scanAnimationTimer.start();
    }

    private void stopAnimations() {
        if (pulseTimer != null) {
            pulseTimer.stop();
        }
        if (scanAnimationTimer != null) {
            scanAnimationTimer.stop();
        }
    }

    private void checkDeviceStatus() {
        new Thread(() -> {
            try {
                boolean deviceAvailable = initializeFingerprintDevice();
                
                SwingUtilities.invokeLater(() -> {
                    if (deviceAvailable) {
                        deviceOpen = true;
                        deviceStatusLabel.setText("🟢 Device: Connected & Ready");
                        deviceStatusLabel.setForeground(new Color(39, 174, 96));
                        fingerprintBtn.setEnabled(true);
                        instructionLabel.setText("Click 'Start Scan' to begin fingerprint authentication");
                        instructionLabel.setForeground(Color.DARK_GRAY);
                        statusLabel.setText("Device ready - Click Start Scan");
                        statusLabel.setForeground(new Color(39, 174, 96));
                        
                        // Animate device status change
                        animateStatusChange(deviceStatusLabel, new Color(39, 174, 96));
                    } else {
                        deviceOpen = false;
                        deviceStatusLabel.setText("🔴 Device: Not Connected");
                        deviceStatusLabel.setForeground(Color.RED);
                        fingerprintBtn.setEnabled(false);
                        instructionLabel.setText("Please connect fingerprint device first");
                        instructionLabel.setForeground(Color.RED);
                        statusLabel.setText("Device not available");
                        statusLabel.setForeground(Color.RED);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    deviceStatusLabel.setText("⚠️ Device: Status Unknown");
                    deviceStatusLabel.setForeground(Color.ORANGE);
                    statusLabel.setText("Device check failed: " + ex.getMessage());
                    statusLabel.setForeground(Color.RED);
                });
            }
        }).start();
    }

    private void animateStatusChange(JLabel label, Color targetColor) {
        Timer animationTimer = new Timer(10, new ActionListener() {
            float alpha = 0.0f;
            Color originalColor = label.getForeground();
            
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.05f;
                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    ((Timer)e.getSource()).stop();
                }
                
                int red = (int)(originalColor.getRed() * (1 - alpha) + targetColor.getRed() * alpha);
                int green = (int)(originalColor.getGreen() * (1 - alpha) + targetColor.getGreen() * alpha);
                int blue = (int)(originalColor.getBlue() * (1 - alpha) + targetColor.getBlue() * alpha);
                
                label.setForeground(new Color(red, green, blue));
            }
        });
        animationTimer.start();
    }

    private boolean initializeFingerprintDevice() {
        try {
            // Initialize fingerprint library
            if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init()) {
                System.out.println("Failed to initialize ZKTeco library");
                return false;
            }
            
            // Check for connected devices
            int deviceCount = FingerprintSensorEx.GetDeviceCount();
            if (deviceCount <= 0) {
                System.out.println("No fingerprint devices found");
                FingerprintSensorEx.Terminate();
                return false;
            }
            
            System.out.println("Found " + deviceCount + " fingerprint device(s)");
            
            // Open the first device
            mhDevice = FingerprintSensorEx.OpenDevice(0);
            if (mhDevice == 0) {
                System.out.println("Failed to open fingerprint device");
                FingerprintSensorEx.Terminate();
                return false;
            }
            
            // Initialize template database for matching
            mhDB = FingerprintSensorEx.DBInit();
            if (mhDB == 0) {
                System.out.println("Failed to initialize template database");
                FingerprintSensorEx.CloseDevice(mhDevice);
                FingerprintSensorEx.Terminate();
                return false;
            }
            
            // Load all enrolled templates from database into memory for matching
            loadEnrolledTemplates();
            
            // Get sensor dimensions
            byte[] paramValue = new byte[4];
            int[] size = new int[1];
            size[0] = 4;
            FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size);
            fpWidth = byteArrayToInt(paramValue);
            size[0] = 4;
            FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size);
            fpHeight = byteArrayToInt(paramValue);
            
            System.out.println("Sensor dimensions: " + fpWidth + "x" + fpHeight);
            
            // Allocate image buffer
            imgbuf = new byte[fpWidth * fpHeight];
            mbStop = false;
            
            System.out.println("Fingerprint device initialized successfully");
            return true;
            
        } catch (Exception e) {
            System.out.println("Device initialization error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void loadEnrolledTemplates() {
        try {
            Connecting conn = new Connecting();
            String sql = "SELECT username, fingerprint_data FROM members WHERE fingerprint_data IS NOT NULL";
            java.sql.PreparedStatement pst = conn.con.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            
            int templateCount = 0;
            while (rs.next()) {
                String username = rs.getString("username");
                byte[] templateData = rs.getBytes("fingerprint_data");
                
                if (templateData != null && templateData.length > 0) {
                    // Add template to in-memory database for matching
                    int templateId = Math.abs(username.hashCode());
                    int result = FingerprintSensorEx.DBAdd(mhDB, templateId, templateData);
                    if (result == 0) {
                        templateCount++;
                        System.out.println("Loaded template for user: " + username);
                    } else {
                        System.out.println("Failed to load template for user: " + username);
                    }
                }
            }
            
            rs.close();
            pst.close();
            conn.close();
            
            System.out.println("Loaded " + templateCount + " enrolled templates into memory");
            
        } catch (Exception e) {
            System.out.println("Error loading enrolled templates: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startSensorMonitoring() {
        if (sensorExecutor != null && !sensorExecutor.isShutdown()) {
            sensorExecutor.shutdown();
        }
        
        sensorExecutor = Executors.newSingleThreadScheduledExecutor();
        sensorExecutor.scheduleAtFixedRate(() -> {
            if (mbStop || mhDevice == 0 || !isScanning) {
                return;
            }
            
            try {
                templateLen[0] = 2048;
                int ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen);
                
                SwingUtilities.invokeLater(() -> {
                    if (ret == FingerprintSensorErrorCode.ZKFP_ERR_OK) {
                        // Fingerprint captured successfully
                        updateScanStatus("🟢 Fingerprint detected!", Color.GREEN, "🟢");
                        progressBar.setVisible(true);
                        statusLabel.setText("Processing fingerprint...");
                        
                        // Use ZKTeco's built-in matching instead of database comparison
                        authenticateWithDeviceMatching(template, templateLen[0]);
                        
                    } else if (ret == FingerprintSensorErrorCode.ZKFP_ERR_CAPTURE) {
                        // No fingerprint detected, but device is ready
                        updateScanStatus("👆 Place finger on scanner", Color.ORANGE, "🟠");
                        progressBar.setVisible(false);
                    } else {
                        // Other error
                        updateScanStatus("⚠️ Scan error: " + ret, Color.RED, "🔴");
                        progressBar.setVisible(false);
                    }
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    updateScanStatus("❌ Scan error", Color.RED, "🔴");
                    statusLabel.setText("Error: " + e.getMessage());
                    progressBar.setVisible(false);
                });
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void authenticateWithDeviceMatching(byte[] fingerprintData, int dataLength) {
        new Thread(() -> {
            try {
                System.out.println("Matching fingerprint, data length: " + dataLength + " bytes");
                
                // Use ZKTeco's built-in template matching
                int[] matchedId = new int[1];
                int[] score = new int[1];
                int matchResult = FingerprintSensorEx.DBIdentify(mhDB, fingerprintData, matchedId, score);
                
                SwingUtilities.invokeLater(() -> {
                    if (matchResult == 0) {
                        // Match found! Now get user details from database
                        String username = findUsernameByTemplateId(matchedId[0]);
                        if (username != null) {
                            statusLabel.setText("Authentication successful!");
                            statusLabel.setForeground(new Color(39, 174, 96));
                            updateScanStatus("✅ Authentication successful!", Color.GREEN, "✅");
                            
                            // Animate success
                            animateSuccess();
                            
                            // Get user role
                            String role = getUserRole(username);
                            
                            JOptionPane.showMessageDialog(this, 
                                "Fingerprint authentication successful!\nWelcome " + username + " (" + role + ")\nMatch score: " + score[0], 
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                            // Stop scanning and launch main application
                            launchMainApplication(username);
                        } else {
                            statusLabel.setText("User not found for template");
                            statusLabel.setForeground(Color.RED);
                            updateScanStatus("❌ User not found", Color.RED, "🔴");
                        }
                    } else {
                        statusLabel.setText("Fingerprint not recognized");
                        statusLabel.setForeground(Color.RED);
                        updateScanStatus("❌ Not recognized", Color.RED, "❌");
                        
                        JOptionPane.showMessageDialog(this, 
                            "Fingerprint not recognized!\n\nMatch result: " + matchResult + 
                            "\nPlease try again or use traditional login.", 
                            "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                    }
                    progressBar.setVisible(false);
                });

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Authentication error");
                    statusLabel.setForeground(Color.RED);
                    updateScanStatus("❌ System error", Color.RED, "🔴");
                    progressBar.setVisible(false);
                    JOptionPane.showMessageDialog(this, 
                        "Authentication error: " + ex.getMessage(), 
                        "System Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void animateSuccess() {
        Timer successTimer = new Timer(50, new ActionListener() {
            int pulseCount = 0;
            float scale = 1.0f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                pulseCount++;
                if (pulseCount <= 10) {
                    scale = 1.0f + pulseCount * 0.02f;
                } else if (pulseCount <= 20) {
                    scale = 1.2f - (pulseCount - 10) * 0.02f;
                } else {
                    scale = 1.0f;
                    ((Timer)e.getSource()).stop();
                }
                
                // Apply scale transformation to fingerprint panel
                fingerprintPanel.setBounds(
                    (int)(150 - (100 * scale - 100) / 2),
                    (int)(75 - (100 * scale - 100) / 2),
                    (int)(100 * scale),
                    (int)(100 * scale)
                );
                fingerprintPanel.repaint();
            }
        });
        successTimer.start();
    }

    private String findUsernameByTemplateId(int templateId) {
        try {
            Connecting conn = new Connecting();
            String sql = "SELECT username FROM members WHERE fingerprint_data IS NOT NULL";
            java.sql.PreparedStatement pst = conn.con.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                String username = rs.getString("username");
                int calculatedId = Math.abs(username.hashCode());
                if (calculatedId == templateId) {
                    rs.close();
                    pst.close();
                    conn.close();
                    return username;
                }
            }
            
            rs.close();
            pst.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUserRole(String username) {
        try {
            Connecting conn = new Connecting();
            String sql = "SELECT role FROM members WHERE username = ?";
            java.sql.PreparedStatement pst = conn.con.prepareStatement(sql);
            pst.setString(1, username);
            java.sql.ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                rs.close();
                pst.close();
                conn.close();
                return role;
            }
            
            rs.close();
            pst.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private void updateScanStatus(String message, Color color, String icon) {
        scanStatusLabel.setText(message);
        scanStatusLabel.setForeground(color);
        fingerprintIcon.setText(icon);
    }

    private void stopFingerprintScan() {
        isScanning = false;
        mbStop = true;
        
        if (sensorExecutor != null) {
            sensorExecutor.shutdown();
            try {
                sensorExecutor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (mhDB != 0) {
            FingerprintSensorEx.DBFree(mhDB);
            mhDB = 0;
        }
        
        if (mhDevice != 0) {
            FingerprintSensorEx.CloseDevice(mhDevice);
            mhDevice = 0;
        }
        
        FingerprintSensorEx.Terminate();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == fingerprintBtn) {
            if (!deviceOpen) {
                JOptionPane.showMessageDialog(this,
                    "Cannot start fingerprint scan.\n\n" +
                    "Fingerprint device is not connected or not ready.\n" +
                    "Please check device connection and try again.",
                    "Device Not Ready",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (isScanning) {
                stopFingerprintScan();
                fingerprintBtn.setText("START SCAN");
                fingerprintBtn.setBackground(new Color(52, 152, 219));
                statusLabel.setText("Scanning stopped");
                updateScanStatus("⏹️ Scan stopped", Color.GRAY, "🔒");
                progressBar.setVisible(false);
                instructionLabel.setText("Click 'Start Scan' to begin");
                instructionLabel.setForeground(Color.DARK_GRAY);
                
                // Reset fingerprint panel position
                fingerprintPanel.setBounds(150, 75, 100, 100);
            } else {
                startFingerprintScan();
            }
            
        } else if (ae.getSource() == traditionalLoginBtn) {
            stopFingerprintScan();
            openTraditionalLogin();
        } else if (ae.getSource() == cancelBtn) {
            stopFingerprintScan();
            dispose();
            System.exit(0);
        } else if (ae.getSource() == checkDeviceBtn) {
            checkDeviceStatus();
        }
    }

    private void startFingerprintScan() {
        if (!deviceOpen || mhDevice == 0) {
            JOptionPane.showMessageDialog(this,
                "Fingerprint device is not available.\n" +
                "Please connect the device and click 'Check Device'.",
                "Device Not Available",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        isScanning = true;
        mbStop = false;
        
        fingerprintBtn.setText("STOP SCANNING");
        fingerprintBtn.setBackground(new Color(231, 76, 60));
        instructionLabel.setText("Place your finger on the scanner now");
        instructionLabel.setForeground(Color.ORANGE);
        statusLabel.setText("Scanning for fingerprints...");
        statusLabel.setForeground(Color.ORANGE);
        updateScanStatus("🟠 Ready for fingerprint...", Color.ORANGE, "🟠");
        progressBar.setVisible(false);

        startSensorMonitoring();
    }

    private int byteArrayToInt(byte[] bytes) {
        int number = bytes[0] & 0xFF;
        number |= ((bytes[1] << 8) & 0xFF00);
        number |= ((bytes[2] << 16) & 0xFF0000);
        number |= ((bytes[3] << 24) & 0xFF000000);
        return number;
    }

    private void openTraditionalLogin() {
        dispose();
        new TraditionalLogin().setVisible(true);
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

    @Override
    public void dispose() {
        stopAnimations();
        stopFingerprintScan();
        if (fingerprintUtil != null) {
            fingerprintUtil.close();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FingerprintLogin().setVisible(true));
    }
}