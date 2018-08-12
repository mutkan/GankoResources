package com.example.cristian.myapplication.ui.common

import android.support.v4.view.ViewPager
import io.reactivex.subjects.PublishSubject

object PageChangeListener : ViewPager.OnPageChangeListener {

    val tabChanges: PublishSubject<Int> = PublishSubject.create()

    override fun onPageSelected(position: Int) {
        tabChanges.onNext(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }


    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

}