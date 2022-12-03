package com.pathak.unbored.ui.Profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pathak.unbored.MainViewModel
import com.pathak.unbored.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private fun initAdapter(
        binding: FragmentProfileBinding,
    ): LeaderboardListAdapter {
        return LeaderboardListAdapter(viewModel, requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Profile"

        binding.leaderboardRV.layoutManager = LinearLayoutManager(binding.leaderboardRV.context)

        val leaderboardAdapter = initAdapter(binding)
        binding.leaderboardRV.adapter = leaderboardAdapter

        viewModel.updateUser()

        viewModel.observeDisplayName().observe(viewLifecycleOwner){
            binding.userName.setText(viewModel.observeDisplayName().value)
        }
        viewModel.observeEmail().observe(viewLifecycleOwner){
            binding.userEmail.setText(viewModel.observeEmail().value)
        }
        viewModel.observeUid().observe(viewLifecycleOwner){
            binding.userUid.text = viewModel.observeUid().value
        }

        viewModel.observeLeaderboardList().observe(viewLifecycleOwner) {
            leaderboardAdapter.submitList(it)
        }

        binding.saveBut.setOnClickListener {
            val newUsername = binding.userName.text.toString().trim()
            val newEmail = binding.userEmail.text.toString().trim()
            if (viewModel.observeIsAnonymous().value == true) {
                val newPass = binding.userNewPassword.text.toString()
                val confirmPass = binding.userConfirmPassword.text.toString()

                if(newPass == confirmPass && newEmail.isNotBlank() && confirmPass.isNotBlank()){
                    viewModel.createPermanentAuth(newEmail, confirmPass) {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                context, "Account created Successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            if (newUsername.isNotBlank() && newUsername != viewModel.observeDisplayName().value) {
                                viewModel.updateEmail(newEmail) { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "UserName Updated!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            Snackbar.make(
                                view, "Bad Request!",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                else{
                    Snackbar.make(view, "Please check email and password",
                        Snackbar.LENGTH_LONG).show()
                }
            }
            else {
                if (newUsername.isNotBlank() && newUsername != viewModel.observeDisplayName().value){
                    viewModel.updateUserName(newUsername) {
                        if (it.isSuccessful){
                            Toast.makeText(context, "UserName Updated!", Toast.LENGTH_SHORT).show()
                            if (newEmail.isNotBlank() && newEmail != viewModel.observeEmail().value) {
                                viewModel.updateEmail(newEmail) { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Email Updated!", Toast.LENGTH_SHORT).show()
                                    }
                                    else {

                                    }
                                }
                            }
                        } else {

                        }
                    }
                }

            }
        }

        viewModel.observeLeaderboardList().observe(viewLifecycleOwner) {
            Log.d("DDD", "$it")
        }

        viewModel.fetchTotalCompletedByUser()
        viewModel.observeTotalCompletedByUser().observe(viewLifecycleOwner) {
            binding.userTotalCompletedByUser.text = it.toString()
        }

        viewModel.observeIsAnonymous().observe(viewLifecycleOwner) {
            if (it == true) {
                binding.newPasswordLayout.visibility = View.VISIBLE
                binding.confirmPasswordLayout.visibility = View.VISIBLE
                binding.saveBut.text = "Register"
            }
            else {
                binding.newPasswordLayout.visibility = View.GONE
                binding.confirmPasswordLayout.visibility = View.GONE
                binding.saveBut.text = "Update"
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}