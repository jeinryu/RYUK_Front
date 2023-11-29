package org.techtown.ryuk.interfaces


import org.techtown.ryuk.models.LoginRequest
import org.techtown.ryuk.models.LoginResponse
import org.techtown.ryuk.models.TeamCheckResponse
import org.techtown.ryuk.models.TeamMembersResponse
import org.techtown.ryuk.models.TeamWithdrawResponse
import org.techtown.ryuk.models.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface UserApiService {
    // 로그인
    @GET("/user/login")
    fun loginUser(
        @Query("loginId") loginId: String,
        @Query("password") password: String
    ): Call<LoginResponse>

    // 유저 정보 얻기
    @GET("/user/getInfo")
    fun getUserInfo(
        @Query("userId") userId: Int
    ): Call<UserResponse>

    @GET("/user/ifHasTeam")
    fun checkUserTeam(
        @Query("userId") userId: Int
    ): Call<TeamCheckResponse>

    @GET("/user/get")
    fun getTeamMembers(
        @Query("teamId") teamId: Int
    ): Call<TeamMembersResponse>

    @GET("/user/withdrawTeam")
    fun withdrawTeam(
        @Query("userId") userId: Int,
        @Query("teamId") teamId: Int
    ): Call<TeamWithdrawResponse>
}
