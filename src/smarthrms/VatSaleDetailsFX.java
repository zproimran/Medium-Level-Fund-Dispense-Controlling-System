package smarthrms;

import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Desktop;
import java.awt.print.PrinterJob;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class VatSaleDetailsFX extends BorderPane {

    private final Connecting con = new Connecting();
    private final String currentUser;

    private final ObservableList<VatSaleModel> masterList = FXCollections.observableArrayList();
    private final ObservableList<VatSaleModel> filteredList = FXCollections.observableArrayList();
    private final ObservableList<VatSaleModel> activeList = FXCollections.observableArrayList();
    private final TableView<VatSaleModel> table = new TableView<>();

    private ComboBox<String> vatCategory, calendarType;
    private ComboBox<Integer> saleType, unitMeasure;
    private javafx.scene.control.TextField buyerTin, buyerName, receiptNumber,mrcNumber, description, quantity, unitPrice;
    private DatePicker datePicker, fromDatePicker, toDatePicker;
    private Label totalValue, vatAmount, totalAfterVat, footerTotal, validationLabel, activeTotalLabel;
    private javafx.scene.control.TextField searchField;
    private Pagination pagination;
    private ScrollPane scrollPane;
    
    // Filter options
    private ComboBox<String> viewFilter;
    private CheckBox showVoidedCheckBox;

    private static final int ROWS_PER_PAGE = 20;
    
    // Validation patterns
    private static final Pattern TIN_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern RECEIPT_MACHINE_PATTERN = Pattern.compile("^FS\\d{8}$");
    private static final Pattern RECEIPT_MANUAL_PATTERN = Pattern.compile("^M\\d+$");

    public VatSaleDetailsFX(String username) {
        this.currentUser = username;
        buildUI();
        loadPage(0);
        setDefaults();
    }

    private void buildUI() {
        // Create a main container with scrolling
        scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: lightgray;");
        
        VBox mainContainer = new VBox();
        mainContainer.setPadding(new Insets(10));
        mainContainer.setSpacing(10);

        Label title = new Label("VAT SALES DETAILS MANAGEMENT");
        title.setFont(Font.font(20));
        title.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold;");

        TitledPane documentationPane = createDocumentationPane();

        // Validation label
        validationLabel = new Label();
        validationLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        validationLabel.setVisible(false);

        VBox topSection = new VBox(8,
                title,
                documentationPane,
                validationLabel,
                createForm(),
                createButtons(),
                createSearchBar(),
                createDateRangeFilter()
        );

        buildTable();

        // Create footer totals section
        HBox totalsBox = new HBox(20);
        totalsBox.setPadding(new Insets(10, 0, 5, 0));
        totalsBox.setAlignment(Pos.CENTER_LEFT);
        
        footerTotal = new Label("Grand Total (All): 0.00");
        footerTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        
        activeTotalLabel = new Label("Active Total: 0.00");
        activeTotalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #27ae60;");
        
        totalsBox.getChildren().addAll(footerTotal, activeTotalLabel);

        // Create a container for table with constraints to make it larger
        VBox tableContainer = new VBox();
        tableContainer.setSpacing(5);
        
        // Add a label for table
        Label tableLabel = new Label("VAT Sales Records");
        tableLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #34495e;");
        
        // Set table properties for better display
        table.setMinHeight(400);
        table.setPrefHeight(500);
        table.setMaxHeight(Double.MAX_VALUE);
        
        // Create a container for the table with scroll bars
        StackPane tableWrapper = new StackPane();
        tableWrapper.getChildren().add(table);
        tableWrapper.setPadding(new Insets(5));
        tableWrapper.setStyle("-fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-color: white;");
        
        VBox.setVgrow(tableWrapper, Priority.ALWAYS);
        
        // Create filter controls
        HBox filterBox = createViewFilterControls();
        
        tableContainer.getChildren().addAll(tableLabel, filterBox, tableWrapper, totalsBox);
        
        // Create center section with proper spacing
        VBox centerBox = new VBox(10);
        centerBox.getChildren().add(tableContainer);
        VBox.setVgrow(centerBox, Priority.ALWAYS);
        
        pagination = new Pagination();
        pagination.setPageFactory(this::createPage);
        
        // Add everything to main container
        mainContainer.getChildren().addAll(topSection, centerBox, pagination);
        
        // Set the main container as content of scroll pane
        scrollPane.setContent(mainContainer);
        
        // Set scroll pane as center of BorderPane
        setCenter(scrollPane);
        
        // Make the BorderPane expand to fill available space
        setPrefSize(1200, 850);
    }

    private HBox createViewFilterControls() {
        HBox filterBox = new HBox(10);
        filterBox.setPadding(new Insets(5, 0, 10, 0));
        
        Label filterLabel = new Label("View:");
        filterLabel.setStyle("-fx-font-weight: bold;");
        
        viewFilter = new ComboBox<>();
        viewFilter.getItems().addAll("All Records", "Active Only", "Voided Only");
        viewFilter.setValue("Active Only");
        viewFilter.setStyle("-fx-background-color: white; -fx-border-color: #3498db;");
        viewFilter.setPrefWidth(120);
        viewFilter.setOnAction(e -> applyViewFilter());
        
        showVoidedCheckBox = new CheckBox("Show Voided Records");
        showVoidedCheckBox.setSelected(false);
        showVoidedCheckBox.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        showVoidedCheckBox.setOnAction(e -> toggleVoidedVisibility());
        
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        refreshBtn.setOnAction(e -> refreshData());
        
        filterBox.getChildren().addAll(filterLabel, viewFilter, showVoidedCheckBox, refreshBtn);
        return filterBox;
    }

    private void applyViewFilter() {
        String filter = viewFilter.getValue();
        if (filter == null) return;
        
        switch (filter) {
            case "All Records":
                table.setItems(masterList);
                break;
            case "Active Only":
                FilteredList<VatSaleModel> activeFiltered = masterList.filtered(sale -> !sale.isVoided());
                activeList.setAll(activeFiltered);
                table.setItems(activeList);
                break;
            case "Voided Only":
                FilteredList<VatSaleModel> voidedFiltered = masterList.filtered(VatSaleModel::isVoided);
                table.setItems(voidedFiltered);
                break;
        }
        updateTotals();
    }

    private void toggleVoidedVisibility() {
        if (showVoidedCheckBox.isSelected()) {
            table.setItems(masterList);
        } else {
            FilteredList<VatSaleModel> activeFiltered = masterList.filtered(sale -> !sale.isVoided());
            activeList.setAll(activeFiltered);
            table.setItems(activeList);
        }
        updateTotals();
    }

    private void refreshData() {
        loadPage(pagination.getCurrentPageIndex());
        showAlert("✅ Data refreshed successfully!");
    }

    private TitledPane createDocumentationPane() {
        TextArea docText = new TextArea(
        "📋 VAT SALES ENTRY GUIDE\n\n" +
        "1. VOIDING FUNCTIONALITY:\n" +
        "   • Records can be voided instead of deleted\n" +
        "   • Voided records appear in RED color\n" +
        "   • Voided records are EXCLUDED from grand total\n" +
        "   • Voided records are NOT exported in reports\n" +
        "   • Voiding requires a reason (mandatory)\n\n" +
        "2. VAT CATEGORY:\n   • G = GOODS (DEFAULT)\n   • S = SERVICES\n\n" +
        "3. CALENDAR TYPE:\n   • G = GREGORIAN (DEFAULT)\n   • E = ETHIOPIAN\n\n" +
        "4. SALE TYPE:\n   • 1 = Taxable Sale (Line no.5) ← DEFAULT\n" +
        "   • 2 = Zero Rated Sale (Line no.15)\n" +
        "   • 3 = Tax Exempted Sale (Line no.20)\n\n" +
        "5. MRC FIELD RULES:\n" +
        "   • MANDATORY for machine receipts (starting with FS...)\n" +
        "   • OPTIONAL for manual receipts (starting with M...)\n" +
        "   • Field highlights yellow when FS receipt is entered\n\n" +
        "6. VOIDING RULES:\n" +
        "   • Only active records can be voided\n" +
        "   • Voided records can be un-voided if needed\n" +
        "   • Voiding is logged with user and timestamp\n" +
        "   • Voided records remain in database for audit\n\n" +
        "7. FILTERING:\n" +
        "   • Use 'View' dropdown to filter records\n" +
        "   • Toggle checkbox to show/hide voided records\n" +
        "   • Export functions always exclude voided records\n\n" +
        "8. BUYER TIN: Must be exactly 10 digits (e.g., 1234567890) - Optional\n\n" +
        "9. BUYER NAME: Required if buyer has no TIN or item sold outside Ethiopia - Optional\n\n" +
        "10. RECEIPT NUMBER:\n   • Machine receipt: FS followed by 8 digits (e.g., FS12345678)\n" +
        "   • Manual receipt: M followed by any digits (e.g., M1234)\n\n" +
        "11. UNIT OF MEASURE:\n   • 2 = KG\n   • 3 = ML\n   • 4 = GM\n   • 5 = LIT\n" +
        "   • 6 = MT\n   • 7 = PCS (DEFAULT)\n   • 8 = CT\n   • 9 = OTHER\n   • 10 = PC\n\n" +
        "12. MANDATORY FIELDS: Date, Receipt No, Description, Quantity, Unit Price\n" +
        "   • MRC is ADDITIONALLY REQUIRED for FS receipts\n\n" +
        "13. CALCULATIONS:\n" +
        "   • Total Value = Quantity × Unit Price\n" +
        "   • VAT = 15% (for taxable sales only - Sale Type 1)\n" +
        "   • Value After VAT = Total Value + VAT\n\n" +
        "14. DATE RANGE FILTERING:\n" +
        "   • Use the date range filter to view sales between specific dates\n" +
        "   • Export/Print filtered data only\n" +
        "   • Clear filter to view all records\n\n" +
        "⚠️ IMPORTANT NOTES:\n" +
        "• Only Sale Type 1 (Taxable) charges 15% VAT\n" +
        "• Sale Type 2 (Zero Rated) and 3 (Exempt) have 0% VAT\n" +
        "• MRC is MANDATORY for machine receipts (FS...)\n" +
        "• Receipt number format is strictly enforced\n" +
        "• All numeric fields must not contain commas or special characters\n" +
        "• Voided records are excluded from all financial calculations and exports"
    );

        docText.setEditable(false);
        docText.setWrapText(true);
        docText.setPrefHeight(450);
        docText.setMinHeight(450);
        docText.setMaxHeight(450);
        docText.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 12px; " +
                         "-fx-background-color: #f8f9fa;");

        TitledPane titledPane = new TitledPane("📘 VAT Sales Documentation & Rules", docText);
        titledPane.setExpanded(false);
        titledPane.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
        titledPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        titledPane.setMaxHeight(Double.MAX_VALUE);
        
        titledPane.expandedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                titledPane.setMinHeight(Region.USE_COMPUTED_SIZE);
                docText.setVisible(true);
            } else {
                titledPane.setMinHeight(Region.USE_COMPUTED_SIZE);
            }
        });
        
        return titledPane;
    }

    private HBox createButtons() {
        Button saveBtn = new Button("💾 Save");
        saveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        saveBtn.setOnAction(e -> saveSale());

        Button voidBtn = new Button("🚫 Void Record");
        voidBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        voidBtn.setOnAction(e -> voidSale());

        Button unvoidBtn = new Button("↩️ Unvoid Record");
unvoidBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
// Replace the existing action with a popup
unvoidBtn.setOnAction(e -> {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Unvoid Not Allowed");
    alert.setHeaderText("❌ Unvoid Function Disabled");
    alert.setContentText("Unvoid functionality is currently disabled for VAT sales records.\n\n" +
                        "For audit and compliance reasons:\n" +
                        "• Once a VAT sale is voided, it cannot be restored\n" +
                        "• Voided records remain in the system for audit trails\n" +
                        "• If you need to correct a voided record, please create a new sale\n" +
                        "• Contact system administrator for exceptional cases");
    alert.showAndWait();
});

        Button clearBtn = new Button("🧹 Clear & Reset");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        clearBtn.setOnAction(e -> {
            clearForm();
            setDefaults();
            showAlert("✅ Form cleared! Default values restored.");
        });

        Button exportCSVBtn = new Button("📊 Export CSV");
        exportCSVBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        exportCSVBtn.setOnAction(e -> exportCSV());

        Button exportPDFBtn = new Button("📄 Export PDF");
        exportPDFBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        exportPDFBtn.setOnAction(e -> exportPDF());

        Button printBtn = new Button("🖨️ Print");
        printBtn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        printBtn.setOnAction(e -> printTable());

        HBox buttonBox = new HBox(10, saveBtn, voidBtn, unvoidBtn, clearBtn, exportCSVBtn, exportPDFBtn, printBtn);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        return buttonBox;
    }

