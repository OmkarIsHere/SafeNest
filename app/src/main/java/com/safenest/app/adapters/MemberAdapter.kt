package com.safenest.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.safenest.app.R
import com.safenest.app.model.Member


class MemberAdapter(private val context: Context, private val members : ArrayList<Member>) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.member_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.memberName.text = "Name: ${members[position].userName}"
        holder.lastSeen.text = "Last seen: ${members[position].dateTime}"
        if(members[position].userIcon!!.isNotEmpty()){
            Glide.with(context).load(members[position].userIcon).into(holder.userIcon)
        }
    }

    override fun getItemCount(): Int {
        return members.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memberName: TextView = itemView.findViewById(R.id.txtUserName)
        val lastSeen: TextView = itemView.findViewById(R.id.txtLastSeen)
        val userIcon: ImageView = itemView.findViewById(R.id.imgUserIcon)

    }

}