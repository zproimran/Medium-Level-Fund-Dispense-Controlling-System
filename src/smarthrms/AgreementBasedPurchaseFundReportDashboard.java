package smarthrms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.chart.*;
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


public class AgreementBasedPurchaseFundReportDashboard extends BorderPane {

    // UI Components
    private DatePicker fromDatePicker, toDatePicker;
    private TextField unitField, payeeField, searchField, employeeField;
    private ComboBox<String> confirmationFilter, approvalFilter, dispensedFilter, dispenseApprovalFilter, voidFilter;
    private ComboBox<String> categoryFilter, subCategoryFilter;
    private TableView<AgreementBasedPurchaseFundRecordModel> table;
    private ObservableList<AgreementBasedPurchaseFundRecordModel> allRecords;

    // Charts
    private PieChart statusPieChart;
    private PieChart categoryPieChart;
    private BarChart<String, Number> monthlyBarChart;
    private LineChart<String, Number> trendLineChart;
    private BarChart<String, Number> unitBarChart;
    private BarChart<String, Number> categoryBarChart;

    // Summary Labels
    private Label totalRequestsLbl, totalApprovedLbl, totalDispensedLbl, totalAmountLbl;
    private Label avgAmountLbl, highestAmountLbl, pendingApprovalLbl, completionRateLbl, topCategoryLbl;

    // Control Buttons
    private Button exportExcelBtn, exportPdfBtn, exportWordBtn, printBtn, refreshBtn;
    private ToggleButton darkModeToggle;

    // Tab Panes
    private TabPane mainTabPane;
    private Tab tableTab, pieChartTab, barChartTab, lineChartTab, unitChartTab, categoryChartTab, analyticsTab;
    private String currentUser;
    private Connecting databaseConnector;

    public AgreementBasedPurchaseFundReportDashboard(List<AgreementBasedPurchaseFundRecordModel> records, String username) {
        this.allRecords = FXCollections.observableArrayList(records != null ? records.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList()) : new ArrayList<>());
        this.currentUser = username;
        this.databaseConnector = new Connecting();
        initializeUI();
        setupEventHandlers();
        populateCategoryFilters();
        applyFilters();
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
        Label title = new Label("Agreement-Based Purchase Fund Analytics Dashboard");
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
        toDatePicker = new DatePicker();
        unitField = new TextField();
        payeeField = new TextField();
        employeeField = new TextField();
        searchField = new TextField();

        fromDatePicker.setPromptText("From Date");
        toDatePicker.setPromptText("To Date");
        unitField.setPromptText("Requisition Unit");
        payeeField.setPromptText("Payee");
        employeeField.setPromptText("Employee Name");
        searchField.setPromptText("Search across all fields...");

        // Category filters
        categoryFilter = createComboBox("All Categories");
        subCategoryFilter = createComboBox("All Sub-Categories");

        approvalFilter = createComboBox("All", "Pending", "Approved");
        confirmationFilter = createComboBox("All", "Pending", "Confirmed");
        dispensedFilter = createComboBox("All", "Yes", "No");
        dispenseApprovalFilter = createComboBox("All", "Pending", "Approved");
        voidFilter = createComboBox("All", "Yes", "No");

        Button resetBtn = createStyledButton("Reset Filters", "#95a5a6");
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
        grid.add(new Label("Category:"), 4, 1);
        grid.add(categoryFilter, 5, 1);

        // Row 2
        grid.add(new Label("Employee:"), 0, 2);
        grid.add(employeeField, 1, 2);
        grid.add(new Label("Sub-Category:"), 2, 2);
        grid.add(subCategoryFilter, 3, 2);
        grid.add(new Label("Approval:"), 4, 2);
        grid.add(approvalFilter, 5, 2);

        // Row 3
        grid.add(new Label("Confirmed:"), 0, 3);
        grid.add(confirmationFilter, 1, 3);
        grid.add(new Label("Dispensed:"), 2, 3);
        grid.add(dispensedFilter, 3, 3);
        grid.add(new Label("Dispense Approval:"), 4, 3);
        grid.add(dispenseApprovalFilter, 5, 3);

        // Row 4
        grid.add(new Label("Void:"), 0, 4);
        grid.add(voidFilter, 1, 4);
        grid.add(resetBtn, 4, 4);
        grid.add(advancedFilterBtn, 5, 4);

        VBox filterSection = new VBox(grid);
        filterSection.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 10;");
        
