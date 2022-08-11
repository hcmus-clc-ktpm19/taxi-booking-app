package com.example.wiberdriver.viewmodels

import android.location.Location
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wiberdriver.activities.HomeActivity
import com.example.wiberdriver.activities.SigninActivity
import com.example.wiberdriver.api.CarRequestService
import com.example.wiberdriver.api.RouteService
import com.example.wiberdriver.models.entity.CarRequest
import com.example.wiberdriver.models.enums.CarRequestStatus
import com.example.wiberdriver.utils.Const
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.naiksoftware.stomp.StompClient

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
                        if (carRequest.status.equals(CarRequestStatus.FINISHED.status))
                        {
                            acceptCarRequestStatus.postValue("Finish trip")
                        }
                        else{
                            acceptCarRequestStatus.postValue("Accept car request successfully")
                        }
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

    val statusPickCustomer = MutableLiveData<Boolean>()

    private val _flagBreakLoopSendCustomer = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _flagBreakLoopCalculateDistance = MutableLiveData<Boolean>().apply {
        value = false
    }

    fun sendLocationToCustomer(
        stompClient: StompClient,
        idCarRequest: String?, latCustomer: Double, lngCustomer: Double,
        locationProvider: FusedLocationProviderClient
    ) {
        GlobalScope.launch {
            val dispatcher = this.coroutineContext
            CoroutineScope(dispatcher).launch {
                while (true)
                {
                    if (_flagBreakLoopSendCustomer.value == true)
                        break
                    Log.i("taisao", "sendLocationToCustomer")
                    locationProvider.lastLocation.addOnSuccessListener { location ->
                        if (location != null)
                        {
                            val jsonObject = JSONObject()
                            try {
                                val results = FloatArray(1)
                                Location.distanceBetween(
                                    location.latitude, location.longitude,
                                    latCustomer, lngCustomer, results
                                )
                                val distance = results[0]
                                if (distance < 50.0)
                                    statusPickCustomer.postValue(true)
                                else
                                    statusPickCustomer.postValue(false)
                                jsonObject.put("latDriver", location.latitude)
                                jsonObject.put("lngDriver", location.longitude)
                                jsonObject.put("toCarRequestId", idCarRequest)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            stompClient.send(Const.chat, jsonObject.toString()).subscribe()
                        }
                    }
                    delay(200)
                }
                _flagBreakLoopSendCustomer.postValue(false)
                statusPickCustomer.postValue(false)
            }
        }
    }

    val statusFinishTrip = MutableLiveData<Boolean>()

    fun calculateToDestination(
        latDestination: Double, lngDestination: Double,
        locationProvider: FusedLocationProviderClient,
        stompClient: StompClient,
        idCarRequest: String?
    ) {
        GlobalScope.launch {
            val dispatcher = this.coroutineContext
            CoroutineScope(dispatcher).launch {
                while (true)
                {
                    if (_flagBreakLoopCalculateDistance.value == true)
                        break
                    locationProvider.lastLocation.addOnSuccessListener { location ->
                        if (location != null)
                        {
                            try {
                                val results = FloatArray(1)
                                Location.distanceBetween(
                                    location.latitude, location.longitude,
                                    latDestination, lngDestination, results
                                )
                                val distance = results[0]
                                if (distance < 150.0)
                                    statusFinishTrip.postValue(true)
                                else
                                    statusFinishTrip.postValue(false)
                                Log.i("taisao", distance.toString())
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    delay(200)
                }
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("message", "Finished")
                    jsonObject.put("toCarRequestId", idCarRequest)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                stompClient.send(Const.chat, jsonObject.toString()).subscribe()
                _flagBreakLoopCalculateDistance.postValue(false)
                statusFinishTrip.postValue(false)
            }
        }
    }



    fun setFlagBreakLoopSendCustomer(temp : Boolean)
    {
        _flagBreakLoopSendCustomer.value = temp
    }

    fun setFlagBreakLoopCalculateDistance(temp : Boolean)
    {
        _flagBreakLoopCalculateDistance.value = temp
    }
}