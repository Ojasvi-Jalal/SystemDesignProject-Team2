package com.example.ojasvi.roboreachapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v4.app.ActivityOptionsCompat
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null

    lateinit var shelf_a1: Shelf
    lateinit var shelf_a2: Shelf
    lateinit var shelf_a3: Shelf
    lateinit var shelf_b1: Shelf
    lateinit var shelf_b2: Shelf
    lateinit var shelf_b3: Shelf

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_shelves)
        supportActionBar?.title = "RoboReach"

        val a1CardView = findViewById<CardView>(R.id.a1_card)
        val a2CardView = findViewById<CardView>(R.id.a2_card)
        val a3CardView = findViewById<CardView>(R.id.a3_card)
        val b1CardView = findViewById<CardView>(R.id.b1_card)
        val b2CardView = findViewById<CardView>(R.id.b2_card)
        val b3CardView = findViewById<CardView>(R.id.b3_card)

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

        shelf_a1 = Shelf(null, "A1")
        shelf_a2 = Shelf(null, "A2")
        shelf_a3 = Shelf(null, "A3")
        shelf_b1 = Shelf(null, "B1")
        shelf_b2 = Shelf(null, "B2")
        shelf_b3 = Shelf(null, "B3")


        /* DEBUG code - REMOVE LATER */

        // Creates fake Item and Notification objects
        val jam = Item("Jam")
        shelf_a3.item = jam
        val notification = Notification(shelf_a3)
        val listOfNotifications = mutableListOf<Notification>()
        listOfNotifications.add(notification)

        // Deals with notification list
        recyclerView = findViewById(R.id.notifications)

        val adapter = NotificationAdapter(listOfNotifications)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.adapter = adapter

        /* DEBUG CODE ABOVE */

    }

    fun displayShelfInfo() {
        val a1_id = findViewById<TextView>(R.id.a1_id)
    }


}
