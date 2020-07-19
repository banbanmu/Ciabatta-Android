package com.example.risogelato.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.risogelato.R
import kotlinx.android.synthetic.main.text_fields_selectable.view.*

class SelectableTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var label: String = ""
    private var hint: String = ""

    var onFieldTouchListener: (() -> Unit)? = null

    init {
        View.inflate(context, R.layout.text_fields_selectable, this)

        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SelectableTextField,
            defStyleAttr,
            0
        )
        label = typedArray.getString(R.styleable.SelectableTextField_text_label) ?: ""
        hint = typedArray.getString(R.styleable.SelectableTextField_android_hint) ?: ""

        initViews()
    }

    private fun initViews() {
        label_textview.text = label
        txt_input.hint = hint

        box_input.setOnClickListener { onFieldTouchListener?.invoke() }
    }

    fun setText(text: String) {
        txt_input.setText(text)
        setError("")
    }

    fun getInputBox(): View? = box_input

    fun setError(error: String) {
        helper_textview.text = error
        helper_textview.setTextColor(ContextCompat.getColor(context, R.color.red100))
    }
}
