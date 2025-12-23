package dev.vdham.dns_toggle

import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import android.widget.Toast

class DnsToggleTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()

        try {
            val currentMode = Settings.Global.getString(contentResolver, "private_dns_mode")

            if (currentMode == "automatic" || currentMode == null || currentMode == "opportunistic") {
                Settings.Global.putString(contentResolver, "private_dns_mode", "hostname")
                Settings.Global.putString(
                    contentResolver,
                    "private_dns_specifier",
                    "dns.adguard.com"
                )
            } else {
                Settings.Global.putString(contentResolver, "private_dns_mode", "automatic")
                Settings.Global.putString(contentResolver, "private_dns_specifier", "")
            }

            updateTileState()

        } catch (e: SecurityException) {
            Log.e("DnsToggle", "Permission missing", e)
            Toast.makeText(
                this,
                "Permission missing! Grant WRITE_SECURE_SETTINGS via ADB.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateTileState() {
        val tile = qsTile ?: return

        val currentMode = Settings.Global.getString(contentResolver, "private_dns_mode")
        val isActive = currentMode == "hostname"

        tile.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE

        tile.updateTile()
    }
}