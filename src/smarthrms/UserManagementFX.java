package smarthrms;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class UserManagementFX extends Application {
    private String currentUser;
    private TableView<User> userTable;
    private ObservableList<User> userData = FXCollections.observableArrayList();
    private BorderPane root;  // Main UI container
    private Stage primaryStage; // Used only in standalone mode

    // Constructor builds UI so getContent() can be called anytime
    public UserManagementFX(String username) {
        this.currentUser=username;
        buildUI();
        fetchUsers();
    }

    // Build the full UI inside root BorderPane
    private void buildUI() {
        root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(240, 240, 240), CornerRadii.EMPTY, Insets.EMPTY)));

        // Title Panel
        Label titleLabel = new Label("User Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.rgb(0, 102, 204));
        HBox titlePanel = new HBox(titleLabel);
        titlePanel.setAlignment(Pos.CENTER);
        titlePanel.setBackground(new Background(new BackgroundFill(Color.rgb(240, 240, 240), CornerRadii.EMPTY, Insets.EMPTY)));

        // Button Panel
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(10));
        buttonPanel.setAlignment(Pos.CENTER_LEFT);

        Button refreshButton = createStyledButton("Refresh");
        Button viewUserButton = createStyledButton("View User");
        Button manageDept = createStyledButton("Departments");
        Button manageRole = createStyledButton("Roles");
        Button resetPAssword = createStyledButton("Reset Password");

        buttonPanel.getChildren().addAll( refreshButton, viewUserButton,manageDept,manageRole,resetPAssword);

        root.setTop(new VBox(titlePanel, buttonPanel));

        // Create Table
        createUserTable();
        root.setCenter(userTable);

        // Set actions
        refreshButton.setOnAction(e -> fetchUsers());
        viewUserButton.setOnAction(e -> viewSelectedRow());
        manageDept.setOnAction(e -> new DepartmentManagement().createAndShowGUI());
        manageRole.setOnAction(e -> new RoleManagement().createAndShowGUI());
        resetPAssword.setOnAction(e -> {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
        showAlert("Please select a user to reset password!", Alert.AlertType.WARNING);
        return;
       }
        String username = selectedUser.getUserName();
     javax.swing.SwingUtilities.invokeLater(() -> {
    PasswordReset pr = new PasswordReset(username);
    pr.createAndShowGUI();
});

        
        });
    }

    // This method lets external callers get the UI node to embed
    public Parent getContent() {
        return root;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("User Management");

        // Set application icon (optional, you can remove if no icon)
        Image appIcon = new Image(getClass().getResourceAsStream("/icons/appIcon.png"));
        primaryStage.getIcons().add(appIcon);

        // Disable maximize option
        primaryStage.setResizable(false);

        // Use UI built in constructor
        Scene scene = new Scene(root, 1200, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial data load
        fetchUsers();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setBackground(new Background(new BackgroundFill(Color.rgb(0, 102, 204), new CornerRadii(3), Insets.EMPTY)));
        button.setTextFill(Color.WHITE);
        button.setPadding(new Insets(10, 20, 10, 20));
        return button;
    }

    private void createUserTable() {
        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create columns
        TableColumn<User, Integer> idCol = new TableColumn<>("UserID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> fullNameCol = new TableColumn<>("Full Name");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Activate/Deactivate column
        TableColumn<User, Void> activateCol = new TableColumn<>("Activate/Deactivate");
        activateCol.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button button = new Button();

            {
                button.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    Connecting conn = new Connecting();
                    boolean success;

                    if (user.getStatus().equals("Active")) {
                        success = conn.deactivateUser(user.getId());
                        if (success) {
                            user.setStatus("Inactive");
                            getTableView().refresh();
                        }
                    } else {
                        success = conn.activateUser(user.getId());
                        if (success) {
                            user.setStatus("Active");
                            getTableView().refresh();
                        }
                    }

                    if (!success) {
                        showAlert("Error updating user status.", Alert.AlertType.ERROR);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    button.setText(user.getStatus().equals("Active") ? "Deactivate" : "Activate");
                    setGraphic(button);
                }
            }
        });

        // Update column
        TableColumn<User, Void> updateCol = new TableColumn<>("Update");
        updateCol.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button button = new Button("Update");

            {
                button.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    showUpdateDialog(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });

        // Delete column
        TableColumn<User, Void> deleteCol = new TableColumn<>("Delete");
        deleteCol.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button button = new Button("Delete");

            {
                button.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });

        // Add columns to table
        userTable.getColumns().addAll(idCol, fullNameCol, usernameCol, emailCol, roleCol, deptCol,
                statusCol, activateCol, updateCol, deleteCol);
    }

    private void fetchUsers() {
        userData.clear();
        Connecting conn = new Connecting();
        List<User> users = conn.getUsers();
        userData.addAll(users);
        userTable.setItems(userData);
    }

    private void viewSelectedRow() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Please select a row to view!", Alert.AlertType.WARNING);
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("User ID: ").append(selectedUser.getId()).append("\n");
        details.append("Full Name: ").append(selectedUser.getFullName()).append("\n");
        details.append("Username: ").append(selectedUser.getUserName()).append("\n");
        details.append("Email: ").append(selectedUser.getEmail()).append("\n");
        details.append("Role: ").append(selectedUser.getRole()).append("\n");
        details.append("Department: ").append(selectedUser.getDepartment()).append("\n");
        details.append("Status: ").append(selectedUser.getStatus());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("View User");
        alert.setHeaderText("User Details");
        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    private void showUpdateDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Update User");
        dialog.setHeaderText("Update User Information");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField fullNameField = new TextField(user.getFullName());
        TextField emailField = new TextField(user.getEmail());

        Connecting conn = new Connecting();
        String[] roles = {"Admin","Doctor","Midwifer"};
        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList(roles));
        roleCombo.setValue(user.getRole());

        String[] depts = conn.getDepartmentsFromDatabase();
        ComboBox<String> deptCombo = new ComboBox<>(FXCollections.observableArrayList(depts));
        deptCombo.setValue(user.getDepartment());

        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Role:"), 0, 2);
        grid.add(roleCombo, 1, 2);
        grid.add(new Label("Department:"), 0, 3);
        grid.add(deptCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                user.setFullName(fullNameField.getText());
                user.setEmail(emailField.getText());
                user.setRole(roleCombo.getValue());
                user.setDepartment(deptCombo.getValue());
                return user;
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();

        result.ifPresent(updatedUser -> {
            Connecting updateConn = new Connecting();
            boolean success = updateConn.updateUser(
                    updatedUser.getId(),
                    updatedUser.getFullName(),
                    updatedUser.getEmail(),
                    updatedUser.getRole(),
                    updatedUser.getDepartment()
            );

            if (success) {
                showAlert("User updated successfully!", Alert.AlertType.INFORMATION);
                userTable.refresh();
            } else {
                showAlert("Error updating user.", Alert.AlertType.ERROR);
            }
        });
    }

    private void showDeleteConfirmation(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText("User: " + user.getFullName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Connecting conn = new Connecting();
            boolean success = conn.deleteUser(user.getId());
            if (success) {
                showAlert("User deleted successfully.", Alert.AlertType.INFORMATION);
                userData.remove(user);
            } else {
                showAlert("Error deleting user.", Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
