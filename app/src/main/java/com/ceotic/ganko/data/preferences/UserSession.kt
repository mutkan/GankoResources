package com.ceotic.ganko.data.preferences

import android.content.SharedPreferences
import com.ceotic.ganko.util.save
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSession @Inject constructor(val prefs: SharedPreferences){

    var token:String
        get() = prefs.getString(KEY_TOKEN,"")
        set(value) = prefs.save(KEY_TOKEN to value)

    var logged: Boolean
        get() = prefs.getBoolean(KEY_LOGGED, false)
        set(value) = prefs.save(KEY_LOGGED to value)

    var userId: String?
        get() = prefs.getString(KEY_USERID, null)
        set(value) = prefs.save(KEY_USERID to value!!)

    var farm: String
        get() = prefs.getString(KEY_FARM, "")
        set(value) = prefs.save(KEY_FARM to value)

    var farmID: String
        get() = prefs.getString(KEY_FARM_ID, "")
        set(value) = prefs.save(KEY_FARM_ID to value)

    fun destroysession(){
        token = ""
        logged = false
        userId = ""
        farm = ""
        farmID = ""
    }

    companion object {
        private val KEY_TOKEN = "token"
        private val KEY_LOGGED = "logged"
        private val KEY_USERID = "userId"
        private val KEY_FARM = "farm"
        private val KEY_FARM_ID = "farmID"
    }


}