package com.rohkee.feature.group

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.rohkee.core.ui.util.collectWithLifecycle
import com.rohkee.feature.group.util.MultiplePermissionHandler

@SuppressLint("MissingPermission")
@Composable
fun GroupScreen(
    modifier: Modifier = Modifier,
    groupViewModel: GroupViewModel = hiltViewModel(),
    onNavigateToGroupRoom: (Long) -> Unit = {},
    onNavigateToCreateGroupRoom: () -> Unit = {},
) {
    val groupUIState by groupViewModel.groupState.collectAsStateWithLifecycle()

    groupViewModel.groupEvent.collectWithLifecycle { event ->
        when (event) {
            is GroupEvent.OpenClient -> onNavigateToGroupRoom(event.roomId)
            GroupEvent.OpenRoomCreation -> onNavigateToCreateGroupRoom()
        }
    }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationCallback by remember { mutableStateOf<LocationCallback?>(null) }

    MultiplePermissionHandler(
        permissions =
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
    ) { result ->
        if (result.all { it.value }) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, 1000).build()
            locationCallback =
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location: Location? = locationResult.lastLocation
                        location ?: return
                        groupViewModel.onIntent(
                            GroupIntent.UpdateLocation(
                                latitude = location.latitude,
                                longitude = location.longitude,
                            ),
                        )
                    }
                }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback as LocationCallback,
                Looper.getMainLooper(),
            )
        }
    }

    DisposableEffect(fusedLocationClient) {
        onDispose {
            locationCallback?.let {
                fusedLocationClient.removeLocationUpdates(it)
            }
        }
    }

    GroupContent(
        modifier = modifier,
        state = groupUIState,
        onIntent = groupViewModel::onIntent,
    )
}
