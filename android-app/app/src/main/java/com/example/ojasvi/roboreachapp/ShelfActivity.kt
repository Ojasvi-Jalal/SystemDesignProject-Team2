package com.example.ojasvi.roboreachapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ShelfActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelf)
        supportActionBar?.title = "Shelf"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
