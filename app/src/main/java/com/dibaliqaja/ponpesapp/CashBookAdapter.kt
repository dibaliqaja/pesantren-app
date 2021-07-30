package com.dibaliqaja.ponpesapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dibaliqaja.ponpesapp.databinding.ItemCashBookBinding
import com.dibaliqaja.ponpesapp.model.CashBook
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CashBookAdapter(private val list: ArrayList<CashBook>): RecyclerView.Adapter<CashBookAdapter.CashBookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashBookViewHolder {
        val binding = ItemCashBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CashBookViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    fun addList(items: ArrayList<CashBook>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    inner class CashBookViewHolder(val binding: ItemCashBookBinding): RecyclerView.ViewHolder(binding.root)

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: CashBookViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.apply {
                    tvItemNote.text = note
                    tvItemDate.text = SimpleDateFormat("d MMMM yyyy").format(date)
                    if (debit == 0.00) {
                        ivCash.setImageResource(R.drawable.ic_cash_out)
                        tvItemCash.text = rupiah(credit)
                    } else if (credit == 0.00) {
                        ivCash.setImageResource(R.drawable.ic_cash_in)
                        tvItemCash.text = rupiah(debit)
                    }
                }
            }
        }
    }

    private fun rupiah(number: Double): String{
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(number).toString()
    }
}