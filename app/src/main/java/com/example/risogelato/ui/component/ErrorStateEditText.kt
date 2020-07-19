package com.example.risogelato.ui.component

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.risogelato.R

class ErrorStateEditText : AppCompatEditText, ErrorStateContainer {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override var isErrorState: Boolean = false
        set(value) {
            field = value
            refreshDrawableState()
        }

    override fun onCreateDrawableState(extraSpace: Int): IntArray =
            super.onCreateDrawableState(extraSpace + 1).also {
                if (isErrorState)
                    mergeDrawableStates(it, intArrayOf(R.attr.state_error))
            }
}
