package me.shetj.cling.entity

import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse

class ClingVolumeResponse : BaseClingResponse<Int> {
    constructor(actionInvocation: ActionInvocation<*>, operation: UpnpResponse?, defaultMsg: String) : super(actionInvocation, operation, defaultMsg) {}
    constructor(actionInvocation: ActionInvocation<*>, info: Int) : super(actionInvocation, info) {}
}