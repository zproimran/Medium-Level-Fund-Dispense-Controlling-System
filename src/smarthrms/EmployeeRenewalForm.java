package smarthrms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class EmployeeRenewalForm extends VBox {
    private String currentUser;

    public EmployeeRenewalForm(String username) {
        this.currentUser=username;
        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: #f9f9f9;");

        // Wrap in scroll pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: #ddd;");
        VBox formLayout = new VBox(20);
        formLayout.setPadding(new Insets(15));

        // Section: Employee Information
        TitledPane empInfoPane = new TitledPane("Employee Information", createEmployeeInfoSection());
        empInfoPane.setExpanded(true);

        // Section: Renewal Type
        TitledPane renewalTypePane = new TitledPane("Renewal Type", createRenewalTypeSection());

        // Section: Contract Details
        TitledPane contractPane = new TitledPane("Contract Details", createContractSection());

        // Section: Renewal Details
        TitledPane renewalPane = new TitledPane("Renewal Details", createRenewalSection());

        // Section: Compensation & Benefits
        TitledPane compPane = new TitledPane("Compensation & Benefits", createCompensationSection());

        // Section: Approvals
        TitledPane approvalPane = new TitledPane("Approvals & Workflow", createApprovalSection());

        // Section: Compliance Docs
        TitledPane docsPane = new TitledPane("Compliance Documents", createDocumentsSection());

        // Submit Button
        Button submitBtn = new Button("Submit Renewal");
        submitBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        submitBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Renewal Submitted Successfully!", ButtonType.OK);
            alert.showAndWait();
        });

        formLayout.getChildren().addAll(
                empInfoPane,
                renewalTypePane,
                contractPane,
                renewalPane,
                compPane,
                approvalPane,
                docsPane,
                submitBtn
        );

        scrollPane.setContent(formLayout);
        this.getChildren().add(scrollPane);
    }

    private GridPane createEmployeeInfoSection() {
        GridPane grid = createGrid();

        TextField empId = new TextField();
        TextField empName = new TextField();

        ComboBox<String> dept = new ComboBox<>();
        dept.getItems().addAll("HR", "Finance", "IT", "Operations");

        ComboBox<String> position = new ComboBox<>();
        position.getItems().addAll("Manager", "Officer", "Staff", "Intern");

        ComboBox<String> empType = new ComboBox<>();
        empType.getItems().addAll("Contract", "Probationary", "Intern");

        grid.addRow(0, new Label("Employee ID:"), empId);
        grid.addRow(1, new Label("Full Name:"), empName);
        grid.addRow(2, new Label("Department:"), dept);
        grid.addRow(3, new Label("Position:"), position);
        grid.addRow(4, new Label("Employment Type:"), empType);

        return grid;
    }

    private VBox createRenewalTypeSection() {
        VBox box = new VBox(10);

        ToggleGroup renewalTypeGroup = new ToggleGroup();
        RadioButton contract = new RadioButton("Contract Renewal");
        RadioButton probation = new RadioButton("Probation Extension");
        RadioButton benefit = new RadioButton("Benefit Renewal");

        contract.setToggleGroup(renewalTypeGroup);
        probation.setToggleGroup(renewalTypeGroup);
        benefit.setToggleGroup(renewalTypeGroup);

        box.getChildren().addAll(contract, probation, benefit);
        return box;
    }

    private GridPane createContractSection() {
        GridPane grid = createGrid();

        DatePicker startDate = new DatePicker();
        DatePicker endDate = new DatePicker();
        TextField duration = new TextField();

        ComboBox<String> status = new ComboBox<>();
        status.getItems().addAll("Active", "Expired", "Pending Renewal");

        grid.addRow(0, new Label("Current Start Date:"), startDate);
        grid.addRow(1, new Label("Current End Date:"), endDate);
        grid.addRow(2, new Label("Contract Duration:"), duration);
        grid.addRow(3, new Label("Status:"), status);

        return grid;
    }

    private GridPane createRenewalSection() {
        GridPane grid = createGrid();

        DatePicker renewalStart = new DatePicker();
        DatePicker renewalEnd = new DatePicker();
        TextField duration = new TextField();

        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("Extension", "New Contract", "Conversion to Permanent");

        DatePicker effectiveDate = new DatePicker();

        grid.addRow(0, new Label("Renewal Start Date:"), renewalStart);
        grid.addRow(1, new Label("Renewal End Date:"), renewalEnd);
        grid.addRow(2, new Label("Renewal Duration:"), duration);
        grid.addRow(3, new Label("Renewal Type:"), type);
        grid.addRow(4, new Label("Effective Date:"), effectiveDate);

        return grid;
    }

    private GridPane createCompensationSection() {
        GridPane grid = createGrid();

        TextField salary = new TextField();
        TextField allowances = new TextField();

        CheckBox insurance = new CheckBox("Insurance Policy Renewed");
        CheckBox pension = new CheckBox("Pension Updated");

        grid.addRow(0, new Label("New Salary:"), salary);
        grid.addRow(1, new Label("Allowances:"), allowances);
        grid.addRow(2, insurance, pension);

        return grid;
    }

    private GridPane createApprovalSection() {
        GridPane grid = createGrid();

        TextField requestedBy = new TextField();
        TextField approvedBy = new TextField();
        DatePicker approvalDate = new DatePicker();
        TextArea remarks = new TextArea();

        remarks.setPrefRowCount(3);

        grid.addRow(0, new Label("Requested By:"), requestedBy);
        grid.addRow(1, new Label("Approved By:"), approvedBy);
        grid.addRow(2, new Label("Approval Date:"), approvalDate);
        grid.addRow(3, new Label("Remarks:"), remarks);

        return grid;
    }

    private GridPane createDocumentsSection() {
        GridPane grid = createGrid();

        TextField permitNo = new TextField();
        TextField policyNo = new TextField();
        Button uploadBtn = new Button("Upload Contract PDF");

        grid.addRow(0, new Label("Work Permit/Visa No:"), permitNo);
        grid.addRow(1, new Label("Insurance Policy No:"), policyNo);
        grid.addRow(2, new Label("Upload Document:"), uploadBtn);

        return grid;
    }

    // Helper to create nice grid layout
    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(15);
        grid.setPadding(new Insets(10));
        grid.setAlignment(Pos.TOP_LEFT);
        ColumnConstraints col1 = new ColumnConstraints(150);
        ColumnConstraints col2 = new ColumnConstraints(300, 300, Double.MAX_VALUE);
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);
        return grid;
    }
}
