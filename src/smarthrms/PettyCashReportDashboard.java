package smarthrms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.chart.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.binding.Bindings;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
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
import javafx.print.PrinterJob;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import java.awt.Desktop;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;
import javafx.application.Platform;


public class PettyCashReportDashboard extends BorderPane {

    // Filters
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private TextField unitField;
    private TextField payeeField;
    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> subCategoryFilter;
    private ComboBox<String> confirmationFilter;
    private ComboBox<String> approvalFilter;
    private ComboBox<String> dispensedFilter;
    private ComboBox<String> dispenseApprovalFilter;
    private ComboBox<String> voidFilter;

    private TableView<PettyCashRecordModel> table;
    private ObservableList<PettyCashRecordModel> allRecords;

    // Charts
    private PieChart statusPieChart;
    private PieChart categoryPieChart;
    private BarChart<String, Number> monthlyBarChart;
    private LineChart<String, Number> trendLineChart;
    private BarChart<String, Number> unitBarChart;
    private BarChart<String, Number> categoryBarChart;

    // Summary labels
    private Label totalRequestsLbl;
    private Label totalApprovedLbl;
    private Label totalDispensedLbl;
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
    private Tab tableTab, pieChartTab, barChartTab, lineChartTab, unitChartTab, categoryChartTab, analyticsTab;
    private String currentUser;
    private Connecting databaseConnector;
    boolean isAdmin=false;
    boolean isCashier=false;
    boolean isFinanceAdmin=false;
    boolean isAccountant=false;
    boolean isReplenishDispenser=false;

   public PettyCashReportDashboard(List<PettyCashRecordModel> records, String username) {
    // Ensure we have a non-null list and handle potential null records
    this.allRecords = FXCollections.observableArrayList(
        records != null ? records.stream()
            .filter(Objects::nonNull) // Filter out null records
            .collect(Collectors.toList()) 
        : new ArrayList<>()
    );
    this.currentUser = username;
    this.databaseConnector = new Connecting();
    
    
        isAdmin=databaseConnector.isAdmin(currentUser.toLowerCase());
        isCashier=databaseConnector.isCashier(currentUser.toLowerCase());
        isAccountant=databaseConnector.isAccountant(currentUser.toLowerCase());
        isFinanceAdmin=databaseConnector.isFinanceAdmin(currentUser.toLowerCase());
        isReplenishDispenser=databaseConnector.isReplenishDispenser(currentUser.toLowerCase());
        
        
    initializeUI();
    setupEventHandlers();
    
    // Delay filters until UI is fully initialized
    Platform.runLater(() -> {
        populateCategoryFilters();
        applyFilters();
    });
}

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
        Label title = new Label("Petty Cash Analytics Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Comprehensive Petty Cash Management and Reporting");
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
        exportExcelBtn = createStyledButton("Export Excel", "#27ae60");
        exportPdfBtn = createStyledButton("Export PDF", "#e74c3c");
        exportWordBtn = createStyledButton("Export Word", "#3498db");
        printBtn = createStyledButton("Print Report", "#9b59b6");
        refreshBtn = createStyledButton("Refresh Data", "#f39c12");
        
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

        searchField = new TextField();
        searchField.setPromptText("Search across all fields...");

        // NEW: Category filters
        categoryFilter = createComboBox("All Categories");
        subCategoryFilter = createComboBox("All Sub-Categories");

        confirmationFilter = createComboBox("All", "Pending", "Confirmed");
        approvalFilter = createComboBox("All", "Pending", "Approved");
        dispensedFilter = createComboBox("All", "Yes", "No");
        dispenseApprovalFilter = createComboBox("All", "Pending", "Approved");
        voidFilter = createComboBox("All", "Yes", "No");

        Button resetBtn = createStyledButton("Reset Filters", "#95a5a6");
        Button advancedFilterBtn = createStyledButton("Advanced Filters", "#16a085");

        resetBtn.setOnAction(e -> resetFilters());
        advancedFilterBtn.setOnAction(e -> showAdvancedFilters());

        // Populate category filters
        populateCategoryFilters();

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
        grid.add(new Label("Category:"), 4, 1);
        grid.add(categoryFilter, 5, 1);

        // Row 2
        grid.add(new Label("Sub-Category:"), 0, 2);
        grid.add(subCategoryFilter, 1, 2);
        grid.add(new Label("Approval:"), 2, 2);
        grid.add(approvalFilter, 3, 2);
        grid.add(new Label("Confirmed:"), 4, 2);
        grid.add(confirmationFilter, 5, 2);

        // Row 3
        grid.add(new Label("Dispensed:"), 0, 3);
        grid.add(dispensedFilter, 1, 3);
        grid.add(new Label("Dispense Approval:"), 2, 3);
        grid.add(dispenseApprovalFilter, 3, 3);
        grid.add(new Label("Void:"), 4, 3);
        grid.add(voidFilter, 5, 3);

        // Row 4
        grid.add(resetBtn, 4, 4);
        grid.add(advancedFilterBtn, 5, 4);

        VBox filterSection = new VBox(grid);
        filterSection.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 10;");
        
        return filterSection;
    }

