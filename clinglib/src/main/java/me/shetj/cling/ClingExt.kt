package me.shetj.cling

import android.content.Context
import android.content.IntentFilter
import android.os.Handler
import me.shetj.cling.callback.ControlCallback
import me.shetj.cling.control.ClingPlayControl
import me.shetj.cling.entity.*
import me.shetj.cling.listener.ClingStateBroadcastReceiver
import me.shetj.cling.manager.ClingManager


/**
 * 播放视频
 */
@JvmOverloads
fun ClingPlayControl.playUrl(url:String,ItemType :ClingPlayType =ClingPlayType.TYPE_VIDEO  ,callback: ControlCallback<*>?){
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
    ClingDeviceList.getInstance().clingDeviceList = devices
    onSuccess?.let {
        onSuccess(devices)
    }
}


fun Context.getAndRegisterClingStateBroadcastReceiver(handler: Handler): ClingStateBroadcastReceiver {
    return ClingStateBroadcastReceiver(handler).apply {
        val filter = IntentFilter()
        filter.addAction(Intents.ACTION_PLAYING)
        filter.addAction(Intents.ACTION_PAUSED_PLAYBACK)
        filter.addAction(Intents.ACTION_STOPPED)
        filter.addAction(Intents.ACTION_TRANSITIONING)
        registerReceiver(this, filter)
    }
}