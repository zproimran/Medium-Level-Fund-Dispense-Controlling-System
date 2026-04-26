package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDate;

public class EmployeeCashIndemnityManagement extends VBox {
    private String currentUser;

    private TableView<EmployeeCashIndemnityRecordModel> table;
    private ObservableList<EmployeeCashIndemnityRecordModel> indemnityData;

    public EmployeeCashIndemnityManagement(String username) {
        this.currentUser=username;
        initializeUI();
        loadSampleData();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #f5f5f5;");

        Text title = new Text("Cash Indemnity Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Buttons
        HBox buttonPanel = new HBox(10);
        buttonPanel.setAlignment(Pos.CENTER_LEFT);

        Button editBtn = createButton("Edit", "#17a2b8", e -> editSelected());
        Button deleteBtn = createButton("Delete", "#dc3545", e -> deleteSelected());
        Button approveBtn = createButton("Approve", "#007bff", e -> approveSelected());
        Button rejectBtn = createButton("Reject", "#6c757d", e -> rejectSelected());
        Button exportBtn = createButton("Export", "#fd7e14", e -> exportData());
        Button refreshBtn = createButton("Refresh", "#6f42c1", e -> refreshData());

        buttonPanel.getChildren().addAll(editBtn, deleteBtn, approveBtn, rejectBtn, exportBtn, refreshBtn);

        // Table
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<EmployeeCashIndemnityRecordModel, String> empCodeCol = new TableColumn<>("Employee Code");
        empCodeCol.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));

        TableColumn<EmployeeCashIndemnityRecordModel, String> empNameCol = new TableColumn<>("Employee Name");
        empNameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<EmployeeCashIndemnityRecordModel, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<EmployeeCashIndemnityRecordModel, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<EmployeeCashIndemnityRecordModel, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<EmployeeCashIndemnityRecordModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<EmployeeCashIndemnityRecordModel, String> approvedByCol = new TableColumn<>("Approved By");
        approvedByCol.setCellValueFactory(new PropertyValueFactory<>("approvedBy"));

        table.getColumns().addAll(empCodeCol, empNameCol, reasonCol, amountCol, dateCol, statusCol, approvedByCol);

        indemnityData = FXCollections.observableArrayList();
        table.setItems(indemnityData);

        ScrollPane tableScroll = new ScrollPane(table);
        tableScroll.setFitToWidth(true);
        tableScroll.setFitToHeight(true);
        tableScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        this.getChildren().addAll(title, buttonPanel, tableScroll);
    }

    private Button createButton(String text, String color, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        btn.setOnAction(action);
        return btn;
    }

    private void showIndemnityForm(EmployeeCashIndemnityRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(record == null ? "Add Cash Indemnity" : "Edit Cash Indemnity");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        TextField empCodeField = new TextField();
        empCodeField.setPromptText("Employee Code");

        TextField empNameField = new TextField();
        empNameField.setPromptText("Employee Name");

        TextField reasonField = new TextField();
        reasonField.setPromptText("Reason");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Pending", "Approved", "Rejected");
        statusCombo.setValue("Pending");

        grid.add(new Label("Employee Code:"), 0, 0);
        grid.add(empCodeField, 1, 0);
        grid.add(new Label("Employee Name:"), 0, 1);
        grid.add(empNameField, 1, 1);
        grid.add(new Label("Reason:"), 0, 2);
        grid.add(reasonField, 1, 2);
        grid.add(new Label("Amount:"), 0, 3);
        grid.add(amountField, 1, 3);
        grid.add(new Label("Date:"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusCombo, 1, 5);

        if (record != null) {
            empCodeField.setText(record.getEmployeeCode());
            empNameField.setText(record.getEmployeeName());
            reasonField.setText(record.getReason());
            amountField.setText(String.valueOf(record.getAmount()));
            datePicker.setValue(record.getDate());
            statusCombo.setValue(record.getStatus());
        }

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                double amount = 0;
                try {
                    amount = Double.parseDouble(amountField.getText());
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(AlertType.ERROR, "Invalid amount entered", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }

                if (record == null) {
                    indemnityData.add(new EmployeeCashIndemnityRecordModel(
                            empCodeField.getText(),
                            empNameField.getText(),
                            reasonField.getText(),
                            amount,
                            datePicker.getValue(),
                            statusCombo.getValue(),
                            ""
                    ));
                } else {
                    record.setEmployeeCode(empCodeField.getText());
                    record.setEmployeeName(empNameField.getText());
                    record.setReason(reasonField.getText());
                    record.setAmount(amount);
                    record.setDate(datePicker.getValue());
                    record.setStatus(statusCombo.getValue());
                    table.refresh();
                }
            }
        });
    }

    private void editSelected() {
        EmployeeCashIndemnityRecordModel selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showIndemnityForm(selected);
        } else {
            Alert alert = new Alert(AlertType.WARNING, "Please select a record to edit.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void deleteSelected() {
        EmployeeCashIndemnityRecordModel selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            indemnityData.remove(selected);
        }
    }

    private void approveSelected() {
        EmployeeCashIndemnityRecordModel selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Approved");
            selected.setApprovedBy("Admin");
            table.refresh();
        }
    }

    private void rejectSelected() {
        EmployeeCashIndemnityRecordModel selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Rejected");
            table.refresh();
        }
    }

    private void exportData() {
        Alert alert = new Alert(AlertType.INFORMATION, "Export functionality coming soon!", ButtonType.OK);
        alert.showAndWait();
    }

    private void refreshData() {
        table.refresh();
    }

    private void loadSampleData() {
        indemnityData.add(new EmployeeCashIndemnityRecordModel("EMP001", "John Doe", "Relocation", 500.0, LocalDate.now(), "Pending", ""));
        indemnityData.add(new EmployeeCashIndemnityRecordModel("EMP002", "Jane Smith", "Leave Encashment", 1200.0, LocalDate.now(), "Approved", "HR Manager"));
    }

}
