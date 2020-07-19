package com.example.risogelato.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import com.example.risogelato.R

class SingleLineInputTextField @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : InputTextField(context, attrs, defStyleAttr) {
    companion object {
        private const val INPUT_MAX_LINES: Int = 1
    }

    public override var limit: Int
        get() = super.limit
        set(value) { super.limit = value }

    public override var text: String
        get() = super.text
        set(value) { super.text = value }

    public override var hint: String
        get() = super.hint
        set(value) { super.hint = value }

    public override var label: String
        get() = super.label
        set(value) { super.label = value }

    public override var helper: String
        get() = super.helper
        set(value) { super.helper = value }

    public override var labelEmphasisMarkShown: Boolean
        get() = super.labelEmphasisMarkShown
        set(value) { super.labelEmphasisMarkShown = value }

    public override var errorText: String
        get() = super.errorText
        set(value) { super.errorText = value }

    public override var isErrorState: Boolean
        get() = super.isErrorState
        set(value) { super.isErrorState = value }

    init {
        inputEdittext.apply {
            maxLines = INPUT_MAX_LINES
            inputType = EditorInfo.TYPE_CLASS_TEXT
            setOnFocusChangeListener { _, _ ->
                setTrailingIconVisibility()
            }
            addTextChangedListener(
                    onTextChanged = { _: CharSequence?, _: Int, _: Int, _: Int ->
                        isErrorState = false
                        setTrailingIconVisibility()
                    }
            )
        }
        trailingIconImageview.apply {
            setImageResource(R.drawable.ico_vector_field_clear)
            setOnClickListener {
                inputEdittext.text?.clear()
            }
        }

        context.theme.obtainStyledAttributes(attrs, R.styleable.SingleLineInputTextField, defStyleAttr, 0).apply {
            try {
                getInt(R.styleable.SingleLineInputTextField_design_textLimitCount, 0).let { limit = it }
                getString(R.styleable.SingleLineInputTextField_design_text)?.let { text = it }
                getString(R.styleable.SingleLineInputTextField_design_label)?.let { label = it }
                getString(R.styleable.SingleLineInputTextField_design_helper)?.let { helper = it }
                getString(R.styleable.SingleLineInputTextField_android_hint)?.let { hint = it }
                labelEmphasisMarkShown = getBoolean(R.styleable.SingleLineInputTextField_design_showEmphasisMark, false)
            } finally {
                recycle()
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        setTrailingIconVisibility()
        super.setEnabled(enabled)
    }

    private fun setTrailingIconVisibility() {
        val visible: Boolean = inputEdittext.run {
            hasFocus() && isEnabled && text?.isNotEmpty() == true
        }
        trailingIconImageview.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
