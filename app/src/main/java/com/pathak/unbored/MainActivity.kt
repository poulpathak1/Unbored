package com.pathak.unbored

import android.app.ActionBar
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent.ColorScheme
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pathak.unbored.databinding.ActivityMainBinding
import com.pathak.unbored.ui.home.HomeViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //private var actionBarBinding: ActionBarBinding? = null


    private val viewModel: MainViewModel by viewModels()
    private lateinit var auth: FirebaseAuth


//    private val signInLauncher =
//        registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
//            result ->
//            if (result.resultCode ==Activity.RESULT_OK) {
//                viewModel.updateUser()
//            }
//            else {
//                Log.d("PPP", "sign in failed $result ")
//            }
//        }

    val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.updateUser()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.d("MainActivity", "sign in failed ${result}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_signOut -> {
            viewModel.signOut()
            AuthInit(viewModel, signInLauncher)
            true
        }
//        R.id.action_login -> {
//            AuthInit(viewModel, signInLauncher)
//            true
//        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //auth = Firebase.auth
        AuthInit(viewModel, signInLauncher)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.action_toolbar))


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView.setBackgroundColor(titleColor)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }

//    private fun initActionBar(actionBar: androidx.appcompat.app.ActionBar) {
//        // Disable the default and enable the custom
//        actionBar.setDisplayShowTitleEnabled(false)
//        actionBar.setDisplayShowCustomEnabled(true)
//        actionBarBinding = ActionBarBinding.inflate(layoutInflater)
//        // Apply the custom view
//        actionBar.customView = actionBarBinding?.root
//    }

    override fun onStart() {
        super.onStart()
//        val currentUser = auth.currentUser
//        if (currentUser == null) {
//            signInAnonymously()
//        }

    }

    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("XXX", "signInAnonymously:success")
                    val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("XXX", "signInAnonymously:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }
}