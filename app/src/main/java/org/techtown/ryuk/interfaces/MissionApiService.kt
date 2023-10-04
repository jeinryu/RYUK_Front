package org.techtown.ryuk.interfaces

import org.techtown.ryuk.models.Json
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MissionApiService {
    @GET("mission/get")
    fun getMissions(
        @Query("date") date: String
    ): Call<Json>
}