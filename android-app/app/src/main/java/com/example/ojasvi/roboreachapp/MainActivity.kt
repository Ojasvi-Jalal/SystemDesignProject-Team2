package com.example.ojasvi.roboreachapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var shelf_a1: Shelf
    private lateinit var shelf_a2: Shelf
    private lateinit var shelf_a3: Shelf
    private lateinit var shelf_b1: Shelf
    private lateinit var shelf_b2: Shelf
    private lateinit var shelf_b3: Shelf

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_shelves)


        /* DEBUG code - REMOVE LATER */
        var jam = Item("Jam", "Delicious goodie")
        shelf_a3 = Shelf(jam, "A3")


    }
}
