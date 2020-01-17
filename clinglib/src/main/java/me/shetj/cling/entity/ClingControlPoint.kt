package me.shetj.cling.entity

import org.fourthline.cling.controlpoint.ControlPoint

class ClingControlPoint private constructor() : IControlPoint<ControlPoint> {
    private var mControlPoint: ControlPoint? = null
    override fun getControlPoint(): ControlPoint {
        return mControlPoint!!
    }

    override fun setControlPoint(controlPoint: ControlPoint) {
        mControlPoint = controlPoint
    }

    override fun destroy() {
        mControlPoint = null
        INSTANCE = null
    }

    companion object {
        private var INSTANCE: ClingControlPoint? = null
        @JvmStatic
        val instance: ClingControlPoint?
            get() {
                if (INSTANCE == null) {
                    INSTANCE = ClingControlPoint()
                }
                return INSTANCE
            }
    }
}