package smarthrms;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.SQLException;
import java.util.Optional;

public class AgreementManagementPage extends BorderPane {
    private String currentUser;
    private boolean isAdmin;

    // ==============================
    // Text fields & areas
    // ==============================
    private TextField employeeNameField;
    private TextArea introArea, purposeArea, consentArea, partiesArea, natureArea, employerArea, employeeArea;
    private TextArea employerDutiesArea, employeeDutiesArea;

    // DB connection
    private Connecting con;

    // Agreement ID (if editing existing one)
    private Integer currentAgreementId = null;

    // ==============================
    // Constructor
    // ==============================
    public AgreementManagementPage(String username) {
        this.currentUser = username;
        con = new Connecting();
        this.isAdmin = con.isAdmin(currentUser.toLowerCase());
        setPadding(new Insets(20));

        // Header
        Label header = new Label("የሥራ ውል አስተዳደር (Employment Agreement Management)");
        header.setFont(AmharicFontLoader.getAmharicFont(18));
        header.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        setTop(header);
        BorderPane.setMargin(header, new Insets(0, 0, 15, 0));

        // Editor area
        ScrollPane editor = new ScrollPane(createEditor());
        editor.setFitToWidth(true);
        editor.setPrefHeight(550);
        setCenter(editor);

        // Action buttons - ONLY UPDATE BUTTON
        HBox actions = new HBox(10);
        actions.setPadding(new Insets(10, 0, 0, 0));

        Button btnUpdate = createStyledButton("🔁 አሻሽል (Update)", "#2980b9");
        
        // Disable update button if user is not admin
        if (!isAdmin) {
            btnUpdate.setDisable(true);
            btnUpdate.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold;");
        }

        btnUpdate.setOnAction(e -> updateAgreement());

        actions.getChildren().addAll(btnUpdate);
        
        // Add admin indicator if not admin
        if (!isAdmin) {
            Label adminNote = new Label("Note: Only administrators can update agreements");
            adminNote.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-font-style: italic;");
            actions.getChildren().add(adminNote);
        }

        setBottom(actions);

        // Load existing agreement automatically
        loadAgreement();
    }

