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

public class ViewEmployeeDisciplanaryCases extends VBox {

    private TableView<EmployeeDisciplanaryViewModel> table;
    private ObservableList<EmployeeDisciplanaryViewModel> employeesDisciplanaryView;
    private FilteredList<EmployeeDisciplanaryViewModel> filteredData;

    // 🔍 Search fields
    private TextField empCodeField;
    private TextField fullNameField;
    private TextField caseField;
    private TextField reportedByField;

    public ViewEmployeeDisciplanaryCases() {
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

        caseField = new TextField();
        caseField.setPromptText("Search by Case");

        reportedByField = new TextField();
        reportedByField.setPromptText("Search by Reported By");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> applyFilter());

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> {
            empCodeField.clear();
            fullNameField.clear();
            caseField.clear();
            reportedByField.clear();
            filteredData.setPredicate(p -> true);
        });

        searchBox.getChildren().addAll(
                new Label("Filters:"),
                empCodeField, fullNameField, caseField, reportedByField,
                searchBtn, clearBtn
        );

        // ✅ Create TableView
        table = createTable(
                "Id", "Emp Code", "Employee FullName",
                "Disciplanary Case", "Reported Date", "Case Date", "Reported By"
        );

        // ✅ Example data
        employeesDisciplanaryView = FXCollections.observableArrayList(
                new EmployeeDisciplanaryViewModel(
                        1, "EMP001", "John Doe",
                        "Late attendance", "2025-01-12", "2025-01-10", "HR Manager"
                ),
                new EmployeeDisciplanaryViewModel(
                        2, "EMP002", "Jane Smith",
                        "Unapproved leave", "2025-02-01", "2025-01-28", "Supervisor"
                )
        );

        // ✅ Wrap with FilteredList
        filteredData = new FilteredList<>(employeesDisciplanaryView, p -> true);
        table.setItems(filteredData);

        // Add TableView inside ScrollPane
        ScrollPane scrollpane = new ScrollPane(table);
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(false);
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        layout.getChildren().addAll(
                new Label("Employee Disciplinary Cases From Another Page"),
                searchBox,
                scrollpane
        );
        this.getChildren().addAll(layout);
    }

    private TableView<EmployeeDisciplanaryViewModel> createTable(String... columnNames) {
        TableView<EmployeeDisciplanaryViewModel> table = new TableView<>();

        for (String columnName : columnNames) {
            TableColumn<EmployeeDisciplanaryViewModel, Object> column = new TableColumn<>(columnName);

            // Explicit mapping (instead of lowercase only)
            String propertyName;
            switch (columnName) {
                case "Id": propertyName = "id"; break;
                case "Emp Code": propertyName = "empcode"; break;
                case "Employee FullName": propertyName = "employeefullname"; break;
                case "Disciplanary Case": propertyName = "disciplanarycase"; break;
                case "Reported Date": propertyName = "reporteddate"; break;
                case "Case Date": propertyName = "casedate"; break;
                case "Reported By": propertyName = "reportedby"; break;
                default: propertyName = columnName.replace(" ", "").toLowerCase();
            }

            column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
            column.setPrefWidth(150);
            table.getColumns().add(column);
        }
        return table;
    }

    // 🔍 Apply filters
    private void applyFilter() {
        String empCode = empCodeField.getText();
        String fullName = fullNameField.getText();
        String caseTxt = caseField.getText();
        String reportedBy = reportedByField.getText();

        filteredData.setPredicate(record -> {
            if (!isBlank(empCode) && !record.getEmpcode().toLowerCase().contains(empCode.toLowerCase())) {
                return false;
            }
            if (!isBlank(fullName) && !record.getEmployeefullname().toLowerCase().contains(fullName.toLowerCase())) {
                return false;
            }
            if (!isBlank(caseTxt) && !record.getDisciplanarycase().toLowerCase().contains(caseTxt.toLowerCase())) {
                return false;
            }
            if (!isBlank(reportedBy) && !record.getReportedby().toLowerCase().contains(reportedBy.toLowerCase())) {
                return false;
            }
            return true;
        });
    }

    // ✅ Helper
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Public method to add data later
    public void addEmployeeDisciplines(EmployeeDisciplanaryViewModel empDiscipline) {
        employeesDisciplanaryView.add(empDiscipline);
    }
}
