package com.ceotic.ganko.ui.account

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.models.LoginResponse
import com.ceotic.ganko.data.models.UserLogin
import com.ceotic.ganko.data.net.LoginClient
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.applySchedulers
import com.ceotic.ganko.util.validateResponse
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

class AccountViewModel @Inject constructor(private val loginClient: LoginClient,
                                           val session: UserSession) : ViewModel() {

    fun Login(userLogin: UserLogin): Observable<String> = loginClient.login(userLogin)
            .flatMap { validateResponse(it) }
            .flatMap {
                saveSession(it)
            }
            .applySchedulers()

//    fun Login(userLogin: UserLogin): Observable<String> = Observable.create<String> {
//        if(userLogin.username == "cristian" && userLogin.pass == "1234"){
//            session.logged = true
//            it.onNext(userLogin.username)
//        }
//        else throw Throwable("Usuario Inactivo, Contacte a su Proveedor")
//    }
//            .applySchedulers()


//    fun validateState(loginResponse: LoginResponse) = Observable.create<String>{
//        if(loginResponse.user.estado == "activo"){
//            session.token = loginResponse.token
//            session.userId = loginResponse.user.id
//            session.logged = true
//            it.onNext(loginResponse.token)
//
//        }
//        else throw Throwable("Usuario Inactivo, Contacte a su Proveedor")
//    }

    fun saveSession(loginResponse: LoginResponse) = Observable.create<String> {
        session.logged = true
        session.token = loginResponse.token
        session.userId = loginResponse.user.id
        session.plan = loginResponse.user.doc.plan
        session.planDate = loginResponse.user.doc.ultimoPago ?: Date()
        it.onNext(loginResponse.user.id)
    }
}

