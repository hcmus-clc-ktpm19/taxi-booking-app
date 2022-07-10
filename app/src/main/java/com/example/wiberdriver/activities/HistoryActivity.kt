package com.example.wiberdriver.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wiberdriver.adapters.HistoryAdapter
import com.example.wiberdriver.databinding.ActivityHistoryBinding
import com.example.wiberdriver.viewmodels.HistoryViewModel

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHistoryBinding
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        if (historyViewModel.historyList.value!!.isEmpty())
            historyViewModel.initHistoryData()

        binding.historyRecyclerview.layoutManager = LinearLayoutManager(this)

        val listUser = historyViewModel.historyList.value

        historyAdapter = HistoryAdapter(listUser!!)

        binding.historyRecyclerview.adapter = historyAdapter

    }
}