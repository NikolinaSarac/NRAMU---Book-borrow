package ba.sum.fsre.bookborrow.models;

import com.google.gson.annotations.SerializedName;

public class Books {

    private String id;
    private String name;
    private String author;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("available")
    private boolean available;

    public Books(String id, String name, String author, String userId, boolean available) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.user_id = userId;
        this.available = available;
    }


    public String getId() { return id; }
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public String getUserId() { return user_id; }
    public boolean isAvailable() { return available; }
    public String getCreatedAt() { return created_at; }


    public void setName(String name) { this.name = name; }
    public void setAuthor(String author) { this.author = author; }
    public void setUserId(String userId) { this.user_id = user_id; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setCreatedAt(String createdAt) { this.created_at = created_at; }
}
