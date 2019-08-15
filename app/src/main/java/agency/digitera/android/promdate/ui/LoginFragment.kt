package agency.digitera.android.promdate.ui

import agency.digitera.android.promdate.DrawerInterface
import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.data.DefaultResponse
import agency.digitera.android.promdate.util.ApiAccessor
import agency.digitera.android.promdate.util.ResultStatus
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private lateinit var drawerInterface: DrawerInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            drawerInterface = activity as DrawerInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement DrawerInterface")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawerInterface.lockDrawer()
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = getString(R.string.login)
        appCompatActivity.setSupportActionBar(toolbar)

        //set up blue text to go to register
        sign_up_text.setOnClickListener {
            signUp()
        }

        //set up login button
        login_button.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = email_edit.text.toString()
        val password = password_edit.text.toString()
        val apiAccessor = ApiAccessor()

        ResultStatus.Idle(true)

        val call: Call<DefaultResponse> = apiAccessor.apiService.login(email, password)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful && response.body()?.status == 200) {
                    response.body()?.let { updateUi(ResultStatus.OnSuccess(it)) }
                } else {
                    response.body()?.let { updateUi(ResultStatus.OnAccessFailure(it)) }
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                ResultStatus.OnFailure(t)
            }
        })
    }

    private fun updateUi(resultRequest: ResultStatus<DefaultResponse>) {
        when(resultRequest) {
            is ResultStatus.Idle -> loading_pb.visibility = if (resultRequest.isLoading) View.VISIBLE else View.GONE
            is ResultStatus.OnSuccess<DefaultResponse> -> {
                //successfully logged in; stores authentication token in file
                context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    ?.edit()
                    ?.putString("token", resultRequest.resultStatus.result)
                    ?.apply()

                //opens up main feed
                findNavController().navigate(R.id.nav_feed)
            }
            is ResultStatus.OnFailure -> {
                Snackbar.make(constraint_layout, R.string.no_internet,
                    Snackbar.LENGTH_LONG)
                    .show()
            }
            is ResultStatus.OnAccessFailure<DefaultResponse> -> {
                if (resultRequest.resultError.result == "Invalid credentials") {
                    Snackbar.make(
                        constraint_layout, R.string.failed_login,
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    Snackbar.make(
                        constraint_layout, getString(R.string.require_email_authentication),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun signUp() {
        findNavController().navigate(R.id.nav_register)
    }
}
