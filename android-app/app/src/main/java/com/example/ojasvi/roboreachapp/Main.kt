package com.example.ojasvi.roboreachapp

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar
import java.time.LocalDate
import org.jetbrains.anko.indeterminateProgressDialog
import java.time.format.DateTimeFormatter

class Main : AppCompatActivity() {

    private var preferences: SharedPreferences? = null
    private var current: Shelf? = null
    private var numberOfShelves: Int = 0
    private var shelves: MutableList<Shelf> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        supportActionBar?.hide()

        preferences = applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)

        getShelves()

        // Gets the last used shelve data
        current = shelves.find { it.id == preferences!!.getString("last_used", "shelf_1") }

        updateShelves()

        // DEBUG CODE - REMOVE LATER!
        initializeCurrentShelf()
        /////////////////////////

        generateNotifications()

        setUpStoreButton()
        setUpInventoryButton()


    }

    private fun setUpStoreButton() {
        val storeButton: Button = findViewById(R.id.store)
        storeButton.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
                    .setView(R.layout.store)
                    .show()
            val exitButton = alertDialog.findViewById<ImageButton>(R.id.exitButton)
            exitButton?.setOnClickListener { alertDialog.dismiss() }
            val confirmButton = alertDialog.findViewById<Button>(R.id.storeButton)
            confirmButton?.setOnClickListener {
                val name = alertDialog.findViewById<EditText>(R.id.name)?.text.toString()
                val barcode = alertDialog.findViewById<EditText>(R.id.barcode)?.text.toString()
                val expiry = alertDialog.findViewById<EditText>(R.id.expiry)?.text.toString()
                if(name == "")
                    longSnackbar(it, "Name should not be blank!")
                else if(!expiry.matches(Regex("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")))  // 2xxx-xx-xx format
                    longSnackbar(it, "Incorrect expiry date format!")
                else {
                    // TODO: add checks for incorrect data and date formats
                    val newItem: Item = Item(name, LocalDate.parse(expiry, DateTimeFormatter.ISO_LOCAL_DATE), barcode)
                    alertDialog.dismiss()
                    val progressDialog = indeterminateProgressDialog("Storing item...")
                    progressDialog.show()
                    progressDialog.setCancelable(false)
                    sendItem(newItem, progressDialog)
                }
            }
            setUpLookupButton(alertDialog)
        }
    }

    private fun setUpLookupButton(alertDialog: AlertDialog) {
        val lookupButton = alertDialog.findViewById<Button>(R.id.quickLookupButton)
        lookupButton?.setOnClickListener {
            // TODO: fill this hashmap with more values
            alertDialog.dismiss()
            val lookupDatabase = LinkedHashMap<String, MutableList<String>>()
            lookupDatabase["Food"] = mutableListOf()
            lookupDatabase["Food"]?.add("Orange")
            lookupDatabase["Food"]?.add("Kiwi")
            lookupDatabase["Food"]?.add("Pear")
            lookupDatabase["Drinks"] = mutableListOf()
            lookupDatabase["Drinks"]?.add("Coke")
            lookupDatabase["Drinks"]?.add("Juice")
            lookupDatabase["Drinks"]?.add("Water")
            val pickerDialog = AlertDialog.Builder(this)
                    .setView(R.layout.activity_picker)
                    .show()
            val categoriesView = pickerDialog.findViewById<RecyclerView>(R.id.categories)
            val itemsView = pickerDialog.findViewById<RecyclerView>(R.id.items)
            categoriesView?.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
            categoriesView?.adapter = CategoryAdapter(lookupDatabase, itemsView)
        }
    }

    private fun setUpInventoryButton() {
        val inventoryButton: Button = findViewById(R.id.inventory)
        inventoryButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                    .setView(R.layout.inventory)
                    .show()
            val recycler = dialog.findViewById<RecyclerView>(R.id.inventory_recycler)
            recycler?.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
            if(current != null)
                recycler?.adapter = InventoryAdapter(current!!.sections.filter { it.item != null }, dialog)
        }
    }

    private fun sendItem(item: Item, progressDialog: ProgressDialog) {
        // TODO: send to RPI here
        //progressDialog.dismiss()
    }

    // Updates the spinner with the shelves list
    private fun updateShelves() {
        val shelf_spinner: Spinner = findViewById(R.id.shelf_spinner)
        val spinner_array = shelves.map { it.title } as ArrayList<String>
        val spinner_adapter = ArrayAdapter(this, R.layout.shelf_title, spinner_array)
        spinner_adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice)
        shelf_spinner.adapter = spinner_adapter
        shelf_spinner.setSelection(spinner_adapter.getPosition(current?.title), true)
    }

    // Gets the list of added shelves
    private fun getShelves() {
        numberOfShelves = preferences!!.getInt("number_of_shelves", 1)
        for(i in 1..numberOfShelves) {
            val identifier = preferences!!.getString("shelf_${i}", "")
            val title = preferences!!.getString("shelf_${i}_title", "Kitchen")
            shelves.add(Shelf("shelf_${i}", identifier, title))
        }
    }

    private fun generateNotifications() {
        val listOfNotifications = mutableListOf<Notification>()
        for(shelfSection in current!!.sections) {
            if(shelfSection.item != null && shelfSection.item!!.expiresSoon())
                listOfNotifications.add(Notification(shelfSection))
        }
        val recyclerView = findViewById<RecyclerView>(R.id.notifications)
        val adapter = NotificationAdapter(listOfNotifications)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
    }


    private fun initializeCurrentShelf() {
        // Should get shelfSection data from hardware/local database
        current?.sections?.add(ShelfSection(null, "1"))
        current?.sections?.add(ShelfSection(null, "2"))
        current?.sections?.add(ShelfSection(null, "3"))
        current?.sections?.add(ShelfSection(null, "4"))
        current?.sections?.add(ShelfSection(null, "5"))
        current?.sections?.add(ShelfSection(null, "6"))
        current?.sections?.add(ShelfSection(null, "7"))
        current?.sections?.add(ShelfSection(null, "8"))
        generateFakeItems()
    }

    private fun generateFakeItems() {
        // Creates fake Item and Notification objects
        val jam = Item("Jam", LocalDate.now().plusDays(16), "564648646464")
        current?.sections?.find{ it.name == "7" }?.item = jam
        val bread = Item("Bread", LocalDate.now().plusDays(4), "548674646464")
        current?.sections?.find{ it.name == "8" }?.item = bread
        val biscuits = Item("Biscuits", LocalDate.now().plusDays(8), "34696453453")
        current?.sections?.find{ it.name == "1" }?.item = biscuits
    }

}
