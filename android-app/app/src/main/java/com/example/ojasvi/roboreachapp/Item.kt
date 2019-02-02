package com.example.ojasvi.roboreachapp

import java.io.Serializable
import java.time.LocalDate

data class Item(var title: String = "Unknown",
                var expiration: LocalDate = LocalDate.now().plusWeeks(1), // default expiry in a week.
                var barcode: String = ""): Serializable