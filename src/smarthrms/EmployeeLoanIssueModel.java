package smarthrms;

import java.time.LocalDate;

public class EmployeeLoanIssueModel {
    private String issueId;
    private String employeeCode;
    private String employeeName;
    private String loanType;
    private double amount;
    private LocalDate issueDate;
    private String status;

    public EmployeeLoanIssueModel(String issueId, String employeeCode, String employeeName, String loanType, double amount, LocalDate issueDate, String status) {
        this.issueId = issueId;
        this.employeeCode=employeeCode;
        this.employeeName = employeeName;
        this.loanType = loanType;
        this.amount = amount;
        this.issueDate = issueDate;
        this.status = status;
    }

    public String getIssueId() { return issueId; }
    public void setIssueId(String issueId) { this.issueId = issueId; }
    
    public String getEmployeeCode(){return employeeCode;}
    public void setEmployeeCode(String employeeCode){this.employeeCode=employeeCode;}
    
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

