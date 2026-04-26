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

public class ManageJobPositions extends VBox {

    private TableView<EmployeeJobPositionModel> positionTable;
    private ObservableList<EmployeeJobPositionModel> positionList;
    private FilteredList<EmployeeJobPositionModel> filteredPositions;
    private int positionCounter = 3;

    public ManageJobPositions() {
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f5f5f5;");

        HBox row = new HBox(20);
        row.setAlignment(Pos.TOP_CENTER);

        // ==================== PANEL 1: View Job Positions ====================
        VBox positionViewPanel = new VBox(10);
        positionViewPanel.setAlignment(Pos.TOP_LEFT);
        Label positionLabel = new Label("Job Positions");
        TextField positionSearchField = new TextField();
        positionSearchField.setPromptText("Search Job Position");

        positionTable = createPositionTable();
        positionList = FXCollections.observableArrayList(
                new EmployeeJobPositionModel(1, "Software Engineer"),
                new EmployeeJobPositionModel(2, "Accountant")
        );
        filteredPositions = new FilteredList<>(positionList, p -> true);
        positionTable.setItems(filteredPositions);

        positionSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            final String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredPositions.setPredicate(pos -> isBlank(filter) ||
                    pos.getPositionName().toLowerCase().contains(filter));
        });

        positionViewPanel.getChildren().addAll(positionLabel, positionSearchField, new ScrollPane(positionTable));

        // ==================== PANEL 2: Add Job Position ====================
        VBox positionAddPanel = new VBox(10);
        positionAddPanel.setAlignment(Pos.TOP_LEFT);
        positionAddPanel.setPadding(new Insets(10));
        positionAddPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");
        Label addPositionLabel = new Label("Add Job Position");
        TextField newPositionField = new TextField();
        newPositionField.setPromptText("Enter Job Position Name");
        Button addPositionButton = new Button("Add Position");

        addPositionButton.setOnAction(e -> {
            if (!isBlank(newPositionField.getText())) {
                positionList.add(new EmployeeJobPositionModel(positionCounter++, newPositionField.getText()));
                newPositionField.clear();
            }
        });

        positionAddPanel.getChildren().addAll(addPositionLabel, newPositionField, addPositionButton);

        // ==================== Add Panels to Row ====================
        row.getChildren().addAll(positionViewPanel, positionAddPanel);
        this.getChildren().add(row);
    }

    private TableView<EmployeeJobPositionModel> createPositionTable() {
        TableView<EmployeeJobPositionModel> table = new TableView<>();

        TableColumn<EmployeeJobPositionModel, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<EmployeeJobPositionModel, String> nameCol = new TableColumn<>("Position Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("positionName"));

        // Action column
        TableColumn<EmployeeJobPositionModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<EmployeeJobPositionModel, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox pane = new HBox(5, editBtn, delBtn);

            {
                pane.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    EmployeeJobPositionModel item = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(item.getPositionName());
                    dialog.setTitle("Edit Job Position");
                    dialog.setHeaderText("Update job position name:");
                    dialog.setContentText("Position Name:");
                    dialog.showAndWait().ifPresent(newVal -> item.setPositionName(newVal));
                    table.refresh();
                });

                delBtn.setOnAction(e -> {
                    EmployeeJobPositionModel item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete this job position?",
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
        table.setPrefHeight(350);
        table.setPrefWidth(400);
        return table;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
