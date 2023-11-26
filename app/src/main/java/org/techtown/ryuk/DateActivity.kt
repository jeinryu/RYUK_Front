package org.techtown.ryuk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.techtown.ryuk.databinding.ActivityDateBinding
import org.techtown.ryuk.interfaces.MissionApiService
import org.techtown.ryuk.interfaces.UserApiService
import org.techtown.ryuk.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import android.util.Log
import android.view.View

class DateActivity : AppCompatActivity() {
    private val retrofit = RetrofitClient.getInstance()
    private val missionApiService = retrofit.create(MissionApiService::class.java)
    private lateinit var binding: ActivityDateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigationView()

        val dateFormat = SimpleDateFormat("yyyy_MM_dd")
        val selectedDate = intent.getStringExtra("selectedDate")
        val userId = getUserIdFromSharedPreferences()

        val containers = mapOf(
            "매일하력" to binding.cont1,
            "시도해력" to binding.cont2,
            "마음봄력" to binding.cont3,
            "유유자력" to binding.cont4,
            "레벨업력" to binding.cont5
        )

        val containersPersonal = mapOf(
            "매일하력" to binding.cont1Personal,
            "시도해력" to binding.cont2Personal,
            "마음봄력" to binding.cont3Personal,
            "유유자력" to binding.cont4Personal,
            "레벨업력" to binding.cont5Personal
        )

        fun paintMission(mission: Mission) {
            val task = CheckBox(this)
            task.layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            task.text = mission.title
            task.isChecked = mission.is_success == 1
            task.isClickable = false
            if (mission.from_team == 0) {
                containersPersonal[mission.mission_type]?.addView(task)
                containersPersonal[mission.mission_type]?.visibility = View.VISIBLE
            } else {
                containers[mission.mission_type]?.addView(task)
                containers[mission.mission_type]?.visibility = View.VISIBLE
            }
        }

        fun getTodos() {
            val call = selectedDate?.let { missionApiService.getMissions(userId, it) }
            call?.enqueue(object : Callback<JsonGetMissions> {
                override fun onResponse(
                    call: Call<JsonGetMissions>,
                    response: Response<JsonGetMissions>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.mission?.forEach { mission ->
                            paintMission(mission)
                        }
                    }
                }

                override fun onFailure(call: Call<JsonGetMissions>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val date = selectedDate?.split("_")
        binding.text.text = "${date!![0]}년 ${date[1]}월 ${date[2]}일"
        getTodos()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_mypage
    }

    private fun setupBottomNavigationView() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val userId = getUserIdFromSharedPreferences()

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_todo -> {
                    startActivity(Intent(this, TodoActivity::class.java))
                    true
                }
                R.id.navigation_team -> {
                    checkUserTeamStatus(userId)
                    true
                }
                R.id.navigation_mypage -> {
                    // finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun checkUserTeamStatus(userId: Int) {
        val apiService = RetrofitClient.getInstance().create(UserApiService::class.java)

        apiService.checkUserTeam(userId).enqueue(object : Callback<TeamCheckResponse> {
            override fun onResponse(call: Call<TeamCheckResponse>, response: Response<TeamCheckResponse>) {
                if (response.isSuccessful) {
                    val teamCheckResponse = response.body()
                    if (teamCheckResponse != null && teamCheckResponse.data.teamId != 0) {
                        // 팀이 있으면 TeamInfoActivity로 이동
                        startActivity(Intent(this@DateActivity, TeamInfoActivity::class.java))
                    } else {
                        // 팀이 없으면 TeamSearchActivity로 이동
                        startActivity(Intent(this@DateActivity, TeamSearchActivity::class.java))
                    }
                }
            }

            override fun onFailure(call: Call<TeamCheckResponse>, t: Throwable) {
                // 네트워크 오류 또는 기타 오류 처리
                Log.e("TeamSearchActivity", "Error: ${t.message}")
            }
        })
    }

    private fun getUserIdFromSharedPreferences(): Int {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", 2) // Default value -1 if not found
    }
}