package me.shetj.cling.callback

import android.util.Log
import org.fourthline.cling.controlpoint.SubscriptionCallback
import org.fourthline.cling.model.gena.CancelReason
import org.fourthline.cling.model.gena.GENASubscription
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Service

abstract class BaseSubscriptionCallback protected constructor(service: Service<*, *>?) :
    SubscriptionCallback(service, SUBSCRIPTION_DURATION_SECONDS) {
    override fun failed(subscription: GENASubscription<*>?, responseStatus: UpnpResponse?, exception: Exception?, defaultMsg: String?) {
        Log.e(TAG, "AVTransportSubscriptionCallback failed.")
    }

    override fun established(subscription: GENASubscription<*>?) {}
    override fun eventsMissed(subscription: GENASubscription<*>?, numberOfMissedEvents: Int) {}
    override fun ended(subscription: GENASubscription<*>?, reason: CancelReason?, responseStatus: UpnpResponse?) {
        Log.e(TAG, "ended")
    }

    companion object {
        private const val SUBSCRIPTION_DURATION_SECONDS = 3600 * 3
        private val TAG = BaseSubscriptionCallback::class.java.simpleName
    }

}