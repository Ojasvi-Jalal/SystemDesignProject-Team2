package com.example.ojasvi.roboreachapp

import java.time.LocalDate

data class Item(var title: String = "Unknown",
                var description: String = "",
                var expiration: LocalDate = LocalDate.now().plusMonths(1), // default expiration in a month
                var barcode: String = "")