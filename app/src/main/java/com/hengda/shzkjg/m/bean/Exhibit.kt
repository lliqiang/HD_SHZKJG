package com.hengda.shzkjg.m.bean

import java.io.Serializable

/**
 * Created by lenovo on 2017/8/8.
 */

/**
 * 创建人：lenovo
 * 创建时间：2017/8/8 15:19
 * 类描述：
 */
class Exhibit(var map: MutableMap<String, Any?>) : Serializable {
    var FileNo: String by map
    var MapId: Int by map
    var Name: String by map
    var LocX: Int by map
    var LocY: Int by map
    var IsRead: Int by map
    var AutoNum: Int by map
    var IsExhibit: Int by map
    var ExhibitId: Int by map
    var pk: Int by map
    var temp: Int by map
    var isClassics: Int by map


}
