package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.chart.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.binding.Bindings;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import java.util.function.Function;
import javafx.print.PrinterJob;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import java.awt.Desktop;

public class RecieptBasedPurchaseFundReportDashboard extends BorderPane {

    // ==================== CONSTANTS AND CONFIGURATION ====================
    private static final String CURRENCY_SYMBOL = "ETB";
    private static final int DEBOUNCE_DELAY_MS = 300;
    private static final int MAX_EXPORT_RECORDS = 10000;
    
    // Color scheme
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String SUCCESS_COLOR = "#27ae60";
    private static final String WARNING_COLOR = "#f39c12";
    private static final String DANGER_COLOR = "#e74c3c";
    private static final String SECONDARY_COLOR = "#95a5a6";

    // Filters
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private TextField unitField;
    private TextField payeeField;
    private ComboBox<String> mainCategoryComboBox;
    private ComboBox<String> subCategoryComboBox;
    private TextField searchField;

    private ComboBox<String> approvalFilter;
    private ComboBox<String> confirmationFilter;
    private ComboBox<String> dispensedFilter;
    private ComboBox<String> dispenseApprovalFilter;
    private ComboBox<String> voidFilter;
    private ComboBox<String> receiptUploadFilter;

    // Table & data
    private TableView<PurchaseFundRecordModel> table;
    private ObservableList<PurchaseFundRecordModel> allRecords = FXCollections.observableArrayList();

    // Charts
    private PieChart statusPieChart;
    private BarChart<String, Number> monthlyBarChart;
    private LineChart<String, Number> trendLineChart;
    private PieChart categoryPieChart;
    private BarChart<String, Number> categoryBarChart;

    // Summary Labels
    private Label totalRequestsLbl;
    private Label totalApprovedLbl;
    private Label totalDispensedLbl;
    private Label totalReceiptUploadedLbl;
    private Label totalAmountLbl;
    private Label avgAmountLbl;
    private Label highestAmountLbl;
    private Label pendingApprovalLbl;
    private Label completionRateLbl;
    private Label topCategoryLbl;

    // Control Buttons
    private Button exportExcelBtn, exportPdfBtn, exportWordBtn, printBtn, refreshBtn;
    private ToggleButton darkModeToggle;

    // Tab Panes
    private TabPane mainTabPane;
    private Tab tableTab, pieChartTab, barChartTab, lineChartTab, categoryTab, analyticsTab;
    private String currentUser;

    // Performance optimization
    private javafx.animation.PauseTransition filterTimer;

    // Category data from database
    private ObservableList<String> mainCategories = FXCollections.observableArrayList();
    private ObservableList<String> subCategories = FXCollections.observableArrayList();
    private Map<String, ObservableList<String>> categorySubcategoryMap = new HashMap<>();

    // Constructor
    public RecieptBasedPurchaseFundReportDashboard(List<PurchaseFundRecordModel> records, String username) {
        if (records != null) this.allRecords.addAll(records);
        this.currentUser = username;
        loadCategoryData();
        initializeFilterTimer();
        initializeUI();
        setupEventHandlers();
        applyFilters();
    }

    // Load category and subcategory data from database
    private void loadCategoryData() {
        try {
            Connecting db = new Connecting();
            
            // Load main categories
            List<String> mainCats = db.getMainCategories();
            if (mainCats != null && !mainCats.isEmpty()) {
                mainCategories.addAll(mainCats);
            } else {
                // Add default categories if database is empty
                mainCategories.addAll("Medical Supplies", "Office Supplies", "Equipment", "Medications", "Laboratory", "Radiology");
            }
            
            // Load subcategories and build mapping
            for (String mainCat : mainCategories) {
                List<String> subCats = db.getSubCategories(mainCat);
                ObservableList<String> subCatList = FXCollections.observableArrayList();
                if (subCats != null && !subCats.isEmpty()) {
                    subCatList.addAll(subCats);
                } else {
                    // Add default subcategories based on main category
                    switch (mainCat) {
                        case "Medical Supplies":
                            subCatList.addAll("Surgical Instruments", "Disposables", "Protective Equipment");
                            break;
                        case "Office Supplies":
                            subCatList.addAll("Stationery", "Furniture", "IT Equipment");
                            break;
                        case "Equipment":
                            subCatList.addAll("Medical Devices", "Maintenance Tools", "Monitoring Equipment");
                            break;
                        case "Medications":
                            subCatList.addAll("Antibiotics", "Analgesics", "Emergency Drugs");
                            break;
                        case "Laboratory":
                            subCatList.addAll("Reagents", "Test Kits", "Lab Equipment");
                            break;
                        case "Radiology":
                            subCatList.addAll("X-ray Supplies", "Contrast Media", "Film");
                            break;
                        default:
                            subCatList.addAll("General", "Miscellaneous");
                    }
                }
                categorySubcategoryMap.put(mainCat, subCatList);
                subCategories.addAll(subCatList);
            }
            
        } catch (Exception e) {
            System.err.println("Error loading category data: " + e.getMessage());
            // Load default categories in case of error
            loadDefaultCategories();
        }
    }

    private void loadDefaultCategories() {
        mainCategories.addAll("Medical Supplies", "Office Supplies", "Equipment", "Medications", "Laboratory", "Radiology");
        
        // Medical Supplies subcategories
        ObservableList<String> medicalSubs = FXCollections.observableArrayList("Surgical Instruments", "Disposables", "Protective Equipment", "Wound Care");
        categorySubcategoryMap.put("Medical Supplies", medicalSubs);
        
        // Office Supplies subcategories
        ObservableList<String> officeSubs = FXCollections.observableArrayList("Stationery", "Furniture", "IT Equipment", "Printing Supplies");
        categorySubcategoryMap.put("Office Supplies", officeSubs);
        
        // Equipment subcategories
        ObservableList<String> equipmentSubs = FXCollections.observableArrayList("Medical Devices", "Maintenance Tools", "Monitoring Equipment", "Diagnostic Tools");
        categorySubcategoryMap.put("Equipment", equipmentSubs);
        
        // Medications subcategories
        ObservableList<String> medsSubs = FXCollections.observableArrayList("Antibiotics", "Analgesics", "Emergency Drugs", "Chronic Medications");
        categorySubcategoryMap.put("Medications", medsSubs);
        
        // Laboratory subcategories
        ObservableList<String> labSubs = FXCollections.observableArrayList("Reagents", "Test Kits", "Lab Equipment", "Consumables");
        categorySubcategoryMap.put("Laboratory", labSubs);
        
        // Radiology subcategories
        ObservableList<String> radiologySubs = FXCollections.observableArrayList("X-ray Supplies", "Contrast Media", "Film", "Ultrasound Supplies");
        categorySubcategoryMap.put("Radiology", radiologySubs);
        
        subCategories.addAll(medicalSubs);
        subCategories.addAll(officeSubs);
        subCategories.addAll(equipmentSubs);
        subCategories.addAll(medsSubs);
        subCategories.addAll(labSubs);
        subCategories.addAll(radiologySubs);
    }

    private void initializeFilterTimer() {
        filterTimer = new javafx.animation.PauseTransition(javafx.util.Duration.millis(DEBOUNCE_DELAY_MS));
        filterTimer.setOnFinished(e -> applyFiltersImmediate());
    }

    // ==================== UI INITIALIZATION ====================
    private void initializeUI() {
        setPadding(new Insets(15));
        setStyle("-fx-background-color: #f8f9fa;");

        // Header Section
        VBox headerSection = createHeaderSection();
        
        // Filter Section
        VBox filterSection = createFilterSection();
        
        // Main Content Area
        mainTabPane = createMainTabPane();
        
        // Summary Section
        HBox summarySection = createSummarySection();

        // Layout Assembly
        VBox topContainer = new VBox(10, headerSection, filterSection);
        setTop(topContainer);
        setCenter(mainTabPane);
        setBottom(summarySection);
    }

    private VBox createHeaderSection() {
        Label title = new Label("Receipt-Based Purchase Fund Analytics Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Comprehensive Financial Analysis and Reporting");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitle.setStyle("-fx-text-fill: #7f8c8d;");

        // Control Buttons
        HBox controlBox = createControlButtons();

        VBox header = new VBox(8, title, subtitle, controlBox);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 10;");
        
        return header;
    }

