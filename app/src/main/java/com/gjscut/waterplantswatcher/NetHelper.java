package com.gjscut.waterplantswatcher;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
    public static NetHelper mInstance;
    public static IApi mApi;
    public Retrofit mRetrofit;
    public static final String BASE_URL = "http://121.196.207.208:3000/";
    private NetHelper(){
        Interceptor mTokenInterceptor = new Interceptor() {
            @Override public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                if (Constant.token == null || originalRequest.header("Authorization") != null) {
                    return chain.proceed(originalRequest);
                }
                String sToken = Constant.token.token_type + " " + Constant.token.access_token;
                Request authorised = originalRequest.newBuilder()
                        .header("Authorization", sToken)
                        .build();
                return chain.proceed(authorised);
            }
        };
        Authenticator mAuthenticator = new Authenticator() {
            @Override public Request authenticate(Route route, Response response)
                    throws IOException {
                if (Constant.token == null) {
                    return response.request().newBuilder()
                            .build();
                }
                Constant.token = api().accessTokenByRefresh("refresh_token", Constant.token.refresh_token, Constant.clientId, Constant.clientSecret)
                        .execute()
                        .body();
                String sToken = Constant.token.token_type + " " + Constant.token.access_token;
                return response.request().newBuilder()
                        .addHeader("Authorization", sToken)
                        .build();
            }
        };
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
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

    public static IApi api() {
        if(mApi == null){
            synchronized (NetHelper.class){
                if(mApi == null)
                    mApi = getInstance().mRetrofit.create(IApi.class);
            }
        }
        return mApi;
    }
}
