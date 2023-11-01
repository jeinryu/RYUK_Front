package org.techtown.ryuk

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.LinearLayoutCompat.LayoutParams
import androidx.core.view.marginTop
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.techtown.ryuk.databinding.TodoBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.techtown.ryuk.interfaces.MissionApiService
import org.techtown.ryuk.models.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class TodoActivity : Activity() {
    val retrofit = RetrofitClient.getInstance()

    val missionApiService = retrofit.create(MissionApiService::class.java)
    private lateinit var binding: TodoBinding
    // var missions = mutableListOf<Mission>() datastore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var totalMission: Int = 0
        var completeMission: Int = 0
        val dateFormat = SimpleDateFormat("yyyy_MM_dd")
        val containers = mapOf("매일하력" to binding.cont1,"시도해력" to binding.cont2,"마음봄력" to binding.cont3,"유유자력" to binding.cont4,"레벨업력" to binding.cont5)
        val containersPersonal = mapOf("매일하력" to binding.cont1Personal,"시도해력" to binding.cont2Personal,"마음봄력" to binding.cont3Personal,"유유자력" to binding.cont4Personal,"레벨업력" to binding.cont5Personal)
        fun paintMission(mission: Mission, personal: Boolean) {
            val task = CheckBox(this)
            task.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            task.text = mission.title
            task.isChecked = mission.is_success==1
            totalMission++
            completeMission += mission.is_success
            if (personal) {
                var layout = LinearLayoutCompat(this)
                val delete = ImageButton(this)
                delete.setBackgroundResource(R.drawable.ic_baseline_delete_24)
                layout.orientation = LinearLayoutCompat.HORIZONTAL
                layout.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                layout.addView(task)
                layout.addView(delete)
                delete.apply { Gravity.RIGHT }
                containersPersonal[mission.mission_type]?.addView(layout)
                layout.layoutParams.apply {
                    width = LayoutParams.MATCH_PARENT
                    height = LayoutParams.WRAP_CONTENT
                }
                delete.setOnClickListener {
                    containersPersonal[mission.mission_type]?.removeView(layout)
                    totalMission--
                    binding.progress.setProgress(completeMission*100/totalMission, true)
                    // mission delete API Call
                }
            } else {
                containers[mission.mission_type]?.addView(task)
            }
            binding.progress.setProgress(completeMission*100/totalMission, true)
            task.setOnClickListener {
                completeMission -= mission.is_success
                mission.is_success = 1 - mission.is_success
                Toast.makeText(applicationContext, mission.is_success.toString(), Toast.LENGTH_SHORT).show()
                completeMission += mission.is_success
                val call = missionApiService.setSuccess(mission.id)
                call.enqueue(object: Callback<JsonBoolean> {
                    override fun onResponse(
                        call: Call<JsonBoolean>,
                        response: Response<JsonBoolean>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(applicationContext, "Call Success", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<JsonBoolean>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
                    }
                })
                binding.progress.setProgress(completeMission*100/totalMission, true)
            }
        }
        val spinner: Spinner = binding.spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.type_list,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        binding.addTask.setOnClickListener {
            val newTask : String = binding.newTaskContent.text.toString()
            var type: String = spinner.selectedItem.toString()
            binding.newTaskContent.text.clear()
            val call = missionApiService.addMission(newTask, type)
            call.enqueue(object: Callback<JsonAddMission> {
                override fun onResponse(
                    call: Call<JsonAddMission>,
                    response: Response<JsonAddMission>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Mission Added Successfully", Toast.LENGTH_SHORT).show()
                        val body = response.body()
                        body?.missionId?.let {
                            val id = it.id
                            val assignCall = missionApiService.assignMission(dateFormat.format(Date(System.currentTimeMillis())), 2, id)
                            assignCall.enqueue(object : Callback<JsonAssignMission> {
                                override fun onResponse(
                                    call: Call<JsonAssignMission>,
                                    response: Response<JsonAssignMission>
                                ) {
                                    val body = response.body()
                                    body?.mission?.let {
                                        val m = Mission(it.id, it.is_success, newTask, type, "check")
                                        paintMission(m, true)
                                    }
                                }
                                override fun onFailure(call: Call<JsonAssignMission>, t: Throwable) {
                                    t.printStackTrace()
                                    Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }

                    }
                }

                override fun onFailure(call: Call<JsonAddMission>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }
        // 관리자 계정으로부터 todoList 받아오기
        fun getTodos() {
            // uid
            val call = missionApiService.getMissions(2, dateFormat.format(Date(System.currentTimeMillis())))
            call.enqueue(object: Callback<JsonGetMissions> {
                override fun onResponse(
                    call: Call<JsonGetMissions>,
                    response: Response<JsonGetMissions>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        body?.mission?.let {
                            for (mission in it) {
                                paintMission(mission, false)
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<JsonGetMissions>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
                }
            })

        }
        getTodos()
    }
}