package smarthrms;

public class EmployeeJobPositionModel {
    private int id;
    private String positionName;

    public EmployeeJobPositionModel(int id, String positionName) {
        this.id = id;
        this.positionName = positionName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }
}
