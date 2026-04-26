package smarthrms;

import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * ZKTECO - Complete Fingerprint Recognition System with All Required Methods
 * Fully integrated with EmployeeSignatureManagement system
 */
public class ZKTECO extends BorderPane {

    // UI Components
    private ImageView fingerprintImageView;
    private TextArea logTextArea;
    private Button btnOpen, btnClose, btnEnroll, btnVerify, btnIdentify, btnRegImg, btnIdentImg, btnUserRegistration;
    private ToggleGroup templateGroup;
    private RadioButton radioANSI, radioISO;
    private ProgressIndicator progressIndicator;
    private Label statusLabel;

    // Fingerprint sensor configuration
    private int fpWidth = 0;
    private int fpHeight = 0;
    
    // Template storage
    private byte[] lastRegTemp = new byte[2048];
    private int cbRegTemp = 0;
    private byte[][] regtemparray = new byte[3][2048];
    private boolean bRegister = false;
    private boolean bIdentify = true;
    private int iFid = 1;
    private int nFakeFunOn = 1;
    private int enroll_idx = 0;

    // Image and template buffers
    private byte[] imgbuf = null;
    private byte[] template = new byte[2048];
    private int[] templateLen = new int[1];

    // Device management
    private boolean mbStop = true;
    private long mhDevice = 0;
    private long mhDB = 0;
    private ScheduledExecutorService sensorExecutor;

    // User registration and matching
    private byte[] currentEnrollmentTemplate = null;
    private String currentEnrollmentUsername = null;
    private Consumer<byte[]> onCaptureCallback = null;
    private Consumer<EmployeeSignatureModel> onMatchCallback = null;

    // For single capture operations
    private AtomicReference<byte[]> singleCaptureResult = new AtomicReference<>();
    private Object captureLock = new Object();
    private boolean captureInProgress = false;

    public ZKTECO() {
        initializeUI();
        setupEventHandlers();
        logMessage("🚀 ZKTeco Fingerprint System Ready");
        logMessage("💡 Click 'Connect REAL Device' to initialize");
    }

    // ========== MISSING CRITICAL METHODS ==========

    /**
     * Capture fingerprint template directly - SIMPLIFIED VERSION
     * This method is called by EmployeeSignatureManagement.captureFingerprint()
     */
    public byte[] captureFingerprint() {
        return captureFingerprint(30); // Default 30 second timeout
    }

