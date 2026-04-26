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

public class AddEmployeeData extends Application {
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
    public AddEmployeeData(String username) {
        this.currentUser=username;
        buildUI();
        fetchUsers();
    }

    // Build the full UI inside root BorderPane
    private void buildUI() {
        root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(240, 240, 240), CornerRadii.EMPTY, Insets.EMPTY)));   
        // Button Panel
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(3));
        buttonPanel.setAlignment(Pos.CENTER_LEFT);        
        // Button Panel
        HBox buttonPanel2 = new HBox(10);
        buttonPanel2.setPadding(new Insets(3));
        buttonPanel2.setAlignment(Pos.CENTER_LEFT);        
                // Button Panel
        HBox buttonPanel3 = new HBox(10);
        buttonPanel3.setPadding(new Insets(3));
        buttonPanel3.setAlignment(Pos.CENTER_LEFT);

        Button personalInfoButton = createStyledButton("Register New");
        personalInfoButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder ;-fx-background-color: #4CAF50; -fx-text-fill: white;");
        personalInfoButton.setOnAction(e->{
        personalInfoForm();
        });
        
        Button remarkButton = createStyledButton("Remark");
        remarkButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder ; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        remarkButton.setOnAction(e->{
        employeeRemarkForm();
        });
        
        Button assignLeaveButton = createStyledButton("Assign Leave");
        assignLeaveButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        assignLeaveButton.setOnAction(e->{
        employeeLeaveAssignForm();   
        });
        
        Button requestLeaveButton = createStyledButton("Request Leave");
        requestLeaveButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        requestLeaveButton.setOnAction(e->{
        checkLeaveAvailability();   
        }); 
        
        Button apprisalButton = createStyledButton("Apprisal");
        apprisalButton.setStyle("-fx-font-size: 10px;-fx-font-weight: bolder; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        apprisalButton.setOnAction(e->{
        employeeApprisalForm();
        });
        
        Button backgroundButton = createStyledButton("Background");
        backgroundButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        backgroundButton.setOnAction(e->{
        employeeBackgroundForm();
       });
       
       Button contactInfoButton = createStyledButton("Contact Information");
          contactInfoButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: #4CAF50; -fx-text-fill: white;");
          contactInfoButton.setOnAction(e->{
          employeeContactInfoForm();    
          });
    
           Button employeeFamilyButton = createStyledButton("Employee Family");
          employeeFamilyButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: #4CAF50; -fx-text-fill: white;");
          employeeFamilyButton.setOnAction(e->{
          employeeFamilyForm();  
          });
           
          Button disciplinaryCasesButton = createStyledButton("Disciplinary Cases");
          disciplinaryCasesButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: #4CAF50; -fx-text-fill: white;");
          disciplinaryCasesButton.setOnAction(e->{
          employeeDisciplinaryCasesForm(); 
          });
          
          Button workExperianceButton = createStyledButton("Work Experiance");
          workExperianceButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder ; -fx-background-color: #4CAF50; -fx-text-fill: white;");
          workExperianceButton.setOnAction(e->{
          employeeWorkExperianceForm(); 
          });
          
          Button awardButton = createStyledButton("Award");
          awardButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: #4CAF50; -fx-text-fill: white;");
          awardButton.setOnAction(e->{
          employeeAwardForm();
          });
          
          Button addEmployeeOvertimeButton = createStyledButton("Add Overtime");
          addEmployeeOvertimeButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: teal; -fx-text-fill: white;");
          addEmployeeOvertimeButton.setOnAction(e->{
          showOvertimeForm(null);
          });
          
          Button addEmployeeDeductionButton = createStyledButton("Add Deduction");
          addEmployeeDeductionButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: teal; -fx-text-fill: white;");
          addEmployeeDeductionButton.setOnAction(e->{
          showDeductionForm(null);
          });
          
          Button addEmployeeAllowanceButton = createStyledButton("Add Allowance");
          addEmployeeAllowanceButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: teal; -fx-text-fill: white;");
          addEmployeeAllowanceButton.setOnAction(e->{
          showAllowanceForm(null);
          });
          
          Button addEmployeeCashIndemnityButton = createStyledButton("Add Cash Indemnity");
          addEmployeeCashIndemnityButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: teal; -fx-text-fill: white;");
          addEmployeeCashIndemnityButton.setOnAction(e->{
          showIndemnityForm(null);
          });
          
          Button addEmployeeCostsharingButton = createStyledButton("Add Cost Sharing");
          addEmployeeCostsharingButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: teal; -fx-text-fill: white;");
          addEmployeeCostsharingButton.setOnAction(e->{
          showForm(null);
          });
          
          Button addEmployeeServiceChargeButton = createStyledButton("Add Serive Charge");
          addEmployeeServiceChargeButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: teal; -fx-text-fill: white;");
          addEmployeeServiceChargeButton.setOnAction(e->{
          showServiceChargeForm();
          });
          
          Button requestLoanButton = createStyledButton("Request Loan");
          requestLoanButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bolder; -fx-background-color: black; -fx-text-fill: white;");
          requestLoanButton.setOnAction(e->{
          showLoanForm(null);
          });
            
          Label searchLabel = new Label("Search");
          TextField searchField = new TextField();
          searchField.setPromptText("Search");
         
          HBox search = new HBox();
          search.getChildren().addAll(searchLabel,new Label("   "),searchField);
          
          buttonPanel.getChildren().addAll(personalInfoButton, remarkButton, assignLeaveButton,requestLeaveButton,apprisalButton,backgroundButton,contactInfoButton, employeeFamilyButton,disciplinaryCasesButton,workExperianceButton,awardButton);
          buttonPanel2.getChildren().addAll(addEmployeeOvertimeButton,addEmployeeDeductionButton,addEmployeeAllowanceButton,addEmployeeCashIndemnityButton,addEmployeeCostsharingButton,addEmployeeServiceChargeButton,requestLoanButton);
        
          VBox v= new VBox(10);
          v.getChildren().addAll(buttonPanel,buttonPanel2,search);
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

        TableColumn<User, String> ftCol = new TableColumn<>("FT Number");
        ftCol.setCellValueFactory(new PropertyValueFactory<>("ftNumber"));

        TableColumn<User, String> titleCol = new TableColumn<>("Courtile Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("courseTitle"));

        TableColumn<User, String> fullNameCol = new TableColumn<>("Full Name");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        
        TableColumn<User, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<User, String> salaryscaleCol = new TableColumn<>("Salary Scale");
        salaryscaleCol.setCellValueFactory(new PropertyValueFactory<>("salaryScale"));

        TableColumn<User, String> monthlysalaryCol = new TableColumn<>("Monthly Salary");
        monthlysalaryCol.setCellValueFactory(new PropertyValueFactory<>("monthlySalary"));

        TableColumn<User, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));
        
         TableColumn<User, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        
        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<User, String> employeementCol = new TableColumn<>("Employment");
        employeementCol.setCellValueFactory(new PropertyValueFactory<>("employement"));
        
        TableColumn<User, String> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        
        TableColumn<User, String> serviceyearCol = new TableColumn<>("Service Year");
        serviceyearCol.setCellValueFactory(new PropertyValueFactory<>("serviceYear"));
        
        // Activate/Deactivate column
        TableColumn<User, Void> activateCol = new TableColumn<>("Deactivate Employee");
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
        employeeTable.getColumns().addAll(employeecodeCol,  ftCol, titleCol,fullNameCol, genderCol,salaryscaleCol,monthlysalaryCol, branchCol, deptCol,
                statusCol, employeementCol,ageCol, serviceyearCol,activateCol, updateCol, deleteCol);
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
    
