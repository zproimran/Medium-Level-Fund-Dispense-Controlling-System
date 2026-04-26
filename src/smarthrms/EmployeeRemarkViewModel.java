package smarthrms;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeRemarkViewModel {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty empcode;
    private final SimpleStringProperty fullname;
    private final SimpleStringProperty liscenceno;
    private final SimpleStringProperty bankaccount;
    private final SimpleStringProperty bank;
    private final SimpleStringProperty department;
    private final SimpleStringProperty assignedposition;
    private final SimpleStringProperty specialization;
    private final SimpleStringProperty remark;

    public EmployeeRemarkViewModel(int id, String empcode, String fullname,
                                   String liscenceno, String bankaccount,
                                   String bank, String department,
                                   String assignedposition, String specialization,
                                   String remark) {
        this.id = new SimpleIntegerProperty(id);
        this.empcode = new SimpleStringProperty(empcode);
        this.fullname = new SimpleStringProperty(fullname);
        this.liscenceno = new SimpleStringProperty(liscenceno);
        this.bankaccount = new SimpleStringProperty(bankaccount);
        this.bank = new SimpleStringProperty(bank);
        this.department = new SimpleStringProperty(department);
        this.assignedposition = new SimpleStringProperty(assignedposition);
        this.specialization = new SimpleStringProperty(specialization);
        this.remark = new SimpleStringProperty(remark);
    }

    // ✅ Getters, Setters & Property methods

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public SimpleIntegerProperty idProperty() { return id; }

    public String getEmpcode() { return empcode.get(); }
    public void setEmpcode(String value) { empcode.set(value); }
    public SimpleStringProperty empcodeProperty() { return empcode; }

    public String getFullname() { return fullname.get(); }
    public void setFullname(String value) { fullname.set(value); }
    public SimpleStringProperty fullnameProperty() { return fullname; }

    public String getLiscenceno() { return liscenceno.get(); }
    public void setLiscenceno(String value) { liscenceno.set(value); }
    public SimpleStringProperty liscencenoProperty() { return liscenceno; }

    public String getBankaccount() { return bankaccount.get(); }
    public void setBankaccount(String value) { bankaccount.set(value); }
    public SimpleStringProperty bankaccountProperty() { return bankaccount; }

    public String getBank() { return bank.get(); }
    public void setBank(String value) { bank.set(value); }
    public SimpleStringProperty bankProperty() { return bank; }

    public String getDepartment() { return department.get(); }
    public void setDepartment(String value) { department.set(value); }
    public SimpleStringProperty departmentProperty() { return department; }

    public String getAssignedposition() { return assignedposition.get(); }
    public void setAssignedposition(String value) { assignedposition.set(value); }
    public SimpleStringProperty assignedpositionProperty() { return assignedposition; }

    public String getSpecialization() { return specialization.get(); }
    public void setSpecialization(String value) { specialization.set(value); }
    public SimpleStringProperty specializationProperty() { return specialization; }

    public String getRemark() { return remark.get(); }
    public void setRemark(String value) { remark.set(value); }
    public SimpleStringProperty remarkProperty() { return remark; }
}
