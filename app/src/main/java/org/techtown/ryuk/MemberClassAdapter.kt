package org.techtown.ryuk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.ryuk.databinding.ItemMemberBinding
import org.techtown.ryuk.models.TeamDailyStat
import org.techtown.ryuk.models.User

class MemberClassAdapter(private val userId: Int) : RecyclerView.Adapter<MemberClassAdapter.ViewHolder>() {
    private var stats: List<TeamDailyStat> = listOf()

    private var originalStats: List<TeamDailyStat> = listOf()

    fun filter(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            originalStats
        } else {
            originalStats.filter { it.nickname.contains(query, ignoreCase = true) }
        }
        stats = filteredList
        notifyDataSetChanged()
    }

    fun submitList(stats: List<TeamDailyStat>, userId: Int) {
        val sortedStats = stats.sortedWith(compareBy { it.userId != userId })
        this.originalStats = sortedStats
        this.stats = sortedStats
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return stats.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stats[position], userId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }



    class ViewHolder(private val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stat: TeamDailyStat, userId: Int) {
            val nicknameText = if (stat.userId == userId) "본인" else "닉네임 = ${stat.nickname}"
            binding.tvMemberNickname.text = nicknameText
            binding.tvProgress.text = "진행률 = ${stat.numSuccess}/${stat.numMission}"
        }
    }
}