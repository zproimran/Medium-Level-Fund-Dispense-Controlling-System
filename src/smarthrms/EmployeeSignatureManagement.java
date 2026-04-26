package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.print.PrinterJob;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import java.awt.image.BufferedImage;
import javafx.scene.Node;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EmployeeSignatureManagement extends BorderPane {
    private String currentUser;
    private TableView<EmployeeSignatureModel> employeeTable;
    private ObservableList<EmployeeSignatureModel> employeeData = FXCollections.observableArrayList();
    private Connecting con;
    private ZKTECO fingerprintModule;
    boolean isAdmin=false;
    boolean isFinanceAdmin=false;

    // UI Components
    private Button enrollFingerprintBtn, captureSignatureBtn, updateEmployeeBtn, deleteEmployeeBtn;
    private ComboBox<String> departmentField;
    private Button exportBtn, printBtn, refreshBtn, connectDeviceBtn, disconnectDeviceBtn;
    private Button loadSignatureByFingerprintBtn, updateSignatureByFingerprintBtn;
    private Label deviceStatusLabel, operationStatusLabel;

    public EmployeeSignatureManagement(String username) {
        this.currentUser=username;
        con = new Connecting();
        fingerprintModule = new ZKTECO();
        isAdmin=con.isAdmin(currentUser.toLowerCase());
        isFinanceAdmin=con.isFinanceAdmin(currentUser.toLowerCase());
        initializeUI();
        loadDataFromDatabase();
        initializeDeviceStatus();
    }

    private void initializeUI() {
        VBox header = createHeader();
        HBox footer = createFooter();
        VBox buttonPanel = createButtonPanel();
        VBox tableSection = createTableSection();

        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));
        mainContent.getChildren().addAll(buttonPanel, tableSection);
        
        this.setTop(header);
        this.setCenter(mainContent);
        this.setBottom(footer);
        this.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f6fa, #dcdde1);");
    }

    private VBox createHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #3498db); -fx-padding: 20; -fx-alignment: center;");
        
        Text hospitalName = new Text("AFRAN GENERAL HOSPITAL");
        hospitalName.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        hospitalName.setFill(Color.WHITE);
        
        Text departmentName = new Text("Employee Biometric & Signature Management System");
        departmentName.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        departmentName.setFill(Color.LIGHTBLUE);
        
        Text currentDate = new Text("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        currentDate.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        currentDate.setFill(Color.WHITE);
        
        VBox textContainer = new VBox(8);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.getChildren().addAll(hospitalName, departmentName, currentDate);
        
        HBox headerContainer = new HBox(20);
        headerContainer.setAlignment(Pos.CENTER);
        
        StackPane biometricIcon = new StackPane();
        biometricIcon.setPrefSize(80, 80);
        biometricIcon.setStyle("-fx-background-color: white; -fx-background-radius: 40; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 40;");
        
        Text iconText = new Text("BIO");
        iconText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        iconText.setFill(Color.DARKBLUE);
        biometricIcon.getChildren().add(iconText);
        
        headerContainer.getChildren().addAll(biometricIcon, textContainer);
        header.getChildren().add(headerContainer);
        
        return header;
    }

    private HBox createFooter() {
        HBox footer = new HBox(10);
        footer.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #34495e); -fx-padding: 15; -fx-alignment: center;");
        
        deviceStatusLabel = new Label("Device Status: Not Connected");
        deviceStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        
        operationStatusLabel = new Label("Ready for operations");
        operationStatusLabel.setStyle("-fx-text-fill: #ecf0f1;");
        
        Text footerText = new Text("© 2024 Afran General Hospital - Smart HRMS | Employee Biometric System v3.0");
        footerText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        footerText.setFill(Color.LIGHTGRAY);
        
        footer.getChildren().addAll(deviceStatusLabel, new Separator(), operationStatusLabel, new Separator(), footerText);
        return footer;
    }

    private VBox createButtonPanel() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(15));
        buttonPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
        buttonPanel.setAlignment(Pos.CENTER_LEFT);

        // Create buttons
        Button addEmployeeBtn = createStyledButton("Add Emp", "#27ae60", e -> showAddEmployeeForm());
        updateEmployeeBtn = createStyledButton("Update Emp", "#2980b9", e -> updateSelectedEmployee());
        deleteEmployeeBtn = createStyledButton("Delete Emp", "#e74c3c", e -> deleteSelectedEmployee());
        
        enrollFingerprintBtn = createStyledButton("Enroll Fingerprint", "#9b59b6", e -> enrollFingerprintForSelected());
        captureSignatureBtn = createStyledButton("Capture Signature", "#16a085", e -> showSignatureCaptureOptions());
        
        loadSignatureByFingerprintBtn = createStyledButton("Load Signature", "#8e44ad", e -> loadSignatureByFingerprint());
        updateSignatureByFingerprintBtn = createStyledButton("Update Signature", "#d35400", e -> showSignatureUpdateOptions());
        
        connectDeviceBtn = createStyledButton("Connect", "#3498db", e -> connectFingerprintDevice());
        disconnectDeviceBtn = createStyledButton("Disconnect", "#e74c3c", e -> disconnectFingerprintDevice());
        
        exportBtn = createStyledButton("Export Data", "#27ae60", e -> exportToExcel());
        printBtn = createStyledButton("Print Report", "#2980b9", e -> printTable());
        refreshBtn = createStyledButton("Refresh", "#7f8c8d", e -> refreshData());

        // Set initial states
        updateEmployeeBtn.setDisable(true);
        deleteEmployeeBtn.setDisable(true);
        enrollFingerprintBtn.setDisable(true);
        captureSignatureBtn.setDisable(true);
        disconnectDeviceBtn.setDisable(true);

        if(isAdmin){
                buttonPanel.getChildren().addAll(
            addEmployeeBtn, updateEmployeeBtn, deleteEmployeeBtn, new Separator(),
            enrollFingerprintBtn, captureSignatureBtn, new Separator(),
            loadSignatureByFingerprintBtn, updateSignatureByFingerprintBtn, new Separator(),
            connectDeviceBtn, disconnectDeviceBtn, new Separator(),
            exportBtn, printBtn, refreshBtn
        );
        }
        if(isFinanceAdmin){
                buttonPanel.getChildren().addAll(
            addEmployeeBtn, new Separator(),
            enrollFingerprintBtn, captureSignatureBtn, new Separator(),
            loadSignatureByFingerprintBtn, new Separator(),
            connectDeviceBtn, disconnectDeviceBtn, new Separator(),
            exportBtn, printBtn, refreshBtn
        );
        }

        return new VBox(10, buttonPanel);
    }

    private Button createStyledButton(String text, String color, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: derive(" + color + ", 20%); -fx-text-fill: white; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;-fx-background-radius: 5;"));
        button.setOnAction(action);
        return button;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox();
        tableSection.setPadding(new Insets(15));
        tableSection.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Create table
        employeeTable = new TableView<>();
        setupTableColumns();
        employeeTable.setItems(employeeData);
        
        // Add double-click listener for row details
        employeeTable.setRowFactory(tv -> {
            TableRow<EmployeeSignatureModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    EmployeeSignatureModel employee = row.getItem();
                    showEmployeeDetailsWithSignature(employee);
                }
            });
            return row;
        });
        
        Label placeholder = new Label("No employees found. Click 'Add Employee' to register new employees.");
        placeholder.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14; -fx-padding: 20;");
        employeeTable.setPlaceholder(placeholder);

        // Selection listener
        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            updateButtonStates(newSel);
        });

        tableSection.getChildren().add(employeeTable);
        return tableSection;
    }

    private void setupTableColumns() {
        TableColumn<EmployeeSignatureModel, String> idCol = createTableColumn("Employee ID", "employeeId");
        TableColumn<EmployeeSignatureModel, String> nameCol = createTableColumn("Employee Name", "employeeName");
        TableColumn<EmployeeSignatureModel, String> deptCol = createTableColumn("Department", "department");
        TableColumn<EmployeeSignatureModel, String> positionCol = createTableColumn("Position", "position");
        TableColumn<EmployeeSignatureModel, LocalDate> enrollDateCol = createTableColumn("Enrollment Date", "enrollmentDate");
        TableColumn<EmployeeSignatureModel, String> statusCol = createTableColumn("Status", "status");
        TableColumn<EmployeeSignatureModel, String> fingerprintCol = createTableColumn("Fingerprint", "fingerprintStatus");
        TableColumn<EmployeeSignatureModel, String> signatureCol = createTableColumn("Signature", "signatureStatus");

        employeeTable.getColumns().addAll(idCol, nameCol, deptCol, positionCol, enrollDateCol, statusCol, fingerprintCol, signatureCol);
    }

    private <T> TableColumn<EmployeeSignatureModel, T> createTableColumn(String title, String property) {
        TableColumn<EmployeeSignatureModel, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(120);
        return column;
    }

