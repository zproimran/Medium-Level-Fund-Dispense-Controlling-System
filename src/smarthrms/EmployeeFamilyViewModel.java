package smarthrms;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeFamilyViewModel {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty empcode;
    private final SimpleStringProperty employeefullname;
    private final SimpleStringProperty familyfullname;
    private final SimpleStringProperty phonenumber;
    private final SimpleStringProperty gender;
    private final SimpleStringProperty educationlevel;
    private final SimpleStringProperty relationship;
    private final SimpleStringProperty maritalstatus;

    public EmployeeFamilyViewModel(int id, String empcode, String employeefullname,
                                   String familyfullname, String phonenumber,
                                   String gender, String educationlevel,
                                   String relationship, String maritalstatus) {
        this.id = new SimpleIntegerProperty(id);
        this.empcode = new SimpleStringProperty(empcode);
        this.employeefullname = new SimpleStringProperty(employeefullname);
        this.familyfullname = new SimpleStringProperty(familyfullname);
        this.phonenumber = new SimpleStringProperty(phonenumber);
        this.gender = new SimpleStringProperty(gender);
        this.educationlevel = new SimpleStringProperty(educationlevel);
        this.relationship = new SimpleStringProperty(relationship);
        this.maritalstatus = new SimpleStringProperty(maritalstatus);
    }

    // Getters
    public int getId() { return id.get(); }
    public String getEmpcode() { return empcode.get(); }
    public String getEmployeefullname() { return employeefullname.get(); }
    public String getFamilyfullname() { return familyfullname.get(); }
    public String getPhonenumber() { return phonenumber.get(); }
    public String getGender() { return gender.get(); }
    public String getEducationlevel() { return educationlevel.get(); }
    public String getRelationship() { return relationship.get(); }
    public String getMaritalstatus() { return maritalstatus.get(); }
}
