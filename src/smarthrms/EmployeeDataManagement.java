package smarthrms;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.geometry.*;

public class EmployeeDataManagement extends VBox {
    private String currentUser;
    private StackPane contentPane;
    private HBox tableButtonsLayout;

    public EmployeeDataManagement(String username) {
        this.currentUser=username;
        // Styling for the page
        this.setStyle("-fx-padding: 20; -fx-background-color: #F4F4F4; -fx-spacing: 10;");

        // Title
        Text title = new Text("Employee Data Management");
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
        tableButtonsLayout = createTableButtons();

        // Add all components to the layout
        this.getChildren().addAll(title, searchAndButtonsLayout, tableButtonsLayout, contentPane);

        // Load default table (Personal Info) and set active style
        loadTabContent("Personal Info");
        setActiveButton((Button) tableButtonsLayout.getChildren().get(0), tableButtonsLayout);
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 12px; -fx-background-color: teal; -fx-text-fill: white; -fx-padding: 5 15;");
        button.setOnAction(e -> System.out.println(text + " button clicked"));
        return button;
    }

    private HBox createTableButtons() {
        HBox buttonsLayout = new HBox(10);
        buttonsLayout.setAlignment(Pos.TOP_LEFT);
        buttonsLayout.setStyle("-fx-padding: 10;");

        String[] tabNames = {
            "Personal Info", "Remark", "Requested Leave", "Available Leave Days",
            "Appraisal", "Background", "Contact Information", "Employee Family",
            "Disciplinary Cases", "Work Experience", "Award"
        };

        for (String tabName : tabNames) {
            Button btn = new Button(tabName);
            btn.setStyle(defaultButtonStyle());

            btn.setOnAction(e -> {
                loadTabContent(tabName);
                setActiveButton(btn, buttonsLayout);
            });

            buttonsLayout.getChildren().add(btn);
        }

        return buttonsLayout;
    }

    // 🔹 Styles
    private String defaultButtonStyle() {
        return "-fx-font-size: 10px; -fx-font-weight: bolder; " +
               "-fx-background-color: #3A4D6B; -fx-text-fill: white; -fx-padding: 5 15;";
    }

    private String activeButtonStyle() {
        return "-fx-font-size: 10px; -fx-font-weight: bolder; " +
               "-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-padding: 5 15;" +
               "-fx-border-color: #27AE60; -fx-border-width: 2;";
    }

    private void setActiveButton(Button activeBtn, HBox container) {
        for (javafx.scene.Node node : container.getChildren()) {
            if (node instanceof Button) {
                node.setStyle(defaultButtonStyle());
            }
        }
        activeBtn.setStyle(activeButtonStyle());
    }

    // 🔹 Content loader
    private void loadTabContent(String tabName) {
        contentPane.getChildren().clear();
        switch (tabName) {
            case "Personal Info":
                contentPane.getChildren().add(new ViewPersonalInfo());
                break;
            case "Remark":
                contentPane.getChildren().add(new ViewEmployeeRemark());
                break;
            case "Requested Leave":
                contentPane.getChildren().add(new ViewEmployeeLeave());
                break;
            case "Available Leave Days":
                contentPane.getChildren().add(new ViewAssignedmployeeLeave());
                break;
            case "Appraisal":
                contentPane.getChildren().add(new ViewEmployeeAppraisal());
                break;
            case "Background":
                contentPane.getChildren().add(new ViewEmployeeBackground());
                break;
            case "Contact Information":
                contentPane.getChildren().add(new ViewEmployeeEmergencyContact());
                break;
            case "Employee Family":
                contentPane.getChildren().add(new ViewEmployeeFamilyMember());
                break;
            case "Disciplinary Cases":
                contentPane.getChildren().add(new ViewEmployeeDisciplanaryCases());
                break;
            case "Work Experience":
                contentPane.getChildren().add(new ViewEmployeeWorkExperience());
                break;
            case "Award":
                contentPane.getChildren().add(new ViewEmployeeRewards());
                break;
            default:
                contentPane.getChildren().add(new Label("Content not available"));
                break;
        }
    }
}
