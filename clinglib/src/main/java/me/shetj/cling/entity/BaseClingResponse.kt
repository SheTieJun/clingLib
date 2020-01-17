package me.shetj.cling.entity

import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse

open class BaseClingResponse<T> : IResponse<T> {
    protected var mActionInvocation: ActionInvocation<*>
    protected var operation: UpnpResponse? = null
    protected var defaultMsg: String? = null
    protected var info: T? = null

    /**
     * 控制操作成功 构造器
     *
     * @param actionInvocation  cling action 调用
     */
    constructor(actionInvocation: ActionInvocation<*>) {
        mActionInvocation = actionInvocation
    }

    /**
     * 控制操作失败 构造器
     *
     * @param actionInvocation  cling action 调用
     * @param operation     执行状态
     * @param defaultMsg    错误信息
     */
    constructor(actionInvocation: ActionInvocation<*>, operation: UpnpResponse?, defaultMsg: String?) {
        mActionInvocation = actionInvocation
        this.operation = operation
        this.defaultMsg = defaultMsg
    }

    /**
     * 接收时的回调
     *
     * @param actionInvocation  cling action 调用
     * @param info      回调的对象
     */
    constructor(actionInvocation: ActionInvocation<*>, info: T) {
        mActionInvocation = actionInvocation
        this.info = info
    }

    override fun getResponse(): T? {
        return info
    }

    override fun setResponse(response: T) {
        info = response
    }
}