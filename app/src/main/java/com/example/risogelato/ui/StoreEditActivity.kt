package com.example.risogelato.ui

import android.content.Context
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

private const val STORE_INTENT_KEY = "STORE_INTENT_KEY"

class StoreEditActivity : AppCompatActivity() {

    private lateinit var categoryDataSource: CategoryDataSource
    private lateinit var storeDataSource: StoreDataSource
    private lateinit var store: Store

    private var selectedCategory: Category? = null

    companion object {

        fun newIntent(context: Context, store: Store): Intent {
            return Intent(context, StoreEditActivity::class.java)
                .putExtra(STORE_INTENT_KEY, store)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_store)

        categoryDataSource = CategoryDataSourceImpl(this)
        storeDataSource = StoreDataSourceImpl(this)

        intent.getParcelableExtra<Store?>(STORE_INTENT_KEY)?.also {
            this.store = it
        } ?: run { finish() }

        loadStore()
        initViewListeners()
    }

    private fun loadStore() {
        input_name.text = store.name
        input_address.text = store.address
        input_phone.text = store.phone
        input_category.setText(store.category.name)

        selectedCategory = store.category
    }

    private fun initViewListeners() {
        toolbar_close.setOnClickListener { finish() }

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

            val store = StoreRequest(store.id, name, address, phone, category.key, store.menu)
            Observable.fromCallable { storeDataSource.update(store) }
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

                    this@StoreEditActivity.selectedCategory = selectedCategory
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
