package agency.digitera.android.promdate.ui

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import agency.digitera.android.promdate.DrawerInterface
import agency.digitera.android.promdate.R
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_dress_search.*
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class DressSearchFragment : Fragment() {

    private lateinit var drawerInterface: DrawerInterface
    private lateinit var brandsList: ArrayList<String>
    private lateinit var typesList: ArrayList<String>
    private lateinit var colorList: ArrayList<String>
    private lateinit var brandDialog: SpinnerDialog
    private lateinit var typeDialog: SpinnerDialog
    private lateinit var colorDialog: SpinnerDialog
    private lateinit var brandB: Button
    private lateinit var typeB: Button
    private lateinit var colorB: Button
    private lateinit var dialog: AlertDialog
    private var brand: String = ""
    private var type: String = ""
    private var color: String = ""
    private var ROOT_URL: String = ""
    public  var URL_DRESS: String = ROOT_URL + "" //url for the server that contains the code for adding the dress to the database

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            drawerInterface = activity as DrawerInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement DrawerInterface")
        }//end try catch

    }//end method onAttach

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawerInterface.lockDrawer()
        return inflater.inflate(R.layout.fragment_dress_search, container, false)
    }// end method onCreateView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*//set up toolbar at top of layout
        initItems()  //initializes all the variables

        //when an item on the searchable spinner is selected
        brandDialog.bindOnSpinerListener { item, _ ->
            brand = item
            brandB.text = brand
        }//end bindOnSpinerListener
        typeDialog.bindOnSpinerListener { item, _ ->
            type = item
            typeB.text = type
            showAlert()
            var result = URL("dress.promdate.app/mobile.php").readText()
        }//end bindOnSpinerListener
        //when an item on the searchable spinner is selected
//        colorDialog.bindOnSpinerListener { item, _ ->
//            color = item
//            colorB.text = color
//        }//end bindOnSpinerListener

        //when the button it clicked
        brandB.setOnClickListener {
            brandDialog.showSpinerDialog()  //show the searchable spinner
        }//end setOnClickListener

