package com.example.wiberdriver.activities

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.example.wiberdriver.R
import com.example.wiberdriver.databinding.ActivityAddNewPaymentBinding
import com.example.wiberdriver.models.Payment
import com.example.wiberdriver.viewmodels.AddNewPaymentViewModel
import com.google.android.material.appbar.MaterialToolbar
import java.util.*

class AddNewPaymentActivity : AppCompatActivity() {
    private lateinit var addNewPaymentViewModel: AddNewPaymentViewModel
    private lateinit var binding: ActivityAddNewPaymentBinding
    private lateinit var editTextName: EditText
    private lateinit var editTextNumber: EditText
    private lateinit var editTextSince: EditText
    private lateinit var submitBtn: Button
    private lateinit var radioGroup: RadioGroup
    private lateinit var cardTypeImageView: ImageView
    private lateinit var picker: DatePickerDialog
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addNewPaymentViewModel = ViewModelProvider(this)[AddNewPaymentViewModel::class.java]

        val payment = Payment()

        editTextName = binding.addPaymentTextInputETName
        editTextNumber = binding.addPaymentTextInputETNumber
        editTextSince = binding.addPaymentTextInputETSince
        submitBtn = binding.addPaymentButtonSave
        cardTypeImageView = binding.addPaymentCardTypeIV

        radioGroup = findViewById(R.id.AddPayment_ChooseTypeCar_RG)
        radioGroup.setOnCheckedChangeListener { _, optionId ->
            run {
                when (optionId) {
                    R.id.AddPayment_Visa_RB -> {
                        payment.type = "visa"
                        cardTypeImageView.setImageResource(R.drawable.ic_visa)
                    }
                    R.id.AddPayment_mastercard_RB -> {
                        payment.type = "master"
                        cardTypeImageView.setImageResource(R.drawable.ic_mastercard)
                    }
                    R.id.AddPayment_atm_RB -> {
                        payment.type = "atm"
                        cardTypeImageView.setImageResource(R.drawable.ic_atm)
                    }

                }
            }
        }

        editTextNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, arg1: Int, arg2: Int,
                arg3: Int
            ) {
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                val initial = s.toString()
                var processed = initial.replace("\\D".toRegex(), "")
                processed = processed.replace("(\\d{4})(?=\\d)".toRegex(), "$1 ")
                if (initial != processed) {
                    s.replace(0, initial.length, processed)
                }
            }
        })

        editTextSince.setOnClickListener {
            picker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val year: Int = year
                    val month: Int = month + 1
                    val day: Int = dayOfMonth
                    val calendar = Calendar.getInstance()
                    calendar.set(year, month, day)
                    payment.since = calendar.time
                    editTextSince.setText("$day/$month/$year")

                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            picker.show()
            picker.datePicker.maxDate = System.currentTimeMillis()
        }

        submitBtn.setOnClickListener {
            if (payment.type.isNullOrEmpty()) {
                payment.type = "visa"
            }

            var notification = ""

            if (TextUtils.isEmpty(editTextName.text.toString().trim())) {
                notification = "The card holder's name must not be empty."
            } else {
                payment.name = editTextName.text.toString()
            }

            if (TextUtils.isEmpty(editTextNumber.text.toString().trim())) {
                notification = "The card number must not be empty."
            } else {
                if (editTextNumber.text.toString().length < 19) {
                    notification = "The card number must be 16 digits."
                } else {
                    payment.number = editTextNumber.text.toString()
                }
            }

            if (TextUtils.isEmpty(editTextSince.text.toString().trim())) {
                notification = "The card creation date cannot be left blank."
            }

            if (notification !== "")
                Toast.makeText(this, notification, Toast.LENGTH_SHORT).show()
            else {
                // save new card here
//                addNewPaymentViewModel.addPayment(payment)
                finish()
            }

        }
        binding.addPaymentToolbar.setNavigationOnClickListener {
            finish()
        }
    }
}