package com.example.wiberdriver.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wibercustomer.adapters.PaymentMethodAdapter
import com.example.wiberdriver.databinding.ActivityPaymentMethodBinding
import com.example.wiberdriver.viewmodels.PaymentMethodViewModel

class PaymentMethodActivity : AppCompatActivity() {
    private lateinit var viewModel: PaymentMethodViewModel
    private lateinit var binding: ActivityPaymentMethodBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var addNewPaymentBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[PaymentMethodViewModel::class.java]

        recyclerView = binding.recyclerView
        val paymentLists = viewModel.getPaymentLists()
        val adapter = PaymentMethodAdapter(paymentLists)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addNewPaymentBtn = binding.addNewPaymentMethod
        addNewPaymentBtn.setOnClickListener {
            val intent = Intent(this, AddNewPaymentActivity::class.java)
            startActivity(intent)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}