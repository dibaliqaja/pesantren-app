package com.dibaliqaja.ponpesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dibaliqaja.ponpesapp.databinding.ActivityCashBookBinding
import com.dibaliqaja.ponpesapp.helper.Constant
import com.dibaliqaja.ponpesapp.helper.PreferencesHelper
import com.dibaliqaja.ponpesapp.helper.rupiah
import com.dibaliqaja.ponpesapp.model.CashBookResponse
import com.dibaliqaja.ponpesapp.services.RetrofitClient.apiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CashBookActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var preferencesHelper: PreferencesHelper
    private lateinit var binding: ActivityCashBookBinding
    private lateinit var adapter: CashBookAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var page = 1
    private var totalPage = 1
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferencesHelper = PreferencesHelper(this)

        layoutManager = LinearLayoutManager(this)
        binding.swipeRefresh.setOnRefreshListener(this)
        setupRecyclerView()

        getCashBook(false)

        binding.tilSearch.setEndIconOnClickListener {
            adapter.clear()
            page = 1
            getSearchCashBook(true)
        }

        binding.rvCashBook.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val visibleItemCount = layoutManager.childCount
                val pastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                val total = adapter.itemCount
                if (!isLoading && page < totalPage) {
                    if (visibleItemCount + pastVisibleItem >= total) {
                        page++

                        if (!TextUtils.isEmpty(binding.edtSearch.toString())) {
                            getSearchCashBook(false)
                        } else {
                            getCashBook(false)
                        }
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (!preferencesHelper.getBoolean(Constant.prefIsLogin)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun getCashBook(isOnRefresh: Boolean) {
        isLoading = true
        if (!isOnRefresh) binding.progressBar.visibility = View.VISIBLE
        val parameters = HashMap<String, String>()
        parameters["page"] = page.toString()
        apiService.getCashBook("Bearer " + preferencesHelper.getString(Constant.prefToken), parameters).enqueue(object : Callback<CashBookResponse>{
            override fun onResponse(call: Call<CashBookResponse>, response: Response<CashBookResponse>) {
                totalPage = response.body()?.totalPage!!
                val listResponse = response.body()?.data
                binding.tvSaldoPrice.text = rupiah(response.body()?.saldo!!.toDouble())

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
                binding.swipeRefresh.isRefreshing = false
            }

            override fun onFailure(call: Call<CashBookResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(baseContext, "Failed load data", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
                Log.d("Failure: ", t.message.toString())
            }

        })
    }

    private fun getSearchCashBook(isOnRefresh: Boolean) {
        isLoading = true
        if (!isOnRefresh) binding.progressBar.visibility = View.VISIBLE
        val parameters = HashMap<String, String>()
        parameters["page"] = page.toString()
        val rSearch = binding.edtSearch.text.toString().trim()
        apiService.getSearchCashBook("Bearer " + preferencesHelper.getString(Constant.prefToken), parameters, rSearch).enqueue(object : Callback<CashBookResponse>{
            override fun onResponse(call: Call<CashBookResponse>, response: Response<CashBookResponse>) {
                totalPage = response.body()?.totalPage!!
                val listResponse = response.body()?.data
                binding.tvSaldoPrice.text = rupiah(response.body()?.saldo!!.toDouble())

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
                binding.swipeRefresh.isRefreshing = false
            }

            override fun onFailure(call: Call<CashBookResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(baseContext, "Failed load data", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
                Log.d("Failure: ", t.message.toString())
            }

        })
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvCashBook.setHasFixedSize(true)
            rvCashBook.layoutManager = layoutManager
            adapter = CashBookAdapter(ArrayList())
            rvCashBook.adapter = adapter
        }
    }

    override fun onRefresh() {
        adapter.clear()
        page = 1
        binding.edtSearch.text = null
        if (!TextUtils.isEmpty(binding.edtSearch.toString())) {
            getSearchCashBook(true)
        } else {
            getCashBook(true)
        }
    }
}