package com.example.cristian.myapplication.data.preferences

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.cristian.myapplication.util.save
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

    var userId: Int
        get() = prefs.getInt(KEY_USERID, 0)
        set(value) = prefs.save(KEY_USERID to value)

    var farm: String
        get() = prefs.getString(KEY_FARM, "")
        set(value) = prefs.save(KEY_FARM to value)

    var farmID: String
        get() = prefs.getString(KEY_FARM_ID, "")
        set(value) = prefs.save(KEY_FARM_ID to value)

    companion object {
        private val KEY_TOKEN = "token"
        private val KEY_LOGGED = "logged"
        private val KEY_USERID = "userId"
        private val KEY_FARM = "farm"
        private val KEY_FARM_ID = "farmID"
    }


}