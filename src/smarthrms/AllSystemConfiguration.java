package smarthrms;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.stage.Stage;

public class AllSystemConfiguration extends VBox {

    private StackPane contentPane;
    private String currentUser;
    public AllSystemConfiguration(String username) {
        this.currentUser=username;
        // Styling for the page
        this.setStyle("-fx-padding: 20; -fx-background-color: #F4F4F4; -fx-spacing: 10;");

        // Title
        Text title = new Text("All System Configuration Page");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Action buttons (Copy, CSV, Excel, PDF, Print)
        Button copyButton = createActionButton("Copy");
        Button csvButton = createActionButton("CSV");
        Button excelButton = createActionButton("Excel");
        Button pdfButton = createActionButton("PDF");
        Button printButton = createActionButton("Print");

        // Search Field
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");
        searchField.setMaxWidth(200);

        // Horizontal layout for buttons and search field
        HBox actionButtonsLayout = new HBox(10, copyButton, csvButton, excelButton, pdfButton, printButton);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Push search field to the right
        HBox searchAndButtonsLayout = new HBox(10, actionButtonsLayout, spacer, searchField);
        searchAndButtonsLayout.setAlignment(Pos.CENTER_LEFT);

        // Content pane where the employee tables will be shown
        contentPane = new StackPane();
        contentPane.setStyle("-fx-background-color: white;");

        // Button panel for switching between tables
        HBox tableButtonsLayout = createTableButtons();

        // Add all components to the layout
        this.getChildren().addAll(title, searchAndButtonsLayout, tableButtonsLayout, contentPane);
        
        // Load default table (Personal Info)
        loadTabContent("Performance Standard");
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 12px; -fx-background-color: #3A4D6B; -fx-text-fill: white; -fx-padding: 5 15;");
        button.setOnAction(e -> System.out.println(text + " button clicked"));
        return button;
    }

    private HBox createTableButtons() {
        Button evaluationTypeButton = new Button("Evaluation Type");
        evaluationTypeButton.setOnAction(e -> loadTabContent("Evaluation Type"));

        Button leaveTypeButton = new Button("Leave Type");
        leaveTypeButton.setOnAction(e -> loadTabContent("Leave Type"));

        Button bankBranchButton = new Button("Bank Branch");
        bankBranchButton.setOnAction(e -> loadTabContent("Bank Branch"));

        Button departmentButton = new Button("Department");
        departmentButton.setOnAction(e -> loadTabContent("Department"));

        Button specializationButton = new Button("Specialization");
        specializationButton.setOnAction(e -> loadTabContent("Specialization"));

        Button jobPositionButton = new Button("Job Position");
        jobPositionButton.setOnAction(e -> loadTabContent("Job Position"));
        
        Button educationLevelButton = new Button("Education Level");
        educationLevelButton.setOnAction(e -> loadTabContent("Education Level"));
        
        Button employeeCodeButton = new Button("Employee Code");
        employeeCodeButton.setOnAction(e -> loadTabContent("Employee Code"));

        HBox buttonsLayout = new HBox(10, evaluationTypeButton, leaveTypeButton, bankBranchButton, 
                departmentButton, specializationButton, jobPositionButton, educationLevelButton,employeeCodeButton);
        buttonsLayout.setAlignment(Pos.TOP_LEFT);
        buttonsLayout.setStyle("-fx-padding: 10;");

        return buttonsLayout;
    }

   private Stage registrationStage = null; // Declare a reference to the currently opened employee registration window


    private void loadTabContent(String tabName) {
        contentPane.getChildren().clear();
        switch (tabName) {
            case "Evaluation Type":
                contentPane.getChildren().add(createEvaluationTypeContent());
                break;
            case "Leave Type":
                contentPane.getChildren().add(createLeaveTypeContent());
                break;
            case "Bank Branch":
                contentPane.getChildren().add(createBankNameContent());
                break;
            case "Department":
                contentPane.getChildren().add(createDepartmentContent());
                break;
            case "Specialization":
                contentPane.getChildren().add(createSpecializationContent());
                break;
            case "Job Position":
                contentPane.getChildren().add(createJobPositionContent());
                break;
            case "Education Level":
                contentPane.getChildren().add(createEducationLevelContent());
                break;
             case "Employee Code":
                contentPane.getChildren().add(createEmployeeCodeContent());
                break;
            default:
                contentPane.getChildren().add(new Label("Content not available"));
                break;
        }
    }

    private VBox createEvaluationTypeContent() {
        return new  ManageEvaluationType();

    }
    private VBox createLeaveTypeContent() {
        return new  ManageLeaveType();

    }
    
    private VBox createBankNameContent() {
        return new  ManageBankNames();

    }
    private VBox createDepartmentContent() {
        return new  ManageDepartments();

    }
    
    private VBox createSpecializationContent() {
        return new  ManageSpecializations();

    }
    private VBox createJobPositionContent() {
        return new  ManageJobPositions();

    }
    private VBox createEducationLevelContent() {
        return new  ManageEducationLevel();

    }
    
    private VBox createEmployeeCodeContent() {
        return new  EditEmployeeCodePanel();

    }
    

}
