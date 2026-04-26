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

public class ViewEmployeeRewards extends VBox {

    private TableView<EmployeeRewardViewModel> table;
    private ObservableList<EmployeeRewardViewModel> employeesRewardView;
    private FilteredList<EmployeeRewardViewModel> filteredList;

    public ViewEmployeeRewards() {
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
        searchField.setPromptText("Search by Emp Code, Name, Award or Company...");
        searchField.setMaxWidth(400);

        // ✅ Create TableView
        table = createTable();

        // ✅ Initialize data
        employeesRewardView = FXCollections.observableArrayList(
            new EmployeeRewardViewModel(
                1, "EMP001", "John Doe",
                "Best Employee of the Year", "ABC Construction",
                "2023-12-15", "Recognized for outstanding performance"
            ),
            new EmployeeRewardViewModel(
                2, "EMP002", "Jane Smith",
                "Excellence in Customer Service", "XYZ Bank",
                "2024-03-20", "Awarded for exceptional client support"
            )
        );

        filteredList = new FilteredList<>(employeesRewardView, p -> true);
        table.setItems(filteredList);

        // ✅ Search functionality
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String filter = newValue.toLowerCase().trim();
            filteredList.setPredicate(emp -> {
                if (isBlank(filter)) return true;
                return emp.getEmpcode().toLowerCase().contains(filter)
                    || emp.getEmployeefullname().toLowerCase().contains(filter)
                    || emp.getAward().toLowerCase().contains(filter)
                    || emp.getFromcompanyname().toLowerCase().contains(filter);
            });
        });

        // Add TableView inside ScrollPane
        ScrollPane scrollpane = new ScrollPane(table);
        scrollpane.setFitToHeight(true);
        scrollpane.setFitToWidth(false);
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        layout.getChildren().addAll(new Label("Employee Rewards From Another Page"), searchField, scrollpane);
        this.getChildren().addAll(layout);
    }

    private TableView<EmployeeRewardViewModel> createTable() {
        TableView<EmployeeRewardViewModel> table = new TableView<>();

        String[][] columnMappings = {
            {"Id", "id"},
            {"Emp Code", "empcode"},
            {"Employee FullName", "employeefullname"},
            {"Award", "award"},
            {"From CompanyName", "fromcompanyname"},
            {"Award Date", "awarddate"},
            {"Remark", "remark"}
        };

        for (String[] mapping : columnMappings) {
            TableColumn<EmployeeRewardViewModel, Object> column = new TableColumn<>(mapping[0]);
            column.setCellValueFactory(new PropertyValueFactory<>(mapping[1]));
            column.setPrefWidth(180);
            table.getColumns().add(column);
        }

        return table;
    }

    // Public method to add data later
    public void addEmployeeReward(EmployeeRewardViewModel empReward) {
        employeesRewardView.add(empReward);
    }

    // ✅ Custom isBlank method
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
