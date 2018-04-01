package com.example.cristian.myapplication.ui.account

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.models.LoginResponse
import com.example.cristian.myapplication.data.models.UserLogin
import com.example.cristian.myapplication.data.models.Usuario
import com.example.cristian.myapplication.data.net.LoginClient
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.validateResponse
import io.reactivex.Observable
import javax.inject.Inject

class AccountViewModel @Inject constructor(private val loginClient: LoginClient,
                                           val session: UserSession):ViewModel(){

    fun Login(userLogin: UserLogin): Observable<String> = loginClient.login(userLogin)
            .flatMap { validateResponse(it) }
            .flatMap { validateState(it) }
            .applySchedulers()

    fun validateState(loginResponse: LoginResponse) = Observable.create<String>{
        if(loginResponse.user.estado == "activo"){
            session.token = loginResponse.token
            session.userId = loginResponse.user.id
            session.logged = true
            it.onNext(loginResponse.token)

        }
        else throw Throwable("Usuario Inactivo, Contacte a su Proveedor")
    }
}

