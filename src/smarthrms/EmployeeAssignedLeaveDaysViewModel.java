package smarthrms;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeAssignedLeaveDaysViewModel {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty empcode;
    private final SimpleStringProperty fullname;
    private final SimpleStringProperty leavetype;
    private final SimpleStringProperty year;
    private final SimpleStringProperty assigneddays;
    private final SimpleStringProperty availabledays;

    public EmployeeAssignedLeaveDaysViewModel(int id, String empcode, String fullname,
                                              String leavetype, String year,
                                              String assigneddays, String availabledays) {
        this.id = new SimpleIntegerProperty(id);
        this.empcode = new SimpleStringProperty(empcode);
        this.fullname = new SimpleStringProperty(fullname);
        this.leavetype = new SimpleStringProperty(leavetype);
        this.year = new SimpleStringProperty(year);
        this.assigneddays = new SimpleStringProperty(assigneddays);
        this.availabledays = new SimpleStringProperty(availabledays);
    }

    // Getters
    public int getId() { return id.get(); }  
    public String getEmpcode() { return empcode.get(); }
    public String getFullname() { return fullname.get(); }
    public String getLeavetype() { return leavetype.get(); }
    public String getYear() { return year.get(); }
    public String getAssigneddays() { return assigneddays.get(); }
    public String getAvailabledays() { return availabledays.get(); }

    // Property getters (needed for TableView bindings)    
    public SimpleIntegerProperty idProperty() { return id; }
    public SimpleStringProperty empcodeProperty() { return empcode; }
    public SimpleStringProperty fullnameProperty() { return fullname; }
    public SimpleStringProperty leavetypeProperty() { return leavetype; }
    public SimpleStringProperty yearProperty() { return year; }
    public SimpleStringProperty assigneddaysProperty() { return assigneddays; }
    public SimpleStringProperty availabledaysProperty() { return availabledays; }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setEmpcode(String empcode) { this.empcode.set(empcode); }
    public void setFullname(String fullname) { this.fullname.set(fullname); }
    public void setLeavetype(String leavetype) { this.leavetype.set(leavetype); }
    public void setYear(String year) { this.year.set(year); }
    public void setAssigneddays(String assigneddays) { this.assigneddays.set(assigneddays); }
    public void setAvailabledays(String availabledays) { this.availabledays.set(availabledays); }
}
