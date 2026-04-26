package smarthrms;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AgreementCategoryManagementPage extends BorderPane {

    private String currentUser;
    private final Connecting categoryDAO;
    private boolean isAdmin;

    private final TableView<String> mainCategoryTable = new TableView<>();
    private final TableView<String> subCategoryTable = new TableView<>();

    private String selectedMainCategory = null;

    public AgreementCategoryManagementPage(String username) {
        this.currentUser = username;
        this.categoryDAO = new Connecting();
        this.isAdmin = categoryDAO.isAdmin(currentUser.toLowerCase());

        setPadding(new Insets(15));

        Label header = new Label("Agreement-Based Purchase Fund Category Management");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);

        SplitPane splitPane = new SplitPane(createMainCategoryBox(), createSubCategoryBox());
        splitPane.setDividerPositions(0.5);
        setCenter(splitPane);

        loadMainCategories();
    }

    /* ===================== MAIN CATEGORY ===================== */
    private VBox createMainCategoryBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label title = new Label("Agreement-Based Main Categories");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableColumn<String, String> col = new TableColumn<>("Category Name");
        col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        col.setPrefWidth(250);

        mainCategoryTable.getColumns().add(col);
        mainCategoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mainCategoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedMainCategory = newVal;
            loadSubCategories();
        });

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Button add = new Button("➕ Add");
        Button edit = new Button("✏️ Edit");
        Button delete = new Button("🗑 Delete");

        // Style buttons
        add.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        edit.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");
        delete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        // Add hover effects
        setupButtonHoverEffects(add, "#27ae60");
        setupButtonHoverEffects(edit, "#2980b9");
        setupButtonHoverEffects(delete, "#e74c3c");

        add.setOnAction(e -> addMainCategory());
        edit.setOnAction(e -> editMainCategory());
        delete.setOnAction(e -> deleteMainCategory());

        // Disable edit and delete for non-admin users
        if (!isAdmin) {
            edit.setDisable(true);
            delete.setDisable(true);
            edit.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
            delete.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        buttons.getChildren().addAll(add, edit, delete);

        // Add admin indicator
        if (!isAdmin) {
            Label adminNote = new Label("Note: Only administrators can edit or delete categories");
            adminNote.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-font-style: italic;");
            box.getChildren().addAll(title, mainCategoryTable, buttons, adminNote);
        } else {
            box.getChildren().addAll(title, mainCategoryTable, buttons);
        }

        return box;
    }

    private void loadMainCategories() {
        ObservableList<String> list = categoryDAO.getAgreementMainCategories();
        mainCategoryTable.setItems(list);
        
        // Clear subcategories if no main category is selected
        if (mainCategoryTable.getSelectionModel().getSelectedItem() == null) {
            subCategoryTable.getItems().clear();
        }
    }

    private void addMainCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Agreement Main Category");
        dialog.setHeaderText("Enter new agreement-based main category:");
        dialog.setContentText("Category Name:");

        dialog.showAndWait().ifPresent(name -> {
            if (name == null || name.trim().isEmpty()) {
                showWarning("Category name cannot be empty!");
                return;
            }
            
            if (categoryDAO.addAgreementMainCategory(name.trim())) {
                loadMainCategories();
                showSuccess("Main category added successfully!");
            } else {
                showError("Failed to add main category! It might already exist.");
            }
        });
    }

    private void editMainCategory() {
        if (!isAdmin) {
            showWarning("Access Denied", "Only administrators can edit categories.");
            return;
        }

        String selected = mainCategoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) { 
            showWarning("Select a main category to edit."); 
            return; 
        }

        TextInputDialog dialog = new TextInputDialog(selected);
        dialog.setTitle("Edit Agreement Main Category");
        dialog.setHeaderText("Edit category name:");
        dialog.setContentText("New Category Name:");

        dialog.showAndWait().ifPresent(newName -> {
            if (newName == null || newName.trim().isEmpty()) {
                showWarning("Category name cannot be empty!");
                return;
            }
            
            if (newName.trim().equals(selected)) {
                showWarning("No changes made.");
                return;
            }
            
            if (categoryDAO.updateAgreementMainCategory(selected, newName.trim())) {
                loadMainCategories();
                showSuccess("Main category updated successfully!");
            } else {
                showError("Failed to update main category! The new name might already exist.");
            }
        });
    }

    private void deleteMainCategory() {
        if (!isAdmin) {
            showWarning("Access Denied", "Only administrators can delete categories.");
            return;
        }

        String selected = mainCategoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) { 
            showWarning("Select a main category to delete."); 
            return; 
        }

        // Check if category has subcategories
        ObservableList<String> subCategories = categoryDAO.getAgreementSubCategories(selected);
        if (subCategories != null && !subCategories.isEmpty()) {
            showWarning("Cannot Delete", 
                "Cannot delete '" + selected + "' because it contains subcategories.\n" +
                "Please delete all subcategories first before deleting the main category.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Main Category");
        confirmation.setContentText("Are you sure you want to delete '" + selected + "'?\nThis action cannot be undone.");

        confirmation.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (categoryDAO.deleteAgreementMainCategory(selected)) {
                    loadMainCategories();
                    showSuccess("Main category deleted successfully!");
                } else {
                    showError("Failed to delete main category! It might be in use.");
                }
            }
        });
    }

    /* ===================== SUBCATEGORY ===================== */
    private VBox createSubCategoryBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label title = new Label("Agreement-Based Subcategories");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Show selected main category
        Label selectedCategoryLabel = new Label();
        selectedCategoryLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
        selectedCategoryLabel.textProperty().bind(
            new SimpleStringProperty("Selected Category: ").concat(
                new SimpleStringProperty() {
                    @Override
                    public String get() {
                        return selectedMainCategory != null ? selectedMainCategory : "None";
                    }
                }
            )
        );

        TableColumn<String, String> col = new TableColumn<>("Subcategory Name");
        col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        col.setPrefWidth(250);

        subCategoryTable.getColumns().add(col);
        subCategoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Button add = new Button("➕ Add");
        Button edit = new Button("✏️ Edit");
        Button delete = new Button("🗑 Delete");

        // Style buttons
        add.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        edit.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");
        delete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        // Add hover effects
        setupButtonHoverEffects(add, "#27ae60");
        setupButtonHoverEffects(edit, "#2980b9");
        setupButtonHoverEffects(delete, "#e74c3c");

        add.setOnAction(e -> addSubCategory());
        edit.setOnAction(e -> editSubCategory());
        delete.setOnAction(e -> deleteSubCategory());

        // Disable all subcategory buttons initially
        add.setDisable(true);
        edit.setDisable(true);
        delete.setDisable(true);

        // Disable edit and delete for non-admin users
        if (!isAdmin) {
            edit.setDisable(true);
            delete.setDisable(true);
            edit.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
            delete.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        // Enable add button when main category is selected (for all users)
        mainCategoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean hasSelection = newVal != null;
            add.setDisable(!hasSelection);
            
            if (!isAdmin) {
                edit.setDisable(true);
                delete.setDisable(true);
            } else {
                edit.setDisable(!hasSelection);
                delete.setDisable(!hasSelection);
            }
        });

        // Enable edit/delete when subcategory is selected (for admin only)
        subCategoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (isAdmin) {
                boolean hasSubSelection = newVal != null;
                edit.setDisable(!hasSubSelection);
                delete.setDisable(!hasSubSelection);
            }
        });

        buttons.getChildren().addAll(add, edit, delete);

        // Add admin indicator for subcategories
        if (!isAdmin) {
            Label adminNote = new Label("Note: Only administrators can edit or delete subcategories");
            adminNote.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-font-style: italic;");
            box.getChildren().addAll(title, selectedCategoryLabel, subCategoryTable, buttons, adminNote);
        } else {
            box.getChildren().addAll(title, selectedCategoryLabel, subCategoryTable, buttons);
        }

        return box;
    }

    private void loadSubCategories() {
        if (selectedMainCategory == null) {
            subCategoryTable.getItems().clear();
            return;
        }

        ObservableList<String> subCategories = categoryDAO.getAgreementSubCategories(selectedMainCategory);
        subCategoryTable.setItems(subCategories);
    }

    private void addSubCategory() {
        if (selectedMainCategory == null) { 
            showWarning("Select a main category first."); 
            return; 
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Agreement Subcategory");
        dialog.setHeaderText("Add subcategory under: " + selectedMainCategory);
        dialog.setContentText("Subcategory Name:");

        dialog.showAndWait().ifPresent(name -> {
            if (name == null || name.trim().isEmpty()) {
                showWarning("Subcategory name cannot be empty!");
                return;
            }
            
            if (categoryDAO.addAgreementSubCategory(selectedMainCategory, name.trim())) {
                loadSubCategories();
                showSuccess("Subcategory added successfully!");
            } else {
                showError("Failed to add subcategory! It might already exist under this category.");
            }
        });
    }

    private void editSubCategory() {
        if (!isAdmin) {
            showWarning("Access Denied", "Only administrators can edit subcategories.");
            return;
        }

        String selected = subCategoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) { 
            showWarning("Select a subcategory to edit."); 
            return; 
        }

        TextInputDialog dialog = new TextInputDialog(selected);
        dialog.setTitle("Edit Agreement Subcategory");
        dialog.setHeaderText("Edit subcategory name:");
        dialog.setContentText("New Subcategory Name:");

        dialog.showAndWait().ifPresent(newName -> {
            if (newName == null || newName.trim().isEmpty()) {
                showWarning("Subcategory name cannot be empty!");
                return;
            }
            
            if (newName.trim().equals(selected)) {
                showWarning("No changes made.");
                return;
            }
            
            if (categoryDAO.updateAgreementSubCategory(selectedMainCategory, selected, newName.trim())) {
                loadSubCategories();
                showSuccess("Subcategory updated successfully!");
            } else {
                showError("Failed to update subcategory! The new name might already exist.");
            }
        });
    }

    private void deleteSubCategory() {
        if (!isAdmin) {
            showWarning("Access Denied", "Only administrators can delete subcategories.");
            return;
        }

        String selected = subCategoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) { 
            showWarning("Select a subcategory to delete."); 
            return; 
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Subcategory");
        confirmation.setContentText("Are you sure you want to delete '" + selected + "'?\nThis action cannot be undone.");

        confirmation.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (categoryDAO.deleteAgreementSubCategory(selectedMainCategory, selected)) {
                    loadSubCategories();
                    showSuccess("Subcategory deleted successfully!");
                } else {
                    showError("Failed to delete subcategory! It might be in use.");
                }
            }
        });
    }

    /* ===================== HELPER METHODS ===================== */

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

    private void showError(String msg) { 
        showAlert(Alert.AlertType.ERROR, "Error", msg); 
    }
    
    private void showWarning(String msg) { 
        showAlert(Alert.AlertType.WARNING, "Warning", msg); 
    }
    
    private void showWarning(String title, String msg) { 
        showAlert(Alert.AlertType.WARNING, title, msg); 
    }
    
    private void showSuccess(String msg) { 
        showAlert(Alert.AlertType.INFORMATION, "Success", msg); 
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}