private void populateCategoryFilters() {
    // Extract unique categories and sub-categories from records - FIXED: Handle null values
    Set<String> categories = allRecords.stream()
        .map(PettyCashRecordModel::getMainCategory)
        .filter(cat -> cat != null && !cat.trim().isEmpty())
        .collect(Collectors.toSet());
    
    Set<String> subCategories = allRecords.stream()
        .map(PettyCashRecordModel::getSubCategory)
        .filter(sub -> sub != null && !sub.trim().isEmpty())
        .collect(Collectors.toSet());

    categoryFilter.getItems().clear();
    categoryFilter.getItems().addAll("All Categories");
    categoryFilter.getItems().addAll(categories);
    
    subCategoryFilter.getItems().clear();
    subCategoryFilter.getItems().addAll("All Sub-Categories");
    subCategoryFilter.getItems().addAll(subCategories);
}

    private ComboBox<String> createComboBox(String... items) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(items);
        cb.setValue(items[0]);
        cb.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7;");
        cb.setPrefWidth(140);
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
        unitChartTab = new Tab("Unit Analysis", createUnitChart());
        categoryChartTab = new Tab("Category Analysis", createCategoryChart());
        analyticsTab = new Tab("Advanced Analytics", createAnalyticsSection());
        
        tabPane.getTabs().addAll(tableTab, pieChartTab, barChartTab, lineChartTab, unitChartTab, categoryChartTab, analyticsTab);
        tabPane.setStyle("-fx-background-color: white;");
        
        return tabPane;
    }

    private VBox createTableSection() {
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        // Define columns with better formatting including new category fields
        TableColumn<PettyCashRecordModel, String> idCol = new TableColumn<>("Request ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRequestId()));

        TableColumn<PettyCashRecordModel, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRequisitionUnit()));

        // NEW: Category columns
        TableColumn<PettyCashRecordModel, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(
        data.getValue().getMainCategory() != null ? data.getValue().getMainCategory() : ""
        ));

        TableColumn<PettyCashRecordModel, String> subCategoryCol = new TableColumn<>("Sub-Category");
        subCategoryCol.setCellValueFactory(data -> new SimpleStringProperty(
        data.getValue().getSubCategory() != null ? data.getValue().getSubCategory() : ""
        ));

        TableColumn<PettyCashRecordModel, String> payeeCol = new TableColumn<>("Payee");
        payeeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPayee()));

        TableColumn<PettyCashRecordModel, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReason()));

        TableColumn<PettyCashRecordModel, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> new SimpleStringProperty(
            String.format("ETB %,.2f", data.getValue().getAmountRequested())
        ));

        TableColumn<PettyCashRecordModel, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getRequestDate().toString()
        ));

        TableColumn<PettyCashRecordModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApprovalStatus()));
        statusCol.setCellFactory(column -> new TableCell<PettyCashRecordModel, String>() {
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

        TableColumn<PettyCashRecordModel, String> dispensedCol = new TableColumn<>("Dispensed");
        dispensedCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDispensedStatus()));
        dispensedCol.setCellFactory(column -> new TableCell<PettyCashRecordModel, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("yes".equalsIgnoreCase(item)) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                    }
                }
            }
        });

        table.getColumns().addAll(idCol, unitCol, categoryCol, subCategoryCol, payeeCol, reasonCol, amountCol, dateCol, statusCol, dispensedCol);

        // Context Menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem exportItem = new MenuItem("Export Selected");
        MenuItem copyItem = new MenuItem("Copy to Clipboard");
        MenuItem detailsItem = new MenuItem("View Details");
        MenuItem printItem = new MenuItem("Print Details");
        
        exportItem.setOnAction(e -> exportSelectedToExcel());
        copyItem.setOnAction(e -> copySelectedToClipboard());
        detailsItem.setOnAction(e -> showRecordDetails());
        printItem.setOnAction(e -> printSelectedDetails());
        
        contextMenu.getItems().addAll(exportItem, copyItem, detailsItem, printItem);
        table.setContextMenu(contextMenu);

        VBox tableSection = new VBox(10, createTableToolbar(), table);
        tableSection.setPadding(new Insets(10));
        
        return tableSection;
    }

    private HBox createTableToolbar() {
        Label countLabel = new Label();
        
        // Fixed binding for Java 8
        countLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            ObservableList<PettyCashRecordModel> items = table.getItems();
            int size = items != null ? items.size() : 0;
            return "Showing " + size + " record" + (size == 1 ? "" : "s");
        }, table.itemsProperty()));

        Button copyBtn = createStyledButton("Copy Selected", "#3498db");
        Button exportSelectionBtn = createStyledButton("Export Selection", "#27ae60");
        Button printSelectionBtn = createStyledButton("Print Selection", "#9b59b6");
        
        copyBtn.setOnAction(e -> copySelectedToClipboard());
        exportSelectionBtn.setOnAction(e -> exportSelectedToExcel());
        printSelectionBtn.setOnAction(e -> printSelectedDetails());

        HBox toolbar = new HBox(10, countLabel, copyBtn, exportSelectionBtn, printSelectionBtn);
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
        monthlyBarChart.setTitle("Monthly Petty Cash Analysis");
        monthlyBarChart.setStyle("-fx-background-color: white;");
        return monthlyBarChart;
    }

    private LineChart<String, Number> createLineChart() {
        CategoryAxis xAxisLine = new CategoryAxis();
        NumberAxis yAxisLine = new NumberAxis();
        trendLineChart = new LineChart<>(xAxisLine, yAxisLine);
        trendLineChart.setTitle("Petty Cash Trends Over Time");
        trendLineChart.setStyle("-fx-background-color: white;");
        return trendLineChart;
    }

    private BarChart<String, Number> createUnitChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        unitBarChart = new BarChart<>(xAxis, yAxis);
        unitBarChart.setTitle("Petty Cash by Requisition Unit");
        unitBarChart.setStyle("-fx-background-color: white;");
        return unitBarChart;
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
        analyticsGrid.add(createMetricCard("Approval Rate", "85%", "#27ae60"), 0, 0);
        analyticsGrid.add(createMetricCard("Dispensed Rate", "72%", "#3498db"), 1, 0);
        analyticsGrid.add(createMetricCard("Avg Processing Time", "1.5 days", "#e67e22"), 2, 0);
        analyticsGrid.add(createMetricCard("Pending Actions", "8", "#e74c3c"), 3, 0);

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
        totalRequestsLbl = createSummaryLabel("Total Requests\n0", "#3498db");
        totalApprovedLbl = createSummaryLabel("Total Approved\n0", "#27ae60");
        totalDispensedLbl = createSummaryLabel("Total Dispensed\n0", "#e67e22");
        totalAmountLbl = createSummaryLabel("Total Amount\nETB 0.00", "#9b59b6");
        avgAmountLbl = createSummaryLabel("Average Amount\nETB 0.00", "#1abc9c");
        highestAmountLbl = createSummaryLabel("Highest Amount\nETB 0.00", "#e74c3c");
        pendingApprovalLbl = createSummaryLabel("Pending Approval\n0", "#f39c12");
        completionRateLbl = createSummaryLabel("Completion Rate\n0%", "#34495e");
        topCategoryLbl = createSummaryLabel("Top Category\n-", "#8e44ad");

        HBox summaryBox = new HBox(10, totalRequestsLbl, totalApprovedLbl, totalDispensedLbl, 
                                 totalAmountLbl, avgAmountLbl, highestAmountLbl, 
                                 pendingApprovalLbl, completionRateLbl, topCategoryLbl);
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

    private void setupEventHandlers() {
        // Filter listeners
        fromDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        toDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        unitField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        payeeField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        subCategoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        approvalFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        confirmationFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        dispensedFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        dispenseApprovalFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        voidFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Button actions
        exportExcelBtn.setOnAction(e -> exportToExcel());
        exportPdfBtn.setOnAction(e -> exportToPDF());
        exportWordBtn.setOnAction(e -> exportToWord());
        printBtn.setOnAction(e -> printReport());
        refreshBtn.setOnAction(e -> refreshData());
        darkModeToggle.setOnAction(e -> toggleDarkMode());
    }

    // ---------------------------
    // FILTER LOGIC
    // ---------------------------
