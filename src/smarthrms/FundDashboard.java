//package smarthrms;
//
//import javafx.application.Application;
//import javafx.beans.binding.Bindings;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.collections.transformation.FilteredList;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.chart.*;
//import javafx.scene.control.*;
//import javafx.scene.layout.*;
//import javafx.stage.Stage;
//
//import java.time.LocalDate;
//import java.util.function.Predicate;
//
//public class FundDashboard extends Application {
//
//    private final ObservableList<PettyCashRecordModel> pettyCashData = FXCollections.observableArrayList();
//    private final ObservableList<PromotionFundRecordModel> promotionFundData = FXCollections.observableArrayList();
//    private final ObservableList<PurchaseFundRecordModel> purchaseFundData = FXCollections.observableArrayList();
//
//    @Override
//    public void start(Stage stage) {
//        stage.setTitle("HRMS Funds Dashboard");
//
//        // Load demo data
//        loadDemoData();
//
//        TabPane tabPane = new TabPane();
//        tabPane.getTabs().addAll(
//                createPettyCashTab(),
//                createPromotionFundTab(),
//                createPurchaseFundTab()
//        );
//
//        Scene scene = new Scene(tabPane, 1200, 700);
//        stage.setScene(scene);
//        stage.show();
//    }
//
//    // ======================== PETTY CASH TAB ========================
//    private Tab createPettyCashTab() {
//        Tab tab = new Tab("Petty Cash");
//        tab.setClosable(false);
//
//        FilteredList<PettyCashRecordModel> filteredData = new FilteredList<>(pettyCashData, p -> true);
//
//        TableView<PettyCashRecordModel> table = new TableView<>(filteredData);
//        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        table.getColumns().addAll(
//                createColumn("Request ID", PettyCashRecordModel::getRequestId),
//                createColumn("Unit", PettyCashRecordModel::getRequisitionUnit),
//                createColumn("Payee", PettyCashRecordModel::getPayee),
//                createColumn("Amount", PettyCashRecordModel::getAmountRequested),
//                createColumn("Request Date", PettyCashRecordModel::getRequestDate),
//                createColumn("Confirmation", PettyCashRecordModel::getConfirmationStatus),
//                createColumn("Approval", PettyCashRecordModel::getApprovalStatus),
//                createColumn("Void", PettyCashRecordModel::getVoidStatus),
//                createColumn("Dispensed", PettyCashRecordModel::getDispensedStatus)
//        );
//
//        // Filters
//        TextField payeeFilter = new TextField();
//        payeeFilter.setPromptText("Filter by Payee");
//        ComboBox<String> statusFilter = new ComboBox<>(FXCollections.observableArrayList("All", "Confirmed", "Pending", "Void", "Dispensed"));
//        statusFilter.getSelectionModel().selectFirst();
//        DatePicker startDate = new DatePicker();
//        DatePicker endDate = new DatePicker();
//
//        // Bind filtering
//        payeeFilter.textProperty().addListener((obs, oldVal, newVal) -> 
//            filteredData.setPredicate(createPettyCashPredicate(payeeFilter.getText(), statusFilter.getValue(), startDate.getValue(), endDate.getValue()))
//        );
//        statusFilter.valueProperty().addListener((obs, oldVal, newVal) -> 
//            filteredData.setPredicate(createPettyCashPredicate(payeeFilter.getText(), statusFilter.getValue(), startDate.getValue(), endDate.getValue()))
//        );
//        startDate.valueProperty().addListener((obs, oldVal, newVal) -> 
//            filteredData.setPredicate(createPettyCashPredicate(payeeFilter.getText(), statusFilter.getValue(), startDate.getValue(), endDate.getValue()))
//        );
//        endDate.valueProperty().addListener((obs, oldVal, newVal) -> 
//            filteredData.setPredicate(createPettyCashPredicate(payeeFilter.getText(), statusFilter.getValue(), startDate.getValue(), endDate.getValue()))
//        );
//
//        HBox filterBox = new HBox(10, new Label("Payee:"), payeeFilter, new Label("Status:"), statusFilter,
//                                  new Label("From:"), startDate, new Label("To:"), endDate);
//        filterBox.setPadding(new Insets(10));
//
//        // Pie Chart
//        PieChart pieChart = new PieChart();
//        updatePettyCashPieChart(pieChart, filteredData);
//
//        filteredData.addListener((obs) -> updatePettyCashPieChart(pieChart, filteredData));
//
//        VBox vbox = new VBox(10, filterBox, table, pieChart);
//        vbox.setPadding(new Insets(10));
//        tab.setContent(new ScrollPane(vbox));
//        return tab;
//    }
//
//    private Predicate<PettyCashRecordModel> createPettyCashPredicate(String payee, String status, LocalDate start, LocalDate end) {
//        return record -> {
//            boolean matchesPayee = payee == null || payee.isEmpty() || record.getPayee().toLowerCase().contains(payee.toLowerCase());
//            boolean matchesStatus = status.equals("All") || 
//                    record.getConfirmationStatus().equalsIgnoreCase(status) ||
//                    record.getVoidStatus().equalsIgnoreCase(status) ||
//                    record.getDispensedStatus().equalsIgnoreCase(status);
//            boolean matchesDate = (start == null || !record.getRequestDate().isBefore(start)) &&
//                                  (end == null || !record.getRequestDate().isAfter(end));
//            return matchesPayee && matchesStatus && matchesDate;
//        };
//    }
//
//    private void updatePettyCashPieChart(PieChart chart, ObservableList<PettyCashRecordModel> filteredData) {
//        long confirmed = filteredData.stream().filter(r -> r.getConfirmationStatus().equalsIgnoreCase("Confirmed")).count();
//        long dispensed = filteredData.stream().filter(r -> r.getDispensedStatus().equalsIgnoreCase("Yes")).count();
//        long voided = filteredData.stream().filter(r -> r.getVoidStatus().equalsIgnoreCase("Yes")).count();
//        chart.setData(FXCollections.observableArrayList(
//                new PieChart.Data("Confirmed", confirmed),
//                new PieChart.Data("Dispensed", dispensed),
//                new PieChart.Data("Voided", voided)
//        ));
//    }
//
//    // ======================== PROMOTION FUND TAB ========================
//    private Tab createPromotionFundTab() {
//        Tab tab = new Tab("Promotion Fund");
//        tab.setClosable(false);
//
//        TableView<PromotionFundRecordModel> table = new TableView<>(promotionFundData);
//        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        table.getColumns().addAll(
//                createColumn("Request ID", PromotionFundRecordModel::getRequestId),
//                createColumn("Unit", PromotionFundRecordModel::getRequisitionUnit),
//                createColumn("Payee", PromotionFundRecordModel::getPayee),
//                createColumn("Amount", PromotionFundRecordModel::getAmountRequested),
//                createColumn("Request Date", PromotionFundRecordModel::getRequestDate),
//                createColumn("Confirmation", PromotionFundRecordModel::getConfirmationStatus),
//                createColumn("Approval", PromotionFundRecordModel::getApprovalStatus),
//                createColumn("Void", PromotionFundRecordModel::getVoidStatus),
//                createColumn("Dispensed", PromotionFundRecordModel::getDispensedStatus)
//        );
//
//        VBox vbox = new VBox(10, table);
//        vbox.setPadding(new Insets(10));
//        tab.setContent(vbox);
//        return tab;
//    }
//
//    // ======================== PURCHASE FUND TAB ========================
//    private Tab createPurchaseFundTab() {
//        Tab tab = new Tab("Purchase Fund");
//        tab.setClosable(false);
//
//        TableView<PurchaseFundRecordModel> table = new TableView<>(purchaseFundData);
//        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        table.getColumns().addAll(
//                createColumn("Request ID", PurchaseFundRecordModel::getRequestId),
//                createColumn("Unit", PurchaseFundRecordModel::getRequisitionUnit),
//                createColumn("Payee", PurchaseFundRecordModel::getPayee),
//                createColumn("Amount", PurchaseFundRecordModel::getAmountRequested),
//                createColumn("Request Date", PurchaseFundRecordModel::getRequestDate),
//                createColumn("Confirmation", PurchaseFundRecordModel::getConfirmationStatus),
//                createColumn("Approval", PurchaseFundRecordModel::getApprovalStatus),
//                createColumn("Void", PurchaseFundRecordModel::getVoidStatus),
//                createColumn("Dispensed", PurchaseFundRecordModel::getDispensedStatus)
//        );
//
//        VBox vbox = new VBox(10, table);
//        vbox.setPadding(new Insets(10));
//        tab.setContent(vbox);
//        return tab;
//    }
//
//    // ======================== UTILS ========================
//    private <T> TableColumn<T, Object> createColumn(String title, javafx.util.Callback<T, Object> mapper) {
//        TableColumn<T, Object> col = new TableColumn<>(title);
//        col.setCellValueFactory(c -> new SimpleObjectProperty<>(mapper.call(c.getValue())));
//        return col;
//    }
//
//    private void loadDemoData() {
//        pettyCashData.addAll(
//                new PettyCashRecordModel("PC001", "Finance", "Stationery", "Alice", 100, LocalDate.now().minusDays(2), "Confirmed", "Admin", "Approved", "Manager"),
//                new PettyCashRecordModel("PC002", "HR", "Travel", "Bob", 150, LocalDate.now().minusDays(5), "Pending", "", "Pending", ""),
//                new PettyCashRecordModel("PC003", "IT", "Equipment", "Charlie", 200, LocalDate.now().minusDays(7), "Confirmed", "Admin", "Approved", "Manager", "No", "", "Yes", "Admin")
//        );
//
//        promotionFundData.addAll(
//                new PromotionFundRecordModel("PF001","HR","Bonus","Alice",2000,LocalDate.now().minusDays(10),"Confirmed","Admin","Approved","Manager"),
//                new PromotionFundRecordModel("PF002","Finance","Incentive","Bob",1500,LocalDate.now().minusDays(15),"Pending","","Pending","")
//        );
//
//        purchaseFundData.addAll(
//                new PurchaseFundRecordModel("PU001","IT","Laptop","Charlie",1200,LocalDate.now().minusDays(20),"Confirmed","Admin","Approved","Manager"),
//                new PurchaseFundRecordModel("PU002","Finance","Printer","David",800,LocalDate.now().minusDays(5),"Pending","","Pending","")
//        );
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
