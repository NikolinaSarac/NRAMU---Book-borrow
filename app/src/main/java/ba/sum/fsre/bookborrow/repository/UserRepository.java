package ba.sum.fsre.bookborrow.repository;

import android.content.Context;

import java.util.List;
import java.util.Map;

import ba.sum.fsre.bookborrow.models.Profile;
import ba.sum.fsre.bookborrow.models.RequestBook;
import ba.sum.fsre.bookborrow.models.requests.RequestForBookModel;
import ba.sum.fsre.bookborrow.utils.AuthManager;
import ba.sum.fsre.bookborrow.utils.RetrofitClientService;
import ba.sum.fsre.bookborrow.utils.SupabaseAuthService;
import retrofit2.Call;

public class UserRepository {
    private final SupabaseAuthService api = RetrofitClientService.getInstance().getApi();

    private final AuthManager auth;

    public UserRepository(Context ctx) { auth = new AuthManager(ctx);
    }

    private String bearer() { return "Bearer " + auth.getToken(); }

    public  Call<List<Profile>> getUserProfile(String userId) {
        return api.getProfile(bearer(),"eq." + userId);
    }
    public String getCurrentUserId() {
        return auth.getUserId();
    }

}
