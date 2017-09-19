package com.hengda.shzkjg.m.bean

/**
 * Created by lenovo on 2017/8/8.
 */
class MapInfo(var map: MutableMap<String, Any?>) {
    var Id: Int by map
    var Width: Int by map
    var Height: Int by map
    var ExhibitionNo: Int by map
    var ExhibitNo: Int by map
    override fun toString(): String {
        return "MapId:${Id}--width: ${Width}--height: ${Height}"
    }
}