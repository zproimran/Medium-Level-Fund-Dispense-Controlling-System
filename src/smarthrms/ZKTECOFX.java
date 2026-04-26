package smarthrms;

// Import ZKTeco biometric library for fingerprint sensor operations
import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
// JavaFX imports for modern UI components and threading
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

// Image processing imports for fingerprint image handling
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ZKTECOFX - Modern Fingerprint Recognition System with Binary Data Storage
 * Benefits: Enterprise-grade biometric authentication with efficient binary storage
 * Features: Fingerprint enrollment, verification, identification, and user registration with binary templates
 */
public class ZKTECOFX extends BorderPane {

    // UI Components for intuitive user interaction
    private ImageView fingerprintImageView;
    
    // Control elements for device operations
    private TextArea logTextArea;
    private Button btnOpen, btnClose, btnEnroll, btnVerify, btnIdentify, btnRegImg, btnIdentImg, btnUserRegistration;
    private ToggleGroup templateGroup;
    private RadioButton radioANSI, radioISO;
    private ProgressIndicator progressIndicator;
    private Label statusLabel;

    // Fingerprint sensor configuration variables
    private int fpWidth = 0;
    private int fpHeight = 0;
    
    // Template storage for binary fingerprint data management
    private byte[] lastRegTemp = new byte[2048];
    private int cbRegTemp = 0;
    private byte[][] regtemparray = new byte[3][2048];
    private boolean bRegister = false;
    private boolean bIdentify = true;
    private int iFid = 1;
    private int nFakeFunOn = 1;
    private int enroll_idx = 0;

    // Image and template buffers for sensor data
    private byte[] imgbuf = null;
    private byte[] template = new byte[2048];
    private int[] templateLen = new int[1];

    // Device management variables
    private boolean mbStop = true;
    private long mhDevice = 0;
    private long mhDB = 0;
    private ScheduledExecutorService sensorExecutor;

    // User registration integration variables
    private byte[] currentEnrollmentTemplate = null;
    private String currentEnrollmentUsername = null;

    /**
     * Constructor - Initializes the complete fingerprint system UI
     */
    public ZKTECOFX() {
        initializeUI();
        setupEventHandlers();
    }

