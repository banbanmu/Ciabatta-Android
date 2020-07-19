package com.example.risogelato.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.risogelato.R
import com.example.risogelato.common.IS_LOGGED_IN
import com.example.risogelato.data.local.SharedPreferencesManager

class SplashActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            SharedPreferencesManager(this)

        setContentView(R.layout.activity_splash)
        Handler().postDelayed(this::navigate, 1500L)
    }

    private fun navigate() {
        if (sharedPreferences.getBoolean(IS_LOGGED_IN, false)) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        ActivityCompat.finishAffinity(this)
    }
}
