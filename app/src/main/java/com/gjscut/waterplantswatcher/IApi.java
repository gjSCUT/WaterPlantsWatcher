package com.gjscut.waterplantswatcher;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Administrator on 2017/4/18.
 */
public interface IApi {
    @POST("oauth/token")
    @FormUrlEncoded
    Observable<Constant.Token> accessTokenByPassword(@Field("grant_type") String grantType,
                                                     @Field("client_id") String clientId, @Field("client_secret") String clientSecret,
                                                     @Field("username") String username, @Field("password") String password);

    @POST("oauth/token")
    @FormUrlEncoded
    Call<Constant.Token> accessTokenByRefresh(@Field("grant_type") String grantType, @Field("refresh_token") String refreshToken,
                                              @Field("username") String username, @Field("password") String password);

    @POST("login")
    @FormUrlEncoded
    Observable<String> login(@Field("username") String username, @Field("password") String password);

}
