package com.ceotic.ganko.data.preferences

import android.content.SharedPreferences
import com.ceotic.ganko.util.save
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSession @Inject constructor(val prefs: SharedPreferences) {

    var token: String
        get() = prefs.getString(KEY_TOKEN, "")
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

    var plan: String
        get() = prefs.getString(KEY_PLAN, "")
        set(value) = prefs.save(KEY_PLAN to value)

    var planDate: Date
        get() = Date(prefs.getLong(KEY_PLAN_DATE, Date().time))
        set(value) = prefs.save(KEY_PLAN_DATE to value.time)

    var planValid:Boolean
        get() = prefs.getBoolean(KEY_PLAN_VALID, false)
        set(value) = prefs.save(KEY_PLAN_VALID to value)


    fun destroysession() {
        token = ""
        logged = false
        userId = ""
        farm = ""
        farmID = ""
        plan = ""
    }

    fun validatePlan(size: Int): Pair<Boolean, Int> {
        val validTime = validatePlanDate()
        if(!validTime.first) return validTime

        val valid:Boolean = when (plan) {
            PLAN_FREE -> size < 10
            PLAN_BASIC -> size < 35
            PLAN_BASIC_2 -> size < 60
            PLAN_BASIC_3 -> size < 100
            PLAN_PREMIUM -> size < 500
            else -> true
        }

        return valid to PLAN_LIMIT
    }

    fun validatePlanDate():Pair<Boolean, Int>{
        val now = Date().time
        val milis = if(plan == PLAN_FREE) FOUR_MONTH else YEAR
        val nextPay = planDate.time + milis
        val valid = now <= nextPay
        planValid = valid
        return valid to PlAN_DATE
    }

    companion object {
        private val KEY_TOKEN = "token"
        private val KEY_LOGGED = "logged"
        private val KEY_USERID = "userId"
        private val KEY_FARM = "farm"
        private val KEY_FARM_ID = "farmID"
        private val KEY_PLAN = "plan"
        private val KEY_PLAN_DATE = "planDate"
        private val KEY_PLAN_VALID = "plan"

        private val PLAN_FREE = "gratuito"
        private val PLAN_BASIC = "basico"
        private val PLAN_BASIC_2 = "basico1"
        private val PLAN_BASIC_3 = "basico2"
        private val PLAN_PREMIUM = "premium"
        private val PLAN_PREMIUM_2 = "premium2"

        val PLAN_LIMIT = 0
        val PlAN_DATE = 1

        const val FOUR_MONTH = 10_368_000L
        const val YEAR = 31_356_000_000L
    }


}