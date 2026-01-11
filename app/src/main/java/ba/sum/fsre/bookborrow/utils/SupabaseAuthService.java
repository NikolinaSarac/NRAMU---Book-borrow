package ba.sum.fsre.bookborrow.utils;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

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

    @GET("rest/v1/book_rent_requests")
    Call<List<RequestBook>> getAllSentRequests(
            @Header("Authorization") String token,
            @Query("requester_id") String userId,
            @Query("select") String select
    );

    @PATCH("rest/v1/borrowing_requests")
    Call<Void> updateRequestStatus(
            @Header("Authorization") String token,
            @Query("id") String id,
            @Body Map<String, String> body
    );
}
