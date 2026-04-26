package smarthrms;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
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
import java.util.function.UnaryOperator;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class EmployeeTerminationApproval extends Application {
    private String currentUser;
    private TableView<User> employeeTable;
    private ObservableList<User> userData = FXCollections.observableArrayList();
    private BorderPane root;  // Main UI container
    private Stage primaryStage; // Used only in standalone mode
    private static ImageView imageView;
    private TableView<EmployeeOvertimeRecordModel> overtimeTable;
    private ObservableList<EmployeeOvertimeRecordModel> overtimeData = FXCollections.observableArrayList();
    private TableView<EmployeeDeductionRecordModel> deductionTable;
    private ObservableList<EmployeeDeductionRecordModel> deductionData = FXCollections.observableArrayList();
    private TableView<EmployeeAllowanceRecordModel> allowanceTable;
    private ObservableList<EmployeeAllowanceRecordModel> allowanceData = FXCollections.observableArrayList();
    private TableView<EmployeeCashIndemnityRecordModel> indemnityTable;
    private ObservableList<EmployeeCashIndemnityRecordModel> indemnityData;
    private TableView<EmployeeCostSharingRecordModel> costSharingTable;
    private ObservableList<EmployeeCostSharingRecordModel> data;
    private ObservableList<EmployeeServiceChargeRecordModel> serviceChargeData = FXCollections.observableArrayList();
    private TableView<EmployeeLoanRecordModel> loanTable;
    private ObservableList<EmployeeLoanRecordModel> loanData = FXCollections.observableArrayList();

    // Constructor builds UI so getContent() can be called anytime
    public EmployeeTerminationApproval(String username) {
        this.currentUser=username;
        buildUI();
        fetchUsers();
    }

    // Build the full UI inside root BorderPane
    private void buildUI() {
        root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(240, 240, 240), CornerRadii.EMPTY, Insets.EMPTY)));   
   
        Button btn7 = createStyledButton("Button7");
        btn7.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: black; -fx-text-fill: white;");
        btn7.setOnAction(e->{
          
        });
            
        Label searchLabel = new Label("Search");
        TextField searchField = new TextField();
        searchField.setPromptText("Search");
         
        HBox search = new HBox();
        search.getChildren().addAll(searchLabel,new Label("   "),searchField);
          
        VBox v= new VBox(10);
        v.getChildren().addAll(search);
        root.setTop(new VBox(v));

          // Create Table
        createUserTable();
        root.setCenter(employeeTable);
    }

    // This method lets external callers get the UI node to embed
    public Parent getContent() {
        return root;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Employee Termination Approval");

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
        button.setPadding(new Insets(3, 20, 3
                , 20));
        return button;
    }
    private void createUserTable() {
        employeeTable = new TableView<>();
        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create columns
        TableColumn<User, Integer> employeecodeCol = new TableColumn<>("Employee Code");
        employeecodeCol.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));

        TableColumn<User, String> titleCol = new TableColumn<>("Courtile Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));

        TableColumn<User, String> fullNameCol = new TableColumn<>("Full Name");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<User, String> monthlysalaryCol = new TableColumn<>("Monthly Salary");
        monthlysalaryCol.setCellValueFactory(new PropertyValueFactory<>("monthlySalary"));
        
         TableColumn<User, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        
        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<User, String> employeementCol = new TableColumn<>("Employment");
        employeementCol.setCellValueFactory(new PropertyValueFactory<>("employement"));
        
        TableColumn<User, String> serviceyearCol = new TableColumn<>("Service Year");
        serviceyearCol.setCellValueFactory(new PropertyValueFactory<>("serviceYear"));
        
        // Activate/Deactivate column
        TableColumn<User, Void> activateCol = new TableColumn<>("Approve Termination");
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
                    button.setText(user.getStatus().equals("Active") ? "Approve Termination" : "Cancel Approval");
                    setGraphic(button);
                }
            }
        });

        // Add columns to table
        employeeTable.getColumns().addAll(employeecodeCol, titleCol,fullNameCol,monthlysalaryCol, deptCol,
                statusCol, employeementCol, serviceyearCol,activateCol);
    }
    
  private Stage registrationStage = null; 
    
    private void fetchUsers() {
        userData.clear();
        Connecting conn = new Connecting();
     List<User> users = conn.getUsers();
        userData.addAll(users);
        employeeTable.setItems(userData);
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
        String[] roles = {"Admin", "Nurse", "Doctor", "Pharmacy", "PharmacyAdmin", "Manager"};
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
                employeeTable.refresh();
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
