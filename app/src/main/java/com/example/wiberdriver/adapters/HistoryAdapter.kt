package com.example.wiberdriver.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wiberdriver.R
import com.example.wiberdriver.models.History
import com.google.android.material.textfield.TextInputLayout

class HistoryAdapter(internal var historyList: List<History>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView)
    {
        val fromLayout = listItemView.findViewById<TextInputLayout>(R.id.fromInputLayout)
        val toWhereLayout = listItemView.findViewById<TextInputLayout>(R.id.toWhereInputLayout)
        val moneyPaidTV = listItemView.findViewById<TextView>(R.id.moneyPaid)
        val starRateTV = listItemView.findViewById<TextView>(R.id.starRated)
        val datePickTV = listItemView.findViewById<TextView>(R.id.datePick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.history_item,parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val perHistory : History = historyList.get(position)

        holder.fromLayout.editText?.setText(perHistory.fromLocation)
        holder.toWhereLayout.editText?.setText(perHistory.toLocation)
        holder.datePickTV.setText(perHistory.datePick)
        holder.moneyPaidTV.setText(perHistory.moneyPaid.toString())
        holder.starRateTV.setText(perHistory.starRated.toString())
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}