package com.example.risogelato.ui.component

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.ParcelCompat
import androidx.core.text.color
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.risogelato.R

open class InputTextField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    companion object {
        private const val HELPER_TEXT_MAX_LINES: Int = 2
    }

    private var count: Count = Count.None
        set(value) {
            field = value
            countTextview.text = value.toFormattedText()
        }

    protected open var limit: Int
        get() = count.limit
        set(value) {
            count = when {
                value < 1 -> Count.None
                else -> {
                    inputEdittext.filters = arrayOf(InputFilter.LengthFilter(value))
                    LimitedCount(text.length, value)
                }
            }
        }

    private var _text: String = ""
        set(value) {
            field = value
            inputEdittext.requestLayout()
            inputEdittext.invalidate()
        }

    protected open var text: String
        get() = _text
        set(value) {
            inputEdittext.setText(value)
        }

    protected open var hint: String = ""
        set(value) {
            field = value
            inputEdittext.hint = value
        }

    protected open var label: String = ""
        set(value) {
            field = value
            labelTextview.text = value
        }

    protected open var helper: String = ""
        set(value) {
            field = value
            refreshBottomGuideText()
            if (value.isNotEmpty()) {
                countTextview.visibility = View.GONE
            }
        }

    protected open var labelEmphasisMarkShown: Boolean = false
        set(value) {
            field = value
            asteriskImageview.isVisible = value
        }

    protected open var errorText: String = ""
        set(value) {
            field = value
            refreshBottomGuideText()
            if (value.isNotEmpty())
                isErrorState = true
        }

    protected open var isErrorState: Boolean = false
        set(value) {
            field = value
            (inputEdittext as? ErrorStateContainer)?.isErrorState = value
            refreshDrawableState()
            if (value.not())
                errorText = ""
        }

    protected var helperTextview: TextView
    protected var countTextview: TextView
    protected var labelTextview: TextView
    protected var inputEdittext: EditText
    protected var asteriskImageview: AppCompatImageView
    protected var trailingIconImageview: AppCompatImageView
    protected var inputContainerScrollView: ScrollView

    init {
        inflate(context, R.layout.text_fields_template, this)
        helperTextview = findViewById(R.id.helper_textview)
        inputEdittext = findViewById(R.id.input_edittext)
        countTextview = findViewById(R.id.count_textview)
        labelTextview = findViewById(R.id.label_textview)
        asteriskImageview = findViewById(R.id.asterisk_imageview)
        trailingIconImageview = findViewById(R.id.trailing_icon_imageview)
        inputContainerScrollView = findViewById(R.id.scroll_input_container)

        helperTextview.maxLines = HELPER_TEXT_MAX_LINES
        inputEdittext.addTextChangedListener(
            onTextChanged = { input: CharSequence?, _: Int, _: Int, _: Int ->
                input?.toString()?.let { changedText ->
                    _text = changedText
                    if (count is LimitedCount)
                        count = LimitedCount(changedText.length, limit)
                }
            }
        )
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        dispatchThawSelfOnly(container)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray =
        super.onCreateDrawableState(extraSpace + 1).also {
            if (isErrorState)
                mergeDrawableStates(it, intArrayOf(R.attr.state_error))
        }

    override fun onSaveInstanceState(): Parcelable? {
        super.onSaveInstanceState()
        val superState = super.onSaveInstanceState()
        return superState?.let { state ->
            SavedState(state).apply {
                inputText = text
                this.labelText = this@InputTextField.label
                labelEmphasisMarkShown = this@InputTextField.labelEmphasisMarkShown
                helperText = helper
                limit.takeIf { it > 0 }?.let { limitCount = it }
                this.error = this@InputTextField.isErrorState
                this.errorText = this@InputTextField.errorText
                this.hintText = this@InputTextField.hint
            }
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(null)
                setEnabled(isEnabled)
                state.limitCount?.let { limit = it }
                inputEdittext.setText(state.inputText)
                state.labelText?.toString()?.let { label = it }
                labelEmphasisMarkShown = state.labelEmphasisMarkShown
                state.helperText?.toString()?.let { helper = it }
                isErrorState = state.error
                state.errorText?.toString()?.let { errorText = it }
                state.hintText?.toString()?.let { hint = it }
            }
            else -> super.onRestoreInstanceState(state)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        inputEdittext.isEnabled = enabled
        super.setEnabled(enabled)
    }

    private fun refreshBottomGuideText() {
        helperTextview.text = errorText.takeIf(String::isNotEmpty) ?: helper
    }


    class SavedState : BaseSavedState {
        var inputText: CharSequence? = null
        var labelText: CharSequence? = null
        var labelEmphasisMarkShown: Boolean = false
        var helperText: CharSequence? = null
        var limitCount: Int? = null
        var error: Boolean = false
        var errorText: CharSequence? = null
        var hintText: CharSequence? = null

        constructor(superState: Parcelable) : super(superState)
        private constructor(source: Parcel) : super(source) {
            inputText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source)
            labelText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source)
            helperText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source)
            labelEmphasisMarkShown = ParcelCompat.readBoolean(source)
            limitCount = source.readInt().takeIf { it > 0 }
            error = ParcelCompat.readBoolean(source)
            errorText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source)
            hintText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            TextUtils.writeToParcel(inputText, out, flags)
            TextUtils.writeToParcel(labelText, out, flags)
            TextUtils.writeToParcel(helperText, out, flags)
            ParcelCompat.writeBoolean(out, labelEmphasisMarkShown)
            out.writeInt(limitCount ?: -1)
            ParcelCompat.writeBoolean(out, error)
            TextUtils.writeToParcel(errorText, out, flags)
            TextUtils.writeToParcel(hintText, out, flags)
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState = SavedState(source)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }


    private interface Count {
        val limit: Int
        fun toFormattedText(): CharSequence

        object None : Count {
            override val limit: Int = -1
            override fun toFormattedText(): CharSequence = ""
        }
    }

    private inner class LimitedCount(val current: Int, override val limit: Int) : Count {
        override fun toFormattedText(): CharSequence = SpannableStringBuilder()
            .color(
                ContextCompat.getColor(
                    context,
                    if (current == 0) R.color.grey150
                    else R.color.grey400
                )
            ) { append(current.toString()) }
            .append(String.format("%d자", limit))
    }
}
