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

public class ManageBankNames extends VBox {

    private TableView<BankModel> bankTable;
    private ObservableList<BankModel> banksList;
    private FilteredList<BankModel> filteredBanks;
    private int bankCounter = 3;

    public ManageBankNames() {
        initializeUI();
    }

    private void initializeUI() {
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f5f5f5;");

        HBox row = new HBox(20);
        row.setAlignment(Pos.TOP_CENTER);

        // ==================== PANEL 1: View Banks ====================
        VBox bankViewPanel = new VBox(10);
        bankViewPanel.setAlignment(Pos.TOP_LEFT);
        Label bankLabel = new Label("Banks");
        TextField bankSearchField = new TextField();
        bankSearchField.setPromptText("Search Bank Name");

        bankTable = createBankTable();
        banksList = FXCollections.observableArrayList(
                new BankModel(1, "Commercial Bank"),
                new BankModel(2, "Awash Bank")
        );
        filteredBanks = new FilteredList<>(banksList, p -> true);
        bankTable.setItems(filteredBanks);

        bankSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            final String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredBanks.setPredicate(bank -> isBlank(filter) ||
                    bank.getBankName().toLowerCase().contains(filter));
        });

        bankViewPanel.getChildren().addAll(bankLabel, bankSearchField, new ScrollPane(bankTable));

        // ==================== PANEL 2: Add Bank ====================
        VBox bankAddPanel = new VBox(10);
        bankAddPanel.setAlignment(Pos.TOP_LEFT);
        bankAddPanel.setPadding(new Insets(10));
        bankAddPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");
        Label addBankLabel = new Label("Add Bank");
        TextField newBankField = new TextField();
        newBankField.setPromptText("Enter Bank Name");
        Button addBankButton = new Button("Add Bank");

        addBankButton.setOnAction(e -> {
            if (!isBlank(newBankField.getText())) {
                banksList.add(new BankModel(bankCounter++, newBankField.getText()));
                newBankField.clear();
            }
        });

        bankAddPanel.getChildren().addAll(addBankLabel, newBankField, addBankButton);

        // ==================== Add Panels to Row ====================
        row.getChildren().addAll(bankViewPanel, bankAddPanel);
        this.getChildren().add(row);
    }

    private TableView<BankModel> createBankTable() {
        TableView<BankModel> table = new TableView<>();

        TableColumn<BankModel, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<BankModel, String> nameCol = new TableColumn<>("Bank Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("bankName"));

        // Action column
        TableColumn<BankModel, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<BankModel, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button delBtn = new Button("Delete");
            private final HBox pane = new HBox(5, editBtn, delBtn);

            {
                pane.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    BankModel item = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog(item.getBankName());
                    dialog.setTitle("Edit Bank");
                    dialog.setHeaderText("Update bank name:");
                    dialog.setContentText("Bank Name:");
                    dialog.showAndWait().ifPresent(newVal -> item.setBankName(newVal));
                    table.refresh();
                });

                delBtn.setOnAction(e -> {
                    BankModel item = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure you want to delete this bank?",
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
