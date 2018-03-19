package com.example.cristian.myapplication.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.cristian.myapplication.R

class FeedBovineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_bovine)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
