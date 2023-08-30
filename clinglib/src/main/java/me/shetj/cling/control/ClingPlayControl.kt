package me.shetj.cling.control

import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import me.shetj.cling.ClingManager
import me.shetj.cling.callback.ControlCallback
import me.shetj.cling.callback.ControlReceiveCallback
import me.shetj.cling.entity.ClingPlayState
import me.shetj.cling.entity.ClingPlayState.STOP
import me.shetj.cling.entity.ClingPlayType
import me.shetj.cling.entity.ClingPlayType.TYPE_AUDIO
import me.shetj.cling.entity.ClingPlayType.TYPE_IMAGE
import me.shetj.cling.entity.ClingPlayType.TYPE_VIDEO
import me.shetj.cling.util.ClingUtils.controlPoint
import me.shetj.cling.util.ClingUtils.findServiceFromSelectedDevice
import me.shetj.cling.util.Utils.getStringTime
import me.shetj.cling.util.Utils.isNotNull
import me.shetj.cling.util.Utils.isNull
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo
import org.fourthline.cling.support.avtransport.callback.Pause
import org.fourthline.cling.support.avtransport.callback.Play
import org.fourthline.cling.support.avtransport.callback.Seek
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI
import org.fourthline.cling.support.avtransport.callback.Stop
import org.fourthline.cling.support.model.DIDLObject
import org.fourthline.cling.support.model.PositionInfo
import org.fourthline.cling.support.model.ProtocolInfo
import org.fourthline.cling.support.model.Res
import org.fourthline.cling.support.model.item.AudioItem
import org.fourthline.cling.support.model.item.ImageItem
import org.fourthline.cling.support.model.item.VideoItem
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume
import org.fourthline.cling.support.renderingcontrol.callback.SetMute
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume
import org.seamless.util.MimeType

internal class ClingPlayControl : IPlayControl {
    /** 上次设置音量时间戳, 防抖动  */
    private var mVolumeLastTime: Long = 0
    private var mCurrentState = STOP
    override fun playNew(url: String?,title:String,itemType: ClingPlayType, callback: ControlCallback?) {
        stop(object : ControlCallback {
            // 1、 停止当前播放视频
            override fun success(response: Any) {
                setAVTransportURI(url,title, itemType, object : ControlCallback {
                    override fun fail(response: Exception) {
                        if (isNotNull(callback)) {
                            callback!!.fail(response)
                        }
                    }

                    override fun success(response: Any) {
                        play(callback) // 3、播放视频
                    }
                })
            }

            override fun fail(response: Exception) {
                if (isNotNull(callback)) {
                    callback!!.fail(response)
                }
            }
        })
    }

