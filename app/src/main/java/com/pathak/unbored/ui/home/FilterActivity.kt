package com.pathak.unbored.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.pathak.unbored.R
import com.pathak.unbored.databinding.ActivityFilterBinding

class FilterActivity : AppCompatActivity() {

    companion object IntentStrings {
        const val typeKey = "type"
        const val participantsKey = "participants"
        const val priceKey = "price"
        const val accessibilityKey = "accessibility"
    }

    private lateinit var filterLayout: ActivityFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        filterLayout = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(filterLayout.root)

        val activityThatCalled = intent

        val callingBundle = activityThatCalled.extras

        if (callingBundle != null) {
            populateFilters(callingBundle)
        }

        filterLayout.saveBut.setOnClickListener {
            sendFilters()
        }
        filterLayout.cancelBut.setOnClickListener {
            finish()
        }

    }

    private fun populateFilters(calllingBundle: Bundle){
        filterLayout.typeET.setText(calllingBundle.getString(typeKey))
        filterLayout.participantsET.setText(calllingBundle.getString(participantsKey))
        filterLayout.priceET.setText(calllingBundle.getString(priceKey))
        filterLayout.accessibilityET.setText(calllingBundle.getString(accessibilityKey))
    }

    private fun sendFilters(){
        val type = filterLayout.typeET.text.toString()
        val participants = filterLayout.participantsET.text.toString()
        val price = filterLayout.priceET.text.toString()
        val accessibility = filterLayout.accessibilityET.text.toString()

        Log.d("XXX", "sendFilters: $type $participants $price $accessibility")

        val returnIntent = Intent().apply {
            putExtra(typeKey, type)
            putExtra(participantsKey, participants)
            putExtra(priceKey, price)
            putExtra(accessibilityKey, accessibility)
        }

        setResult(RESULT_OK, returnIntent)

        finish()
    }
}