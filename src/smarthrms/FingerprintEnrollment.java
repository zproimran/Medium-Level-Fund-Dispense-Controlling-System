package smarthrms;

import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;
import javax.swing.*;
import java.util.Base64;

public class FingerprintEnrollment {
    
    public static void main(String[] args) {
        // Check device first
        if (!checkFingerprintDeviceAvailable()) {
            JOptionPane.showMessageDialog(null,
                "Cannot start fingerprint enrollment.\n\n" +
                "Fingerprint device is not detected.\n" +
                "Please connect your fingerprint scanner and try again.\n\n" +
                "Returning to main menu...",
                "Device Required",
                JOptionPane.ERROR_MESSAGE);
            
            // Return to main menu
            SwingUtilities.invokeLater(() -> new LoginLauncher().setVisible(true));
            return;
        }
        
        FingerprintUtil fingerprintUtil = new FingerprintUtil();
        
        try {
            while (true) {
                String[] options = {
                    "Enroll New Fingerprint", 
                    "Check Existing Enrollment", 
                    "Delete Fingerprint", 
                    "Check Device Status",
                    "Back to Main Menu"
                };
                
                int choice = JOptionPane.showOptionDialog(null,
                    "Fingerprint Enrollment System\nBinary Data Storage\n\n" +
                    "🟢 Device Status: Connected",
                    "AGH Vaccine Management - Biometric System",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);
                
                switch (choice) {
                    case 0:
                        enrollFingerprint(fingerprintUtil);
                        break;
                    case 1:
                        checkEnrollment(fingerprintUtil);
                        break;
                    case 2:
                        deleteFingerprint(fingerprintUtil);
                        break;
                    case 3:
                        checkDeviceStatus();
                        break;
                    case 4:
                    default:
                        fingerprintUtil.close();
                        SwingUtilities.invokeLater(() -> new LoginLauncher().setVisible(true));
                        return;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            fingerprintUtil.close();
        }
    }
    
    private static boolean checkFingerprintDeviceAvailable() {
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
    
    private static void checkDeviceStatus() {
        if (checkFingerprintDeviceAvailable()) {
            JOptionPane.showMessageDialog(null,
                "✅ Fingerprint Device Status:\n\n" +
                "• Device: Connected\n" +
                "• Status: Ready for enrollment\n" +
                "• Library: ZKTeco SDK Loaded\n\n" +
                "You can proceed with fingerprint enrollment.",
                "Device Status - Connected",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                "❌ Fingerprint Device Status:\n\n" +
                "• Device: Not Connected\n" +
                "• Status: Cannot perform enrollment\n" +
                "• Library: Initialization failed\n\n" +
                "Please check device connection and drivers.",
                "Device Status - Not Connected",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void enrollFingerprint(FingerprintUtil fingerprintUtil) {
        String username = JOptionPane.showInputDialog("Enter username to enroll:");
        if (username == null || username.trim().isEmpty()) return;
        
        // Check if user exists in database
        Connecting conn = new Connecting();
        if (!conn.usernameExists(username)) {
            JOptionPane.showMessageDialog(null, 
                "❌ User '" + username + "' not found in database.\nPlease register the user first.",
                "User Not Found", JOptionPane.ERROR_MESSAGE);
            conn.close();
            return;
        }
        conn.close();
        
        // Check if user already has fingerprint
        if (fingerprintUtil.hasFingerprintEnrolled(username)) {
            int overwrite = JOptionPane.showConfirmDialog(null,
                "User '" + username + "' already has a fingerprint enrolled.\n" +
                "Do you want to overwrite it?",
                "Fingerprint Exists",
                JOptionPane.YES_NO_OPTION);
            
            if (overwrite != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        JOptionPane.showMessageDialog(null, 
            "Fingerprint Enrollment for: " + username + 
            "\n\nPlease place your finger on the scanner when prompted.\n" +
            "Binary data will be stored in database.");
        
        // Simulate fingerprint capture
        int option = JOptionPane.showConfirmDialog(null, 
            "Fingerprint Enrollment Simulation\n\n" +
            "Click 'Yes' to enroll with binary fingerprint data\n" +
            "Click 'No' to cancel", 
            "Fingerprint Scan", 
            JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            // Generate binary fingerprint template
            byte[] fingerprintBytes = generateBinaryFingerprintTemplate();
            
            if (fingerprintBytes != null) {
                boolean success = fingerprintUtil.enrollFingerprint(username, fingerprintBytes);
                
                if (success) {
                    JOptionPane.showMessageDialog(null, 
                        "✅ Fingerprint enrolled successfully!\n" +
                        "User: " + username + "\n" +
                        "Binary Data Size: " + fingerprintBytes.length + " bytes\n" +
                        "Storage: BLOB format in database");
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "❌ Failed to enroll fingerprint.\nUser may not exist in database.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "❌ Failed to generate fingerprint data.");
            }
        }
    }
    
    private static byte[] generateBinaryFingerprintTemplate() {
        // Generate realistic binary fingerprint template (400-600 bytes typical)
        int templateSize = 400 + (int)(Math.random() * 200);
        byte[] template = new byte[templateSize];
        
        // Generate realistic fingerprint template data
        for (int i = 0; i < templateSize; i++) {
            if (i < 8) {
                // Header bytes
                template[i] = (byte) 0x46; // 'F' for fingerprint
            } else if (i < 16) {
                // Template info
                template[i] = (byte) (i * 7);
            } else {
                // Random template data (minutiae points, etc.)
                template[i] = (byte) (Math.random() * 256);
            }
        }
        
        System.out.println("Generated binary fingerprint template: " + template.length + " bytes");
        return template;
    }
    
    private static void checkEnrollment(FingerprintUtil fingerprintUtil) {
        String username = JOptionPane.showInputDialog("Enter username to check:");
        if (username == null || username.trim().isEmpty()) return;
        
        boolean hasFingerprint = fingerprintUtil.hasFingerprintEnrolled(username);
        
        if (hasFingerprint) {
            byte[] template = fingerprintUtil.getFingerprintData(username);
            JOptionPane.showMessageDialog(null, 
                "✅ User '" + username + "' has fingerprint enrolled.\n" +
                "Binary Data Size: " + (template != null ? template.length + " bytes" : "Unknown"));
        } else {
            JOptionPane.showMessageDialog(null, 
                "❌ User '" + username + "' does not have fingerprint enrolled.");
        }
    }
    
    private static void deleteFingerprint(FingerprintUtil fingerprintUtil) {
        String username = JOptionPane.showInputDialog("Enter username to delete fingerprint:");
        if (username == null || username.trim().isEmpty()) return;
        
        int confirm = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to delete fingerprint for user '" + username + "'?\n" +
            "This will remove the binary fingerprint data from database.",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Delete by setting fingerprint_data to NULL
            try {
                Connecting connects = new Connecting();
                String sql = "UPDATE members SET fingerprint_data = NULL WHERE username = ?";
                java.sql.PreparedStatement pst = connects.con.prepareStatement(sql);
                pst.setString(1, username);
                int rowsAffected = pst.executeUpdate();
                pst.close();
                connects.con.close();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, 
                        "✅ Fingerprint deleted successfully.\n" +
                        "Binary data removed from database.");
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "❌ User not found or no fingerprint to delete.");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "❌ Error deleting fingerprint: " + e.getMessage());
            }
        }
    }
}