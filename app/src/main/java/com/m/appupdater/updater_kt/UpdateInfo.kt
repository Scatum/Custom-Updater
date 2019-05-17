package com.m.appupdater.updater_kt

import java.io.Serializable

class UpdateInfo : Serializable {
    var code: String? = null
    var name: String? = null
    var apk_url: String? = null

    constructor() {}

    constructor(code: String, name: String, apk_url: String) {
        this.code = code
        this.name = name
        this.apk_url = apk_url
    }
}