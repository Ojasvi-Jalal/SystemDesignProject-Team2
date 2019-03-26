package com.example.ojasvi.roboreachapp

import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

class ItemAdapter(private var items: MutableList<String>, private var alertDialog: AlertDialog, private var pickerDialog: AlertDialog): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        return ItemAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {
        holder.textView.text = items[position]
        holder.textView.setOnClickListener {
            pickerDialog.dismiss()
            val nameField: EditText? = alertDialog.findViewById<EditText>(R.id.itemName)
            nameField?.setText(items[position])
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        var textView: TextView = view.findViewById(R.id.text)
    }


}