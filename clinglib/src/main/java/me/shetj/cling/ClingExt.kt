package me.shetj.cling

import android.content.Context
import android.content.ServiceConnection

fun Context.startBindUpnpService(success: (() -> Unit)? = null): ServiceConnection {
    return ClingManager.getInstant().startBindUpnpService(this, success)
}

fun Context.stopUpnpService(mServiceConnection: ServiceConnection?) {
    ClingManager.getInstant().stopBindService(this, mServiceConnection)
}
