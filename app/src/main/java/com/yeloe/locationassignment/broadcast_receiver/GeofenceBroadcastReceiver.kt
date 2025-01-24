package com.yeloe.locationassignment.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.yeloe.locationassignment.database.VisitDatabase
import com.yeloe.locationassignment.model.Visit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class GeofenceBroadcastReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true) {
            Log.e("MainActivityLog", "Error: ${geofencingEvent.errorCode}")
            return
        }

        val geofenceTransition = geofencingEvent?.geofenceTransition
        val triggeringLocation = geofencingEvent?.triggeringLocation
        val latitude = geofencingEvent?.triggeringLocation?.latitude
        val longitude = geofencingEvent?.triggeringLocation?.longitude

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.d("MainActivityLog", "Entered geofence")
                showToast(
                    context,
                    "Entered geofence at ${triggeringLocation?.latitude}, ${triggeringLocation?.longitude}"
                )

                saveEntryToDatabase(context, System.currentTimeMillis(), latitude, longitude)
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.d("MainActivityLog", "Exited geofence")
                showToast(context, "Exited geofence")
                saveExitToDatabase(context, System.currentTimeMillis())
            }
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveEntryToDatabase(
        context: Context,
        entryTime: Long,
        latitude: Double?,
        longitude: Double?
    ) {
        val locationName = getLocationName(
            context, latitude, longitude
        )
        val db = VisitDatabase.getInstance(context).visitDao()
        CoroutineScope(Dispatchers.IO).launch {
            db.insertVisit(
                Visit(
                    locationName = locationName,
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(entryTime),
                    entryTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(entryTime),
                    exitTime = "",
                    duration = ""
                )
            )
        }

//        mVisitViewModel.insertVisit(
//            Visit(
//                locationName = "Geofence Location",
//                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(entryTime),
//                entryTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(entryTime),
//                exitTime = "",
//                duration = ""
//            )
//        )

    }

    private fun saveExitToDatabase(context: Context, exitTime: Long) {
        val db = VisitDatabase.getInstance(context).visitDao()
        CoroutineScope(Dispatchers.IO).launch {
            val lastVisit = db.getLastVisit()
            lastVisit?.let {
                val duration = (exitTime - SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).parse(it.entryTime)!!.time) / 1000
                db.updateVisit(
                    lastVisit.copy(
                        exitTime = SimpleDateFormat(
                            "HH:mm:ss",
                            Locale.getDefault()
                        ).format(exitTime),
                        duration = "$duration seconds"
                    )
                )
            }
        }
    }

    private fun getLocationName(context: Context, latitude: Double?, longitude: Double?): String {
        return try {
            if (latitude != null && longitude != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    address.locality ?: address.subLocality ?: address.featureName
                    ?: "Unknown location"
                } else {
                    "Unknown location"
                }
            } else {
                "Unknown location"
            }
        } catch (e: Exception) {
            Log.e("MainActivityLog", "Failed to get location name: ${e.message}")
            "Unknown location"
        }
    }

}