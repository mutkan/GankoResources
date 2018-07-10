package com.example.cristian.myapplication.ui.bovine.feed

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.FeedBovineAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_list_feed_bovine.*
import kotlinx.android.synthetic.main.activity_list_health_bovine.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class FeedBvnActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel:FeedBvnViewModel by lazy { buildViewModel<FeedBvnViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)

    @Inject
    lateinit var adapter: FeedBovineAdapter

    val idBovino:String by lazy { intent.extras.getString(EXTRA_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_feed_bovine)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Alimentacion")
        recyclerFeedBovine.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        recyclerFeedBovine.adapter= adapter
        recyclerFeedBovine.layoutManager = LinearLayoutManager(this)
        dis add viewModel.getFeedBovine(idBovino)
                .subscribeBy(
                        onSuccess = {
                            if(it.isEmpty()) emptyFeedText.visibility = View.VISIBLE else emptyFeedText.visibility = View.GONE
                            adapter.feed = it
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}
