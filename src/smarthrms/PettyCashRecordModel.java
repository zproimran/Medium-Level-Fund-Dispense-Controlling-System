package smarthrms;

import java.time.LocalDate;

public class PettyCashRecordModel {

    private String requestId;
    private String requisitionUnit;

    // ✅ NEW FIELDS
    private String mainCategory;
    private String subCategory;

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

    private String dispenseApprovalStatus;
    private String dispenseApprovedBy;

    // ==========================================
    // MAIN CONSTRUCTOR WITH ALL FIELDS
    // ==========================================
    public PettyCashRecordModel(String requestId, String requisitionUnit,
                                String mainCategory, String subCategory,
                                String reason, String payee,
                                double amountRequested, LocalDate requestDate,
                                String confirmationStatus, String confirmedBy,
                                String approvalStatus, String approvedBy,
                                String voidStatus, String voidedBy,
                                String dispensedStatus, String dispensedBy,
                                String dispenseApprovalStatus, String dispenseApprovedBy) {

        this.requestId = requestId;
        this.requisitionUnit = requisitionUnit;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
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
    }

    // ==========================================
    // COMPATIBILITY CONSTRUCTOR (OLD CODE SUPPORT)
    // ==========================================
    public PettyCashRecordModel(String requestId, String requisitionUnit, String reason, String payee,
                                double amountRequested, LocalDate requestDate,
                                String confirmationStatus, String confirmedBy,
                                String approvalStatus, String approvedBy,
                                String voidStatus, String voidedBy,
                                String dispensedStatus, String dispensedBy) {

        this(requestId, requisitionUnit,
             "", "",   // main & sub default
             reason, payee, amountRequested, requestDate,
             confirmationStatus, confirmedBy, approvalStatus, approvedBy,
             voidStatus, voidedBy, dispensedStatus, dispensedBy,
             "Pending", "");
    }

    // ==========================================
    // CONSTRUCTOR FOR NEW REQUESTS
    // ==========================================
    public PettyCashRecordModel(String requestId, String requisitionUnit,
                                String mainCategory, String subCategory,
                                String reason, String payee,
                                double amountRequested, LocalDate requestDate) {

        this(requestId, requisitionUnit,
             mainCategory, subCategory,
             reason, payee, amountRequested, requestDate,
             "Pending", "", "Pending", "",
             "No", "", "No", "",
             "Pending", "");
    }

    // ==========================================
    // DEFAULT CONSTRUCTOR
    // ==========================================
    public PettyCashRecordModel() {
        this("", "", "", "", "", "", 0.0, LocalDate.now(),
             "Pending", "", "Pending", "",
             "No", "", "No", "",
             "Pending", "");
    }

    // ==========================================
    // GETTERS & SETTERS
    // ==========================================
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getRequisitionUnit() { return requisitionUnit; }
    public void setRequisitionUnit(String requisitionUnit) { this.requisitionUnit = requisitionUnit; }

    public String getMainCategory() { return mainCategory; }
    public void setMainCategory(String mainCategory) { this.mainCategory = mainCategory; }

    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

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

    public String getDispenseApprovalStatus() { return dispenseApprovalStatus; }
    public void setDispenseApprovalStatus(String dispenseApprovalStatus) { this.dispenseApprovalStatus = dispenseApprovalStatus; }

    public String getDispenseApprovedBy() { return dispenseApprovedBy; }
    public void setDispenseApprovedBy(String dispenseApprovedBy) { this.dispenseApprovedBy = dispenseApprovedBy; }

    // ==========================================
    // STATE LOGIC METHODS
    // ==========================================
    public boolean isApproved() { return "Approved".equalsIgnoreCase(approvalStatus); }
    public boolean isConfirmed() { return "Confirmed".equalsIgnoreCase(confirmationStatus); }
    public boolean isDispensed() { return "Yes".equalsIgnoreCase(dispensedStatus); }
    public boolean isDispenseApproved() { return "Approved".equalsIgnoreCase(dispenseApprovalStatus); }
    public boolean isVoided() { return "Yes".equalsIgnoreCase(voidStatus); }

    public boolean canBeApproved() { return !isVoided() && !isApproved(); }
    public boolean canBeConfirmed() { return !isVoided() && isApproved() && !isConfirmed(); }
    public boolean canBeDispensed() { return !isVoided() && isConfirmed() && !isDispensed(); }
    public boolean canBeDispenseApproved() { return !isVoided() && isDispensed() && !isDispenseApproved(); }

    // ==========================================
    // REFLECTION SUPPORT
    // ==========================================
    public String getClassValue(String field) {
        try {
            return String.valueOf(
                    this.getClass().getMethod("get" + field.substring(0, 1).toUpperCase() + field.substring(1)).invoke(this)
            );
        } catch (Exception e) {
            return "";
        }
    }

    // ==========================================
    // TO STRING
    // ==========================================
    @Override
    public String toString() {
        return "PettyCashRecordModel{" +
                "requestId='" + requestId + '\'' +
                ", requisitionUnit='" + requisitionUnit + '\'' +
                ", mainCategory='" + mainCategory + '\'' +
                ", subCategory='" + subCategory + '\'' +
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
                '}';
    }

    // ==========================================
    // COPY CONSTRUCTOR
    // ==========================================
    public PettyCashRecordModel(PettyCashRecordModel other) {
        this.requestId = other.requestId;
        this.requisitionUnit = other.requisitionUnit;
        this.mainCategory = other.mainCategory;
        this.subCategory = other.subCategory;
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
    }
}
