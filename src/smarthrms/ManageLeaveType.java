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

public class ManageLeaveType extends VBox {

    private TableView<EmployeeLeaveTypeModel> leaveTable;
    private ObservableList<EmployeeLeaveTypeModel> employeesLeaveTypes;
    private FilteredList<EmployeeLeaveTypeModel> filteredLeaveTypes;

    private int leaveCounter = 3;

    public ManageLeaveType() {
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f5f5f5;");

        HBox row = new HBox(20);
        row.setAlignment(Pos.TOP_CENTER);

        // ==================== PANEL 1: View Leave Types ====================
        VBox leaveViewPanel = new VBox(10);
        leaveViewPanel.setAlignment(Pos.TOP_LEFT);
        Label leaveLabel = new Label("Leave Types");
        TextField leaveSearchField = new TextField();
        leaveSearchField.setPromptText("Search Leave Type");

        leaveTable = createLeaveTable();
        employeesLeaveTypes = FXCollections.observableArrayList(
                new EmployeeLeaveTypeModel(1, "Annual Leave"),
                new EmployeeLeaveTypeModel(2, "Sick Leave")
        );
        filteredLeaveTypes = new FilteredList<>(employeesLeaveTypes, p -> true);
        leaveTable.setItems(filteredLeaveTypes);

        leaveSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            final String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredLeaveTypes.setPredicate(emp -> isBlank(filter) ||
                    emp.getLeavename().toLowerCase().contains(filter));
        });

        leaveViewPanel.getChildren().addAll(leaveLabel, leaveSearchField, new ScrollPane(leaveTable));

        // ==================== PANEL 2: Add Leave Type ====================
        VBox leaveAddPanel = new VBox(10);
        leaveAddPanel.setAlignment(Pos.TOP_LEFT);
        leaveAddPanel.setPadding(new Insets(10));
        leaveAddPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");
        Label addLeaveLabel = new Label("Add Leave Type");
        TextField newLeaveField = new TextField();
        newLeaveField.setPromptText("Enter Leave Type");
        Button addLeaveButton = new Button("Add Leave");

        addLeaveButton.setOnAction(e -> {
            if (!isBlank(newLeaveField.getText())) {
                employeesLeaveTypes.add(new EmployeeLeaveTypeModel(leaveCounter++, newLeaveField.getText()));
                newLeaveField.clear();
            }
        });

        leaveAddPanel.getChildren().addAll(addLeaveLabel, newLeaveField, addLeaveButton);

        // ==================== Add Panels into Row ====================
        row.getChildren().addAll(leaveViewPanel, leaveAddPanel);

        this.getChildren().add(row);
    }

    // ==================== CREATE LEAVE TABLE ====================
    private TableView<EmployeeLeaveTypeModel> createLeaveTable() {
        TableView<EmployeeLeaveTypeModel> table = new TableView<>();

        TableColumn<EmployeeLeaveTypeModel, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<EmployeeLeaveTypeModel, Integer>("id"));

        TableColumn<EmployeeLeaveTypeModel, String> nameCol = new TableColumn<>("Leave Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<EmployeeLeaveTypeModel, String>("leavename"));

        // Action column
        TableColumn<EmployeeLeaveTypeModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<EmployeeLeaveTypeModel, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox pane = new HBox(5, editBtn, delBtn);

            {
                pane.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    EmployeeLeaveTypeModel item = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(item.getLeavename());
                    dialog.setTitle("Edit Leave Type");
                    dialog.setHeaderText("Update leave type:");
                    dialog.setContentText("Name:");
                    dialog.showAndWait().ifPresent(newVal -> item.setLeavename(newVal));
                    table.refresh();
                });

                delBtn.setOnAction(e -> {
                    EmployeeLeaveTypeModel item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete this leave type?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.YES) {
                            table.getItems().remove(item);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        table.getColumns().addAll(idCol, nameCol, actionCol);
        table.setPrefHeight(300);
        table.setPrefWidth(400);
        return table;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}