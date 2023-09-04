package com.android.cling

import android.content.Context
import android.content.ServiceConnection
import com.android.cling.DLNAManager.Companion

fun Context.startBindUpnpService(success: (() -> Unit)? = null): ServiceConnection {
    return DLNAManager.getInstant().startBindUpnpService(this, success)
}

fun Context.stopUpnpService(mServiceConnection: ServiceConnection?) {
    DLNAManager.getInstant().stopBindService(this, mServiceConnection)
}
