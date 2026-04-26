package smarthrms;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeAppraisalViewModel {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty empcode;
    private final SimpleStringProperty fullname;
    private final SimpleStringProperty evaluationdate;
    private final SimpleStringProperty evaluationperiod;
    private final SimpleStringProperty evaluationtype;
    private final SimpleIntegerProperty score;
    private final SimpleDoubleProperty average;
    private final SimpleIntegerProperty total;

    public EmployeeAppraisalViewModel(int id, String empcode, String fullname,
                                      String evaluationdate, String evaluationperiod,
                                      String evaluationtype, int score, double average, int total) {
        this.id = new SimpleIntegerProperty(id);
        this.empcode = new SimpleStringProperty(empcode);
        this.fullname = new SimpleStringProperty(fullname);
        this.evaluationdate = new SimpleStringProperty(evaluationdate);
        this.evaluationperiod = new SimpleStringProperty(evaluationperiod);
        this.evaluationtype = new SimpleStringProperty(evaluationtype);
        this.score = new SimpleIntegerProperty(score);
        this.average = new SimpleDoubleProperty(average);
        this.total = new SimpleIntegerProperty(total);
    }

    // ✅ Getters, Setters, and Property methods

    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public SimpleIntegerProperty idProperty() { return id; }

    public String getEmpcode() { return empcode.get(); }
    public void setEmpcode(String value) { empcode.set(value); }
    public SimpleStringProperty empcodeProperty() { return empcode; }

    public String getFullname() { return fullname.get(); }
    public void setFullname(String value) { fullname.set(value); }
    public SimpleStringProperty fullnameProperty() { return fullname; }

    public String getEvaluationdate() { return evaluationdate.get(); }
    public void setEvaluationdate(String value) { evaluationdate.set(value); }
    public SimpleStringProperty evaluationdateProperty() { return evaluationdate; }

    public String getEvaluationperiod() { return evaluationperiod.get(); }
    public void setEvaluationperiod(String value) { evaluationperiod.set(value); }
    public SimpleStringProperty evaluationperiodProperty() { return evaluationperiod; }

    public String getEvaluationtype() { return evaluationtype.get(); }
    public void setEvaluationtype(String value) { evaluationtype.set(value); }
    public SimpleStringProperty evaluationtypeProperty() { return evaluationtype; }

    public int getScore() { return score.get(); }
    public void setScore(int value) { score.set(value); }
    public SimpleIntegerProperty scoreProperty() { return score; }

    public double getAverage() { return average.get(); }
    public void setAverage(double value) { average.set(value); }
    public SimpleDoubleProperty averageProperty() { return average; }

    public int getTotal() { return total.get(); }
    public void setTotal(int value) { total.set(value); }
    public SimpleIntegerProperty totalProperty() { return total; }
}
