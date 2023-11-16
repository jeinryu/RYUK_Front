package org.techtown.ryuk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import org.techtown.ryuk.databinding.ActivityDateBinding
import org.techtown.ryuk.databinding.TodoBinding
import org.techtown.ryuk.interfaces.MissionApiService
import org.techtown.ryuk.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DateActivity : AppCompatActivity() {
    val retrofit = RetrofitClient.getInstance()
    val missionApiService = retrofit.create(MissionApiService::class.java)
    private lateinit var binding: ActivityDateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dateFormat = SimpleDateFormat("yyyy_MM_dd")
        val selectedDate = intent.getStringExtra("selectedDate")
        val containers = mapOf("매일하력" to binding.cont1,"시도해력" to binding.cont2,"마음봄력" to binding.cont3,"유유자력" to binding.cont4,"레벨업력" to binding.cont5)
        val containersPersonal = mapOf("매일하력" to binding.cont1Personal,"시도해력" to binding.cont2Personal,"마음봄력" to binding.cont3Personal,"유유자력" to binding.cont4Personal,"레벨업력" to binding.cont5Personal)
        fun paintMission(mission: Mission) {
            val task = CheckBox(this)
            task.layoutParams = LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            task.text = mission.title
            task.isChecked = mission.is_success==1
            task.isClickable = false
            if (mission.from_team == 0) {
                containersPersonal[mission.mission_type]?.addView(task)
            } else {
                containers[mission.mission_type]?.addView(task)
            }
        }
        fun getTodos() {
            // uid
            val call = selectedDate?.let { missionApiService.getMissions(1, it) }
            call?.enqueue(object: Callback<JsonGetMissions> {
                override fun onResponse(
                    call: Call<JsonGetMissions>,
                    response: Response<JsonGetMissions>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        body?.mission?.let {
                            for (mission in it) {
                                paintMission(mission)
                            }
                        }
                        val callStat = selectedDate?.let { missionApiService.dailyStat(1, it) }
                        callStat?.enqueue(object: Callback<JsonDailyStat> {
                            override fun onResponse(
                                call: Call<JsonDailyStat>,
                                response: Response<JsonDailyStat>
                            ) {
                                if (response.isSuccessful) {
                                    val bodyStat = response.body()
                                    bodyStat?.data?.percentage?.let {
                                        binding.progress.setProgress(
                                            it
                                        )
                                    }
                                }
                            }

                            override fun onFailure(call: Call<JsonDailyStat>, t: Throwable) {
                                t.printStackTrace()
                                Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
                override fun onFailure(call: Call<JsonGetMissions>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }
        val date = selectedDate?.split("_")
        binding.text.text = date!![0] +"년 "+ date!![1]+"월 "+date[2]!!+"일"
        getTodos()
    }
}