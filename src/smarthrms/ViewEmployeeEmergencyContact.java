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

public class ViewEmployeeEmergencyContact extends VBox {

    private TableView<EmployeeEmergencyContactViewModel> table;
    private ObservableList<EmployeeEmergencyContactViewModel> employeesEmergencyContactView;
    private FilteredList<EmployeeEmergencyContactViewModel> filteredData;

    // 🔍 Search fields
    private TextField empCodeField;
    private TextField fullNameField;
    private TextField contactNameField;
    private TextField relationshipField;
    private TextField cityField;
    private TextField cellPhoneField;

    public ViewEmployeeEmergencyContact() {
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

        contactNameField = new TextField();
        contactNameField.setPromptText("Search by Contact Name");

        relationshipField = new TextField();
        relationshipField.setPromptText("Search by Relationship");

        cityField = new TextField();
        cityField.setPromptText("Search by City");

        cellPhoneField = new TextField();
        cellPhoneField.setPromptText("Search by Cell Phone");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> applyFilter());

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> {
            empCodeField.clear();
            fullNameField.clear();
            contactNameField.clear();
            relationshipField.clear();
            cityField.clear();
            cellPhoneField.clear();
            filteredData.setPredicate(p -> true);
        });

        searchBox.getChildren().addAll(
                new Label("Filters:"),
                empCodeField, fullNameField, contactNameField,
                relationshipField, cityField, cellPhoneField,
                searchBtn, clearBtn
        );

        // ✅ Create TableView
        table = createTable(
                "Id", "Emp Code", "Full Name", "Contact Name", "Relationship",
                "Occupation", "Work Place", "City", "Subcity", "Woreda", "Home Phone", "Cell Phone"
        );

        // ✅ Example data
        employeesEmergencyContactView = FXCollections.observableArrayList(
                new EmployeeEmergencyContactViewModel(
                        1, "EMP001", "John Doe", "Michael Doe", "Brother",
                        "Engineer", "ABC Construction", "Addis Ababa", "Bole", "01",
                        "011-123456", "0911-123456"
                ),
                new EmployeeEmergencyContactViewModel(
                        2, "EMP002", "Jane Smith", "Sarah Smith", "Mother",
                        "Teacher", "High School", "Adama", "Kebele 02", "05",
                        "022-987654", "0922-987654"
                )
        );

        // ✅ Wrap with FilteredList
        filteredData = new FilteredList<>(employeesEmergencyContactView, p -> true);
        table.setItems(filteredData);

        // Add TableView inside ScrollPane
        ScrollPane scrollpane = new ScrollPane(table);
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(false);
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        layout.getChildren().addAll(
                new Label("Employee Emergency Contacts From Another Page"),
                searchBox,
                scrollpane
        );
        this.getChildren().addAll(layout);
    }

    private TableView<EmployeeEmergencyContactViewModel> createTable(String... columnNames) {
        TableView<EmployeeEmergencyContactViewModel> table = new TableView<>();

        for (String columnName : columnNames) {
            TableColumn<EmployeeEmergencyContactViewModel, Object> column = new TableColumn<>(columnName);

            // Explicit mapping
            String propertyName;
            switch (columnName) {
                case "Id": propertyName = "id"; break;
                case "Emp Code": propertyName = "empcode"; break;
                case "Full Name": propertyName = "fullname"; break;
                case "Contact Name": propertyName = "contactname"; break;
                case "Relationship": propertyName = "relationship"; break;
                case "Occupation": propertyName = "occupation"; break;
                case "Work Place": propertyName = "workplace"; break;
                case "City": propertyName = "city"; break;
                case "Subcity": propertyName = "subcity"; break;
                case "Woreda": propertyName = "woreda"; break;
                case "Home Phone": propertyName = "homephone"; break;
                case "Cell Phone": propertyName = "cellphone"; break;
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
        String contactName = contactNameField.getText();
        String relationship = relationshipField.getText();
        String city = cityField.getText();
        String cellPhone = cellPhoneField.getText();

        filteredData.setPredicate(record -> {
            if (!isBlank(empCode) && !record.getEmpcode().toLowerCase().contains(empCode.toLowerCase())) {
                return false;
            }
            if (!isBlank(fullName) && !record.getFullname().toLowerCase().contains(fullName.toLowerCase())) {
                return false;
            }
            if (!isBlank(contactName) && !record.getContactname().toLowerCase().contains(contactName.toLowerCase())) {
                return false;
            }
            if (!isBlank(relationship) && !record.getRelationship().toLowerCase().contains(relationship.toLowerCase())) {
                return false;
            }
            if (!isBlank(city) && !record.getCity().toLowerCase().contains(city.toLowerCase())) {
                return false;
            }
            if (!isBlank(cellPhone) && !record.getCellphone().toLowerCase().contains(cellPhone.toLowerCase())) {
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
    public void addEmployee(EmployeeEmergencyContactViewModel employee) {
        employeesEmergencyContactView.add(employee);
    }
}
