package ba.sum.fsre.bookborrow.models.requests;

import com.google.gson.annotations.SerializedName;

public class RequestForBookModel {

    @SerializedName("book_id")
    private String bookId;

    @SerializedName("owner_id")
    private String ownerId;

    @SerializedName("requester_id")
    private String requesterId;
    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("address")
    private String address;

    @SerializedName("postal_code")
    private String postalCode;

    @SerializedName("city")
    private String city;

    @SerializedName("shipping_note")
    private String shippingNote;

    public RequestForBookModel(
            String bookId,
            String ownerId,
            String requesterId,
            String firstName,
            String lastName,
            String address,
            String postalCode,
            String city,
            String shippingNote
    ) {
        this.bookId = bookId;
        this.ownerId = ownerId;
        this.requesterId = requesterId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.postalCode = postalCode;
        this.city = city;
        this.shippingNote = shippingNote;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }
}
