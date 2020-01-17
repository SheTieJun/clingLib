package me.shetj.cling.entity

/**
 * TYPE_IMAGE = 1;
 * TYPE_VIDEO = 2;
 * TYPE_AUDIO = 3;
 */
enum class ClingPlayType(type: Int) {
        TYPE_IMAGE(1),
        TYPE_VIDEO(2),
        TYPE_AUDIO(3);
}