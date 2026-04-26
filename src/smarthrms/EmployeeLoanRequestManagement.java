package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class EmployeeLoanRequestManagement extends BorderPane {
    private String currentUser;

    private TableView<EmployeeLoanRecordModel> loanTable;
    private ObservableList<EmployeeLoanRecordModel> loanData = FXCollections.observableArrayList();

    public EmployeeLoanRequestManagement(String username) {
        this.currentUser=username;
        // Top buttons
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
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Rejected");
        statusFilter.setValue("All");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> filterTable(searchField.getText(), statusFilter.getValue()));

        filterBox.getChildren().addAll(new Label("Search:"), searchField, new Label("Status:"), statusFilter, searchBtn);

        // Table
        loanTable = new TableView<>();
        loanTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<EmployeeLoanRecordModel, String> idCol = new TableColumn<>("Request ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        
        TableColumn<EmployeeLoanRecordModel, String> empCodeCol = new TableColumn<>("Employee Code");
        empCodeCol.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));

        TableColumn<EmployeeLoanRecordModel, String> empNameCol = new TableColumn<>("Employee Name");
        empNameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<EmployeeLoanRecordModel, String> typeCol = new TableColumn<>("Loan Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("loanType"));

        TableColumn<EmployeeLoanRecordModel, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<EmployeeLoanRecordModel, LocalDate> dateCol = new TableColumn<>("Request Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        TableColumn<EmployeeLoanRecordModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<EmployeeLoanRecordModel, String> approvedByCol = new TableColumn<>("Approved By");
        approvedByCol.setCellValueFactory(new PropertyValueFactory<>("approvedBy"));

        loanTable.getColumns().addAll(idCol, empCodeCol,empNameCol, typeCol, amountCol, dateCol, statusCol, approvedByCol);
        loanTable.setItems(loanData);
        loanTable.setPlaceholder(new Label("No loan requests found"));

        tableSection.getChildren().addAll(filterBox, loanTable);
        return tableSection;
    }

    private void showLoanForm(EmployeeLoanRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(record == null ? "Add Loan Request" : "Edit Loan Request");
        dialog.setHeaderText(record == null ? "Add New Loan Request" : "Edit Loan Request");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField empCodeField = new TextField(record == null ? "" : record.getEmployeeCode());
        TextField empNameField = new TextField(record == null ? "" : record.getEmployeeName());
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Personal", "Education", "Housing", "Medical");
        typeBox.setValue(record == null ? "Personal" : record.getLoanType());

        TextField amountField = new TextField(record == null ? "" : String.valueOf(record.getAmount()));
        DatePicker requestDate = new DatePicker(record == null ? LocalDate.now() : record.getRequestDate());

        grid.addRow(0, new Label("Employee Code:"), empCodeField);
        grid.addRow(1, new Label("Employee Name:"), empNameField);
        grid.addRow(2, new Label("Loan Type:"), typeBox);
        grid.addRow(3, new Label("Amount:"), amountField);
        grid.addRow(4, new Label("Request Date:"), requestDate);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (record == null) {
                    EmployeeLoanRecordModel newRecord = new EmployeeLoanRecordModel(
                            "REQ" + (loanData.size() + 1),
                            empCodeField.getText(),
                            empNameField.getText(),
                            typeBox.getValue(),
                            Double.parseDouble(amountField.getText()),
                            requestDate.getValue(),
                            "Pending",
                            ""
                    );
                    loanData.add(newRecord);
                } else {
                    record.setEmployeeCode(empCodeField.getText());
                    record.setEmployeeName(empNameField.getText());
                    record.setLoanType(typeBox.getValue());
                    record.setAmount(Double.parseDouble(amountField.getText()));
                    record.setRequestDate(requestDate.getValue());
                }
                loanTable.refresh();
            }
        });
    }

    private void editSelected() {
        EmployeeLoanRecordModel selected = loanTable.getSelectionModel().getSelectedItem();
        if (selected != null) showLoanForm(selected);
        else new Alert(Alert.AlertType.WARNING, "Please select a record to edit.", ButtonType.OK).showAndWait();
    }

    private void deleteSelected() {
        EmployeeLoanRecordModel selected = loanTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this loan request?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) loanData.remove(selected);
            });
        }
    }

    private void approveSelected() {
        EmployeeLoanRecordModel selected = loanTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Approved");
            selected.setApprovedBy("Admin User");
            loanTable.refresh();
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
        loanData.add(new EmployeeLoanRecordModel("REQ001","EMP001", "John Doe", "Personal", 5000.0,
                LocalDate.of(2025, 9, 16), "Pending", ""));
        loanData.add(new EmployeeLoanRecordModel("REQ002","EMP002", "Jane Smith", "Education", 10000.0,
                LocalDate.of(2025, 9, 14), "Approved", "HR Manager"));
    }
}
