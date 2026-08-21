package com.arquivoparanormal.app.data

import android.content.Context
import com.arquivoparanormal.app.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseBootstrap {
    fun initialize(context: Context): Boolean {
        if (FirebaseApp.getApps(context).isNotEmpty()) return true
        if (BuildConfig.FIREBASE_API_KEY.isBlank() ||
            BuildConfig.FIREBASE_APP_ID.isBlank() ||
            BuildConfig.FIREBASE_PROJECT_ID.isBlank()
        ) return false

        val builder = FirebaseOptions.Builder()
            .setApiKey(BuildConfig.FIREBASE_API_KEY)
            .setApplicationId(BuildConfig.FIREBASE_APP_ID)
            .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)

        if (BuildConfig.FIREBASE_MESSAGING_SENDER_ID.isNotBlank()) {
            builder.setGcmSenderId(BuildConfig.FIREBASE_MESSAGING_SENDER_ID)
        }
        if (BuildConfig.FIREBASE_STORAGE_BUCKET.isNotBlank()) {
            builder.setStorageBucket(BuildConfig.FIREBASE_STORAGE_BUCKET)
        }

        FirebaseApp.initializeApp(context, builder.build())
        return true
    }
}
