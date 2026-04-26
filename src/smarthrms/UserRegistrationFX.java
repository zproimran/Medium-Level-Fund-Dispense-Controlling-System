package smarthrms;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserRegistrationFX {
    private Stage stage;
    private ZKTECOFX mainApp;
    
    private TextField fullNameField;
    private TextField emailField;
    private ComboBox<String> roleComboBox;
    private ComboBox<String> deptComboBox;
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private CheckBox showPasswordCheckBox;
    private Button submitButton;
    private Button captureFingerprintButton;
    private Label fingerprintStatusLabel;
    
    private byte[] fingerprintTemplate = null;

   // Updated constructor to accept fingerprint template and initialize properly
public UserRegistrationFX(ZKTECOFX mainApp, byte[] fingerprintTemplate) {
    this.mainApp = mainApp;
    this.fingerprintTemplate = fingerprintTemplate;
    initializeUI();
    
    // Check if we received a template and update UI accordingly
    Platform.runLater(() -> {
        if (this.fingerprintTemplate != null && this.fingerprintTemplate.length > 0) {
            updateFingerprintStatus(true, "✅ Using recently enrolled fingerprint (" + this.fingerprintTemplate.length + " bytes)");
        } else {
            updateFingerprintStatus(false, "No fingerprint template available");
        }
    });
}

// Add this helper method to update fingerprint status
private void updateFingerprintStatus(boolean success, String message) {
    fingerprintStatusLabel.setText(message);
    if (success) {
        fingerprintStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
    } else {
        fingerprintStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }
}

    private void initializeUI() {
        stage = new Stage();
        stage.setTitle("User Registration - Smart HRMS");
        
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #ecf0f1, #bdc3c7);");
        
        // Title
        Label titleLabel = new Label("Register New User");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);
        
        // Form
        GridPane formGrid = new GridPane();
        formGrid.setVgap(15);
        formGrid.setHgap(15);
        formGrid.setPadding(new Insets(20));
        formGrid.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 10;");
        
        // Form fields
        fullNameField = createTextField("Full Name");
        emailField = createTextField("Email");
        roleComboBox = createComboBox(new String[]{"FinanceAdmin", "Cashier", "Accountant","Admin","HRManager","ReplenishDispenser"});
        Connecting con=new Connecting();
// Fetch departments from DB
String[] dept = con.getDepartmentsFromDatabase(); 

if (dept != null && dept.length > 0) {
    ObservableList<String> deptList = FXCollections.observableArrayList(dept);
    deptComboBox = new ComboBox<>(deptList);  // Now it's OK
    deptComboBox.setValue(dept[0]); // Set default selected value
}
else {
    // Handle empty case
    deptComboBox = new ComboBox<>();
    deptComboBox.setPromptText("No departments found");
}
        usernameField = createTextField("Username");
        passwordField = new PasswordField();
        confirmPasswordField = new PasswordField();
        
        
        // Show password checkbox
        showPasswordCheckBox = new CheckBox("Show Passwords");
        showPasswordCheckBox.setOnAction(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setPromptText(passwordField.getText());
                confirmPasswordField.setPromptText(confirmPasswordField.getText());
                passwordField.clear();
                confirmPasswordField.clear();
            } else {
                passwordField.setText(passwordField.getPromptText());
                confirmPasswordField.setText(confirmPasswordField.getPromptText());
                passwordField.setPromptText("Password");
                confirmPasswordField.setPromptText("Confirm Password");
            }
        });
        
        // Fingerprint section
        captureFingerprintButton = new Button("Capture Fingerprint");
        captureFingerprintButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        captureFingerprintButton.setOnAction(e -> captureFingerprint());
        
        fingerprintStatusLabel = new Label("No fingerprint captured");
        fingerprintStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        
        // Submit button
        submitButton = new Button("Register User");
        submitButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
        submitButton.setPrefSize(200, 40);
        submitButton.setOnAction(e -> submitRegistration());
        
        // Add components to grid
        formGrid.add(createLabel("Full Name:*"), 0, 0);
        formGrid.add(fullNameField, 1, 0);
        formGrid.add(createLabel("Email:*"), 0, 1);
        formGrid.add(emailField, 1, 1);
        formGrid.add(createLabel("Role:*"), 0, 2);
        formGrid.add(roleComboBox, 1, 2);
        formGrid.add(createLabel("Department:*"), 0, 3);
        formGrid.add(deptComboBox, 1, 3);
        formGrid.add(createLabel("Username:*"), 0, 4);
        formGrid.add(usernameField, 1, 4);
        formGrid.add(createLabel("Password:*"), 0, 5);
        formGrid.add(passwordField, 1, 5);
        formGrid.add(createLabel("Confirm Password:*"), 0, 6);
        formGrid.add(confirmPasswordField, 1, 6);
        formGrid.add(showPasswordCheckBox, 1, 7);
        
        // Fingerprint section
        formGrid.add(createLabel("Fingerprint:"), 0, 8);
        VBox fingerprintBox = new VBox(10);
        fingerprintBox.getChildren().addAll(captureFingerprintButton, fingerprintStatusLabel);
        formGrid.add(fingerprintBox, 1, 8);
        
        // Submit button
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(submitButton);
        formGrid.add(buttonBox, 0, 9, 2, 1);
        
        mainLayout.getChildren().addAll(titleLabel, formGrid);
        
        Scene scene = new Scene(mainLayout, 500, 700);
        stage.setScene(scene);
        stage.setResizable(false);
    }
    
    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        return label;
    }
    
    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-background-radius: 5;");
        field.setPrefHeight(35);
        return field;
    }
    
    private ComboBox<String> createComboBox(String[] items) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(items);
        combo.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-background-radius: 5;");
        combo.setPrefHeight(35);
        combo.setPrefWidth(200);
        if (items.length > 0) {
            combo.setValue(items[0]);
        }
        return combo;
    }
    