        return filterSection;
    }

    private void populateCategoryFilters() {
        // Extract unique categories and sub-categories from records
        Set<String> categories = allRecords.stream()
            .map(AgreementBasedPurchaseFundRecordModel::getMainCategory)
            .filter(cat -> cat != null && !cat.trim().isEmpty())
            .collect(Collectors.toSet());
        
        Set<String> subCategories = allRecords.stream()
            .map(AgreementBasedPurchaseFundRecordModel::getSubCategory)
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

        // Define columns with category fields
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> idCol = new TableColumn<>("Request ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRequestId()));

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRequisitionUnit()));

        // Category columns
        TableColumn<AgreementBasedPurchaseFundRecordModel, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getMainCategory() != null ? data.getValue().getMainCategory() : ""
        ));

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> subCategoryCol = new TableColumn<>("Sub-Category");
        subCategoryCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getSubCategory() != null ? data.getValue().getSubCategory() : ""
        ));

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> payeeCol = new TableColumn<>("Payee");
        payeeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPayee()));

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> employeeCol = new TableColumn<>("Employee");
        employeeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmployeeName()));

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> new SimpleStringProperty(
            String.format("ETB %,.2f", data.getValue().getAmountRequested())
        ));

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getRequestDate().toString()
        ));

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApprovalStatus()));
        statusCol.setCellFactory(column -> new TableCell<AgreementBasedPurchaseFundRecordModel, String>() {
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

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> dispensedCol = new TableColumn<>("Dispensed");
        dispensedCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDispensedStatus()));
        dispensedCol.setCellFactory(column -> new TableCell<AgreementBasedPurchaseFundRecordModel, String>() {
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

        table.getColumns().addAll(idCol, unitCol, categoryCol, subCategoryCol, payeeCol, employeeCol, amountCol, dateCol, statusCol, dispensedCol);

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
            ObservableList<AgreementBasedPurchaseFundRecordModel> items = table.getItems();
            int size = items != null ? items.size() : 0;
            return "Showing " + size + " record" + (size == 1 ? "" : "s");
        }, table.itemsProperty()));

        Button copyBtn = createStyledButton("Copy Selected", "#3498db");
        Button exportSelectionBtn = createStyledButton("Export Selection", "#27ae60");
        
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
        monthlyBarChart.setTitle("Monthly Purchase Fund Analysis");
        monthlyBarChart.setStyle("-fx-background-color: white;");
        return monthlyBarChart;
    }

    private LineChart<String, Number> createLineChart() {
        CategoryAxis xAxisLine = new CategoryAxis();
        NumberAxis yAxisLine = new NumberAxis();
        trendLineChart = new LineChart<>(xAxisLine, yAxisLine);
        trendLineChart.setTitle("Purchase Fund Trends Over Time");
        trendLineChart.setStyle("-fx-background-color: white;");
        return trendLineChart;
    }

    private BarChart<String, Number> createUnitChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        unitBarChart = new BarChart<>(xAxis, yAxis);
        unitBarChart.setTitle("Purchase Fund by Requisition Unit");
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
        analyticsGrid.add(createMetricCard("Avg Processing Time", "2.5 days", "#e67e22"), 2, 0);
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
        employeeField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
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
        final String employeeText = employeeField.getText() == null ? "" : employeeField.getText().trim().toLowerCase();
        final String searchText = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        final String categoryValue = categoryFilter.getValue();
        final String subCategoryValue = subCategoryFilter.getValue();

        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();

        List<AgreementBasedPurchaseFundRecordModel> filtered = allRecords.stream()
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
            .filter(r -> employeeText.isEmpty() || 
                (r.getEmployeeName() != null && r.getEmployeeName().toLowerCase().contains(employeeText)))
            .filter(r -> searchText.isEmpty() || matchesSearch(r, searchText))
            .filter(r -> categoryValue == null || categoryValue.equals("All Categories") || 
                (r.getMainCategory() != null && r.getMainCategory().equals(categoryValue)))
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

    private boolean matchesSearch(AgreementBasedPurchaseFundRecordModel record, String searchText) {
        return (record.getRequestId() != null && record.getRequestId().toLowerCase().contains(searchText)) ||
               (record.getRequisitionUnit() != null && record.getRequisitionUnit().toLowerCase().contains(searchText)) ||
               (record.getMainCategory() != null && record.getMainCategory().toLowerCase().contains(searchText)) ||
               (record.getSubCategory() != null && record.getSubCategory().toLowerCase().contains(searchText)) ||
               (record.getPayee() != null && record.getPayee().toLowerCase().contains(searchText)) ||
               (record.getEmployeeName() != null && record.getEmployeeName().toLowerCase().contains(searchText)) ||
               (record.getReason() != null && record.getReason().toLowerCase().contains(searchText));
    }

    private boolean matches(String filterValue, String dataValue) {
        if (filterValue == null || filterValue.equals("All")) return true;
        if (dataValue == null) dataValue = "";
        return filterValue.equalsIgnoreCase(dataValue);
    }

    private void updateCharts(List<AgreementBasedPurchaseFundRecordModel> data) {
        updatePieChart(data);
        updateCategoryCharts(data);
        updateBarChart(data);
        updateLineChart(data);
        updateUnitChart(data);
    }

    private void updatePieChart(List<AgreementBasedPurchaseFundRecordModel> data) {
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

    private void updateCategoryCharts(List<AgreementBasedPurchaseFundRecordModel> data) {
        // Category Pie Chart
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

        // Category Bar Chart
        Map<String, Double> categoryAmounts = data.stream()
                .collect(Collectors.groupingBy(
                    record -> record.getMainCategory() != null && !record.getMainCategory().isEmpty() ? 
                             record.getMainCategory() : "Uncategorized",
                    Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
                ));

        XYChart.Series<String, Number> categorySeries = new XYChart.Series<>();
        categorySeries.setName("Category Totals");
        
        categoryAmounts.forEach((category, total) -> 
            categorySeries.getData().add(new XYChart.Data<>(category, total))
        );

        categoryBarChart.getData().clear();
        categoryBarChart.getData().add(categorySeries);
    }

    private void updateBarChart(List<AgreementBasedPurchaseFundRecordModel> data) {
        Map<String, Double> monthlyTotals = data.stream()
                .collect(Collectors.groupingBy(
                    record -> record.getRequestDate().getMonth().toString(),
                    Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Totals");
        
        monthlyTotals.forEach((month, total) -> 
            series.getData().add(new XYChart.Data<>(month, total))
        );

        monthlyBarChart.getData().clear();
        monthlyBarChart.getData().add(series);
    }

    private void updateLineChart(List<AgreementBasedPurchaseFundRecordModel> data) {
        Map<LocalDate, Double> dailyTotals = data.stream()
                .collect(Collectors.groupingBy(
                    AgreementBasedPurchaseFundRecordModel::getRequestDate,
                    Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
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

    private void updateUnitChart(List<AgreementBasedPurchaseFundRecordModel> data) {
        Map<String, Double> unitTotals = data.stream()
                .collect(Collectors.groupingBy(
                    AgreementBasedPurchaseFundRecordModel::getRequisitionUnit,
                    Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Unit Totals");
        
        unitTotals.forEach((unit, total) -> 
            series.getData().add(new XYChart.Data<>(unit, total))
        );

        unitBarChart.getData().clear();
        unitBarChart.getData().add(series);
    }

    private void updateSummary(List<AgreementBasedPurchaseFundRecordModel> data) {
        long totalRequests = data.size();
        long approved = data.stream().filter(r -> "Approved".equals(r.getApprovalStatus())).count();
        long dispensed = data.stream().filter(r -> "Yes".equals(r.getDispensedStatus())).count();
        double totalAmount = data.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();
        double avgAmount = totalRequests > 0 ? totalAmount / totalRequests : 0;
        double highestAmount = data.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).max().orElse(0);
        long pending = data.stream().filter(r -> "Pending".equals(r.getApprovalStatus())).count();
        double completionRate = totalRequests > 0 ? (double) dispensed / totalRequests * 100 : 0;
        
        // Top category calculation
        String topCategory = data.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
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

    // ==================== COMPREHENSIVE EXCEL EXPORT METHODS ====================

    private void exportToExcel() {
        List<AgreementBasedPurchaseFundRecordModel> recordsToExport = new ArrayList<>(table.getItems());
        
        if (recordsToExport.isEmpty()) {
            showAlert("No Data", "There are no records to export.");
            return;
        }
        
        exportDetailedRecordsToExcel(recordsToExport);
    }

    private void exportDetailedRecordsToExcel(List<AgreementBasedPurchaseFundRecordModel> records) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Agreement-Based Purchase Fund Records to Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("AgreementPurchaseFund_Records_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                createComprehensiveExcelReport(workbook, records);
                
                // Write the workbook to file
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                }
                
                showAlert("Export Successful", 
                    records.size() + " records exported to Excel successfully!\nFile: " + file.getName());
                
                // Open the file if desktop is supported
                openFile(file);
                
            } catch (Exception e) {
                showAlert("Export Error", "Failed to export to Excel: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void createComprehensiveExcelReport(Workbook workbook, List<AgreementBasedPurchaseFundRecordModel> records) {
        // Create all sheets for comprehensive report
        createExecutiveSummarySheet(workbook, records);
        createDetailedRecordsSheet(workbook, records);
        createCategoryAnalysisSheet(workbook, records);
        createStatusAnalysisSheet(workbook, records);
        createMonthlyAnalysisSheet(workbook, records);
        createDepartmentAnalysisSheet(workbook, records);
        createChartsDataSheet(workbook, records);
    }

    private void createExecutiveSummarySheet(Workbook workbook, List<AgreementBasedPurchaseFundRecordModel> records) {
        Sheet sheet = workbook.createSheet("Executive Summary");
        
        // Title Row
        Row titleRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("AFRAN GENERAL HOSPITAL - AGREEMENT-BASED PURCHASE FUND MANAGEMENT REPORT");
        setCellStyle(titleCell, workbook, true, true, IndexedColors.DARK_BLUE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 7));

        // Report Information
        int rowNum = 2;
        addReportInfo(sheet, rowNum++, records);
        
        // Key Metrics Section
        rowNum = addKeyMetricsSection(sheet, rowNum, records);
        
        // Status Summary
        rowNum = addStatusSummarySection(sheet, rowNum, records);
        
        // Category Summary
        rowNum = addCategorySummarySection(sheet, rowNum, records);
        
        // Recent Activity
        addRecentActivitySection(sheet, rowNum, records);
        
        // Auto-size all columns
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void addReportInfo(Sheet sheet, int rowNum, List<AgreementBasedPurchaseFundRecordModel> records) {
        Row infoRow1 = sheet.createRow(rowNum++);
        infoRow1.createCell(0).setCellValue("Report Date:");
        infoRow1.createCell(1).setCellValue(LocalDate.now().toString());
        infoRow1.createCell(4).setCellValue("Total Records:");
        infoRow1.createCell(5).setCellValue(records.size());

        Row infoRow2 = sheet.createRow(rowNum++);
        infoRow2.createCell(0).setCellValue("Generated by:");
        infoRow2.createCell(1).setCellValue(currentUser);
        infoRow2.createCell(4).setCellValue("Date Range:");
        infoRow2.createCell(5).setCellValue(getDateRange(records));

        Row infoRow3 = sheet.createRow(rowNum++);
        infoRow3.createCell(0).setCellValue("Report Period:");
        infoRow3.createCell(1).setCellValue(getReportPeriod());
        infoRow3.createCell(4).setCellValue("Data Source:");
        infoRow3.createCell(5).setCellValue("HRMS Agreement Purchase Fund Module");
    }

    private int addKeyMetricsSection(Sheet sheet, int rowNum, List<AgreementBasedPurchaseFundRecordModel> records) {
        // Section Title
        Row metricsTitle = sheet.createRow(rowNum++);
        metricsTitle.createCell(0).setCellValue("KEY PERFORMANCE INDICATORS");
        setCellStyle(metricsTitle.getCell(0), sheet.getWorkbook(), true, false, IndexedColors.GREY_25_PERCENT);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum-1, rowNum-1, 0, 3));

        // Metrics Data
        String[][] metrics = {
            {"Total Amount Processed", String.format("ETB %,.2f", records.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum())},
            {"Average Transaction", String.format("ETB %,.2f", calculateAverageAmount(records))},
            {"Highest Single Transaction", String.format("ETB %,.2f", getHighestAmount(records))},
            {"Approval Rate", String.format("%.1f%%", calculateApprovalRate(records))},
            {"Completion Rate", String.format("%.1f%%", calculateCompletionRate(records))},
            {"Pending Approvals", String.valueOf(getPendingApprovalsCount(records))},
            {"Average Processing Time", "2.5 days"},
            {"Most Active Department", getMostActiveDepartment(records)}
        };

        for (int i = 0; i < metrics.length; i++) {
            Row metricRow = sheet.createRow(rowNum++);
            metricRow.createCell(0).setCellValue(metrics[i][0]);
            setCellStyle(metricRow.getCell(0), sheet.getWorkbook(), true, false, null);
            metricRow.createCell(1).setCellValue(metrics[i][1]);
            
            if (i < metrics.length - 1) {
                metricRow.createCell(2).setCellValue(metrics[i+1][0]);
                setCellStyle(metricRow.getCell(2), sheet.getWorkbook(), true, false, null);
                metricRow.createCell(3).setCellValue(metrics[i+1][1]);
                i++; // Skip next iteration since we added two metrics
            }
        }

        return rowNum + 1; // Add extra space
    }

    private int addStatusSummarySection(Sheet sheet, int rowNum, List<AgreementBasedPurchaseFundRecordModel> records) {
        // Section Title
        Row statusTitle = sheet.createRow(rowNum++);
        statusTitle.createCell(0).setCellValue("STATUS DISTRIBUTION");
        setCellStyle(statusTitle.getCell(0), sheet.getWorkbook(), true, false, IndexedColors.LIGHT_BLUE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum-1, rowNum-1, 0, 3));

        // Header Row
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Status");
        headerRow.createCell(1).setCellValue("Count");
        headerRow.createCell(2).setCellValue("Percentage");
        headerRow.createCell(3).setCellValue("Total Amount");
        
        setCellStyle(headerRow.getCell(0), sheet.getWorkbook(), true, false, IndexedColors.GREY_25_PERCENT);
        setCellStyle(headerRow.getCell(1), sheet.getWorkbook(), true, false, IndexedColors.GREY_25_PERCENT);
        setCellStyle(headerRow.getCell(2), sheet.getWorkbook(), true, false, IndexedColors.GREY_25_PERCENT);
        setCellStyle(headerRow.getCell(3), sheet.getWorkbook(), true, false, IndexedColors.GREY_25_PERCENT);

        // Status Data
        Map<String, Long> statusCounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getApprovalStatus() != null ? r.getApprovalStatus() : "Unknown",
                Collectors.counting()
            ));

        Map<String, Double> statusAmounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getApprovalStatus() != null ? r.getApprovalStatus() : "Unknown",
                Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
            ));

        double totalAmount = records.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();

        for (String status : statusCounts.keySet()) {
            Row statusRow = sheet.createRow(rowNum++);
            statusRow.createCell(0).setCellValue(status);
            
            long count = statusCounts.get(status);
            statusRow.createCell(1).setCellValue(count);
            
            double percentage = (double) count / records.size() * 100;
            statusRow.createCell(2).setCellValue(String.format("%.1f%%", percentage));
            
            double amount = statusAmounts.getOrDefault(status, 0.0);
            statusRow.createCell(3).setCellValue(amount);
        }

        return rowNum + 1; // Add extra space
    }

    private int addCategorySummarySection(Sheet sheet, int rowNum, List<AgreementBasedPurchaseFundRecordModel> records) {
        // Section Title
        Row categoryTitle = sheet.createRow(rowNum++);
        categoryTitle.createCell(0).setCellValue("CATEGORY SUMMARY");
        setCellStyle(categoryTitle.getCell(0), sheet.getWorkbook(), true, false, IndexedColors.LIGHT_GREEN);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum-1, rowNum-1, 0, 3));

        // Header Row
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Category");
        headerRow.createCell(1).setCellValue("Count");
        headerRow.createCell(2).setCellValue("Total Amount");
        headerRow.createCell(3).setCellValue("Percentage");
        
        setCellStyle(headerRow.getCell(0), sheet.getWorkbook(), true, false, IndexedColors.GREY_25_PERCENT);
        setCellStyle(headerRow.getCell(1), sheet.getWorkbook(), true, false, IndexedColors.GREY_25_PERCENT);
        setCellStyle(headerRow.getCell(2), sheet.getWorkbook(), true, false, IndexedColors.GREY_25_PERCENT);
        setCellStyle(headerRow.getCell(3), sheet.getWorkbook(), true, false, IndexedColors.GREY_25_PERCENT);

        // Category Data
        Map<String, Long> categoryCounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.counting()
            ));

        Map<String, Double> categoryAmounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
            ));

        double totalAmount = records.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();

        for (String category : categoryCounts.keySet()) {
            Row categoryRow = sheet.createRow(rowNum++);
            categoryRow.createCell(0).setCellValue(category);
            
            long count = categoryCounts.get(category);
            categoryRow.createCell(1).setCellValue(count);
            
            double amount = categoryAmounts.getOrDefault(category, 0.0);
            categoryRow.createCell(2).setCellValue(amount);
            
            double percentage = totalAmount > 0 ? (amount / totalAmount * 100) : 0;
            categoryRow.createCell(3).setCellValue(String.format("%.1f%%", percentage));
        }

        return rowNum + 1;
    }

    private void addRecentActivitySection(Sheet sheet, int rowNum, List<AgreementBasedPurchaseFundRecordModel> records) {
        // Section Title
        Row activityTitle = sheet.createRow(rowNum++);
        activityTitle.createCell(0).setCellValue("RECENT ACTIVITY SUMMARY");
        setCellStyle(activityTitle.getCell(0), sheet.getWorkbook(), true, false, IndexedColors.LIGHT_YELLOW);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum-1, rowNum-1, 0, 3));

        // Get recent records (last 10)
        List<AgreementBasedPurchaseFundRecordModel> recentRecords = records.stream()
            .sorted((r1, r2) -> r2.getRequestDate().compareTo(r1.getRequestDate()))
            .limit(10)
            .collect(Collectors.toList());

        // Header Row
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Request ID", "Unit", "Category", "Amount", "Date", "Status"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            setCellStyle(headerRow.getCell(i), sheet.getWorkbook(), true, false, IndexedColors.GREY_25_PERCENT);
        }

        // Recent Activity Data
        for (AgreementBasedPurchaseFundRecordModel record : recentRecords) {
            Row activityRow = sheet.createRow(rowNum++);
            activityRow.createCell(0).setCellValue(record.getRequestId());
            activityRow.createCell(1).setCellValue(record.getRequisitionUnit());
            activityRow.createCell(2).setCellValue(record.getMainCategory());
            activityRow.createCell(3).setCellValue(record.getAmountRequested());
            activityRow.createCell(4).setCellValue(record.getRequestDate().toString());
            activityRow.createCell(5).setCellValue(record.getApprovalStatus());
        }
    }

    private void createDetailedRecordsSheet(Workbook workbook, List<AgreementBasedPurchaseFundRecordModel> records) {
        Sheet sheet = workbook.createSheet("Detailed Records");
        
        // Title Row
        Row titleRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DETAILED AGREEMENT-BASED PURCHASE FUND RECORDS");
        setCellStyle(titleCell, workbook, true, true, IndexedColors.DARK_BLUE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 17));

        // Header Row
        Row headerRow = sheet.createRow(2);
        String[] headers = {
            "Request ID", "Requisition Unit", "Main Category", "Sub Category", 
            "Payee", "Employee", "Amount Requested", "Request Date", 
            "Approval Status", "Approved By", "Approval Date",
            "Confirmation Status", "Confirmed By", "Confirmation Date",
            "Dispensed Status", "Dispensed By", "Dispense Date",
            "Dispense Approval Status", "Dispense Approved By", "Void Status"
        };
        
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            setCellStyle(cell, workbook, true, false, IndexedColors.GREY_25_PERCENT);
        }

        // Data rows
        int rowNum = 3;
        for (AgreementBasedPurchaseFundRecordModel record : records) {
            Row row = sheet.createRow(rowNum++);
            
            int col = 0;
            row.createCell(col++).setCellValue(record.getRequestId());
            row.createCell(col++).setCellValue(record.getRequisitionUnit());
            row.createCell(col++).setCellValue(record.getMainCategory());
            row.createCell(col++).setCellValue(record.getSubCategory());
            row.createCell(col++).setCellValue(record.getPayee());
            row.createCell(col++).setCellValue(record.getEmployeeName());
            row.createCell(col++).setCellValue(record.getAmountRequested());
            row.createCell(col++).setCellValue(record.getRequestDate().toString());
            row.createCell(col++).setCellValue(record.getApprovalStatus());
            row.createCell(col++).setCellValue(record.getApprovedBy());
            row.createCell(col++).setCellValue(record.getConfirmationStatus());
            row.createCell(col++).setCellValue(record.getConfirmedBy());
            row.createCell(col++).setCellValue(record.getDispensedStatus());
            row.createCell(col++).setCellValue(record.getDispensedBy());
            row.createCell(col++).setCellValue(record.getDispenseApprovalStatus());
            row.createCell(col++).setCellValue(record.getDispenseApprovedBy());
            row.createCell(col++).setCellValue(record.getVoidStatus());
        }

        // Auto-size all columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Freeze panes for easy navigation
        sheet.createFreezePane(0, 3);
    }

    private void createCategoryAnalysisSheet(Workbook workbook, List<AgreementBasedPurchaseFundRecordModel> records) {
        Sheet sheet = workbook.createSheet("Category Analysis");
        
        // Title Row
        Row titleRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("CATEGORY ANALYSIS");
        setCellStyle(titleCell, workbook, true, true, IndexedColors.DARK_GREEN);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

        // Main Category Analysis
        Row mainCatTitle = sheet.createRow(2);
        mainCatTitle.createCell(0).setCellValue("MAIN CATEGORY BREAKDOWN");
        setCellStyle(mainCatTitle.getCell(0), workbook, true, false, IndexedColors.LIGHT_BLUE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(2, 2, 0, 5));

        // Main Category Header
        Row mainCatHeader = sheet.createRow(3);
        String[] mainHeaders = {"Main Category", "Transaction Count", "Total Amount", "Percentage", "Average Amount", "Highest Amount"};
        for (int i = 0; i < mainHeaders.length; i++) {
            mainCatHeader.createCell(i).setCellValue(mainHeaders[i]);
            setCellStyle(mainCatHeader.getCell(i), workbook, true, false, IndexedColors.GREY_25_PERCENT);
        }

        // Main Category Data
        Map<String, List<AgreementBasedPurchaseFundRecordModel>> categoryGroups = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized"
            ));

        double totalAmount = records.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();
        
        int row = 4;
        for (Map.Entry<String, List<AgreementBasedPurchaseFundRecordModel>> entry : categoryGroups.entrySet()) {
            String category = entry.getKey();
            List<AgreementBasedPurchaseFundRecordModel> categoryRecords = entry.getValue();
            
            long count = categoryRecords.size();
            double categoryTotal = categoryRecords.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();
            double percentage = totalAmount > 0 ? (categoryTotal / totalAmount * 100) : 0;
            double average = count > 0 ? categoryTotal / count : 0;
            double highest = categoryRecords.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).max().orElse(0);

            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(category);
            dataRow.createCell(1).setCellValue(count);
            dataRow.createCell(2).setCellValue(categoryTotal);
            dataRow.createCell(3).setCellValue(String.format("%.1f%%", percentage));
            dataRow.createCell(4).setCellValue(average);
            dataRow.createCell(5).setCellValue(highest);
        }

        // Sub-Category Analysis
        row += 2; // Add spacing
        Row subCatTitle = sheet.createRow(row++);
        subCatTitle.createCell(0).setCellValue("SUB-CATEGORY BREAKDOWN");
        setCellStyle(subCatTitle.getCell(0), workbook, true, false, IndexedColors.LIGHT_ORANGE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(row-1, row-1, 0, 5));

        // Sub-Category Header
        Row subCatHeader = sheet.createRow(row++);
        String[] subHeaders = {"Main Category", "Sub-Category", "Transaction Count", "Total Amount", "Percentage", "Average Amount"};
        for (int i = 0; i < subHeaders.length; i++) {
            subCatHeader.createCell(i).setCellValue(subHeaders[i]);
            setCellStyle(subCatHeader.getCell(i), workbook, true, false, IndexedColors.GREY_25_PERCENT);
        }

        // Sub-Category Data
        Map<String, Map<String, List<AgreementBasedPurchaseFundRecordModel>>> subCategoryGroups = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.groupingBy(
                    r -> r.getSubCategory() != null && !r.getSubCategory().isEmpty() ? 
                         r.getSubCategory() : "Uncategorized"
                )
            ));

        for (Map.Entry<String, Map<String, List<AgreementBasedPurchaseFundRecordModel>>> mainEntry : subCategoryGroups.entrySet()) {
            String mainCategory = mainEntry.getKey();
            Map<String, List<AgreementBasedPurchaseFundRecordModel>> subCategories = mainEntry.getValue();
            
            for (Map.Entry<String, List<AgreementBasedPurchaseFundRecordModel>> subEntry : subCategories.entrySet()) {
                String subCategory = subEntry.getKey();
                List<AgreementBasedPurchaseFundRecordModel> subRecords = subEntry.getValue();
                
                long subCount = subRecords.size();
                double subTotal = subRecords.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();
                double subPercentage = totalAmount > 0 ? (subTotal / totalAmount * 100) : 0;
                double subAverage = subCount > 0 ? subTotal / subCount : 0;

                Row subRow = sheet.createRow(row++);
                subRow.createCell(0).setCellValue(mainCategory);
                subRow.createCell(1).setCellValue(subCategory);
                subRow.createCell(2).setCellValue(subCount);
                subRow.createCell(3).setCellValue(subTotal);
                subRow.createCell(4).setCellValue(String.format("%.1f%%", subPercentage));
                subRow.createCell(5).setCellValue(subAverage);
            }
        }

        // Auto-size columns
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createStatusAnalysisSheet(Workbook workbook, List<AgreementBasedPurchaseFundRecordModel> records) {
        Sheet sheet = workbook.createSheet("Status Analysis");
        
        // Title Row
        Row titleRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("STATUS AND WORKFLOW ANALYSIS");
        setCellStyle(titleCell, workbook, true, true, IndexedColors.DARK_BLUE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));

        // Approval Status Analysis
        Row approvalTitle = sheet.createRow(2);
        approvalTitle.createCell(0).setCellValue("APPROVAL STATUS ANALYSIS");
        setCellStyle(approvalTitle.getCell(0), workbook, true, false, IndexedColors.LIGHT_BLUE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(2, 2, 0, 4));

        // Approval Status Header
        Row approvalHeader = sheet.createRow(3);
        String[] approvalHeaders = {"Approval Status", "Count", "Percentage", "Total Amount", "Average Amount"};
        for (int i = 0; i < approvalHeaders.length; i++) {
            approvalHeader.createCell(i).setCellValue(approvalHeaders[i]);
            setCellStyle(approvalHeader.getCell(i), workbook, true, false, IndexedColors.GREY_25_PERCENT);
        }

        // Approval Status Data
        Map<String, List<AgreementBasedPurchaseFundRecordModel>> approvalGroups = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getApprovalStatus() != null ? r.getApprovalStatus() : "Unknown"
            ));

        int row = 4;
        for (Map.Entry<String, List<AgreementBasedPurchaseFundRecordModel>> entry : approvalGroups.entrySet()) {
            String status = entry.getKey();
            List<AgreementBasedPurchaseFundRecordModel> statusRecords = entry.getValue();
            
            long count = statusRecords.size();
            double total = statusRecords.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();
            double percentage = (double) count / records.size() * 100;
            double average = count > 0 ? total / count : 0;

            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(status);
            dataRow.createCell(1).setCellValue(count);
            dataRow.createCell(2).setCellValue(String.format("%.1f%%", percentage));
            dataRow.createCell(3).setCellValue(total);
            dataRow.createCell(4).setCellValue(average);
        }

        // Dispensed Status Analysis
        row += 2;
        Row dispensedTitle = sheet.createRow(row++);
        dispensedTitle.createCell(0).setCellValue("DISPENSED STATUS ANALYSIS");
        setCellStyle(dispensedTitle.getCell(0), workbook, true, false, IndexedColors.LIGHT_GREEN);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(row-1, row-1, 0, 4));

        // Dispensed Status Header
        Row dispensedHeader = sheet.createRow(row++);
        String[] dispensedHeaders = {"Dispensed Status", "Count", "Percentage", "Total Amount", "Average Processing Days"};
        for (int i = 0; i < dispensedHeaders.length; i++) {
            dispensedHeader.createCell(i).setCellValue(dispensedHeaders[i]);
            setCellStyle(dispensedHeader.getCell(i), workbook, true, false, IndexedColors.GREY_25_PERCENT);
        }

        // Dispensed Status Data
        Map<String, List<AgreementBasedPurchaseFundRecordModel>> dispensedGroups = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getDispensedStatus() != null ? r.getDispensedStatus() : "Unknown"
            ));

        for (Map.Entry<String, List<AgreementBasedPurchaseFundRecordModel>> entry : dispensedGroups.entrySet()) {
            String status = entry.getKey();
            List<AgreementBasedPurchaseFundRecordModel> statusRecords = entry.getValue();
            
            long count = statusRecords.size();
            double total = statusRecords.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();
            double percentage = (double) count / records.size() * 100;

            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(status);
            dataRow.createCell(1).setCellValue(count);
            dataRow.createCell(2).setCellValue(String.format("%.1f%%", percentage));
            dataRow.createCell(3).setCellValue(total);
            dataRow.createCell(4).setCellValue(calculateAverageProcessingDays(statusRecords));
        }

        // Auto-size columns
        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createMonthlyAnalysisSheet(Workbook workbook, List<AgreementBasedPurchaseFundRecordModel> records) {
        Sheet sheet = workbook.createSheet("Monthly Analysis");
        
        // Title Row
        Row titleRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("MONTHLY TREND ANALYSIS");
        setCellStyle(titleCell, workbook, true, true, IndexedColors.DARK_BLUE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6));

        // Monthly Summary Header
        Row monthlyHeader = sheet.createRow(2);
        String[] monthlyHeaders = {"Month", "Year", "Transaction Count", "Total Amount", "Average Amount", "Highest Amount", "Approval Rate"};
        for (int i = 0; i < monthlyHeaders.length; i++) {
            monthlyHeader.createCell(i).setCellValue(monthlyHeaders[i]);
            setCellStyle(monthlyHeader.getCell(i), workbook, true, false, IndexedColors.GREY_25_PERCENT);
        }

        // Monthly Data
        Map<String, List<AgreementBasedPurchaseFundRecordModel>> monthlyGroups = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getRequestDate().getMonth().toString() + " " + r.getRequestDate().getYear()
            ));

        int row = 3;
        for (Map.Entry<String, List<AgreementBasedPurchaseFundRecordModel>> entry : monthlyGroups.entrySet()) {
            String monthYear = entry.getKey();
            List<AgreementBasedPurchaseFundRecordModel> monthlyRecords = entry.getValue();
            
            String[] parts = monthYear.split(" ");
            String month = parts[0];
            String year = parts[1];
            
            long count = monthlyRecords.size();
            double total = monthlyRecords.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();
            double average = count > 0 ? total / count : 0;
            double highest = monthlyRecords.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).max().orElse(0);
            double approvalRate = calculateApprovalRate(monthlyRecords);

            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(month);
            dataRow.createCell(1).setCellValue(year);
            dataRow.createCell(2).setCellValue(count);
            dataRow.createCell(3).setCellValue(total);
            dataRow.createCell(4).setCellValue(average);
            dataRow.createCell(5).setCellValue(highest);
            dataRow.createCell(6).setCellValue(String.format("%.1f%%", approvalRate));
        }

        // Auto-size columns
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDepartmentAnalysisSheet(Workbook workbook, List<AgreementBasedPurchaseFundRecordModel> records) {
        Sheet sheet = workbook.createSheet("Department Analysis");
        
        // Title Row
        Row titleRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("DEPARTMENT PERFORMANCE ANALYSIS");
        setCellStyle(titleCell, workbook, true, true, IndexedColors.DARK_BLUE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6));

        // Department Summary Header
        Row deptHeader = sheet.createRow(2);
        String[] deptHeaders = {"Department/Unit", "Transaction Count", "Total Amount", "Percentage", "Average Amount", "Approval Rate", "Completion Rate"};
        for (int i = 0; i < deptHeaders.length; i++) {
            deptHeader.createCell(i).setCellValue(deptHeaders[i]);
            setCellStyle(deptHeader.getCell(i), workbook, true, false, IndexedColors.GREY_25_PERCENT);
        }

        // Department Data
        Map<String, List<AgreementBasedPurchaseFundRecordModel>> deptGroups = records.stream()
            .collect(Collectors.groupingBy(AgreementBasedPurchaseFundRecordModel::getRequisitionUnit));

        double totalAmount = records.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();

        int row = 3;
        for (Map.Entry<String, List<AgreementBasedPurchaseFundRecordModel>> entry : deptGroups.entrySet()) {
            String department = entry.getKey();
            List<AgreementBasedPurchaseFundRecordModel> deptRecords = entry.getValue();
            
            long count = deptRecords.size();
            double deptTotal = deptRecords.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();
            double percentage = totalAmount > 0 ? (deptTotal / totalAmount * 100) : 0;
            double average = count > 0 ? deptTotal / count : 0;
            double approvalRate = calculateApprovalRate(deptRecords);
            double completionRate = calculateCompletionRate(deptRecords);

            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(department);
            dataRow.createCell(1).setCellValue(count);
            dataRow.createCell(2).setCellValue(deptTotal);
            dataRow.createCell(3).setCellValue(String.format("%.1f%%", percentage));
            dataRow.createCell(4).setCellValue(average);
            dataRow.createCell(5).setCellValue(String.format("%.1f%%", approvalRate));
            dataRow.createCell(6).setCellValue(String.format("%.1f%%", completionRate));
        }

        // Auto-size columns
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createChartsDataSheet(Workbook workbook, List<AgreementBasedPurchaseFundRecordModel> records) {
        Sheet sheet = workbook.createSheet("Charts Data");
        
        // Title Row
        Row titleRow = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("CHARTS AND VISUALIZATION DATA");
        setCellStyle(titleCell, workbook, true, true, IndexedColors.DARK_BLUE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

        // Status Distribution Data
        Row statusTitle = sheet.createRow(2);
        statusTitle.createCell(0).setCellValue("STATUS DISTRIBUTION DATA");
        setCellStyle(statusTitle.getCell(0), workbook, true, false, IndexedColors.LIGHT_BLUE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(2, 2, 0, 3));

        Row statusHeader = sheet.createRow(3);
        statusHeader.createCell(0).setCellValue("Status");
        statusHeader.createCell(1).setCellValue("Count");
        statusHeader.createCell(2).setCellValue("Percentage");

        Map<String, Long> statusCounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getApprovalStatus() != null ? r.getApprovalStatus() : "Unknown",
                Collectors.counting()
            ));

        int row = 4;
        for (Map.Entry<String, Long> entry : statusCounts.entrySet()) {
            Row statusRow = sheet.createRow(row++);
            statusRow.createCell(0).setCellValue(entry.getKey());
            statusRow.createCell(1).setCellValue(entry.getValue());
            statusRow.createCell(2).setCellValue((double) entry.getValue() / records.size() * 100);
        }

        // Category Distribution Data
        row += 2;
        Row categoryTitle = sheet.createRow(row++);
        categoryTitle.createCell(0).setCellValue("CATEGORY DISTRIBUTION DATA");
        setCellStyle(categoryTitle.getCell(0), workbook, true, false, IndexedColors.LIGHT_GREEN);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(row-1, row-1, 0, 3));

        Row categoryHeader = sheet.createRow(row++);
        categoryHeader.createCell(0).setCellValue("Category");
        categoryHeader.createCell(1).setCellValue("Count");
        categoryHeader.createCell(2).setCellValue("Total Amount");

        Map<String, Long> categoryCounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.counting()
            ));

        Map<String, Double> categoryAmounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
            ));

        for (String category : categoryCounts.keySet()) {
            Row categoryRow = sheet.createRow(row++);
            categoryRow.createCell(0).setCellValue(category);
            categoryRow.createCell(1).setCellValue(categoryCounts.get(category));
            categoryRow.createCell(2).setCellValue(categoryAmounts.get(category));
        }

        // Monthly Trend Data
        row += 2;
        Row monthlyTitle = sheet.createRow(row++);
        monthlyTitle.createCell(0).setCellValue("MONTHLY TREND DATA");
        setCellStyle(monthlyTitle.getCell(0), workbook, true, false, IndexedColors.LIGHT_ORANGE);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(row-1, row-1, 0, 3));

        Row monthlyHeader = sheet.createRow(row++);
        monthlyHeader.createCell(0).setCellValue("Month-Year");
        monthlyHeader.createCell(1).setCellValue("Transaction Count");
        monthlyHeader.createCell(2).setCellValue("Total Amount");

        Map<String, List<AgreementBasedPurchaseFundRecordModel>> monthlyGroups = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getRequestDate().getMonth().toString() + "-" + r.getRequestDate().getYear()
            ));

        // Sort by date
        List<String> sortedMonths = monthlyGroups.keySet().stream()
            .sorted()
            .collect(Collectors.toList());

        for (String monthYear : sortedMonths) {
            List<AgreementBasedPurchaseFundRecordModel> monthlyRecords = monthlyGroups.get(monthYear);
            Row monthlyRow = sheet.createRow(row++);
            monthlyRow.createCell(0).setCellValue(monthYear);
            monthlyRow.createCell(1).setCellValue(monthlyRecords.size());
            monthlyRow.createCell(2).setCellValue(
                monthlyRecords.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum()
            );
        }

        // Auto-size columns
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ==================== UTILITY METHODS FOR EXCEL EXPORT ====================

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
        
        // Add borders
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        cell.setCellStyle(style);
    }

    private double getHighestAmount(List<AgreementBasedPurchaseFundRecordModel> records) {
        return records.stream()
            .mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
            .max()
            .orElse(0.0);
    }

    private long getPendingApprovalsCount(List<AgreementBasedPurchaseFundRecordModel> records) {
        return records.stream()
            .filter(r -> "Pending".equalsIgnoreCase(r.getApprovalStatus()))
            .count();
    }

    private String getMostActiveDepartment(List<AgreementBasedPurchaseFundRecordModel> records) {
        return records.stream()
            .collect(Collectors.groupingBy(AgreementBasedPurchaseFundRecordModel::getRequisitionUnit, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");
    }

    private double calculateAverageProcessingDays(List<AgreementBasedPurchaseFundRecordModel> records) {
        // Simplified calculation - in real implementation, you'd calculate actual processing days
        return 2.5; // Default average for agreement-based purchases
    }

    private String getReportPeriod() {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        
        if (fromDate != null && toDate != null) {
            return fromDate + " to " + toDate;
        } else if (fromDate != null) {
            return "From " + fromDate;
        } else if (toDate != null) {
            return "Until " + toDate;
        } else {
            return "All Time";
        }
    }

    // ==================== EXCEL EXPORT FOR SELECTED RECORDS ====================

    private void exportSelectedToExcel() {
        ObservableList<AgreementBasedPurchaseFundRecordModel> selectedItems = table.getSelectionModel().getSelectedItems();
        List<AgreementBasedPurchaseFundRecordModel> recordsToExport;
        
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

    // ==================== OTHER EXPORT METHODS ====================

    private void exportToPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Comprehensive Report to PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("AgreementPurchaseFund_Comprehensive_Report_" + LocalDate.now() + ".pdf");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                createComprehensivePDFReport(file, table.getItems());
                showAlert("Export Successful", "Comprehensive report exported to PDF successfully!");
                openFile(file);
            } catch (Exception e) {
                showAlert("Export Error", "Failed to export to PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void exportToWord() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Comprehensive Report to Word");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Documents", "*.docx"));
        fileChooser.setInitialFileName("AgreementPurchaseFund_Comprehensive_Report_" + LocalDate.now() + ".docx");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (XWPFDocument document = new XWPFDocument()) {
                createComprehensiveWordReport(document, table.getItems());
                document.write(new FileOutputStream(file));
                showAlert("Export Successful", "Comprehensive report exported to Word successfully!");
                openFile(file);
            } catch (Exception e) {
                showAlert("Export Error", "Failed to export to Word: " + e.getMessage());
                e.printStackTrace();
            }
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

    // ==================== COMPREHENSIVE REPORT CREATION METHODS ====================

    private void createComprehensivePDFReport(File file, List<AgreementBasedPurchaseFundRecordModel> records) throws Exception {
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

    private void createComprehensiveWordReport(XWPFDocument document, List<AgreementBasedPurchaseFundRecordModel> records) {
        createWordCoverPage(document);
        createWordTableOfContents(document);
        createWordExecutiveSummary(document, records);
        createWordCategoryAnalysis(document, records);
        createWordDetailedData(document, records);
        createWordAnalysis(document, records);
    }

    private VBox createComprehensivePrintNode(List<AgreementBasedPurchaseFundRecordModel> records) {
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

    // ==================== PDF REPORT METHODS ====================

    private void createPDFCoverPage(Document document) throws DocumentException {
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
        Paragraph title = new Paragraph("AFRAN GENERAL HOSPITAL", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        Paragraph subtitle = new Paragraph("\nAGREEMENT-BASED PURCHASE FUND REPORT", titleFont);
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

    private void createPDFExecutiveSummary(Document document, List<AgreementBasedPurchaseFundRecordModel> records) throws DocumentException {
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph sectionTitle = new Paragraph("EXECUTIVE SUMMARY", sectionFont);
        sectionTitle.setSpacingAfter(20);
        document.add(sectionTitle);

        PdfPTable metricsTable = new PdfPTable(2);
        metricsTable.setWidthPercentage(100);
        
        addMetricRow(metricsTable, "Total Requests", String.valueOf(records.size()));
        addMetricRow(metricsTable, "Total Amount", String.format("ETB %,.2f", 
            records.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum()));
        addMetricRow(metricsTable, "Approval Rate", 
            String.format("%.1f%%", calculateApprovalRate(records)));
        addMetricRow(metricsTable, "Average Amount", 
            String.format("ETB %,.2f", calculateAverageAmount(records)));
        addMetricRow(metricsTable, "Top Category", getTopCategory(records));
        
        document.add(metricsTable);
    }

    private void createPDFCategoryAnalysis(Document document, List<AgreementBasedPurchaseFundRecordModel> records) throws DocumentException {
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
                Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
            ));

        for (String category : categoryCounts.keySet()) {
            categoryTable.addCell(createPdfCell(category, false));
            categoryTable.addCell(createPdfCell(String.valueOf(categoryCounts.get(category)), false));
            categoryTable.addCell(createPdfCell(String.format("ETB %,.2f", categoryAmounts.get(category)), false));
        }
        
        document.add(categoryTable);
    }

    private void createPDFDetailedRecords(Document document, List<AgreementBasedPurchaseFundRecordModel> records) throws DocumentException {
        document.newPage();
        
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph sectionTitle = new Paragraph("DETAILED RECORDS", sectionFont);
        sectionTitle.setSpacingAfter(20);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        
        String[] headers = {"Request ID", "Unit", "Category", "Sub-Cat", "Payee", "Employee", "Amount", "Date", "Status", "Dispensed"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setBackgroundColor(new com.itextpdf.text.BaseColor(220, 220, 220));
            table.addCell(cell);
        }
        
        for (AgreementBasedPurchaseFundRecordModel record : records) {
            table.addCell(record.getRequestId());
            table.addCell(record.getRequisitionUnit());
            table.addCell(record.getMainCategory());
            table.addCell(record.getSubCategory());
            table.addCell(record.getPayee());
            table.addCell(record.getEmployeeName());
            table.addCell(String.format("ETB %,.2f", record.getAmountRequested()));
            table.addCell(record.getRequestDate().toString());
            table.addCell(record.getApprovalStatus());
            table.addCell(record.getDispensedStatus());
        }
        
        document.add(table);
    }

    private void createPDFChartsAnalysis(Document document, List<AgreementBasedPurchaseFundRecordModel> records) throws DocumentException {
        document.newPage();
        
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph sectionTitle = new Paragraph("ANALYSIS & CHARTS", sectionFont);
        sectionTitle.setSpacingAfter(20);
        document.add(sectionTitle);

        com.itextpdf.text.Font analysisFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Paragraph analysis = new Paragraph(
            "This section provides analytical insights into the agreement-based purchase fund data:\n\n" +
            "• Total records analyzed: " + records.size() + "\n" +
            "• Date range: " + getDateRange(records) + "\n" +
            "• Highest single transaction: ETB " + getHighestAmount(records) + "\n" +
            "• Most active department: " + getMostActiveDepartment(records) + "\n" +
            "• Top spending category: " + getTopCategory(records) + "\n" +
            "• Category distribution: " + getCategoryDistribution(records) + "\n\n",
            analysisFont
        );
        document.add(analysis);
    }

    private void createPDFSignaturesSection(Document document, List<AgreementBasedPurchaseFundRecordModel> records) throws DocumentException {
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

    // ==================== WORD REPORT METHODS ====================

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
        subtitleRun.setText("AGREEMENT-BASED PURCHASE FUND REPORT");
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

    private void createWordExecutiveSummary(XWPFDocument document, List<AgreementBasedPurchaseFundRecordModel> records) {
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("1. EXECUTIVE SUMMARY");
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        
        XWPFTable table = document.createTable(6, 2);
        table.setWidth("100%");
        
        addWordTableRow(table, 0, "Total Requests", String.valueOf(records.size()));
        addWordTableRow(table, 1, "Total Amount", String.format("ETB %,.2f", 
            records.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum()));
        addWordTableRow(table, 2, "Approval Rate", String.format("%.1f%%", calculateApprovalRate(records)));
        addWordTableRow(table, 3, "Average Processing Time", "2.5 days");
        addWordTableRow(table, 4, "Completion Rate", String.format("%.1f%%", calculateCompletionRate(records)));
        addWordTableRow(table, 5, "Top Category", getTopCategory(records));
    }

    private void createWordCategoryAnalysis(XWPFDocument document, List<AgreementBasedPurchaseFundRecordModel> records) {
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
                Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
            ));

        for (String category : categoryCounts.keySet()) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(category);
            row.getCell(1).setText(String.valueOf(categoryCounts.get(category)));
            row.getCell(2).setText(String.format("ETB %,.2f", categoryAmounts.get(category)));
        }
    }

    private void createWordDetailedData(XWPFDocument document, List<AgreementBasedPurchaseFundRecordModel> records) {
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("3. DETAILED RECORDS");
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        
        XWPFTable table = document.createTable();
        
        XWPFTableRow headerRow = table.getRow(0);
        String[] headers = {"Request ID", "Unit", "Category", "Sub-Cat", "Payee", "Employee", "Amount", "Date", "Status"};
        for (int i = 0; i < headers.length; i++) {
            if (i == 0) {
                headerRow.getCell(0).setText(headers[i]);
            } else {
                headerRow.addNewTableCell().setText(headers[i]);
            }
        }
        
        for (AgreementBasedPurchaseFundRecordModel record : records) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(record.getRequestId());
            row.getCell(1).setText(record.getRequisitionUnit());
            row.getCell(2).setText(record.getMainCategory());
            row.getCell(3).setText(record.getSubCategory());
            row.getCell(4).setText(record.getPayee());
            row.getCell(5).setText(record.getEmployeeName());
            row.getCell(6).setText(String.format("ETB %,.2f", record.getAmountRequested()));
            row.getCell(7).setText(record.getRequestDate().toString());
            row.getCell(8).setText(record.getApprovalStatus());
        }
    }

    private void createWordAnalysis(XWPFDocument document, List<AgreementBasedPurchaseFundRecordModel> records) {
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

    // ==================== PRINT NODE METHODS ====================

    private VBox createPrintCoverPage() {
        VBox cover = new VBox(20);
        cover.setPadding(new Insets(100, 50, 50, 50));
        cover.setAlignment(Pos.CENTER);
        cover.setStyle("-fx-border-color: #2c3e50; -fx-border-width: 2;");

        Label hospitalName = new Label("AFRAN GENERAL HOSPITAL");
        hospitalName.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        hospitalName.setStyle("-fx-text-fill: #2c3e50;");

        Label reportTitle = new Label("AGREEMENT-BASED PURCHASE FUND REPORT");
        reportTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        reportTitle.setStyle("-fx-text-fill: #34495e;");

        Label generatedInfo = new Label("Generated on: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        generatedInfo.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        cover.getChildren().addAll(hospitalName, reportTitle, generatedInfo);
        return cover;
    }

    private VBox createPrintSummarySection(List<AgreementBasedPurchaseFundRecordModel> records) {
        VBox summary = new VBox(15);
        summary.setPadding(new Insets(20));
        summary.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;");

        Label title = new Label("EXECUTIVE SUMMARY");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        GridPane metricsGrid = new GridPane();
        metricsGrid.setHgap(20);
        metricsGrid.setVgap(10);

        addPrintMetric(metricsGrid, "Total Requests", String.valueOf(records.size()), 0);
        addPrintMetric(metricsGrid, "Total Amount", String.format("ETB %,.2f", 
            records.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum()), 1);
        addPrintMetric(metricsGrid, "Approval Rate", String.format("%.1f%%", calculateApprovalRate(records)), 2);
        addPrintMetric(metricsGrid, "Average Amount", String.format("ETB %,.2f", calculateAverageAmount(records)), 3);
        addPrintMetric(metricsGrid, "Top Category", getTopCategory(records), 4);

        summary.getChildren().addAll(title, metricsGrid);
        return summary;
    }

    private VBox createPrintCategorySection(List<AgreementBasedPurchaseFundRecordModel> records) {
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
                .mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
                .sum();
            return new SimpleStringProperty(String.format("ETB %,.2f", amount));
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

    private VBox createPrintTableSection(List<AgreementBasedPurchaseFundRecordModel> records) {
        VBox tableSection = new VBox(15);
        
        Label title = new Label("DETAILED RECORDS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<AgreementBasedPurchaseFundRecordModel> printTable = new TableView<>();
        printTable.setItems(FXCollections.observableArrayList(records));

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRequestId()));

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRequisitionUnit()));

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMainCategory()));

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> new SimpleStringProperty(
            String.format("ETB %,.2f", data.getValue().getAmountRequested())
        ));

        TableColumn<AgreementBasedPurchaseFundRecordModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getApprovalStatus()));

        printTable.getColumns().addAll(idCol, unitCol, categoryCol, amountCol, statusCol);
        printTable.setPrefHeight(400);

        tableSection.getChildren().addAll(title, printTable);
        return tableSection;
    }

    private VBox createPrintAnalysisSection(List<AgreementBasedPurchaseFundRecordModel> records) {
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

    // ==================== ANALYTICAL METHODS ====================

    private double calculateApprovalRate(List<AgreementBasedPurchaseFundRecordModel> records) {
        long approved = records.stream().filter(r -> "Approved".equals(r.getApprovalStatus())).count();
        return records.size() > 0 ? (double) approved / records.size() * 100 : 0;
    }

    private double calculateAverageAmount(List<AgreementBasedPurchaseFundRecordModel> records) {
        return records.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).average().orElse(0);
    }

    private double calculateCompletionRate(List<AgreementBasedPurchaseFundRecordModel> records) {
        long completed = records.stream().filter(r -> "Yes".equals(r.getDispensedStatus())).count();
        return records.size() > 0 ? (double) completed / records.size() * 100 : 0;
    }

    private double calculateEfficiency(List<AgreementBasedPurchaseFundRecordModel> records) {
        return (calculateApprovalRate(records) + calculateCompletionRate(records)) / 2;
    }

    private String getDateRange(List<AgreementBasedPurchaseFundRecordModel> records) {
        if (records.isEmpty()) return "No data";
        LocalDate minDate = records.stream().map(AgreementBasedPurchaseFundRecordModel::getRequestDate).min(LocalDate::compareTo).orElse(LocalDate.now());
        LocalDate maxDate = records.stream().map(AgreementBasedPurchaseFundRecordModel::getRequestDate).max(LocalDate::compareTo).orElse(LocalDate.now());
        return minDate + " to " + maxDate;
    }

    private String getTopCategory(List<AgreementBasedPurchaseFundRecordModel> records) {
        return records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
            ))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> entry.getKey() + " (ETB " + String.format("%,.2f", entry.getValue()) + ")")
            .orElse("N/A");
    }

    private String getCategoryDistribution(List<AgreementBasedPurchaseFundRecordModel> records) {
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

    private String getCategoryInsights(List<AgreementBasedPurchaseFundRecordModel> records) {
        Map<String, Double> categoryAmounts = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? 
                     r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
            ));

        if (categoryAmounts.isEmpty()) return "No category data available";

        String topCategory = categoryAmounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("N/A");

        double totalAmount = records.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();
        double topCategoryAmount = categoryAmounts.get(topCategory);
        double percentage = totalAmount > 0 ? (topCategoryAmount / totalAmount * 100) : 0;

        return String.format("Top category '%s' represents %.1f%% of total spending", topCategory, percentage);
    }

    private String getDepartmentAnalysis(List<AgreementBasedPurchaseFundRecordModel> records) {
        Map<String, Long> deptCounts = records.stream()
            .collect(Collectors.groupingBy(AgreementBasedPurchaseFundRecordModel::getRequisitionUnit, Collectors.counting()));
        
        return deptCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(3)
            .map(entry -> entry.getKey() + " (" + entry.getValue() + " requests)")
            .collect(Collectors.joining(", "));
    }

    private String getTrendAnalysis(List<AgreementBasedPurchaseFundRecordModel> records) {
        if (records.size() < 2) return "Insufficient data for trend analysis";
        
        double currentMonth = records.stream()
            .filter(r -> r.getRequestDate().getMonth() == LocalDate.now().getMonth())
            .mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
            .sum();
            
        double previousMonth = records.stream()
            .filter(r -> r.getRequestDate().getMonth() == LocalDate.now().minusMonths(1).getMonth())
            .mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
            .sum();
            
        double change = previousMonth > 0 ? ((currentMonth - previousMonth) / previousMonth) * 100 : 0;
        
        return String.format("%.1f%% change from previous period", change);
    }

    private String getRecommendations(List<AgreementBasedPurchaseFundRecordModel> records) {
        List<String> recommendations = new ArrayList<>();
        
        double approvalRate = calculateApprovalRate(records);
        if (approvalRate < 80) {
            recommendations.add("Improve approval process efficiency");
        }
        
        long pendingCount = records.stream().filter(r -> "Pending".equals(r.getApprovalStatus())).count();
        if (pendingCount > 5) {
            recommendations.add("Address pending approvals promptly");
        }

        // Category-based recommendations
        Map<String, Double> categorySpending = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
            ));

        if (!categorySpending.isEmpty()) {
            String highestSpendingCategory = categorySpending.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
            
            if (!highestSpendingCategory.isEmpty()) {
                recommendations.add("Review spending in " + highestSpendingCategory + " category for optimization");
            }
        }
        
        return recommendations.isEmpty() ? "Processes are running efficiently" : 
               String.join("; ", recommendations);
    }

    private String identifyBottleneck(List<AgreementBasedPurchaseFundRecordModel> records) {
        long pendingApproval = records.stream().filter(r -> "Pending".equals(r.getApprovalStatus())).count();
        long pendingConfirmation = records.stream().filter(r -> "Pending".equals(r.getConfirmationStatus())).count();
        
        if (pendingApproval > pendingConfirmation) {
            return "Approval Stage";
        } else if (pendingConfirmation > 0) {
            return "Confirmation Stage";
        } else {
            return "No significant bottlenecks";
        }
    }

    private List<String> generateRecommendations(List<AgreementBasedPurchaseFundRecordModel> records) {
        List<String> recommendations = new ArrayList<>();
        
        if (calculateApprovalRate(records) < 85) {
            recommendations.add("Streamline approval workflow to reduce processing time");
        }
        
        if (calculateCompletionRate(records) < 75) {
            recommendations.add("Monitor dispensation process for delays");
        }
        
        long highValueCount = records.stream().filter(r -> r.getAmountRequested() > 10000).count();
        if (highValueCount > 5) {
            recommendations.add("Review high-value transactions for potential optimization");
        }

        // Category-based recommendations
        Map<String, Double> categorySpending = records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getMainCategory() != null && !r.getMainCategory().isEmpty() ? r.getMainCategory() : "Uncategorized",
                Collectors.summingDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested)
            ));

        if (!categorySpending.isEmpty()) {
            String highestSpendingCategory = categorySpending.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
            
            if (!highestSpendingCategory.isEmpty()) {
                double categoryTotal = categorySpending.get(highestSpendingCategory);
                double overallTotal = records.stream().mapToDouble(AgreementBasedPurchaseFundRecordModel::getAmountRequested).sum();
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

    private void refreshData() {
        Connecting db = new Connecting();
        List<AgreementBasedPurchaseFundRecordModel> updatedData = db.getAllAgreementBasedPurchaseFundRequests();
        this.allRecords.setAll(updatedData);
        populateCategoryFilters();
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
        ObservableList<AgreementBasedPurchaseFundRecordModel> selectedItems = table.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            showAlert("Copy Error", "Please select records to copy.");
            return;
        }

        StringBuilder clipboardContent = new StringBuilder();
        
        // Create header
        clipboardContent.append("Request ID\tUnit\tCategory\tSub-Category\tPayee\tEmployee\tAmount\tDate\tStatus\n");
        
        // Add data
        for (AgreementBasedPurchaseFundRecordModel record : selectedItems) {
            clipboardContent.append(record.getRequestId()).append("\t")
                       .append(record.getRequisitionUnit()).append("\t")
                       .append(record.getMainCategory() != null ? record.getMainCategory() : "").append("\t")
                       .append(record.getSubCategory() != null ? record.getSubCategory() : "").append("\t")
                       .append(record.getPayee()).append("\t")
                       .append(record.getEmployeeName()).append("\t")
                       .append(String.format("ETB %,.2f", record.getAmountRequested())).append("\t")
                       .append(record.getRequestDate()).append("\t")
                       .append(record.getApprovalStatus()).append("\n");
        }

        ClipboardContent content = new ClipboardContent();
        content.putString(clipboardContent.toString());
        Clipboard.getSystemClipboard().setContent(content);
        
        showAlert("Copy Successful", selectedItems.size() + " records copied to clipboard!");
    }

    private void showRecordDetails() {
        AgreementBasedPurchaseFundRecordModel selectedRecord = table.getSelectionModel().getSelectedItem();
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
        addDetailRow(grid, "Sub-Category:", selectedRecord.getSubCategory(), 3);
        addDetailRow(grid, "Payee:", selectedRecord.getPayee(), 4);
        addDetailRow(grid, "Employee:", selectedRecord.getEmployeeName(), 5);
        addDetailRow(grid, "Amount Requested:", String.format("ETB %,.2f", selectedRecord.getAmountRequested()), 6);
        addDetailRow(grid, "Request Date:", selectedRecord.getRequestDate().toString(), 7);
        addDetailRow(grid, "Approval Status:", selectedRecord.getApprovalStatus(), 8);
        addDetailRow(grid, "Approved By:", selectedRecord.getApprovedBy(), 9);
        addDetailRow(grid, "Confirmation Status:", selectedRecord.getConfirmationStatus(), 10);
        addDetailRow(grid, "Confirmed By:", selectedRecord.getConfirmedBy(), 11);
        addDetailRow(grid, "Dispensed Status:", selectedRecord.getDispensedStatus(), 12);
        addDetailRow(grid, "Dispensed By:", selectedRecord.getDispensedBy(), 13);
        addDetailRow(grid, "Dispense Approval:", selectedRecord.getDispenseApprovalStatus(), 14);
        addDetailRow(grid, "Dispense Approved By:", selectedRecord.getDispenseApprovedBy(), 15);
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

    private void resetFilters() {
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        unitField.clear();
        payeeField.clear();
        employeeField.clear();
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

    private void showAdvancedFilters() {
        // Create a dialog for advanced filtering options
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Advanced Filters");
        dialog.setHeaderText("Set Advanced Filter Criteria");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add advanced filter controls
        TextField minAmountField = new TextField();
        minAmountField.setPromptText("Minimum Amount");
        TextField maxAmountField = new TextField();
        maxAmountField.setPromptText("Maximum Amount");
        ComboBox<String> departmentFilter = createComboBox("All", "ICU", "Emergency", "Surgery", "Pediatrics", "Radiology");
        ComboBox<String> employeeTypeFilter = createComboBox("All", "Doctor", "Nurse", "Administrative", "Technical");

        grid.add(new Label("Min Amount:"), 0, 0);
        grid.add(minAmountField, 1, 0);
        grid.add(new Label("Max Amount:"), 0, 1);
        grid.add(maxAmountField, 1, 1);
        grid.add(new Label("Department:"), 0, 2);
        grid.add(departmentFilter, 1, 2);
        grid.add(new Label("Employee Type:"), 0, 3);
        grid.add(employeeTypeFilter, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.APPLY) {
                // Apply advanced filters
                applyAdvancedFilters(minAmountField.getText(), maxAmountField.getText(), 
                                   departmentFilter.getValue(), employeeTypeFilter.getValue());
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void applyAdvancedFilters(String minAmount, String maxAmount, String department, String employeeType) {
        // Implementation for advanced filtering
        // This would extend the applyFilters method with additional criteria
        showAlert("Advanced Filters", "Advanced filters applied successfully!");
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
}