private void buildTable() {
    table.setStyle("-fx-font-size: 12px;");
    
// Add Status column first
TableColumn<VatSaleModel, String> statusCol = new TableColumn<>("Status");
// Fix: Use a custom cell value factory that returns String based on voided status
statusCol.setCellValueFactory(cellData -> {
    VatSaleModel sale = cellData.getValue();
    return new SimpleStringProperty(sale.isVoided() ? "VOIDED" : "ACTIVE");
});
statusCol.setCellFactory(tc -> new TableCell<VatSaleModel, String>() {
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setStyle("");
        } else {
            setText(item);
            if ("VOIDED".equals(item)) {
                setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; " +
                         "-fx-background-color: #ffebee; -fx-alignment: center;");
            } else {
                setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; " +
                         "-fx-background-color: #e8f6f3; -fx-alignment: center;");
            }
        }
    }
});
statusCol.setPrefWidth(80);
table.getColumns().add(statusCol);
    
     // UPDATE THE HEADERS AND PROPERTIES TO INCLUDE "Recorded By"
    String[] headers = {"VAT", "Calendar", "Type", "Buyer TIN", "Buyer Name", "Date", 
                       "Receipt", "MRC No", "Description", "Unit", "Qty", "Price", 
                       "Total", "VAT Amt", "After VAT", "Recorded By"}; // Added "Recorded By"
    
    String[] properties = {"vatCategory", "calendarType", "saleType", "buyerTin", 
                          "buyerName", "dateOfSale", "receiptNumber", "mrcNumber",
                          "description", "unitMeasure", "quantity", "unitPrice", "totalValue", 
                          "vatAmount", "totalAfterVat", "createdBy"}; // Added "createdBy"
    
    for (int i = 0; i < headers.length; i++) {
        TableColumn<VatSaleModel, Object> col = new TableColumn<>(headers[i]);
        col.setCellValueFactory(new PropertyValueFactory<>(properties[i]));
        
        // Format numeric columns
        if (properties[i].equals("quantity") || properties[i].equals("unitPrice") || 
            properties[i].equals("totalValue") || properties[i].equals("vatAmount") || 
            properties[i].equals("totalAfterVat")) {
            col.setCellFactory(tc -> new TableCell<VatSaleModel, Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.format("%,.2f", Double.parseDouble(item.toString())));
                    }
                }
            });
        }
        
        // Color code sale types
        if (properties[i].equals("saleType")) {
            col.setCellFactory(tc -> new TableCell<VatSaleModel, Object>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        String type = item.toString();
                        switch(type) {
                            case "1": 
                                setText("Taxable");
                                setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-background-color: #d4f4dd;");
                                break;
                            case "2": 
                                setText("Zero Rated");
                                setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold; -fx-background-color: #ffeaa7;");
                                break;
                            case "3": 
                                setText("Exempt");
                                setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-background-color: #ffcccb;");
                                break;
                            default:
                                setText(type);
                                setStyle("");
                        }
                    }
                }
            });
        }
        
        table.getColumns().add(col);
    }

    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    
    // Alternating row colors with voided records in red
    table.setRowFactory(tv -> new TableRow<VatSaleModel>() {
        @Override
        protected void updateItem(VatSaleModel item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setStyle("");
            } else {
                if (item.isVoided()) {
                    // Red color for voided records
                    setStyle(getIndex() % 2 == 0 ? 
                            "-fx-background-color: #ffebee;" : 
                            "-fx-background-color: #ffcdd2;");
                } else {
                    // Normal colors for active records based on sale type
                    switch(item.getSaleType()) {
                        case 1: // Taxable
                            setStyle(getIndex() % 2 == 0 ? 
                                    "-fx-background-color: #e8f6f3;" : 
                                    "-fx-background-color: #d1f2eb;");
                            break;
                        case 2: // Zero Rated
                            setStyle(getIndex() % 2 == 0 ? 
                                    "-fx-background-color: #fef9e7;" : 
                                    "-fx-background-color: #fcf3cf;");
                            break;
                        case 3: // Exempt
                            setStyle(getIndex() % 2 == 0 ? 
                                    "-fx-background-color: #fdedec;" : 
                                    "-fx-background-color: #fadbd8;");
                            break;
                        default:
                            setStyle(getIndex() % 2 == 0 ? 
                                    "-fx-background-color: #f8f9fa;" : 
                                    "-fx-background-color: #ffffff;");
                    }
                }
                
                setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2 && !isEmpty()) {
                        populateForm(getItem());
                    }
                });
            }
        }
    });
}

