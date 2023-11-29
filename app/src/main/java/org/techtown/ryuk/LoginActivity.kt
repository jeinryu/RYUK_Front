package org.techtown.ryuk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.techtown.ryuk.databinding.ActivityLoginBinding
import org.techtown.ryuk.interfaces.UserApiService
import org.techtown.ryuk.models.LoginRequest
import org.techtown.ryuk.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val apiService: UserApiService = RetrofitClient.getInstance().create(UserApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val loginId = binding.editId.text.toString()
            val password = binding.editPw.text.toString()
            loginUser(loginId, password)
        }
    }

    private fun loginUser(loginId: String, password: String) {
        Log.d("LoginActivity", "Trying to login with ID: $loginId, Password: $password")
        apiService.loginUser(loginId, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val loginResponse = response.body()
                if (loginResponse?.status == "ok" && loginResponse.userData != null) {
                    // 로그인 성공 처리
                    val userId = loginResponse.userData.userId
                    storeUserId(userId)  // SharedPreferences에 userId 저장
                    val intent = Intent(this@LoginActivity, TodoActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // 로그인 실패 처리
                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
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