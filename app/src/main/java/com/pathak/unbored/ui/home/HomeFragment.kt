package com.pathak.unbored.ui.home

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pathak.unbored.MainViewModel
import com.pathak.unbored.R
import com.pathak.unbored.databinding.FragmentHomeBinding
import com.pathak.unbored.ui.dashboard.DashboardFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by viewModels()

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
//        val homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

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

        homeViewModel.observeBoredActivity().observe(viewLifecycleOwner) {
            binding.textHome.text = it.activity
            if (it.activity.isNullOrBlank()) binding.textHome.text = "No results found!"
            mainViewModel.fetchFavorites()
            val activityKey = homeViewModel.observeBoredActivity().value!!.key
            mainViewModel.favoritesList.value?.contains(activityKey)
                ?.let { it1 -> homeViewModel.setIsFavorite(it1) }
        }
//        homeViewModel.observeIsFavorite().observe(viewLifecycleOwner) {
//            if (it == true) binding.favoriteBut.setImageResource(R.drawable.ic_favorite_red_24dp)
//            else binding.favoriteBut.setImageResource(R.drawable.ic_favorite_border_black_50dp)
//        }

        binding.cancelBut.setOnClickListener {
            homeViewModel.filterActivity()
        }
        binding.acceptBut.setOnClickListener {
            val activityKey = homeViewModel.observeBoredActivity().value?.activity
            if (Firebase.auth.currentUser == null) {
                Snackbar.make(view, "Please Login to start", Snackbar.LENGTH_LONG).show()
            }
            else if ((mainViewModel.acceptedList.value?.size?:0 < 3) && activityKey != null) {
                mainViewModel.addAccepted(activityKey)
                Snackbar.make(view, "New activity has been added to Activity Backlog", Snackbar.LENGTH_LONG).show()
                homeViewModel.filterActivity()
            }
            else {
                Snackbar.make(view, "Complete an activity from Dashboard first!", Snackbar.LENGTH_LONG).show()
            }
        }
        binding.favoriteBut.setOnClickListener {
            val activityKey = homeViewModel.observeBoredActivity().value?.activity
            if(mainViewModel.favoritesList.value?.contains(activityKey) == true){
                //homeViewModel.setIsFavorite(false)
                if (activityKey != null) {
                    mainViewModel.removeFavorite(activityKey)

                }
            }
            else{
                //homeViewModel.setIsFavorite(true)
                if (activityKey != null){
                    mainViewModel.addFavorite(activityKey)
                }
            }
        }
        mainViewModel.observeFavoritesList().observe(viewLifecycleOwner) {
            val activityKey = homeViewModel.observeBoredActivity().value!!.activity
            if(mainViewModel.favoritesList.value?.contains(activityKey) == true){
                binding.favoriteBut.setImageResource(R.drawable.ic_favorite_red_24dp)
                //homeViewModel.setIsFavorite(true)
                //mainViewModel.removeFavorite(activityKey)
            }
            else{
                binding.favoriteBut.setImageResource(R.drawable.ic_favorite_border_black_50dp)
                //homeViewModel.setIsFavorite(false)
                //mainViewModel.addFavorite(activityKey)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}