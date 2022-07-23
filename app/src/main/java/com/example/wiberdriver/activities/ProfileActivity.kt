package com.example.wiberdriver.activities

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.wiberdriver.activities.SigninActivity.Companion.authDriverTokenFromSignIn
import com.example.wiberdriver.activities.SigninActivity.Companion.phoneNumberLoginFromSignIn
import com.example.wiberdriver.api.AuthService
import com.example.wiberdriver.api.DriverService
import com.example.wiberdriver.databinding.ActivityProfileBinding
import com.example.wiberdriver.models.DriverInfo
import com.example.wiberdriver.models.roleEnum
import com.example.wiberdriver.viewmodels.ProfileViewModel
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

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
            if (nameLayout.editText?.text?.isNotEmpty() == true)
            {
                alertDialog.show()
                GlobalScope.launch {
                    val accountDetail = AuthService.authService.getAccountDetail(
                        phoneNumberLoginFromSignIn, "Bearer ${authDriverTokenFromSignIn.accessToken}")
                    if (accountDetail != null) {
                        if (newPasswordLayout.editText?.text?.isNotEmpty() == true) {
                            accountDetail.password = newPasswordLayout.editText!!.text.toString()
                            try {
                                AuthService.authService.updatePasswordAPI(
                                    accountDetail.id, accountDetail,
                                    "Bearer ${authDriverTokenFromSignIn.accessToken}"
                                )
                                val customerUpdate = DriverInfo(
                                    accountDetail.id, phoneNumberLoginFromSignIn,
                                    nameLayout.editText!!.text.toString(), roleEnum.CUSTOMER
                                )
                                updateDriverInfo(customerUpdate)
                            } catch (e: Exception) {
                                alertDialog.dismiss()
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(
                                        this@ProfileActivity,
                                        (e as? HttpException)?.response()?.errorBody()?.string(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        else
                        {
                            val customerUpdate = DriverInfo(
                                accountDetail.id, phoneNumberLoginFromSignIn,
                                nameLayout.editText!!.text.toString(), roleEnum.CUSTOMER
                            )
                            updateDriverInfo(customerUpdate)
                        }

                    }
                    else
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                this@ProfileActivity,
                                "Error while saving",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
            else
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this@ProfileActivity, "Please input name", Toast.LENGTH_LONG)
                        .show()
                }
        }
    }

    private fun updateDriverInfo(driverInfo: DriverInfo)
    {
        DriverService.driverService.updateDriverInfoAPI(driverInfo, "Bearer ${authDriverTokenFromSignIn.accessToken}")
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful)
                    {
                        Toast.makeText(this@ProfileActivity, "Update successfully", Toast.LENGTH_LONG).show()
                        profileViewModel.getDriverInfo(driverInfo.phone, authDriverTokenFromSignIn)
                    }
                    else
                    {
                        Toast.makeText(this@ProfileActivity, "Error while updating", Toast.LENGTH_LONG).show()
                    }
                    alertDialog.dismiss()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Unable to connect to server", Toast.LENGTH_LONG).show()
                }

            })
    }
}