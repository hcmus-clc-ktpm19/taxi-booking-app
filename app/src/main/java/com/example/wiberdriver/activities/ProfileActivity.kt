package com.example.wiberdriver.activities

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.wiberdriver.activities.SigninActivity.Companion.authDriverTokenFromSignIn
import com.example.wiberdriver.activities.SigninActivity.Companion.phoneNumberLoginFromSignIn
import com.example.wiberdriver.databinding.ActivityProfileBinding
import com.example.wiberdriver.viewmodels.ProfileViewModel
import dmax.dialog.SpotsDialog

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: ActivityProfileBinding
    private lateinit var alertDialog : AlertDialog

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

        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        profileViewModel.nameText.observe(this){
            nameLayout.editText?.setText(it)
        }

        profileViewModel.phoneNumberText.observe(this){
            binding.phoneNumberInputLayout.editText?.setText(it)
        }
        profileViewModel.getDriverInfo(phoneNumberLoginFromSignIn, authDriverTokenFromSignIn)

        binding.saveBtn.setOnClickListener { saveBtnOnclick ->
            val nameString = nameLayout.editText?.text.toString()
            val passwordString = newPasswordLayout.editText?.text.toString()
            alertDialog.show()
            profileViewModel.startEditingProfile(passwordString, nameString)
        }

        val statusObserver = Observer<String>{ status ->
            alertDialog.dismiss()
            Toast.makeText(this@ProfileActivity, status, Toast.LENGTH_LONG).show()
        }
        profileViewModel.editProfileStatus.observe(this, statusObserver)
    }
}