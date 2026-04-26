package smarthrms;

public class User {
    private int id;
    private String username;
    private String fullName;
    private String email;
    private String role;
    private String status;
    private String department;

    // Constructor
    public User(int id, String username, String fullName, String email, String role, String status,String depart) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.status = status;
        this.department=depart;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String depart) {
        this.department = depart;
    }
    // Override toString() method to display the username in lists
    @Override
    public String toString() {
        return username;
    }
}
