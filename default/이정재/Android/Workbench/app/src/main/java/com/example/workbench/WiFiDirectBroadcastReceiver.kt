package com.example.workbench

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager

class WiFiDirectBroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val activity: MainActivity
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                activity.isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                if (activity.checkPermission()) {
                    manager.requestPeers(channel) { peers ->
                        activity.updatePeerList(peers.deviceList)
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                manager.requestConnectionInfo(channel) { info ->
                    activity.updateConnectionStatus(info)
                }
            }
        }
    }
}