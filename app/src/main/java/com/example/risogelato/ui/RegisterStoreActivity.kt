package com.example.risogelato.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import com.example.risogelato.R
import com.example.risogelato.data.remote.source.CategoryDataSource
import com.example.risogelato.data.remote.source.CategoryDataSourceImpl
import com.example.risogelato.data.remote.source.StoreDataSource
import com.example.risogelato.data.remote.source.StoreDataSourceImpl
import com.example.risogelato.domain.entity.Category
import com.example.risogelato.domain.entity.Store
import com.example.risogelato.domain.entity.StoreRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_store.*

class RegisterStoreActivity : AppCompatActivity() {

    private lateinit var categoryDataSource: CategoryDataSource
    private lateinit var storeDataSource: StoreDataSource

    private var selectedCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_store)

        categoryDataSource = CategoryDataSourceImpl(this)
        storeDataSource = StoreDataSourceImpl(this)

        initViewListeners()
    }

    private fun initViewListeners() {
        input_category.onFieldTouchListener = {
            Observable.fromCallable { categoryDataSource.list() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showCategoryPopup(it)
                }, Throwable::printStackTrace)
        }

        btn_register.setOnClickListener {
            val name = input_name.text
            val address = input_address.text
            val phone = input_phone.text
            val category = selectedCategory ?: run {
                input_category.setError("업종을 선택해주세요")
                return@setOnClickListener
            }

            when {
                name.isEmpty() -> {
                    input_name.errorText = "가게 이름을 입력해주세요"
                    return@setOnClickListener
                }
                address.isEmpty() -> {
                    input_address.errorText = "가게 주소를 입력해주세요"
                    return@setOnClickListener
                }
                phone.isEmpty() -> {
                    input_phone.errorText = "가게 전화번호를 입력하세요"
                    return@setOnClickListener
                }
            }

            val store = StoreRequest(null, name, address, phone, category.key, emptyList())
            Observable.fromCallable { storeDataSource.register(store) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    navigateToMain()
                }, {
                    it.printStackTrace()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })
        }
    }

    private fun showCategoryPopup(categories: List<Category>) {
        input_category.getInputBox()?.let { inputView ->
            PopupMenu(this, inputView, Gravity.END).run {
                categories.forEach { menu.add(it.name) }

                setOnMenuItemClickListener { menuItem ->
                    val selectedCategory = categories.find { it.name == menuItem.title }
                        ?: return@setOnMenuItemClickListener true

                    this@RegisterStoreActivity.selectedCategory = selectedCategory
                    input_category.setText(selectedCategory.name)

                    true
                }

                show()
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        ActivityCompat.finishAffinity(this)
    }
}
