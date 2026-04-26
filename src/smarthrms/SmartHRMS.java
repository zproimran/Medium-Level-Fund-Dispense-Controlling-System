package smarthrms;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
 
public class SmartHRMS extends Application {
    private BorderPane root = new BorderPane();
    private HBox navigationBar = new HBox();
    private TabPane tabPane = new TabPane();
    private String currentUser = "Guest"; // Changed from static to instance variable
    private EthiopianToGregorianFX ECToGC;
   
    // Declare management page references without initializing them yet
    private EmployeeDataManagement employeeDataManagement;
    private UserManagementFX userManagement;
    private AddEmployeeData addEmployeeData;
    private AllSystemConfiguration allSystemConfiguration;
    private EmployeeRequestedLeave employeeRequestedLeave;
    private RequestLeave requestLeave;
    private EmployeeAvailableLeaveDays employeeAvailableLeaveDays;
    private EmployeeRenewalForm employeeRenewalForm;
    private EmployeeRenewalManagement employeeRenewalManagement;
    private EmployeeOvertimeManagement employeeOvertimeManagement;
    private EmployeeDeductionManagement employeeDeductionManagement;
    private EmployeeAllowanceManagement employeeAllowanceManagement;
    private EmployeeServiceChargeManagement employeeServiceCharge;
    private EmployeeCashIndemnityManagement employeeCashIndemnityManagement;
    private EmployeeCostSharingManagement employeeCostSharingManagement;
    private EmployeeLoanRequestManagement employeeLoanRequestManagement;
    private EmployeeLoanIssueManagement employeeLoanIssueManagement;
    private EmployeeLoanReturnManagement employeeLoanReturnManagement;
    private ManageTerminatedEmployee manageTerminatedEmployee;
    private EmployeeTerminationRequest employeeTerminationRequest;
    private EmployeeTerminationApproval employeeTerminationApproval;
    private PettyCashRequestManagement pettyCashRequestManagementSystem;
    private PurchaseFundByAgreementRequestManagement purchaseFundByAgreementRequestManagementSystem;
    private PurchaseFundWithRecieptRequestManagement purchaseFundWithRecieptRequestManagementSystem;
    private ZKTECOFX zkteco;
    private EmployeeSignatureManagement employeeSignatureManagement;
    private AgreementManagementPage agreementManagementPage;
    private RecieptBasedCategoryManagementPage categoryManagementPage;
    private PettyCashReportDashboard pettyCashReportDashboard;
    private RecieptBasedPurchaseFundReportDashboard recieptBasedPurchaseFundReportDashboard;
    private AgreementBasedPurchaseFundReportDashboard agreementBasedPurchaseFundReportDashboard;
    private PettyCashReplenishRequestManagement pettyCashReplenishRequestManagement;
    private MaximumAmountManagement maximumAmountManagement;
    private PettyCashCategoryManagementPage pettyCashCategoryManagementPage;
    private AgreementCategoryManagementPage agreementCategoryManagementPage;
    private PurchaseFundReplenishRequestManagement purchaseFundReplenishRequestManagement;
    private AccessManagementSystem accessManagementSystem;
    // Instead of DefaultDashboard
    private DefaultDashboard defaultDashboard;
    private VatSaleDetailsFX vatSaleDetailsFX;
    private VatPurchaseDetailsFX vatPurchaseDetailsFX;

    private Scene scene;
    private Connecting conn;
    
    boolean isAdmin=false;
    boolean isCashier=false;
    boolean isFinanceAdmin=false;
    boolean isAccountant=false;
    boolean isReplenishDispenser=false;
    
    // Static variable to store username when launched from login
    private static String loginUsername = null;

    // Default constructor required by JavaFX
    public SmartHRMS() {
        // Initialize with default values
    }

    // Alternative constructor for launching with username
    public SmartHRMS(String username) {
        this.currentUser = username;
    }

    // Set the username when launched from login
    public static void setLoginUsername(String username) {
        loginUsername = username;
    }

