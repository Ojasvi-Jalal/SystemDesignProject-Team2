package com.example.ojasvi.roboreachapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.indeterminateProgressDialog
import com.alespero.expandablecardview.ExpandableCardView
import java.time.format.DateTimeFormatter

class InventoryAdapter(private val sections: List<ShelfSection>, private val dialog: android.support.v7.app.AlertDialog, private val main: Main): RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.card.setTitle(if(sections[position].item != null) sections[position].item?.title else "Empty")
        if(sections[position].item != null && sections[position].item!!.expiresSoon())
            holder.card.setIcon(R.drawable.ic_error)
        else
            holder.card.setIcon(R.drawable.ic_fine)
        val barcodeText = holder.itemView.findViewById<TextView>(R.id.barcode)
        val expiresText = holder.itemView.findViewById<TextView>(R.id.expires)
        barcodeText.text = if(sections[position].item != null && sections[position].item?.barcode != null) sections[position].item?.barcode else "-"
        expiresText.text = if(sections[position].item != null && sections[position].item?.expiration != null) sections[position].item?.expiration?.format(DateTimeFormatter.ISO_LOCAL_DATE) else "-"
        val retrieveButton = holder.itemView.findViewById<Button>(R.id.retrieve)
        retrieveButton.setOnClickListener {
            dialog.dismiss()
            val progressDialog = holder.itemView.context.indeterminateProgressDialog("Retrieving item...")
            progressDialog.show()
            main.retrieveItem(sections[position].name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.inventory_card, parent, false))
    }

    override fun getItemCount(): Int = sections.map{ it.item != null }.size

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val card : ExpandableCardView = itemView.findViewById(R.id.item_card)
    }

}