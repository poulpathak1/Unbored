package com.pathak.unbored.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pathak.unbored.MainViewModel
import com.pathak.unbored.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val mainViewModel: MainViewModel by viewModels()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }

    private fun initAdapter(binding: FragmentDashboardBinding, listType: String) : ActivityListAdapter {
        val adapter = ActivityListAdapter(mainViewModel, this.requireActivity(), listType)
        //binding.favoritesRV.adapter = adapter
        return adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Dashboard"

        binding.acceptedRV.layoutManager = LinearLayoutManager(binding.acceptedRV.context)
        binding.favoritesRV.layoutManager = LinearLayoutManager(binding.favoritesRV.context)

        val acceptedAdapter = initAdapter(binding, "acceptedList")
        binding.acceptedRV.adapter = acceptedAdapter

        val adapter = initAdapter(binding, "favoritesList")
        binding.favoritesRV.adapter = adapter

        mainViewModel.fetchFavorites()
        mainViewModel.fetchAccepted()
        mainViewModel.observeAcceptedList().observe(viewLifecycleOwner) {
            acceptedAdapter.submitList(mainViewModel.observeAcceptedList().value)
        }
        mainViewModel.observeFavoritesList().observe(viewLifecycleOwner){
            adapter.submitList(mainViewModel.observeFavoritesList().value)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}