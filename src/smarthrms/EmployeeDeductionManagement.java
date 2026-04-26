package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class EmployeeDeductionManagement extends BorderPane {

    private String currentUser;
    private TableView<EmployeeDeductionRecordModel> deductionTable;
    private ObservableList<EmployeeDeductionRecordModel> deductionData = FXCollections.observableArrayList();

    public EmployeeDeductionManagement(String username) {
        this.currentUser=username;
        // Create top section with buttons
        HBox buttonPanel = createButtonPanel();

        // Create center section with table
        VBox tableSection = createTableSection();

        // Set up the layout
        this.setTop(buttonPanel);
        this.setCenter(tableSection);

        // Load some sample data
        loadSampleData();
    }

    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(15));
        buttonPanel.setStyle("-fx-background-color: #e9ecef; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
        buttonPanel.setAlignment(Pos.CENTER_LEFT);

        // Buttons
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

        // Search / Filter
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
        deductionTable = new TableView<>();
        deductionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<EmployeeDeductionRecordModel, String> idCol = new TableColumn<>("Employee ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<EmployeeDeductionRecordModel, String> nameCol = new TableColumn<>("Employee Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<EmployeeDeductionRecordModel, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<EmployeeDeductionRecordModel, String> typeCol = new TableColumn<>("Deduction Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("deductionType"));

        TableColumn<EmployeeDeductionRecordModel, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<EmployeeDeductionRecordModel, LocalDate> startCol = new TableColumn<>("Start Date");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<EmployeeDeductionRecordModel, LocalDate> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<EmployeeDeductionRecordModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<EmployeeDeductionRecordModel, String> approvedByCol = new TableColumn<>("Approved By");
        approvedByCol.setCellValueFactory(new PropertyValueFactory<>("approvedBy"));

        deductionTable.getColumns().addAll(idCol, nameCol, deptCol, typeCol, amountCol, startCol, endCol, statusCol, approvedByCol);
        deductionTable.setItems(deductionData);
        deductionTable.setPlaceholder(new Label("No deduction records found"));

        tableSection.getChildren().addAll(filterBox, deductionTable);
        return tableSection;
    }

    private void showDeductionForm(EmployeeDeductionRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(record == null ? "Add Deduction" : "Edit Deduction");
        dialog.setHeaderText(record == null ? "Add New Deduction" : "Edit Deduction Record");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Simple form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField empIdField = new TextField(record == null ? "" : record.getEmployeeId());
        TextField nameField = new TextField(record == null ? "" : record.getEmployeeName());
        TextField deptField = new TextField(record == null ? "" : record.getDepartment());

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Tax", "Pension", "Loan", "Penalty", "Insurance", "Other");
        typeBox.setValue(record == null ? "Tax" : record.getDeductionType());

        TextField amountField = new TextField(record == null ? "" : String.valueOf(record.getAmount()));
        DatePicker startDate = new DatePicker(record == null ? LocalDate.now() : record.getStartDate());
        DatePicker endDate = new DatePicker(record == null ? LocalDate.now().plusMonths(1) : record.getEndDate());

        grid.addRow(0, new Label("Employee ID:"), empIdField);
        grid.addRow(1, new Label("Name:"), nameField);
        grid.addRow(2, new Label("Department:"), deptField);
        grid.addRow(3, new Label("Deduction Type:"), typeBox);
        grid.addRow(4, new Label("Amount:"), amountField);
        grid.addRow(5, new Label("Start Date:"), startDate);
        grid.addRow(6, new Label("End Date:"), endDate);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (record == null) {
                    EmployeeDeductionRecordModel newRecord = new EmployeeDeductionRecordModel(
                            empIdField.getText(),
                            nameField.getText(),
                            deptField.getText(),
                            typeBox.getValue(),
                            Double.parseDouble(amountField.getText()),
                            startDate.getValue(),
                            endDate.getValue(),
                            "Pending",
                            ""
                    );
                    deductionData.add(newRecord);
                } else {
                    record.setEmployeeId(empIdField.getText());
                    record.setEmployeeName(nameField.getText());
                    record.setDepartment(deptField.getText());
                    record.setDeductionType(typeBox.getValue());
                    record.setAmount(Double.parseDouble(amountField.getText()));
                    record.setStartDate(startDate.getValue());
                    record.setEndDate(endDate.getValue());
                }
                deductionTable.refresh();
            }
        });
    }

    private void editSelected() {
        EmployeeDeductionRecordModel selected = deductionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showDeductionForm(selected);
        } else {
            new Alert(Alert.AlertType.WARNING, "Please select a record to edit.", ButtonType.OK).showAndWait();
        }
    }

    private void deleteSelected() {
        EmployeeDeductionRecordModel selected = deductionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this deduction?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) deductionData.remove(selected);
            });
        }
    }

    private void approveSelected() {
        EmployeeDeductionRecordModel selected = deductionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Approved");
            selected.setApprovedBy("Admin User");
            deductionTable.refresh();
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
        deductionData.add(new EmployeeDeductionRecordModel("EMP001", "John Doe", "IT", "Tax", 250.0,
                LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "Approved", "HR Manager"));

        deductionData.add(new EmployeeDeductionRecordModel("EMP002", "Jane Smith", "HR", "Loan", 500.0,
                LocalDate.of(2023, 3, 1), LocalDate.of(2023, 9, 1), "Pending", ""));

        deductionData.add(new EmployeeDeductionRecordModel("EMP003", "Robert Johnson", "Finance", "Pension", 300.0,
                LocalDate.of(2023, 2, 1), LocalDate.of(2023, 12, 31), "Approved", "Admin User"));
    }


}