    /**
     * UI Initialization - Creates the main application interface
     */
    private void initializeUI() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #3498db);");
        setCenter(createCenterContent());
        setBottom(createFooter());
    }

    /**
     * Creates the central content area with controls and fingerprint display
     */
    private HBox createCenterContent() {
        HBox centerContent = new HBox(20);
        centerContent.setPadding(new Insets(20));
        centerContent.getChildren().addAll(createControlPanel(), createImagePanel());
        return centerContent;
    }

    /**
     * Creates the device control panel with operation buttons
     */
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(15);
        controlPanel.setPrefWidth(350);
        controlPanel.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 10; -fx-padding: 20;");

        Label controlTitle = new Label("Device Controls");
        controlTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        controlTitle.setTextFill(Color.DARKBLUE);

        btnOpen = createStyledButton("Connect Device", "#27ae60");
        btnClose = createStyledButton("Disconnect", "#e74c3c");
        HBox deviceControls = new HBox(10, btnOpen, btnClose);

        Label opTitle = new Label("Operations");
        opTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        btnEnroll = createStyledButton("Enroll Fingerprint", "#2980b9");
        btnVerify = createStyledButton("Verify", "#f39c12");
        btnIdentify = createStyledButton("Identify", "#8e44ad");
        btnRegImg = createStyledButton("Register from Image", "#16a085");
        btnIdentImg = createStyledButton("Verify from Image", "#d35400");
        
        btnUserRegistration = createStyledButton("User Registration", "#9b59b6");
        btnUserRegistration.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnUserRegistration.setOnMouseEntered(e -> btnUserRegistration.setStyle("-fx-background-color: derive(#9b59b6, 20%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"));
        btnUserRegistration.setOnMouseExited(e -> btnUserRegistration.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"));

        Label formatLabel = new Label("Template Format:");
        formatLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        templateGroup = new ToggleGroup();
        radioANSI = new RadioButton("ANSI");
        radioISO = new RadioButton("ISO");
        radioANSI.setToggleGroup(templateGroup);
        radioISO.setToggleGroup(templateGroup);
        radioANSI.setSelected(true);

        HBox formatBox = new HBox(10, radioANSI, radioISO);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        controlPanel.getChildren().addAll(
                controlTitle, deviceControls, new Separator(),
                opTitle, btnEnroll, btnVerify, btnIdentify,
                btnRegImg, btnIdentImg, new Separator(),
                btnUserRegistration, new Separator(),
                formatLabel, formatBox, progressIndicator
        );

        return controlPanel;
    }

    /**
     * Creates the fingerprint image display panel
     */
    private VBox createImagePanel() {
        VBox imagePanel = new VBox(15);
        imagePanel.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 10; -fx-padding: 20;");

        Label imageTitle = new Label("Fingerprint Preview");
        imageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        imageTitle.setTextFill(Color.DARKBLUE);

        fingerprintImageView = new ImageView();
        fingerprintImageView.setFitWidth(400);
        fingerprintImageView.setFitHeight(300);
        fingerprintImageView.setPreserveRatio(true);
        fingerprintImageView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-border-radius: 5;");
        fingerprintImageView.setImage(createPlaceholderImage());

        imagePanel.getChildren().addAll(imageTitle, fingerprintImageView);
        VBox.setVgrow(fingerprintImageView, Priority.ALWAYS);

        return imagePanel;
    }

    /**
     * Creates the footer with status and logging area
     */
    private VBox createFooter() {
        VBox footer = new VBox(10);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        statusLabel = new Label("Ready to connect...");
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        logTextArea = new TextArea();
        logTextArea.setPrefHeight(120);
        logTextArea.setStyle("-fx-control-inner-background: #2c3e50; -fx-text-fill: white; -fx-font-family: 'Consolas';");
        logTextArea.setEditable(false);

        footer.getChildren().addAll(statusLabel, new Label("Activity Log:"), logTextArea);
        return footer;
    }

    /**
     * Creates consistently styled buttons with hover effects
     */
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(35);
        button.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;",
                color
        ));
        button.setOnMouseEntered(e -> button.setStyle(
                String.format("-fx-background-color: derive(%s, 20%%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;", color)
        ));
        button.setOnMouseExited(e -> button.setStyle(
                String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;", color)
        ));
        return button;
    }

    /**
     * Creates a placeholder image for fingerprint display
     */
    private Image createPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 400; x++)
            for (int y = 0; y < 300; y++)
                placeholder.setRGB(x, y, 0xFFF0F0F0);
        return SwingFXUtils.toFXImage(placeholder, null);
    }

    /**
     * Sets up event handlers for all interactive elements
     */
    private void setupEventHandlers() {
        btnOpen.setOnAction(e -> openDevice());
        btnClose.setOnAction(e -> closeDevice());
        btnEnroll.setOnAction(e -> startEnrollment());
        btnVerify.setOnAction(e -> startVerification());
        btnIdentify.setOnAction(e -> startIdentification());
        btnRegImg.setOnAction(e -> registerFromImage());
        btnIdentImg.setOnAction(e -> verifyFromImage());
        btnUserRegistration.setOnAction(e -> openUserRegistration());
    }
    
    // Add these methods to your ZKTECOFX class

/**
 * Registers a new user with binary fingerprint data
 */
