package com.pathak.unbored.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.pathak.unbored.R
import com.pathak.unbored.databinding.ActivityFilterBinding

class FilterActivity : AppCompatActivity() {

    companion object IntentStrings {
        const val typeKey = "type"
        const val participantsKey = "participants"
        const val priceKey = "price"
        const val maxPriceKey = "maxPrice"
        const val minPriceKey = "minPrice"
        const val accessibilityKey = "accessibility"
        const val maxAccessibilityKey = "maxAccessibility"
        const val minAccessibilityKey = "minAccessibility"

    }

    private lateinit var filterLayout: ActivityFilterBinding
    private lateinit var spinner: Spinner
    private lateinit var adapter: ArrayAdapter<CharSequence>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        supportActionBar?.title = "Filter Activity"

        filterLayout = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(filterLayout.root)

        spinner = findViewById(R.id.spinner)
        adapter = ArrayAdapter.createFromResource(this, R.array.typeList,
            android.R.layout.simple_spinner_dropdown_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

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

    private fun populateFilters(callingBundle: Bundle){
        filterLayout.spinner.setSelection(adapter.getPosition(callingBundle.getString(typeKey)))
        Log.d("ZZZ", "populateFilters: ${callingBundle.getFloat(participantsKey)}")
        filterLayout.participantsSlider.value = callingBundle.getFloat(participantsKey)
        filterLayout.priceSlider.values[0] = callingBundle.getFloat(minPriceKey)
        filterLayout.priceSlider.values[1] = callingBundle.getFloat(maxPriceKey)
        filterLayout.accessibilitySlider.values[0] = callingBundle.getFloat(minAccessibilityKey)
        filterLayout.accessibilitySlider.values[1] = callingBundle.getFloat(maxAccessibilityKey)

        filterLayout.priceLabel.text = "Price : "
        filterLayout.accessibilityLabel.text = "Accessibility : "
    }

    private fun sendFilters(){
        val type = filterLayout.spinner.selectedItem.toString()
        val participants = filterLayout.participantsSlider.value
        val minPrice = filterLayout.priceSlider.values[0]
        val maxPrice = filterLayout.priceSlider.values[1]
        val minAccessibility = filterLayout.accessibilitySlider.values[0]
        val maxAccessibility = filterLayout.accessibilitySlider.values[1]

        val returnIntent = Intent().apply {
            putExtra(typeKey, type)
            putExtra(participantsKey, participants)
            putExtra(minPriceKey, minPrice)
            putExtra(maxPriceKey, maxPrice)
            putExtra(minAccessibilityKey, minAccessibility)
            putExtra(maxAccessibilityKey, maxAccessibility)
        }

        setResult(RESULT_OK, returnIntent)

        finish()
    }
}