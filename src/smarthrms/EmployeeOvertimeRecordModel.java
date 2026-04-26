package smarthrms;

import java.time.LocalDate;
import java.time.LocalTime;

    // Model class
    public  class EmployeeOvertimeRecordModel {
        private String employeeCode;
        private String employeeName;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private double hours;
        private String overtimeType;
        private String payrollPeriod;
        private String status;
        private String approvedBy;

        public EmployeeOvertimeRecordModel(String employeeCode, String employeeName, LocalDate date,
                              LocalTime startTime, LocalTime endTime, double hours,
                              String overtimeType,String payrollPeriod, String status, String approvedBy) {
            this.employeeCode = employeeCode;
            this.employeeName = employeeName;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.hours = hours;
            this.overtimeType = overtimeType;
            this.status = status;
            this.payrollPeriod=payrollPeriod;
            this.approvedBy = approvedBy;
        }

        // Getters & setters
        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

        public double getHours() { return hours; }
        public void setHours(double hours) { this.hours = hours; }

        public String getOvertimeType() { return overtimeType; }
        public void setOvertimeType(String overtimeType) { this.overtimeType = overtimeType; }

        public String getPayrollPeriod() { return payrollPeriod; }
        public void setPayrollPeriod(String payrollPeriod) { this.payrollPeriod = payrollPeriod; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getApprovedBy() { return approvedBy; }
        public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    }