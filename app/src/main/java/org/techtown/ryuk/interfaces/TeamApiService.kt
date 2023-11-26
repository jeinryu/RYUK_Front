package org.techtown.ryuk.interfaces

import org.techtown.ryuk.models.JsonGetTeams
import org.techtown.ryuk.models.JsonTeamDetailResponse
import org.techtown.ryuk.models.JsonTeamJoinResponse
import org.techtown.ryuk.models.JsonTeamMemberCountResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TeamApiService {
    // 팀 리스트 불러오기
    @GET("/team/all")
    fun getAllTeams(): Call<JsonGetTeams>

    // 유저가 팀 참여 요청
    @GET("/user/requestTeam")
    fun requestTeamJoin(
        @Query("userId") uid: Int,
        @Query("teamId") teamId: Int
    ): Call<JsonTeamJoinResponse>

    @GET("/team/getNum")
    fun getTeamMemberCount(): Call<JsonTeamMemberCountResponse>

    @GET("/team/get")
    fun getTeamDetails(
        @Query("teamId") teamId: Int
    ): Call<JsonTeamDetailResponse>
}