public void registerUserWithFingerprint(String fullName, String email, String role, 
                                      String department, String username, String password, 
                                      byte[] fingerprintTemplate) {
    final String finalFullName = fullName;
    final String finalEmail = email;
    final String finalRole = role;
    final String finalDepartment = department;
    final String finalUsername = username;
    final String finalPassword = password;
    final byte[] finalFingerprintTemplate = fingerprintTemplate != null ? 
        Arrays.copyOf(fingerprintTemplate, fingerprintTemplate.length) : null;
    
    new Thread(() -> {
        try {
            Connecting conn = new Connecting();
            
            // Store fingerprint template as binary data
            boolean success = conn.insertUser(finalFullName, finalEmail, finalRole, finalDepartment, 
                                           finalUsername, finalPassword, finalFingerprintTemplate);

            final boolean finalSuccess = success;

            Platform.runLater(() -> {
                if (finalSuccess) {
                    logMessage("✅ User registered successfully: " + finalUsername);
                    logMessage("📊 Binary fingerprint stored: " + 
                              (finalFingerprintTemplate != null ? finalFingerprintTemplate.length + " bytes" : "No fingerprint"));
                    
                    // Show success message
                    javafx.application.Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Registration Success");
                        alert.setHeaderText("User Registered Successfully");
                        alert.setContentText("User '" + finalUsername + "' has been registered.\n" +
                                           "Fingerprint: " + (finalFingerprintTemplate != null ? 
                                           "Binary data stored (" + finalFingerprintTemplate.length + " bytes)" : "Not stored"));
                        alert.showAndWait();
                    });
                } else {
                    logMessage("❌ Failed to register user: " + finalUsername);
                    
                    javafx.application.Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Registration Failed");
                        alert.setHeaderText("Registration Error");
                        alert.setContentText("Failed to register user '" + finalUsername + "'.\n" +
                                           "Username might already exist or database error occurred.");
                        alert.showAndWait();
                    });
                }
            });
            
            conn.close();
            
        } catch (Exception ex) {
            Platform.runLater(() -> {
                logMessage("❌ Database error during registration: " + ex.getMessage());
                
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText("Registration Failed");
                alert.setContentText("Database error: " + ex.getMessage() + "\n" +
                                   "Please check your database connection and table structure.");
                alert.showAndWait();
            });
        }
    }).start();
}

/**
 * Authenticates user with binary fingerprint data
 */
public void authenticateWithFingerprint(byte[] fingerprintData) {
    new Thread(() -> {
        try {
            Connecting conn = new Connecting();
            String authResult = conn.authenticateWithFingerprintTemplate(fingerprintData);
            
            Platform.runLater(() -> {
                if (authResult != null) {
                    if ("INACTIVE".equals(authResult)) {
                        logMessage("❌ Account is deactivated");
                        showAlert("Authentication Failed", "Your account has been deactivated.\nPlease contact administrator.");
                    } else {
                        String[] parts = authResult.split("\\|");
                        String username = parts[0];
                        String role = parts.length > 1 ? parts[1] : "Unknown";
                        
                        logMessage("✅ Authentication successful! User: " + username + ", Role: " + role);
                        
                        // Launch main application
                        launchMainApplication(username);
                    }
                } else {
                    logMessage("❌ Authentication failed - No matching fingerprint found");
                    showAlert("Authentication Failed", 
                        "Fingerprint not recognized!\n\nPlease try again or contact administrator.");
                }
            });
            
            conn.close();
            
        } catch (Exception ex) {
            Platform.runLater(() -> {
                logMessage("❌ Authentication error: " + ex.getMessage());
                showAlert("System Error", "Authentication error: " + ex.getMessage());
            });
        }
    }).start();
}

/**
 * Real-time fingerprint authentication for login
 */
public void startRealtimeAuthentication() {
    if (mhDevice == 0) {
        logMessage("⚠️ Please connect device first!");
        return;
    }
    bRegister = false;
    bIdentify = false;
    logMessage("🔐 Real-time authentication mode activated - Place your finger to login");
    
    // Start monitoring for authentication
    new Thread(() -> {
        while (!mbStop && mhDevice != 0) {
            try {
                templateLen[0] = 2048;
                int ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen);
                
                if (ret == 0) {
                    // Authenticate with binary template
                    authenticateWithFingerprint(template);
                    Thread.sleep(2000); // Prevent rapid repeated attempts
                }
                Thread.sleep(100);
            } catch (Exception e) {
                logMessage("❌ Authentication error: " + e.getMessage());
            }
        }
    }).start();
}

