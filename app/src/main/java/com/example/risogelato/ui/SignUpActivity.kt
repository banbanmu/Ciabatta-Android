package com.example.risogelato.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.risogelato.R
import com.example.risogelato.data.remote.source.UserDataSource
import com.example.risogelato.data.remote.source.UserDataSourceImpl
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var userDataSource: UserDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        userDataSource = UserDataSourceImpl(this)

        initBtnListener()
    }

    private fun initBtnListener() {
        btn_sign_up.setOnClickListener {
            val userName = input_name.text
            val password = input_password.text
            val phone = input_phone.text

            when {
                userName.isEmpty() -> {
                    input_name.errorText = "이름을 입력해주세요"
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    input_password.errorText = "비밀번호는 6자 이상 입력해주세요"
                    return@setOnClickListener
                }
                phone.isEmpty() -> {
                    input_phone.errorText = "휴대폰 번호를 입력해주세요"
                }
            }

            Observable.fromCallable { userDataSource.signUp(userName, password, phone) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    navigateToRegisterStore()
                }, {
                    it.printStackTrace()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })
        }
    }

    private fun navigateToRegisterStore() {
        startActivity(Intent(this, RegisterStoreActivity::class.java))
        ActivityCompat.finishAffinity(this)
    }
}
