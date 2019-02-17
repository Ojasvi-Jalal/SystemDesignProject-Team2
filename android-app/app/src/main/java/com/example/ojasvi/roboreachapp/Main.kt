package com.example.ojasvi.roboreachapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class Main : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        supportActionBar?.hide()
    }
}