    override fun play(callback: ControlCallback?) {
        val avtService = findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE)
        if (isNull(avtService)) {
            return
        }
        val controlPointImpl = controlPoint
        if (isNull(controlPointImpl)) {
            return
        }
        controlPointImpl!!.execute(object : Play(avtService) {
            override fun success(invocation: ActionInvocation<*>?) {
                super.success(invocation)
                if (isNotNull(callback)) {
                    callback!!.success(invocation!!)
                }
            }

            override fun failure(invocation: ActionInvocation<*>?, operation: UpnpResponse, defaultMsg: String) {
                if (isNotNull(callback)) {
                    callback!!.fail(Exception(defaultMsg))
                }
            }
        })
    }

    override fun pause(callback: ControlCallback?) {
        val avtService = findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE)
        if (isNull(avtService)) {
            return
        }
        val controlPointImpl = controlPoint
        if (isNull(controlPointImpl)) {
            return
        }
        controlPointImpl!!.execute(object : Pause(avtService) {
            override fun success(invocation: ActionInvocation<*>?) {
                super.success(invocation)
                if (isNotNull(callback)) {
                    callback!!.success(invocation!!)
                }
            }

            override fun failure(invocation: ActionInvocation<*>?, operation: UpnpResponse, defaultMsg: String) {
                if (isNotNull(callback)) {
                    callback!!.fail(Exception(defaultMsg))
                }
            }
        })
    }

    override fun stop(callback: ControlCallback?) {
        val avtService = findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE)
        if (isNull(avtService)) {
            return
        }
        val controlPointImpl = controlPoint
        if (isNull(controlPointImpl)) {
            return
        }
        controlPointImpl!!.execute(object : Stop(avtService) {
            override fun success(invocation: ActionInvocation<*>?) {
                super.success(invocation)
                if (isNotNull(callback)) {
                    callback!!.success(invocation!!)
                }
            }

            override fun failure(invocation: ActionInvocation<*>?, operation: UpnpResponse, defaultMsg: String) {
                if (isNotNull(callback)) {
                    callback!!.fail(Exception(defaultMsg))
                }
            }
        })
    }

    override fun seek(pos: Int, callback: ControlCallback?) {
        val avtService = findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE)
        if (isNull(avtService)) {
            return
        }
        val controlPointImpl = controlPoint
        if (isNull(controlPointImpl)) {
            return
        }
        val time = getStringTime(pos)
        controlPointImpl!!.execute(object : Seek(avtService, time) {
            override fun success(invocation: ActionInvocation<*>?) {
                super.success(invocation)
                if (isNotNull(callback)) {
                    callback!!.success(invocation!!)
                }
            }

            override fun failure(invocation: ActionInvocation<*>?, operation: UpnpResponse, defaultMsg: String) {
                if (isNotNull(callback)) {
                    callback!!.fail(Exception(defaultMsg))
                }
            }
        })
    }

    override fun setVolume(pos: Int, callback: ControlCallback?) {
        val rcService = findServiceFromSelectedDevice(ClingManager.RENDERING_CONTROL_SERVICE)
        if (isNull(rcService)) {
            return
        }
        val controlPointImpl = controlPoint
        if (isNull(controlPointImpl)) {
            return
        }
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis > mVolumeLastTime + RECEIVE_DELAY) {
            controlPointImpl!!.execute(object : SetVolume(rcService, pos.toLong()) {
                override fun success(invocation: ActionInvocation<*>?) {
                    if (isNotNull(callback)) {
                        callback!!.success(invocation!!)
                    }
                }

                override fun failure(invocation: ActionInvocation<*>?, operation: UpnpResponse, defaultMsg: String) {
                    if (isNotNull(callback)) {
                        callback!!.fail(Exception(defaultMsg))
                    }
                }
            })
        }
        mVolumeLastTime = currentTimeMillis
    }

    /**
     * 静音
     * @param desiredMute
     * @param callback
     */
    override fun setMute(desiredMute: Boolean, callback: ControlCallback?) {
        val rcService = findServiceFromSelectedDevice(ClingManager.RENDERING_CONTROL_SERVICE)
        if (isNull(rcService)) {
            return
        }
        val controlPointImpl = controlPoint
        if (isNull(controlPointImpl)) {
            return
        }
        controlPointImpl!!.execute(object : SetMute(rcService, desiredMute) {
            override fun success(invocation: ActionInvocation<*>?) {
                if (isNotNull(callback)) {
                    callback!!.success(invocation!!)
                }
            }

            override fun failure(invocation: ActionInvocation<*>?, operation: UpnpResponse, defaultMsg: String) {
                if (isNotNull(callback)) {
                    callback!!.fail(Exception(defaultMsg))
                }
            }
        })
    }

    /**
     * 设置片源，用于首次播放
     *
     * @param url   片源地址
     * @param callback  回调
     */
    private fun setAVTransportURI(url: String?,title:String, itemType: ClingPlayType, callback: ControlCallback) {
        if (isNull(url)) {
            return
        }
        val metadata = pushMediaToRender(url, url.hashCode().toString(), title, itemType)
        val avtService = findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE)
        if (isNull(avtService)) {
            return
        }
        val controlPointImpl = controlPoint
        if (isNull(controlPointImpl)) {
            return
        }
        controlPointImpl!!.execute(object : SetAVTransportURI(avtService, url, metadata) {
            override fun success(invocation: ActionInvocation<*>?) {
                super.success(invocation)
                if (isNotNull(callback)) {
                    callback.success(invocation!!)
                }
            }

            override fun failure(invocation: ActionInvocation<*>?, operation: UpnpResponse, defaultMsg: String) {
                if (isNotNull(callback)) {
                    callback.fail(Exception(defaultMsg))
                }
            }
        })
    }

    /**
     * Get position info
     *获取mediarender的播放位置信息
     * @param callback
     */
    override fun getPositionInfo(callback: ControlReceiveCallback?) {
        val avtService = findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE)
        if (isNull(avtService)) {
            return
        }
        Log.d(TAG, "Found media render service in device, sending get position")
        val getPositionInfo: GetPositionInfo = object : GetPositionInfo(avtService) {
            override fun failure(invocation: ActionInvocation<*>?, operation: UpnpResponse, defaultMsg: String) {
                if (isNotNull(callback)) {
                    callback!!.fail(Exception(defaultMsg))
                }
            }

            override fun success(invocation: ActionInvocation<*>?) {
                super.success(invocation)
                if (isNotNull(callback)) {
                    callback!!.success(invocation!!)
                }
            }

            override fun received(invocation: ActionInvocation<*>?, info: PositionInfo) {
                if (isNotNull(callback)) {
                    callback!!.receive(info.trackElapsedSeconds)
                }
            }
        }
        val controlPointImpl = controlPoint
        if (isNull(controlPointImpl)) {
            return
        }
        controlPointImpl!!.execute(getPositionInfo)
    }

    override fun getVolume(callback: ControlReceiveCallback?) {
        val avtService = findServiceFromSelectedDevice(ClingManager.RENDERING_CONTROL_SERVICE)
        if (isNull(avtService)) {
            return
        }
        val getVolume: GetVolume = object : GetVolume(avtService) {
            override fun received(actionInvocation: ActionInvocation<*>?, currentVolume: Int) {
                if (isNotNull(callback)) {
                    callback!!.receive(currentVolume.toLong())
                }
            }

            override fun failure(invocation: ActionInvocation<*>?, operation: UpnpResponse, defaultMsg: String) {
                if (isNotNull(callback)) {
                    callback!!.fail(Exception(defaultMsg))
                }
            }
        }
        val controlPointImpl = controlPoint
        if (isNull(controlPointImpl)) {
            return
        }
        controlPointImpl!!.execute(getVolume)
    }

    var currentState: ClingPlayState
        get() = mCurrentState
        set(currentState) {
            if (mCurrentState !== currentState) {
                mCurrentState = currentState
            }
        }

    private fun pushMediaToRender(url: String?, id: String, name: String, itemType: ClingPlayType): String {
        val size: Long = 0
        val res = Res(MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), size, url)
        val creator = "cling"
        val parentId = "0"
        val metadata: String = when (itemType) {
            TYPE_IMAGE -> {
                val imageItem = ImageItem(id, parentId, name, creator, res)
                createItemMetadata(imageItem)
            }

            TYPE_VIDEO -> {
                val videoItem = VideoItem(id, parentId, name, creator, res)
                createItemMetadata(videoItem)
            }

            TYPE_AUDIO -> {
                val audioItem = AudioItem(id, parentId, name, creator, res)
                createItemMetadata(audioItem)
            }
        }
        return metadata
    }

    /**
     * 创建投屏的参数
     *
     * @param item
     * @return
     */
    private fun createItemMetadata(item: DIDLObject): String {
        val metadata = StringBuilder()
        metadata.append(DIDL_LITE_HEADER)
        metadata.append(
            String.format(
                "<item id=\"%s\" parentID=\"%s\" restricted=\"%s\">",
                item.id,
                item.parentID,
                if (item.isRestricted) "1" else "0"
            )
        )
        metadata.append(String.format("<dc:title>%s</dc:title>", item.title))
        var creator = item.creator
        if (creator != null) {
            creator = creator.replace("<".toRegex(), "_")
            creator = creator.replace(">".toRegex(), "_")
        }
        metadata.append(String.format("<upnp:artist>%s</upnp:artist>", creator))
        metadata.append(String.format("<upnp:class>%s</upnp:class>", item.clazz.value))
        val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val now = Date()
        val time = sdf.format(now)
        metadata.append(String.format("<dc:date>%s</dc:date>", time))
        val res = item.firstResource
        if (res != null) {
            // protocol info
            var protocolinfo = ""
            val pi = res.protocolInfo
            if (pi != null) {
                protocolinfo = String.format(
                    "protocolInfo=\"%s:%s:%s:%s\"", pi.protocol, pi.network, pi.contentFormatMimeType, pi
                        .additionalInfo
                )
            }

            // resolution, extra info, not adding yet
            var resolution = ""
            if (res.resolution != null && res.resolution.length > 0) {
                resolution = String.format("resolution=\"%s\"", res.resolution)
            }

            // duration
            var duration = ""
            if (res.duration != null && res.duration.length > 0) {
                duration = String.format("duration=\"%s\"", res.duration)
            }

            // res begin
            //            metadata.append(String.format("<res %s>", protocolinfo)); // no resolution & duration yet
            metadata.append(String.format("<res %s %s %s>", protocolinfo, resolution, duration))

            // url
            val url = res.value
            metadata.append(url)

            // res end
            metadata.append("</res>")
        }
        metadata.append("</item>")
        metadata.append(DIDL_LITE_FOOTER)
        return metadata.toString()
    }

    companion object {
        private val TAG = ClingPlayControl::class.java.simpleName

        /** 每次接收 500ms 延迟  */
        private const val RECEIVE_DELAY = 500
        private const val DIDL_LITE_FOOTER = "</DIDL-Lite>"
        private const val DIDL_LITE_HEADER =
            "<?xml version=\"1.0\"?>" + "<DIDL-Lite " + "xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" " +
                    "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " + "xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " +
                    "xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\">"
    }
}