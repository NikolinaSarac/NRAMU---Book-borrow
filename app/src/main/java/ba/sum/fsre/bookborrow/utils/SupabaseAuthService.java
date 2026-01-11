package ba.sum.fsre.bookborrow.utils;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
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

    @Headers({
            "Content-Type: application/json",
            "apikey: sb_publishable_QayxGZsh6CBuXJ1DFXsTXA_OzEMBI0e"
    })
    @POST("rest/v1/rpc/get_user_profile")
    Call<JsonObject> getUserProfile(
            @retrofit2.http.Header("Authorization") String bearer,
            @Body JsonObject body
    );


}
