package com.example.ojasvi.roboreachapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.indeterminateProgressDialog
import com.alespero.expandablecardview.ExpandableCardView
import kotlinx.android.synthetic.main.inventory.view.*
import org.jetbrains.anko.find
import java.time.format.DateTimeFormatter

class InventoryAdapter(private val sections: List<ShelfSection>, private val dialog: android.support.v7.app.AlertDialog, private val main: Main): RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var title: String?
        if(sections[position].item != null) {
            title = sections[position].item?.title
            if(title != null && title.length >= 22)
                title = title.take(22) + "..."
        }
        else
            title = "Empty"
        holder.card.setTitle(title)
        if(sections[position].item != null && sections[position].item!!.expiresSoon())
            holder.card.setIcon(R.drawable.ic_error)
        else
            holder.card.setIcon(R.drawable.ic_fine)
        val expiresText = holder.itemView.findViewById<TextView>(R.id.expires)
        expiresText.text = if(sections[position].item != null && sections[position].item?.expiration != null) sections[position].item?.expiration?.format(DateTimeFormatter.ISO_LOCAL_DATE) else "N/A"
        val retrieveButton = holder.itemView.findViewById<Button>(R.id.retrieve)
        retrieveButton.setOnClickListener {
            dialog.dismiss()
            main.progressDialog = holder.itemView.context.indeterminateProgressDialog("Retrieving item...")
            main.progressDialog.show()
            main.retrieveItem(sections[position].name, sections[position].item?.title)
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