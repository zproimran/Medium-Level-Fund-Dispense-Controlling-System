package smarthrms;


import java.time.LocalDate;

    // Model class
    public class EmployeeDeductionRecordModel {
        private String employeeId;
        private String employeeName;
        private String department;
        private String deductionType;
        private double amount;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private String approvedBy;

        public EmployeeDeductionRecordModel(String employeeId, String employeeName, String department,
                               String deductionType, double amount,
                               LocalDate startDate, LocalDate endDate,
                               String status, String approvedBy) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.department = department;
            this.deductionType = deductionType;
            this.amount = amount;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
            this.approvedBy = approvedBy;
        }

        // Getters & Setters
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }

        public String getDeductionType() { return deductionType; }
        public void setDeductionType(String deductionType) { this.deductionType = deductionType; }

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