package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ViewEmployeeAppraisal extends VBox {

    private TableView<EmployeeAppraisalViewModel> table;
    private ObservableList<EmployeeAppraisalViewModel> employeesAppraisalView;
    private FilteredList<EmployeeAppraisalViewModel> filteredData;

    // Search fields
    private TextField empCodeField;
    private TextField fullNameField;
    private TextField evalTypeField;

    public ViewEmployeeAppraisal() {
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f5f5f5;");

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.TOP_LEFT);

        // 🔍 Search bar
        HBox searchBox = new HBox(10);
        empCodeField = new TextField();
        empCodeField.setPromptText("Search by Emp Code");

        fullNameField = new TextField();
        fullNameField.setPromptText("Search by Full Name");

        evalTypeField = new TextField();
        evalTypeField.setPromptText("Search by Evaluation Type");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> applyFilter());

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> {
            empCodeField.clear();
            fullNameField.clear();
            evalTypeField.clear();
            filteredData.setPredicate(p -> true); // reset
        });

        searchBox.getChildren().addAll(
                new Label("Filters:"), empCodeField, fullNameField, evalTypeField, searchBtn, clearBtn
        );

        // Create TableView
        table = createTable(
                "Id", "Emp Code", "Full Name", "Evaluation Date",
                "Evaluation Period", "Evaluation Type", "Score", "Average", "Total"
        );

        // Data
        employeesAppraisalView = FXCollections.observableArrayList(
                new EmployeeAppraisalViewModel(1, "EMP001", "John Doe", "2025-01-10",
                        "Q1 2025", "Performance", 85, 80.5, 250),
                new EmployeeAppraisalViewModel(2, "EMP002", "Jane Smith", "2025-02-15",
                        "Q1 2025", "Attendance", 90, 88.0, 265)
        );

        filteredData = new FilteredList<>(employeesAppraisalView, p -> true);
        table.setItems(filteredData);

        // ScrollPane
        ScrollPane scrollpane = new ScrollPane(table);
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(false);
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        layout.getChildren().addAll(
                new Label("Employee Appraisal From Another Page"),
                searchBox,
                scrollpane
        );
        this.getChildren().addAll(layout);
    }

    private TableView<EmployeeAppraisalViewModel> createTable(String... columnNames) {
        TableView<EmployeeAppraisalViewModel> table = new TableView<>();

        for (String columnName : columnNames) {
            TableColumn<EmployeeAppraisalViewModel, Object> column = new TableColumn<>(columnName);

            String propertyName;
            switch (columnName) {
                case "Id": propertyName = "id"; break;
                case "Emp Code": propertyName = "empcode"; break;
                case "Full Name": propertyName = "fullname"; break;
                case "Evaluation Date": propertyName = "evaluationdate"; break;
                case "Evaluation Period": propertyName = "evaluationperiod"; break;
                case "Evaluation Type": propertyName = "evaluationtype"; break;
                case "Score": propertyName = "score"; break;
                case "Average": propertyName = "average"; break;
                case "Total": propertyName = "total"; break;
                default: propertyName = columnName.replace(" ", "").toLowerCase();
            }

            column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
            column.setPrefWidth(150);
            table.getColumns().add(column);
        }
        return table;
    }

    // 🔹 Filtering logic using isBlank
    private void applyFilter() {
        String empCode = empCodeField.getText();
        String fullName = fullNameField.getText();
        String evalType = evalTypeField.getText();

        filteredData.setPredicate(appraisal -> {
            if (!isBlank(empCode) && !appraisal.getEmpcode().toLowerCase().contains(empCode.toLowerCase())) {
                return false;
            }
            if (!isBlank(fullName) && !appraisal.getFullname().toLowerCase().contains(fullName.toLowerCase())) {
                return false;
            }
            if (!isBlank(evalType) && !appraisal.getEvaluationtype().toLowerCase().contains(evalType.toLowerCase())) {
                return false;
            }
            return true;
        });
    }

    // ✅ isBlank helper
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Public method to add data later
    public void addEmployee(EmployeeAppraisalViewModel appraisal) {
        employeesAppraisalView.add(appraisal);
    }
}
