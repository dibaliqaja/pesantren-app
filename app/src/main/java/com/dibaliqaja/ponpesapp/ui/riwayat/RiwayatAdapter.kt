package com.dibaliqaja.ponpesapp.ui.riwayat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dibaliqaja.ponpesapp.databinding.ItemSyahriahRiwayatBinding
import com.dibaliqaja.ponpesapp.helper.rupiah
import com.dibaliqaja.ponpesapp.model.SyahriahHistory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RiwayatAdapter(private val list: ArrayList<SyahriahHistory>): RecyclerView.Adapter<RiwayatAdapter.RiwayatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatViewHolder {
        val binding = ItemSyahriahRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RiwayatViewHolder(binding)
    }

    override fun getItemCount() = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun addList(items: ArrayList<SyahriahHistory>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

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
                    tvItemDate.text = SimpleDateFormat("d MMMM yyyy", Locale("id")).format(date)
                    tvItemCash.text = rupiah(spp)
                }
            }
        }
    }

    inner class RiwayatViewHolder(val binding: ItemSyahriahRiwayatBinding) :RecyclerView.ViewHolder(binding.root)
}