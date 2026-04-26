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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import javafx.scene.Node;
import java.util.Optional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import java.io.FileOutputStream;
import javafx.stage.FileChooser;
import javafx.print.PrinterJob;
import javafx.scene.transform.Scale;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Desktop;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.apache.poi.ss.util.CellRangeAddress;

public class PurchaseFundReplenishRequestManagement extends BorderPane { 
    
    private String currentUser;

    private TableView<PurchaseFundReplenishRecordModel> purchaseFundReplenishRequestTable;
    private ObservableList<PurchaseFundReplenishRecordModel> purchaseFundReplenishRequestData = FXCollections.observableArrayList();
    private ObservableList<EmployeeSignatureModel> employeeSignatureData = FXCollections.observableArrayList();

    private Button confirmBtn;
    private Button dispenseBtn;
    private Button approveRequestBtn;
    private Button approveDispenseBtn;
    private Button voidBtn;
    private Button requestBtn;
    private Button connectDeviceBtn;
    private Button disconnectDeviceBtn;
    private Button viewDetailsBtn;
    private Button exportPdfBtn;

    private ZKTECO fingerprintModule;
    private Connecting databaseConnector;
    private Label deviceStatusLabel;
    private Label operationStatusLabel;
    private Label summaryLabel;
    
    boolean isAdmin=false;
    boolean isCashier=false;
    boolean isFinanceAdmin=false;
    boolean isAccountant=false;
    boolean isReplenishDispenser=false;

    public PurchaseFundReplenishRequestManagement(String username) {
        this.currentUser=username;
        fingerprintModule = new ZKTECO();
        databaseConnector = new Connecting();
        isAdmin=databaseConnector.isAdmin(currentUser.toLowerCase());
        isCashier=databaseConnector.isCashier(currentUser.toLowerCase());
        isAccountant=databaseConnector.isAccountant(currentUser.toLowerCase());
        isFinanceAdmin=databaseConnector.isFinanceAdmin(currentUser.toLowerCase());
        isReplenishDispenser=databaseConnector.isReplenishDispenser(currentUser.toLowerCase());
        
        initializeUI();
        loadEmployeeSignaturesFromDatabase();
        loadPurchaseFundRequestsFromDatabase();
        initializeDeviceStatus();
        initializeSummary();
    }

