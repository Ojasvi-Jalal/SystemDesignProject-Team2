package com.example.ojasvi.roboreachapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.widget.*
import com.google.zxing.integration.android.IntentIntegrator
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.android.synthetic.main.main.view.*
import org.jetbrains.anko.contentView
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

        generateFakeItems()

        doAsync { sio.emit("get_data") }

    }

    private fun setUpSocket() {

        host = "http://172.20.145.3:8000" // TODO: change/remove for prod to use 192.168.105.131 (gabumon)
        sio = IO.socket(host)

        sio.on(Socket.EVENT_CONNECT) {
            Log.d("SIO", "Connected to $host")
            val snack = Snackbar.make(contentView!!, "Connected", Snackbar.LENGTH_LONG)
            snack.view.findViewById<TextView>(android.support.design.R.id.snackbar_text).setTextColor(Color.parseColor("#006400"))
            snack.view.setBackgroundColor(Color.GREEN)
            snack.show()
            //longSnackbar(findViewById(R.id.layout), "Connected")
        }

        sio.on(Socket.EVENT_DISCONNECT) {
            Log.d("SIO", "Disconnected from $host")
            val snack = Snackbar.make(contentView!!, "Disconnected", Snackbar.LENGTH_LONG)
            snack.view.setBackgroundColor(Color.YELLOW)
            snack.view.findViewById<TextView>(android.support.design.R.id.snackbar_text).setTextColor(Color.parseColor("#999900"))
            snack.show()
        }

        sio.on("get_data") { parameters ->
            // assumes first parameters is a list of dictionaries
            Log.d("SIO", "Received data: ${parameters[0]}")
            updateData(parameters[0] as JSONArray)
            runOnUiThread { generateNotifications() } // updates notifications on main
        }

        sio.on("move_to") { parameters ->
            val response: JSONObject? = parameters[0] as? JSONObject
            val success = response?.getBoolean("success")
            if (success != null && !success) { // failure
                val error: String = response.getString("message")
                Log.d("SIO", "move_to ERROR: $error")
            } else { // success
                Log.d("SIO", "move_to SUCCESS")
            }
        }

        sio.on("add_item") { parameters ->
            val response: JSONObject? = parameters[0] as? JSONObject
            val success = response?.getBoolean("success")
            if (success != null && !success) {
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
            if (success != null && !success) {
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

    fun moveTo(pos: String) {
        Log.d("SIO", "Retrieving item in position $pos")
        val arg = JSONObject().put("pos", pos.toIntOrNull())
        sio.emit("retrieve_item", arg)
    }

    private fun addItem(item: Item) {
        // Find a free shelf section:
        val freeSection: ShelfSection? = current?.sections?.filter { it.value.item == null || it!!.value.item?.title == "null" }?.values?.toList()?.get(0)
        freeSection?.item = item
        // Send details to RPi
        val arg = JSONObject()
        arg.put("pos", freeSection?.name?.toIntOrNull())
        arg.put("name", item.title)
        if (item.barcode != null)
            arg.put("barcode", item.barcode)
        if (item.expiration != null)
            arg.put("expiry", item.expiration?.format(DateTimeFormatter.ISO_LOCAL_DATE))
        Log.d("SIO", "add_item triggered: $arg")
        sio.emit("add_item", arg)
    }

    private fun updateData(database: JSONArray) {
        for (i in 0 until database.length()) {
            val section = database.getJSONObject(i)
            val sectionID = section.getInt("pos")
            val itemName = if (section.has("name")) section.getString("name") else null
            val expiryDate = if (section.has("expiry") && section["expiry"].toString() != "null") LocalDate.parse(section.getString("expiry")) else null
            val barcode = if (section.has("barcode")) section.getString("barcode") else null
            val newSection: ShelfSection
            if (itemName == null || itemName == "null") { // no item in section
                newSection = ShelfSection(null, sectionID.toString())
            } else { // item present
                val item = Item(itemName, expiryDate, barcode)
                newSection = ShelfSection(item, sectionID.toString())
            }
            current?.sections?.put(sectionID.toString(), newSection)
        }
    }

    private fun setUpStoreButton() {

        val storeButton: Button = findViewById(R.id.store)
        storeButton.setOnClickListener {
            setUpStoreDialog()
        }
    }

    private fun setUpStoreDialog() {

        val alertDialog = AlertDialog.Builder(this)
                .setView(R.layout.store)
                .show()

        val nameField: EditText? = findViewById<EditText>(R.id.name)
        val expiryField: EditText? = findViewById<EditText>(R.id.expiry)

        setUpScanButton(alertDialog)

        val exitButton = alertDialog.findViewById<ImageButton>(R.id.exitButton)
        exitButton?.setOnClickListener { alertDialog.dismiss() }

        val confirmButton = alertDialog.findViewById<Button>(R.id.storeButton)
        confirmButton?.setOnClickListener {
            val name = nameField?.text.toString()
            val barcode = null
            val expiry = expiryField?.text.toString()
            if (name == "")
                // TODO: fix below
                //longSnackbar(alertDialog, "Name should not be blank!")
            else if (!expiry.matches(Regex("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))")))  // 2xxx-xx-xx format
                // TODO: fix below
                //longSnackbar(alertDialog, "Incorrect expiry date format!")
            else {
                val newItem: Item =
                        Item(name, if (expiry != "") LocalDate.parse(expiry, DateTimeFormatter.ISO_LOCAL_DATE) else null, if (barcode != null) barcode else null)
                alertDialog.dismiss()
                val progressDialog = indeterminateProgressDialog("Storing item...")
                progressDialog.show()
                progressDialog.setCancelable(false)
                sendItem(newItem, progressDialog)
            }
        }

        setUpLookupButton(alertDialog)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null) {
            if(result.contents == null)
                Toast.makeText(this, "Null contents", Toast.LENGTH_SHORT).show()
            else {
                getItemData(result.contents)
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getItemData(barcode: String) {
        val requestQueue = Volley.newRequestQueue(this)
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, "https://world.openfoodfacts.org/api/v0/product/$barcode.json",
                Response.Listener<String> { response ->
                    val responseJSON: JSONObject = JSONObject(response)
                    val productName: String
                    if(responseJSON.getString("status_verbose") == "product found")
                        productName = responseJSON.getJSONObject("product").getString("product_name")
                    else
                        productName = "Product not found"
                    val nameField: EditText? = findViewById<EditText>(R.id.name)
                    nameField?.setText(productName)
                },
                Response.ErrorListener {
                    AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Request $barcode failed:\n${if (it.networkResponse == null) "No response" else "Error: $it.networkResponse"}")
                            .show()
                })
        requestQueue.add(stringRequest)
    }

    private fun setUpScanButton(dialog: AlertDialog) {
        val scanButton = dialog.findViewById<ImageButton>(R.id.scanButton)
        scanButton?.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setOrientationLocked(true)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_13, IntentIntegrator.EAN_8, IntentIntegrator.UPC_A, IntentIntegrator.UPC_E)
            integrator.setBeepEnabled(true)
            integrator.initiateScan()
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
            if (current != null)
                recycler?.adapter = InventoryAdapter(current!!.sections.filter { it.value.item != null }.values.toList(), dialog, this)
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
        for (i in 1..numberOfShelves) {
            val identifier = preferences!!.getString("shelf_${i}", "")
            val title = preferences!!.getString("shelf_${i}_title", "Kitchen")
            shelves.add(Shelf("shelf_${i}", identifier, title))
        }
    }

    private fun generateNotifications() {
        val listOfNotifications = mutableListOf<Notification>()
        for (key in current!!.sections.keys) {
            if (current!!.sections[key]?.item != null && current!!.sections[key]?.item!!.expiresSoon())
                listOfNotifications.add(Notification(current!!.sections[key]!!))
        }
        val recyclerView = findViewById<RecyclerView>(R.id.notifications)
        val adapter = NotificationAdapter(listOfNotifications)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
    }

    private fun generateFakeItems() {
        // Creates fake Item and Notification objects
        val jam = Item("Jam", LocalDate.now().plusDays(16), "564648646464")
        current!!.sections["1"]?.item = jam
        val bread = Item("Bread", LocalDate.now().plusDays(4), "548674646464")
        current!!.sections["2"]?.item = bread
    }


    private fun initializeCurrentShelf() {
        // Should get shelfSection data from hardware/local database
        for (i in 1..8)
            current?.sections?.put("$i", ShelfSection(null, "$i"))
    }

}

