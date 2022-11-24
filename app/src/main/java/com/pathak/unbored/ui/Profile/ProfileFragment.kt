package com.pathak.unbored.ui.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.pathak.unbored.MainViewModel
import com.pathak.unbored.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        profileViewModel.updateUser()

        profileViewModel.observeDisplayName().observe(viewLifecycleOwner){
            binding.userName.setText(profileViewModel.observeDisplayName().value)
        }
        profileViewModel.observeEmail().observe(viewLifecycleOwner){
            binding.userEmail.setText(profileViewModel.observeEmail().value)
        }
        profileViewModel.observeUid().observe(viewLifecycleOwner){
            binding.userUid.setText(profileViewModel.observeUid().value)
        }

        binding.signOutBut.setOnClickListener {
            viewModel.signOut()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}