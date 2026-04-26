package smarthrms;

public class EmployeeEvaluationPeriodModel {
    private int id;
    private String evaluationperiod;

    // Constructor
    public EmployeeEvaluationPeriodModel(int id, String evaluationperiod) {
        this.id = id;
        this.evaluationperiod = evaluationperiod;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEvaluationperiod() {
        return evaluationperiod;
    }

    public void setEvaluationperiod(String evaluationperiod) {
        this.evaluationperiod = evaluationperiod;
    }

    @Override
    public String toString() {
        return "EmployeeEvaluationPeriodModel{" +
                "id=" + id +
                ", evaluationperiod='" + evaluationperiod + '\'' +
                '}';
    }
}
