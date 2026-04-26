package smarthrms;

import java.time.LocalDate;

public class AgreementBasedPurchaseFundRecordModel {

    private String requestId;
    private String requisitionUnit;

    // ✅ New main/sub category fields
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

    // ✅ New employee/agreement fields
    private String employeeName;
    private String agreementIntro;
    private String agreementPurpose;
    private String agreementConsent;
    private String agreementParties;
    private String agreementNatureOfWork;
    private String agreementEmployerRights;
    private String agreementEmployeeRights;
    private String agreementEmployerDuties;
    private String agreementEmployeeDuties;

    // ==========================================
    // MAIN CONSTRUCTOR
    // ==========================================
    public AgreementBasedPurchaseFundRecordModel(String requestId, String requisitionUnit,
                                                 String mainCategory, String subCategory,
                                                 String reason, String payee,
                                                 double amountRequested, LocalDate requestDate,
                                                 String confirmationStatus, String confirmedBy,
                                                 String approvalStatus, String approvedBy,
                                                 String voidStatus, String voidedBy,
                                                 String dispensedStatus, String dispensedBy,
                                                 String dispenseApprovalStatus, String dispenseApprovedBy,
                                                 String employeeName, String agreementIntro, String agreementPurpose,
                                                 String agreementConsent, String agreementParties,
                                                 String agreementNatureOfWork, String agreementEmployerRights,
                                                 String agreementEmployeeRights, String agreementEmployerDuties,
                                                 String agreementEmployeeDuties) {

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
        this.employeeName = employeeName;
        this.agreementIntro = agreementIntro;
        this.agreementPurpose = agreementPurpose;
        this.agreementConsent = agreementConsent;
        this.agreementParties = agreementParties;
        this.agreementNatureOfWork = agreementNatureOfWork;
        this.agreementEmployerRights = agreementEmployerRights;
        this.agreementEmployeeRights = agreementEmployeeRights;
        this.agreementEmployerDuties = agreementEmployerDuties;
        this.agreementEmployeeDuties = agreementEmployeeDuties;
    }

    // ==========================================
    // CONSTRUCTOR FOR NEW REQUESTS (empty agreement + category fields)
    // ==========================================
    public AgreementBasedPurchaseFundRecordModel(String requestId, String requisitionUnit,
                                                 String mainCategory, String subCategory,
                                                 String reason, String payee,
                                                 double amountRequested, LocalDate requestDate) {
        this(requestId, requisitionUnit, mainCategory, subCategory,
             reason, payee, amountRequested, requestDate,
             "Pending", "", "Pending", "", "No", "", "No", "", "Pending", "",
             "", "", "", "", "", "", "", "", "", "");
    }

    // ==========================================
    // DEFAULT CONSTRUCTOR
    // ==========================================
    public AgreementBasedPurchaseFundRecordModel() {
        this("", "", "", "", "", "", 0.0, LocalDate.now());
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

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getAgreementIntro() { return agreementIntro; }
    public void setAgreementIntro(String agreementIntro) { this.agreementIntro = agreementIntro; }

    public String getAgreementPurpose() { return agreementPurpose; }
    public void setAgreementPurpose(String agreementPurpose) { this.agreementPurpose = agreementPurpose; }

    public String getAgreementConsent() { return agreementConsent; }
    public void setAgreementConsent(String agreementConsent) { this.agreementConsent = agreementConsent; }

    public String getAgreementParties() { return agreementParties; }
    public void setAgreementParties(String agreementParties) { this.agreementParties = agreementParties; }

    public String getAgreementNatureOfWork() { return agreementNatureOfWork; }
    public void setAgreementNatureOfWork(String agreementNatureOfWork) { this.agreementNatureOfWork = agreementNatureOfWork; }

    public String getAgreementEmployerRights() { return agreementEmployerRights; }
    public void setAgreementEmployerRights(String agreementEmployerRights) { this.agreementEmployerRights = agreementEmployerRights; }

    public String getAgreementEmployeeRights() { return agreementEmployeeRights; }
    public void setAgreementEmployeeRights(String agreementEmployeeRights) { this.agreementEmployeeRights = agreementEmployeeRights; }

    public String getAgreementEmployerDuties() { return agreementEmployerDuties; }
    public void setAgreementEmployerDuties(String agreementEmployerDuties) { this.agreementEmployerDuties = agreementEmployerDuties; }

    public String getAgreementEmployeeDuties() { return agreementEmployeeDuties; }
    public void setAgreementEmployeeDuties(String agreementEmployeeDuties) { this.agreementEmployeeDuties = agreementEmployeeDuties; }

    // ==========================================
    // STATE METHODS
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
}
