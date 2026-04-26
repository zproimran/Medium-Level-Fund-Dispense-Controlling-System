
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
import javafx.stage.FileChooser;
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
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import javafx.event.ActionEvent;
import javafx.scene.web.WebView;
import javafx.util.Pair;

/**
 * PurchaseFundByAgreementRequestManagement - Main class for managing agreement-based purchase fund requests
 * This class provides a comprehensive UI for creating, editing, approving, and tracking purchase fund requests
 * with biometric signature verification and advanced reporting capabilities.
 */
public class PurchaseFundByAgreementRequestManagement extends BorderPane {
    
    // ==================== CLASS VARIABLES ====================
    
    /** Current logged-in user */
    private String currentUser;

    /** Table view for displaying purchase fund requests */
    private TableView<AgreementBasedPurchaseFundRecordModel> agreementBasedPurchaseFundRequestTable;
    
    /** Observable list holding purchase fund request data */
    private ObservableList<AgreementBasedPurchaseFundRecordModel> agreementBasedPurchaseFundRequestData = FXCollections.observableArrayList();
    
    /** Observable list holding employee signature data */
    private ObservableList<EmployeeSignatureModel> employeeSignatureData = FXCollections.observableArrayList();

    // ==================== UI COMPONENTS ====================
    
    /** Workflow action buttons */
    private Button confirmBtn;
    private Button dispenseBtn;
    private Button approveRequestBtn;
    private Button approveDispenseBtn;
    private Button editBtn;
    private Button voidBtn;
    private Button deleteBtn;
    private Button requestBtn;
    private Button connectDeviceBtn;
    private Button disconnectDeviceBtn;
    private Button viewDetailsBtn;
    private Button exportPdfBtn;

    // ==================== SYSTEM COMPONENTS ====================
    
    /** Fingerprint module for biometric verification */
    private ZKTECO fingerprintModule;
    
    /** Database connector for data operations */
    private Connecting databaseConnector;
    
    /** Status labels for device and operations */
    private Label deviceStatusLabel;
    private Label operationStatusLabel;
    private Label summaryLabel;
    boolean isAdmin=false;
    boolean isCashier=false;
    boolean isFinanceAdmin=false;
    boolean isAccountant=false;

    // ==================== CONSTRUCTOR ====================
    
    /**
     * Constructor - Initializes the purchase fund request management interface
     * @param username The current logged-in user
     */
    public PurchaseFundByAgreementRequestManagement(String username) {
        this.currentUser = username;
        fingerprintModule = new ZKTECO();
        databaseConnector = new Connecting();
        
        isAdmin=databaseConnector.isAdmin(currentUser.toLowerCase());
        isCashier=databaseConnector.isCashier(currentUser.toLowerCase());
        isAccountant=databaseConnector.isAccountant(currentUser.toLowerCase());
        isFinanceAdmin=databaseConnector.isFinanceAdmin(currentUser.toLowerCase());
        initializeUI();
        loadEmployeeSignaturesFromDatabase();
        loadPurchaseFundRequestsFromDatabase();
        initializeDeviceStatus();
        initializeSummary();
    }

    // ==================== UI INITIALIZATION METHODS ====================
    
