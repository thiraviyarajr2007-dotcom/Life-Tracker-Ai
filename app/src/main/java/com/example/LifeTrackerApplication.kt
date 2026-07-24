package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

class LifeTrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase if not already initialized
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                try {
                    FirebaseApp.initializeApp(this)
                } catch (e: Exception) {
                    Log.w("LifeTrackerApplication", "Default google-services.json options not found, initializing with fallback FirebaseOptions: ${e.message}")
                    val fallbackOptions = FirebaseOptions.Builder()
                        .setApplicationId("1:100000000000:android:lifetrackerapp")
                        .setApiKey("AIzaSyDummyApiKeyForLifeTrackerInit")
                        .setProjectId("lifetracker-app")
                        .build()
                    FirebaseApp.initializeApp(this, fallbackOptions)
                }
                Log.d("LifeTrackerApplication", "Firebase successfully initialized.")
            }
        } catch (e: Exception) {
            Log.e("LifeTrackerApplication", "Failed to initialize FirebaseApp: ${e.message}", e)
        }

        // Configure Firestore with persistent offline caching for real-time data persistence
        if (FirebaseApp.getApps(this).isNotEmpty()) {
            try {
                val firestore = FirebaseFirestore.getInstance()
                val settings = FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(
                        PersistentCacheSettings.newBuilder().build()
                    )
                    .build()
                firestore.firestoreSettings = settings
                Log.d("LifeTrackerApplication", "Firestore configured with offline persistence settings.")
            } catch (e: Exception) {
                Log.e("LifeTrackerApplication", "Failed to configure Firestore settings: ${e.message}", e)
            }

            // Initialize Firebase Auth
            try {
                val auth = FirebaseAuth.getInstance()
                Log.d("LifeTrackerApplication", "Firebase Auth initialized. Current user: ${auth.currentUser?.email ?: "Guest"}")
            } catch (e: Exception) {
                Log.e("LifeTrackerApplication", "Failed to initialize Firebase Auth: ${e.message}", e)
            }
        }
    }
}

