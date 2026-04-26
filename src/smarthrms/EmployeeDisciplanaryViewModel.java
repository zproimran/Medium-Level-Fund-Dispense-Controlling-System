package smarthrms;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeDisciplanaryViewModel {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty empcode;
    private final SimpleStringProperty employeefullname;
    private final SimpleStringProperty disciplanarycase;
    private final SimpleStringProperty reporteddate;
    private final SimpleStringProperty casedate;
    private final SimpleStringProperty reportedby;

    public EmployeeDisciplanaryViewModel(int id, String empcode, String employeefullname,
                                         String disciplanarycase, String reporteddate,
                                         String casedate, String reportedby) {
        this.id = new SimpleIntegerProperty(id);
        this.empcode = new SimpleStringProperty(empcode);
        this.employeefullname = new SimpleStringProperty(employeefullname);
        this.disciplanarycase = new SimpleStringProperty(disciplanarycase);
        this.reporteddate = new SimpleStringProperty(reporteddate);
        this.casedate = new SimpleStringProperty(casedate);
        this.reportedby = new SimpleStringProperty(reportedby);
    }

    public int getId() { return id.get(); }
    public String getEmpcode() { return empcode.get(); }
    public String getEmployeefullname() { return employeefullname.get(); }
    public String getDisciplanarycase() { return disciplanarycase.get(); }
    public String getReporteddate() { return reporteddate.get(); }
    public String getCasedate() { return casedate.get(); }
    public String getReportedby() { return reportedby.get(); }
}
