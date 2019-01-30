package com.example.ojasvi.roboreachapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var notificationAdapter: RecyclerView.Adapter<*>? = null

    private lateinit var shelf_a1: Shelf
    private lateinit var shelf_a2: Shelf
    private lateinit var shelf_a3: Shelf
    private lateinit var shelf_b1: Shelf
    private lateinit var shelf_b2: Shelf
    private lateinit var shelf_b3: Shelf

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_shelves)
        supportActionBar?.title = "RoboReach"

        /* DEBUG code - REMOVE LATER */

        // Sets A3 card to be clickable and launches a new ShelfActivity
        val a3CardView = findViewById<CardView>(R.id.a3_card)
        a3CardView.setOnClickListener { startActivity(Intent(this, ShelfActivity::class.java)) }

        // Creates fake Shelf, Item and Notification objects
        val jam = Item("Jam", "Delicious goodie")
        shelf_a3 = Shelf(jam, "A3")
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
}
