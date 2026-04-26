
package smarthrms;

public class EmployeeLeaveTypeModel {
    private int id;
    private String leavename;

    public EmployeeLeaveTypeModel(int id, String leavename) {
        this.id = id;
        this.leavename = leavename;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLeavename() {
        return leavename;
    }

    public void setLeavename(String leavename) {
        this.leavename = leavename;
    }
}
