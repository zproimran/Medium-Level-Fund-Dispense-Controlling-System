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

public class ViewEmployeeBackground extends VBox {

    private TableView<EmployeeBackgroundViewModel> table;
    private ObservableList<EmployeeBackgroundViewModel> employeesBackgroundView;
    private FilteredList<EmployeeBackgroundViewModel> filteredData;

    // 🔍 Search fields
    private TextField empCodeField;
    private TextField fullNameField;
    private TextField institutionField;
    private TextField eduLevelField;

    public ViewEmployeeBackground() {
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

        institutionField = new TextField();
        institutionField.setPromptText("Search by Institution");

        eduLevelField = new TextField();
        eduLevelField.setPromptText("Search by Education Level");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> applyFilter());

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> {
            empCodeField.clear();
            fullNameField.clear();
            institutionField.clear();
            eduLevelField.clear();
            filteredData.setPredicate(p -> true); // reset filter
        });

        searchBox.getChildren().addAll(
                new Label("Filters:"),
                empCodeField, fullNameField, institutionField, eduLevelField,
                searchBtn, clearBtn
        );

        // ✅ Create TableView
        table = createTable(
                "Id", "Emp Code", "Full Name", "Institution Name",
                "From", "To", "Major", "GPA", "Education Level"
        );

        // ✅ Example data
        employeesBackgroundView = FXCollections.observableArrayList(
                new EmployeeBackgroundViewModel(1, "EMP001", "John Doe", "Addis Ababa University",
                        "2015", "2019", "Computer Science", "3.8", "Bachelor"),
                new EmployeeBackgroundViewModel(2, "EMP002", "Jane Smith", "Haramaya University",
                        "2016", "2020", "Accounting", "3.6", "Bachelor")
        );

        // ✅ Wrap list with FilteredList
        filteredData = new FilteredList<>(employeesBackgroundView, p -> true);
        table.setItems(filteredData);

        // Add TableView inside scrollpane
        ScrollPane scrollpane = new ScrollPane(table);
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(false); 
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        layout.getChildren().addAll(
                new Label("Employee Background From Another Page"),
                searchBox,
                scrollpane
        );
        this.getChildren().addAll(layout);
    }

    private TableView<EmployeeBackgroundViewModel> createTable(String... columnNames) {
        TableView<EmployeeBackgroundViewModel> table = new TableView<>();

        for (String columnName : columnNames) {
            TableColumn<EmployeeBackgroundViewModel, Object> column = new TableColumn<>(columnName);

            // Explicit mapping for readability
            String propertyName;
            switch (columnName) {
                case "Id": propertyName = "id"; break;
                case "Emp Code": propertyName = "empcode"; break;
                case "Full Name": propertyName = "fullname"; break;
                case "Institution Name": propertyName = "institutionname"; break;
                case "From": propertyName = "from"; break;
                case "To": propertyName = "to"; break;
                case "Major": propertyName = "major"; break;
                case "GPA": propertyName = "gpa"; break;
                case "Education Level": propertyName = "educationlevel"; break;
                default: propertyName = columnName.replace(" ", "").toLowerCase();
            }

            column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
            column.setPrefWidth(150);
            table.getColumns().add(column);
        }
        return table;
    }

    // 🔍 Apply filter logic
    private void applyFilter() {
        String empCode = empCodeField.getText();
        String fullName = fullNameField.getText();
        String institution = institutionField.getText();
        String eduLevel = eduLevelField.getText();

        filteredData.setPredicate(background -> {
            if (!isBlank(empCode) && !background.getEmpcode().toLowerCase().contains(empCode.toLowerCase())) {
                return false;
            }
            if (!isBlank(fullName) && !background.getFullname().toLowerCase().contains(fullName.toLowerCase())) {
                return false;
            }
            if (!isBlank(institution) && !background.getInstitutionname().toLowerCase().contains(institution.toLowerCase())) {
                return false;
            }
            if (!isBlank(eduLevel) && !background.getEducationlevel().toLowerCase().contains(eduLevel.toLowerCase())) {
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
    public void addEmployee(EmployeeBackgroundViewModel employee) {
        employeesBackgroundView.add(employee);
    }
}
