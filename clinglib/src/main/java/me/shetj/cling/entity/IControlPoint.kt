package me.shetj.cling.entity

interface IControlPoint<T> {
    /**
     * @return  返回控制点
     */
    fun getControlPoint(): T

    /**
     * 设置控制点
     * @param controlPoint  控制点
     */
    fun setControlPoint(controlPoint: T)

    /**
     * 销毁 清空缓存
     */
    fun destroy()
}