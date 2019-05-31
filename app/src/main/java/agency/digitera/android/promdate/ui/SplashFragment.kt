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
import kotlinx.android.synthetic.main.fragment_splash.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO:? Delay the creation of the animation
        loading_pb.visibility = View.VISIBLE

        //check if currently stored token works; if so, skips login and goes directly to main feed
        val sp: SharedPreferences? = context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val token = sp?.getString("token", null)
        if (token != null) {
            val apiAccessor = ApiAccessor()

            val call = apiAccessor.apiService.regenToken(token)
            call.enqueue(object : Callback<DefaultResponse> {
                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.isSuccessful && response.body()?.status == 200) {
                        //token still works; stores newly generated token in file and starts main feed activity
                        Log.d("TokenRegen", response.body()?.result)
                        sp.edit().putString("token", response.body()?.result).apply()

                        //stops loading anim and starts main activity
                        findNavController().navigate(R.id.nav_feed)

                    } else {
                        sp.edit().putString("token", null).apply()

                        //Goes to login as token did not work
                        findNavController().navigate(R.id.nav_login)
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    //failed to connect to server
                    //TODO: No internet page
                }
            })
        }
        else {
            //no token found
            loading_pb.visibility = View.GONE

            findNavController().navigate(R.id.nav_login)
        }
    }
}

