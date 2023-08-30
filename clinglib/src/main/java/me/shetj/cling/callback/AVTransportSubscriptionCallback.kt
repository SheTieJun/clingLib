package me.shetj.cling.callback

import android.content.Context
import android.util.Log
import me.shetj.cling.ClingManager
import me.shetj.cling.entity.ClingPlayState
import me.shetj.cling.util.Utils.getIntTime
import me.shetj.cling.util.Utils.isNotNull
import me.shetj.cling.util.Utils.isNull
import org.fourthline.cling.model.gena.GENASubscription
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable.RelativeTimePosition
import org.fourthline.cling.support.lastchange.LastChange
import org.fourthline.cling.support.model.TransportState

internal class AVTransportSubscriptionCallback(service: Service<*, *>?, context: Context?) : BaseSubscriptionCallback(service, context) {
    override fun eventReceived(subscription: GENASubscription<*>) { // 这里进行 事件接收处理
        if (isNull(mContext)) return
        val values: Map<*, *>? = subscription.currentValues
        if (values != null && values.containsKey("LastChange")) {
            val lastChangeValue = values["LastChange"].toString()
            doAVTransportChange(lastChangeValue)
        }
    }

    private fun doAVTransportChange(lastChangeValue: String) {
        try {
            val lastChange = LastChange(AVTransportLastChangeParser(), lastChangeValue)
            //Parse TransportState value.
            val transportState = lastChange.getEventedValue(0, AVTransportVariable.TransportState::class.java)
            if (transportState != null) {
                val ts = transportState.value
                when (ts) {
                    TransportState.PLAYING -> {
                        ClingManager.getInstant().updateCurrentPlayState(ClingPlayState.PLAY)
                    }
                    TransportState.PAUSED_PLAYBACK -> {
                        Log.e(TAG, "PAUSED_PLAYBACK")
                        ClingManager.getInstant().updateCurrentPlayState(ClingPlayState.PAUSE)
                    }
                    TransportState.STOPPED -> {
                        Log.e(TAG, "STOPPED")
                        ClingManager.getInstant().updateCurrentPlayState(ClingPlayState.STOP)
                    }
                    TransportState.TRANSITIONING -> { // 转菊花状态
                        Log.e(TAG, "BUFFER")
                        ClingManager.getInstant().updateCurrentPlayState(ClingPlayState.BUFFER)
                    }

                    else -> {}
                }
            }
            //RelativeTimePosition
            var position: String
            val eventedValue = lastChange.getEventedValue(0, RelativeTimePosition::class.java)
            if (isNotNull(eventedValue)) {
                position = lastChange.getEventedValue(0, RelativeTimePosition::class.java).value
                val intTime = getIntTime(position)
                Log.e(TAG, "该设备支持进度回传:position: $position, intTime: $intTime")
                ClingManager.getInstant().updatePlayPosition(intTime)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val TAG = AVTransportSubscriptionCallback::class.java.simpleName
    }
}