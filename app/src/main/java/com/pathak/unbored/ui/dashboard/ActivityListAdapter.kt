package com.pathak.unbored.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.pathak.unbored.MainViewModel
import com.pathak.unbored.R
import com.pathak.unbored.databinding.RowActivityBinding

class ActivityListAdapter(private val mainViewModel: MainViewModel, fragmentActivity: FragmentActivity, private val listType: String):
    ListAdapter<String, ActivityListAdapter.VH>(BoredActivityDiff()){

    class BoredActivityDiff : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    inner class VH(private val rowActivityBinding: RowActivityBinding) :
        RecyclerView.ViewHolder(rowActivityBinding.root){
        fun bind(boredActivityKey: String){
            rowActivityBinding.activityText.text = boredActivityKey

            if (listType == "acceptedList"){
                rowActivityBinding.acceptBut.setImageResource(R.drawable.ic_baseline_check_circle_24)
                rowActivityBinding.cancelBut.setImageResource(R.drawable.ic_favorite_border_black_50dp)
                rowActivityBinding.acceptBut.setOnClickListener {
                    Snackbar.make(itemView, "Congratulations on completing the activity!",
                        Snackbar.LENGTH_SHORT).show()
                    mainViewModel.addTotalCompletedByUser()
                    mainViewModel.removeAccepted(boredActivityKey)
                    notifyDataSetChanged()
                }
                rowActivityBinding.cancelBut.setOnClickListener {
                    mainViewModel.addFavorite(boredActivityKey)
                    mainViewModel.removeAccepted(boredActivityKey)
                    notifyDataSetChanged()
                }
            }
            if (listType == "favoritesList") {
                rowActivityBinding.acceptBut.setImageResource(R.drawable.ic_baseline_move_24)
                rowActivityBinding.cancelBut.setImageResource(R.drawable.ic_baseline_cancel_24)
                rowActivityBinding.acceptBut.setOnClickListener {
                    mainViewModel.addAccepted(boredActivityKey)
                    mainViewModel.removeFavorite(boredActivityKey)
                    notifyDataSetChanged()
                }
                rowActivityBinding.cancelBut.setOnClickListener {
                    mainViewModel.removeFavorite(boredActivityKey)
                    notifyDataSetChanged()
                }
            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowActivityBinding = RowActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowActivityBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(currentList[position])
    }
}
