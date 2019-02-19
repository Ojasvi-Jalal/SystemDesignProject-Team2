package com.example.ojasvi.roboreachapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.jetbrains.anko.indeterminateProgressDialog
import java.time.format.DateTimeFormatter
import java.util.*
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

@GlideModule
class MyAppGlideModule : AppGlideModule()

class ShelfActivity : AppCompatActivity() {

    private lateinit var socket: Socket

    private lateinit var requestQueue: RequestQueue
    private val requestUrl = "http://192.168.105.131:8000" // static gabumon IP. did not seem to work as http://gabumon:8000

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelf)

        requestQueue = Volley.newRequestQueue(this)

        supportActionBar?.title = "ShelfSection"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getShelfData()
    }

    private fun httpRequest(request: String) {
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, "$requestUrl/$request",
                Response.Listener<String> { response ->
                    AlertDialog.Builder(this)
                            .setTitle("Response")
                            .setMessage(response)
                            .show()
                },
                Response.ErrorListener {
                    AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Request $request failed:\n${if (it.networkResponse == null) "No response from $requestUrl" else "Error: $it.networkResponse"}")
                            .show()
                })
        requestQueue.add(stringRequest)
    }

    private fun getShelfData() {

        val shelfSection: ShelfSection = intent.getSerializableExtra("shelfSection") as ShelfSection
        val item = shelfSection.item

        val shelfName: TextView = findViewById(R.id.name)
        val itemTitle: TextView = findViewById(R.id.title)
        val expirationDate: EditText = findViewById(R.id.expiry)
        val barcode: EditText = findViewById(R.id.barcode)
        val warning: ImageView = findViewById(R.id.warn)

        shelfName.text = shelfSection.name
        when (item) {
            null -> {
                itemTitle.text = "Empty"
                warning.visibility = View.GONE
            }
            else -> {
                itemTitle.text = item.title
                if(item.expiresSoon()) warning.visibility = View.VISIBLE
                else warning.visibility = View.GONE
            }
        }
        expirationDate.setText(item?.expiration?.format(DateTimeFormatter.ISO_LOCAL_DATE))
        barcode.setText(item?.barcode)

        val retrieveButton = findViewById<Button>(R.id.retrieveButton)
        val storeButton = findViewById<Button>(R.id.putButton)


        val mEdit1 = findViewById<EditText>(R.id.expiry)
        val mEdit2 = findViewById<EditText>(R.id.barcode)
        mEdit1.isEnabled = false
        mEdit2.isEnabled = false

        // Edit button trigger
        val editButton1 = findViewById<FloatingActionButton>(R.id.editButton1)
        val editButton2 = findViewById<FloatingActionButton>(R.id.editButton2)

        if(itemTitle.text == "Empty"){
            retrieveButton.visibility = View.GONE
            editButton1.visibility = View.GONE
            editButton2.visibility = View.GONE
        }
        else
            storeButton.visibility = View.GONE

        retrieveButton.setOnClickListener {
            val progress = indeterminateProgressDialog("Retrieving item...")
            progress.show()
            progress.setCancelable(false)
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    progress.dismiss()
                }
            }, 10000) // fake 10sec delay
            httpRequest("move_to?pos=${shelfSection.name}")
        }

        storeButton.setOnClickListener {
            val progress = indeterminateProgressDialog("Storing item...")
            progress.show()
            progress.setCancelable(false)
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    progress.dismiss()
                }
            }, 10000) // fake 10sec delay
            httpRequest("move_to?pos=${shelfSection.name}")
        }


        editButton1.setOnClickListener {
            mEdit1.isEnabled  = true
        }

        editButton2.setOnClickListener {
            mEdit2.isEnabled = true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setImageView(view: ImageView, searchTerm: String) {
        val url: String = "https://source.unsplash.com/random/?$searchTerm"
        GlideApp.with(this).load(url).into(view)
    }


    private fun setupSocket(host: String) {
        socket = IO.socket(host)
        socket
                .on(Socket.EVENT_CONNECT) {
                    Log.d("SocketIO", "Connected to $host")
                    socket.emit("connected") } // emit successful connect message
                .on("some_event") {
                    Log.d("SocketIO", "test event response sent")
                    socket.emit("some_response_back_to_server", Ack {
                        // you can do some stuff here when server sends back an acknowledgment of receipt
                    }) } // emit message to server
                .on("some_json") {
                    Log.d("SocketIO", "received JSONObject as first argument of event some_json")
                    var someJsonObject = it[0] as JSONObject } // receive JSon file passed as the first argument
                .on(Socket.EVENT_DISCONNECT) {
                    Log.d("SocketIO", "Disconnected from $host")
                    socket.emit("disconnected") } // emit disconnect message
        socket.connect()
    }

}
