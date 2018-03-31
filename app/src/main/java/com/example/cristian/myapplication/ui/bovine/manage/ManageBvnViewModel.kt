package com.example.cristian.myapplication.ui.bovine.manage

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.models.Manage
import com.example.cristian.myapplication.data.net.ManageClient
import com.example.cristian.myapplication.util.applySchedulers
import io.reactivex.Observable
import javax.inject.Inject

class ManageBvnViewModel @Inject constructor(private val client: ManageClient):ViewModel(){

    fun getManageBovine(): Observable<List<Manage>> =
            client.getManageBovine()
                    .applySchedulers()

}
