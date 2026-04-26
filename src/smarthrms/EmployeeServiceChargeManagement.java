package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class EmployeeServiceChargeManagement extends BorderPane {
    private String currentUser;

    private TableView<EmployeeServiceChargeRecordModel> serviceChargeTable;
    private ObservableList<EmployeeServiceChargeRecordModel> serviceChargeData = FXCollections.observableArrayList();

    public EmployeeServiceChargeManagement(String username) {
        this.currentUser=username;
        // Top button panel
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

        Button addBtn = new Button("Add Service Charge");
        addBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        addBtn.setOnAction(e -> showServiceChargeForm(null));

        Button editBtn = new Button("Edit Selected");
        editBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
        editBtn.setOnAction(e -> editSelected());

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteSelected());

        Button approveBtn = new Button("Approve Selected");
        approveBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        approveBtn.setOnAction(e -> approveSelected());

        Button rejectBtn = new Button("Reject Selected");
        rejectBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        rejectBtn.setOnAction(e -> rejectSelected());

        Button exportBtn = new Button("Export to Excel");
        exportBtn.setStyle("-fx-background-color: #fd7e14; -fx-text-fill: white;");
        exportBtn.setOnAction(e -> exportToExcel());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #6f42c1; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> refreshData());

        buttonPanel.getChildren().addAll(addBtn, editBtn, deleteBtn, approveBtn, rejectBtn, exportBtn, refreshBtn);

        return buttonPanel;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox();
        tableSection.setPadding(new Insets(15));

        // Search/filter
        HBox filterBox = new HBox(10);
        filterBox.setPadding(new Insets(0, 0, 10, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("Search employees...");
        searchField.setPrefWidth(300);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Rejected");
        statusFilter.setValue("All");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> filterTable(searchField.getText(), statusFilter.getValue()));

        filterBox.getChildren().addAll(new Label("Search:"), searchField, new Label("Status:"), statusFilter, searchBtn);

        // Table
        serviceChargeTable = new TableView<>();
        serviceChargeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<EmployeeServiceChargeRecordModel, String> empCodeCol = new TableColumn<>("Employee Code");
        empCodeCol.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));

        TableColumn<EmployeeServiceChargeRecordModel, String> empNameCol = new TableColumn<>("Employee Name");
        empNameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<EmployeeServiceChargeRecordModel, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<EmployeeServiceChargeRecordModel, String> chargeTypeCol = new TableColumn<>("Charge Type");
        chargeTypeCol.setCellValueFactory(new PropertyValueFactory<>("chargeType"));

        TableColumn<EmployeeServiceChargeRecordModel, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<EmployeeServiceChargeRecordModel, LocalDate> periodCol = new TableColumn<>("Applicable Date");
        periodCol.setCellValueFactory(new PropertyValueFactory<>("applicableDate"));

        TableColumn<EmployeeServiceChargeRecordModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<EmployeeServiceChargeRecordModel, String> approvedByCol = new TableColumn<>("Approved By");
        approvedByCol.setCellValueFactory(new PropertyValueFactory<>("approvedBy"));

        serviceChargeTable.getColumns().addAll(empCodeCol, empNameCol, deptCol, chargeTypeCol, amountCol, periodCol, statusCol, approvedByCol);
        serviceChargeTable.setItems(serviceChargeData);
        serviceChargeTable.setPlaceholder(new Label("No service charge records found"));

        tableSection.getChildren().addAll(filterBox, serviceChargeTable);
        return tableSection;
    }

    private void showServiceChargeForm(EmployeeServiceChargeRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(record == null ? "Add Service Charge" : "Edit Service Charge");
        dialog.setHeaderText(record == null ? "Add New Service Charge" : "Edit Service Charge");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField empCodeField = new TextField(record == null ? "" : record.getEmployeeCode());
        TextField nameField = new TextField(record == null ? "" : record.getEmployeeName());
        TextField deptField = new TextField(record == null ? "" : record.getDepartment());

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Customer Service", "Extra Hours", "Tips", "Other");
        typeBox.setValue(record == null ? "Customer Service" : record.getChargeType());

        TextField amountField = new TextField(record == null ? "" : String.valueOf(record.getAmount()));
        DatePicker applicableDate = new DatePicker(record == null ? LocalDate.now() : record.getApplicableDate());

        grid.addRow(0, new Label("Employee Code:"), empCodeField);
        grid.addRow(1, new Label("Employee Name:"), nameField);
        grid.addRow(2, new Label("Department:"), deptField);
        grid.addRow(3, new Label("Charge Type:"), typeBox);
        grid.addRow(4, new Label("Amount:"), amountField);
        grid.addRow(5, new Label("Applicable Date:"), applicableDate);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (record == null) {
                    EmployeeServiceChargeRecordModel newRecord = new EmployeeServiceChargeRecordModel(
                            empCodeField.getText(),
                            nameField.getText(),
                            deptField.getText(),
                            typeBox.getValue(),
                            Double.parseDouble(amountField.getText()),
                            applicableDate.getValue(),
                            "Pending",
                            ""
                    );
                    serviceChargeData.add(newRecord);
                } else {
                    record.setEmployeeCode(empCodeField.getText());
                    record.setEmployeeName(nameField.getText());
                    record.setDepartment(deptField.getText());
                    record.setChargeType(typeBox.getValue());
                    record.setAmount(Double.parseDouble(amountField.getText()));
                    record.setApplicableDate(applicableDate.getValue());
                }
                serviceChargeTable.refresh();
            }
        });
    }

    private void editSelected() {
        EmployeeServiceChargeRecordModel selected = serviceChargeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showServiceChargeForm(selected);
        } else {
            new Alert(Alert.AlertType.WARNING, "Please select a record to edit.", ButtonType.OK).showAndWait();
        }
    }

    private void deleteSelected() {
        EmployeeServiceChargeRecordModel selected = serviceChargeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this record?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) serviceChargeData.remove(selected);
            });
        } else {
            new Alert(Alert.AlertType.WARNING, "Please select a record to delete.", ButtonType.OK).showAndWait();
        }
    }

    private void approveSelected() {
        EmployeeServiceChargeRecordModel selected = serviceChargeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Approved");
            selected.setApprovedBy("Admin User");
            serviceChargeTable.refresh();
        }
    }

    private void rejectSelected() {
        EmployeeServiceChargeRecordModel selected = serviceChargeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Rejected");
            selected.setApprovedBy("Admin User");
            serviceChargeTable.refresh();
        }
    }

    private void exportToExcel() {
        new Alert(Alert.AlertType.INFORMATION, "Exported to Excel (mockup).", ButtonType.OK).showAndWait();
    }

    private void refreshData() {
        new Alert(Alert.AlertType.INFORMATION, "Data refreshed (mockup).", ButtonType.OK).showAndWait();
    }

    private void filterTable(String searchText, String status) {
        new Alert(Alert.AlertType.INFORMATION, "Filter applied: " + searchText + " | Status: " + status, ButtonType.OK).showAndWait();
    }

    private void loadSampleData() {
        serviceChargeData.add(new EmployeeServiceChargeRecordModel("EMP001", "John Doe", "IT", "Customer Service", 50.0, LocalDate.now(), "Pending", ""));
        serviceChargeData.add(new EmployeeServiceChargeRecordModel("EMP002", "Jane Smith", "HR", "Extra Hours", 30.0, LocalDate.now(), "Approved", "Admin User"));
        serviceChargeData.add(new EmployeeServiceChargeRecordModel("EMP003", "Robert Johnson", "Finance", "Tips", 100.0, LocalDate.now(), "Rejected", "Manager"));
    }
}
