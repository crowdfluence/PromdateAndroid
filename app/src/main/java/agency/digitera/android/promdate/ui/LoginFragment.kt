package agency.digitera.android.promdate.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import agency.digitera.android.promdate.util.ApiAccessor
import agency.digitera.android.promdate.DrawerInterface
import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.data.DefaultResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.fragment_login.*

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
        val toolbar: Toolbar = include as Toolbar
        toolbar.title = getString(R.string.login)
        appCompatActivity.setSupportActionBar(toolbar)

        //set up blue text to go to register
        sign_up_text.setOnClickListener {
            signUp(it)
        }

        //set up login button
        login_button.setOnClickListener {
            login(it)
        }
    }

    private fun login(view: View) {
        val email = email_edit.text.toString()
        val password = password_edit.text.toString()
        val apiAccessor = ApiAccessor()

        val loadingAnim = loading_pb
        loadingAnim.visibility = View.VISIBLE

        val call: Call<DefaultResponse> = apiAccessor.apiService.login(email, password)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful && response.body()?.status == 200) {
                    loadingAnim.visibility = View.GONE

                    //successfully logged in; stores authentication token in file
                    val sp: SharedPreferences? = context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    sp?.edit()?.putString("token", response.body()?.result)?.apply()

                    //opens up main feed
                    findNavController().navigate(R.id.nav_feed)

                } else {
                    Snackbar.make(constraint_layout, R.string.failed_login,
                            Snackbar.LENGTH_LONG)
                            .show()

                    loadingAnim.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Snackbar.make(constraint_layout, R.string.no_internet,
                        Snackbar.LENGTH_LONG)
                        .show()

                loadingAnim.visibility = View.GONE
            }
        })
    }

    private fun signUp(view: View) {
        findNavController().navigate(R.id.nav_register)
    }
}
