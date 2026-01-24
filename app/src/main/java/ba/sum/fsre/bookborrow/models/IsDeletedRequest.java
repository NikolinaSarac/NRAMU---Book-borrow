package ba.sum.fsre.bookborrow.models;

public class IsDeletedRequest {
    public boolean is_deleted;

    public IsDeletedRequest(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public boolean isDeleted() {
        return is_deleted;
    }
}
