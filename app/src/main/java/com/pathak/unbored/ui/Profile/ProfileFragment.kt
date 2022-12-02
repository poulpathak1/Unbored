package com.pathak.unbored.ui.Profile

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NavUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pathak.unbored.MainActivity
import com.pathak.unbored.MainViewModel
import com.pathak.unbored.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (Firebase.auth.currentUser == null){
            return root
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Profile"


        profileViewModel.updateUser()

        profileViewModel.observeDisplayName().observe(viewLifecycleOwner){
            binding.userName.setText(profileViewModel.observeDisplayName().value)
        }
        profileViewModel.observeEmail().observe(viewLifecycleOwner){
            binding.userEmail.setText(profileViewModel.observeEmail().value)
        }
        profileViewModel.observeUid().observe(viewLifecycleOwner){
            binding.userUid.text = profileViewModel.observeUid().value
        }

        binding.saveBut.setOnClickListener {
            val newUsername = binding.userName.text.toString()
            val newEmail = binding.userEmail.text.toString()
            viewModel.updateUser(newUsername, newEmail)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}