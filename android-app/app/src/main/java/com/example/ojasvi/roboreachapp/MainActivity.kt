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
        shelf_1 = Shelf(null, "4")
        shelf_2 = Shelf(null, "5")
        shelf_3 = Shelf(null, "6")
        shelf_1 = Shelf(null, "7")
        shelf_2 = Shelf(null, "8")
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

        val `1CardView` = findViewById<CardView>(R.id.1_card)
        val `2CardView` = findViewById<CardView>(R.id.2_card)
        val `3CardView` = findViewById<CardView>(R.id.3_card)
        val `4CardView` = findViewById<CardView>(R.id.4_card)
        val `5CardView` = findViewById<CardView>(R.id.b2_card)
        val `6CardView` = findViewById<CardView>(R.id.b3_card)
        val `7CardView` = findViewById<CardView>(R.id.d1_card)
        val `8CardView` = findViewById<CardView>(R.id.d2_card)

        `1CardView`.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_a1), options.toBundle())
        }
        `2CardView`.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_a2), options.toBundle())
        }
        `3CardView`.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_a3), options.toBundle())
        }
        `4CardView`.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_b1), options.toBundle())
        }
        `5CardView`.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_b2), options.toBundle())
        }
        `6CardView`.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_b3), options.toBundle())
        }

        `7CardView`.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_d1), options.toBundle())
        }

        `8CardView`.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_d2), options.toBundle())
        }

        val a1_title = findViewById<TextView>(R.id.a1_title)
        val a2_title = findViewById<TextView>(R.id.a2_title)
        val a3_title = findViewById<TextView>(R.id.a3_title)
        val b1_title = findViewById<TextView>(R.id.b1_title)
        val b2_title = findViewById<TextView>(R.id.b2_title)
        val b3_title = findViewById<TextView>(R.id.b3_title)
        val d1_title = findViewById<TextView>(R.id.d1_title)
        val d2_title = findViewById<TextView>(R.id.d2_title)

        val a1_warn = findViewById<ImageView>(R.id.a1_warn)
        val a2_warn = findViewById<ImageView>(R.id.a2_warn)
        val a3_warn = findViewById<ImageView>(R.id.a3_warn)
        val b1_warn = findViewById<ImageView>(R.id.b1_warn)
        val b2_warn = findViewById<ImageView>(R.id.b2_warn)
        val b3_warn = findViewById<ImageView>(R.id.b3_warn)
        val d1_warn = findViewById<ImageView>(R.id.d1_warn)
        val d2_warn = findViewById<ImageView>(R.id.d2_warn)

        when(shelf_a1.item) {
            null -> {
                a1_title.text = "Empty"
                a1_warn.visibility = View.GONE
            }
            else -> {
                a1_title.text = shelf_a1.item!!.title
                if(shelf_a1.item!!.expiresSoon()) a1_warn.visibility = View.VISIBLE
                else a1_warn.visibility = View.GONE
            }
        }

        when(shelf_a2.item) {
            null -> {
                a2_title.text = "Empty"
                a2_warn.visibility = View.GONE
            }
            else -> {
                a2_title.text = shelf_a2.item!!.title
                if(shelf_a2.item!!.expiresSoon()) a2_warn.visibility = View.VISIBLE
                else a2_warn.visibility = View.GONE
            }
        }

        when(shelf_a3.item) {
            null -> {
                a3_title.text = "Empty"
                a3_warn.visibility = View.GONE
            }
            else -> {
                a3_title.text = shelf_a3.item!!.title
                if(shelf_a3.item!!.expiresSoon()) a3_warn.visibility = View.VISIBLE
                else a3_warn.visibility = View.GONE
            }
        }

        when(shelf_b1.item) {
            null -> {
                b1_title.text = "Empty"
                b1_warn.visibility = View.GONE
            }
            else -> {
                b1_title.text = shelf_b1.item!!.title
                if(shelf_b1.item!!.expiresSoon()) b1_warn.visibility = View.VISIBLE
                else b1_warn.visibility = View.GONE
            }
        }

        when(shelf_b2.item) {
            null -> {
                b2_title.text = "Empty"
                b2_warn.visibility = View.GONE
            }
            else -> {
                b2_title.text = shelf_b2.item!!.title
                if(shelf_b2.item!!.expiresSoon()) b2_warn.visibility = View.VISIBLE
                else b2_warn.visibility = View.GONE
            }
        }

        when(shelf_b3.item) {
            null -> {
                b3_title.text = "Empty"
                b3_warn.visibility = View.GONE
            }
            else -> {
                b3_title.text = shelf_b3.item!!.title
                if(shelf_b3.item!!.expiresSoon()) b3_warn.visibility = View.VISIBLE
                else b3_warn.visibility = View.GONE
            }
        }

        when(shelf_d1.item) {
            null -> {
                d1_title.text = "Empty"
                d1_warn.visibility = View.GONE
            }
            else -> {
                d1_title.text = shelf_d1.item!!.title
                if(shelf_d1.item!!.expiresSoon()) d1_warn.visibility = View.VISIBLE
                else d1_warn.visibility = View.GONE
            }
        }

        when(shelf_d2.item) {
            null -> {
                d2_title.text = "Empty"
                d2_warn.visibility = View.GONE
            }
            else -> {
                d2_title.text = shelf_d2.item!!.title
                if(shelf_d2.item!!.expiresSoon()) d2_warn.visibility = View.VISIBLE
                else d2_warn.visibility = View.GONE
            }
        }

    }


}