package me.shetj.cling.entity

import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.support.model.PositionInfo

class ClingPositionResponse : BaseClingResponse<PositionInfo>, IResponse<PositionInfo> {
    constructor(actionInvocation: ActionInvocation<*>) : super(actionInvocation) {}
    constructor(actionInvocation: ActionInvocation<*>, operation: UpnpResponse?, defaultMsg: String?) : super(actionInvocation, operation, defaultMsg) {}
    constructor(actionInvocation: ActionInvocation<*>, info: PositionInfo) : super(actionInvocation, info) {}
}