    /**
     * Initializes the main user interface components
     */
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
        this.setStyle("-fx-background-color: linear-gradient(to bottom, #f2f3fa, #e5ecef);");
    }

    /**
     * Creates the header section with hospital information and branding
     * @return VBox containing header components
     */
    private VBox createHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: teal; -fx-padding: 20; -fx-alignment: center;-fx-fill-text:white");
        
        Text departmentName = new Text("AGREEMENT BASED PURCHASE FUND MANAGEMENT");
        departmentName.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        departmentName.setFill(Color.WHITE);
        
        Text hospitalName = new Text("AFRAN GENERAL HOSPITAL");
        hospitalName.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        hospitalName.setFill(Color.LIGHTBLUE);
        
        Text currentDate = new Text("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
        currentDate.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        currentDate.setFill(Color.WHITE);
        
        VBox textContainer = new VBox(8);
        textContainer.setAlignment(Pos.CENTER);
        textContainer.getChildren().addAll(departmentName, hospitalName, currentDate);
        
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

    /**
     * Creates the footer section with device status and system information
     * @return HBox containing footer components
     */
    private HBox createFooter() {
        HBox footer = new HBox(10);
        footer.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #34495e); -fx-padding: 15; -fx-alignment: center;");
        
        deviceStatusLabel = new Label("Device Status: Not Connected");
        deviceStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        
        operationStatusLabel = new Label("Ready for operations");
        operationStatusLabel.setStyle("-fx-text-fill: #ecf0f1;");
        
        Text footerText = new Text("© 2025 Afran General Hospital - Smart HRMS | Agreement Based Purchase Fund Management System v3.0 | Biometric Signature Integration");
        footerText.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        footerText.setFill(Color.LIGHTGRAY);
        
        footer.getChildren().addAll(deviceStatusLabel, new Separator(), operationStatusLabel, new Separator(), footerText);
        return footer;
    }

    /**
     * Creates the summary panel showing request statistics
     * @return HBox containing summary information
     */
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

    /**
     * Initializes and updates the summary information
     */
    private void initializeSummary() {
        updateSummary();
    }

    /**
     * Updates the summary label with current request statistics
     */
    private void updateSummary() {
        if (agreementBasedPurchaseFundRequestData.isEmpty()) {
            summaryLabel.setText("No Agreement based purchase fund requests found");
            return;
        }

        long totalRequests = agreementBasedPurchaseFundRequestData.size();
        long pendingApproval = agreementBasedPurchaseFundRequestData.stream()
            .filter(r -> "Pending".equalsIgnoreCase(r.getApprovalStatus()))
            .count();
        long approved = agreementBasedPurchaseFundRequestData.stream()
            .filter(r -> "Approved".equalsIgnoreCase(r.getApprovalStatus()))
            .count();
        long confirmed = agreementBasedPurchaseFundRequestData.stream()
            .filter(r -> "Confirmed".equalsIgnoreCase(r.getConfirmationStatus()))
            .count();
        long dispensed = agreementBasedPurchaseFundRequestData.stream()
            .filter(r -> "Yes".equalsIgnoreCase(r.getDispensedStatus()))
            .count();

        double totalAmount = agreementBasedPurchaseFundRequestData.stream()
            .mapToDouble(r -> {
                try {
                    return Double.parseDouble(String.valueOf(r.getAmountRequested()));
                } catch (Exception e) {
                    return 0.0;
                }
            })
            .sum();

        String summary = String.format(
            "Summary: Total: %d | Pending: %d | Approved: %d | Confirmed: %d | Dispensed: %d | Total Amount: ETB %.2f",
            totalRequests, pendingApproval, approved, confirmed, dispensed, totalAmount
        );
        summaryLabel.setText(summary);
    }

    /**
     * Creates the main button panel with all action buttons
     * @return VBox containing organized button groups
     */
    private VBox createButtonPanel() {
        VBox vBox = new VBox(10);
        
        HBox buttonPanel1 = new HBox(10);
        HBox buttonPanel2 = new HBox(10);
        
        buttonPanel1.setAlignment(Pos.CENTER_LEFT);
        buttonPanel2.setAlignment(Pos.CENTER_LEFT);

        // CRUD Operations
        requestBtn = createStyledButton("New Request", "#27ae60");
        requestBtn.setOnAction(e -> showPurchaseFundRequestForm());

        editBtn = createStyledButton("Edit Request", "#2980b9");
        editBtn.setOnAction(e -> editSelected());
        
        voidBtn = createStyledButton("Void Request", "#e74c3c");
        voidBtn.setOnAction(e -> voidSelected());

        deleteBtn = createStyledButton("Delete Request", "#c0392b");
        deleteBtn.setOnAction(e -> deleteSelected());

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
            AgreementBasedPurchaseFundRecordModel selected = agreementBasedPurchaseFundRequestTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showDispenseForm(selected);
            } else {
                showAlert("No Selection", "Please select a purchase Fund request to dispense.", Alert.AlertType.WARNING);
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
            requestBtn, editBtn, voidBtn, deleteBtn, viewDetailsBtn, new Separator(),
            approveRequestBtn, confirmBtn, dispenseBtn, approveDispenseBtn
        );
        buttonPanel2.getChildren().addAll(
            connectDeviceBtn, disconnectDeviceBtn, new Separator(),
            exportExcelBtn, exportWordBtn, exportPdfBtn, printBtn, searchAdvancedBtn, refreshBtn
        );
        }
        else if(isCashier){
        buttonPanel1.getChildren().addAll(
            voidBtn,viewDetailsBtn, new Separator(),
            dispenseBtn
        );
        buttonPanel2.getChildren().addAll(
            connectDeviceBtn, disconnectDeviceBtn, new Separator(),
            exportExcelBtn, exportWordBtn, exportPdfBtn, printBtn, searchAdvancedBtn, refreshBtn
        );
        }
        else if(isFinanceAdmin){
        
            buttonPanel1.getChildren().addAll(
            requestBtn,voidBtn,viewDetailsBtn, new Separator(),
            approveRequestBtn,approveDispenseBtn
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
        
        vBox.getChildren().addAll(buttonPanel1, buttonPanel2);

        return vBox;
    }

    /**
     * Creates a styled button with hover effects
     * @param text Button text
     * @param color Background color
     * @return Styled Button object
     */
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5;");
        button.setPrefHeight(35);
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: derive(" + color + ", 20%); -fx-text-fill: white; -fx-background-radius: 5;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5;"));
        return button;
    }

    // ==================== DATABASE OPERATIONS ====================
    
    /**
     * Loads purchase fund requests from database asynchronously
     */
    private void loadPurchaseFundRequestsFromDatabase() {
        new Thread(() -> {
            try {
                List<AgreementBasedPurchaseFundRecordModel> requests = databaseConnector.getAllAgreementBasedPurchaseFundRequests();
                Platform.runLater(() -> {
                    agreementBasedPurchaseFundRequestData.clear();
                    agreementBasedPurchaseFundRequestData.addAll(requests);
                    System.out.println("Loaded " + requests.size() + " purchase Fund requests from database");
                    updateSummary();
                    
                    if (!agreementBasedPurchaseFundRequestData.isEmpty()) {
                        agreementBasedPurchaseFundRequestTable.getSelectionModel().selectFirst();
                        updateButtonStates(agreementBasedPurchaseFundRequestTable.getSelectionModel().getSelectedItem());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> 
                    showAlert("Database Error", "Failed to load purchase fund requests: " + e.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    /**
     * Loads employee signatures from database asynchronously
     */
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
    
    /**
     * Initializes device status display
     */
    private void initializeDeviceStatus() {
        updateDeviceStatus(false, "Not Connected");
    }

    /**
     * Updates the device status display
     * @param connected Whether device is connected
     * @param message Status message to display
     */
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

    /**
     * Connects to the fingerprint device
     */
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

    /**
     * Disconnects from the fingerprint device
     */
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

    /**
     * Shows alert when device is not connected
     */
    private void showDeviceNotConnectedAlert() {
        showAlert("Device Not Connected", 
            "Please connect to the fingerprint device first.\n\n" +
            "Click 'Connect Device' to establish connection.\n" +
            "Required for biometric signature verification.",
            Alert.AlertType.WARNING);
    }

    // ==================== CRUD OPERATIONS ====================
    
    /**
     * Shows the purchase fund request form for creating new requests
     */
private void showPurchaseFundRequestForm() {
    try {
        Dialog<AgreementBasedPurchaseFundRecordModel> dialog = new Dialog<>();
        dialog.setTitle("New Agreement-Based Purchase Fund Request");
        dialog.setHeaderText("Create New Agreement-Based Purchase Fund Request for Afran General Hospital");
        dialog.getDialogPane().setPrefSize(900, 850);

        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // ----------------------------------------------------------
        // Requisition Unit
        // ----------------------------------------------------------
        TextField requisitionUnitField = new TextField();
        requisitionUnitField.setPromptText("e.g., HR Department");
        
        
        ComboBox<String> mainCategoryComboBox = new ComboBox<>();
    mainCategoryComboBox.setPromptText("Select main category...");
    mainCategoryComboBox.setEditable(false);
    
    // Load main categories
    List<String> mainCategories = databaseConnector.getAgreementBasedPurchaseFundMainCategories();
    mainCategoryComboBox.getItems().addAll(mainCategories);

    ComboBox<String> subCategoryComboBox = new ComboBox<>();
    subCategoryComboBox.setPromptText("Select sub-category...");
    subCategoryComboBox.setEditable(false);

    // Load related subcategories when main category changes
    mainCategoryComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
        subCategoryComboBox.getItems().clear();
        if (newVal != null) {
            List<String> subCategories = databaseConnector.getAgreementBasedPurchaseFundSubCategories(newVal);
            subCategoryComboBox.getItems().addAll(subCategories);
        }
    });
    
    Label mainCategoryLabel = new Label("Main Category");
    Label subCategoryLabel = new Label("Sub Category");

        // ----------------------------------------------------------
        // Reason
        // ----------------------------------------------------------
        TextArea reasonField = new TextArea();
        reasonField.setPrefRowCount(8);
        reasonField.setWrapText(true);
        reasonField.setFont(AmharicFontLoader.getAmharicFont(14f));
        reasonField.setPromptText("Enter reason for Agreement-Based Purchase Fund request...");

        // ----------------------------------------------------------
        // Agreement Section
        // ----------------------------------------------------------
        PurchaseFundAgreementModel agreement;
        List<PurchaseFundAgreementModel> allAgreements = databaseConnector.getAllAgreements();
        agreement = allAgreements.isEmpty() ? new PurchaseFundAgreementModel() : allAgreements.get(0);

        Pair<VBox, Map<String, TextArea>> agreementPair = createAgreementTemplateSection(agreement);
        VBox agreementSection = agreementPair.getKey();
        Map<String, TextArea> agreementFields = agreementPair.getValue();
        GridPane.setColumnSpan(agreementSection, 2);

        // ----------------------------------------------------------
        // Payee ComboBox
        // ----------------------------------------------------------
        ComboBox<String> payeeComboBox = new ComboBox<>();
        payeeComboBox.setPromptText("Select payee name...");
        payeeComboBox.getItems().addAll(databaseConnector.getEmployeeSignatureNames());

        // ----------------------------------------------------------
        // Amount
        // ----------------------------------------------------------
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount in ETB");

        // ----------------------------------------------------------
        // Request Date
        // ----------------------------------------------------------
        DatePicker requestDateField = new DatePicker(LocalDate.now());
        requestDateField.setDisable(true);

        // ----------------------------------------------------------
        // File Upload Section
        // ----------------------------------------------------------
        Label uploadLabel = new Label("Upload Required Documents:");
        uploadLabel.setFont(AmharicFontLoader.getAmharicFont(14));

        VBox uploadBox = new VBox(10);
        uploadBox.setPadding(new Insets(10));
        uploadBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: #f4f4f4;");
        uploadBox.setPrefHeight(220);

        List<File> uploadedFiles = new ArrayList<>();
        VBox fileListBox = new VBox(10);

        ScrollPane fileScroll = new ScrollPane(fileListBox);
        fileScroll.setFitToWidth(true);
        fileScroll.setPrefHeight(180);

        Button uploadButton = new Button("Upload Files");
        uploadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Files to Upload");

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Files", "*.*"),
                    new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.docx", "*.txt"),
                    new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png")
            );

            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(
                    dialog.getDialogPane().getScene().getWindow()
            );

            if (selectedFiles != null) {
                for (File file : selectedFiles) {
                    if (!uploadedFiles.contains(file)) {
                        uploadedFiles.add(file);

                        VBox filePreview = new VBox(5);
                        filePreview.setAlignment(Pos.TOP_LEFT);
                        filePreview.setStyle("-fx-border-color: #ddd; -fx-padding: 8; -fx-background-color: white;");
                        filePreview.setPrefWidth(800);

                        Label fileName = new Label("📄 " + file.getName());
                        fileName.setFont(Font.font("Arial", FontWeight.BOLD, 13));

                        Node previewNode;

                        // Image preview
                        if (file.getName().toLowerCase().matches(".*\\.(jpg|jpeg|png)$")) {
                            ImageView imageView = new ImageView(new Image(file.toURI().toString()));
                            imageView.setFitWidth(180);
                            imageView.setPreserveRatio(true);
                            previewNode = imageView;
                        }
                        // PDF preview
                        else if (file.getName().toLowerCase().endsWith(".pdf")) {
                            WebView pdfView = new WebView();
                            pdfView.getEngine().load(file.toURI().toString());
                            pdfView.setPrefHeight(200);
                            previewNode = pdfView;
                        }
                        // TXT preview
                        else if (file.getName().toLowerCase().endsWith(".txt")) {
                            String text = "";
                            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                                text = br.lines().limit(15).reduce("", (a, b) -> a + "\n" + b);
                            } catch (Exception ex) {
                                text = "Error reading text file.";
                            }
                            TextArea textArea = new TextArea(text);
                            textArea.setEditable(false);
                            textArea.setWrapText(true);
                            textArea.setPrefHeight(160);
                            previewNode = textArea;
                        }
                        // No preview
                        else {
                            previewNode = new Label("(Preview not available)");
                        }

                        Button removeBtn = new Button("Remove");
                        removeBtn.setOnAction(ev -> {
                            uploadedFiles.remove(file);
                            fileListBox.getChildren().remove(filePreview);
                        });

                        filePreview.getChildren().addAll(fileName, previewNode, removeBtn);
                        fileListBox.getChildren().add(filePreview);
                    }
                }
            }
        });

        uploadBox.getChildren().addAll(uploadButton, fileScroll);

        // ----------------------------------------------------------
        // Add fields to grid
        // ----------------------------------------------------------
        grid.addRow(0, new Label("Requisition Unit*:"), requisitionUnitField);
        grid.addRow(1, mainCategoryLabel, mainCategoryComboBox);
        grid.addRow(2, subCategoryLabel, subCategoryComboBox);
        grid.addRow(3, new Label("Reason*:"), reasonField);
        grid.addRow(4, new Label("Employment Agreement Template:"), agreementSection);
        grid.addRow(5, new Label("Payee*:"), payeeComboBox);
        grid.addRow(6, new Label("Amount*:"), amountField);
        grid.addRow(7, new Label("Request Date:"), requestDateField);
        grid.addRow(8, uploadLabel, uploadBox);

        mainContent.getChildren().addAll(new Label("Please fill in all required fields (*):"), grid);

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        dialog.getDialogPane().setContent(scrollPane);

        // ----------------------------------------------------------
        // Buttons
        // ----------------------------------------------------------
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        // ----------------------------------------------------------
        // Validation
        // ----------------------------------------------------------
        Runnable validateForm = () -> {
            boolean isValid =
                    !requisitionUnitField.getText().trim().isEmpty() &&
                    !reasonField.getText().trim().isEmpty() &&
                    payeeComboBox.getValue() != null &&
                    !amountField.getText().trim().isEmpty() &&
                    amountField.getText().matches("\\d+(\\.\\d{1,2})?");
            okButton.setDisable(!isValid);
        };

        requisitionUnitField.textProperty().addListener((o, a, b) -> validateForm.run());
        reasonField.textProperty().addListener((o, a, b) -> validateForm.run());
        payeeComboBox.valueProperty().addListener((o, a, b) -> validateForm.run());
        amountField.textProperty().addListener((o, a, b) -> validateForm.run());

        // ----------------------------------------------------------
        // Max Amount Validation
        // ----------------------------------------------------------
        okButton.addEventFilter(ActionEvent.ACTION, event -> {

            double maxAllowed = databaseConnector.getMaximumRequest("agreement based purchase fund");
            double availableAmount = databaseConnector.getAvailableAgreementBasedPurchaseFundAmount();

            if (!amountField.getText().matches("\\d+(\\.\\d{1,2})?")) {
                showAlert("Invalid Amount", "Please enter a valid amount.", Alert.AlertType.ERROR);
                event.consume();
                return;
            }

            double amount = Double.parseDouble(amountField.getText());

            if (amount > maxAllowed) {
                showAlert(
                        "Amount Exceeds Limit",
                        "Requested amount exceeds the allowed limit.\nMax: " + maxAllowed,
                        Alert.AlertType.ERROR
                );
                event.consume();
            }
        });

        // ----------------------------------------------------------
        // Result Converter
        // ----------------------------------------------------------
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {

                String requestId = generateRequestId();
                double amount = Double.parseDouble(amountField.getText());

                return new AgreementBasedPurchaseFundRecordModel(
        requestId,
        requisitionUnitField.getText(),
        mainCategoryComboBox.getValue(),   // ✅ main category
        subCategoryComboBox.getValue(),    // ✅ subcategory
        reasonField.getText(),
        payeeComboBox.getValue(),
        amount,
        LocalDate.now(),
        "Pending", "", "Pending", "", "No", "", "No", "", "Pending", "",
        agreementFields.get("ስም (Employee Name)").getText(),
        agreementFields.get("የውል መጀመሪያ (Introduction)").getText(),
        agreementFields.get("የውል ዓላማ (Purpose)").getText(),
        agreementFields.get("የስምምነቱ ፍቃድ (Mutual Consent)").getText(),
        agreementFields.get("ውል ተቀባይና ውል ሰጪ (Parties)").getText(),
        agreementFields.get("የሥራው ዓይነት (Nature of Work)").getText(),
        agreementFields.get("የውል ሰጪ መብቶች (Employer Rights)").getText(),
        agreementFields.get("የውል ተቀባይ መብቶች (Employee Rights)").getText(),
        agreementFields.get("የውል ሰጪ ግዴታዎች (Employer Duties)").getText(),
        agreementFields.get("የውል ተቀባይ ግዴታዎች (Employee Duties)").getText()
);

            }
            return null;
        });

        // ----------------------------------------------------------
        // Save Result
        // ----------------------------------------------------------
        Optional<AgreementBasedPurchaseFundRecordModel> result = dialog.showAndWait();
        result.ifPresent(newRecord -> {

            boolean saved = databaseConnector.saveAgreementBasedPurchaseFundRequest(newRecord, uploadedFiles);

            if (saved) {
                agreementBasedPurchaseFundRequestData.add(0, newRecord);
                agreementBasedPurchaseFundRequestTable.getSelectionModel().select(newRecord);
                updateSummary();

                showAlert(
                        "Success",
                        "Purchase fund request created successfully!\nRequest ID: " + newRecord.getRequestId(),
                        Alert.AlertType.INFORMATION
                );
            } else {
                showAlert("Error", "Failed to save request to database.", Alert.AlertType.ERROR);
            }
        });

    } catch (SQLException ex) {
        ex.printStackTrace();
        showAlert("Error", "Database error: " + ex.getMessage(), Alert.AlertType.ERROR);
    }
}



    /**
     * Create Agreement Section and return a Pair of VBox and Map of TextAreas
     * @param agreement The agreement model containing template data
     * @return Pair containing the agreement section VBox and field mapping
     */
    private Pair<VBox, Map<String, TextArea>> createAgreementTemplateSection(PurchaseFundAgreementModel agreement) {
        VBox agreementBox = new VBox(10);
        agreementBox.setPadding(new Insets(15));
        agreementBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-background-color: #f9f9f9;");

        Label titleLabel = new Label("የሥራ ውል ስምምነት (Employment Agreement)");
        titleLabel.setFont(AmharicFontLoader.getAmharicFont(16));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        VBox agreementContent = new VBox(15);
        agreementContent.setPadding(new Insets(10));

        Map<String, TextArea> fieldMap = new HashMap<>();

        BiFunction<String, String, TextArea> createSection = (labelText, content) -> {
            TextArea area = new TextArea(content != null ? content : "");
            area.setFont(AmharicFontLoader.getAmharicFont(13));
            area.setWrapText(true);
            area.setPrefRowCount(5);
            fieldMap.put(labelText, area);
            agreementContent.getChildren().addAll(new Label(labelText), area);
            return area;
        };

        createSection.apply("ስም (Employee Name)", agreement.getEmployeeName());
        createSection.apply("የውል መጀመሪያ (Introduction)", agreement.getIntroduction());
        createSection.apply("የውል ዓላማ (Purpose)", agreement.getPurpose());
        createSection.apply("የስምምነቱ ፍቃድ (Mutual Consent)", agreement.getConsent());
        createSection.apply("ውል ተቀባይና ውል ሰጪ (Parties)", agreement.getParties());
        createSection.apply("የሥራው ዓይነት (Nature of Work)", agreement.getNatureOfWork());
        createSection.apply("የውል ሰጪ መብቶች (Employer Rights)", agreement.getEmployerRights());
        createSection.apply("የውል ተቀባይ መብቶች (Employee Rights)", agreement.getEmployeeRights());
        createSection.apply("የውል ሰጪ ግዴታዎች (Employer Duties)", agreement.getEmployerDuties());
        createSection.apply("የውል ተቀባይ ግዴታዎች (Employee Duties)", agreement.getEmployeeDuties());

        ScrollPane scrollPane = new ScrollPane(agreementContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        agreementBox.getChildren().addAll(titleLabel, scrollPane);
        return new Pair<>(agreementBox, fieldMap);
    }

    /**
     * Generates a unique request ID
     * @return Generated request ID string
     */
private String generateRequestId() {
    String prefix = "AGRBPF"; // Agreement prefix
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    String datePart = LocalDate.now().format(dateFormatter);
    
    int maxNumber = agreementBasedPurchaseFundRequestData.stream()
        .filter(record -> record.getRequestId() != null && record.getRequestId().startsWith(prefix + datePart))
        .map(record -> record.getRequestId())
        .map(id -> id.substring(prefix.length() + datePart.length()))
        .filter(part -> part.matches("\\d+"))
        .mapToInt(Integer::parseInt)
        .max()
        .orElse(0);
    
    return prefix + datePart + String.format("%03d", maxNumber + 1);
}
// Examples: AGRBPF20231215001, AGRBPF20231215002

    /**
     * Edits the selected purchase fund request
     */
    private void editSelected() {
        AgreementBasedPurchaseFundRecordModel selected = agreementBasedPurchaseFundRequestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a request to edit.", Alert.AlertType.WARNING);
            return;
        }
        
        if ("Yes".equalsIgnoreCase(selected.getVoidStatus())) {
            showAlert("Cannot Edit", "Voided requests cannot be edited.", Alert.AlertType.WARNING);
            return;
        }
        
        if ("Yes".equalsIgnoreCase(selected.getDispensedStatus())) {
            showAlert("Cannot Edit", "Dispensed requests cannot be edited.", Alert.AlertType.WARNING);
            return;
        }
        
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Agreement Based Purchase Fund Request");
        dialog.setHeaderText("Edit Request: " + selected.getRequestId());
        dialog.getDialogPane().setPrefSize(500, 400);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        TextField requisitionUnitField = new TextField(selected.getRequisitionUnit());
        TextArea reasonField = new TextArea(selected.getReason());
        reasonField.setPrefRowCount(3);
        reasonField.setWrapText(true);
        
        ComboBox<String> payeeComboBox = new ComboBox<>();
        payeeComboBox.setPromptText("Select payee name...");
        List<String> employeeNames = databaseConnector.getEmployeeSignatureNames();
        payeeComboBox.getItems().addAll(employeeNames);
        payeeComboBox.setValue(selected.getPayee());
        
        TextField amountField = new TextField(String.valueOf(selected.getAmountRequested()));

        content.getChildren().addAll(
            new Label("Requisition Unit:"), requisitionUnitField,
            new Label("Reason:"), reasonField,
            new Label("Payee:"), payeeComboBox,
            new Label("Amount:"), amountField
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return true;
            }
            return false;
        });

        dialog.showAndWait().ifPresent(confirmed -> {
            if (confirmed) {
                selected.setRequisitionUnit(requisitionUnitField.getText());
                selected.setReason(reasonField.getText());
                selected.setPayee(payeeComboBox.getValue());
                selected.setAmountRequested(Double.parseDouble(amountField.getText()));
                
                boolean updated = databaseConnector.updateAgreementBasedPurchaseFundRequest(selected);
                if (updated) {
                    agreementBasedPurchaseFundRequestTable.refresh();
                    updateSummary();
                    showAlert("Success", "Request updated successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to update request in database.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    /**
     * Voids the selected purchase fund request
     */
    private void voidSelected() {
    AgreementBasedPurchaseFundRecordModel selected = agreementBasedPurchaseFundRequestTable.getSelectionModel().getSelectedItem();
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
    dialog.setTitle("Void Agreement Based Purchase Fund Request");
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
        new Label("• Request ID: " + selected.getRequestId()),
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
        selected.setVoidedBy(currentUser); // Use currentUser instead of "System Admin"
        
        // Update database with void reason
        boolean updated = databaseConnector.voidAgreementBasedPurchaseFundRequest(
            selected.getRequestId(), 
            currentUser, // Use currentUser instead of "System Admin"
            voidReason   // Pass the void reason
        );
        
        if (updated) {
            agreementBasedPurchaseFundRequestTable.refresh();
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

    /**
     * Deletes the selected purchase fund request
     */
    private void deleteSelected() {
        AgreementBasedPurchaseFundRecordModel selected = agreementBasedPurchaseFundRequestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a request to delete.", Alert.AlertType.WARNING);
            return;
        }

        if ("Yes".equalsIgnoreCase(selected.getDispensedStatus())) {
            showAlert("Cannot Delete", "Dispensed requests cannot be deleted.", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete Agreement Based Purchase Fund Request");
        confirmation.setContentText("Are you sure you want to permanently delete request " + selected.getRequestId() + "?\n\n" +
                                  "Request: " + selected.getReason() + "\n" +
                                  "Amount: ETB " + selected.getAmountRequested() + "\n\n" +
                                  "This action cannot be undone!");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = databaseConnector.deleteRecieptBasedPurchaseFundRequest(selected.getRequestId());
            if (deleted) {
                agreementBasedPurchaseFundRequestData.remove(selected);
                updateSummary();
                showAlert("Success", "Request deleted successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to delete request from database.", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Views details of the selected purchase fund request
     */
    private void viewSelectedDetails() {
        AgreementBasedPurchaseFundRecordModel selected = agreementBasedPurchaseFundRequestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a request to view details.", Alert.AlertType.WARNING);
            return;
        }
        viewRecordDetails(selected);
    }

    // ==================== WORKFLOW METHODS ====================
    
    /**
     * Approves request with biometric verification
     */
    private void approveRequestWithBiometric() {
        AgreementBasedPurchaseFundRecordModel selected = agreementBasedPurchaseFundRequestTable.getSelectionModel().getSelectedItem();
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

    /**
     * Confirms request with biometric verification
     */
    private void confirmWithBiometric() {
        AgreementBasedPurchaseFundRecordModel selected = agreementBasedPurchaseFundRequestTable.getSelectionModel().getSelectedItem();
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

    /**
     * Approves dispense with biometric verification
     */
    private void approveDispenseWithBiometric() {
        AgreementBasedPurchaseFundRecordModel selected = agreementBasedPurchaseFundRequestTable.getSelectionModel().getSelectedItem();
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

        showDispenseApprovalWithBiometricDialog(selected);
    }

    // ==================== BIOMETRIC DIALOGS ====================
    
    /**
     * Shows dialog for request approval with biometric verification
     * @param record The purchase fund record to approve
     */
    private void showRequestApprovalWithBiometricDialog(AgreementBasedPurchaseFundRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Approve Request with Biometric Signature");
        dialog.setHeaderText("Approve Agreement Based Purchase Fund Request - " + record.getRequestId());
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

    /**
     * Shows dialog for dispense approval with biometric verification
     * @param record The purchase fund record to approve dispense for
     */
    private void showDispenseApprovalWithBiometricDialog(AgreementBasedPurchaseFundRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Approve Dispense with Biometric Signature");
        dialog.setHeaderText("Approve Agreement Based Purchase Fund Dispense - " + record.getRequestId());
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

    /**
     * Shows dialog for request confirmation with biometric verification
     * @param record The purchase fund record to confirm
     */
    private void showConfirmationWithBiometricDialog(AgreementBasedPurchaseFundRecordModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirm Request with Biometric Signature");
        dialog.setHeaderText("Confirm Agreement Based Purchase Fund Request - " + record.getRequestId());
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

    /**
     * Shows the dispense form with dual biometric verification
     * @param record The purchase fund record to dispense
     */
    private void showDispenseForm(AgreementBasedPurchaseFundRecordModel record) {
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
            
            DispensedAgreementBasedPurchaseFundModel dispensedRecord = new DispensedAgreementBasedPurchaseFundModel(
                record.getRequestId(),
                record.getRequisitionUnit(),
                record.getReason(),
                verifiedPayee.getEmployeeName(),
                record.getAmountRequested(),   // double
                Double.parseDouble(givenAmountField.getText()),  // double
                verifiedDispenser.getEmployeeName(),
                record.getRequestDate(),
                completedDateField.getValue(),
                payeeFingerprintRef.get(),
                "Dual Biometric Verified"
            );

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

    /**
     * Shows fingerprint verification dialog
     * @param callback Callback function to handle verification result
     */
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

    /**
     * Finds employee by fingerprint template
     * @param capturedTemplate The captured fingerprint template
     * @return EmployeeSignatureModel if match found, null otherwise
     */
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

    /**
     * Saves dispensed record with dual signatures to database
     * @param record The dispensed record
     * @param payeeSignature Payee signature image data
     * @param dispenserSignature Dispenser signature image data
     * @param payeeFingerprint Payee fingerprint template
     * @param dispenserFingerprint Dispenser fingerprint template
     */
    private void saveDispensedRecordWithDualSignatures(DispensedAgreementBasedPurchaseFundModel record, 
                                                     byte[] payeeSignature, byte[] dispenserSignature,
                                                     byte[] payeeFingerprint, byte[] dispenserFingerprint) {
        new Thread(() -> {
            try {
                boolean success = databaseConnector.saveDispensedAgreementBasedPurchaseFundRecordWithDualSignatures(
                    record, payeeSignature, dispenserSignature, payeeFingerprint, dispenserFingerprint
                );

                Platform.runLater(() -> {
                    if (success) {
                        System.out.println("Dual signature dispense record saved successfully");
                        
                        // Find and update the corresponding PurchaseFundRecordModel
                        AgreementBasedPurchaseFundRecordModel updatedRecord = findPurchaseFundRecordById(record.getRequestId());
                        if (updatedRecord != null) {
                            updatedRecord.setDispensedStatus("Yes");
                            updatedRecord.setDispensedBy(record.getGivenBy());
                            agreementBasedPurchaseFundRequestTable.refresh();
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

    /**
     * Helper method to find PurchaseFundRecordModel by request ID
     * @param requestId The request ID to search for
     * @return AgreementBasedPurchaseFundRecordModel if found, null otherwise
     */
    private AgreementBasedPurchaseFundRecordModel findPurchaseFundRecordById(String requestId) {
        return agreementBasedPurchaseFundRequestData.stream()
            .filter(record -> record.getRequestId().equals(requestId))
            .findFirst()
            .orElse(null);
    }

    /**
     * Saves request approval with biometric data to database
     * @param record The purchase fund record
     * @param approver The approver employee
     * @param fingerprintTemplate Approver fingerprint template
     * @param notes Approval notes
     */
    private void saveRequestApprovalWithBiometric(AgreementBasedPurchaseFundRecordModel record, EmployeeSignatureModel approver, 
                                         byte[] fingerprintTemplate, String notes) {
        new Thread(() -> {
            try {
                boolean success = databaseConnector.saveAgreementBasedPurchaseFundRequestApprovalWithBiometric(
                    record, approver, fingerprintTemplate, notes
                );

                Platform.runLater(() -> {
                    if (success) {
                        System.out.println("Biometric approval record saved successfully");
                        agreementBasedPurchaseFundRequestTable.refresh();
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
    
    /**
     * Saves dispense approval with biometric data to database
     * @param record The purchase fund record
     * @param approver The approver employee
     * @param fingerprintTemplate Approver fingerprint template
     * @param notes Approval notes
     */
    private void saveDispenseApprovalWithBiometric(AgreementBasedPurchaseFundRecordModel record, EmployeeSignatureModel approver, 
                                         byte[] fingerprintTemplate, String notes) {
        new Thread(() -> {
            try {
                boolean success = databaseConnector.saveAgreementBasedPurchaseFundDispenseApprovalWithBiometric(
                    record, approver, fingerprintTemplate, notes
                );

                Platform.runLater(() -> {
                    if (success) {
                        System.out.println("Dispense approval biometric record saved successfully");
                        agreementBasedPurchaseFundRequestTable.refresh();
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

    /**
     * Saves confirmation with biometric data to database
     * @param record The purchase fund record
     * @param confirmer The confirmer employee
     * @param fingerprintTemplate Confirmer fingerprint template
     * @param notes Confirmation notes
     */
    private void saveConfirmationWithBiometric(AgreementBasedPurchaseFundRecordModel record, EmployeeSignatureModel confirmer, 
                                             byte[] fingerprintTemplate, String notes) {
        new Thread(() -> {
            try {
                boolean success = databaseConnector.saveAgreementBasedPurchaseFundConfirmationWithBiometric(
                    record, confirmer, fingerprintTemplate, notes
                );

                Platform.runLater(() -> {
                    if (success) {
                        System.out.println("Biometric confirmation record saved successfully");
                        agreementBasedPurchaseFundRequestTable.refresh();
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

    // ==================== BUTTON STATE MANAGEMENT ====================

    /**
     * Updates button states based on selected record status
     * @param selected The selected purchase fund record
     */
    private void updateButtonStates(AgreementBasedPurchaseFundRecordModel selected) {
        if (selected == null) {
            // No selection - disable all workflow buttons but keep CRUD buttons enabled
            approveRequestBtn.setDisable(true);
            confirmBtn.setDisable(true);
            dispenseBtn.setDisable(true);
            approveDispenseBtn.setDisable(true);
            viewDetailsBtn.setDisable(true); // Disable view details when no selection
            editBtn.setDisable(true);
            voidBtn.setDisable(true);
            deleteBtn.setDisable(true);
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
        editBtn.setDisable(isVoided || isDispensed);
        voidBtn.setDisable(isVoided || isDispensed);
        deleteBtn.setDisable(isDispensed);

        updateButtonTooltips(selected);
    }

    /**
     * Updates button tooltips based on selected record status
     * @param selected The selected purchase fund record
     */
    private void updateButtonTooltips(AgreementBasedPurchaseFundRecordModel selected) {
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

    // ==================== RECORD DETAILS VIEW ====================

    /**
     * Views detailed information for a specific record
     * @param record The purchase fund record to view
     */
    private void viewRecordDetails(AgreementBasedPurchaseFundRecordModel record) {
        if (record == null) {
            showAlert("Error", "No record selected to view details.", Alert.AlertType.ERROR);
            return;
        }

        try {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Agreement-Based Purchase Fund Request Details");
            dialog.setHeaderText(null);
            dialog.getDialogPane().setPrefSize(1000, 800);
            dialog.setResizable(true);

            // ========================
            // HEADER SECTION
            // ========================
            HBox headerBox = new HBox(20);
            headerBox.setPrefWidth(1000);
            headerBox.setAlignment(Pos.CENTER);
            headerBox.setSpacing(20);
            headerBox.setStyle("-fx-border-color: black; -fx-border-width: 0 0 2 0; -fx-padding: 10; -fx-background-color: white;");

            // ---------- Left: Logo ----------
            ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/icons/logo.jpg")));
            logoView.setFitWidth(250);
            logoView.setFitHeight(250);

            // ---------- Center: Hospital Info ----------
            Label amharicName = new Label("አፍራን ጠቅላላ ሆስፒታል");
            amharicName.setFont(AmharicFontLoader.getAmharicFont(18));
            amharicName.setStyle("-fx-font-weight: bold;");

            Label englishName = new Label("AFRAN GENERAL HOSPITAL");
            englishName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            Label address = new Label("Addis Ababa, Ethiopia");
            Label phone = new Label("Tel: +251113693384 / +251113693457");

            VBox hospitalInfo = new VBox(3, amharicName, englishName, address, phone);
            hospitalInfo.setAlignment(Pos.CENTER);
            HBox.setHgrow(hospitalInfo, Priority.ALWAYS);

            // ---------- Right: Report Title + Request Info ----------
            Label reportTitle = new Label("AGREEMENT BASED PURCHASE FUND REPORT");
            reportTitle.setStyle(
                    "-fx-font-size: 16px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-underline: true; " +
                    "-fx-text-fill: #000080;"
            );
            reportTitle.setWrapText(true);
            reportTitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            reportTitle.setMaxWidth(300);

            // Request date and ID labels
            Label dateLabel = new Label("Request Date: " + getSafeDate(record.getRequestDate()));
            Label requestIdLabel = new Label("ID: " + getSafeString(record.getRequestId()));
            dateLabel.setStyle("-fx-font-size: 13px;");
            requestIdLabel.setStyle("-fx-font-size: 13px;");

            VBox rightInfo = new VBox(5, reportTitle, dateLabel, requestIdLabel);
            rightInfo.setAlignment(Pos.CENTER_RIGHT);
            rightInfo.setPrefWidth(300);

            // ---------- Assemble Header ----------
            headerBox.getChildren().addAll(logoView, hospitalInfo, rightInfo);

            // ========================
            // MAIN CONTENT
            // ========================
            VBox mainContent = new VBox(20);
            mainContent.setPadding(new Insets(20));

            // --- BASIC INFO ---
            VBox basicInfoSection = createDetailSection("BASIC INFORMATION", "#2980b9");
            GridPane basicGrid = new GridPane();
            basicGrid.setHgap(20);
            basicGrid.setVgap(10);
            basicGrid.setPadding(new Insets(10));
            basicGrid.addRow(0,
                    createBoldLabel("Request ID:"), new Label(getSafeString(record.getRequestId())),
                    createBoldLabel("Employee Name:"), new Label(getSafeString(record.getPayee()))
            );
            basicGrid.addRow(1,
                    createBoldLabel("Main Category"), new Label(getSafeString(record.getMainCategory())),
                    createBoldLabel("Subcategory:"), new Label(getSafeString(record.getSubCategory()))
            );
            basicGrid.addRow(1, 
                    createBoldLabel("Department:"), new Label(getSafeString(record.getRequisitionUnit())),
                    createBoldLabel("Requested Amount:"), new Label(getSafeAmount(record.getAmountRequested()))
            );
            basicGrid.addRow(2, 
                    createBoldLabel("Request Date:"), new Label(getSafeDate(record.getRequestDate())),
                    createBoldLabel("Reason:"), new Label(getSafeString(record.getReason()))
            );
            basicInfoSection.getChildren().add(basicGrid);

            // --- STATUS INFO ---
            VBox statusSection = createDetailSection("STATUS INFORMATION", "#8e44ad");
            GridPane statusGrid = new GridPane();
            statusGrid.setHgap(20);
            statusGrid.setVgap(10);
            statusGrid.setPadding(new Insets(10));
            statusGrid.addRow(0, createBoldLabel("Approval Status:"), createStatusLabel(getSafeString(record.getApprovalStatus())),
                    createBoldLabel("Approved By:"), new Label(getSafeString(record.getApprovedBy())));
            statusGrid.addRow(1, createBoldLabel("Dispense Confirmation Status:"), createStatusLabel(getSafeString(record.getConfirmationStatus())),
                    createBoldLabel("Confirmed By:"), new Label(getSafeString(record.getConfirmedBy())));
            statusGrid.addRow(2, createBoldLabel("Dispensed Status:"), createStatusLabel(getSafeString(record.getDispensedStatus())),
                    createBoldLabel("Dispensed By:"), new Label(getSafeString(record.getDispensedBy())));
            statusGrid.addRow(3, createBoldLabel("Dispense Approval Status:"), createStatusLabel(getSafeString(record.getDispenseApprovalStatus())),
                    createBoldLabel("Dispense Approved By:"), new Label(getSafeString(record.getDispenseApprovedBy())));
            statusGrid.addRow(4, createBoldLabel("Void Status:"), createStatusLabel(getSafeString(record.getVoidStatus())),
                    createBoldLabel("Voided By:"), new Label(getSafeString(record.getVoidedBy())));
            statusGrid.addRow(5,
            createBoldLabel("Void Reason:"), createStatusLabel(getSafeString(databaseConnector.getAgreementBasedPurchaseFundRequestVoidReason(record.getRequestId()))));
            statusSection.getChildren().add(statusGrid);

            // --- AGREEMENT DETAILS ---
            VBox agreementSection = createDetailSection("AGREEMENT DETAILS / የስምምነት ዝርዝሮች", "#27ae60");
            GridPane agreementGrid = new GridPane();
            agreementGrid.setHgap(30);
            agreementGrid.setVgap(10);
            agreementGrid.setPadding(new Insets(10));
            agreementGrid.addRow(0, createBoldLabel("መግቢያ፡"), createWrappedLabel(getSafeString(record.getAgreementIntro())));
            agreementGrid.addRow(1, createBoldLabel("ዓላማ፡"), createWrappedLabel(getSafeString(record.getAgreementPurpose())));
            agreementGrid.addRow(2, createBoldLabel("ስምምነት፡"), createWrappedLabel(getSafeString(record.getAgreementConsent())));
            agreementGrid.addRow(3, createBoldLabel("ተሳታፊዎች፡"), createWrappedLabel(getSafeString(record.getAgreementParties())));
            agreementGrid.addRow(4, createBoldLabel("የሥራ ተፈጻሚነት፡"), createWrappedLabel(getSafeString(record.getAgreementNatureOfWork())));
            agreementGrid.addRow(5, createBoldLabel("የአሰሪ መብቶች፡"), createWrappedLabel(getSafeString(record.getAgreementEmployerRights())));
            agreementGrid.addRow(6, createBoldLabel("የሰራተኛ መብቶች፡"), createWrappedLabel(getSafeString(record.getAgreementEmployeeRights())));
            agreementGrid.addRow(7, createBoldLabel("የአሰሪ ግዴታዎች፡"), createWrappedLabel(getSafeString(record.getAgreementEmployerDuties())));
            agreementGrid.addRow(8, createBoldLabel("የሰራተኛ ግዴታዎች፡"), createWrappedLabel(getSafeString(record.getAgreementEmployeeDuties())));
            agreementSection.getChildren().add(agreementGrid);

            // --- SIGNATURES AT BOTTOM ---
            HBox signaturesBox = new HBox();
            signaturesBox.setPadding(new Insets(20, 10, 10, 10));
            signaturesBox.setAlignment(Pos.CENTER);
            
            VBox leftSignature = new VBox(5);
            leftSignature.setAlignment(Pos.CENTER_LEFT);

            Label payeeName = new Label("Payee: " + getSafeString(record.getPayee()));

            // signature image
            ImageView payeeSignature = new ImageView();
            Image payeeSigImage = databaseConnector.loadSignature(record.getPayee());
            if (payeeSigImage != null) payeeSignature.setImage(payeeSigImage);

            payeeSignature.setFitWidth(150);
            payeeSignature.setFitHeight(50);

            // Add Date
            Label dateLbl = new Label("Request Date: " + record.getRequestDate());
            leftSignature.getChildren().addAll(payeeName, dateLbl, payeeSignature);

            VBox rightSignature = new VBox(5);
            rightSignature.setAlignment(Pos.CENTER_RIGHT);
            Label approverName = new Label("Approved By: " + getSafeString(record.getApprovedBy()));
            ImageView approverSignature = new ImageView();
            Image approverSigImage = databaseConnector.loadSignature(record.getApprovedBy());
            if (approverSigImage != null) approverSignature.setImage(approverSigImage);
            approverSignature.setFitWidth(150);
            approverSignature.setFitHeight(50);
            
            // Add Date
            Label dLbl = new Label("Request Date: " + record.getRequestDate());
            rightSignature.getChildren().addAll(approverName, dLbl, approverSignature);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            signaturesBox.getChildren().addAll(leftSignature, spacer, rightSignature);

            // --- PAYEE ID CARDS ---
            VBox idCardSection = createDetailSection("PAYEE ID CARDS", "#f39c12");
            FlowPane idCardPane = new FlowPane();
            idCardPane.setHgap(20);
            idCardPane.setVgap(20);
            idCardPane.setPadding(new Insets(10));
            idCardPane.setAlignment(Pos.CENTER_LEFT);
            List<ImageView> idCardImages = databaseConnector.loadPayeeIdCardImages(record.getRequestId());
            if (idCardImages.isEmpty()) {
                idCardPane.getChildren().add(new Label("No payee ID cards uploaded for this request."));
            } else {
                idCardPane.getChildren().addAll(idCardImages);
            }
            idCardSection.getChildren().add(idCardPane);

            // --- BIOMETRIC SIGNATURES ---
            VBox biometricSection = createDetailSection("BIOMETRIC SIGNATURES", "#e74c3c");
            HBox biometricContainer = new HBox(20);
            biometricContainer.setPadding(new Insets(10));
            loadSignaturesForDetails(record, biometricContainer);
            if (biometricContainer.getChildren().isEmpty()) {
                biometricContainer.getChildren().add(new Label("No biometric signatures available for this request"));
            }
            biometricSection.getChildren().add(biometricContainer);

            mainContent.getChildren().addAll(
                    basicInfoSection,
                    statusSection,
                    agreementSection,
                    signaturesBox,
                    idCardSection,
                    biometricSection
            );

            // --- EXPORT BUTTON ---
            HBox topButtons = new HBox(15);
            topButtons.setPadding(new Insets(10));
            topButtons.setAlignment(Pos.CENTER_LEFT);
            Button exportButton = new Button("Export PDF");
            exportButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
            exportButton.setDisable(true);
            
            // Check signature availability asynchronously
            new Thread(() -> {
                boolean allSignaturesPresent = hasAllSignatures(record);
                Platform.runLater(() -> {
                    exportButton.setDisable(!allSignaturesPresent);
                    if (!allSignaturesPresent) {
                        exportButton.setTooltip(new Tooltip("Disabled: Missing one or more signatures"));
                    }
                });
            }).start();
            
            exportButton.setOnAction(e -> {
                if (!hasAllSignatures(record)) {
                    showAlert("Missing Signatures", "Cannot export: one or more required signatures are missing.", Alert.AlertType.WARNING);
                    return;
                }
                VBox contentWithHeader = new VBox(10);
                contentWithHeader.getChildren().addAll(headerBox, mainContent);
                exportNodeToPDF(contentWithHeader, "Agreement_Based_Purchase_Fund_" + record.getRequestId() + ".pdf");
            });
            topButtons.getChildren().add(exportButton);

            // --- FOOTER ---
            HBox footer = new HBox();
            footer.setAlignment(Pos.CENTER);
            footer.setPadding(new Insets(10));
            footer.setStyle("-fx-border-color: black; -fx-border-width: 2 0 0 0;");

            Label footerLabel = new Label("ይህ ሰነድ በአፍራን ጠቅላላ ሆስፒታል ሰው ኃብት ስርዓት በራሱ ተመን የተፈጠረ ነው።");
            footerLabel.setFont(AmharicFontLoader.getAmharicFont(13));
            Label systemCredit = new Label("Generated by AFRAN General Hospital HRMS © 2025");
            systemCredit.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

            footer.getChildren().addAll(new VBox(footerLabel, systemCredit));

            // ========================
            // SCROLLABLE CONTENT
            // ========================
            VBox contentWithButtons = new VBox(10);
            contentWithButtons.getChildren().addAll(headerBox, topButtons, mainContent, footer);

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

    // ==================== HELPER METHODS ====================

    /**
     * Creates a bold label with specified text
     * @param text The label text
     * @return Styled Label object
     */
    private Label createBoldLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        label.setFont(AmharicFontLoader.getAmharicFont(18f));
        return label;
    }

    /**
     * Creates a wrapped label with specified text
     * @param text The label text
     * @return Wrapped Label object
     */
    private Label createWrappedLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setFont(AmharicFontLoader.getAmharicFont(14f));
        label.setPrefWidth(800); // preferred wrapping width
        label.setMaxWidth(Double.MAX_VALUE); // allow full expansion
        label.setMinHeight(Region.USE_PREF_SIZE);
        GridPane.setHgrow(label, Priority.ALWAYS); // allow grid resizing
        return label;
    }

    /**
     * Gets safe string value (handles null/empty)
     * @param value The input string
     * @return Safe string value
     */
    private String getSafeString(String value) {
        return (value == null || value.trim().isEmpty()) ? "N/A" : value;
    }

    /**
     * Creates a status label with appropriate styling
     * @param status The status text
     * @return Styled Label object
     */
    private Label createStatusLabel(String status) {
        Label label = new Label(status);
        switch (status.toLowerCase()) {
            case "approved":
            case "confirmed":
            case "yes":
                label.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                break;
            case "pending":
                label.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                break;
            case "rejected":
            case "voided":
                label.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                break;
            default:
                label.setStyle("-fx-text-fill: #7f8c8d;");
        }
        return label;
    }

    /**
     * Creates a detail section with title and color
     * @param title The section title
     * @param color The section color
     * @return VBox containing the section
     */
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

    /**
     * Loads all signatures for detailed view
     * @param record The purchase fund record
     * @param signaturesContainer Container to add signature components
     */
    private void loadSignaturesForDetails(AgreementBasedPurchaseFundRecordModel record, HBox signaturesContainer) {
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

    /**
     * Gets signature title based on role
     * @param role The signature role
     * @return Formatted title string
     */
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

    /**
     * Checks if string is empty or null
     * @param str The input string
     * @return True if empty, false otherwise
     */
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Creates a signature box component
     * @param title The signature title
     * @param employeeName The employee name
     * @return VBox containing signature components
     */
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

    /**
     * Checks if all required signatures are present for a record
     * @param record The purchase fund record
     * @return True if all signatures are present, false otherwise
     */
    private boolean hasAllSignatures(AgreementBasedPurchaseFundRecordModel record) {
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

    // ==================== EXPORT AND PRINTING METHODS ====================

    /**
     * Exports a node to PDF format
     * @param node The JavaFX node to export
     * @param fileName The output file name
     */
    private void exportNodeToPDF(Node node, String fileName) {
        try {
            // ---------- STEP 1: Prepare container ----------
            VBox container = new VBox(node);
            container.setPadding(new Insets(30));
            container.setStyle("-fx-background-color: white;");
            container.setPrefWidth(800); // reasonable screen width

            container.applyCss();
            container.layout();

            // ---------- STEP 2: Snapshot at natural scale ----------
            SnapshotParameters params = new SnapshotParameters();
            WritableImage snapshot = container.snapshot(params, null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

            // ---------- STEP 3: Create PDF (A4 size in points) ----------
            final float A4_WIDTH = PDRectangle.A4.getWidth();
            final float A4_HEIGHT = PDRectangle.A4.getHeight();

            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // ---------- STEP 4: Scale image proportionally to fit A4 ----------
            float imageWidth = bufferedImage.getWidth();
            float imageHeight = bufferedImage.getHeight();

            // Calculate scale factor
            float scale = Math.min(A4_WIDTH / imageWidth, A4_HEIGHT / imageHeight);
            float scaledWidth = imageWidth * scale;
            float scaledHeight = imageHeight * scale;

            // Center image on page
            float x = (A4_WIDTH - scaledWidth) / 2;
            float y = (A4_HEIGHT - scaledHeight) / 2;

            PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdImage, x, y, scaledWidth, scaledHeight);
            }

            // ---------- STEP 5: Save under Documents/AgreementBased ----------
            File documentsDir = new File(System.getProperty("user.home"), "Documents/AgreementBased/");
            if (!documentsDir.exists()) {
                documentsDir.mkdirs();
            }

            File outputFile = new File(documentsDir, fileName);
            document.save(outputFile);
            document.close();

            showAlert("Export Successful",
                    "PDF saved to: " + outputFile.getAbsolutePath(),
                    Alert.AlertType.INFORMATION);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(outputFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Export Failed", "Failed to export PDF: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // ==================== TABLE AND FILTER METHODS ====================

    /**
     * Creates the table section with filtering capabilities
     * @return VBox containing table and filter components
     */
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

        // Date range filters - ADDED BASED ON REQUEST DATE
        DatePicker fromDatePicker = new DatePicker();
        fromDatePicker.setPromptText("From Date");
        fromDatePicker.setPrefWidth(120);

        DatePicker toDatePicker = new DatePicker();
        toDatePicker.setPromptText("To Date");
        toDatePicker.setPrefWidth(120);

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        searchBtn.setOnAction(e -> filterTable(searchField.getText(), statusFilter.getValue(), fromDatePicker.getValue(), toDatePicker.getValue()));

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            statusFilter.setValue("All");
            fromDatePicker.setValue(null);
            toDatePicker.setValue(null);
            filterTable("", "All", null, null);
        });
        
        Button refreshBtn = createStyledButton("Refresh", "#7f8c8d");
        refreshBtn.setOnAction(e -> refreshData());

        filterBox.getChildren().addAll(
            new Label("Search:"), searchField, 
            new Label("Status:"), statusFilter,
            new Label("From:"), fromDatePicker,
            new Label("To:"), toDatePicker,
            searchBtn, clearBtn, refreshBtn
        );

        agreementBasedPurchaseFundRequestTable = new TableView<>();
        agreementBasedPurchaseFundRequestTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        agreementBasedPurchaseFundRequestTable.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");

        // Proper row factory for double-click functionality
        agreementBasedPurchaseFundRequestTable.setRowFactory(tv -> {
    TableRow<AgreementBasedPurchaseFundRecordModel> row = new TableRow<AgreementBasedPurchaseFundRecordModel>() {
        @Override
        protected void updateItem(AgreementBasedPurchaseFundRecordModel item, boolean empty) {
            super.updateItem(item, empty);
            
            // Clear all styles first
            getStyleClass().removeAll("voided-row", "dispensed-row", "approved-row", "pending-row");
            
            if (empty || item == null) {
                setStyle("");
            } else {
                // Apply styles based on status - use !important to override cell styles
                if ("Yes".equalsIgnoreCase(item.getVoidStatus())) {
                    setStyle("-fx-background-color: red !important; -fx-text-fill: #8b0000 !important;");
                    getStyleClass().add("voided-row");
                } else if ("Yes".equalsIgnoreCase(item.getDispensedStatus())) {
                    setStyle("-fx-background-color: #d4edda !important; -fx-text-fill: #155724 !important;");
                    getStyleClass().add("dispensed-row");
                } else if ("Approved".equalsIgnoreCase(item.getDispenseApprovalStatus())) {
                    setStyle("-fx-background-color: #90EE90 !important; -fx-text-fill: #006400 !important;");
                    getStyleClass().add("approved-row");
                } else if ("Pending".equalsIgnoreCase(item.getDispenseApprovalStatus())) {
                    setStyle("-fx-background-color: #fff3cd !important; -fx-text-fill: #856404 !important;");
                    getStyleClass().add("pending-row");
                } else {
                    setStyle("");
                }
            }
        }
    };
    
    row.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && !row.isEmpty()) {
            AgreementBasedPurchaseFundRecordModel selectedRecord = row.getItem();
            if (selectedRecord != null) {
                viewRecordDetails(selectedRecord);
            }
        }
    });
    return row;
});

        // Create table columns
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> requestIdCol = createStyledColumn("Request ID", "requestId", "#2c3e50");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> requisitionUnitCol = createStyledColumn("Requisition Unit", "requisitionUnit", "#34495e");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> reasonCol = createStyledColumn("Reason", "reason", "#16a085");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> payeeCol = createStyledColumn("Payee", "payee", "#27ae60");
        TableColumn<AgreementBasedPurchaseFundRecordModel, Double> amountRequestedCol = createStyledColumn("Amount Requested", "amountRequested", "#2980b9");
        TableColumn<AgreementBasedPurchaseFundRecordModel, LocalDate> requestDateCol = createStyledColumn("Request Date", "requestDate", "#8e44ad");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> approvalStatusCol = createStyledColumn("Approval Status", "approvalStatus", "#c0392b");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> approvedByCol = createStyledColumn("Approved By", "approvedBy", "#7f8c8d");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> confirmationStatusCol = createStyledColumn("Confirmation Status", "confirmationStatus", "#f39c12");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> confirmedByCol = createStyledColumn("Confirmed By", "confirmedBy", "#d35400");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> dispensedStatusCol = createStyledColumn("Dispensed Status", "dispensedStatus", "#16a085");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> dispensedByCol = createStyledColumn("Dispensed By", "dispensedBy", "#27ae60");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> dispenseApprovalStatusCol = createStyledColumn("Dispense Approval Status", "dispenseApprovalStatus", "#d35400");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> dispenseApprovedByCol = createStyledColumn("Dispense Approved By", "dispenseApprovedBy", "#8e44ad");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> voidStatusCol = createStyledColumn("Void Status", "voidStatus", "#e74c3c");
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> voidedByCol = createStyledColumn("Voided By", "voidedBy", "#c0392b");

        agreementBasedPurchaseFundRequestTable.getColumns().addAll(
            requestIdCol, requisitionUnitCol, reasonCol, payeeCol,
            amountRequestedCol, requestDateCol, approvalStatusCol,
            approvedByCol, confirmationStatusCol, confirmedByCol,
            dispensedStatusCol, dispensedByCol, dispenseApprovalStatusCol,
            dispenseApprovedByCol, voidStatusCol, voidedByCol
        );

        agreementBasedPurchaseFundRequestTable.setItems(agreementBasedPurchaseFundRequestData);
        
        Label placeholder = new Label("No Agreement Based Purchase Fund requests found. Click 'New Request' to create one.");
        placeholder.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14; -fx-padding: 20;");
        agreementBasedPurchaseFundRequestTable.setPlaceholder(placeholder);

        // Proper selection listener
        agreementBasedPurchaseFundRequestTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                updateButtonStates(newSel);
                // Enable view details button when a row is selected
                viewDetailsBtn.setDisable(false);
            } else {
                // Disable buttons when no selection
                updateButtonStates(null);
            }
        });

        tableSection.getChildren().addAll(filterBox, agreementBasedPurchaseFundRequestTable);
        return tableSection;
    }

    /**
     * Creates a styled table column
     * @param title Column title
     * @param property Property name
     * @param color Column color
     * @return Styled TableColumn object
     */
   private <T> TableColumn<AgreementBasedPurchaseFundRecordModel, T> createStyledColumn(String title, String property, String color) {
    TableColumn<AgreementBasedPurchaseFundRecordModel, T> column = new TableColumn<>(title);
    column.setCellValueFactory(new PropertyValueFactory<>(property));
    column.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
    
    column.setCellFactory(tc -> new TableCell<AgreementBasedPurchaseFundRecordModel, T>() {
        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setStyle(""); // Clear styling for empty cells
            } else {
                setText(item.toString());
                
                // Only apply cell-specific styling for status columns, otherwise inherit row style
                if (property.contains("Status")) {
                    String status = item.toString().toLowerCase();
                    if (status.contains("approved") || status.contains("confirmed") || status.equals("yes")) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-border-color: #bdc3c7;");
                    } else if (status.contains("pending")) {
                        setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-border-color: #bdc3c7;");
                    } else if (status.contains("rejected") || status.contains("voided")) {
                        setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-border-color: #bdc3c7;");
                    } else {
                        setStyle(""); // Inherit row style for other status values
                    }
                } else {
                    // For non-status columns, don't set background - inherit from row
                    setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 0 0 1 0;");
                }
            }
        }
    });
    
    return column;
}

    /**
     * Filters the table based on search criteria and date range
     * @param text Search text
     * @param status Status filter value
     * @param fromDate From date for filtering
     * @param toDate To date for filtering
     */
    private void filterTable(String text, String status, LocalDate fromDate, LocalDate toDate) {
        agreementBasedPurchaseFundRequestTable.setItems(agreementBasedPurchaseFundRequestData.filtered(record -> {
            boolean matchesSearch = text == null || text.isEmpty() || 
                record.getPayee().toLowerCase().contains(text.toLowerCase()) ||
                record.getRequestId().toLowerCase().contains(text.toLowerCase()) ||
                record.getRequisitionUnit().toLowerCase().contains(text.toLowerCase()) ||
                record.getReason().toLowerCase().contains(text.toLowerCase());
            
            boolean matchesStatus = status.equals("All") || 
                record.getApprovalStatus().equalsIgnoreCase(status) ||
                record.getConfirmationStatus().equalsIgnoreCase(status) ||
                record.getDispensedStatus().equalsIgnoreCase(status) ||
                record.getDispenseApprovalStatus().equalsIgnoreCase(status) ||
                record.getVoidStatus().equalsIgnoreCase(status);
            
            // Date range filtering based on request date - ADDED FUNCTIONALITY
            boolean matchesDate = true;
            if (fromDate != null) {
                matchesDate = !record.getRequestDate().isBefore(fromDate);
            }
            if (toDate != null && matchesDate) {
                matchesDate = !record.getRequestDate().isAfter(toDate);
            }
                
            return matchesSearch && matchesStatus && matchesDate;
        }));
    }

    /**
     * Refreshes data from database
     */
    private void refreshData() {
        loadPurchaseFundRequestsFromDatabase();
        loadEmployeeSignaturesFromDatabase();
        showAlert("Refreshed", "Data refreshed successfully!\n" + 
                  "Loaded " + agreementBasedPurchaseFundRequestData.size() + " Agreement Based Purchase Fund requests and " + 
                  employeeSignatureData.size() + " employee signatures.", Alert.AlertType.INFORMATION);
    }

    // ==================== ADVANCED SEARCH ====================

    /**
     * Shows advanced search dialog with multiple criteria
     */
    private void showAdvancedSearchDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Advanced Search");
        dialog.setHeaderText("Search Agreement Based Purchase Fund Requests with Multiple Criteria");
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
        
        // Date range fields for advanced search - BASED ON REQUEST DATE
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

    /**
     * Performs advanced search with multiple criteria
     * @param requestId Request ID to search for
     * @param payee Payee name to search for
     * @param requisitionUnit Requisition unit to search for
     * @param approvalStatus Approval status filter
     * @param confirmationStatus Confirmation status filter
     * @param dispensedStatus Dispensed status filter
     * @param fromDate From date for filtering
     * @param toDate To date for filtering
     */
    private void performAdvancedSearch(String requestId, String payee, String requisitionUnit, 
                                     String approvalStatus, String confirmationStatus, String dispensedStatus,
                                     LocalDate fromDate, LocalDate toDate) {
        agreementBasedPurchaseFundRequestTable.setItems(agreementBasedPurchaseFundRequestData.filtered(record -> {
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
            
            // Date range filtering based on request date - ADDED FUNCTIONALITY
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
            "Found " + agreementBasedPurchaseFundRequestTable.getItems().size() + " matching records.",
            Alert.AlertType.INFORMATION);
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Creates a biometric section component
     * @param title Section title
     * @param instruction Instruction text
     * @param color Section color
     * @return VBox containing biometric section
     */
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

    /**
     * Loads and displays employee signature
     * @param employee The employee model
     * @param signatureView ImageView to display signature
     * @param statusLabel Label to update status
     */
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

    /**
     * Creates request details section
     * @param record The purchase fund record
     * @return VBox containing request details
     */
    private VBox createRequestDetailsSection(AgreementBasedPurchaseFundRecordModel record) {
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

    /**
     * Converts byte array to JavaFX Image
     * @param imageData The image byte array
     * @return JavaFX Image object
     */
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

    /**
     * Creates a signature placeholder image
     * @return Placeholder Image object
     */
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

    /**
     * Creates a fingerprint placeholder image
     * @return Placeholder Image object
     */
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

    /**
     * Creates a simulated fingerprint image
     * @return Simulated fingerprint Image object
     */
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

    /**
     * Gets safe date string
     * @param date The date object
     * @return Formatted date string
     */
    private String getSafeDate(Object date) {
        return (date == null) ? "" : date.toString();
    }

    /**
     * Gets safe amount string
     * @param amount The amount value
     * @return Formatted amount string
     */
    private String getSafeAmount(Double amount) {
        return (amount == null) ? "" : String.format("ETB %.2f", amount);
    }

    // ==================== EXPORT AND PRINT OPTIONS ====================

    /**
     * Shows print options dialog
     */
    private void showPrintOptions() {
        // Implementation details for print options
        showAlert("Print Options", "Print functionality will be implemented in the next version.", Alert.AlertType.INFORMATION);
    }

    /**
     * Shows Excel export options dialog
     */
    private void showExportExcelOptions() {
        // Implementation details for Excel export options
        showAlert("Excel Export", "Excel export functionality will be implemented in the next version.", Alert.AlertType.INFORMATION);
    }

    /**
     * Shows Word export options dialog
     */
    private void showExportWordOptions() {
        // Implementation details for Word export options
        showAlert("Word Export", "Word export functionality will be implemented in the next version.", Alert.AlertType.INFORMATION);
    }

    /**
     * Exports to PDF (placeholder implementation)
     */
    private void exportToPdf() {
        showAlert("PDF Export", "PDF export functionality will be implemented in the next version.", Alert.AlertType.INFORMATION);
    }

    /**
     * Shows alert dialog
     * @param title Alert title
     * @param message Alert message
     * @param type Alert type
     */
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

    /**
     * Functional interface for biometric verification callback
     */
    @FunctionalInterface
    private interface BiometricVerificationCallback {
        void onVerificationComplete(EmployeeSignatureModel employee);
    }

    /**
     * Cleanup method to release resources
     */
    public void cleanup() {
        if (fingerprintModule != null) {
            fingerprintModule.closeDevice();
        }
        if (databaseConnector != null) {
            databaseConnector.close();
        }
    }
}