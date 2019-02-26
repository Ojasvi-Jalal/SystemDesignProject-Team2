package com.example.ojasvi.roboreachapp

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.*
import io.socket.client.IO
import io.socket.client.Socket
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.doAsync
import java.time.LocalDate
import org.jetbrains.anko.indeterminateProgressDialog
import org.json.JSONArray
import org.json.JSONObject
import java.time.format.DateTimeFormatter

class Main : AppCompatActivity() {

    private var preferences: SharedPreferences? = null
    private var current: Shelf? = null
    private var numberOfShelves: Int = 0
    private lateinit var sio: Socket
    private var host: String = ""
    private var shelves: MutableList<Shelf> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        supportActionBar?.hide()

        preferences = applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)

        getShelves()

        // Gets the last used shelve data
        current = shelves.find { it.id == preferences!!.getString("last_used", "shelf_1") }
        host = preferences!!.getString("current", "http://192.168.105.131:8000")

        setUpSocket()

        updateShelves()

        initializeCurrentShelf()

        generateNotifications()

        setUpStoreButton()
        setUpInventoryButton()

        doAsync { sio.emit("get_data") }

    }

    private fun setUpSocket() {

        //host = "http://129.215.2.230:8000" // TODO: change for prod
        sio = IO.socket(host)

        sio.on(Socket.EVENT_CONNECT) {
            Log.d("SIO", "Connected to $host")
            longSnackbar(findViewById(R.id.layout), "Connected")
        }

        sio.on(Socket.EVENT_DISCONNECT) {
            Log.d("SIO", "Disconnected from $host")
            longSnackbar(findViewById(R.id.layout), "Disconnected")
        }

        sio.on("get_data") { parameters -> // assumes first parameters is a list of dictionaries
            Log.d("SIO", "Received data: ${parameters[0]}")
            updateData(parameters[0] as JSONArray)
            runOnUiThread { generateNotifications() } // updates notifications on main
        }

        sio.on("move_to") { parameters ->
            val response: JSONObject? = parameters[0] as? JSONObject
            val success = response?.getBoolean("success")
            if(success != null && !success) { // failure
                val error: String = response.getString("message")
                Log.d("SIO", "move_to ERROR: $error")
            } else { // success
                Log.d("SIO", "move_to SUCCESS")
            }
        }

        sio.on("add_item") { parameters ->
            val response: JSONObject? = parameters[0] as? JSONObject
            val success = response?.getBoolean("success")
            if(success != null && !success) {
                val error: String = response.getString("message")
                Log.d("SIO", "add_item ERROR: $error")
            } else { // success
                Log.d("SIO", "add_item SUCCESS")
            }
            sio.emit("get_data")
        }

        sio.on("remove_item") { parameters ->
            val response: JSONObject? = parameters[0] as? JSONObject
            val success = response?.getBoolean("success")
            if(success != null && !success) {
                val error: String = response.getString("message")
                Log.d("SIO", "remove_item ERROR: $error")
            } else { // success
                Log.d("SIO", "remove_item SUCCESS")
            }
        }


        Log.d("SIO", "Attempting to connect to $host")
        sio.connect()
    }

    private fun removeItem(pos: String) {
        Log.d("SIO", "Removing item in position $pos")
        val arg = JSONObject().put("pos", pos.toIntOrNull())
        sio.emit("remove_item", arg)
    }

    private fun moveTo(pos: String) {
        Log.d("SIO", "Retrieving item in position $pos")
        val arg = JSONObject().put("pos", pos.toIntOrNull())
        sio.emit("move_to", arg)
    }

    private fun addItem(item: Item) {
        // Find a free shelf section:
        val freeSection: ShelfSection? = current?.sections?.filter { it.value.item == null }?.values?.toList()?.get(0)
        freeSection?.item = item
        // Send details to RPi
        val arg = JSONObject()
        arg.put("pos", freeSection?.name?.toIntOrNull())
        arg.put("name", item.title)
        if(item.barcode != null)
            arg.put("barcode", item.barcode)
        if(item.expiration != null)
            arg.put("expiry", item.expiration?.format(DateTimeFormatter.ISO_LOCAL_DATE))
        Log.d("SIO", "add_item triggered: $arg")
        sio.emit("add_item", arg)
    }

    private fun updateData(database: JSONArray) {
        for(i in 0 until database.length()) {
            val section = database.getJSONObject(i)
            val sectionID = section.getInt("pos")
            val itemName = if(section.has("name")) section.getString("name") else null
            val expiryDate = if(section.has("expiry") && section["expiry"].toString()!="null") LocalDate.parse(section.getString("expiry")) else null
            val barcode = if(section.has("barcode")) section.getString("barcode") else null
            val newSection: ShelfSection
            if(itemName == null || itemName == "null") { // no item in section
                newSection = ShelfSection(null, sectionID.toString())
            }
            else { // item present
                val item = Item(itemName, expiryDate, barcode)
                newSection = ShelfSection(item, sectionID.toString())
            }
            current?.sections?.put(sectionID.toString(), newSection)
        }
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
                    val newItem: Item =
                            Item(name, if(expiry!="") LocalDate.parse(expiry, DateTimeFormatter.ISO_LOCAL_DATE) else null, if(barcode != null) barcode else null)
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
            //sio.emit("get_data")
            val dialog = AlertDialog.Builder(this)
                    .setView(R.layout.inventory)
                    .show()
            val recycler = dialog.findViewById<RecyclerView>(R.id.inventory_recycler)
            recycler?.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
            if(current != null)
                recycler?.adapter = InventoryAdapter(current!!.sections.filter { it.value.item != null }.values.toList(), dialog)
        }
    }

    private fun sendItem(item: Item, progressDialog: ProgressDialog) {
        // TODO: send to RPI here
        addItem(item)
        progressDialog.dismiss()
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
        for(key in current!!.sections.keys) {
            if(current!!.sections[key]?.item != null && current!!.sections[key]?.item!!.expiresSoon())
                listOfNotifications.add(Notification(current!!.sections[key]!!))
        }
        val recyclerView = findViewById<RecyclerView>(R.id.notifications)
        val adapter = NotificationAdapter(listOfNotifications)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
    }


    private fun initializeCurrentShelf() {
        // Should get shelfSection data from hardware/local database
        for(i in 1..8)
            current?.sections?.put("$i", ShelfSection(null, "$i"))
    }

}
