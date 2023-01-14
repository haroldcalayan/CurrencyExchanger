package com.haroldcalayan.currencyexchanger.presentation.ui.currency_exchange

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.haroldcalayan.currencyexchanger.BR
import com.haroldcalayan.currencyexchanger.R
import com.haroldcalayan.currencyexchanger.data.source.local.entity.CurrencyBalance
import com.haroldcalayan.currencyexchanger.databinding.ItemCurrencyBalanceBinding

class CurrencyBalanceAdapter(private var data: List<CurrencyBalance>) :
    RecyclerView.Adapter<CurrencyBalanceAdapter.AdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding: ItemCurrencyBalanceBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_currency_balance,
            parent,
            false
        )
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    fun updateData(data: List<CurrencyBalance>) {
        this.data = (data.sortedByDescending { it.cashInDate }).sortedByDescending { it.isBase }
        notifyDataSetChanged()
    }

    class AdapterViewHolder(private val binding: ItemCurrencyBalanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CurrencyBalance) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
        }
    }
}