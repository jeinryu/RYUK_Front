package org.techtown.ryuk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.ryuk.databinding.ItemMemberBinding
import org.techtown.ryuk.models.User

class MemberClassAdapter : RecyclerView.Adapter<MemberClassAdapter.ViewHolder>() {
    private var members: List<User> = listOf()

    fun submitList(membersList: List<User>) {
        members = membersList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount(): Int {
        return members.size
    }

    class ViewHolder(private val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: User) {
            binding.tvMemberNickname.text = "닉네임 = ${member.nickname}"
            binding.tvProgress.text = "진행률 = 1 / 2"
        }
    }
}