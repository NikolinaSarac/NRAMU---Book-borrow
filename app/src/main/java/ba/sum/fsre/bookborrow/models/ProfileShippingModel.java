package ba.sum.fsre.bookborrow.models;

public class ProfileShippingModel {

    private String first_name;
    private String last_name;
    private String address;
    private String postal_code;
    private String city;
    private String shipping_note;

    public ProfileShippingModel(String first_name, String last_name, String address, String postal_code, String city, String shipping_note) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.address = address;
        this.postal_code = postal_code;
        this.city = city;
        this.shipping_note = shipping_note;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postal_code;
    }

    public String getCity() {
        return city;
    }

    public String getShippingNote() {
        return shipping_note;
    }
}
