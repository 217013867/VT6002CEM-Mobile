package com.example.fishing.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.fishing.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Map Page- show fishing location and navigation
 */
class MapsFragment : Fragment() {

    /**
     * The entry point to the Fused Location Provider.
     */

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /**
     * Use Google Map as mapping system
     */
    private var map: GoogleMap? = null

    private var client: FusedLocationProviderClient? = null

    /**
     * Assign Hong Kong as default location.
     */
    private val defaultLocation = LatLng(22.357648, 114.154229)

    /**
     * Set default location permission are flase
     */
    private var locationPermissionGranted: Boolean = false

    private var lastKnownLocation: Location? = null


    private val callback = OnMapReadyCallback { googleMap: GoogleMap ->
        this.map = googleMap

        /**
         * Get the fishing location stored in firebase (collection name : fishing-point)
         * if succeeded, add the fishing points to the map
         * if fail, log error getting documents message
         */
        val db = Firebase.firestore
        db.collection("fishing-point")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val geo = document.getGeoPoint("location")

                    if (geo != null) {
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(geo.latitude, geo.longitude))
                                .title(document.get("name").toString())
                        )
                    }
                    Log.d("FISHING POINT", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }

        /**
         *  Turn on the My Location layer and the related control on the map.
         */
        updateLocationUI()

        /**
         * Get the current location of the device and set the position of the map.
         */
        getDeviceLocation()
    }

    /**
     * Called to do initial creation of a fragment.
     * Create a new instance of FusedLocationProviderClient for use in an Activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    /**
     *
     * Called to have the fragment instantiate its user interface view.
     * In this case, map fragment was instantiated.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initialize location client
        client = LocationServices.getFusedLocationProviderClient(requireActivity());

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    /**
     * Request location permission, so that we can get the location of the device.
     * The result of the permission request is handled by a callback, onRequestPermissionsResult.
     */
    private fun getLocationPermission() {

        /**
         *  check condition.
         */
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission
                    .ACCESS_FINE_LOCATION
            )
            == PackageManager
                .PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission
                    .ACCESS_COARSE_LOCATION
            )
            == PackageManager
                .PERMISSION_GRANTED
        ) {
            /**
             * When permission is granted
             */
            locationPermissionGranted = true
        } else {
            /**
             * When permission is not granted
             * Call method
             */
            requestPermissions(
                arrayOf(
                    Manifest.permission
                        .ACCESS_FINE_LOCATION,
                    Manifest.permission
                        .ACCESS_COARSE_LOCATION
                ), 100
            );
        }
    }

    /**
     *This interface is the contract for receiving the results for permission requests.
     * If passed, get current user location permission granted. Otherwise, permission denied.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )
        locationPermissionGranted = false
        /***
         * Check condition
         */
        if (requestCode == 100 && grantResults.isNotEmpty()
            && (grantResults[0] + grantResults[1]
                    == PackageManager.PERMISSION_GRANTED)

        ) {
            /**
             * When permission are granted
             */
            locationPermissionGranted = true
        } else {
            /**
             * When permission are denied
             * Display toast
             */
            Toast
                .makeText(
                    requireActivity(),
                    "Permission denied",
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }


    /**
     * Indicates that Lint should ignore the specified warnings for the annotated element.
     */
    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    /**
     * Indicates that Lint should ignore the specified warnings for the annotated element.
     */
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /**
         * Get the best and most recent location of the device, which may be null in rare cases when a location is not available.
         */
        try {
            Log.d("locationPermissionGrant", locationPermissionGranted.toString())
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    Log.d("task.isSuccessful", task.isSuccessful.toString())
                    Log.d("task", task.toString())
                    if (task.isSuccessful) {
                        /**
                         * Set the map's camera position to the current location of the device.
                         */
                        lastKnownLocation = task.result
                        Log.d("LOCATION: ", lastKnownLocation.toString())
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d("Debug: ", "Current location is null. Using defaults.")
                        Log.e("Error: ", "Exception: %s", task.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            } else {
                Log.d("Not Granted", "")
                map?.moveCamera(
                    CameraUpdateFactory
                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                )
                map?.uiSettings?.isMyLocationButtonEnabled = false
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    /**
     * Called immediately after onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle) has returned,
     * but before any saved state has been restored in to the view.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    /**
     * Set the zoom level of the map
     */
    companion object {
        private const val DEFAULT_ZOOM = 10
    }
}