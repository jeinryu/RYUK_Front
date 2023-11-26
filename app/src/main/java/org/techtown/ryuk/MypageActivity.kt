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
import org.techtown.ryuk.interfaces.MissionApiService
import org.techtown.ryuk.interfaces.UserApiService
import org.techtown.ryuk.models.JsonDailyStat
import org.techtown.ryuk.models.JsonMonthlyStat
import org.techtown.ryuk.models.TeamCheckResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MypageActivity : AppCompatActivity() {
    private val retrofit = RetrofitClient.getInstance()
    private val missionApiService = retrofit.create(MissionApiService::class.java)
    private lateinit var binding: ActivityMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 네비게이션 바 설정
        setupBottomNavigationView()

        val dateFormat = SimpleDateFormat("yyyy_MM_dd")
        val userId = getUserIdFromSharedPreferences()

        val calendarView: MaterialCalendarView = binding.calendarView
        calendarView.setOnDateChangedListener { _, date, _ ->
            val selectedDate = dateFormat.format(date.date)
            val intent = Intent(applicationContext, DateActivity::class.java).apply {
                putExtra("selectedDate", selectedDate)
            }
            startActivity(intent)
            finish()
        }

        val today = CalendarDay.today()
        val callStat = missionApiService.monthlyStat(userId, dateFormat.format(Date()))
        callStat.enqueue(object : Callback<JsonMonthlyStat> {
            override fun onResponse(call: Call<JsonMonthlyStat>, response: Response<JsonMonthlyStat>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { data ->
                        data.forEachIndexed { index, stat ->
                            val date = CalendarDay.from(today.year, today.month, index + 1)
                            calendarView.addDecorator(createDecorator(stat, date))
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonMonthlyStat>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
            }
        })
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
                        startActivity(Intent(this@MypageActivity, TeamInfoActivity::class.java))
                    } else {
                        // 팀이 없으면 TeamSearchActivity로 이동
                        startActivity(Intent(this@MypageActivity, TeamSearchActivity::class.java))
                    }
                }
            }

            override fun onFailure(call: Call<TeamCheckResponse>, t: Throwable) {
                // 네트워크 오류 또는 기타 오류 처리
                Log.e("TeamSearchActivity", "Error: ${t.message}")
            }
        })
    }

    private fun createDecorator(stat: Int, date: CalendarDay): DayViewDecorator {
        return object : DayViewDecorator {
            override fun decorate(view: DayViewFacade?) {
                view?.setDaysDisabled(false)
                val color = when (stat) {
                    0 -> "#FFFFFF"
                    in 0..25 -> "#EFFDCC"
                    in 25..50 -> "#DDFD8D"
                    in 50..75 -> "#BFF055"
                    else -> "#ADFF00"
                }
                if (stat == 100) view?.addSpan(DotSpan(10f, Color.YELLOW))
                view?.setBackgroundDrawable(ColorDrawable(Color.parseColor(color)))
            }

            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day == date
            }
        }
    }

    private fun getUserIdFromSharedPreferences(): Int {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sharedPreferences.getInt("user_id",2) // Default value -1 if not found
    }
}