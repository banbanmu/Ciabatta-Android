package com.example.risogelato.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.risogelato.R
import com.example.risogelato.data.remote.source.StoreDataSource
import com.example.risogelato.data.remote.source.StoreDataSourceImpl
import com.example.risogelato.domain.entity.Store
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

private const val PERMISSION_REQ_CODE = 1000

class MainActivity : AppCompatActivity() {

    private lateinit var storeDataSource: StoreDataSource
    private val disposables = CompositeDisposable()

    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private var store: Store? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storeDataSource = StoreDataSourceImpl(this)

        initBtnListener()
    }

    override fun onResume() {
        super.onResume()
        loadStore()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
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

    private fun loadStore() {
        Observable.fromCallable { storeDataSource.get() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                this.store = it

                txt_name.text = it.name
                txt_address.text = it.address
                txt_category.text = it.category.name

                list_menu.removeAllViews()
                it.menu.forEachIndexed { index, menu ->
                    val itemView = MainMenuItemView(this).apply {
                        bind(it, menu, index)
                    }

                    list_menu.addView(itemView)
                }

                initMenuListeners(it)
            }, Throwable::printStackTrace)
            .let { disposables.add(it) }
    }

    private fun initMenuListeners(store: Store) {
        btn_add_menu.setOnClickListener {
            navigateToAddMenu(store)
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
        ActivityCompat.requestPermissions(this, permissions,
            PERMISSION_REQ_CODE
        )
    }

    private fun navigateToLive() {
        store?.let {
            startActivity(LiveActivity.newIntent(this, it))
        }
    }

    private fun navigateToAddMenu(store: Store) {
        startActivity(MenuEditActivity.newIntent(this, store, null))
    }
}
