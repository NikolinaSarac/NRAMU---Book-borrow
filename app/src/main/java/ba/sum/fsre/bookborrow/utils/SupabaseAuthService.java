package ba.sum.fsre.bookborrow.utils;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import ba.sum.fsre.bookborrow.models.Profile;
import ba.sum.fsre.bookborrow.models.RequestBook;
import ba.sum.fsre.bookborrow.models.requests.RequestForBookModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

    @GET("rest/v1/borrowing_requests")
    Call<List<RequestBook>> getAllReceivedRequests(
            @Header("Authorization") String token,
            @Query("owner_id") String userId,
            @Query("select") String select,
            @Query("status") String status
    );

    @GET("rest/v1/borrowing_requests")
    Call<List<RequestBook>> getAllSentRequests(
            @Header("Authorization") String token,
            @Query("requester_id") String userId,
            @Query("select") String select
    );

    @PATCH("rest/v1/borrowing_requests")
    Call<Void> updateRequestStatus(
            @Header("Authorization") String token,
            @Query(value="id", encoded=true) String idFilter,
            @Body Map<String, String> body
    );

    @Headers({
            "Content-Type: application/json",
            "apikey: sb_publishable_QayxGZsh6CBuXJ1DFXsTXA_OzEMBI0e"
    })
    @POST("rest/v1/rpc/get_user_profile")
    Call<JsonObject> getUserProfile(
            @retrofit2.http.Header("Authorization") String bearer,
            @Body JsonObject body
    );

    @GET("rest/v1/profiles")
    Call<List<Profile>> getProfile(
            @Header("Authorization") String token,
            @Query("id") String userId
    );

    @GET("rest/v1/books")
    Call<List<JsonObject>> getAllBooks(
            @Header("Authorization") String token,
            @Query("select") String select,
            @Query("user_id") String userFilter
    );

    @POST("rest/v1/books")
    Call<Void> createBook(
            @Header("Authorization") String token,
            @Body JsonObject body
    );

    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    @POST("rest/v1/books")
    Call<List<JsonObject>> insertBook(
            @Header("Authorization") String token,
            @Body JsonObject body
    );

    @GET("rest/v1/books")
    Call<List<JsonObject>> getMyBooks(
            @Header("Authorization") String auth,
            @Query("select") String select,
            @Query("user_id") String userFilter
    );



}
