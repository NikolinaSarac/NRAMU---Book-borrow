package ba.sum.fsre.bookborrow.models;

import com.google.gson.annotations.SerializedName;

public class Borrowing_requests {

    private String id;

    @SerializedName("book_id")
    private String bookId;

    @SerializedName("requester_id")
    private String requesterId;

    @SerializedName("owner_id")
    private String ownerId;

    private String status;

    @SerializedName("created_at")
    private String createdAt;

    private Book book;

    public static class Book {
        private String name;
        private String author;

        public String getName() { return name; }
        public String getAuthor() { return author; }
    }

    // Getteri
    public String getId() { return id; }
    public String getBookId() { return bookId; }
    public String getRequesterId() { return requesterId; }
    public String getOwnerId() { return ownerId; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public Book getBook() { return book; }

    // Setteri
    public void setStatus(String status) { this.status = status; }
}
