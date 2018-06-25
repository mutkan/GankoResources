package com.example.cristian.myapplication.ui.manage

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.preferences.UserSession
import javax.inject.Inject

class ManageViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession): ViewModel(){

}