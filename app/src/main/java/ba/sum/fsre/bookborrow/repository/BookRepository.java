package ba.sum.fsre.bookborrow.repository;

import android.content.Context;

import java.util.List;
import java.util.Map;

import ba.sum.fsre.bookborrow.models.RequestBook;
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

    public Call<List<RequestBook>> getMyReceivedRequest(String userId, String select) {
        return api.getAllReceivedRequests(bearer(),"eq." + userId,select,"eq.pending");
    }

    public Call<List<RequestBook>> getMySentRequests(String userId, String select) {
        return api.getAllSentRequests(bearer(),"eq." + userId,select);
    }

    public Call<Void> updateBookRequestStatus(String requestId, Map<String, String> body) {
        return api.updateRequestStatus(bearer(),"eq." + requestId,body);
    }

}
