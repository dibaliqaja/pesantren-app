package com.dibaliqaja.ponpesapp.ui.cashbook

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.dibaliqaja.ponpesapp.R
import com.dibaliqaja.ponpesapp.databinding.ItemCashBookBinding
import com.dibaliqaja.ponpesapp.helper.rupiah
import com.dibaliqaja.ponpesapp.model.CashBook
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CashBookAdapter(private val list: ArrayList<CashBook>): RecyclerView.Adapter<CashBookAdapter.CashBookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashBookViewHolder {
        val binding = ItemCashBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CashBookViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun addList(items: ArrayList<CashBook>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    inner class CashBookViewHolder(val binding: ItemCashBookBinding): RecyclerView.ViewHolder(binding.root)

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: CashBookViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.apply {
                    tvItemNote.text = note
                    tvItemDate.text = SimpleDateFormat("d MMMM yyyy", Locale("id")).format(date)
                    if (debit == 0.00) {
                        ivCash.setImageResource(R.drawable.ic_cash_out)
                        ivCash.setColorFilter(holder.itemView.context.getColor(R.color.red_2))
                        tvItemCash.text = "-" + rupiah(credit)
                    } else if (credit == 0.00) {
                        ivCash.setImageResource(R.drawable.ic_cash_in)
                        ivCash.setColorFilter(holder.itemView.context.getColor(R.color.teal_1))
                        tvItemCash.text = rupiah(debit)
                    }
                }
            }
        }
    }
}