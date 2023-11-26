package org.techtown.ryuk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.ryuk.databinding.ActivityTeamsearchBinding
import androidx.appcompat.widget.SearchView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.techtown.ryuk.models.Team
import org.techtown.ryuk.models.JsonGetTeams
import org.techtown.ryuk.interfaces.TeamApiService
import org.techtown.ryuk.models.JsonTeamJoinResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTeamsearchBinding
    private lateinit var teamClassAdapter: TeamClassAdapter
    private var fullTeamList: List<Team> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamsearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchView()
        loadTeams()
        setupBottomNavigationView()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.selectedItemId = R.id.navigation_team
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

    private fun setupRecyclerView() {
        teamClassAdapter = TeamClassAdapter(
            { team -> showJoinDialog(team) },
            { teamId -> requestTeamJoin(teamId) } // requestTeamJoin 함수 전달
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TeamSearchActivity)
            adapter = teamClassAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@TeamSearchActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun showJoinDialog(team: Team) {
        AlertDialog.Builder(this)
            .setTitle("${team.name}에 가입하겠습니까?")
            .setPositiveButton("예") { _, _ -> requestTeamJoin(team.teamId) }
            .setNegativeButton("아니오", null)
            .show()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterList(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterList(it) }
                return true
            }
        })
    }

    private fun requestTeamJoin(teamId: Int) {
        val userId = getUserIdFromSharedPreferences()
        val teamApiService = RetrofitClient.getInstance().create(TeamApiService::class.java)
        teamApiService.requestTeamJoin(userId, teamId)
            .enqueue(object : Callback<JsonTeamJoinResponse> {
                override fun onResponse(
                    call: Call<JsonTeamJoinResponse>,
                    response: Response<JsonTeamJoinResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@TeamSearchActivity, "가입 신청 완료", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            this@TeamSearchActivity,
                            "가입 신청 실패: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<JsonTeamJoinResponse>, t: Throwable) {
                    Toast.makeText(
                        this@TeamSearchActivity,
                        "네트워크 오류: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private fun getUserIdFromSharedPreferences(): Int {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", 1) // Default value -1 if not found
    }

    private fun filterList(query: String) {
        val filteredList = fullTeamList.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.introduce.contains(query, ignoreCase = true) // 팀 소개 문구 검색 추가
        }
        teamClassAdapter.submitList(filteredList)
    }

    private fun loadTeams() {
        val teamApiService = RetrofitClient.getInstance().create(TeamApiService::class.java)
        teamApiService.getAllTeams().enqueue(object : Callback<JsonGetTeams> {
            override fun onResponse(call: Call<JsonGetTeams>, response: Response<JsonGetTeams>) {
                if (response.isSuccessful) {
                    response.body()?.let { jsonGetTeams ->
                        fullTeamList = jsonGetTeams.teams
                        teamClassAdapter.submitList(jsonGetTeams.teams)
                    }
                } else {
                    Log.e("TeamSearchActivity", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonGetTeams>, t: Throwable) {
                Log.e("TeamSearchActivity", "Network Error: ${t.message}")
            }
        })
    }
}