package ba.sum.fsre.bookborrow.repository;

import android.content.Context;

import java.util.List;
import java.util.Map;

import ba.sum.fsre.bookborrow.models.Profile;
import ba.sum.fsre.bookborrow.models.ProfileShippingModel;
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

    public  Call<List<ProfileShippingModel>> getUserProfileShipping(String userId) {
        return api.getProfileShippingModel(bearer(),"eq." + userId);
    }

    public  Call<Void> updateUserProfileShipping(String userId,String first_name,String last_name,String address,String postal_code,String city,String shipping_note) {
        return api.updateShippingInfo(bearer(),
                "eq." + userId,
                    new ProfileShippingModel(
                            first_name,
                            last_name,
                            address,
                            postal_code,
                            city,
                            shipping_note)
                );
    }

    public String getCurrentUserId() {
        return auth.getUserId();
    }

}
