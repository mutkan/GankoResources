package com.example.cristian.myapplication.ui.account

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.cristian.myapplication.App
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.UserLogin
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.farms.FarmActivity
import com.example.cristian.myapplication.ui.menu.MenuActivity
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: AccountViewModel by lazy { buildViewModel<AccountViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)

    @Inject
    lateinit var session: UserSession


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (session.logged) {
            if (session.farm == "") startActivity<FarmActivity>() else startActivity<MenuActivity>()
            finish()
        }

    }

    override fun onResume() {
        super.onResume()

        btnlogin.clicks()
                .flatMap { validateForm(R.string.empty_fields, email.text(), password.text()) }
                .flatMap { viewModel.Login(UserLogin(it[0], it[1])) }
                .subscribeByAction(
                        onNext = {
                            Log.d("ID", it)
                            // (application as App).startReplicator()
                            startActivity<FarmActivity>()
                            finish()
                        },
                        onHttpError = { this.toast(R.string.http_404)
                        Log.d("HTTPERROR", it.toString())},
                        onError = {
                            toast(it.message!!)
                            Log.e("ERROR", it.message, it)
                        }
                )

        dis add forgotPassword.clicks()
                .subscribe { startActivity<RestorePassActivity>() }
    }

}
