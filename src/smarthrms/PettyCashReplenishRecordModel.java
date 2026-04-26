package smarthrms;

import java.time.LocalDate;

public class PettyCashReplenishRecordModel {

    private String requestId;
    private String requisitionUnit;
    private String reason;
    private String payee;
    private double amountRequested;
    private LocalDate requestDate;
    private String confirmationStatus;
    private String confirmedBy;
    private String approvalStatus;
    private String approvedBy;
    
    private String voidStatus;  // "Yes" or "No"
    private String voidedBy;
    
    private String dispensedStatus;
    private String dispensedBy;
    
    // New fields for dispense approval
    private String dispenseApprovalStatus;
    private String dispenseApprovedBy;

    // New fields added
    private double availableAmount;
    private String currentStatus; // "In Use", "Completed", "Active", "Cancelled", "Pending"

    // Main constructor with all fields (18 parameters)
    public PettyCashReplenishRecordModel(String requestId, String requisitionUnit, String reason, String payee,
                            double amountRequested, LocalDate requestDate,
                            String confirmationStatus, String confirmedBy,
                            String approvalStatus, String approvedBy,
                            String voidStatus, String voidedBy,
                            String dispensedStatus, String dispensedBy,
                            String dispenseApprovalStatus, String dispenseApprovedBy,
                            double availableAmount, String currentStatus) {
       
        this.requestId = requestId;
        this.requisitionUnit = requisitionUnit;
        this.reason = reason;
        this.payee = payee;
        this.amountRequested = amountRequested;
        this.requestDate = requestDate;
        this.confirmationStatus = confirmationStatus;
        this.confirmedBy = confirmedBy;
        this.approvalStatus = approvalStatus;
        this.approvedBy = approvedBy;
        this.voidStatus = voidStatus;
        this.voidedBy = voidedBy;
        this.dispensedStatus = dispensedStatus;
        this.dispensedBy = dispensedBy;
        this.dispenseApprovalStatus = dispenseApprovalStatus;
        this.dispenseApprovedBy = dispenseApprovedBy;
        this.availableAmount = availableAmount;
        this.currentStatus = currentStatus;
    }

    // Constructor for existing code compatibility (16 parameters)
    public PettyCashReplenishRecordModel(String requestId, String requisitionUnit, String reason, String payee,
                            double amountRequested, LocalDate requestDate,
                            String confirmationStatus, String confirmedBy,
                            String approvalStatus, String approvedBy,
                            String voidStatus, String voidedBy,
                            String dispensedStatus, String dispensedBy,
                            String dispenseApprovalStatus, String dispenseApprovedBy) {
        this(requestId, requisitionUnit, reason, payee, amountRequested, requestDate,
             confirmationStatus, confirmedBy, approvalStatus, approvedBy,
             voidStatus, voidedBy, dispensedStatus, dispensedBy,
             dispenseApprovalStatus, dispenseApprovedBy,
             0.0, "In Use");  // Default values for new fields
    }

    // Constructor for existing code compatibility (14 parameters)
    public PettyCashReplenishRecordModel(String requestId, String requisitionUnit, String reason, String payee,
                            double amountRequested, LocalDate requestDate,
                            String confirmationStatus, String confirmedBy,
                            String approvalStatus, String approvedBy,
                            String voidStatus, String voidedBy,
                            String dispensedStatus, String dispensedBy) {
        this(requestId, requisitionUnit, reason, payee, amountRequested, requestDate,
             confirmationStatus, confirmedBy, approvalStatus, approvedBy,
             voidStatus, voidedBy, dispensedStatus, dispensedBy,
             "Pending", "", 0.0, "In Use");
    }

    // Constructor for new requests (10 parameters)
    public PettyCashReplenishRecordModel(String requestId, String requisitionUnit, String reason, String payee,
                            double amountRequested, LocalDate requestDate,
                            String confirmationStatus, String confirmedBy,
                            String approvalStatus, String approvedBy) {
        this(requestId, requisitionUnit, reason, payee, amountRequested, requestDate,
             confirmationStatus, confirmedBy, approvalStatus, approvedBy,
             "No", "", "No", "", "Pending", "", 0.0, "In Use");
    }

