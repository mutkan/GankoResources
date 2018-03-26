package com.example.cristian.myapplication.ui.bovine.feed

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.buildViewModel
import javax.inject.Inject

class FeedBvnActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel:FeedBvnViewModel by lazy { buildViewModel<FeedBvnViewModel>(factory) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_bovine)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
