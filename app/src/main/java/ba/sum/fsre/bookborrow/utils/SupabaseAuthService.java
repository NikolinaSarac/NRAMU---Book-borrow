package ba.sum.fsre.bookborrow.utils;

import com.google.gson.JsonObject;

import ba.sum.fsre.bookborrow.models.requests.RequestForBookModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
public interface SupabaseAuthService {
    @Headers({
            "Content-Type: application/json",
            "apikey: sb_publishable_QayxGZsh6CBuXJ1DFXsTXA_OzEMBI0e"
    })
    @POST("auth/v1/token?grant_type=password")
    public Call<JsonObject> login(@Body JsonObject body);


    @Headers({
            "Content-Type: application/json",
            "apikey: sb_publishable_QayxGZsh6CBuXJ1DFXsTXA_OzEMBI0e"
    })
    @POST("auth/v1/signup")
    public Call<JsonObject> register(@Body JsonObject body);

    @POST("rest/v1/borrowing_requests")
    Call<Void> sendRequestForBook(
            @Header("Authorization") String token,
            @Body RequestForBookModel request
    );

}
