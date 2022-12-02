package com.pathak.unbored

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

// https://firebase.google.com/docs/auth/android/firebaseui
class AuthInit(viewModel: MainViewModel, signInLauncher: ActivityResultLauncher<Intent>) {
    companion object {
        private const val TAG = "AuthInit"
    }

    init {
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null) {
            Log.d(TAG, "XXX user null")
            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.AnonymousBuilder().build()
            )

            // Create and launch sign-in intent
            // XXX Write me. Set authentication providers and start sign-in for user
            // setIsSmartLockEnabled(false) solves some problems
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .enableAnonymousUsersAutoUpgrade()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setLogo(R.drawable.ic_baseline_check_circle_24)
                .build()
            signInLauncher.launch(signInIntent)
        } else {
            Log.d(TAG, "XXX user ${user.displayName} email ${user.email}")
            viewModel.updateUser()
        }
    }
}
