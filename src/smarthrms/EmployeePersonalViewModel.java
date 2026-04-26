package smarthrms;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeePersonalViewModel {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty empcode;
    private final SimpleStringProperty fpno;
    private final SimpleStringProperty courtiletitle;
    private final SimpleStringProperty fullname;
    private final SimpleStringProperty gender;
    private final SimpleStringProperty marital;
    private final SimpleStringProperty phone;
    private final SimpleDoubleProperty salary;
    private final SimpleStringProperty hiredate;
    private final SimpleStringProperty department;
    private final SimpleStringProperty position;
    private final SimpleStringProperty status;
    private final SimpleIntegerProperty age;
    private final SimpleIntegerProperty serviceyear;

    public EmployeePersonalViewModel(int id, String empcode, String fpno, String courtiletitle, String fullname,
                                     String gender, String marital, String phone, double salary, String hiredate,
                                     String department, String position, String status, int age, int serviceyear) {
        this.id = new SimpleIntegerProperty(id);
        this.empcode = new SimpleStringProperty(empcode);
        this.fpno = new SimpleStringProperty(fpno);
        this.courtiletitle = new SimpleStringProperty(courtiletitle);
        this.fullname = new SimpleStringProperty(fullname);
        this.gender = new SimpleStringProperty(gender);
        this.marital = new SimpleStringProperty(marital);
        this.phone = new SimpleStringProperty(phone);
        this.salary = new SimpleDoubleProperty(salary);
        this.hiredate = new SimpleStringProperty(hiredate);
        this.department = new SimpleStringProperty(department);
        this.position = new SimpleStringProperty(position);
        this.status = new SimpleStringProperty(status);
        this.age = new SimpleIntegerProperty(age);
        this.serviceyear = new SimpleIntegerProperty(serviceyear);
    }

    // ✅ Getters, Setters & Property methods

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public SimpleIntegerProperty idProperty() { return id; }

    public String getEmpcode() { return empcode.get(); }
    public void setEmpcode(String value) { empcode.set(value); }
    public SimpleStringProperty empcodeProperty() { return empcode; }

    public String getFpno() { return fpno.get(); }
    public void setFpno(String value) { fpno.set(value); }
    public SimpleStringProperty fpnoProperty() { return fpno; }

    public String getCourtiletitle() { return courtiletitle.get(); }
    public void setCourtiletitle(String value) { courtiletitle.set(value); }
    public SimpleStringProperty courtiletitleProperty() { return courtiletitle; }

    public String getFullname() { return fullname.get(); }
    public void setFullname(String value) { fullname.set(value); }
    public SimpleStringProperty fullnameProperty() { return fullname; }

    public String getGender() { return gender.get(); }
    public void setGender(String value) { gender.set(value); }
    public SimpleStringProperty genderProperty() { return gender; }

    public String getMarital() { return marital.get(); }
    public void setMarital(String value) { marital.set(value); }
    public SimpleStringProperty maritalProperty() { return marital; }

    public String getPhone() { return phone.get(); }
    public void setPhone(String value) { phone.set(value); }
    public SimpleStringProperty phoneProperty() { return phone; }

    public double getSalary() { return salary.get(); }
    public void setSalary(double value) { salary.set(value); }
    public SimpleDoubleProperty salaryProperty() { return salary; }

    public String getHiredate() { return hiredate.get(); }
    public void setHiredate(String value) { hiredate.set(value); }
    public SimpleStringProperty hiredateProperty() { return hiredate; }

    public String getDepartment() { return department.get(); }
    public void setDepartment(String value) { department.set(value); }
    public SimpleStringProperty departmentProperty() { return department; }

    public String getPosition() { return position.get(); }
    public void setPosition(String value) { position.set(value); }
    public SimpleStringProperty positionProperty() { return position; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public SimpleStringProperty statusProperty() { return status; }

    public int getAge() { return age.get(); }
    public void setAge(int value) { age.set(value); }
    public SimpleIntegerProperty ageProperty() { return age; }

    public int getServiceyear() { return serviceyear.get(); }
    public void setServiceyear(int value) { serviceyear.set(value); }
    public SimpleIntegerProperty serviceyearProperty() { return serviceyear; }
}
