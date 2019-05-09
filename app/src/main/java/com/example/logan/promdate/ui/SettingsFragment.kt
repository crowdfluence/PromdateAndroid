package com.example.logan.promdate.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.logan.promdate.*
import com.example.logan.promdate.data.*
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NumberFormatException
import android.provider.MediaStore
import androidx.core.content.FileProvider
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import com.example.logan.promdate.util.ApiAccessor
import com.example.logan.promdate.util.CircleTransformation
import com.example.logan.promdate.util.HintAdapter
import com.example.logan.promdate.util.LoadUrl
import com.squareup.picasso.MemoryPolicy
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawerInterface.lockDrawer()
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = getString(R.string.edit_profile)
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

        //set up save button
        save_button.setOnClickListener {
            updateUser()
        }

        //set up change profile picture
        profile_picture_image.setOnClickListener {
            showImagePickerDialog()
        }
        change_profile_text.setOnClickListener {
            showImagePickerDialog()
        }

        //load data
        loadData()
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
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                ?: throw BadTokenException()
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
                        LoadUrl.loadUrl(context!!, profile_picture_image, user.self.profilePictureUrl)
                    }
                    first_name_edit.setText(user.self.firstName)
                    last_name_edit.setText(user.self.lastName)
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

                    instagram_edit.setText(user.self.instagram)
                    snapchat_edit.setText(user.self.snapchat)
                    twitter_edit.setText(user.self.twitter)
                } else {
                    Snackbar.make(
                        constraint_layout, R.string.unexpected_error,
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    loading_pb.visibility = View.GONE
                }
            }
        })
    }

    private fun updateUser() {
        val firstName = first_name_edit.text.toString()
        val lastName = last_name_edit.text.toString()
        val bio = bio_edit.text.toString()
        val snapchat = snapchat_edit.text.toString()
        val instagram = instagram_edit.text.toString()
        val twitter = twitter_edit.text.toString()
        val schoolId = 1
        val grade: Int = try {
            grade_spinner.selectedItem.toString().toInt()
        } catch (e: NumberFormatException) {
            -1
        }
        val gender: String = gender_spinner.selectedItem.toString()

        //check that all required fields are there & valid
        var missingFields = false
        if (firstName.isEmpty()) {
            first_name_edit_wrapper.error = getString(R.string.required_field)
            missingFields = true
        } else {
            first_name_edit_wrapper.error = null
        }
        if (lastName.isEmpty()) {
            last_name_edit_wrapper.error = getString(R.string.required_field)
            missingFields = true
        } else {
            last_name_edit_wrapper.error = null
        }
        if (schoolId < 0) {
            missingFields = true
        } else {
            school_edit_wrapper.error = null
        }
        if (missingFields) {
            return
        }

        val apiAccessor = ApiAccessor()

        val sp: SharedPreferences =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                ?: throw BadTokenException()
        val token = sp.getString("token", null) ?: ""

        //new profile picture
        val bodyImage: MultipartBody.Part? = if (profilePicUri != null) {
            val file = profilePicUri!!.toFile()
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part.createFormData("img", file.name, requestFile)
        }
        else {
            null
        }

        val bodyToken = RequestBody.create(MediaType.parse("multipart/form-data"), token)
        val bodyInsta = RequestBody.create(MediaType.parse("multipart/form-data"), instagram)
        val bodySnap = RequestBody.create(MediaType.parse("multipart/form-data"), snapchat)
        val bodyTwitter = RequestBody.create(MediaType.parse("multipart/form-data"), twitter)
        val bodyBio = RequestBody.create(MediaType.parse("multipart/form-data"), bio)
        val bodyFirst = RequestBody.create(MediaType.parse("multipart/form-data"), firstName)
        val bodyLast = RequestBody.create(MediaType.parse("multipart/form-data"), lastName)
        val bodySchool = RequestBody.create(MediaType.parse("multipart/form-data"), schoolId.toString())
        val bodyGrade = RequestBody.create(MediaType.parse("multipart/form-data"), grade.toString())
        val bodyGender = RequestBody.create(MediaType.parse("multipart/form-data"), gender)

        //create request
        val call: Call<UpdateResponse> = apiAccessor.apiService.update(
            bodyToken, bodyInsta, bodySnap, bodyTwitter, bodyBio, bodyFirst, bodyLast, bodySchool, bodyGrade, bodyGender, bodyImage
        )

        val loadingAnim = loading_pb
        loadingAnim.visibility = View.VISIBLE

        //send request
        call.enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                loadingAnim.visibility = View.GONE
                if (response.body()?.status != 200) {
                    Snackbar.make(
                        constraint_layout,
                        R.string.server_error,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                else {
                    fragmentManager?.popBackStackImmediate()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = Uri.parse(currentPhotoPath)
            openCropActivity(uri, uri)
        } else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = data?.data ?: throw Exception("Failed to load image from gallery")
            openCropActivity(uri, uri)
        }
        else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val uri = UCrop.getOutput(data!!)
            profilePicUri = uri
            showImage(uri!!)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openCamera()
            else {
                snackbar("PromDate requires camera access in order to take a photo.")
            }
        }
        else if (requestCode == REQUEST_EXTERNAL_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                openGallery()
            else {
                snackbar("PromDate requires storage and camera access in order to select a photo from gallery.")
            }
        }
    }

    private fun showImage(imageUri: Uri) {
        profilePicUri = imageUri
        Picasso.get()
            .load(imageUri)
            .transform(
                CircleTransformation(
                    256,
                    1,
                    ContextCompat.getColor(context!!, R.color.lightGray)
                )
            )
            .resize(512, 512)
            .centerCrop()
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .placeholder(R.drawable.default_profile) //TODO: Change to loading animation
            .error(R.drawable.default_profile) //TODO: Change to actual error
            .into(profile_picture_image)
    }

    private fun openCropActivity(sourceUri: Uri, destinationUri: Uri) {
        UCrop.of(sourceUri, destinationUri)
            .withMaxResultSize(1280, 1280)
            .withAspectRatio(5f, 5f)
            .start(context!!, this, UCrop.REQUEST_CROP)
    }

    @Throws(IOException::class)
    private fun getImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: throw Exception("Context not found")
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
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity?.packageManager ?: throw Exception("PackageManager not found"))?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    getImageFile()
                } catch (ex: IOException) {
                    return
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context!!,
                        "com.example.logan.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, PICK_IMAGE_CAMERA_REQUEST_CODE)
                }
            }
        }
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                activity as Activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
            throw Exception("Missing required permissions!")
        }

        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY_REQUEST_CODE)
    }

    private fun snackbar(msg: String) {
        Snackbar.make(constraint_layout, msg, Snackbar.LENGTH_SHORT).show()
    }

    private fun showImagePickerDialog() {
        AlertDialog.Builder(context)
            .setCancelable(true)
            .setPositiveButton("From Gallery") { _, _ ->
                openGallery()
            }
            .setNegativeButton("From Camera") { _, _ ->
                openCamera()
            }
            .show()
    }

    companion object {
        private const val PICK_IMAGE_CAMERA_REQUEST_CODE = 610
        private const val PICK_IMAGE_GALLERY_REQUEST_CODE = 609
        private const val REQUEST_EXTERNAL_STORAGE = 0
        private const val REQUEST_CAMERA = 1
        private val PERMISSIONS_STORAGE =
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private val PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)
    }
}

