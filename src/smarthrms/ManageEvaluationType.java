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

public class ManageEvaluationType extends VBox {

    private TableView<EmployeeEvaluationTypeModel> typeTable;
    private TableView<EmployeeEvaluationPeriodModel> periodTable;

    private ObservableList<EmployeeEvaluationTypeModel> employeesEvaluationType;
    private ObservableList<EmployeeEvaluationPeriodModel> employeesEvaluationPeriod;

    private FilteredList<EmployeeEvaluationTypeModel> filteredTypes;
    private FilteredList<EmployeeEvaluationPeriodModel> filteredPeriods;

    private int typeCounter = 3;
    private int periodCounter = 3;

    public ManageEvaluationType() {
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f5f5f5;");

        HBox row = new HBox(20);
        row.setAlignment(Pos.TOP_CENTER);

        // ==================== PANEL 1: View Evaluation Type ====================
        VBox typeViewPanel = new VBox(10);
        typeViewPanel.setAlignment(Pos.TOP_LEFT);
        Label typeLabel = new Label("Evaluation Types");
        TextField typeSearchField = new TextField();
        typeSearchField.setPromptText("Search Evaluation Type");

        typeTable = createTypeTable();
        employeesEvaluationType = FXCollections.observableArrayList(
                new EmployeeEvaluationTypeModel(1, "Performance Review"),
                new EmployeeEvaluationTypeModel(2, "Technical Skills")
        );
        filteredTypes = new FilteredList<>(employeesEvaluationType, p -> true);
        typeTable.setItems(filteredTypes);

        typeSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            final String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredTypes.setPredicate(emp -> isBlank(filter) ||
                    emp.getEvaluationname().toLowerCase().contains(filter));
        });

        typeViewPanel.getChildren().addAll(typeLabel, typeSearchField, new ScrollPane(typeTable));

        // ==================== PANEL 2: Add Evaluation Type ====================
        VBox typeAddPanel = new VBox(10);
        typeAddPanel.setAlignment(Pos.TOP_LEFT);
        typeAddPanel.setPadding(new Insets(10));
        typeAddPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");
        Label addTypeLabel = new Label("Add Evaluation Type");
        TextField newTypeField = new TextField();
        newTypeField.setPromptText("Enter Evaluation Type");
        Button addTypeButton = new Button("Add Type");

        addTypeButton.setOnAction(e -> {
            if (!isBlank(newTypeField.getText())) {
                employeesEvaluationType.add(new EmployeeEvaluationTypeModel(typeCounter++, newTypeField.getText()));
                newTypeField.clear();
            }
        });

        typeAddPanel.getChildren().addAll(addTypeLabel, newTypeField, addTypeButton);

        // ==================== PANEL 3: View Evaluation Period ====================
        VBox periodViewPanel = new VBox(10);
        periodViewPanel.setAlignment(Pos.TOP_LEFT);
        Label periodLabel = new Label("Evaluation Periods");
        TextField periodSearchField = new TextField();
        periodSearchField.setPromptText("Search Evaluation Period");

        periodTable = createPeriodTable();
        employeesEvaluationPeriod = FXCollections.observableArrayList(
                new EmployeeEvaluationPeriodModel(1, "2023-Q1"),
                new EmployeeEvaluationPeriodModel(2, "2023-Q2")
        );
        filteredPeriods = new FilteredList<>(employeesEvaluationPeriod, p -> true);
        periodTable.setItems(filteredPeriods);

        periodSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            final String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredPeriods.setPredicate(emp -> isBlank(filter) ||
                    emp.getEvaluationperiod().toLowerCase().contains(filter));
        });

        periodViewPanel.getChildren().addAll(periodLabel, periodSearchField, new ScrollPane(periodTable));

        // ==================== PANEL 4: Add Evaluation Period ====================
        VBox periodAddPanel = new VBox(10);
        periodAddPanel.setAlignment(Pos.TOP_LEFT);
        periodAddPanel.setPadding(new Insets(10));
        periodAddPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");
        Label addPeriodLabel = new Label("Add Evaluation Period");
        TextField newPeriodField = new TextField();
        newPeriodField.setPromptText("Enter Evaluation Period");
        Button addPeriodButton = new Button("Add Period");

        addPeriodButton.setOnAction(e -> {
            if (!isBlank(newPeriodField.getText())) {
                employeesEvaluationPeriod.add(new EmployeeEvaluationPeriodModel(periodCounter++, newPeriodField.getText()));
                newPeriodField.clear();
            }
        });

        periodAddPanel.getChildren().addAll(addPeriodLabel, newPeriodField, addPeriodButton);

        // ==================== Add all panels into one row ====================
        row.getChildren().addAll(typeViewPanel, typeAddPanel, periodViewPanel, periodAddPanel);

        this.getChildren().add(row);
    }

    // ==================== CREATE TYPE TABLE ====================
    private TableView<EmployeeEvaluationTypeModel> createTypeTable() {
        TableView<EmployeeEvaluationTypeModel> table = new TableView<>();

        TableColumn<EmployeeEvaluationTypeModel, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<EmployeeEvaluationTypeModel, String> nameCol = new TableColumn<>("Evaluation Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("evaluationname"));

        // Action column
        TableColumn<EmployeeEvaluationTypeModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<EmployeeEvaluationTypeModel, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox pane = new HBox(5, editBtn, delBtn);

            {
                pane.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    EmployeeEvaluationTypeModel item = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(item.getEvaluationname());
                    dialog.setTitle("Edit Evaluation Type");
                    dialog.setHeaderText("Update evaluation type:");
                    dialog.setContentText("Name:");
                    dialog.showAndWait().ifPresent(newVal -> item.setEvaluationname(newVal));
                    table.refresh();
                });

                delBtn.setOnAction(e -> {
                    EmployeeEvaluationTypeModel item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete this type?",
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
        table.setPrefWidth(350);
        return table;
    }

    // ==================== CREATE PERIOD TABLE ====================
    private TableView<EmployeeEvaluationPeriodModel> createPeriodTable() {
        TableView<EmployeeEvaluationPeriodModel> table = new TableView<>();

        TableColumn<EmployeeEvaluationPeriodModel, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<EmployeeEvaluationPeriodModel, String> periodCol = new TableColumn<>("Evaluation Period");
        periodCol.setCellValueFactory(new PropertyValueFactory<>("evaluationperiod"));

        // Action column
        TableColumn<EmployeeEvaluationPeriodModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<EmployeeEvaluationPeriodModel, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox pane = new HBox(5, editBtn, delBtn);

            {
                pane.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    EmployeeEvaluationPeriodModel item = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(item.getEvaluationperiod());
                    dialog.setTitle("Edit Evaluation Period");
                    dialog.setHeaderText("Update evaluation period:");
                    dialog.setContentText("Period:");
                    dialog.showAndWait().ifPresent(newVal -> item.setEvaluationperiod(newVal));
                    table.refresh();
                });

                delBtn.setOnAction(e -> {
                    EmployeeEvaluationPeriodModel item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete this period?",
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

        table.getColumns().addAll(idCol, periodCol, actionCol);
        table.setPrefHeight(300);
        table.setPrefWidth(350);
        return table;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
