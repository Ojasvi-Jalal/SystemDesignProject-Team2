package com.example.ojasvi.roboreachapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.twiceyuan.library.MultiColumnPicker



class PickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        val button = findViewById<Button>(R.id.button)
        button. setOnClickListener {

            val categories: MutableList<String> = mutableListOf()
            categories.add("Beauty")
            categories.add("Food")
            categories.add("Drinks")
            categories.add("Other")

            val items: MutableList<String> = mutableListOf()
            items.add("Coke")
            items.add("Pear")
            items.add("Hairbrush")
            items.add("Nail clipper")

            val picker = MultiColumnPicker<String, String>(this) // instantiation
            picker.setLeftContent(categories) // setup left content
            //picker.setOnLeftSelected { position, city -> right(city) } // left selected listener
            //picker.setOnRightSelected { position, city -> action(city) } // right selected listener
            picker.show() // display
        }
    }
}
