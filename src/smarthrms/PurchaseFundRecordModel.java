package smarthrms;

import java.time.LocalDate;

public class PurchaseFundRecordModel {

    private String requestId;
    private String requisitionUnit;

    // ✅ New fields
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

    private String voidStatus;
    private String voidedBy;

    private String dispensedStatus;
    private String dispensedBy;

    private String dispenseApprovalStatus;
    private String dispenseApprovedBy;

    private String recieptUploadStatus;
    private String recieptUploadedBy;

    // ✅ Updated main constructor (with categories)
    public PurchaseFundRecordModel(String requestId, String requisitionUnit,
                                   String mainCategory, String subCategory,
                                   String reason, String payee,
                                   double amountRequested, LocalDate requestDate,
                                   String confirmationStatus, String confirmedBy,
                                   String approvalStatus, String approvedBy,
                                   String voidStatus, String voidedBy,
                                   String dispensedStatus, String dispensedBy,
                                   String dispenseApprovalStatus, String dispenseApprovedBy,
                                   String recieptUploadStatus, String recieptUploadedBy) {

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
        this.recieptUploadStatus = recieptUploadStatus;
        this.recieptUploadedBy = recieptUploadedBy;
    }

    // ✅ Compatibility constructor (no category)
    public PurchaseFundRecordModel(String requestId, String requisitionUnit, String reason, String payee,
                                   double amountRequested, LocalDate requestDate,
                                   String confirmationStatus, String confirmedBy,
                                   String approvalStatus, String approvedBy,
                                   String voidStatus, String voidedBy,
                                   String dispensedStatus, String dispensedBy,
                                   String dispenseApprovalStatus, String dispenseApprovedBy,
                                   String recieptUploadStatus, String recieptUploadedBy) {
        this(requestId, requisitionUnit, "", "", reason, payee, amountRequested, requestDate,
             confirmationStatus, confirmedBy, approvalStatus, approvedBy,
             voidStatus, voidedBy, dispensedStatus, dispensedBy,
             dispenseApprovalStatus, dispenseApprovedBy, recieptUploadStatus, recieptUploadedBy);
    }

    // ✅ Constructor for new requests
    public PurchaseFundRecordModel(String requestId, String requisitionUnit,
                                   String mainCategory, String subCategory,
                                   String reason, String payee,
                                   double amountRequested, LocalDate requestDate) {
        this(requestId, requisitionUnit, mainCategory, subCategory, reason, payee,
             amountRequested, requestDate,
             "Pending", "", "Pending", "", "No", "", "No", "",
             "Pending", "", "Pending", "");
    }

    // Default constructor
    public PurchaseFundRecordModel() {
        this("", "", "", "", "", "", 0.0, LocalDate.now(),
             "Pending", "", "Pending", "", "No", "", "No", "",
             "Pending", "", "Pending", "");
    }

    // ✅ Getters and Setters
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

    public String getRecieptUploadStatus() { return recieptUploadStatus; }
    public void setRecieptUploadStatus(String recieptUploadStatus) { this.recieptUploadStatus = recieptUploadStatus; }

    public String getRecieptUploadedBy() { return recieptUploadedBy; }
    public void setRecieptUploadedBy(String recieptUploadedBy) { this.recieptUploadedBy = recieptUploadedBy; }

    // ✅ Utility methods
    public boolean isApproved() { return "Approved".equalsIgnoreCase(approvalStatus); }
    public boolean isRecieptUploaded() { return "Uploaded".equalsIgnoreCase(recieptUploadStatus); }
    public boolean isConfirmed() { return "Confirmed".equalsIgnoreCase(confirmationStatus); }
    public boolean isDispensed() { return "Yes".equalsIgnoreCase(dispensedStatus); }
    public boolean isDispenseApproved() { return "Approved".equalsIgnoreCase(dispenseApprovalStatus); }
    public boolean isVoided() { return "Yes".equalsIgnoreCase(voidStatus); }

    public boolean canBeApproved() { return !isVoided() && !isApproved(); }
    public boolean canBeConfirmed() { return !isVoided() && isApproved() && !isConfirmed(); }
    public boolean canBeDispensed() { return !isVoided() && isConfirmed() && !isDispensed(); }
    public boolean canBeDispenseApproved() { return !isVoided() && isDispensed() && !isDispenseApproved(); }
    
    public String getClassValue(String fieldName) {
    try {
        java.lang.reflect.Field field = this.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object value = field.get(this);
        return value == null ? "" : value.toString();
    } catch (Exception e) {
        return "";
    }
}


    @Override
    public String toString() {
        return "PurchaseFundRecordModel{" +
                "requestId='" + requestId + '\'' +
                ", requisitionUnit='" + requisitionUnit + '\'' +
                ", mainCategory='" + mainCategory + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", reason='" + reason + '\'' +
                ", payee='" + payee + '\'' +
                ", amountRequested=" + amountRequested +
                ", requestDate=" + requestDate +
                ", confirmationStatus='" + confirmationStatus + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", dispensedStatus='" + dispensedStatus + '\'' +
                ", dispenseApprovalStatus='" + dispenseApprovalStatus + '\'' +
                '}';
    }

    // ✅ Copy constructor
    public PurchaseFundRecordModel(PurchaseFundRecordModel other) {
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
        this.recieptUploadStatus = other.recieptUploadStatus;
        this.recieptUploadedBy = other.recieptUploadedBy;
    }
}