    // ==============================
    // UI Editor
    // ==============================
    private VBox createEditor() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(10));

        VBox employeeSection = createTextFieldSection("1 የሰራተኛ ስም (Employee Name)");
        employeeNameField = (TextField) employeeSection.getChildren().get(1);

        VBox introSection = createTextAreaSection("2 የውል መጀመሪያ (Introduction)");
        introArea = (TextArea) introSection.getChildren().get(1);

        VBox purposeSection = createTextAreaSection("3 የውል ዓላማ (Purpose)");
        purposeArea = (TextArea) purposeSection.getChildren().get(1);

        VBox consentSection = createTextAreaSection("4 የስምምነቱ ፍቃድ (Mutual Consent)");
        consentArea = (TextArea) consentSection.getChildren().get(1);

        VBox partiesSection = createTextAreaSection("5 የውል ወገኖች (Parties)");
        partiesArea = (TextArea) partiesSection.getChildren().get(1);

        VBox natureSection = createTextAreaSection("6 የሥራው ዓይነት (Nature of Work)");
        natureArea = (TextArea) natureSection.getChildren().get(1);

        VBox employerRightsSection = createTextAreaSection("7 የውል ሰጪ መብቶች (Employer Rights)");
        employerArea = (TextArea) employerRightsSection.getChildren().get(1);

        VBox employeeRightsSection = createTextAreaSection("8 የውል ተቀባይ መብቶች (Employee Rights)");
        employeeArea = (TextArea) employeeRightsSection.getChildren().get(1);

        VBox employerDutiesSection = createTextAreaSection("9 የውል ሰጪ ተግባራት (Employer Duties)");
        employerDutiesArea = (TextArea) employerDutiesSection.getChildren().get(1);

        VBox employeeDutiesSection = createTextAreaSection("10 የውል ተቀባይ ተግባራት (Employee Duties)");
        employeeDutiesArea = (TextArea) employeeDutiesSection.getChildren().get(1);

        box.getChildren().addAll(
                employeeSection,
                introSection,
                purposeSection,
                consentSection,
                partiesSection,
                natureSection,
                employerRightsSection,
                employeeRightsSection,
                employerDutiesSection,
                employeeDutiesSection
        );

        return box;
    }

    // ==============================
    // Helper Methods for Fields
    // ==============================
    private VBox createTextFieldSection(String labelText) {
        VBox section = new VBox(5);
        Label label = new Label(labelText);
        label.setFont(AmharicFontLoader.getAmharicFont(14));
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");

        TextField field = new TextField();
        field.setFont(AmharicFontLoader.getAmharicFont(13));
        field.setStyle("-fx-border-color: #cccccc; -fx-background-color: #fcfcfc;");

        section.getChildren().addAll(label, field);
        return section;
    }

    private VBox createTextAreaSection(String labelText) {
        VBox section = new VBox(5);
        Label label = new Label(labelText);
        label.setFont(AmharicFontLoader.getAmharicFont(14));
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");

        TextArea textArea = new TextArea();
        textArea.setFont(AmharicFontLoader.getAmharicFont(13));
        textArea.setWrapText(true);
        textArea.setPrefRowCount(4);
        textArea.setStyle("-fx-border-color: #cccccc; -fx-background-color: #fcfcfc;");

        section.getChildren().addAll(label, textArea);
        return section;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(AmharicFontLoader.getAmharicFont(14));
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        
        // Add hover effects only if button is enabled
        if (!color.equals("#7f8c8d")) {
            button.setOnMouseEntered(e -> {
                if (!button.isDisable()) {
                    button.setStyle("-fx-background-color: derive(" + color + ", 20%); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
                }
            });
            
            button.setOnMouseExited(e -> {
                if (!button.isDisable()) {
                    button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
                }
            });
        }
        
        return button;
    }

    // ==============================
    // Update Existing Agreement (Admin Only)
    // ==============================
    private void updateAgreement() {
        // Check admin permission
        if (!isAdmin) {
            showAlert(Alert.AlertType.WARNING, 
                "Access Denied", 
                "Only administrators can update agreements.\n\n" +
                "አስተዳዳሪዎች ብቻ ውሎችን ማዘመን ይችላሉ።");
            return;
        }

        try {
            if (currentAgreementId == null) {
                showAlert(Alert.AlertType.WARNING, 
                    "No Agreement Loaded", 
                    "Please load an agreement first or create a new one.\n\n" +
                    "እባክዎ መጀመሪያ ውል ያስገቡ ወይም አዲስ ይፍጠሩ።");
                return;
            }

            // Trim all fields to validate
            String employeeName = employeeNameField.getText().trim();
            String intro = introArea.getText().trim();
            String purpose = purposeArea.getText().trim();
            String consent = consentArea.getText().trim();
            String parties = partiesArea.getText().trim();
            String nature = natureArea.getText().trim();
            String employerRights = employerArea.getText().trim();
            String employeeRights = employeeArea.getText().trim();
            String employerDuties = employerDutiesArea.getText().trim();
            String employeeDuties = employeeDutiesArea.getText().trim();

            // Validate all fields are filled
            if (employeeName.isEmpty() || intro.isEmpty() || purpose.isEmpty() || consent.isEmpty() ||
                parties.isEmpty() || nature.isEmpty() || employerRights.isEmpty() ||
                employeeRights.isEmpty() || employerDuties.isEmpty() || employeeDuties.isEmpty()) {

                showAlert(Alert.AlertType.WARNING,
                    "ያልተሟላ መረጃ (Incomplete Information)",
                    "እባክዎ ሁሉንም መረጃዎች በፊት ያስገቡ።\n(Please fill in all fields before updating!)");
                return;
            }

            // Confirmation dialog for update
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("ውል አዘምን (Update Agreement)");
            confirm.setHeaderText("ይህን ውል ማዘመን እርግጠኛ ነዎት?");
            confirm.setContentText("ይህ ቀድሞውኑ ካለው ውል ጋር ይተካል!");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean success = con.updateAgreement(
                    currentAgreementId,
                    employeeName,
                    intro,
                    purpose,
                    consent,
                    parties,
                    nature,
                    employerRights,
                    employeeRights,
                    employerDuties,
                    employeeDuties
                );

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION,
                        "ተዘምኗል (Updated Successfully)",
                        "ውሉ በትክክል ተዘምኗል።\n(The agreement has been updated successfully!)");
                } else {
                    showAlert(Alert.AlertType.WARNING,
                        "አልተዘመነም (Update Failed)",
                        "ውል ማዘመን አልተሳካም።\n(Failed to update the agreement.)");
                }
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR,
                "ስህተት (Error)",
                "Error during update:\n" + e.getMessage());
        }
    }

    // ==============================
    // Load Agreement (Automatically on startup)
    // ==============================
    private void loadAgreement() {
        try {
            PurchaseFundAgreementModel agreement = con.getAgreements();
            if (agreement != null) {
                currentAgreementId = agreement.getId();
                employeeNameField.setText(agreement.getEmployeeName());
                introArea.setText(agreement.getIntroduction());
                purposeArea.setText(agreement.getPurpose());
                consentArea.setText(agreement.getConsent());
                partiesArea.setText(agreement.getParties());
                natureArea.setText(agreement.getNatureOfWork());
                employerArea.setText(agreement.getEmployerRights());
                employeeArea.setText(agreement.getEmployeeRights());
                employerDutiesArea.setText(agreement.getEmployerDuties());
                employeeDutiesArea.setText(agreement.getEmployeeDuties());
                
                
            } else {
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, 
                "Error", 
                "Error during load:\n" + e.getMessage());
        }
    }

    // ==============================
    // Show Alert
    // ==============================
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #ecf0f1;");
        
        alert.showAndWait();
    }
    
}