    /**
     * Enhanced capture method with timeout parameter
     */
    public byte[] captureFingerprint(int timeoutSeconds) {
        if (!isDeviceActuallyConnected()) {
            logMessage("❌ Device not connected for capture");
            return null;
        }

        if (captureInProgress) {
            logMessage("⚠️ Capture already in progress");
            return null;
        }

        logMessage("📸 Starting fingerprint capture (timeout: " + timeoutSeconds + "s)...");
        
        captureInProgress = true;
        singleCaptureResult.set(null);
        
        // Store previous states
        boolean wasRegistering = bRegister;
        boolean wasIdentifying = bIdentify;
        
        // Set to capture mode
        bRegister = false;
        bIdentify = false;
        
        try {
            Consumer<byte[]> originalCallback = onCaptureCallback;
            onCaptureCallback = capturedTemplate -> {
                if (capturedTemplate != null && capturedTemplate.length > 0) {
                    singleCaptureResult.set(Arrays.copyOf(capturedTemplate, capturedTemplate.length));
                    logMessage("✅ Fingerprint captured successfully (" + capturedTemplate.length + " bytes)");
                    
                    // Restore original callback
                    onCaptureCallback = originalCallback;
                    
                    synchronized (captureLock) {
                        captureLock.notifyAll();
                    }
                }
            };
            
            // Wait for capture with timeout
            synchronized (captureLock) {
                try {
                    captureLock.wait(timeoutSeconds * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logMessage("❌ Capture interrupted");
                    return null;
                }
            }
            
            byte[] result = singleCaptureResult.get();
            if (result != null) {
                return result;
            } else {
                logMessage("❌ Fingerprint capture timeout after " + timeoutSeconds + " seconds");
                return null;
            }
            
        } finally {
            // Restore previous states
            bRegister = wasRegistering;
            bIdentify = wasIdentifying;
            onCaptureCallback = null;
            captureInProgress = false;
        }
    }

    /**
     * Open device with boolean return - SIMPLIFIED VERSION
     * This method is called by EmployeeSignatureManagement.connectFingerprintDevice()
     */
    public boolean openDevice() {
        if (isDeviceActuallyConnected()) {
            logMessage("⚠️ Device is already connected!");
            return true;
        }

        final AtomicReference<Boolean> connectionResult = new AtomicReference<>(false);
        final Object lock = new Object();
        
        showProgress(true);
        logMessage("🔄 Initializing fingerprint sensor...");

        new Thread(() -> {
            try {
                int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
                cbRegTemp = 0;
                bRegister = false;
                bIdentify = false;
                iFid = 1;
                enroll_idx = 0;

                // Initialize SDK
                if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init()) {
                    Platform.runLater(() -> {
                        logMessage("❌ SDK Initialization failed!");
                        logMessage("💡 Check if ZKTeco drivers are installed");
                        showProgress(false);
                    });
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    return;
                }

                // Check device count
                ret = FingerprintSensorEx.GetDeviceCount();
                if (ret < 0) {
                    Platform.runLater(() -> {
                        logMessage("❌ No devices connected!");
                        logMessage("💡 Please connect a ZKTeco fingerprint scanner");
                        showProgress(false);
                    });
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    return;
                }

                logMessage("✅ Devices detected: " + ret);

                // Open device
                if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0))) {
                    Platform.runLater(() -> {
                        logMessage("❌ Failed to open device!");
                        logMessage("💡 Device might be in use by another application");
                        showProgress(false);
                    });
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    return;
                }

                // Initialize database
                if (0 == (mhDB = FingerprintSensorEx.DBInit())) {
                    Platform.runLater(() -> {
                        logMessage("❌ Database initialization failed!");
                        showProgress(false);
                    });
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    return;
                }

                int nFmt = radioISO.isSelected() ? 1 : 0;
                FingerprintSensorEx.DBSetParameter(mhDB, 5010, nFmt);

                // Get device parameters
                byte[] paramValue = new byte[4];
                int[] size = new int[1];
                size[0] = 4;
                
                // Get width
                if (FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size) == 0) {
                    fpWidth = byteArrayToInt(paramValue);
                } else {
                    logMessage("⚠️ Could not get device width, using default: 256");
                    fpWidth = 256;
                }
                
