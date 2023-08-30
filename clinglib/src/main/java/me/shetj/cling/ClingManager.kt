package me.shetj.cling

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import me.shetj.cling.callback.ControlCallback
import me.shetj.cling.callback.ControlReceiveCallback
import me.shetj.cling.control.ClingPlayControl
import me.shetj.cling.control.IPlayControl
import me.shetj.cling.entity.ClingDevice
import me.shetj.cling.entity.ClingDeviceList
import me.shetj.cling.entity.ClingPlayState
import me.shetj.cling.entity.ClingPlayType
import me.shetj.cling.listener.BrowseRegistryListener
import me.shetj.cling.manager.DeviceManager
import me.shetj.cling.manager.IClingManager
import me.shetj.cling.manager.IDeviceManager
import me.shetj.cling.service.ClingUpnpService
import me.shetj.cling.util.Utils
import me.shetj.cling.util.Utils.isNull
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.controlpoint.ControlPoint
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.model.types.DeviceType
import org.fourthline.cling.model.types.ServiceType
import org.fourthline.cling.model.types.UDADeviceType
import org.fourthline.cling.model.types.UDAServiceType
import org.fourthline.cling.registry.Registry
import org.fourthline.cling.registry.RegistryListener

class ClingManager private constructor() : IClingManager {
    private var mUpnpService: AndroidUpnpService? = null
    private val mBrowseRegistryListener = BrowseRegistryListener()  //设备监听
    private val mClingPlayControl: IPlayControl = ClingPlayControl()
    private val deviceManager = DeviceManager()
    private var mDeviceManager: IDeviceManager? = null
    private val clingPlayState = MutableLiveData<ClingPlayState>() //当前状态
    private val currentDevices = MutableLiveData<MutableList<ClingDevice>>()  //当前链接的设备
    private val currentPosition = MutableLiveData<Int>() //当前进度,暂时不知道有没有用
    private val curVolume = MutableLiveData<Int>() //当前音量
    private var referer: String? = null


    private val isInit: Boolean
        get() = !isNull(mUpnpService)

    override fun searchDevices() {
        if (isInit) {
            ClingDeviceList.clear()
            registry!!.removeAllRemoteDevices()
            registry!!.removeAllLocalDevices()
            controlPoint?.search()
        }
    }

    fun getSearchDevices(): MutableLiveData<MutableList<ClingDevice>> {
        return currentDevices
    }

    fun getPlayState(): MutableLiveData<ClingPlayState> {
        return clingPlayState
    }

    //TODO 当前进度
    fun getCurPosition(): MutableLiveData<Int> {
        return currentPosition
    }

    fun getCurVolume(): MutableLiveData<Int> {
        return curVolume
    }

    internal fun updateCurrentPlayState(state: ClingPlayState) {
        clingPlayState.postValue(state)
    }

    internal fun updateCurrentDevices(devices: MutableList<ClingDevice>) {
        currentDevices.postValue(devices)
    }

    internal fun updatePlayPosition(position: Int) {
        currentPosition.postValue(position)
    }

    override val dmrDevices: Collection<ClingDevice>?
        get() {
            if (isNull(mUpnpService)) {
                return null
            }
            val devices = registry!!.getDevices(DMR_DEVICE_TYPE)
            if (Utils.isEmpty(devices)) {
                return null
            }
            val clingDevices: MutableCollection<ClingDevice> = ArrayList()
            for (device in devices) {
                if (device is RemoteDevice) {
                    val clingDevice = ClingDevice(device)
                    clingDevices.add(clingDevice)
                }
            }
            return clingDevices
        }

    override val controlPoint: ControlPoint?
        get() {
            if (isNull(mUpnpService)) {
                return null
            }
            return mUpnpService!!.controlPoint
        }


    override val registry: Registry?
        get() {
            if (isNull(mUpnpService)) {
                return null
            }
            return mUpnpService!!.registry
        }


    override fun setSelectDevice(selectedDevice: ClingDevice) {
        if (!isNull(mDeviceManager)) {
            mDeviceManager!!.setSelectedDevice(selectedDevice)
        }
    }

    override fun getSelectedDevice(): ClingDevice? {
        return mDeviceManager?.getSelectedDevice()
    }

