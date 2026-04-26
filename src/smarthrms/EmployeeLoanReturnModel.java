package smarthrms;

import java.time.LocalDate;

public class EmployeeLoanReturnModel {
    private String returnId;
    private String employeeCode;
    private String employeeName;
    private String loanType;
    private double amount;
    private LocalDate returnDate;
    private String status;

    public EmployeeLoanReturnModel(String returnId, String employeeCode, String employeeName, String loanType, double amount, LocalDate returnDate, String status) {
        this.returnId = returnId;
        this.employeeCode=employeeCode;
        this.employeeName = employeeName;
        this.loanType = loanType;
        this.amount = amount;
        this.returnDate = returnDate;
        this.status = status;
    }

    public String getReturnId() { return returnId; }
    public void setReturnId(String returnId) { this.returnId = returnId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    
    public String getEmployeeCode(){return employeeCode;}
    public void setEmployeeCode(String employeeCode){this.employeeCode=employeeCode;}

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
