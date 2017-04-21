package com.gjscut.waterplantswatcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.gjscut.waterplantswatcher.model.Token;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetHelper {
    private static final Logger logger = Logger.getLogger("NetHelper");
    public static NetHelper mInstance;
    public static IApi mApi;
    public static final String BASE_URL = "http://121.196.207.208:3000/";

    private static Context context;
    private Retrofit mRetrofit;
    private SharedPreferences.Editor editor;

    private NetHelper(){
        Interceptor mTokenInterceptor = new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                String path = originalRequest.url().encodedPath();
                String authHeader = originalRequest.header("Authorization");
                if (Constant.token == null || authHeader != null && !authHeader.equals(" ") || !path.startsWith("/api")) {
                    logger.info(path + " Don't need Authorization" + authHeader);
                    return chain.proceed(originalRequest);
                }
                String sToken = Constant.token.token_type + " " + Constant.token.access_token;
                logger.info("Authorization = " + sToken);
                Request authorised = originalRequest.newBuilder()
                        .header("Authorization", sToken)
                        .build();
                return chain.proceed(authorised);
            }
        };
        Authenticator mAuthenticator = new Authenticator() {
            @Override public Request authenticate(Route route, Response response)
                    throws IOException {
                editor = NetHelper.context.getSharedPreferences("water_plants", Context.MODE_PRIVATE).edit();
                if (Constant.token == null || Constant.token.refresh_token.isEmpty()) {
                    logger.info("No refresh token");
                    Intent intent = new Intent(NetHelper.context, LoginActivity.class);
                    context.startActivity(intent);
                    editor.putString("accessToken", "");
                    editor.putString("tokenType", "");
                    editor.putString("refreshToken", "");
                    editor.putInt("expiresIn", 0);
                    editor.apply();
                    return null;
                }
                try {
                    retrofit2.Response<Token> tokenResponse = api(NetHelper.context)
                            .accessTokenByRefresh("refresh_token", Constant.token.refresh_token, Constant.clientId, Constant.clientSecret)
                            .execute();
                    if (tokenResponse.isSuccessful()) {
                        Constant.token = tokenResponse.body();
                    } else {
                        logger.info("refresh execute failure");
                        Intent intent = new Intent(NetHelper.context, LoginActivity.class);
                        context.startActivity(intent);
                        editor.putString("accessToken", "");
                        editor.putString("tokenType", "");
                        editor.putString("refreshToken", "");
                        editor.putInt("expiresIn", 0);
                        editor.apply();
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("refresh execute error");
                    return null;
                }
                editor.putLong("loginTime", new Date().getTime());
                editor.putString("accessToken", Constant.token.access_token);
                editor.putString("tokenType", Constant.token.token_type);
                editor.putString("refreshToken", Constant.token.refresh_token);
                editor.putInt("expiresIn", Constant.token.expires_in);
                editor.apply();

                String sToken = Constant.token.token_type + " " + Constant.token.access_token;
                logger.info("new Authorization = " + sToken);
                return response.request().newBuilder()
                        .addHeader("Authorization", sToken)
                        .build();
            }
        };
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .authenticator(mAuthenticator)
                .addNetworkInterceptor(mTokenInterceptor)
                .build();
        mRetrofit = new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    public static NetHelper getInstance(){
        if(mInstance==null){
            synchronized (NetHelper.class){
                if(mInstance==null)
                    mInstance = new NetHelper();
            }
        }
        return mInstance;
    }

    public static IApi api(Context context) {
        NetHelper.context = context;
        if(mApi == null){
            synchronized (NetHelper.class){
                if(mApi == null)
                    mApi = getInstance().mRetrofit.create(IApi.class);
            }
        }
        return mApi;
    }
}
