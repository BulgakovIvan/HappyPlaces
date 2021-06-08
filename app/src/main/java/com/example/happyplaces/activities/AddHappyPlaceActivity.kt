package com.example.happyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import com.example.happyplaces.R
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplaces.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var bi: ActivityAddHappyPlaceBinding
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    private var imageUri: Uri? = null

    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var mHappyPlaceDetails: HappyPlaceModel? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    private val pickImagesFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
        if (result == null) {
            Toast.makeText(this, "Nothing selected / User Cancelled", Toast.LENGTH_SHORT).show()
        }
        else {
            imageUri = result
            val bitmap = this.contentResolver.loadThumbnail(imageUri!!,Size(200, 200), null)
            bi.ivPlaceImage.setImageBitmap(bitmap)
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val takePictureByCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val bitmap = this.contentResolver.loadThumbnail(imageUri!!,Size(200, 200), null)
            bi.ivPlaceImage.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bi = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(bi.root)

        setSupportActionBar(bi.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bi.toolbarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            mHappyPlaceDetails = intent.getParcelableExtra<Parcelable>(
                MainActivity.EXTRA_PLACE_DETAILS)!! as HappyPlaceModel
        }

        dateSetListener = DatePickerDialog.OnDateSetListener {
            _, year, month, dayOfMonth ->
            cal.set(year, month, dayOfMonth)
        }
        updateDateInView()

        if (mHappyPlaceDetails != null) {
            supportActionBar?.title = "Edit Happy Place"

            bi.etTitle.setText(mHappyPlaceDetails!!.title)
            bi.etDescription.setText(mHappyPlaceDetails!!.description)
            bi.etDate.setText(mHappyPlaceDetails!!.date)
            bi.etLocation.setText(mHappyPlaceDetails!!.location)
            mLatitude = mHappyPlaceDetails!!.latitude
            mLongitude = mHappyPlaceDetails!!.longitude

            imageUri = Uri.parse(mHappyPlaceDetails!!.image)
            bi.ivPlaceImage.setImageURI(imageUri)

            bi.btnSave.text = this.getText(R.string.btnUpdate)
        }

        bi.etDate.setOnClickListener(this)
        bi.tvAddImage.setOnClickListener(this)
        bi.btnSave.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener, // is invoked when the user sets the date
                    cal.get(Calendar.YEAR), //shows the the current year that’s visible when the dialog pops up
                    cal.get(Calendar.MONTH), //It shows the the current month that’s visible when the dialog pops up
                    cal.get(Calendar.DAY_OF_MONTH) //It shows the the current day that’s visible when the dialog pops up
                ).show()
            }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems) {_, which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save -> {
                when{
                    bi.etTitle.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
                    }
                    bi.etDescription.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
                    }
                    bi.etLocation.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
                    }
                    imageUri == null -> {
                        Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                    } else -> {
                        dbSave()
                    }
                }
            }
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        bi.etDate.setText(sdf.format(cal.time).toString())
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                             Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onPermissionsChecked(report: MultiplePermissionsReport?)
                {
                    if (report!!.areAllPermissionsGranted()) {
                        pickImagesFromGallery.launch("image/*")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken)
                {
                    showRationalDialogForPermissions()
                }
            })
            .onSameThread()
            .check()
    }

    private fun takePhotoFromCamera() {
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                             Manifest.permission.WRITE_EXTERNAL_STORAGE,
                             Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun onPermissionsChecked(report: MultiplePermissionsReport?)
                {
                    if (report!!.areAllPermissionsGranted()) {
                        cacheImageUri()
                        takePictureByCamera.launch(imageUri)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken)
                {
                    showRationalDialogForPermissions()
                }
            })
            .onSameThread()
            .check()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. " +
                        "It can be enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS")
            { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel")
            { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun cacheImageUri() {
        val fileName = UUID.randomUUID().toString()

        val file = File.createTempFile(fileName, ".jpg")

        imageUri = getUriForFile(
            this,
            "com.example.happyplaces.fileprovider",
            file)
    }

    private fun copyToImageDir(uri: Uri): Uri {
        val mimeType = contentResolver.getType(uri) // image/jpg
        val fileName = "${UUID.randomUUID()}.${mimeType!!.split("/")[1]}"

        val imageDir = File(this.filesDir.absolutePath + File.separator + "images")
        if (!imageDir.exists()) {
            imageDir.mkdir()
        }

        val outputFile = File(imageDir, fileName)
        val stream: OutputStream = FileOutputStream(outputFile)

        contentResolver.openInputStream(uri)?.copyTo(stream)

        return getUriForFile(
            this,
            "com.example.happyplaces.fileprovider",
            outputFile)
    }

    private fun dbSave() {
        val happyPlaceModel = HappyPlaceModel(
            mHappyPlaceDetails?.id ?: 0,
            bi.etTitle.text.toString(),
            copyToImageDir(imageUri!!).toString(),
            bi.etDescription.text.toString(),
            bi.etDate.text.toString(),
            bi.etLocation.text.toString(),
            mLatitude,
            mLongitude
        )
        val dbHandler = DatabaseHandler(this)

        if (mHappyPlaceDetails == null) {
            val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)

            if (addHappyPlace > 0) {
                Toast.makeText(this,
                    "The happy place details are inserted successfully",
                    Toast.LENGTH_SHORT).show()

                setResult(Activity.RESULT_OK)
                finish()
            }
        } else {
            val updateHappyPlace = dbHandler.updateHappyPlace(happyPlaceModel)

            if (updateHappyPlace > 0) {
                Toast.makeText(
                    this,
                    "The happy place details are updated successfully",
                    Toast.LENGTH_SHORT
                ).show()

                setResult(Activity.RESULT_OK)
                finish()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        val files = this.cacheDir.listFiles()

        if (files != null) {
            for (file in files) file.delete()
        }
    }
}