package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class EmployeeOvertimeManagement extends BorderPane {
    
    private String currentUser;
    private TableView<EmployeeOvertimeRecordModel> overtimeTable;
    private ObservableList<EmployeeOvertimeRecordModel> overtimeData = FXCollections.observableArrayList();

    public EmployeeOvertimeManagement(String username) {
        this.currentUser=username;
        // Create top section with buttons
        HBox buttonPanel = createButtonPanel();

        // Create center section with table
        VBox tableSection = createTableSection();

        // Set up the layout
        this.setTop(buttonPanel);
        this.setCenter(tableSection);

        // Load sample data
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

        Button rejectBtn = new Button("Reject Selected");
        rejectBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        rejectBtn.setOnAction(e -> rejectSelected());

        Button exportBtn = new Button("Export to Excel");
        exportBtn.setStyle("-fx-background-color: #fd7e14; -fx-text-fill: white;");
        exportBtn.setOnAction(e -> exportToExcel());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #6f42c1; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> refreshData());

        buttonPanel.getChildren().addAll(editBtn, deleteBtn, approveBtn, rejectBtn, exportBtn, refreshBtn);
        return buttonPanel;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox();
        tableSection.setPadding(new Insets(15));

        // Table
        overtimeTable = new TableView<>();
        overtimeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Columns
        TableColumn<EmployeeOvertimeRecordModel, String> empCodeCol = new TableColumn<>("Employee Code");
        empCodeCol.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));

        TableColumn<EmployeeOvertimeRecordModel, String> empNameCol = new TableColumn<>("Employee Name");
        empNameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<EmployeeOvertimeRecordModel, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<EmployeeOvertimeRecordModel, LocalTime> startCol = new TableColumn<>("Start Time");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));

        TableColumn<EmployeeOvertimeRecordModel, LocalTime> endCol = new TableColumn<>("End Time");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));

        TableColumn<EmployeeOvertimeRecordModel, Double> hoursCol = new TableColumn<>("Hours");
        hoursCol.setCellValueFactory(new PropertyValueFactory<>("hours"));

        TableColumn<EmployeeOvertimeRecordModel, String> typeCol = new TableColumn<>("OT Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("overtimeType"));
        
        
        TableColumn<EmployeeOvertimeRecordModel, String> payrollPeriodCol = new TableColumn<>("Payroll Period");
        payrollPeriodCol.setCellValueFactory(new PropertyValueFactory<>("payrollPeriod"));

        TableColumn<EmployeeOvertimeRecordModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<EmployeeOvertimeRecordModel, String> approvedByCol = new TableColumn<>("Approved By");
        approvedByCol.setCellValueFactory(new PropertyValueFactory<>("approvedBy"));

        // Add columns
        overtimeTable.getColumns().addAll(empCodeCol, empNameCol, dateCol, startCol, endCol, hoursCol,payrollPeriodCol, typeCol, statusCol, approvedByCol);

        // Bind data
        overtimeTable.setItems(overtimeData);
        overtimeTable.setPlaceholder(new Label("No overtime records found"));

        tableSection.getChildren().addAll(overtimeTable);
        return tableSection;
    }

    private void showOvertimeForm(EmployeeOvertimeRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Overtime Form");
        dialog.setHeaderText(record == null ? "Add New Overtime" : "Edit Overtime");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        TextField empCodeField = new TextField();
        empCodeField.setPromptText("Employee Code");
        TextField empNameField = new TextField();
        empNameField.setPromptText("Employee Name");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        TextField startTimeField = new TextField("18:00");
        TextField endTimeField = new TextField("20:00");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Weekday", "Weekend", "Holiday");
        typeCombo.setValue("Weekday");
        
        ComboBox<String> payrollPeriodCombo = new ComboBox<>();
        payrollPeriodCombo.getItems().addAll("payroll period1", "payroll period2", "payroll period3");
        payrollPeriodCombo.setValue("payroll period1");

        form.addRow(0, new Label("Employee Code:"), empCodeField);
        form.addRow(1, new Label("Employee Name:"), empNameField);
        form.addRow(2, new Label("Date:"), datePicker);
        form.addRow(3, new Label("Start Time:"), startTimeField);
        form.addRow(4, new Label("End Time:"), endTimeField);
        form.addRow(5, new Label("OT Type:"), typeCombo);
        form.addRow(6, new Label("Payroll Period:"), payrollPeriodCombo);

        dialog.getDialogPane().setContent(form);

        if (record != null) {
            empCodeField.setText(record.getEmployeeCode());
            empNameField.setText(record.getEmployeeName());
            datePicker.setValue(record.getDate());
            startTimeField.setText(record.getStartTime().toString());
            endTimeField.setText(record.getEndTime().toString());
            typeCombo.setValue(record.getOvertimeType());
        }

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                LocalTime start = LocalTime.parse(startTimeField.getText());
                LocalTime end = LocalTime.parse(endTimeField.getText());
                double hours = java.time.Duration.between(start, end).toHours();

                if (record == null) {
                    overtimeData.add(new EmployeeOvertimeRecordModel(
                        empCodeField.getText(),
                        empNameField.getText(),
                        datePicker.getValue(),
                        start,
                        end,
                        hours,
                        typeCombo.getValue(),
                        payrollPeriodCombo.getValue(),
                        "Pending",
                        ""
                    ));
                } else {
                    record.setEmployeeCode(empCodeField.getText());
                    record.setEmployeeName(empNameField.getText());
                    record.setDate(datePicker.getValue());
                    record.setStartTime(start);
                    record.setEndTime(end);
                    record.setHours(hours);
                    record.setPayrollPeriod(payrollPeriodCombo.getValue());
                    record.setOvertimeType(typeCombo.getValue());
                }
                overtimeTable.refresh();
            }
        });
    }

    private void editSelected() {
        EmployeeOvertimeRecordModel selected = overtimeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showOvertimeForm(selected);
        } else {
            showAlert("Please select a record to edit.", Alert.AlertType.WARNING);
        }
    }

    private void deleteSelected() {
        EmployeeOvertimeRecordModel selected = overtimeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            overtimeData.remove(selected);
        } else {
            showAlert("Please select a record to delete.", Alert.AlertType.WARNING);
        }
    }

    private void approveSelected() {
        EmployeeOvertimeRecordModel selected = overtimeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Approved");
            selected.setApprovedBy("Manager");
            overtimeTable.refresh();
        } else {
            showAlert("Please select a record to approve.", Alert.AlertType.WARNING);
        }
    }

    private void rejectSelected() {
        EmployeeOvertimeRecordModel selected = overtimeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Rejected");
            overtimeTable.refresh();
        } else {
            showAlert("Please select a record to reject.", Alert.AlertType.WARNING);
        }
    }

    private void exportToExcel() {
        showAlert("Export to Excel not implemented yet.", Alert.AlertType.INFORMATION);
    }

    private void refreshData() {
        showAlert("Data refreshed successfully!", Alert.AlertType.INFORMATION);
    }

    private void loadSampleData() {
        overtimeData.add(new EmployeeOvertimeRecordModel("EMP001", "John Doe", LocalDate.of(2023, 9, 1),
                LocalTime.of(18, 0), LocalTime.of(21, 0), 3, "Weekday","payroll period1", "Approved", "HR Manager"));

        overtimeData.add(new EmployeeOvertimeRecordModel("EMP002", "Jane Smith", LocalDate.of(2023, 9, 2),
                LocalTime.of(19, 0), LocalTime.of(22, 0), 3, "Weekend","payroll period2", "Pending", ""));
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