private void personalInfoForm() {
    // Create the main container
    VBox information = new VBox(10);
    information.setAlignment(Pos.TOP_LEFT);
    information.setPadding(new Insets(10));

    // Create all your form components (same as your original code)
    TextField employeeCodeField = new TextField();
    employeeCodeField.setPromptText("Employee Code");
    Label employeeCodeLabel = new Label("Employee Code");
    employeeCodeField.setMaxWidth(120);
    
    TextField ftField = new TextField();
    ftField.setPromptText("FP No");
    Label fpIDLabel = new Label("FP No");
    ftField.setMaxWidth(120);
    
    ComboBox<String> courtesyComboBox = new ComboBox<>();
    courtesyComboBox.getItems().addAll("Mr","Mrs","Prof","Ato", "Sr", "W/ro","W/rt","Dr","HO","Nurse");
    courtesyComboBox.setPromptText("please Select");
    Label courseTitleLabel = new Label("Courtesy title");
    courseTitleLabel.setMaxWidth(120);
    
    TextField fNameField = new TextField();
    fNameField.setPromptText("First Name");
    Label firstNameLabel = new Label("First Name");
    fNameField.setMaxWidth(120);
    
    TextField mNameField = new TextField();
    mNameField.setPromptText("Middle Name");
    Label middleNameLabel = new Label("Middle Name");
    mNameField.setMaxWidth(120);
    
    TextField lNameField = new TextField();
    lNameField.setPromptText("Last Name");
    Label lastNameLabel = new Label("Last Name");
    lNameField.setMaxWidth(120);
    
    ComboBox<String> genderComboBox = new ComboBox<>();
    genderComboBox.getItems().addAll("Male", "Female", "Other");
    genderComboBox.setPromptText("Gender");
    Label genderLabel = new Label("Gender");
    genderComboBox.setMaxWidth(120);

    ComboBox<String> martialComboBox = new ComboBox<>();
    martialComboBox.getItems().addAll("","Single", "Married", "Divorced","Widowed");
    martialComboBox.setPromptText("");
    Label martialStatusLabel = new Label("Martial Status");
    martialComboBox.setMaxWidth(120);
    
    TextField motherNameField = new TextField();
    motherNameField.setPromptText("Mother Name");
    Label motherNameLabel = new Label("Mother Name");
    motherNameField.setMaxWidth(120);
    
    TextField tinNoField = new TextField();
    tinNoField.setPromptText("Tin No");
    Label tinnoLabel = new Label("Tin No");
    tinNoField.setMaxWidth(120);
    
    TextField telePhoneField = new TextField();
    telePhoneField.setPromptText("Telephone");
    Label telLabel = new Label("Telephone");
    telePhoneField.setMaxWidth(120);
     
    TextField mobileField = new TextField();
    mobileField.setPromptText("Mobile");
    Label mobileLabel = new Label("Mobile");
    mobileField.setMaxWidth(120);
    
    TextField emailField = new TextField();
    emailField.setPromptText("Email");
    Label emailLabel = new Label("Email");
    emailField.setMaxWidth(120);
    
    DatePicker dobPicker = new DatePicker();
    dobPicker.setPromptText("Birth Date");
    Label birthDateLabel = new Label("Birth Date");
    dobPicker.setMaxWidth(120);
    
    DatePicker hdPicker = new DatePicker();
    hdPicker.setPromptText("Hire Date");
    Label hireDateLabel = new Label("Hire Date");
    hdPicker.setMaxWidth(120);
    
    RadioButton permanent = new RadioButton("Permanent");
    RadioButton temporary = new RadioButton("Temporary");
    RadioButton contract = new RadioButton("Contract");
    RadioButton probation = new RadioButton("Probation");
    RadioButton intern = new RadioButton("Intern");
    Label employemmentLabel = new Label("Employment Type");
    employemmentLabel.setMaxWidth(120);
    
    ToggleGroup group = new ToggleGroup();
    permanent.setToggleGroup(group);
    temporary.setToggleGroup(group);
    contract.setToggleGroup(group);
    probation.setToggleGroup(group);
    intern.setToggleGroup(group);
    
    TextField contractDurationField = new TextField();
    contractDurationField.setPromptText("Contract Duration");
    Label contractDurationLabel = new Label("Contract Duration");
    contractDurationField.setMaxWidth(120);
    
    TextField probationDurationField = new TextField();
    probationDurationField.setPromptText("Probation Duration");
    Label probationDurationLabel = new Label("Probation Duration");
    probationDurationField.setMaxWidth(120);
    
    TextField internDurationField = new TextField();
    internDurationField.setPromptText("Intern Duration");
    Label internDurationLabel = new Label("Intern Duration");
    internDurationField.setMaxWidth(120);
    
    //This code is used make field accepts only number in javaFx
    UnaryOperator<TextFormatter.Change> filter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("\\d*")) {
            return change;
        }
        return null;
    };
    TextFormatter<String> textFormatter1 = new TextFormatter<>(filter);
    TextFormatter<String> textFormatter2 = new TextFormatter<>(filter);
    TextFormatter<String> textFormatter3 = new TextFormatter<>(filter);
    TextFormatter<String> textFormatter4 = new TextFormatter<>(filter);
    TextFormatter<String> textFormatter5 = new TextFormatter<>(filter);
    
    contractDurationField.setTextFormatter(textFormatter1);
    probationDurationField.setTextFormatter(textFormatter2);
    internDurationField.setTextFormatter(textFormatter3);
    telePhoneField.setTextFormatter(textFormatter4);
    mobileField.setTextFormatter(textFormatter5);
    
    TextField formVacancyField = new TextField();
    formVacancyField.setPromptText("From Vacancy");
    Label formVacancyLabel = new Label("From Vacancy");
    formVacancyField.setMaxWidth(120);
    
    Label photolabel = new Label("Employee Photo");
    ImageView imageView = new ImageView();
    imageView.setFitWidth(150);
    imageView.setFitHeight(150);
    imageView.setPreserveRatio(true);
    imageView.setStyle("-fx-border-color: gray; -fx-border-width: 1px;");

    FileChooser chooser = new FileChooser();
    Button chooseImageButton = new Button("Choose File");
    Button clearFieldsButton = new Button("Clear");
    Button saveButton = new Button("Save");

    chooseImageButton.setOnAction(e -> {
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            // imageView.setImage(new Image(file.toURI().toString()));
        }
    });
    clearFieldsButton.setOnAction(e -> imageView.setImage(null));
    
    VBox v1 = new VBox(5);
    v1.getChildren().addAll(
        employeeCodeLabel, employeeCodeField, 
        fpIDLabel, ftField, 
        courseTitleLabel, courtesyComboBox,
        firstNameLabel, fNameField,
        middleNameLabel, mNameField,
        lastNameLabel, lNameField,
        genderLabel, genderComboBox,
        martialStatusLabel, martialComboBox,
        contractDurationLabel, contractDurationField,
        probationDurationLabel,probationDurationField,
        internDurationLabel,internDurationField
        
    );
    
    VBox v2 = new VBox(5);
    v2.getChildren().addAll(
        motherNameLabel, motherNameField,
        tinnoLabel, tinNoField,
        telLabel, telePhoneField,
        mobileLabel, mobileField,
        emailLabel, emailField,
        birthDateLabel, dobPicker,
        hireDateLabel, hdPicker,
        employemmentLabel, permanent, temporary,contract,probation,intern
    );
    
    VBox v3 = new VBox(5);
    v3.getChildren().addAll(
        formVacancyLabel, formVacancyField,
        photolabel, imageView, chooseImageButton, clearFieldsButton,saveButton
    );
    
    HBox h = new HBox(15);
    h.getChildren().addAll(v1, v2, v3);
    
    information.getChildren().addAll(h);

    // Create a ScrollPane to handle overflow if the form is too large
    ScrollPane scrollPane = new ScrollPane(information);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefSize(800, 600); // Adjust size as needed

    // Create the Alert dialog
    Alert alert = new Alert(Alert.AlertType.NONE);
    alert.setTitle("Add New Employee");
    alert.setHeaderText("Employee Information Form");
     ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
     alert.getButtonTypes().setAll(closeButton);
    
    // Set the content
    alert.getDialogPane().setContent(scrollPane);
    
    // Optional: Add event handler for the save button
    alert.showAndWait();
}   

  private void checkLeaveAvailability(){
      
      /* at this time employee was selected from table and his/her empcode,fullname will be sent to
      database to check leave availablity for that employee.
      e.g empcode  = empcode of selected employee from table.
          fullname = empfullname of selected employee from table.
      else if there is no selected employee from table must alert for that to select employee frist from table.
      */
    VBox leaveInfo = new VBox(10);
    leaveInfo.setAlignment(Pos.TOP_LEFT);
    leaveInfo.setPadding(new Insets(10));
         
    Label employeeCodeLabel = new Label("Employee Code");
    TextField employeeCodeField = new TextField();
    employeeCodeField.setMaxWidth(300);
    
    Label employeeFullNameLabel = new Label("Employee FullName");
    TextField employeeFullNameField = new TextField();
    employeeFullNameField.setMaxWidth(300);
           
    Label leaveTypeLabel = new Label(" Please Select Leave Type ");
    ComboBox<String> leaveTypeComboBox = new ComboBox<>();
    leaveTypeComboBox.getItems().addAll("L1", "l2");      
    leaveTypeComboBox.setMaxWidth(100);
    
    Label yearTypeLabel = new Label("Leave Year. E.G 2010");
    TextField leaveYearField = new TextField();
    leaveYearField.setMaxWidth(100);
    
    Button checkLeaveButton= new Button("Check");
          
    VBox vbox = new VBox(10);
    vbox.getChildren().addAll(employeeCodeLabel ,employeeCodeField,employeeFullNameLabel,employeeFullNameField,leaveTypeLabel,leaveTypeComboBox,yearTypeLabel,leaveYearField,new Label("  "),checkLeaveButton);
         
    VBox hleave = new VBox();
    hleave.getChildren().addAll(vbox);
    leaveInfo.getChildren().addAll(hleave);
        
    // Create a ScrollPane to handle overflow if the form is too large
    ScrollPane scrollPane = new ScrollPane(leaveInfo);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefWidth(500);
 
    Alert alert = new Alert(Alert.AlertType.NONE);
    alert.setTitle(" Leave Availability Form");
    alert.setHeaderText("  Check Leave Availability ");
    ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(closeButton);
    
    checkLeaveButton.setOnAction(e->{
   // leaveNotAvailable();
    employeeLeaveForm();
    alert.close();
    }); 
    // scrollPane.setPrefSize(800, 600); // Adjust size as needed
    
    alert.getDialogPane().setContent(scrollPane);
    alert.showAndWait();
 }
  
  private void leaveNotAvailable(){
    Alert alert = new Alert(Alert.AlertType.NONE);
    alert.setTitle("Unfortunetly This Leave Not Available");
    alert.setHeaderText("Tis leave not available");
    
    // Add custom buttons
    ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(closeButton);
       
    // Optional: Add event handler for the save button
    alert.showAndWait();             
 }
 
  private void employeeLeaveForm(){
    VBox leaveInfo = new VBox(10);
    leaveInfo.setAlignment(Pos.TOP_LEFT);
    leaveInfo.setPadding(new Insets(10));
         
    Label employeeCodeLabel = new Label("Employee code");
    TextField employeeCodeField = new TextField();
    employeeCodeField.setMaxWidth(300);
    
    Label employeeFullNameLabel = new Label("Employee FullName");
    TextField employeeFullNameField = new TextField();
    employeeFullNameField.setMaxWidth(300);
    
    Label leaveYearLabel = new Label("Leave Year E.G 2010");
    TextField leaveYearField = new TextField();
    leaveYearField.setMaxWidth(100);
           
    Label leaveTypeLabel = new Label(" Leave Type ");
    ComboBox<String> leaveTypeComboBox = new ComboBox<>();
    leaveTypeComboBox.getItems().addAll("L1", "l2");      
    leaveTypeComboBox.setMaxWidth(100);
         
    Label halfDayLabel = new Label(" Half Day ");
    CheckBox halfDayCheckBox= new CheckBox();
    HBox hDay = new HBox();
    hDay.getChildren().addAll(halfDayLabel, new Label(" "),halfDayCheckBox);
         
    Label fromDateLabel = new Label("From");
    DatePicker fromDatePicker = new DatePicker();
   
    Label toDateLabel = new Label("To");
    DatePicker toDatePicker = new DatePicker();
    
    Button applyLeave=new Button("Apply Leave");
        
    VBox vdate = new VBox(10);
    vdate.getChildren().addAll(employeeCodeLabel ,employeeCodeField,employeeFullNameLabel,employeeFullNameField,leaveYearLabel,leaveYearField,leaveTypeLabel,leaveTypeComboBox,hDay,fromDateLabel,fromDatePicker,toDateLabel,toDatePicker,new Label("   "),applyLeave);
         
    VBox hleave = new VBox();
    hleave.getChildren().addAll(vdate);
    leaveInfo.getChildren().addAll(hleave);
        
    // Create a ScrollPane to handle overflow if the form is too large
    ScrollPane scrollPane = new ScrollPane(leaveInfo);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefSize(600, 500); // Adjust size as needed

    Alert alert = new Alert(Alert.AlertType.NONE);
    alert.setTitle(" Leave Information Form");
    alert.setHeaderText("  Add Leave ");
    
    ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(cancelButton);
    
    // Set the content
    alert.getDialogPane().setContent(scrollPane);
    
    // Optional: Add event handler for the save button
    alert.showAndWait();             
 }
 

 private void employeeRemarkForm(){
        VBox remark = new VBox(10);
        remark.setAlignment(Pos.TOP_LEFT);
        remark.setPadding(new Insets(10));
        
        
        Label employeeCodeLabel = new Label("Employee Code");
        TextField employeeCodeField = new TextField();
        employeeCodeField.setMaxWidth(100);
           
        TextField liscenceField = new TextField();
        liscenceField.setPromptText("Liscence No");
        Label liscenceLabel = new Label("Liscence No");
         
        TextField bankaccField = new TextField();
        bankaccField.setPromptText("Bank Acc.No");
        Label bankaccLabel = new Label("Bank Acc.No");
        
        ComboBox<String> bankComboBox = new ComboBox<>();
        bankComboBox.getItems().addAll("From DB");
        bankComboBox.setPromptText("Select Bank");
        Label bankLabel = new Label("Bank");
        bankComboBox.setMaxWidth(120);
            
        ComboBox<String> departmentComboBox = new ComboBox<>();
        departmentComboBox.getItems().addAll("From DB");
        departmentComboBox.setPromptText("Select Department");
        Label departmentLabel = new Label("Department");
        departmentComboBox.setMaxWidth(120);  
        
        ComboBox<String> jobPositionComboBox = new ComboBox<>();
        jobPositionComboBox.getItems().addAll("From DB");
        jobPositionComboBox.setPromptText("Select Position");
        Label jobPositionLabel = new Label("Assign Position");
        jobPositionComboBox.setMaxWidth(120);
        
        TextField monthlySalaryField = new TextField();
        monthlySalaryField.setPromptText("Monthly Salary");
        Label monthlySalaryLabel = new Label("Monthly Salary"); 
         
        ComboBox<String> specializationComboBox = new ComboBox<>();
        specializationComboBox.getItems().addAll("From DB");
        specializationComboBox.setPromptText("Specialization");
        Label specializationLabel = new Label("Specialization");
        specializationComboBox.setMaxWidth(120);  
         
        TextField remarkField = new TextField();
        remarkField.setPromptText("Remark");
        Label remarkLabel = new Label("Remark");
        remarkField.setMaxWidth(120); 
        
        Button saveEmployeeButton= new Button("Save");

        VBox v4 = new VBox();
        v4.getChildren().addAll(employeeCodeLabel,employeeCodeField,new Label("   "),liscenceLabel,liscenceField,new Label("   "),bankaccLabel,bankaccField,new Label("   "));
        VBox  v5 = new VBox();
        v5.getChildren().addAll(monthlySalaryLabel,monthlySalaryField,new Label("   "),departmentLabel,departmentComboBox, new Label("   "),bankLabel, bankComboBox);
        VBox v6 = new VBox();
        v6.getChildren().addAll(specializationLabel, specializationComboBox,new Label("   "),jobPositionLabel,jobPositionComboBox,new Label("   "),remarkLabel,remarkField,new Label("  "),saveEmployeeButton);
        
       HBox address= new HBox();
       address.getChildren().addAll(v4,new Label("                    "),v5,new Label("       "),v6);
               
      
        remark.getChildren().addAll(address);
        
    // Create a ScrollPane to handle overflow if the form is too large
    ScrollPane scrollPane = new ScrollPane(remark);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefSize(800, 600); // Adjust size as needed   
    // Create the Alert dialog
    Alert alert = new Alert(Alert.AlertType.NONE);
    alert.setTitle("  Remark  Form");
    alert.setHeaderText("  Add New Remark ");
     ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
     alert.getButtonTypes().setAll(closeButton);
    
    // Set the content
    alert.getDialogPane().setContent(scrollPane);   
    alert.showAndWait();
 
 }
 
  private void employeeLeaveAssignForm(){
    VBox leaveInfo = new VBox(10);
    leaveInfo.setAlignment(Pos.TOP_LEFT);
    leaveInfo.setPadding(new Insets(10));
         
    Label employeeCodeLabel = new Label("Employee code");
    TextField employeeCodeField = new TextField();
    employeeCodeField.setMaxWidth(100);
    
    Label employeeNameLabel = new Label("Employee Name");
    TextField employeeNameField = new TextField();
    employeeNameField.setMaxWidth(300);
    
    Label leaveYearLabel = new Label("Leave Year E.G 2010");
    TextField leaveYearField = new TextField();
    leaveYearField.setMaxWidth(100);
           
    Label leaveTypeLabel = new Label(" Leave Type ");
    ComboBox<String> leaveTypeComboBox = new ComboBox<>();
    leaveTypeComboBox.getItems().addAll("From DB");      
    leaveTypeComboBox.setMaxWidth(100);
    
    Label employeeLeaveDaysLabel = new Label("Number Of Days");
    TextField employeeLeaveDaysField = new TextField();
    employeeLeaveDaysField.setMaxWidth(100);
    
    Button assignLeave=new Button("Assign Leave");
        
    VBox vdate = new VBox(10);
    vdate.getChildren().addAll(employeeCodeLabel ,employeeCodeField,employeeNameLabel,employeeNameField,leaveYearLabel,leaveYearField,leaveTypeLabel,leaveTypeComboBox,employeeLeaveDaysLabel,employeeLeaveDaysField,new Label("   "),assignLeave);
         
    VBox hleave = new VBox();
    hleave.getChildren().addAll(vdate);
    leaveInfo.getChildren().addAll(hleave);
        
    // Create a ScrollPane to handle overflow if the form is too large
    ScrollPane scrollPane = new ScrollPane(leaveInfo);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefSize(800, 600); // Adjust size as needed

    Alert alert = new Alert(Alert.AlertType.NONE);
    alert.setTitle("Leave Information Form");
    alert.setHeaderText("Assign Leave");
    
    ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(cancelButton);
    
    // Set the content
    alert.getDialogPane().setContent(scrollPane);
    
    // Optional: Add event handler for the save button
    alert.showAndWait();             
 }
 
 
 private void employeeApprisalForm(){
 
  VBox apprisalInfo = new VBox(10);
        apprisalInfo.setAlignment(Pos.TOP_LEFT);
        apprisalInfo.setPadding(new Insets(10));
         
        Label employeeIdLabel = new Label("Employee Id");
        TextField employeeId = new TextField();
        employeeId.setMaxWidth(100);
           
        Label evaluationperiodLabel = new Label("Evaluation Period ");
        ComboBox<String> evaluationPeriodComboBox = new ComboBox<>();
        evaluationPeriodComboBox.getItems().addAll("EPeriod1", "EPeriod2","EPeriod3","EPeriod4");
        evaluationPeriodComboBox.setPromptText("");
        evaluationPeriodComboBox.setMaxWidth(200);
        
        Label evaluationTypeLabel = new Label("Evaluation Type ");
        ComboBox<String> evaluationTypeComboBox = new ComboBox<>();
        evaluationTypeComboBox.getItems().addAll("EType1", "EType2","EType3","EType4","EType5");
        evaluationTypeComboBox.setPromptText("");
        evaluationTypeComboBox.setMaxWidth(200);
         
        VBox v11 = new VBox(10);
        v11.getChildren().addAll( employeeIdLabel,employeeId,evaluationperiodLabel,evaluationPeriodComboBox,evaluationTypeLabel,evaluationTypeComboBox);
        Label dateLabel = new Label("Date");
        DatePicker date =  new DatePicker();
         
        Label scoreLabel = new Label("Score");
        TextField scoreField = new TextField();
        scoreField.setPromptText("Score");

        Label averageLabel = new Label("Average");
        TextField averageField = new TextField();
        averageField.setPromptText("");

        Label totalLabel = new Label("Total");
        TextField totalField = new TextField();
        totalField.setPromptText("");

        Button saveApprisal= new Button("Save");
        
        VBox v12 = new VBox(10);
        v12.getChildren().addAll(dateLabel,date,scoreLabel,scoreField,averageLabel,averageField,totalLabel,totalField,saveApprisal);  
        HBox apprisal = new HBox();
        apprisal.getChildren().addAll(v11, new Label("     "), v12);
        apprisalInfo.getChildren().addAll(apprisal);
            // Create a ScrollPane to handle overflow if the form is too large
        ScrollPane scrollPane = new ScrollPane(apprisalInfo);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(800, 600); // Adjust size as needed

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(" Appraisal Information Form");
        alert.setHeaderText("  Add Appraisal ");
    
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(cancelButton);
    
        // Set the content
        alert.getDialogPane().setContent(scrollPane);
         alert.showAndWait();
              
 }
 private void employeeBackgroundForm(){
     VBox backgroundInfo = new VBox(10);
        backgroundInfo.setAlignment(Pos.TOP_LEFT);
        backgroundInfo.setPadding(new Insets(10));

        Label employeeIdLabel = new Label("Employee Id");
        TextField employeeId = new TextField();
        employeeId.setMaxWidth(100);
        
        Label schoolLabel = new Label("Institution Name");
        TextField schoolNameField = new TextField();
        schoolNameField.setMaxWidth(200);
        schoolNameField.setPromptText("Institution Name");
        
        Label fromdatelabel = new Label(" Date From");
        DatePicker fromDateField = new DatePicker();
        fromDateField.setMaxWidth(100);
        
        Label todatelabel = new Label("Date To");
        DatePicker toDateField = new DatePicker();
        toDateField.setMaxWidth(100);
       
        Label majorLabel = new Label("Major");
        TextField majorField = new TextField();
        majorField.setMaxWidth(200);
         
        Label gpaLabel = new Label("GPA");
        TextField gpaField = new TextField();
        gpaField.setMaxWidth(50);
        
        Label eduLabel = new Label(" Education Level ");
        ComboBox<String> eduComboBox = new ComboBox<>();
        eduComboBox.getItems().addAll("Level","Bachelor Degree", "Master Degree","Diploma","Medical Doctor","Ms","Phd");
        eduComboBox.setMaxWidth(100);
        
        Button saveBackground = new Button("Save");
        saveBackground.setPrefWidth(60);  
        saveBackground.setPrefHeight(30);   
           
        VBox v13 = new VBox(10);
        v13.getChildren().addAll(employeeIdLabel,employeeId,schoolLabel,schoolNameField,fromdatelabel,fromDateField,todatelabel,toDateField,majorLabel, majorField,gpaLabel,gpaField,eduLabel,eduComboBox,saveBackground);
        backgroundInfo.getChildren().addAll(v13);
       
       // Create a ScrollPane to handle overflow if the form is too large
       ScrollPane scrollPane = new ScrollPane(backgroundInfo);
       scrollPane.setFitToWidth(true);
       scrollPane.setPrefSize(800, 600); // Adjust size as needed

       Alert alert = new Alert(Alert.AlertType.NONE);
       alert.setTitle(" Background Information Form");
       alert.setHeaderText("  Add Background Information ");
    
       ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
       alert.getButtonTypes().setAll(cancelButton);
    
       // Set the content
       alert.getDialogPane().setContent(scrollPane);
    
       // Optional: Add event handler for the save button
       alert.showAndWait();
 }

