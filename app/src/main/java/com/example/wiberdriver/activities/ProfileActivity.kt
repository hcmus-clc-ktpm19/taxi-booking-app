package com.example.wiberdriver.activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.wiberdriver.activities.SigninActivity.Companion.authDriverTokenFromSignIn
import com.example.wiberdriver.activities.SigninActivity.Companion.driverInfoFromSignIn
import com.example.wiberdriver.activities.SigninActivity.Companion.phoneNumberLoginFromSignIn
import com.example.wiberdriver.databinding.ActivityProfileBinding
import com.example.wiberdriver.models.enums.CarType
import com.example.wiberdriver.viewmodels.ProfileViewModel
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: ActivityProfileBinding
    private lateinit var alertDialog : AlertDialog
    private var imageUri: Uri? = null
    private var avatarUrl: String = driverInfoFromSignIn.avatar.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        alertDialog = SpotsDialog.Builder().setContext(this)
            .setCancelable(false)
            .setMessage("Uploading")
            .build()

        val nameLayout = binding.nameInputLayout
        val newPasswordLayout = binding.newPasswordInputLayout

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        profileViewModel.nameText.observe(this){
            nameLayout.editText?.setText(it)
        }

        profileViewModel.carTypeValue.observe(this){
            when (it){
                CarType.FOUR_SEATS.status ->{
                    binding.seats4Type.isChecked = true
                }
                CarType.SEVEN_SEATS.status ->{
                    binding.seats7Type.isChecked = true
                }
            }
        }
        // load image
        if (!TextUtils.isEmpty(driverInfoFromSignIn.avatar)) Picasso.get().load(driverInfoFromSignIn.avatar)
            .into(binding.avaProfile)

        profileViewModel.phoneNumberText.observe(this){
            binding.phoneNumberInputLayout.editText?.setText(it)
        }
        profileViewModel.getDriverInfo(phoneNumberLoginFromSignIn, authDriverTokenFromSignIn)

        binding.saveBtn.setOnClickListener { saveBtnOnclick ->
            val nameString = nameLayout.editText?.text.toString()
            val passwordString = newPasswordLayout.editText?.text.toString()
            alertDialog.show()
            val carTypeGroup = binding.chooseCarTypeGroup
            val idCheckType = carTypeGroup.checkedRadioButtonId
            val radioBtnChecked = findViewById<RadioButton>(idCheckType)
            Log.i("driverUpdate", avatarUrl)
            if (radioBtnChecked.text.toString().equals("4 seats"))
                profileViewModel.startEditingProfile(passwordString, nameString, CarType.FOUR_SEATS.status, avatarUrl)
            else
                profileViewModel.startEditingProfile(passwordString, nameString, CarType.SEVEN_SEATS.status, avatarUrl)
        }

        val statusObserver = Observer<String>{ status ->
            alertDialog.dismiss()
            Toast.makeText(this@ProfileActivity, status, Toast.LENGTH_LONG).show()
        }
        profileViewModel.editProfileStatus.observe(this, statusObserver)

        binding.addAvatar.setOnClickListener {
            selectImage()
        }
        val uploadAvatarObserver = Observer<String>{ status ->
            when(status){
                "Fail" -> {
                    Toast.makeText(this@ProfileActivity, "Upload fail", Toast.LENGTH_LONG).show()
                }
                else -> {
                    avatarUrl = status
                    Toast.makeText(this@ProfileActivity, "Success", Toast.LENGTH_LONG).show()
                }
            }
        }
        profileViewModel.uploadAvatarStatus.observe(this, uploadAvatarObserver)
    }
    fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data!!
            binding.avaProfile.setImageURI(imageUri)
            imageUri?.let{
                profileViewModel.startUploadingAvatar(it)
            }
        }
    }
}