private void updateButtonStates(EmployeeSignatureModel selected) {
    boolean hasSelection = selected != null;
    
    if (hasSelection) {
        // Check if employee already has fingerprint enrolled
        boolean hasFingerprint = selected.getFingerprintTemplate() != null 
            && selected.getFingerprintTemplate().length > 0
            && !"Not Enrolled".equals(selected.getFingerprintStatus());
        
        // Check if employee already has signature captured
        boolean hasSignature = selected.getSignatureImage() != null 
            && selected.getSignatureImage().length > 0
            && !"Not Captured".equals(selected.getSignatureStatus());
        
        updateEmployeeBtn.setDisable(!hasSelection);
        deleteEmployeeBtn.setDisable(!hasSelection);
        
        // For Admin users - allow re-enrollment and re-capture even if data exists
        // For non-Admin users - disable if data already exists
        if (isAdmin) {
            // Admin can always enroll fingerprint and capture signature (allow updates)
            enrollFingerprintBtn.setDisable(false);
            captureSignatureBtn.setDisable(false);
        } else {
            // Non-admin users can only enroll/capture if data doesn't exist
            enrollFingerprintBtn.setDisable(hasFingerprint);
            captureSignatureBtn.setDisable(hasSignature);
        }
        
        // Update button texts to reflect current state
        if (hasFingerprint) {
            enrollFingerprintBtn.setText("Fingerprint Enrolled ✓");
            enrollFingerprintBtn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-background-radius: 5;");
            
            // For Admin, change text to indicate update capability
            if (isAdmin) {
                enrollFingerprintBtn.setText("Update Fingerprint");
                enrollFingerprintBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-background-radius: 5;");
            }
        } else {
            enrollFingerprintBtn.setText("Enroll Fingerprint");
            enrollFingerprintBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-background-radius: 5;");
        }
        
        if (hasSignature) {
            captureSignatureBtn.setText("Signature Captured ✓");
            captureSignatureBtn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-background-radius: 5;");
            
            // For Admin, change text to indicate update capability
            if (isAdmin) {
                captureSignatureBtn.setText("Update Signature");
                captureSignatureBtn.setStyle("-fx-background-color: #16a085; -fx-text-fill: white; -fx-background-radius: 5;");
            }
        } else {
            captureSignatureBtn.setText("Capture Signature");
            captureSignatureBtn.setStyle("-fx-background-color: #16a085; -fx-text-fill: white; -fx-background-radius: 5;");
        }
    } else {
        // No selection - disable all action buttons
        updateEmployeeBtn.setDisable(true);
        deleteEmployeeBtn.setDisable(true);
        enrollFingerprintBtn.setDisable(true);
        captureSignatureBtn.setDisable(true);
        
        // Reset button texts
        enrollFingerprintBtn.setText("Enroll Fingerprint");
        enrollFingerprintBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-background-radius: 5;");
        
        captureSignatureBtn.setText("Capture Signature");
        captureSignatureBtn.setStyle("-fx-background-color: #16a085; -fx-text-fill: white; -fx-background-radius: 5;");
    }
}

    private void initializeDeviceStatus() {
        updateDeviceStatus(false, "Not Connected");
    }

    private void updateDeviceStatus(boolean connected, String message) {
        Platform.runLater(() -> {
            if (connected) {
                deviceStatusLabel.setText("Device Status: Connected ✓");
                deviceStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                connectDeviceBtn.setDisable(true);
                disconnectDeviceBtn.setDisable(false);
            } else {
                deviceStatusLabel.setText("Device Status: " + message);
                deviceStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                connectDeviceBtn.setDisable(false);
                disconnectDeviceBtn.setDisable(true);
            }
        });
    }

    // Database Operations
    private void loadDataFromDatabase() {
        new Thread(() -> {
            try {
                List<EmployeeSignatureModel> employees = con.getAllEmployees();
                Platform.runLater(() -> {
                    employeeData.clear();
                    employeeData.addAll(employees);
                   // showAlert("Data Loaded", "Loaded " + employees.size() + " employees from database.", Alert.AlertType.INFORMATION);
                });
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Database Error", "Failed to load employee signature data: " + e.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    private void saveEmployeeToDatabase(EmployeeSignatureModel employee) {
        new Thread(() -> {
            try {
                boolean success = con.createEmployee(employee);
                Platform.runLater(() -> {
                    if (success) {
                        employeeData.add(employee);
                        showAlert("Success", "Employee registered successfully!", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Failed to register employee. Employee ID might already exist.", Alert.AlertType.ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Database Error", "Failed to save employee: " + e.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    private void updateEmployeeInDatabase(EmployeeSignatureModel employee) {
        new Thread(() -> {
            try {
                boolean success = con.updateEmployee(employee);
                Platform.runLater(() -> {
                    if (success) {
                        employeeTable.refresh();
                        showAlert("Success", "Employee updated successfully!", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Failed to update employee.", Alert.AlertType.ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Database Error", "Failed to update employee: " + e.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    private void deleteEmployeeFromDatabase(EmployeeSignatureModel employee) {
        new Thread(() -> {
            try {
                boolean success = con.deleteEmployee(employee.getEmployeeId());
                Platform.runLater(() -> {
                    if (success) {
                        employeeData.remove(employee);
                        showAlert("Success", "Employee deleted successfully!", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("Error", "Failed to delete employee.", Alert.AlertType.ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Database Error", "Failed to delete employee: " + e.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

private void updateEmployeeFingerprintInDatabase(EmployeeSignatureModel employee, byte[] fingerprintTemplate) {
    new Thread(() -> {
        try {
            boolean success = con.updateFingerprintTemplate(employee.getEmployeeId(), fingerprintTemplate);
            Platform.runLater(() -> {
                if (success) {
                    employee.setFingerprintTemplate(fingerprintTemplate);
                    employeeTable.refresh();
                    
                    // Update button state after successful enrollment
                    updateButtonStates(employeeTable.getSelectionModel().getSelectedItem());
                    
                    showAlert("Success", "Fingerprint enrolled successfully for " + employee.getEmployeeName(), Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to update fingerprint in database.", Alert.AlertType.ERROR);
                }
            });
        } catch (Exception e) {
            Platform.runLater(() -> showAlert("Database Error", "Failed to update fingerprint: " + e.getMessage(), Alert.AlertType.ERROR));
        }
    }).start();
}

private void updateEmployeeSignatureInDatabase(EmployeeSignatureModel employee, byte[] signatureImage) {
    new Thread(() -> {
        try {
            boolean success = con.updateSignatureImage(employee.getEmployeeId(), signatureImage);
            Platform.runLater(() -> {
                if (success) {
                    employee.setSignatureImage(signatureImage);
                    employeeTable.refresh();
                    
                    // Update button state after successful capture
                    updateButtonStates(employeeTable.getSelectionModel().getSelectedItem());
                    
                    showAlert("Success", "Signature captured successfully for " + employee.getEmployeeName(), Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to update signature in database.", Alert.AlertType.ERROR);
                }
            });
        } catch (Exception e) {
            Platform.runLater(() -> showAlert("Database Error", "Failed to update signature: " + e.getMessage(), Alert.AlertType.ERROR));
        }
    }).start();
}

    // Employee Management
    private void showAddEmployeeForm() {
        Dialog<EmployeeSignatureModel> dialog = createEmployeeDialog("Register New Employee", "Add New Employee to Biometric System", null);
        dialog.showAndWait().ifPresent(this::saveEmployeeToDatabase);
    }

    private void updateSelectedEmployee() {
        EmployeeSignatureModel selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an employee to update.", Alert.AlertType.WARNING);
            return;
        }

        Dialog<EmployeeSignatureModel> dialog = createEmployeeDialog("Update Employee", "Update Employee Information", selected);
        dialog.showAndWait().ifPresent(this::updateEmployeeInDatabase);
    }

    private Dialog<EmployeeSignatureModel> createEmployeeDialog(String title, String header, EmployeeSignatureModel employee) {
        Dialog<EmployeeSignatureModel> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField employeeIdField = new TextField(employee != null ? employee.getEmployeeId() : "");
        TextField employeeNameField = new TextField(employee != null ? employee.getEmployeeName() : "");
        
        // Fetch departments from DB
        String[] dept = con.getDepartmentsFromDatabase(); 
        
        if (dept != null && dept.length > 0) {
    ObservableList<String> deptList = FXCollections.observableArrayList(dept);
    departmentField = new ComboBox<>(deptList);
    departmentField.setValue(dept[0]); // Set default selected value
}
        TextField positionField = new TextField(employee != null ? employee.getPosition() : "");
        ComboBox<String> statusField = new ComboBox<>();
        statusField.getItems().addAll("Active", "Inactive");
        statusField.setValue(employee != null ? employee.getStatus() : "Active");

        if (employee != null) {
            employeeIdField.setDisable(true);
            departmentField.setValue(employee.getDepartment());
        }

        grid.addRow(0, new Label("Employee Phone*:"), employeeIdField);
        grid.addRow(1, new Label("Employee Name*:"), employeeNameField);
        grid.addRow(2, new Label("Department*:"), departmentField);
        grid.addRow(3, new Label("Position*:"), positionField);
        grid.addRow(4, new Label("Status*:"), statusField);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Form validation
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        Runnable validateForm = () -> {
            boolean valid = !employeeIdField.getText().trim().isEmpty() && 
                           !employeeNameField.getText().trim().isEmpty() &&
                           departmentField.getValue() != null &&
                           !positionField.getText().trim().isEmpty();
            okButton.setDisable(!valid);
        };

        employeeIdField.textProperty().addListener((obs, old, now) -> validateForm.run());
        employeeNameField.textProperty().addListener((obs, old, now) -> validateForm.run());
        departmentField.valueProperty().addListener((obs, old, now) -> validateForm.run());
        positionField.textProperty().addListener((obs, old, now) -> validateForm.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (employee != null) {
                    employee.setEmployeeName(employeeNameField.getText());
                    employee.setDepartment(departmentField.getValue());
                    employee.setPosition(positionField.getText());
                    employee.setStatus(statusField.getValue());
                    return employee;
                } else {
                    return new EmployeeSignatureModel(
                        employeeIdField.getText().trim(),
                        employeeNameField.getText().trim(),
                        departmentField.getValue(),
                        positionField.getText().trim(),
                        LocalDate.now(),
                        statusField.getValue(),
                        new byte[0],
                        new byte[0]
                    );
                }
            }
            return null;
        });

        return dialog;
    }

    private void deleteSelectedEmployee() {
        EmployeeSignatureModel selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an employee to delete.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Employee");
        confirmation.setContentText("Are you sure you want to delete employee " + selected.getEmployeeName() + "? This action cannot be undone.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteEmployeeFromDatabase(selected);
            }
        });
    }

    // Fingerprint Operations
    private void enrollFingerprintForSelected() {
        EmployeeSignatureModel selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an employee to enroll fingerprint.", Alert.AlertType.WARNING);
            return;
        }

        if (!fingerprintModule.isDeviceActuallyConnected()) {
            showDeviceNotConnectedAlert();
            return;
        }

        showFingerprintEnrollmentDialog(selected);
    }

    private void showFingerprintEnrollmentDialog(EmployeeSignatureModel employee) {
        Dialog<byte[]> dialog = new Dialog<>();
        dialog.setTitle("Fingerprint Enrollment");
        dialog.setHeaderText("Enroll Fingerprint for " + employee.getEmployeeName());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        ImageView fingerprintView = new ImageView(createFingerprintPlaceholder());
        fingerprintView.setFitWidth(300);
        fingerprintView.setFitHeight(200);

        Label instructionLabel = new Label("Place finger on device for enrollment");
        instructionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        Label statusLabel = new Label("Ready for fingerprint enrollment");
        statusLabel.setStyle("-fx-text-fill: #7f8c8d;");

        Button startEnrollmentBtn = new Button("Start Enrollment");
        startEnrollmentBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        AtomicReference<byte[]> capturedTemplateRef = new AtomicReference<>();

        startEnrollmentBtn.setOnAction(e -> {
            progressIndicator.setVisible(true);
            statusLabel.setText("Capturing fingerprint...");
            statusLabel.setStyle("-fx-text-fill: #f39c12;");
            startEnrollmentBtn.setDisable(true);

            new Thread(() -> {
                try {
                    // Use the enhanced capture method with timeout
                    byte[] template = fingerprintModule.captureFingerprint(30); // 30 second timeout
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        startEnrollmentBtn.setDisable(false);

                        if (template != null && template.length > 0) {
                            capturedTemplateRef.set(template);
                            statusLabel.setText("✓ Fingerprint captured successfully! (" + template.length + " bytes)");
                            statusLabel.setStyle("-fx-text-fill: #27ae60;");
                            fingerprintView.setImage(createSimulatedFingerprintImage());
                            
                            // Auto-save the template
                            updateEmployeeFingerprintInDatabase(employee, template);
                        } else {
                            statusLabel.setText("✗ Failed to capture fingerprint or no fingerprint detected");
                            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                        }
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        startEnrollmentBtn.setDisable(false);
                        statusLabel.setText("✗ Error: " + ex.getMessage());
                        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    });
                }
            }).start();
        });

        content.getChildren().addAll(instructionLabel, fingerprintView, startEnrollmentBtn, progressIndicator, statusLabel);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return capturedTemplateRef.get();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(template -> {
            if (template != null) {
                updateEmployeeFingerprintInDatabase(employee, template);
            }
        });
    }

    // Signature Operations - NEW: Added file upload option
    private void showSignatureCaptureOptions() {
        EmployeeSignatureModel selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an employee to capture signature.", Alert.AlertType.WARNING);
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Signature Capture Options");
        dialog.setHeaderText("Choose how to capture signature for " + selected.getEmployeeName());

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label instructionLabel = new Label("Select signature capture method:");
        instructionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14;");

        Button drawSignatureBtn = createStyledButton("Draw Signature", "#3498db");
        Button uploadSignatureBtn = createStyledButton("Upload Signature File", "#9b59b6");

        drawSignatureBtn.setPrefSize(200, 50);
        uploadSignatureBtn.setPrefSize(200, 50);

        drawSignatureBtn.setOnAction(e -> {
            dialog.setResult("DRAW");
            dialog.close();
        });

        uploadSignatureBtn.setOnAction(e -> {
            dialog.setResult("UPLOAD");
            dialog.close();
        });

        content.getChildren().addAll(instructionLabel, drawSignatureBtn, uploadSignatureBtn);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if ("DRAW".equals(result)) {
                captureSignatureByDrawing(selected);
            } else if ("UPLOAD".equals(result)) {
                captureSignatureByUpload(selected);
            }
        });
    }

    private void captureSignatureByDrawing(EmployeeSignatureModel employee) {
        Dialog<byte[]> dialog = new Dialog<>();
        dialog.setTitle("Digital Signature Capture - Drawing");
        dialog.setHeaderText("Draw Signature for " + employee.getEmployeeName());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Canvas signatureCanvas = new Canvas(400, 200);
        signatureCanvas.setStyle("-fx-border-color: #2c3e50; -fx-border-width: 2; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        GraphicsContext gc = signatureCanvas.getGraphicsContext2D();
        setupSignatureCanvas(gc);

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);

        Button clearBtn = new Button("Clear Signature");
        clearBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        clearBtn.setOnAction(e -> clearSignatureCanvas(gc));

        Button previewBtn = new Button("Preview Signature");
        previewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        previewBtn.setOnAction(e -> previewSignature(signatureCanvas));

        Label instructionLabel = new Label("Draw your signature in the area above");
        instructionLabel.setStyle("-fx-text-fill: #7f8c8d;");

        content.getChildren().addAll(instructionLabel, signatureCanvas, controls);
        controls.getChildren().addAll(clearBtn, previewBtn);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Customize OK button
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true); // Initially disabled until signature is drawn

        // Add validation - enable OK button only when signature is not empty
        signatureCanvas.setOnMouseReleased(e -> {
            boolean hasSignature = checkIfCanvasHasContent(signatureCanvas);
            okButton.setDisable(!hasSignature);
            
            if (hasSignature) {
                showTempAlert("Signature ready to save", Color.GREEN);
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    if (checkIfCanvasHasContent(signatureCanvas)) {
                        return captureSignatureFromCanvas(signatureCanvas);
                    } else {
                        showAlert("No Signature", "Please draw a signature before saving.", Alert.AlertType.WARNING);
                        return null;
                    }
                } catch (Exception ex) {
                    showAlert("Error", "Failed to capture signature: " + ex.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(signatureBytes -> {
            if (signatureBytes != null) {
                updateEmployeeSignatureInDatabase(employee, signatureBytes);
                showAlert("Success", "Signature captured successfully for " + employee.getEmployeeName(), Alert.AlertType.INFORMATION);
            }
        });
    }

    private void captureSignatureByUpload(EmployeeSignatureModel employee) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Signature Image File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Read the image file
                BufferedImage bufferedImage = ImageIO.read(selectedFile);
                if (bufferedImage == null) {
                    showAlert("Invalid File", "The selected file is not a valid image.", Alert.AlertType.ERROR);
                    return;
                }

                // Convert to byte array
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                String format = getFileExtension(selectedFile.getName()).toLowerCase();
                if (!format.equals("png") && !format.equals("jpg") && !format.equals("jpeg")) {
                    format = "png"; // Default to PNG if format is not supported
                }
                ImageIO.write(bufferedImage, format, baos);
                byte[] signatureBytes = baos.toByteArray();

                // Show preview before saving
                if (showSignaturePreviewDialog(signatureBytes, "Preview Uploaded Signature")) {
                    updateEmployeeSignatureInDatabase(employee, signatureBytes);
                    showAlert("Success", "Signature uploaded successfully for " + employee.getEmployeeName(), Alert.AlertType.INFORMATION);
                }

            } catch (Exception e) {
                showAlert("Upload Error", "Failed to upload signature file: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }

    private boolean showSignaturePreviewDialog(byte[] signatureBytes, String title) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Preview Signature Before Saving");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        try {
            ImageView signatureView = new ImageView(convertByteArrayToImage(signatureBytes));
            signatureView.setFitWidth(350);
            signatureView.setFitHeight(175);
            signatureView.setPreserveRatio(true);
            signatureView.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 5;");

            Label previewLabel = new Label("This is how the signature will look. Save this signature?");
            previewLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");

            content.getChildren().addAll(previewLabel, signatureView);
        } catch (Exception e) {
            Label errorLabel = new Label("Error previewing signature: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: #e74c3c;");
            content.getChildren().add(errorLabel);
        }

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        dialog.setResultConverter(dialogButton -> {
            return dialogButton == ButtonType.YES;
        });

        return dialog.showAndWait().orElse(false);
    }

    private void setupSignatureCanvas(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 400, 200);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        final boolean[] isDrawing = {false};
        
        gc.getCanvas().setOnMousePressed(e -> {
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            isDrawing[0] = true;
        });

        gc.getCanvas().setOnMouseDragged(e -> {
            if (isDrawing[0]) {
                gc.lineTo(e.getX(), e.getY());
                gc.stroke();
            }
        });

        gc.getCanvas().setOnMouseReleased(e -> {
            isDrawing[0] = false;
        });
    }

    private void clearSignatureCanvas(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 400, 200);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
    }

    private byte[] captureSignatureFromCanvas(Canvas canvas) throws Exception {
        javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(400, 200);
        canvas.snapshot(null, writableImage);
        
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "PNG", baos);
        return baos.toByteArray();
    }

    // Fingerprint Signature Operations
    private void loadSignatureByFingerprint() {
        if (!fingerprintModule.isDeviceActuallyConnected()) {
            showDeviceNotConnectedAlert();
            return;
        }

        findEmployeeByFingerprint(employee -> {
            if (employee != null) {
                showEmployeeSignatureDialog(employee, "Load Signature by Fingerprint");
            } else {
                showAlert("No Match Found", "No employee found with this fingerprint. Please try again or enroll the fingerprint first.", Alert.AlertType.WARNING);
            }
        });
    }

    private void showSignatureUpdateOptions() {
        if (!fingerprintModule.isDeviceActuallyConnected()) {
            showDeviceNotConnectedAlert();
            return;
        }

        findEmployeeByFingerprint(employee -> {
            if (employee != null) {
                showSignatureUpdateOptionsDialog(employee);
            } else {
                showAlert("No Match Found", "No employee found with this fingerprint. Please try again or enroll the fingerprint first.", Alert.AlertType.WARNING);
            }
        });
    }
private void showSignatureUpdateOptionsDialog(EmployeeSignatureModel employee) {
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Signature Update Options");
    dialog.setHeaderText("Choose how to update signature for " + employee.getEmployeeName());

    VBox content = new VBox(20);
    content.setPadding(new Insets(20));
    content.setAlignment(Pos.CENTER);

    Label instructionLabel = new Label("Select signature update method:");
    instructionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14;");

    Button drawSignatureBtn = createStyledButton("Draw New Signature", "#3498db");
    Button uploadSignatureBtn = createStyledButton("Upload Signature File", "#9b59b6");

    drawSignatureBtn.setPrefSize(200, 50);
    uploadSignatureBtn.setPrefSize(200, 50);

    drawSignatureBtn.setOnAction(e -> {
        dialog.setResult("DRAW");
        dialog.close();
    });

    uploadSignatureBtn.setOnAction(e -> {
        dialog.setResult("UPLOAD");
        dialog.close();
    });

    content.getChildren().addAll(instructionLabel, drawSignatureBtn, uploadSignatureBtn);
    dialog.getDialogPane().setContent(content);
    dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

    dialog.showAndWait().ifPresent(result -> {
        if ("DRAW".equals(result)) {
            showSignatureUpdateDialog(employee, "DRAW");
        } else if ("UPLOAD".equals(result)) {
            showSignatureUpdateDialog(employee, "UPLOAD");
        }
    });
}

private void showSignatureUpdateDialog(EmployeeSignatureModel employee, String method) {
    Dialog<byte[]> dialog = new Dialog<>();
    dialog.setTitle("Update Signature - " + (method.equals("DRAW") ? "Drawing" : "File Upload"));
    dialog.setHeaderText("Update Signature for " + employee.getEmployeeName());
    dialog.getDialogPane().setPrefSize(500, 600);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    VBox content = new VBox(15);
    content.setPadding(new Insets(20));
    content.setAlignment(Pos.CENTER);

    // Show current signature if available
    if (employee.getSignatureImage() != null && employee.getSignatureImage().length > 0) {
        Label currentLabel = new Label("Current Signature:");
        currentLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14;");
        ImageView currentSignature = new ImageView(convertByteArrayToImage(employee.getSignatureImage()));
        currentSignature.setFitWidth(300);
        currentSignature.setFitHeight(150);
        currentSignature.setPreserveRatio(true);
        currentSignature.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 5;");
        content.getChildren().addAll(currentLabel, currentSignature);
    }

    if ("DRAW".equals(method)) {
        Label newLabel = new Label("Draw New Signature Below:");
        newLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14;");
        content.getChildren().add(newLabel);

        Canvas signatureCanvas = new Canvas(400, 200);
        signatureCanvas.setStyle("-fx-border-color: #2c3e50; -fx-border-width: 2; -fx-background-color: white;");
        GraphicsContext gc = signatureCanvas.getGraphicsContext2D();
        setupSignatureCanvas(gc);

        Label instructionLabel = new Label("Click and drag to draw your signature here");
        instructionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12;");

        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10, 0, 0, 0));

        Button clearBtn = createStyledButton("Clear Signature", "#e74c3c");
        Button previewBtn = createStyledButton("Preview Signature", "#3498db");
        controls.getChildren().addAll(clearBtn, previewBtn);

        content.getChildren().addAll(instructionLabel, signatureCanvas, controls);

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        clearBtn.setOnAction(e -> {
            clearSignatureCanvas(gc);
            okButton.setDisable(true);
            showTempAlert("Signature cleared", Color.ORANGE);
        });

        previewBtn.setOnAction(e -> previewSignature(signatureCanvas));

        // Enable OK button only if there’s drawing
        signatureCanvas.setOnMouseReleased(e -> {
            boolean hasSignature = checkIfCanvasHasContent(signatureCanvas);
            okButton.setDisable(!hasSignature);
            if (hasSignature) {
                showTempAlert("Signature ready to save", Color.GREEN);
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK && checkIfCanvasHasContent(signatureCanvas)) {
                try {
                    return captureSignatureFromCanvas(signatureCanvas);
                } catch (Exception ex) {
                    Logger.getLogger(EmployeeSignatureManagement.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return null;
        });

    } else {
        // Upload Method
        Label uploadLabel = new Label("Upload New Signature File:");
        uploadLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14;");
        content.getChildren().add(uploadLabel);

        Button uploadBtn = createStyledButton("Select Signature File", "#9b59b6");
        uploadBtn.setPrefSize(200, 40);

        AtomicReference<byte[]> uploadedSignatureRef = new AtomicReference<>();

        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Signature Image File");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );

            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                try {
                    BufferedImage bufferedImage = ImageIO.read(selectedFile);
                    if (bufferedImage == null) {
                        showAlert("Invalid File", "The selected file is not a valid image.", Alert.AlertType.ERROR);
                        return;
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    String format = getFileExtension(selectedFile.getName()).toLowerCase();
                    if (!format.equals("png") && !format.equals("jpg") && !format.equals("jpeg")) {
                        format = "png";
                    }
                    ImageIO.write(bufferedImage, format, baos);
                    byte[] signatureBytes = baos.toByteArray();
                    uploadedSignatureRef.set(signatureBytes);

                    ImageView previewView = new ImageView(convertByteArrayToImage(signatureBytes));
                    previewView.setFitWidth(300);
                    previewView.setFitHeight(150);
                    previewView.setPreserveRatio(true);
                    previewView.setStyle("-fx-border-color: #27ae60; -fx-border-width: 2; -fx-border-radius: 5;");

                    content.getChildren().add(previewView);
                    showTempAlert("File uploaded successfully", Color.GREEN);
                } catch (Exception ex) {
                    showAlert("Upload Error", "Failed to upload signature file: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });

        content.getChildren().add(uploadBtn);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return uploadedSignatureRef.get();
            }
            return null;
        });
    }

    dialog.getDialogPane().setContent(content);

    dialog.showAndWait().ifPresent(signatureBytes -> {
        if (signatureBytes != null) {
            updateEmployeeSignatureInDatabase(employee, signatureBytes);
            showAlert("Success", "Signature updated successfully for " + employee.getEmployeeName(), Alert.AlertType.INFORMATION);
        }
    });
}


    private void findEmployeeByFingerprint(Consumer<EmployeeSignatureModel> onMatchFound) {
        if (!fingerprintModule.isDeviceActuallyConnected()) {
            showAlert("Device Error", "Fingerprint device is not connected.", Alert.AlertType.ERROR);
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Fingerprint Identification");
        dialog.setHeaderText("Place your finger on the scanner");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        ImageView fingerprintView = new ImageView(createFingerprintPlaceholder());
        fingerprintView.setFitWidth(300);
        fingerprintView.setFitHeight(200);

        ProgressIndicator progress = new ProgressIndicator();
        progress.setVisible(true);

        Label statusLabel = new Label("Waiting for fingerprint...");
        statusLabel.setStyle("-fx-text-fill: #7f8c8d;");

        content.getChildren().addAll(fingerprintView, progress, statusLabel);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        new Thread(() -> {
            try {
                statusLabel.setText("🔍 Scanning for fingerprint...");
                statusLabel.setStyle("-fx-text-fill: #3498db;");

                byte[] capturedTemplate = fingerprintModule.captureFingerprint(30);
                
                Platform.runLater(() -> {
                    progress.setVisible(false);
                    
                    if (capturedTemplate != null && capturedTemplate.length > 0) {
                        statusLabel.setText("✓ Captured, searching database...");
                        statusLabel.setStyle("-fx-text-fill: #3498db;");
                        fingerprintView.setImage(createSimulatedFingerprintImage());

                        EmployeeSignatureModel matched = enhancedTemplateMatching(capturedTemplate);
                        if (matched != null) {
                            statusLabel.setText("✅ Match found: " + matched.getEmployeeName());
                            statusLabel.setStyle("-fx-text-fill: #27ae60;");
                            
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1500);
                                    Platform.runLater(() -> {
                                        dialog.close();
                                        onMatchFound.accept(matched);
                                    });
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }).start();
                        } else {
                            statusLabel.setText("✗ No match found in database");
                            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                            
                            showAlert("No Match", 
                                "Fingerprint not found in database.\n\n" +
                                "Possible reasons:\n" +
                                "• Fingerprint not enrolled for any employee\n" +
                                "• Poor fingerprint quality\n" +
                                "• Try enrolling fingerprint first", 
                                Alert.AlertType.WARNING);
                        }
                    } else {
                        statusLabel.setText("✗ Failed to capture fingerprint");
                        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                        showAlert("Capture Failed", 
                            "Could not capture fingerprint.\n\n" +
                            "Please ensure:\n" +
                            "• Finger is placed correctly on scanner\n" +
                            "• Finger is clean and dry\n" +
                            "• Scanner is functioning properly", 
                            Alert.AlertType.ERROR);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progress.setVisible(false);
                    statusLabel.setText("✗ Error: " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    showAlert("System Error", "Fingerprint identification failed: " + e.getMessage(), Alert.AlertType.ERROR);
                });
            }
        }).start();

        dialog.showAndWait();
    }

    private EmployeeSignatureModel enhancedTemplateMatching(byte[] capturedTemplate) {
        try {
            System.out.println("Starting ZKTECO template matching...");
            System.out.println("Captured template size: " + (capturedTemplate != null ? capturedTemplate.length : 0) + " bytes");
            
            if (capturedTemplate == null || capturedTemplate.length == 0) {
                System.out.println("Invalid captured template");
                return null;
            }

            List<EmployeeSignatureModel> employeesWithFingerprints = employeeData.filtered(
                emp -> emp.getFingerprintTemplate() != null && emp.getFingerprintTemplate().length > 0
            ).stream().collect(Collectors.toList());

            System.out.println("Employees with fingerprints: " + employeesWithFingerprints.size());

            if (employeesWithFingerprints.isEmpty()) {
                System.out.println("No employees with enrolled fingerprints found");
                return null;
            }

            for (EmployeeSignatureModel employee : employeesWithFingerprints) {
                byte[] storedTemplate = employee.getFingerprintTemplate();
                
                if (storedTemplate != null && storedTemplate.length > 0) {
                    try {
                        boolean isMatch = fingerprintModule.compareTemplate(storedTemplate, capturedTemplate);
                        
                        if (isMatch) {
                            System.out.println("✅ Match found: " + employee.getEmployeeName());
                            return employee;
                        } else {
                            System.out.println("❌ No match with: " + employee.getEmployeeName());
                        }
                    } catch (Exception e) {
                        System.out.println("Error comparing with " + employee.getEmployeeName() + ": " + e.getMessage());
                    }
                }
            }

            System.out.println("❌ No match found with any enrolled fingerprint");
            return null;

        } catch (Exception e) {
            System.out.println("❌ Error in ZKTECO template matching: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private boolean checkIfCanvasHasContent(Canvas canvas) {
        try {
            javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(400, 200);
            canvas.snapshot(null, writableImage);
            
            for (int x = 0; x < 400; x += 10) {
                for (int y = 0; y < 200; y += 10) {
                    Color color = writableImage.getPixelReader().getColor(x, y);
                    if (color.getRed() < 0.9 || color.getGreen() < 0.9 || color.getBlue() < 0.9) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void previewSignature(Canvas canvas) {
        try {
            byte[] signatureBytes = captureSignatureFromCanvas(canvas);
            if (signatureBytes != null) {
                Dialog<Void> previewDialog = new Dialog<>();
                previewDialog.setTitle("Signature Preview");
                previewDialog.setHeaderText("Preview Your Signature");
                
                VBox previewContent = new VBox(15);
                previewContent.setPadding(new Insets(20));
                previewContent.setAlignment(Pos.CENTER);
                
                ImageView previewImage = new ImageView(convertByteArrayToImage(signatureBytes));
                previewImage.setFitWidth(350);
                previewImage.setFitHeight(175);
                previewImage.setPreserveRatio(true);
                previewImage.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 5;");
                
                Label previewLabel = new Label("This is how your signature will look");
                previewLabel.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");
                
                previewContent.getChildren().addAll(previewLabel, previewImage);
                previewDialog.getDialogPane().setContent(previewContent);
                previewDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                previewDialog.showAndWait();
            }
        } catch (Exception e) {
            showAlert("Preview Error", "Cannot preview signature: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: derive(" + color + ", 20%); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;"));
        return button;
    }

    private void showTempAlert(String message, Color color) {
        System.out.println("Status: " + message);
    }

    // Enhanced employee details dialog with signature display
    private void showEmployeeDetailsWithSignature(EmployeeSignatureModel employee) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Employee Details with Signature");
        dialog.setHeaderText("Employee Biometric Information - " + employee.getEmployeeName());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(10);
        detailsGrid.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-border-radius: 5;");

        String[][] details = {
            {"Employee ID:", employee.getEmployeeId()},
            {"Employee Name:", employee.getEmployeeName()},
            {"Department:", employee.getDepartment()},
            {"Position:", employee.getPosition()},
            {"Enrollment Date:", employee.getEnrollmentDate().toString()},
            {"Status:", employee.getStatus()},
            {"Fingerprint Status:", employee.getFingerprintStatus()},
            {"Signature Status:", employee.getSignatureStatus()}
        };

        for (int i = 0; i < details.length; i++) {
            Label titleLabel = new Label(details[i][0]);
            titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            Label valueLabel = new Label(details[i][1]);
            valueLabel.setStyle("-fx-text-fill: #34495e;");
            
            detailsGrid.add(titleLabel, 0, i);
            detailsGrid.add(valueLabel, 1, i);
        }

        content.getChildren().add(detailsGrid);

        Label signatureTitle = new Label("Digital Signature");
        signatureTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #2c3e50;");
        content.getChildren().add(signatureTitle);

        if (employee.getSignatureImage() != null && employee.getSignatureImage().length > 0) {
            try {
                ImageView signatureView = new ImageView(convertByteArrayToImage(employee.getSignatureImage()));
                signatureView.setFitWidth(400);
                signatureView.setFitHeight(200);
                signatureView.setPreserveRatio(true);
                signatureView.setStyle("-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 5;");
                content.getChildren().add(signatureView);
                
                Label signatureInfo = new Label("Signature captured: " + employee.getSignatureImage().length + " bytes");
                signatureInfo.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12;");
                content.getChildren().add(signatureInfo);
            } catch (Exception e) {
                Label errorLabel = new Label("Error displaying signature: " + e.getMessage());
                errorLabel.setStyle("-fx-text-fill: #e74c3c;");
                content.getChildren().add(errorLabel);
            }
        } else {
            Label noSignatureLabel = new Label("No signature available for this employee");
            noSignatureLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            content.getChildren().add(noSignatureLabel);
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefSize(600, 650);
        dialog.showAndWait();
    }

    private void showEmployeeSignatureDialog(EmployeeSignatureModel employee, String title) {
        showEmployeeDetailsWithSignature(employee);
    }

    // Device Management
    private void connectFingerprintDevice() {
        new Thread(() -> {
            Platform.runLater(() -> {
                operationStatusLabel.setText("Connecting to fingerprint device...");
                operationStatusLabel.setStyle("-fx-text-fill: #f39c12;");
            });
            
            boolean connected = fingerprintModule.openDevice();
            
            Platform.runLater(() -> {
                if (connected) {
                    updateDeviceStatus(true, "Connected");
                    operationStatusLabel.setText("Device connected successfully");
                    operationStatusLabel.setStyle("-fx-text-fill: #27ae60;");
                    showAlert("Success", "Fingerprint device connected successfully!", Alert.AlertType.INFORMATION);
                } else {
                    updateDeviceStatus(false, "Connection Failed");
                    operationStatusLabel.setText("Device connection failed");
                    operationStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    showAlert("Error", "Failed to connect to fingerprint device. Please check device connection and drivers.", Alert.AlertType.ERROR);
                }
            });
        }).start();
    }

    private void disconnectFingerprintDevice() {
        new Thread(() -> {
            Platform.runLater(() -> {
                operationStatusLabel.setText("Disconnecting device...");
                operationStatusLabel.setStyle("-fx-text-fill: #f39c12;");
            });
            
            fingerprintModule.closeDevice();
            
            Platform.runLater(() -> {
                updateDeviceStatus(false, "Disconnected");
                operationStatusLabel.setText("Device disconnected");
                operationStatusLabel.setStyle("-fx-text-fill: #7f8c8d;");
                showAlert("Disconnected", "Fingerprint device disconnected successfully.", Alert.AlertType.INFORMATION);
            });
        }).start();
    }

    // Utility Methods
    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Employee Biometric Data to Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("Employee_Biometric_Data_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                XSSFSheet sheet = workbook.createSheet("Employee Biometric Data");
                
                XSSFRow headerRow = sheet.createRow(0);
                String[] headers = {"Employee ID", "Employee Name", "Department", "Position", 
                    "Enrollment Date", "Status", "Fingerprint Status", "Signature Status"};
                
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }
                
                int rowNum = 1;
                for (EmployeeSignatureModel employee : employeeData) {
                    XSSFRow row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(employee.getEmployeeId());
                    row.createCell(1).setCellValue(employee.getEmployeeName());
                    row.createCell(2).setCellValue(employee.getDepartment());
                    row.createCell(3).setCellValue(employee.getPosition());
                    row.createCell(4).setCellValue(employee.getEnrollmentDate().toString());
                    row.createCell(5).setCellValue(employee.getStatus());
                    row.createCell(6).setCellValue(employee.getFingerprintStatus());
                    row.createCell(7).setCellValue(employee.getSignatureStatus());
                }
                
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                
                showAlert("Export Successful", "Employee biometric data exported to Excel successfully!", Alert.AlertType.INFORMATION);
                
            } catch (Exception e) {
                showAlert("Export Error", "Error exporting to Excel: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void printTable() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            boolean success = job.printPage(employeeTable);
            if (success) {
                job.endJob();
                showAlert("Print Successful", "Employee biometric data printed successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Print Failed", "Failed to print employee data.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Print Error", "Could not create printer job.", Alert.AlertType.ERROR);
        }
    }

private void refreshData() {
    loadDataFromDatabase();
    Platform.runLater(() -> {
        operationStatusLabel.setText("Data refreshed successfully");
        operationStatusLabel.setStyle("-fx-text-fill: #27ae60;");
        // Reset button states after refresh
        updateButtonStates(employeeTable.getSelectionModel().getSelectedItem());
    });
}

    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showDeviceNotConnectedAlert() {
        showAlert("Device Not Connected", 
            "Please connect to the fingerprint device first.\n" +
            "Click 'Connect Device' to establish connection.",
            Alert.AlertType.WARNING);
    }

    // Image Utility Methods
    private Image convertByteArrayToImage(byte[] imageData) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage bufferedImage = ImageIO.read(bis);
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (Exception e) {
            return createSignaturePlaceholder();
        }
    }

    private Image createSignaturePlaceholder() {
        Canvas canvas = new Canvas(400, 200);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 400, 200);
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        gc.strokeRect(0, 0, 400, 200);
        gc.setFill(Color.GRAY);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.NORMAL, 14));
        gc.fillText("No Signature Available", 150, 100);
        return canvas.snapshot(null, null);
    }

    private Image createFingerprintPlaceholder() {
        Canvas canvas = new Canvas(300, 200);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, 300, 200);
        gc.setFill(Color.DARKGRAY);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));
        gc.fillText("Fingerprint Scanner", 80, 100);
        return canvas.snapshot(null, null);
    }

    private Image createSimulatedFingerprintImage() {
        Canvas canvas = new Canvas(300, 200);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 300, 200);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        
        for (int i = 0; i < 20; i++) {
            double y = 20 + i * 8;
            gc.strokeOval(50, y, 200, 30);
        }
        
        return canvas.snapshot(null, null);
    }

    public void cleanup() {
        if (fingerprintModule != null) {
            fingerprintModule.closeDevice();
        }
        if (con != null) {
            con.close();
        }
    }
}