package smarthrms;

public class EmployeeSpecializationModel {
    private int id;
    private String specName;

    public EmployeeSpecializationModel(int id, String specName) {
        this.id = id;
        this.specName = specName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }
}
