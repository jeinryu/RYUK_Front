package org.techtown.ryuk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import org.techtown.ryuk.databinding.ActivityDateBinding
import org.techtown.ryuk.databinding.ActivityMypageBinding
import java.text.SimpleDateFormat
import java.util.*

class MypageActivity : AppCompatActivity() {
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
    }
}