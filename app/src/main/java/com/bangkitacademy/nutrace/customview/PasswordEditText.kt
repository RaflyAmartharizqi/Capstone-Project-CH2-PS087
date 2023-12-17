package com.bangkitacademy.nutrace.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.bangkitacademy.nutrace.R

class PasswordEditText: AppCompatEditText {

    private var isPassValid: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(onTextChanged = { _: CharSequence?, _: Int, _: Int, _: Int ->
            val pass = text?.trim()
            when {
                pass.isNullOrEmpty() -> {
                    isPassValid = false
                    error = resources.getString(R.string.input_pass)
                }
                pass.length < 8 -> {
                    isPassValid = false
                    error = resources.getString(R.string.pass_length)
                }
                else -> {
                    isPassValid = true
                    error = null
                }
            }
        })
    }

}