//        //when the button it clicked
//        colorB.setOnClickListener {
//            colorDialog.showSpinerDialog()  //show the searchable spinner
//        }//end setOnClickListener

        typeB.setOnClickListener {
            when (brand) {   // adds different model items to the ArrayList to display based on which brand the user chooses
                "Sherri Hill" -> {
                    sherriHill()
                }
                "Faviana" -> {
                    faviana()
                }
                "Zoey Grey" -> {
                    zoeyGrey()
                }
                "Jovani" -> {
                    jovani()
                }
            }
            typeDialog.showSpinerDialog()   //show the searchable spinner
        }//end setOnClickListener

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = getString(R.string.dress_search)
        appCompatActivity.setSupportActionBar(toolbar)

        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        //set up login button
        search_button.setOnClickListener {
            search(it)
        }*/
    }

    private fun search(view: View) {
        findNavController().navigate(R.id.nav_dress_profile)
    }//end method search

    //method that initializes all the variables and lists
    private fun initItems() {
        typesList = ArrayList()  //the models of the various dresses
        brandsList = ArrayList() //the brands of the dresses
        colorList = ArrayList()   // color test

        //TODO: Add things to the list from the database with all the dresses
        brandsList.add("Sherri Hill")
        brandsList.add("Faviana")
        brandsList.add("Jovani")
        brandsList.add("Zoey Grey")

//TODO: Add things to the list from the database with all the dresses
        colorList.add("Red")
        colorList.add("Blue")
        colorList.add("Black")
        colorList.add("White")
        colorList.add("Silver")
        colorList.add("Purple")
        colorList.add("Yellow")
        colorList.add("Orange")

        brandB = view!!.findViewById(R.id.brandB)
        typeB = view!!.findViewById(R.id.typesB)
        colorB = view!!.findViewById(R.id.colorB)
        brandDialog = SpinnerDialog(activity, brandsList, "Choose the brand: ")
        typeDialog = SpinnerDialog(activity, typesList, "Choose the model: ")
        colorDialog = SpinnerDialog(activity, colorList, "Choose the color: ")
    }//end fun initItems

    /**
     * function that shows the alert dialog for confirming a dress
     */
    private fun showAlert() {
        val builder = AlertDialog.Builder(view!!.context)
        builder.setCancelable(true)
        builder.setTitle("Confirm Dress")
        builder.setMessage("Is this the dress you bought: a $color $type from $brand")
        builder.setPositiveButton("Confirm") { dialog, which ->
            addDress()
            search(view!!)
        }
        builder.setNegativeButton(android.R.string.cancel) { dialog, which -> }
        val dialog = builder.create()
        dialog.show()
    }//end fun showAlert()

    /**
     * TODO: Add things to the list from the database with all the dresses
     */
    private fun sherriHill() {
        typesList.clear()
        typesList.add("Style 50516")
        typesList.add("Style 50812")
        typesList.add("Style 51611")
        typesList.add("Style 51582")
        typesList.add("Style 51578")
    }//end function sherriHill

    /**
     * TODO: Add things to the list from the database with all the dresses
     */
    private fun faviana() {
        typesList.clear()
        typesList.add("FAVIANA 7755")
        typesList.add("FAVIANA S7916")
        typesList.add("FAVIANA ES10112")
        typesList.add("FAVIANA 7946")
        typesList.add("FAVIANA S 10205")
    }//end function faviana

    /**
     * TODO: Add things to the list from the database with all the dresses
     */
    private fun zoeyGrey() {
        typesList.clear()
        typesList.add("STYLE 31301")
        typesList.add("STYLE 31309")
        typesList.add("STYLE 31314")
        typesList.add("STYLE 31316")
        typesList.add("STYLE 31219")
    }//end function zoeyGrey

    /**
     * TODO: Add things to the list from the database with all the dresses
     */
    private fun jovani() {
        typesList.clear()
        typesList.add("Prom Dress 63350")
        typesList.add("Prom Dress 60283")
        typesList.add("Prom Dress 55187")
        typesList.add("Prom Dress 63563")
        typesList.add("Prom Dress 45811")
    }//end function jovani

    /**
     * Method that adds the selected dress to a database
     * TODO: Connect to a bigger server
     * TODO: Call the currents user's dress when calling on the dress profile
     * TODO: Call the dresses information when opening the dress profile
     */
    private fun addDress() {
        val stringRequest = object : StringRequest(Method.POST, URL_DRESS,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getString("message") == "This dress has already been added by another user") {
                        val builder = AlertDialog.Builder(view!!.context)
                        builder.setCancelable(true)
                        builder.setTitle("Warning!")
                        builder.setMessage("You are potentially ruining your prom night by wearing the same dress as someone else. Do you really want to be that person?")
                        builder.setPositiveButton("Yes, I am that person") { dialog, which ->
                            addDressWithRepeat()
                        }
                        builder.setNegativeButton("Hell no") { dialog, which -> }
                        dialog = builder.create()
                        dialog.show()
                    } else {
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["brand"] = brand
                params["model"] = type
                return params
            }
        }
        val rq: RequestQueue = Volley.newRequestQueue(context)
        rq.add(stringRequest)
    }//end add dress

    /**
     * I had to create 2 different PHP files to save the dress, the first was to add the dress and inform the user
     * if there was a duplicate. However, I could not find a way to communicate between Android Studio and my PHP files
     * so I created a second file that would add the dress even if there was a duplicate.
     * It is not the mos efficient way to do
     */
    private fun addDressWithRepeat() {
        val stringRequest = object : StringRequest(Method.POST, ROOT_URL + "", //php file, or whatever that has the code that connects to the database
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["brand"] = brand
                params["model"] = type
                return params
            }
        }
        val rq: RequestQueue = Volley.newRequestQueue(context)
        rq.add(stringRequest)
    }//end addDressRepeat
}//end class