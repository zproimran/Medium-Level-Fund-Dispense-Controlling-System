package smarthrms;

import javafx.beans.property.*;

public class VatPurchaseModel {

    private final StringProperty vatCategory = new SimpleStringProperty();
    private final StringProperty calendarType = new SimpleStringProperty();
    private final IntegerProperty purchaseType = new SimpleIntegerProperty();
    private final StringProperty sellerTin = new SimpleStringProperty();
    private final StringProperty sellerName = new SimpleStringProperty();
    private final StringProperty dateOfPurchase = new SimpleStringProperty();
    private final StringProperty mrcNumber = new SimpleStringProperty();
    private final StringProperty receiptNumber = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty unitMeasure = new SimpleIntegerProperty();
    private final DoubleProperty quantity = new SimpleDoubleProperty();
    private final DoubleProperty unitPrice = new SimpleDoubleProperty();
    private final DoubleProperty totalValue = new SimpleDoubleProperty();
    private final DoubleProperty vatAmount = new SimpleDoubleProperty();
    private final DoubleProperty totalAfterVat = new SimpleDoubleProperty();
    private final BooleanProperty voided = new SimpleBooleanProperty();
    private final StringProperty voidedAt = new SimpleStringProperty();
    private final StringProperty voidedBy = new SimpleStringProperty();
    private final StringProperty voidReason = new SimpleStringProperty();
    private final StringProperty createdBy = new SimpleStringProperty(); // NEW FIELD

    // Main constructor with ALL fields including createdBy
    public VatPurchaseModel(String vatCategory, String calendarType, int purchaseType,
                            String sellerTin, String sellerName, String dateOfPurchase,
                            String mrcNumber, String receiptNumber, String description,
                            int unitMeasure, double quantity, double unitPrice,
                            double totalValue, double vatAmount, double totalAfterVat,
                            boolean voided, String voidedAt, String voidedBy, String voidReason,
                            String createdBy) {
        this.vatCategory.set(vatCategory);
        this.calendarType.set(calendarType);
        this.purchaseType.set(purchaseType);
        this.sellerTin.set(sellerTin);
        this.sellerName.set(sellerName);
        this.dateOfPurchase.set(dateOfPurchase);
        this.mrcNumber.set(mrcNumber);
        this.receiptNumber.set(receiptNumber);
        this.description.set(description);
        this.unitMeasure.set(unitMeasure);
        this.quantity.set(quantity);
        this.unitPrice.set(unitPrice);
        this.totalValue.set(totalValue);
        this.vatAmount.set(vatAmount);
        this.totalAfterVat.set(totalAfterVat);
        this.voided.set(voided);
        this.voidedAt.set(voidedAt);
        this.voidedBy.set(voidedBy);
        this.voidReason.set(voidReason);
        this.createdBy.set(createdBy); // NEW
    }
    
    // Constructor for existing records (from database)
    public VatPurchaseModel(String vatCategory, String calendarType, int purchaseType,
                            String sellerTin, String sellerName, String dateOfPurchase,
                            String mrcNumber, String receiptNumber, String description,
                            int unitMeasure, double quantity, double unitPrice,
                            double totalValue, double vatAmount, double totalAfterVat,
                            boolean voided, String voidedAt, String voidedBy, String voidReason) {
        this(vatCategory, calendarType, purchaseType, sellerTin, sellerName, dateOfPurchase,
             mrcNumber, receiptNumber, description, unitMeasure, quantity, unitPrice,
             totalValue, vatAmount, totalAfterVat, voided, voidedAt, voidedBy, voidReason, "");
    }
    
    // Default constructor for new records (from UI form)
    public VatPurchaseModel(String vatCategory, String calendarType, int purchaseType,
                            String sellerTin, String sellerName, String dateOfPurchase,
                            String mrcNumber, String receiptNumber, String description,
                            int unitMeasure, double quantity, double unitPrice,
                            double totalValue, double vatAmount, double totalAfterVat) {
        this(vatCategory, calendarType, purchaseType, sellerTin, sellerName, dateOfPurchase,
             mrcNumber, receiptNumber, description, unitMeasure, quantity, unitPrice,
             totalValue, vatAmount, totalAfterVat, false, null, null, null, "");
    }

