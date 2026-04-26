package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class MaximumAmountManagement extends BorderPane {

    private TableView<MaximumAmountModel> table;
    private ObservableList<MaximumAmountModel> data;
    private Connecting databaseConnector;
    private boolean isAdmin;
    private String currentUser;

    public MaximumAmountManagement(String username) {
        this.currentUser = username;
        this.databaseConnector = new Connecting();
        this.isAdmin = databaseConnector.isAdmin(currentUser.toLowerCase());

        Label title = new Label("Manage Maximum Allowed Request Amounts");
        title.setFont(Font.font(20));
        title.setPadding(new Insets(10));

        // --- Table ---
        table = new TableView<>();
        data = FXCollections.observableArrayList(databaseConnector.getAllMaximumAmountRecords());
        table.setItems(data);

        TableColumn<MaximumAmountModel, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        idCol.setPrefWidth(60);

        TableColumn<MaximumAmountModel, String> typeCol = new TableColumn<>("Fund Type");
        typeCol.setCellValueFactory(c -> c.getValue().fundTypeProperty());
        typeCol.setPrefWidth(220);

        TableColumn<MaximumAmountModel, String> amountCol = new TableColumn<>("Maximum Amount (ETB)");
        amountCol.setCellValueFactory(c -> c.getValue().maximumAmountProperty());
        amountCol.setPrefWidth(180);

        table.getColumns().addAll(idCol, typeCol, amountCol);

        // --- Buttons ---
        Button editBtn = new Button("Edit");
        
        // Style the edit button
        if (isAdmin) {
            editBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");
            setupButtonHoverEffects(editBtn, "#2980b9");
        } else {
            editBtn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
            editBtn.setDisable(true);
        }

        HBox topBar = new HBox(10, editBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        // Add admin indicator if not admin
        if (!isAdmin) {
            Label adminNote = new Label("Note: Only administrators can edit maximum amounts");
            adminNote.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-font-style: italic;");
            topBar.getChildren().add(adminNote);
        }

        // --- Actions ---
        editBtn.setOnAction(e -> {
            if (!isAdmin) {
                showAlert("Access Denied", "Only administrators can edit maximum amounts.", Alert.AlertType.WARNING);
                return;
            }
            
            MaximumAmountModel selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openEditDialog(selected);
            } else {
                showAlert("No Selection", "Please select a record to edit.", Alert.AlertType.WARNING);
            }
        });

        // ---- Layout ----
        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(10));
        mainContainer.getChildren().addAll(topBar, table);
        
        this.setCenter(mainContainer);
    }

    // -------------------------------------------------------------------------
    // EDIT RECORD
    // -------------------------------------------------------------------------
    private void openEditDialog(MaximumAmountModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Maximum Amount");
        dialog.setHeaderText("Modify Maximum Amount for " + record.getFundType());
        dialog.getDialogPane().setPrefWidth(450);

        // Create form fields
        TextField typeField = new TextField(record.getFundType());
        typeField.setDisable(true);
        typeField.setStyle("-fx-opacity: 1.0; -fx-background-color: #f8f9fa;");
        
        TextField amountField = new TextField(record.getMaximumAmount());
        amountField.setPromptText("Enter amount in ETB (e.g., 1000.00)");
        
        Label validationLabel = new Label();
        validationLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        // Create grid layout
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        // Add form fields with descriptions
        Label typeLabel = new Label("Fund Type:");
        typeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label amountLabel = new Label("Maximum Amount (ETB):");
        amountLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label amountHint = new Label("Enter numeric value only (e.g., 5000, 2500.50)");
        amountHint.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");

        grid.addRow(0, typeLabel, typeField);
        grid.addRow(1, amountLabel, amountField);
        grid.addRow(2, new Label(), amountHint);
        grid.addRow(3, new Label(), validationLabel);
        GridPane.setColumnSpan(validationLabel, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Get the OK button and set its style
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");

        // Add hover effects to dialog buttons
        setupButtonHoverEffects(okButton, "#27ae60");
        setupButtonHoverEffects(cancelButton, "#95a5a6");

        // Real-time validation
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                validationLabel.setText("Amount cannot be empty");
                okButton.setDisable(true);
            } else if (!newValue.matches("\\d+(\\.\\d{0,2})?")) {
                validationLabel.setText("Please enter a valid numeric amount (e.g., 1000 or 1000.50)");
                okButton.setDisable(true);
            } else if (Double.parseDouble(newValue) <= 0) {
                validationLabel.setText("Amount must be greater than 0");
                okButton.setDisable(true);
            } else {
                validationLabel.setText("");
                okButton.setDisable(false);
            }
        });

        // Initial validation
        okButton.setDisable(!amountField.getText().matches("\\d+(\\.\\d{0,2})?") || amountField.getText().isEmpty());

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String amountText = amountField.getText().trim();
                
                // Final validation
                if (amountText.isEmpty() || !amountText.matches("\\d+(\\.\\d{0,2})?")) {
                    showAlert("Invalid Input", "Please enter a valid numeric amount.", Alert.AlertType.ERROR);
                    return null;
                }

                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    showAlert("Invalid Amount", "Amount must be greater than 0.", Alert.AlertType.ERROR);
                    return null;
                }

                // Format amount to 2 decimal places
                String formattedAmount = String.format("%.2f", amount);

                boolean updated = databaseConnector.updateMaximumAmountRecord(
                        record.getId(),
                        record.getFundType(),
                        formattedAmount
                );

                if (updated) {
                    data.setAll(databaseConnector.getAllMaximumAmountRecords());
                    showAlert("Success", "Maximum amount updated successfully for " + record.getFundType() + "!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to update record. Please try again.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // -------------------------------------------------------------------------
    // HELPER METHODS
    // -------------------------------------------------------------------------
    private void setupButtonHoverEffects(Button button, String baseColor) {
        button.setOnMouseEntered(e -> {
            if (!button.isDisable()) {
                button.setStyle("-fx-background-color: derive(" + baseColor + ", 20%); -fx-text-fill: white; -fx-font-weight: bold;");
            }
        });
        
        button.setOnMouseExited(e -> {
            if (!button.isDisable()) {
                button.setStyle("-fx-background-color: " + baseColor + "; -fx-text-fill: white; -fx-font-weight: bold;");
            }
        });
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        
        // Style the alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #ecf0f1;");
        
        alert.showAndWait();
    }
}