package com.example.cristian.myapplication.ui.bovine.feed

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.FeedBovineAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.subscribeByShot
import kotlinx.android.synthetic.main.activity_feed_bovine.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class FeedBvnActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel:FeedBvnViewModel by lazy { buildViewModel<FeedBvnViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)

    @Inject
    lateinit var adapter: FeedBovineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_bovine)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getFeedById()
                .subscribeByShot(
                        onNext = {
                            adapter.feed = it
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )
    }
}
