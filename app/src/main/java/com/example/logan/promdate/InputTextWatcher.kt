package com.example.logan.promdate

import com.google.android.material.textfield.TextInputLayout
import android.text.Editable
import android.text.TextWatcher

class InputTextWatcher(private val parent: TextInputLayout) : TextWatcher {
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        parent.error = null
        parent.isErrorEnabled = false
    }

    //don't need these but have to override as it is an interface
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
}