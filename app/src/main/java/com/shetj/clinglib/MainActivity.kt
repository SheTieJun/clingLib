package com.shetj.clinglib

import android.content.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.cling.ClingDLNAManager
import com.android.cling.control.DeviceControl
import com.android.cling.control.OnDeviceControlListener
import com.android.cling.control.ServiceActionCallback
import com.android.cling.entity.ClingPlayType
import com.android.cling.startBindUpnpService
import com.android.cling.stopUpnpService
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.shetj.clinglib.databinding.ActivityMainBinding
import me.shetj.base.ktx.setAppearance
import me.shetj.base.ktx.showToast
import me.shetj.base.ktx.toJson
import me.shetj.base.mvvm.viewbind.BaseBindingActivity
import me.shetj.base.mvvm.viewbind.BaseViewModel
import me.shetj.base.tools.app.Tim
import org.fourthline.cling.model.meta.Device

class MainActivity : BaseBindingActivity<ActivityMainBinding, BaseViewModel>() {
    private lateinit var mAdapter: DeviceAdapter
    private var control: DeviceControl ?=null

    //播放、停止相关控制


    private var mUpnpServiceConnection: ServiceConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tim.setLogAuto(true)
    }

    override fun initBaseView() {
        super.initBaseView()
        setAppearance(true)
        initView()
        initData()
    }


    fun initView() {
        mBinding.startScreen.setOnClickListener {
            val url = "https://200024424.vod.myqcloud.com/200024424_709ae516bdf811e6ad39991f76a4df69.f20.mp4"
            control?.setAVTransportURI(url,"直播视频介绍", ClingPlayType.TYPE_VIDEO, object : ServiceActionCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    "投放成功".showToast()
                    control?.play() //有些还要重新调用一次播放
                }

                override fun onFailure(msg: String) {
                    "投放失败:$msg".showToast()
                }
            })
        }

        mBinding.stopScreen.setOnClickListener {
            control?.stop(object : ServiceActionCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    "停止成功".showToast()
                }

                override fun onFailure(msg: String) {
                    "停止失败".showToast()
                }
            })
        }
        mBinding.search.setOnClickListener {
            "开始搜索...".showToast()
            ClingDLNAManager.getInstant().searchDevices()
        }
        showRecycleView()
    }

    private fun initData() {
        bindServices()
    }


    private fun bindServices() { // Bind UPnP service
        mUpnpServiceConnection = startBindUpnpService {
            Log.i("Cling", "startBindUpnpService OK")
        }
    }

    private fun showRecycleView() {
        mAdapter = DeviceAdapter().apply {
            setOnItemClickListener { _, _, position ->
                getItem(position).apply {
                    control = ClingDLNAManager.getInstant().connectDevice(this, object : OnDeviceControlListener {
                        override fun onConnected(device: Device<*, *, *>) {
                            super.onConnected(device)
                            Toast.makeText(this@MainActivity, "连接成功", Toast.LENGTH_SHORT).show()
                        }

                        override fun onDisconnected(device: Device<*, *, *>) {
                            super.onDisconnected(device)
                            Toast.makeText(this@MainActivity, "无法连接: ${device.details.friendlyName}", Toast.LENGTH_SHORT).show()
                        }
                    })
                    control?.addControlObservers()
                    setPlay(position)
                    mBinding.tvMsg.text = "您选择了：${this.name}"
                }
            }
        }
        mBinding.iRecyclerView.adapter = mAdapter
        mBinding.iRecyclerView.addItemDecoration(MaterialDividerItemDecoration(this, MaterialDividerItemDecoration.VERTICAL))
        ClingDLNAManager.getInstant().getSearchDevices().observe(this) {
            mAdapter.setList(it)
        }
    }

    /**
     * Add control observers
     * 监听control的状态
     */
    private fun DeviceControl.addControlObservers() {
        getCurrentState().observe(this@MainActivity) {
            mBinding.playState.text = "当前状态：$it"
        }
        getCurrentPositionInfo().observe(this@MainActivity){
            mBinding.playPosition.text = "当前进度：${it.toJson()}"
        }
        getCurrentVolume().observe(this@MainActivity){
            mBinding.playVolume.text = "当前音量：$it"
        }
        getCurrentMute().observe(this@MainActivity) {
            mBinding.playVolume.text = "当前静音：$it"
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        stopUpnpService(mUpnpServiceConnection)
        ClingDLNAManager.getInstant().destroy()
    }
}