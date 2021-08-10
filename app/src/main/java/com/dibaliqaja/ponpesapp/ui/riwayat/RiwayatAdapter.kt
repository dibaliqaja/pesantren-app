package com.dibaliqaja.ponpesapp.ui.riwayat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dibaliqaja.ponpesapp.databinding.ItemSyahriahRiwayatBinding
import com.dibaliqaja.ponpesapp.helper.formatDate
import com.dibaliqaja.ponpesapp.helper.rupiah
import com.dibaliqaja.ponpesapp.model.Syahriah
import kotlin.collections.ArrayList

class RiwayatAdapter(private val list: ArrayList<Syahriah>): RecyclerView.Adapter<RiwayatAdapter.RiwayatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatViewHolder {
        val binding = ItemSyahriahRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RiwayatViewHolder(binding)
    }

    override fun getItemCount() = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun addList(items: ArrayList<Syahriah>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: RiwayatViewHolder, position: Int) {
        with(holder){
            with(list[position]) {
                binding.apply {
                    tvItemMonth.text = "$month $year"
                    tvItemDate.text = formatDate(date)
                    tvItemCash.text = rupiah(spp).dropLast(3)
                }
            }
        }
    }

    inner class RiwayatViewHolder(val binding: ItemSyahriahRiwayatBinding) :RecyclerView.ViewHolder(binding.root)
}