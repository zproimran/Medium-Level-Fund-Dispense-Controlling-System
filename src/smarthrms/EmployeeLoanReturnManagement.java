package smarthrms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class EmployeeLoanReturnManagement extends BorderPane {
    private String currentUser;

    private TableView<EmployeeLoanReturnModel> returnTable;
    private ObservableList<EmployeeLoanReturnModel> returnData = FXCollections.observableArrayList();

    public EmployeeLoanReturnManagement(String username) {
        this.currentUser=username;
        HBox buttonPanel = createButtonPanel();
        VBox tableSection = createTableSection();

        this.setTop(buttonPanel);
        this.setCenter(tableSection);
        loadSampleData();
    }

    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(15));
        buttonPanel.setStyle("-fx-background-color: #e9ecef; -fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
        buttonPanel.setAlignment(Pos.CENTER_LEFT);

        Button editBtn = new Button("Edit Selected");
        editBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
        editBtn.setOnAction(e -> editSelected());

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteSelected());

        Button exportBtn = new Button("Export to Excel");
        exportBtn.setStyle("-fx-background-color: #fd7e14; -fx-text-fill: white;");
        exportBtn.setOnAction(e -> exportToExcel());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #6f42c1; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> refreshData());

        buttonPanel.getChildren().addAll(editBtn, deleteBtn, exportBtn, refreshBtn);
        return buttonPanel;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox();
        tableSection.setPadding(new Insets(15));

        HBox filterBox = new HBox(10);
        filterBox.setPadding(new Insets(0,0,10,0));

        TextField searchField = new TextField();
        searchField.setPromptText("Search employee...");
        searchField.setPrefWidth(300);

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Returned");
        statusFilter.setValue("All");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> filterTable(searchField.getText(), statusFilter.getValue()));

        filterBox.getChildren().addAll(new Label("Search:"), searchField, new Label("Status:"), statusFilter, searchBtn);

        returnTable = new TableView<>();
        returnTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<EmployeeLoanReturnModel, String> returnIdCol = new TableColumn<>("Return ID");
        returnIdCol.setCellValueFactory(new PropertyValueFactory<>("returnId"));

        TableColumn<EmployeeLoanReturnModel, String> empCodeCol = new TableColumn<>("Employee Code");
        empCodeCol.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));
        
        TableColumn<EmployeeLoanReturnModel, String> empNameCol = new TableColumn<>("Employee Name");
        empNameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));

        TableColumn<EmployeeLoanReturnModel, String> loanTypeCol = new TableColumn<>("Loan Type");
        loanTypeCol.setCellValueFactory(new PropertyValueFactory<>("loanType"));

        TableColumn<EmployeeLoanReturnModel, Double> amountCol = new TableColumn<>("Amount Returned");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<EmployeeLoanReturnModel, LocalDate> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        TableColumn<EmployeeLoanReturnModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        returnTable.getColumns().addAll(returnIdCol, empCodeCol,empNameCol, loanTypeCol, amountCol, returnDateCol, statusCol);
        returnTable.setItems(returnData);
        returnTable.setPlaceholder(new Label("No loan returns found"));

        tableSection.getChildren().addAll(filterBox, returnTable);
        return tableSection;
    }

    private void showReturnForm(EmployeeLoanReturnModel record) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(record == null ? "Add Loan Return" : "Edit Loan Return");
        dialog.setHeaderText(record == null ? "Add New Loan Return" : "Edit Loan Return");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField empCodeField = new TextField(record == null ? "" : record.getEmployeeCode());
        TextField empNameField = new TextField(record == null ? "" : record.getEmployeeName());
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Personal", "Education", "Housing", "Medical");
        typeBox.setValue(record == null ? "Personal" : record.getLoanType());

        TextField amountField = new TextField(record == null ? "" : String.valueOf(record.getAmount()));
        DatePicker returnDate = new DatePicker(record == null ? LocalDate.now() : record.getReturnDate());
        
        grid.addRow(0, new Label("Employee Code:"), empCodeField);
        grid.addRow(1, new Label("Employee Name:"), empNameField);
        grid.addRow(2, new Label("Loan Type:"), typeBox);
        grid.addRow(3, new Label("Amount Returned:"), amountField);
        grid.addRow(4, new Label("Return Date:"), returnDate);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if(response == ButtonType.OK) {
                if(record == null) {
                    EmployeeLoanReturnModel newRecord = new EmployeeLoanReturnModel(
                            "RET" + (returnData.size()+1),
                            empCodeField.getText(),
                            empNameField.getText(),
                            typeBox.getValue(),
                            Double.parseDouble(amountField.getText()),
                            returnDate.getValue(),
                            "Returned"
                    );
                    returnData.add(newRecord);
                } else {
                    record.setEmployeeName(empNameField.getText());
                    record.setEmployeeCode(empCodeField.getText());
                    record.setLoanType(typeBox.getValue());
                    record.setAmount(Double.parseDouble(amountField.getText()));
                    record.setReturnDate(returnDate.getValue());
                }
                returnTable.refresh();
            }
        });
    }

    private void editSelected() {
        EmployeeLoanReturnModel selected = returnTable.getSelectionModel().getSelectedItem();
        if(selected != null) showReturnForm(selected);
        else new Alert(Alert.AlertType.WARNING,"Please select a record to edit.", ButtonType.OK).showAndWait();
    }

    private void deleteSelected() {
        EmployeeLoanReturnModel selected = returnTable.getSelectionModel().getSelectedItem();
        if(selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this loan return?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> { if(response == ButtonType.YES) returnData.remove(selected); });
        }
    }

    private void exportToExcel() {
        new Alert(Alert.AlertType.INFORMATION,"Exported to Excel (mockup)", ButtonType.OK).showAndWait();
    }

    private void refreshData() {
        new Alert(Alert.AlertType.INFORMATION,"Data refreshed (mockup)", ButtonType.OK).showAndWait();
    }

    private void filterTable(String search, String status) {
        new Alert(Alert.AlertType.INFORMATION,"Filter applied: "+search+" | "+status, ButtonType.OK).showAndWait();
    }

    private void loadSampleData() {
        returnData.add(new EmployeeLoanReturnModel("RET001","EMP001","John Doe","Personal",2500.0,LocalDate.of(2025,9,16),"Returned"));
        returnData.add(new EmployeeLoanReturnModel("RET002","EMP002","Jane Smith","Education",5000.0,LocalDate.of(2025,9,14),"Returned"));
    }
}
