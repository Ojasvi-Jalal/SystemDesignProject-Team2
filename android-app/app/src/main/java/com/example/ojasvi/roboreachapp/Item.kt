package com.example.ojasvi.roboreachapp

import java.io.Serializable
import java.time.LocalDate

data class Item(var title: String = "Unknown",
                var expiration: LocalDate? = LocalDate.now().plusDays(7), // default expiration in a week
                var barcode: String? = ""): Serializable {


    fun expiresSoon(): Boolean =
            if(expiration == null) false
            else (expiration!!.isBefore(LocalDate.now().plusDays(7)) || expiration!!.isEqual(LocalDate.now().plusDays(7)))

}