private void applyFilters() {
    final String unitText = unitField.getText() == null ? "" : unitField.getText().trim().toLowerCase();
    final String payeeText = payeeField.getText() == null ? "" : payeeField.getText().trim().toLowerCase();
    final String searchText = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
    final String categoryValue = categoryFilter.getValue();
    final String subCategoryValue = subCategoryFilter.getValue();

    LocalDate from = fromDatePicker.getValue();
    LocalDate to = toDatePicker.getValue();

    List<PettyCashRecordModel> filtered = allRecords.stream()
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
        .filter(r -> searchText.isEmpty() || matchesSearch(r, searchText))
        // FIXED: Handle null categories in filtering
        .filter(r -> categoryValue == null || categoryValue.equals("All Categories") || 
            (r.getMainCategory() != null && r.getMainCategory().equals(categoryValue)))
        // FIXED: Handle null sub-categories in filtering
        .filter(r -> subCategoryValue == null || subCategoryValue.equals("All Sub-Categories") || 
            (r.getSubCategory() != null && r.getSubCategory().equals(subCategoryValue)))
        .filter(r -> matches(confirmationFilter.getValue(), r.getConfirmationStatus()))
        .filter(r -> matches(approvalFilter.getValue(), r.getApprovalStatus()))
        .filter(r -> matches(dispensedFilter.getValue(), r.getDispensedStatus()))
        .filter(r -> matches(dispenseApprovalFilter.getValue(), r.getDispenseApprovalStatus()))
        .filter(r -> matches(voidFilter.getValue(), r.getVoidStatus()))
        .collect(Collectors.toList());

    table.setItems(FXCollections.observableArrayList(filtered));
    updateCharts(filtered);
    updateSummary(filtered);
}

    private boolean matchesSearch(PettyCashRecordModel record, String searchText) {
        return (record.getRequestId() != null && record.getRequestId().toLowerCase().contains(searchText)) ||
               (record.getRequisitionUnit() != null && record.getRequisitionUnit().toLowerCase().contains(searchText)) ||
               (record.getMainCategory() != null && record.getMainCategory().toLowerCase().contains(searchText)) ||
               (record.getSubCategory() != null && record.getSubCategory().toLowerCase().contains(searchText)) ||
               (record.getPayee() != null && record.getPayee().toLowerCase().contains(searchText)) ||
               (record.getReason() != null && record.getReason().toLowerCase().contains(searchText));
    }

    private boolean matches(String filterValue, String dataValue) {
        if (filterValue == null || filterValue.equals("All")) return true;
        if (dataValue == null) dataValue = "";
        return filterValue.equalsIgnoreCase(dataValue);
    }

    private void updateCharts(List<PettyCashRecordModel> data) {
        updatePieChart(data);
        updateCategoryCharts(data);
        updateBarChart(data);
        updateLineChart(data);
        updateUnitChart(data);
    }

    private void updatePieChart(List<PettyCashRecordModel> data) {
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

private void updateCategoryCharts(List<PettyCashRecordModel> data) {
    // Category Pie Chart - FIXED: Handle null categories
    Map<String, Long> categoryCounts = data.stream()
            .collect(Collectors.groupingBy(
                record -> record.getMainCategory() != null && !record.getMainCategory().isEmpty() ? 
                         record.getMainCategory() : "Uncategorized",
                Collectors.counting()
            ));

    ObservableList<PieChart.Data> categoryPieData = FXCollections.observableArrayList();
    categoryCounts.forEach((category, count) -> 
        categoryPieData.add(new PieChart.Data(category + " (" + count + ")", count))
    );
    categoryPieChart.setData(categoryPieData);

    // Category Bar Chart - FIXED: Handle null categories
    Map<String, Double> categoryAmounts = data.stream()
            .collect(Collectors.groupingBy(
                record -> record.getMainCategory() != null && !record.getMainCategory().isEmpty() ? 
                         record.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(PettyCashRecordModel::getAmountRequested)
            ));

    XYChart.Series<String, Number> categorySeries = new XYChart.Series<>();
    categorySeries.setName("Category Totals");
    
    categoryAmounts.forEach((category, total) -> 
        categorySeries.getData().add(new XYChart.Data<>(category, total))
    );

    categoryBarChart.getData().clear();
    categoryBarChart.getData().add(categorySeries);
}

    private void updateBarChart(List<PettyCashRecordModel> data) {
        Map<String, Double> monthlyTotals = data.stream()
                .collect(Collectors.groupingBy(
                    record -> record.getRequestDate().getMonth().toString(),
                    Collectors.summingDouble(PettyCashRecordModel::getAmountRequested)
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Totals");
        
        monthlyTotals.forEach((month, total) -> 
            series.getData().add(new XYChart.Data<>(month, total))
        );

        monthlyBarChart.getData().clear();
        monthlyBarChart.getData().add(series);
    }

    private void updateLineChart(List<PettyCashRecordModel> data) {
        Map<LocalDate, Double> dailyTotals = data.stream()
                .collect(Collectors.groupingBy(
                    PettyCashRecordModel::getRequestDate,
                    Collectors.summingDouble(PettyCashRecordModel::getAmountRequested)
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

    private void updateUnitChart(List<PettyCashRecordModel> data) {
        Map<String, Double> unitTotals = data.stream()
                .collect(Collectors.groupingBy(
                    PettyCashRecordModel::getRequisitionUnit,
                    Collectors.summingDouble(PettyCashRecordModel::getAmountRequested)
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Unit Totals");
        
        unitTotals.forEach((unit, total) -> 
            series.getData().add(new XYChart.Data<>(unit, total))
        );

        unitBarChart.getData().clear();
        unitBarChart.getData().add(series);
    }

private void updateSummary(List<PettyCashRecordModel> data) {
    long totalRequests = data.size();
    long approved = data.stream().filter(r -> "Approved".equals(r.getApprovalStatus())).count();
    long dispensed = data.stream().filter(r -> "Yes".equals(r.getDispensedStatus())).count();
    double totalAmount = data.stream().mapToDouble(PettyCashRecordModel::getAmountRequested).sum();
    double avgAmount = totalRequests > 0 ? totalAmount / totalRequests : 0;
    double highestAmount = data.stream().mapToDouble(PettyCashRecordModel::getAmountRequested).max().orElse(0);
    long pending = data.stream().filter(r -> "Pending".equals(r.getApprovalStatus())).count();
    double completionRate = totalRequests > 0 ? (double) dispensed / totalRequests * 100 : 0;
    
    // FIXED: Top category calculation - handle null categories
    String topCategory = data.stream()
        .collect(Collectors.groupingBy(
            r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                 r.getMainCategory() : "Uncategorized",
            Collectors.summingDouble(PettyCashRecordModel::getAmountRequested)
        ))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(entry -> entry.getKey() + " (ETB " + String.format("%,.2f", entry.getValue()) + ")")
        .orElse("-");

    totalRequestsLbl.setText("Total Requests\n" + totalRequests);
    totalApprovedLbl.setText("Total Approved\n" + approved);
    totalDispensedLbl.setText("Total Dispensed\n" + dispensed);
    totalAmountLbl.setText("Total Amount\nETB " + String.format("%,.2f", totalAmount));
    avgAmountLbl.setText("Average Amount\nETB " + String.format("%,.2f", avgAmount));
    highestAmountLbl.setText("Highest Amount\nETB " + String.format("%,.2f", highestAmount));
    pendingApprovalLbl.setText("Pending Approval\n" + pending);
    completionRateLbl.setText("Completion Rate\n" + String.format("%.1f%%", completionRate));
    topCategoryLbl.setText("Top Category\n" + topCategory);
}

    // ==================== ENHANCED PRINT AND EXPORT METHODS ====================

    private void printSelectedDetails() {
        ObservableList<PettyCashRecordModel> selectedItems = table.getSelectionModel().getSelectedItems();
        List<PettyCashRecordModel> recordsToPrint;
        
        if (selectedItems.isEmpty()) {
            // If nothing selected, ask user if they want to print all records
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Print Records");
            confirmation.setHeaderText("No records selected");
            confirmation.setContentText("Do you want to print all " + table.getItems().size() + " records?");
            
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                recordsToPrint = new ArrayList<>(table.getItems());
            } else {
                return;
            }
        } else {
            recordsToPrint = new ArrayList<>(selectedItems);
        }
        
        printDetailedRecords(recordsToPrint);
    }

    private void printDetailedRecords(List<PettyCashRecordModel> records) {
        try {
            VBox printNode = createDetailedPrintNode(records);
            
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(null)) {
                boolean success = job.printPage(printNode);
                if (success) {
                    job.endJob();
                    showAlert("Print Successful", 
                        records.size() + " detailed records sent to printer successfully!");
                } else {
                    showAlert("Print Error", "Failed to print the detailed records.");
                }
            } else {
                showAlert("Print Error", "Could not create print job or printing was cancelled.");
            }
        } catch (Exception e) {
            showAlert("Print Error", "Printing failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createDetailedPrintNode(List<PettyCashRecordModel> records) {
        VBox printNode = new VBox(20);
        printNode.setPadding(new Insets(40));
        printNode.setStyle("-fx-background-color: white;");

        // Header
        VBox header = createPrintHeader(records.size());
        
        // Records
        VBox recordsSection = createPrintRecordsSection(records);
        
        // Footer
        VBox footer = createPrintFooter();

        printNode.getChildren().addAll(header, recordsSection, footer);
        return printNode;
    }

    private VBox createPrintHeader(int recordCount) {
        VBox header = new VBox(10);
        header.setPadding(new Insets(0, 0, 20, 0));
        header.setAlignment(Pos.CENTER);

        Label title = new Label("AFRAN GENERAL HOSPITAL");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Label subtitle = new Label("PETTY CASH RECORDS - DETAILED REPORT");
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        subtitle.setStyle("-fx-text-fill: #34495e;");

        Label info = new Label("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) + 
                             " | Total Records: " + recordCount);
        info.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        info.setStyle("-fx-text-fill: #7f8c8d;");

        header.getChildren().addAll(title, subtitle, info);
        return header;
    }

    private VBox createPrintRecordsSection(List<PettyCashRecordModel> records) {
        VBox recordsSection = new VBox(15);
        
        for (int i = 0; i < records.size(); i++) {
            PettyCashRecordModel record = records.get(i);
            VBox recordCard = createIndividualRecordCard(record, i + 1);
            recordsSection.getChildren().add(recordCard);
            
            // Add page break after every 3 records for better readability
            if ((i + 1) % 3 == 0 && i < records.size() - 1) {
                Region pageBreak = new Region();
                pageBreak.setPrefHeight(20);
                pageBreak.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");
                recordsSection.getChildren().add(pageBreak);
            }
        }
        
        return recordsSection;
    }

    private VBox createIndividualRecordCard(PettyCashRecordModel record, int recordNumber) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8;");

        // Record header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label recordNum = new Label("Record #" + recordNumber);
        recordNum.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        recordNum.setStyle("-fx-text-fill: #2c3e50;");
        
        Label requestId = new Label("ID: " + record.getRequestId());
        requestId.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        requestId.setStyle("-fx-text-fill: #7f8c8d;");
        
        HBox.setHgrow(requestId, Priority.ALWAYS);
        header.getChildren().addAll(recordNum, requestId);

        // Record details in grid
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(8);
        detailsGrid.setPadding(new Insets(10, 0, 0, 0));

        // Column 1
        addDetailRow(detailsGrid, "Requisition Unit:", record.getRequisitionUnit(), 0, 0);
        addDetailRow(detailsGrid, "Main Category:", record.getMainCategory(), 0, 1);
        addDetailRow(detailsGrid, "Sub-Category:", record.getSubCategory(), 0, 2);
        addDetailRow(detailsGrid, "Payee:", record.getPayee(), 0, 3);
        addDetailRow(detailsGrid, "Reason:", record.getReason(), 0, 4);

        // Column 2
        addDetailRow(detailsGrid, "Amount:", String.format("ETB %,.2f", record.getAmountRequested()), 1, 0);
        addDetailRow(detailsGrid, "Request Date:", record.getRequestDate().toString(), 1, 1);
        addDetailRow(detailsGrid, "Approval Status:", record.getApprovalStatus(), 1, 2);
        addDetailRow(detailsGrid, "Confirmed:", record.getConfirmationStatus(), 1, 3);
        addDetailRow(detailsGrid, "Dispensed:", record.getDispensedStatus(), 1, 4);

        // Column 3
        addDetailRow(detailsGrid, "Approved By:", record.getApprovedBy(), 2, 0);
        addDetailRow(detailsGrid, "Confirmed By:", record.getConfirmedBy(), 2, 1);
        addDetailRow(detailsGrid, "Dispensed By:", record.getDispensedBy(), 2, 2);
        addDetailRow(detailsGrid, "Dispense Approval:", record.getDispenseApprovalStatus(), 2, 3);
        addDetailRow(detailsGrid, "Void Status:", record.getVoidStatus(), 2, 4);

        card.getChildren().addAll(header, detailsGrid);
        return card;
    }

    private void addDetailRow(GridPane grid, String label, String value, int column, int row) {
        Label detailLabel = new Label(label);
        detailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        detailLabel.setStyle("-fx-text-fill: #495057;");

        Label detailValue = new Label(value != null ? value : "N/A");
        detailValue.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        detailValue.setStyle("-fx-text-fill: #6c757d;");
        detailValue.setWrapText(true);

        grid.add(detailLabel, column * 2, row);
        grid.add(detailValue, column * 2 + 1, row);
    }

    private VBox createPrintFooter() {
        VBox footer = new VBox(10);
        footer.setPadding(new Insets(20, 0, 0, 0));
        footer.setAlignment(Pos.CENTER);
        
        Separator separator = new Separator();
        
        Label generatedBy = new Label("Generated by: " + currentUser);
        generatedBy.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        generatedBy.setStyle("-fx-text-fill: #666;");
        
        Label timestamp = new Label("Printed on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        timestamp.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        timestamp.setStyle("-fx-text-fill: #666;");
        
        Label confidential = new Label("Confidential - AFRAN General Hospital Internal Use Only");
        confidential.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        confidential.setStyle("-fx-text-fill: #999;");
        
        footer.getChildren().addAll(separator, generatedBy, timestamp, confidential);
        return footer;
    }

    // ==================== DETAILED EXPORT METHODS ====================

    private void exportSelectedToExcel() {
        ObservableList<PettyCashRecordModel> selectedItems = table.getSelectionModel().getSelectedItems();
        List<PettyCashRecordModel> recordsToExport;
        
        if (selectedItems.isEmpty()) {
            // If nothing selected, ask user if they want to export all records
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Export Records");
            confirmation.setHeaderText("No records selected");
            confirmation.setContentText("Do you want to export all " + table.getItems().size() + " records to Excel?");
            
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                recordsToExport = new ArrayList<>(table.getItems());
            } else {
                return;
            }
        } else {
            recordsToExport = new ArrayList<>(selectedItems);
        }
        
        exportDetailedRecordsToExcel(recordsToExport);
    }

    private void exportDetailedRecordsToExcel(List<PettyCashRecordModel> records) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Detailed Records to Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("PettyCash_Detailed_Records_" + LocalDate.now() + ".xlsx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                createDetailedExcelReport(workbook, records);
                workbook.write(new FileOutputStream(file));
                showAlert("Export Successful", 
                    records.size() + " detailed records exported to Excel successfully!");
                openFile(file);
            } catch (Exception e) {
                showAlert("Export Error", "Failed to export to Excel: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createDetailedExcelReport(Workbook workbook, List<PettyCashRecordModel> records) {
        // Summary Sheet
        createSummarySheet(workbook, records);
        
        // Detailed Records Sheet
        createDetailedRecordsSheet(workbook, records);
        
        // Individual Records Sheets (for better readability)
        createIndividualRecordsSheets(workbook, records);
    }

    private void createSummarySheet(Workbook workbook, List<PettyCashRecordModel> records) {
        Sheet sheet = workbook.createSheet("Report Summary");
        
        // Header
        Row headerRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("AFRAN GENERAL HOSPITAL - DETAILED PETTY CASH RECORDS");
        setCellStyle(headerCell, workbook, true, true, IndexedColors.DARK_BLUE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

        // Report Info
        Row infoRow1 = sheet.createRow(2);
        infoRow1.createCell(0).setCellValue("Report Date: " + LocalDate.now());
        infoRow1.createCell(3).setCellValue("Total Records: " + records.size());

        Row infoRow2 = sheet.createRow(3);
        infoRow2.createCell(0).setCellValue("Generated by: " + currentUser);
        infoRow2.createCell(3).setCellValue("Date Range: " + getDateRange(records));

        // Summary Statistics
        Row statsTitle = sheet.createRow(5);
        statsTitle.createCell(0).setCellValue("SUMMARY STATISTICS");
        setCellStyle(statsTitle.getCell(0), workbook, true, false, IndexedColors.GREY_25_PERCENT);

        String[][] stats = {
            {"Total Amount", String.format("ETB %,.2f", records.stream().mapToDouble(PettyCashRecordModel::getAmountRequested).sum())},
            {"Average Amount", String.format("ETB %,.2f", calculateAverageAmount(records))},
            {"Highest Amount", String.format("ETB %,.2f", records.stream().mapToDouble(PettyCashRecordModel::getAmountRequested).max().orElse(0))},
            {"Approval Rate", String.format("%.1f%%", calculateApprovalRate(records))},
            {"Completion Rate", String.format("%.1f%%", calculateCompletionRate(records))}
        };

        int row = 6;
        for (String[] stat : stats) {
            Row statRow = sheet.createRow(row++);
            statRow.createCell(0).setCellValue(stat[0]);
            statRow.createCell(1).setCellValue(stat[1]);
        }

        // Auto-size columns
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDetailedRecordsSheet(Workbook workbook, List<PettyCashRecordModel> records) {
        Sheet sheet = workbook.createSheet("Detailed Records");
        
        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "Request ID", "Requisition Unit", "Main Category", "Sub Category", 
            "Payee", "Reason", "Amount", "Request Date", 
            "Approval Status", "Approved By", "Confirmation Status", 
            "Confirmed By", "Dispensed Status", "Dispensed By", 
            "Dispense Approval", "Dispense Approved By", "Void Status", "Voided By"
        };
        
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            setCellStyle(cell, workbook, true, false, IndexedColors.GREY_25_PERCENT);
        }

        // Data rows
        int rowNum = 1;
        for (PettyCashRecordModel record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getRequestId());
            row.createCell(1).setCellValue(record.getRequisitionUnit());
            row.createCell(2).setCellValue(record.getMainCategory());
            row.createCell(3).setCellValue(record.getSubCategory());
            row.createCell(4).setCellValue(record.getPayee());
            row.createCell(5).setCellValue(record.getReason());
            row.createCell(6).setCellValue(record.getAmountRequested());
            row.createCell(7).setCellValue(record.getRequestDate().toString());
            row.createCell(8).setCellValue(record.getApprovalStatus());
            row.createCell(9).setCellValue(record.getApprovedBy());
            row.createCell(10).setCellValue(record.getConfirmationStatus());
            row.createCell(11).setCellValue(record.getConfirmedBy());
            row.createCell(12).setCellValue(record.getDispensedStatus());
            row.createCell(13).setCellValue(record.getDispensedBy());
            row.createCell(14).setCellValue(record.getDispenseApprovalStatus());
            row.createCell(15).setCellValue(record.getDispenseApprovedBy());
            row.createCell(16).setCellValue(record.getVoidStatus());
            row.createCell(17).setCellValue(record.getVoidedBy());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createIndividualRecordsSheets(Workbook workbook, List<PettyCashRecordModel> records) {
        // Create individual sheets for better readability (max 10 records per sheet)
        int recordsPerSheet = 10;
        int sheetCount = (int) Math.ceil((double) records.size() / recordsPerSheet);
        
        for (int sheetIndex = 0; sheetIndex < sheetCount; sheetIndex++) {
            int startIndex = sheetIndex * recordsPerSheet;
            int endIndex = Math.min(startIndex + recordsPerSheet, records.size());
            List<PettyCashRecordModel> sheetRecords = records.subList(startIndex, endIndex);
            
            Sheet sheet = workbook.createSheet("Records " + (startIndex + 1) + "-" + endIndex);
            createIndividualRecordSheet(sheet, sheetRecords, startIndex + 1);
        }
    }

    private void createIndividualRecordSheet(Sheet sheet, List<PettyCashRecordModel> records, int startNumber) {
        int rowNum = 0;
        
        for (int i = 0; i < records.size(); i++) {
            PettyCashRecordModel record = records.get(i);
            
            // Record header
            Row headerRow = sheet.createRow(rowNum++);
            org.apache.poi.ss.usermodel.Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("RECORD #" + (startNumber + i) + " - " + record.getRequestId());
            setCellStyle(headerCell, sheet.getWorkbook(), true, false, IndexedColors.LIGHT_BLUE);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum-1, rowNum-1, 0, 3));
            
            // Record details
            String[][] details = {
                {"Requisition Unit", record.getRequisitionUnit()},
                {"Main Category", record.getMainCategory()},
                {"Sub-Category", record.getSubCategory()},
                {"Payee", record.getPayee()},
                {"Reason", record.getReason()},
                {"Amount", String.format("ETB %,.2f", record.getAmountRequested())},
                {"Request Date", record.getRequestDate().toString()},
                {"Approval Status", record.getApprovalStatus()},
                {"Approved By", record.getApprovedBy()},
                {"Confirmation Status", record.getConfirmationStatus()},
                {"Confirmed By", record.getConfirmedBy()},
                {"Dispensed Status", record.getDispensedStatus()},
                {"Dispensed By", record.getDispensedBy()},
                {"Dispense Approval", record.getDispenseApprovalStatus()},
                {"Dispense Approved By", record.getDispenseApprovedBy()},
                {"Void Status", record.getVoidStatus()},
                {"Voided By", record.getVoidedBy()}
            };
            
            for (String[] detail : details) {
                Row detailRow = sheet.createRow(rowNum++);
                detailRow.createCell(0).setCellValue(detail[0] + ":");
                setCellStyle(detailRow.getCell(0), sheet.getWorkbook(), true, false, null);
                detailRow.createCell(1).setCellValue(detail[1] != null ? detail[1] : "N/A");
                
                if (rowNum < details.length + 2) {
                    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum-1, rowNum-1, 1, 3));
                }
            }
            
            // Add empty row between records
            rowNum++;
        }
        
        // Auto-size columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    
    // ==================== EXCEL EXPORT METHODS ====================

private void exportToExcel() {
    List<PettyCashRecordModel> recordsToExport = new ArrayList<>(table.getItems());
    
    if (recordsToExport.isEmpty()) {
        showAlert("No Data", "There are no records to export.");
        return;
    }
    
    exportDetailedRecordsToExcel(recordsToExport);
}

    // ==================== ENHANCED EXPORT TO PDF ====================

    private void exportToPDF() {
        List<PettyCashRecordModel> recordsToExport = new ArrayList<>(table.getItems());
        exportDetailedRecordsToPDF(recordsToExport);
    }

    private void exportDetailedRecordsToPDF(List<PettyCashRecordModel> records) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Detailed Records to PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("PettyCash_Detailed_Records_" + LocalDate.now() + ".pdf");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                createDetailedPDFReport(file, records);
                showAlert("Export Successful", 
                    records.size() + " detailed records exported to PDF successfully!");
                openFile(file);
            } catch (Exception e) {
                showAlert("Export Error", "Failed to export to PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createDetailedPDFReport(File file, List<PettyCashRecordModel> records) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        createPDFCoverPage(document, records.size());
        createPDFSummaryPage(document, records);
        createPDFIndividualRecords(document, records);
        createPDFSignaturesSection(document);
        
        document.close();
    }

    private void createPDFCoverPage(Document document, int recordCount) throws DocumentException {
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
        Paragraph title = new Paragraph("AFRAN GENERAL HOSPITAL", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Paragraph subtitle = new Paragraph("\nDETAILED PETTY CASH RECORDS", titleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);
        
        com.itextpdf.text.Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph info = new Paragraph("\n\nGenerated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) + 
                                     "\nGenerated by: " + currentUser + 
                                     "\nTotal Records: " + recordCount + 
                                     "\nReport Period: " + getDateRange(table.getItems()), infoFont);
        info.setAlignment(Element.ALIGN_CENTER);
        document.add(info);
        
        document.newPage();
    }

    private void createPDFSummaryPage(Document document, List<PettyCashRecordModel> records) throws DocumentException {
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph sectionTitle = new Paragraph("REPORT SUMMARY", sectionFont);
        sectionTitle.setSpacingAfter(20);
        document.add(sectionTitle);

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        
        addMetricRow(summaryTable, "Total Records", String.valueOf(records.size()));
        addMetricRow(summaryTable, "Total Amount", String.format("ETB %,.2f", 
            records.stream().mapToDouble(PettyCashRecordModel::getAmountRequested).sum()));
        addMetricRow(summaryTable, "Average Amount", 
            String.format("ETB %,.2f", calculateAverageAmount(records)));
        addMetricRow(summaryTable, "Highest Amount", 
            String.format("ETB %,.2f", records.stream().mapToDouble(PettyCashRecordModel::getAmountRequested).max().orElse(0)));
        addMetricRow(summaryTable, "Approval Rate", 
            String.format("%.1f%%", calculateApprovalRate(records)));
        addMetricRow(summaryTable, "Completion Rate", 
            String.format("%.1f%%", calculateCompletionRate(records)));
        addMetricRow(summaryTable, "Date Range", getDateRange(records));
        addMetricRow(summaryTable, "Top Category", getTopCategory(records));
        
        document.add(summaryTable);
        document.newPage();
    }

    private void createPDFIndividualRecords(Document document, List<PettyCashRecordModel> records) throws DocumentException {
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph sectionTitle = new Paragraph("DETAILED RECORDS", sectionFont);
        sectionTitle.setSpacingAfter(20);
        document.add(sectionTitle);

        for (int i = 0; i < records.size(); i++) {
            PettyCashRecordModel record = records.get(i);
            
            // Record header
            com.itextpdf.text.Font recordFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph recordHeader = new Paragraph("Record #" + (i + 1) + " - " + record.getRequestId(), recordFont);
            recordHeader.setSpacingAfter(10);
            document.add(recordHeader);

            // Record details table
            PdfPTable recordTable = new PdfPTable(4);
            recordTable.setWidthPercentage(100);
            
            addRecordDetailRow(recordTable, "Requisition Unit", record.getRequisitionUnit());
            addRecordDetailRow(recordTable, "Main Category", record.getMainCategory());
            addRecordDetailRow(recordTable, "Sub-Category", record.getSubCategory());
            addRecordDetailRow(recordTable, "Payee", record.getPayee());
            addRecordDetailRow(recordTable, "Reason", record.getReason());
            addRecordDetailRow(recordTable, "Amount", String.format("ETB %,.2f", record.getAmountRequested()));
            addRecordDetailRow(recordTable, "Request Date", record.getRequestDate().toString());
            addRecordDetailRow(recordTable, "Approval Status", record.getApprovalStatus());
            addRecordDetailRow(recordTable, "Approved By", record.getApprovedBy());
            addRecordDetailRow(recordTable, "Confirmation Status", record.getConfirmationStatus());
            addRecordDetailRow(recordTable, "Confirmed By", record.getConfirmedBy());
            addRecordDetailRow(recordTable, "Dispensed Status", record.getDispensedStatus());
            addRecordDetailRow(recordTable, "Dispensed By", record.getDispensedBy());
            addRecordDetailRow(recordTable, "Dispense Approval", record.getDispenseApprovalStatus());
            addRecordDetailRow(recordTable, "Dispense Approved By", record.getDispenseApprovedBy());
            addRecordDetailRow(recordTable, "Void Status", record.getVoidStatus());
            addRecordDetailRow(recordTable, "Voided By", record.getVoidedBy());
            
            document.add(recordTable);
            
            // Add space between records, new page after every 3 records
            if ((i + 1) % 3 == 0 && i < records.size() - 1) {
                document.newPage();
                document.add(sectionTitle);
            } else if (i < records.size() - 1) {
                document.add(new Paragraph("\n"));
            }
        }
    }

    private void addRecordDetailRow(PdfPTable table, String label, String value) {
        table.addCell(createPdfCell(label, true));
        table.addCell(createPdfCell(value != null ? value : "N/A", false));
        table.addCell(createPdfCell("", true)); // Empty cell for spacing
        table.addCell(createPdfCell("", false)); // Empty cell for spacing
    }

    private void createPDFSignaturesSection(Document document) throws DocumentException {
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

    // ==================== ENHANCED EXPORT TO WORD ====================

    private void exportToWord() {
        List<PettyCashRecordModel> recordsToExport = new ArrayList<>(table.getItems());
        exportDetailedRecordsToWord(recordsToExport);
    }

    private void exportDetailedRecordsToWord(List<PettyCashRecordModel> records) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Detailed Records to Word");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Documents", "*.docx"));
        fileChooser.setInitialFileName("PettyCash_Detailed_Records_" + LocalDate.now() + ".docx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (XWPFDocument document = new XWPFDocument()) {
                createDetailedWordReport(document, records);
                document.write(new FileOutputStream(file));
                showAlert("Export Successful", 
                    records.size() + " detailed records exported to Word successfully!");
                openFile(file);
            } catch (Exception e) {
                showAlert("Export Error", "Failed to export to Word: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createDetailedWordReport(XWPFDocument document, List<PettyCashRecordModel> records) {
        createWordCoverPage(document, records.size());
        createWordSummaryPage(document, records);
        createWordIndividualRecords(document, records);
        createWordSignaturesSection(document);
    }

    private void createWordCoverPage(XWPFDocument document, int recordCount) {
        XWPFParagraph titlePara = document.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("AFRAN GENERAL HOSPITAL");
        titleRun.setBold(true);
        titleRun.setFontSize(20);
        
        XWPFParagraph subtitlePara = document.createParagraph();
        subtitlePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun subtitleRun = subtitlePara.createRun();
        subtitleRun.setText("DETAILED PETTY CASH RECORDS");
        subtitleRun.setBold(true);
        subtitleRun.setFontSize(16);
        
        XWPFParagraph infoPara = document.createParagraph();
        infoPara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun infoRun = infoPara.createRun();
        infoRun.setText("\nGenerated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        infoRun.addBreak();
        infoRun.setText("Generated by: " + currentUser);
        infoRun.addBreak();
        infoRun.setText("Total Records: " + recordCount);
        infoRun.addBreak();
        infoRun.setText("Report Period: " + getDateRange(table.getItems()));
        
        // Page break
        XWPFParagraph breakPara = document.createParagraph();
        XWPFRun breakRun = breakPara.createRun();
        breakRun.addBreak();
        breakRun.addBreak();
    }

    private void createWordSummaryPage(XWPFDocument document, List<PettyCashRecordModel> records) {
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("REPORT SUMMARY");
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        
        XWPFTable table = document.createTable(8, 2);
        table.setWidth("100%");
        
        addWordTableRow(table, 0, "Total Records", String.valueOf(records.size()));
        addWordTableRow(table, 1, "Total Amount", String.format("ETB %,.2f", 
            records.stream().mapToDouble(PettyCashRecordModel::getAmountRequested).sum()));
        addWordTableRow(table, 2, "Average Amount", String.format("ETB %,.2f", calculateAverageAmount(records)));
        addWordTableRow(table, 3, "Highest Amount", 
            String.format("ETB %,.2f", records.stream().mapToDouble(PettyCashRecordModel::getAmountRequested).max().orElse(0)));
        addWordTableRow(table, 4, "Approval Rate", String.format("%.1f%%", calculateApprovalRate(records)));
        addWordTableRow(table, 5, "Completion Rate", String.format("%.1f%%", calculateCompletionRate(records)));
        addWordTableRow(table, 6, "Date Range", getDateRange(records));
        addWordTableRow(table, 7, "Top Category", getTopCategory(records));
        
        // Page break
        XWPFParagraph breakPara = document.createParagraph();
        XWPFRun breakRun = breakPara.createRun();
        breakRun.addBreak();
    }

    private void createWordIndividualRecords(XWPFDocument document, List<PettyCashRecordModel> records) {
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("DETAILED RECORDS");
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        
        for (int i = 0; i < records.size(); i++) {
            PettyCashRecordModel record = records.get(i);
            
            // Record header
            XWPFParagraph recordPara = document.createParagraph();
            XWPFRun recordRun = recordPara.createRun();
            recordRun.setText("Record #" + (i + 1) + " - " + record.getRequestId());
            recordRun.setBold(true);
            recordRun.setFontSize(12);
            
            // Record details table
            XWPFTable recordTable = document.createTable();
            recordTable.setWidth("100%");
            
            addWordRecordDetailRow(recordTable, "Requisition Unit", record.getRequisitionUnit());
            addWordRecordDetailRow(recordTable, "Main Category", record.getMainCategory());
            addWordRecordDetailRow(recordTable, "Sub-Category", record.getSubCategory());
            addWordRecordDetailRow(recordTable, "Payee", record.getPayee());
            addWordRecordDetailRow(recordTable, "Reason", record.getReason());
            addWordRecordDetailRow(recordTable, "Amount", String.format("ETB %,.2f", record.getAmountRequested()));
            addWordRecordDetailRow(recordTable, "Request Date", record.getRequestDate().toString());
            addWordRecordDetailRow(recordTable, "Approval Status", record.getApprovalStatus());
            addWordRecordDetailRow(recordTable, "Approved By", record.getApprovedBy());
            addWordRecordDetailRow(recordTable, "Confirmation Status", record.getConfirmationStatus());
            addWordRecordDetailRow(recordTable, "Confirmed By", record.getConfirmedBy());
            addWordRecordDetailRow(recordTable, "Dispensed Status", record.getDispensedStatus());
            addWordRecordDetailRow(recordTable, "Dispensed By", record.getDispensedBy());
            addWordRecordDetailRow(recordTable, "Dispense Approval", record.getDispenseApprovalStatus());
            addWordRecordDetailRow(recordTable, "Dispense Approved By", record.getDispenseApprovedBy());
            addWordRecordDetailRow(recordTable, "Void Status", record.getVoidStatus());
            addWordRecordDetailRow(recordTable, "Voided By", record.getVoidedBy());
            
            // Add space between records
            XWPFParagraph spacePara = document.createParagraph();
            XWPFRun spaceRun = spacePara.createRun();
            spaceRun.setText("");
        }
    }

    private void addWordRecordDetailRow(XWPFTable table, String label, String value) {
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(label + ":");
        row.addNewTableCell().setText(value != null ? value : "N/A");
    }

    private void createWordSignaturesSection(XWPFDocument document) {
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("APPROVALS AND SIGNATURES");
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        
        XWPFTable signatureTable = document.createTable(3, 3);
        signatureTable.setWidth("100%");
        
        addWordSignatureRow(signatureTable, 0, "Prepared by:", currentUser, "___________________");
        addWordSignatureRow(signatureTable, 1, "Reviewed by:", "Finance Manager", "___________________");
        addWordSignatureRow(signatureTable, 2, "Approved by:", "Head of Department", "___________________");
        
        XWPFParagraph footerPara = document.createParagraph();
        footerPara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun footerRun = footerPara.createRun();
        footerRun.setText("\nGenerated by AFRAN General Hospital HRMS System - " + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        footerRun.setItalic(true);
        footerRun.setFontSize(10);
    }

    private void addWordSignatureRow(XWPFTable table, int row, String role, String name, String signature) {
        table.getRow(row).getCell(0).setText(role);
        table.getRow(row).getCell(1).setText(name);
        table.getRow(row).getCell(2).setText(signature);
    }

    // ==================== COMPREHENSIVE PRINT REPORT ====================

    private void printReport() {
        List<PettyCashRecordModel> recordsToPrint = new ArrayList<>(table.getItems());
        printDetailedRecords(recordsToPrint);
    }

    // ==================== INDIVIDUAL RECORD METHODS ====================

    private void showRecordDetails() {
        PettyCashRecordModel selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showIndividualRecordDetails(selected);
        } else {
            showAlert("No Selection", "Please select a record to view details.");
        }
    }

    private void showIndividualRecordDetails(PettyCashRecordModel record) {
        Alert detailsAlert = new Alert(Alert.AlertType.INFORMATION);
        detailsAlert.setTitle("Record Details");
        detailsAlert.setHeaderText("Petty Cash Request Details - " + record.getRequestId());
        detailsAlert.getDialogPane().setPrefSize(600, 500);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Create a detailed view similar to the print layout
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(10);
        detailsGrid.setPadding(new Insets(10));

        // Column 1
        addDetailRowToGrid(detailsGrid, "Request ID:", record.getRequestId(), 0, 0);
        addDetailRowToGrid(detailsGrid, "Requisition Unit:", record.getRequisitionUnit(), 0, 1);
        addDetailRowToGrid(detailsGrid, "Main Category:", record.getMainCategory(), 0, 2);
        addDetailRowToGrid(detailsGrid, "Sub-Category:", record.getSubCategory(), 0, 3);
        addDetailRowToGrid(detailsGrid, "Payee:", record.getPayee(), 0, 4);

        // Column 2
        addDetailRowToGrid(detailsGrid, "Amount:", String.format("ETB %,.2f", record.getAmountRequested()), 1, 0);
        addDetailRowToGrid(detailsGrid, "Request Date:", record.getRequestDate().toString(), 1, 1);
        addDetailRowToGrid(detailsGrid, "Approval Status:", record.getApprovalStatus(), 1, 2);
        addDetailRowToGrid(detailsGrid, "Confirmed:", record.getConfirmationStatus(), 1, 3);
        addDetailRowToGrid(detailsGrid, "Dispensed:", record.getDispensedStatus(), 1, 4);

        // Column 3
        addDetailRowToGrid(detailsGrid, "Approved By:", record.getApprovedBy(), 2, 0);
        addDetailRowToGrid(detailsGrid, "Confirmed By:", record.getConfirmedBy(), 2, 1);
        addDetailRowToGrid(detailsGrid, "Dispensed By:", record.getDispensedBy(), 2, 2);
        addDetailRowToGrid(detailsGrid, "Dispense Approval:", record.getDispenseApprovalStatus(), 2, 3);
        addDetailRowToGrid(detailsGrid, "Void Status:", record.getVoidStatus(), 2, 4);

        // Reason (full width)
        Label reasonLabel = new Label("Reason:");
        reasonLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Label reasonValue = new Label(record.getReason());
        reasonValue.setWrapText(true);
        VBox reasonBox = new VBox(5, reasonLabel, reasonValue);
        reasonBox.setPadding(new Insets(10, 0, 0, 0));

        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button printBtn = new Button("Print This Record");
        Button exportBtn = new Button("Export This Record");
        Button closeBtn = new Button("Close");
        
        printBtn.setOnAction(e -> {
            detailsAlert.close();
            printDetailedRecords(Collections.singletonList(record));
        });
        
        exportBtn.setOnAction(e -> {
            detailsAlert.close();
            exportDetailedRecordsToExcel(Collections.singletonList(record));
        });
        
        closeBtn.setOnAction(e -> detailsAlert.close());
        
        buttonBox.getChildren().addAll(printBtn, exportBtn, closeBtn);

        content.getChildren().addAll(detailsGrid, reasonBox, buttonBox);
        detailsAlert.getDialogPane().setContent(content);
        detailsAlert.showAndWait();
    }

    private void addDetailRowToGrid(GridPane grid, String label, String value, int column, int row) {
        Label detailLabel = new Label(label);
        detailLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        Label detailValue = new Label(value != null ? value : "N/A");
        detailValue.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        detailValue.setWrapText(true);

        grid.add(detailLabel, column * 2, row);
        grid.add(detailValue, column * 2 + 1, row);
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

    private double calculateApprovalRate(List<PettyCashRecordModel> records) {
        long approved = records.stream().filter(r -> "Approved".equals(r.getApprovalStatus())).count();
        return records.size() > 0 ? (double) approved / records.size() * 100 : 0;
    }

    private double calculateAverageAmount(List<PettyCashRecordModel> records) {
        return records.stream().mapToDouble(PettyCashRecordModel::getAmountRequested).average().orElse(0);
    }

    private double calculateCompletionRate(List<PettyCashRecordModel> records) {
        long completed = records.stream().filter(r -> "Yes".equals(r.getDispensedStatus())).count();
        return records.size() > 0 ? (double) completed / records.size() * 100 : 0;
    }

    private String getDateRange(List<PettyCashRecordModel> records) {
        if (records.isEmpty()) return "No data";
        LocalDate minDate = records.stream().map(PettyCashRecordModel::getRequestDate).min(LocalDate::compareTo).orElse(LocalDate.now());
        LocalDate maxDate = records.stream().map(PettyCashRecordModel::getRequestDate).max(LocalDate::compareTo).orElse(LocalDate.now());
        return minDate + " to " + maxDate;
    }

    private String getTopCategory(List<PettyCashRecordModel> records) {
        return records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(PettyCashRecordModel::getAmountRequested)
            ))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> entry.getKey() + " (ETB " + String.format("%,.2f", entry.getValue()) + ")")
            .orElse("N/A");
    }
    
    
    

    // ==================== OTHER METHODS ====================

    private void refreshData() {
        Connecting db = new Connecting();
        List<PettyCashRecordModel> updatedData = db.getAllPettyCashRecords();
        this.allRecords.setAll(updatedData);
        populateCategoryFilters(); // Refresh category filters
        applyFilters();
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

    private void copySelectedToClipboard() {
        PettyCashRecordModel selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String content = String.format(
                "Request ID: %s\nUnit: %s\nCategory: %s\nSub-Category: %s\nPayee: %s\nReason: %s\nAmount: ETB %.2f\nDate: %s\nStatus: %s\nDispensed: %s",
                selected.getRequestId(),
                selected.getRequisitionUnit(),
                selected.getMainCategory(),
                selected.getSubCategory(),
                selected.getPayee(),
                selected.getReason(),
                selected.getAmountRequested(),
                selected.getRequestDate(),
                selected.getApprovalStatus(),
                selected.getDispensedStatus()
            );
            
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(content);
            clipboard.setContent(clipboardContent);
            
            showAlert("Copied", "Selected record copied to clipboard!");
        } else {
            showAlert("No Selection", "Please select a record to copy.");
        }
    }

    private void showAdvancedFilters() {
        showAlert("Advanced Filters", "Advanced filter options will be implemented in the next version.");
    }

    private void resetFilters() {
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        unitField.clear();
        payeeField.clear();
        searchField.clear();
        categoryFilter.setValue("All Categories");
        subCategoryFilter.setValue("All Sub-Categories");
        confirmationFilter.setValue("All");
        approvalFilter.setValue("All");
        dispensedFilter.setValue("All");
        dispenseApprovalFilter.setValue("All");
        voidFilter.setValue("All");
        applyFilters();
    }

    private void openFile(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            System.out.println("Could not open file: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}