package com.example.risogelato.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.text.bold
import androidx.core.text.underline
import com.example.risogelato.R
import com.example.risogelato.data.remote.source.UserDataSource
import com.example.risogelato.data.remote.source.UserDataSourceImpl
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var userDataSource: UserDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        userDataSource = UserDataSourceImpl(this)

        initBtnSignUp()
        initInputListeners()
    }

    private fun initBtnSignUp() {
        btn_sign_up.text = SpannableStringBuilder()
            .append("Ciabatta가 처음이시라면?")
            .append(" ")
            .underline { bold { append("회원가입") } }

        btn_sign_up.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun initInputListeners() {
        btn_sign_in.setOnClickListener {
            val userName = input_name.text
            val password = input_password.text

            when {
                userName.isEmpty() -> {
                    input_name.errorText = "이름을 입력해주세요"
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    input_password.errorText = "비밀번호를 입력해주세요"
                    return@setOnClickListener
                }
            }

            Observable.fromCallable { userDataSource.signIn(userName, password) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    navigateToMain()
                }, {
                    it.printStackTrace()
                    Toast.makeText(this, "로그인에 실패했습니다", Toast.LENGTH_SHORT).show()
                })
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        ActivityCompat.finishAffinity(this)
    }
}
