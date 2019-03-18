package com.example.ojasvi.roboreachapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.time.LocalDate


class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null

    private lateinit var shelf_a1: Shelf
    private lateinit var shelf_a2: Shelf
    private lateinit var shelf_a3: Shelf
    private lateinit var shelf_b1: Shelf
    private lateinit var shelf_b2: Shelf
    private lateinit var shelf_b3: Shelf
    private lateinit var shelf_d1: Shelf
    private lateinit var shelf_d2: Shelf

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
        shelf_a1 = Shelf(null, "A1")
        shelf_a2 = Shelf(null, "A2")
        shelf_a3 = Shelf(null, "A3")
        shelf_b1 = Shelf(null, "B1")
        shelf_b2 = Shelf(null, "B2")
        shelf_b3 = Shelf(null, "B3")
        shelf_d1 = Shelf(null, "D1")
        shelf_d2 = Shelf(null, "D2")
    }

    private fun generateFakeItems() {
        // Creates fake Item and Notification objects
        val jam = Item("Jam")
        shelf_a3.item = jam
        val bread = Item("Bread")
        shelf_b1.item = bread
        val biscuits = Item("Biscuits", LocalDate.now().plusDays(8), "34696453453")
        shelf_b2.item = biscuits
    }


    private fun generateNotifications() {

        val listOfNotifications = mutableListOf<Notification>()

        if(shelf_a1.item != null && shelf_a1.item!!.expiresSoon()) {
            val notification = Notification(shelf_a1)
            listOfNotifications.add(notification)
        }

        if(shelf_a2.item != null && shelf_a2.item!!.expiresSoon()) {
            val notification = Notification(shelf_a2)
            listOfNotifications.add(notification)
        }

        if(shelf_a3.item != null && shelf_a3.item!!.expiresSoon()) {
            val notification = Notification(shelf_a3)
            listOfNotifications.add(notification)
        }

        if(shelf_b1.item != null && shelf_b1.item!!.expiresSoon()) {
            val notification = Notification(shelf_b1)
            listOfNotifications.add(notification)
        }

        if(shelf_b2.item != null && shelf_b2.item!!.expiresSoon()) {
            val notification = Notification(shelf_b2)
            listOfNotifications.add(notification)
        }

        if(shelf_b3.item != null && shelf_b3.item!!.expiresSoon()) {
            val notification = Notification(shelf_b3)
            listOfNotifications.add(notification)
        }

        if(shelf_d1.item != null && shelf_d1.item!!.expiresSoon()) {
            val notification = Notification(shelf_d1)
            listOfNotifications.add(notification)
        }

        if(shelf_d2.item != null && shelf_d2.item!!.expiresSoon()) {
            val notification = Notification(shelf_d2)
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

        val a1CardView = findViewById<CardView>(R.id.a1_card)
        val a2CardView = findViewById<CardView>(R.id.a2_card)
        val a3CardView = findViewById<CardView>(R.id.a3_card)
        val b1CardView = findViewById<CardView>(R.id.b1_card)
        val b2CardView = findViewById<CardView>(R.id.b2_card)
        val b3CardView = findViewById<CardView>(R.id.b3_card)
        val d1CardView = findViewById<CardView>(R.id.d1_card)
        val d2CardView = findViewById<CardView>(R.id.d2_card)

        a1CardView.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_a1), options.toBundle())
        }
        a2CardView.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_a2), options.toBundle())
        }
        a3CardView.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_a3), options.toBundle())
        }
        b1CardView.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_b1), options.toBundle())
        }
        b2CardView.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_b2), options.toBundle())
        }
        b3CardView.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_b3), options.toBundle())
        }

        d1CardView.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it, getString(R.string.transition_string))
            startActivity(Intent(this, ShelfActivity::class.java).putExtra("shelf", shelf_d1), options.toBundle())
        }

        d2CardView.setOnClickListener {
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