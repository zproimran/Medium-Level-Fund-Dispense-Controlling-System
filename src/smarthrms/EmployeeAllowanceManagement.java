package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class EmployeeAllowanceManagement extends BorderPane {
    private String currentUser;

    private TableView<EmployeeAllowanceRecordModel> allowanceTable;
    private ObservableList<EmployeeAllowanceRecordModel> allowanceData = FXCollections.observableArrayList();

    public EmployeeAllowanceManagement(String username) {
        this.currentUser=username;
        // Top row buttons
        HBox buttonPanel = createButtonPanel();
        VBox tableSection = createTableSection();

        this.setTop(buttonPanel);
        this.setCenter(tableSection);

        loadSampleData();
    }

    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(15));
        buttonPanel.setStyle("-fx-background-color: #e9ecef; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
        buttonPanel.setAlignment(Pos.CENTER_LEFT);

        Button editBtn = new Button("Edit Selected");
        editBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
        editBtn.setOnAction(e -> editSelected());

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteSelected());

        Button approveBtn = new Button("Approve Selected");
        approveBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        approveBtn.setOnAction(e -> approveSelected());

        Button exportBtn = new Button("Export to Excel");
        exportBtn.setStyle("-fx-background-color: #fd7e14; -fx-text-fill: white;");
        exportBtn.setOnAction(e -> exportToExcel());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #6f42c1; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> refreshData());

        buttonPanel.getChildren().addAll(editBtn, deleteBtn, approveBtn, exportBtn, refreshBtn);
        return buttonPanel;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox();
        tableSection.setPadding(new Insets(15));

        // Search/filter
        HBox filterBox = new HBox(10);
        filterBox.setPadding(new Insets(0, 0, 10, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("Search employee...");
        searchField.setPrefWidth(300);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Completed");
        statusFilter.setValue("All");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> filterTable(searchField.getText(), statusFilter.getValue()));

        filterBox.getChildren().addAll(new Label("Search:"), searchField, new Label("Status:"), statusFilter, searchBtn);

        // Table
        allowanceTable = new TableView<>();
        allowanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<EmployeeAllowanceRecordModel, String> empCodeCol = new TableColumn<>("Employee Code");
        empCodeCol.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));
        
        TableColumn<EmployeeAllowanceRecordModel, String> empNameCol = new TableColumn<>("Employee Name");
        empNameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<EmployeeAllowanceRecordModel, String> empDeptCol = new TableColumn<>("Department");
        empDeptCol.setCellValueFactory(new PropertyValueFactory<>("employeeDepartment"));

        TableColumn<EmployeeAllowanceRecordModel, String> allowanceTypeCol = new TableColumn<>("Allowance Type");
        allowanceTypeCol.setCellValueFactory(new PropertyValueFactory<>("allowanceType"));

        TableColumn<EmployeeAllowanceRecordModel, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<EmployeeAllowanceRecordModel, LocalDate> startCol = new TableColumn<>("Start Date");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<EmployeeAllowanceRecordModel, LocalDate> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<EmployeeAllowanceRecordModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<EmployeeAllowanceRecordModel, String> approvedByCol = new TableColumn<>("Approved By");
        approvedByCol.setCellValueFactory(new PropertyValueFactory<>("approvedBy"));

        allowanceTable.getColumns().addAll(empCodeCol,empNameCol, empDeptCol, allowanceTypeCol, amountCol, startCol, endCol, statusCol, approvedByCol);
        allowanceTable.setItems(allowanceData);
        allowanceTable.setPlaceholder(new Label("No allowance records found"));

        tableSection.getChildren().addAll(filterBox, allowanceTable);
        return tableSection;
    }

    private void showAllowanceForm(EmployeeAllowanceRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(record == null ? "Add Allowance" : "Edit Allowance");
        dialog.setHeaderText(record == null ? "Add New Allowance" : "Edit Allowance Record");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField empCodeField = new TextField(record == null ? "" : record.getEmployeeCode());
        TextField empNameField = new TextField(record == null ? "" : record.getEmployeeName());
        TextField empDeptField = new TextField(record == null ? "" : record.getEmployeeDepartment());

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Housing", "Transport", "Meal", "Medical", "Internet", "Other");
        typeBox.setValue(record == null ? "Housing" : record.getAllowanceType());

        TextField amountField = new TextField(record == null ? "" : String.valueOf(record.getAmount()));
        DatePicker startDate = new DatePicker(record == null ? LocalDate.now() : record.getStartDate());
        DatePicker endDate = new DatePicker(record == null ? LocalDate.now().plusMonths(1) : record.getEndDate());

        grid.addRow(0, new Label("Employee Code:"), empCodeField);
        grid.addRow(1, new Label("Employee Name:"), empNameField);
        grid.addRow(2, new Label("Employee Department:"), empDeptField);
        grid.addRow(3, new Label("Allowance Type:"), typeBox);
        grid.addRow(4, new Label("Amount:"), amountField);
        grid.addRow(5, new Label("Start Date:"), startDate);
        grid.addRow(6, new Label("End Date:"), endDate);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (record == null) {
                    EmployeeAllowanceRecordModel newRecord = new EmployeeAllowanceRecordModel(
                            empCodeField.getText(),
                            empNameField.getText(),
                            empDeptField.getText(),
                            typeBox.getValue(),
                            Double.parseDouble(amountField.getText()),
                            startDate.getValue(),
                            endDate.getValue(),
                            "Pending",
                            ""
                    );
                    allowanceData.add(newRecord);
                } else {
                    record.setEmployeeCode(empCodeField.getText());
                    record.setEmployeeName(empNameField.getText());
                    record.setEmployeeDepartment(empDeptField.getText());
                    record.setAllowanceType(typeBox.getValue());
                    record.setAmount(Double.parseDouble(amountField.getText()));
                    record.setStartDate(startDate.getValue());
                    record.setEndDate(endDate.getValue());
                }
                allowanceTable.refresh();
            }
        });
    }

    private void editSelected() {
        EmployeeAllowanceRecordModel selected = allowanceTable.getSelectionModel().getSelectedItem();
        if (selected != null) showAllowanceForm(selected);
        else new Alert(Alert.AlertType.WARNING, "Please select a record to edit.", ButtonType.OK).showAndWait();
    }

    private void deleteSelected() {
        EmployeeAllowanceRecordModel selected = allowanceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this allowance?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) allowanceData.remove(selected);
            });
        }
    }

    private void approveSelected() {
        EmployeeAllowanceRecordModel selected = allowanceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Approved");
            selected.setApprovedBy("Admin User");
            allowanceTable.refresh();
        }
    }

    private void exportToExcel() {
        new Alert(Alert.AlertType.INFORMATION, "Exported to Excel (mockup)", ButtonType.OK).showAndWait();
    }

    private void refreshData() {
        new Alert(Alert.AlertType.INFORMATION, "Data refreshed (mockup)", ButtonType.OK).showAndWait();
    }

    private void filterTable(String search, String status) {
        new Alert(Alert.AlertType.INFORMATION, "Filter applied: " + search + " | " + status, ButtonType.OK).showAndWait();
    }

    private void loadSampleData() {
        allowanceData.add(new EmployeeAllowanceRecordModel("EMP001", "John Doe", "IT", "Housing", 1500.0,
                LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "Approved", "HR Manager"));
        allowanceData.add(new EmployeeAllowanceRecordModel("EMP002", "Jane Smith", "HR", "Transport", 500.0,
                LocalDate.of(2023, 3, 1), LocalDate.of(2023, 9, 1), "Pending", ""));
        allowanceData.add(new EmployeeAllowanceRecordModel("EMP003", "Robert Johnson", "Finance", "Meal", 300.0,
                LocalDate.of(2023, 2, 1), LocalDate.of(2023, 12, 31), "Approved", "Admin User"));
    }


}
