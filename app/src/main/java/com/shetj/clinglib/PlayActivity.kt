package com.shetj.clinglib

import android.content.Context
import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import com.shetj.clinglib.databinding.ActivityPlayBinding
import me.shetj.base.base.AbBindingActivity
import me.shetj.base.ktx.logI
import me.shetj.base.ktx.start

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2023/9/18<br>
 */
class PlayActivity:AbBindingActivity<ActivityPlayBinding>() {

    companion object{
        fun start(context: Context,url:String){
           context.start(Intent(context,PlayActivity::class.java).apply {
               putExtra("url",url)
           })
        }
    }


    @androidx.media3.common.util.UnstableApi
    override fun  onInitialized() {
        super.onInitialized()
        intent.getStringExtra("url")?.let {
            mBinding.tvMsg.text= "当前播放链接：$it"
            "当前播放链接：$it".logI("PlayActivity")
            val mediaSource = DefaultMediaSourceFactory(this)
                .createMediaSource(MediaItem.fromUri(it))
            val player = ExoPlayer.Builder(this).build()
            player.setMediaSource(mediaSource)
            player.prepare()
            mBinding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            mBinding.playerView.player = player
        }
    }

}