private GridPane createForm() {
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(8);
    grid.setPadding(new Insets(15));
    grid.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");

    // Initialize components
    vatCategory = new ComboBox<>();
    vatCategory.getItems().addAll("G", "S");
    vatCategory.setStyle("-fx-background-color: white;");
    vatCategory.setPrefWidth(150);

    calendarType = new ComboBox<>();
    calendarType.getItems().addAll("G", "E");
    calendarType.setStyle("-fx-background-color: white;");
    calendarType.setPrefWidth(150);

    saleType = new ComboBox<>();
    saleType.getItems().addAll(1,2,3);
    saleType.setStyle("-fx-background-color: white;");
    saleType.setPrefWidth(150);

    unitMeasure = new ComboBox<>();
    unitMeasure.getItems().addAll(2,3,4,5,6,7,8,9,10);
    unitMeasure.setStyle("-fx-background-color: white;");
    unitMeasure.setPrefWidth(150);

    // Text fields with validation listeners
    buyerTin = createTextField();
    buyerTin.setPromptText("Enter 10-digit TIN (optional)");
    buyerTin.focusedProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal) validateTin();
    });

    buyerName = createTextField();
    buyerName.setPromptText("Buyer Name (optional)");

    receiptNumber = createTextField();
    receiptNumber.setPromptText("FS12345678 or M1234");
    receiptNumber.focusedProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal) {
            validateReceipt();
            // Update MRC field styling based on receipt type
            String receipt = receiptNumber.getText().trim();
            if (receipt.startsWith("FS")) {
                // Machine receipt - highlight MRC as required
                mrcNumber.setPromptText("MRC Number (REQUIRED for FS receipts)");
                mrcNumber.setStyle("-fx-background-color: #fffacd; -fx-border-color: #ffa500;");
            } else if (receipt.startsWith("M")) {
                // Manual receipt - MRC is optional
                mrcNumber.setPromptText("MRC Number (optional)");
                mrcNumber.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7;");
            }
        }
    });

    // ADD THIS FIELD - MRC Number
    mrcNumber = createTextField();
    mrcNumber.setPromptText("MRC Number");
    // Add tooltip
    Tooltip mrcTooltip = new Tooltip("MRC is MANDATORY for machine receipts (starting with FS)\n" +
                                     "Optional for manual receipts (starting with M)");
    mrcNumber.setTooltip(mrcTooltip);

    description = createTextField();
    description.setPromptText("Enter item/service description");

    quantity = createTextField();
    quantity.setPromptText("e.g., 10.5");
    quantity.textProperty().addListener((o,ov,nv)->{
        //allow only numerical value
        if (!nv.matches("\\d*(\\.\\d*)?")) {
            quantity.setText(ov);
        }
        calculate();
    });

    unitPrice = createTextField();
    unitPrice.setPromptText("e.g., 100.00");
    unitPrice.textProperty().addListener((o,ov,nv)->{
        //allow only numerical value
        if (!nv.matches("\\d*(\\.\\d*)?")) {
            unitPrice.setText(ov);
        }
        calculate();
    });

    datePicker = new DatePicker(LocalDate.now());
    datePicker.setStyle("-fx-background-color: white;");
    datePicker.setPrefWidth(150);

    totalValue = new Label("0.00");
    totalValue.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    vatAmount = new Label("0.00");
    vatAmount.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    totalAfterVat = new Label("0.00");
    totalAfterVat.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #27ae60;");

    // Add form fields with labels
    int r = 0;
    
    // Row 1
    Label vatCatLabel = createRequiredLabel("VAT Category *");
    grid.add(vatCatLabel,0,r);
    grid.add(vatCategory,1,r);
    
    Label calendarLabel = createRequiredLabel("Calendar Type *");
    grid.add(calendarLabel,2,r);
    grid.add(calendarType,3,r);
    
    Label saleTypeLabel = createRequiredLabel("Sale Type *");
    grid.add(saleTypeLabel,4,r);
    grid.add(saleType,5,r++);

    // Row 2
    Label unitLabel = createRequiredLabel("Unit Measure *");
    grid.add(unitLabel,0,r);
    grid.add(unitMeasure,1,r);
    
    Label tinLabel = createLabel("Buyer TIN");
    grid.add(tinLabel,2,r);
    grid.add(buyerTin,3,r);
    
    Label nameLabel = createLabel("Buyer Name");
    grid.add(nameLabel,4,r);
    grid.add(buyerName,5,r++);

    // Row 3
    Label receiptLabel = createRequiredLabel("Receipt No *");
    grid.add(receiptLabel,0,r);
    grid.add(receiptNumber,1,r);
    
    // ADD MRC LABEL AND FIELD HERE
    Label mrcLabel = createLabel("MRC No");
    grid.add(mrcLabel,2,r);
    grid.add(mrcNumber,3,r);
    
    Label dateLabel = createRequiredLabel("Date *");
    grid.add(dateLabel,4,r);
    grid.add(datePicker,5,r++);

    // Row 4
    Label descLabel = createRequiredLabel("Description *");
    grid.add(descLabel,0,r);
    grid.add(description,1,r,5,1); // Span 5 columns for description

    // Row 5
    grid.add(createRequiredLabel("Quantity *"),0,++r);
    grid.add(quantity,1,r);
    grid.add(createRequiredLabel("Unit Price *"),2,r);
    grid.add(unitPrice,3,r);
    grid.add(createLabel("Total Value"),4,r);
    grid.add(totalValue,5,r++);

    // Row 6
    grid.add(createLabel("VAT (15%)"),0,r);
    grid.add(vatAmount,1,r);
    grid.add(createLabel("After VAT"),2,r);
    grid.add(totalAfterVat,3,r);

    return grid;
}

    private HBox createDateRangeFilter() {
        HBox dateRangeBox = new HBox(10);
        dateRangeBox.setPadding(new Insets(10));
        dateRangeBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        
        Label filterLabel = new Label("📅 FILTER BY DATE RANGE:");
        filterLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label fromLabel = new Label("From:");
        fromLabel.setStyle("-fx-font-weight: bold;");
        
        fromDatePicker = new DatePicker(LocalDate.now().minusMonths(1));
        fromDatePicker.setStyle("-fx-background-color: white;");
        fromDatePicker.setPrefWidth(120);
        
        Label toLabel = new Label("To:");
        toLabel.setStyle("-fx-font-weight: bold;");
        
        toDatePicker = new DatePicker(LocalDate.now());
        toDatePicker.setStyle("-fx-background-color: white;");
        toDatePicker.setPrefWidth(120);
        
        Button filterBtn = new Button("Apply Filter");
        filterBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        filterBtn.setOnAction(e -> applyDateRangeFilter());
        
        Button clearFilterBtn = new Button("Clear Filter");
        clearFilterBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
        clearFilterBtn.setOnAction(e -> clearDateRangeFilter());
        
        Button exportFilteredCSVBtn = new Button("📊 Export Filtered CSV");
        exportFilteredCSVBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        exportFilteredCSVBtn.setOnAction(e -> exportFilteredCSV());
        
        Button printFilteredBtn = new Button("🖨️ Print Filtered");
        printFilteredBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
        printFilteredBtn.setOnAction(e -> printFilteredData());
        
        Button exportFilteredPDFBtn = new Button("📄 Export Filtered PDF");
        exportFilteredPDFBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        exportFilteredPDFBtn.setOnAction(e -> exportFilteredPDF());
        
        dateRangeBox.getChildren().addAll(
            filterLabel, fromLabel, fromDatePicker, toLabel, toDatePicker,
            filterBtn, clearFilterBtn, exportFilteredCSVBtn, printFilteredBtn, exportFilteredPDFBtn
        );
        
        return dateRangeBox;
    }

    private void applyDateRangeFilter() {
        try {
            LocalDate fromDate = fromDatePicker.getValue();
            LocalDate toDate = toDatePicker.getValue();
            
            if (fromDate == null || toDate == null) {
                showAlert("⚠️ Please select both From and To dates.");
                return;
            }
            
            if (fromDate.isAfter(toDate)) {
                showAlert("⚠️ 'From' date cannot be after 'To' date.");
                return;
            }
            
            FilteredList<VatSaleModel> filtered = masterList.filtered(sale -> {
                try {
                    LocalDate saleDate = LocalDate.parse(sale.getDateOfSale());
                    return !saleDate.isBefore(fromDate) && !saleDate.isAfter(toDate);
                } catch (Exception e) {
                    return false;
                }
            });
            
            filteredList.setAll(filtered);
            table.setItems(filteredList);
            updateTotals();
            
            int recordCount = filteredList.size();
            double totalAmount = filteredList.stream()
                .filter(s -> !s.isVoided())
                .mapToDouble(VatSaleModel::getTotalAfterVat)
                .sum();
            double vatAmount = filteredList.stream()
                .filter(s -> s.getSaleType() == 1 && !s.isVoided())
                .mapToDouble(VatSaleModel::getVatAmount)
                .sum();
            
            showAlert("✅ Filter applied successfully!\n" +
                     "Date Range: " + fromDate + " to " + toDate + "\n" +
                     "Active Records Found: " + filteredList.stream().filter(s -> !s.isVoided()).count() + "\n" +
                     "Total Sales: ETB " + String.format("%,.2f", totalAmount) + "\n" +
                     "VAT Collected: ETB " + String.format("%,.2f", vatAmount));
            
        } catch (Exception e) {
            showAlert("❌ Error applying filter: " + e.getMessage());
        }
    }

    private void clearDateRangeFilter() {
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));
        toDatePicker.setValue(LocalDate.now());
        filteredList.clear();
        // Show active records by default
        FilteredList<VatSaleModel> activeFiltered = masterList.filtered(sale -> !sale.isVoided());
        activeList.setAll(activeFiltered);
        table.setItems(activeList);
        updateTotals();
        showAlert("✅ Filter cleared. Showing active records.");
    }

    private TextField createTextField() {
        TextField tf = new TextField();
        tf.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 3;");
        tf.setPrefWidth(200);
        return tf;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private Label createRequiredLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        return label;
    }

    private HBox createSearchBar() {
        searchField = new TextField();
        searchField.setPromptText("Search by Receipt No, Buyer Name, Description, or TIN...");
        searchField.setStyle("-fx-background-color: white; -fx-border-color: #3498db; -fx-border-radius: 3;");
        searchField.setPrefWidth(400);
        searchField.textProperty().addListener((obs, oldV, newV) -> filterData());
        
        Button searchBtn = new Button("🔍");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        searchBtn.setOnAction(e -> filterData());
        
        HBox searchBox = new HBox(5, new Label("Search:"), searchField, searchBtn);
        searchBox.setPadding(new Insets(5, 0, 5, 0));
        return searchBox;
    }

    private void filterData() {
        String searchTerm = searchField.getText().toLowerCase();
        ObservableList<VatSaleModel> currentList = table.getItems();
        FilteredList<VatSaleModel> filtered = new FilteredList<>(currentList, p -> true);
        filtered.setPredicate(sale ->
            sale.getReceiptNumber().toLowerCase().contains(searchTerm) ||
            sale.getBuyerName().toLowerCase().contains(searchTerm) ||
            sale.getDescription().toLowerCase().contains(searchTerm) ||
            sale.getBuyerTin().toLowerCase().contains(searchTerm)
        );
        table.setItems(filtered);
        updateTotals();
    }

    // ========== VALIDATION METHODS ==========

    private boolean validateTin() {
        String tin = buyerTin.getText().trim();
        if (!tin.isEmpty()) {
            if (!TIN_PATTERN.matcher(tin).matches()) {
                showValidationError("TIN must be exactly 10 digits (e.g., 1234567890)");
                buyerTin.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
                return false;
            }
        }
        buyerTin.setStyle("-fx-border-color: #27ae60; -fx-border-width: 1px;");
        return true;
    }

    private boolean validateReceipt() {
        String receipt = receiptNumber.getText().trim();
        if (receipt.isEmpty()) {
            showValidationError("Receipt number is required");
            receiptNumber.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
            return false;
        }
        
        boolean isValid = RECEIPT_MACHINE_PATTERN.matcher(receipt).matches() || 
                          RECEIPT_MANUAL_PATTERN.matcher(receipt).matches();
        
        if (!isValid) {
            showValidationError("Receipt must be: FS + 8 digits (machine) OR M + digits (manual)");
            receiptNumber.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
            return false;
        }
        
        receiptNumber.setStyle("-fx-border-color: #27ae60; -fx-border-width: 1px;");
        return true;
    }

