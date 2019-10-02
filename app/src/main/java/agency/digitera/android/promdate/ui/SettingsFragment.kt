package agency.digitera.android.promdate.ui

import agency.digitera.android.promdate.DrawerInterface
import agency.digitera.android.promdate.MainActivity
import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.adapters.SocialAdapter
import agency.digitera.android.promdate.data.*
import agency.digitera.android.promdate.util.*
import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.user_info.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class SettingsFragment : Fragment() {

    private lateinit var drawerInterface: DrawerInterface
    private var profilePicUri: Uri? = null
    private var currentPhotoPath = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            drawerInterface = activity as DrawerInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement MyInterface")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        drawerInterface.lockDrawer()
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = getString(R.string.edit_profile)
        toolbar.setNavigationIcon(R.drawable.ic_close)
        appCompatActivity.setSupportActionBar(toolbar)

        //set up back arrow
        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }


        //set up gender spinner with hint
        val genderOptions: Array<String> = resources.getStringArray(R.array.genders_array)
        val genderAdapter = HintAdapter(
            context!!,
            genderOptions,
            android.R.layout.simple_spinner_dropdown_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val genderSpinner: Spinner = gender_spinner
        genderSpinner.adapter = genderAdapter
        genderSpinner.setSelection(genderAdapter.count)

        //set up grade spinner with hint
        val gradeOptions: Array<String> = resources.getStringArray(R.array.grades_array)
        val gradeAdapter = HintAdapter(
            context!!,
            gradeOptions,
            android.R.layout.simple_spinner_dropdown_item
        )
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val gradeSpinner: Spinner = grade_spinner
        gradeSpinner.adapter = gradeAdapter
        gradeSpinner.setSelection(gradeAdapter.count)

        //set up change profile picture
        include_user_info.profile_picture_image.setOnClickListener {
            showImagePickerDialog()
        }

        //load data
        loadData()

        fab_add_social_media.setOnClickListener { openSocialMediaDialog() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        drawerInterface.unlockDrawer() //unlocks drawer upon exiting fragment

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val imm: InputMethodManager =
                    activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
            } catch (e: Exception) {
                Log.d("HideKeyboardFail", "${e.javaClass.canonicalName}: ${e.localizedMessage}")
            }
        }
    }

    private fun loadData() {
        val accessor = ApiAccessor()

        //get token
        val sp: SharedPreferences =
            context?.getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            ) ?: throw MissingSpException()
        val token = sp.getString("token", null) ?: ""

        //send request
        val call = accessor.apiService.getUser(token)

        loading_pb.visibility = View.VISIBLE

        call.enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(
                    "ProfileFragmentOnCreate",
                    "Failed to get data! ${t.localizedMessage}, ${t.javaClass.canonicalName}"
                )
                Snackbar.make(
                    constraint_layout, R.string.no_internet,
                    Snackbar.LENGTH_LONG
                )
                    .show()
                loading_pb.visibility = View.GONE
                //TODO: Proper no internet
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val serverResponse = response.body()
                if (serverResponse != null && serverResponse.status == 200) {
                    val user: FullUser = serverResponse.result

                    loading_pb.visibility = View.GONE

                    //set up user profile with user's information
                    if (user.self.profilePictureUrl.isNotEmpty()) {
                        LoadUrl.loadProfilePicture(
                            context!!,
                            include_user_info.profile_picture_image,
                            user.self.profilePictureUrl,
                            1
                        )
                    }
                    include_user_info.first_name_edit.setText(user.self.firstName)
                    include_user_info.last_name_edit.setText(user.self.lastName)
                    school_edit.setText(user.school.name)
                    //set grade
                    val gradeId = user.self.grade?.minus(9) ?: -1
                    if (gradeId in 0..3) {
                        grade_spinner.setSelection(gradeId)
                    } else {
                        grade_spinner.setSelection(4)
                    }
                    bio_edit.setText(user.self.bio)

                    //set gender
                    when (user.self.gender?.toLowerCase()) {
                        "male" -> gender_spinner.setSelection(0)
                        "female" -> gender_spinner.setSelection(1)
                        "other" -> gender_spinner.setSelection(2)
                        else -> gender_spinner.setSelection(3)
                    }

                    val listSocialMedias = listOf(
                        UserSocial(INSTAGRAM, user.self.instagram),
                        UserSocial(SNAPCHAT, user.self.snapchat),
                        UserSocial(TWITTER, user.self.twitter)
                    )

                    //set up social media recycler view
                    list_social_media.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = SocialAdapter(listSocialMedias)
                    }

                    if (user.partner != null) {
                        unmatch_partner_button.visibility = View.VISIBLE
                        current_partner_text.visibility = View.VISIBLE
                        current_partner_text.text = getString(
                            R.string.currently_matched,
                            user.partner?.firstName,
                            user.partner?.lastName
                        )

                        unmatch_partner_button.setOnClickListener {
                            unmatch(user.partner?.id ?: -1)
                        }
                    }
                } else {
                    Snackbar.make(
                        constraint_layout, R.string.unexpected_error,
                        Snackbar.LENGTH_LONG
                    ).show()
                    loading_pb.visibility = View.GONE
                }
            }
        })
    }

    private fun unmatch(partnerId: Int) {

        //unmatch current partner
        val api = ApiAccessor().apiService
        val sp: SharedPreferences? =
            context?.getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
        val token = sp?.getString("token", null) ?: ""

        api.matchUser(token, partnerId, 1)
            .enqueue(object : Callback<DefaultResponse> {

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.e(
                        "MatchUser",
                        "Failed to unmatch! ${t.javaClass.canonicalName}: ${t.message}"
                    )
                    Snackbar.make(
                        constraint_layout,
                        R.string.match_error,
                        Snackbar.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if (response.body()?.status != 200) { //something went wrong, but server received request
                        //Match request failed
                        Log.e("MatchUser", "${response.body()?.status}: ${response.body()?.result}")
                        Snackbar.make(
                            constraint_layout,
                            R.string.match_error,
                            Snackbar.LENGTH_LONG
                        ).show()

                    } else { //success
                        unmatch_partner_button.visibility = View.GONE
                        current_partner_text.visibility = View.GONE
                    }
                }
            })
    }

    private fun updateUser() {
        val updatedUser = User()
        updatedUser.firstName = include_user_info.first_name_edit.text.toString()
        updatedUser.lastName = include_user_info.last_name_edit.text.toString()
        updatedUser.bio = bio_edit.text.toString()
//        updatedUser.snapchat = snapchat_edit.text.toString()
//        updatedUser.instagram = instagram_edit.text.toString()
//        updatedUser.twitter = twitter_edit.text.toString()
        updatedUser.schoolId = 1
        updatedUser.grade = try {
            grade_spinner.selectedItem.toString().toInt()
        } catch (e: NumberFormatException) {
            -1
        }
        updatedUser.gender = gender_spinner.selectedItem.toString()

        //check that all required fields are there & valid
        var missingFields = false
        if (!isValidName(updatedUser.firstName)) {
            include_user_info.first_name_edit_wrapper.error = getString(R.string.invalid_name)
            missingFields = true
        } else {
            include_user_info.first_name_edit_wrapper.error = null
        }
        if (!isValidName(updatedUser.lastName)) {
            include_user_info.last_name_edit_wrapper.error = getString(R.string.invalid_name)
            missingFields = true
        } else {
            include_user_info.last_name_edit_wrapper.error = null
        }
        if (updatedUser.schoolId < 0) {
            missingFields = true
        } else {
            school_edit_wrapper.error = null
        }
        if (missingFields) {
            return
        }

        val apiAccessor = ApiAccessor()

        val sp: SharedPreferences =
            context?.getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            ) ?: throw MissingSpException()
        val token = sp.getString("token", null) ?: ""

        //new profile picture
        val bodyImage: MultipartBody.Part? = if (profilePicUri != null) {
            val file = profilePicUri!!.toFile()
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part.createFormData("img", file.name, requestFile)
        } else {
            null
        }

        val bodyToken = RequestBody.create(MediaType.parse("multipart/form-data"), token)
        val bodyInsta =
            RequestBody.create(MediaType.parse("multipart/form-data"), updatedUser.instagram ?: "")
        val bodySnap =
            RequestBody.create(MediaType.parse("multipart/form-data"), updatedUser.snapchat ?: "")
        val bodyTwitter =
            RequestBody.create(MediaType.parse("multipart/form-data"), updatedUser.twitter ?: "")
        val bodyBio =
            RequestBody.create(MediaType.parse("multipart/form-data"), updatedUser.bio ?: "")
        val bodyFirst =
            RequestBody.create(MediaType.parse("multipart/form-data"), updatedUser.firstName)
        val bodyLast =
            RequestBody.create(MediaType.parse("multipart/form-data"), updatedUser.lastName)
        val bodySchool = RequestBody.create(
            MediaType.parse("multipart/form-data"),
            updatedUser.schoolId.toString()
        )
        val bodyGrade =
            RequestBody.create(MediaType.parse("multipart/form-data"), updatedUser.grade.toString())
        val bodyGender =
            RequestBody.create(MediaType.parse("multipart/form-data"), updatedUser.gender ?: "")

        //create request
        val call: Call<UpdateResponse> = apiAccessor.apiService.updateUser(
            bodyToken,
            bodyInsta,
            bodySnap,
            bodyTwitter,
            bodyBio,
            bodyFirst,
            bodyLast,
            bodySchool,
            bodyGrade,
            bodyGender,
            bodyImage
        )

        val loadingAnim = loading_pb
        loadingAnim.visibility = View.VISIBLE

        //send request
        call.enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(
                call: Call<UpdateResponse>,
                response: Response<UpdateResponse>
            ) {
                loadingAnim.visibility = View.GONE

                if (response.body()?.status != 200) {
                    Snackbar.make(
                        constraint_layout,
                        R.string.server_error,
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    AsyncTask.execute {
                        (activity as MainActivity).singlesDb.singleDao().updateUser(updatedUser)
                    }
                    findNavController().popBackStack()
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                Snackbar.make(
                    constraint_layout,
                    R.string.no_internet,
                    Snackbar.LENGTH_LONG
                ).show()
                loadingAnim.visibility = View.GONE
            }
        })
    }

    private fun isValidName(name: String): Boolean {
        if (name.isEmpty()) {
            return false
        } else {
            for (i in 0 until name.length) {
                if (name[i] != ' ' && name[i] != '\n') {
                    return true
                }
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //from camera
        if (requestCode == PICK_IMAGE_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = Uri.parse(currentPhotoPath)
            openCropActivity(uri)
        }
        //from gallery
        else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK) { //from
            val uri: Uri = data?.data ?: throw Exception("Failed to load image from gallery")
            Log.d("OnActivityResult", uri.toString())
            openCropActivity(uri)
        }
        //cropped image
        else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            profilePicUri = UCrop.getOutput(data!!)
            showImage(profilePicUri!!)
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode != RESULT_CANCELED) {
            //error cropping image
            val cropError = UCrop.getError(data!!)
            if (cropError != null) {
                Log.e("OnActivityResult", "handleCropError: ", cropError)
                Toast.makeText(context, cropError.message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                snackbar("PromDate requires camera access in order to take a photo.")
            }
        } else if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                snackbar("PromDate requires storage and camera access in order to select a photo from gallery.")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showImage(imageUri: Uri) {
        profilePicUri = imageUri
        Picasso.get()
            .load(imageUri)
            .transform(
                SelectImageOverlayTransformation(
                    256,
                    1,
                    ContextCompat.getColor(context!!, R.color.lightGray),
                    context!!
                )
            )
            .resize(512, 512)
            .centerCrop()
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .placeholder(R.drawable.default_profile) //TODO: Change to loading animation
            .error(R.drawable.default_profile) //TODO: Change to actual error
            .into(include_user_info.profile_picture_image)
    }

    private fun openCropActivity(sourceUri: Uri) {
        UCrop.of(sourceUri, Uri.fromFile(getImageFile()))
            .withMaxResultSize(1280, 1280)
            .withAspectRatio(5f, 5f)
            .start(context!!, this, UCrop.REQUEST_CROP)
    }

    @Throws(IOException::class)
    private fun getImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: throw Exception("Context not found")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = "file:" + absolutePath
        }
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                PERMISSIONS_CAMERA,
                REQUEST_CAMERA
            )
            return
        }
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(
                activity?.packageManager ?: throw Exception("PackageManager not found")
            )?.also {
                // Create the File where the photo should go
                val photoFile: File = try {
                    getImageFile()
                } catch (e: IOException) {
                    return
                }
                // Continue only if the File was successfully created
                photoFile.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context ?: throw Exception("Unable to retrieve context"),
                        "agency.digitera.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(
                        takePictureIntent,
                        PICK_IMAGE_CAMERA_REQUEST_CODE
                    )
                }
            }
        }
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
            return
        }
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(
            pickPhoto,
            PICK_IMAGE_GALLERY_REQUEST_CODE
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //controls what happens when button on toolbar is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //checks to make sure it was not the back button pressed
        if (item.itemId == R.id.action_save) {
            updateUser()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun snackbar(msg: String) {
        Snackbar.make(constraint_layout, msg, Snackbar.LENGTH_SHORT).show()
    }

    private fun showImagePickerDialog() {
        AddPhotoDialogFragment().apply {
            setOnCameraClick { openCamera() }
            setOnGalleryClick { openGallery() }
        }.also { dialog ->
            dialog.show(
                fragmentManager ?: throw Exception("Fragment manager not found"),
                "add_photo_dialog_fragment"
            )
        }
    }

    private fun openSocialMediaDialog() {
        val selectSocialAccount = SocialMediaDialogFragment()

        val onSocialMediaSelected = fun(id: Int) {
            selectSocialAccount.dismiss()
            SocialMediaTagDialogFragment(id).show(
                fragmentManager ?: throw Exception("Fragment manager not found"),
                "social_media_tag_dialog_fragment"
            )
        }

        selectSocialAccount.apply {
            onSocialMediaClicked = onSocialMediaSelected
        }.show(
            fragmentManager ?: throw Exception("Fragment manager not found"),
            "social_media_dialog_fragment"
        )
    }

    companion object {
        private const val PICK_IMAGE_CAMERA_REQUEST_CODE = 610
        private const val PICK_IMAGE_GALLERY_REQUEST_CODE = 609
        private const val REQUEST_EXTERNAL_STORAGE = 0
        private const val REQUEST_CAMERA = 1
        private val PERMISSIONS_STORAGE =
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        private val PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)
    }
}

