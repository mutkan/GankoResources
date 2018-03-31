package com.example.cristian.myapplication.ui.account

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.UserLogin
import com.example.cristian.myapplication.databinding.ActivityLoginBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.farms.FarmActivity
import com.example.cristian.myapplication.util.*
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory:ViewModelProvider.Factory
    val viewModel:AccountViewModel by lazy { buildViewModel<AccountViewModel>(factory) }
    lateinit var binding: ActivityLoginBinding
    val dis: LifeDisposable = LifeDisposable(this)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        //viewModel = ViewModelProviders.of(this, factory).get(LoginActivity::class.java)

    }

    override fun onResume() {
        super.onResume()

        btnlogin.clicks()
                .flatMap { validateForm(R.string.empty_fields, email.text(), password.text())}
                .flatMap { viewModel.Login(UserLogin(it[0], it[1]))}
                .subscribeByAction(
                        onNext = {startActivity<FarmActivity>()},
                        onHttpError = {this.toast(R.string.http_404)},
                        onError = { toast(it.message!!) }
                )

        dis add forgotPassword.clicks()
                .subscribe{startActivity<RestorePassActivity>()}
    }

}
