package com.example.ojasvi.roboreachapp

import android.graphics.Color
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

class CategoryAdapter(private var lookupDatabase: LinkedHashMap<String, MutableList<String>>, private var itemsView: RecyclerView?, private var alertDialog: AlertDialog, private var pickerDialog: AlertDialog): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition: Int = 0
    private lateinit var selectedRow: CategoryAdapter.ViewHolder
    private var categories: List<String> = lookupDatabase.keys.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        itemsView?.layoutManager = LinearLayoutManager(parent.context, LinearLayout.VERTICAL, false)
        itemsView?.adapter = ItemAdapter(lookupDatabase[categories[selectedPosition]]!!, alertDialog, pickerDialog)
        return CategoryAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.category, parent, false))
    }

    override fun getItemCount(): Int = lookupDatabase.size

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        holder.categoryName.text = categories[position]
        holder.itemView.isSelected = selectedPosition == position
        if(holder.itemView.isSelected)
            selectRow(holder)
        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            if(::selectedRow.isInitialized)
                deselectRow(selectedRow)
            selectRow(holder)
            itemsView?.adapter = ItemAdapter(lookupDatabase[categories[selectedPosition]]!!, alertDialog, pickerDialog)
        }
    }

    private fun selectRow(holder: CategoryAdapter.ViewHolder) {
        selectedRow = holder
        holder.categoryRow.setBackgroundColor(Color.WHITE)
        holder.categoryName.setTextColor(Color.parseColor("#c00000"))
        holder.categoryArrow.setColorFilter(Color.parseColor("#c00000"))
    }

    private fun deselectRow(holder: CategoryAdapter.ViewHolder) {
        holder.categoryRow.setBackgroundColor(Color.parseColor("#c00000"))
        holder.categoryName.setTextColor(Color.WHITE)
        holder.categoryArrow.setColorFilter(Color.WHITE)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val categoryName: TextView = view.findViewById(R.id.name)
        val categoryRow: RelativeLayout = view.findViewById(R.id.category)
        val categoryArrow: ImageView = view.findViewById(R.id.arrow)
    }

}