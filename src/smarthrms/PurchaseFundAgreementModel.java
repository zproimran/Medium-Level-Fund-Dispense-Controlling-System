package smarthrms;

public class PurchaseFundAgreementModel {
    private int id;
    private String employeeName;
    private String introduction;
    private String purpose;
    private String consent;
    private String parties;
    private String natureOfWork;
    private String employerRights;
    private String employeeRights;
    private String employerDuties;   // new
    private String employeeDuties;   // new

    // Constructor
    public PurchaseFundAgreementModel(int id, String employeeName, String introduction, String purpose, String consent,
                                      String parties, String natureOfWork, String employerRights, String employeeRights,
                                      String employerDuties, String employeeDuties) {
        this.id = id;
        this.employeeName = employeeName;
        this.introduction = introduction;
        this.purpose = purpose;
        this.consent = consent;
        this.parties = parties;
        this.natureOfWork = natureOfWork;
        this.employerRights = employerRights;
        this.employeeRights = employeeRights;
        this.employerDuties = employerDuties;
        this.employeeDuties = employeeDuties;
    }
    
    // Add this inside PurchaseFundAgreementModel
public PurchaseFundAgreementModel() {
    this.id = 0;
    this.employeeName = "";
    this.introduction = "";
    this.purpose = "";
    this.consent = "";
    this.parties = "";
    this.natureOfWork = "";
    this.employerRights = "";
    this.employeeRights = "";
    this.employerDuties = "";
    this.employeeDuties = "";
}


    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getConsent() {
        return consent;
    }

    public void setConsent(String consent) {
        this.consent = consent;
    }

    public String getParties() {
        return parties;
    }

    public void setParties(String parties) {
        this.parties = parties;
    }

    public String getNatureOfWork() {
        return natureOfWork;
    }

    public void setNatureOfWork(String natureOfWork) {
        this.natureOfWork = natureOfWork;
    }

    public String getEmployerRights() {
        return employerRights;
    }

    public void setEmployerRights(String employerRights) {
        this.employerRights = employerRights;
    }

    public String getEmployeeRights() {
        return employeeRights;
    }

    public void setEmployeeRights(String employeeRights) {
        this.employeeRights = employeeRights;
    }

    public String getEmployerDuties() {
        return employerDuties;
    }

    public void setEmployerDuties(String employerDuties) {
        this.employerDuties = employerDuties;
    }

    public String getEmployeeDuties() {
        return employeeDuties;
    }

    public void setEmployeeDuties(String employeeDuties) {
        this.employeeDuties = employeeDuties;
    }
}
