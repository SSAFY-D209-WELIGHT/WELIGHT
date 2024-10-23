package com.example.workbench

import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workbench.ui.theme.WorkbenchTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import java.net.InetAddress

val pretendard = FontFamily(
    Font(R.font.ptd_regular)
)

class MainActivity : ComponentActivity() {
    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var receiver: WiFiDirectBroadcastReceiver
    private val peers = mutableStateListOf<WifiP2pDevice>()
    var isWifiP2pEnabled = false
    private val PERMISSIONS_REQUEST_CODE = 123
    private var isDiscovering = mutableStateOf(false)  // 상태 추가

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(this, mainLooper, null)
        requestPermissions() // 권한 요청 추가

        enableEdgeToEdge()
        setContent {
            WorkbenchTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CustomizedUI(
                        startDiscovery = { startWifiP2p() },
                        stopDiscovery = { stopWifiP2p() },
                        peers = peers,
                        onPeerClick = { device -> connectToPeer(device) },
                        isDiscovering = isDiscovering.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        receiver = WiFiDirectBroadcastReceiver(wifiP2pManager, channel, this)
        registerReceiver(
            receiver,
            IntentFilter().apply {
                addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
                addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            }
        )
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    fun updatePeerList(deviceList: Collection<WifiP2pDevice>) {
        peers.clear()
        peers.addAll(deviceList)
    }

    fun updateConnectionStatus(info: WifiP2pInfo) {
        if (info.groupFormed) {
            if (info.isGroupOwner) {
                startServerSocket()
            } else {
                connectToServer(info.groupOwnerAddress)
            }
        }
    }

    private fun startWifiP2p() {
        if (checkPermission()) {
            wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    isDiscovering.value = true  // Discovery 시작 시 상태 변경
                    Log.d("WiFiDirect", "Discovery Started")
                    Toast.makeText(applicationContext, "주변 탐색을 시작합니다.", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(reason: Int) {
                    isDiscovering.value = false  // 실패 시 상태 변경
                    Log.d("WiFiDirect", "Discovery Failed: $reason")
                    Toast.makeText(applicationContext, "탐색에 실패했습니다.: $reason", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun stopWifiP2p() {
        if (checkPermission()) {
            wifiP2pManager.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    isDiscovering.value = false  // Discovery 중지 시 상태 변경
                    Log.d("WiFiDirect", "Discovery Stopped")
                    Toast.makeText(applicationContext, "주변 탐색을 종료합니다.", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(reason: Int) {
                    Log.d("WiFiDirect", "Failed to stop discovery: $reason")
                    Toast.makeText(applicationContext, "탐색에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun checkPermission(): Boolean {
        return checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun startServerSocket() {
        // 서버 소켓 구현
        Log.d("WiFiDirect", "Server socket started")
    }

    private fun connectToServer(address: InetAddress) {
        // 클라이언트 연결 구현
        Log.d("WiFiDirect", "Connecting to server: ${address.hostAddress}")
    }

    private fun connectToPeer(device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
        }

        if (checkPermission()) {
            wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    val toast = Toast.makeText(
                        applicationContext,
                        "연결 시도 중...",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.CENTER, 0, 0)  // 화면 중앙에 표시
                    toast.show()
                }

                override fun onFailure(reason: Int) {
                    val toast = Toast.makeText(
                        applicationContext,
                        "연결 실패: $reason",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.CENTER, 0, 0)  // 화면 중앙에 표시
                    toast.show()
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.NEARBY_WIFI_DEVICES
            ),
            PERMISSIONS_REQUEST_CODE
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        style = TextStyle(
            color = Color.White
        )
    )
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun GreetingPreview() {
    WorkbenchTheme {
        Greeting("Android")
    }
}

@Composable
fun ButtonText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = pretendard,
        style = TextStyle(
            color = Color.Black
        )
    )
}

@Composable
fun DeviceInfo(name: String, address: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
//            .background(
//                color = Color.DarkGray,
//                shape = RoundedCornerShape(8.dp)
//            )
            .padding(8.dp)  // 외부 여백
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = pretendard,
            style = TextStyle(
                color = Color.White
            )
        )
        Text(
            text = address,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = pretendard,
            style = TextStyle(
                color = Color.White
            )
        )
    }
}

@Composable
fun DiscoveryIndicator(isDiscovering: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(12.dp)
            .background(
                color = if (isDiscovering) Color.Green else Color.Gray,
                shape = CircleShape
            )
    )
}

@Composable
fun CustomizedUI(
    startDiscovery: () -> Unit,
    stopDiscovery: () -> Unit,
    peers: List<WifiP2pDevice>,
    onPeerClick: (WifiP2pDevice) -> Unit,
    isDiscovering: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
//                .background(Color.DarkGray)  // 배경색 지정
                .padding(horizontal = 16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Start,  // 왼쪽 정렬로 변경
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DiscoveryIndicator(
                        isDiscovering = isDiscovering,
                        modifier = Modifier.padding(all = 8.dp)
                    )
                    Text(
                        text = if (isDiscovering) "연결 가능한 장치" else "버튼을 눌러 주변 장치를 탐색하세요.",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = pretendard,
                        style = TextStyle(
                            color = Color.White
                        )
                    )
                }
            }

            items(peers) { device ->
                DeviceInfo(
                    device.deviceName,
                    device.deviceAddress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPeerClick(device) }
                        .padding(vertical = 4.dp)
                )
            }
        }

        // 하단 고정 영역
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Greeting("ROHKEE")

            Button(
                onClick = { startDiscovery() },
                modifier = Modifier.padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green
                )
            ) {
                WorkbenchTheme {
                    ButtonText("탐색 시작")
                }
            }

            Button(
                onClick = { stopDiscovery() },
                modifier = Modifier.padding(top = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                WorkbenchTheme {
                    ButtonText("탐색 종료")
                }
            }
        }
    }
}