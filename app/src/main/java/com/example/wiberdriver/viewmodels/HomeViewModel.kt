package com.example.wiberdriver.viewmodels

import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wiberdriver.activities.SigninActivity
import com.example.wiberdriver.api.CarRequestService
import com.example.wiberdriver.api.DriverService
import com.example.wiberdriver.api.RouteService
import com.example.wiberdriver.models.entity.CarRequest
import com.example.wiberdriver.models.entity.DriverInfo
import com.example.wiberdriver.models.enums.CarRequestStatus
import com.google.android.gms.maps.model.LatLng
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _geoPoint = MutableLiveData<ArrayList<LatLng>>().apply {
        value = ArrayList<LatLng>()
    }
    val geoPoint: LiveData<ArrayList<LatLng>> = _geoPoint


    var acceptCarRequestStatus = MutableLiveData<String>()
    fun acceptTheCarRequest(carRequest: CarRequest) {
        CarRequestService.carRequestService.requestCarByAPI(
            carRequest,
            "Bearer ${SigninActivity.authDriverTokenFromSignIn.accessToken}"
        )
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        carRequest.id = response.body()
                            ?.string() //this consume that one line string so be careful to use this
                        Log.i("request car", carRequest.id.toString())
                        acceptCarRequestStatus.postValue("Accept car request successfully")
                    } else {
                        acceptCarRequestStatus.postValue(
                            "error: ${
                                response.errorBody().toString()
                            }"
                        )
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //server Dead
                    acceptCarRequestStatus.postValue(t.toString())
                }
            })
    }


    fun getDirectionAndDistance(startLocation: LatLng, destinatioLocation: LatLng) {
        RouteService.routeService.getPolyline(
            "5b3ce3597851110001cf62488405514894ed4132af5ce11377c3a573",
            "${startLocation.longitude},${startLocation.latitude}",
            "${destinatioLocation.longitude},${destinatioLocation.latitude}"
        )
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("error Api", t.toString())
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val dataFromApi = response.body()?.string()
                    val elementObj = JSONObject(dataFromApi.toString())
                    val lineString = elementObj.getJSONArray("features")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONArray("coordinates")

                    val coordinates = ArrayList<LatLng>()
                    (0 until lineString.length()).forEach {
                        val iteratorCoordinate = lineString.get(it) as JSONArray
                        coordinates.add(
                            LatLng(
                                iteratorCoordinate[1] as Double,
                                iteratorCoordinate[0] as Double
                            )
                        )
                    }
                    _geoPoint.value = coordinates
                }
            })
    }

}