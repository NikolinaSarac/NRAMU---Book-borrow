package ba.sum.fsre.bookborrow.models.requests;

import com.google.gson.annotations.SerializedName;

public class RequestForBookModel {

    @SerializedName("book_id")
    private String bookId;

    @SerializedName("owner_id")
    private String ownerId;

    @SerializedName("requester_id")
    private String requesterId;

    @SerializedName("pickup_date")
    private String pickupDate;

    @SerializedName("pickup_time")
    private String pickupTime;

    @SerializedName("location")
    private String location;

    @SerializedName("description")
    private String description;


    public RequestForBookModel(String bookId, String ownerId, String requesterId) {
        this.bookId = bookId;
        this.ownerId = ownerId;
        this.requesterId = requesterId;
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

    public String getPickupDate() { return pickupDate; }
    public void setPickupDate(String pickupDate) { this.pickupDate = pickupDate; }

    public String getPickupTime() { return pickupTime; }
    public void setPickupTime(String pickupTime) { this.pickupTime = pickupTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
