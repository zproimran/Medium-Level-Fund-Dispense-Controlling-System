package smarthrms;

import java.time.LocalDate;

public class EmployeeLoanRecordModel {

    private String requestId;
    private String employeeCode;
    private String employeeName;
    private String loanType;
    private double amount;
    private LocalDate requestDate;
    private String status;
    private String approvedBy;

    public EmployeeLoanRecordModel(String requestId,String employeeCode, String employeeName, String loanType,
                                   double amount, LocalDate requestDate, String status, String approvedBy) {
        this.requestId = requestId;
        this.employeeCode=employeeCode;
        this.employeeName = employeeName;
        this.loanType = loanType;
        this.amount = amount;
        this.requestDate = requestDate;
        this.status = status;
        this.approvedBy = approvedBy;
    }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    
    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
}
