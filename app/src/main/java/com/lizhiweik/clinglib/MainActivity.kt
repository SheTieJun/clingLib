package com.lizhiweik.clinglib

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import me.shetj.base.base.BaseActivity
import me.shetj.base.base.BasePresenter
import me.shetj.base.kt.showToast
import me.shetj.cling.*
import me.shetj.cling.callback.ControlCallback
import me.shetj.cling.control.ClingPlayControl
import me.shetj.cling.entity.*
import me.shetj.cling.listener.BrowseRegistryListener
import me.shetj.cling.listener.ClingStateBroadcastReceiver
import me.shetj.cling.listener.ClingStateBroadcastReceiver.Companion.ERROR_ACTION
import me.shetj.cling.listener.ClingStateBroadcastReceiver.Companion.PAUSE_ACTION
import me.shetj.cling.listener.ClingStateBroadcastReceiver.Companion.PLAY_ACTION
import me.shetj.cling.listener.ClingStateBroadcastReceiver.Companion.STOP_ACTION
import me.shetj.cling.listener.ClingStateBroadcastReceiver.Companion.TRANSITIONING_ACTION
import me.shetj.cling.listener.DeviceListChangedListener
import me.shetj.cling.manager.ClingManager
import me.shetj.cling.util.Utils
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity<BasePresenter<*>>()   {
    private lateinit var mAdapter: AutoRecycleViewAdapter
    //播放、停止相关控制
    private val mClingPlayControl by lazy {
        ClingPlayControl()
    }
    private val mBrowseRegistryListener by lazy {
        BrowseRegistryListener()
    }
    private val mHandler: Handler = InnerHandler()
    private var mUpnpServiceConnection: ServiceConnection? =null
    private var mTransportStateBroadcastReceiver: ClingStateBroadcastReceiver ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun initView() {
        start_screen.setOnClickListener {
            val url = "https://vod.lycheer.net/e22cd48bvodtransgzp1253442168/d6b59e205285890789389180692/v.f20.mp4"
            mClingPlayControl.playUrl(url, ClingPlayType.TYPE_VIDEO,object : ControlCallback{

                override fun success(response: Any) {
                    "投放成功".showToast()
                    ClingManager.instance.registerAVTransport(this@MainActivity)
                    ClingManager.instance.registerRenderingControl(this@MainActivity)
                }

                override fun fail(response: Exception) {
                    "投放失败".showToast()
                    mHandler.sendEmptyMessage(ERROR_ACTION)
                }
            })

        }

        stop_screen.setOnClickListener {
            mClingPlayControl.stop(object :ControlCallback{

                override fun success(response: Any) {
                    "停止成功".showToast()
                }

                override fun fail(response: Exception) {
                    "停止失败".showToast()
                    mHandler.sendEmptyMessage( ERROR_ACTION)
                }
            })
        }

        SwipeRefreshLayout.setOnRefreshListener {
            refreshDeviceList {
                if (this?.isNotEmpty() == true){
                    mAdapter.setNewData(this.toMutableList())
                }
                SwipeRefreshLayout.isRefreshing = false
            }
            AndroidSchedulers.mainThread().scheduleDirect({
                if (SwipeRefreshLayout.isRefreshing){
                    SwipeRefreshLayout.isRefreshing = false
                    "暂无搜索到设备".showToast()
                }
            },2,TimeUnit.SECONDS)
        }
        showRecycleView()

    }
    override fun initData() {

        bindServices()
        registerReceivers()

    }


    private fun bindServices() { // Bind UPnP service
        mUpnpServiceConnection =   startBindService{
            ClingManager.instance.addListener(mBrowseRegistryListener)
            ClingManager.instance.searchDevices()
        }
    }


    private fun registerReceivers() { //Register play status broadcast
        mTransportStateBroadcastReceiver = getAndRegisterClingStateBroadcastReceiver(mHandler)
    }



    private fun showRecycleView() {
        mAdapter = AutoRecycleViewAdapter(ClingDeviceList.getClingDeviceList().toMutableList()).apply {
            setOnItemClickListener { _, _, position ->
                getItem(position)?.apply {
                    // 选择连接设备
                    if (Utils.isNull(this)) {
                        return@setOnItemClickListener
                    }
                    ClingManager.instance.selectedDevice = this
                    val device  = this.device
                    if (Utils.isNull(device)) {
                        return@setOnItemClickListener
                    }
                    setPlay(position)
                    tv_msg.text = "您选择了：${this.name}"
                }

            }
            iRecyclerView.adapter = this
            iRecyclerView?.apply {
                iRecyclerView.layoutManager = LinearLayoutManager(rxContext)
            }
            setEmptyView(R.layout.base_empty_date_view)
        }

        // 设置发现设备监听
        mBrowseRegistryListener.setOnDeviceListChangedListener(object : DeviceListChangedListener {
            override fun onDeviceRemoved(device: IDevice<*>?) {
                runOnUiThread { mAdapter.remove(device as ClingDevice) }
            }

            override fun onDeviceAdded(device: IDevice<*>?) {
                runOnUiThread { mAdapter.addData(device as ClingDevice)}
            }

        })
    }


    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
        mUpnpServiceConnection?.let {
            unbindService(it)
        }
        mTransportStateBroadcastReceiver?.unregisterReceiver()
        ClingManager.instance.removeListener(mBrowseRegistryListener)
        ClingManager.instance.destroy()
    }

    inner class InnerHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                PLAY_ACTION -> {
                    Toast.makeText(this@MainActivity, "正在投放", Toast.LENGTH_SHORT).show()
                    mClingPlayControl.currentState = ClingPlayState.PLAY
                }
                PAUSE_ACTION -> {
                    Timber.i(  "Execute PAUSE_ACTION")
                    mClingPlayControl.currentState = ClingPlayState.PAUSE
                }
                STOP_ACTION -> {
                    Timber.i(  "Execute STOP_ACTION")
                    mClingPlayControl.currentState = ClingPlayState.STOP
                }
                TRANSITIONING_ACTION -> {
                    Timber.i( "Execute TRANSITIONING_ACTION")
                    "正在连接".showToast()
                }
                ERROR_ACTION -> {
                    Timber.i("Execute ERROR_ACTION")
                    "投放失败" .showToast()
                }
            }
        }
    }
}