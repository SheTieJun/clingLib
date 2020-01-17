package me.shetj.cling.entity

interface IResponse<T> {
    fun getResponse(): T?
    fun setResponse(response: T)
}