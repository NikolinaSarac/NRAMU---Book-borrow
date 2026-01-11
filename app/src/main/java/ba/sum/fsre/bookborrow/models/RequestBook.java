package ba.sum.fsre.bookborrow.models;

import com.google.gson.annotations.SerializedName;

public class RequestBook {
    private String id;

    @SerializedName("book_id")
    private String bookId;

    @SerializedName("requester_id")
    private String requesterId;

    @SerializedName("owner_id")
    private String ownerId;
    private String status;
    private Book book;

    public static class Book {
        private String name;
        private String author;

        public String getName() { return name; }
        public String getAuthor() { return author; }
    }

    public Book getBook() { return book; }

    public String getId() {
        return id;
    }

    public String getBookId() {
        return bookId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
