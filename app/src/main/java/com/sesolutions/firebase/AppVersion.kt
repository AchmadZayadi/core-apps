package com.sesolutions.firebase

import com.sesolutions.BuildConfig

class AppVersion {
    val currentVersion: Int = 0
    val minVersion: Int = 0
    var isForceUpdate: Boolean = false
        private set
    val isClearAppData: Boolean = false
    val updateTitle: String? = null
    val updateDescription: String? = null
    val updateButtonText: String? = null
    val updateCancelText: String? = null
    val dontShowText: String? = null
    val isCanShowCheckbox: Boolean = false
    val sslCertificate: String? = null

    fun canUpdate(): Boolean {
        try {
            //if version same
            if (BuildConfig.VERSION_CODE >= currentVersion)
                return false
            else {
                //it means update available

                // so check for FORCE UPDATE

                if (BuildConfig.VERSION_CODE < minVersion || isForceUpdate) {
                    isForceUpdate = true
                }
                if (isClearAppData) {
                    //TODO 15/9/2018 add clear app data code here
                }
                return true
            }
        } catch (ignore: NumberFormatException) {
            return false
        }

    }
}