    override fun cleanSelectedDevice() {
        if (isNull(mDeviceManager)) {
            return
        }
        mDeviceManager!!.cleanSelectedDevice()
    }

    override fun registerAVTransport() {
        if (isNull(mDeviceManager)) return
        mDeviceManager!!.registerAVTransport()
    }

    override fun registerRenderingControl() {
        if (isNull(mDeviceManager)) return
        mDeviceManager!!.registerRenderingControl()
    }

    override fun setUpnpService(upnpService: AndroidUpnpService?) {
        mUpnpService = upnpService
    }

    override fun setDeviceManager(deviceManager: IDeviceManager?) {
        mDeviceManager = deviceManager
    }

    override fun addListener(registryListener: RegistryListener) {
        registry?.addListener(registryListener)
    }

    override fun removeListener(registryListener: RegistryListener) {
        registry?.removeListener(registryListener)
    }


    override fun destroy() {
        registry?.listeners?.clear()
        mDeviceManager!!.destroy()
    }


    fun startBindUpnpService(context: Context, success: (() -> Unit)? = null): ServiceConnection {
        val mServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val upnpService: AndroidUpnpService = service as AndroidUpnpService
                setUpnpService(upnpService)
                setDeviceManager(deviceManager)
                addListener(mBrowseRegistryListener)
                searchDevices()
                success?.invoke()
            }

            override fun onServiceDisconnected(className: ComponentName) {
                setUpnpService(null)
            }
        }

        val serviceIntent = Intent(context, ClingUpnpService::class.java)
        context.applicationContext.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
        return mServiceConnection
    }

    fun stopBindService(context: Context, mServiceConnection: ServiceConnection?) {
        mServiceConnection?.let { context.unbindService(it) }
    }

    internal fun updateCurrentVolume(volume: Int?) {
        curVolume.postValue(volume)
    }

    internal fun getReferer(): String? {
        return referer
    }

    fun setReferer(referer: String?) {
        this.referer = referer
    }

    companion object {
        internal val AV_TRANSPORT_SERVICE: ServiceType = UDAServiceType("AVTransport")
        internal val RENDERING_CONTROL_SERVICE: ServiceType = UDAServiceType("RenderingControl")
        internal val DMR_DEVICE_TYPE: DeviceType = UDADeviceType("MediaRenderer")
        private var INSTANCE: ClingManager? = null

        @Synchronized
        fun getInstant(): ClingManager {
            if (INSTANCE == null) {
                INSTANCE = ClingManager()
            }
            return INSTANCE!!
        }

        /**
         * Set referer
         * 用于一些播放链接有防盗链的情况，需要设置referer
         * TODO 测试
         */
        fun setReferer(referer: String?) {
            getInstant().setReferer(referer)
        }

        fun playNew(url: String, title: String, itemType: ClingPlayType = ClingPlayType.TYPE_VIDEO, callback: ControlCallback? = null) {
            getInstant().mClingPlayControl.playNew(url, title, itemType, callback)
        }

        fun play(callback: ControlCallback? = null) {
            getInstant().mClingPlayControl.play(callback)
        }

        fun pause(controlCallback: ControlCallback? = null) {
            getInstant().mClingPlayControl.pause(controlCallback)
        }

        fun stop(controlCallback: ControlCallback? = null) {
            getInstant().mClingPlayControl.stop(controlCallback)
        }

        fun seek(position: Int, controlCallback: ControlCallback? = null) {
            getInstant().mClingPlayControl.seek(position, controlCallback)
        }

        fun setVolume(volume: Int, controlCallback: ControlCallback? = null) {
            getInstant().mClingPlayControl.setVolume(volume, controlCallback)
        }

        fun setMute(isMute: Boolean, controlCallback: ControlCallback? = null) {
            getInstant().mClingPlayControl.setMute(isMute, controlCallback)
        }

        fun getPositionInfo(controlCallback: ControlReceiveCallback? = null) {
            getInstant().mClingPlayControl.getPositionInfo(controlCallback)
        }

        fun getVolume(controlCallback: ControlReceiveCallback? = null) {
            getInstant().mClingPlayControl.getVolume(controlCallback)
        }
    }


}