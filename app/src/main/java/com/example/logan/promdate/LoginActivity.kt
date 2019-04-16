package com.example.logan.promdate

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.Group
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.example.logan.promdate.data.DefaultResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        checkToken()
    }

    fun login(view: View) {
        val email = findViewById<EditText>(R.id.email_edit).text.toString()
        val password = findViewById<EditText>(R.id.password_edit).text.toString()
        val apiAccessor = ApiAccessor()

        val loadingAnim = findViewById<ProgressBar>(R.id.loading_pb)
        loadingAnim.visibility = View.VISIBLE

        val call: Call<DefaultResponse> = apiAccessor.apiService.login(email, password)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful && response.body()?.status == 200) {
                    loadingAnim.visibility = View.GONE
                    //successfully logged in; stores authentication token in file
                    val sp: SharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE)
                    sp.edit().putString("token", response.body()?.result).apply()

                    //opens up main feed
                    val mainFeedIntent = Intent(this@LoginActivity, MainFeedActivity::class.java)
                    startActivity(mainFeedIntent)
                    finish()
                } else {
                    Snackbar.make(findViewById(R.id.constraint_layout), R.string.failed_login,
                            Snackbar.LENGTH_LONG)
                            .show()

                    loadingAnim.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Snackbar.make(findViewById(R.id.constraint_layout), R.string.no_internet,
                        Snackbar.LENGTH_LONG)
                        .show()

                loadingAnim.visibility = View.GONE
            }
        })
    }

    private fun signUp(view: View) {
        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
    }

    private fun checkToken() {
        //check if currently stored token works; if so, skips login and goes directly to main feed
        val sp: SharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE)
        val token = sp.getString("token", null)
        if (token != null) {
            val apiAccessor = ApiAccessor()

            val loadingAnim = findViewById<ProgressBar>(R.id.loading_pb)
            loadingAnim.visibility = View.VISIBLE

            val call = apiAccessor.apiService.regenToken(token)
            call.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful && response.body()?.status == 200) {
                        //token still works; stores newly generated token in file and starts main feed activity
                        sp.edit().putString("token", response.body()?.result).apply()

                        //opens up main feed
                        val mainFeedIntent = Intent(this@LoginActivity, MainFeedActivity::class.java)
                        startActivity(mainFeedIntent)
                        finish()
                    } else {
                        Log.d("CheckToken", "${response.body()?.status ?: ""}, ${response.body()?.result ?: ""}")
                        sp.edit().putString("token", null).apply()

                        //sets up normal layout if token doesn't work
                        loadingAnim.visibility = View.GONE

                        val toolbar: Toolbar = findViewById(R.id.include)
                        toolbar.title = getString(R.string.login)
                        setSupportActionBar(toolbar)

                        val signUpText = findViewById<TextView>(R.id.sign_in_text)
                        signUpText.setOnClickListener {
                            signUp(it)
                        }

                        findViewById<Group>(R.id.blank_group).visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {

                    //sets up normal layout if token doesn't work
                    loadingAnim.visibility = View.GONE

                    val toolbar: Toolbar = findViewById(R.id.include)
                    toolbar.title = getString(R.string.login)
                    setSupportActionBar(toolbar)

                    val signUpText = findViewById<TextView>(R.id.sign_in_text)
                    signUpText.setOnClickListener {
                        signUp(it)
                    }

                    findViewById<Group>(R.id.blank_group).visibility = View.VISIBLE
                }
            })
        }
    }
}
