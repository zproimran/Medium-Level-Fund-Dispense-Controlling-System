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

public class ViewPersonalInfo extends VBox {

    private TableView<EmployeePersonalViewModel> table;
    private ObservableList<EmployeePersonalViewModel> employeesPersonalView;
    private FilteredList<EmployeePersonalViewModel> filteredList;

    public ViewPersonalInfo() {
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f5f5f5;");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.TOP_LEFT);

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Employee Code, Name, Department, or Position...");
        searchField.setMaxWidth(400);

        // Create TableView
        table = createTable();

        // Initialize data
        employeesPersonalView = FXCollections.observableArrayList(
            new EmployeePersonalViewModel(
                1, "EMP001", "FP1001", "Mr", "John Doe", "Male", "Single", "0912345678",
                5000.0, "2020-05-01", "IT Department", "Software Engineer", "Active", 29, 3
            ),
            new EmployeePersonalViewModel(
                2, "EMP002", "FP1002", "Ms", "Jane Smith", "Female", "Married", "0923456789",
                4500.0, "2021-01-10", "Finance", "Accountant", "Active", 26, 1
            )
        );

        filteredList = new FilteredList<>(employeesPersonalView, p -> true);
        table.setItems(filteredList);

        // Search functionality
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String filter = newValue.toLowerCase().trim();
            filteredList.setPredicate(emp -> {
                if (isBlank(filter)) return true;
                return emp.getEmpcode().toLowerCase().contains(filter)
                    || emp.getFullname().toLowerCase().contains(filter)
                    || emp.getDepartment().toLowerCase().contains(filter)
                    || emp.getPosition().toLowerCase().contains(filter);
            });
        });

        // Add TableView inside ScrollPane
        ScrollPane scrollpane = new ScrollPane(table);
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(false);
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        layout.getChildren().addAll(new Label("Employee Info From Another Page"), searchField, scrollpane);
        this.getChildren().addAll(layout);
    }

    private TableView<EmployeePersonalViewModel> createTable() {
        TableView<EmployeePersonalViewModel> table = new TableView<>();

        String[] columnNames = {
            "Id", "Emp Code", "FP No", "Courtile Title", "Full Name",
            "Gender", "Marital", "Phone", "Monthly Salary", "Hire Date",
            "Department", "Position", "Status", "Age", "Service Year"
        };

        for (String columnName : columnNames) {
            TableColumn<EmployeePersonalViewModel, Object> column = new TableColumn<>(columnName);
            column.setCellValueFactory(new PropertyValueFactory<>(columnName.replace(" ", "").toLowerCase()));
            column.setPrefWidth(150);
            table.getColumns().add(column);
        }
        return table;
    }

    // Add new record
    public void addEmployee(EmployeePersonalViewModel employee) {
        employeesPersonalView.add(employee);
    }

    // ✅ Custom isBlank method
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
