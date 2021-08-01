package com.dibaliqaja.ponpesapp.ui.riwayat

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dibaliqaja.ponpesapp.databinding.FragmentRiwayatBinding
import com.dibaliqaja.ponpesapp.helper.Constant
import com.dibaliqaja.ponpesapp.helper.PreferencesHelper
import com.dibaliqaja.ponpesapp.model.SyahriahHistoryResponse
import com.dibaliqaja.ponpesapp.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RiwayatFragment : Fragment() {

    private lateinit var adapter: RiwayatAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var preferencesHelper: PreferencesHelper
    private var _binding: FragmentRiwayatBinding? = null
    private var page = 1
    private var totalPage = 1
    private var isLoading = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val preferencesHelper = PreferencesHelper(requireContext())
        layoutManager = LinearLayoutManager(context)
        setupRecyclerView()

        preferencesHelper.getString(Constant.prefToken)?.let { getSyahriahHistory(it, false) }

        binding.tilSearch.setEndIconOnClickListener {
            adapter.clear()
            page = 1
            preferencesHelper.getString(Constant.prefToken)?.let { getSearchSyahriahHistory(it, true) }
        }

        binding.rvRiwayat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val total = adapter.itemCount
                if (!isLoading && page < totalPage) {
                    if (visibleItemCount + pastVisibleItem >= total) {
                        page++

                        if (!TextUtils.isEmpty(binding.edtSearch.toString())) {
                            preferencesHelper.getString(Constant.prefToken)?.let { getSyahriahHistory(it, false) }
                        } else {
                            preferencesHelper.getString(Constant.prefToken)?.let { getSearchSyahriahHistory(it, false) }
                        }
                    }
                }
            }
        })

        return root
    }

    private fun getSyahriahHistory(token: String, isOnRefresh: Boolean) {
        isLoading = true
        if (!isOnRefresh) binding.progressBar.visibility = View.VISIBLE
        val parameters = HashMap<String, String>()
        parameters["page"] = page.toString()
        RetrofitClient.apiService.getSyahriahHistory("Bearer $token", parameters).enqueue(object :
            Callback<SyahriahHistoryResponse> {
            override fun onResponse(call: Call<SyahriahHistoryResponse>, response: Response<SyahriahHistoryResponse>) {
                totalPage = response.body()?.totalPage!!
                val listResponse = response.body()?.data

                Log.d("Response: ", listResponse.toString())
                if (listResponse != null) {
                    adapter.addList(listResponse)
                }
                if (page == totalPage) {
                    binding.progressBar.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.INVISIBLE
                }
                isLoading = false
            }

            override fun onFailure(call: Call<SyahriahHistoryResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Failed load data", Toast.LENGTH_SHORT).show()
                preferencesHelper.clear()
                Log.d("Failure: ", t.message.toString())
            }

        })
    }

    private fun getSearchSyahriahHistory(token: String, isOnRefresh: Boolean) {
        isLoading = true
        if (!isOnRefresh) binding.progressBar.visibility = View.VISIBLE
        val parameters = HashMap<String, String>()
        parameters["page"] = page.toString()
        val rSearch = binding.edtSearch.text.toString().trim()
        RetrofitClient.apiService.getSearchSyahriahHistory("Bearer $token", parameters, rSearch).enqueue(object :
            Callback<SyahriahHistoryResponse> {
            override fun onResponse(call: Call<SyahriahHistoryResponse>, response: Response<SyahriahHistoryResponse>) {
                totalPage = response.body()?.totalPage!!
                val listResponse = response.body()?.data

                Log.d("Response: ", listResponse.toString())
                if (listResponse != null) {
                    adapter.addList(listResponse)
                }
                if (page == totalPage) {
                    binding.progressBar.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.INVISIBLE
                }
                isLoading = false
            }

            override fun onFailure(call: Call<SyahriahHistoryResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Failed load data", Toast.LENGTH_SHORT).show()
                preferencesHelper.clear()
                Log.d("Failure: ", t.message.toString())
            }

        })
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvRiwayat.setHasFixedSize(true)
            rvRiwayat.layoutManager = layoutManager
            adapter = RiwayatAdapter(ArrayList())
            rvRiwayat.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}