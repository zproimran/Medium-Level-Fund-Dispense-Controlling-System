package smarthrms;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.LocalDate;

public class EmployeeSignatureModel {
    private final SimpleStringProperty employeeId;
    private final SimpleStringProperty employeeName;
    private final SimpleStringProperty department;
    private final SimpleStringProperty position;
    private final SimpleObjectProperty<LocalDate> enrollmentDate;
    private final SimpleStringProperty status;
    private byte[] fingerprintTemplate;
    private byte[] signatureImage;
    
    public EmployeeSignatureModel() {
    this.employeeId = new SimpleStringProperty("");
    this.employeeName = new SimpleStringProperty("");
    this.department = new SimpleStringProperty("");
    this.position = new SimpleStringProperty("");
    this.enrollmentDate = new SimpleObjectProperty<>(LocalDate.now());
    this.status = new SimpleStringProperty("");
    this.fingerprintTemplate = null;
    this.signatureImage = null;
}
    
    public EmployeeSignatureModel(String employeeId, String employeeName, String department, 
                                 String position, LocalDate enrollmentDate, String status,
                                 byte[] fingerprintTemplate, byte[] signatureImage) {
        this.employeeId = new SimpleStringProperty(employeeId);
        this.employeeName = new SimpleStringProperty(employeeName);
        this.department = new SimpleStringProperty(department);
        this.position = new SimpleStringProperty(position);
        this.enrollmentDate = new SimpleObjectProperty<>(enrollmentDate);
        this.status = new SimpleStringProperty(status);
        this.fingerprintTemplate = fingerprintTemplate;
        this.signatureImage = signatureImage;
    }

    // Getters
    public String getEmployeeId() { return employeeId.get(); }
    public String getEmployeeName() { return employeeName.get(); }
    public String getDepartment() { return department.get(); }
    public String getPosition() { return position.get(); }
    public LocalDate getEnrollmentDate() { return enrollmentDate.get(); }
    public String getStatus() { return status.get(); }
    public byte[] getFingerprintTemplate() { return fingerprintTemplate; }
    public byte[] getSignatureImage() { return signatureImage; }

    // Setters
    public void setEmployeeId(String employeeId) { this.employeeId.set(employeeId); }
    public void setEmployeeName(String employeeName) { this.employeeName.set(employeeName); }
    public void setDepartment(String department) { this.department.set(department); }
    public void setPosition(String position) { this.position.set(position); }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate.set(enrollmentDate); }
    public void setStatus(String status) { this.status.set(status); }
    public void setFingerprintTemplate(byte[] fingerprintTemplate) { this.fingerprintTemplate = fingerprintTemplate; }
    public void setSignatureImage(byte[] signatureImage) { this.signatureImage = signatureImage; }

    // Property getters for TableView
    public SimpleStringProperty employeeIdProperty() { return employeeId; }
    public SimpleStringProperty employeeNameProperty() { return employeeName; }
    public SimpleStringProperty departmentProperty() { return department; }
    public SimpleStringProperty positionProperty() { return position; }
    public SimpleObjectProperty<LocalDate> enrollmentDateProperty() { return enrollmentDate; }
    public SimpleStringProperty statusProperty() { return status; }

    // Status methods for display
    public String getFingerprintStatus() {
        return (fingerprintTemplate != null && fingerprintTemplate.length > 0) ? "✅ Enrolled" : "❌ Not Enrolled";
    }

    public String getSignatureStatus() {
        return (signatureImage != null && signatureImage.length > 0) ? "✅ Captured" : "❌ Not Captured";
    }
}