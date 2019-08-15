package agency.digitera.android.promdate.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.navigation.fragment.findNavController
import agency.digitera.android.promdate.util.ApiAccessor
import agency.digitera.android.promdate.DrawerInterface
import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.data.DefaultResponse
import agency.digitera.android.promdate.util.ResultStatus
import kotlinx.android.synthetic.main.fragment_splash.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashFragment : Fragment() {

    private lateinit var drawerInterface: DrawerInterface
    private lateinit var sp: SharedPreferences

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
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //check if currently stored token works; if so, skips login and goes directly to main feed
        context?.let { sp = it.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) }

        val token = sp.getString("token", null)

        token?.let {
            updateUi(ResultStatus.Idle(true))

            val call = ApiAccessor().apiService.regenToken(token)
            call.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful && response.body()?.status == 200) {
                        updateUi(ResultStatus.OnSuccess(response.body()!!))
                    } else {
                        updateUi(ResultStatus.RequireLogin)
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    updateUi(ResultStatus.OnFailure(t))
                    /*failed to connect to server
                    note: do not delete token here or navigate to login, as the token could still work, the client
                    just doesn't have internet access */
                    //TODO: No internet page
                }
            })
        } ?: findNavController().navigate(R.id.nav_login)
    }

    private fun updateUi(resultRequest: ResultStatus<DefaultResponse>) {
        when(resultRequest) {
            is ResultStatus.Idle -> loading_pb.visibility = if (resultRequest.isLoading) View.VISIBLE else View.GONE
            is ResultStatus.OnSuccess<DefaultResponse> -> {
                //token still works; stores newly generated token in file and starts main feed activity
                Log.d("TokenRegen", resultRequest.resultStatus.result)
                sp.edit()
                    ?.putString("token", resultRequest.resultStatus.result)
                    ?.apply()

                //stops loading anim and starts main activity
                findNavController().navigate(R.id.nav_feed)
            }
            is ResultStatus.OnFailure -> {

            }
            is ResultStatus.RequireLogin -> {
                sp.edit()
                    ?.putString("token", null)
                    ?.apply()
                //Goes to login as token did not work
                findNavController().navigate(R.id.nav_login)
            }
        }
    }
}

