package smarthrms;

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
import javafx.stage.Stage;
import java.util.List;

public class EmployeeAvailableLeaveDays extends Application {
    private String currentUser;
    private TableView<User> employeeAvailableLeaveDaysTable;
    private ObservableList<User> leaveData = FXCollections.observableArrayList();
    private BorderPane root;  // Main UI container
    private Stage primaryStage; // Used only in standalone mode
    
    // Constructor builds UI so getContent() can be called anytime
    public EmployeeAvailableLeaveDays(String username) {
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
          
        Label searchLabel = new Label("Search");
        TextField searchField = new TextField();
        searchField.setPromptText("Search");
         
        HBox search = new HBox();
        search.getChildren().addAll(searchLabel,new Label("   "),searchField);
        
        VBox v= new VBox();
        v.getChildren().addAll(buttonPanel,search);
        root.setTop(new VBox(v));

        // Create Table
        createUserTable();
        root.setCenter(employeeAvailableLeaveDaysTable);
    }

    // This method lets external callers get the UI node to embed
    public Parent getContent() {
        return root;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Available Leave Days");

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
    private void createUserTable() {
        employeeAvailableLeaveDaysTable = new TableView<>();
        employeeAvailableLeaveDaysTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create columns
        TableColumn<User, Integer> employeecodeCol = new TableColumn<>("Employee Code");
        employeecodeCol.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));

        TableColumn<User, String> fullNameCol = new TableColumn<>("Employee FullName");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("employeFullname"));

        TableColumn<User, String> leaveYearCol = new TableColumn<>("Leave Year");
        leaveYearCol.setCellValueFactory(new PropertyValueFactory<>("leaveYear"));

        TableColumn<User, String> leaveTypeCol = new TableColumn<>("Leave Type");
        leaveTypeCol.setCellValueFactory(new PropertyValueFactory<>("leaveType"));
        
        TableColumn<User, String> availableDaysCol = new TableColumn<>("Available Days");
        availableDaysCol.setCellValueFactory(new PropertyValueFactory<>("availableDays"));
                
        // Add columns to table
        employeeAvailableLeaveDaysTable.getColumns().addAll(employeecodeCol,  fullNameCol, leaveYearCol, leaveTypeCol,availableDaysCol);
    }
    
    private Stage registrationStage = null; 
    
    private void fetchUsers() {
        leaveData.clear();
        Connecting conn = new Connecting();
        List<User> users = conn.getUsers();
        leaveData.addAll(users);
        employeeAvailableLeaveDaysTable.setItems(leaveData);
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
 
}
