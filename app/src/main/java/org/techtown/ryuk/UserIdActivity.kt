package org.techtown.ryuk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.techtown.ryuk.databinding.ActivityUserIdBinding
import org.techtown.ryuk.interfaces.UserApiService
import org.techtown.ryuk.models.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserIdActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserIdBinding
    private val apiService: UserApiService = RetrofitClient.getInstance().create(UserApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val userId = binding.editId.text.toString().toIntOrNull()
            userId?.let { checkUserId(it) } ?: Toast.makeText(this, "잘못된 사용자 ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkUserId(userId: Int) {
        apiService.getUserInfo(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val userResponse = response.body()
                if (response.isSuccessful && userResponse?.status == "ok" && userResponse.userData != null) {
                    storeUserId(userId)  // SharedPreferences에 userId 저장
                    val intent = Intent(this@UserIdActivity, TodoActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // 사용자 ID 확인 실패 처리
                    Toast.makeText(this@UserIdActivity, "로그인 실패", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("UserIdActivity", "Network Error: ${t.message}", t)
                Toast.makeText(this@UserIdActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun storeUserId(userId: Int) {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", userId)
        editor.apply()
    }
}