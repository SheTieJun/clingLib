package me.shetj.cling.callback


interface ControlCallback  {
    fun success(response: Any)
    fun fail(response:Exception)
}