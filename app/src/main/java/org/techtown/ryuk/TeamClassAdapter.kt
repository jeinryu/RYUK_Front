package org.techtown.ryuk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.techtown.ryuk.databinding.ItemMemberBinding
import org.techtown.ryuk.databinding.ItemTeamBinding
import org.techtown.ryuk.models.Team

class TeamClassAdapter(
    private val onItemClicked: (Team) -> Unit,
    private val onJoinClicked: (Int) -> Unit,
    private val teamMemberCounts: Map<Int, Int> = emptyMap()
) : ListAdapter<Team, TeamClassAdapter.TeamClassViewHolder>(TeamClassCallback) {
    private var fullList: List<Team> = listOf()

    fun updateList(list: List<Team>) {
        fullList = list
        submitList(list)
    }

    fun filter(query: String) {
        val filteredList = fullList.filter {
            it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }
        submitList(filteredList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamClassViewHolder {
        val binding = ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeamClassViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: TeamClassViewHolder, position: Int) {
        val team = getItem(position)
        val memberCount = teamMemberCounts[team.teamId] ?: 0
        holder.bind(team, memberCount, onJoinClicked)
    }

    class TeamClassViewHolder(private val binding: ItemTeamBinding,
                              private val onItemClicked: (Team) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Team, memberCount: Int, onJoinClicked: (Int) -> Unit) {
            with(binding) {
                tvName.text = item.name
                tvCategory.text = "카테고리: ${item.category}"
                tvIntroduce.text = "소개: ${item.introduce}"
                tvLink.text = "링크: ${item.link}"
                tvNumber.text = "팀원 수: $memberCount"

                btnRegisterTeam.setOnClickListener {
                    onJoinClicked(item.teamId)
                }

                root.setOnClickListener { onItemClicked(item) }
            }
        }
    }
    companion object {
        private val TeamClassCallback = object : DiffUtil.ItemCallback<Team>() {
            override fun areItemsTheSame(oldItem: Team, newItem: Team): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: Team, newItem: Team): Boolean {
                return oldItem == newItem
            }
        }
    }
}