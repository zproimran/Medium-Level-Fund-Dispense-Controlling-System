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

public class ViewEmployeeLeave extends VBox {

    private TableView<EmployeeLeaveViewModel> table;
    private ObservableList<EmployeeLeaveViewModel> employeesLeaveView;
    private FilteredList<EmployeeLeaveViewModel> filteredData;

    // 🔍 Search fields
    private TextField empCodeField;
    private TextField fullNameField;
    private TextField leaveTypeField;
    private TextField yearField;

    public ViewEmployeeLeave() {
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

        leaveTypeField = new TextField();
        leaveTypeField.setPromptText("Search by Leave Type");

        yearField = new TextField();
        yearField.setPromptText("Search by Year");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> applyFilter());

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> {
            empCodeField.clear();
            fullNameField.clear();
            leaveTypeField.clear();
            yearField.clear();
            filteredData.setPredicate(p -> true);
        });

        searchBox.getChildren().addAll(
                new Label("Filters:"),
                empCodeField, fullNameField, leaveTypeField, yearField,
                searchBtn, clearBtn
        );

        // ✅ Create TableView
        table = createTable();

        // ✅ Example data
        employeesLeaveView = FXCollections.observableArrayList(
                new EmployeeLeaveViewModel(
                        1, "EMP001", "John Doe", "Half", "Annual Leave",
                        "2018", "01/08/2025", "01/09/2025"
                ),
                new EmployeeLeaveViewModel(
                        2, "EMP002", "Jane Smith", "Full", "Sick Leave",
                        "2020", "05/08/2025", "15/08/2025"
                )
        );

        // ✅ Wrap with FilteredList
        filteredData = new FilteredList<>(employeesLeaveView, p -> true);
        table.setItems(filteredData);

        // Add TableView inside scrollpane
        ScrollPane scrollpane = new ScrollPane(table);
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(false);
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        layout.getChildren().addAll(
                new Label("Employee Leave From Another Page"),
                searchBox,
                scrollpane
        );
        this.getChildren().addAll(layout);
    }

    private TableView<EmployeeLeaveViewModel> createTable() {
        TableView<EmployeeLeaveViewModel> table = new TableView<>();

        String[][] columnMappings = {
                {"Id", "id"},
                {"Emp Code", "empcode"},
                {"Full Name", "fullname"},
                {"Day Status", "daystatus"},
                {"Leave Type", "leavetype"},
                {"Year", "year"},
                {"From", "fromdate"},
                {"To", "todate"}
        };

        for (String[] mapping : columnMappings) {
            TableColumn<EmployeeLeaveViewModel, Object> column = new TableColumn<>(mapping[0]);
            column.setCellValueFactory(new PropertyValueFactory<>(mapping[1]));
            column.setPrefWidth(150);
            table.getColumns().add(column);
        }
        return table;
    }

    // 🔍 Apply filters
    private void applyFilter() {
        String empCode = empCodeField.getText();
        String fullName = fullNameField.getText();
        String leaveType = leaveTypeField.getText();
        String year = yearField.getText();

        filteredData.setPredicate(record -> {
            if (!isBlank(empCode) && !record.getEmpcode().toLowerCase().contains(empCode.toLowerCase())) {
                return false;
            }
            if (!isBlank(fullName) && !record.getFullname().toLowerCase().contains(fullName.toLowerCase())) {
                return false;
            }
            if (!isBlank(leaveType) && !record.getLeavetype().toLowerCase().contains(leaveType.toLowerCase())) {
                return false;
            }
            if (!isBlank(year) && !record.getYear().toLowerCase().contains(year.toLowerCase())) {
                return false;
            }
            return true;
        });
    }

    // ✅ Helper
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Public method to add data in future
    public void addEmployee(EmployeeLeaveViewModel leave) {
        employeesLeaveView.add(leave);
    }
}
