package smarthrms;

    // Model class
    public class EmployeeCostSharingRecordModel {
        private String employeeCode;
        private String employeeName;
        private String costType;
        private double totalCost;
        private double employeeShare;
        private double employerShare;

        public EmployeeCostSharingRecordModel(String employeeCode, String employeeName, String costType,
                                 double totalCost, double employeeShare, double employerShare) {
            this.employeeCode = employeeCode;
            this.employeeName = employeeName;
            this.costType = costType;
            this.totalCost = totalCost;
            this.employeeShare = employeeShare;
            this.employerShare = employerShare;
        }

        // Getters & Setters
        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeId) { this.employeeCode = employeeCode; }
        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
        public String getCostType() { return costType; }
        public void setCostType(String costType) { this.costType = costType; }
        public double getTotalCost() { return totalCost; }
        public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
        public double getEmployeeShare() { return employeeShare; }
        public void setEmployeeShare(double employeeShare) { this.employeeShare = employeeShare; }
        public double getEmployerShare() { return employerShare; }
        public void setEmployerShare(double employerShare) { this.employerShare = employerShare; }
    }