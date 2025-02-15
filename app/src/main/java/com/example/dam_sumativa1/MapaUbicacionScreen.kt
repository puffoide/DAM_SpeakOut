package com.example.dam_sumativa1

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaUbicacionScreen(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var isPermissionGranted by remember { mutableStateOf(false) }
    var isGpsEnabled by remember { mutableStateOf(isLocationEnabled(context)) }
    var isLoading by remember { mutableStateOf(true) }
    val cameraPositionState = rememberCameraPositionState()

    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    userLocation = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    DisposableEffect(context) {
        val gpsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                isGpsEnabled = isLocationEnabled(context!!)
            }
        }

        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(gpsReceiver, filter)

        onDispose {
            context.unregisterReceiver(gpsReceiver)
        }
    }

    LaunchedEffect(Unit) {
        checkAndRequestLocationPermission(context, requestPermissionLauncher) { granted ->
            isPermissionGranted = granted
        }
    }

    LaunchedEffect(isGpsEnabled, isPermissionGranted) {
        if (isPermissionGranted && isGpsEnabled) {
            getUserLocation(fusedLocationClient, locationRequest, locationCallback) { location ->
                if (location != null) {
                    userLocation = LatLng(location.latitude, location.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation!!, 15f)
                }
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Obteniendo ubicación...", style = MaterialTheme.typography.bodyLarge)
        } else {
            if (!isGpsEnabled) {
                Text("Debes activar el GPS para ver el mapa.", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                ) {
                    Text("Activar GPS")
                }
            } else if (userLocation != null) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true),
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = userLocation!!),
                        title = "Tu ubicación"
                    )
                }
            } else {
                Text("No se pudo obtener la ubicación.", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

fun checkAndRequestLocationPermission(
    context: Context,
    requestPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    onPermissionResult: (Boolean) -> Unit
) {
    val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION

    if (ContextCompat.checkSelfPermission(context, fineLocation) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, coarseLocation) == PackageManager.PERMISSION_GRANTED
    ) {
        onPermissionResult(true)
    } else {
        requestPermissionLauncher.launch(arrayOf(fineLocation, coarseLocation))
    }
}

@SuppressLint("MissingPermission")
fun getUserLocation(
    fusedLocationClient: FusedLocationProviderClient,
    locationRequest: LocationRequest,
    locationCallback: LocationCallback,
    onLocationReceived: (Location?) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocationReceived(location)
        } else {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }.addOnFailureListener {
        onLocationReceived(null)
    }
}
