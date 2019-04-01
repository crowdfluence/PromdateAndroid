package com.example.logan.promdate

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.EditText
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar: Toolbar = findViewById(R.id.include)
        toolbar.title = getString(R.string.login)
        setSupportActionBar(toolbar)

        val signUpText = findViewById<TextView>(R.id.sign_in_text)
        signUpText.setOnClickListener {
            signUp(it)
        }
    }

    fun login(view: View) {
        val email = findViewById<EditText>(R.id.email_edit).text.toString()
        val password = findViewById<EditText>(R.id.password_edit).text.toString()
        val apiAccessor = APIAccessor()

        val call: Call<DefaultResponse> = apiAccessor.apiService.login(email, password)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    //successfully logged in; stores authentication token in file
                    val filename = "token"
                    val fileContents: String = response.body()?.result ?: ""
                    this@LoginActivity.openFileOutput(filename, Context.MODE_PRIVATE).use {
                        it.write(fileContents.toByteArray())
                    }

                    //opens up main feed
                    val mainFeedIntent = Intent(this@LoginActivity, MainFeedActivity::class.java)
                    startActivity(mainFeedIntent)
                }
                else {
                    Snackbar.make(findViewById(R.id.constraint_layout), R.string.failed_login,
                        Snackbar.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Snackbar.make(findViewById(R.id.constraint_layout), R.string.no_internet,
                    Snackbar.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun signUp(view: View) {
        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
    }
}
