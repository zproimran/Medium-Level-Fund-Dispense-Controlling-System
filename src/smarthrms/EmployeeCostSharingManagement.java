package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class EmployeeCostSharingManagement extends VBox {
    
    private String currentUser;

    private TableView<EmployeeCostSharingRecordModel> table;
    private ObservableList<EmployeeCostSharingRecordModel> data;

    public EmployeeCostSharingManagement(String username) {
        this.currentUser=username;
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: #f5f5f5;");

        Label title = new Label("Cost Sharing Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
        editBtn.setOnAction(e -> {
            EmployeeCostSharingRecordModel selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showForm(selected);
            else showAlert("Please select a record to edit.");
        });

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> {
            EmployeeCostSharingRecordModel selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                data.remove(selected);
                showAlert("Record deleted successfully!");
            } else showAlert("Please select a record to delete.");
        });

        buttonBox.getChildren().addAll(editBtn, deleteBtn);

        // Table
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<EmployeeCostSharingRecordModel, String> empCodeCol = new TableColumn<>("Employee Code");
        empCodeCol.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));

        TableColumn<EmployeeCostSharingRecordModel, String> empNameCol = new TableColumn<>("Employee Name");
        empNameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<EmployeeCostSharingRecordModel, String> costTypeCol = new TableColumn<>("Cost Type");
        costTypeCol.setCellValueFactory(new PropertyValueFactory<>("costType"));

        TableColumn<EmployeeCostSharingRecordModel, Double> totalCostCol = new TableColumn<>("Total Cost");
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        TableColumn<EmployeeCostSharingRecordModel, Double> employeeShareCol = new TableColumn<>("Employee Share");
        employeeShareCol.setCellValueFactory(new PropertyValueFactory<>("employeeShare"));

        TableColumn<EmployeeCostSharingRecordModel, Double> employerShareCol = new TableColumn<>("Employer Share");
        employerShareCol.setCellValueFactory(new PropertyValueFactory<>("employerShare"));

        table.getColumns().addAll(empCodeCol, empNameCol, costTypeCol, totalCostCol, employeeShareCol, employerShareCol);
        table.setPrefHeight(400);

        // Sample data
        data = FXCollections.observableArrayList(
                new EmployeeCostSharingRecordModel("EMP001", "John Doe", "Health Insurance", 1000, 300, 700),
                new EmployeeCostSharingRecordModel("EMP002", "Jane Smith", "Pension Contribution", 500, 200, 300)
        );
        table.setItems(data);

        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        this.getChildren().addAll(title, buttonBox, scrollPane);
    }

    private void showForm(EmployeeCostSharingRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(record == null ? "Add Cost Sharing" : "Edit Cost Sharing");

        // Fields
        TextField empCodeField = new TextField();
        empCodeField.setPromptText("Employee Code");

        TextField empNameField = new TextField();
        empNameField.setPromptText("Employee Name");

        TextField costTypeField = new TextField();
        costTypeField.setPromptText("Cost Type");

        TextField totalCostField = new TextField();
        totalCostField.setPromptText("Total Cost");

        TextField employeeShareField = new TextField();
        employeeShareField.setPromptText("Employee Share");

        TextField employerShareField = new TextField();
        employerShareField.setPromptText("Employer Share");

        if (record != null) {
            empCodeField.setText(record.getEmployeeCode());
            empNameField.setText(record.getEmployeeName());
            costTypeField.setText(record.getCostType());
            totalCostField.setText(String.valueOf(record.getTotalCost()));
            employeeShareField.setText(String.valueOf(record.getEmployeeShare()));
            employerShareField.setText(String.valueOf(record.getEmployerShare()));
        }

        VBox formLayout = new VBox(10, new Label("Employee Code:"), empCodeField,
                new Label("Employee Name:"), empNameField,
                new Label("Cost Type:"), costTypeField,
                new Label("Total Cost:"), totalCostField,
                new Label("Employee Share:"), employeeShareField,
                new Label("Employer Share:"), employerShareField);
        formLayout.setPadding(new Insets(15));

        dialog.getDialogPane().setContent(formLayout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    double total = Double.parseDouble(totalCostField.getText());
                    double empShare = Double.parseDouble(employeeShareField.getText());
                    double employerShare = Double.parseDouble(employerShareField.getText());

                    if (record == null) {
                        data.add(new EmployeeCostSharingRecordModel(
                                empCodeField.getText(),
                                empNameField.getText(),
                                costTypeField.getText(),
                                total, empShare, employerShare
                        ));
                        showAlert("Cost sharing record added!");
                    } else {
                        record.setEmployeeCode(empCodeField.getText());
                        record.setEmployeeName(empNameField.getText());
                        record.setCostType(costTypeField.getText());
                        record.setTotalCost(total);
                        record.setEmployeeShare(empShare);
                        record.setEmployerShare(employerShare);
                        table.refresh();
                        showAlert("Cost sharing record updated successfully!");
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Please enter valid numbers for cost fields.");
                }
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

}
