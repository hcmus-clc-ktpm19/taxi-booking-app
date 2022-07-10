package com.example.wiberdriver.viewmodels

import androidx.lifecycle.ViewModel
import com.example.wiberdriver.models.Payment
import java.util.*
import kotlin.collections.ArrayList

class PaymentMethodViewModel: ViewModel() {
    fun getPaymentLists(): ArrayList<Payment> {
        return arrayListOf<Payment>(
            Payment("Nguyen Van A", "visa", "4000 1234 5678 9012", Date()),
            Payment("Nguyen Van B", "visa", "4000 1234 5678 9012", Date()),
            Payment("Nguyen Van A", "visa", "4000 1234 5678 9012", Date()),
            Payment("Nguyen Van A", "visa", "4000 1234 5678 9012", Date()),
            Payment("Nguyen Van B", "visa", "4000 1234 5678 9012", Date()),
            Payment("Nguyen Van A", "visa", "4000 1234 5678 9012", Date()),
            Payment("Nguyen Van B", "visa", "4000 1234 5678 9012", Date()),
        )
    }
}