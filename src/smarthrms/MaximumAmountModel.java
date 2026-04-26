package smarthrms;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MaximumAmountModel {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty fundType = new SimpleStringProperty();
    private StringProperty maximumAmount = new SimpleStringProperty();

    public MaximumAmountModel(int id, String fundType, String maxAmount) {
        this.id.set(id);
        this.fundType.set(fundType);
        this.maximumAmount.set(maxAmount);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getFundType() { return fundType.get(); }
    public StringProperty fundTypeProperty() { return fundType; }

    public String getMaximumAmount() { return maximumAmount.get(); }
    public StringProperty maximumAmountProperty() { return maximumAmount; }
}
