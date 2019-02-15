package com.example.ojasvi.roboreachapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator


class LauncherActivity : AppCompatActivity() {

    private var identifier: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        supportActionBar?.hide()

        // checks if the initial configuration gone through (a single shelf set up)

        val preferences = applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val configured = preferences.getBoolean("configured", false)

        if(configured) {
            editor.apply()
            startActivity(Intent(this, MainActivity::class.java))
        }
        else {
            val continueButton = findViewById<Button>(R.id.continueButton)
            continueButton.setOnClickListener {
                scanBarcode()
            }
        }

        //GlideApp.with(this).load("https://source.unsplash.com/random/?shelves,bright").centerCrop().into(findViewById(R.id.background))

    }

    private fun isIpAddress(input: String) = Patterns.IP_ADDRESS.matcher(input).matches()

    private fun scanBarcode() {
        val integrator = IntentIntegrator(this)
        integrator.setOrientationLocked(true)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val preferences = applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null) {
            if(result.contents == null)
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            else {
                Log.d("LauncherActivity", "Scanned: ${result.contents}")
                identifier = result.contents
                if(true) { // check if actual shelf id
                    editor.putBoolean("configured", true)
                    editor.putInt("number_of_shelves", 1)
                    editor.putString("shelf_1", identifier)
                    editor.apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                else {
                    Toast.makeText(this, "This is not a valid shelf ID!", Toast.LENGTH_LONG).show()
                }
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data)
    }
}
