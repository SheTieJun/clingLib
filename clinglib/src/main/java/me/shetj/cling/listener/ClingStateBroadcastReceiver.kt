package me.shetj.cling.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import me.shetj.cling.entity.Intents

class ClingStateBroadcastReceiver(private val handler: Handler) : BroadcastReceiver() {

    companion object{
        /** 连接设备状态: 播放状态  */
        val PLAY_ACTION = 0xa1
        /** 连接设备状态: 暂停状态  */
        val PAUSE_ACTION = 0xa2
        /** 连接设备状态: 停止状态  */
        val STOP_ACTION = 0xa3
        /** 连接设备状态: 转菊花状态  */
        val TRANSITIONING_ACTION = 0xa4
        /** 获取进度  */
        val GET_POSITION_INFO_ACTION = 0xa5
        /** 投放失败  */
        val ERROR_ACTION = 0xa5
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intents.ACTION_PLAYING -> {
                handler.sendEmptyMessage(PLAY_ACTION)
            }
            Intents.ACTION_PAUSED_PLAYBACK -> {
                handler.sendEmptyMessage(PAUSE_ACTION)
            }
            Intents.ACTION_STOPPED -> {
                handler.sendEmptyMessage(STOP_ACTION)
            }
            Intents.ACTION_TRANSITIONING -> {
                handler.sendEmptyMessage(TRANSITIONING_ACTION)
            }
        }
    }
}