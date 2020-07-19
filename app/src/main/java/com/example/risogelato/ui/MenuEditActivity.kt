package com.example.risogelato.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.risogelato.R
import com.example.risogelato.data.remote.source.StoreDataSource
import com.example.risogelato.data.remote.source.StoreDataSourceImpl
import com.example.risogelato.domain.entity.Menu
import com.example.risogelato.domain.entity.Store
import com.example.risogelato.domain.entity.StoreRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_menu_edit.*

private const val STORE_INTENT_KEY = "STORE_INTENT_KEY"
private const val MENU_INTENT_KEY = "MENU_INTENT_KEY"
private const val MENU_INDEX_INTENT_KEY = "MENU_INDEX_INTENT_KEY"

class MenuEditActivity : AppCompatActivity() {

    private lateinit var storeDataSource: StoreDataSource

    private lateinit var store: Store
    private var menu: Menu? = null
    private var menuIndex = -1

    companion object {

        fun newIntent(context: Context, store: Store, menu: Menu?, index: Int = -1): Intent {
            return Intent(context, MenuEditActivity::class.java)
                .putExtra(STORE_INTENT_KEY, store)
                .putExtra(MENU_INTENT_KEY, menu)
                .putExtra(MENU_INDEX_INTENT_KEY, index)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_edit)

        storeDataSource = StoreDataSourceImpl(this)

        intent.getParcelableExtra<Store?>(STORE_INTENT_KEY)
            ?.also { this.store = it }
            ?: run {
                finish()
            }

        menu = intent.getParcelableExtra(MENU_INTENT_KEY)
        menuIndex = intent.getIntExtra(MENU_INDEX_INTENT_KEY, -1)

        initText()
        initBtnListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MenuEditActivity", "onDestroy")
    }

    private fun initText() {
        btn_delete.isVisible = menu != null
        menu?.let {
            toolbar_title.text = "메뉴 수정"
            input_name.text = it.name
            input_description.text = it.description
            input_price.text = it.price.toString()
        } ?: run {
            toolbar_title.text = "메뉴 추가"
        }

        toolbar_close.setOnClickListener {
            finish()
        }
    }

    private fun initBtnListeners() {
        btn_save.setOnClickListener {
            val name = input_name.text
            val description = input_description.text
            val price = input_price.text

            when {
                name.isEmpty() -> {
                    input_name.errorText = "메뉴 이름을 입력해주세요"
                    return@setOnClickListener
                }
                description.isEmpty() -> {
                    input_description.errorText = "메뉴 설명을 입력해주세요"
                    return@setOnClickListener
                }
                price.isEmpty() -> {
                    input_price.errorText = "가격을 입력해주세요"
                    return@setOnClickListener
                }
            }

            val menuList = store.menu.toMutableList()
            val newMenu = Menu(name, price.toInt(), description, null)

            menu?.let {
                if (menuIndex >= 0) menuList[menuIndex] = newMenu
            } ?: run {
                menuList.add(newMenu)
            }

            val store = StoreRequest(
                store.id,
                store.name,
                store.address,
                store.phone,
                store.category.key,
                menuList
            )
            Observable.fromCallable { storeDataSource.update(store) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show()
                    finish()
                }, {
                    it.printStackTrace()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })
        }

        btn_delete.setOnClickListener {
            val menuList = store.menu.toMutableList()
            menu?.let {
                if (menuIndex > 0) menuList.removeAt(menuIndex)
            }

            val store = StoreRequest(
                store.id,
                store.name,
                store.address,
                store.phone,
                store.category.key,
                menuList
            )
            Observable.fromCallable { storeDataSource.update(store) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Toast.makeText(this, "삭제되었습니다", Toast.LENGTH_SHORT).show()
                    finish()
                }, {
                    it.printStackTrace()
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                })
        }
    }
}
