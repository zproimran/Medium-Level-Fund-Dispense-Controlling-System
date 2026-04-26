package smarthrms;

public class EmployeeEvaluationTypeModel {
    private int id;
    private String evaluationname;

    // Constructor
    public EmployeeEvaluationTypeModel(int id, String evaluationname) {
        this.id = id;
        this.evaluationname = evaluationname;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEvaluationname() {
        return evaluationname;
    }

    public void setEvaluationname(String evaluationname) {
        this.evaluationname = evaluationname;
    }

    // For debugging / logging
    @Override
    public String toString() {
        return "EmployeeEvaluationTypeModel{" +
                "id=" + id +
                ", evaluationname='" + evaluationname + '\'' +
                '}';
    }
}
