package com.example.ojasvi.roboreachapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        supportActionBar?.title = "Edit"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
