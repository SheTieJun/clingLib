package me.shetj.cling.callback

import android.util.Log
import me.shetj.cling.ClingManager
import me.shetj.cling.util.Utils
import me.shetj.cling.util.Utils.isNull
import org.fourthline.cling.model.gena.GENASubscription
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable.RelativeTimePosition
import org.fourthline.cling.support.lastchange.LastChange
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable.Volume

internal class RenderingControlSubscriptionCallback(service: Service<*, *>?) :
    BaseSubscriptionCallback(service) {

    private val TAG = RenderingControlSubscriptionCallback::class.java.simpleName

    override fun eventReceived(subscription: GENASubscription<*>) {
        val values = subscription.currentValues
        if (isNull(values)) {
            return
        }
        if (!values.containsKey("LastChange")) {
            return
        }
        val lastChangeValue = values["LastChange"].toString()
        Log.i(TAG, "LastChange:$lastChangeValue")
        val lastChange: LastChange
        try {
            lastChange = LastChange(RenderingControlLastChangeParser(), lastChangeValue)
            //获取音量 volume
            val volume: Int
            if (lastChange.getEventedValue(0, Volume::class.java) != null) {
                volume = lastChange.getEventedValue(0, Volume::class.java).value.volume
                ClingManager.getInstant().updateCurrentVolume(volume)
            }
            val position: String
            val eventedValue = lastChange.getEventedValue(0, RelativeTimePosition::class.java)
            if (Utils.isNotNull(eventedValue)) {
                position = lastChange.getEventedValue(0, RelativeTimePosition::class.java).value
                val intTime = Utils.getIntTime(position)
                ClingManager.getInstant().updatePlayPosition(intTime)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}