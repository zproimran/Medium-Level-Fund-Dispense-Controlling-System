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

public class ViewEmployeeFamilyMember extends VBox {

    private TableView<EmployeeFamilyViewModel> table;
    private ObservableList<EmployeeFamilyViewModel> employeesFamilyView;
    private FilteredList<EmployeeFamilyViewModel> filteredData;

    // 🔍 Search fields
    private TextField empCodeField;
    private TextField empFullNameField;
    private TextField familyFullNameField;
    private TextField phoneField;
    private TextField relationshipField;
    private TextField maritalStatusField;

    public ViewEmployeeFamilyMember() {
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

        empFullNameField = new TextField();
        empFullNameField.setPromptText("Search by Employee Full Name");

        familyFullNameField = new TextField();
        familyFullNameField.setPromptText("Search by Family Full Name");

        phoneField = new TextField();
        phoneField.setPromptText("Search by Phone");

        relationshipField = new TextField();
        relationshipField.setPromptText("Search by Relationship");

        maritalStatusField = new TextField();
        maritalStatusField.setPromptText("Search by Marital Status");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> applyFilter());

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> {
            empCodeField.clear();
            empFullNameField.clear();
            familyFullNameField.clear();
            phoneField.clear();
            relationshipField.clear();
            maritalStatusField.clear();
            filteredData.setPredicate(p -> true);
        });

        searchBox.getChildren().addAll(
                new Label("Filters:"),
                empCodeField, empFullNameField, familyFullNameField,
                phoneField, relationshipField, maritalStatusField,
                searchBtn, clearBtn
        );

        // ✅ Create TableView
        table = createTable(
                "Id", "Emp Code", "Employee FullName", "Family FullName", "Phone Number", "Gender",
                "Education Level", "Relationship", "Marital Status"
        );

        // ✅ Example data
        employeesFamilyView = FXCollections.observableArrayList(
                new EmployeeFamilyViewModel(
                        1, "EMP001", "John Doe", "Michael Doe", "0911-123456",
                        "Male", "BSc Computer Science", "Brother", "Single"
                ),
                new EmployeeFamilyViewModel(
                        2, "EMP002", "Jane Smith", "Sarah Smith", "0922-987654",
                        "Female", "Diploma in Accounting", "Mother", "Married"
                )
        );

        // ✅ Wrap with FilteredList
        filteredData = new FilteredList<>(employeesFamilyView, p -> true);
        table.setItems(filteredData);

        // Add TableView inside scrollpane
        ScrollPane scrollpane = new ScrollPane(table);
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(false);
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        layout.getChildren().addAll(
                new Label("Employee Family From Another Page"),
                searchBox,
                scrollpane
        );
        this.getChildren().addAll(layout);
    }

    private TableView<EmployeeFamilyViewModel> createTable(String... columnNames) {
        TableView<EmployeeFamilyViewModel> table = new TableView<>();

        for (String columnName : columnNames) {
            TableColumn<EmployeeFamilyViewModel, Object> column = new TableColumn<>(columnName);

            // Explicit property mapping
            String propertyName;
            switch (columnName) {
                case "Id": propertyName = "id"; break;
                case "Emp Code": propertyName = "empcode"; break;
                case "Employee FullName": propertyName = "employeefullname"; break;
                case "Family FullName": propertyName = "familyfullname"; break;
                case "Phone Number": propertyName = "phonenumber"; break;
                case "Gender": propertyName = "gender"; break;
                case "Education Level": propertyName = "educationlevel"; break;
                case "Relationship": propertyName = "relationship"; break;
                case "Marital Status": propertyName = "maritalstatus"; break;
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
        String empFullName = empFullNameField.getText();
        String familyName = familyFullNameField.getText();
        String phone = phoneField.getText();
        String relation = relationshipField.getText();
        String marital = maritalStatusField.getText();

        filteredData.setPredicate(record -> {
            if (!isBlank(empCode) && !record.getEmpcode().toLowerCase().contains(empCode.toLowerCase())) {
                return false;
            }
            if (!isBlank(empFullName) && !record.getEmployeefullname().toLowerCase().contains(empFullName.toLowerCase())) {
                return false;
            }
            if (!isBlank(familyName) && !record.getFamilyfullname().toLowerCase().contains(familyName.toLowerCase())) {
                return false;
            }
            if (!isBlank(phone) && !record.getPhonenumber().toLowerCase().contains(phone.toLowerCase())) {
                return false;
            }
            if (!isBlank(relation) && !record.getRelationship().toLowerCase().contains(relation.toLowerCase())) {
                return false;
            }
            if (!isBlank(marital) && !record.getMaritalstatus().toLowerCase().contains(marital.toLowerCase())) {
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
    public void addEmployeeFamily(EmployeeFamilyViewModel empFamily) {
        employeesFamilyView.add(empFamily);
    }
}
