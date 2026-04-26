package smarthrms;

import java.time.LocalDate;

public class PettyCashFundRecordModel {
    private String fundId;
    private String monthlyMoment;
    private double acceptedAmount;
    private double availableAmount;
    private String acceptedBy;
    private LocalDate acceptedDate;
    private String status;
    private String approvedBy;

    public PettyCashFundRecordModel(String fundId,String monthlyMoment, double acceptedAmount,
                                   double availableAmount,String acceptedBy, LocalDate acceptedDate, String status, String approvedBy) {
        this.fundId = fundId;
        this.monthlyMoment=monthlyMoment;
        this.acceptedAmount = acceptedAmount;
        this.availableAmount = availableAmount;
        this.acceptedBy=acceptedBy;
        this.acceptedDate = acceptedDate;
        this.status = status;
        this.approvedBy = approvedBy; 
    }

    public String getFundId() { return fundId; }
    public void setFundId(String fundId) { this.fundId = fundId; }

    public String getMonthlyMoment() { return monthlyMoment; }
    public void setMonthlyMoment(String monthlyMoment) { this.monthlyMoment = monthlyMoment; }

    public double getAcceptedAmount() { return acceptedAmount; }
    public void setAcceptedAmount(double acceptedAmount) { this.acceptedAmount = acceptedAmount; }
    
    public double getAvailableAmount() { return availableAmount; }
    public void setAvailableAmount(double availableAmount) { this.availableAmount = availableAmount; }
    
    public String getAcceptedBy(){return acceptedBy;}
    public void setAcceptedBy(String acceptedBy){this.acceptedBy=acceptedBy;}

    public LocalDate getAcceptedDate() { return acceptedDate; }
    public void setAcceptedDate(LocalDate requestDate) { this.acceptedDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
}
