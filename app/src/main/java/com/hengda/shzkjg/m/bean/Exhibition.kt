package com.hengda.shzkjg.m.bean

import java.io.Serializable

/**
 * Created by lenovo on 2017/8/9.
 */
class Exhibition(var map: MutableMap<String, Any?>):Serializable {
    var MapId :Int by map
    var ExhibitId :Int by map
    var ExhibitName :String by map

}