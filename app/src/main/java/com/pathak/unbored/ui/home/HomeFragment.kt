package com.pathak.unbored.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.pathak.unbored.FirestoreAuthLiveData
import com.pathak.unbored.MainViewModel
import com.pathak.unbored.R
import com.pathak.unbored.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()


    private var resultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Get the data that was returned, and display it
                //SSS
                val data: Intent? = result.data
                data?.extras?.apply {
                    getString(FilterActivity.typeKey)?.let {
                        Log.d("XXX", "type: $it ")
                        homeViewModel.setType(it) }
                    getFloat(FilterActivity.participantsKey)?.let {
                        Log.d("XXX", "participants: $it ")
                        homeViewModel.setParticipants(it)
                    }
                    getFloat(FilterActivity.minPriceKey)?.let {
                        Log.d("XXX", "minPrice: $it ")
                        homeViewModel.setMinPrice(it)
                    }
                    getFloat(FilterActivity.maxPriceKey)?.let {
                        Log.d("XXX", "maxPrice: $it ")
                        homeViewModel.setMaxPrice(it)
                    }
                    getFloat(FilterActivity.minAccessibilityKey)?.let {
                        Log.d("XXX", "minAxx: $it ")
                        homeViewModel.setMinAccessibility(it)
                    }
                    getFloat(FilterActivity.maxAccessibilityKey)?.let {
                        Log.d("XXX", "maxAxx: $it ")
                        homeViewModel.setMaxAccessibility(it)
                    }
                }
                binding.filterSwitch.isChecked = true
                binding.filterSwitch.text = "On"
                homeViewModel.filterActivity()
            } else {
                Log.w(javaClass.simpleName, "Bad activity return code ${result.resultCode}")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun doFilter() {
        val getFilterIntent = Intent(context, FilterActivity::class.java)

        getFilterIntent.putExtra(FilterActivity.typeKey, homeViewModel.observeType().value ?: "social")
        getFilterIntent.putExtra(FilterActivity.participantsKey, homeViewModel.observeParticipants().value?.toFloat() ?: 1.0f)
        getFilterIntent.putExtra(FilterActivity.minPriceKey, homeViewModel.observeMinPrice().value ?: 0.0f)
        getFilterIntent.putExtra(FilterActivity.maxPriceKey, homeViewModel.observeMaxPrice().value ?: 1.0f)
        getFilterIntent.putExtra(FilterActivity.minAccessibilityKey,homeViewModel.observeMinAccessibility().value ?: 0.0f)
        getFilterIntent.putExtra(FilterActivity.maxAccessibilityKey,homeViewModel.observeMaxAccessibility().value ?: 1.0f)

        getFilterIntent.putExtra(FilterActivity.priceKey, homeViewModel.observePrice().value ?: 0.0f)
        getFilterIntent.putExtra(FilterActivity.accessibilityKey, homeViewModel.observeAccessibility().value ?: 0.0f)
        resultLauncher.launch(getFilterIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Activity"


        binding.filterButton.setOnClickListener {
            doFilter()
        }
        binding.filterSwitch.setOnClickListener {
            if (binding.filterSwitch.isChecked){
                binding.filterSwitch.text = "On"
            }
            else {
                binding.filterSwitch.text = "Off"
            }
        }

        homeViewModel.observeBoredActivity().observe(viewLifecycleOwner) {
            binding.textHome.text = it.activity
            if (it.activity.isNullOrBlank()) binding.textHome.text = "No results found!"
            mainViewModel.fetchFavorites()
            val activityKey = homeViewModel.observeBoredActivity().value!!.key
            mainViewModel.favoritesList.value?.contains(activityKey)
                ?.let { it1 -> homeViewModel.setIsFavorite(it1) }
        }

        binding.cancelBut.setOnClickListener {
            if (binding.filterSwitch.isChecked){
                homeViewModel.filterActivity()
            }
            else {
                homeViewModel.netRefresh()
            }
        }
        binding.acceptBut.setOnClickListener {
            val activityKey = homeViewModel.observeBoredActivity().value?.activity
            val currentUser = firebaseAuthLiveData.getCurrentUser()
            if (currentUser == null) {
                Toast.makeText(context, "Please Login to start", Toast.LENGTH_SHORT).show()
            }
            else if ((mainViewModel.observeAcceptedList().value?.size ?: 0 < 3) && activityKey != null) {
                mainViewModel.addAccepted(activityKey)
                Toast.makeText(context, "New activity has been added to Activity Backlog",
                    Toast.LENGTH_SHORT).show()
                if (binding.filterSwitch.isChecked){
                    homeViewModel.filterActivity()
                }
                else {
                    homeViewModel.netRefresh()
                }
            }
            else {
                Toast.makeText(context, "Complete an activity from Dashboard first!",
                    Toast.LENGTH_SHORT).show()
            }
        }
        binding.favoriteBut.setOnClickListener {
            val activityKey = homeViewModel.observeBoredActivity().value?.activity
            if(mainViewModel.favoritesList.value?.contains(activityKey) == true){
                if (activityKey != null) {
                    mainViewModel.removeFavorite(activityKey)
                }
            }
            else{
                if (activityKey != null){
                    mainViewModel.addFavorite(activityKey)
                }
            }
        }
        mainViewModel.observeFavoritesList().observe(viewLifecycleOwner) {
            val activityKey = homeViewModel.observeBoredActivity().value!!.activity
            if(mainViewModel.favoritesList.value?.contains(activityKey) == true){
                binding.favoriteBut.setImageResource(R.drawable.ic_favorite_red_24dp)
            }
            else{
                binding.favoriteBut.setImageResource(R.drawable.ic_favorite_border_black_50dp)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}