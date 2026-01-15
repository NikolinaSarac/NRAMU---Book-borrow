package ba.sum.fsre.bookborrow.models;

import com.google.gson.annotations.SerializedName;

public class Books {

    private String id;
    private String name;
    private String author;
    private String description;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("available")
    private boolean available;

    public Books(
            String id,
            String name,
            String author,
            String description,
            String imageUrl,
            String userId,
            boolean available
    ) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.description = description;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.available = available;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getUserId() { return userId; }
    public boolean isAvailable() { return available; }
    public String getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }
    public void setAuthor(String author) { this.author = author; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
