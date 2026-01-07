package ba.sum.fsre.bookborrow.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SupabaseInstance {

    private static final String SUPABASE_URL = "https://gvqwwllhdvsjtpodlruk.supabase.co";
    private static final String SUPABASE_KEY = "sb_publishable_QayxGZsh6CBuXJ1DFXsTXA_OzEMBI0e";

    private static final OkHttpClient client = new OkHttpClient();

    // Test konekcije
    public static void testConnection() {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/rest/v1/profiles?select=*")
                        .addHeader("apikey", SUPABASE_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String body = response.body().string();
                    Log.d("SupabaseTest", "Connection OK: " + body);
                } else {
                    Log.e("SupabaseTest", "Error: " + response.code() + " " + response.message());
                }

            } catch (IOException e) {
                Log.e("SupabaseTest", "Exception: ", e);
            }
        }).start();
    }
    public static OkHttpClient getClient() {
        return client;
    }

    public static String getAuthLoginUrl() {
        return SUPABASE_URL + "/auth/v1/token?grant_type=password";
    }

    public static String getAnonKey() {
        return SUPABASE_KEY;
    }
    public static String getAuthRegisterUrl() {
        return SUPABASE_URL + "/auth/v1/signup";
    }

}