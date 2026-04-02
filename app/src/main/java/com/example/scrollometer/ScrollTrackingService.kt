package com.example.scrollometer

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ScrollTrackingService : AccessibilityService() {

    private var totalDistanceInches = 0.0f
    private var screenHeightPixels = 0
    private var screenDpi = 0
    private var lastEventTime: Long = 0
    private lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onServiceConnected() {
        super.onServiceConnected()
        val displayMetrics = resources.displayMetrics
        screenHeightPixels = displayMetrics.heightPixels
        screenDpi = displayMetrics.densityDpi
        localBroadcastManager = LocalBroadcastManager.getInstance(this)

        // Load the last saved distance when the service starts
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        totalDistanceInches = prefs.getFloat(KEY_DISTANCE, 0.0f)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Only count if tracking is globally enabled
        if (!isTrackingEnabled) {
            return
        }

        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastEventTime > 500) {
                lastEventTime = currentTime

                if (screenDpi > 0) {
                    val inchesScrolledThisEvent = screenHeightPixels.toFloat() / screenDpi
                    totalDistanceInches += inchesScrolledThisEvent

                    // Save the new distance
                    val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    with(prefs.edit()) {
                        putFloat(KEY_DISTANCE, totalDistanceInches)
                        apply()
                    }

                    // Send the broadcast
                    val intent = Intent(ACTION_UPDATE)
                    intent.putExtra(EXTRA_DISTANCE, totalDistanceInches)
                    localBroadcastManager.sendBroadcast(intent)

                }
            }
        }
    }

    override fun onInterrupt() {}

    companion object {
        // The switch to turn tracking on/off
        var isTrackingEnabled = false

        // Constants for broadcasts
        const val ACTION_UPDATE = "com.example.scrollometer.UPDATE"
        const val EXTRA_DISTANCE = "extra_distance"

        // Constants for saving data
        const val PREFS_NAME = "ScrollOMeterPrefs"
        const val KEY_DISTANCE = "total_distance_inches"
    }
}