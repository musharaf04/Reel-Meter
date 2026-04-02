package com.example.scrollometer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

// Data class to hold our landmark information, including the drawable resource ID
data class Landmark(val name: String, val heightInMeters: Double, val drawableResId: Int?)

class MainActivity : AppCompatActivity() {

    // Updated landmarkLevels with drawable resource IDs (null for now if you don't have images)
//    private val landmarkLevels = listOf(
//        // IMPORTANT: For each landmark, you need to create a cartoon PNG image in your `drawable` folder.
//        // Replace `R.drawable.your_image_name` with the actual name of your PNG file.
//        // If you don't have an image yet, you can leave it as `null`.
//        Landmark("Giraffe", 5.5, R.drawable.giraffie), // Make sure you have giraffe_cartoon.png
//        Landmark("Two-Story Building", 6.0, null),
//        Landmark("Brachiosaurus", 13.0, null),
//        Landmark("Hollywood Sign", 14.0, null),
//        Landmark("Leaning Tower of Pisa", 57.0, null),
//        Landmark("Statue of Liberty", 93.0, null),
//        Landmark("Big Ben", 96.0, null),
//        Landmark("Great Pyramid of Giza", 139.0, null),
//        Landmark("Eiffel Tower", 330.0, null),
//        Landmark("Empire State Building", 443.0, null),
//        Landmark("Burj Khalifa", 828.0, null)
//    ).sortedBy { it.heightInMeters } // Ensure it's sorted by height
    private val landmarkLevels = listOf(
        Landmark("Ostrich", 2.7, R.drawable.ostrich),
        Landmark("African Elephant", 3.3, R.drawable.african_elephant),
        Landmark("Giraffe", 5.5, R.drawable.giraffe),
        Landmark("Tyrannosaurus Rex", 6.0, R.drawable.t_rex),
        Landmark("Telephone Pole", 11.0, R.drawable.telephone_pole),
        Landmark("Colosseum in Rome", 48.0, R.drawable.colosseum),
        Landmark("Leaning Tower of Pisa", 57.0, R.drawable.pisa_tower),
        Landmark("Taj Mahal", 73.0, R.drawable.taj_mahal),
        Landmark("Statue of Liberty", 93.0, R.drawable.statue_of_liberty),
        Landmark("Big Ben Clock Tower", 96.0, R.drawable.big_ben),
        Landmark("Great Pyramid of Giza", 139.0, R.drawable.giza_pyramid),
        Landmark("Washington Monument", 169.0, R.drawable.washington_monument),
        Landmark("Gateway Arch", 192.0, R.drawable.gateway_arch),
        Landmark("Eiffel Tower", 330.0, R.drawable.eiffel_tower),
        Landmark("Empire State Building", 443.0, R.drawable.empire_state_building),
        Landmark("One World Trade Center", 541.0, R.drawable.one_world_trade),
        Landmark("Shanghai Tower", 632.0, R.drawable.shanghai_tower),
        Landmark("Burj Khalifa", 828.0, R.drawable.burj_khalifa),
        Landmark("Angel Falls", 979.0, R.drawable.angel_falls),
        Landmark("Mount Fuji", 3776.0, R.drawable.mount_fuji),
        Landmark("Mount Everest", 8849.0, R.drawable.mount_everest)
    ).sortedBy { it.heightInMeters }

    private lateinit var tvDistance: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvFunFact: TextView
    private lateinit var btnToggleTracking: Button
    private lateinit var varIvProgressImage: ProgressImageView // Changed to our custom view type
    private lateinit var localBroadcastManager: LocalBroadcastManager

    private val distanceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ScrollTrackingService.ACTION_UPDATE) {
                val distanceInches = intent.getFloatExtra(ScrollTrackingService.EXTRA_DISTANCE, 0f)
                updateDistanceUI(distanceInches)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        tvDistance = findViewById(R.id.tvDistance)
        tvStatus = findViewById(R.id.tvStatus)
        tvFunFact = findViewById(R.id.tvFunFact)
        btnToggleTracking = findViewById(R.id.btnToggleTracking)
        varIvProgressImage = findViewById(R.id.ivProgressImage) // Initialize our custom image view
        val tvEnableServiceLink: TextView = findViewById(R.id.tvEnableServiceLink)

        btnToggleTracking.setOnClickListener {
            ScrollTrackingService.isTrackingEnabled = !ScrollTrackingService.isTrackingEnabled
            updateButtonAndStatus()
        }

        tvEnableServiceLink.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        localBroadcastManager.registerReceiver(distanceReceiver, IntentFilter(ScrollTrackingService.ACTION_UPDATE))
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences(ScrollTrackingService.PREFS_NAME, Context.MODE_PRIVATE)
        val savedDistance = prefs.getFloat(ScrollTrackingService.KEY_DISTANCE, 0.0f)
        updateDistanceUI(savedDistance)
        updateButtonAndStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        localBroadcastManager.unregisterReceiver(distanceReceiver)
    }

    private fun updateButtonAndStatus() {
        if (ScrollTrackingService.isTrackingEnabled) {
            btnToggleTracking.text = "Stop Tracking"
            tvStatus.text = "TRACKING ACTIVE"
        } else {
            btnToggleTracking.text = "Start Tracking"
            tvStatus.text = "TRACKING PAUSED"
        }
    }

    private fun updateDistanceUI(distanceInches: Float) {
        val distanceMeters = distanceInches * 0.0254f
        val distanceText: String

        if (distanceMeters < 1000) {
            distanceText = "%.1f m".format(distanceMeters)
        } else {
            val distanceKm = distanceMeters / 1000
            distanceText = "%.2f km".format(distanceKm)
        }
        tvDistance.text = distanceText

        val nextGoal = landmarkLevels.find { it.heightInMeters > distanceMeters }

        if (nextGoal != null) {
            val percentage = (distanceMeters / nextGoal.heightInMeters * 100).toInt().coerceIn(0, 100)
            tvFunFact.text = "That's $percentage% of the way to the top of the ${nextGoal.name}!"

            // NEW: Load the image and update progress
            nextGoal.drawableResId?.let {
                varIvProgressImage.setImageResource(it) // Set the image
                varIvProgressImage.setProgress(percentage) // Update progress on our custom view
            } ?: run {
                varIvProgressImage.setImageDrawable(null) // Clear image if no drawable for landmark
                varIvProgressImage.setProgress(0) // Reset progress
            }

        } else if (distanceMeters > 0) {
            tvFunFact.text = "You've passed all landmarks. You are a scrolling legend!"
            // If all landmarks passed, maybe show a "trophy" image or clear the progress image
            varIvProgressImage.setImageDrawable(null)
            varIvProgressImage.setProgress(0)
        } else {
            tvFunFact.text = "Let's start scrolling!"
            varIvProgressImage.setImageDrawable(null)
            varIvProgressImage.setProgress(0)
        }
    }
}