    @Override
    public void init() throws Exception {
        // Initialize database connection here
        this.conn = new Connecting();
    }

    @Override
    public void start(Stage primaryStage) {
        // Use the username from login if available
        if (loginUsername != null) {
            currentUser = loginUsername;
            System.out.println("ERP launched with username: " + conn.getFullNameByUsername(currentUser));
        } else {
            // Check for command line parameters
            Application.Parameters params = getParameters();
            if (!params.getRaw().isEmpty()) {
                currentUser = params.getRaw().get(0);
            } else {
                currentUser = "Guest"; // fallback
            }
        }

        // Initialize all management pages AFTER currentUser is set
        initializeManagementPages();
        
        setupUI(primaryStage);
        
        // Initialize report dashboards here (DB is now ready)
        List<PettyCashRecordModel> pettyCashRecords = conn.getAllPettyCashRecords();
        List<PurchaseFundRecordModel> recieptBasedPurchaseFundRecords = conn.getAllPurchaseFundRequests();
        List<AgreementBasedPurchaseFundRecordModel> agreementBasedpurchaseFundRecords = conn.getAllAgreementBasedPurchaseFundRecords();
        pettyCashReportDashboard = new PettyCashReportDashboard(pettyCashRecords, currentUser);
        agreementBasedPurchaseFundReportDashboard = new AgreementBasedPurchaseFundReportDashboard(agreementBasedpurchaseFundRecords, currentUser);
        recieptBasedPurchaseFundReportDashboard = new RecieptBasedPurchaseFundReportDashboard(recieptBasedPurchaseFundRecords, currentUser);

        // Intercept the close button
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Stop the default close behavior

            // Show confirmation dialog
            boolean exitConfirmed = showExitConfirmation();
            if (exitConfirmed) {
                primaryStage.close(); // Close the app if user clicked YES
            }
            // else do nothing, app stays open
        });
    }

    /**
     * Initialize all management pages with the current username
     */
    private void initializeManagementPages() {
        
        isAdmin=conn.isAdmin(currentUser.toLowerCase());
        isCashier=conn.isCashier(currentUser.toLowerCase());
        isAccountant=conn.isAccountant(currentUser.toLowerCase());
        isFinanceAdmin=conn.isFinanceAdmin(currentUser.toLowerCase());
        isReplenishDispenser=conn.isReplenishDispenser(currentUser.toLowerCase());
        
        ECToGC = new EthiopianToGregorianFX();
        employeeDataManagement = new EmployeeDataManagement(currentUser);
        userManagement = new UserManagementFX(currentUser);
        addEmployeeData = new AddEmployeeData(currentUser);
        allSystemConfiguration = new AllSystemConfiguration(currentUser);
        employeeRequestedLeave = new EmployeeRequestedLeave(currentUser);
        requestLeave = new RequestLeave(currentUser);
        employeeAvailableLeaveDays = new EmployeeAvailableLeaveDays(currentUser);
        employeeRenewalForm = new EmployeeRenewalForm(currentUser);
        employeeRenewalManagement = new EmployeeRenewalManagement(currentUser);
        employeeOvertimeManagement = new EmployeeOvertimeManagement(currentUser);
        employeeDeductionManagement = new EmployeeDeductionManagement(currentUser);
        employeeAllowanceManagement = new EmployeeAllowanceManagement(currentUser);
        employeeServiceCharge = new EmployeeServiceChargeManagement(currentUser);
        employeeCashIndemnityManagement = new EmployeeCashIndemnityManagement(currentUser);
        employeeCostSharingManagement = new EmployeeCostSharingManagement(currentUser);
        employeeLoanRequestManagement = new EmployeeLoanRequestManagement(currentUser);
        employeeLoanIssueManagement = new EmployeeLoanIssueManagement(currentUser);
        employeeLoanReturnManagement = new EmployeeLoanReturnManagement(currentUser);
        manageTerminatedEmployee = new ManageTerminatedEmployee(currentUser);
        employeeTerminationRequest = new EmployeeTerminationRequest(currentUser);
        employeeTerminationApproval = new EmployeeTerminationApproval(currentUser);
        pettyCashRequestManagementSystem = new PettyCashRequestManagement(currentUser);
        purchaseFundByAgreementRequestManagementSystem = new PurchaseFundByAgreementRequestManagement(currentUser);
        purchaseFundWithRecieptRequestManagementSystem = new PurchaseFundWithRecieptRequestManagement(currentUser);
        zkteco = new ZKTECOFX();
        employeeSignatureManagement = new EmployeeSignatureManagement(currentUser);
        agreementManagementPage = new AgreementManagementPage(currentUser);
        categoryManagementPage = new RecieptBasedCategoryManagementPage(currentUser);
        pettyCashReplenishRequestManagement = new PettyCashReplenishRequestManagement(currentUser);
        maximumAmountManagement = new MaximumAmountManagement(currentUser);
        pettyCashCategoryManagementPage =new PettyCashCategoryManagementPage(currentUser);
        agreementCategoryManagementPage = new AgreementCategoryManagementPage(currentUser);
        purchaseFundReplenishRequestManagement= new PurchaseFundReplenishRequestManagement(currentUser);
        accessManagementSystem= new AccessManagementSystem(currentUser);
        defaultDashboard=new DefaultDashboard(currentUser);
        vatSaleDetailsFX =new VatSaleDetailsFX(currentUser);
        vatPurchaseDetailsFX = new VatPurchaseDetailsFX(currentUser);
        
    }

    /**
     * Show a simple confirmation dialog asking if the user wants to exit.
     */
    private boolean showExitConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Press ENTER to confirm, ESC to cancel.");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, noButton);

        // ENTER = Yes, ESC = No
        ((Button) alert.getDialogPane().lookupButton(yesButton)).setDefaultButton(true);
        ((Button) alert.getDialogPane().lookupButton(noButton)).setCancelButton(true);

        return alert.showAndWait().filter(response -> response == yesButton).isPresent();
    }

    /**
     * Launch SmartHRMS from login with username
     */
    public static void launchFromLogin(String username) {
        setLoginUsername(username);
        System.out.println("Launching ERP for user: " + username);
        
        // Launch JavaFX application in a new thread
        new Thread(() -> {
            try {
                Application.launch(SmartHRMS.class);
            } catch (IllegalStateException e) {
                // Application is already running, just show the stage
                System.out.println("ERP already running, showing window...");
            } catch (Exception e) {
                e.printStackTrace();
                // Show error message
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null,
                        "Failed to launch ERP: " + e.getMessage(),
                        "Launch Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
    
    public void setupUI(Stage primaryStage) {
        // Create main layout with sidebar and content area
        SplitPane mainSplitPane = new SplitPane();
        mainSplitPane.setDividerPositions(0.2); // 20% for sidebar, 80% for main content
        
        // Create sidebar
        VBox sidebarContent = new VBox(15);
        sidebarContent.setStyle("-fx-background-color: #2E3A47; -fx-padding: 10;");
        
        Button employeeManagementButton = createSidebarButton("Employee Management", "/icons/employee.png");
        
        employeeManagementButton.setOnAction(e -> {
            replaceMainPanel(new EmployeeDataManagement(currentUser), primaryStage);
        });
        sidebarContent.getChildren().addAll(employeeManagementButton);
        
        PauseTransition delay = new PauseTransition(Duration.seconds(0.0005));
        delay.setOnFinished(event -> setupNavigation());
        delay.play();

        setupMainContent();
        applyStyles();

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(visualBounds.getMinX());
        primaryStage.setY(visualBounds.getMinY());
        primaryStage.setWidth(visualBounds.getWidth());
        primaryStage.setHeight(visualBounds.getHeight());
      
        root.setStyle("-fx-background-color: gray;");
        scene = new Scene(root, visualBounds.getWidth(), visualBounds.getHeight());
        primaryStage.setTitle("Smart ERP - Welcome: " + currentUser);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);
        // ✅ APP ICON HERE
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/appIcon.png")));

        primaryStage.show();
        
        // Set default dashboard as initial content
    openTab("Dashboard", defaultDashboard);
        
        // Show welcome message
      //  showWelcomeMessage();
    }

//    private void showWelcomeMessage() {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Welcome");
//        alert.setHeaderText("Login Successful");
//        alert.setContentText("Welcome to Smart HRMS, " + currentUser + "!");
//        alert.showAndWait();
//    }

    private void replaceMainPanel(Region newContent, Stage primaryStage) {
        SplitPane splitPane = (SplitPane) ((BorderPane) primaryStage.getScene().getRoot()).getCenter();
        Node mainPanelNode = splitPane.getItems().get(1); // Get the second item (main panel)

        if (mainPanelNode instanceof StackPane) {
            StackPane mainPanel = (StackPane) mainPanelNode;
            mainPanel.getChildren().clear();
            mainPanel.getChildren().add(newContent);
        }
    }
    
    // Create a sidebar button with an icon
    private Button createSidebarButton(String text, String iconPath) {
        Button button = new Button(text);
        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            icon.setFitWidth(20);
            icon.setFitHeight(20);
            button.setGraphic(icon);
        } catch (Exception e) {
            System.out.println("Icon not found: " + iconPath);
        }
        button.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-background-color: #3A4D6B; -fx-border-radius: 5px; -fx-padding: 10px;");
        return button;
    }

private void setupNavigation() {
    navigationBar.setPadding(new Insets(15));
    navigationBar.setSpacing(10);
    navigationBar.setBackground(new Background(new BackgroundFill(
        Color.web("#2D3748"), CornerRadii.EMPTY, Insets.EMPTY)));

    List<MenuButton> menuButtons = new ArrayList<>();

    // Fund Request Tracking Menu - only show if not replenish dispenser
    if (!isReplenishDispenser) {
        MenuButton fundTrackingMenu = createMenuButton("Fund Request Tracking", new String[]{
            "PettyCash Fund Request", 
            "Purchase Fund Request With Reciept", 
            "Purchase Fund Request By Agreement"
        });
        menuButtons.add(fundTrackingMenu);
    }

    // Replenish Tracking Menu - always show
    MenuButton replenishMenu = createMenuButton("Replenish Tracking", new String[]{
        "Petty Cash Replenish", 
        "Purchase Fund Replenish"
    });
    menuButtons.add(replenishMenu);
        if(isAdmin){
//    MenuButton employeeMenu =createMenuButton("Employee",new String[]{
//        "Add/Edit Permanent Employee Data",
//        "Manage Employee Data",
//        "Add New Contract Employee",
//        "Manage Contract Employee"
//    });
//    menuButtons.add(employeeMenu);
    
//    MenuButton leaveMenu= createMenuButton("Leave",new String[]{
//    "Request Leave",
//    "View Requested Leave",
//    "View Available Days"
//    });
//    menuButtons.add(leaveMenu);
    
//    MenuButton compensationMenu =createMenuButton("Employee Compensation",new String[]{
//    "Over Time",
//    "Deduction",
//    "Allowance",
//    "Cash Indemnity",
//    "Cost Sharing",
//    "Service Charge"
//    });
//    menuButtons.add(compensationMenu);
    
//    MenuButton loanMenu = createMenuButton("Loan",new String[]{
//    "Employee Loan Request",
//    "Employee Loan Issue",
//    "Employee Loan Return"
//    }); 
//    menuButtons.add(loanMenu);
    
//    MenuButton employeeTerminationMenu = createMenuButton("Employee Termination",new String[]{
//    "Request Termination",
//    "Termination Approval",
//    "Terminated Employees"
//    });
//    menuButtons.add(employeeTerminationMenu);
    

//    MenuButton payrollMenu = createMenuButton("Payroll", new String[]{
//    "Payroll Period",
//    "Recored",
//    "Main Recored",
//    " Data Recored",
//    "Calculate Payroll",
//    "Calculate Incentive" ,
//    "Accruals"  ,
//    "Payroll Slip" ,
//    "Incentiva Slip" ,
//    "Employee Slip" ,
//    "Bank Transfer" ,
//    "ERCA",
//    "Profit Fund Slip" ,
//    "Payroll Journal Entry"
//    });
//    menuButtons.add(payrollMenu);
   }
        
    MenuButton etaxMenu= createMenuButton("Etax",new String[]{
    "Sales",
    "Purchases"
    });
    menuButtons.add(etaxMenu);

    // Fund Reports Menu - only show if not replenish dispenser
    if (!isReplenishDispenser) {
        MenuButton fundReportsMenu = createMenuButton("Fund Reports", new String[]{
            "Petty Cash Report", 
            "Reciept Based Purchase Fund Report", 
            "Agreement Based Purchase Fund Report"
        });
        menuButtons.add(fundReportsMenu);
    }

    // Setting Menu - role-based options
    List<String> settingOptions = new ArrayList<>();

    if (isAdmin) {
        settingOptions.add("Manage System User");
        settingOptions.add("Add New User");
        settingOptions.add("Access Management");
    }
    
    if (isAdmin || isFinanceAdmin) {
        settingOptions.add("Manage Employee Signature");
        settingOptions.add("System Configuration");
        settingOptions.add("Manage Purchase Fund With Reciept Category");
        settingOptions.add("Manage Purchase Fund With Agreement Category");
        settingOptions.add("Manage Petty Cash Fund Category");
        settingOptions.add("Purchase Fund Agreement");
        settingOptions.add("Maximum Allowed Request");
    }

    if (!settingOptions.isEmpty()) {
        MenuButton settingMenu = createMenuButton("Setting", settingOptions.toArray(new String[0]));
        menuButtons.add(settingMenu);
    }

    // Add all menu buttons to navigation bar
    navigationBar.getChildren().addAll(menuButtons);
    root.setTop(navigationBar);
}

    private MenuButton createMenuButton(String title, String[] items) {
        MenuButton menuButton = new MenuButton(title);
        menuButton.getStyleClass().add("nav-button");

        for (String item : items) {
            MenuItem menuItem = new MenuItem(item);
            menuItem.setOnAction(e -> handleMenuAction(item));
            menuButton.getItems().add(menuItem);
        }
        return menuButton;
    }

    private void handleMenuAction(String item) {
        Parent panel = null;

        switch (item) {
            case "Manage Employee Data":
                panel = employeeDataManagement;
                break;
            case "Add/Edit Permanent Employee Data":
                panel = addEmployeeData.getContent();
                break;
            case "Date Converter":
                panel = ECToGC;
                break;
            case "Manage System User":
                panel = userManagement.getContent();
                break;
            case "System Configuration":
                panel = allSystemConfiguration;
                break;
            case "Request Leave":
                panel = requestLeave.getContent();
                break;
            case "View Requested Leave":
                panel = employeeRequestedLeave.getContent();
                break;
            case "View Available Days":
                panel = employeeAvailableLeaveDays.getContent();
                break;
            case "Add New Contract Employee":
                panel = employeeRenewalForm;
                break;
            case "Manage Contract Employee":
                panel = employeeRenewalManagement;
                break;
            case "Over Time":
                panel = employeeOvertimeManagement;
                break;
            case "Deduction":
                panel = employeeDeductionManagement;
                break;
            case "Allowance":
                panel = employeeAllowanceManagement;
                break;
            case "Service Charge":
                panel = employeeServiceCharge;
                break;
            case "Cash Indemnity":
                panel = employeeCashIndemnityManagement;
                break;
            case "Cost Sharing":
                panel = employeeCostSharingManagement;
                break;
            case "Employee Loan Request":
                panel = employeeLoanRequestManagement;
                break;
            case "Employee Loan Issue":
                panel = employeeLoanIssueManagement;
                break;
            case "Employee Loan Return":
                panel = employeeLoanReturnManagement;
                break;  
            case "Terminated Employees":
                panel = manageTerminatedEmployee.getContent();
                break;
            case "Request Termination":
                panel = employeeTerminationRequest.getContent();
                break;
            case "Termination Approval":
                panel = employeeTerminationApproval.getContent();
                break;
            case "PettyCash Fund Request":
                panel = pettyCashRequestManagementSystem;
                break;
            case "Purchase Fund Request By Agreement":
                panel = purchaseFundByAgreementRequestManagementSystem;
                break;
            case "Purchase Fund Request With Reciept":
                panel = purchaseFundWithRecieptRequestManagementSystem;
                break;
            case "Add New User":
                panel = zkteco;
                break;
            case "Manage Employee Signature":
                panel = employeeSignatureManagement;
                break;
            case "Manage Purchase Fund With Reciept Category":
                panel = categoryManagementPage;
                break;
            case "Purchase Fund Agreement":
                panel = agreementManagementPage;
                break;
            case "Petty Cash Report":
                panel = pettyCashReportDashboard;
                break;
            case "Reciept Based Purchase Fund Report":
                panel = recieptBasedPurchaseFundReportDashboard;
                break;
            case "Agreement Based Purchase Fund Report":
                panel = agreementBasedPurchaseFundReportDashboard;
                break;
            case "Petty Cash Replenish":
                panel = pettyCashReplenishRequestManagement;
                break;
            case "Maximum Allowed Request":
                panel=maximumAmountManagement;
                break;
            case "Manage Petty Cash Fund Category":
                panel=pettyCashCategoryManagementPage;
                break;
            case "Manage Purchase Fund With Agreement Category":
                panel=agreementCategoryManagementPage;
                break;
            case "Purchase Fund Replenish":
                panel=purchaseFundReplenishRequestManagement;
                break;
            case "Access Management":
                panel=accessManagementSystem;
                break;
            case "Sales":
                panel=vatSaleDetailsFX;
                break;
            case "Purchases":
                panel=vatPurchaseDetailsFX;
                break;
            default:
                System.out.println("Unknown menu item: " + item);
                // For any unknown items, create a new instance with current user
                panel = createDefaultPanel(item);
                return;
        }

        openTab(item, panel);
    }

    /**
     * Create a default panel for unknown menu items
     */
    private Parent createDefaultPanel(String item) {
        Label label = new Label("Feature not implemented: " + item);
        label.setStyle("-fx-font-size: 16px; -fx-padding: 20;");
        return new StackPane(label);
    }

    private void setupMainContent() {
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        root.setCenter(tabPane);
    }

    private void openTab(String title, Parent content) {
        // If tab already exists → select it
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().equals(title)) {
                tabPane.getSelectionModel().select(tab);
                return;
            }
        }

        // Create new styled tab
        Tab tab = new Tab(title, content);
        tab.setClosable(true);

        // Base style for new tab (inactive by default)
        tab.setStyle(
            "-fx-background-color: #E0E0E0;" +  // light gray
            "-fx-text-base-color: black;" +      // text color
            "-fx-padding: 5 15 5 15;" +          // padding
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;"
        );

        // Add the new tab
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);

        // Ensure tab highlighting works
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            for (Tab t : tabPane.getTabs()) {
                if (t == newTab) {
                    t.setStyle(
                        "-fx-background-color: black;" +
                        "-fx-text-base-color: white;" +
                        "-fx-padding: 5 15 5 15;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
                    );
                } else {
                    t.setStyle(
                        "-fx-background-color: #E0E0E0;" +
                        "-fx-text-base-color: black;" +
                        "-fx-padding: 5 15 5 15;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
                    );
                }
            }
        });
    }

    private void applyStyles() {
        navigationBar.setStyle("-fx-spacing: 10;");
    }

    public static void main(String[] args) {
        // If launched directly (not from login), show the login screen
      //  SmartHRMS.launchFromLogin("admin");
        
        SwingUtilities.invokeLater(() -> {  
          //  new LoginLauncher().setVisible(true);
                new FingerprintLogin().setVisible(true);
        });
    }
    
}