    // ---------------- Properties ----------------
    public StringProperty vatCategoryProperty() { return vatCategory; }
    public StringProperty calendarTypeProperty() { return calendarType; }
    public IntegerProperty purchaseTypeProperty() { return purchaseType; }
    public StringProperty sellerTinProperty() { return sellerTin; }
    public StringProperty sellerNameProperty() { return sellerName; }
    public StringProperty dateOfPurchaseProperty() { return dateOfPurchase; }
    public StringProperty receiptNumberProperty() { return receiptNumber; }
    public DoubleProperty totalAfterVatProperty() { return totalAfterVat; }
    public StringProperty createdByProperty() { return createdBy; } // NEW
    
    // ----------------- VOIDING GETTERS -----------------
    public boolean isVoided() { return voided.get(); }
    public String getVoidedAt() { return voidedAt.get(); }
    public String getVoidedBy() { return voidedBy.get(); }
    public String getVoidReason() { return voidReason.get(); }
    public String getCreatedBy() { return createdBy.get(); } // NEW

    public BooleanProperty voidedProperty() { return voided; }
    public StringProperty voidedAtProperty() { return voidedAt; }
    public StringProperty voidedByProperty() { return voidedBy; }
    public StringProperty voidReasonProperty() { return voidReason; }

    // ----------------- VOIDING SETTERS -----------------
    public void setVoided(boolean value) { voided.set(value); }
    public void setVoidedAt(String value) { voidedAt.set(value); }
    public void setVoidedBy(String value) { voidedBy.set(value); }
    public void setVoidReason(String value) { voidReason.set(value); }
    public void setCreatedBy(String value) { createdBy.set(value); } // NEW

    // ---------------- Getters ----------------
    public String getVatCategory() { return vatCategory.get(); }
    public String getCalendarType() { return calendarType.get(); }
    public int getPurchaseType() { return purchaseType.get(); }
    public String getSellerTin() { return sellerTin.get(); }
    public String getSellerName() { return sellerName.get(); }
    public String getDateOfPurchase() { return dateOfPurchase.get(); }
    public String getMrcNumber() { return mrcNumber.get(); }
    public String getReceiptNumber() { return receiptNumber.get(); }
    public String getDescription() { return description.get(); }
    public int getUnitMeasure() { return unitMeasure.get(); }
    public double getQuantity() { return quantity.get(); }
    public double getUnitPrice() { return unitPrice.get(); }
    public double getTotalValue() { return totalValue.get(); }
    public double getVatAmount() { return vatAmount.get(); }
    public double getTotalAfterVat() { return totalAfterVat.get(); }

    // ---------------- Setters ----------------
    public void setVatCategory(String value) { vatCategory.set(value); }
    public void setCalendarType(String value) { calendarType.set(value); }
    public void setPurchaseType(int value) { purchaseType.set(value); }
    public void setSellerTin(String value) { sellerTin.set(value); }
    public void setSellerName(String value) { sellerName.set(value); }
    public void setDateOfPurchase(String value) { dateOfPurchase.set(value); }
    public void setMrcNumber(String value) { mrcNumber.set(value); }
    public void setReceiptNumber(String value) { receiptNumber.set(value); }
    public void setDescription(String value) { description.set(value); }
    public void setUnitMeasure(int value) { unitMeasure.set(value); }
    public void setQuantity(double value) { quantity.set(value); }
    public void setUnitPrice(double value) { unitPrice.set(value); }
    public void setTotalValue(double value) { totalValue.set(value); }
    public void setVatAmount(double value) { vatAmount.set(value); }
    public void setTotalAfterVat(double value) { totalAfterVat.set(value); }
}