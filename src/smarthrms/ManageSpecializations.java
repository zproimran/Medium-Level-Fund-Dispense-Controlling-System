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

public class ManageSpecializations extends VBox {

    private TableView<EmployeeSpecializationModel> specTable;
    private ObservableList<EmployeeSpecializationModel> specList;
    private FilteredList<EmployeeSpecializationModel> filteredSpecs;
    private int specCounter = 3;

    public ManageSpecializations() {
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f5f5f5;");

        HBox row = new HBox(20);
        row.setAlignment(Pos.TOP_CENTER);

        // ==================== PANEL 1: View Specializations ====================
        VBox specViewPanel = new VBox(10);
        specViewPanel.setAlignment(Pos.TOP_LEFT);
        Label specLabel = new Label("Specializations");
        TextField specSearchField = new TextField();
        specSearchField.setPromptText("Search Specialization");

        specTable = createSpecTable();
        specList = FXCollections.observableArrayList(
                new EmployeeSpecializationModel(1, "Java Development"),
                new EmployeeSpecializationModel(2, "Accounting")
        );
        filteredSpecs = new FilteredList<>(specList, p -> true);
        specTable.setItems(filteredSpecs);

        specSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            final String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredSpecs.setPredicate(spec -> isBlank(filter) ||
                    spec.getSpecName().toLowerCase().contains(filter));
        });

        specViewPanel.getChildren().addAll(specLabel, specSearchField, new ScrollPane(specTable));

        // ==================== PANEL 2: Add Specialization ====================
        VBox specAddPanel = new VBox(10);
        specAddPanel.setAlignment(Pos.TOP_LEFT);
        specAddPanel.setPadding(new Insets(10));
        specAddPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");
        Label addSpecLabel = new Label("Add Specialization");
        TextField newSpecField = new TextField();
        newSpecField.setPromptText("Enter Specialization Name");
        Button addSpecButton = new Button("Add Specialization");

        addSpecButton.setOnAction(e -> {
            if (!isBlank(newSpecField.getText())) {
                specList.add(new EmployeeSpecializationModel(specCounter++, newSpecField.getText()));
                newSpecField.clear();
            }
        });

        specAddPanel.getChildren().addAll(addSpecLabel, newSpecField, addSpecButton);

        // ==================== Add Panels to Row ====================
        row.getChildren().addAll(specViewPanel, specAddPanel);
        this.getChildren().add(row);
    }

    private TableView<EmployeeSpecializationModel> createSpecTable() {
        TableView<EmployeeSpecializationModel> table = new TableView<>();

        TableColumn<EmployeeSpecializationModel, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<EmployeeSpecializationModel, String> nameCol = new TableColumn<>("Specialization Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("specName"));

        // Action column
        TableColumn<EmployeeSpecializationModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<EmployeeSpecializationModel, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox pane = new HBox(5, editBtn, delBtn);

            {
                pane.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    EmployeeSpecializationModel item = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(item.getSpecName());
                    dialog.setTitle("Edit Specialization");
                    dialog.setHeaderText("Update specialization name:");
                    dialog.setContentText("Specialization Name:");
                    dialog.showAndWait().ifPresent(newVal -> item.setSpecName(newVal));
                    table.refresh();
                });

                delBtn.setOnAction(e -> {
                    EmployeeSpecializationModel item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete this specialization?",
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