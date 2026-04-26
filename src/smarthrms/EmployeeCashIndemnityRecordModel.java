package smarthrms;


import java.time.LocalDate;


    // Model
    public class EmployeeCashIndemnityRecordModel {
        private String employeeCode;
        private String employeeName;
        private String reason;
        private double amount;
        private LocalDate date;
        private String status;
        private String approvedBy;

        public EmployeeCashIndemnityRecordModel(String employeeCode, String employeeName, String reason, double amount, LocalDate date, String status, String approvedBy) {
            this.employeeCode = employeeCode;
            this.employeeName = employeeName;
            this.reason = reason;
            this.amount = amount;
            this.date = date;
            this.status = status;
            this.approvedBy = approvedBy;
        }

        // Getters & Setters
        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getApprovedBy() { return approvedBy; }
        public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    }