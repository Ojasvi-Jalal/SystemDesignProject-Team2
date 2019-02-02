package com.example.ojasvi.roboreachapp

import java.io.Serializable
import java.time.LocalDate

data class Item(var title: String = "Unknown",
                var expiration: LocalDate = LocalDate.now().plusDays(7), // default expiration in a month
                var barcode: String = ""): Serializable {


    fun expiresSoon(): Boolean = expiration.isBefore(LocalDate.now().plusDays(7)) || expiration.isEqual(LocalDate.now().plusDays(7))


}