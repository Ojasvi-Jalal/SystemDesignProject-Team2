package com.example.ojasvi.roboreachapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.support.design.widget.FloatingActionButton
import android.widget.TextView
import java.time.format.DateTimeFormatter

class ShelfActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelf)

        supportActionBar?.title = "Shelf"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getShelfData()


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

    private fun getShelfData() {

        val shelf: Shelf = intent.getSerializableExtra("shelf") as Shelf
        val item = shelf.item

        val shelfName: TextView = findViewById(R.id.name)
        val itemTitle: TextView = findViewById(R.id.title)
        val expirationDate: EditText = findViewById(R.id.expiry)
        val barcode: EditText = findViewById(R.id.barcode)

        shelfName.text = shelf.name
        itemTitle.text = item?.title
        expirationDate.setText(item?.expiration?.format(DateTimeFormatter.ISO_LOCAL_DATE))
        barcode.setText(item?.barcode)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
