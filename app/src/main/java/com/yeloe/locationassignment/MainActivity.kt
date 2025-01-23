package com.yeloe.locationassignment

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yeloe.locationassignment.broadcast_receiver.GeofenceBroadcastReceiver
import com.yeloe.locationassignment.databinding.ActivityMainBinding
import com.yeloe.locationassignment.ui.adapter.VisitAdapter
import com.yeloe.locationassignment.view_model.VisitViewModel


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mVisitViewModel: VisitViewModel

    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geoFenceHelper: GeoFenceHelper
    private val geofenceRadius = 57f // Radius in meters
    private val geofenceCenter =
        LatLng(21.4718766, 80.2013712) // Replace with actual center coordinates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
        }

        // creating view model
        mVisitViewModel =
            ViewModelProvider(this)[VisitViewModel::class.java]


        // Initialize map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geofencingClient = LocationServices.getGeofencingClient(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geoFenceHelper = GeoFenceHelper(this);

        setupRecyclerView()

    }

    private fun addGeoFence() {
        val geofence = geoFenceHelper.getGeofence(
            "GeoFence",
            LatLng(
                geofenceCenter.latitude,
                geofenceCenter.longitude,
            ),
            geofenceRadius,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val geofencingRequest = geoFenceHelper.getGeofencingRequest(geofence)
        val pendingIntent = geoFenceHelper.getPendingIntent()

        if (checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            geofencingClient.addGeofences(geofencingRequest, pendingIntent!!)
                .addOnSuccessListener {
                    Log.d("", "onSuccess: Geofence Added...")
                    Log.d("MainActivityLog", "Geofence added successfully.")
                }
                .addOnFailureListener { e ->
                    val errorMessage = geoFenceHelper.getErrorString(e)
                    Log.d("", "onFailure: $errorMessage")
                }
        } else {
            requestPermissions()
        }


    }

    private fun requestPermissions() {
        val permissions = mutableListOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            12
        )
    }

    private fun setupGeofence() {
        val geofence = Geofence.Builder()
            .setRequestId("GeofenceID")
            .setCircularRegion(
                geofenceCenter.latitude,
                geofenceCenter.longitude,
                geofenceRadius
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val geofencePendingIntent: PendingIntent by lazy {
            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

//        val geofencePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            PendingIntent.getActivity(
//                this,
//                0,
//                Intent(this, GeofenceBroadcastReceiver::class.java),
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
//            )
//        } else {
//            PendingIntent.getActivity(
//                this,
//                0,
//                Intent(this, GeofenceBroadcastReceiver::class.java),
//                PendingIntent.FLAG_UPDATE_CURRENT
//            )
//        }

        // Creating a PendingIntent for the GeofenceBroadcastReceiver
//        val geofencePendingIntent = PendingIntent.getBroadcast(
//            this,
//            0,
//            Intent(this, GeofenceBroadcastReceiver::class.java),
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
                .run {
                    addOnSuccessListener {
                        Log.d("MainActivityLog", "Geofence added successfully.")
                    }
                    addOnFailureListener {
                        Log.e("MainActivityLog", "Failed to add geofence: ${it.message}")
                    }
                }

        }
    }

    private fun setupRecyclerView() {
        mVisitViewModel.readAllVisit.observe(
            this
        ) { response ->
            val recyclerView = binding.recyclerView
            val layout = WrapContentLinearLayoutManager(
                this@MainActivity, LinearLayoutManager.VERTICAL, false
            )
            recyclerView.layoutManager = layout
            recyclerView.adapter = VisitAdapter(response)
            if (response.isEmpty()) {
                Toast.makeText(this, "Visits is empty", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.uiSettings.isZoomControlsEnabled = true

        // Add geofence circle
        mMap.addCircle(
            CircleOptions()
                .center(geofenceCenter)
                .radius(geofenceRadius.toDouble())
                .strokeColor(Color.RED)
                .fillColor(0x22FF0000)
                .strokeWidth(2f)
        )

        // Move camera to geofence center
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(geofenceCenter, 15f))
        if (checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MainActivityLog", "Location is not enabled")
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap.isMyLocationEnabled = true
        // Enable location tracking
        enableLocationTracking()

        // Set up geofence
        addGeoFence()
    }

    private fun enableLocationTracking() {
        if (checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(userLatLng).title("You are here"))
                }
            }
        } else {
            requestPermissions()
        }
    }

}


class WrapContentLinearLayoutManager : LinearLayoutManager {
    constructor(context: Context) : super(context)
    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(
        context, orientation, reverseLayout
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context, attrs, defStyleAttr, defStyleRes
    )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        try {
            if (state.isPreLayout) {
                return  // Ignore layout on pre-layout
            }
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("MainActivityLog", "Caught IndexOutOfBoundsException in RecyclerView")
        }
    }
}



