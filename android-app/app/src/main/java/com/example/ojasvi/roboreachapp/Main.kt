package com.example.ojasvi.roboreachapp

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.HORIZONTAL
import android.support.v7.widget.RecyclerView.VERTICAL
import android.text.InputType
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import android.widget.*
import com.google.zxing.integration.android.IntentIntegrator
import com.redmadrobot.inputmask.MaskedTextChangedListener
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.item.view.*
import kotlinx.android.synthetic.main.main.view.*
import kotlinx.android.synthetic.main.store.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.design.indefiniteSnackbar
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.doAsync
import java.time.LocalDate
import org.jetbrains.anko.indeterminateProgressDialog
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Thread.sleep
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class Main : AppCompatActivity() {

    private var preferences: SharedPreferences? = null
    private var current: Shelf? = null
    private var numberOfShelves: Int = 0
    private lateinit var sio: Socket
    private var host: String = ""
    private var shelves: MutableList<Shelf> = mutableListOf()
    lateinit var alertDialog: AlertDialog
    private val lookupDatabase = LinkedHashMap<String, MutableList<String>>()
    lateinit var progressDialog: ProgressDialog

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
        setUpScanButton()
        setUpHelpButton()

        //generateFakeItems()

        doAsync { sio.emit("get_data") }

    }

    private fun setUpHelpButton() {
        val helpButton = findViewById<Button>(R.id.help_button)
        helpButton.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
                    .setView(R.layout.help_dialog)
                    .show()
            val closeButton = alertDialog.findViewById<ImageButton>(R.id.exitButton)
            closeButton?.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    private fun setUpSocket() {

        //host = "http://129.215.2.183:8000" // TODO: remove for prod to use 192.168.105.131 (gabumon)
        sio = IO.socket(host)

        sio.on(Socket.EVENT_CONNECT) {
            Log.d("SIO", "Connected to $host")
            val snack = Snackbar.make(contentView!!, "Connected", Snackbar.LENGTH_LONG)
            snack.view.findViewById<TextView>(android.support.design.R.id.snackbar_text).setTextColor(Color.parseColor("#006400"))
            snack.view.setBackgroundColor(Color.GREEN)
            snack.show()
            runOnUiThread {
                val storeButton: Button = findViewById(R.id.store)
                val inventoryButton: Button = findViewById(R.id.inventory)
                storeButton.isEnabled = true
                inventoryButton.isEnabled = true
                //sio.emit("get_data")
            }
        }

        sio.on(Socket.EVENT_DISCONNECT) {
            Log.d("SIO", "Disconnected from $host")
            val snack = Snackbar.make(contentView!!, "Disconnected", Snackbar.LENGTH_LONG)
            snack.view.setBackgroundColor(Color.YELLOW)
            snack.view.findViewById<TextView>(android.support.design.R.id.snackbar_text).setTextColor(Color.parseColor("#999900"))
            snack.show()
            runOnUiThread {
//                val storeButton: Button = findViewById(R.id.store)
//                val inventoryButton: Button = findViewById(R.id.inventory)
//                storeButton.isEnabled = false
//                inventoryButton.isEnabled = false
            }
        }

        sio.on("get_data") { parameters ->
            // assumes first parameters is a list of dictionaries
            Log.d("SIO", "Received data: ${parameters[0]}")
            updateData(parameters[0] as JSONArray)
            runOnUiThread { generateNotifications() } // updates notifications on main
        }

        sio.on("retrieve_result") { parameters ->
            runOnUiThread{ if(::progressDialog.isInitialized) progressDialog.dismiss() } // enable interaction again
            val response: JSONObject? = parameters[0] as? JSONObject
            val success = response?.getBoolean("success")
            Log.d("SIO", "retrieve_result success=$success")
            if (success != null && !success) { // failure
                val error: String = response.getString("error")
                Log.d("SIO", "retrieve_result ERROR: $error")
                runOnUiThread {
                    AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage(error)
                            .setIcon(R.drawable.ic_error)
                            .setNeutralButton("Dismiss", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                            .show()
                }
            }
        }

        sio.on("store_result") { parameters ->
            runOnUiThread{ if(::progressDialog.isInitialized) progressDialog.dismiss() } // enable interaction again
            val response: JSONObject? = parameters[0] as? JSONObject
            val success = response?.getBoolean("success")
            Log.d("SIO", "store_result success=$success")
            if (success != null && !success) { // failure
                val error: String = response.getString("error")
                Log.d("SIO", "store_result ERROR: $error")
                runOnUiThread {
                    AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage(error)
                            .setIcon(R.drawable.ic_error)
                            .setNeutralButton("Dismiss", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                            .show()
                }
            }
        }

//        sio.on("move_to") { parameters ->
//            val response: JSONObject? = parameters[0] as? JSONObject
//            val success = response?.getBoolean("success")
//            if (success != null && !success) { // failure
//                val error: String = response.getString("message")
//                Log.d("SIO", "move_to ERROR: $error")
//                runOnUiThread {
//                    AlertDialog.Builder(this)
//                            .setTitle("Error")
//                            .setMessage(error)
//                            .setIcon(R.drawable.ic_error)
//                            .setNeutralButton("Dismiss", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
//                            .show()
//                }
//            } else { // success
//                Log.d("SIO", "move_to SUCCESS")
//            }
//        }

        sio.on("scan_result") { parameters ->
            runOnUiThread{ if(::progressDialog.isInitialized) progressDialog.dismiss() } // enable interaction again
            val response: JSONObject? = parameters[0] as? JSONObject
            val success = response?.getBoolean("success")
            Log.d("SIO", "scan_result success=$success")
            if (success != null && !success) { // failure
                val error: String = response.getString("error")
                Log.d("SIO", "scan_result ERROR: $error")
                runOnUiThread {
                    AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage(error)
                            .setIcon(R.drawable.ic_error)
                            .setNeutralButton("Dismiss", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                            .show()
                }
            }
            else
                sio.emit("get_data")
        }

        sio.on("scan") {
            Log.d("SIO", "Manual scan triggered")
            runOnUiThread {
                progressDialog = indeterminateProgressDialog("Automatic sync in progress...")
                progressDialog.show()
            }
        }

        sio.on("add_item") { parameters ->
            runOnUiThread { progressDialog.dismiss() } // enable interaction again
            val response: JSONObject? = parameters[0] as? JSONObject
            val success = response?.getBoolean("success")
            if (success != null && !success) {
                val error: String = response.getString("message")
                Log.d("SIO", "add_item ERROR: $error")
                runOnUiThread {
                    AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage(error)
                            .setIcon(R.drawable.ic_error)
                            .setNeutralButton("Dismiss", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                            .show()
                }
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
                runOnUiThread {
                    AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage(error)
                            .setIcon(R.drawable.ic_error)
                            .setNeutralButton("Dismiss", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                            .show()
                }
            } else { // success
                Log.d("SIO", "remove_item SUCCESS")
            }
        }

        sio.on("message") { parameters ->
            Log.d("SIO", parameters[0] as String)
        }


        Log.d("SIO", "Attempting to connect to $host")
        sio.connect()
    }

//    private fun removeItem(pos: String) {
//        Log.d("SIO", "Removing item in position $pos")
//        val arg = JSONObject().put("pos", pos.toIntOrNull())
//        sio.emit("remove_item", arg)
//    }

    fun retrieveItem(pos: String, itemName: String?) {
        Log.d("SIO", "Retrieving item in position $pos")
        val arg = JSONObject().put("pos", pos.toIntOrNull())
        if(!lookupDatabase.containsKey("Recents"))
            lookupDatabase["Recents"] = mutableListOf()
        if(itemName != null && lookupDatabase.containsKey("Recents"))
            if(!lookupDatabase["Recents"]!!.contains(itemName))
                lookupDatabase["Recents"]?.add(itemName)
        sio.emit("retrieve_item", arg)
    }

    private fun updateData(database: JSONArray) {
        current?.sections?.clear()
        for (i in 0 until database.length()) {
            val section = database.getJSONObject(i)
            val sectionID = section.getInt("pos")
            val itemName = if (section.has("name")) section.getString("name") else null
            val expiryDate = if (section.has("expiry") && section["expiry"].toString() != "null") LocalDate.parse(section.getString("expiry")) else null
            val barcode = if (section.has("barcode") && section.getString("barcode") != "null") section.getString("barcode") else null
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

    private fun setUpScanButton() {
        val scanButton: Button = findViewById(R.id.scan_button)
        scanButton.setOnClickListener {
            progressDialog = indeterminateProgressDialog("Syncing state...")
            progressDialog.show()
            sio.emit("scan")
            Log.d("SIO", "Scan event sent")
        }
    }

    private fun setUpStoreButton() {

        val storeButton: Button = findViewById(R.id.store)
        storeButton.setOnClickListener {
            alertDialog = AlertDialog.Builder(this)
                    .setView(R.layout.store)
                    .show()

            val nameField: EditText? = alertDialog.findViewById<EditText>(R.id.itemName)
            val expiryField: EditText? = alertDialog.findViewById<EditText>(R.id.expiry)
            val largeSwitch: Switch? = alertDialog.findViewById(R.id.large_switch)

            val largeFree: List<ShelfSection>? = current?.sections?.filter { it.value.item == null || it!!.value.item?.title == "null" }?.values?.toList()?.filter { it.name == "4" || it.name == "5" }
            if(largeFree != null && largeFree.isEmpty()) { // no empty large shelves, disable large item switch
                largeSwitch?.isEnabled = false
                largeSwitch?.visibility = View.GONE
                val fullText: TextView? = alertDialog.findViewById(R.id.full_text)
                fullText?.visibility = View.VISIBLE
                fullText?.setOnClickListener {
                    longSnackbar(it, "All large sections are full!")
                }
            }

            if(expiryField != null) {
                expiryField.inputType = InputType.TYPE_NULL
                expiryField.focusable = View.NOT_FOCUSABLE
                expiryField.keyListener = null
                expiryField.setOnClickListener {
                        val calendar = Calendar.getInstance()
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        val month = calendar.get(Calendar.MONTH)
                        val year = calendar.get(Calendar.YEAR)
                        //  val picker = DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, DatePickerDialog.OnDateSetListener { view, yearSelected, monthSelected, daySelected ->
                    val picker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, yearSelected, monthSelected, daySelected ->
                            val mm = monthSelected + 1
                            val dd: String = if (daySelected<10) "0$daySelected" else daySelected.toString()
                            expiryField.setText("$yearSelected-${if (mm<10) "0$mm" else "$mm"}-$dd")
                        }, year, month, day)
                        picker.show()
                }
            }

            setUpScanButton(alertDialog)
            setUpLookupButton(alertDialog)

            val exitButton = alertDialog.findViewById<ImageButton>(R.id.exitButton)
            exitButton!!.setOnClickListener { alertDialog.dismiss() }

            val confirmButton = alertDialog.findViewById<Button>(R.id.storeButton)
            confirmButton!!.setOnClickListener {
                val name = nameField?.text.toString()
                val barcode = null
                val expiry = expiryField?.text.toString()
                if (name == "") {
                    longSnackbar(it, "Item name should not be blank!")
                } else if (expiry != "" && !expiry.matches(Regex("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))"))) { // 2xxx-xx-xx format
                    longSnackbar(it, "Incorrect expiry date format!")
                }
                else {
                    // Adding to recents
                    if(!lookupDatabase.containsKey("Recents"))
                        lookupDatabase["Recents"] = mutableListOf()
                    if(lookupDatabase.containsKey("Recents"))
                        if(!lookupDatabase["Recents"]!!.contains(name))
                            lookupDatabase["Recents"]?.add(name)
                    // continuing store
                    Log.d("STORE ITEM", "Started item store")
                    val newItem: Item =
                            Item(name, if (expiry != "") LocalDate.parse(expiry, DateTimeFormatter.ISO_LOCAL_DATE) else null, if (barcode != null) barcode else null)
                    alertDialog.dismiss()
                    progressDialog = indeterminateProgressDialog("Storing item...")
                    progressDialog.show()
                    //progressDialog.setCancelable(false)
                    sendItem(newItem, largeSwitch!!.isChecked, progressDialog)
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null)
                Toast.makeText(this, "Scan aborted", Toast.LENGTH_SHORT).show()
            else {
                getItemData(result.contents)
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getItemData(barcode: String) {

        val mock = true // Flag to use mock database if no internet connectivity available for demo

        if(mock) {
            /* Mock data for no internet connection */
            val mockItemDatabase: HashMap<String, String> = hashMapOf()
            // mockItemDatabase["barcode here"] = "Item name here"
            mockItemDatabase["03045015"] = "String. 40m. - Tesco"
            mockItemDatabase["03246566"] = "Tesco Cashew Nuts - 25g"
            mockItemDatabase["42182634"] = "Vaseline - 100ml"
            mockItemDatabase["5000436574637"] = "Ibuprofen 200mg caplets - Tesco - 16x200mg"
            /* End of mock data */
            if(mockItemDatabase.containsKey(barcode)) {
                val nameField: EditText? = alertDialog.findViewById<EditText>(R.id.itemName)
                nameField?.setText(mockItemDatabase[barcode])
            }
            else {
                AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Product not found in database!")
                        .setIcon(R.drawable.ic_error)
                        .setNeutralButton("Dismiss", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                        .show()
            }
        }

        else {
            val requestQueue = Volley.newRequestQueue(this)
            // Request a string response from the provided URL.
            val stringRequest = StringRequest(Request.Method.GET, "https://world.openfoodfacts.org/api/v0/product/$barcode.json",
                    Response.Listener<String> { response ->
                        val responseJSON: JSONObject = JSONObject(response)
                        val productName: String
                        if (responseJSON.getString("status_verbose") == "product found") {
                            productName = responseJSON.getJSONObject("product").getString("product_name")
                            val nameField: EditText? = alertDialog.findViewById<EditText>(R.id.itemName)
                            nameField?.setText(productName)
                        } else {
                            alertDialog.dismiss()
                            AlertDialog.Builder(this)
                                    .setTitle("Error")
                                    .setMessage("Product not found in database!")
                                    .setIcon(R.drawable.ic_error)
                                    .setNeutralButton("Dismiss", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                                    .show()
                        }

                    },
                    Response.ErrorListener {
                        AlertDialog.Builder(this)
                                .setTitle("Error")
                                .setMessage("Request $barcode failed:\n${if (it.networkResponse == null) "No response" else "Error: $it.networkResponse"}")
                                .show()
                    })
            requestQueue.add(stringRequest)
        }

    }

    private fun setUpScanButton(dialog: AlertDialog) {
        val scanButton = dialog.findViewById<Button>(R.id.scanButton)
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
            //alertDialog.dismiss()
            lookupDatabase["Food"] = mutableListOf()
            lookupDatabase["Food"]?.add("Orange")
            lookupDatabase["Food"]?.add("Kiwi")
            lookupDatabase["Food"]?.add("Pear")
            lookupDatabase["Food"]?.add("Garlic")
            lookupDatabase["Food"]?.add("Banana")
            lookupDatabase["Food"]?.add("Nuts")
            lookupDatabase["Drinks"] = mutableListOf()
            lookupDatabase["Drinks"]?.add("Coke")
            lookupDatabase["Drinks"]?.add("Juice")
            lookupDatabase["Drinks"]?.add("Water")
            lookupDatabase["Drinks"]?.add("Tea")
            lookupDatabase["Drinks"]?.add("Coffee")
            lookupDatabase["Drinks"]?.add("Energy drink")
            val pickerDialog = AlertDialog.Builder(this)
                    .setView(R.layout.activity_picker)
                    .show()
            val categoriesView = pickerDialog.findViewById<RecyclerView>(R.id.categories)
            val itemsView = pickerDialog.findViewById<RecyclerView>(R.id.items)
            itemsView?.addItemDecoration(DividerItemDecoration(this, VERTICAL))
            categoriesView?.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
            categoriesView?.adapter = CategoryAdapter(lookupDatabase, itemsView, alertDialog, pickerDialog)
        }
    }

    private fun setUpInventoryButton() {
        val inventoryButton: Button = findViewById(R.id.inventory)
        inventoryButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                    .setView(R.layout.inventory)
                    .show()
            val notice = dialog.findViewById<TextView>(R.id.empty_notice)
            var items = listOf<ShelfSection>()
            if(current != null)
                items = current!!.sections.filter { it.value.item != null }.values.toList()
            if(items.isEmpty()) {
                notice?.visibility = View.VISIBLE
                Log.d("INVENTORY", "Inventory error message displayed")
            }
            else
                Log.d("INVENTORY", "Current: items")
            val recycler = dialog.findViewById<RecyclerView>(R.id.inventory_recycler)
            recycler?.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
            if (current != null)
                recycler?.adapter = InventoryAdapter(items, dialog, this)
        }
    }

    private fun sendItem(item: Item, large: Boolean, progressDialog: ProgressDialog) {

        // Find a free shelf section:
        var freeSections: List<ShelfSection>? = current?.sections?.filter { it.value.item == null || it!!.value.item?.title == "null" }?.values?.toList()

        if(large) // allow only large sections 4 or 5 if true
            freeSections = freeSections?.filter { it.name == "4" || it.name == "5" }

        if (freeSections != null && freeSections.isNotEmpty()) {
            val freeSection = freeSections?.get(0)
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
        } else {
            AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("All shelf sections are full!")
                    .setIcon(R.drawable.ic_error)
                    .setNeutralButton("Dismiss", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                    .show()
        }
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

//    private fun generateFakeItems() {
//        // Creates fake Item and Notification objects
//        val jam = Item("Jam", LocalDate.now().plusDays(16), "564648646464")
//        current!!.sections["1"]?.item = jam
//        val bread = Item("Bread", LocalDate.now().plusDays(4), "548674646464")
//        current!!.sections["2"]?.item = bread
//    }


    private fun initializeCurrentShelf() {
        // Should get shelfSection data from hardware/local database
        for (i in 1..8)
            current?.sections?.put("$i", ShelfSection(null, "$i"))
    }

}


