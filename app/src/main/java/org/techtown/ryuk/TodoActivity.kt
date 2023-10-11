package org.techtown.ryuk

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.marginTop
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.techtown.ryuk.databinding.TodoBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.techtown.ryuk.interfaces.MissionApiService
import org.techtown.ryuk.models.Json
import org.techtown.ryuk.models.Mission
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class TodoActivity : Activity() {
    val retrofit = RetrofitClient.getInstance()

    val missionApiService = retrofit.create(MissionApiService::class.java)
    private lateinit var binding: TodoBinding
    var missions = mutableListOf<Mission>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val containers : List<LinearLayoutCompat> = listOf(binding.cont1, binding.cont2, binding.cont3, binding.cont4, binding.cont5)
        fun paintTodos(todoList: List<ToDo>) {
            for (todo in todoList) {
                val task = CheckBox(this)
                task.layoutParams = LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                task.text = todo.content
                containers[todo.type].addView(task)
                task.setOnClickListener {
                    todo.reverseCheck()
                    binding.progress.setProgress(ToDo.checkedCount * 100 / ToDo.todoCount, true)
                    // db에 데이터 전송
                }
            }
        }
//        fun paintMissions(missions: List<Mission>) {
//            for (mission in missions) {
//                val task = CheckBox(this)
//                task.layoutParams = LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//                task.text = mission.title
//                containers[mission.type[0]].addView(task)
//            }
//        }
        fun paintMission(mission: Mission) {
            val task = CheckBox(this)
            task.layoutParams = LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            task.text = mission.title
            containers[mission.type[0]].addView(task)
        }
        val spinner: Spinner = binding.spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.type_list,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // addTask(optional)
        binding.addTask.setOnClickListener {
            val newTask : String = binding.newTaskContent.text.toString()
            var type: Int = spinner.selectedItemPosition
            paintTodos(listOf(ToDo(newTask, false, type)))
            binding.progress.setProgress(ToDo.checkedCount * 100 / ToDo.todoCount, true)
            // 이거 대신 live data 알아보기
        }
        // 관리자 계정으로부터 todoList 받아오기
        fun getTodos() {
            val call = missionApiService.getMissions("2023_11_23")
            call.enqueue(object: Callback<Json> {
                override fun onResponse(
                    call: Call<Json>,
                    response: Response<Json>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        body?.mission?.let { paintMission(it) }
                        Log.d("OK", "Successful")
                    }
                }

                override fun onFailure(call: Call<Json>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }
        getTodos()
    }
}