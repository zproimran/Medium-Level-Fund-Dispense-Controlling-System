//
//package smarthrms;
//
//import java.awt.Image;
//import java.io.File;
//import javafx.scene.layout.*;
//import javafx.scene.control.*;
//import javafx.scene.text.*;
//import javafx.geometry.*;
//import java.time.LocalDate;
//import javafx.scene.image.ImageView;
//import javafx.stage.FileChooser;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.control.TextFormatter;
//import javafx.scene.layout.VBox;
//import java.util.function.UnaryOperator;
//import javafx.scene.control.cell.PropertyValueFactory;
//
//public class EmployeeRegistration extends VBox {
//    private StackPane contentPane;  
//    private static ImageView imageView;
//    public EmployeeRegistration() {
//        // Styling for the page
//        this.setPrefWidth(1500); // Replace 800 with your desired width.
//        this.setStyle("-fx-padding: 20; -fx-background-color: #FAFAFA; -fx-spacing: 15;");
//        
//        // Title
//        Text title = new Text("Employee Registration");
//        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-fill: #3A4D6B;");
//
//       
//        
//        HBox tab1 = new HBox();
//         tab1.setStyle("-fx-background-color: #ECECEC; -fx-padding: 10; -fx-spacing: 40;-fx-border-color: #CCC;");
//        tab1.setAlignment(Pos.CENTER);
//        
//        HBox tab2 = new HBox();
//         tab2.setStyle("-fx-background-color: #ECECEC; -fx-padding: 10; -fx-spacing: 40; -fx-border-color: #CCC;");
//        tab2.setAlignment(Pos.CENTER);
//        
//        
//
//        String[] tabNames = {
//            "Information", "Remark", "Education",  "Appraisal","Background"
//           
//        };
//        String[] tabName = {
//            "Contact Information","Employee Family", "Disciplinary Cases", "Work Experiance", "Awards"
//        };
//
//        for (String name : tabNames) {
//            Button tabButton = createTabButton(name);
//            tab1.getChildren().add(tabButton);
//        } 
//         for (String name : tabName) {
//            Button tabButton = createTabButton(name);
//            tab2.getChildren().add(tabButton);
//        } 
//          // Row of buttons for tabs
//        VBox tabs = new VBox(10);
//        tabs.getChildren().addAll(tab1,tab2);
//        // Content Pane
//        contentPane = new StackPane();
//        contentPane.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-color: #CCC; -fx-border-width: 1;");
//        contentPane.getChildren().add(createPersonalInfoContent()); // Default content
//        // Add components to the layout
//        this.getChildren().addAll(title, tabs,contentPane);
//      loadTabContent("Information");
//    }
//    
//    private Button createTabButton(String name) {
//        Button button = new Button(name);
//        button.setStyle("-fx-font-size: 13px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 7 15;");
//        button.setOnAction(e -> loadTabContent(name));
//        return button;
//    }
//
//    private void loadTabContent(String tabName) {
//       contentPane.getChildren().clear();
//        switch (tabName) {
//            case "Information":
//                contentPane.getChildren().add(createPersonalInfoContent());
//                break;
//            case "Remark":
//                contentPane.getChildren().add(createRemarkContent());
//                break;
//            case "Education":
//                contentPane.getChildren().add(createLeavesContent());
//                break;
//            case "Contact Information":
//                contentPane.getChildren().add(createContactInformationContent());
//                break;
//            case "Appraisal":
//                contentPane.getChildren().add(createAppraisalContent());
//                break;
//            case "Background":
//                contentPane.getChildren().add(createBackgroundContent());
//                break;
//            case "Employee Family":
//                contentPane.getChildren().add(createEmployeeFamilyContent());
//                break;
//            case "Disciplinary Cases":
//                contentPane.getChildren().add(createDisciplinaryCasesContent());
//                break;
//            case "Work Experiance":
//                contentPane.getChildren().add(createWorkExperianceContent());
//                break;
//            case "Awards":
//                contentPane.getChildren().add(createAwardsContent());
//                break;
//            default:
//                contentPane.getChildren().add(new Label("Content not available."));
//                break;
//        }
//    }
//    
//    
//     private TableView createTable(String... columnNames) {
//        TableView table = new TableView();
//        for (String columnName : columnNames) {
//            TableColumn column = new TableColumn(columnName);
//            column.setCellValueFactory(new PropertyValueFactory<>(columnName.replace(" ", " ").toLowerCase()));
//            table.getColumns().add(column);
//        }
//        return table;
//    }
//
//    // Method to create content for Personal Info tab
//    private VBox createPersonalInfoContent() {
//        VBox layout = new VBox(10);
//        layout.setAlignment(Pos.TOP_LEFT);
//
//        
//        TextField employeeCodeField = new TextField();
//        employeeCodeField.setPromptText("Employee Code");
//        Label employeeCodeLabel = new Label("Employee Code");
//        employeeCodeField.setMaxWidth(120);
//        
//        TextField ftField = new TextField();
//        ftField.setPromptText("FP Id No");
//        Label fpIDLabel = new Label("FP ID No");
//        ftField.setMaxWidth(120);
//        
//        ComboBox<String> courtesyComboBox = new ComboBox<>();
//        courtesyComboBox.getItems().addAll("Ato", "Sr.", "W/ro.","W/rt.","Dr.","H.o","Nurse");
//        courtesyComboBox.setPromptText("Courtesy title");
//        Label courseTitleLabel = new Label("Courtesy title");
//        courseTitleLabel.setMaxWidth(120);
//        
//        TextField fNameField = new TextField();
//        fNameField.setPromptText("First Name");
//         Label firstNameLabel = new Label("First Name");
//         fNameField.setMaxWidth(120);
//        
//         
//        TextField mNameField = new TextField();
//        mNameField.setPromptText("Middle Name");
//        Label middleNameLabel = new Label("Middle Name");
//        mNameField.setMaxWidth(120);
//        
//        TextField lNameField = new TextField();
//        lNameField.setPromptText("Last Name");
//        Label lastNameLabel = new Label("Last Name");
//        lNameField.setMaxWidth(120);
//        
//        ComboBox<String> genderComboBox = new ComboBox<>();
//        genderComboBox.getItems().addAll("Male", "Female", "Other");
//        genderComboBox.setPromptText("Gender");
//         Label genderLabel = new Label("Gender");
//         genderComboBox.setMaxWidth(120);
//
//        ComboBox<String> martialComboBox = new ComboBox<>();
//        martialComboBox.getItems().addAll("Single", "Married", "Divorsed","Widow");
//        martialComboBox.setPromptText("Martial Status");
//         Label martialStatusLabel = new Label("Martial Status");
//         martialComboBox.setMaxWidth(120);
//         
//        
//        ComboBox<String> nationalityComboBox = new ComboBox<>();
//        nationalityComboBox.getItems().addAll("Ethiopia", "Other");
//        nationalityComboBox.setPromptText("Nationality");
//        Label nationalityLabel = new Label("Nationality");
//        nationalityComboBox.setMaxWidth(120);
//        
//        ComboBox<String> ethnicityComboBox = new ComboBox<>();
//        ethnicityComboBox.getItems().addAll("a", "b");
//        ethnicityComboBox.setPromptText("Ethnicity");
//        Label ethnicityLabel = new Label("Ethnicity");
//         ethnicityComboBox.setMaxWidth(120);
//        
//        TextField motherNameField = new TextField();
//        motherNameField.setPromptText("Mother Name");
//        Label motherNameLabel = new Label("Mother Name");
//         motherNameField.setMaxWidth(120);
//        
//        TextField tinNoField = new TextField();
//        tinNoField.setPromptText("Tin No");
//        Label tinnoLabel = new Label("Tin No");
//        tinNoField.setMaxWidth(120);
//        
//        TextField telephoneField = new TextField();
//        telephoneField.setPromptText("Telephone");
//        Label telLabel = new Label("Telephone");
//        telephoneField.setMaxWidth(120);
//         
//        TextField mobileField = new TextField();
//        mobileField.setPromptText("Mobile");
//        Label mobileLabel = new Label("Mobile");
//        mobileField.setMaxWidth(120);
//        
//        TextField emailField = new TextField();
//        emailField.setPromptText("Email");
//        Label emailLabel = new Label("Email");
//        emailField.setMaxWidth(120);
//        
//        
//        DatePicker dobPicker = new DatePicker();
//        dobPicker.setPromptText("Birth Date");
//        Label birthDateLabel = new Label("Birth Date");
//         dobPicker.setMaxWidth(120);
//        
//        DatePicker hdPicker = new DatePicker();
//        hdPicker.setPromptText("Hire Date");
//        Label hireDateLabel = new Label("Hire Date");
//        hdPicker.setMaxWidth(120);
//        
//        
//         RadioButton permanent = new RadioButton("Permant");
//        RadioButton temporary = new RadioButton("Temporary");
//        Label employemmentLabel = new Label("Employment");
//        employemmentLabel.setMaxWidth(120);
//        
//        
//        // Group them together
//        ToggleGroup group = new ToggleGroup();
//        permanent.setToggleGroup(group);
//        temporary.setToggleGroup(group);
//        
//
//        // Set default selection
//       // permanent.setSelected(true);
//
//        // Add a listener to show selected option
//        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
//            if (newToggle != null) {
//                RadioButton selected = (RadioButton) newToggle;
//                //selected.setText("You selected: " + selected.getText());
//            }
//        });
//        
//       TextField contactDurationField = new TextField();
//        contactDurationField.setPromptText("Contract Duration");
//        Label contactDurationLabel = new Label("Contract Duration");
//        contactDurationField.setMaxWidth(120);
//        
//         // Apply the filter with a TextFormatter
//        
//          // Create a filter that allows only digits
//        UnaryOperator<TextFormatter.Change> filter = change -> {
//            String newText = change.getControlNewText();
//            if (newText.matches("\\d*")) {
//                return change; // Accept change
//            }
//            return null; // Reject change
//        };
//        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
//        contactDurationField.setTextFormatter(textFormatter);
//           
//        DatePicker terminateField = new DatePicker();
//        terminateField.setPromptText("Terminate");
//        Label terminateLabel = new Label("Terminate");
//        terminateField.setMaxWidth(120);
//        
//        
//        CheckBox   active  = new CheckBox("Active");
//        CheckBox costSharing  = new CheckBox("CostSharing");
//        CheckBox   bonus  = new CheckBox("Bonus");
//        CheckBox calculateOT  = new CheckBox("Calculate OT Automatically");
//        
//        TextField formVacancyField = new TextField();
//        formVacancyField.setPromptText("From Vacancy");
//        Label formVacancyLabel = new Label("From Vacancy");
//        formVacancyField.setMaxWidth(120);
//        
//        Label photolabel = new Label("Employee Photo");
//        imageView = new ImageView();
//        imageView.setFitWidth(150);
//        imageView.setFitHeight(150);
//        imageView.setPreserveRatio(true);
//        imageView.setStyle("-fx-border-color: gray; -fx-border-width: 1px;");
//
//        FileChooser chooser = new FileChooser();
//        Button choose = new Button("Choose File");
//        Button clear = new Button("Clear");
//
//        choose.setOnAction(e -> {
//            File file = chooser.showOpenDialog(null);
//            if (file != null) {
//               // imageView.setImage(new Image(file.toURI().toString()));
//            }
//        });
//        clear.setOnAction(e -> imageView.setImage(null));
// 
//      HBox h1 = new HBox();
//      h1.getChildren().addAll(active,costSharing,bonus,calculateOT);
//      
//       VBox v1= new VBox();
//      v1.getChildren().addAll(employeeCodeLabel,employeeCodeField, new Label("       "),fpIDLabel,ftField, new Label("       "),courseTitleLabel,courtesyComboBox,firstNameLabel,fNameField,middleNameLabel,mNameField,lastNameLabel,lNameField,genderLabel,genderComboBox,martialStatusLabel,martialComboBox,contactDurationLabel,contactDurationField,terminateLabel,terminateField,h1);
//      
//      VBox v2= new VBox();
//      v2.getChildren().addAll(nationalityLabel,nationalityComboBox,ethnicityLabel,ethnicityComboBox,motherNameLabel,motherNameField,tinnoLabel,tinNoField, new Label("       "),telLabel,telephoneField,new Label("       ") ,mobileLabel,mobileField,emailLabel,emailField,birthDateLabel,dobPicker,hireDateLabel,hdPicker,employemmentLabel,permanent,temporary);
//             
//       VBox v3= new VBox();
//      v3.getChildren().addAll(formVacancyLabel,formVacancyField, new Label("     "),photolabel,choose); 
//         
//       HBox h = new HBox();
//      h.getChildren().addAll(v1, new Label("     "),v2,new Label("             "),v3);
//      
//      layout.getChildren().addAll(h);
//        return layout;
//    }
//    // Method to create content for Address tab
//    private VBox createRemarkContent() {
//        VBox layout = new VBox(10);
//        layout.setAlignment(Pos.TOP_LEFT);
//
//        TextField liscenceField = new TextField();
//        liscenceField.setPromptText("Liscence No");
//        Label liscenceLabel = new Label("Liscence No");
//         
//        TextField bankaccField = new TextField();
//        bankaccField.setPromptText("Bank Acc.No");
//         Label bankaccLabel = new Label("Bank Acc.No");
//
//        TextArea pfField = new TextArea();
//        pfField.setPromptText("PF Account No");
//        Label  pfLabel = new Label("PF Account No");
//        pfLabel.setMaxWidth(120);
//        
//        TextField idCodeField = new TextField();
//        idCodeField.setPromptText("ID Code");
//        Label idCodeLabel = new Label("ID Code");
//        idCodeLabel.setMaxWidth(120);
//        
//        ComboBox<String> bankComboBox = new ComboBox<>();
//        bankComboBox.getItems().addAll("CBE", "Dashen" ,"Hijra");
//        bankComboBox.setPromptText("Bank");
//        Label bankLabel = new Label("Bank");
//        bankComboBox.setMaxWidth(120);
//        
//        ComboBox<String> branchComboBox = new ComboBox<>();
//        branchComboBox.getItems().addAll("Branch A", "Branch B");
//        branchComboBox.setPromptText("Branch");
//        Label branchLabel = new Label("Branch");
//         branchComboBox.setMaxWidth(120);
//            
//        ComboBox<String> departmentComboBox = new ComboBox<>();
//        departmentComboBox.getItems().addAll("Department A", "Department B");
//        departmentComboBox.setPromptText("Department");
//        Label departmentLabel = new Label("Department");
//         departmentComboBox.setMaxWidth(120);
//        
//        TextField jobPositionField = new TextField();
//        jobPositionField.setPromptText("Job Position");
//        Label jobPositionLabel = new Label("Job Position");
//        jobPositionField.setMaxWidth(120);        
//        
//        ComboBox<String> gradeComboBox = new ComboBox<>();
//        gradeComboBox.getItems().addAll("A", "B");
//        gradeComboBox.setPromptText("Grade");
//        Label gradeLabel = new Label("Grade");
//         gradeComboBox.setMaxWidth(120);
//        
//        ComboBox<String> monthlySalaryComboBox = new ComboBox<>();
//        monthlySalaryComboBox.getItems().addAll("1000", "2000","3000","4000","5000");
//        monthlySalaryComboBox.setPromptText("Monthly Salary");
//        Label monthlySalaryLabel = new Label("Monthly Salary");
//         monthlySalaryComboBox.setMaxWidth(120);
//         
//         ComboBox<String> categoryComboBox = new ComboBox<>();
//        categoryComboBox.getItems().addAll("a", "b");
//        categoryComboBox.setPromptText("Category");
//        Label categoryLabel = new Label("Category");
//         categoryComboBox.setMaxWidth(120);
// 
//         ComboBox<String> centerComboBox = new ComboBox<>();
//        centerComboBox.getItems().addAll("a", "b");
//        centerComboBox.setPromptText("Center");
//        Label centerLabel = new Label("Center");
//         centerComboBox.setMaxWidth(120);
//         
//         ComboBox<String> subCenterComboBox = new ComboBox<>();
//        subCenterComboBox.getItems().addAll("a", "b");
//        subCenterComboBox.setPromptText("Sub Center");
//        Label subCenterLabel = new Label("Sub Center");
//         subCenterComboBox.setMaxWidth(120);
//         
//         ComboBox<String> projectComboBox = new ComboBox<>();
//        projectComboBox.getItems().addAll("Project A", "Project B");
//        projectComboBox.setPromptText("Project");
//        Label projectComboBoxLabel = new Label("Project");
//         projectComboBox.setMaxWidth(120);
//         
//
//         ComboBox<String> programComboBox = new ComboBox<>();
//        programComboBox.getItems().addAll("Program A", "Program B");
//        programComboBox.setPromptText("Program");
//        Label programLabel = new Label("Program");
//         programComboBox.setMaxWidth(120);
//         
//         ComboBox<String> disciplineComboBox = new ComboBox<>();
//        disciplineComboBox.getItems().addAll("Discipline  A", "Discipline B");
//        disciplineComboBox.setPromptText("Discipline");
//        Label disciplineLabel = new Label("Discipline");
//         disciplineComboBox.setMaxWidth(120);
//         
//  
//         ComboBox<String> specializationComboBox = new ComboBox<>();
//        specializationComboBox.getItems().addAll("A", "B");
//        specializationComboBox.setPromptText("Specializzation");
//        Label specializationLabel = new Label("Specializzation");
//         specializationComboBox.setMaxWidth(120);  
//         
//         ComboBox<String> salaryTypeComboBox = new ComboBox<>();
//        salaryTypeComboBox.getItems().addAll("a", "b");
//        salaryTypeComboBox.setPromptText("Salary");
//        Label salaryTypeLabel = new Label("Salary");
//         salaryTypeComboBox.setMaxWidth(120);
//         
//        TextField remarkField = new TextField();
//        remarkField.setPromptText("Remark");
//        Label remarkLabel = new Label("Remark");
//         remarkField.setMaxWidth(120);
//         
//         
//          RadioButton direct  = new RadioButton("Direct");
//        RadioButton nonDirect  = new RadioButton("Non Direct");
//        Label manufacturingLabel = new Label("Manufacturing");
//        manufacturingLabel.setMaxWidth(100);
//        
//         ComboBox<String> serviceLocationComboBox = new ComboBox<>();
//         serviceLocationComboBox.getItems().addAll("A", "B");
//         serviceLocationComboBox.setPromptText("Service Location");
//         Label serviceLocationLabel = new Label(" Service Location ");
//         serviceLocationComboBox.setMaxWidth(100);
//         
//         
//         Label overTimeLabel = new Label(" Over Time"); 
//         RadioButton company1  = new RadioButton("Company Hr");
//         RadioButton position1  = new RadioButton("Position Hr");
//         RadioButton payroll1 = new RadioButton("Payroll Period Hr");
//          
//         Label serviceChargeLabel = new Label(" Service Charge"); 
//         RadioButton company2  = new RadioButton("Company Hr");
//         RadioButton position2  = new RadioButton("Position Hr");
//         RadioButton payroll2 = new RadioButton("Payroll Period Hr");
//         
//         Label employeementDayMissLabel = new Label(" Employeement Day Miss"); 
//         RadioButton company3  = new RadioButton("Company Hr");
//         RadioButton position3  = new RadioButton("Position Hr");
//         RadioButton payroll3 = new RadioButton("Payroll Period Hr");
//         
//         Label allowanceLabel = new Label("Allowance"); 
//         RadioButton company4  = new RadioButton("Company Hr");
//         RadioButton position4  = new RadioButton("Position Hr");
//         RadioButton payroll4 = new RadioButton("Payroll Period Hr");
//
//     VBox v4 = new VBox();
//      v4.getChildren().addAll(liscenceLabel,liscenceField,new Label("   "),bankaccLabel,bankaccField,new Label("   "),pfLabel,pfField,new Label("   "),idCodeLabel,idCodeField,new Label("   "),
//         bankLabel, bankComboBox,new Label("   "),  branchLabel,  branchComboBox,new Label("   "),departmentLabel,departmentComboBox,new Label("   "),jobPositionLabel,jobPositionField,
//          categoryLabel,categoryComboBox,new Label("   ")
//         );
//      
//      VBox  v5 = new VBox();
//      v5.getChildren().addAll(  centerLabel, centerComboBox, monthlySalaryLabel,monthlySalaryComboBox,new Label("   "),gradeLabel, gradeComboBox,new Label("   "),subCenterLabel,subCenterComboBox, new Label("   "),projectComboBoxLabel,projectComboBox,new Label("   "),programLabel,programComboBox,new Label("   "),
//         disciplineLabel,disciplineComboBox,new Label("   "),  new Label("   "),remarkLabel,remarkField
//         );
//      
//        VBox v6 = new VBox();
//        v6.getChildren().addAll(specializationLabel, specializationComboBox,salaryTypeLabel,salaryTypeComboBox,manufacturingLabel,direct,nonDirect ,serviceLocationLabel,serviceLocationComboBox,
//               new Label("          "), overTimeLabel,company1,position1,payroll1,new Label("          "),serviceChargeLabel,company2,position2,payroll2,new Label("         "),employeementDayMissLabel,company3,position3,payroll3,new Label("        "),allowanceLabel,company4,position4,payroll4);
//        
//       HBox address= new HBox();
//       address.getChildren().addAll(v4,new Label("                    "),v5,new Label("       "),v6);
//               
//      
//        layout.getChildren().addAll(address);
//        return layout;
//
//        
//    }
//
//    // Method to create content for Education tab
//    private VBox createLeavesContent() {
//        VBox layout = new VBox(10);
//        layout.setAlignment(Pos.TOP_LEFT);
//
//        TextField instituteField = new TextField();
//        instituteField.setPromptText("Institute");
//
//        TextField degreeField = new TextField();
//        degreeField.setPromptText("Degree");
//
//        TextField yearField = new TextField();
//        yearField.setPromptText("Graduation Year");
//
//        Button saveButton = new Button("Save");
//        saveButton.setOnAction(e -> saveEducation(instituteField.getText(), degreeField.getText(), yearField.getText()));
//
//        layout.getChildren().addAll(new Label("Education"), instituteField, degreeField, yearField, saveButton);
//        return layout;
//    }
//
//    // Method to create content for Experience tab
//    private VBox createContactInformationContent() {
//        VBox layout = new VBox(10);
//        layout.setAlignment(Pos.TOP_LEFT);
// 
//        Label contactLabel = new Label(" Contact Name ");
//        TextField contactField = new TextField();
//        contactField.setPromptText("Contact Name");
//
//        Label relationShipLabel = new Label(" Relation Ship ");
//        ComboBox<String> relationShipComboBox = new ComboBox<>();
//         relationShipComboBox.getItems().addAll("A", "B");
//         relationShipComboBox.setPromptText(" Relation Ship");
//         relationShipComboBox.setMaxWidth(100);
//         
//          Label occupationLabel = new Label(" Occupation ");
//        ComboBox<String> occupationComboBox = new ComboBox<>();
//         occupationComboBox.getItems().addAll("AA", "BB");
//         occupationComboBox.setPromptText(" Occupation ");
//         occupationComboBox.setMaxWidth(100);
//         
//         
//           Label workPlaceLabel = new Label(" Work Place ");
//        ComboBox<String> workPlaceComboBox = new ComboBox<>();
//         workPlaceComboBox.getItems().addAll("AA", "BB");
//         workPlaceComboBox.setPromptText(" Work Place ");
//         workPlaceComboBox.setMaxWidth(100);
//             
//         Label nationalityLabel = new Label("Nationality ");
//        ComboBox<String> nationalityComboBox = new ComboBox<>();
//         nationalityComboBox.getItems().addAll("", "BB");
//         nationalityComboBox.setPromptText(" Nationality");
//         nationalityComboBox.setMaxWidth(100);
//           
//        VBox v7 = new VBox();
//        v7.getChildren().addAll( contactLabel,contactField,new Label("    "),relationShipLabel,relationShipComboBox,new Label("    "),occupationLabel,occupationComboBox,new Label("    "),workPlaceLabel,workPlaceComboBox,new Label("    "),nationalityLabel,nationalityComboBox);
//        
//       Label cityLabel = new Label("City ");
//        TextField cityField = new TextField();
//        cityField.setPromptText("City");
//        
//       Label contactAddressLabel = new Label("Address ");
//        TextField contactAddressField = new TextField();
//        contactAddressField.setPromptText("Address");
// 
//        Label homePhoneLabel = new Label("Home Phone ");
//        TextField homePhoneField = new TextField();
//        homePhoneField.setPromptText("Home Phone");
//        
//        Label workPhoneLabel = new Label(" Work Phone ");
//        TextField workPhoneField = new TextField();
//        workPhoneField.setPromptText("Work Phone");
//        
//        Label cellPhoneLabel = new Label("Cell Phone ");
//        TextField cellPhoneField = new TextField();
//        cellPhoneField.setPromptText("Cell Phone");
//        
//        VBox v8 = new VBox();
//        v8.getChildren().addAll(cityLabel,cityField,new Label("   "),contactAddressLabel,contactAddressField,new Label("   "),homePhoneLabel,homePhoneField,new Label("    "),workPhoneLabel,workPhoneField,new Label("    "),cellPhoneLabel,cellPhoneField);
//        
//        Label saveContactLabel = new Label("Save");
//        Button saveContact = new Button(     );
//         saveContact.setPrefWidth(60);  
//         saveContact.setPrefHeight(30);
//       
//        Label activeContactLabel = new Label("Active");
//        Button activeContact = new Button();
//        activeContact.setPrefWidth(60);  
//        activeContact.setPrefHeight(30);
//        
//        Label inactiveContactLabel = new Label("InActive");
//        Button inactiveContact = new Button();
//        inactiveContact.setPrefWidth(60);  
//        inactiveContact.setPrefHeight(30);
//        
//        HBox v9  = new HBox();
//        v9.getChildren().addAll(saveContactLabel,new Label("    "),saveContact,new Label("                    "),activeContactLabel,new Label("    "),activeContact,new Label("                "),inactiveContactLabel,inactiveContact);
//        
//       HBox contactInfo= new HBox();
//       contactInfo.getChildren().addAll(v7,new Label("                                         "),v8, new Label("                       "),v9);
//       
//        layout.getChildren().addAll(contactInfo);
//        return layout;
//    }
//
//    // Method to create content for Bank Account tab
//    private VBox createAppraisalContent() {
//        
//        VBox layout = new VBox(10);
//        layout.setAlignment(Pos.TOP_LEFT);
//
//        TableView table = createTable("Date", "Discription");
//       
//        
//       VBox v10 = new VBox(10);
//         v10.getChildren().addAll( table);
//        
//         Label evaluationperiodLabel = new Label("Evaluation Period ");
//        ComboBox<String> evaluationPeriodComboBox = new ComboBox<>();
//         evaluationPeriodComboBox.getItems().addAll("E1", "E2");
//         evaluationPeriodComboBox.setPromptText(" Evaluation Period");
//         evaluationPeriodComboBox.setMaxWidth(100);
//         
//         VBox v11 = new VBox(10);
//         v11.getChildren().addAll( evaluationperiodLabel,evaluationPeriodComboBox);
//         
//         Label dateLabel = new Label("Date");
//         DatePicker date =  new DatePicker();
//         
//        Label scoreLabel = new Label("Score");
//        TextField scoreField = new TextField();
//        scoreField.setPromptText("Score");
//
//        Label averageLabel = new Label("Average");
//        TextField averageField = new TextField();
//        averageField.setPromptText("Avarage");
//
//        Label totalLabel = new Label("Total");
//        TextField totalField = new TextField();
//        totalField.setPromptText("Account Number");
//
//        Button saveApprisal= new Button("Save");
//        
//        VBox v12 = new VBox(10);
//         v12.getChildren().addAll(dateLabel,date,scoreLabel,scoreField,averageLabel,averageField,totalLabel,totalField,saveApprisal);
//        
//
//        
//        HBox apprisal = new HBox();
//        apprisal.getChildren().addAll(v10, new Label("           "), v11, new Label("     "), v12);
//        layout.getChildren().addAll(apprisal);
//       return layout;
//    }
//
//    // Method to create content for Document tab
//    private VBox createBackgroundContent() {
//        
//        VBox layout = new VBox(10);
//        layout.setAlignment(Pos.TOP_LEFT);
//
//        TableView table = createTable("School Name", "From" ,"To" ,"Major","Level","Graduate Status");
//        table.setMaxHeight(150);
//        Label schoolLabel = new Label("School Name");
//        TextField schoolNameField = new TextField();
//        schoolNameField.setPromptText("School Name");
//        
//        Label fromdatelabel = new Label(" Date From");
//        DatePicker fromDateField = new DatePicker();
//        fromDateField.setMaxWidth(100);
//        
//         Label todatelabel = new Label("Date To");
//        DatePicker toDateField = new DatePicker();
//        toDateField.setMaxWidth(100);
//       
//        Label majorLabel = new Label("Major");
//        TextField majorField = new TextField();
//         majorField.setMaxWidth(100);
//         
//         Label gpaLabel = new Label("GPA");
//        TextField gpaField = new TextField();
//          gpaField.setMaxWidth(100);
//        
//         Label eduLabel = new Label(" Education Label ");
//        ComboBox<String> eduComboBox = new ComboBox<>();
//         eduComboBox.getItems().addAll("Level", "Degree","Ms","Phd");
//         eduComboBox.setMaxWidth(100);
//
//         
//         Label graduateLabel = new Label(" Graduate ");
//        ComboBox<String> graduateComboBox = new ComboBox<>();
//         graduateComboBox.getItems().addAll("AA", "BB","CC");
//         graduateComboBox.setMaxWidth(100);
//        
//         Label saveBackgroundLabel = new Label("Save");
//         Button saveBackground = new Button();
//          saveBackground.setPrefWidth(60);  
//          saveBackground.setPrefHeight(30);
//         
//         
//          Label deleteBackgroundLabel = new Label("Delete");
//         Button deleteBackground = new Button();
//         deleteBackground.setPrefWidth(60);  
//          deleteBackground.setPrefHeight(30);
//         
//         
//          Label cancelBackgroundLabel = new Label("Cancel");
//          Button cancelBackground = new Button();
//           cancelBackground.setPrefWidth(60);  
//          cancelBackground.setPrefHeight(30);
//         
//         HBox  savevbox = new HBox();
//         savevbox.getChildren().addAll(saveBackgroundLabel,new Label("  "),saveBackground,new Label("       "),deleteBackgroundLabel,new Label("  "),deleteBackground,new Label("       "),cancelBackgroundLabel,new Label("  "),cancelBackground);
//         
//         HBox dateBox = new HBox();
//         dateBox.getChildren().addAll(fromdatelabel,fromDateField,todatelabel,toDateField, new Label("                  "),savevbox);
//         
//         
//         
//        VBox v13 = new VBox(10);
//      v13.getChildren().addAll(table,schoolLabel,schoolNameField,dateBox,majorLabel, majorField,gpaLabel,gpaField,eduLabel,eduComboBox,graduateLabel,graduateComboBox);
//
//        layout.getChildren().addAll(v13);
//        return layout;
//    }
//
//    // Method to create content for Salary tab
//    private VBox createEmployeeFamilyContent() {
//        VBox layout = new VBox(10);
//        layout.setAlignment(Pos.TOP_LEFT);
//   
//        Label fNameLabel = new Label("First Name");
//        TextField fNameField = new TextField();
//         fNameField.setMaxWidth(100);
//         
//         Label mNameLabel = new Label("Middle Name");
//        TextField mNameField = new TextField(); 
//        mNameField.setMaxWidth(100);
//        
//         Label lNameLabel = new Label("Last Name");
//        TextField lNameField = new TextField();
//        lNameField.setMaxWidth(100);
//
//        
//        Label genderLabel = new Label(" Gender ");
//        ComboBox<String> graduateComboBox = new ComboBox<>();
//         graduateComboBox.getItems().addAll("Female", "male");
//         graduateComboBox.setMaxWidth(100);
//         
//         Label birthDateLabel  =  new Label("Birth date");
//         DatePicker birthdate = new DatePicker();
//         
//           Label relationshipLabel = new Label(" Relationship ");
//        ComboBox<String> relationComboBox = new ComboBox<>();
//         relationComboBox.getItems().addAll("Married", "Single");
//         relationComboBox.setMaxWidth(100);
//         
//         Label addEmployeeLabel = new Label("ADD");
//         Button addEmployee = new Button();
//          addEmployee.setPrefWidth(60);  
//          addEmployee.setPrefHeight(30);
//         
//         Label inActiveEmployeeLable= new Label("InActive");
//         Button inActiveEmployeeField = new Button();
//          inActiveEmployeeField.setPrefWidth(60);  
//          inActiveEmployeeField.setPrefHeight(30);
//         
//         Label cancelEmployeeLable= new Label("Cancel");
//         Button cancelEmployeeField = new Button();
//           cancelEmployeeField.setPrefWidth(60);  
//           cancelEmployeeField.setPrefHeight(30);
//         
//             HBox button = new HBox();    
//             button.getChildren().addAll(addEmployeeLabel,addEmployee,new Label("       "),inActiveEmployeeLable,inActiveEmployeeField,new Label("        "),cancelEmployeeLable,cancelEmployeeField);
//         
//          TableView table = createTable("First Name", "Second Name" ,"Last Name" ,"Gender","Birth Date","RelatiionShip","Age"," Status");
//         VBox v14 = new VBox();
//         v14.getChildren().addAll( fNameLabel,fNameField,new Label(" "),mNameLabel,mNameField,new Label(" "),lNameLabel,lNameField ,new Label(" "),genderLabel,graduateComboBox,new Label(" "),birthDateLabel,birthdate,new Label(" "),relationshipLabel,relationComboBox,new Label(" "),button,table);
//        
//          
//        
//
//        layout.getChildren().addAll(v14);
//        return layout;
//    }
//
//    
//    // Method to create content for Leave tab
//    private VBox createDisciplinaryCasesContent() {
//        VBox layout = new VBox(10);
//        layout.setAlignment(Pos.TOP_LEFT);
//
//        Label disciplinaryCasesLabel = new Label("Disciplinary Cases");
//        TextField disciplinaryCasesField = new TextField();
//       disciplinaryCasesField.setMaxWidth(100);
//        Label reportdateLabel  = new Label("Report Date");
//        DatePicker reportDate = new DatePicker();
//
//        
//        Label caseDateLabel  = new Label("Case Date");
//        DatePicker caseDate = new DatePicker();
//        
//        
//        Label reportedbyLabel = new Label("Reported By");
//        TextField reportedbyField = new TextField();
//        reportedbyField.setMaxWidth(100);
//
//        
//        Label addcaseLabel = new Label("ADD");
//         Button addcase = new Button();
//         addcase.setPrefWidth(60);  
//         addcase.setPrefHeight(30);
//         
//         
//         Label inActivecaseLable= new Label("InActive");
//         Button inActivecaseField = new Button();
//         inActivecaseField.setPrefWidth(60);  
//         inActivecaseField.setPrefHeight(30);
//         
//         
//         Label cancelcaseLable= new Label("Cancel");
//         Button cancelcaseField = new Button();
//         cancelcaseField.setPrefWidth(60);  
//         cancelcaseField.setPrefHeight(30);
//         
//             HBox casebutton = new HBox();    
//             casebutton.getChildren().addAll(addcaseLabel,addcase,new Label("         "),inActivecaseLable, new Label("         "),inActivecaseField, new Label("         "),cancelcaseLable,new Label("         "),cancelcaseField);
//         
//          TableView casetable = createTable("Discipliany Case", "Report Date " ,"Case Date" ,"Reported By"," Status");
//         VBox v15 = new VBox();
//         v15.getChildren().addAll( disciplinaryCasesLabel,disciplinaryCasesField,reportdateLabel,reportDate,caseDateLabel,caseDate,reportedbyLabel,reportedbyField , casebutton,casetable);
//        
//
//
//        layout.getChildren().addAll(v15);
//        return layout;
//    }
//
//    // Method to create content for Leave tab
//    private VBox createWorkExperianceContent() {
//        VBox layout = new VBox(10);
//        layout.setAlignment(Pos.TOP_LEFT);
//       
//         Label institutionLabel = new Label("Institution");
//        TextField institutionField = new TextField();
//       institutionField.setMaxWidth(100);
//       
//       Label salaryLabel = new Label("Salary");
//        TextField salaryField = new TextField();
//        salaryField.setMaxWidth(100);
//
//        Label startdateLabel  = new Label("Start Date");
//        DatePicker startDate = new DatePicker();
//
//        Label endDateLabel  = new Label("End Date");
//        DatePicker endDate = new DatePicker();
//
//        Label addworkexLabel = new Label("ADD");
//         Button addworkex = new Button();
//        addworkex.setPrefWidth(60);  
//        addworkex.setPrefHeight(30); 
//
//        Label inActiveworkexLable= new Label("InActive");
//         Button inActivecworkexField = new Button();
//         inActivecworkexField.setPrefWidth(60);  
//         inActivecworkexField.setPrefHeight(30); 
//         
//        Label cancelworkexLable= new Label("Cancel");
//         Button cancelworkexField = new Button();
//         cancelworkexField.setPrefWidth(60);  
//         cancelworkexField.setPrefHeight(30);
//         
//         HBox workbutton = new HBox();    
//         workbutton.getChildren().addAll(addworkexLabel,addworkex,new Label("  "),inActiveworkexLable, new Label("      "),inActivecworkexField, new Label("     "),cancelworkexLable,new Label("     "),cancelworkexField);
//         
//        TableView workextable = createTable("Institution", "Position " ,"Salary" ,"Start date",  "End Date", " Status");
//         VBox v16 = new VBox();
//         v16.getChildren().addAll( institutionLabel,institutionField,salaryLabel,salaryField,startdateLabel,startDate,endDateLabel,endDate , workbutton,workextable);
//        
//        layout.getChildren().addAll(v16);
//        return layout;
//    }
//
//    // Method to create content for Social Media tab
//    private VBox createAwardsContent() {
//        VBox layout = new VBox(10);
//        layout.setAlignment(Pos.TOP_LEFT);
//
//        Label awardLabel = new  Label("Award");
//        TextField awardField = new TextField();
//        awardField.setMaxWidth(100);
//        
//        Label awardDateLabel = new Label("Award Date");
//        DatePicker awardDate = new DatePicker();
//
//        Label remarkLabel = new Label("Remark");
//        TextField remarkField = new TextField();
//        remarkField.setMaxWidth(100);
//       
//        Label addAwardLabel = new Label("ADD");
//         Button addAward = new Button();
//         addAward.setPrefWidth(60);  
//         addAward.setPrefHeight(30);  
//         
//         Label inActiveAwardLable= new Label("InActive");
//         Button inActiveAwardField = new Button();
//         inActiveAwardField.setPrefWidth(60);  
//         inActiveAwardField.setPrefHeight(30);  
//         
//         Label cancelAwardLable= new Label("Cancel");
//         Button cancelAwardField = new Button();
//         cancelAwardField.setPrefWidth(60);  
//         cancelAwardField.setPrefHeight(30); 
//         
//         HBox awardbutton = new HBox();    
//         awardbutton.getChildren().addAll(new Label("                                                                     "),addAwardLabel,new Label(" "),addAward,new Label("  "),inActiveAwardLable, new Label("   "),inActiveAwardField, new Label("   "),cancelAwardLable,new Label("     "),cancelAwardField);
//         
//        TableView  awardtable = createTable("Award", "Award Date " ,"Remark" );
//        awardtable.setMaxWidth(300);
//        
//         VBox v17 = new VBox();
//         v17.getChildren().addAll(awardLabel,awardField,awardDateLabel,awardDate,remarkLabel,remarkField,awardbutton,awardtable);
//         
//         
//        layout.getChildren().addAll(v17);
//        return layout;
//    }
//
//    // Method to create content for Change Password tab
//    private VBox createChangePasswordContent() {
//        VBox layout = new VBox(10);
//        layout.setAlignment(Pos.TOP_LEFT);
//
//        PasswordField oldPasswordField = new PasswordField();
//        oldPasswordField.setPromptText("Old Password");
//
//        PasswordField newPasswordField = new PasswordField();
//        newPasswordField.setPromptText("New Password");
//
//        Button saveButton = new Button("Change Password");
//        saveButton.setOnAction(e -> changePassword(oldPasswordField.getText(), newPasswordField.getText()));
//
//        layout.getChildren().addAll(new Label("Change Password"), oldPasswordField, newPasswordField, saveButton);
//        return layout;
//    }
//
//    // Placeholder save methods for each content
//    private void savePersonalInfo(String firstName, String lastName, String gender, LocalDate dob) {
//        System.out.println("Saved Personal Info: " + firstName + " " + lastName + ", " + gender + ", DOB: " + dob);
//    }
//
//    private void saveAddress(String city, String country, String address) {
//        System.out.println("Saved Address: " + city + ", " + country + ", " + address);
//    }
//
//    private void saveEducation(String institute, String degree, String year) {
//        System.out.println("Saved Education: " + institute + ", " + degree + ", " + year);
//    }
//
//    private void saveExperience(String company, String position, String duration) {
//        System.out.println("Saved Experience: " + company + ", " + position + ", " + duration);
//    }
//
//    private void saveBankAccount(String holder, String bankName, String accountNumber) {
//        System.out.println("Saved Bank Account: " + holder + ", " + bankName + ", " + accountNumber);
//    }
//
//    private void saveDocument(String fileTitle, String fileUrl) {
//        System.out.println("Saved Document: " + fileTitle + ", URL: " + fileUrl);
//    }
//
//    private void saveSalary(String salaryAmount, LocalDate effectiveDate) {
//        System.out.println("Saved Salary: " + salaryAmount + ", Effective Date: " + effectiveDate);
//    }
//
//    private void saveLeave(String leaveType, LocalDate startDate, LocalDate endDate) {
//        System.out.println("Saved Leave: " + leaveType + ", Start Date: " + startDate + ", End Date: " + endDate);
//    }
//
//    private void saveSocialMedia(String facebook, String twitter, String linkedin) {
//        System.out.println("Saved Social Media: Facebook=" + facebook + ", Twitter=" + twitter + ", LinkedIn=" + linkedin);
//    }
//
//    private void changePassword(String oldPassword, String newPassword) {
//        System.out.println("Changed Password: Old=" + oldPassword + ", New=" + newPassword);
//    }
//    
//   
//}
