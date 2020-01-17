/*
 * Copyright (C) 2014 Kevin Shen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.shetj.cling.entity

import android.content.Intent
import java.io.Serializable

object Intents {
    /**
     * Prefix for all intents created
     */
    const val INTENT_PREFIX = "com.zane.androidupnpdemo."
    /**
     * Prefix for all extra data added to intents
     */
    const val INTENT_EXTRA_PREFIX = INTENT_PREFIX + "extra."
    /**
     * Prefix for all action in intents
     */
    const val INTENT_ACTION_PREFIX = INTENT_PREFIX + "action."
    /**
     * Playing action for MediaPlayer
     */
    const val ACTION_PLAYING = INTENT_ACTION_PREFIX + "playing"
    /**
     * Paused playback action for MediaPlayer
     */
    const val ACTION_PAUSED_PLAYBACK = INTENT_ACTION_PREFIX + "paused_playback"
    /**
     * Stopped action for MediaPlayer
     */
    const val ACTION_STOPPED = INTENT_ACTION_PREFIX + "stopped"
    /**
     * transitioning action for MediaPlayer
     */
    const val ACTION_TRANSITIONING = INTENT_ACTION_PREFIX + "transitioning"
    /**
     * Change device action for MediaPlayer
     */
    const val ACTION_CHANGE_DEVICE = INTENT_ACTION_PREFIX + "change_device"
    /**
     * Set volume action for MediaPlayer
     */
    const val ACTION_SET_VOLUME = INTENT_ACTION_PREFIX + "set_volume"
    /**
     * 主动获取播放进度
     */
    const val ACTION_GET_POSITION = INTENT_ACTION_PREFIX + "get_position"
    /**
     * 远程设备回传播放进度
     */
    const val ACTION_POSITION_CALLBACK = INTENT_ACTION_PREFIX + "position_callback"
    /**
     * 音量回传
     */
    const val ACTION_VOLUME_CALLBACK = INTENT_ACTION_PREFIX + "volume_callback"
    /**
     * 播放进度回传值
     */
    const val EXTRA_POSITION = INTENT_ACTION_PREFIX + "extra_position"
    /**
     * 音量回传值
     */
    const val EXTRA_VOLUME = INTENT_ACTION_PREFIX + "extra_volume"
    /**
     * 投屏端播放完成
     */
    const val ACTION_PLAY_COMPLETE = INTENT_ACTION_PREFIX + "play_complete"
    /**
     * Update the lastChange value action for MediaPlayer
     */
    const val ACTION_UPDATE_LAST_CHANGE = INTENT_ACTION_PREFIX + "update_last_change"

    /**
     * Builder for generating an intent configured with extra data.
     */
    class Builder(actionSuffix: String) {
        private val intent: Intent
        /**
         * Add extra field data value to intent being built up
         *
         * @param fieldName
         * @param value
         * @return this builder
         */
        fun add(fieldName: String?, value: String?): Builder {
            intent.putExtra(fieldName, value)
            return this
        }

        /**
         * Add extra field data values to intent being built up
         *
         * @param fieldName
         * @param values
         * @return this builder
         */
        fun add(fieldName: String?, values: Array<CharSequence?>?): Builder {
            intent.putExtra(fieldName, values)
            return this
        }

        /**
         * Add extra field data value to intent being built up
         *
         * @param fieldName
         * @param value
         * @return this builder
         */
        fun add(fieldName: String?, value: Int): Builder {
            intent.putExtra(fieldName, value)
            return this
        }

        /**
         * Add extra field data value to intent being built up
         *
         * @param fieldName
         * @param values
         * @return this builder
         */
        fun add(fieldName: String?, values: IntArray?): Builder {
            intent.putExtra(fieldName, values)
            return this
        }

        /**
         * Add extra field data value to intent being built up
         *
         * @param fieldName
         * @param values
         * @return this builder
         */
        fun add(fieldName: String?, values: BooleanArray?): Builder {
            intent.putExtra(fieldName, values)
            return this
        }

        /**
         * Add extra field data value to intent being built up
         *
         * @param fieldName
         * @param value
         * @return this builder
         */
        fun add(fieldName: String?, value: Serializable?): Builder {
            intent.putExtra(fieldName, value)
            return this
        }

        /**
         * Get built intent
         *
         * @return intent
         */
        fun toIntent(): Intent {
            return intent
        }

        /**
         * Create builder with suffix
         *
         * @param actionSuffix
         */
        init {
            intent = Intent(INTENT_PREFIX + actionSuffix)
        }
    }
}