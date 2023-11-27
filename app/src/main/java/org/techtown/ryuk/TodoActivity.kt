package org.techtown.ryuk

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.techtown.ryuk.databinding.TodoBinding
import org.techtown.ryuk.interfaces.MissionApiService
import org.techtown.ryuk.interfaces.UserApiService
import org.techtown.ryuk.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class TodoActivity : Activity() {
    private val retrofit = RetrofitClient.getInstance()
    private val missionApiService = retrofit.create(MissionApiService::class.java)
    private lateinit var binding: TodoBinding
    private var totalMission: Int = 0
    private var completeMission: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = getUserIdFromSharedPreferences()
        val dateFormat = SimpleDateFormat("yyyy_MM_dd")
        val dateNow = LocalDate.now().toString().replace("-", "_")
        binding.textToday.text = dateNow.replace("_", "/")

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

        setupUI(dateNow, userId, containers, containersPersonal)
        getTodos(userId, dateNow, containers, containersPersonal)
        setupBottomNavigationView()
    }

    private fun setupBottomNavigationView() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val userId = getUserIdFromSharedPreferences()

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_todo -> {
                    true
                }
                R.id.navigation_team -> {
                    checkUserTeamStatus(userId)
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

    private fun checkUserTeamStatus(userId: Int) {
        val apiService = RetrofitClient.getInstance().create(UserApiService::class.java)

        apiService.checkUserTeam(userId).enqueue(object : Callback<TeamCheckResponse> {
            override fun onResponse(call: Call<TeamCheckResponse>, response: Response<TeamCheckResponse>) {
                if (response.isSuccessful && response.body()?.status == "ok") {
                    // 팀이 있으면 TeamInfoActivity로 이동
                    startActivity(Intent(this@TodoActivity, TeamInfoActivity::class.java))
                } else {
                    // 팀이 없으면 TeamSearchActivity로 이동
                    startActivity(Intent(this@TodoActivity, TeamSearchActivity::class.java))
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
        return sharedPreferences.getInt("user_id", 2) // -1 as default value if not found
    }

    private fun setupUI(dateNow: String, userId: Int, containers: Map<String, LinearLayoutCompat>, containersPersonal: Map<String, LinearLayoutCompat>) {
        setupSpinner()
        setupAddTaskButton(dateNow, userId, containers, containersPersonal)
    }

    private fun setupSpinner() {
        val spinner: Spinner = binding.spinner
        val adapter = ArrayAdapter.createFromResource(this, R.array.type_list, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupAddTaskButton(dateNow: String, userId: Int, containers: Map<String, LinearLayoutCompat>, containersPersonal: Map<String, LinearLayoutCompat>) {
        binding.addTask.setOnClickListener {
            val newTask = binding.newTaskContent.text.toString()
            val type = binding.spinner.selectedItem.toString()
            binding.newTaskContent.text.clear()
            addMission(newTask, type, dateNow, userId, containers, containersPersonal)
        }
    }

    private fun addMission(title: String, type: String, date: String, userId: Int, containers: Map<String, LinearLayoutCompat>, containersPersonal: Map<String, LinearLayoutCompat>) {
        val call = missionApiService.addMission(title, type, date, userId)
        call.enqueue(object : Callback<JsonAddMission> {
            override fun onResponse(call: Call<JsonAddMission>, response: Response<JsonAddMission>) {
                if (response.isSuccessful) {
                    response.body()?.mission?.let {
                        val mission = Mission(it.user_mission_id, it.is_success, title, type, "check", 0)
                        paintMission(mission, containers, containersPersonal)
                    }
                    updateProgress()
                }
            }

            override fun onFailure(call: Call<JsonAddMission>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun paintMission(mission: Mission, containers: Map<String, LinearLayoutCompat>, containersPersonal: Map<String, LinearLayoutCompat>) {
        val task = CheckBox(this)
        task.layoutParams = LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        task.text = mission.title
        task.isChecked = mission.is_success == 1
        // task.buttonTintList = ColorStateList.valueOf(Color.parseColor("#BFF055"))
        totalMission++
        completeMission += mission.is_success
        if (mission.from_team == 0) {
            var layout = LinearLayoutCompat(this)
            val delete = ImageButton(this)
            delete.setBackgroundResource(R.drawable.ic_baseline_delete_24)
            layout.orientation = LinearLayoutCompat.HORIZONTAL
            layout.layoutParams = LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layout.addView(task)
            layout.addView(delete)
            delete.setOnClickListener {
                deleteMission(mission, layout, containersPersonal)
            }
            containersPersonal[mission.mission_type]?.visibility = View.VISIBLE
            containersPersonal[mission.mission_type]?.addView(layout)
        } else {
            containers[mission.mission_type]?.visibility = View.VISIBLE
            containers[mission.mission_type]?.addView(task)
        }
        task.setOnClickListener {
            updateMission(mission)
        }
    }

    private fun deleteMission(mission: Mission, layout: LinearLayoutCompat, containersPersonal: Map<String, LinearLayoutCompat>) {
        containersPersonal[mission.mission_type]?.removeView(layout)
        totalMission--
        completeMission -= mission.is_success
        updateProgress()
        val call = missionApiService.deleteMission(mission.user_mission_id)
        call.enqueue(object : Callback<JsonBoolean> {
            override fun onResponse(call: Call<JsonBoolean>, response: Response<JsonBoolean>) {
                // Handle response
            }

            override fun onFailure(call: Call<JsonBoolean>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateMission(mission: Mission) {
        completeMission -= mission.is_success
        mission.is_success = 1 - mission.is_success
        completeMission += mission.is_success
        val call = missionApiService.setSuccess(mission.user_mission_id)
        call.enqueue(object : Callback<JsonBoolean> {
            override fun onResponse(call: Call<JsonBoolean>, response: Response<JsonBoolean>) {
                // Handle response
            }

            override fun onFailure(call: Call<JsonBoolean>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
            }
        })
        updateProgress()
    }

    private fun getTodos(userId: Int, dateNow: String, containers: Map<String, LinearLayoutCompat>, containersPersonal: Map<String, LinearLayoutCompat>) {
        val call = missionApiService.getMissions(userId, dateNow)
        call.enqueue(object : Callback<JsonGetMissions> {
            override fun onResponse(call: Call<JsonGetMissions>, response: Response<JsonGetMissions>) {
                if (response.isSuccessful) {
                    response.body()?.mission?.forEach { mission ->
                        paintMission(mission, containers, containersPersonal)
                    }
                    updateProgress()
                }
            }

            override fun onFailure(call: Call<JsonGetMissions>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProgress() {
        if (totalMission == 0) binding.progress.setProgress(0, true) else binding.progress.setProgress(completeMission * 100 / totalMission, true)
        binding.progressValue.text = completeMission.toString() + "/" + totalMission.toString()
    }
}