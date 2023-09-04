package com.shetj.clinglib

import android.graphics.Color
import com.android.cling.entity.ClingDevice
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import me.shetj.base.base.BaseSAdapter

class DeviceAdapter(data:MutableList<ClingDevice>?=null) : BaseSAdapter<ClingDevice, BaseViewHolder>(R.layout.item_recycle_string,data) {

    private var playPosition = -1

    private var oldPosition = -1

    override fun convert(holder: BaseViewHolder, item: ClingDevice) {
        item.let {
            if (holder.layoutPosition == playPosition) {
                holder.setText(R.id.tv_string, "选中:【${holder.bindingAdapterPosition}】" +item.name)
            } else {
                holder.setText(R.id.tv_string,"【${holder.bindingAdapterPosition}】" +item.name)
            }
            holder.setTextColor(R.id.tv_string,when(holder.layoutPosition == playPosition){
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

    fun removeDevice(device: ClingDevice) {
        data.find {
            it.device.equals(device)
        }?.apply {
            remove(this)
        }
    }
}