// In the captureFingerprint method, update the success message:
private void captureFingerprint() {
    if (fingerprintTemplate != null && fingerprintTemplate.length > 0) {
        updateFingerprintStatus(true, "✅ Binary fingerprint ready (" + fingerprintTemplate.length + " bytes)");
        showAlert("Fingerprint", "Binary fingerprint template ready for registration.");
        return;
    }
    
    byte[] recentTemplate = mainApp.getCurrentEnrollmentTemplate();
    if (recentTemplate != null && recentTemplate.length > 0) {
        fingerprintTemplate = recentTemplate;
        updateFingerprintStatus(true, "✅ Binary fingerprint ready (" + fingerprintTemplate.length + " bytes)");
        showAlert("Fingerprint", "Binary fingerprint template ready for registration.");
    } else {
        showAlert("Fingerprint Capture", 
            "Please enroll a fingerprint first using the main application's 'Enroll Fingerprint' feature.\n\n" +
            "Steps:\n" +
            "1. Click 'Enroll Fingerprint' in main window\n" +
            "2. Complete the 3-step enrollment\n" +
            "3. Return to this registration form\n" +
            "4. Click 'Capture Fingerprint' to use the enrolled template");
    }
}
    
private void submitRegistration() {
    String fullName = fullNameField.getText().trim();
    String email = emailField.getText().trim();
    String role = roleComboBox.getValue();
    String department = deptComboBox.getValue();
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();
    String confirmPassword = confirmPasswordField.getText().trim();
    
    // Validation
    if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
        showAlert("Validation Error", "Please fill in all required fields.");
        return;
    }
    
    if (!isValidEmail(email)) {
        showAlert("Validation Error", "Please enter a valid email address.");
        return;
    }
    
    if (!password.equals(confirmPassword)) {
        showAlert("Validation Error", "Passwords do not match.");
        return;
    }
       
    // Enhanced fingerprint validation with debugging info
    if (fingerprintTemplate == null) {
        showAlert("Validation Error", 
            "No fingerprint template available.\n\n" +
            "Please click 'Capture Fingerprint' to use the recently enrolled fingerprint.");
        return;
    }
    
    if (fingerprintTemplate.length == 0) {
        showAlert("Validation Error", 
            "Fingerprint template is empty (0 bytes).\n" +
            "Please enroll a fingerprint first.");
        return;
    }
     // ✅ Hash password before saving
                String hashedPassword = PasswordUtils.hashPassword(password);
    
    // In the submitRegistration method, update the confirmation dialog:
Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
confirmAlert.setTitle("Confirm Registration");
confirmAlert.setHeaderText("Register User with Binary Fingerprint");
confirmAlert.setContentText("Are you sure you want to register this user?\n\n" +
    "Name: " + fullName + "\n" +
    "Email: " + email + "\n" +
    "Role: " + role + "\n" +
    "Department: " + department + "\n" +
    "Username: " + username + "\n" +
    "Fingerprint: Binary data (" + fingerprintTemplate.length + " bytes)");
    
    confirmAlert.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            // Register user with fingerprint
            mainApp.registerUserWithFingerprint(fullName, email, role, department, username, hashedPassword, fingerprintTemplate);
            stage.close();
        }
    });
}


    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void show() {
        stage.show();
    }
}