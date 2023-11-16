package org.techtown.ryuk

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import org.techtown.ryuk.databinding.ActivityDateBinding
import org.techtown.ryuk.databinding.ActivityMypageBinding
import org.techtown.ryuk.models.JsonDailyStat
import org.techtown.ryuk.interfaces.MissionApiService
import org.techtown.ryuk.models.JsonMonthlyStat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MypageActivity : AppCompatActivity() {
    val retrofit = RetrofitClient.getInstance()
    val missionApiService = retrofit.create(MissionApiService::class.java)
    private lateinit var binding: ActivityMypageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dateFormat = SimpleDateFormat("yyyy_MM_dd")

        val calendarView: MaterialCalendarView = binding.calendarView
        calendarView.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
            val selectedDate = dateFormat.format(date.date)
            val intent = Intent(this, DateActivity::class.java)
            intent.putExtra("selectedDate", selectedDate) // You can pass data to the new activity if needed
            startActivity(intent)
        })
        val today = CalendarDay.today()
        val callStat = missionApiService.monthlyStat(1, dateFormat.format(Date(System.currentTimeMillis())))
        callStat?.enqueue(object: Callback<JsonMonthlyStat> {
            override fun onResponse(
                call: Call<JsonMonthlyStat>,
                response: Response<JsonMonthlyStat>
            ) {
                if (response.isSuccessful) {
                    val bodyStat = response.body()
                    bodyStat?.data?.let {
                        for ((index, stat) in it.withIndex()) {
                            val date = CalendarDay.from(today.year, today.month, index+1)
                            calendarView.addDecorator(object : DayViewDecorator{
                                override fun decorate(view: DayViewFacade?) {
                                    view?.setDaysDisabled(false)
                                    when (stat) {
                                        0 -> 1
                                        in 0..25 -> view?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#EFFDCC")))
                                        in 25..50 -> view?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#DDFD8D")))
                                        in 50..75 -> view?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#BFF055")))
                                        else -> {
                                            if (stat == 100) view?.addSpan(DotSpan(10f, Color.YELLOW))
                                            view?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ADFF00")))
                                        }
                                    }
                                }
                                override fun shouldDecorate(day: CalendarDay?): Boolean {
                                    return day == date
                                }
                            })
                        }
                    }
                }
            }
            override fun onFailure(call: Call<JsonMonthlyStat>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(applicationContext, "Call Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}