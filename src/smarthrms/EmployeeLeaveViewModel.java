package smarthrms;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeLeaveViewModel {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty empcode;
    private final SimpleStringProperty fullname;
    private final SimpleStringProperty daystatus;
    private final SimpleStringProperty leavetype;
    private final SimpleStringProperty year;
    private final SimpleStringProperty from;
    private final SimpleStringProperty to;

    public EmployeeLeaveViewModel(int id, String empcode, String fullname,
                                  String daystatus, String leavetype,
                                  String year, String from, String to) {
        this.id = new SimpleIntegerProperty(id);
        this.empcode = new SimpleStringProperty(empcode);
        this.fullname = new SimpleStringProperty(fullname);
        this.daystatus = new SimpleStringProperty(daystatus);
        this.leavetype = new SimpleStringProperty(leavetype);
        this.year = new SimpleStringProperty(year);
        this.from = new SimpleStringProperty(from);
        this.to = new SimpleStringProperty(to);
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

    public String getDaystatus() { return daystatus.get(); }
    public void setDaystatus(String value) { daystatus.set(value); }
    public SimpleStringProperty daystatusProperty() { return daystatus; }

    public String getLeavetype() { return leavetype.get(); }
    public void setLeavetype(String value) { leavetype.set(value); }
    public SimpleStringProperty leavetypeProperty() { return leavetype; }

    public String getYear() { return year.get(); }
    public void setYear(String value) { year.set(value); }
    public SimpleStringProperty yearProperty() { return year; }

    public String getFrom() { return from.get(); }
    public void setFrom(String value) { from.set(value); }
    public SimpleStringProperty fromProperty() { return from; }

    public String getTo() { return to.get(); }
    public void setTo(String value) { to.set(value); }
    public SimpleStringProperty toProperty() { return to; }
}
