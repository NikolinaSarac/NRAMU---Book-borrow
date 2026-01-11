package ba.sum.fsre.bookborrow.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientService {

    private static RetrofitClientService instance;
    private SupabaseAuthService api;

    private RetrofitClientService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(
                        chain.request().newBuilder()
                                .addHeader("apikey", SupabaseInstance.getAnonKey())
                                .addHeader("Content-Type", "application/json")
                                .build()
                ))
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SupabaseInstance.getSupabaseUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(SupabaseAuthService.class);
    }

    public static RetrofitClientService getInstance() {
        if (instance == null) {
            instance = new RetrofitClientService();
        }
        return instance;
    }

    public SupabaseAuthService getApi() {
        return api;
    }
}
