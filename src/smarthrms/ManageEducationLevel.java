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

public class ManageEducationLevel extends VBox {

    private TableView<EducationLevelModel> educationTable;
    private ObservableList<EducationLevelModel> educationList;
    private FilteredList<EducationLevelModel> filteredEducation;
    private int eduCounter = 3;

    public ManageEducationLevel() {
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f5f5f5;");

        HBox row = new HBox(20);
        row.setAlignment(Pos.TOP_CENTER);

        // ==================== PANEL 1: View Education Levels ====================
        VBox viewPanel = new VBox(10);
        viewPanel.setAlignment(Pos.TOP_LEFT);
        Label viewLabel = new Label("Education Levels");
        TextField searchField = new TextField();
        searchField.setPromptText("Search Education Level");

        educationTable = createEducationTable();
        educationList = FXCollections.observableArrayList(
                new EducationLevelModel(1, "Bachelor's"),
                new EducationLevelModel(2, "Master's")
        );
        filteredEducation = new FilteredList<>(educationList, p -> true);
        educationTable.setItems(filteredEducation);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredEducation.setPredicate(edu -> isBlank(filter) ||
                    edu.getEducationName().toLowerCase().contains(filter));
        });

        viewPanel.getChildren().addAll(viewLabel, searchField, new ScrollPane(educationTable));

        // ==================== PANEL 2: Add Education Level ====================
        VBox addPanel = new VBox(10);
        addPanel.setAlignment(Pos.TOP_LEFT);
        addPanel.setPadding(new Insets(10));
        addPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");
        Label addLabel = new Label("Add Education Level");
        TextField newEduField = new TextField();
        newEduField.setPromptText("Enter Education Level");
        Button addButton = new Button("Add Education");

        addButton.setOnAction(e -> {
            if (!isBlank(newEduField.getText())) {
                educationList.add(new EducationLevelModel(eduCounter++, newEduField.getText()));
                newEduField.clear();
            }
        });

        addPanel.getChildren().addAll(addLabel, newEduField, addButton);

        row.getChildren().addAll(viewPanel, addPanel);
        this.getChildren().add(row);
    }

    private TableView<EducationLevelModel> createEducationTable() {
        TableView<EducationLevelModel> table = new TableView<>();

        TableColumn<EducationLevelModel, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<EducationLevelModel, String> nameCol = new TableColumn<>("Education Level");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("educationName"));

        // Action column
        TableColumn<EducationLevelModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<EducationLevelModel, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox pane = new HBox(5, editBtn, delBtn);

            {
                pane.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    EducationLevelModel item = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(item.getEducationName());
                    dialog.setTitle("Edit Education Level");
                    dialog.setHeaderText("Update education level:");
                    dialog.setContentText("Name:");
                    dialog.showAndWait().ifPresent(newVal -> item.setEducationName(newVal));
                    table.refresh();
                });

                delBtn.setOnAction(e -> {
                    EducationLevelModel item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete this education level?",
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