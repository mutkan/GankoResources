package com.example.cristian.myapplication.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.SharedPreferences
import android.databinding.BindingAdapter
import android.graphics.drawable.ColorDrawable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.example.cristian.myapplication.R
import io.reactivex.Observable
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

fun ViewGroup.inflate(layout: Int) = LayoutInflater.from(context).inflate(layout, this, false)

fun EditText.text(): String = text.toString()

inline fun <reified T : ViewModel> AppCompatActivity.buildViewModel(factory: ViewModelProvider.Factory): T = ViewModelProviders.of(this, factory).get(T::class.java)

inline fun <reified T : ViewModel> Fragment.buildViewModel(factory: ViewModelProvider.Factory): T = ViewModelProviders.of(this, factory).get(T::class.java)


fun SharedPreferences.save(vararg data: Pair<String, Any>) {
    val editor = edit()
    data.forEach { (key, value) ->
        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Long -> editor.putLong(key, value)
        }

    }
    editor.apply()
}

fun AppCompatActivity.fixColor(content: Int) {
    when (content) {
        2 -> {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.bovine_primary)))
            window.statusBarColor = ContextCompat.getColor(this, R.color.bovine_dark)
        }
        3 -> {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.milk_primary)))
            window.statusBarColor = ContextCompat.getColor(this, R.color.milk_dark)
        }
        4 -> {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.feed_primary)))
            window.statusBarColor = ContextCompat.getColor(this, R.color.feed_dark)
        }
        5 -> {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.management_primary)))
            window.statusBarColor = ContextCompat.getColor(this, R.color.management_dark)
        }
        6 -> {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.movements_primary)))
            window.statusBarColor = ContextCompat.getColor(this, R.color.movements_dark)
        }
        7 -> {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.vaccine_primary)))
            window.statusBarColor = ContextCompat.getColor(this, R.color.vaccine_dark)
        }
        8 -> {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.health_primary)))
            window.statusBarColor = ContextCompat.getColor(this, R.color.health_dark)
        }
        9 -> {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.straw_primary)))
            window.statusBarColor = ContextCompat.getColor(this, R.color.straw_dark)
        }
        10 -> {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.prairie_primary)))
            window.statusBarColor = ContextCompat.getColor(this, R.color.prairie_dark)
        }
        11 -> {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.reports_primary)))
            window.statusBarColor = ContextCompat.getColor(this, R.color.reports_dark)
        }
        11-> {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.group_primary)))
            window.statusBarColor = ContextCompat.getColor(this, R.color.group_dark)
        }
    }
}


fun AppCompatActivity.validateForm(message: Int,
                                   vararg fields: String): Observable<List<String>> = Observable.create<List<String>> {
    if (fields.contains("")) toast(message)
    else it.onNext(fields.toList())
    it.onComplete()
}

fun AppCompatActivity.putFragment(container: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
            .replace(container, fragment)
            .commit()
}


fun Fragment.putFragment(container: Int, fragment: Fragment) {
    childFragmentManager.beginTransaction()
            .replace(container, fragment)
            .commit()
}

fun AppCompatActivity.addFragment(container: Int, fragment: Fragment, backStack: Boolean = true) {
    val transaction = supportFragmentManager.beginTransaction()
            .add(container, fragment)
    if (backStack) transaction.addToBackStack(null)
    transaction.commit()
}


fun Fragment.addFragment(container: Int, fragment: Fragment, backStack: Boolean = true) {
    val transaction = childFragmentManager.beginTransaction()
            .add(container, fragment)
    if (backStack) transaction.addToBackStack(null)
    transaction.commit()
}


@BindingAdapter("app:dateFormat")
fun applyFormat(textView: TextView, date: Date?) {
    if (date != null) {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        textView.text = format.format(date)
    } else textView.text = ""


}

fun Float.currencyFormat(): String {
    val currencyFormat = NumberFormat.getCurrencyInstance()
    currencyFormat.currency = Currency.getInstance(Locale.getDefault())
    currencyFormat.maximumFractionDigits = 0
    return currencyFormat.format(this).replace(",", ".", true)
}

fun String.toDate(): Date = format.parse(this)


fun Date.toStringFormat(): String = format.format(this)


fun <T> Fragment.dialog(msg: Int, data: T) = Observable.create<T> { emitter ->
    alert(msg) {
        yesButton {
            emitter.onNext(data)
            emitter.onComplete()
        }

        noButton {
            emitter.onComplete()
        }
    }.show()
}