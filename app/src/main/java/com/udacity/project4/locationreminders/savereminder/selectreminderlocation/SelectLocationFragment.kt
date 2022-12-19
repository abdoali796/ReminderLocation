package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.Manifest.*
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding

import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import kotlinx.android.synthetic.main.fragment_save_reminder.view.*
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.properties.Delegates

class SelectLocationFragment : BaseFragment(),OnMapReadyCallback  {

    //Use Koin to get the view model of the
    private lateinit var maps: GoogleMap
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
   // private  lateinit var fusedLocationProviderClient: FusedLocationProviderClient
   private val fusedLocationProvider: FusedLocationProviderClient by lazy {
       LocationServices.getFusedLocationProviderClient(requireContext())
   }
    var lng by Delegates.notNull<Double>()
    var lint by Delegates.notNull<Double>()
    var point by Delegates.notNull<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mMapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mMapFragment.getMapAsync(this)
//        fusedLocationProviderClient = FusedLocationProviderClient
//        TODokO: add the map setup implementation

//        TODO0k: zoom to the user location after taking his permission
//        TODOok add style to the map
//        TODOok: put a marker to location that the user selected


//        TODOok: call this function after the user confirms on the selected location
        binding.button.isEnabled = false
        binding.button.setOnClickListener {
            onLocationSelected()
        }
        return binding.root
    }

    private fun onLocationSelected() {
        //        TODO_dane: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
    _viewModel.longitude.postValue(lng)
    _viewModel.latitude.postValue(lint)
_viewModel.reminderSelectedLocationStr.postValue(point)
        findNavController().popBackStack()
    //  Toast.makeText(requireActivity(),"$lint $lng $point",Toast.LENGTH_LONG).show()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO_ok: Change the map type based on the user's selection/.
        R.id.normal_map -> {
            maps.mapType=GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            maps.mapType=GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            maps.mapType=GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            maps.mapType=GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(map: GoogleMap) {
      maps=map
            fusedLocationProvider.lastLocation.addOnSuccessListener { loctin: Location? ->
            if (loctin != null) {
                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            loctin.latitude,
                            loctin.longitude
                        ), 15f
                    )
                )
            }else{
                Toast.makeText(requireActivity(),R.string.select_location,Toast.LENGTH_LONG).show()
            }
        }
        setMapLongClickOrPio(map)
        setMapStyle(map)
        enableMyLocation()
    }
    private fun setMapLongClickOrPio(map:GoogleMap) {
        map.setOnMapLongClickListener { latLng ->

            val snppet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .title(getString(R.string.dropped_pin))
                    .position(latLng).snippet(snppet)
            )
        lng=latLng.longitude
        lint=latLng.latitude
        point=snppet
            binding.button.isEnabled = true
        }
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()
            lng=poi.latLng.longitude
            lint=poi.latLng.latitude
            point=poi.name
            binding.button.isEnabled = true
        }
    }
    private fun setMapStyle(map: GoogleMap) {
        try {

            val succes = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireActivity(),
                    R.raw.map_style
                )
            )

            if (!succes) {
            Toast.makeText(requireActivity(),R.string.error_happened,Toast.LENGTH_LONG).show()
            }
        } catch (e: Resources.NotFoundException) {
            Toast.makeText(requireActivity(),R.string.error_happened,Toast.LENGTH_LONG).show()        }
    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
    }


    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            maps.isMyLocationEnabled = true
            checkDeviceLocationSettings()
        }
        else {
            requestPermissions(
                arrayOf<String>(permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }




    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
                checkDeviceLocationSettings()
            }else{
                         Snackbar.make(
              binding.mapView ,
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.settings) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
            }
       }
    }
    private fun checkDeviceLocationSettings(resolve:Boolean = true) {

        val locationRequest= LocationRequest.create().apply {
            priority= LocationRequest.PRIORITY_LOW_POWER
        }
        val builder= LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient=LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener {
                exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    startIntentSenderForResult(exception.resolution.intentSender,
                    REQUEST_TURN_DEVICE_LOCATION_ON, null, 0, 0, 0, null)


                } catch (sendEx: IntentSender.SendIntentException) {
//                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            }else {
                Snackbar.make(
                    binding.mapView,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }.show()
            }
        }
    }
    private val REQUEST_LOCATION_PERMISSION = 1

    private  val REQUEST_TURN_DEVICE_LOCATION_ON = 29

    }

