package smarthrms;


import java.time.LocalDate;

    public class EmployeeServiceChargeRecordModel {
        private String employeeCode;
        private String employeeName;
        private String department;
        private String chargeType;
        private double amount;
        private LocalDate applicableDate;
        private String status;
        private String approvedBy;

        public EmployeeServiceChargeRecordModel(String employeeCode, String employeeName, String department,
                                   String chargeType, double amount, LocalDate applicableDate,
                                   String status, String approvedBy) {
            this.employeeCode = employeeCode;
            this.employeeName = employeeName;
            this.department = department;
            this.chargeType = chargeType;
            this.amount = amount;
            this.applicableDate = applicableDate;
            this.status = status;
            this.approvedBy = approvedBy;
        }

        // Getters and setters
        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }

        public String getChargeType() { return chargeType; }
        public void setChargeType(String chargeType) { this.chargeType = chargeType; }

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }

        public LocalDate getApplicableDate() { return applicableDate; }
        public void setApplicableDate(LocalDate applicableDate) { this.applicableDate = applicableDate; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getApprovedBy() { return approvedBy; }
        public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    }