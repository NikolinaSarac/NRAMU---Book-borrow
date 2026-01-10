package ba.sum.fsre.bookborrow.repository;

import android.content.Context;

import ba.sum.fsre.bookborrow.models.requests.RequestForBookModel;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import ba.sum.fsre.bookborrow.utils.RetrofitClientService;
import ba.sum.fsre.bookborrow.utils.SupabaseAuthService;
import retrofit2.Call;

public class BookRepository  {

    private final SupabaseAuthService api = RetrofitClientService.getInstance().getApi();

    private final AuthManager auth;

    public BookRepository(Context ctx) { auth = new AuthManager(ctx);
    }

    private String bearer() { return "Bearer " + auth.getToken(); }

    public Call<Void> sendBookRequest(String bookId, String ownerId, String requesterId) {
        return api.sendRequestForBook(bearer(),new RequestForBookModel(bookId,ownerId,requesterId));
    }
}
