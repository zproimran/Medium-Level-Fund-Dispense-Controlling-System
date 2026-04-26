package smarthrms;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeRewardViewModel {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty empcode;
    private final SimpleStringProperty employeefullname;
    private final SimpleStringProperty award;
    private final SimpleStringProperty fromcompanyname;
    private final SimpleStringProperty awarddate;
    private final SimpleStringProperty remark;

    public EmployeeRewardViewModel(int id, String empcode, String employeefullname,
                                   String award, String fromcompanyname,
                                   String awarddate, String remark) {
        this.id = new SimpleIntegerProperty(id);
        this.empcode = new SimpleStringProperty(empcode);
        this.employeefullname = new SimpleStringProperty(employeefullname);
        this.award = new SimpleStringProperty(award);
        this.fromcompanyname = new SimpleStringProperty(fromcompanyname);
        this.awarddate = new SimpleStringProperty(awarddate);
        this.remark = new SimpleStringProperty(remark);
    }

    // Getters
    public int getId() { return id.get(); }
    public String getEmpcode() { return empcode.get(); }
    public String getEmployeefullname() { return employeefullname.get(); }
    public String getAward() { return award.get(); }
    public String getFromcompanyname() { return fromcompanyname.get(); }
    public String getAwarddate() { return awarddate.get(); }
    public String getRemark() { return remark.get(); }

    // Property methods (useful for TableView binding)
    public SimpleIntegerProperty idProperty() { return id; }
    public SimpleStringProperty empcodeProperty() { return empcode; }
    public SimpleStringProperty employeefullnameProperty() { return employeefullname; }
    public SimpleStringProperty awardProperty() { return award; }
    public SimpleStringProperty fromcompanynameProperty() { return fromcompanyname; }
    public SimpleStringProperty awarddateProperty() { return awarddate; }
    public SimpleStringProperty remarkProperty() { return remark; }
}
