package com.dibaliqaja.ponpesapp.ui.spp

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dibaliqaja.ponpesapp.model.Syahriah
import com.dibaliqaja.ponpesapp.model.SyahriahSppResponse
import com.dibaliqaja.ponpesapp.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SppViewModel : ViewModel() {
    private val listSPP = MutableLiveData<HashMap<String, Syahriah>>()
    private val year = MutableLiveData<Int>()

    fun setYear(token: String) {
        RetrofitClient.apiService.getSyahriahSpp(token).enqueue(object :
            Callback<SyahriahSppResponse> {
            override fun onResponse(call: Call<SyahriahSppResponse>, response: Response<SyahriahSppResponse>) {
                if (response.isSuccessful) {
                    year.postValue(response.body()?.year)
                }
            }

            override fun onFailure(call: Call<SyahriahSppResponse>, t: Throwable) {
                t.printStackTrace()
                Log.e("Failure: ", t.message.toString())
            }

        })
    }

    fun getYear(): LiveData<Int> {
        return year
    }

    fun setSPP(token: String) {
        RetrofitClient.apiService.getSyahriahSpp(token).enqueue(object : Callback<SyahriahSppResponse>{
            override fun onResponse(call: Call<SyahriahSppResponse>, response: Response<SyahriahSppResponse>) {
                if (response.isSuccessful) {
                    listSPP.postValue(response.body()?.data)
                }
            }

            override fun onFailure(call: Call<SyahriahSppResponse>, t: Throwable) {
                t.printStackTrace()
                Log.e("Failure: ", t.message.toString())
            }

        })
    }

    fun getSPP(): LiveData<HashMap<String, Syahriah>> {
        return listSPP
    }

//    fun setSearchYear(token: String, rSearch: Int) {
//        RetrofitClient.apiService.getSearchSyahriahSpp(token, rSearch).enqueue(object :
//            Callback<SyahriahSppResponse> {
//            override fun onResponse(call: Call<SyahriahSppResponse>, response: Response<SyahriahSppResponse>) {
//                if (response.isSuccessful) {
//                    year.postValue(response.body()?.year)
//                }
//            }
//
//            override fun onFailure(call: Call<SyahriahSppResponse>, t: Throwable) {
//                t.printStackTrace()
//                Log.e("Failure: ", t.message.toString())
//            }
//
//        })
//    }
//
//    fun getSearchYear(): LiveData<Int> {
//        return year
//    }

//    fun setSearchSPP(token: String, rSearch: Int) {
//        RetrofitClient.apiService.getSearchSyahriahSpp(token, rSearch).enqueue(object : Callback<SyahriahSppResponse>{
//            override fun onResponse(call: Call<SyahriahSppResponse>, response: Response<SyahriahSppResponse>) {
//                if (response.isSuccessful) {
//                    listSPP.postValue(response.body()?.data)
//                }
//            }
//
//            override fun onFailure(call: Call<SyahriahSppResponse>, t: Throwable) {
//                t.printStackTrace()
//                Log.e("Failure: ", t.message.toString())
//            }
//
//        })
//    }
//
//    fun getSearchSPP(): LiveData<List<SPP>> {
//        return listSPP
//    }
}