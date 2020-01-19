package me.shetj.cling.callback


interface ControlReceiveCallback : ControlCallback {
    fun receive(size: Long)
}