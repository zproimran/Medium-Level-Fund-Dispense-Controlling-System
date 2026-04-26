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

public class ViewEmployeeWorkExperience extends VBox {

    private TableView<EmployeeWorkExperienceViewModel> table;
    private ObservableList<EmployeeWorkExperienceViewModel> employeesWorkExperienceView;
    private FilteredList<EmployeeWorkExperienceViewModel> filteredList;

    public ViewEmployeeWorkExperience() {
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
        searchField.setPromptText("Search by Employee Code, Name, Company...");
        searchField.setMaxWidth(400);

        // Create TableView
        table = createTable();

        // Initialize data
        employeesWorkExperienceView = FXCollections.observableArrayList(
            new EmployeeWorkExperienceViewModel(1, "EMP001", "John Doe", "ABC Construction", "Software Engineer", "2018-05-01", "2021-08-31"),
            new EmployeeWorkExperienceViewModel(2, "EMP002", "Jane Smith", "XYZ Bank", "Accountant", "2017-02-15", "2020-12-20")
        );

        filteredList = new FilteredList<>(employeesWorkExperienceView, p -> true);
        table.setItems(filteredList);

        // Search functionality
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String filter = newValue.toLowerCase().trim();
            filteredList.setPredicate(emp -> {
                if (isBlank(filter)) return true; // ✅ use custom method
                return emp.getEmpcode().toLowerCase().contains(filter)
                    || emp.getEmployeefullname().toLowerCase().contains(filter)
                    || emp.getCompanyname().toLowerCase().contains(filter)
                    || emp.getJobposition().toLowerCase().contains(filter);
            });
        });

        // Add TableView inside ScrollPane
        ScrollPane scrollpane = new ScrollPane(table);
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(false);
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        layout.getChildren().addAll(new Label("Employee Work Experience From Another Page"), searchField, scrollpane);
        this.getChildren().addAll(layout);
    }

    private TableView<EmployeeWorkExperienceViewModel> createTable() {
        TableView<EmployeeWorkExperienceViewModel> table = new TableView<>();

        // Explicit mapping
        String[][] columnMappings = {
            {"Id", "id"},
            {"Emp Code", "empcode"},
            {"Employee FullName", "employeefullname"},
            {"Company Name", "companyname"},
            {"Job Position", "jobposition"},
            {"Start Date", "startdate"},
            {"End Date", "enddate"}
        };

        for (String[] mapping : columnMappings) {
            TableColumn<EmployeeWorkExperienceViewModel, Object> column = new TableColumn<>(mapping[0]);
            column.setCellValueFactory(new PropertyValueFactory<>(mapping[1]));
            column.setPrefWidth(150);
            table.getColumns().add(column);
        }
        return table;
    }

    // Add new record
    public void addEmployeeWorkExperience(EmployeeWorkExperienceViewModel empWorkExperience) {
        employeesWorkExperienceView.add(empWorkExperience);
    }

    // ✅ Custom isBlank method
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
