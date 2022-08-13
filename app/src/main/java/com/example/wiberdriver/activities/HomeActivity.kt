package com.example.wiberdriver.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.wiberdriver.R
import com.example.wiberdriver.activities.SigninActivity.Companion.accountDriverFromSignIn
import com.example.wiberdriver.activities.SigninActivity.Companion.driverInfoFromSignIn
import com.example.wiberdriver.databinding.ActivityHomeBinding
import com.example.wiberdriver.models.entity.CarRequest
import com.example.wiberdriver.models.enums.CarRequestStatus
import com.example.wiberdriver.utils.Const
import com.example.wiberdriver.utils.Const.TAG
import com.example.wiberdriver.utils.StompUtils
import com.example.wiberdriver.viewmodels.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompMessage
import java.io.IOException
import java.util.*


class HomeActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var startLocation: LatLng
    private lateinit var carRequest: CarRequest
    internal var destinationLocationMarker: Marker? = null
    private var isOnline: Boolean = true
    private lateinit var fromLayout: TextInputLayout
    private lateinit var toWhereLayout: TextInputLayout
    private lateinit var distanceLayout: TextInputLayout
    private lateinit var moneyLayout: TextInputLayout

    // bottom sheet
    private lateinit var bottomLayout :LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        bottomLayout = findViewById(R.id.bottom_sheet_layout)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomLayout)
        fromLayout = bottomLayout.findViewById<TextInputLayout>(R.id.fromInputLayout)
        toWhereLayout = bottomLayout.findViewById<TextInputLayout>(R.id.toWhereInputLayout)
        distanceLayout = bottomLayout.findViewById<TextInputLayout>(R.id.distanceToGo)
        moneyLayout = bottomLayout.findViewById<TextInputLayout>(R.id.moneyToPay)
        val acceptRequestBtn = bottomLayout.findViewById<Button>(R.id.accept_button)
        val rejectRequestBtn = bottomLayout.findViewById<Button>(R.id.reject_button)

        var toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Home"
        var drawerLayout = binding.drawerLayout
        var navigationView = binding.navView
        var actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.openNavDrawer,
            R.string.closeNavDrawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_payment_method -> {
                    val intent = Intent(this, PaymentMethodActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        //map
        val supportMapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // async map
        Log.i("namedriver", accountDriverFromSignIn.phone)
        supportMapFragment.getMapAsync(this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        // socket config
        val stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Const.address)
        StompUtils.lifecycle(stompClient)
        Toast.makeText(this, "Start connecting to server", Toast.LENGTH_SHORT).show()
        // Connect to WebSocket server
        stompClient.connect()
        this.subscribeToTopic(stompClient)
//        Log.i(TAG, "Subscribe broadcast endpoint to receive response")
//        stompClient.topic(Const.broadcastResponse).subscribe { stompMessage: StompMessage ->
//            val jsonObject = JSONObject(stompMessage.payload)
//            carRequest = Gson().fromJson(jsonObject.getString("carRequestDto"), CarRequest::class.java)
//            Log.i(TAG, "Receive: " + stompMessage.payload)
//            Log.i("convert", carRequest.id!! + " " + carRequest.customerId + " " + carRequest.arrivingAddress)
//            runOnUiThread {
//                try {
//                    if (!driverInfoFromSignIn.name.equals("") && !driverInfoFromSignIn.id.equals(""))
//                    {
//                        val latCustomer = carRequest.latPickingAddress
//                        val lngCustomer = carRequest.lngPickingAddress
//                        if (latCustomer != null && lngCustomer != null) {
//                            val results = FloatArray(1)
//                            Location.distanceBetween(
//                                startLocation.latitude, startLocation.longitude,
//                                latCustomer, lngCustomer, results
//                            )
//                            val distance = results[0]
//                            if (distance < 1500.0 && accountDriverFromSignIn.isFree() && driverInfoFromSignIn.carType.equals(carRequest.carType)) {
//                                if (!this.isFinishing) {
//                                    if(bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED){
//                                        fromLayout.editText?.setText(carRequest.pickingAddress)
//                                        toWhereLayout.editText?.setText(carRequest.arrivingAddress)
//                                        distanceLayout.editText?.setText(carRequest.distance.toString() + "m")
//                                        moneyLayout.editText?.setText(carRequest.price.toString() + "VND")
//                                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            }
//        }


        rejectRequestBtn.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        acceptRequestBtn.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("Accept Request")
                .setMessage("Are you sure to accept this request?")
                .setPositiveButton("OK") { dialog, which ->
                    // send rest api to server that accept the car request
                    dialog.dismiss()
                    accountDriverFromSignIn.nextStatusRequest()
                    carRequest.status = CarRequestStatus.ACCEPTED.name
                    carRequest.driverId = driverInfoFromSignIn.id
                    carRequest.driverName = driverInfoFromSignIn.name
                    carRequest.driverPhone = driverInfoFromSignIn.phone
                    homeViewModel.acceptTheCarRequest(carRequest)
                    if (destinationLocationMarker != null) {
                        mMap.clear()
                        destinationLocationMarker!!.remove()
                    }
                    val customerLocation = LatLng(
                        carRequest.latPickingAddress,
                        carRequest.lngPickingAddress
                    )
                    destinationLocationMarker = mMap.addMarker(
                        MarkerOptions().position(customerLocation).title("Destination")
                    )
                    homeViewModel.getDirectionAndDistance(
                        startLocation,
                        customerLocation
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(customerLocation))
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }


        val acceptCarRequestStatusObserver = Observer<String>{ status ->
            when(status){
                "Accept car request successfully" -> {
                    Toast.makeText(this, "Accept the request successfully", Toast.LENGTH_SHORT).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    // after accept the request, driver will send his location to customer/server
                    homeViewModel.sendLocationToCustomer(stompClient, carRequest.id, carRequest.latPickingAddress, carRequest.lngPickingAddress, fusedLocationProviderClient)
                }
                "Finish trip" -> {
                    Toast.makeText(this, "Finish trip", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Accept the request failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        homeViewModel.acceptCarRequestStatus.observe(this, acceptCarRequestStatusObserver)

        val statusPickCustomerObserver = Observer<Boolean>{ status ->
            when(status){
                true -> {
                    binding.pickCustomerBtn.visibility = View.VISIBLE
                }
                else -> {
                    binding.pickCustomerBtn.visibility = View.GONE
                }
            }
        }
        homeViewModel.statusPickCustomer.observe(this, statusPickCustomerObserver)

        binding.pickCustomerBtn.setOnClickListener {
            homeViewModel.setFlagBreakLoopSendCustomer(true)
            binding.pickCustomerBtn.visibility = View.GONE
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null)
                {
                    if (homeViewModel.requestByCallCenter.value == false)
                    {
                        mMap.clear()
                        homeViewModel.getDirectionAndDistance(LatLng(location.latitude, location.longitude), LatLng(
                            carRequest.latArrivingAddress!!, carRequest.lngArrivingAddress!!
                        ))

                        homeViewModel.calculateToDestination(carRequest.latArrivingAddress!!,
                            carRequest.lngArrivingAddress!!, fusedLocationProviderClient, stompClient, carRequest.id)
                    }
                    else
                    {
                        mMap.clear()
                        startLocation = LatLng(location.latitude, location.longitude)
                        binding.destinationInputLayout.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.destinationInputLayout.editText?.setOnKeyListener(View.OnKeyListener { textView, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {

                val destination = binding.destinationInputLayout.editText!!.text
                val coder = Geocoder(applicationContext, Locale.getDefault())
                try {
                    val adresses: ArrayList<Address> =
                        coder.getFromLocationName(destination.toString(), 1) as ArrayList<Address>
                    if (adresses.isNotEmpty()) {
                        val location: Address = adresses[0]
                        val destinatioLocation = LatLng(location.latitude, location.longitude)
                        carRequest.latArrivingAddress = location.latitude
                        carRequest.lngArrivingAddress = location.longitude
                        if (destinationLocationMarker != null) {
                            mMap.clear()
                            destinationLocationMarker!!.remove()
                        }
                        //Put marker on map on that LatLng
                        destinationLocationMarker = mMap.addMarker(
                            MarkerOptions().position(destinatioLocation).title("Destination")
                        )
                        homeViewModel.getDirectionAndDistance(
                            startLocation,
                            destinatioLocation
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(destinatioLocation))
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
                        binding.confirmDestination.visibility = View.VISIBLE
                    } else
                        Toast.makeText(this, "No address found", Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    Log.i("error", e.toString())
                }
                hideKeyboad()
                return@OnKeyListener true
            }
            false
        })

        binding.confirmDestination.setOnClickListener {
            binding.destinationInputLayout.visibility = View.GONE
            binding.confirmDestination.visibility = View.GONE
            homeViewModel.calculateToDestination(
                carRequest.latArrivingAddress!!, carRequest.lngArrivingAddress!!,
            fusedLocationProviderClient, stompClient, carRequest.id)
        }

        binding.finishedRequestBtn.setOnClickListener {
            mMap.clear()
            homeViewModel.setFlagBreakLoopCalculateDistance(true)
            homeViewModel.setRequestByCallCenter(false)
            binding.finishedRequestBtn.visibility = View.GONE
            accountDriverFromSignIn.nextStatusRequest()
            carRequest.status = CarRequestStatus.FINISHED.status
            homeViewModel.acceptTheCarRequest(carRequest)
        }

        val statusFinishTripObserver = Observer<Boolean>{ status ->
            when(status){
                true -> {
                    binding.finishedRequestBtn.visibility = View.VISIBLE
                }
                else -> {
                    binding.finishedRequestBtn.visibility = View.GONE
                }
            }
        }
        homeViewModel.statusFinishTrip.observe(this, statusFinishTripObserver)

        // status of driver
        val onlineSwitchMenuItem = navigationView.menu.findItem(R.id.nav_status)
        val onlineSwitchBtn = onlineSwitchMenuItem.actionView as SwitchCompat
        onlineSwitchBtn.isChecked = isOnline // default is online
        onlineSwitchBtn.setOnClickListener { _ ->
            if (onlineSwitchBtn.isChecked) {
                isOnline = true
                onlineSwitchMenuItem.title = resources.getString(R.string.online_status)
                onlineSwitchMenuItem.icon = resources.getDrawable(R.drawable.ic_baseline_notifications_active_24)
                stompClient.connect()
                this.subscribeToTopic(stompClient)
                Toast.makeText(this, "You are online", Toast.LENGTH_SHORT).show()
            } else {
                isOnline = false
                onlineSwitchMenuItem.title = resources.getString(R.string.offline_status)
                onlineSwitchMenuItem.icon = resources.getDrawable(R.drawable.ic_baseline_notifications_off_24)
                stompClient.disconnect()
                Toast.makeText(this, "You are offline", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun subscribeToTopic(stompClient: StompClient) {
        Log.i(TAG, "Subscribe broadcast endpoint to receive response")
        stompClient.topic(Const.broadcastResponse).subscribe { stompMessage: StompMessage ->
            val jsonObject = JSONObject(stompMessage.payload)
            carRequest = Gson().fromJson(jsonObject.getString("carRequestDto"), CarRequest::class.java)
            Log.i(TAG, "Receive: " + stompMessage.payload)
            Log.i("convert", carRequest.id!! + " " + carRequest.customerId + " " + carRequest.arrivingAddress)
            runOnUiThread {
                try {
                    if (!driverInfoFromSignIn.name.equals("") && !driverInfoFromSignIn.id.equals(""))
                    {
                        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                            startLocation = LatLng(location.latitude, location.longitude)
                            val latCustomer = carRequest.latPickingAddress
                            val lngCustomer = carRequest.lngPickingAddress
                            if (latCustomer != null && lngCustomer != null) {
                                val results = FloatArray(1)
                                Location.distanceBetween(
                                    startLocation.latitude, startLocation.longitude,
                                    latCustomer, lngCustomer, results
                                )
                                val distance = results[0]
                                if (distance < 1500.0 && accountDriverFromSignIn.isFree() && driverInfoFromSignIn.carType.equals(carRequest.carType)) {
                                    if (!this.isFinishing) {
                                        if(bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED){
                                            if (!carRequest.arrivingAddress.isNullOrEmpty())
                                            {
                                                fromLayout.editText?.setText(carRequest.pickingAddress)
                                                toWhereLayout.editText?.setText(carRequest.arrivingAddress)
                                                distanceLayout.editText?.setText(carRequest.distance.toString() + "m")
                                                moneyLayout.editText?.setText(carRequest.price.toString() + "VND")
                                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                                            }
                                            else
                                            {
                                                homeViewModel.setRequestByCallCenter(true)
                                                fromLayout.editText?.setText(carRequest.pickingAddress)
                                                toWhereLayout.editText?.setText("")
                                                distanceLayout.editText?.setText("0m")
                                                moneyLayout.editText?.setText("0VND")
                                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        homeViewModel.geoPoint.observe(this) {
            val polylineOptions = PolylineOptions()
            polylineOptions.addAll(it)
            mMap.addPolyline(polylineOptions)
        }
        // Add a marker at current user location and move the camera
        mMap.uiSettings.isZoomControlsEnabled = false
        setUpMap()

    }

    private fun setUpMap() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("info", "permission denied")
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            Log.i("info", "permission granted")
            mMap.isMyLocationEnabled = true
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    startLocation = LatLng(location.latitude, location.longitude)
                    Log.i("info", "current location: $currentLatLng")
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
    }

    private fun hideKeyboad() {
        val view: View? = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}