    // Constructor for form creation (6 parameters)
    public PettyCashReplenishRecordModel(String requestId, String requisitionUnit, String reason, 
                            String payee, double amountRequested, LocalDate requestDate) {
        this(requestId, requisitionUnit, reason, payee, amountRequested, requestDate,
             "Pending", "", "Pending", "", "No", "", "No", "", "Pending", "", 0.0, "In Use");
    }

    // Default constructor
    public PettyCashReplenishRecordModel() {
        this("", "", "", "", 0.0, LocalDate.now(),
             "Pending", "", "Pending", "",
             "No", "", "No", "", "Pending", "", 0.0, "In Use");
    }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getRequisitionUnit() { return requisitionUnit; }
    public void setRequisitionUnit(String requisitionUnit) { this.requisitionUnit = requisitionUnit; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getPayee() { return payee; }
    public void setPayee(String payee) { this.payee = payee; }

    public double getAmountRequested() { return amountRequested; }
    public void setAmountRequested(double amountRequested) { this.amountRequested = amountRequested; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }
    
    public String getConfirmationStatus() { return confirmationStatus; }
    public void setConfirmationStatus(String confirmationStatus) { this.confirmationStatus = confirmationStatus; }
    
    public String getConfirmedBy() { return confirmedBy; }
    public void setConfirmedBy(String confirmedBy) { this.confirmedBy = confirmedBy; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    
    public String getVoidStatus() { return voidStatus; }
    public void setVoidStatus(String voidStatus) { this.voidStatus = voidStatus; }
    
    public String getVoidedBy() { return voidedBy; }
    public void setVoidedBy(String voidedBy) { this.voidedBy = voidedBy; }
    
    public String getDispensedStatus() { return dispensedStatus; }
    public void setDispensedStatus(String dispensedStatus) { this.dispensedStatus = dispensedStatus; }

    public String getDispensedBy() { return dispensedBy; }
    public void setDispensedBy(String dispensedBy) { this.dispensedBy = dispensedBy; }

    // New getters and setters for dispense approval
    public String getDispenseApprovalStatus() { return dispenseApprovalStatus; }
    public void setDispenseApprovalStatus(String dispenseApprovalStatus) { 
        this.dispenseApprovalStatus = dispenseApprovalStatus; 
    }

    public String getDispenseApprovedBy() { return dispenseApprovedBy; }
    public void setDispenseApprovedBy(String dispenseApprovedBy) { 
        this.dispenseApprovedBy = dispenseApprovedBy; 
    }

    // New getters and setters for availableAmount and currentStatus
    public double getAvailableAmount() { return availableAmount; }
    public void setAvailableAmount(double availableAmount) { this.availableAmount = availableAmount; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }

    // Utility methods
    public boolean isApproved() {
        return "Approved".equalsIgnoreCase(approvalStatus);
    }

    public boolean isConfirmed() {
        return "Confirmed".equalsIgnoreCase(confirmationStatus);
    }

    public boolean isDispensed() {
        return "Yes".equalsIgnoreCase(dispensedStatus);
    }

    public boolean isDispenseApproved() {
        return "Approved".equalsIgnoreCase(dispenseApprovalStatus);
    }

    public boolean isVoided() {
        return "Yes".equalsIgnoreCase(voidStatus);
    }

    public boolean isCompleted() {
        return "Completed".equalsIgnoreCase(currentStatus);
    }

    public boolean isInUse() {
        return "In Use".equalsIgnoreCase(currentStatus);
    }

    public boolean isActive() {
        return "Active".equalsIgnoreCase(currentStatus);
    }

    public boolean isCancelled() {
        return "Cancelled".equalsIgnoreCase(currentStatus);
    }

    public boolean hasAvailableAmount() {
        return availableAmount > 0;
    }

    public boolean isFullyUsed() {
        return availableAmount <= 0 && amountRequested > 0;
    }

    public double getUsedAmount() {
        return amountRequested - availableAmount;
    }

    public double getUsagePercentage() {
        if (amountRequested == 0) return 0.0;
        return ((amountRequested - availableAmount) / amountRequested) * 100;
    }

    public boolean canBeApproved() {
        return !isVoided() && !isApproved() && !isCompleted() && !isCancelled();
    }

    public boolean canBeConfirmed() {
        return !isVoided() && isApproved() && !isConfirmed() && !isCompleted() && !isCancelled();
    }

    public boolean canBeDispensed() {
        return !isVoided() && isConfirmed() && !isDispensed() && !isCompleted() && !isCancelled();
    }

    public boolean canBeDispenseApproved() {
        return !isVoided() && isDispensed() && !isDispenseApproved() && !isCompleted() && !isCancelled();
    }

    public boolean canBeCompleted() {
        return !isVoided() && isDispenseApproved() && !isCompleted() && !isCancelled();
    }

    public boolean canBeCancelled() {
        return !isVoided() && !isCompleted() && !isCancelled();
    }
    
    public String getClassValue(String field) {
        try {
            return String.valueOf(
                    this.getClass().getMethod("get" + field.substring(0,1).toUpperCase() + field.substring(1)).invoke(this)
            );
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String toString() {
        return "PettyCashReplenishRecordModel{" +
                "requestId='" + requestId + '\'' +
                ", requisitionUnit='" + requisitionUnit + '\'' +
                ", reason='" + reason + '\'' +
                ", payee='" + payee + '\'' +
                ", amountRequested=" + amountRequested +
                ", requestDate=" + requestDate +
                ", confirmationStatus='" + confirmationStatus + '\'' +
                ", confirmedBy='" + confirmedBy + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", approvedBy='" + approvedBy + '\'' +
                ", voidStatus='" + voidStatus + '\'' +
                ", voidedBy='" + voidedBy + '\'' +
                ", dispensedStatus='" + dispensedStatus + '\'' +
                ", dispensedBy='" + dispensedBy + '\'' +
                ", dispenseApprovalStatus='" + dispenseApprovalStatus + '\'' +
                ", dispenseApprovedBy='" + dispenseApprovedBy + '\'' +
                ", availableAmount=" + availableAmount +
                ", currentStatus='" + currentStatus + '\'' +
                '}';
    }

    // Copy constructor
    public PettyCashReplenishRecordModel(PettyCashReplenishRecordModel other) {
        this.requestId = other.requestId;
        this.requisitionUnit = other.requisitionUnit;
        this.reason = other.reason;
        this.payee = other.payee;
        this.amountRequested = other.amountRequested;
        this.requestDate = other.requestDate;
        this.confirmationStatus = other.confirmationStatus;
        this.confirmedBy = other.confirmedBy;
        this.approvalStatus = other.approvalStatus;
        this.approvedBy = other.approvedBy;
        this.voidStatus = other.voidStatus;
        this.voidedBy = other.voidedBy;
        this.dispensedStatus = other.dispensedStatus;
        this.dispensedBy = other.dispensedBy;
        this.dispenseApprovalStatus = other.dispenseApprovalStatus;
        this.dispenseApprovedBy = other.dispenseApprovedBy;
        this.availableAmount = other.availableAmount;
        this.currentStatus = other.currentStatus;
    }

    // Helper method to update available amount
    public void deductFromAvailableAmount(double amount) {
        if (amount > 0 && amount <= this.availableAmount) {
            this.availableAmount -= amount;
            if (this.availableAmount == 0) {
                this.currentStatus = "Completed";
            }
        }
    }

    // Helper method to add to available amount
    public void addToAvailableAmount(double amount) {
        if (amount > 0) {
            this.availableAmount += amount;
            if (this.currentStatus.equals("Completed") && this.availableAmount > 0) {
                this.currentStatus = "In Use";
            }
        }
    }

    // Helper method to mark as completed
    public void markAsCompleted() {
        this.currentStatus = "Completed";
        this.availableAmount = 0.0;
    }

    // Helper method to mark as cancelled
    public void markAsCancelled() {
        this.currentStatus = "Cancelled";
        this.voidStatus = "Yes";
    }

    // Helper method to reset status
    public void resetToActive() {
        this.currentStatus = "In Use";
        this.voidStatus = "No";
        this.voidedBy = "";
    }
}