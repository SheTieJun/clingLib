package com.lizhiweik.clinglib

import android.graphics.Color
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.cling.entity.ClingDevice

class AutoRecycleViewAdapter(data: MutableList<ClingDevice>?) : BaseQuickAdapter<ClingDevice, BaseViewHolder>(R.layout.item_recycle_string, data) {

    private var playPosition = -1

    private var oldPosition = -1

    var i = 0

    override fun convert(helper: BaseViewHolder, item: ClingDevice?) {
        item?.let {
            if (helper.layoutPosition == playPosition) {
                helper.setText(R.id.tv_string, "选中:" +item.name)
            } else {
                helper.setText(R.id.tv_string, item.name)
            }
            helper.setTextColor(R.id.tv_string,when(helper.layoutPosition == playPosition){
                true ->  Color.RED
                false -> Color.BLACK
            })
        }
    }

    fun setPlay(i: Int) {
        if (playPosition != i) {
            playPosition = i
            if (oldPosition != -1) {
                notifyItemChanged(oldPosition)
            }
            oldPosition = playPosition
            notifyItemChanged(playPosition)
        }
    }

    fun removeDevice(device:  ClingDevice) {
        data.find {
            it.device?.equals(device) ?: false
        }?.apply {
            remove(this)
        }
    }
}
