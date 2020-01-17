package me.shetj.cling.control

import me.shetj.cling.callback.ControlCallback
import me.shetj.cling.callback.ControlReceiveCallback
import me.shetj.cling.entity.ClingPlayType

interface IPlayControl {
    /**
     * 播放一个新片源
     *
     * @param url   片源地址
     */
    fun playNew(url: String?, ItemType: ClingPlayType, callback: ControlCallback<*>?)

    /**
     * 继续播放
     */
    fun play(callback: ControlCallback<*>?)

    /**
     * 暂停
     */
    fun pause(callback: ControlCallback<*>?)

    /**
     * 停止
     */
    fun stop(callback: ControlCallback<*>?)

    /**
     * 视频 seek
     *
     * @param pos   seek到的位置(单位:毫秒)
     */
    fun seek(pos: Int, callback: ControlCallback<*>?)

    /**
     * 设置音量
     *
     * @param pos   音量值，最大为 100，最小为 0
     */
    fun setVolume(pos: Int, callback: ControlCallback<*>?)

    /**
     * 设置静音
     *
     * @param desiredMute   是否静音
     */
    fun setMute(desiredMute: Boolean, callback: ControlCallback<*>?)

    /**
     * 获取tv进度
     */
    fun getPositionInfo(callback: ControlReceiveCallback<*>?)

    /**
     * 获取音量
     */
    fun getVolume(callback: ControlReceiveCallback<*>?)
}