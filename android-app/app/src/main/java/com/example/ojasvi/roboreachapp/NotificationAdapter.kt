package com.example.ojasvi.roboreachapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class NotificationAdapter(notifications: List<Notification>): RecyclerView.Adapter<NotificationAdapter.ViewHolder>()  {

    var notifications: List<Notification> = notifications

    override fun getItemCount(): Int = notifications.size

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.messageText.text = String.format("${notifications[i].shelf.item.title} in shelf section ${notifications[i].shelf.name} is about to expire.")
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(p0.context)
            .inflate(R.layout.notification_card, p0, false))


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var messageText: TextView = itemView.findViewById(R.id.notification_text)
    }

}