package smarthrms;

import java.io.File;
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
import javafx.scene.image.ImageView;

public class RequestLeave extends Application {
    private String currentUser;
    private TableView<User> employeeTable;
    private ObservableList<User> userData = FXCollections.observableArrayList();
    private BorderPane root;  // Main UI container
    private Stage primaryStage; // Used only in standalone mode
     private static ImageView imageView;
    
    // Constructor builds UI so getContent() can be called anytime
    public RequestLeave(String username) {
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
        buttonPanel.setPadding(new Insets(10));
        buttonPanel.setAlignment(Pos.CENTER_LEFT);

        Button personalInfoButton = createStyledButton("Requested Leave");
        personalInfoButton.setStyle("-fx-font-size: 12px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        personalInfoButton.setOnAction(e->{
        checkLeaveAvailability();
        }); 
          
        Label searchLabel = new Label("Search");
        TextField searchField = new TextField();
        searchField.setPromptText("Search");
         
          HBox search = new HBox();
          search.getChildren().addAll(searchLabel,new Label("   "),searchField);
          

        buttonPanel.getChildren().addAll(personalInfoButton);
        
        VBox v= new VBox();
        v.getChildren().addAll(buttonPanel,search);
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
        primaryStage.setTitle("Apply New Leave");

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
        
         TableColumn<User, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        
        TableColumn<User, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Add columns to table
        employeeTable.getColumns().addAll(employeecodeCol,  ftCol, titleCol,fullNameCol, genderCol,deptCol,
                statusCol);
    }
    
  private Stage registrationStage = null; 
    
    private void fetchUsers() {
        userData.clear();
        Connecting conn = new Connecting();
     List<User> users = conn.getUsers();
        userData.addAll(users);
        employeeTable.setItems(userData);
    }


    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
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
 
 
}
