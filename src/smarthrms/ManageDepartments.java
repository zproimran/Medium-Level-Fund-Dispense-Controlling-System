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

public class ManageDepartments extends VBox {

    private TableView<DepartmentModel> deptTable;
    private ObservableList<DepartmentModel> deptList;
    private FilteredList<DepartmentModel> filteredDepts;
    private int deptCounter = 3;

    public ManageDepartments() {
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f5f5f5;");

        HBox row = new HBox(20);
        row.setAlignment(Pos.TOP_CENTER);

        // ==================== PANEL 1: View Departments ====================
        VBox deptViewPanel = new VBox(10);
        deptViewPanel.setAlignment(Pos.TOP_LEFT);
        Label deptLabel = new Label("Departments");
        TextField deptSearchField = new TextField();
        deptSearchField.setPromptText("Search Department");

        deptTable = createDeptTable();
        deptList = FXCollections.observableArrayList(
                new DepartmentModel(1, "IT Department"),
                new DepartmentModel(2, "Finance Department")
        );
        filteredDepts = new FilteredList<>(deptList, p -> true);
        deptTable.setItems(filteredDepts);

        deptSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            final String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredDepts.setPredicate(dept -> isBlank(filter) ||
                    dept.getDeptName().toLowerCase().contains(filter));
        });

        deptViewPanel.getChildren().addAll(deptLabel, deptSearchField, new ScrollPane(deptTable));

        // ==================== PANEL 2: Add Department ====================
        VBox deptAddPanel = new VBox(10);
        deptAddPanel.setAlignment(Pos.TOP_LEFT);
        deptAddPanel.setPadding(new Insets(10));
        deptAddPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");
        Label addDeptLabel = new Label("Add Department");
        TextField newDeptField = new TextField();
        newDeptField.setPromptText("Enter Department Name");
        Button addDeptButton = new Button("Add Department");

        addDeptButton.setOnAction(e -> {
            if (!isBlank(newDeptField.getText())) {
                deptList.add(new DepartmentModel(deptCounter++, newDeptField.getText()));
                newDeptField.clear();
            }
        });

        deptAddPanel.getChildren().addAll(addDeptLabel, newDeptField, addDeptButton);

        // ==================== Add Panels to Row ====================
        row.getChildren().addAll(deptViewPanel, deptAddPanel);
        this.getChildren().add(row);
    }

    private TableView<DepartmentModel> createDeptTable() {
        TableView<DepartmentModel> table = new TableView<>();

        TableColumn<DepartmentModel, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<DepartmentModel, String> nameCol = new TableColumn<>("Department Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("deptName"));

        // Action column
        TableColumn<DepartmentModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<DepartmentModel, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox pane = new HBox(5, editBtn, delBtn);

            {
                pane.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    DepartmentModel item = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(item.getDeptName());
                    dialog.setTitle("Edit Department");
                    dialog.setHeaderText("Update department name:");
                    dialog.setContentText("Department Name:");
                    dialog.showAndWait().ifPresent(newVal -> item.setDeptName(newVal));
                    table.refresh();
                });

                delBtn.setOnAction(e -> {
                    DepartmentModel item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete this department?",
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