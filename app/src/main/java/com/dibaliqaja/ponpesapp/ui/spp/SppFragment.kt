package com.dibaliqaja.ponpesapp.ui.spp

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dibaliqaja.ponpesapp.databinding.FragmentSppBinding
import com.dibaliqaja.ponpesapp.helper.Constant
import com.dibaliqaja.ponpesapp.helper.PreferencesHelper
import com.dibaliqaja.ponpesapp.helper.formatDate
import com.dibaliqaja.ponpesapp.helper.rupiah
import com.dibaliqaja.ponpesapp.model.Syahriah
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SppFragment : Fragment() {

    private lateinit var sppViewModel: SppViewModel
    private var _binding: FragmentSppBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sppViewModel =
            ViewModelProvider(this).get(SppViewModel::class.java)

        _binding = FragmentSppBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val preferencesHelper = PreferencesHelper(requireContext())

//        binding.tilSearchYear.setEndIconOnClickListener {
//            val rSearch = binding.edtSearchYear.text.toString().trim()
//            if (!TextUtils.isEmpty(rSearch)) {
//                sppViewModel.setSearchYear("Bearer " + preferencesHelper.getString(Constant.prefToken), rSearch.toInt())
//                sppViewModel.getSearchYear().observe(viewLifecycleOwner, {
//                    binding.tvYear.text = it.toString()
//                })
//            }
//            else {
//                sppViewModel.setYear("Bearer " + preferencesHelper.getString(Constant.prefToken))
//                sppViewModel.getYear().observe(viewLifecycleOwner, {
//                    binding.tvYear.text = it.toString()
//                })
//            }
//        }

        sppViewModel.setYear("Bearer " + preferencesHelper.getString(Constant.prefToken))
        sppViewModel.getYear().observe(viewLifecycleOwner, {
            binding.tvYear.text = it.toString()
        })
        sppViewModel.setSPP("Bearer " + preferencesHelper.getString(Constant.prefToken))
        sppViewModel.getSPP().observe(viewLifecycleOwner, {
            if (it != null) {
                binding.apply {
                    if (it["jan"] != null) {
                        tvDateJan.text = formatDate(it["jan"]!!.date)
                        tvCashJan.text = rupiah(it["jan"]!!.spp)
                    }
                    if (it["feb"] != null) {
                        tvDateFeb.text = formatDate(it["feb"]!!.date)
                        tvCashFeb.text = rupiah(it["feb"]!!.spp)
                    }
                    if (it["mar"] != null) {
                        tvDateMar.text = formatDate(it["mar"]!!.date)
                        tvCashMar.text = rupiah(it["mar"]!!.spp)
                    }
                    if (it["apr"] != null) {
                        tvDateApr.text = formatDate(it["apr"]!!.date)
                        tvCashApr.text = rupiah(it["apr"]!!.spp)
                    }
                    if (it["may"] != null) {
                        tvDateMay.text = formatDate(it["may"]!!.date)
                        tvCashMay.text = rupiah(it["may"]!!.spp)
                    }
                    if (it["jun"] != null) {
                        tvDateJun.text = formatDate(it["jun"]!!.date)
                        tvCashJun.text = rupiah(it["jun"]!!.spp)
                    }
                    if (it["jul"] != null) {
                        tvDateJul.text = formatDate(it["jul"]!!.date)
                        tvCashJul.text = rupiah(it["jul"]!!.spp)
                    }
                    if (it["aug"] != null) {
                        tvDateAug.text = formatDate(it["aug"]!!.date)
                        tvCashAug.text = rupiah(it["aug"]!!.spp)
                    }
                    if (it["sep"] != null) {
                        tvDateSep.text = formatDate(it["sep"]!!.date)
                        tvCashSep.text = rupiah(it["sep"]!!.spp)
                    }
                    if (it["oct"] != null) {
                        tvDateOct.text = formatDate(it["oct"]!!.date)
                        tvCashOct.text = rupiah(it["oct"]!!.spp)
                    }
                    if (it["nov"] != null) {
                        tvDateNov.text = formatDate(it["nov"]!!.date)
                        tvCashNov.text = rupiah(it["nov"]!!.spp)
                    }
                    if (it["dec"] != null) {
                        tvDateDec.text = formatDate(it["dec"]!!.date)
                        tvCashDec.text = rupiah(it["dec"]!!.spp)
                    }
                }
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}