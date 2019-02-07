package com.example.ojasvi.roboreachapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v4.app.ActivityOptionsCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null

    private lateinit var shelf_1: Shelf
    private lateinit var shelf_2: Shelf
    private lateinit var shelf_3: Shelf
    private lateinit var shelf_4: Shelf
    private lateinit var shelf_5: Shelf
    private lateinit var shelf_6: Shelf
    private lateinit var shelf_7: Shelf
    private lateinit var shelf_8: Shelf

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_shelves)
        supportActionBar?.title = "Main"

        initializeShelves()

        /* DEBUG code - REMOVE LATER */
        generateFakeItems()
        /* DEBUG CODE ABOVE */

        generateNotifications()
        refreshShelfInfo()

    }


    private fun initializeShelves() {
        // Should get shelf data from hardware/local database
        shelf_1 = Shelf(null, "1")
        shelf_2 = Shelf(null, "2")
        shelf_3 = Shelf(null, "3")
        shelf_4 = Shelf(null, "4")
        shelf_5 = Shelf(null, "5")
        shelf_6 = Shelf(null, "6")
        shelf_7 = Shelf(null, "7")
        shelf_8 = Shelf(null, "8")
    }

    private fun generateFakeItems() {
        // Creates fake Item and Notification objects
        val jam = Item("Jam", LocalDate.now().plusDays(16), "564648646464")
        shelf_7.item = jam
        val bread = Item("Bread", LocalDate.now().plusDays(4), "548674646464")
        shelf_8.item = bread
        val biscuits = Item("Biscuits", LocalDate.now().plusDays(8), "34696453453")
        shelf_1.item = biscuits
    }


    private fun generateNotifications() {

        val listOfNotifications = mutableListOf<Notification>()

        if(shelf_1.item != null && shelf_1.item!!.expiresSoon()) {
            val notification = Notification(shelf_1)
            listOfNotifications.add(notification)
        }

        if(shelf_2.item != null && shelf_2.item!!.expiresSoon()) {
            val notification = Notification(shelf_2)
            listOfNotifications.add(notification)
        }

        if(shelf_3.item != null && shelf_3.item!!.expiresSoon()) {
            val notification = Notification(shelf_3)
            listOfNotifications.add(notification)
        }

        if(shelf_4.item != null && shelf_4.item!!.expiresSoon()) {
            val notification = Notification(shelf_4)
            listOfNotifications.add(notification)
        }

        if(shelf_5.item != null && shelf_5.item!!.expiresSoon()) {
            val notification = Notification(shelf_5)
            listOfNotifications.add(notification)
        }

        if(shelf_6.item != null && shelf_6.item!!.expiresSoon()) {
            val notification = Notification(shelf_6)
            listOfNotifications.add(notification)
        }

        if(shelf_7.item != null && shelf_7.item!!.expiresSoon()) {
            val notification = Notification(shelf_7)
            listOfNotifications.add(notification)
        }

        if(shelf_8.item != null && shelf_8.item!!.expiresSoon()) {
            val notification = Notification(shelf_8)
            listOfNotifications.add(notification)
        }

        // Deals with notification list
        recyclerView = findViewById(R.id.notifications)
        val adapter = NotificationAdapter(listOfNotifications)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.adapter = adapter

    }

    private fun refreshShelfInfo() {

        val card_1 = findViewById<CardView>(R.id.card_1)
        val card_2 = findViewById<CardView>(R.id.card_2)
        val card_3 = findViewById<CardView>(R.id.card_3)
        val card_4 = findViewById<CardView>(R.id.card_4)
        val card_5 = findViewById<CardView>(R.id.card_5)
        val card_6 = findViewById<CardView>(R.id.card_6)
        val card_7 = findViewById<CardView>(R.id.card_7)
        val card_8 = findViewById<CardView>(R.id.card_8)

        card_1.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_1), options.toBundle())
        }
        card_2.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_2), options.toBundle())
        }
        card_3.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_3), options.toBundle())
        }
        card_4.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_4), options.toBundle())
        }
        card_5.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_5), options.toBundle())
        }
        card_6.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_6), options.toBundle())
        }

        card_7.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_7), options.toBundle())
        }

        card_8.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_8), options.toBundle())
        }

        val title_1 = findViewById<TextView>(R.id.title_1)
        val title_2 = findViewById<TextView>(R.id.title_2)
        val title_3 = findViewById<TextView>(R.id.title_3)
        val title_4 = findViewById<TextView>(R.id.title_4)
        val title_5 = findViewById<TextView>(R.id.title_5)
        val title_6 = findViewById<TextView>(R.id.title_6)
        val title_7 = findViewById<TextView>(R.id.title_7)
        val title_8 = findViewById<TextView>(R.id.title_8)

        val warn_1 = findViewById<ImageView>(R.id.warn_1)
        val warn_2 = findViewById<ImageView>(R.id.warn_2)
        val warn_3 = findViewById<ImageView>(R.id.warn_3)
        val warn_4 = findViewById<ImageView>(R.id.warn_4)
        val warn_5 = findViewById<ImageView>(R.id.warn_5)
        val warn_6 = findViewById<ImageView>(R.id.warn_6)
        val warn_7 = findViewById<ImageView>(R.id.warn_7)
        val warn_8 = findViewById<ImageView>(R.id.warn_8)

        when(shelf_1.item) {
            null -> {
                title_1.text = "Empty"
                warn_1.visibility = View.GONE
            }
            else -> {
                title_1.text = shelf_1.item!!.title
                if(shelf_1.item!!.expiresSoon()) warn_1.visibility = View.VISIBLE
                else warn_1.visibility = View.GONE
            }
        }

        when(shelf_2.item) {
            null -> {
                title_2.text = "Empty"
                warn_2.visibility = View.GONE
            }
            else -> {
                title_2.text = shelf_2.item!!.title
                if(shelf_2.item!!.expiresSoon()) warn_2.visibility = View.VISIBLE
                else warn_2.visibility = View.GONE
            }
        }

        when(shelf_3.item) {
            null -> {
                title_3.text = "Empty"
                warn_3.visibility = View.GONE
            }
            else -> {
                title_3.text = shelf_3.item!!.title
                if(shelf_3.item!!.expiresSoon()) warn_3.visibility = View.VISIBLE
                else warn_3.visibility = View.GONE
            }
        }

        when(shelf_4.item) {
            null -> {
                title_4.text = "Empty"
                warn_4.visibility = View.GONE
            }
            else -> {
                title_4.text = shelf_4.item!!.title
                if(shelf_4.item!!.expiresSoon()) warn_4.visibility = View.VISIBLE
                else warn_4.visibility = View.GONE
            }
        }

        when(shelf_5.item) {
            null -> {
                title_5.text = "Empty"
                warn_5.visibility = View.GONE
            }
            else -> {
                title_5.text = shelf_5.item!!.title
                if(shelf_5.item!!.expiresSoon()) warn_5.visibility = View.VISIBLE
                else warn_5.visibility = View.GONE
            }
        }

        when(shelf_6.item) {
            null -> {
                title_6.text = "Empty"
                warn_6.visibility = View.GONE
            }
            else -> {
                title_6.text = shelf_6.item!!.title
                if(shelf_6.item!!.expiresSoon()) warn_6.visibility = View.VISIBLE
                else warn_6.visibility = View.GONE
            }
        }

        when(shelf_7.item) {
            null -> {
                title_7.text = "Empty"
                warn_7.visibility = View.GONE
            }
            else -> {
                title_7.text = shelf_7.item!!.title
                if(shelf_7.item!!.expiresSoon()) warn_7.visibility = View.VISIBLE
                else warn_7.visibility = View.GONE
            }
        }

        when(shelf_8.item) {
            null -> {
                title_8.text = "Empty"
                warn_8.visibility = View.GONE
            }
            else -> {
                title_8.text = shelf_8.item!!.title
                if(shelf_8.item!!.expiresSoon()) warn_8.visibility = View.VISIBLE
                else warn_8.visibility = View.GONE
            }
        }

    }


}