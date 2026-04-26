package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import java.time.LocalDate;

public class EmployeeRenewalManagement extends BorderPane {
    private String currentUser;

    private TableView<RenewalRecord> renewalTable;
    private ObservableList<RenewalRecord> renewalData = FXCollections.observableArrayList();

    public EmployeeRenewalManagement(String username) {
        this.currentUser=username;
        // Create top section with buttons
        HBox buttonPanel = createButtonPanel();
        
        // Create center section with table
        VBox tableSection = createTableSection();
        
        // Set up the layout
        this.setTop(buttonPanel);
        this.setCenter(tableSection);
        
        // Add some sample data
        loadSampleData();
    }
    
    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(15));
        buttonPanel.setStyle("-fx-background-color: #e9ecef; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
        buttonPanel.setAlignment(Pos.CENTER_LEFT);
        
        // Create buttons
        Button addBtn = new Button("Add New Renewal");
        addBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        addBtn.setOnAction(e -> showRenewalForm());
        
        Button editBtn = new Button("Edit Selected");
        editBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
        editBtn.setOnAction(e -> editSelected());
        
        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteSelected());
        
        Button approveBtn = new Button("Approve Selected");
        approveBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        approveBtn.setOnAction(e -> approveSelected());
        
        Button rejectBtn = new Button("Reject Selected");
        rejectBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        rejectBtn.setOnAction(e -> rejectSelected());
        
        Button exportBtn = new Button("Export to Excel");
        exportBtn.setStyle("-fx-background-color: #fd7e14; -fx-text-fill: white;");
        exportBtn.setOnAction(e -> exportToExcel());
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #6f42c1; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> refreshData());
        
        // Add buttons to panel
        buttonPanel.getChildren().addAll(addBtn, editBtn, deleteBtn, approveBtn, rejectBtn, exportBtn, refreshBtn);
        
        return buttonPanel;
    }
    
    private VBox createTableSection() {
        VBox tableSection = new VBox();
        tableSection.setPadding(new Insets(15));
        
        // Create table
        renewalTable = new TableView<>();
        renewalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Create columns
        TableColumn<RenewalRecord, String> idCol = new TableColumn<>("Employee ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        
        TableColumn<RenewalRecord, String> nameCol = new TableColumn<>("Employee Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        
        TableColumn<RenewalRecord, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        
        TableColumn<RenewalRecord, String> typeCol = new TableColumn<>("Renewal Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("renewalType"));
        
        TableColumn<RenewalRecord, LocalDate> startCol = new TableColumn<>("Start Date");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        
        TableColumn<RenewalRecord, LocalDate> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        
        TableColumn<RenewalRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<RenewalRecord, String> approvedByCol = new TableColumn<>("Approved By");
        approvedByCol.setCellValueFactory(new PropertyValueFactory<>("approvedBy"));
        
        // Add columns to table
        renewalTable.getColumns().addAll(idCol, nameCol, deptCol, typeCol, startCol, endCol, statusCol, approvedByCol);
        
        // Set table properties
        renewalTable.setItems(renewalData);
        renewalTable.setPlaceholder(new Label("No renewal records found"));
        
        // Add search/filter functionality
        HBox filterBox = new HBox(10);
        filterBox.setPadding(new Insets(0, 0, 10, 0));
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search employees...");
        searchField.setPrefWidth(300);
        
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Rejected", "Expired");
        statusFilter.setValue("All");
        
        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> filterTable(searchField.getText(), statusFilter.getValue()));
        
        filterBox.getChildren().addAll(new Label("Search:"), searchField, new Label("Status:"), statusFilter, searchBtn);
        
        tableSection.getChildren().addAll(filterBox, renewalTable);
        
        return tableSection;
    }
    
    private void showRenewalForm() {
        // Create a dialog with the renewal form
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Employee Renewal Form");
        dialog.setHeaderText("Create New Employee Renewal");
        
        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Add the form to the dialog
        EmployeeRenewalForm form = new EmployeeRenewalForm(currentUser);
        dialog.getDialogPane().setContent(form);
        
        // Show and wait for response
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Here you would normally save the data and refresh the table
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Renewal created successfully!", ButtonType.OK);
                alert.showAndWait();
                refreshData();
            }
        });
    }
    
    private void editSelected() {
        RenewalRecord selected = renewalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Create a dialog with the renewal form pre-filled with selected data
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Employee Renewal");
            dialog.setHeaderText("Edit Employee Renewal Record");
            
            // Set the button types
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            // Add the form to the dialog
            EmployeeRenewalForm form = new EmployeeRenewalForm(currentUser);
            dialog.getDialogPane().setContent(form);
            
            // Show and wait for response
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Renewal updated successfully!", ButtonType.OK);
                    alert.showAndWait();
                    refreshData();
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a record to edit.", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    private void deleteSelected() {
        RenewalRecord selected = renewalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to delete the renewal record for " + selected.getEmployeeName() + "?", 
                ButtonType.YES, ButtonType.NO);
            
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    renewalData.remove(selected);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Renewal record deleted successfully!", ButtonType.OK);
                    alert.showAndWait();
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a record to delete.", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    private void approveSelected() {
        RenewalRecord selected = renewalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Approved");
            selected.setApprovedBy("Admin User"); // This would normally be the logged-in user
            renewalTable.refresh();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Renewal approved successfully!", ButtonType.OK);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a record to approve.", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    private void rejectSelected() {
        RenewalRecord selected = renewalTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setStatus("Rejected");
            renewalTable.refresh();
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Renewal rejected.", ButtonType.OK);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a record to reject.", ButtonType.OK);
            alert.showAndWait();
        }
    }
    
    private void exportToExcel() {
        // This would normally export the table data to Excel
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data exported to Excel successfully!", ButtonType.OK);
        alert.showAndWait();
    }
    
    private void refreshData() {
        // This would normally reload data from the database
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data refreshed successfully!", ButtonType.OK);
        alert.showAndWait();
    }
    
    private void filterTable(String searchText, String status) {
        // This would normally filter the table based on search criteria
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Filter applied: " + searchText + " | Status: " + status, ButtonType.OK);
        alert.showAndWait();
    }
    
    private void loadSampleData() {
        // Add some sample data for demonstration
        renewalData.add(new RenewalRecord("EMP001", "John Doe", "IT", "Contract Renewal", 
            LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1), "Approved", "Admin User"));
        
        renewalData.add(new RenewalRecord("EMP002", "Jane Smith", "HR", "Probation Extension", 
            LocalDate.of(2023, 3, 15), LocalDate.of(2023, 9, 15), "Pending", ""));
        
        renewalData.add(new RenewalRecord("EMP003", "Robert Johnson", "Finance", "Visa Renewal", 
            LocalDate.of(2023, 2, 1), LocalDate.of(2025, 2, 1), "Approved", "HR Manager"));
        
        renewalData.add(new RenewalRecord("EMP004", "Sarah Williams", "Operations", "Contract Renewal", 
            LocalDate.of(2023, 5, 10), LocalDate.of(2024, 5, 10), "Rejected", ""));
    }
    
    // Model class for renewal records
    public static class RenewalRecord {
        private String employeeId;
        private String employeeName;
        private String department;
        private String renewalType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private String approvedBy;
        
        public RenewalRecord(String employeeId, String employeeName, String department, 
                            String renewalType, LocalDate startDate, LocalDate endDate, 
                            String status, String approvedBy) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.department = department;
            this.renewalType = renewalType;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
            this.approvedBy = approvedBy;
        }
        
        // Getters and setters
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        
        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public String getRenewalType() { return renewalType; }
        public void setRenewalType(String renewalType) { this.renewalType = renewalType; }
        
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getApprovedBy() { return approvedBy; }
        public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    }
}