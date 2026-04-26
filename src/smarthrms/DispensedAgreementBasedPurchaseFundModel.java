package smarthrms;

import java.time.LocalDate;

public class DispensedAgreementBasedPurchaseFundModel {

    // ==================== Fields ====================
    private String requestId;
    private String requisitionUnit;
    private String reason;
    private String payee;
    private double requestedAmount;
    private double givenAmount;
    private String givenBy;
    private LocalDate requestDate;
    private LocalDate completedDate;
    private byte[] fingerprintTemplate;
    private String verificationMethod;

    // ==================== Constructors ====================
    public DispensedAgreementBasedPurchaseFundModel() {}

    public DispensedAgreementBasedPurchaseFundModel(
            String requestId,
            String requisitionUnit,
            String reason,
            String payee,
            double requestedAmount,
            double givenAmount,
            String givenBy,
            LocalDate requestDate,
            LocalDate completedDate,
            byte[] fingerprintTemplate,
            String verificationMethod
    ) {
        this.requestId = requestId;
        this.requisitionUnit = requisitionUnit;
        this.reason = reason;
        this.payee = payee;
        this.requestedAmount = requestedAmount;
        this.givenAmount = givenAmount;
        this.givenBy = givenBy;
        this.requestDate = requestDate;
        this.completedDate = completedDate;
        this.fingerprintTemplate = fingerprintTemplate;
        this.verificationMethod = verificationMethod;
    }

    // ==================== Getters and Setters ====================
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getRequisitionUnit() { return requisitionUnit; }
    public void setRequisitionUnit(String requisitionUnit) { this.requisitionUnit = requisitionUnit; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getPayee() { return payee; }
    public void setPayee(String payee) { this.payee = payee; }

    public double getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(double requestedAmount) { this.requestedAmount = requestedAmount; }

    public double getGivenAmount() { return givenAmount; }
    public void setGivenAmount(double givenAmount) { this.givenAmount = givenAmount; }

    public String getGivenBy() { return givenBy; }
    public void setGivenBy(String givenBy) { this.givenBy = givenBy; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }

    public byte[] getFingerprintTemplate() { return fingerprintTemplate; }
    public void setFingerprintTemplate(byte[] fingerprintTemplate) { this.fingerprintTemplate = fingerprintTemplate; }

    public String getVerificationMethod() { return verificationMethod; }
    public void setVerificationMethod(String verificationMethod) { this.verificationMethod = verificationMethod; }
}
