package com.shetj.clinglib

import android.content.*
import android.os.Bundle
import android.util.Log
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.shetj.clinglib.databinding.ActivityMainBinding
import me.shetj.base.ktx.setAppearance
import me.shetj.cling.*
import me.shetj.cling.callback.ControlCallback
import me.shetj.cling.entity.*
import me.shetj.cling.ClingManager
import me.shetj.base.ktx.showToast
import me.shetj.base.ktx.toJson
import me.shetj.base.mvvm.viewbind.BaseBindingActivity
import me.shetj.base.mvvm.viewbind.BaseViewModel
import me.shetj.base.tools.app.Tim

class MainActivity : BaseBindingActivity<ActivityMainBinding, BaseViewModel>() {
    private lateinit var mAdapter: DeviceAdapter

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
            if (ClingManager.getInstant().getPlayState().value == ClingPlayState.STOP){
                ClingManager.playNew(url,"直播视频介绍", ClingPlayType.TYPE_VIDEO, object : ControlCallback {
                    override fun success(response: Any) {
                        "投放成功".showToast()
                        ClingManager.getInstant().registerAVTransport(this@MainActivity)
                        ClingManager.getInstant().registerRenderingControl(this@MainActivity)
                    }

                    override fun fail(response: Exception) {

                    }
                })
            }else{
                ClingManager.play()
            }
        }

        mBinding.stopScreen.setOnClickListener {
            ClingManager.stop(object : ControlCallback {
                override fun success(response: Any) {
                    "停止成功".showToast()
                }

                override fun fail(response: Exception) {
                    "停止失败".showToast()
                }
            })
        }
        mBinding.search.setOnClickListener {
            ClingManager.getInstant().searchDevices()
        }
        showRecycleView()
    }

    private fun initData() {
        bindServices()

        // TODO 可能需要轮询获取当前进度
//        ClingManager.getInstant().getCurPosition().observe(this) {
//            mBinding.playPosition.text = "当前进度：$it"
//        }

        ClingManager.getInstant().getCurVolume().observe(this) {
            mBinding.playVolume.text = "当前音量：$it"
        }

        ClingManager.getInstant().getPlayState().observe(this) {
            mBinding.playState.text = "当前状态：${it.type}"
        }
    }


    private fun bindServices() { // Bind UPnP service
        mUpnpServiceConnection = startBindUpnpService {
            Log.i("Cling", "startBindUpnpService OK")
            Log.i("Cling", ClingManager.getInstant().dmrDevices.toJson().toString())

        }
    }

    private fun showRecycleView() {
        mAdapter = DeviceAdapter().apply {
            setOnItemClickListener { _, _, position ->
                getItem(position).apply {
                    ClingManager.getInstant().setSelectDevice(this)
                    setPlay(position)
                    mBinding.tvMsg.text = "您选择了：${this.name}"
                }
            }
        }
        mBinding.iRecyclerView.adapter = mAdapter
        mBinding.iRecyclerView.addItemDecoration(MaterialDividerItemDecoration(this, MaterialDividerItemDecoration.VERTICAL))
        ClingManager.getInstant().getSearchDevices().observe(this) {
            mAdapter.setList(it)
            Log.i("Cling", " mAdapter.data.size = ${mAdapter.data.size}")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        stopUpnpService(mUpnpServiceConnection)
        ClingManager.getInstant().destroy()
    }
}