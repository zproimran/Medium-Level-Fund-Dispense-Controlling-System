 package smarthrms;

import com.zkteco.biometric.FingerprintSensorEx;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.DriverManager;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Connecting {

   public Statement st;
    Connection con;
        // Fingerprint module
    private ZKTECO fingerprintModule;

    // Define constants for Success and Failure
    public static final int SUCCESS = 0;
    public static final int FAILURE = -1;

    public Connecting() {
         fingerprintModule=new ZKTECO();
        try {
            // Try to load the MySQL JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Attempt to establish a connection to the database
            con = DriverManager.getConnection("jdbc:sqlserver://DESKTOP-5GCFCQE:1433;databaseName=hrms;trustServerCertificate=true;encrypt=true","javadeveloper","developer");
            
          // con = DriverManager.getConnection("jdbc:sqlserver://DESKTOP-C0944KR:1433;databaseName=hrms;trustServerCertificate=true;encrypt=true","kebnar","@#kebnar@#");
            
           
            // If the connection is successful, create a Statement object
            st = con.createStatement();
            
            // Show a success message if connection is successful
            showMessage(SUCCESS);
        } catch (Exception e) {
            // If there is an error (e.g., connection failure), show a failure message
            // Show a success message if connection is successful
            showMessage(FAILURE);
            showMessage(FAILURE, e.getMessage());
        }
       
    }

    // Helper method to show success or failure message
    private void showMessage(int status) {
        if (status == SUCCESS) {
         //  JOptionPane.showMessageDialog(null, "Sql Connected Successfully!");
        } else if (status == FAILURE) {
          JOptionPane.showMessageDialog(null, "Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Overloaded method to handle error messages in case of failure
    private void showMessage(int status, String errorMessage) {
        if (status == FAILURE) {
           JOptionPane.showMessageDialog(null, "Connection Failed: " + errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Calling the Conn constructor to establish the connection
        new Connecting();  // This will try to connect to the database
    }  
                    
                        // Check if a user exists by ID
    private boolean userExists(int userId) {
        String query = "SELECT COUNT(*) FROM members WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }  
     // Activate a user by setting their status to "active"
    public boolean activateUser(int userId) {
        String query = "UPDATE members SET status = 'Active' WHERE id = ?";
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;  // Return true if the user is successfully activated
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public String getFullNameByUsername(String username){
    String fullName = null;
    String  query ="SELECT full_name FROM members WHERE username = ?";
    try(PreparedStatement stmt = con.prepareStatement(query)){
    stmt.setString(1, username);
    ResultSet rs = stmt.executeQuery();
    if(rs.next()){
    fullName = rs.getString("full_name");
    }
    }
    catch(SQLException e){
    e.printStackTrace();
    }
    return fullName;
    }
    // Deactivate a user by setting their status to "inactive"
    public boolean deactivateUser(int userId) {
        String query = "UPDATE members SET status = 'Inactive' WHERE id = ?";
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;  // Return true if the user is successfully deactivated
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Delete a user from the database by their user ID
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM members WHERE id = ?";
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            int rowsDeleted = ps.executeUpdate();
            return rowsDeleted > 0;  // Return true if the user is successfully deleted
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Update a user's details (e.g., full name, email, role, etc.)
    public boolean updateUser(int userId, String fullName, String email, String role,String dept) {
        String query = "UPDATE members SET full_name = ?, email = ?, role = ?, department = ? WHERE id = ?";
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, role);
            ps.setString(4, dept);
            ps.setInt(5, userId);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;  // Return true if the user's details are successfully updated
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }   
        // Fetch all users from the database
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username, full_name, email, role,department, status FROM members";
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String role = rs.getString("role");
                String status = rs.getString("status");
                String depart=rs.getString("department");
                users.add(new User(id, username, fullName, email, role, status,depart));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
 public String[] getDepartmentsFromDatabase() {
    List<String> departments = new ArrayList<>();
    String sql = "SELECT department_name FROM department";
    
    try (PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        
        while (rs.next()) { // Iterate through all rows
            departments.add(rs.getString("department_name"));
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    // Return the departments as an array, or a default array if empty
    return departments.isEmpty() ? new String[]{"Unknown"} : departments.toArray(new String[0]);
}

 
 // Insert user with binary fingerprint data
    public boolean insertUser(String fullName, String email, String role, String department, 
                            String username, String password, byte[] fingerprintData) {
        try {
            String sql = "INSERT INTO members (full_name, email, role, department, username, password, fingerprint_data) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, fullName);
            pst.setString(2, email);
            pst.setString(3, role);
            pst.setString(4, department);
            pst.setString(5, username);
            pst.setString(6, password);
            
            if (fingerprintData != null && fingerprintData.length > 0) {
                pst.setBytes(7, fingerprintData); // Store as binary BLOB
            } else {
                pst.setNull(7, java.sql.Types.BLOB);
            }
            
            int rowsAffected = pst.executeUpdate();
            pst.close();
            
            System.out.println("User registered: " + username + ", Fingerprint: " + 
                             (fingerprintData != null ? fingerprintData.length + " bytes" : "None"));
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.out.println("Username already exists: " + username);
            } else {
                e.printStackTrace();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
   // In Connecting.java - Add this method
public String authenticateWithFingerprintTemplate(byte[] fingerprintData) {
    try {
        // Get all users with fingerprints for comparison
        String sql = "SELECT username, role, status, fingerprint_data FROM members WHERE fingerprint_data IS NOT NULL AND status = 'Active'";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        
        String matchedUser = null;
        
        while (rs.next()) {
            byte[] storedTemplate = rs.getBytes("fingerprint_data");
            
            // Compare the templates - you might need to adjust the similarity threshold
            if (storedTemplate != null && compareFingerprintTemplates(fingerprintData, storedTemplate)) {
                matchedUser = rs.getString("username") + "|" + rs.getString("role");
                break;
            }
        }
        
        rs.close();
        pst.close();
        return matchedUser;
        
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

// Simple template comparison - you might need to use ZKTeco's matching function
private boolean compareFingerprintTemplates(byte[] template1, byte[] template2) {
    if (template1 == null || template2 == null) return false;
    if (template1.length != template2.length) return false;
    
    // Simple byte-by-byte comparison for exact match
    // In production, you should use ZKTeco's DBMatch function
    for (int i = 0; i < template1.length; i++) {
        if (template1[i] != template2[i]) {
            return false;
        }
    }
    return true;
}
    
    // Check if username exists
    public boolean usernameExists(String username) {
        try {
            String sql = "SELECT id FROM members WHERE username = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            boolean exists = rs.next();
            rs.close();
            pst.close();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get user by fingerprint (for real-time authentication)
    public String getUserByFingerprint(byte[] fingerprintData) {
        try {
            String sql = "SELECT username, role FROM members WHERE fingerprint_data = ? AND status = 'Active'";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setBytes(1, fingerprintData);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String username = rs.getString("username");
                String role = rs.getString("role");
                rs.close();
                pst.close();
                return username + "|" + role;
            }
            
            rs.close();
            pst.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Close connection
    public void close() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
 
 

 
  public boolean insertUser(String fullName, String email, String role, String department, 
                         String username, String password, String fingerprintBase64) {
    try {
        
        String sql = "INSERT INTO members (full_name, email, role, department, username, password, fingerprint_template) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setString(1, fullName);
        pst.setString(2, email);
        pst.setString(3, role);
        pst.setString(4, department);
        pst.setString(5, username);
        pst.setString(6, password); // Make sure to hash this in production!
        
        // Handle fingerprint data - either as BLOB or TEXT
        if (fingerprintBase64 != null && !fingerprintBase64.isEmpty()) {
            // Option 1: Store as TEXT (Base64 string)
            pst.setString(7, fingerprintBase64);
            
            // Option 2: Or store as BLOB (convert back to bytes)
            // byte[] fingerprintBytes = java.util.Base64.getDecoder().decode(fingerprintBase64);
            // pst.setBytes(7, fingerprintBytes);
        } else {
            pst.setNull(7, java.sql.Types.VARCHAR);
        }
        
        int rowsAffected = pst.executeUpdate();
        pst.close();
        con.close();
        
        return rowsAffected > 0;
        
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


       public boolean resetPassword(String username, String newPassword) {
        String updateQuery = "UPDATE members SET password = ? WHERE username = ?";
        try (PreparedStatement preparedStatement = con.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
        public void insertDepartmentToDB(DepartmentNameModel department) {
    String deptName = department.getDeptName().trim().toLowerCase();  // Convert to lowercase for case-insensitive comparison
    
    // Check if department already exists (case-insensitive)
    String checkSql = "SELECT COUNT(*) FROM department WHERE LOWER(department_name) = ?";
    try {
        java.sql.PreparedStatement checkStmt = con.prepareStatement(checkSql);
        checkStmt.setString(1, deptName);
        java.sql.ResultSet rs = checkStmt.executeQuery();
        rs.next();
        
        // If department exists, show error message
        if (rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(null, "Department name already exists. Please choose another one.", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
        } else {
            // Insert new department if it doesn't exist
            String sql = "INSERT INTO department (department_name) VALUES (?)";
            java.sql.PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, department.getDeptName());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Department successfully added!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error inserting data: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
    }
}

public void insertRoleToDB(RoleNameModel role) {
    String roleName = role.getRoleName().trim().toLowerCase();  // Convert to lowercase for case-insensitive comparison
    
    // Check if the role already exists (case-insensitive)
    String checkSql = "SELECT COUNT(*) FROM role WHERE LOWER(role_name) = ?";
    try {
        java.sql.PreparedStatement checkStmt = con.prepareStatement(checkSql);
        checkStmt.setString(1, roleName);
        java.sql.ResultSet rs = checkStmt.executeQuery();
        rs.next();
        
        // If role exists, show error message
        if (rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(null, "Role name already exists. Please choose another one.", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
        } else {
            // Insert new role if it doesn't exist
            String sql = "INSERT INTO role (role_name) VALUES (?)";
            java.sql.PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, role.getRoleName());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Role successfully added!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error inserting data: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
    }
}

      // Fetch all users from the database
    public List<DepartmentNameModel> getAllDepartments() {
        List<DepartmentNameModel> allRequests = new ArrayList<>();
        String query = "SELECT department_name FROM department";
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String deptname = rs.getString("department_name");
                allRequests.add(new DepartmentNameModel(deptname));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allRequests;
    }
    
     // Delete a user from the database by their user ID
    public boolean deleteDept(String deptName) {
        String query = "DELETE FROM department WHERE department_name = ?";
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, deptName);
            int rowsDeleted = ps.executeUpdate();
            return rowsDeleted > 0;  // Return true if the user is successfully deleted
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update a user's details (e.g., full name, email, role, etc.)
    public boolean updateDept(String deptName,String dept) {
        String query = "UPDATE department SET department_name = ? WHERE department_name = ?";
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, dept);
            ps.setString(2, deptName);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;  // Return true if the user's details are successfully updated
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }   

     public boolean updateRole(String roleName,String role) {
        String query = "UPDATE role SET role_name = ? WHERE role_name = ?";
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, role);
            ps.setString(2, roleName);
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;  // Return true if the user's details are successfully updated
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    } 

     public boolean deleteRole(String roleName) {
        String query = "DELETE FROM role WHERE role_name = ?";
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, roleName);
            int rowsDeleted = ps.executeUpdate();
            return rowsDeleted > 0;  // Return true if the user is successfully deleted
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<RoleNameModel> getAllRoles() {
        List<RoleNameModel> allRequests = new ArrayList<>();
        String query = "SELECT role_name FROM role";
        
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String roleName = rs.getString("role_name");
                allRequests.add(new RoleNameModel(roleName));
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return allRequests;
    }
    
    
    public ObservableList<DispensedPettyCashModel> searchByFingerprint(byte[] fingerprintTemplate) {
        ObservableList<DispensedPettyCashModel> results = FXCollections.observableArrayList();
        
        try {
            
            String sql = "SELECT * FROM dispensed_petty_cash WHERE fingerprint_template IS NOT NULL";
            
            PreparedStatement pstmt = con.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                byte[] storedTemplate = rs.getBytes("fingerprint_template");
                
                // Simple binary comparison (in real scenario, use proper fingerprint matching algorithm)
                if (storedTemplate != null && Arrays.equals(fingerprintTemplate, storedTemplate)) {
                    DispensedPettyCashModel record = new DispensedPettyCashModel(
                        rs.getString("request_id"),
                        rs.getString("requisition_unit"),
                        rs.getString("reason"),
                        rs.getString("payee"),
                        rs.getString("requested_amount"),
                        rs.getString("given_amount"),
                        rs.getString("given_by"),
                        rs.getDate("request_date").toLocalDate(),
                        rs.getDate("completed_date").toLocalDate(),
                        storedTemplate,
                        rs.getString("signature_status")
                    );
                    results.add(record);
                }
            }
            
            rs.close();
            pstmt.close();
            con.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return results;
    }
  
    
     // Create employee
    public boolean createEmployee(EmployeeSignatureModel employee) {
        String sql = "INSERT INTO employee_signatures (employee_id, employee_name, department, position, enrollment_date, status, fingerprint_template, signature_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, employee.getEmployeeId());
            stmt.setString(2, employee.getEmployeeName());
            stmt.setString(3, employee.getDepartment());
            stmt.setString(4, employee.getPosition());
            stmt.setDate(5, Date.valueOf(employee.getEnrollmentDate()));
            stmt.setString(6, employee.getStatus());
            
            if (employee.getFingerprintTemplate() != null && employee.getFingerprintTemplate().length > 0) {
                stmt.setBytes(7, employee.getFingerprintTemplate());
            } else {
                stmt.setNull(7, Types.VARBINARY);
            }
            
            if (employee.getSignatureImage() != null && employee.getSignatureImage().length > 0) {
                stmt.setBytes(8, employee.getSignatureImage());
            } else {
                stmt.setNull(8, Types.VARBINARY);
            }
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating employee: " + e.getMessage());
            return false;
        }
    }

    // Read all employees
    public List<EmployeeSignatureModel> getAllEmployees() {
        List<EmployeeSignatureModel> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee_signatures ORDER BY employee_id";
        
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                EmployeeSignatureModel employee = new EmployeeSignatureModel(
                    rs.getString("employee_id"),
                    rs.getString("employee_name"),
                    rs.getString("department"),
                    rs.getString("position"),
                    rs.getDate("enrollment_date").toLocalDate(),
                    rs.getString("status"),
                    rs.getBytes("fingerprint_template"),
                    rs.getBytes("signature_image")
                );
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
        }
        
        return employees;
    }
    
    public Image getEmployeeSignatureAsFXImage(String employeeName) {
    if (employeeName == null || employeeName.trim().isEmpty()) {
        return null;
    }

    String sql = "SELECT signature_image FROM employee_signatures WHERE employee_name = ?";
    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
        pstmt.setString(1, employeeName);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                byte[] imageBytes = rs.getBytes("signature_image");
                if (imageBytes != null && imageBytes.length > 0) {
                    return new Image(new ByteArrayInputStream(imageBytes));
                }
            }
        }
    } catch (SQLException e) {
        System.err.println("Error fetching signature for " + employeeName + ": " + e.getMessage());
    }

    // Return null if no signature found
    return null;
}

    
 public List<String> getEmployeeSignatureNames() {
    List<String> employeeNames = new ArrayList<>();
    String sql = "SELECT employee_name FROM employee_signatures WHERE status = 'Active' ORDER BY employee_name";
    
    try (Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            employeeNames.add(rs.getString("employee_name"));
        }
    } catch (SQLException e) {
        System.err.println("Error fetching active employee names: " + e.getMessage());
    }
    
    return employeeNames;
}
 
 public List<String> getEmployeeSignatureNames(String username) {
    List<String> employeeNames = new ArrayList<>();
    String sql = "SELECT employee_name FROM employee_signatures WHERE status = 'Active' AND username = ? ORDER BY employee_name";
    
    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
        pstmt.setString(1, username);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                employeeNames.add(rs.getString("employee_name"));
            }
        }
    } catch (SQLException e) {
        System.err.println("Error fetching active employee names for username: " + username + ": " + e.getMessage());
        e.printStackTrace();
    }
    
    return employeeNames;
}



    // Update employee
    public boolean updateEmployee(EmployeeSignatureModel employee) {
        String sql = "UPDATE employee_signatures SET employee_name = ?, department = ?, position = ?, status = ? WHERE employee_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, employee.getEmployeeName());
            stmt.setString(2, employee.getDepartment());
            stmt.setString(3, employee.getPosition());
            stmt.setString(4, employee.getStatus());
            stmt.setString(5, employee.getEmployeeId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }

    // Delete employee
    public boolean deleteEmployee(String employeeId) {
        String sql = "DELETE FROM employee_signatures WHERE employee_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    // Update fingerprint template
    public boolean updateFingerprintTemplate(String employeeId, byte[] fingerprintTemplate) {
        String sql = "UPDATE employee_signatures SET fingerprint_template = ? WHERE employee_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            if (fingerprintTemplate != null && fingerprintTemplate.length > 0) {
                stmt.setBytes(1, fingerprintTemplate);
            } else {
                stmt.setNull(1, Types.VARBINARY);
            }
            stmt.setString(2, employeeId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating fingerprint: " + e.getMessage());
            return false;
        }
    }

    // Update signature image
    public boolean updateSignatureImage(String employeeId, byte[] signatureImage) {
        String sql = "UPDATE employee_signatures SET signature_image = ? WHERE employee_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            if (signatureImage != null && signatureImage.length > 0) {
                stmt.setBytes(1, signatureImage);
            } else {
                stmt.setNull(1, Types.VARBINARY);
            }
            stmt.setString(2, employeeId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating signature: " + e.getMessage());
            return false;
        }
    }

    // Check if employee ID exists
    public boolean employeeExists(String employeeId) {
        String sql = "SELECT COUNT(*) FROM employee_signatures WHERE employee_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking employee existence: " + e.getMessage());
        }
        
        return false;
    }

    // Search employees
    public List<EmployeeSignatureModel> searchEmployees(String searchTerm) {
        List<EmployeeSignatureModel> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee_signatures WHERE employee_id LIKE ? OR employee_name LIKE ? OR department LIKE ? ORDER BY employee_id";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                EmployeeSignatureModel employee = new EmployeeSignatureModel(
                    rs.getString("employee_id"),
                    rs.getString("employee_name"),
                    rs.getString("department"),
                    rs.getString("position"),
                    rs.getDate("enrollment_date").toLocalDate(),
                    rs.getString("status"),
                    rs.getBytes("fingerprint_template"),
                    rs.getBytes("signature_image")
                );
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error searching employees: " + e.getMessage());
        }
        
        return employees;
    }

    // Get employee by ID
    public EmployeeSignatureModel getEmployeeById(String employeeId) {
        String sql = "SELECT * FROM employee_signatures WHERE employee_id = ?";
        
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new EmployeeSignatureModel(
                    rs.getString("employee_id"),
                    rs.getString("employee_name"),
                    rs.getString("department"),
                    rs.getString("position"),
                    rs.getDate("enrollment_date").toLocalDate(),
                    rs.getString("status"),
                    rs.getBytes("fingerprint_template"),
                    rs.getBytes("signature_image")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employee: " + e.getMessage());
        }
        
        return null;
    }
    
    public EmployeeSignatureModel matchWithDatabaseTemplates(byte[] capturedTemplate) {
    String sql = "SELECT employee_id, employee_name, department, position, enrollment_date, status, fingerprint_template " +
                 "FROM employee_signatures WHERE fingerprint_template IS NOT NULL";

    try (
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            byte[] dbTemplate = rs.getBytes("fingerprint_template");

            if (dbTemplate != null && dbTemplate.length > 0) {
                int matchScore = FingerprintSensorEx.DBMatch(
                        fingerprintModule.getDatabaseHandle(),
                        capturedTemplate,
                        dbTemplate
                );

                if (matchScore > 60) { // ✅ Threshold (adjust 40–80 based on quality)
                    EmployeeSignatureModel model = new EmployeeSignatureModel();
                    model.setEmployeeId(rs.getString("employee_id"));
                    model.setEmployeeName(rs.getString("employee_name"));
                    model.setDepartment(rs.getString("department"));
                    model.setPosition(rs.getString("position"));
                    model.setEnrollmentDate(rs.getDate("enrollment_date").toLocalDate());
                    model.setStatus(rs.getString("status"));
                    return model;
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null;
}
  
    
    
    
    // ==================== PETTY CASH REQUEST METHODS ====================
    
    public boolean savePettyCashRequest(PettyCashRecordModel record) {
    PreparedStatement pstmt = null;

    try {
        String sql = "INSERT INTO petty_cash_requests (" +
                "request_id, requisition_unit, main_category, sub_category, reason, payee, amount_requested, " +
                "request_date, confirmation_status, confirmed_by, approval_status, " +
                "approved_by, dispensed_status, dispensed_by, void_status, voided_by" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, record.getRequestId());
        pstmt.setString(2, record.getRequisitionUnit());
        pstmt.setString(3, record.getMainCategory());   // NEW
        pstmt.setString(4, record.getSubCategory());    // NEW
        pstmt.setString(5, record.getReason());
        pstmt.setString(6, record.getPayee());
        pstmt.setDouble(7, record.getAmountRequested());
        pstmt.setDate(8, Date.valueOf(record.getRequestDate()));
        pstmt.setString(9, record.getConfirmationStatus());
        pstmt.setString(10, record.getConfirmedBy());
        pstmt.setString(11, record.getApprovalStatus());
        pstmt.setString(12, record.getApprovedBy());
        pstmt.setString(13, record.getDispensedStatus());
        pstmt.setString(14, record.getDispensedBy());
        pstmt.setString(15, record.getVoidStatus());
        pstmt.setString(16, record.getVoidedBy());

        int rowsAffected = pstmt.executeUpdate();
        System.out.println("✅ Petty cash request saved: " + record.getRequestId());
        return rowsAffected > 0;

    } catch (SQLException e) {
        System.out.println("❌ Error saving petty cash request: " + e.getMessage());
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}

    
    public boolean savePettyCashReplenishRequest(PettyCashReplenishRecordModel record) {
        PreparedStatement pstmt = null;
        
        try {
            String sql = "INSERT INTO petty_cash_replenish_requests (" +
                "request_id, requisition_unit, reason, payee, amount_requested, " +
                "request_date, confirmation_status, confirmed_by, approval_status, " +
                "approved_by, dispensed_status, dispensed_by, void_status, voided_by,current_status" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, record.getRequisitionUnit());
            pstmt.setString(3, record.getReason());
            pstmt.setString(4, record.getPayee());
            pstmt.setDouble(5, record.getAmountRequested());
            pstmt.setDate(6, Date.valueOf(record.getRequestDate()));
            pstmt.setString(7, record.getConfirmationStatus());
            pstmt.setString(8, record.getConfirmedBy());
            pstmt.setString(9, record.getApprovalStatus());
            pstmt.setString(10, record.getApprovedBy());
            pstmt.setString(11, record.getDispensedStatus());
            pstmt.setString(12, record.getDispensedBy());
            pstmt.setString(13, record.getVoidStatus());
            pstmt.setString(14, record.getVoidedBy());
            pstmt.setString(15, "Pending");
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Petty cash request saved: " + record.getRequestId());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error saving petty cash request: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
    public boolean savePurchaseFundReplenishRequest(PurchaseFundReplenishRecordModel record) {
        PreparedStatement pstmt = null;
        
        try {
            String sql = "INSERT INTO purchase_fund_replenish_requests (" +
                "request_id, requisition_unit, reason, payee, amount_requested, " +
                "request_date, confirmation_status, confirmed_by, approval_status, " +
                "approved_by, dispensed_status, dispensed_by, void_status, voided_by,current_status" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, record.getRequisitionUnit());
            pstmt.setString(3, record.getReason());
            pstmt.setString(4, record.getPayee());
            pstmt.setDouble(5, record.getAmountRequested());
            pstmt.setDate(6, Date.valueOf(record.getRequestDate()));
            pstmt.setString(7, record.getConfirmationStatus());
            pstmt.setString(8, record.getConfirmedBy());
            pstmt.setString(9, record.getApprovalStatus());
            pstmt.setString(10, record.getApprovedBy());
            pstmt.setString(11, record.getDispensedStatus());
            pstmt.setString(12, record.getDispensedBy());
            pstmt.setString(13, record.getVoidStatus());
            pstmt.setString(14, record.getVoidedBy());
            pstmt.setString(15, "Pending");
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Petty cash request saved: " + record.getRequestId());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error saving petty cash request: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
public List<PettyCashRecordModel> getAllPettyCashRequests() {
    List<PettyCashRecordModel> requests = new ArrayList<>();
    String sql = "SELECT * FROM petty_cash_requests ORDER BY request_date DESC, request_id";

    try (Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {

            PettyCashRecordModel request = new PettyCashRecordModel(
                rs.getString("request_id"),
                rs.getString("requisition_unit"),
                rs.getString("main_category"),
                rs.getString("sub_category"),
                rs.getString("reason"),
                rs.getString("payee"),
                rs.getDouble("amount_requested"),
                rs.getDate("request_date").toLocalDate(),
                rs.getString("confirmation_status"),
                rs.getString("confirmed_by"),
                rs.getString("approval_status"),
                rs.getString("approved_by"),
                rs.getString("void_status"),
                rs.getString("voided_by"),
                rs.getString("dispensed_status"),
                rs.getString("dispensed_by"),
                rs.getString("dispense_approval_status"),
                rs.getString("dispense_approved_by")
            );

            requests.add(request);
        }

        System.out.println("✅ Loaded " + requests.size() + " petty cash requests from database");

    } catch (SQLException e) {
        System.err.println("❌ Error fetching petty cash requests: " + e.getMessage());
    }

    return requests;
}


    
     public List<PettyCashReplenishRecordModel> getAllPettyCashReplenishRequests() {
        List<PettyCashReplenishRecordModel> requests = new ArrayList<>();
        String sql = "SELECT * FROM petty_cash_replenish_requests ORDER BY request_date DESC, request_id";
        
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                PettyCashReplenishRecordModel request = new PettyCashReplenishRecordModel(
                    rs.getString("request_id"),
                    rs.getString("requisition_unit"),
                    rs.getString("reason"),
                    rs.getString("payee"),
                    rs.getDouble("amount_requested"),
                    rs.getDate("request_date").toLocalDate(),
                    rs.getString("confirmation_status"),
                    rs.getString("confirmed_by"),
                    rs.getString("approval_status"),
                    rs.getString("approved_by"),
                    rs.getString("void_status"),
                    rs.getString("voided_by"),
                    rs.getString("dispensed_status"),
                    rs.getString("dispensed_by"),
                    rs.getString("dispense_approval_status"),
                    rs.getString("dispense_approved_by"),
                    rs.getDouble("available_amount"),
                    rs.getString("current_status")
                );
                requests.add(request);
            }
            System.out.println("✅ Loaded " + requests.size() + " petty cash requests from database");
        } catch (SQLException e) {
            System.err.println("❌ Error fetching petty cash requests: " + e.getMessage());
        }
        
        return requests;
    }
     
     public List<PurchaseFundReplenishRecordModel> getAllPurchaseFundReplenishRequests() {
        List<PurchaseFundReplenishRecordModel> requests = new ArrayList<>();
        String sql = "SELECT * FROM purchase_fund_replenish_requests ORDER BY request_date DESC, request_id";
        
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                PurchaseFundReplenishRecordModel request = new PurchaseFundReplenishRecordModel(
                    rs.getString("request_id"),
                    rs.getString("requisition_unit"),
                    rs.getString("reason"),
                    rs.getString("payee"),
                    rs.getDouble("amount_requested"),
                    rs.getDate("request_date").toLocalDate(),
                    rs.getString("confirmation_status"),
                    rs.getString("confirmed_by"),
                    rs.getString("approval_status"),
                    rs.getString("approved_by"),
                    rs.getString("void_status"),
                    rs.getString("voided_by"),
                    rs.getString("dispensed_status"),
                    rs.getString("dispensed_by"),
                    rs.getString("dispense_approval_status"),
                    rs.getString("dispense_approved_by"),
                    rs.getDouble("available_amount"),
                    rs.getString("current_status")
                );
                requests.add(request);
            }
            System.out.println("✅ Loaded " + requests.size() + " petty cash requests from database");
        } catch (SQLException e) {
            System.err.println("❌ Error fetching petty cash requests: " + e.getMessage());
        }
        
        return requests;
    }
    
   public List<PettyCashRecordModel> getAllPettyCashRecords() {
    List<PettyCashRecordModel> records = new ArrayList<>();
    String sql = "SELECT * FROM petty_cash_requests ORDER BY request_date DESC, request_id";

    try (Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {

            PettyCashRecordModel record = new PettyCashRecordModel(
                rs.getString("request_id"),
                rs.getString("requisition_unit"),
                rs.getString("main_category"),
                rs.getString("sub_category"),
                rs.getString("reason"),
                rs.getString("payee"),
                rs.getDouble("amount_requested"),
                rs.getDate("request_date").toLocalDate(),
                rs.getString("confirmation_status"),
                rs.getString("confirmed_by"),
                rs.getString("approval_status"),
                rs.getString("approved_by"),
                rs.getString("void_status"),
                rs.getString("voided_by"),
                rs.getString("dispensed_status"),
                rs.getString("dispensed_by"),
                rs.getString("dispense_approval_status"),
                rs.getString("dispense_approved_by")
            );

            records.add(record);
        }

        System.out.println("✅ Loaded " + records.size() + " petty cash records");

    } catch (SQLException e) {
        System.err.println("❌ Error fetching petty cash records: " + e.getMessage());
    }

    return records;
}

    public List<PurchaseFundRecordModel> getAllPurchaseFundRequests() {
    List<PurchaseFundRecordModel> records = new ArrayList<>();

    String sql = "SELECT * FROM reciept_based_purchase_fund_requests " +
                 "ORDER BY request_date DESC, request_id ASC";

    try (Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            PurchaseFundRecordModel record = new PurchaseFundRecordModel(
                rs.getString("request_id"),
                rs.getString("requisition_unit"),
                rs.getString("main_category"),
                rs.getString("sub_category"),
                rs.getString("reason"),
                rs.getString("payee"),
                rs.getDouble("amount_requested"),
                rs.getDate("request_date").toLocalDate(),
                rs.getString("confirmation_status"),
                rs.getString("confirmed_by"),
                rs.getString("approval_status"),
                rs.getString("approved_by"),
                rs.getString("void_status"),
                rs.getString("voided_by"),
                rs.getString("dispensed_status"),
                rs.getString("dispensed_by"),
                rs.getString("dispense_approval_status"),
                rs.getString("dispense_approved_by"),
                rs.getString("reciept_upload_status"),
                rs.getString("reciept_uploaded_by")
            );

            records.add(record);
        }

        System.out.println("✅ Loaded " + records.size() + " receipt-based purchase fund requests");

    } catch (SQLException e) {
        System.err.println("❌ Error fetching purchase fund requests: " + e.getMessage());
    }

    return records;
}

    
   public List<AgreementBasedPurchaseFundRecordModel> getAllAgreementBasedPurchaseFundRecords() {
    List<AgreementBasedPurchaseFundRecordModel> records = new ArrayList<>();

    String sql = "SELECT * FROM agreement_based_purchase_fund_requests " +
                 "ORDER BY request_date DESC, request_id ASC";

    try (Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            AgreementBasedPurchaseFundRecordModel record = new AgreementBasedPurchaseFundRecordModel(
                rs.getString("request_id"),
                rs.getString("requisition_unit"),
                rs.getString("main_category"),          // ✅ main category
                rs.getString("sub_category"),           // ✅ subcategory
                rs.getString("reason"),
                rs.getString("payee"),
                rs.getDouble("amount_requested"),
                rs.getDate("request_date").toLocalDate(),
                rs.getString("confirmation_status"),
                rs.getString("confirmed_by"),
                rs.getString("approval_status"),
                rs.getString("approved_by"),
                rs.getString("void_status"),
                rs.getString("voided_by"),
                rs.getString("dispensed_status"),
                rs.getString("dispensed_by"),
                rs.getString("dispense_approval_status"),
                rs.getString("dispense_approved_by"),
                rs.getString("employee_name"),
                rs.getString("agreement_intro"),
                rs.getString("agreement_purpose"),
                rs.getString("agreement_consent"),
                rs.getString("agreement_parties"),
                rs.getString("agreement_nature_of_work"),
                rs.getString("agreement_employer_rights"),
                rs.getString("agreement_employee_rights"),
                rs.getString("agreement_employer_duties"),
                rs.getString("agreement_employee_duties")
            );

            records.add(record);
        }

        System.out.println("✅ Loaded " + records.size() + " agreement-based purchase fund requests");

    } catch (SQLException e) {
        System.err.println("❌ Error fetching agreement-based purchase fund requests: " + e.getMessage());
    }

    return records;
}
    
    public boolean updatePettyCashRequest(PettyCashRecordModel record) {
        PreparedStatement pstmt = null;
        
        try {
            String sql = "UPDATE petty_cash_requests SET " +
                "requisition_unit = ?, reason = ?, payee = ?, amount_requested = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, record.getRequisitionUnit());
            pstmt.setString(2, record.getReason());
            pstmt.setString(3, record.getPayee());
            pstmt.setDouble(4, record.getAmountRequested());
            pstmt.setString(5, record.getRequestId());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Petty cash request updated: " + record.getRequestId());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error updating petty cash request: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
public boolean voidPettyCashRequest(String requestId, String voidedBy, String voidReason) {
    PreparedStatement pstmt = null;
    
    try {
        String sql = "UPDATE petty_cash_requests SET " +
            "void_status = 'Yes', " +
            "voided_by = ?, " +
            "void_reason = ?, " +
            "updated_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, voidedBy);
        pstmt.setString(2, voidReason);
        pstmt.setString(3, requestId);
        
        int rowsAffected = pstmt.executeUpdate();
        System.out.println("✅ Petty cash request voided: " + requestId + " - Reason: " + voidReason);
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.out.println("❌ Error voiding petty cash request: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}
    
public boolean voidPettyCashReplenishRequest(String requestId, String voidedBy, String voidReason) {
    PreparedStatement pstmt = null;
    
    try {
        String sql = "UPDATE petty_cash_replenish_requests SET " +
            "void_status = 'Yes', " +
            "voided_by = ?, " +
            "void_reason = ?, " +
            "current_status = 'Cancelled', " +
            "updated_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, voidedBy);
        pstmt.setString(2, voidReason);
        pstmt.setString(3, requestId);
        
        int rowsAffected = pstmt.executeUpdate();
        System.out.println("✅ Petty cash request voided: " + requestId + " - Reason: " + voidReason);
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.out.println("❌ Error voiding petty cash request: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}

public boolean voidPurchaseFundReplenishRequest(String requestId, String voidedBy, String voidReason) {
    PreparedStatement pstmt = null;
    
    try {
        String sql = "UPDATE purchase_fund_replenish_requests SET " +
            "void_status = 'Yes', " +
            "voided_by = ?, " +
            "void_reason = ?, " +
            "current_status = 'Cancelled', " +
            "updated_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, voidedBy);
        pstmt.setString(2, voidReason);
        pstmt.setString(3, requestId);
        
        int rowsAffected = pstmt.executeUpdate();
        System.out.println("✅ Purchase fund request voided: " + requestId + " - Reason: " + voidReason);
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.out.println("❌ Error voiding petty cash request: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}
    
    public boolean deletePettyCashRequest(String requestId) {
        PreparedStatement pstmt = null;
        
        try {
            String sql = "DELETE FROM petty_cash_requests WHERE request_id = ?";
            
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, requestId);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Petty cash request deleted: " + requestId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error deleting petty cash request: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
    // ==================== DUAL SIGNATURES SAVING ====================
// ==================== DUAL SIGNATURES SAVING ====================
public boolean saveDispensedPettyCashRecordWithDualSignatures(
        DispensedPettyCashModel record, 
        byte[] payeeSignature, 
        byte[] dispenserSignature,
        byte[] payeeFingerprint, 
        byte[] dispenserFingerprint) {

    PreparedStatement pstmt = null;

    try {
        con.setAutoCommit(false);

        // ----------------------------
        // 1. Update main petty cash request as dispensed
        // ----------------------------
        String updateRequestSQL = "UPDATE petty_cash_requests SET " +
                "dispensed_status = 'Yes', " +
                "dispensed_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";

        pstmt = con.prepareStatement(updateRequestSQL);
        pstmt.setString(1, record.getGivenBy());
        pstmt.setString(2, record.getRequestId());
        int updateRows = pstmt.executeUpdate();
        pstmt.close();

        if (updateRows == 0) {
            con.rollback();
            System.out.println("❌ No record found to update for request: " + record.getRequestId());
            return false;
        }

        // ----------------------------
        // 2. Insert into dispensed records with dual signatures
        // ----------------------------
        String insertDispensedSQL = "INSERT INTO dispensed_petty_cash_records_dual (" +
                "request_id, requisition_unit, reason, payee, requested_amount, " +
                "given_amount, given_by, request_date, completed_date, " +
                "payee_signature, dispenser_signature, payee_fingerprint, " +
                "dispenser_fingerprint, verification_method" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        pstmt = con.prepareStatement(insertDispensedSQL);
        pstmt.setString(1, record.getRequestId());
        pstmt.setString(2, record.getRequisitionUnit());
        pstmt.setString(3, record.getReason());
        pstmt.setString(4, record.getPayee());
        pstmt.setDouble(5, Double.parseDouble(record.getRequestedAmount()));
        pstmt.setDouble(6, Double.parseDouble(record.getGivenAmount()));
        pstmt.setString(7, record.getGivenBy());
        pstmt.setDate(8, Date.valueOf(record.getRequestDate()));
        pstmt.setDate(9, Date.valueOf(record.getCompletedDate()));

        setBlobParameter(pstmt, 10, payeeSignature);
        setBlobParameter(pstmt, 11, dispenserSignature);
        setBlobParameter(pstmt, 12, payeeFingerprint);
        setBlobParameter(pstmt, 13, dispenserFingerprint);

        pstmt.setString(14, record.getVerificationMethod());
        pstmt.executeUpdate();
        pstmt.close();

        // ----------------------------
        // 3. Update available amount in replenish request
        // ----------------------------
        String selectAvailableSQL = "SELECT request_id, available_amount FROM petty_cash_replenish_requests " +
                                    "WHERE current_status = ? AND dispense_approval_status = ?";
        pstmt = con.prepareStatement(selectAvailableSQL);
        pstmt.setString(1, "In Use");
        pstmt.setString(2, "Approved");
        ResultSet rs = pstmt.executeQuery();

        double currentAvailable = 0;
        String replenishRequestId = null;
        if (rs.next()) {
            currentAvailable = rs.getDouble("available_amount");
            replenishRequestId = rs.getString("request_id"); // capture the correct replenish request
        }
        rs.close();
        pstmt.close();

        if (replenishRequestId == null) {
            con.rollback();
            System.out.println("❌ No 'In Use' replenish request found to update available amount.");
            return false;
        }

        double givenAmount = Double.parseDouble(record.getGivenAmount());
        double newAvailable = currentAvailable - givenAmount;
        if (newAvailable < 0) newAvailable = 0; // avoid negative

        // Determine if we need to update the status to "Completed"
        String updateAvailableSQL;
        if (newAvailable == 0) {
            // If available amount becomes 0, change status to "Completed"
            updateAvailableSQL = "UPDATE petty_cash_replenish_requests SET available_amount = ?, current_status = 'Completed', updated_at = GETDATE() " +
                                "WHERE request_id = ?";
            System.out.println("✅ Available amount reached 0. Changing status to 'Completed' for request: " + replenishRequestId);
        } else {
            // Otherwise just update the available amount
            updateAvailableSQL = "UPDATE petty_cash_replenish_requests SET available_amount = ?, updated_at = GETDATE() " +
                                "WHERE request_id = ?";
        }

        pstmt = con.prepareStatement(updateAvailableSQL);
        pstmt.setDouble(1, newAvailable);
        pstmt.setString(2, replenishRequestId); // use the replenish request ID
        pstmt.executeUpdate();
        pstmt.close();

        // ----------------------------
        // 4. Commit transaction
        // ----------------------------
        con.commit();
        System.out.println("✅ Dual signature dispense record saved and available amount updated for request: " + record.getRequestId());
        System.out.println("✅ New available amount: " + newAvailable + " for replenish request: " + replenishRequestId);
        return true;

    } catch (SQLException e) {
        try {
            if (con != null) con.rollback();
        } catch (SQLException ex) {
            System.out.println("❌ Rollback failed: " + ex.getMessage());
        }
        System.out.println("❌ Error saving dual signature dispense record: " + e.getMessage());
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}


    
     public boolean saveDispensedPettyCashReplenishRecordWithDualSignatures(DispensedPettyCashModel record, 
                                                        byte[] payeeSignature, 
                                                        byte[] dispenserSignature,
                                                        byte[] payeeFingerprint, 
                                                        byte[] dispenserFingerprint) {
        
        PreparedStatement pstmt = null;
        
        try {
            
            con.setAutoCommit(false);
            
            // Update the main petty cash request table
            String updateRequestSQL = "UPDATE petty_cash_replenish_requests SET " +
                "dispensed_status = 'Yes', " +
                "dispensed_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(updateRequestSQL);
            pstmt.setString(1, record.getGivenBy());
            pstmt.setString(2, record.getRequestId());
            int updateRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updateRows == 0) {
                con.rollback();
                System.out.println("❌ No record found to update for request: " + record.getRequestId());
                return false;
            }
            
            // Insert into dispensed records with dual signatures
            String insertDispensedSQL = "INSERT INTO dispensed_petty_cash_replenish_records_dual (" +
                "request_id, requisition_unit, reason, payee, requested_amount, " +
                "given_amount, given_by, request_date, completed_date, " +
                "payee_signature, dispenser_signature, payee_fingerprint, " +
                "dispenser_fingerprint, verification_method" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertDispensedSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, record.getRequisitionUnit());
            pstmt.setString(3, record.getReason());
            pstmt.setString(4, record.getPayee());
            pstmt.setDouble(5, Double.parseDouble(record.getRequestedAmount()));
            pstmt.setDouble(6, Double.parseDouble(record.getGivenAmount()));
            pstmt.setString(7, record.getGivenBy());
            pstmt.setDate(8, Date.valueOf(record.getRequestDate()));
            pstmt.setDate(9, Date.valueOf(record.getCompletedDate()));
            
            // Set signatures and fingerprints
            setBlobParameter(pstmt, 10, payeeSignature);
            setBlobParameter(pstmt, 11, dispenserSignature);
            setBlobParameter(pstmt, 12, payeeFingerprint);
            setBlobParameter(pstmt, 13, dispenserFingerprint);
            
            pstmt.setString(14, record.getVerificationMethod());
            
            int insertRows = pstmt.executeUpdate();
            
            con.commit();
            
            System.out.println("✅ Dual signature dispense record saved successfully for request: " + record.getRequestId());
            return insertRows > 0;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Error saving dual signature dispense record: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
     
      public boolean saveDispensedPurchaseFundReplenishRecordWithDualSignatures(DispensedReplenishPurchaseFundModel record, 
                                                        byte[] payeeSignature, 
                                                        byte[] dispenserSignature,
                                                        byte[] payeeFingerprint, 
                                                        byte[] dispenserFingerprint) {
        
        PreparedStatement pstmt = null;
        
        try {
            
            con.setAutoCommit(false);
            
            // Update the main petty cash request table
            String updateRequestSQL = "UPDATE purchase_fund_replenish_requests SET " +
                "dispensed_status = 'Yes', " +
                "dispensed_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(updateRequestSQL);
            pstmt.setString(1, record.getGivenBy());
            pstmt.setString(2, record.getRequestId());
            int updateRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updateRows == 0) {
                con.rollback();
                System.out.println("❌ No record found to update for request: " + record.getRequestId());
                return false;
            }
            
            // Insert into dispensed records with dual signatures
            String insertDispensedSQL = "INSERT INTO dispensed_purchase_fund_replenish_records_dual (" +
                "request_id, requisition_unit, reason, payee, requested_amount, " +
                "given_amount, given_by, request_date, completed_date, " +
                "payee_signature, dispenser_signature, payee_fingerprint, " +
                "dispenser_fingerprint, verification_method" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertDispensedSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, record.getRequisitionUnit());
            pstmt.setString(3, record.getReason());
            pstmt.setString(4, record.getPayee());
            pstmt.setDouble(5, Double.parseDouble(record.getRequestedAmount()));
            pstmt.setDouble(6, Double.parseDouble(record.getGivenAmount()));
            pstmt.setString(7, record.getGivenBy());
            pstmt.setDate(8, Date.valueOf(record.getRequestDate()));
            pstmt.setDate(9, Date.valueOf(record.getCompletedDate()));
            
            // Set signatures and fingerprints
            setBlobParameter(pstmt, 10, payeeSignature);
            setBlobParameter(pstmt, 11, dispenserSignature);
            setBlobParameter(pstmt, 12, payeeFingerprint);
            setBlobParameter(pstmt, 13, dispenserFingerprint);
            
            pstmt.setString(14, record.getVerificationMethod());
            
            int insertRows = pstmt.executeUpdate();
            
            con.commit();
            
            System.out.println("✅ Dual signature dispense record saved successfully for request: " + record.getRequestId());
            return insertRows > 0;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Error saving dual signature dispense record: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
    // ==================== APPROVAL WITH BIOMETRIC ====================
    
    public boolean savePettyCashRequestApprovalWithBiometric(PettyCashRecordModel record, 
                                           EmployeeSignatureModel approver,
                                           byte[] fingerprintTemplate, 
                                           String notes) {
        
        PreparedStatement pstmt = null;
        
        try {
            
            con.setAutoCommit(false);
            
            // Update the main petty cash request table
            String updateRequestSQL = "UPDATE petty_cash_requests SET " +
                "approval_status = 'Approved', " +
                "approved_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(updateRequestSQL);
            pstmt.setString(1, approver.getEmployeeName());
            pstmt.setString(2, record.getRequestId());
            int updateRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updateRows == 0) {
                con.rollback();
                System.out.println("❌ No record found to update for request: " + record.getRequestId());
                return false;
            }
            
            // Insert into approval biometric records
            String insertApprovalSQL = "INSERT INTO petty_cash_approval_biometric_records (" +
                "request_id, approver_name, approver_employee_id, approver_department, " +
                "approver_signature, approver_fingerprint, approval_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertApprovalSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, approver.getEmployeeName());
            pstmt.setString(3, approver.getEmployeeId());
            pstmt.setString(4, approver.getDepartment());
            
            // Set signature and fingerprint
            setBlobParameter(pstmt, 5, approver.getSignatureImage());
            setBlobParameter(pstmt, 6, fingerprintTemplate);
            
            pstmt.setString(7, notes != null ? notes : "");
            
            int insertRows = pstmt.executeUpdate();
            
            con.commit();
           
            System.out.println("✅ Biometric approval record saved successfully for request: " + record.getRequestId());
            return insertRows > 0;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Error saving biometric approval record: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
        public boolean savePettyCashReplenishRequestApprovalWithBiometric(PettyCashReplenishRecordModel record, 
                                           EmployeeSignatureModel approver,
                                           byte[] fingerprintTemplate, 
                                           String notes) {
        
        PreparedStatement pstmt = null;
        
        try {
            
            con.setAutoCommit(false);
            
            // Update the main petty cash request table
            String updateRequestSQL = "UPDATE petty_cash_replenish_requests SET " +
                "approval_status = 'Approved', " +
                "approved_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(updateRequestSQL);
            pstmt.setString(1, approver.getEmployeeName());
            pstmt.setString(2, record.getRequestId());
            int updateRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updateRows == 0) {
                con.rollback();
                System.out.println("❌ No record found to update for request: " + record.getRequestId());
                return false;
            }
            
            // Insert into approval biometric records
            String insertApprovalSQL = "INSERT INTO petty_cash_replenish_approval_biometric_records (" +
                "request_id, approver_name, approver_employee_id, approver_department, " +
                "approver_signature, approver_fingerprint, approval_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertApprovalSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, approver.getEmployeeName());
            pstmt.setString(3, approver.getEmployeeId());
            pstmt.setString(4, approver.getDepartment());
            
            // Set signature and fingerprint
            setBlobParameter(pstmt, 5, approver.getSignatureImage());
            setBlobParameter(pstmt, 6, fingerprintTemplate);
            
            pstmt.setString(7, notes != null ? notes : "");
            
            int insertRows = pstmt.executeUpdate();
            
            con.commit();
           
            System.out.println("✅ Biometric approval record saved successfully for request: " + record.getRequestId());
            return insertRows > 0;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Error saving biometric approval record: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
        
        
         public boolean savePurchaseFundReplenishRequestApprovalWithBiometric(PurchaseFundReplenishRecordModel record, 
                                           EmployeeSignatureModel approver,
                                           byte[] fingerprintTemplate, 
                                           String notes) {
        
        PreparedStatement pstmt = null;
        
        try {
            
            con.setAutoCommit(false);
            
            // Update the main petty cash request table
            String updateRequestSQL = "UPDATE purchase_fund_replenish_requests SET " +
                "approval_status = 'Approved', " +
                "approved_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(updateRequestSQL);
            pstmt.setString(1, approver.getEmployeeName());
            pstmt.setString(2, record.getRequestId());
            int updateRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updateRows == 0) {
                con.rollback();
                System.out.println("❌ No record found to update for request: " + record.getRequestId());
                return false;
            }
            
            // Insert into approval biometric records
            String insertApprovalSQL = "INSERT INTO purchase_fund_replenish_approval_biometric_records (" +
                "request_id, approver_name, approver_employee_id, approver_department, " +
                "approver_signature, approver_fingerprint, approval_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertApprovalSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, approver.getEmployeeName());
            pstmt.setString(3, approver.getEmployeeId());
            pstmt.setString(4, approver.getDepartment());
            
            // Set signature and fingerprint
            setBlobParameter(pstmt, 5, approver.getSignatureImage());
            setBlobParameter(pstmt, 6, fingerprintTemplate);
            
            pstmt.setString(7, notes != null ? notes : "");
            
            int insertRows = pstmt.executeUpdate();
            
            con.commit();
           
            System.out.println("✅ Biometric approval record saved successfully for request: " + record.getRequestId());
            return insertRows > 0;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Error saving biometric approval record: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
   public boolean savePettyCashDispenseApprovalWithBiometric(PettyCashRecordModel record, 
                                       EmployeeSignatureModel approver,
                                       byte[] fingerprintTemplate, 
                                       String notes) {
    
    PreparedStatement pstmt = null;
    
    try {
        
        con.setAutoCommit(false);
        
        // Update the dispensed_petty_cash_records_dual table with approval information
        String updateDispensedSQL = "UPDATE dispensed_petty_cash_records_dual SET " +
            "approval_status = 'Approved', " +
            "approved_by = ?, " +
            "approver_name = ?, " +
            "approver_employee_id = ?, " +
            "approver_department = ?, " +
            "approver_signature = ?, " +
            "approver_fingerprint = ?, " +
            "approval_notes = ?, " +
            "created_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(updateDispensedSQL);
        pstmt.setString(1, approver.getEmployeeName()); // approved_by
        pstmt.setString(2, approver.getEmployeeName()); // approver_name
        pstmt.setString(3, approver.getEmployeeId());   // approver_employee_id
        pstmt.setString(4, approver.getDepartment());   // approver_department
        
        // Set signature and fingerprint blobs
        setBlobParameter(pstmt, 5, approver.getSignatureImage()); // approver_signature
        setBlobParameter(pstmt, 6, fingerprintTemplate);          // approver_fingerprint
        
        pstmt.setString(7, notes != null ? notes : "");           // approval_notes
        pstmt.setString(8, record.getRequestId());                // request_id
        
        int updateRows = pstmt.executeUpdate();
        pstmt.close();
        
        if (updateRows == 0) {
            // No record found in dispensed_petty_cash_records_dual, try to insert a new one
            String insertDispensedSQL = "INSERT INTO dispensed_petty_cash_records_dual (" +
                "request_id, requisition_unit, reason, payee, requested_amount, " +
                "given_amount, given_by, request_date, completed_date, " +
                "approval_status, approved_by, approver_name, approver_employee_id, " +
                "approver_department, approver_signature, approver_fingerprint, approval_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertDispensedSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, record.getRequisitionUnit());
            pstmt.setString(3, record.getReason());
            pstmt.setString(4, record.getPayee());
            pstmt.setDouble(5, record.getAmountRequested());
            pstmt.setDouble(6, record.getAmountRequested()); // Assuming given amount equals requested amount
            pstmt.setString(7, record.getDispensedBy());     // Use dispensed by as given by
            pstmt.setDate(8, Date.valueOf(record.getRequestDate()));
            pstmt.setDate(9, Date.valueOf(LocalDate.now())); // Use current date as completed date
            
            // Approval information
            pstmt.setString(10, "Approved");
            pstmt.setString(11, approver.getEmployeeName());
            pstmt.setString(12, approver.getEmployeeName());
            pstmt.setString(13, approver.getEmployeeId());
            pstmt.setString(14, approver.getDepartment());
            
            // Set signature and fingerprint blobs
            setBlobParameter(pstmt, 15, approver.getSignatureImage());
            setBlobParameter(pstmt, 16, fingerprintTemplate);
            
            pstmt.setString(17, notes != null ? notes : "");
            
            updateRows = pstmt.executeUpdate();
            pstmt.close();
        }
        
        if (updateRows == 0) {
            con.rollback();
            System.out.println("❌ No record found to update or insert for request: " + record.getRequestId());
            return false;
        }
        
        // Also update the main petty_cash_requests table with dispense approval status
        String updateMainRequestSQL = "UPDATE petty_cash_requests SET " +
            "dispense_approval_status = 'Approved', " +
            "dispense_approved_by = ?, " +
            "updated_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(updateMainRequestSQL);
        pstmt.setString(1, approver.getEmployeeName());
        pstmt.setString(2, record.getRequestId());
        pstmt.executeUpdate();
        pstmt.close();
        
        con.commit();
       
        System.out.println("✅ Dispense approval biometric record saved successfully for request: " + record.getRequestId());
        return true;
        
    } catch (SQLException e) {
        try {
            if (con != null) con.rollback();
        } catch (SQLException ex) {
            System.out.println("❌ Rollback failed: " + ex.getMessage());
        }
        System.out.println("❌ Error saving dispense approval biometric record: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}
   
   public boolean savePettyCashReplenishDispenseApprovalWithBiometric(PettyCashReplenishRecordModel record, 
                                       EmployeeSignatureModel approver,
                                       byte[] fingerprintTemplate, 
                                       String notes) {
    
    PreparedStatement pstmt = null;
    
    try {
        
        con.setAutoCommit(false);
        
        // Update the dispensed_petty_cash_records_dual table with approval information
        String updateDispensedSQL = "UPDATE dispensed_petty_cash_replenish_records_dual SET " +
            "approval_status = 'Approved', " +
            "approved_by = ?, " +
            "approver_name = ?, " +
            "approver_employee_id = ?, " +
            "approver_department = ?, " +
            "approver_signature = ?, " +
            "approver_fingerprint = ?, " +
            "approval_notes = ?, " +
            "created_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(updateDispensedSQL);
        pstmt.setString(1, approver.getEmployeeName()); // approved_by
        pstmt.setString(2, approver.getEmployeeName()); // approver_name
        pstmt.setString(3, approver.getEmployeeId());   // approver_employee_id
        pstmt.setString(4, approver.getDepartment());   // approver_department
        
        // Set signature and fingerprint blobs
        setBlobParameter(pstmt, 5, approver.getSignatureImage()); // approver_signature
        setBlobParameter(pstmt, 6, fingerprintTemplate);          // approver_fingerprint
        
        pstmt.setString(7, notes != null ? notes : "");           // approval_notes
        pstmt.setString(8, record.getRequestId());                // request_id
        
        int updateRows = pstmt.executeUpdate();
        pstmt.close();
        
        if (updateRows == 0) {
            // No record found in dispensed_petty_cash_records_dual, try to insert a new one
            String insertDispensedSQL = "INSERT INTO dispensed_petty_cash_replenish_records_dual (" +
                "request_id, requisition_unit, reason, payee, requested_amount, " +
                "given_amount, given_by, request_date, completed_date, " +
                "approval_status, approved_by, approver_name, approver_employee_id, " +
                "approver_department, approver_signature, approver_fingerprint, approval_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertDispensedSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, record.getRequisitionUnit());
            pstmt.setString(3, record.getReason());
            pstmt.setString(4, record.getPayee());
            pstmt.setDouble(5, record.getAmountRequested());
            pstmt.setDouble(6, record.getAmountRequested()); // Assuming given amount equals requested amount
            pstmt.setString(7, record.getDispensedBy());     // Use dispensed by as given by
            pstmt.setDate(8, Date.valueOf(record.getRequestDate()));
            pstmt.setDate(9, Date.valueOf(LocalDate.now())); // Use current date as completed date
            
            // Approval information
            pstmt.setString(10, "Approved");
            pstmt.setString(11, approver.getEmployeeName());
            pstmt.setString(12, approver.getEmployeeName());
            pstmt.setString(13, approver.getEmployeeId());
            pstmt.setString(14, approver.getDepartment());
            
            // Set signature and fingerprint blobs
            setBlobParameter(pstmt, 15, approver.getSignatureImage());
            setBlobParameter(pstmt, 16, fingerprintTemplate);
            
            pstmt.setString(17, notes != null ? notes : "");
            
            updateRows = pstmt.executeUpdate();
            pstmt.close();
        }
        
        if (updateRows == 0) {
            con.rollback();
            System.out.println("❌ No record found to update or insert for request: " + record.getRequestId());
            return false;
        }
        
        // In the main request update section:
          String updateMainRequestSQL = "UPDATE petty_cash_replenish_requests SET " +
          "dispense_approval_status = 'Approved', " +
          "dispense_approved_by = ?, " +
          "available_amount = ?, " +  // Set to full requested amount when approved
          "current_status = 'In Use', " +
          "updated_at = GETDATE() " +
          "WHERE request_id = ?";

          pstmt = con.prepareStatement(updateMainRequestSQL);
          pstmt.setString(1, approver.getEmployeeName());        // dispense_approved_by
          pstmt.setDouble(2, record.getAmountRequested());       // available_amount - set to full amount
          pstmt.setString(3, record.getRequestId());             // request_id
          pstmt.executeUpdate();
          pstmt.close();
        
        con.commit();
       
        System.out.println("✅ Dispense approval biometric record saved successfully for request: " + record.getRequestId());
        return true;
        
    } catch (SQLException e) {
        try {
            if (con != null) con.rollback();
        } catch (SQLException ex) {
            System.out.println("❌ Rollback failed: " + ex.getMessage());
        }
        System.out.println("❌ Error saving dispense approval biometric record: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}
   
   
   public boolean savePurchaseFundReplenishDispenseApprovalWithBiometric(PurchaseFundReplenishRecordModel record, 
                                       EmployeeSignatureModel approver,
                                       byte[] fingerprintTemplate, 
                                       String notes) {
    
    PreparedStatement pstmt = null;
    
    try {
        
        con.setAutoCommit(false);
        
        // Update the dispensed_petty_cash_records_dual table with approval information
        String updateDispensedSQL = "UPDATE dispensed_purchase_fund_replenish_records_dual SET " +
            "approval_status = 'Approved', " +
            "approved_by = ?, " +
            "approver_name = ?, " +
            "approver_employee_id = ?, " +
            "approver_department = ?, " +
            "approver_signature = ?, " +
            "approver_fingerprint = ?, " +
            "approval_notes = ?, " +
            "created_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(updateDispensedSQL);
        pstmt.setString(1, approver.getEmployeeName()); // approved_by
        pstmt.setString(2, approver.getEmployeeName()); // approver_name
        pstmt.setString(3, approver.getEmployeeId());   // approver_employee_id
        pstmt.setString(4, approver.getDepartment());   // approver_department
        
        // Set signature and fingerprint blobs
        setBlobParameter(pstmt, 5, approver.getSignatureImage()); // approver_signature
        setBlobParameter(pstmt, 6, fingerprintTemplate);          // approver_fingerprint
        
        pstmt.setString(7, notes != null ? notes : "");           // approval_notes
        pstmt.setString(8, record.getRequestId());                // request_id
        
        int updateRows = pstmt.executeUpdate();
        pstmt.close();
        
        if (updateRows == 0) {
            // No record found in dispensed_petty_cash_records_dual, try to insert a new one
            String insertDispensedSQL = "INSERT INTO dispensed_purchase_fund_replenish_records_dual (" +
                "request_id, requisition_unit, reason, payee, requested_amount, " +
                "given_amount, given_by, request_date, completed_date, " +
                "approval_status, approved_by, approver_name, approver_employee_id, " +
                "approver_department, approver_signature, approver_fingerprint, approval_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertDispensedSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, record.getRequisitionUnit());
            pstmt.setString(3, record.getReason());
            pstmt.setString(4, record.getPayee());
            pstmt.setDouble(5, record.getAmountRequested());
            pstmt.setDouble(6, record.getAmountRequested()); // Assuming given amount equals requested amount
            pstmt.setString(7, record.getDispensedBy());     // Use dispensed by as given by
            pstmt.setDate(8, Date.valueOf(record.getRequestDate()));
            pstmt.setDate(9, Date.valueOf(LocalDate.now())); // Use current date as completed date
            
            // Approval information
            pstmt.setString(10, "Approved");
            pstmt.setString(11, approver.getEmployeeName());
            pstmt.setString(12, approver.getEmployeeName());
            pstmt.setString(13, approver.getEmployeeId());
            pstmt.setString(14, approver.getDepartment());
            
            // Set signature and fingerprint blobs
            setBlobParameter(pstmt, 15, approver.getSignatureImage());
            setBlobParameter(pstmt, 16, fingerprintTemplate);
            
            pstmt.setString(17, notes != null ? notes : "");
            
            updateRows = pstmt.executeUpdate();
            pstmt.close();
        }
        
        if (updateRows == 0) {
            con.rollback();
            System.out.println("❌ No record found to update or insert for request: " + record.getRequestId());
            return false;
        }
        
        // In the main request update section:
          String updateMainRequestSQL = "UPDATE purchase_fund_replenish_requests SET " +
          "dispense_approval_status = 'Approved', " +
          "dispense_approved_by = ?, " +
          "available_amount = ?, " +  // Set to full requested amount when approved
          "current_status = 'In Use', " +
          "updated_at = GETDATE() " +
          "WHERE request_id = ?";

          pstmt = con.prepareStatement(updateMainRequestSQL);
          pstmt.setString(1, approver.getEmployeeName());        // dispense_approved_by
          pstmt.setDouble(2, record.getAmountRequested());       // available_amount - set to full amount
          pstmt.setString(3, record.getRequestId());             // request_id
          pstmt.executeUpdate();
          pstmt.close();
        
        con.commit();
       
        System.out.println("✅ Dispense approval biometric record saved successfully for request: " + record.getRequestId());
        return true;
        
    } catch (SQLException e) {
        try {
            if (con != null) con.rollback();
        } catch (SQLException ex) {
            System.out.println("❌ Rollback failed: " + ex.getMessage());
        }
        System.out.println("❌ Error saving dispense approval biometric record: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}
   
    // ==================== CONFIRMATION WITH BIOMETRIC ====================
    
    public boolean savePettyCashConfirmationWithBiometric(PettyCashRecordModel record, 
                                               EmployeeSignatureModel confirmer,
                                               byte[] fingerprintTemplate, 
                                               String notes) {
        
        PreparedStatement pstmt = null;
        
        try {
           
            con.setAutoCommit(false);
            
            // Update the main petty cash request table
            String updateRequestSQL = "UPDATE petty_cash_requests SET " +
                "confirmation_status = 'Confirmed', " +
                "confirmed_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(updateRequestSQL);
            pstmt.setString(1, confirmer.getEmployeeName());
            pstmt.setString(2, record.getRequestId());
            int updateRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updateRows == 0) {
                con.rollback();
                System.out.println("❌ No record found to update for request: " + record.getRequestId());
                return false;
            }
            
            // Insert into confirmation biometric records
            String insertConfirmationSQL = "INSERT INTO petty_cash_confirmation_biometric_records (" +
                "request_id, confirmer_name, confirmer_employee_id, confirmer_department, " +
                "confirmer_signature, confirmer_fingerprint, confirmation_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertConfirmationSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, confirmer.getEmployeeName());
            pstmt.setString(3, confirmer.getEmployeeId());
            pstmt.setString(4, confirmer.getDepartment());
            
            // Set signature and fingerprint
            setBlobParameter(pstmt, 5, confirmer.getSignatureImage());
            setBlobParameter(pstmt, 6, fingerprintTemplate);
            
            pstmt.setString(7, notes != null ? notes : "");
            
            int insertRows = pstmt.executeUpdate();
            
            con.commit();
            
            System.out.println("✅ Biometric confirmation record saved successfully for request: " + record.getRequestId());
            return insertRows > 0;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Error saving biometric confirmation record: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
    public boolean savePettyCashReplenishConfirmationWithBiometric(PettyCashReplenishRecordModel record, 
                                               EmployeeSignatureModel confirmer,
                                               byte[] fingerprintTemplate, 
                                               String notes) {
        
        PreparedStatement pstmt = null;
        
        try {
           
            con.setAutoCommit(false);
            
            // Update the main petty cash request table
            String updateRequestSQL = "UPDATE petty_cash_replenish_requests SET " +
                "confirmation_status = 'Confirmed', " +
                "confirmed_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(updateRequestSQL);
            pstmt.setString(1, confirmer.getEmployeeName());
            pstmt.setString(2, record.getRequestId());
            int updateRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updateRows == 0) {
                con.rollback();
                System.out.println("❌ No record found to update for request: " + record.getRequestId());
                return false;
            }
            
            // Insert into confirmation biometric records
            String insertConfirmationSQL = "INSERT INTO petty_cash_replenish_confirmation_biometric_records (" +
                "request_id, confirmer_name, confirmer_employee_id, confirmer_department, " +
                "confirmer_signature, confirmer_fingerprint, confirmation_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertConfirmationSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, confirmer.getEmployeeName());
            pstmt.setString(3, confirmer.getEmployeeId());
            pstmt.setString(4, confirmer.getDepartment());
            
            // Set signature and fingerprint
            setBlobParameter(pstmt, 5, confirmer.getSignatureImage());
            setBlobParameter(pstmt, 6, fingerprintTemplate);
            
            pstmt.setString(7, notes != null ? notes : "");
            
            int insertRows = pstmt.executeUpdate();
            
            con.commit();
            
            System.out.println("✅ Biometric confirmation record saved successfully for request: " + record.getRequestId());
            return insertRows > 0;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Error saving biometric confirmation record: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
    
        public boolean savePurchaseFundReplenishConfirmationWithBiometric(PurchaseFundReplenishRecordModel record, 
                                               EmployeeSignatureModel confirmer,
                                               byte[] fingerprintTemplate, 
                                               String notes) {
        
        PreparedStatement pstmt = null;
        
        try {
           
            con.setAutoCommit(false);
            
            // Update the main petty cash request table
            String updateRequestSQL = "UPDATE purchase_fund_replenish_requests SET " +
                "confirmation_status = 'Confirmed', " +
                "confirmed_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(updateRequestSQL);
            pstmt.setString(1, confirmer.getEmployeeName());
            pstmt.setString(2, record.getRequestId());
            int updateRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updateRows == 0) {
                con.rollback();
                System.out.println("❌ No record found to update for request: " + record.getRequestId());
                return false;
            }
            
            // Insert into confirmation biometric records
            String insertConfirmationSQL = "INSERT INTO purchase_fund_replenish_confirmation_biometric_records (" +
                "request_id, confirmer_name, confirmer_employee_id, confirmer_department, " +
                "confirmer_signature, confirmer_fingerprint, confirmation_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertConfirmationSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, confirmer.getEmployeeName());
            pstmt.setString(3, confirmer.getEmployeeId());
            pstmt.setString(4, confirmer.getDepartment());
            
            // Set signature and fingerprint
            setBlobParameter(pstmt, 5, confirmer.getSignatureImage());
            setBlobParameter(pstmt, 6, fingerprintTemplate);
            
            pstmt.setString(7, notes != null ? notes : "");
            
            int insertRows = pstmt.executeUpdate();
            
            con.commit();
            
            System.out.println("✅ Biometric confirmation record saved successfully for request: " + record.getRequestId());
            return insertRows > 0;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Error saving biometric confirmation record: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
    public byte[] getEmployeeSignature(String employeeName) {
        String sql = "SELECT signature_image FROM employee_signatures WHERE employee_name = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, employeeName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("signature_image");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching employee signature: " + e.getMessage());
        }
        
        return null;
    }
    
    
    
    //=====================PURCHASE  FUND  BY  AGREEMENT============================
    
    public boolean saveRecieptBasedPurchaseFundRequest(PurchaseFundRecordModel record) {
    PreparedStatement pstmt = null;

    try {
        String sql = "INSERT INTO reciept_based_purchase_fund_requests (" +
                "request_id, requisition_unit, reason, payee, amount_requested, " +
                "request_date, confirmation_status, confirmed_by, approval_status, " +
                "approved_by, dispensed_status, dispensed_by, void_status, voided_by, " +
                "main_category, sub_category" +  // ✅ added
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, record.getRequestId());
        pstmt.setString(2, record.getRequisitionUnit());
        pstmt.setString(3, record.getReason());
        pstmt.setString(4, record.getPayee());
        pstmt.setDouble(5, record.getAmountRequested());
        pstmt.setDate(6, Date.valueOf(record.getRequestDate()));
        pstmt.setString(7, record.getConfirmationStatus());
        pstmt.setString(8, record.getConfirmedBy());
        pstmt.setString(9, record.getApprovalStatus());
        pstmt.setString(10, record.getApprovedBy());
        pstmt.setString(11, record.getDispensedStatus());
        pstmt.setString(12, record.getDispensedBy());
        pstmt.setString(13, record.getVoidStatus());
        pstmt.setString(14, record.getVoidedBy());
        pstmt.setString(15, record.getMainCategory());   // ✅ added
        pstmt.setString(16, record.getSubCategory());    // ✅ added

        int rowsAffected = pstmt.executeUpdate();
        System.out.println("✅ reciept_based_purchase_fund request saved: " + record.getRequestId());
        return rowsAffected > 0;

    } catch (SQLException e) {
        System.out.println("❌ Error saving reciept_based_purchase_fund request: " + e.getMessage());
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}

    
    
    public List<PurchaseFundRecordModel> getAllRecieptBasedPurchaseFundRequests() {
        List<PurchaseFundRecordModel> requests = new ArrayList<>();
        String sql = "SELECT * FROM reciept_based_purchase_fund_requests ORDER BY request_date DESC, request_id";
        
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                PurchaseFundRecordModel request = new PurchaseFundRecordModel(
    rs.getString("request_id"),
    rs.getString("requisition_unit"),
    rs.getString("main_category"),  // 3rd
    rs.getString("sub_category"),   // 4th
    rs.getString("reason"),         // 5th
    rs.getString("payee"),          // 6th
    rs.getDouble("amount_requested"),
    rs.getDate("request_date").toLocalDate(),
    rs.getString("confirmation_status"),
    rs.getString("confirmed_by"),
    rs.getString("approval_status"),
    rs.getString("approved_by"),
    rs.getString("void_status"),
    rs.getString("voided_by"),
    rs.getString("dispensed_status"),
    rs.getString("dispensed_by"),
    rs.getString("dispense_approval_status"),
    rs.getString("dispense_approved_by"),
    rs.getString("reciept_upload_status"),
    rs.getString("reciept_uploaded_by")
);

                requests.add(request);
            }
            System.out.println("✅ Loaded " + requests.size() + " reciept_based_purchase_fund requests from database");
        } catch (SQLException e) {
            System.err.println("❌ Error fetchingreciept_based_purchase_fund requests: " + e.getMessage());
        }
        
        return requests;
    }
    
    
      public boolean updateRecieptBasedPurchaseFundRequest(PurchaseFundRecordModel record) {
        PreparedStatement pstmt = null;
        
        try {
            String sql = "UPDATE reciept_based_purchase_fund_requests SET " +
                "requisition_unit = ?, reason = ?, payee = ?, amount_requested = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, record.getRequisitionUnit());
            pstmt.setString(2, record.getReason());
            pstmt.setString(3, record.getPayee());
            pstmt.setDouble(4, record.getAmountRequested());
            pstmt.setString(5, record.getRequestId());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ reciept_based_purchase_fund request updated: " + record.getRequestId());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error updating reciept_based_purchase_fund request: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
      
      public boolean updateRecieptBasedPurchaseFundRequestUploadStatus(String requestId, String uploadedBy) {
    
    PreparedStatement pstmt = null;
    
    try {
       
        String sql = "UPDATE reciept_based_purchase_fund_requests SET " +
                    "reciept_upload_status = 'Uploaded', " +
                    "reciept_uploaded_by = ?, " +
                    "reciept_upload_date = CURRENT_TIMESTAMP " +
                    "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, uploadedBy);
        pstmt.setString(2, requestId);
        
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.err.println("Error updating receipt upload status: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        closeResources(con, pstmt, null);
    }
}
      
      public boolean saveRecieptFiles(String requestId, List<File> receiptFiles, String uploadedBy) throws FileNotFoundException {

    PreparedStatement pstmt = null;
    FileInputStream fis = null;
    
    try {

        
        // First update the main request status
        if (!updateRecieptBasedPurchaseFundRequestUploadStatus(requestId, uploadedBy)) {
            return false;
        }
        
        // Insert each receipt file
        String sql = "INSERT INTO purchase_fund_reciepts (request_id, file_name, file_data, uploaded_by, upload_date) " +
                    "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        for (File file : receiptFiles) {
            fis = new FileInputStream(file);
            
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, requestId);
            pstmt.setString(2, file.getName());
            pstmt.setBinaryStream(3, fis, (int) file.length());
            pstmt.setString(4, uploadedBy);
            
            pstmt.executeUpdate();
            
            // Close the file input stream for this file
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(Connecting.class.getName()).log(Level.SEVERE, null, ex);
                }
                fis = null;
            }
            
            if (pstmt != null) {
                pstmt.close();
                pstmt = null;
            }
        }
        
        return true;
        
    } catch (SQLException e) {
        System.err.println("Error saving receipt files: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(Connecting.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        closeResources(con, pstmt, null);
    }
}
      
      // Method to retrieve receipt files
public List<byte[]> getRecieptFiles(String requestId) {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<byte[]> receiptFiles = new ArrayList<>();
    
    try {

        String sql = "SELECT file_data FROM purchase_fund_reciepts WHERE request_id = ? ORDER BY upload_date";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, requestId);
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            receiptFiles.add(rs.getBytes("file_data"));
        }
        
        return receiptFiles;
        
    } catch (SQLException e) {
        System.err.println("Error retrieving receipt files: " + e.getMessage());
        e.printStackTrace();
        return receiptFiles;
    } finally {
        closeResources(con, pstmt, rs);
    }
}

// Method to get receipt file names
public List<String> getRecieptFileNames(String requestId) {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    List<String> fileNames = new ArrayList<>();
    
    try {
        String sql = "SELECT file_name FROM purchase_fund_reciepts WHERE request_id = ? ORDER BY upload_date";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, requestId);
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            fileNames.add(rs.getString("file_name"));
        }
        
        return fileNames;
        
    } catch (SQLException e) {
        System.err.println("Error retrieving receipt file names: " + e.getMessage());
        e.printStackTrace();
        return fileNames;
    } finally {
        closeResources(con, pstmt, rs);
    }
}     
      
      public boolean voidRecieptBasedPurchaseFundRequest(String requestId, String voidedBy, String voidReason) {
    PreparedStatement pstmt = null;
    
    try {
        String sql = "UPDATE receipt_based_purchase_fund_requests SET " +
            "void_status = 'Yes', " +
            "voided_by = ?, " +
            "void_reason = ?, " +
            "updated_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, voidedBy);
        pstmt.setString(2, voidReason);
        pstmt.setString(3, requestId);
        
        int rowsAffected = pstmt.executeUpdate();
        System.out.println("✅ Purchase fund request voided: " + requestId + " - Reason: " + voidReason);
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.out.println("❌ Error voiding purchase fund request: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}
      
      public boolean voidAgreementBasedPurchaseFundRequest(String requestId, String voidedBy, String voidReason) {
    PreparedStatement pstmt = null;
    
    try {
        String sql = "UPDATE agreement_based_purchase_fund_requests SET " +
            "void_status = 'Yes', " +
            "voided_by = ?, " +
            "void_reason = ?, " +
            "updated_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, voidedBy);
        pstmt.setString(2, voidReason);
        pstmt.setString(3, requestId);
        
        int rowsAffected = pstmt.executeUpdate();
        System.out.println("✅ Agreement based purchase fund request voided: " + requestId + " - Reason: " + voidReason);
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.out.println("❌ Error voiding agreement based purchase fund request: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}
    
  public String getRecieptBasedPurchaseFundRequestVoidReason(String requestId) {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        String sql = "SELECT void_reason FROM receipt_based_purchase_fund_requests WHERE request_id = ?";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, requestId);
        
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            String voidReason = rs.getString("void_reason");
            return voidReason != null ? voidReason : "";
        }
        
        return ""; // Return empty string if no record found
        
    } catch (SQLException e) {
        System.out.println("❌ Error getting void reason for request: " + requestId + " - " + e.getMessage());
        e.printStackTrace();
        return "";
    } finally {
        closeResources(con, pstmt, null);
    }
}
   public String getAgreementBasedPurchaseFundRequestVoidReason(String requestId) {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        String sql = "SELECT void_reason FROM agreement_based_purchase_fund_requests WHERE request_id = ?";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, requestId);
        
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            String voidReason = rs.getString("void_reason");
            return voidReason != null ? voidReason : "";
        }
        
        return ""; // Return empty string if no record found
        
    } catch (SQLException e) {
        System.out.println("❌ Error getting void reason for request: " + requestId + " - " + e.getMessage());
        e.printStackTrace();
        return "";
    } finally {
        closeResources(con, pstmt, null);
    }
}
    public String getPettyCashRequestVoidReason(String requestId) {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        String sql = "SELECT void_reason FROM petty_cash_requests WHERE request_id = ?";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, requestId);
        
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            String voidReason = rs.getString("void_reason");
            return voidReason != null ? voidReason : "";
        }
        
        return ""; // Return empty string if no record found
        
    } catch (SQLException e) {
        System.out.println("❌ Error getting void reason for request: " + requestId + " - " + e.getMessage());
        e.printStackTrace();
        return "";
    } finally {
        closeResources(con, pstmt, null);
    }
}
    
     public String getPettyCashReplenishRequestVoidReason(String requestId) {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        String sql = "SELECT void_reason FROM petty_cash_replenish_requests WHERE request_id = ?";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, requestId);
        
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            String voidReason = rs.getString("void_reason");
            return voidReason != null ? voidReason : "";
        }
        
        return ""; // Return empty string if no record found
        
    } catch (SQLException e) {
        System.out.println("❌ Error getting void reason for request: " + requestId + " - " + e.getMessage());
        e.printStackTrace();
        return "";
    } finally {
        closeResources(con, pstmt, null);
    }
}
     
     public String getPurchaseFundReplenishRequestVoidReason(String requestId) {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        String sql = "SELECT void_reason FROM purchase_fund_replenish_requests WHERE request_id = ?";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, requestId);
        
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            String voidReason = rs.getString("void_reason");
            return voidReason != null ? voidReason : "";
        }
        
        return ""; // Return empty string if no record found
        
    } catch (SQLException e) {
        System.out.println("❌ Error getting void reason for request: " + requestId + " - " + e.getMessage());
        e.printStackTrace();
        return "";
    } finally {
        closeResources(con, pstmt, null);
    }
}
  
    
    public boolean deleteRecieptBasedPurchaseFundRequest(String requestId) {
        PreparedStatement pstmt = null;
        
        try {
            String sql = "DELETE FROM reciept_based_purchase_fund_requests WHERE request_id = ?";
            
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, requestId);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ reciept_based_purchase_fund request deleted: " + requestId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error deleting reciept_based_purchase_fund request: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
    
    
     // ==================== DUAL SIGNATURES SAVING ====================
    
public boolean saveDispensedRecieptBasedPurchaseFundRecordWithDualSignatures(DispensedPurchaseFundModel record, 
                                                        byte[] payeeSignature, 
                                                        byte[] dispenserSignature,
                                                        byte[] payeeFingerprint, 
                                                        byte[] dispenserFingerprint) {
        
    PreparedStatement pstmt = null;
    
    try {
        
        con.setAutoCommit(false);
        
        // Update the main reciept_based_purchase_fund request table
        String updateRequestSQL = "UPDATE reciept_based_purchase_fund_requests SET " +
            "dispensed_status = 'Yes', " +
            "dispensed_by = ?, " +
            "updated_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(updateRequestSQL);
        pstmt.setString(1, record.getGivenBy());
        pstmt.setString(2, record.getRequestId());
        int updateRows = pstmt.executeUpdate();
        pstmt.close();
        
        if (updateRows == 0) {
            con.rollback();
            System.out.println("❌ No record found to update for request: " + record.getRequestId());
            return false;
        }
        
        // Insert into dispensed records with dual signatures
        String insertDispensedSQL = "INSERT INTO dispensed_reciept_based_purchase_fund_records_dual (" +
            "request_id, requisition_unit, reason, payee, requested_amount, " +
            "given_amount, given_by, request_date, completed_date, " +
            "payee_signature, dispenser_signature, payee_fingerprint, " +
            "dispenser_fingerprint, verification_method" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        pstmt = con.prepareStatement(insertDispensedSQL);
        pstmt.setString(1, record.getRequestId());
        pstmt.setString(2, record.getRequisitionUnit());
        pstmt.setString(3, record.getReason());
        pstmt.setString(4, record.getPayee());
        pstmt.setDouble(5, record.getRequestedAmount());
        pstmt.setDouble(6, record.getGivenAmount());
        pstmt.setString(7, record.getGivenBy());
        pstmt.setDate(8, Date.valueOf(record.getRequestDate()));
        pstmt.setDate(9, Date.valueOf(record.getCompletedDate()));
        
        // Set signatures and fingerprints
        setBlobParameter(pstmt, 10, payeeSignature);
        setBlobParameter(pstmt, 11, dispenserSignature);
        setBlobParameter(pstmt, 12, payeeFingerprint);
        setBlobParameter(pstmt, 13, dispenserFingerprint);
        
        pstmt.setString(14, record.getVerificationMethod());
        pstmt.executeUpdate();
        pstmt.close();

        // ----------------------------
        // 3. Update available amount in purchase fund replenish request
        // ----------------------------
        String selectAvailableSQL = "SELECT request_id, available_amount FROM purchase_fund_replenish_requests " +
                                    "WHERE current_status = ? AND dispense_approval_status = ?";
        pstmt = con.prepareStatement(selectAvailableSQL);
        pstmt.setString(1, "In Use");
        pstmt.setString(2, "Approved");
        ResultSet rs = pstmt.executeQuery();

        double currentAvailable = 0;
        String replenishRequestId = null;
        if (rs.next()) {
            currentAvailable = rs.getDouble("available_amount");
            replenishRequestId = rs.getString("request_id"); // capture the correct replenish request
        }
        rs.close();
        pstmt.close();

        if (replenishRequestId == null) {
            con.rollback();
            System.out.println("❌ No 'In Use' purchase fund replenish request found to update available amount.");
            return false;
        }

        double givenAmount = record.getGivenAmount();
        double newAvailable = currentAvailable - givenAmount;
        if (newAvailable < 0) newAvailable = 0; // avoid negative

        // Determine if we need to update the status to "Completed"
        String updateAvailableSQL;
        if (newAvailable == 0) {
            // If available amount becomes 0, change status to "Completed"
            updateAvailableSQL = "UPDATE purchase_fund_replenish_requests SET available_amount = ?, current_status = 'Completed', updated_at = GETDATE() " +
                                "WHERE request_id = ?";
            System.out.println("✅ Available amount reached 0. Changing status to 'Completed' for purchase fund request: " + replenishRequestId);
        } else {
            // Otherwise just update the available amount
            updateAvailableSQL = "UPDATE purchase_fund_replenish_requests SET available_amount = ?, updated_at = GETDATE() " +
                                "WHERE request_id = ?";
        }

        pstmt = con.prepareStatement(updateAvailableSQL);
        pstmt.setDouble(1, newAvailable);
        pstmt.setString(2, replenishRequestId); // use the replenish request ID
        pstmt.executeUpdate();
        pstmt.close();
        
        con.commit();
        
        System.out.println("✅ Dual signature dispense record saved successfully for request: " + record.getRequestId());
        System.out.println("✅ New available amount: " + newAvailable + " for purchase fund replenish request: " + replenishRequestId);
        return true;
        
    } catch (SQLException e) {
        try {
            if (con != null) con.rollback();
        } catch (SQLException ex) {
            System.out.println("❌ Rollback failed: " + ex.getMessage());
        }
        System.out.println("❌ Error saving dual signature dispense record: " + e.getMessage());
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}
    
    // ==================== APPROVAL WITH BIOMETRIC ====================
    
    public boolean saveRecieptBasedPurchaseFundRequestApprovalWithBiometric(PurchaseFundRecordModel record, 
                                           EmployeeSignatureModel approver,
                                           byte[] fingerprintTemplate, 
                                           String notes) {
        
        PreparedStatement pstmt = null;
        
        try {
            
            con.setAutoCommit(false);
            
            // Update the main petty cash request table
            String updateRequestSQL = "UPDATE reciept_based_purchase_fund_requests SET " +
                "approval_status = 'Approved', " +
                "approved_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(updateRequestSQL);
            pstmt.setString(1, approver.getEmployeeName());
            pstmt.setString(2, record.getRequestId());
            int updateRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updateRows == 0) {
                con.rollback();
                System.out.println("❌ No record found to update for request: " + record.getRequestId());
                return false;
            }
            
            // Insert into approval biometric records
            String insertApprovalSQL = "INSERT INTO reciept_based_purchase_fund_approval_biometric_records (" +
                "request_id, approver_name, approver_employee_id, approver_department, " +
                "approver_signature, approver_fingerprint, approval_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertApprovalSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, approver.getEmployeeName());
            pstmt.setString(3, approver.getEmployeeId());
            pstmt.setString(4, approver.getDepartment());
            
            // Set signature and fingerprint
            setBlobParameter(pstmt, 5, approver.getSignatureImage());
            setBlobParameter(pstmt, 6, fingerprintTemplate);
            
            pstmt.setString(7, notes != null ? notes : "");
            
            int insertRows = pstmt.executeUpdate();
            
            con.commit();
           
            System.out.println("✅ Biometric approval record saved successfully for request: " + record.getRequestId());
            return insertRows > 0;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Error saving biometric approval record: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
   public boolean saveRecieptBasedPurchaseFundDispenseApprovalWithBiometric(PurchaseFundRecordModel record, 
                                       EmployeeSignatureModel approver,
                                       byte[] fingerprintTemplate, 
                                       String notes) {
    
    PreparedStatement pstmt = null;
    
    try {
        
        con.setAutoCommit(false);
        
        // Update the dispensed_petty_cash_records_dual table with approval information
        String updateDispensedSQL = "UPDATE dispensed_reciept_based_purchase_fund_records_dual SET " +
            "approval_status = 'Approved', " +
            "approved_by = ?, " +
            "approver_name = ?, " +
            "approver_employee_id = ?, " +
            "approver_department = ?, " +
            "approver_signature = ?, " +
            "approver_fingerprint = ?, " +
            "approval_notes = ?, " +
            "created_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(updateDispensedSQL);
        pstmt.setString(1, approver.getEmployeeName()); // approved_by
        pstmt.setString(2, approver.getEmployeeName()); // approver_name
        pstmt.setString(3, approver.getEmployeeId());   // approver_employee_id
        pstmt.setString(4, approver.getDepartment());   // approver_department
        
        // Set signature and fingerprint blobs
        setBlobParameter(pstmt, 5, approver.getSignatureImage()); // approver_signature
        setBlobParameter(pstmt, 6, fingerprintTemplate);          // approver_fingerprint
        
        pstmt.setString(7, notes != null ? notes : "");           // approval_notes
        pstmt.setString(8, record.getRequestId());                // request_id
        
        int updateRows = pstmt.executeUpdate();
        pstmt.close();
        
        if (updateRows == 0) {
            // No record found in dispensed_petty_cash_records_dual, try to insert a new one
            String insertDispensedSQL = "INSERT INTO dispensed_reciept_based_purchase_fund_records_dual (" +
                "request_id, requisition_unit, reason, payee, requested_amount, " +
                "given_amount, given_by, request_date, completed_date, " +
                "approval_status, approved_by, approver_name, approver_employee_id, " +
                "approver_department, approver_signature, approver_fingerprint, approval_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertDispensedSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, record.getRequisitionUnit());
            pstmt.setString(3, record.getReason());
            pstmt.setString(4, record.getPayee());
            pstmt.setDouble(5, record.getAmountRequested());
            pstmt.setDouble(6, record.getAmountRequested()); // Assuming given amount equals requested amount
            pstmt.setString(7, record.getDispensedBy());     // Use dispensed by as given by
            pstmt.setDate(8, Date.valueOf(record.getRequestDate()));
            pstmt.setDate(9, Date.valueOf(LocalDate.now())); // Use current date as completed date
            
            // Approval information
            pstmt.setString(10, "Approved");
            pstmt.setString(11, approver.getEmployeeName());
            pstmt.setString(12, approver.getEmployeeName());
            pstmt.setString(13, approver.getEmployeeId());
            pstmt.setString(14, approver.getDepartment());
            
            // Set signature and fingerprint blobs
            setBlobParameter(pstmt, 15, approver.getSignatureImage());
            setBlobParameter(pstmt, 16, fingerprintTemplate);
            
            pstmt.setString(17, notes != null ? notes : "");
            
            updateRows = pstmt.executeUpdate();
            pstmt.close();
        }
        
        if (updateRows == 0) {
            con.rollback();
            System.out.println("❌ No record found to update or insert for request: " + record.getRequestId());
            return false;
        }
        
        // Also update the main petty_cash_requests table with dispense approval status
        String updateMainRequestSQL = "UPDATE reciept_based_purchase_fund_requests SET " +
            "dispense_approval_status = 'Approved', " +
            "dispense_approved_by = ?, " +
            "updated_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(updateMainRequestSQL);
        pstmt.setString(1, approver.getEmployeeName());
        pstmt.setString(2, record.getRequestId());
        pstmt.executeUpdate();
        pstmt.close();
        
        con.commit();
       
        System.out.println("✅ Dispense approval biometric record saved successfully for request: " + record.getRequestId());
        return true;
        
    } catch (SQLException e) {
        try {
            if (con != null) con.rollback();
        } catch (SQLException ex) {
            System.out.println("❌ Rollback failed: " + ex.getMessage());
        }
        System.out.println("❌ Error saving dispense approval biometric record: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}
    // ==================== CONFIRMATION WITH BIOMETRIC ====================
    
    public boolean saveRecieptBasedPurchaseFundConfirmationWithBiometric(PurchaseFundRecordModel record, 
                                               EmployeeSignatureModel confirmer,
                                               byte[] fingerprintTemplate, 
                                               String notes) {
        
        PreparedStatement pstmt = null;
        
        try {
           
            con.setAutoCommit(false);
            
            // Update the main petty cash request table
            String updateRequestSQL = "UPDATE reciept_based_purchase_fund_requests SET " +
                "confirmation_status = 'Confirmed', " +
                "confirmed_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(updateRequestSQL);
            pstmt.setString(1, confirmer.getEmployeeName());
            pstmt.setString(2, record.getRequestId());
            int updateRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updateRows == 0) {
                con.rollback();
                System.out.println("❌ No record found to update for request: " + record.getRequestId());
                return false;
            }
            
            // Insert into confirmation biometric records
            String insertConfirmationSQL = "INSERT INTO reciept_based_purchase_fund_confirmation_biometric_records (" +
                "request_id, confirmer_name, confirmer_employee_id, confirmer_department, " +
                "confirmer_signature, confirmer_fingerprint, confirmation_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertConfirmationSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, confirmer.getEmployeeName());
            pstmt.setString(3, confirmer.getEmployeeId());
            pstmt.setString(4, confirmer.getDepartment());
            
            // Set signature and fingerprint
            setBlobParameter(pstmt, 5, confirmer.getSignatureImage());
            setBlobParameter(pstmt, 6, fingerprintTemplate);
            
            pstmt.setString(7, notes != null ? notes : "");
            
            int insertRows = pstmt.executeUpdate();
            
            con.commit();
            
            System.out.println("✅ Biometric confirmation record saved successfully for request: " + record.getRequestId());
            return insertRows > 0;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Error saving biometric confirmation record: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    
    
     /* ===================== MAIN CATEGORIES ===================== */

    public ObservableList<String> getMainCategories() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT name FROM reciept_based_purchase_fund_main_categories ORDER BY name";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
     public ObservableList<String> getPettyCashMainCategories() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT name FROM petty_cash_fund_main_categories ORDER BY name";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
     
     public ObservableList<String> getAgreementBasedPurchaseFundMainCategories() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT name FROM agreement_based_purchase_fund_main_categories ORDER BY name";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addMainCategory(String name) {
        String sql = "INSERT INTO reciept_based_purchase_fund_main_categories(name) VALUES (?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMainCategory(String oldName, String newName) {
        String sql = "UPDATE reciept_based_purchase_fund_main_categories SET name = ? WHERE name = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newName.trim());
            ps.setString(2, oldName);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMainCategory(String name) {
        String sql = "DELETE FROM reciept_based_purchase_fund_main_categories WHERE name = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== SUBCATEGORIES ===================== */

    public ObservableList<String> getSubCategories(String mainCategory) {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT s.name FROM reciept_based_purchase_fund_sub_categories s JOIN reciept_based_purchase_fund_main_categories m ON s.main_category_id = m.id WHERE m.name = ? ORDER BY s.name";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mainCategory);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
     public ObservableList<String> getPettyCashSubCategories(String mainCategory) {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT s.name FROM petty_cash_fund_sub_categories s JOIN petty_cash_fund_main_categories m ON s.main_category_id = m.id WHERE m.name = ? ORDER BY s.name";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mainCategory);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
     
     public ObservableList<String> getAgreementBasedPurchaseFundSubCategories(String mainCategory) {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT s.name FROM agreement_based_purchase_fund_sub_categories s JOIN agreement_based_purchase_fund_main_categories m ON s.main_category_id = m.id WHERE m.name = ? ORDER BY s.name";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mainCategory);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(rs.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addSubCategory(String mainCategory, String subName) {
        String sql = "INSERT INTO reciept_based_purchase_fund_sub_categories(main_category_id, name) VALUES ((SELECT id FROM reciept_based_purchase_fund_main_categories WHERE name = ?), ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mainCategory);
            ps.setString(2, subName.trim());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSubCategory(String mainCategory, String oldName, String newName) {
        String sql = "UPDATE reciept_based_purchase_fund_sub_categories SET name = ? WHERE name = ? AND main_category_id = (SELECT id FROM reciept_based_purchase_fund_main_categories WHERE name = ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newName.trim());
            ps.setString(2, oldName);
            ps.setString(3, mainCategory);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSubCategory(String mainCategory, String subName) {
        String sql = "DELETE FROM reciept_based_purchase_fund_sub_categories WHERE name = ? AND main_category_id = (SELECT id FROM reciept_based_purchase_fund_main_categories WHERE name = ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subName);
            ps.setString(2, mainCategory);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    // ==================== UTILITY METHODS ====================
    
    private void setBlobParameter(PreparedStatement pstmt, int parameterIndex, byte[] data) throws SQLException {
        if (data != null && data.length > 0) {
            pstmt.setBytes(parameterIndex, data);
        } else {
            pstmt.setNull(parameterIndex, Types.BLOB);
        }
    }
    
    private void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            // Don't close the connection here as we're using a single connection
        } catch (SQLException e) {
            System.out.println("❌ Error closing resources: " + e.getMessage());
        }
    }
    
    public boolean isConnected() {
        try {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
// ✅ 1. Insert New Agreement
public int insertAgreement(String employeeName, String introduction, String purpose, String consent,
                           String parties, String natureOfWork,
                           String employerRights, String employeeRights,
                           String employerDuties, String employeeDuties) throws SQLException {
    String sql = "INSERT INTO purchase_fund_agreements " +
                 "(employee_name, introduction, purpose, consent, parties, nature_of_work, employer_rights, employee_rights, employer_duties, employee_duties) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, employeeName);
        ps.setString(2, introduction);
        ps.setString(3, purpose);
        ps.setString(4, consent);
        ps.setString(5, parties);
        ps.setString(6, natureOfWork);
        ps.setString(7, employerRights);
        ps.setString(8, employeeRights);
        ps.setString(9, employerDuties);
        ps.setString(10, employeeDuties);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1); // return new ID
        }
    }
    return -1;
}

// ✅ 2. Update Agreement
public boolean updateAgreement(int id, String employeeName, String introduction, String purpose, String consent,
                               String parties, String natureOfWork,
                               String employerRights, String employeeRights,
                               String employerDuties, String employeeDuties) throws SQLException {
    String sql = "UPDATE purchase_fund_agreements SET " +
                 "employee_name=?, introduction=?, purpose=?, consent=?, parties=?, nature_of_work=?, " +
                 "employer_rights=?, employee_rights=?, employer_duties=?, employee_duties=? " +
                 "WHERE id=?";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, employeeName);
        ps.setString(2, introduction);
        ps.setString(3, purpose);
        ps.setString(4, consent);
        ps.setString(5, parties);
        ps.setString(6, natureOfWork);
        ps.setString(7, employerRights);
        ps.setString(8, employeeRights);
        ps.setString(9, employerDuties);
        ps.setString(10, employeeDuties);
        ps.setInt(11, id);
        return ps.executeUpdate() > 0;
    }
}

// ✅ 3. Load Agreement by ID
public PurchaseFundAgreementModel getAgreementById(int id) throws SQLException {
    String sql = "SELECT * FROM purchase_fund_agreements WHERE id=?";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new PurchaseFundAgreementModel(
                    rs.getInt("id"),
                    rs.getString("employee_name"),
                    rs.getString("introduction"),
                    rs.getString("purpose"),
                    rs.getString("consent"),
                    rs.getString("parties"),
                    rs.getString("nature_of_work"),
                    rs.getString("employer_rights"),
                    rs.getString("employee_rights"),
                    rs.getString("employer_duties"),
                    rs.getString("employee_duties")
            );
        }
    }
    return null;
}

// ✅ 3. Load Agreement by ID
public PurchaseFundAgreementModel getAgreements() throws SQLException {
    String sql = "SELECT * FROM purchase_fund_agreements";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new PurchaseFundAgreementModel(
                    rs.getInt("id"),
                    rs.getString("employee_name"),
                    rs.getString("introduction"),
                    rs.getString("purpose"),
                    rs.getString("consent"),
                    rs.getString("parties"),
                    rs.getString("nature_of_work"),
                    rs.getString("employer_rights"),
                    rs.getString("employee_rights"),
                    rs.getString("employer_duties"),
                    rs.getString("employee_duties")
            );
        }
    }
    return null;
}

// ✅ 4. Load All Agreements
public List<PurchaseFundAgreementModel> getAllAgreements() throws SQLException {
    List<PurchaseFundAgreementModel> list = new ArrayList<>();
    String sql = "SELECT * FROM purchase_fund_agreements ORDER BY id DESC";
    try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
        while (rs.next()) {
            list.add(new PurchaseFundAgreementModel(
                    rs.getInt("id"),
                    rs.getString("employee_name"),
                    rs.getString("introduction"),
                    rs.getString("purpose"),
                    rs.getString("consent"),
                    rs.getString("parties"),
                    rs.getString("nature_of_work"),
                    rs.getString("employer_rights"),
                    rs.getString("employee_rights"),
                    rs.getString("employer_duties"),
                    rs.getString("employee_duties")
            ));
        }
    }
    return list;
}

// ✅ 5. Delete Agreement
public boolean deleteAgreement(int id) throws SQLException {
    String sql = "DELETE FROM purchase_fund_agreements WHERE id=?";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
    }
}



public boolean saveAgreementBasedPurchaseFundRequest(
        AgreementBasedPurchaseFundRecordModel record, List<File> uploadedFiles) {

    String insertRequestSQL = "INSERT INTO agreement_based_purchase_fund_requests " +
            "(request_id, requisition_unit, main_category, sub_category, reason, payee, amount_requested, request_date, " +
            "confirmation_status, confirmed_by, approval_status, approved_by, dispensed_status, " +
            "dispensed_by, void_status, voided_by, dispense_approval_status, dispense_approved_by, " +
            "employee_name, agreement_intro, agreement_purpose, agreement_consent, agreement_parties, " +
            "agreement_nature_of_work, agreement_employer_rights, agreement_employee_rights, " +
            "agreement_employer_duties, agreement_employee_duties) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    String insertFileSQL = "INSERT INTO agreement_based_purchase_fund_requests_payee_id_cards " +
            "(request_id, file_name, file_type, file_data) VALUES (?, ?, ?, ?)";

    try  {
        con.setAutoCommit(false); // Start transaction

        // -------------------------------
        // Insert main fund request
        // -------------------------------
        try (PreparedStatement ps = con.prepareStatement(insertRequestSQL)) {
            ps.setString(1, record.getRequestId());
            ps.setString(2, record.getRequisitionUnit());
            ps.setString(3, record.getMainCategory());   // ✅ main category
            ps.setString(4, record.getSubCategory());    // ✅ subcategory
            ps.setString(5, record.getReason());
            ps.setString(6, record.getPayee());
            ps.setDouble(7, record.getAmountRequested());
            ps.setDate(8, java.sql.Date.valueOf(record.getRequestDate()));
            ps.setString(9, record.getConfirmationStatus());
            ps.setString(10, record.getConfirmedBy());
            ps.setString(11, record.getApprovalStatus());
            ps.setString(12, record.getApprovedBy());
            ps.setString(13, record.getDispensedStatus());
            ps.setString(14, record.getDispensedBy());
            ps.setString(15, record.getVoidStatus());
            ps.setString(16, record.getVoidedBy());
            ps.setString(17, record.getDispenseApprovalStatus());
            ps.setString(18, record.getDispenseApprovedBy());
            ps.setString(19, record.getEmployeeName());
            ps.setString(20, record.getAgreementIntro());
            ps.setString(21, record.getAgreementPurpose());
            ps.setString(22, record.getAgreementConsent());
            ps.setString(23, record.getAgreementParties());
            ps.setString(24, record.getAgreementNatureOfWork());
            ps.setString(25, record.getAgreementEmployerRights());
            ps.setString(26, record.getAgreementEmployeeRights());
            ps.setString(27, record.getAgreementEmployerDuties());
            ps.setString(28, record.getAgreementEmployeeDuties());
            ps.executeUpdate();
        }

        // -------------------------------
        // Insert uploaded files
        // -------------------------------
        if (uploadedFiles != null && !uploadedFiles.isEmpty()) {
            try (PreparedStatement psFile = con.prepareStatement(insertFileSQL)) {
                for (File file : uploadedFiles) {
                    psFile.setString(1, record.getRequestId());
                    psFile.setString(2, file.getName());
                    psFile.setString(3, Files.probeContentType(file.toPath())); // MIME type
                    psFile.setBytes(4, Files.readAllBytes(file.toPath())); // file content
                    psFile.addBatch();
                }
                psFile.executeBatch();
            }
        }

        con.commit(); // Commit transaction
        return true;

    } catch (Exception e) {
        e.printStackTrace();
        try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    } finally {
        try { con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
    }
}


public List<AgreementBasedPurchaseFundRecordModel> getAllAgreementBasedPurchaseFundRequests() {
    List<AgreementBasedPurchaseFundRecordModel> requests = new ArrayList<>();
    String query = "SELECT request_id, requisition_unit, main_category, sub_category, reason, payee, amount_requested, request_date, " +
            "confirmation_status, confirmed_by, approval_status, approved_by, dispensed_status, " +
            "dispensed_by, void_status, voided_by, dispense_approval_status, dispense_approved_by, " +
            "employee_name, agreement_intro, agreement_purpose, agreement_consent, agreement_parties, " +
            "agreement_nature_of_work, agreement_employer_rights, agreement_employee_rights, " +
            "agreement_employer_duties, agreement_employee_duties " +
            "FROM agreement_based_purchase_fund_requests ORDER BY request_date DESC";

    try (PreparedStatement ps = con.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            AgreementBasedPurchaseFundRecordModel record = new AgreementBasedPurchaseFundRecordModel(
                    rs.getString("request_id"),
                    rs.getString("requisition_unit"),
                    rs.getString("main_category"),      // ✅ main category
                    rs.getString("sub_category"),       // ✅ subcategory
                    rs.getString("reason"),
                    rs.getString("payee"),
                    rs.getDouble("amount_requested"),
                    rs.getDate("request_date").toLocalDate(),
                    rs.getString("confirmation_status"),
                    rs.getString("confirmed_by"),
                    rs.getString("approval_status"),
                    rs.getString("approved_by"),
                    rs.getString("void_status"),
                    rs.getString("voided_by"),
                    rs.getString("dispensed_status"),
                    rs.getString("dispensed_by"),
                    rs.getString("dispense_approval_status"),
                    rs.getString("dispense_approved_by"),
                    rs.getString("employee_name"),
                    rs.getString("agreement_intro"),
                    rs.getString("agreement_purpose"),
                    rs.getString("agreement_consent"),
                    rs.getString("agreement_parties"),
                    rs.getString("agreement_nature_of_work"),
                    rs.getString("agreement_employer_rights"),
                    rs.getString("agreement_employee_rights"),
                    rs.getString("agreement_employer_duties"),
                    rs.getString("agreement_employee_duties")
            );
            requests.add(record);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return requests;
}


public boolean updateAgreementBasedPurchaseFundRequest(AgreementBasedPurchaseFundRecordModel record) {
        PreparedStatement pstmt = null;
        
        try {
            String sql = "UPDATE agreement_based_purchase_fund_requests SET " +
                "requisition_unit = ?, reason = ?, payee = ?, amount_requested = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, record.getRequisitionUnit());
            pstmt.setString(2, record.getReason());
            pstmt.setString(3, record.getPayee());
            pstmt.setDouble(4, record.getAmountRequested());
            pstmt.setString(5, record.getRequestId());
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ agreement_based_purchase_fund request updated: " + record.getRequestId());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("❌ Error updating agreement_based_purchase_fund request: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

 // ==================== APPROVAL WITH BIOMETRIC ====================
    
    public boolean saveAgreementBasedPurchaseFundRequestApprovalWithBiometric(AgreementBasedPurchaseFundRecordModel record, 
                                           EmployeeSignatureModel approver,
                                           byte[] fingerprintTemplate, 
                                           String notes) {
        
        PreparedStatement pstmt = null;
        
        try {
            
            con.setAutoCommit(false);
            
            // Update the main petty cash request table
            String updateRequestSQL = "UPDATE agreement_based_purchase_fund_requests SET " +
                "approval_status = 'Approved', " +
                "approved_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(updateRequestSQL);
            pstmt.setString(1, approver.getEmployeeName());
            pstmt.setString(2, record.getRequestId());
            int updateRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updateRows == 0) {
                con.rollback();
                System.out.println("❌ No record found to update for request: " + record.getRequestId());
                return false;
            }
            
            // Insert into approval biometric records
            String insertApprovalSQL = "INSERT INTO agreement_based_purchase_fund_approval_biometric_records (" +
                "request_id, approver_name, approver_employee_id, approver_department, " +
                "approver_signature, approver_fingerprint, approval_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertApprovalSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, approver.getEmployeeName());
            pstmt.setString(3, approver.getEmployeeId());
            pstmt.setString(4, approver.getDepartment());
            
            // Set signature and fingerprint
            setBlobParameter(pstmt, 5, approver.getSignatureImage());
            setBlobParameter(pstmt, 6, fingerprintTemplate);
            
            pstmt.setString(7, notes != null ? notes : "");
            
            int insertRows = pstmt.executeUpdate();
            
            con.commit();
           
            System.out.println("✅ Biometric approval record saved successfully for request: " + record.getRequestId());
            return insertRows > 0;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Error saving biometric approval record: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }
    

    public boolean saveAgreementBasedPurchaseFundDispenseApprovalWithBiometric(AgreementBasedPurchaseFundRecordModel record, 
                                       EmployeeSignatureModel approver,
                                       byte[] fingerprintTemplate, 
                                       String notes) {
    
    PreparedStatement pstmt = null;
    
    try {
        
        con.setAutoCommit(false);
        
        // Update the dispensed_petty_cash_records_dual table with approval information
        String updateDispensedSQL = "UPDATE dispensed_agreement_based_purchase_fund_records_dual SET " +
            "approval_status = 'Approved', " +
            "approved_by = ?, " +
            "approver_name = ?, " +
            "approver_employee_id = ?, " +
            "approver_department = ?, " +
            "approver_signature = ?, " +
            "approver_fingerprint = ?, " +
            "approval_notes = ?, " +
            "created_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(updateDispensedSQL);
        pstmt.setString(1, approver.getEmployeeName()); // approved_by
        pstmt.setString(2, approver.getEmployeeName()); // approver_name
        pstmt.setString(3, approver.getEmployeeId());   // approver_employee_id
        pstmt.setString(4, approver.getDepartment());   // approver_department
        
        // Set signature and fingerprint blobs
        setBlobParameter(pstmt, 5, approver.getSignatureImage()); // approver_signature
        setBlobParameter(pstmt, 6, fingerprintTemplate);          // approver_fingerprint
        
        pstmt.setString(7, notes != null ? notes : "");           // approval_notes
        pstmt.setString(8, record.getRequestId());                // request_id
        
        int updateRows = pstmt.executeUpdate();
        pstmt.close();
        
        if (updateRows == 0) {
            // No record found in dispensed_petty_cash_records_dual, try to insert a new one
            String insertDispensedSQL = "INSERT INTO dispensed_agreement_based_purchase_fund_records_dual (" +
                "request_id, requisition_unit, reason, payee, requested_amount, " +
                "given_amount, given_by, request_date, completed_date, " +
                "approval_status, approved_by, approver_name, approver_employee_id, " +
                "approver_department, approver_signature, approver_fingerprint, approval_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertDispensedSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, record.getRequisitionUnit());
            pstmt.setString(3, record.getReason());
            pstmt.setString(4, record.getPayee());
            pstmt.setDouble(5, record.getAmountRequested());
            pstmt.setDouble(6, record.getAmountRequested()); // Assuming given amount equals requested amount
            pstmt.setString(7, record.getDispensedBy());     // Use dispensed by as given by
            pstmt.setDate(8, Date.valueOf(record.getRequestDate()));
            pstmt.setDate(9, Date.valueOf(LocalDate.now())); // Use current date as completed date
            
            // Approval information
            pstmt.setString(10, "Approved");
            pstmt.setString(11, approver.getEmployeeName());
            pstmt.setString(12, approver.getEmployeeName());
            pstmt.setString(13, approver.getEmployeeId());
            pstmt.setString(14, approver.getDepartment());
            
            // Set signature and fingerprint blobs
            setBlobParameter(pstmt, 15, approver.getSignatureImage());
            setBlobParameter(pstmt, 16, fingerprintTemplate);
            
            pstmt.setString(17, notes != null ? notes : "");
            
            updateRows = pstmt.executeUpdate();
            pstmt.close();
        }
        
        if (updateRows == 0) {
            con.rollback();
            System.out.println("❌ No record found to update or insert for request: " + record.getRequestId());
            return false;
        }
        
        // Also update the main petty_cash_requests table with dispense approval status
        String updateMainRequestSQL = "UPDATE agreement_based_purchase_fund_requests SET " +
            "dispense_approval_status = 'Approved', " +
            "dispense_approved_by = ?, " +
            "updated_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(updateMainRequestSQL);
        pstmt.setString(1, approver.getEmployeeName());
        pstmt.setString(2, record.getRequestId());
        pstmt.executeUpdate();
        pstmt.close();
        
        con.commit();
       
        System.out.println("✅ Dispense approval biometric record saved successfully for request: " + record.getRequestId());
        return true;
        
    } catch (SQLException e) {
        try {
            if (con != null) con.rollback();
        } catch (SQLException ex) {
            System.out.println("❌ Rollback failed: " + ex.getMessage());
        }
        System.out.println("❌ Error saving dispense approval biometric record: " + e.getMessage());
        e.printStackTrace();
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}


    // ==================== CONFIRMATION WITH BIOMETRIC ====================
    
    public boolean saveAgreementBasedPurchaseFundConfirmationWithBiometric(AgreementBasedPurchaseFundRecordModel record, 
                                               EmployeeSignatureModel confirmer,
                                               byte[] fingerprintTemplate, 
                                               String notes) {
        
        PreparedStatement pstmt = null;
        
        try {
           
            con.setAutoCommit(false);
            
            // Update the main petty cash request table
            String updateRequestSQL = "UPDATE agreement_based_purchase_fund_requests SET " +
                "confirmation_status = 'Confirmed', " +
                "confirmed_by = ?, " +
                "updated_at = GETDATE() " +
                "WHERE request_id = ?";
            
            pstmt = con.prepareStatement(updateRequestSQL);
            pstmt.setString(1, confirmer.getEmployeeName());
            pstmt.setString(2, record.getRequestId());
            int updateRows = pstmt.executeUpdate();
            pstmt.close();
            
            if (updateRows == 0) {
                con.rollback();
                System.out.println("❌ No record found to update for request: " + record.getRequestId());
                return false;
            }
            
            // Insert into confirmation biometric records
            String insertConfirmationSQL = "INSERT INTO agreement_based_purchase_fund_confirmation_biometric_records (" +
                "request_id, confirmer_name, confirmer_employee_id, confirmer_department, " +
                "confirmer_signature, confirmer_fingerprint, confirmation_notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = con.prepareStatement(insertConfirmationSQL);
            pstmt.setString(1, record.getRequestId());
            pstmt.setString(2, confirmer.getEmployeeName());
            pstmt.setString(3, confirmer.getEmployeeId());
            pstmt.setString(4, confirmer.getDepartment());
            
            // Set signature and fingerprint
            setBlobParameter(pstmt, 5, confirmer.getSignatureImage());
            setBlobParameter(pstmt, 6, fingerprintTemplate);
            
            pstmt.setString(7, notes != null ? notes : "");
            
            int insertRows = pstmt.executeUpdate();
            
            con.commit();
            
            System.out.println("✅ Biometric confirmation record saved successfully for request: " + record.getRequestId());
            return insertRows > 0;
            
        } catch (SQLException e) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.out.println("❌ Rollback failed: " + ex.getMessage());
            }
            System.out.println("❌ Error saving biometric confirmation record: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, pstmt, null);
        }
    }

    
     // ==================== DUAL SIGNATURES SAVING ====================
    
public boolean saveDispensedAgreementBasedPurchaseFundRecordWithDualSignatures(DispensedAgreementBasedPurchaseFundModel record, 
                                                        byte[] payeeSignature, 
                                                        byte[] dispenserSignature,
                                                        byte[] payeeFingerprint, 
                                                        byte[] dispenserFingerprint) {
        
    PreparedStatement pstmt = null;
    
    try {
        
        con.setAutoCommit(false);
        
        // Update the main agreement_based_purchase_fund request table
        String updateRequestSQL = "UPDATE agreement_based_purchase_fund_requests SET " +
            "dispensed_status = 'Yes', " +
            "dispensed_by = ?, " +
            "updated_at = GETDATE() " +
            "WHERE request_id = ?";
        
        pstmt = con.prepareStatement(updateRequestSQL);
        pstmt.setString(1, record.getGivenBy());
        pstmt.setString(2, record.getRequestId());
        int updateRows = pstmt.executeUpdate();
        pstmt.close();
        
        if (updateRows == 0) {
            con.rollback();
            System.out.println("❌ No record found to update for request: " + record.getRequestId());
            return false;
        }
        
        // Insert into dispensed records with dual signatures
        String insertDispensedSQL = "INSERT INTO dispensed_agreement_based_purchase_fund_records_dual (" +
            "request_id, requisition_unit, reason, payee, requested_amount, " +
            "given_amount, given_by, request_date, completed_date, " +
            "payee_signature, dispenser_signature, payee_fingerprint, " +
            "dispenser_fingerprint, verification_method" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        pstmt = con.prepareStatement(insertDispensedSQL);
        pstmt.setString(1, record.getRequestId());
        pstmt.setString(2, record.getRequisitionUnit());
        pstmt.setString(3, record.getReason());
        pstmt.setString(4, record.getPayee());
        pstmt.setDouble(5, record.getRequestedAmount());
        pstmt.setDouble(6, record.getGivenAmount());
        pstmt.setString(7, record.getGivenBy());
        pstmt.setDate(8, Date.valueOf(record.getRequestDate()));
        pstmt.setDate(9, Date.valueOf(record.getCompletedDate()));
        
        // Set signatures and fingerprints
        setBlobParameter(pstmt, 10, payeeSignature);
        setBlobParameter(pstmt, 11, dispenserSignature);
        setBlobParameter(pstmt, 12, payeeFingerprint);
        setBlobParameter(pstmt, 13, dispenserFingerprint);
        
        pstmt.setString(14, record.getVerificationMethod());
        pstmt.executeUpdate();
        pstmt.close();

        // ----------------------------
        // 3. Update available amount in purchase fund replenish request
        // ----------------------------
        String selectAvailableSQL = "SELECT request_id, available_amount FROM purchase_fund_replenish_requests " +
                                    "WHERE current_status = ? AND dispense_approval_status = ?";
        pstmt = con.prepareStatement(selectAvailableSQL);
        pstmt.setString(1, "In Use");
        pstmt.setString(2, "Approved");
        ResultSet rs = pstmt.executeQuery();

        double currentAvailable = 0;
        String replenishRequestId = null;
        if (rs.next()) {
            currentAvailable = rs.getDouble("available_amount");
            replenishRequestId = rs.getString("request_id"); // capture the correct replenish request
        }
        rs.close();
        pstmt.close();

        if (replenishRequestId == null) {
            con.rollback();
            System.out.println("❌ No 'In Use' purchase fund replenish request found to update available amount.");
            return false;
        }

        double givenAmount = record.getGivenAmount();
        double newAvailable = currentAvailable - givenAmount;
        if (newAvailable < 0) newAvailable = 0; // avoid negative

        // Determine if we need to update the status to "Completed"
        String updateAvailableSQL;
        if (newAvailable == 0) {
            // If available amount becomes 0, change status to "Completed"
            updateAvailableSQL = "UPDATE purchase_fund_replenish_requests SET available_amount = ?, current_status = 'Completed', updated_at = GETDATE() " +
                                "WHERE request_id = ?";
            System.out.println("✅ Available amount reached 0. Changing status to 'Completed' for purchase fund request: " + replenishRequestId);
        } else {
            // Otherwise just update the available amount
            updateAvailableSQL = "UPDATE purchase_fund_replenish_requests SET available_amount = ?, updated_at = GETDATE() " +
                                "WHERE request_id = ?";
        }

        pstmt = con.prepareStatement(updateAvailableSQL);
        pstmt.setDouble(1, newAvailable);
        pstmt.setString(2, replenishRequestId); // use the replenish request ID
        pstmt.executeUpdate();
        pstmt.close();
        
        con.commit();
        
        System.out.println("✅ Dual signature dispense record saved successfully for request: " + record.getRequestId());
        System.out.println("✅ New available amount: " + newAvailable + " for purchase fund replenish request: " + replenishRequestId);
        return true;
        
    } catch (SQLException e) {
        try {
            if (con != null) con.rollback();
        } catch (SQLException ex) {
            System.out.println("❌ Rollback failed: " + ex.getMessage());
        }
        System.out.println("❌ Error saving dual signature dispense record: " + e.getMessage());
        return false;
    } finally {
        closeResources(null, pstmt, null);
    }
}
 
    
    
    public List<ImageView> loadPayeeIdCardImages(String requestId) {
    List<ImageView> images = new ArrayList<>();
    String sql = "SELECT file_name, file_type, file_data FROM agreement_based_purchase_fund_requests_payee_id_cards WHERE request_id = ?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, requestId);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                byte[] fileData = rs.getBytes("file_data");
                if (fileData != null && fileData.length > 0) {
                    ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(fileData)));
                    imageView.setFitWidth(250);
                    imageView.setFitHeight(150);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                    imageView.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-padding: 5;");
                    Tooltip.install(imageView, new Tooltip(rs.getString("file_name")));
                    images.add(imageView);
                }
            }
        }
    } catch (SQLException e) {
        System.out.println("❌ Error loading ID card images: " + e.getMessage());
    }
    return images;
}
    
    public Image loadSignature(String employeeName) {
        if (employeeName == null || employeeName.trim().isEmpty()) return null;

        String sql = "SELECT signature_image FROM employee_signatures WHERE employee_name = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, employeeName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                byte[] imgBytes = rs.getBytes("signature_image");
                if (imgBytes != null && imgBytes.length > 0) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(imgBytes);
                    return new Image(bis);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // return null if not found or error
    }

    
   // fetch all users from database where user is Admin
    public boolean isAdmin(String username) {
        boolean isAdmin=false;
        String sql = "SELECT role FROM members WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                if(role.equals("Admin")){
                isAdmin=true;
                }
                else{
                isAdmin=false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isAdmin;
    } 
    
    // fetch all users from database where user is Admin
    public boolean isFinanceAdmin(String username) {
        boolean isFinanceAdmin=false;
        String sql = "SELECT role FROM members WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                if(role.equals("FinanceAdmin")){
                isFinanceAdmin=true;
                }
                else{
                isFinanceAdmin=false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isFinanceAdmin;
    } 
    
    // fetch all users from database where user is Admin
    public boolean isReplenishDispenser(String username) {
        boolean isReplenishDispenser=false;
        String sql = "SELECT role FROM members WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                if(role.equals("ReplenishDispenser")){
                isReplenishDispenser=true;
                }
                else{
                isReplenishDispenser=false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isReplenishDispenser;
    } 
    
    // fetch all users from database where user is Admin
    public boolean isAccountant(String username) {
        boolean isAccountant=false;
        String sql = "SELECT role FROM members WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                if(role.equals("Accountant")){
                isAccountant=true;
                }
                else{
                isAccountant=false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isAccountant;
    } 
    
        // fetch all users from database where user is Admin
    public boolean isCashier(String username) {
        boolean isCashier=false;
        String sql = "SELECT role FROM members WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                if(role.equals("Cashier")){
                isCashier=true;
                }
                else{
                isCashier=false;
                }
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return isCashier;
    } 

    double getMaximumRequest(String fundType) {
      double max=0;
        String sql = "SELECT maximum_amount FROM maximum_amount_to_request WHERE fund_type = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fundType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                max=Double.parseDouble(rs.getString("maximum_amount"));
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return max;
    }

    double getAvailablePettyCashAmount() {
        double availableAmount=0;
        String sql = "SELECT available_amount FROM petty_cash_replenish_requests WHERE current_status = ? AND dispense_approval_status = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "In Use");
            ps.setString(2, "Approved");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                availableAmount=Double.parseDouble(rs.getString("available_amount"));
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return availableAmount;
        
    }
    
    double getAvailableAgreementBasedPurchaseFundAmount() {
        double availableAmount=0;
        String sql = "SELECT available_amount FROM purchase_fund_replenish_requests WHERE current_status = ? AND dispense_approval_status = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "In Use");
            ps.setString(2, "Approved");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                availableAmount=Double.parseDouble(rs.getString("available_amount"));
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return availableAmount;
        
    }
    
    double getAvailableRecieptBasedPurchaseFundAmount() {
        double availableAmount=0;
        String sql = "SELECT available_amount FROM purchase_fund_replenish_requests WHERE current_status = ? AND dispense_approval_status = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "In Use");
            ps.setString(2, "Approved");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                availableAmount=Double.parseDouble(rs.getString("available_amount"));
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        return availableAmount;
        
    }
    
    // -----------------------------------
    // GET ALL MAXIMUM AMOUNT RECORDS
    // -----------------------------------
    public List<MaximumAmountModel> getAllMaximumAmountRecords() {
        List<MaximumAmountModel> list = new ArrayList<>();
        String sql = "SELECT id, fund_type, maximum_amount FROM maximum_amount_to_request ORDER BY id DESC";

        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                MaximumAmountModel record = new MaximumAmountModel(
                        rs.getInt("id"),
                        rs.getString("fund_type"),
                        rs.getString("maximum_amount")
                );
                list.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // -----------------------------------
    // INSERT NEW RECORD

    // -----------------------------------
    // UPDATE RECORD
    // -----------------------------------
    public boolean updateMaximumAmountRecord(int id, String fundType, String maxAmount) {
        String sql = "UPDATE maximum_amount_to_request SET fund_type = ?, maximum_amount = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fundType);
            ps.setString(2, maxAmount);
            ps.setInt(3, id);

            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // -----------------------------------
    // DELETE RECORD
    // -----------------------------------
    public boolean deleteMaximumAmountRecord(int id) {
        String sql = "DELETE FROM maximum_amount_to_request WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Optional: close connection
    public void closeConnection() {
        try {
            if (con != null && !con.isClosed()) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    public ObservableList<String> getPettyMainCategories() {
    ObservableList<String> list = FXCollections.observableArrayList();

    String sql = "SELECT name FROM petty_cash_fund_main_categories ORDER BY name ASC";

    try (PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            list.add(rs.getString("name"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

    public boolean addPettyMainCategory(String name) {
    String sql = "INSERT INTO petty_cash_fund_main_categories (name) VALUES (?)";

    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, name);
        return pst.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    public boolean updatePettyMainCategory(String oldName, String newName) {
    String sql = "UPDATE petty_cash_fund_main_categories SET name = ? WHERE name = ?";

    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, newName);
        pst.setString(2, oldName);
        return pst.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    public boolean deletePettyMainCategory(String name) {
    String sql = "DELETE FROM petty_cash_fund_main_categories WHERE name = ?";

    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, name);
        return pst.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    public ObservableList<String> getPettySubCategories(String mainCategoryName) {
    ObservableList<String> list = FXCollections.observableArrayList();

    String sql = "SELECT s.name FROM petty_cash_fund_sub_categories s JOIN petty_cash_fund_main_categories m ON s.main_category_id = m.id WHERE m.name = ? ORDER BY s.name ASC";

    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, mainCategoryName);

        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            list.add(rs.getString("name"));
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

    public boolean addPettySubCategory(String mainCategoryName, String subName) {
    String sql = "INSERT INTO petty_cash_fund_sub_categories (main_category_id, name) SELECT id, ? FROM petty_cash_fund_main_categories WHERE name = ?";

    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, subName);
        pst.setString(2, mainCategoryName);
        return pst.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
    public boolean updatePettySubCategory(String mainCategoryName, String oldName, String newName) {
    String sql = "UPDATE s SET s.name = ? FROM petty_cash_fund_sub_categories s JOIN petty_cash_fund_main_categories m ON s.main_category_id = m.id WHERE m.name = ? AND s.name = ?";

    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, newName);
        pst.setString(2, mainCategoryName);
        pst.setString(3, oldName);
        return pst.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    public boolean deletePettySubCategory(String mainCategoryName, String subName) {
    String sql = "DELETE s FROM petty_cash_fund_sub_categories s JOIN petty_cash_fund_main_categories m ON s.main_category_id = m.id WHERE m.name = ? AND s.name = ?";

    try (PreparedStatement pst = con.prepareStatement(sql)) {
        pst.setString(1, mainCategoryName);
        pst.setString(2, subName);
        return pst.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
    
    
    
     // ===================== MAIN CATEGORY METHODS =====================
    public ObservableList<String> getAgreementMainCategories() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT name FROM agreement_based_purchase_fund_main_categories ORDER BY name";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading main categories: " + e.getMessage());
        }

        return list;
    }

    public boolean addAgreementMainCategory(String name) {
        String sql = "INSERT INTO agreement_based_purchase_fund_main_categories (name) VALUES (?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, name);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error adding main category: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAgreementMainCategory(String oldName, String newName) {
        String sql = "UPDATE agreement_based_purchase_fund_main_categories SET name = ? WHERE name = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, oldName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating main category: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAgreementMainCategory(String name) {
        String sql = "DELETE FROM agreement_based_purchase_fund_main_categories WHERE name = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, name);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error deleting main category: " + e.getMessage());
            return false;
        }
    }

    // ===================== SUBCATEGORY METHODS =====================
    public ObservableList<String> getAgreementSubCategories(String mainCategoryName) {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT s.name FROM agreement_based_purchase_fund_sub_categories s INNER JOIN agreement_based_purchase_fund_main_categories m ON s.main_category_id = m.id WHERE m.name = ? ORDER BY s.name";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, mainCategoryName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading subcategories: " + e.getMessage());
        }

        return list;
    }

    public boolean addAgreementSubCategory(String mainCategoryName, String subCategoryName) {
        String sql = "INSERT INTO agreement_based_purchase_fund_sub_categories (main_category_id, name) VALUES ((SELECT id FROM agreement_based_purchase_fund_main_categories WHERE name = ?), ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, mainCategoryName);
            pstmt.setString(2, subCategoryName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error adding subcategory: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAgreementSubCategory(String mainCategoryName, String oldSubCategory, String newSubCategory) {
        String sql = "UPDATE s SET s.name = ? FROM agreement_based_purchase_fund_sub_categories s INNER JOIN agreement_based_purchase_fund_main_categories m ON s.main_category_id = m.id WHERE s.name = ? AND m.name = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, newSubCategory);
            pstmt.setString(2, oldSubCategory);
            pstmt.setString(3, mainCategoryName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating subcategory: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAgreementSubCategory(String mainCategoryName, String subCategoryName) {
        String sql = "DELETE s FROM agreement_based_purchase_fund_sub_categories s INNER JOIN agreement_based_purchase_fund_main_categories m ON s.main_category_id = m.id WHERE s.name = ? AND m.name = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, subCategoryName);
            pstmt.setString(2, mainCategoryName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error deleting subcategory: " + e.getMessage());
            return false;
        }
    }

   public boolean hasActiveReplenishmentFundInUse() {
    String sql = "SELECT COUNT(*) FROM purchase_fund_replenish_requests WHERE current_status = 'In Use'";
    
    try (PreparedStatement pstmt = con.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        if (rs.next()) {
            int count = rs.getInt(1);
            return count > 0; // Returns true if there's at least one active fund
        }
    } catch (SQLException e) {
        System.err.println("❌ Error checking active replenishment funds: " + e.getMessage());
        e.printStackTrace();
    }
    
    return false; // Return false if there's an error (safer to allow than to block)
}

    boolean hasActivePettyCashReplenishmentFundInUse() {
       String sql = "SELECT COUNT(*) FROM petty_cash_replenish_requests WHERE current_status = 'In Use'";
    
    try (PreparedStatement pstmt = con.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        if (rs.next()) {
            int count = rs.getInt(1);
            return count > 0; // Returns true if there's at least one active fund
        }
    } catch (SQLException e) {
        System.err.println("❌ Error checking active replenishment funds: " + e.getMessage());
        e.printStackTrace();
    }
    
    return false; // Return false if there's an error (safer to allow than to block)
    }

    
// Enhanced Role management methods
public ObservableList<String> getSystemRoles() {
    ObservableList<String> roles = FXCollections.observableArrayList();
    String query = "SELECT name FROM system_roles ORDER BY name";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            roles.add(rs.getString("name"));
        }
    } catch (SQLException e) {
        System.err.println("Error loading system roles: " + e.getMessage());
        e.printStackTrace();
    }
    return roles;
}

public boolean addSystemRole(String roleName, String description) {
    String query = "INSERT INTO system_roles (name, description) VALUES (?, ?)";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query)) {
        
        pstmt.setString(1, roleName.trim());
        pstmt.setString(2, description);
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
            System.err.println("Role already exists: " + roleName);
        } else {
            System.err.println("Error adding system role: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}

public boolean updateSystemRole(String oldName, String newName, String description) {
    String query = "UPDATE system_roles SET name = ?, description = ? WHERE name = ?";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query)) {
        
        pstmt.setString(1, newName.trim());
        pstmt.setString(2, description);
        pstmt.setString(3, oldName);
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
            System.err.println("Role name already exists: " + newName);
        } else {
            System.err.println("Error updating system role: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}

public String getRoleDescription(String roleName) {
    String query = "SELECT description FROM system_roles WHERE name = ?";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query)) {
        
        pstmt.setString(1, roleName);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("description");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error getting role description: " + e.getMessage());
        e.printStackTrace();
    }
    return "";
}

// Enhanced Permission management methods

// Add these methods to your Connecting class

public ObservableList<SystemPermission> getAllSystemPermissions() {
    ObservableList<SystemPermission> permissions = FXCollections.observableArrayList();
    String query = "SELECT permission_code, permission_name, permission_description, permission_category, is_active " +
                   "FROM system_permissions_master WHERE is_active = 1 ORDER BY permission_category, permission_name";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            permissions.add(new SystemPermission(
                rs.getString("permission_code"),
                rs.getString("permission_name"),
                rs.getString("permission_description"),
                rs.getString("permission_category"),
                rs.getBoolean("is_active")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return permissions;
}

public ObservableList<SystemPermission> getRolePermissions(String roleName) {
    ObservableList<SystemPermission> permissions = FXCollections.observableArrayList();
    String query = "SELECT rp.permission_name, rp.permission_description, rp.is_active, spm.permission_code, spm.permission_category " +
                   "FROM role_permissions rp " +
                   "INNER JOIN system_roles sr ON rp.role_id = sr.id " +
                   "LEFT JOIN system_permissions_master spm ON rp.permission_name = spm.permission_name " +
                   "WHERE sr.name = ?";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query)) {
        
        pstmt.setString(1, roleName);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            permissions.add(new SystemPermission(
                rs.getString("permission_code"),
                rs.getString("permission_name"),
                rs.getString("permission_description"),
                rs.getString("permission_category"),
                rs.getBoolean("is_active")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return permissions;
}

public boolean toggleRolePermission(String roleName, String permissionCode, boolean isActive) {
    // First, check if the permission already exists for this role
    String checkQuery = "SELECT COUNT(*) FROM role_permissions rp " +
                       "INNER JOIN system_roles sr ON rp.role_id = sr.id " +
                       "INNER JOIN system_permissions_master spm ON rp.permission_name = spm.permission_name " +
                       "WHERE sr.name = ? AND spm.permission_code = ?";
    
    try {
        // Check if record exists
        int count = 0;
        try (PreparedStatement checkStmt = con.prepareStatement(checkQuery)) {
            checkStmt.setString(1, roleName);
            checkStmt.setString(2, permissionCode);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        }
        
        if (count > 0) {
            // Update existing record
            String updateQuery = "UPDATE rp SET rp.is_active = ? " +
                               "FROM role_permissions rp " +
                               "INNER JOIN system_roles sr ON rp.role_id = sr.id " +
                               "INNER JOIN system_permissions_master spm ON rp.permission_name = spm.permission_name " +
                               "WHERE sr.name = ? AND spm.permission_code = ?";
            
            try (PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {
                updateStmt.setBoolean(1, isActive);
                updateStmt.setString(2, roleName);
                updateStmt.setString(3, permissionCode);
                return updateStmt.executeUpdate() > 0;
            }
        } else {
            // Insert new record
            String insertQuery = "INSERT INTO role_permissions (role_id, permission_name, permission_description, is_active, created_date) " +
                               "SELECT sr.id, spm.permission_name, spm.permission_description, ?, GETDATE() " +
                               "FROM system_roles sr, system_permissions_master spm " +
                               "WHERE sr.name = ? AND spm.permission_code = ?";
            
            try (PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
                insertStmt.setBoolean(1, isActive);
                insertStmt.setString(2, roleName);
                insertStmt.setString(3, permissionCode);
                return insertStmt.executeUpdate() > 0;
            }
        }
    } catch (SQLException e) {
        System.err.println("Error toggling permission: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

public boolean bulkUpdateRolePermissions(String roleName, Map<String, Boolean> permissionStates) {
    try  {
        con.setAutoCommit(false);
        
        // First, deactivate all current permissions
        String clearQuery = "UPDATE rp SET rp.is_active = 0 " +
                           "FROM role_permissions rp " +
                           "INNER JOIN system_roles sr ON rp.role_id = sr.id " +
                           "WHERE sr.name = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(clearQuery)) {
            pstmt.setString(1, roleName);
            pstmt.executeUpdate();
        }
        
        // Then activate the selected ones
        String insertQuery = "MERGE INTO role_permissions AS target " +
                            "USING (SELECT sr.id as role_id, spm.permission_name, spm.permission_description " +
                            "       FROM system_roles sr, system_permissions_master spm " +
                            "       WHERE sr.name = ? AND spm.permission_code = ?) AS source " +
                            "ON target.role_id = source.role_id AND target.permission_name = source.permission_name " +
                            "WHEN MATCHED THEN " +
                            "    UPDATE SET is_active = 1 " +
                            "WHEN NOT MATCHED THEN " +
                            "    INSERT (role_id, permission_name, permission_description, is_active, created_date) " +
                            "    VALUES (source.role_id, source.permission_name, source.permission_description, 1, GETDATE());";
        
        for (Map.Entry<String, Boolean> entry : permissionStates.entrySet()) {
            if (entry.getValue()) { // If permission should be active
                try (PreparedStatement pstmt = con.prepareStatement(insertQuery)) {
                    pstmt.setString(1, roleName);
                    pstmt.setString(2, entry.getKey());
                    pstmt.executeUpdate();
                }
            }
        }
        
        con.commit();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean addSystemPermission(String code, String name, String description, String category) {
    String query = "INSERT INTO system_permissions_master (permission_code, permission_name, permission_description, permission_category, is_active, created_date) " +
                   "VALUES (?, ?, ?, ?, 1, GETDATE())";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query)) {
        
        pstmt.setString(1, code);
        pstmt.setString(2, name);
        pstmt.setString(3, description);
        pstmt.setString(4, category);
        
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean addRolePermission(String roleName, String permissionCode, String permissionName, String description) {
    String query = "INSERT INTO role_permissions (role_id, permission_name, permission_description, is_active, created_date) " +
                   "SELECT id, ?, ?, 1, GETDATE() FROM system_roles WHERE name = ?";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query)) {
        
        pstmt.setString(1, permissionName);
        pstmt.setString(2, description);
        pstmt.setString(3, roleName);
        
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean addRolePermission(String roleName, String permission, String description) {
    String query = "INSERT INTO role_permissions (role_id, permission_name, permission_description) " +
                   "SELECT id, ?, ? FROM system_roles WHERE name = ?";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query)) {
        
        pstmt.setString(1, permission.trim());
        pstmt.setString(2, description);
        pstmt.setString(3, roleName);
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
            System.err.println("Permission already exists for this role: " + permission);
        } else {
            System.err.println("Error adding role permission: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}

public boolean updateRolePermission(String roleName, String oldPermission, String newPermission, String description) {
    String query = "UPDATE role_permissions SET permission_name = ?, permission_description = ? " +
                   "WHERE role_id = (SELECT id FROM system_roles WHERE name = ?) " +
                   "AND permission_name = ?";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query)) {
        
        pstmt.setString(1, newPermission.trim());
        pstmt.setString(2, description);
        pstmt.setString(3, roleName);
        pstmt.setString(4, oldPermission);
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
            System.err.println("Permission already exists for this role: " + newPermission);
        } else {
            System.err.println("Error updating role permission: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}



public String getPermissionDescription(String roleName, String permission) {
    String query = "SELECT permission_description FROM role_permissions rp " +
                   "JOIN system_roles sr ON rp.role_id = sr.id " +
                   "WHERE sr.name = ? AND rp.permission_name = ?";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query)) {
        
        pstmt.setString(1, roleName);
        pstmt.setString(2, permission);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("permission_description");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error getting permission description: " + e.getMessage());
        e.printStackTrace();
    }
    return "";
}

public boolean deleteRolePermission(String roleName, String permission) {
    String query = "DELETE FROM role_permissions " +
                   "WHERE role_id = (SELECT id FROM system_roles WHERE name = ?) " +
                   "AND permission_name = ?";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query)) {
        
        pstmt.setString(1, roleName);
        pstmt.setString(2, permission);
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        System.err.println("Error deleting role permission: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

public boolean deleteSystemRole(String roleName) {
    String query = "DELETE FROM system_roles WHERE name = ?";
    
    try (
         PreparedStatement pstmt = con.prepareStatement(query)) {
        
        pstmt.setString(1, roleName);
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        if (e.getErrorCode() == 547) { // Foreign key constraint violation
            System.err.println("Cannot delete role '" + roleName + "' - it has dependencies (permissions or users assigned)");
            // You might want to show a more specific message based on what dependencies exist
        } else {
            System.err.println("Error deleting system role '" + roleName + "': " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}

// Add these methods to Connecting.java

public boolean vatSalesReceiptExists(String receipt) throws Exception {
    String sql = "SELECT COUNT(*) FROM VatSales WHERE ReceiptNumber = ?";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, receipt);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }
}
public void saveVatSales(VatSaleModel sale) throws Exception {
    if (vatSalesReceiptExists(sale.getReceiptNumber()))
        throw new Exception("Receipt number already exists!");

    String sql = "INSERT INTO VatSales (VatCategory, CalendarType, SaleType, BuyerTIN, BuyerName, " +
                 "SaleDate, MRCNumber, ReceiptNumber, Description, UnitMeasure, Quantity, " +
                 "UnitPrice, TotalValue, VatAmount, TotalAfterVat, CreatedAt, Voided, CreatedBy) " +
                 "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,GETDATE(),0,?)"; // Added CreatedBy

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, sale.getVatCategory());
        ps.setString(2, sale.getCalendarType());
        ps.setInt(3, sale.getSaleType());
        ps.setString(4, sale.getBuyerTin());
        ps.setString(5, sale.getBuyerName());
        ps.setDate(6, Date.valueOf(sale.getDateOfSale()));
        ps.setString(7, sale.getMrcNumber());
        ps.setString(8, sale.getReceiptNumber());
        ps.setString(9, sale.getDescription());
        ps.setInt(10, sale.getUnitMeasure());
        ps.setDouble(11, sale.getQuantity());
        ps.setDouble(12, sale.getUnitPrice());
        ps.setDouble(13, sale.getTotalValue());
        ps.setDouble(14, sale.getVatAmount());
        ps.setDouble(15, sale.getTotalAfterVat());
        ps.setString(16, sale.getCreatedBy()); // Set created by user

        ps.executeUpdate();
    } catch (SQLException e) {
        throw new Exception("Database error: " + e.getMessage());
    }
}

public boolean voidVatSale(String receiptNumber, String voidedBy, String voidReason) throws Exception {
    String checkSql = "SELECT Id, Voided FROM VatSales WHERE ReceiptNumber = ?";
    String updateSql = "UPDATE VatSales SET Voided = 1, VoidedAt = GETDATE(), " +
                       "VoidedBy = ?, VoidReason = ? WHERE ReceiptNumber = ? AND Voided = 0";
    
    try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
        checkPs.setString(1, receiptNumber);
        ResultSet rs = checkPs.executeQuery();
        
        if (!rs.next()) {
            throw new Exception("Receipt not found: " + receiptNumber);
        }
        
        if (rs.getBoolean("Voided")) {
            throw new Exception("Receipt already voided: " + receiptNumber);
        }
        
        try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
            updatePs.setString(1, voidedBy);
            updatePs.setString(2, voidReason);
            updatePs.setString(3, receiptNumber);
            
            int rowsAffected = updatePs.executeUpdate();
            return rowsAffected > 0;
        }
    }
}

public boolean unvoidVatSale(String receiptNumber) {
    
    PreparedStatement pstmt = null;
    try {
        
        String sql = "UPDATE VatSales SET " +
                     "Voided = 0, " +
                     "VoidedBy = NULL, " +
                     "VoidedAt = NULL, " +
                     "VoidReason = NULL " +
                     "WHERE ReceiptNumber = ? AND Voided = 1";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, receiptNumber);
        
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    } finally {
      //  closeResources(null, pstmt, (ResultSet) con);
    }
}

public List<VatSaleModel> fetchAllVatSales(int offset, int limit) {
    List<VatSaleModel> list = new ArrayList<>();
    try {
        String sql = "SELECT * FROM VatSales ORDER BY CreatedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, offset);
        ps.setInt(2, limit);
        
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            VatSaleModel sale = new VatSaleModel(
                rs.getString("VatCategory"),
                rs.getString("CalendarType"),
                rs.getInt("SaleType"),
                rs.getString("BuyerTIN"),
                rs.getString("BuyerName"),
                rs.getDate("SaleDate").toString(),
                rs.getString("MRCNumber"),
                rs.getString("ReceiptNumber"),
                rs.getString("Description"),
                rs.getInt("UnitMeasure"),
                rs.getDouble("Quantity"),
                rs.getDouble("UnitPrice"),
                rs.getDouble("TotalValue"),
                rs.getDouble("VatAmount"),
                rs.getDouble("TotalAfterVat"),
                rs.getBoolean("Voided"),
                rs.getString("VoidedAt"),
                rs.getString("VoidedBy"),
                rs.getString("VoidReason"),
                rs.getString("CreatedBy") // Get created by from database
            );
            
            list.add(sale);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return list;
}

public List<VatSaleModel> fetchActiveVatSales(int offset, int limit) throws Exception {
    String sql = "SELECT *, 'ACTIVE' AS Status " +
                 "FROM VatSales WHERE Voided = 0 " +
                 "ORDER BY Id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    List<VatSaleModel> list = new ArrayList<>();

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, offset);
        ps.setInt(2, limit);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new VatSaleModel(
                rs.getString("VatCategory"),
                rs.getString("CalendarType"),
                rs.getInt("SaleType"),
                rs.getString("BuyerTIN"),
                rs.getString("BuyerName"),
                rs.getDate("SaleDate").toString(),
                rs.getString("MRCNumber"),
                rs.getString("ReceiptNumber"),
                rs.getString("Description"),
                rs.getInt("UnitMeasure"),
                rs.getDouble("Quantity"),
                rs.getDouble("UnitPrice"),
                rs.getDouble("TotalValue"),
                rs.getDouble("VatAmount"),
                rs.getDouble("TotalAfterVat")
            ));
        }
    }
    return list;
}

 
 // Add these methods to Connecting.java

public boolean vatPurchaseReceiptExists(String receipt) throws Exception {
    String sql = "SELECT COUNT(*) FROM VatPurchases WHERE ReceiptNumber = ?";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, receipt);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }
}

public void saveVatPurchases(VatPurchaseModel purchase) throws Exception {
    if (vatPurchaseReceiptExists(purchase.getReceiptNumber()))
        throw new Exception("Receipt number already exists!");

    String sql = "INSERT INTO VatPurchases (VatCategory, CalendarType, PurchaseType, SellerTIN, SellerName, " +
                 "PurchaseDate, MRCNumber, ReceiptNumber, Description, UnitMeasure, Quantity, " +
                 "UnitPrice, TotalValue, VatAmount, TotalAfterVat, CreatedAt, Voided, CreatedBy) " +
                 "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,GETDATE(),0,?)"; // Added CreatedBy

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, purchase.getVatCategory());
        ps.setString(2, purchase.getCalendarType());
        ps.setInt(3, purchase.getPurchaseType());
        ps.setString(4, purchase.getSellerTin());
        ps.setString(5, purchase.getSellerName());
        ps.setDate(6, Date.valueOf(purchase.getDateOfPurchase()));
        ps.setString(7, purchase.getMrcNumber());
        ps.setString(8, purchase.getReceiptNumber());
        ps.setString(9, purchase.getDescription());
        ps.setInt(10, purchase.getUnitMeasure());
        ps.setDouble(11, purchase.getQuantity());
        ps.setDouble(12, purchase.getUnitPrice());
        ps.setDouble(13, purchase.getTotalValue());
        ps.setDouble(14, purchase.getVatAmount());
        ps.setDouble(15, purchase.getTotalAfterVat());
        ps.setString(16, purchase.getCreatedBy()); // Set created by user

        ps.executeUpdate();
    } catch (SQLException e) {
        throw new Exception("Database error: " + e.getMessage());
    }
}

public boolean voidVatPurchase(String receiptNumber, String voidedBy, String voidReason) throws Exception {
    String checkSql = "SELECT Id, Voided FROM VatPurchases WHERE ReceiptNumber = ?";
    String updateSql = "UPDATE VatPurchases SET Voided = 1, VoidedAt = GETDATE(), " +
                       "VoidedBy = ?, VoidReason = ? WHERE ReceiptNumber = ? AND Voided = 0";
    
    try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
        checkPs.setString(1, receiptNumber);
        ResultSet rs = checkPs.executeQuery();
        
        if (!rs.next()) {
            throw new Exception("Receipt not found: " + receiptNumber);
        }
        
        if (rs.getBoolean("Voided")) {
            throw new Exception("Receipt already voided: " + receiptNumber);
        }
        
        try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
            updatePs.setString(1, voidedBy);
            updatePs.setString(2, voidReason);
            updatePs.setString(3, receiptNumber);
            
            int rowsAffected = updatePs.executeUpdate();
            return rowsAffected > 0;
        }
    }
}

public boolean unvoidVatPurchase(String receiptNumber) {
    
    PreparedStatement pstmt = null;
    try {
        
        String sql = "UPDATE VatPurchases SET " +
                     "Voided = 0, " +
                     "VoidedBy = NULL, " +
                     "VoidedAt = NULL, " +
                     "VoidReason = NULL " +
                     "WHERE ReceiptNumber = ? AND Voided = 1";
        
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, receiptNumber);
        
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    } finally {
        // closeResources(null, pstmt, null);
    }
}

public List<VatPurchaseModel> fetchAllVatPurchases(int offset, int limit) throws Exception {
    String sql = "SELECT *, " +
                 "CASE WHEN Voided = 1 THEN 'VOIDED' ELSE 'ACTIVE' END AS Status " +
                 "FROM VatPurchases ORDER BY Id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    List<VatPurchaseModel> list = new ArrayList<>();

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, offset);
        ps.setInt(2, limit);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new VatPurchaseModel(
                rs.getString("VatCategory"),
                rs.getString("CalendarType"),
                rs.getInt("PurchaseType"),
                rs.getString("SellerTIN"),
                rs.getString("SellerName"),
                rs.getDate("PurchaseDate").toString(),
                rs.getString("MRCNumber"),
                rs.getString("ReceiptNumber"),
                rs.getString("Description"),
                rs.getInt("UnitMeasure"),
                rs.getDouble("Quantity"),
                rs.getDouble("UnitPrice"),
                rs.getDouble("TotalValue"),
                rs.getDouble("VatAmount"),
                rs.getDouble("TotalAfterVat"),
                rs.getBoolean("Voided"),
                rs.getTimestamp("VoidedAt") != null ? 
                    rs.getTimestamp("VoidedAt").toString() : null,
                rs.getString("VoidedBy"),
                rs.getString("VoidReason"),
                rs.getString("CreatedBy") // Get created by from database
            ));
        }
    } catch (SQLException e) {
        throw new Exception("Error fetching purchases: " + e.getMessage());
    }
    return list;
}

public List<VatPurchaseModel> fetchActiveVatPurchases(int offset, int limit) throws Exception {
    String sql = "SELECT *, 'ACTIVE' AS Status " +
                 "FROM VatPurchases WHERE Voided = 0 " +
                 "ORDER BY Id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

    List<VatPurchaseModel> list = new ArrayList<>();

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, offset);
        ps.setInt(2, limit);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new VatPurchaseModel(
                rs.getString("VatCategory"),
                rs.getString("CalendarType"),
                rs.getInt("PurchaseType"),
                rs.getString("SellerTIN"),
                rs.getString("SellerName"),
                rs.getDate("PurchaseDate").toString(),
                rs.getString("MRCNumber"),
                rs.getString("ReceiptNumber"),
                rs.getString("Description"),
                rs.getInt("UnitMeasure"),
                rs.getDouble("Quantity"),
                rs.getDouble("UnitPrice"),
                rs.getDouble("TotalValue"),
                rs.getDouble("VatAmount"),
                rs.getDouble("TotalAfterVat")
            ));
        }
    }
    return list;
}

}

    
   
