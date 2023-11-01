package org.techtown.ryuk.interfaces

import org.techtown.ryuk.models.JsonAddMission
import org.techtown.ryuk.models.JsonAssignMission
import org.techtown.ryuk.models.JsonBoolean
import org.techtown.ryuk.models.JsonGetMissions
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MissionApiService {
    @GET("/user/todayMission")
    fun getMissions(
        @Query("userId") uid: Int,
        @Query("date") date: String
    ): Call<JsonGetMissions>

    @GET("mission/add")
    fun addMission(
        @Query("title") title: String,
        @Query("missionType") type: String,
    ): Call<JsonAddMission>

    @GET("mission/assign")
    fun assignMission(
        @Query("date") date: String,
        @Query("userId") uid: Int,
        @Query("missionId") mid: Int,
    ): Call<JsonAssignMission>

    @GET("user/setSuccess")
    fun setSuccess(
        @Query("userMissionId") userMissionId: Int
    ): Call<JsonBoolean>

    @GET("mission/delete")
    fun deleteMission(
        @Query("missionId") id: Int
    ): Call<JsonBoolean>
}