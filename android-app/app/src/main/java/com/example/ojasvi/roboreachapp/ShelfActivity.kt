package com.example.ojasvi.roboreachapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.support.design.widget.FloatingActionButton

class ShelfActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelf)
        supportActionBar?.title = "Shelf"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Edit button trigger
        val editButton1 = findViewById<FloatingActionButton>(R.id.editButton1)
        val editButton2 = findViewById<FloatingActionButton>(R.id.editButton2)

        editButton1.setOnClickListener {
            val mEdit = findViewById<EditText>(R.id.expiry)
            mEdit.isEnabled= true
        }

        editButton2.setOnClickListener {
            val mEdit = findViewById<EditText>(R.id.barcode)
            mEdit.isEnabled = true
        }
    }
}
