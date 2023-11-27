package org.techtown.ryuk

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.techtown.ryuk.databinding.ActivityMypageBinding
import org.techtown.ryuk.databinding.ActivityTeaminfoBinding
import org.techtown.ryuk.databinding.ItemMemberBinding
import org.techtown.ryuk.interfaces.MissionApiService
import org.techtown.ryuk.interfaces.TeamApiService
import org.techtown.ryuk.interfaces.UserApiService
import org.techtown.ryuk.models.JsonDailyStat
import org.techtown.ryuk.models.JsonMonthlyStat
import org.techtown.ryuk.models.JsonTeamDetailResponse
import org.techtown.ryuk.models.Team
import org.techtown.ryuk.models.TeamCheckResponse
import org.techtown.ryuk.models.TeamMembersResponse
import org.techtown.ryuk.models.TeamWithdrawResponse
import org.techtown.ryuk.models.User
import org.techtown.ryuk.models.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class TeamInfoActivity : AppCompatActivity() {
    //...
    private lateinit var binding: ActivityTeaminfoBinding
    private val userApiService: UserApiService =
        RetrofitClient.getInstance().create(UserApiService::class.java)
    private val teamApiService: TeamApiService =
        RetrofitClient.getInstance().create(TeamApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeaminfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView의 LayoutManager 설정
        binding.recyclerViewMembers.layoutManager = LinearLayoutManager(this)

        // RecyclerView의 초기 어댑터 설정
        val initialAdapter = MemberClassAdapter()
        binding.recyclerViewMembers.adapter = initialAdapter

        val userId = getUserIdFromSharedPreferences()
        fetchUserTeamDetails(userId)
        setupBottomNavigationView()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_team

        binding.btnLeaveTeam.setOnClickListener {
            val userId = getUserIdFromSharedPreferences()
            lifecycleScope.launch {
                val currentTeamId = getCurrentTeamId(userId)
                if (currentTeamId != -1) {
                    withdrawFromTeam(userId, currentTeamId)
                } else {
                    // 오류 처리
                }
            }
        }
    }

    private suspend fun getCurrentTeamId(userId: Int): Int {
        return withContext(Dispatchers.IO) {
            try {
                val response = userApiService.getUserInfo(userId).execute() // 동기적으로 API 호출
                if (response.isSuccessful) {
                    response.body()?.userData?.teamId ?: -1
                } else {
                    -1
                }
            } catch (e: Exception) {
                -1
            }
        }
    }

    private fun getUserIdFromSharedPreferences(): Int {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", 2) // Default to -1 if not found
    }



    private fun fetchUserTeamDetails(userId: Int) {
        userApiService.checkUserTeam(userId).enqueue(object : Callback<TeamCheckResponse> {
            override fun onResponse(
                call: Call<TeamCheckResponse>,
                response: Response<TeamCheckResponse>
            ) {
                val teamCheckResponse = response.body()
                if (response.isSuccessful && teamCheckResponse?.status == "ok") {
                    val teamId = teamCheckResponse.data.teamId
                    fetchTeamDetails(teamId)
                } else {
                    // Handle error or no team found
                }
            }

            override fun onFailure(call: Call<TeamCheckResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun fetchTeamDetails(teamId: Int) {
        teamApiService.getTeamDetails(teamId).enqueue(object : Callback<JsonTeamDetailResponse> {
            override fun onResponse(
                call: Call<JsonTeamDetailResponse>,
                response: Response<JsonTeamDetailResponse>
            ) {
                val teamDetailResponse = response.body()
                if (response.isSuccessful && teamDetailResponse?.status == "ok") {
                    val teamDetails = teamDetailResponse.data
                    updateUIWithTeamDetails(teamDetails)
                    fetchTeamMembers(teamId)
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<JsonTeamDetailResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun withdrawFromTeam(userId: Int, teamId: Int) {
        userApiService.withdrawTeam(userId, teamId).enqueue(object : Callback<TeamWithdrawResponse> {
            override fun onResponse(
                call: Call<TeamWithdrawResponse>,
                response: Response<TeamWithdrawResponse>
            ) {
                if (response.isSuccessful) {
                    // 팀 탈퇴 성공 처리
                    Toast.makeText(this@TeamInfoActivity, "팀에서 탈퇴했습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 에러 처리
                    Toast.makeText(this@TeamInfoActivity, "팀 탈퇴 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TeamWithdrawResponse>, t: Throwable) {
                // 네트워크 실패 처리
                Toast.makeText(this@TeamInfoActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }




    private fun setupBottomNavigationView() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_todo -> {
                    startActivity(Intent(this, TodoActivity::class.java))
                    true
                }

                R.id.navigation_team -> {
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

    private fun updateUIWithTeamDetails(teamDetails: Team) {
        val teamName = teamDetails.name // 예시 값
        val teamCategory = teamDetails.category  // 예시 값
        val teamIntroduce = teamDetails.introduce  // 예시 값
        val teamLink = teamDetails.link  // 예시 값
        val teamStart = teamDetails.startDay // 예시 값
        val teamEnd = teamDetails.endDay// 예시 값

        // 각 TextView에 팀 정보 설정
        binding.teamName.text = teamName
        binding.teamCategory.text = "카테고리 : $teamCategory"
        binding.teamIntroduce.text = "팀 소개 : $teamIntroduce"
        binding.teamLink.text = "링크 : $teamLink"
        binding.teamStart.text = "시작 날짜 : $teamStart"
        binding.teamEnd.text = "종료 날짜 : $teamEnd"

    }

    private fun fetchTeamMembers(teamId: Int) {
        userApiService.getTeamMembers(teamId).enqueue(object : Callback<TeamMembersResponse> {
            override fun onResponse(
                call: Call<TeamMembersResponse>,
                response: Response<TeamMembersResponse>
            ) {
                if (response.isSuccessful) {
                    val members =
                        response.body()?.members?.filter { it.userId != getUserIdFromSharedPreferences() }
                    members?.let {
                        // 여기서 기존 어댑터 대신 새 어댑터를 생성하지 않고 기존 어댑터에 데이터를 업데이트
                        (binding.recyclerViewMembers.adapter as? MemberClassAdapter)?.submitList(it)
                    }
                } else {
                    // 에러 처리...
                }
            }

            override fun onFailure(call: Call<TeamMembersResponse>, t: Throwable) {
                // 실패 처리...
            }
        })
    }

    private fun updateRecyclerViewWithMembers(members: List<User>) {
        val adapter = MemberClassAdapter()
        binding.recyclerViewMembers.adapter = adapter
    }

    class MemberClassAdapter : RecyclerView.Adapter<MemberClassAdapter.ViewHolder>() {
        private var members: List<User> = listOf()

        fun submitList(membersList: List<User>) {
            members = membersList
            notifyDataSetChanged() // 데이터가 변경되었음을 어댑터에 알림
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(members[position])
        }

        override fun getItemCount(): Int {
            return members.size
        }

        class ViewHolder(private val binding: ItemMemberBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(member: User) {
                binding.tvMemberNickname.text = member.nickname
                binding.tvProgress.text = "1" // 진행률을 "1"로 설정
            }
        }
    }
}