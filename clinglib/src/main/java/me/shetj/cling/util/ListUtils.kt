package me.shetj.cling.util

/**
 * 说明：
 * 作者：zhouzhan
 * 日期：17/6/28 16:54
 */
object ListUtils {
    @JvmStatic
    fun isEmpty(list: Collection<*>?): Boolean {
        return !(list != null && list.isNotEmpty())
    }
}