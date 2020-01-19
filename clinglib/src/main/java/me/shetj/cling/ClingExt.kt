package me.shetj.cling

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import me.shetj.cling.callback.ControlCallback
import me.shetj.cling.control.ClingPlayControl
import me.shetj.cling.entity.ClingDevice
import me.shetj.cling.entity.ClingDeviceList
import me.shetj.cling.entity.ClingPlayState
import me.shetj.cling.entity.ClingPlayType
import me.shetj.cling.listener.ClingStateBroadcastReceiver
import me.shetj.cling.manager.ClingManager
import me.shetj.cling.manager.DeviceManager
import me.shetj.cling.service.ClingUpnpService


/**
 * 播放视频
 */
@JvmOverloads
fun ClingPlayControl.playUrl(url:String,ItemType :ClingPlayType =ClingPlayType.TYPE_VIDEO  ,callback: ControlCallback?){
    val currentState: ClingPlayState = currentState
    if (currentState == ClingPlayState.STOP) {
        playNew(url,ItemType,callback)
    } else {
        play(callback)
    }
}

/**
 * 刷新设备
 */
fun refreshDeviceList(onSuccess: (Collection<ClingDevice>?.() -> Unit)? = {}) {
    val devices: Collection<ClingDevice>? = ClingManager.instance.dmrDevices
    ClingDeviceList.setClingDeviceList(devices?.toMutableList())
    onSuccess?.let {
        onSuccess(devices)
    }
}


fun Context.getAndRegisterClingStateBroadcastReceiver(handler: Handler): ClingStateBroadcastReceiver {
    return ClingStateBroadcastReceiver(handler,this).apply {
        registerReceiver()
    }
}

fun Context.startBindService(success: (()->Unit)? = {}): ServiceConnection {
   val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder: ClingUpnpService.LocalBinder = service as ClingUpnpService.LocalBinder
            val upnpService: ClingUpnpService = binder.service
            val clingUpnpServiceManager = ClingManager.instance
            clingUpnpServiceManager.setUpnpService(upnpService)
            clingUpnpServiceManager.setDeviceManager(DeviceManager())
            success?.let {
                it()
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            ClingManager.instance.setUpnpService(null)
        }
    }

    val serviceIntent = Intent(this, ClingUpnpService::class.java)
    bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)

    return mServiceConnection
}

fun isEmpty(list: Collection<*>?): Boolean {
    return !(list != null && list.isNotEmpty())
}