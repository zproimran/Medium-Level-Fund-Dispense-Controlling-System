package smarthrms;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.Map;

public class AccessManagementSystem extends BorderPane {

    private String currentUser;
    private final Connecting accessDAO;
    private boolean isAdmin;

    private final TableView<String> rolesTable = new TableView<>();
    private final TableView<PermissionWrapper> permissionsTable = new TableView<>();

    private String selectedRole = null;
    private ObservableList<SystemPermission> allSystemPermissions;

    public AccessManagementSystem(String username) {
        this.currentUser = username;
        this.accessDAO = new Connecting();
        this.isAdmin = accessDAO.isAdmin(currentUser.toLowerCase());
        this.allSystemPermissions = accessDAO.getAllSystemPermissions();

        setPadding(new Insets(15));
        initializeUI();
    }

    private void initializeUI() {
        // Create header with refresh button
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setSpacing(20);
        headerBox.setPadding(new Insets(0, 0, 10, 0));

        Label header = new Label("Access Management System");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Refresh button
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
        setupButtonHoverEffects(refreshBtn, "#9b59b6");
        refreshBtn.setOnAction(e -> refreshAll());

        headerBox.getChildren().addAll(header, refreshBtn);
        setTop(headerBox);
        BorderPane.setAlignment(headerBox, Pos.CENTER);

        SplitPane splitPane = new SplitPane(createRolesBox(), createPermissionsBox());
        splitPane.setDividerPositions(0.5);
        setCenter(splitPane);

        loadRoles();
    }

    /* ===================== ROLES MANAGEMENT ===================== */
    private VBox createRolesBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-color: #f8f9fa;");

        Label title = new Label("System Roles");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Role count label
        Label roleCountLabel = new Label();
        roleCountLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        TableColumn<String, String> col = new TableColumn<>("Role Name");
        col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        col.setPrefWidth(250);

        rolesTable.getColumns().add(col);
        rolesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rolesTable.setStyle("-fx-border-color: #ddd; -fx-border-width: 1;");

        rolesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedRole = newVal;
            loadPermissions();
            updateRoleCount();
        });

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Button add = new Button("➕ Add Role");
        Button edit = new Button("✏️ Edit Role");
        Button delete = new Button("🗑 Delete Role");

        // Style buttons
        add.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        edit.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");
        delete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        // Add hover effects
        setupButtonHoverEffects(add, "#27ae60");
        setupButtonHoverEffects(edit, "#2980b9");
        setupButtonHoverEffects(delete, "#e74c3c");

        add.setOnAction(e -> addRole());
        edit.setOnAction(e -> editRole());
        delete.setOnAction(e -> deleteRole());

        // Disable edit and delete for non-admin users
        if (!isAdmin) {
            edit.setDisable(true);
            delete.setDisable(true);
            edit.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
            delete.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        buttons.getChildren().addAll(add, edit, delete);

        // Update role count when roles are loaded
        rolesTable.itemsProperty().addListener((obs, oldVal, newVal) -> updateRoleCount());

        // Add admin indicator
        if (!isAdmin) {
            Label adminNote = new Label("Note: Only administrators can edit or delete roles");
            adminNote.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-font-style: italic;");
            box.getChildren().addAll(title, roleCountLabel, rolesTable, buttons, adminNote);
        } else {
            box.getChildren().addAll(title, roleCountLabel, rolesTable, buttons);
        }

        return box;
    }

    private void loadRoles() {
        ObservableList<String> list = accessDAO.getSystemRoles();
        rolesTable.setItems(list);
        
        // Clear permissions if no role is selected
        if (rolesTable.getSelectionModel().getSelectedItem() == null) {
            permissionsTable.getItems().clear();
        }
        updateRoleCount();
    }

    private void updateRoleCount() {
        int totalRoles = rolesTable.getItems().size();
        int selectedCount = rolesTable.getSelectionModel().getSelectedItems().size();
        
        // Get the center content (SplitPane)
        SplitPane splitPane = (SplitPane) getCenter();
        if (splitPane != null && splitPane.getItems().size() > 0) {
            // Get the first item in SplitPane (roles VBox)
            VBox rolesBox = (VBox) splitPane.getItems().get(0);
            if (rolesBox.getChildren().size() > 1) {
                Label roleCountLabel = (Label) rolesBox.getChildren().get(1);
                roleCountLabel.setText("Total Roles: " + totalRoles + " | Selected: " + selectedCount);
            }
        }
    }

    private void addRole() {
        // Create custom dialog with description field
        Dialog<RoleData> dialog = new Dialog<>();
        dialog.setTitle("Add System Role");
        dialog.setHeaderText("Enter new system role details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField roleName = new TextField();
        roleName.setPromptText("Role Name");
        TextArea roleDescription = new TextArea();
        roleDescription.setPromptText("Role Description (Optional)");
        roleDescription.setPrefRowCount(3);

        grid.add(new Label("Role Name:"), 0, 0);
        grid.add(roleName, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(roleDescription, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Enable/disable add button based on input
        dialog.getDialogPane().lookupButton(addButtonType).setDisable(true);
        roleName.textProperty().addListener((obs, oldVal, newVal) -> {
            dialog.getDialogPane().lookupButton(addButtonType).setDisable(newVal == null || newVal.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new RoleData(roleName.getText().trim(), roleDescription.getText().trim());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(roleData -> {
            if (roleData.name.isEmpty()) {
                showWarning("Role name cannot be empty!");
                return;
            }
            
            if (accessDAO.addSystemRole(roleData.name, roleData.description)) {
                loadRoles();
                showSuccess("Role '" + roleData.name + "' added successfully!");
            } else {
                showError("Failed to add role! It might already exist.");
            }
        });
    }

    private void editRole() {
        if (!isAdmin) {
            showWarning("Access Denied", "Only administrators can edit roles.");
            return;
        }

        String selected = rolesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { 
            showWarning("Select a role to edit."); 
            return; 
        }

        // Get current role description
        String currentDescription = accessDAO.getRoleDescription(selected);

        Dialog<RoleData> dialog = new Dialog<>();
        dialog.setTitle("Edit System Role");
        dialog.setHeaderText("Edit role details:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField roleName = new TextField(selected);
        TextArea roleDescription = new TextArea(currentDescription);
        roleDescription.setPromptText("Role Description (Optional)");
        roleDescription.setPrefRowCount(3);

        grid.add(new Label("Role Name:"), 0, 0);
        grid.add(roleName, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(roleDescription, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Enable/disable save button based on input
        dialog.getDialogPane().lookupButton(saveButtonType).setDisable(true);
        roleName.textProperty().addListener((obs, oldVal, newVal) -> {
            dialog.getDialogPane().lookupButton(saveButtonType).setDisable(
                newVal == null || newVal.trim().isEmpty() || newVal.trim().equals(selected)
            );
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new RoleData(roleName.getText().trim(), roleDescription.getText().trim());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(roleData -> {
            if (roleData.name.isEmpty()) {
                showWarning("Role name cannot be empty!");
                return;
            }
            
            if (accessDAO.updateSystemRole(selected, roleData.name, roleData.description)) {
                loadRoles();
                showSuccess("Role updated successfully!");
            } else {
                showError("Failed to update role! The new name might already exist.");
            }
        });
    }

    private void deleteRole() {
        if (!isAdmin) {
            showWarning("Access Denied", "Only administrators can delete roles.");
            return;
        }

        String selected = rolesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { 
            showWarning("Select a role to delete."); 
            return; 
        }

        // Check if role has permissions assigned
        ObservableList<SystemPermission> permissions = accessDAO.getRolePermissions(selected);
        if (permissions != null && !permissions.isEmpty()) {
            showWarning("Cannot Delete", 
                "Cannot delete '" + selected + "' because it has " + permissions.size() + " permissions assigned.\n" +
                "Please remove all permissions first before deleting the role.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Role: " + selected);
        confirmation.setContentText("Are you sure you want to delete this role?\nThis action cannot be undone.");

        confirmation.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (accessDAO.deleteSystemRole(selected)) {
                    loadRoles();
                    showSuccess("Role '" + selected + "' deleted successfully!");
                } else {
                    showError("Failed to delete role! It might be assigned to users.");
                }
            }
        });
    }

    /* ===================== PERMISSIONS MANAGEMENT ===================== */
    private VBox createPermissionsBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-color: #f8f9fa;");

        Label title = new Label("Role Permissions");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Show selected role
        Label selectedRoleLabel = new Label();
        selectedRoleLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
        selectedRoleLabel.textProperty().bind(
            new SimpleStringProperty("Selected Role: ").concat(
                new SimpleStringProperty() {
                    @Override
                    public String get() {
                        return selectedRole != null ? selectedRole : "None";
                    }
                }
            )
        );

        // Permission count label
        Label permissionCountLabel = new Label();
        permissionCountLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        // Create columns for permissions table
        TableColumn<PermissionWrapper, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setPrefWidth(60);
        activeCol.setCellValueFactory(cellData -> cellData.getValue().activeProperty());
        
        TableColumn<PermissionWrapper, String> nameCol = new TableColumn<>("Permission Name");
        nameCol.setPrefWidth(200);
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        
        TableColumn<PermissionWrapper, String> descCol = new TableColumn<>("Description");
        descCol.setPrefWidth(250);
        descCol.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        
        TableColumn<PermissionWrapper, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setPrefWidth(120);
        categoryCol.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        permissionsTable.getColumns().addAll(activeCol, nameCol, descCol, categoryCol);
        permissionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        permissionsTable.setStyle("-fx-border-color: #ddd; -fx-border-width: 1;");

        // Set cell factory for checkbox column
        activeCol.setCellFactory(col -> new TableCell<PermissionWrapper, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            
            {
                checkBox.setOnAction(e -> {
                    PermissionWrapper permission = getTableView().getItems().get(getIndex());
                    if (permission != null) {
                        permission.setActive(checkBox.isSelected());
                        updatePermissionState(permission);
                    }
                });
            }
            
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setGraphic(null);
                } else {
                    PermissionWrapper permission = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(permission.isActive());
                    checkBox.setDisable(!isAdmin);
                    setGraphic(checkBox);
                }
            }
        });

        HBox topButtons = new HBox(10);
        topButtons.setAlignment(Pos.CENTER_LEFT);

        Button bulkAssign = new Button("📋 Bulk Assign");
        Button selectAll = new Button("✓ Select All");
        Button deselectAll = new Button("✗ Deselect All");

        // Style buttons
        bulkAssign.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        selectAll.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        deselectAll.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        setupButtonHoverEffects(bulkAssign, "#f39c12");
        setupButtonHoverEffects(selectAll, "#27ae60");
        setupButtonHoverEffects(deselectAll, "#e74c3c");

        bulkAssign.setOnAction(e -> bulkAssignPermissions());
        selectAll.setOnAction(e -> selectAllPermissions());
        deselectAll.setOnAction(e -> deselectAllPermissions());

        // Disable buttons for non-admin users
        if (!isAdmin) {
            bulkAssign.setDisable(true);
            selectAll.setDisable(true);
            deselectAll.setDisable(true);
            bulkAssign.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
            selectAll.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
            deselectAll.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        topButtons.getChildren().addAll(bulkAssign, selectAll, deselectAll);

        HBox bottomButtons = new HBox(10);
        bottomButtons.setAlignment(Pos.CENTER_LEFT);

        Button add = new Button("➕ Add Custom");
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

        add.setOnAction(e -> addCustomPermission());
        edit.setOnAction(e -> editPermission());
        delete.setOnAction(e -> deletePermission());

        // Disable all permission buttons initially
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

        // Enable add button when role is selected (for all users)
        rolesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean hasSelection = newVal != null;
            add.setDisable(!hasSelection);
            bulkAssign.setDisable(!hasSelection || !isAdmin);
            selectAll.setDisable(!hasSelection || !isAdmin);
            deselectAll.setDisable(!hasSelection || !isAdmin);
            
            if (!isAdmin) {
                edit.setDisable(true);
                delete.setDisable(true);
            } else {
                edit.setDisable(!hasSelection);
                delete.setDisable(!hasSelection);
            }
            
            updatePermissionCount();
        });

        // Enable edit/delete when permission is selected (for admin only)
        permissionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (isAdmin) {
                boolean hasPermissionSelection = newVal != null;
                edit.setDisable(!hasPermissionSelection);
                delete.setDisable(!hasPermissionSelection);
            }
            updatePermissionCount();
        });

        // Update permission count when permissions change
        permissionsTable.itemsProperty().addListener((obs, oldVal, newVal) -> updatePermissionCount());

        bottomButtons.getChildren().addAll(add, edit, delete);

        // Add admin indicator for permissions
        if (!isAdmin) {
            Label adminNote = new Label("Note: Only administrators can edit or delete permissions");
            adminNote.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-font-style: italic;");
            box.getChildren().addAll(title, selectedRoleLabel, permissionCountLabel, topButtons, permissionsTable, bottomButtons, adminNote);
        } else {
            box.getChildren().addAll(title, selectedRoleLabel, permissionCountLabel, topButtons, permissionsTable, bottomButtons);
        }

        return box;
    }

    private void loadPermissions() {
        if (selectedRole == null) {
            permissionsTable.getItems().clear();
            return;
        }

        // Get current role permissions
        ObservableList<SystemPermission> rolePermissions = accessDAO.getRolePermissions(selectedRole);
        Map<String, Boolean> permissionStates = new HashMap<>();
        
        // Create a map of current permissions for quick lookup
        for (SystemPermission perm : rolePermissions) {
            permissionStates.put(perm.getCode(), perm.isActive());
        }
        
        // Create wrapper objects for all system permissions with their current state
        ObservableList<PermissionWrapper> permissionWrappers = FXCollections.observableArrayList();
        for (SystemPermission systemPerm : allSystemPermissions) {
            boolean isActive = permissionStates.getOrDefault(systemPerm.getCode(), false);
            permissionWrappers.add(new PermissionWrapper(
                systemPerm.getCode(),
                systemPerm.getName(),
                systemPerm.getDescription(),
                systemPerm.getCategory(),
                isActive
            ));
        }
        
        permissionsTable.setItems(permissionWrappers);
        updatePermissionCount();
    }

    private void updatePermissionCount() {
        int totalPermissions = permissionsTable.getItems().size();
        int activeCount = 0;
        int selectedCount = permissionsTable.getSelectionModel().getSelectedItems().size();
        
        for (PermissionWrapper perm : permissionsTable.getItems()) {
            if (perm.isActive()) {
                activeCount++;
            }
        }
        
        // Get the center content (SplitPane)
        SplitPane splitPane = (SplitPane) getCenter();
        if (splitPane != null && splitPane.getItems().size() > 1) {
            // Get the second item in SplitPane (permissions VBox)
            VBox permissionsBox = (VBox) splitPane.getItems().get(1);
            if (permissionsBox.getChildren().size() > 2) {
                Label permissionCountLabel = (Label) permissionsBox.getChildren().get(2);
                permissionCountLabel.setText("Total: " + totalPermissions + " | Active: " + activeCount + " | Selected: " + selectedCount);
            }
        }
    }

    private void updatePermissionState(PermissionWrapper permission) {
        if (selectedRole == null || !isAdmin) return;
        
        if (accessDAO.toggleRolePermission(selectedRole, permission.getCode(), permission.isActive())) {
//            showSuccess("Permission '" + permission.getName() + "' " + 
//                       (permission.isActive() ? "activated" : "deactivated") + " successfully!");
        } else {
            showError("Failed to update permission state!");
            // Revert the change in UI
            permission.setActive(!permission.isActive());
        }
    }

    private void bulkAssignPermissions() {
        if (selectedRole == null || !isAdmin) {
            showWarning("Select a role first.");
            return;
        }

        Dialog<Map<String, Boolean>> dialog = new Dialog<>();
        dialog.setTitle("Bulk Assign Permissions");
        dialog.setHeaderText("Select permissions for role: " + selectedRole);

        ButtonType applyButtonType = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.CANCEL);

        // Create a scrollable grid of checkboxes for all permissions
        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        // Group permissions by category
        Map<String, VBox> categoryBoxes = new HashMap<>();
        
        for (SystemPermission perm : allSystemPermissions) {
            String category = perm.getCategory();
            if (!categoryBoxes.containsKey(category)) {
                VBox categoryBox = new VBox(5);
                categoryBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #ecf0f1;");
                
                Label categoryLabel = new Label(category);
                categoryLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                categoryBox.getChildren().add(categoryLabel);
                
                categoryBoxes.put(category, categoryBox);
                content.getChildren().add(categoryBox);
            }
            
            CheckBox checkBox = new CheckBox(perm.getName() + " - " + perm.getDescription());
            checkBox.setUserData(perm.getCode());
            categoryBoxes.get(category).getChildren().add(checkBox);
        }

        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        dialog.getDialogPane().setContent(scrollPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButtonType) {
                Map<String, Boolean> permissionStates = new HashMap<>();
                for (SystemPermission perm : allSystemPermissions) {
                    permissionStates.put(perm.getCode(), false);
                }
                
                // Collect all checked permissions
                for (String category : categoryBoxes.keySet()) {
                    VBox categoryBox = categoryBoxes.get(category);
                    for (javafx.scene.Node node : categoryBox.getChildren()) {
                        if (node instanceof CheckBox) {
                            CheckBox checkBox = (CheckBox) node;
                            if (checkBox.isSelected()) {
                                permissionStates.put((String) checkBox.getUserData(), true);
                            }
                        }
                    }
                }
                return permissionStates;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(permissionStates -> {
            if (accessDAO.bulkUpdateRolePermissions(selectedRole, permissionStates)) {
                loadPermissions();
                showSuccess("Bulk permissions assignment completed successfully!");
            } else {
                showError("Failed to apply bulk permissions!");
            }
        });
    }

    private void selectAllPermissions() {
        if (selectedRole == null || !isAdmin) return;
        
        for (PermissionWrapper perm : permissionsTable.getItems()) {
            perm.setActive(true);
            accessDAO.toggleRolePermission(selectedRole, perm.getCode(), true);
        }
        showSuccess("All permissions activated!");
        updatePermissionCount();
    }

    private void deselectAllPermissions() {
        if (selectedRole == null || !isAdmin) return;
        
        for (PermissionWrapper perm : permissionsTable.getItems()) {
            perm.setActive(false);
            accessDAO.toggleRolePermission(selectedRole, perm.getCode(), false);
        }
        showSuccess("All permissions deactivated!");
        updatePermissionCount();
    }

    private void addCustomPermission() {
        if (selectedRole == null) { 
            showWarning("Select a role first."); 
            return; 
        }

        // Create a custom dialog for permission selection
        Dialog<PermissionData> dialog = new Dialog<>();
        dialog.setTitle("Add Custom Permission");
        dialog.setHeaderText("Add custom permission for role: " + selectedRole);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField permissionCode = new TextField();
        permissionCode.setPromptText("Permission Code (e.g., CUSTOM_ACTION)");
        
        TextField permissionName = new TextField();
        permissionName.setPromptText("Permission Name");
        
        TextArea permissionDescription = new TextArea();
        permissionDescription.setPromptText("Permission Description");
        permissionDescription.setPrefRowCount(2);

        grid.add(new Label("Code:"), 0, 0);
        grid.add(permissionCode, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(permissionName, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(permissionDescription, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Enable/disable add button based on input
        dialog.getDialogPane().lookupButton(addButtonType).setDisable(true);

        // Create a single listener that checks both fields
        javafx.beans.value.ChangeListener<String> fieldListener = (obs, oldVal, newVal) -> {
            boolean disable = permissionCode.getText().trim().isEmpty() || permissionName.getText().trim().isEmpty();
            dialog.getDialogPane().lookupButton(addButtonType).setDisable(disable);
        };

        permissionCode.textProperty().addListener(fieldListener);
        permissionName.textProperty().addListener(fieldListener);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new PermissionData(
                    permissionCode.getText().trim(),
                    permissionName.getText().trim(),
                    permissionDescription.getText().trim()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(permissionData -> {
            if (permissionData.name.isEmpty() || permissionData.code.isEmpty()) {
                showWarning("Permission code and name cannot be empty!");
                return;
            }
            
            // Add the custom permission to the role
            if (accessDAO.addRolePermission(selectedRole, permissionData.code, permissionData.name, permissionData.description)) {
                // Also add it to the system permissions master table if it doesn't exist
                accessDAO.addSystemPermission(permissionData.code, permissionData.name, permissionData.description, "Custom");
                
                // Refresh the permissions list
                allSystemPermissions = accessDAO.getAllSystemPermissions();
                loadPermissions();
                showSuccess("Custom permission '" + permissionData.name + "' added successfully!");
            } else {
                showError("Failed to add custom permission! It might already exist for this role.");
            }
        });
    }

    private void editPermission() {
        if (!isAdmin) {
            showWarning("Access Denied", "Only administrators can edit permissions.");
            return;
        }

        PermissionWrapper selectedPermission = permissionsTable.getSelectionModel().getSelectedItem();
        if (selectedPermission == null) { 
            showWarning("Select a permission to edit."); 
            return; 
        }

        // Get current permission description
        String currentDescription = accessDAO.getPermissionDescription(selectedRole, selectedPermission.getCode());

        Dialog<PermissionData> dialog = new Dialog<>();
        dialog.setTitle("Edit Permission");
        dialog.setHeaderText("Edit permission for role: " + selectedRole);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField permissionName = new TextField(selectedPermission.getName());
        TextArea permissionDescription = new TextArea(currentDescription);
        permissionDescription.setPromptText("Permission Description");
        permissionDescription.setPrefRowCount(3);

        grid.add(new Label("Permission Name:"), 0, 0);
        grid.add(permissionName, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(permissionDescription, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Enable/disable save button based on input
        dialog.getDialogPane().lookupButton(saveButtonType).setDisable(true);
        permissionName.textProperty().addListener((obs, oldVal, newVal) -> {
            dialog.getDialogPane().lookupButton(saveButtonType).setDisable(
                newVal == null || newVal.trim().isEmpty() || newVal.trim().equals(selectedPermission.getName())
            );
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new PermissionData(selectedPermission.getCode(), permissionName.getText().trim(), permissionDescription.getText().trim());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(permissionData -> {
            if (permissionData.name.isEmpty()) {
                showWarning("Permission name cannot be empty!");
                return;
            }
            
            if (accessDAO.updateRolePermission(selectedRole, selectedPermission.getCode(), permissionData.name, permissionData.description)) {
                loadPermissions();
                showSuccess("Permission updated successfully!");
            } else {
                showError("Failed to update permission! The new name might already exist.");
            }
        });
    }

    private void deletePermission() {
        if (!isAdmin) {
            showWarning("Access Denied", "Only administrators can delete permissions.");
            return;
        }

        PermissionWrapper selectedPermission = permissionsTable.getSelectionModel().getSelectedItem();
        if (selectedPermission == null) { 
            showWarning("Select a permission to delete."); 
            return; 
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Permission");
        confirmation.setContentText("Are you sure you want to delete permission '" + selectedPermission.getName() + "' from role '" + selectedRole + "'?\nThis action cannot be undone.");

        confirmation.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (accessDAO.deleteRolePermission(selectedRole, selectedPermission.getCode())) {
                    loadPermissions();
                    showSuccess("Permission '" + selectedPermission.getName() + "' deleted successfully!");
                } else {
                    showError("Failed to delete permission!");
                }
            }
        });
    }

    /* ===================== REFRESH FUNCTIONALITY ===================== */
    private void refreshAll() {
        loadRoles();
        allSystemPermissions = accessDAO.getAllSystemPermissions();
        if (selectedRole != null) {
            loadPermissions();
        }
        showSuccess("Data refreshed successfully!");
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

    /* ===================== DATA CLASSES ===================== */
    
    private static class RoleData {
        String name;
        String description;
        
        RoleData(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
    
    private static class PermissionData {
        String code;
        String name;
        String description;
        
        PermissionData(String code, String name, String description) {
            this.code = code;
            this.name = name;
            this.description = description;
        }
    }
    
    public static class PermissionWrapper {
        private final SimpleStringProperty code;
        private final SimpleStringProperty name;
        private final SimpleStringProperty description;
        private final SimpleStringProperty category;
        private final SimpleBooleanProperty active;
        
        public PermissionWrapper(String code, String name, String description, String category, boolean active) {
            this.code = new SimpleStringProperty(code);
            this.name = new SimpleStringProperty(name);
            this.description = new SimpleStringProperty(description);
            this.category = new SimpleStringProperty(category);
            this.active = new SimpleBooleanProperty(active);
        }
        
        public String getCode() { return code.get(); }
        public String getName() { return name.get(); }
        public String getDescription() { return description.get(); }
        public String getCategory() { return category.get(); }
        public boolean isActive() { return active.get(); }
        
        public void setActive(boolean active) { this.active.set(active); }
        
        public SimpleStringProperty codeProperty() { return code; }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty descriptionProperty() { return description; }
        public SimpleStringProperty categoryProperty() { return category; }
        public SimpleBooleanProperty activeProperty() { return active; }
    }
}