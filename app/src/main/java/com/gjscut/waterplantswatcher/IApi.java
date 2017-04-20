package com.gjscut.waterplantswatcher;

import com.gjscut.waterplantswatcher.model.ActivatedCarbonPool;
import com.gjscut.waterplantswatcher.model.ChlorineAddPool;
import com.gjscut.waterplantswatcher.model.CoagulatePool;
import com.gjscut.waterplantswatcher.model.CombinedWell;
import com.gjscut.waterplantswatcher.model.DepositPool;
import com.gjscut.waterplantswatcher.model.DistributeWell;
import com.gjscut.waterplantswatcher.model.OzonePoolAdvance;
import com.gjscut.waterplantswatcher.model.OzonePoolMain;
import com.gjscut.waterplantswatcher.model.PumpRoomFirst;
import com.gjscut.waterplantswatcher.model.PumpRoomOut;
import com.gjscut.waterplantswatcher.model.PumpRoomSecond;
import com.gjscut.waterplantswatcher.model.SandLeachPool;
import com.gjscut.waterplantswatcher.model.SuctionWell;
import com.gjscut.waterplantswatcher.model.Token;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

interface IApi {
    @POST("oauth/token")
    @FormUrlEncoded
    Observable<Token> accessTokenByPassword(@Field("grant_type") String grantType,
                                            @Field("client_id") String clientId, @Field("client_secret") String clientSecret,
                                            @Field("username") String username, @Field("password") String password);

    @POST("oauth/token")
    @FormUrlEncoded
    Call<Token> accessTokenByRefresh(@Field("grant_type") String grantType, @Field("refresh_token") String refreshToken,
                                              @Field("client_id") String clientId, @Field("client_secret") String clientSecret);

    @POST("login")
    @FormUrlEncoded
    Observable<String> login(@Field("username") String username, @Field("password") String password);


    @GET("api/pumpRoomFirst")
    Observable<List<PumpRoomFirst>> getPumpRoomFirst(@Query("limit") int limit, @Query("sort") String columnName);

    @GET("api/pumpRoomSecond")
    Observable<List<PumpRoomSecond>> getPumpRoomSecond(@Query("limit") int limit, @Query("sort") String columnName);

    @GET("api/pumpRoomOut")
    Observable<List<PumpRoomOut>> getPumpRoomOut(@Query("limit") int limit, @Query("sort") String columnName);

    @GET("api/ozonePoolMain")
    Observable<List<OzonePoolMain>> getOzonePoolMain(@Query("limit") int limit, @Query("sort") String columnName);

    @GET("api/ozonePoolAdvance")
    Observable<List<OzonePoolAdvance>> getOzonePoolAdvance(@Query("limit") int limit, @Query("sort") String columnName);

    @GET("api/activatedCarbonPool")
    Observable<List<ActivatedCarbonPool>> getActivatedCarbonPool(@Query("limit") int limit, @Query("sort") String columnName);

    @GET("api/chlorineAddPool")
    Observable<List<ChlorineAddPool>> getChlorineAddPool(@Query("limit") int limit, @Query("sort") String columnName);

    @GET("api/coagulatePool")
    Observable<List<CoagulatePool>> getCoagulatePool(@Query("limit") int limit, @Query("sort") String columnName);

    @GET("api/combinedWell")
    Observable<List<CombinedWell>> getCombinedWell(@Query("limit") int limit, @Query("sort") String columnName);

    @GET("api/depositPool")
    Observable<List<DepositPool>> getDepositPool(@Query("limit") int limit, @Query("sort") String columnName);

    @GET("api/distributeWell")
    Observable<List<DistributeWell>> getDistributeWell(@Query("limit") int limit, @Query("sort") String columnName);

    @GET("api/sandLeachPool")
    Observable<List<SandLeachPool>> getSandLeachPool(@Query("limit") int limit, @Query("sort") String columnName);

    @GET("api/suctionWell")
    Observable<List<SuctionWell>> getSuctionWell(@Query("limit") int limit, @Query("sort") String columnName);
}
