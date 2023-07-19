package com.example.supertestapplication.repository.network

import com.example.supertestapplication.repository.network.responses.AccessToken
import com.example.supertestapplication.repository.network.responses.SearchUserResponse
import com.example.supertestapplication.repository.network.responses.UserDetailResponse
import retrofit2.Response
import retrofit2.http.*

interface NetworkService {

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("login/oauth/access_token")
    suspend fun getAccessToken(
        @Field("client_id") client_id:String,
        @Field("client_secret") client_secret:String,
        @Field("code") code:String,
    ) : AccessToken

    /**
     * Endpoints Detail User
     */
    @GET("users/{username}")
    suspend fun getDetailUser(
        @Path("username") username: String,
        @Header("Authorization") authHeader :String
    ) : UserDetailResponse
}