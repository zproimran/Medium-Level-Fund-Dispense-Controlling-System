package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class ViewEmployeeRemark extends VBox {

    private TableView<EmployeeRemarkViewModel> table;
    private ObservableList<EmployeeRemarkViewModel> employeesRemarkView;
    private FilteredList<EmployeeRemarkViewModel> filteredList;

    public ViewEmployeeRemark() {
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f5f5f5;");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.TOP_LEFT);

        // ✅ Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Emp Code, Name, Bank, or Department...");
        searchField.setMaxWidth(400);

        // ✅ Create TableView
        table = createTable();

        // ✅ Initialize data
        employeesRemarkView = FXCollections.observableArrayList(
            new EmployeeRemarkViewModel(
                1, "EMP001", "John Doe", "LIC12345", "1234567890",
                "Commercial Bank", "IT Department", "Software Engineer",
                "Java Development", "Excellent problem solver"
            ),
            new EmployeeRemarkViewModel(
                2, "EMP002", "Jane Smith", "LIC67890", "9876543210",
                "Awash Bank", "Finance", "Accountant",
                "Taxation", "Very detail-oriented"
            )
        );

        filteredList = new FilteredList<>(employeesRemarkView, p -> true);
        table.setItems(filteredList);

        // ✅ Search functionality
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String filter = newValue.toLowerCase().trim();
            filteredList.setPredicate(emp -> {
                if (isBlank(filter)) return true;
                return emp.getEmpcode().toLowerCase().contains(filter)
                    || emp.getFullname().toLowerCase().contains(filter)
                    || emp.getBank().toLowerCase().contains(filter)
                    || emp.getDepartment().toLowerCase().contains(filter);
            });
        });

        // Wrap TableView inside ScrollPane
        ScrollPane scrollpane = new ScrollPane(table);
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(false);
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        layout.getChildren().addAll(new Label("Employee Remark From Another Page"), searchField, scrollpane);
        this.getChildren().addAll(layout);
    }

    private TableView<EmployeeRemarkViewModel> createTable() {
        TableView<EmployeeRemarkViewModel> table = new TableView<>();

        String[][] columnMappings = {
            {"Id", "id"},
            {"Emp Code", "empcode"},
            {"Full Name", "fullname"},
            {"License No", "licenseno"},
            {"Bank Account", "bankaccount"},
            {"Bank", "bank"},
            {"Department", "department"},
            {"Assigned Position", "assignedposition"},
            {"Specialization", "specialization"},
            {"Remark", "remark"}
        };

        for (String[] mapping : columnMappings) {
            TableColumn<EmployeeRemarkViewModel, Object> column = new TableColumn<>(mapping[0]);
            column.setCellValueFactory(new PropertyValueFactory<>(mapping[1]));
            column.setPrefWidth(150);
            table.getColumns().add(column);
        }

        return table;
    }

    // Public method to add data in future
    public void addEmployee(EmployeeRemarkViewModel employee) {
        employeesRemarkView.add(employee);
    }

    // ✅ Safe isBlank method
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
