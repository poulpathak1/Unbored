package com.pathak.unbored.ui.Profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pathak.unbored.MainViewModel
import com.pathak.unbored.databinding.LeaderboardRowBinding

class LeaderboardListAdapter(private val mainViewModel: MainViewModel, fragmentActivity: FragmentActivity):
    ListAdapter<String, LeaderboardListAdapter.VH>(BoredActivityDiff()){

    class BoredActivityDiff : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    inner class VH(private val leaderboardRowBinding: LeaderboardRowBinding) :
        RecyclerView.ViewHolder(leaderboardRowBinding.root){
        fun bind(userScore: String){
            leaderboardRowBinding.scoreText.text = userScore
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val leaderboardRowBinding = LeaderboardRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return VH(leaderboardRowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(currentList[position])
    }
}