/**
 * Helper method to show alerts
 */
private void showAlert(String title, String message) {
    Platform.runLater(() -> {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    });
}

/**
 * Launches main application after successful authentication
 */
private void launchMainApplication(String username) {
    new Thread(() -> {
        try {
            // Close fingerprint application
            Platform.runLater(() -> {
                javafx.stage.Stage stage = (javafx.stage.Stage) getScene().getWindow();
                stage.close();
            });

            // Launch main HRMS application
            Thread.sleep(1000);
            
            // Use reflection to launch SmartHRMS
            Class<?> smartHRMSClass = Class.forName("smarthrms.SmartHRMS");
            java.lang.reflect.Method mainMethod = smartHRMSClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[]{username});
            
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                showAlert("System Error", "Error launching application: " + e.getMessage());
            });
        }
    }).start();
}
    
    

    /**
     * Opens user registration interface with current fingerprint template
     */
    private void openUserRegistration() {
        try {
            logMessage("🔍 Checking template - currentEnrollmentTemplate: " + 
                      (currentEnrollmentTemplate != null ? currentEnrollmentTemplate.length + " bytes" : "null"));
            
            if (currentEnrollmentTemplate == null || currentEnrollmentTemplate.length == 0) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("No Fingerprint Template");
                    alert.setHeaderText("Fingerprint Required");
                    alert.setContentText("Please enroll a fingerprint first using the 'Enroll Fingerprint' button.\n\n" +
                                       "Steps:\n" +
                                       "1. Click 'Enroll Fingerprint'\n" +
                                       "2. Place your finger 3 times as instructed\n" +
                                       "3. After successful enrollment, click 'User Registration' again");
                    alert.showAndWait();
                });
                return;
            }
            
            UserRegistrationFX registration = new UserRegistrationFX(this, currentEnrollmentTemplate);
            registration.show();
            
            logMessage("📋 Opening user registration with binary fingerprint template (" + 
                      currentEnrollmentTemplate.length + " bytes)");
            
        } catch (Exception e) {
            logMessage("❌ Error opening user registration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Checks current template status for debugging
     */
    public void checkTemplateStatus() {
        Platform.runLater(() -> {
            String status = currentEnrollmentTemplate != null ? 
                "Binary template available (" + currentEnrollmentTemplate.length + " bytes)" : 
                "No template available";
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Template Status");
            alert.setHeaderText("Fingerprint Template Status");
            alert.setContentText(status);
            alert.showAndWait();
        });
    }

    // Template management getter and setter methods
    public byte[] getCurrentEnrollmentTemplate() {
        return currentEnrollmentTemplate;
    }

    public void setCurrentEnrollmentTemplate(byte[] template) {
        this.currentEnrollmentTemplate = template;
    }

    public void setCurrentEnrollmentUsername(String username) {
        this.currentEnrollmentUsername = username;
    }

    /**
     * Handles application close request
     */
    public void handleCloseRequest() {
        closeDevice();
    }

    /**
     * Opens and initializes the fingerprint sensor device
     */
    void openDevice() {
        if (mhDevice != 0) {
            logMessage("Device is already connected!");
            return;
        }
        showProgress(true);
        logMessage("Initializing fingerprint sensor...");

        new Thread(() -> {
            try {
                int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
                cbRegTemp = 0;
                bRegister = false;
                bIdentify = false;
                iFid = 1;
                enroll_idx = 0;

                if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init()) {
                    Platform.runLater(() -> {
                        logMessage("❌ Initialization failed!");
                        showProgress(false);
                    });
                    return;
                }

                ret = FingerprintSensorEx.GetDeviceCount();
                if (ret < 0) {
                    Platform.runLater(() -> {
                        logMessage("❌ No devices connected!");
                        showProgress(false);
                        FreeSensor();
                    });
                    return;
                }

                if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0))) {
                    Platform.runLater(() -> {
                        logMessage("❌ Failed to open device!");
                        showProgress(false);
                        FreeSensor();
                    });
                    return;
                }

                if (0 == (mhDB = FingerprintSensorEx.DBInit())) {
                    Platform.runLater(() -> {
                        logMessage("❌ Database initialization failed!");
                        showProgress(false);
                        FreeSensor();
                    });
                    return;
                }

                int nFmt = radioISO.isSelected() ? 1 : 0;
                FingerprintSensorEx.DBSetParameter(mhDB, 5010, nFmt);

                byte[] paramValue = new byte[4];
                int[] size = new int[1];
                size[0] = 4;
                FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size);
                fpWidth = byteArrayToInt(paramValue);
                size[0] = 4;
                FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size);
                fpHeight = byteArrayToInt(paramValue);

                imgbuf = new byte[fpWidth * fpHeight];
                mbStop = false;

                startSensorMonitoring();

                Platform.runLater(() -> {
                    logMessage("✅ Device connected successfully!");
                    updateStatus("Connected - Ready for operations");
                    showProgress(false);
                    updateButtonStates(true);
                });

            } catch (Exception ex) {
                Platform.runLater(() -> {
                    logMessage("❌ Error: " + ex.getMessage());
                    showProgress(false);
                });
            }
        }).start();
    }
    
    

    /**
     * Closes the fingerprint sensor device and releases resources
     */
    public void closeDevice() {
        showProgress(true);
        logMessage("Closing device connection...");

        new Thread(() -> {
            FreeSensor();
            Platform.runLater(() -> {
                logMessage("✅ Device disconnected successfully!");
                updateStatus("Disconnected");
                showProgress(false);
                updateButtonStates(false);
                fingerprintImageView.setImage(createPlaceholderImage());
            });
        }).start();
    }

    /**
     * Starts continuous sensor monitoring for fingerprint capture
     */
    private void startSensorMonitoring() {
        sensorExecutor = Executors.newSingleThreadScheduledExecutor();
        sensorExecutor.scheduleAtFixedRate(() -> {
            if (mbStop || mhDevice == 0) return;

            templateLen[0] = 2048;
            int ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen);

            if (ret == 0) {
                Platform.runLater(() -> {
                    onCaptureSuccess(imgbuf);
                    onExtractSuccess(template, templateLen[0]);
                });
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Processes successfully captured fingerprint image
     */
    private void onCaptureSuccess(byte[] imgBuf) {
        try {
            writeBitmap(imgBuf, fpWidth, fpHeight, "fingerprint_current.bmp");
            File imageFile = new File("fingerprint_current.bmp");
            if (imageFile.exists()) {
                BufferedImage bufferedImage = ImageIO.read(imageFile);
                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                fingerprintImageView.setImage(fxImage);
            }
        } catch (IOException e) {
            logMessage("❌ Error displaying fingerprint image: " + e.getMessage());
        }
    }

    /**
     * Processes successfully extracted fingerprint template
     */
    private void onExtractSuccess(byte[] template, int len) {
        if (bRegister) handleEnrollment(template);
        else if (bIdentify) handleIdentification(template);
        else handleVerification(template);
    }

    /**
     * Handles fingerprint enrollment process
     */
    private void handleEnrollment(byte[] template) {
        int[] fid = new int[1];
        int[] score = new int[1];
        int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);

        if (ret == 0) {
            logMessage("❌ Finger already enrolled by ID: " + fid[0]);
            bRegister = false;
            enroll_idx = 0;
            return;
        }

        if (enroll_idx > 0 && FingerprintSensorEx.DBMatch(mhDB, regtemparray[enroll_idx - 1], template) <= 0) {
            logMessage("⚠️ Please use the same finger for all 3 enrollments");
            return;
        }

        System.arraycopy(template, 0, regtemparray[enroll_idx], 0, 2048);
        enroll_idx++;

        if (enroll_idx == 3) completeEnrollment();
        else logMessage("📝 Enrollment step " + enroll_idx + "/3 completed");
    }

    /**
     * Completes the enrollment process by merging templates
     */
    private void completeEnrollment() {
        int[] retLen = new int[1];
        retLen[0] = 2048;
        byte[] regTemp = new byte[retLen[0]];

        int ret = FingerprintSensorEx.DBMerge(mhDB, regtemparray[0], regtemparray[1], regtemparray[2], regTemp, retLen);
        if (ret == 0) {
            ret = FingerprintSensorEx.DBAdd(mhDB, iFid, regTemp);
            if (ret == 0) {
                iFid++;
                cbRegTemp = retLen[0];
                System.arraycopy(regTemp, 0, lastRegTemp, 0, cbRegTemp);
                
                // Store binary template for user registration
                currentEnrollmentTemplate = Arrays.copyOf(regTemp, cbRegTemp);
                
                logMessage("✅ Enrollment successful! Assigned ID: " + (iFid - 1));
                logMessage("📋 Binary template ready for user registration (" + cbRegTemp + " bytes)");
                
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Enrollment Complete");
                    alert.setHeaderText("Fingerprint Enrollment Successful");
                    alert.setContentText("Binary template is ready for user registration.\nYou can now register a user with this fingerprint.");
                    alert.showAndWait();
                });
            } else logMessage("❌ Database add failed: " + ret);
        } else logMessage("❌ Template merge failed: " + ret);

        bRegister = false;
        enroll_idx = 0;
    }

    /**
     * Handles fingerprint identification against database
     */
    private void handleIdentification(byte[] template) {
        int[] fid = new int[1];
        int[] score = new int[1];
        int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);

        if (ret == 0) {
            logMessage("✅ Identified! User ID: " + fid[0] + " | Score: " + score[0]);
        } else {
            logMessage("❌ Identification failed: " + ret);
        }
    }

    /**
     * Handles fingerprint verification against specific template
     */
    private void handleVerification(byte[] template) {
        if (cbRegTemp <= 0) {
            logMessage("⚠️ Please enroll a fingerprint first!");
            return;
        }

        int ret = FingerprintSensorEx.DBMatch(mhDB, lastRegTemp, template);
        if (ret > 0) {
            logMessage("✅ Verification successful! Score: " + ret);
        } else {
            logMessage("❌ Verification failed: " + ret);
        }
    }

    /**
     * Starts fingerprint enrollment process
     */
    public void startEnrollment() {
        if (mhDevice == 0) {
            logMessage("⚠️ Please connect device first!");
            return;
        }
        bRegister = true;
        bIdentify = false;
        enroll_idx = 0;
        currentEnrollmentTemplate = null;
        logMessage("🎯 Enrollment started - Place your finger 3 times");
    }

    /**
     * Starts fingerprint verification process
     */
    private void startVerification() {
        if (mhDevice == 0) {
            logMessage("⚠️ Please connect device first!");
            return;
        }
        bRegister = false;
        bIdentify = false;
        logMessage("🔍 Verification mode activated");
    }

    /**
     * Starts fingerprint identification process
     */
    void startIdentification() {
        if (mhDevice == 0) {
            logMessage("⚠️ Please connect device first!");
            return;
        }
        bRegister = false;
        bIdentify = true;
        logMessage("👤 Identification mode activated");
    }

    /**
     * Real-time fingerprint authentication for login
     */

    /**
     * Placeholder for image-based registration
     */
    private void registerFromImage() {
        logMessage("📁 Image registration feature not implemented");
    }

    /**
     * Placeholder for image-based verification
     */
    private void verifyFromImage() {
        logMessage("📁 Image verification feature not implemented");
    }

    /**
     * Updates button states based on connection status
     */
    private void updateButtonStates(boolean connected) {
        btnOpen.setDisable(connected);
        btnClose.setDisable(!connected);
        btnEnroll.setDisable(!connected);
        btnVerify.setDisable(!connected);
        btnIdentify.setDisable(!connected);
        btnRegImg.setDisable(!connected);
        btnIdentImg.setDisable(!connected);
        btnUserRegistration.setDisable(!connected);
    }

    /**
     * Shows or hides progress indicator
     */
    private void showProgress(boolean show) {
        progressIndicator.setVisible(show);
        progressIndicator.setProgress(show ? ProgressIndicator.INDETERMINATE_PROGRESS : 0);
    }

    /**
     * Updates status label with current system state
     */
    private void updateStatus(String message) {
        statusLabel.setText("Status: " + message);
    }

    /**
     * Logs messages to text area with thread safety
     */
    private void logMessage(String message) {
        Platform.runLater(() -> {
            logTextArea.appendText(message + "\n");
            logTextArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    /**
     * Releases all sensor resources and stops monitoring
     */
    private void FreeSensor() {
        mbStop = true;
        if (sensorExecutor != null) sensorExecutor.shutdown();
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
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

    /**
     * Saves fingerprint image to bitmap file
     */
    public static void writeBitmap(byte[] imageBuf, int nWidth, int nHeight, String path) throws IOException {
        java.io.FileOutputStream fos = new java.io.FileOutputStream(path);
        java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);

        int w = (((nWidth + 3) / 4) * 4);
        int bfType = 0x424d;
        int bfSize = 54 + 1024 + w * nHeight;
        int bfReserved1 = 0;
        int bfReserved2 = 0;
        int bfOffBits = 54 + 1024;

        dos.writeShort(bfType);
        dos.write(changeByte(bfSize), 0, 4);
        dos.write(changeByte(bfReserved1), 0, 2);
        dos.write(changeByte(bfReserved2), 0, 2);
        dos.write(changeByte(bfOffBits), 0, 4);

        int biSize = 40;
        int biWidth = nWidth;
        int biHeight = nHeight;
        int biPlanes = 1;
        int biBitcount = 8;
        int biCompression = 0;
        int biSizeImage = w * nHeight;
        int biXPelsPerMeter = 0;
        int biYPelsPerMeter = 0;
        int biClrUsed = 0;
        int biClrImportant = 0;

        dos.write(changeByte(biSize), 0, 4);
        dos.write(changeByte(biWidth), 0, 4);
        dos.write(changeByte(biHeight), 0, 4);
        dos.write(changeByte(biPlanes), 0, 2);
        dos.write(changeByte(biBitcount), 0, 2);
        dos.write(changeByte(biCompression), 0, 4);
        dos.write(changeByte(biSizeImage), 0, 4);
        dos.write(changeByte(biXPelsPerMeter), 0, 4);
        dos.write(changeByte(biYPelsPerMeter), 0, 4);
        dos.write(changeByte(biClrUsed), 0, 4);
        dos.write(changeByte(biClrImportant), 0, 4);

        for (int i = 0; i < 256; i++) {
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(i);
            dos.writeByte(0);
        }

        byte[] filter = null;
        if (w > nWidth) filter = new byte[w - nWidth];

        for (int i = 0; i < nHeight; i++) {
            dos.write(imageBuf, (nHeight - 1 - i) * nWidth, nWidth);
            if (w > nWidth) dos.write(filter, 0, w - nWidth);
        }

        dos.flush();
        dos.close();
        fos.close();
    }

    /**
     * Converts integer to byte array for bitmap writing
     */
    public static byte[] changeByte(int data) {
        return intToByteArray(data);
    }

    /**
     * Converts integer to little-endian byte array
     */
    public static byte[] intToByteArray(final int number) {
        byte[] abyte = new byte[4];
        abyte[0] = (byte) (0xff & number);
        abyte[1] = (byte) ((0xff00 & number) >> 8);
        abyte[2] = (byte) ((0xff0000 & number) >> 16);
        abyte[3] = (byte) ((0xff000000 & number) >> 24);
        return abyte;
    }

    /**
     * Converts byte array to integer
     */
    public static int byteArrayToInt(byte[] bytes) {
        int number = bytes[0] & 0xFF;
        number |= ((bytes[1] << 8) & 0xFF00);
        number |= ((bytes[2] << 16) & 0xFF0000);
        number |= ((bytes[3] << 24) & 0xFF000000);
        return number;
    }

}