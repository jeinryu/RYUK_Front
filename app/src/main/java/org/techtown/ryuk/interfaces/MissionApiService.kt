package org.techtown.ryuk.interfaces

import org.techtown.ryuk.models.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MissionApiService {
    @GET("/user/todayMission")
    fun getMissions(
        @Query("userId") uid: Int,
        @Query("date") date: String
    ): Call<JsonGetMissions>

    @GET("/user/mission/add")
    fun addMission(
        @Query("title") title: String,
        @Query("missionType") type: String,
        @Query("date") date: String,
        @Query("userId") uid: Int,
    ): Call<JsonAddMission>

    @GET("user/setSuccess")
    fun setSuccess(
        @Query("userMissionId") userMissionId: Int
    ): Call<JsonBoolean>

    @GET("user/mission/delete")
    fun deleteMission(
        @Query("user_mission_id") userMissionId: Int
    ): Call<JsonBoolean>

    @GET("stats/userDaily")
    fun dailyStat(
        @Query("user_id") uid: Int,
        @Query("date") date: String
    ): Call<JsonDailyStat>

    @GET("stats/userMonth")
    fun monthlyStat(
        @Query("userId") uid: Int,
        @Query("date") date: String
    ): Call<JsonMonthlyStat>
}