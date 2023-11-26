package org.techtown.ryuk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.techtown.ryuk.databinding.ItemTeamBinding
import org.techtown.ryuk.models.Team

class TeamClassAdapter(
    private val onItemClicked: (Team) -> Unit,
    private val onJoinClicked: (Int) -> Unit // 이 부분 추가
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
        holder.bind(getItem(position), onJoinClicked) // onJoinClicked 함수 전달
    }

    class TeamClassViewHolder(private val binding: ItemTeamBinding,
                              private val onItemClicked: (Team) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Team, onJoinClicked: (Int) -> Unit) { // 이 매개변수 추가
            with(binding) {
                tvName.text = item.name
                tvCategory.text = item.category
                tvIntroduce.text = item.introduce
                tvLink.text = item.link

                btnRegisterTeam.setOnClickListener { // 클릭 리스너 설정
                    onJoinClicked(item.teamId) // 팀 ID와 함께 함수 호출
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