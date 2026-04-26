package smarthrms;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class EmployeeEmergencyContactViewModel {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty empcode;
    private final SimpleStringProperty fullname;
    private final SimpleStringProperty contactname;
    private final SimpleStringProperty relationship;
    private final SimpleStringProperty occupation;
    private final SimpleStringProperty workplace;
    private final SimpleStringProperty city;
    private final SimpleStringProperty subcity;
    private final SimpleStringProperty woreda;
    private final SimpleStringProperty homephone;
    private final SimpleStringProperty cellphone;

    public EmployeeEmergencyContactViewModel(int id, String empcode, String fullname,
                                             String contactname, String relationship,
                                             String occupation, String workplace,
                                             String city, String subcity, String woreda,
                                             String homephone,String cellphone) {
        this.id = new SimpleIntegerProperty(id);
        this.empcode = new SimpleStringProperty(empcode);
        this.fullname = new SimpleStringProperty(fullname);
        this.contactname = new SimpleStringProperty(contactname);
        this.relationship = new SimpleStringProperty(relationship);
        this.occupation = new SimpleStringProperty(occupation);
        this.workplace = new SimpleStringProperty(workplace);
        this.city = new SimpleStringProperty(city);
        this.subcity = new SimpleStringProperty(subcity);
        this.woreda = new SimpleStringProperty(woreda);
        this.homephone = new SimpleStringProperty(homephone);
        this.cellphone = new SimpleStringProperty(cellphone);
    }

    // ✅ Getters & Property methods

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }

    public String getEmpcode() { return empcode.get(); }
    public SimpleStringProperty empcodeProperty() { return empcode; }

    public String getFullname() { return fullname.get(); }
    public SimpleStringProperty fullnameProperty() { return fullname; }

    public String getContactname() { return contactname.get(); }
    public SimpleStringProperty contactnameProperty() { return contactname; }

    public String getRelationship() { return relationship.get(); }
    public SimpleStringProperty relationshipProperty() { return relationship; }

    public String getOccupation() { return occupation.get(); }
    public SimpleStringProperty occupationProperty() { return occupation; }

    public String getWorkplace() { return workplace.get(); }
    public SimpleStringProperty workplaceProperty() { return workplace; }
  
    public String getCity() { return city.get(); }
    public SimpleStringProperty cityProperty() { return city; }

    public String getSubcity() { return subcity.get(); }
    public SimpleStringProperty subcityProperty() { return subcity; }

    public String getWoreda() { return woreda.get(); }
    public SimpleStringProperty woredaProperty() { return woreda; }

    public String getHomephone() { return homephone.get(); }
    public SimpleStringProperty homephoneProperty() { return homephone; }

    public String getCellphone() { return cellphone.get(); }
    public SimpleStringProperty cellphoneProperty() { return cellphone; }
}
