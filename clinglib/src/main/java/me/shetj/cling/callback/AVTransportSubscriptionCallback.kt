package me.shetj.cling.callback

import android.content.Context
import android.content.Intent
import android.util.Log
import me.shetj.cling.entity.Intents
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

/**
 * 说明：
 * 作者：zhouzhan
 * 日期：15/7/17 AM11:33
 */
class AVTransportSubscriptionCallback(service: Service<*, *>?, context: Context?) : BaseSubscriptionCallback(service, context) {
    override fun eventReceived(subscription: GENASubscription<*>) { // 这里进行 事件接收处理
        if (isNull(mContext)) return
        val values: Map<*, *>? = subscription.currentValues
        if (values != null && values.containsKey("LastChange")) {
            val lastChangeValue = values["LastChange"].toString()
            Log.i(TAG, "LastChange:$lastChangeValue")
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
                if (ts == TransportState.PLAYING) {
                    Log.e(TAG, "PLAYING")
                    val intent = Intent(Intents.ACTION_PLAYING)
                    mContext?.sendBroadcast(intent)
                    return
                } else if (ts == TransportState.PAUSED_PLAYBACK) {
                    Log.e(TAG, "PAUSED_PLAYBACK")
                    val intent = Intent(Intents.ACTION_PAUSED_PLAYBACK)
                    mContext?.sendBroadcast(intent)
                    return
                } else if (ts == TransportState.STOPPED) {
                    Log.e(TAG, "STOPPED")
                    val intent = Intent(Intents.ACTION_STOPPED)
                    mContext?.sendBroadcast(intent)
                    return
                } else if (ts == TransportState.TRANSITIONING) { // 转菊花状态
                    Log.e(TAG, "BUFFER")
                    val intent = Intent(Intents.ACTION_TRANSITIONING)
                    mContext?.sendBroadcast(intent)
                    return
                }
            }
            //RelativeTimePosition
            var position = "00:00:00"
            val eventedValue = lastChange.getEventedValue(0, RelativeTimePosition::class.java)
            if (isNotNull(eventedValue)) {
                position = lastChange.getEventedValue(0, RelativeTimePosition::class.java).value
                val intTime = getIntTime(position)
                Log.e(TAG, "position: $position, intTime: $intTime")
                // 该设备支持进度回传
                val intent = Intent(Intents.ACTION_POSITION_CALLBACK)
                intent.putExtra(Intents.EXTRA_POSITION, intTime)
                mContext?.sendBroadcast(intent)
                // TODO: 17/7/20 ACTION_PLAY_COMPLETE 播完了
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val TAG = AVTransportSubscriptionCallback::class.java.simpleName
    }
}