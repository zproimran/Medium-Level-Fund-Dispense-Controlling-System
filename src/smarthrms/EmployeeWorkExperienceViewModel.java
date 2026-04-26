package smarthrms;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeWorkExperienceViewModel {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty empcode;
    private final SimpleStringProperty employeefullname;
    private final SimpleStringProperty companyname;
    private final SimpleStringProperty jobposition;
    private final SimpleStringProperty startdate;
    private final SimpleStringProperty enddate;

    public EmployeeWorkExperienceViewModel(int id, String empcode, String employeefullname,
                                           String companyname, String jobposition,
                                           String startdate, String enddate) {
        this.id = new SimpleIntegerProperty(id);
        this.empcode = new SimpleStringProperty(empcode);
        this.employeefullname = new SimpleStringProperty(employeefullname);
        this.companyname = new SimpleStringProperty(companyname);
        this.jobposition = new SimpleStringProperty(jobposition);
        this.startdate = new SimpleStringProperty(startdate);
        this.enddate = new SimpleStringProperty(enddate);
    }

    public int getId() { return id.get(); }
    public String getEmpcode() { return empcode.get(); }
    public String getEmployeefullname() { return employeefullname.get(); }
    public String getCompanyname() { return companyname.get(); }
    public String getJobposition() { return jobposition.get(); }
    public String getStartdate() { return startdate.get(); }
    public String getEnddate() { return enddate.get(); }
}