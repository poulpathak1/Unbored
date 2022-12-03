package com.pathak.unbored.ui.Profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pathak.unbored.FirestoreAuthLiveData
import com.pathak.unbored.MainViewModel
import com.pathak.unbored.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

        binding.saveBut.setOnClickListener {
            val newUsername = binding.userName.text.toString().trim()
            val newEmail = binding.userEmail.text.toString().trim()
            if (FirebaseAuth.getInstance().currentUser?.isAnonymous == true) {
                val newPass = binding.userNewPassword.text.toString()
                val confirmPass = binding.userConfirmPassword.text.toString()

                if(newPass == confirmPass && !newEmail.isNullOrBlank()){
                    viewModel.createPermanentAuth(newEmail, confirmPass) {
                        if (it.isSuccessful) {
                            Snackbar.make(
                                view, "Account created Successfully",
                                Snackbar.LENGTH_LONG
                            ).show()
                            viewModel.updateUser()
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
                viewModel.updateUser(newUsername, newEmail)
                Snackbar.make(view, "Update Successful",
                    Snackbar.LENGTH_LONG).show()
            }
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