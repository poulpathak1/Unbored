package com.pathak.unbored.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.pathak.unbored.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

    private var resultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Get the data that was returned, and display it
                //SSS
                val data: Intent? = result.data
                data?.extras?.apply {
                    getString(FilterActivity.typeKey)?.let {
                        homeViewModel.setType(it) }
                    getString(FilterActivity.participantsKey)?.let {
                        homeViewModel.setParticipants(
                            it.toInt())
                    }
                    getString(FilterActivity.priceKey)?.let {
                        homeViewModel.setPrice(
                            it.toDouble())
                    }
                    getString(FilterActivity.accessibilityKey)?.let {
                        homeViewModel.setAccessibility(
                            it.toDouble())
                    }
                }

                homeViewModel.filterActivity()
                //EEE // XXX Write me
            } else {
                Log.w(javaClass.simpleName, "Bad activity return code ${result.resultCode}")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        binding.filterButton.setOnClickListener {
            doFilter()
        }

        homeViewModel.observeBoredActivity().observe(viewLifecycleOwner) {
            binding.textHome.text = homeViewModel.observeBoredActivity().value?.activity ?: "No results found"
        }
        return binding.root
    }

    private fun doFilter() {
        val getFilterIntent = Intent(context, FilterActivity::class.java)

        val boredActivity = homeViewModel.observeBoredActivity().value

        getFilterIntent.putExtra(FilterActivity.typeKey, boredActivity?.type)
        getFilterIntent.putExtra(FilterActivity.participantsKey, boredActivity?.participants.toString())
        getFilterIntent.putExtra(FilterActivity.priceKey, boredActivity?.price.toString())
        getFilterIntent.putExtra(FilterActivity.accessibilityKey, boredActivity?.accessibility.toString())

        resultLauncher.launch(getFilterIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}