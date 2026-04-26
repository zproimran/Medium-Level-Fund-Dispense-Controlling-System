package smarthrms;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class FingerprintUtil {
    private Connecting connects;
    
    public FingerprintUtil() {
        connects = new Connecting();
    }
    
    // Authenticate with binary fingerprint data
    public Map<String, String> authenticateByFingerprint(byte[] fingerprintData) {
        Map<String, String> userData = new HashMap<>();
        
        try {
            if (fingerprintData != null && fingerprintData.length > 0) {
                String sql = "SELECT username, password, role, status FROM members WHERE fingerprint_data = ?";
                PreparedStatement pst = connects.con.prepareStatement(sql);
                pst.setBytes(1, fingerprintData); // Binary comparison
                ResultSet rs = pst.executeQuery();
                
                if (rs.next()) {
                    userData.put("username", rs.getString("username"));
                    userData.put("password", rs.getString("password"));
                    userData.put("role", rs.getString("role"));
                    userData.put("status", rs.getString("status"));
                    System.out.println("User found: " + rs.getString("username"));
                } else {
                    System.out.println("No user found with this fingerprint");
                }
                
                rs.close();
                pst.close();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return userData;
    }
    
    // Method to enroll fingerprint with binary data
    public boolean enrollFingerprint(String username, byte[] fingerprintData) {
        try {
            String sql = "UPDATE members SET fingerprint_data = ? WHERE username = ?";
            PreparedStatement pst = connects.con.prepareStatement(sql);
            pst.setBytes(1, fingerprintData); // Store as binary
            pst.setString(2, username);
            
            int rowsAffected = pst.executeUpdate();
            pst.close();
            
            System.out.println("Fingerprint enrolled for " + username + ", binary data size: " + 
                             (fingerprintData != null ? fingerprintData.length : 0) + " bytes");
            
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Method to check if a user has fingerprint enrolled
    public boolean hasFingerprintEnrolled(String username) {
        try {
            String sql = "SELECT fingerprint_data FROM members WHERE username = ? AND fingerprint_data IS NOT NULL";
            PreparedStatement pst = connects.con.prepareStatement(sql);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            
            boolean hasFingerprint = rs.next();
            
            if (hasFingerprint) {
                byte[] data = rs.getBytes("fingerprint_data");
                System.out.println("User " + username + " has fingerprint data: " + 
                                 (data != null ? data.length + " bytes" : "null"));
            }
            
            rs.close();
            pst.close();
            
            return hasFingerprint;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get fingerprint data for a user
    public byte[] getFingerprintData(String username) {
        try {
            String sql = "SELECT fingerprint_data FROM members WHERE username = ?";
            PreparedStatement pst = connects.con.prepareStatement(sql);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getBytes("fingerprint_data");
            }
            
            rs.close();
            pst.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public void close() {
        try {
            if (connects.con != null) {
                connects.con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}