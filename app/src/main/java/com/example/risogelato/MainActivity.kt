package com.example.risogelato

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.risogelato.ui.LiveActivity
import kotlinx.android.synthetic.main.activity_main.*

private const val PERMISSION_REQ_CODE = 1000

class MainActivity : AppCompatActivity() {

    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBtnListener()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                navigateToLive()
            } else {
                Toast.makeText(this, "필수 권한에 동의해주세요", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun initBtnListener() {
        btn_start_broadcast.setOnClickListener {
            checkPermissions { navigateToLive() }
        }
    }

    private fun checkPermissions(onGranted: () -> Unit) {
        val granted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (granted) onGranted()
        else requestPermissions()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQ_CODE)
    }

    private fun navigateToLive() {
        startActivity(Intent(this, LiveActivity::class.java))
    }
}