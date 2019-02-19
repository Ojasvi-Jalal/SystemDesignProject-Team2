package com.example.ojasvi.roboreachapp

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class CategoryAdapter(private var lookupDatabase: LinkedHashMap<String, MutableList<String>>, private var itemsView: RecyclerView?): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition: Int = 0
    private var categories: List<String> = lookupDatabase.keys.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        itemsView?.layoutManager = LinearLayoutManager(parent.context, LinearLayout.VERTICAL, false)
        itemsView?.adapter = ItemAdapter(lookupDatabase[categories[selectedPosition]]!!)
        return CategoryAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.category, parent, false))
    }

    override fun getItemCount(): Int = lookupDatabase.size

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        holder.categoryName.text = categories[position]
        holder.itemView.isSelected = selectedPosition == position

        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition
            itemsView?.adapter = ItemAdapter(lookupDatabase[categories[selectedPosition]]!!)
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val categoryName: TextView = view.findViewById(R.id.name)
        // val categoryPicture = view.findViewById<ImageView>(R.id.image)
        // TODO: remove above if unnecessary?
    }

}