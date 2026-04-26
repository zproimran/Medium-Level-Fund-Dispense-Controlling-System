package smarthrms;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class EditEmployeeCodePanel extends VBox {

    private TableView<EmployeeCodeRow> table;

    public EditEmployeeCodePanel() {
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setSpacing(15);

        Label title = new Label("Edit Employee Code");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        table = new TableView<>();
        table.setPrefHeight(80);
        table.setEditable(true);

        // Employee Code column (editable)
        TableColumn<EmployeeCodeRow, String> empCodeCol = new TableColumn<>("Employee Code");
        empCodeCol.setCellValueFactory(data -> data.getValue().employeeCodeProperty());
        empCodeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        empCodeCol.setPrefWidth(200);

        // Edit Button column
        TableColumn<EmployeeCodeRow, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(new Callback<TableColumn<EmployeeCodeRow, Void>, TableCell<EmployeeCodeRow, Void>>() {
            @Override
            public TableCell<EmployeeCodeRow, Void> call(final TableColumn<EmployeeCodeRow, Void> param) {
                final TableCell<EmployeeCodeRow, Void> cell = new TableCell<EmployeeCodeRow, Void>() {

                    private final Button btn = new Button("Save Update");

                    {
                        btn.setOnAction(e -> {
                            EmployeeCodeRow row = getTableView().getItems().get(getIndex());
                            String newCode = row.getEmployeeCode();
                            // TODO: Save the new code to database
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Employee code updated: " + newCode, ButtonType.OK);
                            alert.showAndWait();
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });
        actionCol.setPrefWidth(100);

        table.getColumns().addAll(empCodeCol, actionCol);

        // Add a single empty row
        table.getItems().add(new EmployeeCodeRow("agh/reg/*"));

        this.getChildren().addAll(title, table);
    }

    // Row model
    public static class EmployeeCodeRow {
        private final SimpleStringProperty employeeCode;

        public EmployeeCodeRow(String code) {
            this.employeeCode = new SimpleStringProperty(code);
        }

        public String getEmployeeCode() {
            return employeeCode.get();
        }

        public void setEmployeeCode(String code) {
            this.employeeCode.set(code);
        }

        public SimpleStringProperty employeeCodeProperty() {
            return employeeCode;
        }
    }
}
