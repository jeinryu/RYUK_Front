package org.techtown.ryuk

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import org.techtown.ryuk.databinding.ActivityMypageBinding
import org.techtown.ryuk.databinding.ActivityTeaminfoBinding
import org.techtown.ryuk.interfaces.MissionApiService
import org.techtown.ryuk.interfaces.TeamApiService
import org.techtown.ryuk.interfaces.UserApiService
import org.techtown.ryuk.models.JsonDailyStat
import org.techtown.ryuk.models.JsonMonthlyStat
import org.techtown.ryuk.models.JsonTeamDetailResponse
import org.techtown.ryuk.models.Team
import org.techtown.ryuk.models.TeamCheckResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TeamInfoActivity : AppCompatActivity() {
    //...
    private lateinit var binding: ActivityTeaminfoBinding
    private val userApiService: UserApiService = RetrofitClient.getInstance().create(UserApiService::class.java)
    private val teamApiService: TeamApiService = RetrofitClient.getInstance().create(TeamApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeaminfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = getUserIdFromSharedPreferences()
        fetchUserTeamDetails(userId)
        setupBottomNavigationView()
    }

    private fun getUserIdFromSharedPreferences(): Int {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", 2) // Default to -1 if not found
    }

    private fun fetchUserTeamDetails(userId: Int) {
        userApiService.checkUserTeam(userId).enqueue(object : Callback<TeamCheckResponse> {
            override fun onResponse(call: Call<TeamCheckResponse>, response: Response<TeamCheckResponse>) {
                val teamCheckResponse = response.body()
                if (response.isSuccessful && teamCheckResponse?.status == "ok") {
                    val teamId = teamCheckResponse.data.teamId
                    fetchTeamDetails(teamId)
                } else {
                    // Handle error or no team found
                }
            }

            override fun onFailure(call: Call<TeamCheckResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun fetchTeamDetails(teamId: Int) {
        teamApiService.getTeamDetails(teamId).enqueue(object : Callback<JsonTeamDetailResponse> {
            override fun onResponse(call: Call<JsonTeamDetailResponse>, response: Response<JsonTeamDetailResponse>) {
                val teamDetailResponse = response.body()
                if (response.isSuccessful && teamDetailResponse?.status == "ok") {
                    val teamDetails = teamDetailResponse.data
                    updateUIWithTeamDetails(teamDetails)
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<JsonTeamDetailResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun setupBottomNavigationView() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_todo -> {
                    startActivity(Intent(this, TodoActivity::class.java))
                    true
                }
                R.id.navigation_team -> {
                    true
                }
                R.id.navigation_mypage -> {
                    startActivity(Intent(this, MypageActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun updateUIWithTeamDetails(teamDetails: Team) {
        binding.teamName.text = teamDetails.name
        binding.teamCategory.text = teamDetails.category
        binding.teamIntroduce.text = teamDetails.introduce
        binding.teamLink.text = teamDetails.link
        binding.teamStart.text = teamDetails.startDay
        binding.teamEnd.text = teamDetails.endDay
        // You may format the dates if needed
    }

    //... Rest of your Activity code
}