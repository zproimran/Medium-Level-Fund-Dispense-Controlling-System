


package smarthrms;

public class EducationLevelModel {
    private int id;
    private String educationName;

    public EducationLevelModel(int id, String educationName) {
        this.id = id;
        this.educationName = educationName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEducationName() {
        return educationName;
    }

    public void setEducationName(String educationName) {
        this.educationName = educationName;
    }
}
