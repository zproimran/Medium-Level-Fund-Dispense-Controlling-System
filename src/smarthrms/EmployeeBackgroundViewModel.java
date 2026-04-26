
package smarthrms;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeBackgroundViewModel {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty empcode;
    private final SimpleStringProperty fullname;
    private final SimpleStringProperty institutionname;
    private final SimpleStringProperty from;
    private final SimpleStringProperty to;
    private final SimpleStringProperty major;
    private final SimpleStringProperty gpa;
    private final SimpleStringProperty educationlevel;

    public EmployeeBackgroundViewModel(int id, String empcode, String fullname, 
                                       String institutionname, String from, 
                                       String to, String major, String gpa, 
                                       String educationlevel) {
        this.id = new SimpleIntegerProperty(id);
        this.empcode = new SimpleStringProperty(empcode);
        this.fullname = new SimpleStringProperty(fullname);
        this.institutionname = new SimpleStringProperty(institutionname);
        this.from = new SimpleStringProperty(from);
        this.to = new SimpleStringProperty(to);
        this.major = new SimpleStringProperty(major);
        this.gpa = new SimpleStringProperty(gpa);
        this.educationlevel = new SimpleStringProperty(educationlevel);
    }

    // ✅ Getters and Setters (JavaFX properties for TableView binding)

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public SimpleIntegerProperty idProperty() { return id; }

    public String getEmpcode() { return empcode.get(); }
    public void setEmpcode(String value) { empcode.set(value); }
    public SimpleStringProperty empcodeProperty() { return empcode; }

    public String getFullname() { return fullname.get(); }
    public void setFullname(String value) { fullname.set(value); }
    public SimpleStringProperty fullnameProperty() { return fullname; }

    public String getInstitutionname() { return institutionname.get(); }
    public void setInstitutionname(String value) { institutionname.set(value); }
    public SimpleStringProperty institutionnameProperty() { return institutionname; }

    public String getFrom() { return from.get(); }
    public void setFrom(String value) { from.set(value); }
    public SimpleStringProperty fromProperty() { return from; }

    public String getTo() { return to.get(); }
    public void setTo(String value) { to.set(value); }
    public SimpleStringProperty toProperty() { return to; }

    public String getMajor() { return major.get(); }
    public void setMajor(String value) { major.set(value); }
    public SimpleStringProperty majorProperty() { return major; }

    public String getGpa() { return gpa.get(); }
    public void setGpa(String value) { gpa.set(value); }
    public SimpleStringProperty gpaProperty() { return gpa; }

    public String getEducationlevel() { return educationlevel.get(); }
    public void setEducationlevel(String value) { educationlevel.set(value); }
    public SimpleStringProperty educationlevelProperty() { return educationlevel; }
}