private void employeeContactInfoForm(){
        VBox ContactInformation = new VBox(10);
        ContactInformation.setAlignment(Pos.TOP_LEFT);
 
        Label employeeIdLabel = new Label("Employee Id");
        TextField employeeId = new TextField();
        employeeId.setMaxWidth(100);
        
        Label contactLabel = new Label(" Contact Name ");
        TextField contactField = new TextField();
        contactField.setPromptText("Contact Name");

        Label relationShipLabel = new Label(" Relationship ");
        ComboBox<String> relationShipComboBox = new ComboBox<>();
        relationShipComboBox.getItems().addAll("Brother", "Sister","Father","Mother","Uncle","Aunt","Cousin","Friend","Cooworker","Other");
        relationShipComboBox.setPromptText("");
        relationShipComboBox.setMaxWidth(100);
         
        Label occupationLabel = new Label(" Occupation ");
        TextField occupationField = new TextField();
        occupationField.setPromptText("Occupation");
         
         
        Label workPlaceLabel = new Label(" Work Place ");
        TextField workPlaceField = new TextField();
        workPlaceField.setPromptText("Work Place");
             
           
        VBox v7 = new VBox();
        v7.getChildren().addAll( employeeIdLabel,employeeId,contactLabel,contactField,new Label("    "),relationShipLabel,relationShipComboBox,new Label("    "),occupationLabel,occupationField,new Label("    "),workPlaceLabel,workPlaceField,new Label("    "));
        
        Label cityLabel = new Label("City ");
        TextField cityField = new TextField();
        cityField.setPromptText("City");
        
        Label contactSubcityLabel = new Label("Subcity ");
        TextField contactSubcityField = new TextField();
        contactSubcityField.setPromptText("Subcity");
        
        Label contactWoredaLabel = new Label("Woreda");
        TextField contactWoredaField = new TextField();
        contactWoredaField.setPromptText("Woreda");
 
        Label homePhoneLabel = new Label("Home Phone ");
        TextField homePhoneField = new TextField();
        homePhoneField.setPromptText("Home Phone");
        
        Label cellPhoneLabel = new Label("Cell Phone ");
        TextField cellPhoneField = new TextField();
        cellPhoneField.setPromptText("Cell Phone");
        
        Button saveContact = new Button("Save");
        saveContact.setPrefWidth(60);  
        saveContact.setPrefHeight(30);
        
        VBox v8 = new VBox();
        v8.getChildren().addAll(cityLabel,cityField,new Label("   "),contactSubcityLabel,contactSubcityField,new Label("   "),contactWoredaLabel,contactWoredaField,new Label("   "),homePhoneLabel,homePhoneField,new Label("    "),cellPhoneLabel,cellPhoneField,new Label("     "),saveContact);
        
        HBox contactI= new HBox();
        contactI.getChildren().addAll(v7,new Label("                                         "),v8);
       
        ContactInformation.getChildren().addAll(contactI);
        
        // Create a ScrollPane to handle overflow if the form is too large
        ScrollPane scrollPane = new ScrollPane(ContactInformation);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(800, 600); // Adjust size as needed

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(" Contact Iformation Form");
        alert.setHeaderText("  Add Contact Information ");
    
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(cancelButton);
    
        // Set the content
        alert.getDialogPane().setContent(scrollPane);
    
        // Optional: Add event handler for the save button
        alert.showAndWait(); 

}
  private void employeeFamilyForm(){    
        VBox employeeFamily = new VBox(10);
        employeeFamily.setAlignment(Pos.TOP_LEFT);
   
        Label employeeCodeLabel = new Label("Employee Code");
        TextField employeeCodeField = new TextField();
        employeeCodeField.setMaxWidth(100);
        
        Label fNameLabel = new Label("First Name");
        TextField fNameField = new TextField();
        fNameField.setMaxWidth(100);
         
        Label mNameLabel = new Label("Middle Name");
        TextField mNameField = new TextField(); 
        mNameField.setMaxWidth(100);
        
        Label lNameLabel = new Label("Last Name");
        TextField lNameField = new TextField();
        lNameField.setMaxWidth(100);
        
        Label phoneNumberLabel = new Label("Phone Number");
        TextField phoneNumberField = new TextField();
        phoneNumberField.setMaxWidth(100);

        Label genderLabel = new Label("Gender");
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Female", "Male","Other");
        genderComboBox.setMaxWidth(100);
         
        Label educationLevelLabel = new Label("Educational Level");
        TextField educationLevelField = new TextField();
        educationLevelField.setMaxWidth(100);
         
        Label relationshipLabel = new Label("Relationship");
        ComboBox<String> relationshipComboBox = new ComboBox<>();
        relationshipComboBox.getItems().addAll("Brother", "Sister","Father","Mother","Uncle","Aunt","Cousin","Friend","Cooworker","Other");
        relationshipComboBox.setMaxWidth(100);
        
        Label maritalStatusLabel = new Label("Material Status");
        ComboBox<String> maritalStatusComboBox = new ComboBox<>();
        maritalStatusComboBox.getItems().addAll("Married", "Single","Divorced","Widowed","Other");
        maritalStatusComboBox.setMaxWidth(100);
         
        Button saveEmployeeFamily = new Button("Save");
        saveEmployeeFamily.setPrefWidth(60);  
        saveEmployeeFamily.setPrefHeight(30);

        HBox button = new HBox();    
        button.getChildren().addAll(saveEmployeeFamily);
         
        VBox v14 = new VBox();
        v14.getChildren().addAll(employeeCodeLabel,employeeCodeField,fNameLabel,fNameField,new Label(" "),mNameLabel,mNameField,new Label(" "),lNameLabel,lNameField,new Label(" "),phoneNumberLabel,phoneNumberField,new Label(" "),genderLabel,genderComboBox,new Label(" "),educationLevelLabel,educationLevelField,new Label(" "),relationshipLabel,relationshipComboBox,maritalStatusLabel,maritalStatusComboBox,new Label(" "),button);
        employeeFamily.getChildren().addAll(v14);
            
         // Create a ScrollPane to handle overflow if the form is too large
        ScrollPane scrollPane = new ScrollPane(employeeFamily);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(800, 600); // Adjust size as needed
 
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(" EmployeeFamily Information Form");
        alert.setHeaderText("  Add Employee Family Information ");
    
         // Add custom buttons
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(cancelButton);
         
         // Set the content
        alert.getDialogPane().setContent(scrollPane);
         // Optional: Add event handler for the save button
        alert.showAndWait();     
  }   
 
 private void employeeDisciplinaryCasesForm(){
 VBox disciplinaryCases = new VBox(10);
        disciplinaryCases.setAlignment(Pos.TOP_LEFT);
        
        Label employeeCodeLabel = new Label("Employee Code");
        TextField employeeCodeField = new TextField();
        employeeCodeField.setMaxWidth(100);

        Label disciplinaryCasesLabel = new Label("Disciplinary Cases");
        TextArea disciplinaryCasesField = new TextArea();
        disciplinaryCasesField.setMaxWidth(800);
        Label reportdateLabel  = new Label("Report Date");
        DatePicker reportDate = new DatePicker();
        
        Label caseDateLabel  = new Label("Case Date");
        DatePicker caseDate = new DatePicker();
           
        Label reportedByLabel = new Label("Reported By");
        TextField reportedByField = new TextField();
        reportedByField.setMaxWidth(100);
    
        Button saveCaseButton = new Button("Save");
        saveCaseButton.setPrefWidth(60);  
        saveCaseButton.setPrefHeight(30);
         
        HBox casebutton = new HBox();    
        casebutton.getChildren().addAll(disciplinaryCasesLabel,saveCaseButton);
        
        VBox v15 = new VBox();
        v15.getChildren().addAll(employeeCodeLabel,employeeCodeField,disciplinaryCasesLabel,disciplinaryCasesField,reportdateLabel,reportDate,caseDateLabel,caseDate,reportedByLabel,reportedByField,new Label("  "),casebutton);

        disciplinaryCases.getChildren().addAll(v15);
              
        // Create a ScrollPane to handle overflow if the form is too large
        ScrollPane scrollPane = new ScrollPane(disciplinaryCases);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(800, 600); // Adjust size as needed

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Contact Disciplinary Cases Form");
        alert.setHeaderText("Add Contact Information");
    
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(cancelButton);
    
    // Set the content
    alert.getDialogPane().setContent(scrollPane);
    
        // Optional: Add event handler for the save button
    alert.showAndWait(); 
 }
 private void  employeeWorkExperianceForm(){
 
  VBox WorkEx = new VBox(10);
        WorkEx.setAlignment(Pos.TOP_LEFT);
        
        Label employeeIdLabel = new Label("Employee Id");
        TextField employeeId = new TextField();
        employeeId.setMaxWidth(100);
       
        Label companyLabel = new Label("Company Name");
        TextField companyField = new TextField();
        companyField.setMaxWidth(100);
        
        Label jobPositionLabel = new Label("Job Position");
        TextField jobPositionField = new TextField();
        jobPositionField.setMaxWidth(100);

        Label startDateLabel  = new Label("Start Date");
        DatePicker startDate = new DatePicker();

        Label endDateLabel  = new Label("End Date");
        DatePicker endDate = new DatePicker();

        Button saveWorkExpButton = new Button("Save");
        saveWorkExpButton.setPrefWidth(60);  
        saveWorkExpButton.setPrefHeight(30); 
         
        HBox workbutton = new HBox();    
        workbutton.getChildren().addAll(companyLabel,companyField,new Label("  "));
         
        VBox v16 = new VBox();
        v16.getChildren().addAll( employeeIdLabel,employeeId,companyLabel,companyField,jobPositionLabel,jobPositionField,startDateLabel,startDate,endDateLabel,endDate ,saveWorkExpButton);
        
        WorkEx.getChildren().addAll(v16);
        
        // Create a ScrollPane to handle overflow if the form is too large
        ScrollPane scrollPane = new ScrollPane(WorkEx);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(800, 600); // Adjust size as needed

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(" Work Expireance Iformation Form");
        alert.setHeaderText("  Add Work Expireance Information ");
    
        // Add custom buttons
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(cancelButton);
    
        // Set the content
        alert.getDialogPane().setContent(scrollPane);
    
        // Optional: Add event handler for the save button
        alert.showAndWait(); 
 
 } 
 private void employeeAwardForm()
 {  VBox award = new VBox(10);
        award.setAlignment(Pos.TOP_LEFT);
        
        Label employeeCodeLabel = new Label("Employee Code");
        TextField employeeCodeField = new TextField();
        employeeCodeField.setMaxWidth(100);

        Label awardLabel = new  Label("Award");
        TextArea awardField = new TextArea();
        awardField.setMaxWidth(300);
        
        Label companyLabel = new  Label("From Which Company");
        TextField companyField = new TextField();
        companyField.setMaxWidth(400);
        
        Label awardDateLabel = new Label("Award Date");
        DatePicker awardDate = new DatePicker();

        Label remarkLabel = new Label("Remark");
        TextArea remarkField = new TextArea();
        remarkField.setMaxWidth(300);
       
         Button saveAwardButton = new Button("Save");
         saveAwardButton.setPrefWidth(60);  
         saveAwardButton.setPrefHeight(30);  
         
         VBox v17 = new VBox();
         v17.getChildren().addAll(employeeCodeLabel,employeeCodeField,awardLabel,awardField,companyLabel,companyField,awardDateLabel,awardDate,remarkLabel,remarkField,new Label("  "),saveAwardButton);  
        award.getChildren().addAll(v17);
                 
  // Create a ScrollPane to handle overflow if the form is too large
    ScrollPane scrollPane = new ScrollPane(award);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefSize(800, 600); // Adjust size as needed

Alert alert = new Alert(Alert.AlertType.NONE);
    alert.setTitle(" Award Iformation Form");
    alert.setHeaderText("  Add Award Information ");
    
    // Add custom buttons
    ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    alert.getButtonTypes().setAll(cancelButton);
    
    // Set the content
    alert.getDialogPane().setContent(scrollPane);
        // Optional: Add event handler for the save button
    alert.showAndWait(); 
      }
 
     private void showOvertimeForm(EmployeeOvertimeRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Overtime Form");
        dialog.setHeaderText(record == null ? "Add New Overtime" : "Edit Overtime");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        TextField empCodeField = new TextField();
        empCodeField.setPromptText("Employee Code");
        TextField empNameField = new TextField();
        empNameField.setPromptText("Employee Name");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        TextField startTimeField = new TextField("18:00");
        TextField endTimeField = new TextField("20:00");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Weekday", "Weekend", "Holiday");
        typeCombo.setValue("Weekday");
        
        ComboBox<String> payrollPeriodCombo = new ComboBox<>();
        payrollPeriodCombo.getItems().addAll("Payroll Period1", "Payroll Period2", "Payroll Period3");
        payrollPeriodCombo.setValue("Payroll Period1");

        form.addRow(0, new Label("Employee Code:"), empCodeField);
        form.addRow(1, new Label("Name:"), empNameField);
        form.addRow(2, new Label("Date:"), datePicker);
        form.addRow(3, new Label("Start Time:"), startTimeField);
        form.addRow(4, new Label("End Time:"), endTimeField);
        form.addRow(5, new Label("OT Type:"), typeCombo);
        form.addRow(6, new Label("Payroll Period:"), payrollPeriodCombo);

        dialog.getDialogPane().setContent(form);

        if (record != null) {
            empCodeField.setText(record.getEmployeeCode());
            empNameField.setText(record.getEmployeeName());
            datePicker.setValue(record.getDate());
            startTimeField.setText(record.getStartTime().toString());
            endTimeField.setText(record.getEndTime().toString());
            typeCombo.setValue(record.getOvertimeType());
            payrollPeriodCombo.setValue(record.getPayrollPeriod());
        }

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                LocalTime start = LocalTime.parse(startTimeField.getText());
                LocalTime end = LocalTime.parse(endTimeField.getText());
                double hours = java.time.Duration.between(start, end).toHours();

                if (record == null) {
                    overtimeData.add(new EmployeeOvertimeRecordModel(
                        empCodeField.getText(),
                        empNameField.getText(),
                        datePicker.getValue(),
                        start,
                        end,
                        hours,
                        typeCombo.getValue(),
                        payrollPeriodCombo.getValue(),
                        "Pending",
                        ""
                    ));
                } else {
                    record.setEmployeeCode(empCodeField.getText());
                    record.setEmployeeName(empNameField.getText());
                    record.setDate(datePicker.getValue());
                    record.setStartTime(start);
                    record.setEndTime(end);
                    record.setHours(hours);
                    record.setPayrollPeriod(payrollPeriodCombo.getValue());
                    record.setOvertimeType(typeCombo.getValue());
                }
                overtimeTable.refresh();
            }
        });
    }
     
        private void showDeductionForm(EmployeeDeductionRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(record == null ? "Add Deduction" : "Edit Deduction");
        dialog.setHeaderText(record == null ? "Add New Deduction" : "Edit Deduction Record");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Simple form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField empIdField = new TextField(record == null ? "" : record.getEmployeeId());
        TextField nameField = new TextField(record == null ? "" : record.getEmployeeName());
        TextField deptField = new TextField(record == null ? "" : record.getDepartment());

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Tax", "Pension", "Loan", "Penalty", "Insurance", "Other");
        typeBox.setValue(record == null ? "Tax" : record.getDeductionType());

        TextField amountField = new TextField(record == null ? "" : String.valueOf(record.getAmount()));
        DatePicker startDate = new DatePicker(record == null ? LocalDate.now() : record.getStartDate());
        DatePicker endDate = new DatePicker(record == null ? LocalDate.now().plusMonths(1) : record.getEndDate());

        grid.addRow(0, new Label("Employee ID:"), empIdField);
        grid.addRow(1, new Label("Name:"), nameField);
        grid.addRow(2, new Label("Department:"), deptField);
        grid.addRow(3, new Label("Deduction Type:"), typeBox);
        grid.addRow(4, new Label("Amount:"), amountField);
        grid.addRow(5, new Label("Start Date:"), startDate);
        grid.addRow(6, new Label("End Date:"), endDate);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (record == null) {
                    EmployeeDeductionRecordModel newRecord = new EmployeeDeductionRecordModel(
                            empIdField.getText(),
                            nameField.getText(),
                            deptField.getText(),
                            typeBox.getValue(),
                            Double.parseDouble(amountField.getText()),
                            startDate.getValue(),
                            endDate.getValue(),
                            "Pending",
                            ""
                    );
                    deductionData.add(newRecord);
                } else {
                    record.setEmployeeId(empIdField.getText());
                    record.setEmployeeName(nameField.getText());
                    record.setDepartment(deptField.getText());
                    record.setDeductionType(typeBox.getValue());
                    record.setAmount(Double.parseDouble(amountField.getText()));
                    record.setStartDate(startDate.getValue());
                    record.setEndDate(endDate.getValue());
                }
                deductionTable.refresh();
            }
        });
    }
         
             private void showAllowanceForm(EmployeeAllowanceRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(record == null ? "Add Allowance" : "Edit Allowance");
        dialog.setHeaderText(record == null ? "Add New Allowance" : "Edit Allowance Record");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField empCodeField = new TextField(record == null ? "" : record.getEmployeeCode());
        TextField empNameField = new TextField(record == null ? "" : record.getEmployeeName());
        TextField empDeptField = new TextField(record == null ? "" : record.getEmployeeDepartment());

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Housing", "Transport", "Meal", "Medical", "Internet", "Other");
        typeBox.setValue(record == null ? "Housing" : record.getAllowanceType());

        TextField amountField = new TextField(record == null ? "" : String.valueOf(record.getAmount()));
        DatePicker startDate = new DatePicker(record == null ? LocalDate.now() : record.getStartDate());
        DatePicker endDate = new DatePicker(record == null ? LocalDate.now().plusMonths(1) : record.getEndDate());

        grid.addRow(0, new Label("Employee ID:"), empCodeField);
        grid.addRow(1, new Label("Name:"), empNameField);
        grid.addRow(2, new Label("Department:"), empDeptField);
        grid.addRow(3, new Label("Allowance Type:"), typeBox);
        grid.addRow(4, new Label("Amount:"), amountField);
        grid.addRow(5, new Label("Start Date:"), startDate);
        grid.addRow(6, new Label("End Date:"), endDate);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (record == null) {
                    EmployeeAllowanceRecordModel newRecord = new EmployeeAllowanceRecordModel(
                            empCodeField.getText(),
                            empNameField.getText(),
                            empDeptField.getText(),
                            typeBox.getValue(),
                            Double.parseDouble(amountField.getText()),
                            startDate.getValue(),
                            endDate.getValue(),
                            "Pending",
                            ""
                    );
                    allowanceData.add(newRecord);
                } else {
                    record.setEmployeeCode(empCodeField.getText());
                    record.setEmployeeName(empNameField.getText());
                    record.setEmployeeDepartment(empDeptField.getText());
                    record.setAllowanceType(typeBox.getValue());
                    record.setAmount(Double.parseDouble(amountField.getText()));
                    record.setStartDate(startDate.getValue());
                    record.setEndDate(endDate.getValue());
                }
                allowanceTable.refresh();
            }
        });
    }
     private void showIndemnityForm(EmployeeCashIndemnityRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(record == null ? "Add Cash Indemnity" : "Edit Cash Indemnity");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        TextField empCodeField = new TextField();
        empCodeField.setPromptText("Employee Code");

        TextField empNameField = new TextField();
        empNameField.setPromptText("Employee Name");

        TextField reasonField = new TextField();
        reasonField.setPromptText("Reason");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Pending", "Approved", "Rejected");
        statusCombo.setValue("Pending");

        grid.add(new Label("Employee ID:"), 0, 0);
        grid.add(empCodeField, 1, 0);
        grid.add(new Label("Employee Name:"), 0, 1);
        grid.add(empNameField, 1, 1);
        grid.add(new Label("Reason:"), 0, 2);
        grid.add(reasonField, 1, 2);
        grid.add(new Label("Amount:"), 0, 3);
        grid.add(amountField, 1, 3);
        grid.add(new Label("Date:"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusCombo, 1, 5);

        if (record != null) {
            empCodeField.setText(record.getEmployeeCode());
            empNameField.setText(record.getEmployeeName());
            reasonField.setText(record.getReason());
            amountField.setText(String.valueOf(record.getAmount()));
            datePicker.setValue(record.getDate());
            statusCombo.setValue(record.getStatus());
        }

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                double amount = 0;
                try {
                    amount = Double.parseDouble(amountField.getText());
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid amount entered", ButtonType.OK);
                    alert.showAndWait();
                    return;
                }

                if (record == null) {
                    indemnityData.add(new EmployeeCashIndemnityRecordModel(
                            empCodeField.getText(),
                            empNameField.getText(),
                            reasonField.getText(),
                            amount,
                            datePicker.getValue(),
                            statusCombo.getValue(),
                            ""
                    ));
                } else {
                    record.setEmployeeCode(empCodeField.getText());
                    record.setEmployeeName(empNameField.getText());
                    record.setReason(reasonField.getText());
                    record.setAmount(amount);
                    record.setDate(datePicker.getValue());
                    record.setStatus(statusCombo.getValue());
                    indemnityTable.refresh();
                }
            }
        });
    }
     
      private void showForm(EmployeeCostSharingRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(record == null ? "Add Cost Sharing" : "Edit Cost Sharing");

        // Fields
        TextField empCodeField = new TextField();
        empCodeField.setPromptText("Employee Code");

        TextField empNameField = new TextField();
        empNameField.setPromptText("Employee Name");

        TextField costTypeField = new TextField();
        costTypeField.setPromptText("Cost Type");

        TextField totalCostField = new TextField();
        totalCostField.setPromptText("Total Cost");

        TextField employeeShareField = new TextField();
        employeeShareField.setPromptText("Employee Share");

        TextField employerShareField = new TextField();
        employerShareField.setPromptText("Employer Share");

        if (record != null) {
            empCodeField.setText(record.getEmployeeCode());
            empNameField.setText(record.getEmployeeName());
            costTypeField.setText(record.getCostType());
            totalCostField.setText(String.valueOf(record.getTotalCost()));
            employeeShareField.setText(String.valueOf(record.getEmployeeShare()));
            employerShareField.setText(String.valueOf(record.getEmployerShare()));
        }

        VBox formLayout = new VBox(10, new Label("Employee Code:"), empCodeField,
                new Label("Employee Name:"), empNameField,
                new Label("Cost Type:"), costTypeField,
                new Label("Total Cost:"), totalCostField,
                new Label("Employee Share:"), employeeShareField,
                new Label("Employer Share:"), employerShareField);
        formLayout.setPadding(new Insets(15));

        dialog.getDialogPane().setContent(formLayout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    double total = Double.parseDouble(totalCostField.getText());
                    double empShare = Double.parseDouble(employeeShareField.getText());
                    double employerShare = Double.parseDouble(employerShareField.getText());

                    if (record == null) {
                        data.add(new EmployeeCostSharingRecordModel(
                                empCodeField.getText(),
                                empNameField.getText(),
                                costTypeField.getText(),
                                total, empShare, employerShare
                        ));
                        showAlert("Cost sharing record added!");
                    } else {
                        record.setEmployeeCode(empCodeField.getText());
                        record.setEmployeeName(empNameField.getText());
                        record.setCostType(costTypeField.getText());
                        record.setTotalCost(total);
                        record.setEmployeeShare(empShare);
                        record.setEmployerShare(employerShare);
                        costSharingTable.refresh();
                        showAlert("Cost sharing record updated!");
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Please enter valid numbers for cost fields.");
                }
            }
        });
    }
        private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
        
    private void showServiceChargeForm() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Service Charge Form");
        dialog.setHeaderText("Add New Service Charge");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox form = new VBox(10);
        form.setPadding(new Insets(10));

        TextField empCodeField = new TextField();
        empCodeField.setPromptText("Employee Code");

        TextField empNameField = new TextField();
        empNameField.setPromptText("Employee Name");

        TextField empDeptField = new TextField();
        empDeptField.setPromptText("Department");

        TextField chargeTypeField = new TextField();
        chargeTypeField.setPromptText("Charge Type");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");

        DatePicker applicableDatePicker = new DatePicker(LocalDate.now());

        form.getChildren().addAll(new Label("Employee Code:"), empCodeField,
                                  new Label("Employee Name:"), empNameField,
                                  new Label("Employee Department:"), empDeptField,
                                  new Label("Charge Type:"), chargeTypeField,
                                  new Label("Amount:"), amountField,
                                  new Label("Applicable Date:"), applicableDatePicker);

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                EmployeeServiceChargeRecordModel record = new EmployeeServiceChargeRecordModel(
                        empCodeField.getText(),
                        empNameField.getText(),
                        empDeptField.getText(),
                        chargeTypeField.getText(),
                        Double.parseDouble(amountField.getText()),
                        applicableDatePicker.getValue(),
                        "Pending",
                        ""
                );
                serviceChargeData.add(record);
            }
        });
    }
    
        private void showLoanForm(EmployeeLoanRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(record == null ? "Add Loan Request" : "Edit Loan Request");
        dialog.setHeaderText(record == null ? "Add New Loan Request" : "Edit Loan Request");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        TextField empCodeField = new TextField(record == null ? "" : record.getEmployeeCode());
        TextField empNameField = new TextField(record == null ? "" : record.getEmployeeName());
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Personal", "Education", "Housing", "Medical");
        typeBox.setValue(record == null ? "Personal" : record.getLoanType());

        TextField amountField = new TextField(record == null ? "" : String.valueOf(record.getAmount()));
        DatePicker requestDate = new DatePicker(record == null ? LocalDate.now() : record.getRequestDate());

        grid.addRow(0, new Label("Employee Code:"), empCodeField);
        grid.addRow(1, new Label("Employee Name:"), empNameField);
        grid.addRow(2, new Label("Loan Type:"), typeBox);
        grid.addRow(3, new Label("Amount:"), amountField);
        grid.addRow(4, new Label("Request Date:"), requestDate);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (record == null) {
                    EmployeeLoanRecordModel newRecord = new EmployeeLoanRecordModel(
                            "REQ" + (loanData.size() + 1),
                            empCodeField.getText(),
                            empNameField.getText(),
                            typeBox.getValue(),
                            Double.parseDouble(amountField.getText()),
                            requestDate.getValue(),
                            "Pending",
                            ""
                    );
                    loanData.add(newRecord);
                } else {
                    record.setEmployeeCode(empCodeField.getText());
                    record.setEmployeeName(empNameField.getText());
                    record.setLoanType(typeBox.getValue());
                    record.setAmount(Double.parseDouble(amountField.getText()));
                    record.setRequestDate(requestDate.getValue());
                }
                loanTable.refresh();
            }
        });
    }
}