private boolean validateMandatoryFields() {
    StringBuilder errors = new StringBuilder();
    
    if (vatCategory.getValue() == null || vatCategory.getValue().isEmpty()) {
        errors.append("• VAT Category is required\n");
        vatCategory.setStyle("-fx-border-color: #e74c3c;");
    } else {
        vatCategory.setStyle("");
    }
    
    if (calendarType.getValue() == null || calendarType.getValue().isEmpty()) {
        errors.append("• Calendar Type is required\n");
        calendarType.setStyle("-fx-border-color: #e74c3c;");
    } else {
        calendarType.setStyle("");
    }
    
    if (saleType.getValue() == null) {
        errors.append("• Sale Type is required\n");
        saleType.setStyle("-fx-border-color: #e74c3c;");
    } else {
        saleType.setStyle("");
    }
    
    if (unitMeasure.getValue() == null) {
        errors.append("• Unit Measure is required\n");
        unitMeasure.setStyle("-fx-border-color: #e74c3c;");
    } else {
        unitMeasure.setStyle("");
    }
    
    if (datePicker.getValue() == null) {
        errors.append("• Date is required\n");
        datePicker.setStyle("-fx-border-color: #e74c3c;");
    } else {
        datePicker.setStyle("");
    }
    
    if (description.getText().trim().isEmpty()) {
        errors.append("• Description is required\n");
        description.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
    } else {
        description.setStyle("");
    }
    
    if (quantity.getText().trim().isEmpty() || !isNumeric(quantity.getText())) {
        errors.append("• Valid quantity is required\n");
        quantity.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
    } else {
        quantity.setStyle("");
    }
    
    if (unitPrice.getText().trim().isEmpty() || !isNumeric(unitPrice.getText())) {
        errors.append("• Valid unit price is required\n");
        unitPrice.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
    } else {
        unitPrice.setStyle("");
    }
    
    // Validate receipt number
    if (!validateReceipt()) {
        errors.append("• Valid receipt number is required\n");
    } else {
        // If receipt is valid, check if it's FS type and MRC is required
        String receipt = receiptNumber.getText().trim();
        if (receipt.startsWith("FS")) {
            // Machine receipt - MRC is mandatory
            if (mrcNumber.getText().trim().isEmpty()) {
                errors.append("• MRC Number is REQUIRED for machine receipts (FS...)\n");
                mrcNumber.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
            } else {
                mrcNumber.setStyle("");
            }
        } else {
            // Manual receipt - MRC is optional
            mrcNumber.setStyle("");
        }
    }
    
    if (errors.length() > 0) {
        showValidationError("Please fix the following errors:\n" + errors.toString());
        return false;
    }
    
    return true;
}

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showValidationError(String message) {
        validationLabel.setText(message);
        validationLabel.setVisible(true);
    }

    private void clearValidation() {
        validationLabel.setVisible(false);
        vatCategory.setStyle("");
        calendarType.setStyle("");
        saleType.setStyle("");
        unitMeasure.setStyle("");
        buyerTin.setStyle("");
        receiptNumber.setStyle("");
        description.setStyle("");
        quantity.setStyle("");
        unitPrice.setStyle("");
        datePicker.setStyle("");
    }

    private void setDefaults() {
        vatCategory.setValue("G");          // Default: Goods
        calendarType.setValue("G");         // Default: Gregorian
        saleType.setValue(1);              // Default: Taxable Sale
        unitMeasure.setValue(7);            // Default: PCS
        datePicker.setValue(LocalDate.now());
        
        // Set default date range (last 30 days)
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));
        toDatePicker.setValue(LocalDate.now());
        
        // Clear other fields but keep defaults
        buyerTin.clear();
        buyerName.clear();
        receiptNumber.clear();
        description.clear();
        quantity.clear();
        unitPrice.clear();
        totalValue.setText("0.00");
        vatAmount.setText("0.00");
        totalAfterVat.setText("0.00");
        validationLabel.setVisible(false);
        
        // Clear field styles
        vatCategory.setStyle("");
        calendarType.setStyle("");
        saleType.setStyle("");
        unitMeasure.setStyle("");
        buyerTin.setStyle("");
        receiptNumber.setStyle("");
        description.setStyle("");
        quantity.setStyle("");
        unitPrice.setStyle("");
        datePicker.setStyle("");
    }

    // ========== FORM POPULATION AND CLEARING ==========

