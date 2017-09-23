package com.hengda.shzkjg.m.base

import android.content.Context
import com.hengda.shzkjg.m.tool.MyDatabaseOpenHelper
import com.hengda.shzkjg.m.tool.SDCardUtil
import com.knightdavion.kotlin.ibiliplayer.extensions.DelegatesExt
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.SelectQueryBuilder
import java.io.File

/**
 * Created by lenovo on 2017/8/8.
 */


open class AppConfig {
    companion object {
        const val FIRST_CONFIG = "FIRST_CONFIG"
        const val FIRST_CREATE = "FIRST_CREATE"
        const val COUNT = "COUNT"
        var AUTO = 1
        //        var Recard: String by DelegatesExt.preference(App.instance, DEVICENUM, DEFALUT_DEVICENUM)
        var FirstConfig: Boolean by DelegatesExt.preference(App.instance, FIRST_CONFIG, true)
        var FirstCreate: Boolean by DelegatesExt.preference(App.instance, FIRST_CREATE, true)
        var Count: Int by DelegatesExt.preference(App.instance, COUNT, 69)
        //        var deviceNum: String by DelegatesExt.preference(App.instance, DEVICENUM, DEFALUT_DEVICENUM)
        var reCard = arrayOfNulls<String>(5)
        const val DEFAULT_SSID = "SHZBWG"
        const val TEXTTEXT_SSID = "GLBWG"
        var ISPLAY:Boolean=false
        var ISHOME:Boolean=false
        var CONSTANT = 0
        var TYPECONFIG=1
        //    馆方内网-默认网络请求服务器地址
        const val DEFAULT_IP_PORT_I = "192.168.16.30/12345"
        //    馆方外网-默认网络请求服务器地址
        val database: MyDatabaseOpenHelper
            get() = MyDatabaseOpenHelper.getInstance(App.instance)

        fun getDefaultFileDir(): String {
            return SDCardUtil.getSDCardPath() + ".Hd_SHZBWG_res/"
        }

        fun getMapPath(floor: Int): String {
            return getDefaultFileDir() + "map/" + floor
        }

        fun getRoutePath(type: String): String {
            return getDefaultFileDir() + "map/" + "route_"+type+".png"
        }

        //展品列表图片
        fun getImgPath(fileNo: String): String {
            return getDefaultFileDir() + "exhibit/" + fileNo + "/" + fileNo + ".png"
        }

        //展品mark图片
        fun getMarkPath(fileNo: String): String {
            return getDefaultFileDir() + "exhibit/" + fileNo + "/" + fileNo + "_map.png"
        }

        //展厅图片
        fun getImgExhibitionPath(mapId: Int, ExhibitId: Int): String {
            return getDefaultFileDir() + "exhibition/" + mapId + "_" + ExhibitId + ".png"
        }

        fun getVoicePath(fileNo: String, type: String): String {
            return getDefaultFileDir() + "exhibit/" + fileNo + "/" + fileNo + type
        }

        fun getWebPath(type: String): String {
            return getDefaultFileDir() + "introduction/" + type + ".html"
        }

        fun isResExist(): Boolean {
            var file: File = File(getMapPath(1) + "/img.png")
            return file.exists()
        }

        fun <T : Any> SelectQueryBuilder.parseList(parser: (Map<String, Any?>) -> T): List<T> =
                parseList(object : MapRowParser<T> {
                    override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
                })
    }
}
