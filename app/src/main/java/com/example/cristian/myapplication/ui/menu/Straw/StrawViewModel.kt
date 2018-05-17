package com.example.cristian.myapplication.ui.menu.Straw

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.Manage
import com.example.cristian.myapplication.data.models.Straw
import com.example.cristian.myapplication.util.applySchedulers
import io.reactivex.Single
import javax.inject.Inject

class StrawViewModel @Inject constructor(private val db: CouchRx) : ViewModel() {

    fun addStraw(straw: Straw): Single<String> =
            db.insert(straw)
                    .applySchedulers()


}