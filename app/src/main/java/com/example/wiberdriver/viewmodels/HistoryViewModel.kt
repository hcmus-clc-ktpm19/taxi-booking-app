package com.example.wiberdriver.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wiberdriver.models.entity.History

class HistoryViewModel : ViewModel() {

    private val _historyList = MutableLiveData<List<History>>().apply {
        value = ArrayList<History>()
    }

    val historyList : LiveData<List<History>> = _historyList

    fun initHistoryData() {
        val newHistoryList = ArrayList<History>()
        for (i in 0 ..4)
        {
            newHistoryList.add(History("Home", "To school", 20F, 4.4F, "15/9/2022"))
        }

        _historyList.value = newHistoryList
    }
}