    private HBox createControlButtons() {
        exportExcelBtn = createStyledButton("Export Excel", SUCCESS_COLOR);
        exportPdfBtn = createStyledButton("Export PDF", DANGER_COLOR);
        exportWordBtn = createStyledButton("Export Word", PRIMARY_COLOR);
        printBtn = createStyledButton("Print Report", "#9b59b6");
        refreshBtn = createStyledButton("Refresh Data", WARNING_COLOR);
        
        // Dark Mode Toggle
        darkModeToggle = new ToggleButton("Dark Mode");
        darkModeToggle.setStyle("-fx-background-color: #34495e; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, exportExcelBtn, exportPdfBtn, exportWordBtn, printBtn, refreshBtn, darkModeToggle);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        return buttonBox;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;",
            color
        ));
        button.setOnMouseEntered(e -> button.setStyle(String.format(
            "-fx-background-color: derive(%s, -20%%); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;",
            color
        )));
        button.setOnMouseExited(e -> button.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;",
            color
        )));
        return button;
    }

    private VBox createFilterSection() {
        fromDatePicker = new DatePicker();
        fromDatePicker.setPromptText("From Date");
        toDatePicker = new DatePicker();
        toDatePicker.setPromptText("To Date");

        unitField = new TextField();
        unitField.setPromptText("Requisition Unit");

        payeeField = new TextField();
        payeeField.setPromptText("Payee");

        // Main Category ComboBox
        mainCategoryComboBox = new ComboBox<>();
        mainCategoryComboBox.setItems(mainCategories);
        mainCategoryComboBox.setPromptText("Select Main Category");
        mainCategoryComboBox.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7;");
        mainCategoryComboBox.setPrefWidth(180);

        // Sub Category ComboBox
        subCategoryComboBox = new ComboBox<>();
        subCategoryComboBox.setPromptText("Select Sub Category");
        subCategoryComboBox.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7;");
        subCategoryComboBox.setPrefWidth(180);
        subCategoryComboBox.setDisable(true); // Initially disabled

        searchField = new TextField();
        searchField.setPromptText("Search across all fields...");

        approvalFilter = createComboBox("All", "Pending", "Approved");
        confirmationFilter = createComboBox("All", "Pending", "Confirmed");
        dispensedFilter = createComboBox("All", "Yes", "No");
        dispenseApprovalFilter = createComboBox("All", "Pending", "Approved");
        voidFilter = createComboBox("All", "Yes", "No");
        receiptUploadFilter = createComboBox("All", "Pending", "Uploaded");

        Button resetBtn = createStyledButton("Reset Filters", SECONDARY_COLOR);
        Button advancedFilterBtn = createStyledButton("Advanced Filters", "#16a085");

        resetBtn.setOnAction(e -> resetFilters());
        advancedFilterBtn.setOnAction(e -> showAdvancedFilters());

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setStyle("-fx-background-color: white; -fx-border-radius: 10;");

        // Row 0
        grid.add(new Label("From Date:"), 0, 0);
        grid.add(fromDatePicker, 1, 0);
        grid.add(new Label("To Date:"), 2, 0);
        grid.add(toDatePicker, 3, 0);
        grid.add(new Label("Quick Search:"), 4, 0);
        grid.add(searchField, 5, 0);

        // Row 1
        grid.add(new Label("Unit:"), 0, 1);
        grid.add(unitField, 1, 1);
        grid.add(new Label("Payee:"), 2, 1);
        grid.add(payeeField, 3, 1);
        grid.add(new Label("Main Category:"), 4, 1);
        grid.add(mainCategoryComboBox, 5, 1);

        // Row 2
        grid.add(new Label("Sub Category:"), 0, 2);
        grid.add(subCategoryComboBox, 1, 2);
        grid.add(new Label("Approval:"), 2, 2);
        grid.add(approvalFilter, 3, 2);
        grid.add(new Label("Confirmed:"), 4, 2);
        grid.add(confirmationFilter, 5, 2);

        // Row 3
        grid.add(new Label("Dispensed:"), 0, 3);
        grid.add(dispensedFilter, 1, 3);
        grid.add(new Label("Dispense Approval:"), 2, 3);
        grid.add(dispenseApprovalFilter, 3, 3);
        grid.add(new Label("Receipt Upload:"), 4, 3);
        grid.add(receiptUploadFilter, 5, 3);

        // Row 4
        grid.add(new Label("Void:"), 0, 4);
        grid.add(voidFilter, 1, 4);
        grid.add(resetBtn, 4, 4);
        grid.add(advancedFilterBtn, 5, 4);

        VBox filterSection = new VBox(grid);
        filterSection.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 10;");
        
        return filterSection;
    }

    private ComboBox<String> createComboBox(String... items) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(items);
        cb.setValue("All");
        cb.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7;");
        cb.setPrefWidth(120);
        return cb;
    }

    private TabPane createMainTabPane() {
        TabPane tabPane = new TabPane();
        
        // Table Tab
        tableTab = new Tab("Data Table", createTableSection());
        tableTab.setClosable(false);
        
        // Charts Tabs
        pieChartTab = new Tab("Status Distribution", createPieChart());
        barChartTab = new Tab("Monthly Analysis", createBarChart());
        lineChartTab = new Tab("Trend Analysis", createLineChart());
        categoryTab = new Tab("Category Analysis", createCategoryChart());
        analyticsTab = new Tab("Advanced Analytics", createAnalyticsSection());
        
        tabPane.getTabs().addAll(tableTab, pieChartTab, barChartTab, lineChartTab, categoryTab, analyticsTab);
        tabPane.setStyle("-fx-background-color: white;");
        
        return tabPane;
    }

    private VBox createTableSection() {
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        // Define columns with better formatting
        TableColumn<PurchaseFundRecordModel, String> idCol = new TableColumn<>("Request ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRequestId()));

        TableColumn<PurchaseFundRecordModel, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRequisitionUnit()));

        TableColumn<PurchaseFundRecordModel, String> mainCatCol = new TableColumn<>("Main Category");
        mainCatCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getMainCategory() != null ? data.getValue().getMainCategory() : ""
        ));

        TableColumn<PurchaseFundRecordModel, String> subCatCol = new TableColumn<>("Sub Category");
        subCatCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getSubCategory() != null ? data.getValue().getSubCategory() : ""
        ));

        TableColumn<PurchaseFundRecordModel, String> payeeCol = new TableColumn<>("Payee");
        payeeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPayee()));

        TableColumn<PurchaseFundRecordModel, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> new SimpleStringProperty(
            String.format("%s %,.2f", CURRENCY_SYMBOL, data.getValue().getAmountRequested())
        ));

        TableColumn<PurchaseFundRecordModel, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getRequestDate().toString()
        ));

        TableColumn<PurchaseFundRecordModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApprovalStatus()));
        statusCol.setCellFactory(column -> new TableCell<PurchaseFundRecordModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item.toLowerCase()) {
                        case "approved":
                            setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                            break;
                        case "pending":
                            setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-font-weight: bold;");
                            break;
                        case "confirmed":
                            setStyle("-fx-background-color: #d1ecf1; -fx-text-fill: #0c5460; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-font-weight: bold;");
                    }
                }
            }
        });

        TableColumn<PurchaseFundRecordModel, String> receiptCol = new TableColumn<>("Receipt Upload");
        receiptCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRecieptUploadStatus()));
        receiptCol.setCellFactory(column -> new TableCell<PurchaseFundRecordModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("uploaded".equalsIgnoreCase(item)) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                    }
                }
            }
        });

        table.getColumns().addAll(idCol, unitCol, mainCatCol, subCatCol, payeeCol, amountCol, dateCol, statusCol, receiptCol);

        // Context Menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem exportItem = new MenuItem("Export Selected");
        MenuItem copyItem = new MenuItem("Copy to Clipboard");
        MenuItem detailsItem = new MenuItem("View Details");
        
        exportItem.setOnAction(e -> exportSelectedToExcel());
        copyItem.setOnAction(e -> copySelectedToClipboard());
        detailsItem.setOnAction(e -> showRecordDetails());
        
        contextMenu.getItems().addAll(exportItem, copyItem, detailsItem);
        table.setContextMenu(contextMenu);

        VBox tableSection = new VBox(10, createTableToolbar(), table);
        tableSection.setPadding(new Insets(10));
        
        return tableSection;
    }

    private HBox createTableToolbar() {
        Label countLabel = new Label();
        
        countLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            ObservableList<PurchaseFundRecordModel> items = table.getItems();
            int size = items != null ? items.size() : 0;
            return "Showing " + size + " record" + (size == 1 ? "" : "s");
        }, table.itemsProperty()));

        Button copyBtn = createStyledButton("Copy Selected", PRIMARY_COLOR);
        Button exportSelectionBtn = createStyledButton("Export Selection", SUCCESS_COLOR);
        
        copyBtn.setOnAction(e -> copySelectedToClipboard());
        exportSelectionBtn.setOnAction(e -> exportSelectedToExcel());

        HBox toolbar = new HBox(10, countLabel, copyBtn, exportSelectionBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(5, 10, 5, 10));
        toolbar.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 5;");
        
        return toolbar;
    }

    private PieChart createPieChart() {
        statusPieChart = new PieChart();
        statusPieChart.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");
        statusPieChart.setTitle("Request Status Distribution");
        return statusPieChart;
    }

    private BarChart<String, Number> createBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        monthlyBarChart = new BarChart<>(xAxis, yAxis);
        monthlyBarChart.setTitle("Monthly Fund Requests Analysis");
        monthlyBarChart.setStyle("-fx-background-color: white;");
        return monthlyBarChart;
    }

    private LineChart<String, Number> createLineChart() {
        CategoryAxis xAxisLine = new CategoryAxis();
        NumberAxis yAxisLine = new NumberAxis();
        trendLineChart = new LineChart<>(xAxisLine, yAxisLine);
        trendLineChart.setTitle("Funding Trends Over Time");
        trendLineChart.setStyle("-fx-background-color: white;");
        return trendLineChart;
    }

    private VBox createCategoryChart() {
        VBox categoryBox = new VBox(10);
        
        categoryPieChart = new PieChart();
        categoryPieChart.setTitle("Category Distribution");
        categoryPieChart.setStyle("-fx-background-color: white;");
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        categoryBarChart = new BarChart<>(xAxis, yAxis);
        categoryBarChart.setTitle("Spending by Category");
        categoryBarChart.setStyle("-fx-background-color: white;");
        
        categoryBox.getChildren().addAll(categoryPieChart, categoryBarChart);
        return categoryBox;
    }

    private VBox createAnalyticsSection() {
        GridPane analyticsGrid = new GridPane();
        analyticsGrid.setHgap(20);
        analyticsGrid.setVgap(15);
        analyticsGrid.setPadding(new Insets(20));

        // Key Metrics
        Label metricsTitle = new Label("Key Performance Indicators");
        metricsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        metricsTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Add various analytics components
        analyticsGrid.add(createMetricCard("Approval Rate", "78%", SUCCESS_COLOR), 0, 0);
        analyticsGrid.add(createMetricCard("Receipt Upload Rate", "65%", PRIMARY_COLOR), 1, 0);
        analyticsGrid.add(createMetricCard("Avg Processing Time", "2.8 days", WARNING_COLOR), 2, 0);
        analyticsGrid.add(createMetricCard("Pending Actions", "15", DANGER_COLOR), 3, 0);

        // Category analysis
        VBox categoryAnalysis = createCategoryAnalysisSection();

        VBox analyticsSection = new VBox(15, metricsTitle, analyticsGrid, categoryAnalysis);
        analyticsSection.setPadding(new Insets(15));
        analyticsSection.setStyle("-fx-background-color: white;");
        
        return analyticsSection;
    }

    private VBox createCategoryAnalysisSection() {
        VBox categoryBox = new VBox(10);
        
        Label categoryTitle = new Label("Category Analysis");
        categoryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        TextArea categoryInsights = new TextArea();
        categoryInsights.setWrapText(true);
        categoryInsights.setEditable(false);
        categoryInsights.setPrefHeight(150);
        categoryInsights.setText("Category insights will be displayed here...");
        
        categoryBox.getChildren().addAll(categoryTitle, categoryInsights);
        return categoryBox;
    }

    private VBox createMetricCard(String title, String value, String color) {
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setStyle("-fx-text-fill: " + color + ";");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        titleLabel.setStyle("-fx-text-fill: #7f8c8d;");

        VBox card = new VBox(5, valueLabel, titleLabel);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-radius: 8;");
        card.setPrefSize(150, 80);
        
        return card;
    }

    private HBox createSummarySection() {
        totalRequestsLbl = createSummaryLabel("Total Requests\n0", PRIMARY_COLOR);
        totalApprovedLbl = createSummaryLabel("Total Approved\n0", SUCCESS_COLOR);
        totalDispensedLbl = createSummaryLabel("Total Dispensed\n0", WARNING_COLOR);
        totalReceiptUploadedLbl = createSummaryLabel("Receipts Uploaded\n0", "#9b59b6");
        totalAmountLbl = createSummaryLabel("Total Amount\n" + CURRENCY_SYMBOL + " 0.00", "#1abc9c");
        avgAmountLbl = createSummaryLabel("Average Amount\n" + CURRENCY_SYMBOL + " 0.00", "#34495e");
        highestAmountLbl = createSummaryLabel("Highest Amount\n" + CURRENCY_SYMBOL + " 0.00", DANGER_COLOR);
        pendingApprovalLbl = createSummaryLabel("Pending Approval\n0", WARNING_COLOR);
        completionRateLbl = createSummaryLabel("Completion Rate\n0%", "#16a085");
        topCategoryLbl = createSummaryLabel("Top Category\n-", "#8e44ad");

        HBox summaryBox = new HBox(10, totalRequestsLbl, totalApprovedLbl, totalDispensedLbl, 
                                 totalReceiptUploadedLbl, totalAmountLbl, avgAmountLbl, 
                                 highestAmountLbl, pendingApprovalLbl, completionRateLbl, topCategoryLbl);
        summaryBox.setPadding(new Insets(15));
        summaryBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 10;");
        summaryBox.setAlignment(Pos.CENTER);
        
        return summaryBox;
    }

    private Label createSummaryLabel(String text, String color) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        label.setStyle(String.format(
            "-fx-text-fill: %s; -fx-background-color: %s20; -fx-padding: 10; -fx-border-radius: 8; -fx-background-radius: 8; -fx-alignment: center;",
            color, color
        ));
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        label.setWrapText(true);
        return label;
    }

    // ==================== EVENT HANDLERS AND FILTER LOGIC ====================
    private void setupEventHandlers() {
        // Filter listeners with debouncing
        fromDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> debouncedApplyFilters());
        toDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> debouncedApplyFilters());
        unitField.textProperty().addListener((obs, oldVal, newVal) -> debouncedApplyFilters());
        payeeField.textProperty().addListener((obs, oldVal, newVal) -> debouncedApplyFilters());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> debouncedApplyFilters());
        approvalFilter.valueProperty().addListener((obs, oldVal, newVal) -> debouncedApplyFilters());
        confirmationFilter.valueProperty().addListener((obs, oldVal, newVal) -> debouncedApplyFilters());
        dispensedFilter.valueProperty().addListener((obs, oldVal, newVal) -> debouncedApplyFilters());
        dispenseApprovalFilter.valueProperty().addListener((obs, oldVal, newVal) -> debouncedApplyFilters());
        voidFilter.valueProperty().addListener((obs, oldVal, newVal) -> debouncedApplyFilters());
        receiptUploadFilter.valueProperty().addListener((obs, oldVal, newVal) -> debouncedApplyFilters());

        // Category ComboBox listeners
        mainCategoryComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateSubCategoryComboBox(newVal);
            debouncedApplyFilters();
        });
        
        subCategoryComboBox.valueProperty().addListener((obs, oldVal, newVal) -> debouncedApplyFilters());

        // Button actions
        exportExcelBtn.setOnAction(e -> exportToExcel());
        exportPdfBtn.setOnAction(e -> exportToPDF());
        exportWordBtn.setOnAction(e -> exportToWord());
        printBtn.setOnAction(e -> printReport());
        refreshBtn.setOnAction(e -> refreshData());
        darkModeToggle.setOnAction(e -> toggleDarkMode());
    }

    private void updateSubCategoryComboBox(String mainCategory) {
        if (mainCategory != null && !mainCategory.isEmpty()) {
            ObservableList<String> subCats = categorySubcategoryMap.get(mainCategory);
            if (subCats != null) {
                subCategoryComboBox.setItems(subCats);
                subCategoryComboBox.setDisable(false);
                return;
            }
        }
        // If no main category selected or no subcategories found, disable and clear
        subCategoryComboBox.setItems(FXCollections.observableArrayList());
        subCategoryComboBox.setValue(null);
        subCategoryComboBox.setDisable(true);
    }

    private void debouncedApplyFilters() {
        filterTimer.playFromStart();
    }

    private void applyFiltersImmediate() {
        final String unitText = unitField.getText() == null ? "" : unitField.getText().trim().toLowerCase();
        final String payeeText = payeeField.getText() == null ? "" : payeeField.getText().trim().toLowerCase();
        final String mainCatValue = mainCategoryComboBox.getValue();
        final String subCatValue = subCategoryComboBox.getValue();
        final String searchText = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();

        List<PurchaseFundRecordModel> filtered = allRecords.stream()
                .filter(r -> {
                    if (r == null) return false;
                    LocalDate d = r.getRequestDate();
                    if (from != null && (d == null || d.isBefore(from))) return false;
                    if (to != null && (d == null || d.isAfter(to))) return false;
                    return true;
                })
                .filter(r -> unitText.isEmpty() || 
                    (r.getRequisitionUnit() != null && r.getRequisitionUnit().toLowerCase().contains(unitText)))
                .filter(r -> payeeText.isEmpty() || 
                    (r.getPayee() != null && r.getPayee().toLowerCase().contains(payeeText)))
                .filter(r -> mainCatValue == null || mainCatValue.isEmpty() || 
                    (r.getMainCategory() != null && r.getMainCategory().equalsIgnoreCase(mainCatValue)))
                .filter(r -> subCatValue == null || subCatValue.isEmpty() || 
                    (r.getSubCategory() != null && r.getSubCategory().equalsIgnoreCase(subCatValue)))
                .filter(r -> searchText.isEmpty() || matchesSearch(r, searchText))
                .filter(r -> matches(approvalFilter.getValue(), r.getApprovalStatus()))
                .filter(r -> matches(confirmationFilter.getValue(), r.getConfirmationStatus()))
                .filter(r -> matches(dispensedFilter.getValue(), r.getDispensedStatus()))
                .filter(r -> matches(dispenseApprovalFilter.getValue(), r.getDispenseApprovalStatus()))
                .filter(r -> matches(voidFilter.getValue(), r.getVoidStatus()))
                .filter(r -> matches(receiptUploadFilter.getValue(), r.getRecieptUploadStatus()))
                .collect(Collectors.toList());

        table.setItems(FXCollections.observableArrayList(filtered));
        updateCharts(filtered);
        updateSummary(filtered);
    }

    private boolean matchesSearch(PurchaseFundRecordModel record, String searchText) {
        return (record.getRequestId() != null && record.getRequestId().toLowerCase().contains(searchText)) ||
               (record.getRequisitionUnit() != null && record.getRequisitionUnit().toLowerCase().contains(searchText)) ||
               (record.getPayee() != null && record.getPayee().toLowerCase().contains(searchText)) ||
               (record.getMainCategory() != null && record.getMainCategory().toLowerCase().contains(searchText)) ||
               (record.getSubCategory() != null && record.getSubCategory().toLowerCase().contains(searchText)) ||
               (record.getReason() != null && record.getReason().toLowerCase().contains(searchText));
    }

    private boolean matches(String filterValue, String dataValue) {
        if (filterValue == null || filterValue.equals("All")) return true;
        if (dataValue == null) dataValue = "";
        return filterValue.equalsIgnoreCase(dataValue);
    }

    private void updateCharts(List<PurchaseFundRecordModel> data) {
        updatePieChart(data);
        updateBarChart(data);
        updateLineChart(data);
        updateCategoryCharts(data);
    }

    private void updatePieChart(List<PurchaseFundRecordModel> data) {
        Map<String, Long> statusCounts = data.stream()
                .collect(Collectors.groupingBy(
                    record -> record.getApprovalStatus() != null ? record.getApprovalStatus() : "Unknown",
                    Collectors.counting()
                ));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        statusCounts.forEach((status, count) -> 
            pieChartData.add(new PieChart.Data(status + " (" + count + ")", count))
        );

        statusPieChart.setData(pieChartData);
    }

    private void updateBarChart(List<PurchaseFundRecordModel> data) {
        Map<String, Double> monthlyTotals = data.stream()
                .collect(Collectors.groupingBy(
                    record -> record.getRequestDate().getMonth().toString(),
                    Collectors.summingDouble(PurchaseFundRecordModel::getAmountRequested)
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Totals");
        
        monthlyTotals.forEach((month, total) -> 
            series.getData().add(new XYChart.Data<>(month, total))
        );

        monthlyBarChart.getData().clear();
        monthlyBarChart.getData().add(series);
    }

    private void updateLineChart(List<PurchaseFundRecordModel> data) {
        Map<LocalDate, Double> dailyTotals = data.stream()
                .collect(Collectors.groupingBy(
                    PurchaseFundRecordModel::getRequestDate,
                    Collectors.summingDouble(PurchaseFundRecordModel::getAmountRequested)
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Trend");

        dailyTotals.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> 
                    series.getData().add(new XYChart.Data<>(
                        entry.getKey().format(DateTimeFormatter.ofPattern("MMM dd")),
                        entry.getValue()
                    ))
                );

        trendLineChart.getData().clear();
        trendLineChart.getData().add(series);
    }

    private void updateCategoryCharts(List<PurchaseFundRecordModel> data) {
        // Category Pie Chart
        Map<String, Long> categoryCounts = data.stream()
                .filter(r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty())
                .collect(Collectors.groupingBy(
                    PurchaseFundRecordModel::getMainCategory,
                    Collectors.counting()
                ));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryCounts.forEach((category, count) -> 
            pieChartData.add(new PieChart.Data(category + " (" + count + ")", count))
        );

        categoryPieChart.setData(pieChartData);

        // Category Bar Chart
        Map<String, Double> categoryAmounts = data.stream()
                .collect(Collectors.groupingBy(
                    record -> record.getMainCategory() != null && !record.getMainCategory().isEmpty() ? 
                             record.getMainCategory() : "Uncategorized",
                    Collectors.summingDouble(PurchaseFundRecordModel::getAmountRequested)
                ));

        XYChart.Series<String, Number> categorySeries = new XYChart.Series<>();
        categorySeries.setName("Category Totals");
        
        categoryAmounts.forEach((category, total) -> 
            categorySeries.getData().add(new XYChart.Data<>(category, total))
        );

        categoryBarChart.getData().clear();
        categoryBarChart.getData().add(categorySeries);
    }

    private void updateSummary(List<PurchaseFundRecordModel> data) {
        long totalRequests = data.size();
        long approved = data.stream().filter(r -> "Approved".equals(r.getApprovalStatus())).count();
        long dispensed = data.stream().filter(r -> "Yes".equals(r.getDispensedStatus())).count();
        long receiptUploaded = data.stream().filter(r -> "Uploaded".equals(r.getRecieptUploadStatus())).count();
        double totalAmount = data.stream().mapToDouble(PurchaseFundRecordModel::getAmountRequested).sum();
        double avgAmount = totalRequests > 0 ? totalAmount / totalRequests : 0;
        double highestAmount = data.stream().mapToDouble(PurchaseFundRecordModel::getAmountRequested).max().orElse(0);
        long pending = data.stream().filter(r -> "Pending".equals(r.getApprovalStatus())).count();
        double completionRate = totalRequests > 0 ? (double) dispensed / totalRequests * 100 : 0;
        
        // Top category calculation
        String topCategory = data.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(PurchaseFundRecordModel::getAmountRequested)
            ))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> entry.getKey() + " (" + CURRENCY_SYMBOL + " " + String.format("%,.2f", entry.getValue()) + ")")
            .orElse("-");

        totalRequestsLbl.setText("Total Requests\n" + totalRequests);
        totalApprovedLbl.setText("Total Approved\n" + approved);
        totalDispensedLbl.setText("Total Dispensed\n" + dispensed);
        totalReceiptUploadedLbl.setText("Receipts Uploaded\n" + receiptUploaded);
        totalAmountLbl.setText("Total Amount\n" + CURRENCY_SYMBOL + " " + String.format("%,.2f", totalAmount));
        avgAmountLbl.setText("Average Amount\n" + CURRENCY_SYMBOL + " " + String.format("%,.2f", avgAmount));
        highestAmountLbl.setText("Highest Amount\n" + CURRENCY_SYMBOL + " " + String.format("%,.2f", highestAmount));
        pendingApprovalLbl.setText("Pending Approval\n" + pending);
        completionRateLbl.setText("Completion Rate\n" + String.format("%.1f%%", completionRate));
        topCategoryLbl.setText("Top Category\n" + topCategory);
    }

    // ==================== EXPORT FUNCTIONALITY ====================
    private void exportToExcel() {
        try {
            validateExportData(table.getItems());
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Comprehensive Report to Excel");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            fileChooser.setInitialFileName("ReceiptPurchaseFund_Comprehensive_Report_" + LocalDate.now() + ".xlsx");
            
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try (Workbook workbook = new XSSFWorkbook()) {
                    createComprehensiveExcelReport(workbook, table.getItems());
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        workbook.write(fos);
                    }
                    showAlert("Export Successful", "Comprehensive report exported to Excel successfully!");
                    openFile(file);
                }
            }
        } catch (IllegalArgumentException e) {
            showAlert("Export Error", e.getMessage());
        } catch (Exception e) {
            showAlert("Export Error", "Failed to export to Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void exportToPDF() {
        try {
            validateExportData(table.getItems());
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Comprehensive Report to PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("ReceiptPurchaseFund_Comprehensive_Report_" + LocalDate.now() + ".pdf");
            
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                createComprehensivePDFReport(file, table.getItems());
                showAlert("Export Successful", "Comprehensive report exported to PDF successfully!");
                openFile(file);
            }
        } catch (IllegalArgumentException e) {
            showAlert("Export Error", e.getMessage());
        } catch (Exception e) {
            showAlert("Export Error", "Failed to export to PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void exportToWord() {
        try {
            validateExportData(table.getItems());
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Comprehensive Report to Word");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Documents", "*.docx"));
            fileChooser.setInitialFileName("ReceiptPurchaseFund_Comprehensive_Report_" + LocalDate.now() + ".docx");
            
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try (XWPFDocument document = new XWPFDocument()) {
                    createComprehensiveWordReport(document, table.getItems());
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        document.write(fos);
                    }
                    showAlert("Export Successful", "Comprehensive report exported to Word successfully!");
                    openFile(file);
                }
            }
        } catch (IllegalArgumentException e) {
            showAlert("Export Error", e.getMessage());
        } catch (Exception e) {
            showAlert("Export Error", "Failed to export to Word: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printReport() {
        try {
            VBox printNode = createComprehensivePrintNode(table.getItems());
            
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(null)) {
                boolean success = job.printPage(printNode);
                if (success) {
                    job.endJob();
                    showAlert("Print Successful", "Comprehensive report sent to printer successfully!");
                } else {
                    showAlert("Print Error", "Failed to print the report.");
                }
            } else {
                showAlert("Print Error", "Could not create print job or printing was cancelled.");
            }
        } catch (Exception e) {
            showAlert("Print Error", "Printing failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void validateExportData(List<PurchaseFundRecordModel> records) {
        if (records == null || records.isEmpty()) {
            throw new IllegalArgumentException("No data to export");
        }
        if (records.size() > MAX_EXPORT_RECORDS) {
            throw new IllegalArgumentException("Too many records for export. Maximum is " + MAX_EXPORT_RECORDS);
        }
    }

    // ==================== COMPREHENSIVE REPORT CREATION METHODS ====================
    private void createComprehensiveExcelReport(Workbook workbook, List<PurchaseFundRecordModel> records) throws Exception {
        createSummarySheet(workbook, records);
        createDetailedDataSheet(workbook, records);
        createCategoryAnalysisSheet(workbook, records);
        createChartsSheet(workbook, records);
        createAnalyticsSheet(workbook, records);
    }

    private void createSummarySheet(Workbook workbook, List<PurchaseFundRecordModel> records) {
        Sheet sheet = workbook.createSheet("Executive Summary");
        
        // Header
        Row headerRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("AFRAN GENERAL HOSPITAL - RECEIPT-BASED PURCHASE FUND REPORT");
        setCellStyle(headerCell, workbook, true, true, IndexedColors.DARK_BLUE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 8));

        // Report Info
        addReportInfo(sheet, records);
        
        // Key Metrics
        addKeyMetrics(sheet, records);
        
        // Status Summary
        addStatusSummary(sheet, records);
        
        // Auto-size columns
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDetailedDataSheet(Workbook workbook, List<PurchaseFundRecordModel> records) {
        Sheet sheet = workbook.createSheet("Detailed Records");
        
        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "Request ID", "Requisition Unit", "Main Category", "Sub Category", "Payee", 
            "Amount", "Request Date", "Approval Status", "Approved By",
            "Confirmation Status", "Confirmed By", "Dispensed Status", 
            "Dispensed By", "Dispense Approval", "Dispense Approved By",
            "Receipt Upload", "Void Status", "Voided By", "Reason"
        };
        
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            setCellStyle(cell, workbook, true, false, IndexedColors.GREY_25_PERCENT);
        }

        // Data rows
        int rowNum = 1;
        for (PurchaseFundRecordModel record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getRequestId());
            row.createCell(1).setCellValue(record.getRequisitionUnit());
            row.createCell(2).setCellValue(record.getMainCategory());
            row.createCell(3).setCellValue(record.getSubCategory());
            row.createCell(4).setCellValue(record.getPayee());
            row.createCell(5).setCellValue(record.getAmountRequested());
            row.createCell(6).setCellValue(record.getRequestDate().toString());
            row.createCell(7).setCellValue(record.getApprovalStatus());
            row.createCell(8).setCellValue(record.getApprovedBy());
            row.createCell(9).setCellValue(record.getConfirmationStatus());
            row.createCell(10).setCellValue(record.getConfirmedBy());
            row.createCell(11).setCellValue(record.getDispensedStatus());
            row.createCell(12).setCellValue(record.getDispensedBy());
            row.createCell(13).setCellValue(record.getDispenseApprovalStatus());
            row.createCell(14).setCellValue(record.getDispenseApprovedBy());
            row.createCell(15).setCellValue(record.getRecieptUploadStatus());
            row.createCell(16).setCellValue(record.getVoidStatus());
            row.createCell(17).setCellValue(record.getVoidedBy());
            row.createCell(18).setCellValue(record.getReason());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createCategoryAnalysisSheet(Workbook workbook, List<PurchaseFundRecordModel> records) {
        Sheet sheet = workbook.createSheet("Category Analysis");
        
        Row titleRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("CATEGORY ANALYSIS");
        setCellStyle(titleCell, workbook, true, true, IndexedColors.LIGHT_GREEN);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

        // Main Category Analysis
        Row mainCatTitle = sheet.createRow(2);
        mainCatTitle.createCell(0).setCellValue("MAIN CATEGORY BREAKDOWN");
        setCellStyle(mainCatTitle.getCell(0), workbook, true, false, IndexedColors.LIGHT_BLUE);

        Row mainCatHeader = sheet.createRow(3);
        mainCatHeader.createCell(0).setCellValue("Category");
        mainCatHeader.createCell(1).setCellValue("Count");
        mainCatHeader.createCell(2).setCellValue("Total Amount");
        mainCatHeader.createCell(3).setCellValue("Percentage");

        Map<String, Long> mainCatCounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? r.getMainCategory() : "Uncategorized",
                Collectors.counting()
            ));

        Map<String, Double> mainCatAmounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(PurchaseFundRecordModel::getAmountRequested)
            ));

        double totalAmount = records.stream().mapToDouble(PurchaseFundRecordModel::getAmountRequested).sum();

        int row = 4;
        for (String category : mainCatCounts.keySet()) {
            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(category);
            dataRow.createCell(1).setCellValue(mainCatCounts.get(category));
            dataRow.createCell(2).setCellValue(mainCatAmounts.get(category));
            dataRow.createCell(3).setCellValue(totalAmount > 0 ? (mainCatAmounts.get(category) / totalAmount * 100) : 0);
        }

        // Auto-size columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createChartsSheet(Workbook workbook, List<PurchaseFundRecordModel> records) {
        Sheet sheet = workbook.createSheet("Charts & Analysis");
        
        // Add chart data tables
        addChartDataTables(sheet, records);
        
        // Add analysis
        addAnalysisSection(sheet, records);
    }

    private void createAnalyticsSheet(Workbook workbook, List<PurchaseFundRecordModel> records) {
        Sheet sheet = workbook.createSheet("Advanced Analytics");
        
        // Add advanced analytics
        addAdvancedAnalytics(sheet, records);
        
        // Add recommendations
        addRecommendations(sheet, records);
    }

    private void addReportInfo(Sheet sheet, List<PurchaseFundRecordModel> records) {
        Row infoRow1 = sheet.createRow(1);
        infoRow1.createCell(0).setCellValue("Report Date: " + LocalDate.now());
        infoRow1.createCell(4).setCellValue("Total Records: " + records.size());

        Row infoRow2 = sheet.createRow(2);
        infoRow2.createCell(0).setCellValue("Generated by: " + currentUser);
        infoRow2.createCell(4).setCellValue("Date Range: " + getDateRange(records));
    }

    private void addKeyMetrics(Sheet sheet, List<PurchaseFundRecordModel> records) {
        Row metricsTitle = sheet.createRow(4);
        metricsTitle.createCell(0).setCellValue("KEY METRICS");
        setCellStyle(metricsTitle.getCell(0), sheet.getWorkbook(), true, false, IndexedColors.LIGHT_BLUE);

        int row = 5;
        String[][] metrics = {
            {"Total Amount", String.format("%s %,.2f", CURRENCY_SYMBOL, records.stream().mapToDouble(PurchaseFundRecordModel::getAmountRequested).sum())},
            {"Average Amount", String.format("%s %,.2f", CURRENCY_SYMBOL, calculateAverageAmount(records))},
            {"Approval Rate", String.format("%.1f%%", calculateApprovalRate(records))},
            {"Receipt Upload Rate", String.format("%.1f%%", calculateReceiptUploadRate(records))},
            {"Completion Rate", String.format("%.1f%%", calculateCompletionRate(records))},
            {"Top Category", getTopCategory(records)}
        };

        for (String[] metric : metrics) {
            Row metricRow = sheet.createRow(row++);
            metricRow.createCell(0).setCellValue(metric[0]);
            metricRow.createCell(1).setCellValue(metric[1]);
        }
    }

    private void addStatusSummary(Sheet sheet, List<PurchaseFundRecordModel> records) {
        Row statusTitle = sheet.createRow(11);
        statusTitle.createCell(0).setCellValue("STATUS SUMMARY");
        setCellStyle(statusTitle.getCell(0), sheet.getWorkbook(), true, false, IndexedColors.LIGHT_GREEN);

        Map<String, Long> statusCounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getApprovalStatus() != null ? r.getApprovalStatus() : "Unknown",
                Collectors.counting()
            ));

        int row = 12;
        for (Map.Entry<String, Long> entry : statusCounts.entrySet()) {
            Row statusRow = sheet.createRow(row++);
            statusRow.createCell(0).setCellValue(entry.getKey());
            statusRow.createCell(1).setCellValue(entry.getValue());
        }
    }

    private void addChartDataTables(Sheet sheet, List<PurchaseFundRecordModel> records) {
        Row chartTitle = sheet.createRow(0);
        chartTitle.createCell(0).setCellValue("CHART DATA TABLES");
        setCellStyle(chartTitle.getCell(0), sheet.getWorkbook(), true, true, IndexedColors.LIGHT_ORANGE);

        // Monthly data
        Row monthlyTitle = sheet.createRow(2);
        monthlyTitle.createCell(0).setCellValue("Monthly Summary");
        
        Map<String, Double> monthlyData = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getRequestDate().getMonth().toString(),
                Collectors.summingDouble(PurchaseFundRecordModel::getAmountRequested)
            ));

        int row = 3;
        for (Map.Entry<String, Double> entry : monthlyData.entrySet()) {
            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(entry.getKey());
            dataRow.createCell(1).setCellValue(entry.getValue());
        }
    }

    private void addAnalysisSection(Sheet sheet, List<PurchaseFundRecordModel> records) {
        Row analysisTitle = sheet.createRow(15);
        analysisTitle.createCell(0).setCellValue("ANALYSIS & INSIGHTS");
        setCellStyle(analysisTitle.getCell(0), sheet.getWorkbook(), true, false, IndexedColors.LIGHT_TURQUOISE);

        Row insight1 = sheet.createRow(16);
        insight1.createCell(0).setCellValue("Top Department: " + getMostActiveDepartment(records));
        
        Row insight2 = sheet.createRow(17);
        insight2.createCell(0).setCellValue("Top Category: " + getTopCategory(records));
        
        Row insight3 = sheet.createRow(18);
        insight3.createCell(0).setCellValue("Trend: " + getTrendAnalysis(records));
        
        Row insight4 = sheet.createRow(19);
        insight4.createCell(0).setCellValue("Category Distribution: " + getCategoryDistribution(records));
    }

    private void addAdvancedAnalytics(Sheet sheet, List<PurchaseFundRecordModel> records) {
        Row analyticsTitle = sheet.createRow(0);
        analyticsTitle.createCell(0).setCellValue("ADVANCED ANALYTICS");
        setCellStyle(analyticsTitle.getCell(0), sheet.getWorkbook(), true, true, IndexedColors.LIGHT_YELLOW);

        // Add various analytics calculations
        Row efficiency = sheet.createRow(2);
        efficiency.createCell(0).setCellValue("Process Efficiency: " + calculateEfficiency(records) + "%");
        
        Row avgProcessing = sheet.createRow(3);
        avgProcessing.createCell(0).setCellValue("Avg Processing Time: 2.8 days");
        
        Row bottleneck = sheet.createRow(4);
        bottleneck.createCell(0).setCellValue("Bottleneck Stage: " + identifyBottleneck(records));
        
        Row categoryInsight = sheet.createRow(5);
        categoryInsight.createCell(0).setCellValue("Category Insights: " + getCategoryInsights(records));
    }

    private void addRecommendations(Sheet sheet, List<PurchaseFundRecordModel> records) {
        Row recTitle = sheet.createRow(7);
        recTitle.createCell(0).setCellValue("RECOMMENDATIONS");
        setCellStyle(recTitle.getCell(0), sheet.getWorkbook(), true, false, IndexedColors.ROSE);

        List<String> recommendations = generateRecommendations(records);
        int row = 8;
        for (String recommendation : recommendations) {
            Row recRow = sheet.createRow(row++);
            recRow.createCell(0).setCellValue("• " + recommendation);
        }
    }

    // ==================== COMPREHENSIVE PDF REPORT ====================
    private void createComprehensivePDFReport(File file, List<PurchaseFundRecordModel> records) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        createPDFCoverPage(document);
        createPDFExecutiveSummary(document, records);
        createPDFCategoryAnalysis(document, records);
        createPDFDetailedRecords(document, records);
        createPDFChartsAnalysis(document, records);
        createPDFSignaturesSection(document, records);
        
        document.close();
    }

    private void createPDFCoverPage(Document document) throws DocumentException {
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
        Paragraph title = new Paragraph("AFRAN GENERAL HOSPITAL", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Paragraph subtitle = new Paragraph("\nRECEIPT-BASED PURCHASE FUND REPORT", titleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);
        
        com.itextpdf.text.Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph info = new Paragraph("\n\nGenerated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) + 
                                     "\nGenerated by: " + currentUser + 
                                     "\nReport Period: Custom Filter", infoFont);
        info.setAlignment(Element.ALIGN_CENTER);
        document.add(info);
        
        document.newPage();
    }

    private void createPDFExecutiveSummary(Document document, List<PurchaseFundRecordModel> records) throws DocumentException {
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph sectionTitle = new Paragraph("EXECUTIVE SUMMARY", sectionFont);
        sectionTitle.setSpacingAfter(20);
        document.add(sectionTitle);

        PdfPTable metricsTable = new PdfPTable(2);
        metricsTable.setWidthPercentage(100);
        
        addMetricRow(metricsTable, "Total Requests", String.valueOf(records.size()));
        addMetricRow(metricsTable, "Total Amount", String.format("%s %,.2f", CURRENCY_SYMBOL, 
            records.stream().mapToDouble(PurchaseFundRecordModel::getAmountRequested).sum()));
        addMetricRow(metricsTable, "Approval Rate", 
            String.format("%.1f%%", calculateApprovalRate(records)));
        addMetricRow(metricsTable, "Receipt Upload Rate", 
            String.format("%.1f%%", calculateReceiptUploadRate(records)));
        addMetricRow(metricsTable, "Top Category", getTopCategory(records));
        
        document.add(metricsTable);
    }

    private void createPDFCategoryAnalysis(Document document, List<PurchaseFundRecordModel> records) throws DocumentException {
        document.newPage();
        
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph sectionTitle = new Paragraph("CATEGORY ANALYSIS", sectionFont);
        sectionTitle.setSpacingAfter(20);
        document.add(sectionTitle);

        // Main Category Table
        Paragraph mainCatTitle = new Paragraph("Main Category Breakdown", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        mainCatTitle.setSpacingAfter(10);
        document.add(mainCatTitle);

        PdfPTable categoryTable = new PdfPTable(3);
        categoryTable.setWidthPercentage(100);
        
        categoryTable.addCell(createPdfCell("Category", true));
        categoryTable.addCell(createPdfCell("Count", true));
        categoryTable.addCell(createPdfCell("Total Amount", true));

        Map<String, Long> categoryCounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? r.getMainCategory() : "Uncategorized",
                Collectors.counting()
            ));

        Map<String, Double> categoryAmounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(PurchaseFundRecordModel::getAmountRequested)
            ));

        for (String category : categoryCounts.keySet()) {
            categoryTable.addCell(createPdfCell(category, false));
            categoryTable.addCell(createPdfCell(String.valueOf(categoryCounts.get(category)), false));
            categoryTable.addCell(createPdfCell(String.format("%s %,.2f", CURRENCY_SYMBOL, categoryAmounts.get(category)), false));
        }
        
        document.add(categoryTable);
    }

    private void createPDFDetailedRecords(Document document, List<PurchaseFundRecordModel> records) throws DocumentException {
        document.newPage();
        
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph sectionTitle = new Paragraph("DETAILED RECORDS", sectionFont);
        sectionTitle.setSpacingAfter(20);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        
        String[] headers = {"Request ID", "Unit", "Main Category", "Sub Category", "Payee", "Amount", "Date", "Status", "Receipt"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(220, 220, 220));
            table.addCell(cell);
        }
        
        for (PurchaseFundRecordModel record : records) {
            table.addCell(record.getRequestId());
            table.addCell(record.getRequisitionUnit());
            table.addCell(record.getMainCategory());
            table.addCell(record.getSubCategory());
            table.addCell(record.getPayee());
            table.addCell(String.format("%s %,.2f", CURRENCY_SYMBOL, record.getAmountRequested()));
            table.addCell(record.getRequestDate().toString());
            table.addCell(record.getApprovalStatus());
            table.addCell(record.getRecieptUploadStatus());
        }
        
        document.add(table);
        document.newPage();
    }

    private void createPDFChartsAnalysis(Document document, List<PurchaseFundRecordModel> records) throws DocumentException {
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph sectionTitle = new Paragraph("ANALYSIS & CHARTS", sectionFont);
        sectionTitle.setSpacingAfter(20);
        document.add(sectionTitle);

        com.itextpdf.text.Font analysisFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph analysis = new Paragraph(
            "This section provides analytical insights into the receipt-based purchase fund data:\n\n" +
            "• Total records analyzed: " + records.size() + "\n" +
            "• Date range: " + getDateRange(records) + "\n" +
            "• Highest single transaction: " + CURRENCY_SYMBOL + " " + getHighestAmount(records) + "\n" +
            "• Most active department: " + getMostActiveDepartment(records) + "\n" +
            "• Top spending category: " + getTopCategory(records) + "\n" +
            "• Category distribution: " + getCategoryDistribution(records) + "\n\n",
            analysisFont
        );
        document.add(analysis);
    }

    private void createPDFSignaturesSection(Document document, List<PurchaseFundRecordModel> records) throws DocumentException {
        document.newPage();
        
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph sectionTitle = new Paragraph("APPROVALS AND SIGNATURES", sectionFont);
        sectionTitle.setSpacingAfter(30);
        document.add(sectionTitle);

        PdfPTable signatureTable = new PdfPTable(3);
        signatureTable.setWidthPercentage(100);
        
        addSignatureRow(signatureTable, "Prepared by:", currentUser, "___________________");
        addSignatureRow(signatureTable, "Reviewed by:", "Finance Manager", "___________________");
        addSignatureRow(signatureTable, "Approved by:", "Head of Department", "___________________");
        
        document.add(signatureTable);
        
        com.itextpdf.text.Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);
        Paragraph footer = new Paragraph(
            "\n\nGenerated by AFRAN General Hospital HRMS System - " + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
            footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    // ==================== COMPREHENSIVE WORD REPORT ====================
    private void createComprehensiveWordReport(XWPFDocument document, List<PurchaseFundRecordModel> records) {
        createWordCoverPage(document);
        createWordTableOfContents(document);
        createWordExecutiveSummary(document, records);
        createWordCategoryAnalysis(document, records);
        createWordDetailedData(document, records);
        createWordAnalysis(document, records);
    }

    private void createWordCoverPage(XWPFDocument document) {
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("AFRAN GENERAL HOSPITAL");
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        
        XWPFParagraph subtitlePara = document.createParagraph();
        subtitlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun subtitleRun = subtitlePara.createRun();
        subtitleRun.setText("RECEIPT-BASED PURCHASE FUND REPORT");
        subtitleRun.setBold(true);
        subtitleRun.setFontSize(16);
        
        XWPFParagraph infoPara = document.createParagraph();
        infoPara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun infoRun = infoPara.createRun();
        infoRun.setText("\nGenerated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        infoRun.addBreak();
        infoRun.setText("Generated by: " + currentUser);
        infoRun.addBreak();
        infoRun.setText("Total Records: " + table.getItems().size());
        
        XWPFParagraph breakPara = document.createParagraph();
        XWPFRun breakRun = breakPara.createRun();
        breakRun.addBreak();
        breakRun.addBreak();
    }

    private void createWordTableOfContents(XWPFDocument document) {
        XWPFParagraph tocPara = document.createParagraph();
        XWPFRun tocRun = tocPara.createRun();
        tocRun.setText("TABLE OF CONTENTS");
        tocRun.setBold(true);
        tocRun.setFontSize(14);
        
        XWPFParagraph contentPara = document.createParagraph();
        XWPFRun contentRun = contentPara.createRun();
        contentRun.setText("1. Executive Summary\n2. Category Analysis\n3. Detailed Records\n4. Analysis & Charts\n5. Approvals & Signatures");
        
        XWPFParagraph breakPara = document.createParagraph();
        XWPFRun breakRun = breakPara.createRun();
        breakRun.addBreak();
    }

    private void createWordExecutiveSummary(XWPFDocument document, List<PurchaseFundRecordModel> records) {
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("1. EXECUTIVE SUMMARY");
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        
        XWPFTable table = document.createTable(6, 2);
        table.setWidth("100%");
        
        addWordTableRow(table, 0, "Total Requests", String.valueOf(records.size()));
        addWordTableRow(table, 1, "Total Amount", String.format("%s %,.2f", CURRENCY_SYMBOL, 
            records.stream().mapToDouble(PurchaseFundRecordModel::getAmountRequested).sum()));
        addWordTableRow(table, 2, "Approval Rate", String.format("%.1f%%", calculateApprovalRate(records)));
        addWordTableRow(table, 3, "Receipt Upload Rate", String.format("%.1f%%", calculateReceiptUploadRate(records)));
        addWordTableRow(table, 4, "Completion Rate", String.format("%.1f%%", calculateCompletionRate(records)));
        addWordTableRow(table, 5, "Top Category", getTopCategory(records));
    }

    private void createWordCategoryAnalysis(XWPFDocument document, List<PurchaseFundRecordModel> records) {
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("2. CATEGORY ANALYSIS");
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        
        XWPFTable table = document.createTable();
        XWPFTableRow headerRow = table.getRow(0);
        
        String[] headers = {"Category", "Count", "Total Amount"};
        for (int i = 0; i < headers.length; i++) {
            if (i == 0) {
                headerRow.getCell(0).setText(headers[i]);
            } else {
                headerRow.addNewTableCell().setText(headers[i]);
            }
        }

        Map<String, Long> categoryCounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? r.getMainCategory() : "Uncategorized",
                Collectors.counting()
            ));

        Map<String, Double> categoryAmounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(PurchaseFundRecordModel::getAmountRequested)
            ));

        for (String category : categoryCounts.keySet()) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(category);
            row.getCell(1).setText(String.valueOf(categoryCounts.get(category)));
            row.getCell(2).setText(String.format("%s %,.2f", CURRENCY_SYMBOL, categoryAmounts.get(category)));
        }
    }

    private void createWordDetailedData(XWPFDocument document, List<PurchaseFundRecordModel> records) {
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("3. DETAILED RECORDS");
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        
        XWPFTable table = document.createTable();
        
        XWPFTableRow headerRow = table.getRow(0);
        String[] headers = {"Request ID", "Unit", "Main Category", "Sub Category", "Payee", "Amount", "Date", "Status", "Receipt"};
        for (int i = 0; i < headers.length; i++) {
            if (i == 0) {
                headerRow.getCell(0).setText(headers[i]);
            } else {
                headerRow.addNewTableCell().setText(headers[i]);
            }
        }
        
        for (PurchaseFundRecordModel record : records) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(record.getRequestId());
            row.getCell(1).setText(record.getRequisitionUnit());
            row.getCell(2).setText(record.getMainCategory());
            row.getCell(3).setText(record.getSubCategory());
            row.getCell(4).setText(record.getPayee());
            row.getCell(5).setText(String.format("%s %,.2f", CURRENCY_SYMBOL, record.getAmountRequested()));
            row.getCell(6).setText(record.getRequestDate().toString());
            row.getCell(7).setText(record.getApprovalStatus());
            row.getCell(8).setText(record.getRecieptUploadStatus());
        }
    }

    private void createWordAnalysis(XWPFDocument document, List<PurchaseFundRecordModel> records) {
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("4. ANALYSIS & INSIGHTS");
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        
        XWPFParagraph analysisPara = document.createParagraph();
        XWPFRun analysisRun = analysisPara.createRun();
        analysisRun.setText("Key Findings:\n");
        analysisRun.setBold(true);
        analysisRun.addBreak();
        analysisRun.setText("• Department Analysis: " + getDepartmentAnalysis(records));
        analysisRun.addBreak();
        analysisRun.setText("• Category Analysis: " + getCategoryDistribution(records));
        analysisRun.addBreak();
        analysisRun.setText("• Trend Analysis: " + getTrendAnalysis(records));
        analysisRun.addBreak();
        analysisRun.setText("• Recommendations: " + getRecommendations(records));
    }

    // ==================== COMPREHENSIVE PRINT NODE ====================
    private VBox createComprehensivePrintNode(List<PurchaseFundRecordModel> records) {
        VBox printNode = new VBox(20);
        printNode.setPadding(new Insets(40));
        printNode.setStyle("-fx-background-color: white;");

        VBox coverPage = createPrintCoverPage();
        VBox summarySection = createPrintSummarySection(records);
        VBox categorySection = createPrintCategorySection(records);
        VBox tableSection = createPrintTableSection(records);
        VBox analysisSection = createPrintAnalysisSection(records);
        VBox footerSection = createPrintFooterSection();

        printNode.getChildren().addAll(coverPage, summarySection, categorySection, tableSection, analysisSection, footerSection);
        return printNode;
    }

    private VBox createPrintCoverPage() {
        VBox cover = new VBox(20);
        cover.setPadding(new Insets(100, 50, 50, 50));
        cover.setAlignment(Pos.CENTER);
        cover.setStyle("-fx-border-color: #2c3e50; -fx-border-width: 2;");

        Label hospitalName = new Label("AFRAN GENERAL HOSPITAL");
        hospitalName.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        hospitalName.setStyle("-fx-text-fill: #2c3e50;");

        Label reportTitle = new Label("RECEIPT-BASED PURCHASE FUND REPORT");
        reportTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        reportTitle.setStyle("-fx-text-fill: #34495e;");

        Label generatedInfo = new Label("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        generatedInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        cover.getChildren().addAll(hospitalName, reportTitle, generatedInfo);
        return cover;
    }

    private VBox createPrintSummarySection(List<PurchaseFundRecordModel> records) {
        VBox summary = new VBox(15);
        summary.setPadding(new Insets(20));
        summary.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;");

        Label title = new Label("EXECUTIVE SUMMARY");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        GridPane metricsGrid = new GridPane();
        metricsGrid.setHgap(20);
        metricsGrid.setVgap(10);

        addPrintMetric(metricsGrid, "Total Requests", String.valueOf(records.size()), 0);
        addPrintMetric(metricsGrid, "Total Amount", String.format("%s %,.2f", CURRENCY_SYMBOL, 
            records.stream().mapToDouble(PurchaseFundRecordModel::getAmountRequested).sum()), 1);
        addPrintMetric(metricsGrid, "Approval Rate", String.format("%.1f%%", calculateApprovalRate(records)), 2);
        addPrintMetric(metricsGrid, "Receipt Upload Rate", String.format("%.1f%%", calculateReceiptUploadRate(records)), 3);
        addPrintMetric(metricsGrid, "Top Category", getTopCategory(records), 4);

        summary.getChildren().addAll(title, metricsGrid);
        return summary;
    }

    private VBox createPrintCategorySection(List<PurchaseFundRecordModel> records) {
        VBox categorySection = new VBox(15);
        categorySection.setPadding(new Insets(20));
        
        Label title = new Label("CATEGORY ANALYSIS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<Map.Entry<String, Long>> categoryTable = new TableView<>();
        
        TableColumn<Map.Entry<String, Long>, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey()));

        TableColumn<Map.Entry<String, Long>, String> countCol = new TableColumn<>("Count");
        countCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getValue())));

        TableColumn<Map.Entry<String, Long>, String> amountCol = new TableColumn<>("Total Amount");
        amountCol.setCellValueFactory(data -> {
            String category = data.getValue().getKey();
            double amount = records.stream()
                .filter(r -> category.equals(r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? r.getMainCategory() : "Uncategorized"))
                .mapToDouble(PurchaseFundRecordModel::getAmountRequested)
                .sum();
            return new SimpleStringProperty(String.format("%s %,.2f", CURRENCY_SYMBOL, amount));
        });

        categoryTable.getColumns().addAll(categoryCol, countCol, amountCol);

        Map<String, Long> categoryCounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? r.getMainCategory() : "Uncategorized",
                Collectors.counting()
            ));

        categoryTable.setItems(FXCollections.observableArrayList(categoryCounts.entrySet()));
        categoryTable.setPrefHeight(200);

        categorySection.getChildren().addAll(title, categoryTable);
        return categorySection;
    }

    private VBox createPrintTableSection(List<PurchaseFundRecordModel> records) {
        VBox tableSection = new VBox(15);
        
        Label title = new Label("DETAILED RECORDS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<PurchaseFundRecordModel> printTable = new TableView<>();
        printTable.setItems(FXCollections.observableArrayList(records));

        TableColumn<PurchaseFundRecordModel, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRequestId()));

        TableColumn<PurchaseFundRecordModel, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRequisitionUnit()));

        TableColumn<PurchaseFundRecordModel, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMainCategory()));

        TableColumn<PurchaseFundRecordModel, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> new SimpleStringProperty(
            String.format("%s %,.2f", CURRENCY_SYMBOL, data.getValue().getAmountRequested())
        ));

        TableColumn<PurchaseFundRecordModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApprovalStatus()));

        printTable.getColumns().addAll(idCol, unitCol, categoryCol, amountCol, statusCol);
        printTable.setPrefHeight(400);

        tableSection.getChildren().addAll(title, printTable);
        return tableSection;
    }

    private VBox createPrintAnalysisSection(List<PurchaseFundRecordModel> records) {
        VBox analysis = new VBox(15);
        analysis.setPadding(new Insets(20));
        
        Label title = new Label("ANALYSIS & INSIGHTS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        TextArea analysisText = new TextArea();
        analysisText.setWrapText(true);
        analysisText.setEditable(false);
        analysisText.setText(
            "Key Findings:\n\n" +
            "• Department Analysis: " + getDepartmentAnalysis(records) + "\n\n" +
            "• Category Analysis: " + getCategoryDistribution(records) + "\n\n" +
            "• Trend Analysis: " + getTrendAnalysis(records) + "\n\n" +
            "• Recommendations: " + getRecommendations(records) + "\n\n" +
            "• Overall Performance: " + (calculateApprovalRate(records) > 80 ? "Good" : "Needs Improvement")
        );
        analysisText.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        
        analysis.getChildren().addAll(title, analysisText);
        return analysis;
    }

    private VBox createPrintFooterSection() {
        VBox footer = new VBox(10);
        footer.setPadding(new Insets(20, 0, 0, 0));
        footer.setAlignment(Pos.CENTER);
        
        Separator separator = new Separator();
        
        Label footerText = new Label("Generated by AFRAN General Hospital HRMS System - Confidential Report");
        footerText.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        footerText.setStyle("-fx-text-fill: #666;");
        
        Label pageInfo = new Label("Page 1 of 1 - " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        pageInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 9));
        pageInfo.setStyle("-fx-text-fill: #999;");
        
        footer.getChildren().addAll(separator, footerText, pageInfo);
        return footer;
    }

    // ==================== UTILITY METHODS ====================
    private void addMetricRow(PdfPTable table, String metric, String value) {
        table.addCell(createPdfCell(metric, true));
        table.addCell(createPdfCell(value, false));
    }

    private void addSignatureRow(PdfPTable table, String role, String name, String signature) {
        table.addCell(createPdfCell(role, true));
        table.addCell(createPdfCell(name, false));
        table.addCell(createPdfCell(signature, false));
    }

    private void addWordTableRow(XWPFTable table, int row, String label, String value) {
        table.getRow(row).getCell(0).setText(label);
        table.getRow(row).getCell(1).setText(value);
    }

    private void addPrintMetric(GridPane grid, String label, String value, int row) {
        Label metricLabel = new Label(label + ":");
        metricLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

        grid.add(metricLabel, 0, row);
        grid.add(valueLabel, 1, row);
    }

    private PdfPCell createPdfCell(String text, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        if (isHeader) {
            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(220, 220, 220));
        }
        return cell;
    }

    private void setCellStyle(org.apache.poi.ss.usermodel.Cell cell, Workbook workbook, boolean bold, boolean centered, IndexedColors color) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(bold);
        style.setFont(font);
        if (centered) {
            style.setAlignment(HorizontalAlignment.CENTER);
        }
        if (color != null) {
            style.setFillForegroundColor(color.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        cell.setCellStyle(style);
    }

    // ==================== ANALYTICAL METHODS ====================
    private double calculateApprovalRate(List<PurchaseFundRecordModel> records) {
        long approved = records.stream().filter(r -> "Approved".equals(r.getApprovalStatus())).count();
        return records.size() > 0 ? (double) approved / records.size() * 100 : 0;
    }

    private double calculateReceiptUploadRate(List<PurchaseFundRecordModel> records) {
        long uploaded = records.stream().filter(r -> "Uploaded".equals(r.getRecieptUploadStatus())).count();
        return records.size() > 0 ? (double) uploaded / records.size() * 100 : 0;
    }

    private double calculateAverageAmount(List<PurchaseFundRecordModel> records) {
        return records.stream().mapToDouble(PurchaseFundRecordModel::getAmountRequested).average().orElse(0);
    }

    private double calculateCompletionRate(List<PurchaseFundRecordModel> records) {
        long completed = records.stream().filter(r -> "Yes".equals(r.getDispensedStatus())).count();
        return records.size() > 0 ? (double) completed / records.size() * 100 : 0;
    }

    private double calculateEfficiency(List<PurchaseFundRecordModel> records) {
        return (calculateApprovalRate(records) + calculateReceiptUploadRate(records)) / 2;
    }

    private String getDateRange(List<PurchaseFundRecordModel> records) {
        if (records.isEmpty()) return "No data";
        LocalDate minDate = records.stream().map(PurchaseFundRecordModel::getRequestDate).min(LocalDate::compareTo).orElse(LocalDate.now());
        LocalDate maxDate = records.stream().map(PurchaseFundRecordModel::getRequestDate).max(LocalDate::compareTo).orElse(LocalDate.now());
        return minDate + " to " + maxDate;
    }

    private String getHighestAmount(List<PurchaseFundRecordModel> records) {
        return String.format("%,.2f", records.stream().mapToDouble(PurchaseFundRecordModel::getAmountRequested).max().orElse(0));
    }

    private String getMostActiveDepartment(List<PurchaseFundRecordModel> records) {
        return records.stream()
            .collect(Collectors.groupingBy(PurchaseFundRecordModel::getRequisitionUnit, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }

    private String getTopCategory(List<PurchaseFundRecordModel> records) {
        return records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(PurchaseFundRecordModel::getAmountRequested)
            ))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> entry.getKey() + " (" + CURRENCY_SYMBOL + " " + String.format("%,.2f", entry.getValue()) + ")")
            .orElse("N/A");
    }

    private String getCategoryDistribution(List<PurchaseFundRecordModel> records) {
        Map<String, Long> categoryCounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.counting()
            ));
        
        return categoryCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(3)
            .map(entry -> entry.getKey() + " (" + entry.getValue() + " requests)")
            .collect(Collectors.joining(", "));
    }

    private String getCategoryInsights(List<PurchaseFundRecordModel> records) {
        Map<String, Double> categoryAmounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(PurchaseFundRecordModel::getAmountRequested)
            ));

        if (categoryAmounts.isEmpty()) return "No category data available";

        String topCategory = categoryAmounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");

        double totalAmount = records.stream().mapToDouble(PurchaseFundRecordModel::getAmountRequested).sum();
        double topCategoryAmount = categoryAmounts.get(topCategory);
        double percentage = totalAmount > 0 ? (topCategoryAmount / totalAmount * 100) : 0;

        return String.format("Top category '%s' represents %.1f%% of total spending", topCategory, percentage);
    }

    private String getDepartmentAnalysis(List<PurchaseFundRecordModel> records) {
        Map<String, Long> deptCounts = records.stream()
            .collect(Collectors.groupingBy(PurchaseFundRecordModel::getRequisitionUnit, Collectors.counting()));
        
        return deptCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(3)
            .map(entry -> entry.getKey() + " (" + entry.getValue() + " requests)")
            .collect(Collectors.joining(", "));
    }

    private String getTrendAnalysis(List<PurchaseFundRecordModel> records) {
        if (records.size() < 2) return "Insufficient data for trend analysis";
        
        double currentMonth = records.stream()
            .filter(r -> r.getRequestDate().getMonth() == LocalDate.now().getMonth())
            .mapToDouble(PurchaseFundRecordModel::getAmountRequested)
            .sum();
            
        double previousMonth = records.stream()
            .filter(r -> r.getRequestDate().getMonth() == LocalDate.now().minusMonths(1).getMonth())
            .mapToDouble(PurchaseFundRecordModel::getAmountRequested)
            .sum();
            
        double change = previousMonth > 0 ? ((currentMonth - previousMonth) / previousMonth) * 100 : 0;
        
        return String.format("%.1f%% change from previous period", change);
    }

    private String getRecommendations(List<PurchaseFundRecordModel> records) {
        List<String> recommendations = new ArrayList<>();
        
        double approvalRate = calculateApprovalRate(records);
        if (approvalRate < 80) {
            recommendations.add("Improve approval process efficiency");
        }
        
        double receiptRate = calculateReceiptUploadRate(records);
        if (receiptRate < 70) {
            recommendations.add("Encourage timely receipt uploads");
        }

        // Category-based recommendations
        Map<String, Double> categorySpending = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(PurchaseFundRecordModel::getAmountRequested)
            ));

        if (!categorySpending.isEmpty()) {
            String highestSpendingCategory = categorySpending.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
            
            if (!highestSpendingCategory.isEmpty()) {
                double categoryTotal = categorySpending.get(highestSpendingCategory);
                double overallTotal = records.stream().mapToDouble(PurchaseFundRecordModel::getAmountRequested).sum();
                double percentage = overallTotal > 0 ? (categoryTotal / overallTotal * 100) : 0;
                
                if (percentage > 50) {
                    recommendations.add("Review spending in " + highestSpendingCategory + " category for optimization");
                }
            }
        }
        
        return recommendations.isEmpty() ? "Processes are running efficiently" : 
               String.join("; ", recommendations);
    }

    private String identifyBottleneck(List<PurchaseFundRecordModel> records) {
        long pendingApproval = records.stream().filter(r -> "Pending".equals(r.getApprovalStatus())).count();
        long pendingReceipt = records.stream().filter(r -> "Pending".equals(r.getRecieptUploadStatus())).count();
        
        if (pendingApproval > pendingReceipt) {
            return "Approval Stage";
        } else if (pendingReceipt > 0) {
            return "Receipt Upload Stage";
        } else {
            return "No significant bottlenecks";
        }
    }

    private List<String> generateRecommendations(List<PurchaseFundRecordModel> records) {
        List<String> recommendations = new ArrayList<>();
        
        if (calculateApprovalRate(records) < 80) {
            recommendations.add("Streamline approval workflow to reduce processing time");
        }
        
        if (calculateReceiptUploadRate(records) < 70) {
            recommendations.add("Implement reminders for receipt uploads");
        }
        
        long highValueCount = records.stream().filter(r -> r.getAmountRequested() > 10000).count();
        if (highValueCount > 5) {
            recommendations.add("Review high-value transactions for potential optimization");
        }

        // Category-based recommendations
        Map<String, Double> categorySpending = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(PurchaseFundRecordModel::getAmountRequested)
            ));

        if (!categorySpending.isEmpty()) {
            String highestSpendingCategory = categorySpending.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
            
            if (!highestSpendingCategory.isEmpty()) {
                double categoryTotal = categorySpending.get(highestSpendingCategory);
                double overallTotal = records.stream().mapToDouble(PurchaseFundRecordModel::getAmountRequested).sum();
                double percentage = overallTotal > 0 ? (categoryTotal / overallTotal * 100) : 0;
                
                if (percentage > 50) {
                    recommendations.add("Consider budget reallocation for " + highestSpendingCategory + " category");
                }
            }
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("Current processes are efficient. Maintain current standards.");
        }
        
        return recommendations;
    }

    // ==================== OTHER METHODS ====================
    private void resetFilters() {
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        unitField.clear();
        payeeField.clear();
        mainCategoryComboBox.setValue(null);
        subCategoryComboBox.setValue(null);
        subCategoryComboBox.setDisable(true);
        searchField.clear();
        approvalFilter.setValue("All");
        confirmationFilter.setValue("All");
        dispensedFilter.setValue("All");
        dispenseApprovalFilter.setValue("All");
        voidFilter.setValue("All");
        receiptUploadFilter.setValue("All");
        applyFiltersImmediate();
    }

    private void showAdvancedFilters() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Advanced Filters");
        dialog.setHeaderText("Set Advanced Filter Criteria");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField minAmountField = new TextField();
        minAmountField.setPromptText("Minimum Amount");
        TextField maxAmountField = new TextField();
        maxAmountField.setPromptText("Maximum Amount");
        ComboBox<String> departmentFilter = createComboBox("All", "ICU", "Emergency", "Surgery", "Pediatrics", "Radiology");

        grid.add(new Label("Min Amount:"), 0, 0);
        grid.add(minAmountField, 1, 0);
        grid.add(new Label("Max Amount:"), 0, 1);
        grid.add(maxAmountField, 1, 1);
        grid.add(new Label("Department:"), 0, 2);
        grid.add(departmentFilter, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.APPLY) {
                showAlert("Advanced Filters", "Advanced filters applied successfully!");
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void exportSelectedToExcel() {
        ObservableList<PurchaseFundRecordModel> selectedItems = table.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            showAlert("Export Error", "Please select records to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Selected Records to Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("Selected_ReceiptPurchaseFund_Records_" + LocalDate.now() + ".xlsx");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Selected Records");

                // Create header row
                Row headerRow = sheet.createRow(0);
                String[] headers = {"Request ID", "Unit", "Main Category", "Sub Category", "Payee", "Amount", "Date", "Status", "Receipt Upload"};
                for (int i = 0; i < headers.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    setCellStyle(cell, workbook, true, false, IndexedColors.GREY_25_PERCENT);
                }

                // Add data rows
                int rowNum = 1;
                for (PurchaseFundRecordModel record : selectedItems) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(record.getRequestId());
                    row.createCell(1).setCellValue(record.getRequisitionUnit());
                    row.createCell(2).setCellValue(record.getMainCategory());
                    row.createCell(3).setCellValue(record.getSubCategory());
                    row.createCell(4).setCellValue(record.getPayee());
                    row.createCell(5).setCellValue(record.getAmountRequested());
                    row.createCell(6).setCellValue(record.getRequestDate().toString());
                    row.createCell(7).setCellValue(record.getApprovalStatus());
                    row.createCell(8).setCellValue(record.getRecieptUploadStatus());
                }

                // Auto-size columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(new FileOutputStream(file));
                showAlert("Export Successful", selectedItems.size() + " records exported successfully!");
                openFile(file);
            } catch (Exception e) {
                showAlert("Export Error", "Failed to export selected records: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void copySelectedToClipboard() {
        ObservableList<PurchaseFundRecordModel> selectedItems = table.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            showAlert("Copy Error", "Please select records to copy.");
            return;
        }

        StringBuilder clipboardContent = new StringBuilder();
        
        // Create header
        clipboardContent.append("Request ID\tUnit\tMain Category\tSub Category\tPayee\tAmount\tDate\tStatus\tReceipt Upload\n");
        
        // Add data
        for (PurchaseFundRecordModel record : selectedItems) {
            clipboardContent.append(record.getRequestId()).append("\t")
                           .append(record.getRequisitionUnit()).append("\t")
                           .append(record.getMainCategory()).append("\t")
                           .append(record.getSubCategory()).append("\t")
                           .append(record.getPayee()).append("\t")
                           .append(String.format("%s %,.2f", CURRENCY_SYMBOL, record.getAmountRequested())).append("\t")
                           .append(record.getRequestDate()).append("\t")
                           .append(record.getApprovalStatus()).append("\t")
                           .append(record.getRecieptUploadStatus()).append("\n");
        }

        ClipboardContent content = new ClipboardContent();
        content.putString(clipboardContent.toString());
        Clipboard.getSystemClipboard().setContent(content);
        
        showAlert("Copy Successful", selectedItems.size() + " records copied to clipboard!");
    }

    private void showRecordDetails() {
        PurchaseFundRecordModel selectedRecord = table.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            showAlert("Details Error", "Please select a record to view details.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Record Details");
        dialog.setHeaderText("Detailed Information for Request: " + selectedRecord.getRequestId());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Add record details to grid
        addDetailRow(grid, "Request ID:", selectedRecord.getRequestId(), 0);
        addDetailRow(grid, "Requisition Unit:", selectedRecord.getRequisitionUnit(), 1);
        addDetailRow(grid, "Main Category:", selectedRecord.getMainCategory(), 2);
        addDetailRow(grid, "Sub Category:", selectedRecord.getSubCategory(), 3);
        addDetailRow(grid, "Payee:", selectedRecord.getPayee(), 4);
        addDetailRow(grid, "Amount Requested:", String.format("%s %,.2f", CURRENCY_SYMBOL, selectedRecord.getAmountRequested()), 5);
        addDetailRow(grid, "Request Date:", selectedRecord.getRequestDate().toString(), 6);
        addDetailRow(grid, "Approval Status:", selectedRecord.getApprovalStatus(), 7);
        addDetailRow(grid, "Approved By:", selectedRecord.getApprovedBy(), 8);
        addDetailRow(grid, "Confirmation Status:", selectedRecord.getConfirmationStatus(), 9);
        addDetailRow(grid, "Confirmed By:", selectedRecord.getConfirmedBy(), 10);
        addDetailRow(grid, "Dispensed Status:", selectedRecord.getDispensedStatus(), 11);
        addDetailRow(grid, "Dispensed By:", selectedRecord.getDispensedBy(), 12);
        addDetailRow(grid, "Dispense Approval:", selectedRecord.getDispenseApprovalStatus(), 13);
        addDetailRow(grid, "Dispense Approved By:", selectedRecord.getDispenseApprovedBy(), 14);
        addDetailRow(grid, "Receipt Upload:", selectedRecord.getRecieptUploadStatus(), 15);
        addDetailRow(grid, "Void Status:", selectedRecord.getVoidStatus(), 16);
        addDetailRow(grid, "Voided By:", selectedRecord.getVoidedBy(), 17);
        addDetailRow(grid, "Reason:", selectedRecord.getReason(), 18);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void addDetailRow(GridPane grid, String label, String value, int row) {
        Label detailLabel = new Label(label);
        detailLabel.setStyle("-fx-font-weight: bold;");
        
        Label valueLabel = new Label(value != null ? value : "N/A");
        
        grid.add(detailLabel, 0, row);
        grid.add(valueLabel, 1, row);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openFile(File file) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception e) {
                System.err.println("Failed to open file: " + e.getMessage());
            }
        }
    }

    private void refreshData() {
        Connecting db = new Connecting();
        List<PurchaseFundRecordModel> updatedData = db.getAllPurchaseFundRequests();
        this.allRecords.setAll(updatedData);
        applyFiltersImmediate();
        showAlert("Data Refreshed", "Dashboard data has been updated with " + updatedData.size() + " records.");
    }

    private void toggleDarkMode() {
        if (darkModeToggle.isSelected()) {
            setStyle("-fx-background-color: #2c3e50;");
            // Apply dark theme to all components
        } else {
            setStyle("-fx-background-color: #f8f9fa;");
            // Apply light theme to all components
        }
    }

    // Allow reloading data later
    public void setRecords(List<PurchaseFundRecordModel> records) {
        allRecords.clear();
        if (records != null) allRecords.addAll(records);
        applyFiltersImmediate();
    }

    // Apply filters method for backward compatibility
    private void applyFilters() {
        debouncedApplyFilters();
    }
}