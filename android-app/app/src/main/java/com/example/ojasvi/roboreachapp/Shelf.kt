package com.example.ojasvi.roboreachapp

data class Shelf(var id: String, var identifier: String = "", var title: String = "Unknown", var sections: MutableList<ShelfSection> = mutableListOf())