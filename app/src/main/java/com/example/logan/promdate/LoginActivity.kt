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
import com.example.logan.promdate.data.DefaultResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //check if token exists; if it does, skips this activity
        checkToken()

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
        val apiAccessor = ApiAccessor()

        val call: Call<DefaultResponse> = apiAccessor.apiService.login(email, password)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful && response.body()?.status == 200) {
                    //successfully logged in; stores authentication token in file
                    File(this@LoginActivity.filesDir, "token.txt").writeText(response.body()?.result ?: "")

                    //opens up main feed
                    val mainFeedIntent = Intent(this@LoginActivity, MainFeedActivity::class.java)
                    startActivity(mainFeedIntent)
                    finish()
                } else {
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

    private fun checkToken() {
        //check if currently stored token works; if so, skips login and goes directly to main feed
        val tokenFile = File(this.filesDir, "token.txt")
        if (tokenFile.exists()) {
            val apiAccessor = ApiAccessor()

            val call = apiAccessor.apiService.regenToken(tokenFile.readText())
            call.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful && response.body()?.status == 200) {
                        //successfully logged in; stores authentication token in file
                        File(this@LoginActivity.filesDir, "token.txt").writeText(response.body()?.result
                                ?: "")

                        //opens up main feed
                        val mainFeedIntent = Intent(this@LoginActivity, MainFeedActivity::class.java)
                        startActivity(mainFeedIntent)
                        finish()
                    } else {
                        tokenFile.delete()
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    tokenFile.delete()
                }
            })
        }
    }
}
