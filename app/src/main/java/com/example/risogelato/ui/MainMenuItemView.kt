package com.example.risogelato.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.risogelato.R
import com.example.risogelato.domain.entity.Menu
import com.example.risogelato.domain.entity.Store
import kotlinx.android.synthetic.main.view_main_menu_item.view.*

class MainMenuItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_main_menu_item, this)
    }

    fun bind(store: Store, menu: Menu, index: Int) {
        txt_name.text = menu.name
        txt_description.text = menu.description
        txt_price.text = String.format("%,d원", menu.price)

        btn_more.setOnClickListener {
            PopupMenu(context, it).run {
                this.menu.add("편집")
                setOnMenuItemClickListener {
                    context.startActivity(MenuEditActivity.newIntent(context, store, menu, index))
                    true
                }
                show()
            }
        }
    }
}
