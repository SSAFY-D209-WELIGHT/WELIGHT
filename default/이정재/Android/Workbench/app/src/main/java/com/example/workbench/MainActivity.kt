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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
                    Log.d("WiFiDirect", "Discovery Started")
                    Toast.makeText(applicationContext, "Discovery Started", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(reason: Int) {
                    Log.d("WiFiDirect", "Discovery Failed: $reason")
                    Toast.makeText(applicationContext, "Discovery Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun stopWifiP2p() {
        if (checkPermission()) {
            wifiP2pManager.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d("WiFiDirect", "Discovery Stopped")
                    Toast.makeText(applicationContext, "Discovery Stopped", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(reason: Int) {
                    Log.d("WiFiDirect", "Failed to stop discovery: $reason")
                    Toast.makeText(applicationContext, "Failed to stop discovery", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(applicationContext,
                        "연결 시도 중...",
                        Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(reason: Int) {
                    Toast.makeText(applicationContext,
                        "연결 실패: $reason",
                        Toast.LENGTH_SHORT).show()
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
        text = "Hello, $name!",
        modifier = modifier,
        fontSize = 24.sp,
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
            .background(
                color = Color.DarkGray,
                shape = RoundedCornerShape(8.dp)
            )
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
fun CustomizedUI(
    startDiscovery: () -> Unit,
    stopDiscovery: () -> Unit,
    peers: List<WifiP2pDevice>,
    onPeerClick: (WifiP2pDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Greeting("ROHKEE")

        Button(onClick = { startDiscovery() },
            modifier = Modifier.padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Green
            )
        ) {
            WorkbenchTheme {
                ButtonText("Start Discovery")
            }
        }

        Button(
            onClick = { stopDiscovery() },
            modifier = Modifier.padding(top = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray
            ),
            shape = RoundedCornerShape(5.dp)  // 모서리 반경 설정
        ) {
            WorkbenchTheme {
                ButtonText("Stop Discovery")
            }
        }

        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            items(peers) { device ->
                WorkbenchTheme {
                    DeviceInfo(
                        device.deviceName,
                        device.deviceAddress,
                        modifier = Modifier
                            .clickable { onPeerClick(device) }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}