    private void initializeUI() {
        
        VBox header = createHeader();
        HBox footer = createFooter();
        VBox buttonPanel = createButtonPanel();
        VBox tableSection = createTableSection();
        HBox summaryPanel = createSummaryPanel();

        VBox mainContent = new VBox(10);
        mainContent.setPadding(new Insets(15));
        mainContent.getChildren().addAll(summaryPanel, buttonPanel, tableSection);
        
        this.setTop(header);
        this.setCenter(mainContent);
        this.setBottom(footer);
        this.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);");
    }

    private VBox createHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #3498db); -fx-padding: 20; -fx-alignment: center;");
        
        Text departmentName = new Text("PURCHASE FUND REPLENISH REQUEST MANAGEMENT");
        departmentName.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        departmentName.setFill(Color.WHITE);
        
        Text hospitalName = new Text("AFRAN GENERAL HOSPITAL");
        hospitalName.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        hospitalName.setFill(Color.LIGHTBLUE);
        
        Text currentDate = new Text("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        currentDate.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        currentDate.setFill(Color.WHITE);
        
        VBox textContainer = new VBox(8);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.getChildren().addAll(departmentName,hospitalName, currentDate);
        
        HBox headerContainer = new HBox(20);
        headerContainer.setAlignment(Pos.CENTER);
        
        StackPane logoPlaceholder = new StackPane();
        logoPlaceholder.setPrefSize(80, 80);
        logoPlaceholder.setStyle("-fx-background-color: white; -fx-background-radius: 40; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 40;");
        
        Text logoText = new Text("AGH");
        logoText.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        logoText.setFill(Color.DARKBLUE);
        logoPlaceholder.getChildren().add(logoText);
        
        headerContainer.getChildren().addAll(logoPlaceholder, textContainer);
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
        
        Text footerText = new Text("© 2025 Afran General Hospital - Smart HRMS | Purchase Fund Replenish Management System v3.0 | Biometric Signature Integration");
        footerText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        footerText.setFill(Color.LIGHTGRAY);
        
        footer.getChildren().addAll(deviceStatusLabel, new Separator(), operationStatusLabel, new Separator(), footerText);
        return footer;
    }

    private HBox createSummaryPanel() {
        HBox summaryPanel = new HBox(20);
        summaryPanel.setPadding(new Insets(10));
        summaryPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
        summaryPanel.setAlignment(Pos.CENTER_LEFT);

        summaryLabel = new Label("Loading summary...");
        summaryLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #2c3e50;");

        summaryPanel.getChildren().add(summaryLabel);
        return summaryPanel;
    }

    private void initializeSummary() {
        updateSummary();
    }

    private void updateSummary() {
        if (purchaseFundReplenishRequestData.isEmpty()) {
            summaryLabel.setText("No purchase fund replenish requests found");
            return;
        }

        long totalRequests = purchaseFundReplenishRequestData.size();
        long pendingApproval = purchaseFundReplenishRequestData.stream()
            .filter(r -> "Pending".equals(r.getApprovalStatus()))
            .count();
        long approved = purchaseFundReplenishRequestData.stream()
            .filter(r -> "Approved".equals(r.getApprovalStatus()))
            .count();
        long confirmed = purchaseFundReplenishRequestData.stream()
            .filter(r -> "Confirmed".equals(r.getConfirmationStatus()))
            .count();
        long dispensed = purchaseFundReplenishRequestData.stream()
            .filter(r -> "Yes".equals(r.getDispensedStatus()))
            .count();
        double totalAmount = purchaseFundReplenishRequestData.stream()
            .mapToDouble(PurchaseFundReplenishRecordModel::getAmountRequested)
            .sum();

        String summary = String.format(
            "Summary: Total: %d | Pending: %d | Approved: %d | Confirmed: %d | Dispensed: %d | Total Amount: ETB %.2f",
            totalRequests, pendingApproval, approved, confirmed, dispensed, totalAmount
        );
        summaryLabel.setText(summary);
    }

    private VBox createButtonPanel() {
        VBox vBox = new VBox(10);
        
        HBox buttonPanel1 = new HBox(10);
        HBox buttonPanel2 = new HBox(10);
        
        buttonPanel1.setAlignment(Pos.CENTER_LEFT);
        buttonPanel2.setAlignment(Pos.CENTER_LEFT);

        // CRUD Operations
        requestBtn = createStyledButton("New Replenish", "#27ae60");
        requestBtn.setOnAction(e -> showPurchaseFundRequestForm());
        
        voidBtn = createStyledButton("Void Request", "#e74c3c");
        voidBtn.setOnAction(e -> voidSelected());


        viewDetailsBtn = createStyledButton("View Details", "#8e44ad");
        viewDetailsBtn.setOnAction(e -> viewSelectedDetails());

        // Workflow Buttons - FOLLOWING THE SPECIFIED FLOW
        approveRequestBtn = createStyledButton("Approve Request", "#f39c12");
        approveRequestBtn.setOnAction(e -> approveRequestWithBiometric());
        
        confirmBtn = createStyledButton("Confirm Request", "#9b59b6");
        confirmBtn.setDisable(true);
        confirmBtn.setOnAction(e -> confirmWithBiometric());

        dispenseBtn = createStyledButton("Dispense Cash", "#16a085");
        dispenseBtn.setDisable(true);
        dispenseBtn.setOnAction(e -> {
            PurchaseFundReplenishRecordModel selected = purchaseFundReplenishRequestTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showDispenseForm(selected);
            } else {
                showAlert("No Selection", "Please select a purchase fund request to dispense.", Alert.AlertType.WARNING);
            }
        });

        approveDispenseBtn = createStyledButton("Approve Dispense", "#d35400");
        approveDispenseBtn.setDisable(true);
        approveDispenseBtn.setOnAction(e -> approveDispenseWithBiometric());

        // Device Management Buttons
        connectDeviceBtn = createStyledButton("Connect Device", "#3498db");
        connectDeviceBtn.setOnAction(e -> connectFingerprintDevice());

        disconnectDeviceBtn = createStyledButton("Disconnect Device", "#e74c3c");
        disconnectDeviceBtn.setDisable(true);
        disconnectDeviceBtn.setOnAction(e -> disconnectFingerprintDevice());

        // Export and Utility Buttons
        Button exportExcelBtn = createStyledButton("Export To Excel", "#27ae60");
        exportExcelBtn.setOnAction(e -> showExportExcelOptions());

        Button exportWordBtn = createStyledButton("Export To Word", "#2980b9");
        exportWordBtn.setOnAction(e -> showExportWordOptions());

        exportPdfBtn = createStyledButton("Export To PDF", "#e74c3c");
        exportPdfBtn.setOnAction(e -> exportToPdf());

        Button printBtn = createStyledButton("Print Report", "#8e44ad");
        printBtn.setOnAction(e -> showPrintOptions());

        Button searchAdvancedBtn = createStyledButton("Advanced Search", "#16a085");
        searchAdvancedBtn.setOnAction(e -> showAdvancedSearchDialog());

        Button refreshBtn = createStyledButton("Refresh Data", "#7f8c8d");
        refreshBtn.setOnAction(e -> refreshData());

        if(isAdmin){
        buttonPanel1.getChildren().addAll(
            requestBtn, voidBtn, viewDetailsBtn, new Separator(),
            approveRequestBtn, confirmBtn, dispenseBtn, approveDispenseBtn
        );
        buttonPanel2.getChildren().addAll(
            connectDeviceBtn, disconnectDeviceBtn, new Separator(),
            exportExcelBtn, exportWordBtn, exportPdfBtn, printBtn, searchAdvancedBtn, refreshBtn
        );
        }
        else if(isCashier){
        buttonPanel1.getChildren().addAll(
            requestBtn,voidBtn,viewDetailsBtn, new Separator()
        );
        buttonPanel2.getChildren().addAll(
            connectDeviceBtn, disconnectDeviceBtn, new Separator(),
            exportExcelBtn, exportWordBtn, exportPdfBtn, printBtn, searchAdvancedBtn, refreshBtn
        );
            
        }
        else if(isReplenishDispenser){
        buttonPanel1.getChildren().addAll(
            viewDetailsBtn, new Separator(),
            dispenseBtn
        );
        buttonPanel2.getChildren().addAll(
            connectDeviceBtn, disconnectDeviceBtn, new Separator(),
            exportExcelBtn, exportWordBtn, exportPdfBtn, printBtn, searchAdvancedBtn, refreshBtn
        );
            
        }
        else if(isAccountant){
        buttonPanel1.getChildren().addAll(
            viewDetailsBtn, new Separator(),
            confirmBtn
        );
        buttonPanel2.getChildren().addAll(
            connectDeviceBtn, disconnectDeviceBtn, new Separator(),
            exportExcelBtn, exportWordBtn, exportPdfBtn, printBtn, searchAdvancedBtn, refreshBtn
        ); 
        }
        else if(isFinanceAdmin){ 
        buttonPanel1.getChildren().addAll(
            voidBtn,viewDetailsBtn, new Separator(),
            approveRequestBtn, approveDispenseBtn
        );
        buttonPanel2.getChildren().addAll(
            connectDeviceBtn, disconnectDeviceBtn, new Separator(),
            exportExcelBtn, exportWordBtn, exportPdfBtn, printBtn, searchAdvancedBtn, refreshBtn
        );
        } 
        else{
        buttonPanel1.getChildren().addAll();
        buttonPanel2.getChildren().addAll();
        }
        vBox.getChildren().addAll(buttonPanel1, buttonPanel2);

        return vBox;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5;");
        button.setPrefHeight(35);
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: derive(" + color + ", 20%); -fx-text-fill: white; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5;"));
        return button;
    }

    // ==================== DATABASE LOADING METHODS ====================

    private void loadPurchaseFundRequestsFromDatabase() {
        new Thread(() -> {
            try {
                List<PurchaseFundReplenishRecordModel> requests = databaseConnector.getAllPurchaseFundReplenishRequests();
                Platform.runLater(() -> {
                    purchaseFundReplenishRequestData.clear();
                    purchaseFundReplenishRequestData.addAll(requests);
                    System.out.println("Loaded " + requests.size() + " purchase fund requests from database");
                    updateSummary();
                    
                    if (!purchaseFundReplenishRequestData.isEmpty()) {
                        purchaseFundReplenishRequestTable.getSelectionModel().selectFirst();
                        updateButtonStates(purchaseFundReplenishRequestTable.getSelectionModel().getSelectedItem());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> 
                    showAlert("Database Error", "Failed to load purchase fund requests: " + e.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    private void loadEmployeeSignaturesFromDatabase() {
        new Thread(() -> {
            try {
                List<EmployeeSignatureModel> employees = databaseConnector.getAllEmployees();
                Platform.runLater(() -> {
                    employeeSignatureData.clear();
                    employeeSignatureData.addAll(employees);
                    System.out.println("Loaded " + employees.size() + " employee signatures from database");
                });
            } catch (Exception e) {
                Platform.runLater(() -> 
                    showAlert("Database Error", "Failed to load employee signatures: " + e.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    // ==================== DEVICE MANAGEMENT ====================

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
                operationStatusLabel.setText("Device connected successfully");
                operationStatusLabel.setStyle("-fx-text-fill: #27ae60;");
            } else {
                deviceStatusLabel.setText("Device Status: " + message);
                deviceStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                connectDeviceBtn.setDisable(false);
                disconnectDeviceBtn.setDisable(true);
                operationStatusLabel.setText("Device disconnected");
                operationStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });
    }

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
                    showAlert("Success", "Fingerprint device connected successfully!", Alert.AlertType.INFORMATION);
                } else {
                    updateDeviceStatus(false, "Connection Failed");
                    showAlert("Error", 
                        "Failed to connect to fingerprint device.\n\n" +
                        "Please check:\n" +
                        "• Device is properly connected via USB\n" +
                        "• Drivers are installed correctly\n" +
                        "• Device is not being used by another application", 
                        Alert.AlertType.ERROR);
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
                showAlert("Disconnected", "Fingerprint device disconnected successfully.", Alert.AlertType.INFORMATION);
            });
        }).start();
    }

    private void showDeviceNotConnectedAlert() {
        showAlert("Device Not Connected", 
            "Please connect to the fingerprint device first.\n\n" +
            "Click 'Connect Device' to establish connection.\n" +
            "Required for biometric signature verification.",
            Alert.AlertType.WARNING);
    }

    // ==================== CRUD OPERATIONS ====================

private void showPurchaseFundRequestForm() {
    Dialog<PurchaseFundReplenishRecordModel> dialog = new Dialog<>();
    dialog.setTitle("New Purcahse Fund Request");
    dialog.setHeaderText("Create New Purchase Fund Request for Afran General Hospital");
    dialog.getDialogPane().setPrefSize(900, 500);

    VBox mainContent = new VBox(15);
    mainContent.setPadding(new Insets(20));

    GridPane grid = new GridPane();
    grid.setHgap(15);
    grid.setVgap(15);
    grid.setPadding(new Insets(30));

    // Create labels with proper sizing
    Label requisitionUnitLabel = new Label("Requisition Unit*:");
    requisitionUnitLabel.setPrefWidth(120);
    requisitionUnitLabel.setWrapText(true);
    
    Label reasonLabel = new Label("Reason*:");
    reasonLabel.setPrefWidth(120);
    reasonLabel.setWrapText(true);
    
    Label payeeLabel = new Label("Payee*:");
    payeeLabel.setPrefWidth(120);
    payeeLabel.setWrapText(true);
    
    Label amountLabel = new Label("Amount*:");
    amountLabel.setPrefWidth(120);
    amountLabel.setWrapText(true);
    
    Label requestDateLabel = new Label("Request Date:");
    requestDateLabel.setPrefWidth(120);
    requestDateLabel.setWrapText(true);

    TextField requisitionUnitField = new TextField();
    requisitionUnitField.setPromptText("e.g., HR Department");
    
    TextArea reasonField = new TextArea();
    reasonField.setPrefRowCount(3);
    reasonField.setWrapText(true);
    reasonField.setPromptText("Enter reason for purchase fund request...");
    reasonField.setFont(AmharicFontLoader.getAmharicFont(22f));

    ComboBox<String> payeeComboBox = new ComboBox<>();
    payeeComboBox.setPromptText("Select payee name...");
    payeeComboBox.setEditable(false);

    List<String> employeeNames = databaseConnector.getEmployeeSignatureNames(currentUser);
    payeeComboBox.getItems().addAll(employeeNames);

    TextField amountField = new TextField();
    amountField.setPromptText("Enter amount in ETB");

    DatePicker requestDateField = new DatePicker(LocalDate.now());
    requestDateField.setDisable(true);

    // Add rows with properly sized labels
    grid.addRow(0, requisitionUnitLabel, requisitionUnitField);
    grid.addRow(1, reasonLabel, reasonField);
    grid.addRow(2, payeeLabel, payeeComboBox);
    grid.addRow(3, amountLabel, amountField);
    grid.addRow(4, requestDateLabel, requestDateField);

    // Set column constraints to prevent label truncation
    ColumnConstraints labelCol = new ColumnConstraints();
    labelCol.setPrefWidth(130);
    labelCol.setHgrow(Priority.NEVER);
    
    ColumnConstraints fieldCol = new ColumnConstraints();
    fieldCol.setPrefWidth(300);
    fieldCol.setHgrow(Priority.ALWAYS);
    
    grid.getColumnConstraints().addAll(labelCol, fieldCol);

    mainContent.getChildren().addAll(
        new Label("Please fill in all required fields (*):"),
        grid
    );

    ScrollPane scrollPane = new ScrollPane(mainContent);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setStyle("-fx-background-color: transparent;");
    
    dialog.getDialogPane().setContent(scrollPane);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
    okButton.setDisable(true);

    Runnable validateForm = () -> {
        boolean isValid = !requisitionUnitField.getText().trim().isEmpty() &&
                        !reasonField.getText().trim().isEmpty() &&
                        payeeComboBox.getValue() != null &&
                        !amountField.getText().trim().isEmpty() &&
                        amountField.getText().matches("\\d+(\\.\\d{1,2})?");
        okButton.setDisable(!isValid);
    };

    requisitionUnitField.textProperty().addListener((obs, old, now) -> validateForm.run());
    reasonField.textProperty().addListener((obs, old, now) -> validateForm.run());
    payeeComboBox.valueProperty().addListener((obs, old, now) -> validateForm.run());
    amountField.textProperty().addListener((obs, old, now) -> validateForm.run());

    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == ButtonType.OK) {
            String requestId = generateRequestId();
            return new PurchaseFundReplenishRecordModel(
                requestId,
                requisitionUnitField.getText(),
                reasonField.getText(),
                payeeComboBox.getValue(),
                Double.parseDouble(amountField.getText()),
                LocalDate.now(),
                "Pending", "", "Pending", "", "No", "", "No", "", "Pending", ""
            );
        }
        return null;
    });

    Optional<PurchaseFundReplenishRecordModel> result = dialog.showAndWait();
    result.ifPresent(newRecord -> {
        boolean saved = databaseConnector.savePurchaseFundReplenishRequest(newRecord);
        if (saved) {
            purchaseFundReplenishRequestData.add(0, newRecord);
            purchaseFundReplenishRequestTable.getSelectionModel().select(newRecord);
            updateSummary();
            showAlert("Success", "New purchase fund request created successfully!\nRequest ID: " + newRecord.getRequestId(), Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to save request to database.", Alert.AlertType.ERROR);
        }
    });
}

private String generateRequestId() {
    String prefix = "PFREPLN"; // Replenish prefix
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String datePart = LocalDate.now().format(dateFormatter);
    
    int maxNumber = purchaseFundReplenishRequestData.stream()
        .filter(record -> record.getRequestId() != null && record.getRequestId().startsWith(prefix + datePart))
        .map(record -> record.getRequestId())
        .map(id -> id.substring(prefix.length() + datePart.length()))
        .filter(part -> part.matches("\\d+"))
        .mapToInt(Integer::parseInt)
        .max()
        .orElse(0);
    
    return prefix + datePart + String.format("%03d", maxNumber + 1);
    // Examples: PFREPLN20231215001, PFREPLN20231215002
}


//    private void editSelected() {
//        PurcahseFundReplenishRecordModel selected = purchaseFundReplenishRequestTable.getSelectionModel().getSelectedItem();
//        if (selected == null) {
//            showAlert("No Selection", "Please select a request to edit.", Alert.AlertType.WARNING);
//            return;
//        }
//        
//        if ("Yes".equalsIgnoreCase(selected.getVoidStatus())) {
//            showAlert("Cannot Edit", "Voided requests cannot be edited.", Alert.AlertType.WARNING);
//            return;
//        }
//        
//        if ("Yes".equalsIgnoreCase(selected.getDispensedStatus())) {
//            showAlert("Cannot Edit", "Dispensed requests cannot be edited.", Alert.AlertType.WARNING);
//            return;
//        }
//        
//        Dialog<Boolean> dialog = new Dialog<>();
//        dialog.setTitle("Edit Purchase Fund Request");
//        dialog.setHeaderText("Edit Request: " + selected.getRequestId());
//        dialog.getDialogPane().setPrefSize(500, 400);
//
//        VBox content = new VBox(15);
//        content.setPadding(new Insets(20));
//
//        TextField requisitionUnitField = new TextField(selected.getRequisitionUnit());
//        TextArea reasonField = new TextArea(selected.getReason());
//        reasonField.setPrefRowCount(3);
//        reasonField.setWrapText(true);
//        
//        ComboBox<String> payeeComboBox = new ComboBox<>();
//        payeeComboBox.setPromptText("Select payee name...");
//        List<String> employeeNames = databaseConnector.getEmployeeSignatureNames();
//        payeeComboBox.getItems().addAll(employeeNames);
//        payeeComboBox.setValue(selected.getPayee());
//        
//        TextField amountField = new TextField(String.valueOf(selected.getAmountRequested()));
//
//        content.getChildren().addAll(
//            new Label("Requisition Unit:"), requisitionUnitField,
//            new Label("Reason:"), reasonField,
//            new Label("Payee:"), payeeComboBox,
//            new Label("Amount:"), amountField
//        );
//
//        ScrollPane scrollPane = new ScrollPane(content);
//        scrollPane.setFitToWidth(true);
//        
//        dialog.getDialogPane().setContent(scrollPane);
//        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
//
//        dialog.setResultConverter(dialogButton -> {
//            if (dialogButton == ButtonType.OK) {
//                return true;
//            }
//            return false;
//        });
//
//        dialog.showAndWait().ifPresent(confirmed -> {
//            if (confirmed) {
//                selected.setRequisitionUnit(requisitionUnitField.getText());
//                selected.setReason(reasonField.getText());
//                selected.setPayee(payeeComboBox.getValue());
//                selected.setAmountRequested(Double.parseDouble(amountField.getText()));
//                
//                boolean updated = databaseConnector.updatePurchaseFundRequest(selected);
//                if (updated) {
//                    purchasefundReplenishRequestTable.refresh();
//                    updateSummary();
//                    showAlert("Success", "Request updated successfully!", Alert.AlertType.INFORMATION);
//                } else {
//                    showAlert("Error", "Failed to update request in database.", Alert.AlertType.ERROR);
//                }
//            }
//        });
//    }
    
    //    private void deleteSelected() {
//        PurchaseFundReplenishRecordModel selected = purchaseFundReplenishRequestTable.getSelectionModel().getSelectedItem();
//        if (selected == null) {
//            showAlert("No Selection", "Please select a request to delete.", Alert.AlertType.WARNING);
//            return;
//        }
//
//        if ("Yes".equalsIgnoreCase(selected.getDispensedStatus())) {
//            showAlert("Cannot Delete", "Dispensed requests cannot be deleted.", Alert.AlertType.WARNING);
//            return;
//        }
//        
//        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
//        confirmation.setTitle("Confirm Delete");
//        confirmation.setHeaderText("Delete Purchase Fund Request");
//        confirmation.setContentText("Are you sure you want to permanently delete request " + selected.getRequestId() + "?\n\n" +
//                                  "Request: " + selected.getReason() + "\n" +
//                                  "Amount: ETB " + selected.getAmountRequested() + "\n\n" +
//                                  "This action cannot be undone!");
//        
//        Optional<ButtonType> result = confirmation.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            boolean deleted = databaseConnector.deletePurchaseFundRequest(selected.getRequestId());
//            if (deleted) {
//                purchaseFundReplenishRequestData.remove(selected);
//                updateSummary();
//                showAlert("Success", "Request deleted successfully!", Alert.AlertType.INFORMATION);
//            } else {
//                showAlert("Error", "Failed to delete request from database.", Alert.AlertType.ERROR);
//            }
//        }
//    }

private void voidSelected() {
    PurchaseFundReplenishRecordModel selected = purchaseFundReplenishRequestTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showAlert("No Selection", "Please select a request to void.", Alert.AlertType.WARNING);
        return;
    }

    if ("Yes".equalsIgnoreCase(selected.getVoidStatus())) {
        showAlert("Already Voided", "This request has already been voided.", Alert.AlertType.WARNING);
        return;
    }
    
    if ("Yes".equalsIgnoreCase(selected.getDispensedStatus())) {
        showAlert("Cannot Void", "Dispensed requests cannot be voided.", Alert.AlertType.WARNING);
        return;
    }

    // Create dialog for void reason
    Dialog<String> dialog = new Dialog<>();
    dialog.setTitle("Void Purchase Fund Request");
    dialog.setHeaderText("Enter void reason for request: " + selected.getRequestId());
    
    // Set the button types
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    
    // Create the reason text area
    TextArea reasonTextArea = new TextArea();
    reasonTextArea.setPromptText("Enter reason for voiding this request (required)...");
    reasonTextArea.setPrefRowCount(4);
    reasonTextArea.setWrapText(true);
    reasonTextArea.setPrefWidth(400);
    
    // Create mandatory indicator label
    Label voidReasonLabel = new Label("Void Reason*:");
    voidReasonLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    
    // Create layout
    VBox content = new VBox(10);
    content.setPadding(new Insets(20));
    content.getChildren().addAll(
        new Label("Request Details:"),
        new Label("• Reason: " + selected.getReason()),
        new Label("• Amount: ETB " + selected.getAmountRequested()),
        new Label("• Payee: " + selected.getPayee()),
        new Separator(),
        voidReasonLabel,
        new Label("This field is required to void the request"),
        reasonTextArea
    );
    
    dialog.getDialogPane().setContent(content);
    
    // Enable/disable OK button based on input
    Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
    okButton.setDisable(true);
    
    reasonTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
        boolean isEmpty = newValue.trim().isEmpty();
        okButton.setDisable(isEmpty);
        
        // Optional: Visual feedback
        if (isEmpty) {
            reasonTextArea.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
        } else {
            reasonTextArea.setStyle("-fx-border-color: green; -fx-border-width: 1px;");
        }
    });
    
    // Set result converter
    dialog.setResultConverter(dialogButton -> {
        if (dialogButton == ButtonType.OK) {
            return reasonTextArea.getText().trim();
        }
        return null;
    });
    
    Optional<String> result = dialog.showAndWait();
    
    if (result.isPresent() && !result.get().isEmpty()) {
        String voidReason = result.get();
        
        selected.setVoidStatus("Yes");
        selected.setVoidedBy(currentUser);
        
        boolean updated = databaseConnector.voidPurchaseFundReplenishRequest(
            selected.getRequestId(), 
            currentUser, 
            voidReason
        );
        
        if (updated) {
            purchaseFundReplenishRequestTable.refresh();
            updateButtonStates(selected);
            updateSummary();
            showAlert("Success", "Request voided successfully!\nReason: " + voidReason, Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to void request in database.", Alert.AlertType.ERROR);
            // Revert the changes if database update failed
            selected.setVoidStatus("No");
            selected.setVoidedBy("");
        }
    } else if (result.isPresent() && result.get().isEmpty()) {
        // This shouldn't happen due to button disable, but just in case
        showAlert("Required Field", "Void reason is required to proceed.", Alert.AlertType.WARNING);
    }
}

    private void viewSelectedDetails() {
        PurchaseFundReplenishRecordModel selected = purchaseFundReplenishRequestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a request to view details.", Alert.AlertType.WARNING);
            return;
        }
        viewRecordDetails(selected);
    }

    // ==================== WORKFLOW METHODS - FOLLOWING THE SPECIFIED FLOW ====================

    private void approveRequestWithBiometric() {
        PurchaseFundReplenishRecordModel selected = purchaseFundReplenishRequestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a request to approve.", Alert.AlertType.WARNING);
            return;
        }

        if ("Yes".equalsIgnoreCase(selected.getVoidStatus())) {
            showAlert("Invalid Action", "You cannot approve a voided request.", Alert.AlertType.WARNING);
            return;
        }

        if ("Approved".equalsIgnoreCase(selected.getApprovalStatus())) {
            showAlert("Already Approved", "This request has already been approved.", Alert.AlertType.INFORMATION);
            return;
        }

        showRequestApprovalWithBiometricDialog(selected);
    }

    private void confirmWithBiometric() {
        PurchaseFundReplenishRecordModel selected = purchaseFundReplenishRequestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a request to confirm.", Alert.AlertType.WARNING);
            return;
        }

        if ("Yes".equalsIgnoreCase(selected.getVoidStatus())) {
            showAlert("Invalid Action", "You cannot confirm a voided request.", Alert.AlertType.WARNING);
            return;
        }

        if (!"Approved".equalsIgnoreCase(selected.getApprovalStatus())) {
            showAlert("Not Approved", "Request must be approved before confirmation.", Alert.AlertType.ERROR);
            return;
        }

        if ("Confirmed".equalsIgnoreCase(selected.getConfirmationStatus())) {
            showAlert("Already Confirmed", "This request has already been confirmed.", Alert.AlertType.INFORMATION);
            return;
        }

        showConfirmationWithBiometricDialog(selected);
    }

  private void approveDispenseWithBiometric() {
    PurchaseFundReplenishRecordModel selected = purchaseFundReplenishRequestTable.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showAlert("No Selection", "Please select a request to approve for dispense.", Alert.AlertType.WARNING);
        return;
    }

    if ("Yes".equalsIgnoreCase(selected.getVoidStatus())) {
        showAlert("Invalid Action", "You cannot approve a voided request.", Alert.AlertType.WARNING);
        return;
    }

    if (!"Yes".equalsIgnoreCase(selected.getDispensedStatus())) {
        showAlert("Not Dispensed", "Request must be dispensed before dispense approval.", Alert.AlertType.ERROR);
        return;
    }

    if ("Approved".equalsIgnoreCase(selected.getDispenseApprovalStatus())) {
        showAlert("Already Approved", "This dispense has already been approved.", Alert.AlertType.INFORMATION);
        return;
    }

    // Check if there's already an active replenishment fund in use
    if (databaseConnector.hasActiveReplenishmentFundInUse()) {
        showAlert("Active Fund in Use", 
            "Cannot approve new dispense while there is an active fund in use.\n\n" +
            "Please wait until the current active fund is fully utilized or closed before approving a new dispense.",
            Alert.AlertType.ERROR);
        return;
    }

    showDispenseApprovalWithBiometricDialog(selected);
}

    // ==================== BIOMETRIC DIALOGS ====================

    private void showRequestApprovalWithBiometricDialog(PurchaseFundReplenishRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Approve Request with Biometric Signature");
        dialog.setHeaderText("Approve Purchase Fund Request - " + record.getRequestId());
        dialog.getDialogPane().setPrefSize(700, 600);

        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));

        VBox requestDetails = createRequestDetailsSection(record);

        VBox approvalSection = new VBox(10);
        approvalSection.setStyle(
            "-fx-background-color: #fff3cd; " +
            "-fx-padding: 15; " +
            "-fx-border-color: #ffc107; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5;"
        );

        Label approvalTitle = new Label("REQUEST APPROVAL AUTHORIZATION");
        approvalTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #856404;");

        Label instruction = new Label("Approver must verify identity using fingerprint to authorize this request");
        instruction.setStyle("-fx-text-fill: #856404;");

        Button approveWithFingerprintBtn = createStyledButton("Verify Approver Identity & Load Signature", "#f39c12");
        ImageView approverSignatureView = new ImageView();
        approverSignatureView.setFitWidth(300);
        approverSignatureView.setFitHeight(150);
        approverSignatureView.setPreserveRatio(true);
        approverSignatureView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-border-radius: 5;");

        Label approverStatusLabel = new Label("Approver signature not loaded");
        approverStatusLabel.setStyle("-fx-text-fill: #e74c3c;");

        AtomicReference<EmployeeSignatureModel> verifiedApproverRef = new AtomicReference<>();
        AtomicReference<byte[]> approverFingerprintRef = new AtomicReference<>();

        approveWithFingerprintBtn.setOnAction(e -> {
            showFingerprintVerificationDialog(employee -> {
                if (employee != null) {
                    verifiedApproverRef.set(employee);
                    approverFingerprintRef.set(employee.getFingerprintTemplate());
                    
                    if (employee.getSignatureImage() != null && employee.getSignatureImage().length > 0) {
                        try {
                            Image signatureImage = convertByteArrayToImage(employee.getSignatureImage());
                            approverSignatureView.setImage(signatureImage);
                            approverStatusLabel.setText("✓ Approved by: " + employee.getEmployeeName());
                            approverStatusLabel.setStyle("-fx-text-fill: #27ae60;");
                        } catch (Exception ex) {
                            approverStatusLabel.setText("✗ Error loading approver signature");
                            approverStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
                        }
                    } else {
                        approverStatusLabel.setText("✗ No signature available for approver");
                        approverStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    }
                }
            });
        });

        approvalSection.getChildren().addAll(
            approvalTitle, instruction, approveWithFingerprintBtn,
            approverSignatureView, approverStatusLabel
        );

        TextArea approvalNotes = new TextArea();
        approvalNotes.setPromptText("Enter approval notes or comments (optional)...");
        approvalNotes.setPrefRowCount(3);
        approvalNotes.setWrapText(true);

        mainContent.getChildren().addAll(
            requestDetails, new Separator(), 
            approvalSection, new Separator(),
            new Label("Approval Notes:"), approvalNotes
        );

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        ButtonType approveButtonType = new ButtonType("APPROVE REQUEST", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(approveButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(scrollPane);

        Node okButton = dialog.getDialogPane().lookupButton(approveButtonType);
        okButton.setDisable(true);

        approverStatusLabel.textProperty().addListener((obs, old, now) -> {
            boolean isVerified = now.startsWith("✓");
            okButton.setDisable(!isVerified);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == approveButtonType) {
                return ButtonType.OK;
            }
            return null;
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            EmployeeSignatureModel approver = verifiedApproverRef.get();
            
            record.setApprovalStatus("Approved");
            record.setApprovedBy(approver.getEmployeeName());
            
            saveRequestApprovalWithBiometric(record, approver, approverFingerprintRef.get(), approvalNotes.getText());
            
            showAlert("Approval Successful",
                "Request approved successfully with biometric verification!\n\n" +
                "Request ID: " + record.getRequestId() + "\n" +
                "Approved by: " + approver.getEmployeeName() + "\n" +
                "Amount: ETB " + record.getAmountRequested() + "\n" +
                "Approver Signature: Digitally verified and stored",
                Alert.AlertType.INFORMATION);
        }
    }

    private void showDispenseApprovalWithBiometricDialog(PurchaseFundReplenishRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Approve Dispense with Biometric Signature");
        dialog.setHeaderText("Approve Purchase Fund Dispense - " + record.getRequestId());
        dialog.getDialogPane().setPrefSize(700, 600);

        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));

        VBox requestDetails = createRequestDetailsSection(record);

        VBox approvalSection = new VBox(10);
        approvalSection.setStyle(
            "-fx-background-color: #fff3cd; " +
            "-fx-padding: 15; " +
            "-fx-border-color: #ffc107; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5;"
        );

        Label approvalTitle = new Label("DISPENSE APPROVAL AUTHORIZATION");
        approvalTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #856404;");

        Label instruction = new Label("Approver must verify identity using fingerprint to authorize this dispense");
        instruction.setStyle("-fx-text-fill: #856404;");

        Button approveWithFingerprintBtn = createStyledButton("Verify Approver Identity & Load Signature", "#f39c12");
        ImageView approverSignatureView = new ImageView();
        approverSignatureView.setFitWidth(300);
        approverSignatureView.setFitHeight(150);
        approverSignatureView.setPreserveRatio(true);
        approverSignatureView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-border-radius: 5;");

        Label approverStatusLabel = new Label("Approver signature not loaded");
        approverStatusLabel.setStyle("-fx-text-fill: #e74c3c;");

        AtomicReference<EmployeeSignatureModel> verifiedApproverRef = new AtomicReference<>();
        AtomicReference<byte[]> approverFingerprintRef = new AtomicReference<>();

        approveWithFingerprintBtn.setOnAction(e -> {
            showFingerprintVerificationDialog(employee -> {
                if (employee != null) {
                    verifiedApproverRef.set(employee);
                    approverFingerprintRef.set(employee.getFingerprintTemplate());
                    
                    if (employee.getSignatureImage() != null && employee.getSignatureImage().length > 0) {
                        try {
                            Image signatureImage = convertByteArrayToImage(employee.getSignatureImage());
                            approverSignatureView.setImage(signatureImage);
                            approverStatusLabel.setText("✓ Dispense Approved by: " + employee.getEmployeeName());
                            approverStatusLabel.setStyle("-fx-text-fill: #27ae60;");
                        } catch (Exception ex) {
                            approverStatusLabel.setText("✗ Error loading approver signature");
                            approverStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
                        }
                    } else {
                        approverStatusLabel.setText("✗ No signature available for approver");
                        approverStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    }
                }
            });
        });

        approvalSection.getChildren().addAll(
            approvalTitle, instruction, approveWithFingerprintBtn,
            approverSignatureView, approverStatusLabel
        );

        TextArea approvalNotes = new TextArea();
        approvalNotes.setPromptText("Enter dispense approval notes or comments (optional)...");
        approvalNotes.setPrefRowCount(3);
        approvalNotes.setWrapText(true);

        mainContent.getChildren().addAll(
            requestDetails, new Separator(), 
            approvalSection, new Separator(),
            new Label("Dispense Approval Notes:"), approvalNotes
        );

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        ButtonType approveButtonType = new ButtonType("APPROVE DISPENSE", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(approveButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(scrollPane);

        Node okButton = dialog.getDialogPane().lookupButton(approveButtonType);
        okButton.setDisable(true);

        approverStatusLabel.textProperty().addListener((obs, old, now) -> {
            boolean isVerified = now.startsWith("✓");
            okButton.setDisable(!isVerified);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == approveButtonType) {
                return ButtonType.OK;
            }
            return null;
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            EmployeeSignatureModel approver = verifiedApproverRef.get();
            
            record.setDispenseApprovalStatus("Approved");
            record.setDispenseApprovedBy(approver.getEmployeeName());
            
            saveDispenseApprovalWithBiometric(record, approver, approverFingerprintRef.get(), approvalNotes.getText());
            
            showAlert("Dispense Approval Successful",
                "Dispense approved successfully with biometric verification!\n\n" +
                "Request ID: " + record.getRequestId() + "\n" +
                "Approved by: " + approver.getEmployeeName() + "\n" +
                "Amount: ETB " + record.getAmountRequested() + "\n" +
                "Approver Signature: Digitally verified and stored",
                Alert.AlertType.INFORMATION);
        }
    }

    private void showConfirmationWithBiometricDialog(PurchaseFundReplenishRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirm Request with Biometric Signature");
        dialog.setHeaderText("Confirm Purchase Fund Request - " + record.getRequestId());
        dialog.getDialogPane().setPrefSize(700, 600);

        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));

        VBox requestDetails = createRequestDetailsSection(record);

        VBox confirmationSection = new VBox(10);
        confirmationSection.setStyle(
            "-fx-background-color: #d1ecf1; " +
            "-fx-padding: 15; " +
            "-fx-border-color: #17a2b8; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5;"
        );

        Label confirmationTitle = new Label("CONFIRMATION AUTHORIZATION");
        confirmationTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #0c5460;");

        Label instruction = new Label("Confirmer must verify identity using fingerprint to confirm this request");
        instruction.setStyle("-fx-text-fill: #0c5460;");

        Button confirmWithFingerprintBtn = createStyledButton("Verify Confirmer Identity & Load Signature", "#17a2b8");
        ImageView confirmerSignatureView = new ImageView();
        confirmerSignatureView.setFitWidth(300);
        confirmerSignatureView.setFitHeight(150);
        confirmerSignatureView.setPreserveRatio(true);
        confirmerSignatureView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-border-radius: 5;");

        Label confirmerStatusLabel = new Label("Confirmer signature not loaded");
        confirmerStatusLabel.setStyle("-fx-text-fill: #e74c3c;");

        AtomicReference<EmployeeSignatureModel> verifiedConfirmerRef = new AtomicReference<>();
        AtomicReference<byte[]> confirmerFingerprintRef = new AtomicReference<>();

        confirmWithFingerprintBtn.setOnAction(e -> {
            showFingerprintVerificationDialog(employee -> {
                if (employee != null) {
                    verifiedConfirmerRef.set(employee);
                    confirmerFingerprintRef.set(employee.getFingerprintTemplate());
                    
                    if (employee.getSignatureImage() != null && employee.getSignatureImage().length > 0) {
                        try {
                            Image signatureImage = convertByteArrayToImage(employee.getSignatureImage());
                            confirmerSignatureView.setImage(signatureImage);
                            confirmerStatusLabel.setText("✓ Confirmed by: " + employee.getEmployeeName());
                            confirmerStatusLabel.setStyle("-fx-text-fill: #27ae60;");
                        } catch (Exception ex) {
                            confirmerStatusLabel.setText("✗ Error loading confirmer signature");
                            confirmerStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
                        }
                    } else {
                        confirmerStatusLabel.setText("✗ No signature available for confirmer");
                        confirmerStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    }
                }
            });
        });

        confirmationSection.getChildren().addAll(
            confirmationTitle, instruction, confirmWithFingerprintBtn,
            confirmerSignatureView, confirmerStatusLabel
        );

        TextArea confirmationNotes = new TextArea();
        confirmationNotes.setPromptText("Enter confirmation notes or comments (optional)...");
        confirmationNotes.setPrefRowCount(3);
        confirmationNotes.setWrapText(true);

        mainContent.getChildren().addAll(
            requestDetails, new Separator(), 
            confirmationSection, new Separator(),
            new Label("Confirmation Notes:"), confirmationNotes
        );

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        ButtonType confirmButtonType = new ButtonType("CONFIRM REQUEST", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(scrollPane);

        Node okButton = dialog.getDialogPane().lookupButton(confirmButtonType);
        okButton.setDisable(true);

        confirmerStatusLabel.textProperty().addListener((obs, old, now) -> {
            boolean isVerified = now.startsWith("✓");
            okButton.setDisable(!isVerified);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return ButtonType.OK;
            }
            return null;
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            EmployeeSignatureModel confirmer = verifiedConfirmerRef.get();
            
            record.setConfirmationStatus("Confirmed");
            record.setConfirmedBy(confirmer.getEmployeeName());
            
            saveConfirmationWithBiometric(record, confirmer, confirmerFingerprintRef.get(), confirmationNotes.getText());
            
            showAlert("Confirmation Successful",
                "Request confirmed successfully with biometric verification!\n\n" +
                "Request ID: " + record.getRequestId() + "\n" +
                "Confirmed by: " + confirmer.getEmployeeName() + "\n" +
                "Amount: ETB " + record.getAmountRequested() + "\n" +
                "Confirmer Signature: Digitally verified and stored",
                Alert.AlertType.INFORMATION);
        }
    }

    // ==================== DISPENSE WITH DUAL SIGNATURES ====================

    private void showDispenseForm(PurchaseFundReplenishRecordModel record) {
    // First check if there's already an active replenishment fund in use
    if (databaseConnector.hasActiveReplenishmentFundInUse()) {
        showAlert("Active Fund in Use", 
            "Cannot dispense new replenishment fund while there is an active fund in use.\n\n" +
            "Please wait until the current active fund is fully utilized or closed before dispensing a new replenishment.",
            Alert.AlertType.ERROR);
        return;
    }

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Dispense Cash with Dual Biometric Verification");
    dialog.setHeaderText("Record Cash Dispensing - " + record.getRequestId());
    dialog.getDialogPane().setPrefSize(900, 800);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    VBox mainContent = new VBox(15);
    mainContent.setPadding(new Insets(20));

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);

    TextField requestIdField = new TextField(record.getRequestId());
    requestIdField.setDisable(true);
    TextField requisitionUnitField = new TextField(record.getRequisitionUnit());
    requisitionUnitField.setDisable(true);
    TextArea reasonField = new TextArea(record.getReason());
    reasonField.setWrapText(true);
    reasonField.setPrefRowCount(3);
    reasonField.setDisable(true);
    TextField payeeField = new TextField(record.getPayee());
    TextField requestedAmountField = new TextField(String.valueOf(record.getAmountRequested()));
    requestedAmountField.setDisable(true);
    TextField givenAmountField = new TextField(String.valueOf(record.getAmountRequested()));
    DatePicker requestDateField = new DatePicker(record.getRequestDate());
    requestDateField.setDisable(true);
    DatePicker completedDateField = new DatePicker(LocalDate.now());
    TextField givenByField = new TextField();

    grid.addRow(0, new Label("Request ID:"), requestIdField);
    grid.addRow(1, new Label("Requisition Unit:"), requisitionUnitField);
    grid.addRow(2, new Label("Reason:"), reasonField);
    grid.addRow(3, new Label("Payee:"), payeeField);
    grid.addRow(4, new Label("Requested Amount:"), requestedAmountField);
    grid.addRow(5, new Label("Given Amount:"), givenAmountField);
    grid.addRow(6, new Label("Request Date:"), requestDateField);
    grid.addRow(7, new Label("Completed Date:"), completedDateField);
    grid.addRow(8, new Label("Given By:"), givenByField);

    Label biometricSectionTitle = new Label("Dual Biometric Signature Verification");
    biometricSectionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #2c3e50;");

    VBox payeeVerificationSection = createBiometricSection(
        "Payee Identity Verification", 
        "Step 1: Verify payee identity using fingerprint",
        "#3498db"
    );

    VBox dispenserVerificationSection = createBiometricSection(
        "Dispenser Identity Verification",
        "Step 2: Verify dispenser identity using fingerprint", 
        "#9b59b6"
    );

    HBox dualBiometricContainer = new HBox(20);
    dualBiometricContainer.getChildren().addAll(payeeVerificationSection, dispenserVerificationSection);

    AtomicReference<EmployeeSignatureModel> verifiedPayeeRef = new AtomicReference<>();
    AtomicReference<EmployeeSignatureModel> verifiedDispenserRef = new AtomicReference<>();
    AtomicReference<byte[]> payeeFingerprintRef = new AtomicReference<>();
    AtomicReference<byte[]> dispenserFingerprintRef = new AtomicReference<>();

    Button payeeVerifyBtn = (Button) payeeVerificationSection.getChildren().get(1);
    ImageView payeeSignatureView = (ImageView) payeeVerificationSection.getChildren().get(3);
    Label payeeStatusLabel = (Label) payeeVerificationSection.getChildren().get(4);

    payeeVerifyBtn.setOnAction(e -> {
        showFingerprintVerificationDialog(employee -> {
            if (employee != null) {
                verifiedPayeeRef.set(employee);
                payeeFingerprintRef.set(employee.getFingerprintTemplate());
                loadAndDisplaySignature(employee, payeeSignatureView, payeeStatusLabel);
                
                if (payeeField.getText().isEmpty()) {
                    payeeField.setText(employee.getEmployeeName());
                }
            }
        });
    });

    Button dispenserVerifyBtn = (Button) dispenserVerificationSection.getChildren().get(1);
    ImageView dispenserSignatureView = (ImageView) dispenserVerificationSection.getChildren().get(3);
    Label dispenserStatusLabel = (Label) dispenserVerificationSection.getChildren().get(4);

    dispenserVerifyBtn.setOnAction(e -> {
        showFingerprintVerificationDialog(employee -> {
            if (employee != null) {
                verifiedDispenserRef.set(employee);
                dispenserFingerprintRef.set(employee.getFingerprintTemplate());
                loadAndDisplaySignature(employee, dispenserSignatureView, dispenserStatusLabel);
                
                if (givenByField.getText().isEmpty()) {
                    givenByField.setText(employee.getEmployeeName());
                }
            }
        });
    });

    mainContent.getChildren().addAll(
        grid,
        new Separator(),
        biometricSectionTitle,
        dualBiometricContainer
    );

    ScrollPane scrollPane = new ScrollPane(mainContent);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);
    scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 10;");

    dialog.getDialogPane().setContent(scrollPane);
    dialog.setResizable(true);

    Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
    okButton.setDisable(true);

    Runnable validateForm = () -> {
        boolean payeeVerified = verifiedPayeeRef.get() != null;
        boolean dispenserVerified = verifiedDispenserRef.get() != null;
        boolean amountValid = !givenAmountField.getText().trim().isEmpty();
        boolean givenByValid = !givenByField.getText().trim().isEmpty();
        
        okButton.setDisable(!(payeeVerified && dispenserVerified && amountValid && givenByValid));
    };

    givenAmountField.textProperty().addListener((obs, old, now) -> validateForm.run());
    givenByField.textProperty().addListener((obs, old, now) -> validateForm.run());

    Optional<ButtonType> result = dialog.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        EmployeeSignatureModel verifiedPayee = verifiedPayeeRef.get();
        EmployeeSignatureModel verifiedDispenser = verifiedDispenserRef.get();

        record.setDispensedStatus("Yes");
        record.setDispensedBy(verifiedDispenser.getEmployeeName());
        
        DispensedReplenishPurchaseFundModel dispensedRecord = new DispensedReplenishPurchaseFundModel(
            record.getRequestId(),
            record.getRequisitionUnit(),
            record.getReason(),
            verifiedPayee.getEmployeeName(),
            String.valueOf(record.getAmountRequested()),
            givenAmountField.getText(),
            verifiedDispenser.getEmployeeName(),
            record.getRequestDate(),
            completedDateField.getValue(),
            "Dual Biometric Verified"
        );

        // Set fingerprint separately if needed
        dispensedRecord.setFingerprintTemplate(payeeFingerprintRef.get());

        saveDispensedRecordWithDualSignatures(dispensedRecord, 
            verifiedPayee.getSignatureImage(), verifiedDispenser.getSignatureImage(),
            payeeFingerprintRef.get(), dispenserFingerprintRef.get());

        showAlert("Dispense Complete",
            "Cash dispensed successfully with dual biometric verification!\n\n" +
            "Request ID: " + record.getRequestId() + "\n" +
            "Payee: " + verifiedPayee.getEmployeeName() + " (Biometrically Verified)\n" +
            "Dispenser: " + verifiedDispenser.getEmployeeName() + " (Biometrically Verified)\n" +
            "Amount: ETB " + givenAmountField.getText() + "\n" +
            "Signatures: Both digitally verified and stored",
            Alert.AlertType.INFORMATION);
    }
}

    // ==================== FINGERPRINT VERIFICATION ====================

    private void showFingerprintVerificationDialog(BiometricVerificationCallback callback) {
        if (!fingerprintModule.isDeviceActuallyConnected()) {
            showDeviceNotConnectedAlert();
            return;
        }

        Dialog<EmployeeSignatureModel> dialog = new Dialog<>();
        dialog.setTitle("Biometric Signature Verification");
        dialog.setHeaderText("Verify Identity via Fingerprint");
        dialog.getDialogPane().setPrefSize(600, 500);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        ImageView fingerprintView = new ImageView(createFingerprintPlaceholder());
        fingerprintView.setFitWidth(300);
        fingerprintView.setFitHeight(200);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        Label statusLabel = new Label("Ready for fingerprint verification");
        statusLabel.setStyle("-fx-text-fill: #7f8c8d;");

        Button startVerificationBtn = new Button("Start Fingerprint Verification");
        startVerificationBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        AtomicReference<EmployeeSignatureModel> matchedEmployeeRef = new AtomicReference<>();

        startVerificationBtn.setOnAction(e -> {
            progressIndicator.setVisible(true);
            statusLabel.setText("🔍 Scanning fingerprint...");
            statusLabel.setStyle("-fx-text-fill: #3498db;");
            startVerificationBtn.setDisable(true);

            new Thread(() -> {
                try {
                    byte[] capturedTemplate = fingerprintModule.captureFingerprint(30);
                    
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        startVerificationBtn.setDisable(false);

                        if (capturedTemplate != null && capturedTemplate.length > 0) {
                            statusLabel.setText("✓ Fingerprint captured, searching database...");
                            statusLabel.setStyle("-fx-text-fill: #3498db;");
                            fingerprintView.setImage(createSimulatedFingerprintImage());

                            EmployeeSignatureModel matchedEmployee = findEmployeeByFingerprintTemplate(capturedTemplate);
                            
                            if (matchedEmployee != null) {
                                matchedEmployeeRef.set(matchedEmployee);
                                statusLabel.setText("✅ Identity Verified: " + matchedEmployee.getEmployeeName());
                                statusLabel.setStyle("-fx-text-fill: #27ae60;");
                                
                                new Thread(() -> {
                                    try {
                                        Thread.sleep(1500);
                                        Platform.runLater(() -> {
                                            dialog.setResult(matchedEmployee);
                                            dialog.close();
                                        });
                                    } catch (InterruptedException ex) {
                                        Thread.currentThread().interrupt();
                                    }
                                }).start();
                            } else {
                                statusLabel.setText("❌ No matching employee found");
                                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                            }
                        } else {
                            statusLabel.setText("❌ Failed to capture fingerprint");
                            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                        }
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        progressIndicator.setVisible(false);
                        startVerificationBtn.setDisable(false);
                        statusLabel.setText("❌ Error: " + ex.getMessage());
                        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    });
                }
            }).start();
        });

        content.getChildren().addAll(fingerprintView, startVerificationBtn, progressIndicator, statusLabel);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return matchedEmployeeRef.get();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(callback::onVerificationComplete);
    }

    private EmployeeSignatureModel findEmployeeByFingerprintTemplate(byte[] capturedTemplate) {
        try {
            System.out.println("Starting fingerprint template matching...");
            System.out.println("Captured template size: " + (capturedTemplate != null ? capturedTemplate.length : 0) + " bytes");

            if (capturedTemplate == null || capturedTemplate.length == 0) {
                return null;
            }

            List<EmployeeSignatureModel> employeesWithFingerprints = employeeSignatureData.stream()
                .filter(emp -> emp.getFingerprintTemplate() != null && emp.getFingerprintTemplate().length > 0)
                .collect(Collectors.toList());

            System.out.println("Employees with fingerprints: " + employeesWithFingerprints.size());

            for (EmployeeSignatureModel employee : employeesWithFingerprints) {
                byte[] storedTemplate = employee.getFingerprintTemplate();
                
                if (storedTemplate != null && storedTemplate.length > 0) {
                    try {
                        boolean isMatch = fingerprintModule.compareTemplate(storedTemplate, capturedTemplate);
                        
                        if (isMatch) {
                            System.out.println("✅ Fingerprint match found: " + employee.getEmployeeName());
                            return employee;
                        }
                    } catch (Exception e) {
                        System.out.println("Error comparing with " + employee.getEmployeeName() + ": " + e.getMessage());
                    }
                }
            }

            System.out.println("❌ No fingerprint match found");
            return null;

        } catch (Exception e) {
            System.out.println("❌ Error in fingerprint matching: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ==================== DATABASE OPERATIONS ====================

    private void saveDispensedRecordWithDualSignatures(DispensedReplenishPurchaseFundModel record, 
                                                     byte[] payeeSignature, byte[] dispenserSignature,
                                                     byte[] payeeFingerprint, byte[] dispenserFingerprint) {
        new Thread(() -> {
            try {
                boolean success = databaseConnector.saveDispensedPurchaseFundReplenishRecordWithDualSignatures(
                    record, payeeSignature, dispenserSignature, payeeFingerprint, dispenserFingerprint
                );

                Platform.runLater(() -> {
                    if (success) {
                        System.out.println("Dual signature dispense record saved successfully");
                        
                        // Find and update the corresponding PurchaseFundReplenishRecordModel
                        PurchaseFundReplenishRecordModel updatedRecord = findPurchaseFundRecordById(record.getRequestId());
                        if (updatedRecord != null) {
                            updatedRecord.setDispensedStatus("Yes");
                            updatedRecord.setDispensedBy(record.getGivenBy());
                            purchaseFundReplenishRequestTable.refresh();
                            updateButtonStates(updatedRecord);
                        }
                        updateSummary();
                    } else {
                        showAlert("Database Warning", "Record saved but signature storage may have issues.", Alert.AlertType.WARNING);
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> showAlert("Database Error", "Error saving dual signature record: " + ex.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    // Helper method to find PurchaseFundReplenishRecordModel by request ID
    private PurchaseFundReplenishRecordModel findPurchaseFundRecordById(String requestId) {
        return purchaseFundReplenishRequestData.stream()
            .filter(record -> record.getRequestId().equals(requestId))
            .findFirst()
            .orElse(null);
    }

    private void saveRequestApprovalWithBiometric(PurchaseFundReplenishRecordModel record, EmployeeSignatureModel approver, 
                                         byte[] fingerprintTemplate, String notes) {
        new Thread(() -> {
            try {
                boolean success = databaseConnector.savePurchaseFundReplenishRequestApprovalWithBiometric(
                    record, approver, fingerprintTemplate, notes
                );

                Platform.runLater(() -> {
                    if (success) {
                        System.out.println("Biometric approval record saved successfully");
                        purchaseFundReplenishRequestTable.refresh();
                        updateButtonStates(record);
                        updateSummary();
                    } else {
                        showAlert("Database Warning", "Approval saved but biometric data storage may have issues.", Alert.AlertType.WARNING);
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> showAlert("Database Error", "Error saving biometric approval: " + ex.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }
    
    private void saveDispenseApprovalWithBiometric(PurchaseFundReplenishRecordModel record, EmployeeSignatureModel approver, 
                                         byte[] fingerprintTemplate, String notes) {
        new Thread(() -> {
            try {
                boolean success = databaseConnector.savePurchaseFundReplenishDispenseApprovalWithBiometric(
                    record, approver, fingerprintTemplate, notes
                );

                Platform.runLater(() -> {
                    if (success) {
                        System.out.println("Dispense approval biometric record saved successfully");
                        purchaseFundReplenishRequestTable.refresh();
                        updateButtonStates(record);
                        updateSummary();
                    } else {
                        showAlert("Database Warning", "Dispense approval saved but biometric data storage may have issues.", Alert.AlertType.WARNING);
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> showAlert("Database Error", "Error saving dispense biometric approval: " + ex.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    private void saveConfirmationWithBiometric(PurchaseFundReplenishRecordModel record, EmployeeSignatureModel confirmer, 
                                             byte[] fingerprintTemplate, String notes) {
        new Thread(() -> {
            try {
                boolean success = databaseConnector.savePurchaseFundReplenishConfirmationWithBiometric(
                    record, confirmer, fingerprintTemplate, notes
                );

                Platform.runLater(() -> {
                    if (success) {
                        System.out.println("Biometric confirmation record saved successfully");
                        purchaseFundReplenishRequestTable.refresh();
                        updateButtonStates(record);
                        updateSummary();
                    } else {
                        showAlert("Database Warning", "Confirmation saved but biometric data storage may have issues.", Alert.AlertType.WARNING);
                    }
                });

            } catch (Exception ex) {
                Platform.runLater(() -> showAlert("Database Error", "Error saving biometric confirmation: " + ex.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    // ==================== BUTTON STATE MANAGEMENT - FOLLOWING THE SPECIFIED FLOW ====================

    private void updateButtonStates(PurchaseFundReplenishRecordModel selected) {
        if (selected == null) {
            // No selection - disable all workflow buttons but keep CRUD buttons enabled
            approveRequestBtn.setDisable(true);
            confirmBtn.setDisable(true);
            dispenseBtn.setDisable(true);
            approveDispenseBtn.setDisable(true);
            viewDetailsBtn.setDisable(true); // Disable view details when no selection
            voidBtn.setDisable(true);
            return;
        }

        boolean isVoided = "Yes".equalsIgnoreCase(selected.getVoidStatus());
        boolean isDispensed = "Yes".equalsIgnoreCase(selected.getDispensedStatus());
        boolean isApproved = "Approved".equalsIgnoreCase(selected.getApprovalStatus());
        boolean isConfirmed = "Confirmed".equalsIgnoreCase(selected.getConfirmationStatus());
        boolean isDispenseApproved = "Approved".equalsIgnoreCase(selected.getDispenseApprovalStatus());

        // FOLLOW THE SPECIFIED WORKFLOW:
        // 1. New Request: Only Approve Request button active
        // 2. After Approval: Only Dispense button active
        // 3. After Dispense: Only Confirm button active  
        // 4. After Confirm: Only Approve Dispense button active
        // 5. After Approve Dispense: All workflow buttons disabled

        // STEP 1: New Request - Only Approve Request button active
        if (!isApproved && !isConfirmed && !isDispensed && !isDispenseApproved && !isVoided) {
            approveRequestBtn.setDisable(false);
            confirmBtn.setDisable(true);
            dispenseBtn.setDisable(true);
            approveDispenseBtn.setDisable(true);
        }
        // STEP 2: After Approval - Only Dispense button active
        else if (isApproved && !isConfirmed && !isDispensed && !isDispenseApproved && !isVoided) {
            approveRequestBtn.setDisable(true);
            confirmBtn.setDisable(true);
            dispenseBtn.setDisable(false);
            approveDispenseBtn.setDisable(true);
        }
        // STEP 3: After Dispense - Only Confirm button active
        else if (isApproved && !isConfirmed && isDispensed && !isDispenseApproved && !isVoided) {
            approveRequestBtn.setDisable(true);
            confirmBtn.setDisable(false);
            dispenseBtn.setDisable(true);
            approveDispenseBtn.setDisable(true);
        }
        // STEP 4: After Confirm - Only Approve Dispense button active
        else if (isApproved && isConfirmed && isDispensed && !isDispenseApproved && !isVoided) {
            approveRequestBtn.setDisable(true);
            confirmBtn.setDisable(true);
            dispenseBtn.setDisable(true);
            approveDispenseBtn.setDisable(false);
        }
        // STEP 5: After Approve Dispense - All workflow buttons disabled
        else if (isApproved && isConfirmed && isDispensed && isDispenseApproved && !isVoided) {
            approveRequestBtn.setDisable(true);
            confirmBtn.setDisable(true);
            dispenseBtn.setDisable(true);
            approveDispenseBtn.setDisable(true);
        }
        // Voided request - all workflow buttons disabled
        else if (isVoided) {
            approveRequestBtn.setDisable(true);
            confirmBtn.setDisable(true);
            dispenseBtn.setDisable(true);
            approveDispenseBtn.setDisable(true);
        }

        // CRUD buttons state - always enable view details when a row is selected
        viewDetailsBtn.setDisable(false);
        voidBtn.setDisable(isVoided || isDispensed);

        updateButtonTooltips(selected);
    }

    private void updateButtonTooltips(PurchaseFundReplenishRecordModel selected) {
        if (selected == null) return;

        boolean isVoided = "Yes".equalsIgnoreCase(selected.getVoidStatus());
        boolean isDispensed = "Yes".equalsIgnoreCase(selected.getDispensedStatus());
        boolean isApproved = "Approved".equalsIgnoreCase(selected.getApprovalStatus());
        boolean isConfirmed = "Confirmed".equalsIgnoreCase(selected.getConfirmationStatus());
        boolean isDispenseApproved = "Approved".equalsIgnoreCase(selected.getDispenseApprovalStatus());

        if (isVoided) {
            approveRequestBtn.setTooltip(new Tooltip("Cannot approve voided request"));
            confirmBtn.setTooltip(new Tooltip("Cannot confirm voided request"));
            approveDispenseBtn.setTooltip(new Tooltip("Cannot approve dispense for voided request"));
            dispenseBtn.setTooltip(new Tooltip("Cannot dispense voided request"));
        } else if (!isApproved) {
            confirmBtn.setTooltip(new Tooltip("Request must be approved first"));
            dispenseBtn.setTooltip(new Tooltip("Request must be approved first"));
            approveDispenseBtn.setTooltip(new Tooltip("Request must go through full workflow first"));
        } else if (!isDispensed) {
            confirmBtn.setTooltip(new Tooltip("Request must be dispensed first"));
            approveDispenseBtn.setTooltip(new Tooltip("Request must be dispensed and confirmed first"));
        } else if (!isConfirmed) {
            approveDispenseBtn.setTooltip(new Tooltip("Request must be confirmed first"));
        } else if (isDispenseApproved) {
            approveDispenseBtn.setTooltip(new Tooltip("Dispense already approved"));
        } else {
            approveRequestBtn.setTooltip(new Tooltip("Approve this request"));
            confirmBtn.setTooltip(new Tooltip("Confirm this request"));
            dispenseBtn.setTooltip(new Tooltip("Dispense cash for this request"));
            approveDispenseBtn.setTooltip(new Tooltip("Approve this dispense"));
        } 
    }

    // ==================== DOUBLE CLICK ROW DETAILS ====================

private void viewRecordDetails(PurchaseFundReplenishRecordModel record) {
    if (record == null) {
        showAlert("Error", "No record selected to view details.", Alert.AlertType.ERROR);
        return;
    }

    try {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Request Details - " + getSafeString(record.getRequestId()));
        dialog.setHeaderText(null); // We'll use a custom header
        dialog.getDialogPane().setPrefSize(1000, 700);
        dialog.setResizable(true);

        // ===== HEADER SECTION =====
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(10, 20, 10, 20));
        headerBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        // Left: Logo
        ImageView logoView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/icons/logo.png"));
            logoView.setImage(logoImage);
        } catch (Exception e) {
            // Use a placeholder if logo not found
            logoView.setStyle("-fx-background-color: #f0f0f0; -fx-min-width: 100; -fx-min-height: 50;");
        }
          logoView.setFitWidth(250);
          logoView.setFitHeight(250);
//        logoView.setPreserveRatio(true);

        // Center: Hospital Name & Info
        VBox hospitalInfo = new VBox(2);
        hospitalInfo.setAlignment(Pos.CENTER);
        Label name2 = new Label("አፍራን አጠቃላይ ሆስፒታል");
        name2.setStyle("-fx-font-weight: bolder;-fx-text-fill:orange");
        name2.setFont(AmharicFontLoader.getAmharicFont(26f));
        Label name = new Label("AFRAN GENERAL HOSPITAL");
        name.setStyle("-fx-font-size: 22px; -fx-font-weight: bolder;-fx-text-fill:darkblue");
        Label contact = new Label("+251113693384 / +251113693457");
        Label location = new Label("Addis Ababa, Ethiopia");
        Label title = new Label("PURCHASE FUND FUND REPORT");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-underline: true;");
        hospitalInfo.getChildren().addAll(name2,name, contact, location, title);

        // Right: Date and Request ID
        VBox rightInfo = new VBox(5);
        rightInfo.setAlignment(Pos.BASELINE_RIGHT);
        Label dateLabel = new Label("Request Date:"+record.getRequestDate());
        Label requestIdLabel = new Label("PURCHASE FUND ID: " + getSafeString(record.getRequestId()));
        rightInfo.getChildren().addAll(dateLabel, requestIdLabel);

        HBox.setHgrow(hospitalInfo, Priority.ALWAYS);
        headerBox.getChildren().addAll(logoView, hospitalInfo, rightInfo);

        // ===== MAIN CONTENT =====
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));

        // ===== BASIC INFORMATION =====
        VBox basicInfoSection = createDetailSection("BASIC INFORMATION", "#3498db");
        GridPane basicInfoGrid = new GridPane();
        basicInfoGrid.setHgap(20);
        basicInfoGrid.setVgap(10);
        basicInfoGrid.setPadding(new Insets(10));

        basicInfoGrid.addRow(0,
            createBoldLabel("Request ID:"), new Label(getSafeString(record.getRequestId())),
            createBoldLabel("Requisition Unit:"), new Label(getSafeString(record.getRequisitionUnit()))
        );
        basicInfoGrid.addRow(1,
            createBoldLabel("Payee:"), new Label(getSafeString(record.getPayee())),
            createBoldLabel("Amount:"), new Label(getSafeAmount(record.getAmountRequested()))
        );
        basicInfoGrid.addRow(2,
            createBoldLabel("Request Date:"), new Label(getSafeDate(record.getRequestDate())),
            createBoldLabel("Reason:"), new Label(getSafeString(record.getReason()))
        );
        basicInfoSection.getChildren().add(basicInfoGrid);

        // ===== STATUS INFORMATION =====
        VBox statusSection = createDetailSection("STATUS INFORMATION", "#9b59b6");
        GridPane statusGrid = new GridPane();
        statusGrid.setHgap(20);
        statusGrid.setVgap(10);
        statusGrid.setPadding(new Insets(10));
        
        Label approvedByLabel = new Label(getSafeString(record.getApprovedBy()));
        approvedByLabel.setStyle("-fx-text-fill: #2E8B57; -fx-font-weight: bold;-fx-text-fill:teal; -fx-font-size: 14px;");
        
        Label confirmedByLabel = new Label(getSafeString(record.getConfirmedBy()));
        confirmedByLabel.setStyle("-fx-text-fill: #2E8B57; -fx-font-weight: bold;-fx-text-fill:teal; -fx-font-size: 14px;");
        
        Label dispensedByLabel = new Label(getSafeString(record.getDispensedBy()));
        dispensedByLabel.setStyle("-fx-text-fill: #2E8B57; -fx-font-weight: bold;-fx-text-fill:teal; -fx-font-size: 14px;");
        
        Label dispenseApprovedByLabel =  new Label(getSafeString(record.getDispenseApprovedBy()));
        dispenseApprovedByLabel.setStyle("-fx-text-fill: #2E8B57; -fx-font-weight: bold;-fx-text-fill:teal; -fx-font-size: 14px;");
        
        Label voidedByLabel = new Label(getSafeString(record.getVoidedBy()));
        voidedByLabel.setStyle("-fx-text-fill: #2E8B57; -fx-font-weight: bold;-fx-text-fill:teal; -fx-font-size: 14px;");

        statusGrid.addRow(0,
            createBoldLabel("Request Approval Status:"), createStatusLabel(getSafeString(record.getApprovalStatus())),
            createBoldLabel("Request Approved By:"), new Label(getSafeString(record.getApprovedBy()))
        );
        statusGrid.addRow(1,
            createBoldLabel("Dispense Confirmation Status:"), createStatusLabel(getSafeString(record.getConfirmationStatus())),
            createBoldLabel("Confirmed By:"), new Label(getSafeString(record.getConfirmedBy()))
        );
        statusGrid.addRow(2,
            createBoldLabel("Dispensed Status:"), createStatusLabel(getSafeString(record.getDispensedStatus())),
            createBoldLabel("Dispensed By:"), new Label(getSafeString(record.getDispensedBy()))
        );
        statusGrid.addRow(3,
            createBoldLabel("Dispense Approval Status:"), createStatusLabel(getSafeString(record.getDispenseApprovalStatus())),
            createBoldLabel("Dispense Approved By:"), new Label(getSafeString(record.getDispenseApprovedBy()))
        );
        statusGrid.addRow(4,
            createBoldLabel("Void Status:"), createStatusLabel(getSafeString(record.getVoidStatus())),
            createBoldLabel("Voided By:"), new Label(getSafeString(record.getVoidedBy()))
        );
         statusGrid.addRow(5,
            createBoldLabel("Void Reason:"), createStatusLabel(getSafeString(databaseConnector.getPurchaseFundReplenishRequestVoidReason(record.getRequestId()))));

        statusSection.getChildren().add(statusGrid);

        // ===== SIGNATURES =====
        VBox signaturesSection = createDetailSection("BIOMETRIC SIGNATURES", "#e74c3c");
        HBox signaturesContainer = new HBox(20);
        signaturesContainer.setPadding(new Insets(10));
        loadSignaturesForDetails(record, signaturesContainer);
        if (signaturesContainer.getChildren().isEmpty()) {
            signaturesContainer.getChildren().add(new Label("No biometric signatures available for this request"));
        }
        signaturesSection.getChildren().add(signaturesContainer);

        // Add all sections
        mainContent.getChildren().addAll(basicInfoSection, statusSection, signaturesSection);

        // ===== TOP BUTTONS =====
        HBox topButtons = new HBox(15);
        topButtons.setPadding(new Insets(10));
        topButtons.setAlignment(Pos.CENTER_LEFT);

        Button exportButton = new Button("Export To Pdf");
        exportButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        exportButton.setDisable(true);

        // ===== FOOTER =====
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 0, 10, 0));
        footer.setStyle("-fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");
        Label footerLabel = new Label("Generated by AFRAN General Hospital HRMS System");
        footerLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        footer.getChildren().add(footerLabel);

        // ===== SCROLLABLE CONTENT =====
        // Make this final so it can be accessed in the lambda
        final VBox contentWithButtons = new VBox(10);
        contentWithButtons.getChildren().addAll(headerBox, topButtons, mainContent, footer);

        // Set up export button action - NOW contentWithButtons is accessible
        exportButton.setOnAction(e -> {
            if (!hasAllSignatures(record)) {
                showAlert("Missing Signatures", "Cannot export: one or more required signatures are missing.", Alert.AlertType.WARNING);
                return;
            }
            // Export the entire content including header and footer
            exportNodeToPDF(contentWithButtons, "PurchaseFundRequest_" + record.getRequestId() + ".pdf");
        });
        
        exportButton.setOnAction(e -> {
    if (!hasAllSignatures(record)) {
        showAlert("Missing Signatures", "Cannot export: one or more required signatures are missing.", Alert.AlertType.WARNING);
        return;
    }
    topButtons.getChildren().remove(exportButton);

     // Export the entire content including header and footer
            exportNodeToPDF(contentWithButtons, "PurchaseFundRequest_" + record.getRequestId() + ".pdf");

    // Add receipts section back after a small delay to ensure UI updates
    Platform.runLater(() -> {
        if (!topButtons.getChildren().contains(exportButton)) {
            topButtons.getChildren().add(exportButton);
        }
    });
});
        

        // Add signature check and enable/disable button
        new Thread(() -> {
            boolean allSignaturesPresent = hasAllSignatures(record);
            Platform.runLater(() -> {
                exportButton.setDisable(!allSignaturesPresent);
                if (!allSignaturesPresent) {
                    exportButton.setTooltip(new Tooltip("Disabled: Missing one or more signatures"));
                }
            });
        }).start();

        topButtons.getChildren().addAll(exportButton);

        ScrollPane scrollPane = new ScrollPane(contentWithButtons);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();

    } catch (Exception e) {
        showAlert("Error", "Failed to load details: " + e.getMessage(), Alert.AlertType.ERROR);
        e.printStackTrace();
    }
}

    
   private void printNode(Node node) {
    try {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            showAlert("Error", "No printer job could be created.", Alert.AlertType.ERROR);
            return;
        }

        // Try to safely get the window
        Window window = null;
        if (node.getScene() != null) {
            window = node.getScene().getWindow();
        }

        // Show print dialog (attach to window if available)
        boolean proceed;
        if (window != null) {
            proceed = job.showPrintDialog(window);
        } else {
            proceed = job.showPrintDialog(null);
        }

        if (proceed) {
            boolean success = job.printPage(node);
            if (success) {
                job.endJob();
                showAlert("Print Successful", 
                    "Purchase Fund request details were sent to the printer.", 
                    Alert.AlertType.INFORMATION);
            } else {
                showAlert("Print Failed", "Failed to print the document.", Alert.AlertType.ERROR);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Error", "Printing failed: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}
    
  private void exportNodeToPDF(Node node, String fileName) {
    try {
        // Ensure the node is properly laid out before taking snapshot
        node.snapshot(new SnapshotParameters(), null);
        
        // Allow for layout pass
        Platform.runLater(() -> {
            try {
                // Take snapshot after layout is complete
                WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);

                // Ensure Documents folder exists
                File documentsDir = new File(System.getProperty("user.home"), "Documents/PurchaseFunds/");
                if (!documentsDir.exists()) {
                    documentsDir.mkdirs();
                }

                // Save path in Documents folder
                File outputFile = new File(documentsDir, fileName);

                // Convert snapshot to BufferedImage
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

                // Create PDF document with proper dimensions
                PDDocument document = new PDDocument();
                PDPage page = new PDPage(new PDRectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
                document.addPage(page);

                PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.drawImage(pdImage, 0, 0);
                contentStream.close();

                // Save PDF
                document.save(outputFile);
                document.close();

                showAlert("Export Successful", "PDF saved to: " + outputFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
                
                // Open the PDF file
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(outputFile);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Export Failed", "Failed to export PDF: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
        
    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Export Failed", "Failed to export PDF: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}


    // ===== Helper Methods =====
    private String getSafeString(String value) {
        return (value == null || value.trim().isEmpty()) ? "" : value;
    }

    private String getSafeDate(Object date) {
        return (date == null) ? "" : date.toString();
    }

    private String getSafeAmount(Double amount) {
        return (amount == null) ? "" : String.format("ETB %.2f", amount);
    }

// Load all signatures for detailed view
private void loadSignaturesForDetails(PurchaseFundReplenishRecordModel record, HBox signaturesContainer) {
    // Each role is distinct — even if same person signed multiple times
    Map<String, String> roleToOwner = new LinkedHashMap<>();

    roleToOwner.put("approvedBy", record.getApprovedBy());
    roleToOwner.put("confirmedBy", record.getConfirmedBy());
    roleToOwner.put("dispensedBy", record.getDispensedBy());
    roleToOwner.put("payee", record.getPayee());
    roleToOwner.put("dispenseApprovedBy", record.getDispenseApprovedBy());

    // Iterate each role and load its signature
    for (Map.Entry<String, String> entry : roleToOwner.entrySet()) {
        String role = entry.getKey();
        String owner = entry.getValue();

        if (!isEmpty(owner)) {
            VBox signatureBox = createSignatureBox(getSignatureTitleByRole(role), owner);
            signaturesContainer.getChildren().add(signatureBox);
        }
    }
}


private String getSignatureTitleByRole(String role) {
    switch (role) {
        case "approvedBy":
            return "Request Approver Signature";
        case "dispenseApprovedBy":
            return "Dispense Approver Signature";
        case "confirmedBy":
            return "Dispense Confirmer Signature";
        case "dispensedBy":
            return "Dispenser Signature";
        case "payee":
            return "Payee Signature";
        default:
            return "Signature";
    }
}



//
//    // Helper method to determine signature title
//    private String getSignatureTitle(String owner, PurchaseFundReplenishRecordModel record) {
//        if (owner.equals(record.getDispenseApprovedBy())) return "Dispense Approver Signature";
//        if (owner.equals(record.getApprovedBy())) return "Request Approver Signature";
//        if (owner.equals(record.getConfirmedBy())) return "Dispense Confirmer Signature";
//        if (owner.equals(record.getDispensedBy())) return "Dispenser Signature";
//        if (owner.equals(record.getPayee())) return "Payee Signature";
//        return "Signature";
//    }




    // Helper method to check for empty strings
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Improved signature box creation
    private VBox createSignatureBox(String title, String employeeName) {
        VBox signatureBox = new VBox(10);
        signatureBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 5; -fx-padding: 10;");
        signatureBox.setPrefWidth(250);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label nameLabel = new Label(employeeName);
        nameLabel.setStyle("-fx-text-fill: #7f8c8d;");

        ImageView signatureView = new ImageView();
        signatureView.setFitWidth(200);
        signatureView.setFitHeight(100);
        signatureView.setStyle("-fx-border-color: #ecf0f1; -fx-border-width: 1;");
        signatureView.setImage(createSignaturePlaceholder()); // Set placeholder initially

        // Load signature asynchronously
        new Thread(() -> {
            try {
                byte[] signatureData = databaseConnector.getEmployeeSignature(employeeName);
                if (signatureData != null && signatureData.length > 0) {
                    Image signatureImage = convertByteArrayToImage(signatureData);
                    Platform.runLater(() -> signatureView.setImage(signatureImage));
                }
            } catch (Exception e) {
                System.out.println("Error loading signature for " + employeeName + ": " + e.getMessage());
                // Keep the placeholder image if loading fails
            }
        }).start();

        signatureBox.getChildren().addAll(titleLabel, nameLabel, signatureView);
        return signatureBox;
    }
    
    private boolean hasAllSignatures(PurchaseFundReplenishRecordModel record) {
    try {
        // Check only your five known roles
        String[] owners = {
            record.getApprovedBy(),
            record.getConfirmedBy(),
            record.getDispensedBy(),
            record.getPayee(),
            record.getDispenseApprovedBy()
        };

        for (String owner : owners) {
            if (isEmpty(owner)) return false; // field missing

            byte[] signatureData = databaseConnector.getEmployeeSignature(owner);
            if (signatureData == null || signatureData.length == 0) {
                return false; // signature not found in DB
            }
        }
        return true; // all exist
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


    // ==================== ENHANCED PRINTING AND EXPORT METHODS ====================

    private void showPrintOptions() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Print Options");
        dialog.setHeaderText("Select Printing Option");
        dialog.getDialogPane().setPrefSize(500, 400);

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Choose Printing Option");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button tabularPrintBtn = createStyledButton("Tabular Print (Date Range)", "#3498db");
        tabularPrintBtn.setPrefWidth(300);
        tabularPrintBtn.setOnAction(e -> {
            dialog.close();
            showDateRangePrintDialog(false);
        });

        Button selectedRowPrintBtn = createStyledButton("Selected Row Details with Images", "#27ae60");
        selectedRowPrintBtn.setPrefWidth(300);
        selectedRowPrintBtn.setOnAction(e -> {
            dialog.close();
            printSelectedRowWithImages();
        });

        Button dateRangeImagesPrintBtn = createStyledButton("Date Range Details with Images", "#e74c3c");
        dateRangeImagesPrintBtn.setPrefWidth(300);
        dateRangeImagesPrintBtn.setOnAction(e -> {
            dialog.close();
            showDateRangePrintDialog(true);
        });

        Button cancelBtn = createStyledButton("Cancel", "#95a5a6");
        cancelBtn.setPrefWidth(300);
        cancelBtn.setOnAction(e -> dialog.close());

        content.getChildren().addAll(
            titleLabel, 
            new Separator(),
            tabularPrintBtn,
            selectedRowPrintBtn,
            dateRangeImagesPrintBtn,
            new Separator(),
            cancelBtn
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showExportExcelOptions() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Export to Excel Options");
        dialog.setHeaderText("Select Excel Export Option");
        dialog.getDialogPane().setPrefSize(500, 400);

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Choose Excel Export Option");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button tabularExportBtn = createStyledButton("Tabular Export (Date Range)", "#3498db");
        tabularExportBtn.setPrefWidth(300);
        tabularExportBtn.setOnAction(e -> {
            dialog.close();
            showDateRangeExcelDialog(false);
        });

        Button selectedRowExportBtn = createStyledButton("Selected Row Details with Images", "#27ae60");
        selectedRowExportBtn.setPrefWidth(300);
        selectedRowExportBtn.setOnAction(e -> {
            dialog.close();
            exportSelectedRowToExcelWithImages();
        });

        Button dateRangeImagesExportBtn = createStyledButton("Date Range Details with Images", "#e74c3c");
        dateRangeImagesExportBtn.setPrefWidth(300);
        dateRangeImagesExportBtn.setOnAction(e -> {
            dialog.close();
            showDateRangeExcelDialog(true);
        });

        Button cancelBtn = createStyledButton("Cancel", "#95a5a6");
        cancelBtn.setPrefWidth(300);
        cancelBtn.setOnAction(e -> dialog.close());

        content.getChildren().addAll(
            titleLabel, 
            new Separator(),
            tabularExportBtn,
            selectedRowExportBtn,
            dateRangeImagesExportBtn,
            new Separator(),
            cancelBtn
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showExportWordOptions() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Export to Word Options");
        dialog.setHeaderText("Select Word Export Option");
        dialog.getDialogPane().setPrefSize(500, 400);

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Choose Word Export Option");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Button tabularExportBtn = createStyledButton("Tabular Export (Date Range)", "#3498db");
        tabularExportBtn.setPrefWidth(300);
        tabularExportBtn.setOnAction(e -> {
            dialog.close();
            showDateRangeWordDialog(false);
        });

        Button selectedRowExportBtn = createStyledButton("Selected Row Details with Images", "#27ae60");
        selectedRowExportBtn.setPrefWidth(300);
        selectedRowExportBtn.setOnAction(e -> {
            dialog.close();
            exportSelectedRowToWordWithImages();
        });

        Button dateRangeImagesExportBtn = createStyledButton("Date Range Details with Images", "#e74c3c");
        dateRangeImagesExportBtn.setPrefWidth(300);
        dateRangeImagesExportBtn.setOnAction(e -> {
            dialog.close();
            showDateRangeWordDialog(true);
        });

        Button cancelBtn = createStyledButton("Cancel", "#95a5a6");
        cancelBtn.setPrefWidth(300);
        cancelBtn.setOnAction(e -> dialog.close());

        content.getChildren().addAll(
            titleLabel, 
            new Separator(),
            tabularExportBtn,
            selectedRowExportBtn,
            dateRangeImagesExportBtn,
            new Separator(),
            cancelBtn
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void showDateRangePrintDialog(boolean includeImages) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Print Date Range");
        dialog.setHeaderText("Select Date Range for Printing" + (includeImages ? " (with Images)" : ""));
        dialog.getDialogPane().setPrefSize(400, 300);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        DatePicker fromDatePicker = new DatePicker(LocalDate.now().minusDays(30));
        DatePicker toDatePicker = new DatePicker(LocalDate.now());

        content.getChildren().addAll(
            new Label("From Date:"),
            fromDatePicker,
            new Label("To Date:"),
            toDatePicker
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (fromDatePicker.getValue() != null && toDatePicker.getValue() != null) {
                if (includeImages) {
                    printDateRangeWithImages(fromDatePicker.getValue(), toDatePicker.getValue());
                } else {
                    printDateRangeTabular(fromDatePicker.getValue(), toDatePicker.getValue());
                }
            } else {
                showAlert("Error", "Please select both from and to dates.", Alert.AlertType.ERROR);
            }
        }
    }

    private void showDateRangeExcelDialog(boolean includeImages) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Export Excel Date Range");
        dialog.setHeaderText("Select Date Range for Excel Export" + (includeImages ? " (with Images)" : ""));
        dialog.getDialogPane().setPrefSize(400, 300);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        DatePicker fromDatePicker = new DatePicker(LocalDate.now().minusDays(30));
        DatePicker toDatePicker = new DatePicker(LocalDate.now());

        content.getChildren().addAll(
            new Label("From Date:"),
            fromDatePicker,
            new Label("To Date:"),
            toDatePicker
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (fromDatePicker.getValue() != null && toDatePicker.getValue() != null) {
                if (includeImages) {
                    exportDateRangeToExcelWithImages(fromDatePicker.getValue(), toDatePicker.getValue());
                } else {
                    exportDateRangeToExcel(fromDatePicker.getValue(), toDatePicker.getValue());
                }
            } else {
                showAlert("Error", "Please select both from and to dates.", Alert.AlertType.ERROR);
            }
        }
    }

    private void showDateRangeWordDialog(boolean includeImages) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Export Word Date Range");
        dialog.setHeaderText("Select Date Range for Word Export" + (includeImages ? " (with Images)" : ""));
        dialog.getDialogPane().setPrefSize(400, 300);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        DatePicker fromDatePicker = new DatePicker(LocalDate.now().minusDays(30));
        DatePicker toDatePicker = new DatePicker(LocalDate.now());

        content.getChildren().addAll(
            new Label("From Date:"),
            fromDatePicker,
            new Label("To Date:"),
            toDatePicker
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (fromDatePicker.getValue() != null && toDatePicker.getValue() != null) {
                if (includeImages) {
                    exportDateRangeToWordWithImages(fromDatePicker.getValue(), toDatePicker.getValue());
                } else {
                    exportDateRangeToWord(fromDatePicker.getValue(), toDatePicker.getValue());
                }
            } else {
                showAlert("Error", "Please select both from and to dates.", Alert.AlertType.ERROR);
            }
        }
    }

    // ==================== IMPLEMENTED PRINTING METHODS ====================

    private void printSelectedRowWithImages() {
        PurchaseFundReplenishRecordModel selected = purchaseFundReplenishRequestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a row to print.", Alert.AlertType.WARNING);
            return;
        }

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null && printerJob.showPrintDialog(null)) {
            VBox printNode = createDetailedPrintNode(selected, "Selected Request Details");
            
            Scale scale = new Scale(0.8, 0.8);
            printNode.getTransforms().add(scale);
            
            boolean success = printerJob.printPage(printNode);
            if (success) {
                printerJob.endJob();
                showAlert("Print Successful", "Selected row printed successfully with images!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Print Failed", "Failed to print the selected row.", Alert.AlertType.ERROR);
            }
        }
    }

    private void printDateRangeTabular(LocalDate fromDate, LocalDate toDate) {
        List<PurchaseFundReplenishRecordModel> filteredData = purchaseFundReplenishRequestData.stream()
            .filter(record -> !record.getRequestDate().isBefore(fromDate) && !record.getRequestDate().isAfter(toDate))
            .collect(Collectors.toList());

        if (filteredData.isEmpty()) {
            showAlert("No Data", "No records found for the selected date range.", Alert.AlertType.INFORMATION);
            return;
        }

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null && printerJob.showPrintDialog(null)) {
            VBox printNode = createTabularPrintNode(filteredData, fromDate, toDate);
            
            Scale scale = new Scale(0.7, 0.7);
            printNode.getTransforms().add(scale);
            
            boolean success = printerJob.printPage(printNode);
            if (success) {
                printerJob.endJob();
                showAlert("Print Successful", 
                    String.format("Tabular data printed successfully!\nDate Range: %s to %s\nRecords: %d", 
                        fromDate, toDate, filteredData.size()), 
                    Alert.AlertType.INFORMATION);
            } else {
                showAlert("Print Failed", "Failed to print the tabular data.", Alert.AlertType.ERROR);
            }
        }
    }

    private void printDateRangeWithImages(LocalDate fromDate, LocalDate toDate) {
        List<PurchaseFundReplenishRecordModel> filteredData = purchaseFundReplenishRequestData.stream()
            .filter(record -> !record.getRequestDate().isBefore(fromDate) && !record.getRequestDate().isAfter(toDate))
            .collect(Collectors.toList());

        if (filteredData.isEmpty()) {
            showAlert("No Data", "No records found for the selected date range.", Alert.AlertType.INFORMATION);
            return;
        }

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null && printerJob.showPrintDialog(null)) {
            VBox printNode = createDateRangeDetailedPrintNode(filteredData, fromDate, toDate);
            
            Scale scale = new Scale(0.7, 0.7);
            printNode.getTransforms().add(scale);
            
            boolean success = printerJob.printPage(printNode);
            if (success) {
                printerJob.endJob();
                showAlert("Print Successful", 
                    String.format("Date range details printed successfully with images!\nDate Range: %s to %s\nRecords: %d", 
                        fromDate, toDate, filteredData.size()), 
                    Alert.AlertType.INFORMATION);
            } else {
                showAlert("Print Failed", "Failed to print the date range details.", Alert.AlertType.ERROR);
            }
        }
    }

    // ==================== IMPLEMENTED EXPORT TO EXCEL METHODS ====================

    private void exportSelectedRowToExcelWithImages() {
        PurchaseFundReplenishRecordModel selected = purchaseFundReplenishRequestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a row to export.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Selected Row to Excel with Images");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("PurchaseFund_Details_" + selected.getRequestId() + "_" + LocalDate.now() + ".xlsx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            new Thread(() -> {
                try (Workbook workbook = new XSSFWorkbook()) {
                    Sheet sheet = workbook.createSheet("Request Details");
                    
                    // Header with hospital info
                    Row headerRow = sheet.createRow(0);
                    org.apache.poi.ss.usermodel.Cell headerCell = headerRow.createCell(0);
                    headerCell.setCellValue("AFRAN GENERAL HOSPITAL - PURCHASE FUND REQUEST DETAILS");
                    CellStyle headerStyle = workbook.createCellStyle();
                    org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                    headerFont.setBold(true);
                    headerFont.setFontHeightInPoints((short)16);
                    headerStyle.setFont(headerFont);
                    headerStyle.setAlignment(HorizontalAlignment.CENTER);
                    headerCell.setCellStyle(headerStyle);
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
                    
                    // Request details
                    int rowNum = 2;
                    String[][] details = {
                        {"Request ID:", selected.getRequestId()},
                        {"Requisition Unit:", selected.getRequisitionUnit()},
                        {"Payee:", selected.getPayee()},
                        {"Amount:", String.format("ETB %.2f", selected.getAmountRequested())},
                        {"Request Date:", selected.getRequestDate().toString()},
                        {"Approval Status:", selected.getApprovalStatus()},
                        {"Confirmed Status:", selected.getConfirmationStatus()},
                        {"Dispensed Status:", selected.getDispensedStatus()}
                    };
                    
                    for (String[] detail : details) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(detail[0]);
                        row.createCell(1).setCellValue(detail[1]);
                    }
                    
                    // Auto-size columns
                    for (int i = 0; i < 8; i++) {
                        sheet.autoSizeColumn(i);
                    }
                    
                    try (FileOutputStream fileOut = new FileOutputStream(file)) {
                        workbook.write(fileOut);
                    }
                    
                    Platform.runLater(() -> 
                        showAlert("Export Successful", 
                            "Selected row exported to Excel successfully!\nFile: " + file.getAbsolutePath(), 
                            Alert.AlertType.INFORMATION)
                    );
                    
                } catch (Exception e) {
                    Platform.runLater(() -> 
                        showAlert("Export Error", "Failed to export to Excel: " + e.getMessage(), Alert.AlertType.ERROR)
                    );
                }
            }).start();
        }
    }

    private void exportDateRangeToExcel(LocalDate fromDate, LocalDate toDate) {
        List<PurchaseFundReplenishRecordModel> filteredData = purchaseFundReplenishRequestData.stream()
            .filter(record -> !record.getRequestDate().isBefore(fromDate) && !record.getRequestDate().isAfter(toDate))
            .collect(Collectors.toList());

        if (filteredData.isEmpty()) {
            showAlert("No Data", "No records found for the selected date range.", Alert.AlertType.INFORMATION);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Date Range to Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("PurchaseFund_Report_" + fromDate + "_to_" + toDate + ".xlsx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            exportToExcelWithData(filteredData, file, fromDate, toDate);
        }
    }

    private void exportDateRangeToExcelWithImages(LocalDate fromDate, LocalDate toDate) {
        List<PurchaseFundReplenishRecordModel> filteredData = purchaseFundReplenishRequestData.stream()
            .filter(record -> !record.getRequestDate().isBefore(fromDate) && !record.getRequestDate().isAfter(toDate))
            .collect(Collectors.toList());

        if (filteredData.isEmpty()) {
            showAlert("No Data", "No records found for the selected date range.", Alert.AlertType.INFORMATION);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Date Range to Excel with Images");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("PurchaseFund_Detailed_Report_" + fromDate + "_to_" + toDate + ".xlsx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            exportToExcelWithImages(filteredData, file, fromDate, toDate);
        }
    }

    // ==================== IMPLEMENTED EXPORT TO WORD METHODS ====================

    private void exportSelectedRowToWordWithImages() {
        PurchaseFundReplenishRecordModel selected = purchaseFundReplenishRequestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a row to export.", Alert.AlertType.WARNING);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Selected Row to Word with Images");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Documents", "*.docx"));
        fileChooser.setInitialFileName("PurchaseFund_Details_" + selected.getRequestId() + "_" + LocalDate.now() + ".docx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            new Thread(() -> {
                try (XWPFDocument document = new XWPFDocument()) {
                    // Title
                    XWPFParagraph titleParagraph = document.createParagraph();
                    titleParagraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun titleRun = titleParagraph.createRun();
                    titleRun.setText("AFRAN GENERAL HOSPITAL");
                    titleRun.setBold(true);
                    titleRun.setFontSize(16);
                    
                    XWPFParagraph subtitleParagraph = document.createParagraph();
                    subtitleParagraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun subtitleRun = subtitleParagraph.createRun();
                    subtitleRun.setText("PURCHASE FUND REQUEST DETAILS");
                    subtitleRun.setBold(true);
                    subtitleRun.setFontSize(14);
                    
                    // Request details table
                    XWPFTable table = document.createTable();
                    table.setWidth("100%");
                    
                    // Add details
                    addTableRow(table, "Request ID:", selected.getRequestId());
                    addTableRow(table, "Requisition Unit:", selected.getRequisitionUnit());
                    addTableRow(table, "Payee:", selected.getPayee());
                    addTableRow(table, "Amount:", String.format("ETB %.2f", selected.getAmountRequested()));
                    addTableRow(table, "Request Date:", selected.getRequestDate().toString());
                    addTableRow(table, "Approval Status:", selected.getApprovalStatus());
                    addTableRow(table, "Confirmed Status:", selected.getConfirmationStatus());
                    addTableRow(table, "Dispensed Status:", selected.getDispensedStatus());
                    addTableRow(table, "Reason:", selected.getReason());
                    
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        document.write(out);
                    }
                    
                    Platform.runLater(() -> 
                        showAlert("Export Successful", 
                            "Selected row exported to Word successfully!\nFile: " + file.getAbsolutePath(), 
                            Alert.AlertType.INFORMATION)
                    );
                    
                } catch (Exception e) {
                    Platform.runLater(() -> 
                        showAlert("Export Error", "Failed to export to Word: " + e.getMessage(), Alert.AlertType.ERROR)
                    );
                }
            }).start();
        }
    }

    private void exportDateRangeToWord(LocalDate fromDate, LocalDate toDate) {
        List<PurchaseFundReplenishRecordModel> filteredData = purchaseFundReplenishRequestData.stream()
            .filter(record -> !record.getRequestDate().isBefore(fromDate) && !record.getRequestDate().isAfter(toDate))
            .collect(Collectors.toList());

        if (filteredData.isEmpty()) {
            showAlert("No Data", "No records found for the selected date range.", Alert.AlertType.INFORMATION);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Date Range to Word");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Documents", "*.docx"));
        fileChooser.setInitialFileName("PurchaseFund_Report_" + fromDate + "_to_" + toDate + ".docx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            exportToWordWithData(filteredData, file, fromDate, toDate);
        }
    }

    private void exportDateRangeToWordWithImages(LocalDate fromDate, LocalDate toDate) {
        List<PurchaseFundReplenishRecordModel> filteredData = purchaseFundReplenishRequestData.stream()
            .filter(record -> !record.getRequestDate().isBefore(fromDate) && !record.getRequestDate().isAfter(toDate))
            .collect(Collectors.toList());

        if (filteredData.isEmpty()) {
            showAlert("No Data", "No records found for the selected date range.", Alert.AlertType.INFORMATION);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Date Range to Word with Images");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Documents", "*.docx"));
        fileChooser.setInitialFileName("PurchaseFund_Detailed_Report_" + fromDate + "_to_" + toDate + ".docx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            exportToWordWithImages(filteredData, file, fromDate, toDate);
        }
    }

    // ==================== HELPER METHODS FOR PRINTING/EXPORTING ====================

    private VBox createDetailedPrintNode(PurchaseFundReplenishRecordModel record, String title) {
        VBox printNode = new VBox(15);
        printNode.setStyle("-fx-padding: 30; -fx-background-color: white;");
        
        // Header
        Label header = new Label("AFRAN GENERAL HOSPITAL");
        header.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-alignment: center;");
        
        Label subHeader = new Label("PURCHASE FUND REPLENISH REQUEST MANAGEMENT SYSTEM");
        subHeader.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #3498db; -fx-alignment: center;");
        
        Label reportTitle = new Label(title);
        reportTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #e74c3c; -fx-alignment: center;");
        
        Label dateLabel = new Label("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #7f8c8d; -fx-alignment: center;");
        
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getChildren().addAll(header, subHeader, reportTitle, dateLabel);
        
        // Details Grid
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(8);
        detailsGrid.setPadding(new Insets(20, 0, 0, 0));
        
        String[][] details = {
            {"Request ID:", record.getRequestId()},
            {"Requisition Unit:", record.getRequisitionUnit()},
            {"Payee:", record.getPayee()},
            {"Amount Requested:", String.format("ETB %.2f", record.getAmountRequested())},
            {"Request Date:", record.getRequestDate().toString()},
            {"Reason:", record.getReason()},
            {"Approval Status:", record.getApprovalStatus()},
            {"Approved By:", record.getApprovedBy()},
            {"Confirmation Status:", record.getConfirmationStatus()},
            {"Confirmed By:", record.getConfirmedBy()},
            {"Dispensed Status:", record.getDispensedStatus()},
            {"Dispensed By:", record.getDispensedBy()},
            {"Dispense Approval Status:", record.getDispenseApprovalStatus()},
            {"Dispense Approved By:", record.getDispenseApprovedBy()},
            {"Void Status:", record.getVoidStatus()},
            {"Voided By:", record.getVoidedBy()}
        };
        
        for (int i = 0; i < details.length; i++) {
            Label fieldLabel = new Label(details[i][0]);
            fieldLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            Label valueLabel = new Label(details[i][1]);
            valueLabel.setStyle("-fx-text-fill: #34495e;");
            
            detailsGrid.add(fieldLabel, 0, i);
            detailsGrid.add(valueLabel, 1, i);
        }
        
        // Footer
        Label footer = new Label("© 2025 Afran General Hospital - Smart HRMS | Confidential Document");
        footer.setStyle("-fx-font-size: 10; -fx-text-fill: #95a5a6; -fx-alignment: center; -fx-padding: 20 0 0 0;");
        
        printNode.getChildren().addAll(headerBox, new Separator(), detailsGrid, new Separator(), footer);
        return printNode;
    }

    private VBox createTabularPrintNode(List<PurchaseFundReplenishRecordModel> data, LocalDate fromDate, LocalDate toDate) {
        VBox printNode = new VBox(15);
        printNode.setStyle("-fx-padding: 25; -fx-background-color: white;");
        
        // Header
        Label header = new Label("AFRAN GENERAL HOSPITAL");
        header.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-alignment: center;");
        
        Label subHeader = new Label("PURCHASE FUND REPLENISH REQUESTS SUMMARY REPORT");
        subHeader.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #3498db; -fx-alignment: center;");
        
        Label dateRange = new Label("Date Range: " + fromDate + " to " + toDate);
        dateRange.setStyle("-fx-font-size: 12; -fx-text-fill: #7f8c8d; -fx-alignment: center;");
        
        Label generatedDate = new Label("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        generatedDate.setStyle("-fx-font-size: 11; -fx-text-fill: #7f8c8d; -fx-alignment: center;");
        
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getChildren().addAll(header, subHeader, dateRange, generatedDate);
        
        // Summary Statistics
        long totalRequests = data.size();
        long pending = data.stream().filter(r -> "Pending".equals(r.getApprovalStatus())).count();
        long approved = data.stream().filter(r -> "Approved".equals(r.getApprovalStatus())).count();
        double totalAmount = data.stream().mapToDouble(PurchaseFundReplenishRecordModel::getAmountRequested).sum();
        
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(5);
        statsGrid.setPadding(new Insets(10, 0, 10, 0));
        
        String[][] stats = {
            {"Total Requests:", String.valueOf(totalRequests)},
            {"Pending Approval:", String.valueOf(pending)},
            {"Approved:", String.valueOf(approved)},
            {"Total Amount:", String.format("ETB %.2f", totalAmount)}
        };
        
        for (int i = 0; i < stats.length; i++) {
            Label statLabel = new Label(stats[i][0]);
            statLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            Label valueLabel = new Label(stats[i][1]);
            valueLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            
            statsGrid.add(statLabel, 0, i);
            statsGrid.add(valueLabel, 1, i);
        }
        
        // Table
        TableView<PurchaseFundReplenishRecordModel> printTable = new TableView<>();
        printTable.setItems(FXCollections.observableArrayList(data));
        
        TableColumn<PurchaseFundReplenishRecordModel, String> reqIdCol = new TableColumn<>("Request ID");
        reqIdCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        
        TableColumn<PurchaseFundReplenishRecordModel, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("requisitionUnit"));
        
        TableColumn<PurchaseFundReplenishRecordModel, String> payeeCol = new TableColumn<>("Payee");
        payeeCol.setCellValueFactory(new PropertyValueFactory<>("payee"));
        
        TableColumn<PurchaseFundReplenishRecordModel, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amountRequested"));
        
        TableColumn<PurchaseFundReplenishRecordModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("approvalStatus"));
        
        printTable.getColumns().addAll(reqIdCol, unitCol, payeeCol, amountCol, statusCol);
        printTable.setPrefHeight(400);
        
        // Footer
        Label footer = new Label("© 2025 Afran General Hospital - Confidential Report | Page 1 of 1");
        footer.setStyle("-fx-font-size: 9; -fx-text-fill: #95a5a6; -fx-alignment: center; -fx-padding: 10 0 0 0;");
        
        printNode.getChildren().addAll(headerBox, new Separator(), statsGrid, new Separator(), printTable, new Separator(), footer);
        return printNode;
    }

    private VBox createDateRangeDetailedPrintNode(List<PurchaseFundReplenishRecordModel> data, LocalDate fromDate, LocalDate toDate) {
        VBox printNode = new VBox(15);
        printNode.setStyle("-fx-padding: 20; -fx-background-color: white;");
        
        // Header
        Label header = new Label("AFRAN GENERAL HOSPITAL");
        header.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-alignment: center;");
        
        Label subHeader = new Label("DETAILED PURHCASE FUND REPLENISH REQUESTS REPORT");
        subHeader.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #3498db; -fx-alignment: center;");
        
        Label dateRange = new Label("Date Range: " + fromDate + " to " + toDate);
        dateRange.setStyle("-fx-font-size: 12; -fx-text-fill: #7f8c8d; -fx-alignment: center;");
        
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.getChildren().addAll(header, subHeader, dateRange);
        
        // Create detailed sections for each record
        VBox recordsBox = new VBox(20);
        for (int i = 0; i < Math.min(data.size(), 5); i++) { // Limit to 5 records for printing
            PurchaseFundReplenishRecordModel record = data.get(i);
            VBox recordBox = createRecordPrintBox(record, i + 1);
            recordsBox.getChildren().add(recordBox);
        }
        
        // Footer
        Label footer = new Label("© 2025 Afran General Hospital - Confidential Detailed Report | Total Records: " + data.size());
        footer.setStyle("-fx-font-size: 9; -fx-text-fill: #95a5a6; -fx-alignment: center; -fx-padding: 20 0 0 0;");
        
        printNode.getChildren().addAll(headerBox, new Separator(), recordsBox, new Separator(), footer);
        return printNode;
    }

    private VBox createRecordPrintBox(PurchaseFundReplenishRecordModel record, int index) {
        VBox recordBox = new VBox(10);
        recordBox.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-padding: 15; -fx-background-color: #f8f9fa;");
        
        Label recordHeader = new Label("Record #" + index + " - " + record.getRequestId());
        recordHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 12; -fx-text-fill: #2c3e50;");
        
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(5);
        
        String[][] details = {
            {"Requisition Unit:", record.getRequisitionUnit()},
            {"Payee:", record.getPayee()},
            {"Amount:", String.format("ETB %.2f", record.getAmountRequested())},
            {"Date:", record.getRequestDate().toString()},
            {"Status:", record.getApprovalStatus()}
        };
        
        for (int i = 0; i < details.length; i++) {
            Label fieldLabel = new Label(details[i][0]);
            fieldLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 10;");
            
            Label valueLabel = new Label(details[i][1]);
            valueLabel.setStyle("-fx-text-fill: #34495e; -fx-font-size: 10;");
            
            detailsGrid.add(fieldLabel, 0, i);
            detailsGrid.add(valueLabel, 1, i);
        }
        
        recordBox.getChildren().addAll(recordHeader, detailsGrid);
        return recordBox;
    }

    // ==================== EXCEL EXPORT IMPLEMENTATION ====================

    private void exportToExcelWithData(List<PurchaseFundReplenishRecordModel> data, File file, LocalDate fromDate, LocalDate toDate) {
        new Thread(() -> {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Purchase Fund Replenish Requests");
                
                // Create header style
                CellStyle headerStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short)12);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                
                // Create data style
                CellStyle dataStyle = workbook.createCellStyle();
                dataStyle.setBorderBottom(BorderStyle.THIN);
                dataStyle.setBorderTop(BorderStyle.THIN);
                dataStyle.setBorderLeft(BorderStyle.THIN);
                dataStyle.setBorderRight(BorderStyle.THIN);
                
                // Report header
                Row titleRow = sheet.createRow(0);
                org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("AFRAN GENERAL HOSPITAL - PURCHASE FUND REPLENISH REQUESTS REPORT");
                CellStyle titleStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short)14);
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);
                titleCell.setCellStyle(titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
                
                Row dateRow = sheet.createRow(1);
                org.apache.poi.ss.usermodel.Cell dateCell = dateRow.createCell(0);
                dateCell.setCellValue("Date Range: " + fromDate + " to " + toDate + " | Generated on: " + LocalDate.now());
                dateCell.setCellStyle(titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));
                
                // Column headers
                Row headerRow = sheet.createRow(3);
                String[] headers = {
                    "Request ID", "Requisition Unit", "Reason", "Payee", 
                    "Amount", "Request Date", "Approval Status", "Approved By",
                    "Confirmation Status", "Dispensed Status"
                };
                
                for (int i = 0; i < headers.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }
                
                // Data rows
                int rowNum = 4;
                for (PurchaseFundReplenishRecordModel record : data) {
                    Row row = sheet.createRow(rowNum++);
                    
                    row.createCell(0).setCellValue(record.getRequestId());
                    row.createCell(1).setCellValue(record.getRequisitionUnit());
                    row.createCell(2).setCellValue(record.getReason());
                    row.createCell(3).setCellValue(record.getPayee());
                    row.createCell(4).setCellValue(record.getAmountRequested());
                    row.createCell(5).setCellValue(record.getRequestDate().toString());
                    row.createCell(6).setCellValue(record.getApprovalStatus());
                    row.createCell(7).setCellValue(record.getApprovedBy());
                    row.createCell(8).setCellValue(record.getConfirmationStatus());
                    row.createCell(9).setCellValue(record.getDispensedStatus());
                    
                    // Apply data style to all cells
                    for (int i = 0; i < headers.length; i++) {
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
                
                // Auto-size columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                
                // Summary row
                Row summaryRow = sheet.createRow(rowNum + 1);
                org.apache.poi.ss.usermodel.Cell summaryCell = summaryRow.createCell(0);
                summaryCell.setCellValue("SUMMARY: Total Requests: " + data.size() + 
                    " | Total Amount: ETB " + data.stream().mapToDouble(PurchaseFundReplenishRecordModel::getAmountRequested).sum());
                summaryCell.setCellStyle(headerStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowNum + 1, rowNum + 1, 0, 9));
                
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                
                Platform.runLater(() -> 
                    showAlert("Export Successful", 
                        "Data exported to Excel successfully!\nFile: " + file.getAbsolutePath() + 
                        "\nRecords: " + data.size(), 
                        Alert.AlertType.INFORMATION)
                );
                
            } catch (Exception e) {
                Platform.runLater(() -> 
                    showAlert("Export Error", "Failed to export to Excel: " + e.getMessage(), Alert.AlertType.ERROR)
                );
            }
        }).start();
    }

    private void exportToExcelWithImages(List<PurchaseFundReplenishRecordModel> data, File file, LocalDate fromDate, LocalDate toDate) {
        // This would require more advanced Excel image handling
        // For now, we'll export the detailed data without actual signature images
        exportToExcelWithData(data, file, fromDate, toDate);
    }

    // ==================== WORD EXPORT IMPLEMENTATION ====================

    private void exportToWordWithData(List<PurchaseFundReplenishRecordModel> data, File file, LocalDate fromDate, LocalDate toDate) {
        new Thread(() -> {
            try (XWPFDocument document = new XWPFDocument()) {
                // Title
                XWPFParagraph titleParagraph = document.createParagraph();
                titleParagraph.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun titleRun = titleParagraph.createRun();
                titleRun.setText("AFRAN GENERAL HOSPITAL");
                titleRun.setBold(true);
                titleRun.setFontSize(16);
                
                XWPFParagraph subtitleParagraph = document.createParagraph();
                subtitleParagraph.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun subtitleRun = subtitleParagraph.createRun();
                subtitleRun.setText("PURCHASE FUND REPLENISH REQUESTS REPORT");
                subtitleRun.setBold(true);
                subtitleRun.setFontSize(14);
                
                // Date range
                XWPFParagraph dateParagraph = document.createParagraph();
                dateParagraph.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun dateRun = dateParagraph.createRun();
                dateRun.setText("Date Range: " + fromDate + " to " + toDate);
                dateRun.setFontSize(12);
                
                XWPFParagraph genDateParagraph = document.createParagraph();
                genDateParagraph.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun genDateRun = genDateParagraph.createRun();
                genDateRun.setText("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
                genDateRun.setFontSize(11);
                
                // Summary
                XWPFParagraph summaryParagraph = document.createParagraph();
                XWPFRun summaryRun = summaryParagraph.createRun();
                summaryRun.setText("Summary: Total Requests: " + data.size() + 
                    " | Total Amount: ETB " + String.format("%.2f", 
                    data.stream().mapToDouble(PurchaseFundReplenishRecordModel::getAmountRequested).sum()));
                summaryRun.setBold(true);
                summaryRun.setFontSize(12);
                
                // Table
                XWPFTable table = document.createTable();
                table.setWidth("100%");
                
                // Header row
                XWPFTableRow headerRow = table.getRow(0);
                String[] headers = {"Request ID", "Unit", "Payee", "Amount", "Date", "Status"};
                
                for (int i = 0; i < headers.length; i++) {
                    if (i == 0) {
                        headerRow.getCell(0).setText(headers[i]);
                    } else {
                        headerRow.addNewTableCell().setText(headers[i]);
                    }
                }
                
                // Data rows
                for (PurchaseFundReplenishRecordModel record : data) {
                    XWPFTableRow row = table.createRow();
                    row.getCell(0).setText(record.getRequestId());
                    row.getCell(1).setText(record.getRequisitionUnit());
                    row.getCell(2).setText(record.getPayee());
                    row.getCell(3).setText(String.format("ETB %.2f", record.getAmountRequested()));
                    row.getCell(4).setText(record.getRequestDate().toString());
                    row.getCell(5).setText(record.getApprovalStatus());
                }
                
                // Footer
                XWPFParagraph footerParagraph = document.createParagraph();
                XWPFRun footerRun = footerParagraph.createRun();
                footerRun.setText("\n© 2025 Afran General Hospital - Confidential Document");
                footerRun.setFontSize(10);
                footerRun.setColor("95a5a6");
                
                try (FileOutputStream out = new FileOutputStream(file)) {
                    document.write(out);
                }
                
                Platform.runLater(() -> 
                    showAlert("Export Successful", 
                        "Report exported to Word successfully!\nFile: " + file.getAbsolutePath() + 
                        "\nRecords: " + data.size(), 
                        Alert.AlertType.INFORMATION)
                );
                
            } catch (Exception e) {
                Platform.runLater(() -> 
                    showAlert("Export Error", "Failed to export to Word: " + e.getMessage(), Alert.AlertType.ERROR)
                );
            }
        }).start();
    }

    private void exportToWordWithImages(List<PurchaseFundReplenishRecordModel> data, File file, LocalDate fromDate, LocalDate toDate) {
        // This would require more advanced Word image handling
        // For now, we'll export the detailed data without actual signature images
        exportToWordWithData(data, file, fromDate, toDate);
    }
    
    
    

    // ==================== UTILITY METHODS ====================

    private void addTableRow(XWPFTable table, String label, String value) {
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(label);
        row.getCell(1).setText(value);
    }

    private VBox createBiometricSection(String title, String instruction, String color) {
        VBox section = new VBox(10);
        section.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-padding: 15; " +
            "-fx-border-color: " + color + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + "; -fx-font-size: 14;");

        Button verifyBtn = createStyledButton("Verify Identity & Load Signature", color);
        
        Label instructionLabel = new Label(instruction);
        instructionLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12;");

        ImageView signatureImageView = new ImageView();
        signatureImageView.setFitWidth(250);
        signatureImageView.setFitHeight(120);
        signatureImageView.setPreserveRatio(true);
        signatureImageView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 3;");

        Label statusLabel = new Label("No signature loaded");
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11;");

        section.getChildren().addAll(
            titleLabel, verifyBtn, instructionLabel, 
            signatureImageView, statusLabel
        );

        return section;
    }

    private void loadAndDisplaySignature(EmployeeSignatureModel employee, ImageView signatureView, Label statusLabel) {
        if (employee.getSignatureImage() != null && employee.getSignatureImage().length > 0) {
            try {
                Image signatureImage = convertByteArrayToImage(employee.getSignatureImage());
                signatureView.setImage(signatureImage);
                statusLabel.setText("✓ " + employee.getEmployeeName());
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
            } catch (Exception ex) {
                statusLabel.setText("✗ Error loading signature");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        } else {
            statusLabel.setText("✗ No signature available");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private VBox createRequestDetailsSection(PurchaseFundReplenishRecordModel record) {
        VBox detailsSection = new VBox(10);
        detailsSection.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-border-radius: 5;");

        Label detailsTitle = new Label("REQUEST DETAILS");
        detailsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #2c3e50;");

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(5);

        detailsGrid.addRow(0, createBoldLabel("Request ID:"), new Label(record.getRequestId()));
        detailsGrid.addRow(1, createBoldLabel("Requisition Unit:"), new Label(record.getRequisitionUnit()));
        detailsGrid.addRow(2, createBoldLabel("Reason:"), new Label(record.getReason()));
        detailsGrid.addRow(3, createBoldLabel("Payee:"), new Label(record.getPayee()));
        detailsGrid.addRow(4, createBoldLabel("Amount:"), new Label(String.format("ETB %.2f", record.getAmountRequested())));
        detailsGrid.addRow(5, createBoldLabel("Request Date:"), new Label(record.getRequestDate().toString()));
        detailsGrid.addRow(6, createBoldLabel("Approval Status:"), new Label(record.getApprovalStatus()));
        detailsGrid.addRow(7, createBoldLabel("Confirmation Status:"), new Label(record.getConfirmationStatus()));
        detailsGrid.addRow(8, createBoldLabel("Dispensed Status:"), new Label(record.getDispensedStatus()));
        detailsGrid.addRow(9, createBoldLabel("Dispense Approval Status:"), new Label(record.getDispenseApprovalStatus()));

        detailsSection.getChildren().addAll(detailsTitle, detailsGrid);
        return detailsSection;
    }

    private VBox createDetailSection(String title, String color) {
        VBox section = new VBox(10);
        section.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-padding: 15; " +
            "-fx-border-color: " + color + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: " + color + ";");

        section.getChildren().add(titleLabel);
        return section;
    }

    private Label createBoldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;-fx-font-size:16; -fx-text-fill: #2c3e50;");
        return label;
    }

    private Label createStatusLabel(String status) {
        Label label = new Label(status);
        switch (status.toLowerCase()) {
            case "approved":
            case "confirmed":
            case "yes":
                label.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14; -fx-font-weight: bold; -fx-underline: true;");
                break;
            case "pending":
                label.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14; -fx-font-weight: bold; -fx-underline: true;");
                break;
            case "rejected":
            case "voided":
                label.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14; -fx-font-weight: bold; -fx-underline: true;");
                break;
            default:
                label.setStyle("-fx-text-fill: #7f8c8d;");
        }
        return label;
    }

    private Image convertByteArrayToImage(byte[] imageData) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            BufferedImage bufferedImage = ImageIO.read(bis);
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (Exception e) {
            System.out.println("Error converting byte array to image: " + e.getMessage());
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

    // ==================== TABLE AND FILTER METHODS ====================

    private VBox createTableSection() {
        VBox tableSection = new VBox();
        tableSection.setPadding(new Insets(15));
        tableSection.setStyle("-fx-background-color: #ffffff; -fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox filterBox = new HBox(10);
        filterBox.setPadding(new Insets(0, 0, 15, 0));
        filterBox.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by employee, request ID, or payee...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-radius: 5; -fx-border-radius: 5;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Rejected", "Confirmed", "Dispensed", "Voided");
        statusFilter.setValue("All");
        statusFilter.setStyle("-fx-background-radius: 5;");
        
        DatePicker dateFromPicker = new DatePicker();
        dateFromPicker.setPromptText("From Date");

        DatePicker dateToPicker = new DatePicker();
        dateToPicker.setPromptText("To Date");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
    @Override
    public String toString(LocalDate date) {
        return (date != null) ? formatter.format(date) : "";
    }

    @Override
    public LocalDate fromString(String string) {
        return (string != null && !string.isEmpty())
                ? LocalDate.parse(string, formatter)
                : null;
    }
};

dateFromPicker.setConverter(converter);
dateToPicker.setConverter(converter);

// Optional: force the editor to use the same format
dateFromPicker.getEditor().setPromptText("yyyy-MM-dd");
dateToPicker.getEditor().setPromptText("yyyy-MM-dd");


        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        searchBtn.setOnAction(e -> 
               filterTable(
        searchField.getText(),
        statusFilter.getValue(),
        dateFromPicker.getValue(),
        dateToPicker.getValue()
    ));

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
         clearBtn.setOnAction(e -> {
        searchField.clear();
        statusFilter.setValue("All");
        dateFromPicker.setValue(null);
        dateToPicker.setValue(null);
        filterTable("", "All", null, null);
});
        
        Button refreshBtn = createStyledButton("Refresh", "#7f8c8d");
        refreshBtn.setOnAction(e -> refreshData());
        
filterBox.getChildren().addAll(
        new Label("Search:"), searchField,
        new Label("Status:"), statusFilter,
        new Label("From:"), dateFromPicker,
        new Label("To:"), dateToPicker,
        searchBtn, clearBtn, refreshBtn
);

        purchaseFundReplenishRequestTable = new TableView<>();
        purchaseFundReplenishRequestTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        purchaseFundReplenishRequestTable.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");

        purchaseFundReplenishRequestTable.setRowFactory(tv -> {
    TableRow<PurchaseFundReplenishRecordModel> row = new TableRow<PurchaseFundReplenishRecordModel>() {
        @Override
        protected void updateItem(PurchaseFundReplenishRecordModel item, boolean empty) {
            super.updateItem(item, empty);
            
            // Clear all styles first
            getStyleClass().removeAll("selected-row", "voided-row", "completed-row", "inuse-row", 
                                    "pending-row", "dispensed-row", "approved-row");
            
            if (empty || item == null) {
                setStyle("");
            } else {
                // Apply custom background colors based on status
                if ("Yes".equalsIgnoreCase(item.getVoidStatus())) {
                    getStyleClass().add("voided-row");
                    setStyle("-fx-background-color: red;"); // Red for voided
                } else if ("Completed".equalsIgnoreCase(item.getCurrentStatus())) {
                    getStyleClass().add("completed-row");
                    setStyle("-fx-background-color: #006400; -fx-text-fill: white;"); // Deep green for completed
                } else if ("In Use".equalsIgnoreCase(item.getCurrentStatus())) {
                    getStyleClass().add("inuse-row");
                    setStyle("-fx-background-color: #ff8c00; -fx-text-fill: white;"); // Deep orange for In Use
                } else if ("Pending".equalsIgnoreCase(item.getCurrentStatus())) {
                    getStyleClass().add("pending-row");
                    setStyle("-fx-background-color: #ffff00;"); // Yellow for pending
                } else if ("Yes".equalsIgnoreCase(item.getDispensedStatus())) {
                    getStyleClass().add("dispensed-row");
                    setStyle("-fx-background-color: #d4edda;"); // Light green for dispensed
                } else if ("Approved".equalsIgnoreCase(item.getApprovalStatus())) {
                    getStyleClass().add("approved-row");
                    setStyle("-fx-background-color: #fff3cd;"); // Light yellow for approved
                } else {
                    setStyle(""); // Default style
                }
                
                // Handle selection - add border or other visual indicator
                if (isSelected()) {
                    getStyleClass().add("selected-row");
                    // Add a border to highlight selection while keeping background
                    String currentStyle = getStyle();
                    if (currentStyle == null) currentStyle = "";
                    setStyle(currentStyle + " -fx-border-color: #3498db; -fx-border-width: 2px; -fx-border-radius: 3px;");
                }
            }
        }
    };
    
    // Add listener to update selection style when selection changes
    row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
        if (isNowSelected && row.getItem() != null) {
            row.getStyleClass().add("selected-row");
            String currentStyle = row.getStyle();
            if (currentStyle == null) currentStyle = "";
            row.setStyle(currentStyle + " -fx-border-color: #3498db; -fx-border-width: 2px; -fx-border-radius: 3px;");
        } else {
            row.getStyleClass().remove("selected-row");
            // Re-apply the original style without selection border
            PurchaseFundReplenishRecordModel item = row.getItem();
            if (item != null) {
                if ("Yes".equalsIgnoreCase(item.getVoidStatus())) {
                    row.setStyle("-fx-background-color: #ffcccc;");
                } else if ("Completed".equalsIgnoreCase(item.getCurrentStatus())) {
                    row.setStyle("-fx-background-color: #006400; -fx-text-fill: white;");
                } else if ("In Use".equalsIgnoreCase(item.getCurrentStatus())) {
                    row.setStyle("-fx-background-color: #ff8c00; -fx-text-fill: white;");
                } else if ("Pending".equalsIgnoreCase(item.getCurrentStatus())) {
                    row.setStyle("-fx-background-color: #ffff00;");
                } else if ("Yes".equalsIgnoreCase(item.getDispensedStatus())) {
                    row.setStyle("-fx-background-color: #d4edda;");
                } else if ("Approved".equalsIgnoreCase(item.getApprovalStatus())) {
                    row.setStyle("-fx-background-color: #fff3cd;");
                } else {
                    row.setStyle("");
                }
            }
        }
    });
    
    row.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && !row.isEmpty()) {
            PurchaseFundReplenishRecordModel selectedRecord = row.getItem();
            if (selectedRecord != null) {
                viewRecordDetails(selectedRecord);
            }
        }
    });
    return row;
});

        // Create table columns
        TableColumn<PurchaseFundReplenishRecordModel, String> requestIdCol = createStyledColumn("Request ID", "requestId", "#2c3e50");
        TableColumn<PurchaseFundReplenishRecordModel, String> requisitionUnitCol = createStyledColumn("Requisition Unit", "requisitionUnit", "#34495e");
        TableColumn<PurchaseFundReplenishRecordModel, String> reasonCol = createStyledColumn("Reason", "reason", "#16a085");
        TableColumn<PurchaseFundReplenishRecordModel, String> payeeCol = createStyledColumn("Payee", "payee", "#27ae60");
        TableColumn<PurchaseFundReplenishRecordModel, Double> amountRequestedCol = createStyledColumn("Amount Requested", "amountRequested", "#2980b9");
        TableColumn<PurchaseFundReplenishRecordModel, Double> amountAvailableCol = createStyledColumn("Amount Available", "availableAmount", "#2980b9");
        TableColumn<PurchaseFundReplenishRecordModel, String> currentStatusCol = createStyledColumn("Status", "currentStatus", "#2980b9");
        TableColumn<PurchaseFundReplenishRecordModel, LocalDate> requestDateCol = createStyledColumn("Request Date", "requestDate", "#8e44ad");
        TableColumn<PurchaseFundReplenishRecordModel, String> approvalStatusCol = createStyledColumn("Approval Status", "approvalStatus", "#c0392b");
        TableColumn<PurchaseFundReplenishRecordModel, String> approvedByCol = createStyledColumn("Approved By", "approvedBy", "#7f8c8d");
        TableColumn<PurchaseFundReplenishRecordModel, String> confirmationStatusCol = createStyledColumn("Confirmation Status", "confirmationStatus", "#f39c12");
        TableColumn<PurchaseFundReplenishRecordModel, String> confirmedByCol = createStyledColumn("Confirmed By", "confirmedBy", "#d35400");
        TableColumn<PurchaseFundReplenishRecordModel, String> dispensedStatusCol = createStyledColumn("Dispensed Status", "dispensedStatus", "#16a085");
        TableColumn<PurchaseFundReplenishRecordModel, String> dispensedByCol = createStyledColumn("Dispensed By", "dispensedBy", "#27ae60");
        TableColumn<PurchaseFundReplenishRecordModel, String> dispenseApprovalStatusCol = createStyledColumn("Dispense Approval Status", "dispenseApprovalStatus", "#d35400");
        TableColumn<PurchaseFundReplenishRecordModel, String> dispenseApprovedByCol = createStyledColumn("Dispense Approved By", "dispenseApprovedBy", "#8e44ad");
        TableColumn<PurchaseFundReplenishRecordModel, String> voidStatusCol = createStyledColumn("Void Status", "voidStatus", "#e74c3c");
        TableColumn<PurchaseFundReplenishRecordModel, String> voidedByCol = createStyledColumn("Voided By", "voidedBy", "#c0392b");

        purchaseFundReplenishRequestTable.getColumns().addAll(
            requestIdCol, requisitionUnitCol, reasonCol, payeeCol,
            amountRequestedCol,amountAvailableCol, currentStatusCol,requestDateCol, approvalStatusCol,
            approvedByCol, confirmationStatusCol, confirmedByCol,
            dispensedStatusCol, dispensedByCol, dispenseApprovalStatusCol,
            dispenseApprovedByCol, voidStatusCol, voidedByCol
        );

        purchaseFundReplenishRequestTable.setItems(purchaseFundReplenishRequestData);
        
        Label placeholder = new Label("No purchase fund requests found. Click 'New Request' to create one.");
        placeholder.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14; -fx-padding: 20;");
        purchaseFundReplenishRequestTable.setPlaceholder(placeholder);

        // Proper selection listener
        purchaseFundReplenishRequestTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                updateButtonStates(newSel);
                // Enable view details button when a row is selected
                viewDetailsBtn.setDisable(false);
            } else {
                // Disable buttons when no selection
                updateButtonStates(null);
            }
        });

        tableSection.getChildren().addAll(filterBox, purchaseFundReplenishRequestTable);
        return tableSection;
    }
    
    
    private void exportSelectedRecordToPDF(PurchaseFundReplenishRecordModel record) {
    if (record == null) {
        showAlert("No Selection", "Please select a record to export.", Alert.AlertType.WARNING);
        return;
    }

    try {
        // Build the same VBox content as in viewRecordDetails
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));

        // ===== BASIC INFORMATION =====
        VBox basicInfoSection = createDetailSection("BASIC INFORMATION", "#3498db");
        GridPane basicInfoGrid = new GridPane();
        basicInfoGrid.setHgap(20);
        basicInfoGrid.setVgap(10);
        basicInfoGrid.setPadding(new Insets(10));

        basicInfoGrid.addRow(0,
            createBoldLabel("Request ID:"), new Label(getSafeString(record.getRequestId())),
            createBoldLabel("Requisition Unit:"), new Label(getSafeString(record.getRequisitionUnit()))
        );
        basicInfoGrid.addRow(1,
            createBoldLabel("Payee:"), new Label(getSafeString(record.getPayee())),
            createBoldLabel("Amount:"), new Label(getSafeAmount(record.getAmountRequested()))
        );
        basicInfoGrid.addRow(2,
            createBoldLabel("Request Date:"), new Label(getSafeDate(record.getRequestDate())),
            createBoldLabel("Reason:"), new Label(getSafeString(record.getReason()))
        );
        basicInfoSection.getChildren().add(basicInfoGrid);

        // ===== STATUS INFORMATION =====
        VBox statusSection = createDetailSection("STATUS INFORMATION", "#9b59b6");
        GridPane statusGrid = new GridPane();
        statusGrid.setHgap(20);
        statusGrid.setVgap(10);
        statusGrid.setPadding(new Insets(10));

        statusGrid.addRow(0,
            createBoldLabel("Request Approval Status:"), createStatusLabel(getSafeString(record.getApprovalStatus())),
            createBoldLabel("Request Approved By:"), new Label(getSafeString(record.getApprovedBy()))
        );
        statusGrid.addRow(1,
            createBoldLabel("Dispense Confirmation Status:"), createStatusLabel(getSafeString(record.getConfirmationStatus())),
            createBoldLabel("Confirmed By:"), new Label(getSafeString(record.getConfirmedBy()))
        );
        statusGrid.addRow(2,
            createBoldLabel("Dispensed Status:"), createStatusLabel(getSafeString(record.getDispensedStatus())),
            createBoldLabel("Dispensed By:"), new Label(getSafeString(record.getDispensedBy()))
        );
        statusGrid.addRow(3,
            createBoldLabel("Dispense Approval Status:"), createStatusLabel(getSafeString(record.getDispenseApprovalStatus())),
            createBoldLabel("Dispense Approved By:"), new Label(getSafeString(record.getDispenseApprovedBy()))
        );
        statusGrid.addRow(4,
            createBoldLabel("Void Status:"), createStatusLabel(getSafeString(record.getVoidStatus())),
            createBoldLabel("Voided By:"), new Label(getSafeString(record.getVoidedBy()))
        );
        statusSection.getChildren().add(statusGrid);

        // ===== SIGNATURES =====
        VBox signaturesSection = createDetailSection("BIOMETRIC SIGNATURES", "#e74c3c");
        HBox signaturesContainer = new HBox(20);
        signaturesContainer.setPadding(new Insets(10));
        loadSignaturesForDetails(record, signaturesContainer);

        if (signaturesContainer.getChildren().isEmpty()) {
            signaturesContainer.getChildren().add(new Label("No biometric signatures available for this request"));
        }

        signaturesSection.getChildren().add(signaturesContainer);

        mainContent.getChildren().addAll(basicInfoSection, statusSection, signaturesSection);

        // ===== EXPORT TO PDF =====
        String fileName = "PurchaseFundRequest_" + record.getRequestId() + ".pdf";
        exportNodeToPDF(mainContent, fileName);

    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Export Failed", "Failed to export PDF: " + e.getMessage(), Alert.AlertType.ERROR);
    }
}


   private <T> TableColumn<PurchaseFundReplenishRecordModel, T> createStyledColumn(String title, String property, String color) {
    TableColumn<PurchaseFundReplenishRecordModel, T> column = new TableColumn<>(title);
    column.setCellValueFactory(new PropertyValueFactory<>(property));
    column.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
    
    column.setCellFactory(tc -> new TableCell<PurchaseFundReplenishRecordModel, T>() {
        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setStyle("");
            } else {
                setText(item.toString());
                
                // Only set text colors, let row factory handle backgrounds
                String baseStyle = "-fx-border-color: #bdc3c7; -fx-border-width: 0 0 1 0;";
                
                if (property.contains("Status")) {
                    String status = item.toString().toLowerCase();
                    if (status.contains("approved") || status.contains("confirmed") || status.equals("yes")) {
                        setStyle(baseStyle + " -fx-text-fill: #155724; -fx-font-weight: bold;");
                    } else if (status.contains("pending")) {
                        setStyle(baseStyle + " -fx-text-fill: #856404; -fx-font-weight: bold;");
                    } else if (status.contains("rejected") || status.contains("voided")) {
                        setStyle(baseStyle + " -fx-text-fill: #721c24; -fx-font-weight: bold;");
                    } else {
                        setStyle(baseStyle + " -fx-text-fill: #000000;");
                    }
                } else {
                    setStyle(baseStyle + " -fx-text-fill: #000000;");
                }
            }
        }
    });
    
    return column;
}


    
     private void filterTable(String text, String value, LocalDate dateFrom, LocalDate dateTo) {
    purchaseFundReplenishRequestTable.setItems(
    purchaseFundReplenishRequestData.filtered(record -> {

            // ------------------- TEXT FILTER -------------------
            boolean matchesSearch = text == null || text.isEmpty() ||
                record.getPayee().toLowerCase().contains(text.toLowerCase()) ||
                record.getRequestId().toLowerCase().contains(text.toLowerCase()) ||
                record.getRequisitionUnit().toLowerCase().contains(text.toLowerCase()) ||
                record.getReason().toLowerCase().contains(text.toLowerCase());

            // ------------------- STATUS FILTER -------------------
            boolean matchesStatus = value.equals("All") ||
                record.getApprovalStatus().equalsIgnoreCase(value) ||
                record.getConfirmationStatus().equalsIgnoreCase(value) ||
                record.getDispensedStatus().equalsIgnoreCase(value) ||
                record.getDispenseApprovalStatus().equalsIgnoreCase(value) ||
                record.getVoidStatus().equalsIgnoreCase(value);

            // ------------------- DATE FILTER -------------------
            LocalDate requestDate = record.getRequestDate(); // must exist in model
            boolean matchesDate = true;

            if (dateFrom != null && requestDate.isBefore(dateFrom))
                matchesDate = false;

            if (dateTo != null && requestDate.isAfter(dateTo))
                matchesDate = false;

            // return final combined filter
            return matchesSearch && matchesStatus && matchesDate;
        })
    );
}

    private void refreshData() {
        loadPurchaseFundRequestsFromDatabase();
        loadEmployeeSignaturesFromDatabase();
        showAlert("Refreshed", "Data refreshed successfully!\n" + 
                  "Loaded " + purchaseFundReplenishRequestData.size() + " purchase fund requests and " + 
                  employeeSignatureData.size() + " employee signatures.", Alert.AlertType.INFORMATION);
    }

    // ==================== ADVANCED SEARCH ====================

    private void showAdvancedSearchDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Advanced Search");
        dialog.setHeaderText("Search Purchase fund Requests with Multiple Criteria");
        dialog.getDialogPane().setPrefSize(600, 500);

        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        TextField requestIdField = new TextField();
        requestIdField.setPromptText("Request ID");
        
        TextField payeeField = new TextField();
        payeeField.setPromptText("Payee Name");
        
        TextField requisitionUnitField = new TextField();
        requisitionUnitField.setPromptText("Requisition Unit");
        
        ComboBox<String> approvalStatusCombo = new ComboBox<>();
        approvalStatusCombo.getItems().addAll("All", "Pending", "Approved", "Rejected");
        approvalStatusCombo.setValue("All");
        
        ComboBox<String> confirmationStatusCombo = new ComboBox<>();
        confirmationStatusCombo.getItems().addAll("All", "Pending", "Confirmed");
        confirmationStatusCombo.setValue("All");
        
        ComboBox<String> dispenseStatusCombo = new ComboBox<>();
        dispenseStatusCombo.getItems().addAll("All", "No", "Yes");
        dispenseStatusCombo.setValue("All");
        
        DatePicker fromDateField = new DatePicker();
        fromDateField.setPromptText("From Date");
        
        DatePicker toDateField = new DatePicker();
        toDateField.setPromptText("To Date");

        grid.addRow(0, new Label("Request ID:"), requestIdField);
        grid.addRow(1, new Label("Payee:"), payeeField);
        grid.addRow(2, new Label("Requisition Unit:"), requisitionUnitField);
        grid.addRow(3, new Label("Approval Status:"), approvalStatusCombo);
        grid.addRow(4, new Label("Confirmation Status:"), confirmationStatusCombo);
        grid.addRow(5, new Label("Dispensed Status:"), dispenseStatusCombo);
        grid.addRow(6, new Label("From Date:"), fromDateField);
        grid.addRow(7, new Label("To Date:"), toDateField);

        mainContent.getChildren().addAll(
            new Label("Enter search criteria (leave blank for any):"),
            grid
        );

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        ButtonType searchButtonType = new ButtonType("SEARCH", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL, ButtonType.CLOSE);

        dialog.getDialogPane().setContent(scrollPane);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                performAdvancedSearch(
                    requestIdField.getText(),
                    payeeField.getText(),
                    requisitionUnitField.getText(),
                    approvalStatusCombo.getValue(),
                    confirmationStatusCombo.getValue(),
                    dispenseStatusCombo.getValue(),
                    fromDateField.getValue(),
                    toDateField.getValue()
                );
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void performAdvancedSearch(String requestId, String payee, String requisitionUnit, 
                                     String approvalStatus, String confirmationStatus, String dispensedStatus,
                                     LocalDate fromDate, LocalDate toDate) {
        purchaseFundReplenishRequestTable.setItems(purchaseFundReplenishRequestData.filtered(record -> {
            boolean matchesRequestId = requestId.isEmpty() || 
                record.getRequestId().toLowerCase().contains(requestId.toLowerCase());
            
            boolean matchesPayee = payee.isEmpty() || 
                record.getPayee().toLowerCase().contains(payee.toLowerCase());
            
            boolean matchesUnit = requisitionUnit.isEmpty() || 
                record.getRequisitionUnit().toLowerCase().contains(requisitionUnit.toLowerCase());
            
            boolean matchesApproval = "All".equals(approvalStatus) || 
                record.getApprovalStatus().equalsIgnoreCase(approvalStatus);
            
            boolean matchesConfirmation = "All".equals(confirmationStatus) || 
                record.getConfirmationStatus().equalsIgnoreCase(confirmationStatus);
            
            boolean matchesDispensed = "All".equals(dispensedStatus) || 
                record.getDispensedStatus().equalsIgnoreCase(dispensedStatus);
            
            boolean matchesDate = true;
            if (fromDate != null) {
                matchesDate = !record.getRequestDate().isBefore(fromDate);
            }
            if (toDate != null && matchesDate) {
                matchesDate = !record.getRequestDate().isAfter(toDate);
            }
            
            return matchesRequestId && matchesPayee && matchesUnit && 
                   matchesApproval && matchesConfirmation && matchesDispensed && matchesDate;
        }));

        showAlert("Search Complete", 
            "Found " + purchaseFundReplenishRequestTable.getItems().size() + " matching records.",
            Alert.AlertType.INFORMATION);
    }

    // ==================== PDF EXPORT ====================

    private void exportToPdf() {
        showAlert("PDF Export", "PDF export functionality will be implemented in the next version.", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        switch (type) {
            case INFORMATION:
                alert.getDialogPane().setStyle("-fx-background-color: #d4edda; -fx-border-color: #c3e6cb;");
                break;
            case WARNING:
                alert.getDialogPane().setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffeaa7;");
                break;
            case ERROR:
                alert.getDialogPane().setStyle("-fx-background-color: #f8d7da; -fx-border-color: #f5c6cb;");
                break;
        }
        
        alert.showAndWait();
    }

    @FunctionalInterface
    private interface BiometricVerificationCallback {
        void onVerificationComplete(EmployeeSignatureModel employee);
    }

    public void cleanup() {
        if (fingerprintModule != null) {
            fingerprintModule.closeDevice();
        }
        if (databaseConnector != null) {
            databaseConnector.close();
        }
    }
}