                size[0] = 4;
                // Get height
                if (FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size) == 0) {
                    fpHeight = byteArrayToInt(paramValue);
                } else {
                    logMessage("⚠️ Could not get device height, using default: 360");
                    fpHeight = 360;
                }

                logMessage("📏 Sensor resolution: " + fpWidth + "x" + fpHeight);

                imgbuf = new byte[fpWidth * fpHeight];
                mbStop = false;

                startSensorMonitoring();

                Platform.runLater(() -> {
                    logMessage("✅ Device connected successfully!");
                    updateStatus("Connected - Ready for operations");
                    showProgress(false);
                    updateButtonStates(true);
                    connectionResult.set(true);
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> {
                    logMessage("❌ Error during device connection: " + ex.getMessage());
                    logMessage("💡 Check device drivers and connection");
                    showProgress(false);
                });
                FreeSensor();
                synchronized (lock) {
                    lock.notifyAll();
                }
                ex.printStackTrace();
            }
        }).start();

        // Wait for connection result
        synchronized (lock) {
            try {
                lock.wait(15000); // 15 second timeout
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return connectionResult.get();
    }

    /**
     * Quick device open with minimal logging
     */
    public boolean quickOpenDevice() {
        if (isDeviceActuallyConnected()) {
            return true;
        }

        try {
            // Initialize SDK
            if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init()) {
                return false;
            }

            // Check device count
            int deviceCount = FingerprintSensorEx.GetDeviceCount();
            if (deviceCount <= 0) {
                return false;
            }

            // Open device
            if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0))) {
                return false;
            }

            // Initialize database
            if (0 == (mhDB = FingerprintSensorEx.DBInit())) {
                FingerprintSensorEx.CloseDevice(mhDevice);
                mhDevice = 0;
                return false;
            }

            // Get basic device parameters
            fpWidth = 256; // Default
            fpHeight = 360; // Default
            
            imgbuf = new byte[fpWidth * fpHeight];
            mbStop = false;

            startSensorMonitoring();
            
            logMessage("✅ Device connected (quick mode)");
            Platform.runLater(() -> {
                updateButtonStates(true);
                updateStatus("Connected - Quick Mode");
            });
            return true;

        } catch (Exception e) {
            FreeSensor();
            return false;
        }
    }

    /**
     * REAL device connection - Original version (void return)
     */
    public void openDeviceOriginal() {
        if (mhDevice != 0) {
            logMessage("⚠️ Device is already connected!");
            return;
        }

        showProgress(true);
        logMessage("🔄 Initializing fingerprint sensor...");

        new Thread(() -> {
            try {
                int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
                cbRegTemp = 0;
                bRegister = false;
                bIdentify = false;
                iFid = 1;
                enroll_idx = 0;

                // Initialize SDK
                if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init()) {
                    Platform.runLater(() -> {
                        logMessage("❌ SDK Initialization failed!");
                        logMessage("💡 Check if ZKTeco drivers are installed");
                        showProgress(false);
                    });
                    return;
                }

                // Check device count
                ret = FingerprintSensorEx.GetDeviceCount();
                if (ret < 0) {
                    Platform.runLater(() -> {
                        logMessage("❌ No devices connected!");
                        logMessage("💡 Please connect a ZKTeco fingerprint scanner");
                        showProgress(false);
                        FreeSensor();
                    });
                    return;
                }

                logMessage("✅ Devices detected: " + ret);

                // Open device
                if (0 == (mhDevice = FingerprintSensorEx.OpenDevice(0))) {
                    Platform.runLater(() -> {
                        logMessage("❌ Failed to open device!");
                        logMessage("💡 Device might be in use by another application");
                        showProgress(false);
                        FreeSensor();
                    });
                    return;
                }

                // Initialize database
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

                // Get device parameters
                byte[] paramValue = new byte[4];
                int[] size = new int[1];
                size[0] = 4;
                
                // Get width
                if (FingerprintSensorEx.GetParameters(mhDevice, 1, paramValue, size) == 0) {
                    fpWidth = byteArrayToInt(paramValue);
                } else {
                    logMessage("⚠️ Could not get device width, using default: 256");
                    fpWidth = 256;
                }
                
                size[0] = 4;
                // Get height
                if (FingerprintSensorEx.GetParameters(mhDevice, 2, paramValue, size) == 0) {
                    fpHeight = byteArrayToInt(paramValue);
                } else {
                    logMessage("⚠️ Could not get device height, using default: 360");
                    fpHeight = 360;
                }

                logMessage("📏 Sensor resolution: " + fpWidth + "x" + fpHeight);

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
                    logMessage("❌ Error during device connection: " + ex.getMessage());
                    logMessage("💡 Check device drivers and connection");
                    showProgress(false);
                    FreeSensor();
                });
                ex.printStackTrace();
            }
        }).start();
    }

    // ========== EXISTING METHODS (Updated with better integration) ==========

    /**
     * Start REAL sensor monitoring
     */
    private void startSensorMonitoring() {
        sensorExecutor = Executors.newSingleThreadScheduledExecutor();
        sensorExecutor.scheduleAtFixedRate(() -> {
            if (mbStop || mhDevice == 0) return;

            try {
                templateLen[0] = 2048;
                int ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen);

                if (ret == 0) {
                    Platform.runLater(() -> {
                        onCaptureSuccess(imgbuf);
                        onExtractSuccess(template, templateLen[0]);
                    });
                }
            } catch (Exception e) {
                logMessage("⚠️ Sensor monitoring error: " + e.getMessage());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * Handle REAL fingerprint capture success
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
     * Handle REAL template extraction success - UPDATED
     */
    private void onExtractSuccess(byte[] template, int len) {
        // Notify single capture first
        if (onCaptureCallback != null) {
            onCaptureCallback.accept(template);
        }
        
        // Then handle normal operations
        if (bRegister) handleEnrollment(template);
        else if (bIdentify) handleIdentification(template);
        else handleVerification(template);
    }

    /**
     * Handle REAL enrollment process
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
     * Complete REAL enrollment
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
            } else {
                logMessage("❌ Database add failed: " + ret);
            }
        } else {
            logMessage("❌ Template merge failed: " + ret);
        }

        bRegister = false;
        enroll_idx = 0;
    }

    /**
     * Handle REAL identification
     */
    private void handleIdentification(byte[] template) {
        int[] fid = new int[1];
        int[] score = new int[1];
        int ret = FingerprintSensorEx.DBIdentify(mhDB, template, fid, score);

        if (ret == 0) {
            logMessage("✅ Identified! User ID: " + fid[0] + " | Score: " + score[0]);
            
            // Notify match callback if set
            if (onMatchCallback != null) {
                // Create a dummy employee for demonstration
                EmployeeSignatureModel matchedEmployee = new EmployeeSignatureModel();
                matchedEmployee.setEmployeeId("EMP" + fid[0]);
                matchedEmployee.setEmployeeName("User " + fid[0]);
                matchedEmployee.setDepartment("Identified Department");
                matchedEmployee.setPosition("Identified Position");
                onMatchCallback.accept(matchedEmployee);
            }
        } else {
            logMessage("❌ Identification failed: " + ret);
        }
    }

    /**
     * Handle REAL verification
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

    // ========== REQUIRED METHODS FOR EMPLOYEE SIGNATURE MANAGEMENT ==========

    /**
     * REAL fingerprint enrollment - Required by EmployeeSignatureManagement
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
     * Start enrollment with callback support
     */
    public void startEnrollment(Consumer<byte[]> onEnrollmentComplete) {
        if (mhDevice == 0) {
            logMessage("⚠️ Please connect device first!");
            if (onEnrollmentComplete != null) {
                onEnrollmentComplete.accept(null);
            }
            return;
        }
        
        bRegister = true;
        bIdentify = false;
        enroll_idx = 0;
        currentEnrollmentTemplate = null;
        
        // Set up enrollment completion callback
        if (onEnrollmentComplete != null) {
            // Store the callback and call it when enrollment completes
            new Thread(() -> {
                int initialFid = iFid;
                while (bRegister && mhDevice != 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                // Check if enrollment was successful
                if (!bRegister && currentEnrollmentTemplate != null) {
                    onEnrollmentComplete.accept(currentEnrollmentTemplate);
                } else {
                    onEnrollmentComplete.accept(null);
                }
            }).start();
        }
        
        logMessage("🎯 Enrollment started - Place your finger 3 times");
    }

    /**
     * REAL fingerprint verification - Required by EmployeeSignatureManagement
     */
    public void startVerification() {
        if (mhDevice == 0) {
            logMessage("⚠️ Please connect device first!");
            return;
        }
        bRegister = false;
        bIdentify = false;
        logMessage("🔍 Verification mode activated");
    }

    /**
     * REAL fingerprint identification - Required by EmployeeSignatureManagement
     */
    public void startIdentification() {
        if (mhDevice == 0) {
            logMessage("⚠️ Please connect device first!");
            return;
        }
        bRegister = false;
        bIdentify = true;
        logMessage("👤 Identification mode activated");
    }

    /**
     * Close device - Required by EmployeeSignatureManagement
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
     * Check if device is actually connected - Required by EmployeeSignatureManagement
     */
    public boolean isDeviceActuallyConnected() {
        return mhDevice != 0 && !mbStop;
    }

    /**
     * Enhanced device status check
     */
    public String getDeviceStatus() {
        if (mhDevice == 0) return "Disconnected";
        if (mbStop) return "Disconnected";
        if (mhDB == 0) return "Connected (No Database)";
        return "Connected & Ready";
    }

    /**
     * Get current enrollment template - Required by EmployeeSignatureManagement
     */
    public byte[] getCurrentEnrollmentTemplate() {
        return currentEnrollmentTemplate;
    }

    /**
     * Set current enrollment template - Required by EmployeeSignatureManagement
     */
    public void setCurrentEnrollmentTemplate(byte[] template) {
        this.currentEnrollmentTemplate = template;
    }

    /**
     * Get device handle for fingerprint operations - Required by EmployeeSignatureManagement
     */
    public long getDeviceHandle() {
        return mhDevice;
    }

    /**
     * Get database handle for fingerprint operations - Required by EmployeeSignatureManagement
     */
    public long getDatabaseHandle() {
        return mhDB;
    }

    /**
     * Get image buffer for fingerprint capture - Required by EmployeeSignatureManagement
     */
    public byte[] getImageBuffer() {
        return imgbuf;
    }

    /**
     * Check if database is initialized and ready - Required by EmployeeSignatureManagement
     */
    public boolean isDatabaseReady() {
        return mhDB != 0;
    }

    /**
     * Get the number of enrolled fingerprints - Required by EmployeeSignatureManagement
     */
    public int getEnrolledFingerprintCount() {
        if (mhDB == 0) return 0;
        return iFid - 1;
    }

    /**
     * Capture fingerprint template directly - NEW METHOD for EmployeeSignatureManagement
     */
    public byte[] captureFingerprintTemplate() {
        if (!isDeviceActuallyConnected()) {
            logMessage("⚠️ Device not connected for capture");
            return null;
        }

        try {
            templateLen[0] = 2048;
            int ret = FingerprintSensorEx.AcquireFingerprint(mhDevice, imgbuf, template, templateLen);
            
            if (ret == 0) {
                byte[] capturedTemplate = new byte[templateLen[0]];
                System.arraycopy(template, 0, capturedTemplate, 0, templateLen[0]);
                logMessage("✅ Fingerprint captured successfully (" + templateLen[0] + " bytes)");
                return capturedTemplate;
            } else {
                logMessage("❌ Fingerprint capture failed: " + ret);
                return null;
            }
        } catch (Exception e) {
            logMessage("❌ Error capturing fingerprint: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verify fingerprint against stored template - NEW METHOD for EmployeeSignatureManagement
     */
    public boolean verifyFingerprint(byte[] storedTemplate, byte[] capturedTemplate) {
        if (!isDeviceActuallyConnected() || storedTemplate == null || capturedTemplate == null) {
            logMessage("⚠️ Invalid parameters for verification");
            return false;
        }

        try {
            int matchScore = FingerprintSensorEx.DBMatch(mhDB, storedTemplate, capturedTemplate);
            final int MATCH_THRESHOLD = 60; // Adjust based on requirements
            boolean isMatch = matchScore >= MATCH_THRESHOLD;
            
            logMessage("🔍 Verification score: " + matchScore + " | Match: " + isMatch);
            return isMatch;
        } catch (Exception e) {
            logMessage("❌ Error during verification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Quick template verification
     */
    public boolean quickVerify(byte[] storedTemplate, byte[] capturedTemplate) {
        if (!isDeviceActuallyConnected() || storedTemplate == null || capturedTemplate == null) {
            return false;
        }

        try {
            int matchScore = FingerprintSensorEx.DBMatch(mhDB, storedTemplate, capturedTemplate);
            return matchScore >= 50; // Lower threshold for quick verification
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Identify fingerprint in database - NEW METHOD for EmployeeSignatureManagement
     */
    public int identifyFingerprint(byte[] capturedTemplate) {
        if (!isDeviceActuallyConnected() || capturedTemplate == null) {
            logMessage("⚠️ Invalid parameters for identification");
            return -1;
        }

        try {
            int[] fid = new int[1];
            int[] score = new int[1];
            int ret = FingerprintSensorEx.DBIdentify(mhDB, capturedTemplate, fid, score);
            
            if (ret == 0) {
                logMessage("✅ Identified user ID: " + fid[0] + " | Score: " + score[0]);
                return fid[0];
            } else {
                logMessage("❌ Identification failed: " + ret);
                return -1;
            }
        } catch (Exception e) {
            logMessage("❌ Error during identification: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Add template to database - NEW METHOD for EmployeeSignatureManagement
     */
    public boolean addTemplateToDatabase(int userId, byte[] template) {
        if (!isDeviceActuallyConnected() || template == null) {
            logMessage("⚠️ Invalid parameters for adding template");
            return false;
        }

        try {
            int ret = FingerprintSensorEx.DBAdd(mhDB, userId, template);
            if (ret == 0) {
                logMessage("✅ Template added for user ID: " + userId);
                return true;
            } else {
                logMessage("❌ Failed to add template: " + ret);
                return false;
            }
        } catch (Exception e) {
            logMessage("❌ Error adding template: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove template from database - NEW METHOD for EmployeeSignatureManagement
     */
    public boolean removeTemplateFromDatabase(int userId) {
        if (!isDeviceActuallyConnected()) {
            logMessage("⚠️ Device not connected");
            return false;
        }

        try {
            int ret = FingerprintSensorEx.DBDel(mhDB, userId);
            if (ret == 0) {
                logMessage("✅ Template removed for user ID: " + userId);
                return true;
            } else {
                logMessage("❌ Failed to remove template: " + ret);
                return false;
            }
        } catch (Exception e) {
            logMessage("❌ Error removing template: " + e.getMessage());
            return false;
        }
    }

    /**
     * Set callback for fingerprint capture - NEW METHOD for EmployeeSignatureManagement
     */
    public void setOnFingerprintCapture(Consumer<byte[]> callback) {
        this.onCaptureCallback = callback;
    }

    /**
     * Set callback for fingerprint match - NEW METHOD for EmployeeSignatureManagement
     */
    public void setOnFingerprintMatch(Consumer<EmployeeSignatureModel> callback) {
        this.onMatchCallback = callback;
    }

    /**
     * Clear all templates from database - NEW METHOD for EmployeeSignatureManagement
     */
    public boolean clearDatabase() {
        if (!isDeviceActuallyConnected()) {
            logMessage("⚠️ Device not connected");
            return false;
        }

        try {
            int ret = FingerprintSensorEx.DBClear(mhDB);
            if (ret == 0) {
                iFid = 1; // Reset ID counter
                logMessage("✅ Database cleared successfully");
                return true;
            } else {
                logMessage("❌ Failed to clear database: " + ret);
                return false;
            }
        } catch (Exception e) {
            logMessage("❌ Error clearing database: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get device information - NEW METHOD for EmployeeSignatureManagement
     */
    public String getDeviceInfo() {
        if (!isDeviceActuallyConnected()) {
            return "Device not connected";
        }

        try {
            StringBuilder info = new StringBuilder();
            info.append("Device Handle: ").append(mhDevice).append("\n");
            info.append("Database Handle: ").append(mhDB).append("\n");
            info.append("Resolution: ").append(fpWidth).append("x").append(fpHeight).append("\n");
            info.append("Enrolled Users: ").append(getEnrolledFingerprintCount()).append("\n");
            info.append("Template Format: ").append(radioISO.isSelected() ? "ISO" : "ANSI").append("\n");
            
            return info.toString();
        } catch (Exception e) {
            return "Error getting device info: " + e.getMessage();
        }
    }

    /**
     * Free sensor resources - Required by EmployeeSignatureManagement
     */
    private void FreeSensor() {
        mbStop = true;
        if (sensorExecutor != null) {
            sensorExecutor.shutdown();
            try {
                if (!sensorExecutor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    sensorExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                sensorExecutor.shutdownNow();
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

    // ========== UI METHODS ==========

    private void initializeUI() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #3498db);");
        setCenter(createCenterContent());
        setBottom(createFooter());
    }

    private HBox createCenterContent() {
        HBox centerContent = new HBox(20);
        centerContent.setPadding(new Insets(20));
        centerContent.getChildren().addAll(createControlPanel(), createImagePanel());
        return centerContent;
    }

    private VBox createControlPanel() {
        VBox controlPanel = new VBox(15);
        controlPanel.setPrefWidth(350);
        controlPanel.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 10; -fx-padding: 20;");

        Label controlTitle = new Label("Device Controls");
        controlTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        controlTitle.setTextFill(Color.DARKBLUE);

        btnOpen = createStyledButton("Connect REAL Device", "#27ae60");
        btnClose = createStyledButton("Disconnect", "#e74c3c");
        HBox deviceControls = new HBox(10, btnOpen, btnClose);

        Label opTitle = new Label("Operations");
        opTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        btnEnroll = createStyledButton("Enroll Fingerprint", "#2980b9");
        btnVerify = createStyledButton("Verify", "#f39c12");
        btnIdentify = createStyledButton("Identify", "#8e44ad");
        
        // Add test capture button
        Button btnTestCapture = createStyledButton("Test Capture", "#16a085");
        btnTestCapture.setOnAction(e -> testCapture());

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
                opTitle, btnEnroll, btnVerify, btnIdentify, btnTestCapture,
                new Separator(),
                formatLabel, formatBox, progressIndicator
        );

        return controlPanel;
    }

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

    private void testCapture() {
        new Thread(() -> {
            logMessage("🧪 Testing capture method...");
            byte[] template = captureFingerprint(10); // 10 second timeout
            if (template != null) {
                logMessage("✅ Test capture successful! Template size: " + template.length + " bytes");
            } else {
                logMessage("❌ Test capture failed");
            }
        }).start();
    }

    private Image createPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 400; x++)
            for (int y = 0; y < 300; y++)
                placeholder.setRGB(x, y, 0xFFF0F0F0);
        return SwingFXUtils.toFXImage(placeholder, null);
    }

    private void setupEventHandlers() {
        btnOpen.setOnAction(e -> openDevice());
        btnClose.setOnAction(e -> closeDevice());
        btnEnroll.setOnAction(e -> startEnrollment());
        btnVerify.setOnAction(e -> startVerification());
        btnIdentify.setOnAction(e -> startIdentification());
    }

    private void updateButtonStates(boolean connected) {
        btnOpen.setDisable(connected);
        btnClose.setDisable(!connected);
        btnEnroll.setDisable(!connected);
        btnVerify.setDisable(!connected);
        btnIdentify.setDisable(!connected);
    }

    private void showProgress(boolean show) {
        progressIndicator.setVisible(show);
        progressIndicator.setProgress(show ? ProgressIndicator.INDETERMINATE_PROGRESS : 0);
    }

    private void updateStatus(String message) {
        statusLabel.setText("Status: " + message);
    }

    private void logMessage(String message) {
        Platform.runLater(() -> {
            logTextArea.appendText(message + "\n");
            logTextArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    // ========== UTILITY METHODS ==========

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

    public static byte[] changeByte(int data) {
        return intToByteArray(data);
    }

    public static byte[] intToByteArray(final int number) {
        byte[] abyte = new byte[4];
        abyte[0] = (byte) (0xff & number);
        abyte[1] = (byte) ((0xff00 & number) >> 8);
        abyte[2] = (byte) ((0xff0000 & number) >> 16);
        abyte[3] = (byte) ((0xff000000 & number) >> 24);
        return abyte;
    }

    public static int byteArrayToInt(byte[] bytes) {
        int number = bytes[0] & 0xFF;
        number |= ((bytes[1] << 8) & 0xFF00);
        number |= ((bytes[2] << 16) & 0xFF0000);
        number |= ((bytes[3] << 24) & 0xFF000000);
        return number;
    }

    public void handleCloseRequest() {
        closeDevice();
    }

    public void setCurrentEnrollmentUsername(String username) {
        this.currentEnrollmentUsername = username;
    }
    
    /**
 * Compare two fingerprint templates using ZKTECO SDK's DBMatch function
 * This is the CORRECT way to compare fingerprint templates
 * 
 * @param template1 First fingerprint template (stored template from database)
 * @param template2 Second fingerprint template (newly captured template)
 * @return true if templates match, false otherwise
 */
public boolean compareTemplate(byte[] template1, byte[] template2) {
    try {
        // Validate input parameters
        if (template1 == null || template2 == null) {
            logMessage("❌ Template comparison failed: One or both templates are null");
            return false;
        }
        
        if (template1.length == 0 || template2.length == 0) {
            logMessage("❌ Template comparison failed: One or both templates are empty");
            return false;
        }
        
        if (!isDeviceActuallyConnected()) {
            logMessage("❌ Template comparison failed: Device not connected");
            return false;
        }
        
        if (mhDB == 0) {
            logMessage("❌ Template comparison failed: Database not initialized");
            return false;
        }
        
        logMessage("🔍 Comparing templates...");
        logMessage("   Template 1 size: " + template1.length + " bytes");
        logMessage("   Template 2 size: " + template2.length + " bytes");
        
        // Use ZKTECO SDK's DBMatch function for proper template comparison
        int matchScore = FingerprintSensorEx.DBMatch(mhDB, template1, template2);
        
        logMessage("📊 Template match score: " + matchScore);
        
        // Determine match threshold (adjust based on your security requirements)
        final int MATCH_THRESHOLD = 50; // You can adjust this value (0-100)
        
        boolean isMatch = matchScore >= MATCH_THRESHOLD;
        
        if (isMatch) {
            logMessage("✅ Template comparison: MATCH (Score: " + matchScore + " >= " + MATCH_THRESHOLD + ")");
        } else {
            logMessage("❌ Template comparison: NO MATCH (Score: " + matchScore + " < " + MATCH_THRESHOLD + ")");
        }
        
        return isMatch;
        
    } catch (Exception e) {
        logMessage("❌ Error during template comparison: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
}