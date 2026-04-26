package smarthrms;


import java.time.LocalDate;

    // Model class
    public class EmployeeAllowanceRecordModel {
        private String employeeCode;
        private String employeeName;
        private String empDepartment;
        private String allowanceType;
        private double amount;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private String approvedBy;

        public EmployeeAllowanceRecordModel(String employeeCode, String employeeName, String empDepartment,
                               String allowanceType, double amount,
                               LocalDate startDate, LocalDate endDate,
                               String status, String approvedBy) {
            this.employeeCode = employeeCode;
            this.employeeName = employeeName;
            this.empDepartment = empDepartment;
            this.allowanceType = allowanceType;
            this.amount = amount;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
            this.approvedBy = approvedBy;
        }

        // Getters & Setters
        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
        public String getEmployeeDepartment() { return empDepartment; }
        public void setEmployeeDepartment(String empDepartment) { this.empDepartment = empDepartment; }
        public String getAllowanceType() { return allowanceType; }
        public void setAllowanceType(String allowanceType) { this.allowanceType = allowanceType; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getApprovedBy() { return approvedBy; }
        public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    }