package com.android.cling.entity

enum class ClingPlayState(val type: Int) {

    PLAY(1), //
    PAUSE(2),
    STOP(3),
    BUFFER(4),
    ERROR(5),

    //    // 以下不算设备状态, 只是常量
    //    /** 主动轮询获取播放进度(在远程设备不支持播放进度回传时使用)  */
    //    const val GET_POSITION_POLING = 6
    //    /** 远程设备播放进度回传  */
    //    const val POSITION_CALLBACK = 7
    //    /** 投屏端播放完成  */
    //    const val PLAY_COMPLETE = 8

    GET_POSITION_POLING(6),
    POSITION_CALLBACK(7),
    PLAY_COMPLETE(8);


}