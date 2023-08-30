package me.shetj.cling.control

import android.content.Context
import me.shetj.cling.ClingManager
import me.shetj.cling.callback.AVTransportSubscriptionCallback
import me.shetj.cling.callback.RenderingControlSubscriptionCallback
import me.shetj.cling.entity.ClingDevice
import me.shetj.cling.util.ClingUtils
import me.shetj.cling.util.Utils.isNotNull
import me.shetj.cling.util.Utils.isNull

internal class SubscriptionControl : ISubscriptionControl{
    private var mAVTransportSubscriptionCallback: AVTransportSubscriptionCallback? = null
    private var mRenderingControlSubscriptionCallback: RenderingControlSubscriptionCallback? = null


    override fun registerAVTransport(device: ClingDevice) {
        if (isNotNull(mAVTransportSubscriptionCallback)) {
            mAVTransportSubscriptionCallback!!.end()
        }
        val controlPointImpl = ClingUtils.controlPoint
        if (isNull(controlPointImpl)) {
            return
        }
        mAVTransportSubscriptionCallback = AVTransportSubscriptionCallback(device.device.findService(ClingManager.AV_TRANSPORT_SERVICE))
        controlPointImpl!!.execute(mAVTransportSubscriptionCallback)
    }

    override fun registerRenderingControl(device: ClingDevice) {
        if (isNotNull(mRenderingControlSubscriptionCallback)) {
            mRenderingControlSubscriptionCallback!!.end()
        }
        val controlPointImpl = ClingUtils.controlPoint
        if (isNull(controlPointImpl)) {
            return
        }
        mRenderingControlSubscriptionCallback = RenderingControlSubscriptionCallback(device.device.findService(ClingManager.RENDERING_CONTROL_SERVICE))
        controlPointImpl!!.execute(mRenderingControlSubscriptionCallback)
    }

    override fun destroy() {
        if (isNotNull(mAVTransportSubscriptionCallback)) {
            mAVTransportSubscriptionCallback!!.end()
        }
        if (isNotNull(mRenderingControlSubscriptionCallback)) {
            mRenderingControlSubscriptionCallback!!.end()
        }
    }


}