private void populateForm(VatSaleModel s) {
    vatCategory.setValue(s.getVatCategory());
    calendarType.setValue(s.getCalendarType());
    saleType.setValue(s.getSaleType());
    buyerTin.setText(s.getBuyerTin());
    buyerName.setText(s.getBuyerName());
    receiptNumber.setText(s.getReceiptNumber());
    mrcNumber.setText(s.getMrcNumber()); // Load MRC value
    description.setText(s.getDescription());
    quantity.setText(String.valueOf(s.getQuantity()));
    unitPrice.setText(String.valueOf(s.getUnitPrice()));
    unitMeasure.setValue(s.getUnitMeasure());
    
    // Update MRC styling based on receipt type
    String receipt = receiptNumber.getText().trim();
    if (receipt.startsWith("FS")) {
        mrcNumber.setPromptText("MRC Number (REQUIRED for FS receipts)");
        mrcNumber.setStyle("-fx-background-color: #fffacd; -fx-border-color: #ffa500;");
    } else if (receipt.startsWith("M")) {
        mrcNumber.setPromptText("MRC Number (optional)");
        mrcNumber.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7;");
    }
    
    try {
        datePicker.setValue(LocalDate.parse(s.getDateOfSale()));
    } catch (Exception e) {
        datePicker.setValue(LocalDate.now());
    }
    
    calculate();
    
    String status = s.isVoided() ? "VOIDED" : "ACTIVE";
    showAlert("📝 Record loaded for editing.\n" +
              "Receipt: " + s.getReceiptNumber() + "\n" +
              "Status: " + status + "\n" +
              "Amount: " + String.format("%,.2f", s.getTotalAfterVat()));
}

    private void clearForm() {
        clearValidation();
        buyerTin.clear();
        buyerName.clear();
        receiptNumber.clear();
        mrcNumber.clear();
        description.clear();
        quantity.clear();
        unitPrice.clear();
        totalValue.setText("0.00");
        vatAmount.setText("0.00");
        totalAfterVat.setText("0.00");
    }

    // ========== VOIDING FUNCTIONALITY ==========
    
    private void voidSale() {
        VatSaleModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Please select a record to void.");
            return;
        }
        
        if (selected.isVoided()) {
            showAlert("⚠️ This record is already voided!");
            return;
        }
        
        // Create void dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Void Sales Record");
        dialog.setHeaderText("Are you sure you want to void this record?\n" +
                           "Receipt: " + selected.getReceiptNumber() + "\n" +
                           "Amount: " + String.format("%,.2f", selected.getTotalAfterVat()));
        
        // Set the button types
        ButtonType voidButtonType = new ButtonType("Void", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voidButtonType, ButtonType.CANCEL);
        
        // Create reason input
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextArea reasonField = new TextArea();
        reasonField.setPromptText("Enter void reason (required)");
        reasonField.setWrapText(true);
        reasonField.setPrefRowCount(3);
        
        grid.add(new Label("Void Reason:"), 0, 0);
        grid.add(reasonField, 0, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Enable/Disable void button based on reason input
        Node voidButton = dialog.getDialogPane().lookupButton(voidButtonType);
        voidButton.setDisable(true);
        
        reasonField.textProperty().addListener((observable, oldValue, newValue) -> {
            voidButton.setDisable(newValue.trim().isEmpty());
        });
        
        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voidButtonType) {
                return reasonField.getText().trim();
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(reason -> {
            try {
                boolean success = con.voidVatSale(selected.getReceiptNumber(), currentUser, reason);
                if (success) {
                    // Update the model
                    selected.setVoided(true);
                    selected.setVoidedBy(currentUser);
                    selected.setVoidedAt(LocalDateTime.now().toString());
                    selected.setVoidReason(reason);
                    
                    // Refresh the table to update colors
                    table.refresh();
                    
                    // Update totals
                    updateTotals();
                    
                    showAlert("✅ Record voided successfully!\n" +
                             "Receipt: " + selected.getReceiptNumber() + "\n" +
                             "Voided by: " + currentUser + "\n" +
                             "Reason: " + reason);
                } else {
                    showAlert("❌ Failed to void record.");
                }
            } catch (Exception ex) {
                showAlert("❌ Error voiding record: " + ex.getMessage());
            }
        });
    }
    
    private void unvoidSale() {
        VatSaleModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Please select a voided record to restore.");
            return;
        }
        
        if (!selected.isVoided()) {
            showAlert("⚠️ This record is not voided!");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Unvoid Sales Record");
        confirm.setHeaderText("Restore Voided Record");
        confirm.setContentText("Are you sure you want to restore this voided record?\n\n" +
                             "Receipt: " + selected.getReceiptNumber() + "\n" +
                             "Original Amount: " + String.format("%,.2f", selected.getTotalAfterVat()) + "\n" +
                             "Voided by: " + selected.getVoidedBy() + "\n" +
                             "Reason: " + selected.getVoidReason());
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean success = con.unvoidVatSale(selected.getReceiptNumber());
                    if (success) {
                        // Update the model
                        selected.setVoided(false);
                        selected.setVoidedBy(null);
                        selected.setVoidedAt(null);
                        selected.setVoidReason(null);
                        
                        // Refresh the table
                        table.refresh();
                        
                        // Update totals
                        updateTotals();
                        
                        showAlert("✅ Record restored successfully!\n" +
                                 "Receipt: " + selected.getReceiptNumber() + " is now active.");
                    } else {
                        showAlert("❌ Failed to restore record.");
                    }
                } catch (Exception ex) {
                    showAlert("❌ Error restoring record: " + ex.getMessage());
                }
            }
        });
    }
    
    // ========== UPDATED CALCULATION METHODS ==========
    
    private void calculate() {
        try {
            double qty = Double.parseDouble(quantity.getText());
            double price = Double.parseDouble(unitPrice.getText());
            double total = qty * price;
            
            Integer type = saleType.getValue();
            double vat = (type != null && type == 1) ? total * 0.15 : 0; // VAT only for type 1
            double after = total + vat;

            totalValue.setText(String.format("%,.2f", total));
            vatAmount.setText(String.format("%,.2f", vat));
            totalAfterVat.setText(String.format("%,.2f", after));
        } catch(Exception ignored) {
            totalValue.setText("0.00");
            vatAmount.setText("0.00");
            totalAfterVat.setText("0.00");
        }
    }
    
    private void updateTotals() {
        ObservableList<VatSaleModel> currentList = table.getItems();
        
        // Calculate grand total (all records)
        double grandTotal = masterList.stream()
            .filter(sale -> !sale.isVoided()) // Exclude voided from totals
            .mapToDouble(VatSaleModel::getTotalAfterVat)
            .sum();
        
        // Calculate active total (only active records in current view)
        double activeTotal = currentList.stream()
            .filter(sale -> !sale.isVoided())
            .mapToDouble(VatSaleModel::getTotalAfterVat)
            .sum();
        
        // Calculate voided total for information
        double voidedTotal = masterList.stream()
            .filter(VatSaleModel::isVoided)
            .mapToDouble(VatSaleModel::getTotalAfterVat)
            .sum();
        
        footerTotal.setText(String.format("Grand Total (Active Only): ETB %,.2f", grandTotal));
        activeTotalLabel.setText(String.format("View Total (Active): ETB %,.2f | Voided Total: ETB %,.2f", 
            activeTotal, voidedTotal));
    }
    
    // ========== UPDATED LOAD METHOD ==========
    
