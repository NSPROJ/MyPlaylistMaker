package com.example.myplaylistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var clearButton: ImageView
    private var savedText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val arrow2Button: ImageView = findViewById(R.id.arrow2)
        editText = findViewById(R.id.editText)
        clearButton = findViewById(R.id.clearButton)

        arrow2Button.setOnClickListener { finish() }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s?.isNotEmpty() == true) View.VISIBLE else View.INVISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        clearButton.setOnClickListener {
            editText.text.clear()
            hideKeyboard()
        }

        savedInstanceState?.let { it ->
            savedText = it.getString("savedText")
            savedText?.let { editText.setText(it) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("savedText", editText.text.toString())
        super.onSaveInstanceState(outState)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}