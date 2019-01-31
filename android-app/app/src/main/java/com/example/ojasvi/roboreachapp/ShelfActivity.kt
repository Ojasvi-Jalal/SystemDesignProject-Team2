package com.example.ojasvi.roboreachapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ShelfActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelf)
        supportActionBar?.title = "Shelf"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Edit button trigger
        val editButton = findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }

    }
}