private void loadPage(int pageIndex) {
    try {
        List<VatSaleModel> list = con.fetchAllVatSales(pageIndex * ROWS_PER_PAGE, ROWS_PER_PAGE);
        masterList.setAll(list);
        
        // Default to showing active records only
        FilteredList<VatSaleModel> activeFiltered = masterList.filtered(sale -> !sale.isVoided());
        activeList.setAll(activeFiltered);
        
        // Apply current filter
        String filter = viewFilter.getValue();
        if (filter != null) {
            switch (filter) {
                case "All Records":
                    table.setItems(masterList);
                    break;
                case "Active Only":
                    table.setItems(activeList);
                    break;
                case "Voided Only":
                    FilteredList<VatSaleModel> voidedFiltered = masterList.filtered(VatSaleModel::isVoided);
                    table.setItems(voidedFiltered);
                    break;
                default:
                    table.setItems(activeList);
            }
        } else {
            table.setItems(activeList);
        }
        
        updateTotals();
        table.refresh(); // Force refresh the table display
        
    } catch(Exception e) {
        showAlert("Error loading data: " + e.getMessage());
        e.printStackTrace(); // Add for debugging
    }
}
    
    // ========== UPDATED EXPORT METHODS ==========
    
private void exportCSV() {
    // Filter out voided records for export
    ObservableList<VatSaleModel> dataToExport = masterList.filtered(sale -> !sale.isVoided());
    
    if (dataToExport.isEmpty()) {
        showAlert("⚠️ No active data to export!");
        return;
    }
    
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Export Active Sales to CSV");
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    fileChooser.setInitialFileName("VAT_Active_Sales_" + LocalDate.now() + ".csv");
    
    File file = fileChooser.showSaveDialog(this.getScene().getWindow());
    
    if (file != null) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // UPDATE HEADER TO INCLUDE MRC
            writer.println("Status,VAT Category,Calendar Type,Sale Type,Buyer TIN,Buyer Name,Date,Receipt Number,MRC Number,Description,Unit Measure,Quantity,Unit Price,Total Value,VAT Amount,Total After VAT");
            
            for (VatSaleModel sale : dataToExport) {
                writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%d\",\"%.2f\",\"%.2f\",\"%.2f\",\"%.2f\",\"%.2f\"",
                    "ACTIVE",
                    sale.getVatCategory(),
                    sale.getCalendarType(),
                    sale.getSaleType(),
                    sale.getBuyerTin(),
                    sale.getBuyerName(),
                    sale.getDateOfSale(),
                    sale.getReceiptNumber(),
                    sale.getMrcNumber(), // ADD MRC HERE
                    sale.getDescription(),
                    sale.getUnitMeasure(),
                    sale.getQuantity(),
                    sale.getUnitPrice(),
                    sale.getTotalValue(),
                    sale.getVatAmount(),
                    sale.getTotalAfterVat()
                ));
            }
            
            showAlert("✅ Active CSV exported successfully!\n\n" +
                     "File: " + file.getName() + "\n" +
                     "Active Records: " + dataToExport.size() + "\n" +
                     "Voided Records Excluded: " + (masterList.size() - dataToExport.size()) + "\n" +
                     "Location: " + file.getParent());
            
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (IOException e) {
            showAlert("❌ Error exporting CSV: " + e.getMessage());
        }
    }
}
    
    private void exportFilteredCSV() {
        ObservableList<VatSaleModel> currentViewData = table.getItems();
        // Filter out voided records from current view
        ObservableList<VatSaleModel> dataToExport = currentViewData.filtered(sale -> !sale.isVoided());
        
        if (dataToExport.isEmpty()) {
            showAlert("⚠️ No active data to export! Please apply a filter first.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Filtered Active Data to CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        String fileName = "VAT_Filtered_Active_Sales_" + 
                         (fromDate != null ? fromDate.toString() : "all") + "_to_" +
                         (toDate != null ? toDate.toString() : "all") + ".csv";
        fileChooser.setInitialFileName(fileName);
        
        File file = fileChooser.showSaveDialog(this.getScene().getWindow());
        
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("Status,VAT Category,Calendar Type,Sale Type,Buyer TIN,Buyer Name,Date,Receipt Number,Description,Unit Measure,Quantity,Unit Price,Total Value,VAT Amount,Total After VAT");
                
                for (VatSaleModel sale : dataToExport) {
                    writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%d\",\"%.2f\",\"%.2f\",\"%.2f\",\"%.2f\",\"%.2f\"",
                        "ACTIVE",
                        sale.getVatCategory(),
                        sale.getCalendarType(),
                        sale.getSaleType(),
                        sale.getBuyerTin(),
                        sale.getBuyerName(),
                        sale.getDateOfSale(),
                        sale.getReceiptNumber(),
                        sale.getDescription(),
                        sale.getUnitMeasure(),
                        sale.getQuantity(),
                        sale.getUnitPrice(),
                        sale.getTotalValue(),
                        sale.getVatAmount(),
                        sale.getTotalAfterVat()
                    ));
                }
                
                double totalAmount = dataToExport.stream()
                    .mapToDouble(VatSaleModel::getTotalAfterVat)
                    .sum();
                
                String dateRangeInfo = "";
                if (fromDate != null && toDate != null) {
                    dateRangeInfo = "Date Range: " + fromDate + " to " + toDate + "\n";
                }
                
                showAlert("✅ Filtered Active CSV exported successfully!\n\n" +
                         dateRangeInfo +
                         "File: " + file.getName() + "\n" +
                         "Active Records: " + dataToExport.size() + "\n" +
                         "Total Active Sales: ETB " + String.format("%,.2f", totalAmount) + "\n" +
                         "Location: " + file.getParent());
                
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file);
                }
            } catch (IOException e) {
                showAlert("❌ Error exporting filtered CSV: " + e.getMessage());
            }
        }
    }
    
    // ========== UPDATED PDF EXPORT METHODS ==========
    
    private void exportPDF() {
        // Filter out voided records for export
        ObservableList<VatSaleModel> dataToExport = masterList.filtered(sale -> !sale.isVoided());
        
        if (dataToExport.isEmpty()) {
            showAlert("⚠️ No active data to export!");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Active Sales to PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("VAT_Active_Sales_Report_" + LocalDate.now() + ".pdf");
        
        File file = fileChooser.showSaveDialog(this.getScene().getWindow());
        
        if (file != null) {
            try {
                String title = "VAT ACTIVE SALES REPORT";
                String subtitle = "Voided Records Excluded";
                exportDataToPDF(file, dataToExport, title, subtitle);
            } catch (Exception e) {
                showAlert("❌ Error exporting PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void exportFilteredPDF() {
        ObservableList<VatSaleModel> currentViewData = table.getItems();
        // Filter out voided records from current view
        ObservableList<VatSaleModel> dataToExport = currentViewData.filtered(sale -> !sale.isVoided());
        
        if (dataToExport.isEmpty()) {
            showAlert("⚠️ No active data to export! Please apply a filter first.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Filtered Active Data to PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        String dateRange = "";
        if (fromDate != null && toDate != null) {
            dateRange = fromDate + " to " + toDate;
        }
        String fileName = "VAT_Filtered_Active_Sales_" + dateRange.replace(" to ", "_") + "_" + LocalDate.now() + ".pdf";
        fileChooser.setInitialFileName(fileName);
        
        File file = fileChooser.showSaveDialog(this.getScene().getWindow());
        
        if (file != null) {
            try {
                String title = "FILTERED VAT ACTIVE SALES REPORT";
                String subtitle = "Date Range: " + (dateRange.isEmpty() ? "All Active Records" : dateRange) + 
                                " | Voided Records Excluded";
                exportDataToPDF(file, dataToExport, title, subtitle);
            } catch (Exception e) {
                showAlert("❌ Error exporting filtered PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void exportDataToPDF(File file, ObservableList<VatSaleModel> data, String title, String subtitle) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
        
        // Add company header
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);
        Paragraph header = new Paragraph("SMART HRMS - VAT ACTIVE SALES REPORT", headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(10);
        document.add(header);
        
        // Add title and subtitle
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLUE);
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(5);
        document.add(titlePara);
        
        // Add disclaimer about voided records
        com.itextpdf.text.Font disclaimerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.RED);
        Paragraph disclaimer = new Paragraph(subtitle, disclaimerFont);
        disclaimer.setAlignment(Element.ALIGN_CENTER);
        disclaimer.setSpacingAfter(10);
        document.add(disclaimer);
        
        // Add generation info
        com.itextpdf.text.Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
        Paragraph info = new Paragraph("Generated on: " + LocalDate.now() + " | Generated by: " + currentUser + " | Total Active Records: " + data.size(), infoFont);
        info.setAlignment(Element.ALIGN_CENTER);
        info.setSpacingAfter(15);
        document.add(info);
        
        // Create table
        PdfPTable pdfTable = new PdfPTable(16); // 16 columns
        pdfTable.setWidthPercentage(100);
        pdfTable.setSpacingBefore(10f);
        pdfTable.setSpacingAfter(10f);
        
        // Set column widths
        float[] columnWidths = {1.2f, 0.8f, 1f, 0.8f, 1.5f, 2f, 1f, 1.2f, 1.5f, 0.8f, 1f, 1f, 1.2f, 1.2f, 1.2f, 0.8f};
        pdfTable.setWidths(columnWidths);
        
        // Add table headers
        String[] headers = {"Status", "VAT", "Calendar", "Type", "Buyer TIN", "Buyer Name", "Date", 
                           "Receipt", "Description", "Unit", "Qty", "Price", 
                           "Total", "VAT Amt", "After VAT", "Voided?"};
        
        for (String headerText : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(headerText, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            pdfTable.addCell(cell);
        }
        
        // Add data rows
        int rowCount = 0;
        double grandTotal = 0;
        double vatTotal = 0;
        
        for (VatSaleModel sale : data) {
            // Status cell
            PdfPCell statusCell = new PdfPCell(new Phrase("ACTIVE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.GREEN)));
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            statusCell.setPadding(3);
            pdfTable.addCell(statusCell);
            
            // Other data cells
            pdfTable.addCell(new PdfPCell(new Phrase(sale.getVatCategory(), FontFactory.getFont(FontFactory.HELVETICA, 8))));
            pdfTable.addCell(new PdfPCell(new Phrase(sale.getCalendarType(), FontFactory.getFont(FontFactory.HELVETICA, 8))));
            
            // Sale Type with coloring
            String typeText = "";
            BaseColor typeColor = BaseColor.BLACK;
            switch(sale.getSaleType()) {
                case 1: 
                    typeText = "Taxable";
                    typeColor = new BaseColor(39, 174, 96); // Green
                    break;
                case 2: 
                    typeText = "Zero Rated";
                    typeColor = new BaseColor(243, 156, 18); // Orange
                    break;
                case 3: 
                    typeText = "Exempt";
                    typeColor = new BaseColor(231, 76, 60); // Red
                    break;
            }
            PdfPCell typeCell = new PdfPCell(new Phrase(typeText, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, typeColor)));
            typeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfTable.addCell(typeCell);
            
            pdfTable.addCell(new PdfPCell(new Phrase(sale.getBuyerTin(), FontFactory.getFont(FontFactory.HELVETICA, 8))));
            pdfTable.addCell(new PdfPCell(new Phrase(sale.getBuyerName(), FontFactory.getFont(FontFactory.HELVETICA, 8))));
            pdfTable.addCell(new PdfPCell(new Phrase(sale.getDateOfSale(), FontFactory.getFont(FontFactory.HELVETICA, 8))));
            pdfTable.addCell(new PdfPCell(new Phrase(sale.getReceiptNumber(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8))));
            pdfTable.addCell(new PdfPCell(new Phrase(sale.getDescription(), FontFactory.getFont(FontFactory.HELVETICA, 8))));
            pdfTable.addCell(new PdfPCell(new Phrase(String.valueOf(sale.getUnitMeasure()), FontFactory.getFont(FontFactory.HELVETICA, 8))));
            pdfTable.addCell(new PdfPCell(new Phrase(String.format("%,.2f", sale.getQuantity()), FontFactory.getFont(FontFactory.HELVETICA, 8))));
            pdfTable.addCell(new PdfPCell(new Phrase(String.format("%,.2f", sale.getUnitPrice()), FontFactory.getFont(FontFactory.HELVETICA, 8))));
            pdfTable.addCell(new PdfPCell(new Phrase(String.format("%,.2f", sale.getTotalValue()), FontFactory.getFont(FontFactory.HELVETICA, 8))));
            pdfTable.addCell(new PdfPCell(new Phrase(String.format("%,.2f", sale.getVatAmount()), FontFactory.getFont(FontFactory.HELVETICA, 8))));
            pdfTable.addCell(new PdfPCell(new Phrase(String.format("%,.2f", sale.getTotalAfterVat()), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8))));
            
            // Voided status
            PdfPCell voidedCell = new PdfPCell(new Phrase("NO", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.GREEN)));
            voidedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfTable.addCell(voidedCell);
            
            grandTotal += sale.getTotalAfterVat();
            vatTotal += sale.getVatAmount();
            rowCount++;
            
            // Add page break if too many rows
            if (rowCount % 30 == 0 && rowCount < data.size()) {
                pdfTable.completeRow();
                document.add(pdfTable);
                document.newPage();
                
                // Add header to new page
                document.add(header);
                document.add(new Paragraph("(Continued)", titleFont));
                document.add(info);
                
                // Create new table for continuation
                pdfTable = new PdfPTable(16);
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(columnWidths);
                
                // Add headers again
                for (String headerText : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(headerText, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setPadding(5);
                    pdfTable.addCell(cell);
                }
            }
        }
        
        // Add table to document
        document.add(pdfTable);
        
        // Add summary
        document.add(new Paragraph("\n\n"));
        
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(50);
        summaryTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        // Summary rows
        addSummaryRow(summaryTable, "Total Active Records:", String.valueOf(data.size()));
        addSummaryRow(summaryTable, "Total VAT Collected (Taxable Only):", String.format("ETB %,.2f", vatTotal));
        addSummaryRow(summaryTable, "Total Sales Amount (Active):", String.format("ETB %,.2f", grandTotal));
        
        document.add(summaryTable);
        
        // Add footer
        document.add(new Paragraph("\n\n"));
        com.itextpdf.text.Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY);
        Paragraph footer = new Paragraph("This report excludes voided records. For audit purposes, voided records are maintained separately.", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
        
        document.close();
        
        // Show success message
        showAlert("✅ Active PDF exported successfully!\n\n" +
                 "File: " + file.getName() + "\n" +
                 "Active Records: " + data.size() + "\n" +
                 "Voided Records Excluded: " + (masterList.size() - data.size()) + "\n" +
                 "Total Sales: ETB " + String.format("%,.2f", grandTotal));
        
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }
    }
    
    private void addSummaryRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(5);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLUE)));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        valueCell.setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    // ========== UPDATED PRINT METHODS ==========
    
    private void printTable() {
        // Filter out voided records for printing
        ObservableList<VatSaleModel> dataToPrint = masterList.filtered(sale -> !sale.isVoided());
        
        if (dataToPrint.isEmpty()) {
            showAlert("⚠️ No active data to print!");
            return;
        }
        
        try {
            Node printNode = createPrintableNode(dataToPrint, "VAT ACTIVE SALES REPORT");
            printNode(printNode, "VAT Sales Report");
        } catch (Exception e) {
            showAlert("❌ Error printing: " + e.getMessage());
        }
    }
    
    private void printFilteredData() {
        ObservableList<VatSaleModel> currentViewData = table.getItems();
        // Filter out voided records from current view
        ObservableList<VatSaleModel> dataToPrint = currentViewData.filtered(sale -> !sale.isVoided());
        
        if (dataToPrint.isEmpty()) {
            showAlert("⚠️ No active data to print! Please apply a filter first.");
            return;
        }
        
        try {
            Node printNode = createPrintableNode(dataToPrint, "FILTERED VAT ACTIVE SALES REPORT");
            printNode(printNode, "Filtered VAT Sales Report");
        } catch (Exception e) {
            showAlert("❌ Error printing filtered data: " + e.getMessage());
        }
    }
    
    private void printNode(Node node, String jobName) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(jobName);
        
        if (job.printDialog()) {
            try {
                job.print();
                showAlert("✅ Print job sent to printer: " + jobName);
            } catch (Exception e) {
                showAlert("❌ Error sending to printer: " + e.getMessage());
            }
        }
    }
    
    private Node createPrintableNode(ObservableList<VatSaleModel> dataToPrint, String title) {
        VBox printBox = new VBox(20);
        printBox.setPadding(new Insets(30));
        printBox.setStyle("-fx-background-color: white;");
        
        // Add disclaimer
        Label disclaimer = new Label("⚠️ VOIDED RECORDS ARE EXCLUDED");
        disclaimer.setFont(Font.font("Arial", 12));
        disclaimer.setStyle("-fx-font-weight: bold; -fx-text-fill: #e74c3c;");
        
        // Title
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Date
        Label dateLabel = new Label("Generated on: " + LocalDate.now() + " | Generated by: " + currentUser);
        dateLabel.setFont(Font.font("Arial", 10));
        
        // Summary
        double taxableTotal = dataToPrint.stream()
            .filter(s -> s.getSaleType() == 1)
            .mapToDouble(VatSaleModel::getTotalAfterVat)
            .sum();
        double exemptTotal = dataToPrint.stream()
            .filter(s -> s.getSaleType() != 1)
            .mapToDouble(VatSaleModel::getTotalAfterVat)
            .sum();
        double grandTotal = dataToPrint.stream()
            .mapToDouble(VatSaleModel::getTotalAfterVat)
            .sum();
        
        Label summary = new Label(String.format("Total Active Records: %d | Taxable Sales: ETB %,.2f | Exempt/Zero Rated: ETB %,.2f", 
            dataToPrint.size(), taxableTotal, exemptTotal));
        summary.setFont(Font.font("Arial", 12));
        summary.setStyle("-fx-font-weight: bold;");
        
        // Create a copy of the table for printing
        TableView<VatSaleModel> printTable = createPrintTableView();
        printTable.setItems(dataToPrint);
        printTable.setPrefHeight(500);
        
        // Footer
        Label footer = new Label(String.format("GRAND TOTAL (ACTIVE): ETB %,.2f", grandTotal));
        footer.setFont(Font.font("Arial", 14));
        footer.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60; -fx-padding: 20 0 0 0;");
        
        printBox.getChildren().addAll(disclaimer, titleLabel, dateLabel, summary, printTable, footer);
        
        return printBox;
    }

    private TableView<VatSaleModel> createPrintTableView() {
        TableView<VatSaleModel> printTable = new TableView<>();
        
        String[] headers = {"Status", "VAT", "Calendar", "Type", "Buyer TIN", "Buyer Name", "Date", 
                           "Receipt", "Description", "Unit", "Qty", "Price", 
                           "Total", "VAT Amt", "After VAT"};
        
        for (String header : headers) {
            TableColumn<VatSaleModel, Object> col = new TableColumn<>(header);
            
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<VatSaleModel, Object>, ObservableValue<Object>>() {
                @Override
                public ObservableValue<Object> call(TableColumn.CellDataFeatures<VatSaleModel, Object> param) {
                    VatSaleModel sale = param.getValue();
                    Object value = null;
                    
                    switch (header) {
                        case "Status": value = sale.isVoided() ? "VOIDED" : "ACTIVE"; break;
                        case "VAT": value = sale.getVatCategory(); break;
                        case "Calendar": value = sale.getCalendarType(); break;
                        case "Type": value = sale.getSaleType(); break;
                        case "Buyer TIN": value = sale.getBuyerTin(); break;
                        case "Buyer Name": value = sale.getBuyerName(); break;
                        case "Date": value = sale.getDateOfSale(); break;
                        case "Receipt": value = sale.getReceiptNumber(); break;
                        case "Description": value = sale.getDescription(); break;
                        case "Unit": value = sale.getUnitMeasure(); break;
                        case "Qty": value = sale.getQuantity(); break;
                        case "Price": value = sale.getUnitPrice(); break;
                        case "Total": value = sale.getTotalValue(); break;
                        case "VAT Amt": value = sale.getVatAmount(); break;
                        case "After VAT": value = sale.getTotalAfterVat(); break;
                    }
                    
                    return new SimpleObjectProperty<>(value);
                }
            });
            
            // Format numeric columns
            if (header.equals("Qty") || header.equals("Price") || 
                header.equals("Total") || header.equals("VAT Amt") || 
                header.equals("After VAT")) {
                col.setCellFactory(tc -> new TableCell<VatSaleModel, Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(String.format("%,.2f", Double.parseDouble(item.toString())));
                        }
                    }
                });
            }
            
            // Format sale type
            if (header.equals("Type")) {
                col.setCellFactory(tc -> new TableCell<VatSaleModel, Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            String type = item.toString();
                            switch(type) {
                                case "1": setText("Taxable"); break;
                                case "2": setText("Zero Rated"); break;
                                case "3": setText("Exempt"); break;
                                default: setText(type);
                            }
                        }
                    }
                });
            }
            
            // Format status
            if (header.equals("Status")) {
                col.setCellFactory(tc -> new TableCell<VatSaleModel, Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            String status = item.toString();
                            if ("VOIDED".equals(status)) {
                                setText(status);
                                setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            } else {
                                setText(status);
                                setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            }
                        }
                    }
                });
            }
            
            printTable.getColumns().add(col);
        }
        
        return printTable;
    }

    // ========== OTHER UPDATED METHODS ==========
    
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
private void saveSale() {
    try {
        clearValidation();
        
        if (!validateMandatoryFields()) {
            return;
        }
        
        if (!validateTin()) {
            return;
        }
        
        // Create sale object using the constructor without voiding fields
        VatSaleModel sale = new VatSaleModel(
            vatCategory.getValue(),
            calendarType.getValue(),
            saleType.getValue(),
            buyerTin.getText().trim(),
            buyerName.getText().trim(),
            datePicker.getValue().toString(),
            mrcNumber.getText().trim(),
            receiptNumber.getText().trim(),
            description.getText().trim(),
            unitMeasure.getValue(),
            Double.parseDouble(quantity.getText()),
            Double.parseDouble(unitPrice.getText()),
            Double.parseDouble(totalValue.getText().replace(",", "")),
            Double.parseDouble(vatAmount.getText().replace(",", "")),
            Double.parseDouble(totalAfterVat.getText().replace(",", ""))
        );
        
        // SET THE CREATED BY USER
        sale.setCreatedBy(currentUser);

        con.saveVatSales(sale);
        
        showAlert("✅ Sale saved successfully!\n" +
                 "Receipt: " + sale.getReceiptNumber() + "\n" +
                 "Sale Type: " + getSaleTypeDescription(sale.getSaleType()) + "\n" +
                 "Amount: " + String.format("%,.2f", sale.getTotalAfterVat()) + "\n" +
                 "Recorded by: " + currentUser); // Show who recorded it
        
        // Clear the form
        clearForm();
        setDefaults();
        
        // Force reload of ALL data from the beginning
        masterList.clear();
        activeList.clear();
        filteredList.clear();
        
        // Load page 0 (where new records appear)
        pagination.setCurrentPageIndex(0);
        loadPage(0);
        
        // Ensure the table shows the new record
        table.refresh();
        
    } catch(Exception ex) {
        showAlert("❌ Error saving sale: " + ex.getMessage());
        ex.printStackTrace();
    }
}


    private String getSaleTypeDescription(int saleType) {
        switch(saleType) {
            case 1: return "Taxable Sale (15% VAT)";
            case 2: return "Zero Rated Sale (0% VAT)";
            case 3: return "Tax Exempted Sale (0% VAT)";
            default: return "Unknown";
        }
    }
    
    private VBox createPage(int pageIndex) {
        loadPage(pageIndex);
        VBox pageBox = new VBox();
        pageBox.getChildren().add(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return pageBox;
    }
    
    // Helper method to get unit measure name
    private String getUnitMeasureName(int unitCode) {
        switch(unitCode) {
            case 2: return "KG";
            case 3: return "ML";
            case 4: return "GM";
            case 5: return "LIT";
            case 6: return "MT";
            case 7: return "PCS";
            case 8: return "CT";
            case 9: return "OTHER";
            case 10: return "PC";
            default: return String.valueOf(unitCode);
        }
    }
}
