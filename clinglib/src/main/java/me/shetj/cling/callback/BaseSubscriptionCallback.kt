package me.shetj.cling.callback

import android.content.Context
import android.util.Log
import org.fourthline.cling.controlpoint.SubscriptionCallback
import org.fourthline.cling.model.gena.CancelReason
import org.fourthline.cling.model.gena.GENASubscription
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Service

/**
 * 说明：
 * 作者：zhouzhan
 * 日期：17/7/20 15:48
 */
abstract class BaseSubscriptionCallback protected constructor(service: Service<*, *>?, protected var mContext: Context?) : SubscriptionCallback(service, SUBSCRIPTION_DURATION_SECONDS) {
    override fun failed(subscription: GENASubscription<*>?, responseStatus: UpnpResponse, exception: Exception, defaultMsg: String) {
        Log.e(TAG, "AVTransportSubscriptionCallback failed.")
    }

    override fun established(subscription: GENASubscription<*>?) {}
    override fun eventsMissed(subscription: GENASubscription<*>?, numberOfMissedEvents: Int) {}
    override fun ended(subscription: GENASubscription<*>?, reason: CancelReason, responseStatus: UpnpResponse) {
        mContext = null
        Log.e(TAG, "ended")
    }

    companion object {
        private const val SUBSCRIPTION_DURATION_SECONDS = 3600 * 3
        private val TAG = BaseSubscriptionCallback::class.java.simpleName
    }

}