package com.example.ojasvi.roboreachapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.indeterminateProgressDialog
import java.time.format.DateTimeFormatter
import java.util.*

class ShelfActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelf)

        supportActionBar?.title = "Shelf"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getShelfData()

        val retrieveButton = findViewById<Button>(R.id.retrieveButton)
        retrieveButton.setOnClickListener {
        val progress = indeterminateProgressDialog("Scanning item...")
            progress.show()
            progress.setCancelable(false)
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    progress.dismiss()
                }
            }, 3000) // fake 3sec delay

        }

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
        val warning: ImageView = findViewById(R.id.warn)

        shelfName.text = shelf.name
        when (item) {
            null -> {
                itemTitle.text = "Empty"
                warning.visibility = View.GONE
            }
            else -> {
                itemTitle.text = item.title
                if(item.expiresSoon()) warning.visibility = View.VISIBLE
                else warning.visibility = View.GONE
            }
        }
        expirationDate.setText(item?.expiration?.format(DateTimeFormatter.ISO_LOCAL_DATE))
        barcode.setText(item?.barcode)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
