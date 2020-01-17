package me.shetj.cling.callback

import android.content.Context
import android.content.Intent
import android.util.Log
import me.shetj.cling.entity.Intents
import me.shetj.cling.util.Utils.isNull
import org.fourthline.cling.model.gena.GENASubscription
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.support.lastchange.LastChange
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable.Volume
class RenderingControlSubscriptionCallback(service: Service<*, *>?, context: Context?) : BaseSubscriptionCallback(service, context) {

    private val TAG = RenderingControlSubscriptionCallback::class.java.simpleName

    override fun eventReceived(subscription: GENASubscription<*>) {
        val values = subscription.currentValues
        if (isNull(values)) {
            return
        }
        if (isNull(mContext)) {
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
            var volume = 0
            if (lastChange.getEventedValue(0, Volume::class.java) != null) {
                volume = lastChange.getEventedValue(0, Volume::class.java).value.volume
                Log.e(TAG, "onVolumeChange volume: $volume")
                val intent = Intent(Intents.ACTION_VOLUME_CALLBACK)
                intent.putExtra(Intents.EXTRA_VOLUME, volume)
                mContext?.sendBroadcast(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}