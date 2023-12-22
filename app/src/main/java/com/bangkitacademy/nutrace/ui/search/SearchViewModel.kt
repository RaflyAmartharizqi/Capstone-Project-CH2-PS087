package com.bangkitacademy.nutrace.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkitacademy.nutrace.data.model.Food
import com.bangkitacademy.nutrace.data.model.FoodResponse
import com.bangkitacademy.nutrace.data.model.dummyFoodList
import com.bangkitacademy.nutrace.data.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*class SearchViewModel : ViewModel() {

    val listFood = MutableLiveData<ArrayList<Food>>()

    private val isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoadingLiveData

    init {
        isLoadingLiveData.value = false
    }

    fun setSearchFood(query: String) {

        if (listFood.value == null) {
            listFood.value = ArrayList()
        }
        isLoadingLiveData.value = true

        ApiService.create()
            .getSearchFood(query)
            .enqueue(object : Callback<FoodResponse> {
                override fun onResponse(
                    call: Call<FoodResponse>,
                    response: Response<FoodResponse>
                ) {
                    isLoadingLiveData.value = false
                    if (response.isSuccessful) {
                        listFood.postValue(response.body()?.items)
                    }
                }

                override fun onFailure(call: Call<FoodResponse>, t: Throwable) {
                    isLoadingLiveData.value = false
                    Log.d("Failure", t.message.toString())
                }
            })
    }

    fun getSearchFood(): LiveData<ArrayList<Food>> {
        return listFood
    }

    fun clearSearchFood() {
        listFood.value = ArrayList()
    }

}*/

/*class SearchViewModel : ViewModel() {

    private val listFood = MutableLiveData<ArrayList<Food>>()
    val isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoadingLiveData

    init {
        isLoadingLiveData.value = false
    }

    fun setSearchFood(query: String) {

        if (listFood.value == null) {
            listFood.value = ArrayList()
        }
        isLoadingLiveData.value = true

        val requestBody = ApiService.FoodRequestBody(food_name = query)

        ApiService.create()
            .getSearchFood(requestBody)
            .enqueue(object : Callback<FoodResponse> {
                override fun onResponse(
                    call: Call<FoodResponse>,
                    response: Response<FoodResponse>
                ) {
                    isLoadingLiveData.value = false
                    if (response.isSuccessful) {
                        listFood.postValue(response.body()?.items)
                    }
                }

                override fun onFailure(call: Call<FoodResponse>, t: Throwable) {
                    isLoadingLiveData.value = false
                    Log.d("Failure", t.message.toString())
                    Log.e("SearchViewModel", "Error fetching search results: ${t.message}", t)
                }
            })
    }

    fun getSearchFood(): LiveData<ArrayList<Food>> {
        return listFood
    }

    fun clearSearchFood() {
        listFood.value = ArrayList()
    }
}*/

class SearchViewModel : ViewModel() {

    private val listFood = MutableLiveData<List<Food>>() // Menggunakan List sebagai tipe data
    val isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoadingLiveData

    init {
        isLoadingLiveData.value = false
    }

    // Metode untuk mendapatkan data dari dummyFoodList
    fun getDummyFoodList(): LiveData<List<Food>> {
        return MutableLiveData<List<Food>>(dummyFoodList)
    }

    fun setSearchFood(query: String) {

        isLoadingLiveData.value = true

        // Biasanya, Anda tidak memerlukan objek MutableLiveData untuk mendapatkan data dari dummyFoodList
        listFood.value = dummyFoodList

        // Dalam kasus penggunaan API, metode ini bisa diubah sesuai dengan kebutuhan Anda.
        // Pada contoh ini, langsung memasukkan dummyFoodList ke LiveData tanpa panggilan ke API.
        // Untuk penggunaan sebenarnya, Anda akan menggantinya dengan pemanggilan API sebagaimana dijelaskan sebelumnya.

        isLoadingLiveData.value = false
    }

    // Metode untuk mendapatkan data dari dummyFoodList
    fun getDummyFoodListLiveData(): LiveData<List<Food>> {
        return MutableLiveData<List<Food>>(dummyFoodList)
    }

    fun clearSearchFood() {
        listFood.value = emptyList()
    }
}
