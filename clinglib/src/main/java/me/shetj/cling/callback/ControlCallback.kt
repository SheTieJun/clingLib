package me.shetj.cling.callback

import me.shetj.cling.entity.IResponse

interface ControlCallback<T> {
    fun success(response: IResponse<T>)
    fun fail(response: IResponse<T>)
}