package com.example.gigachat.chats.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.example.gigachat.R
import com.gowtham.letschat.db.data.ChatUser

class ContactsUserAdapter(private var chatUsers: List<ChatUser>) : RecyclerView.Adapter<ContactsUserAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val bioTextView: TextView = itemView.findViewById(R.id.bioTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_contact, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatUser = chatUsers[position]
        holder.nameTextView.text = chatUser.user.UserName
        holder.bioTextView.text = chatUser.user.UserBio

        holder.profileImageView.load(chatUser.user.ProfileImage) {
            crossfade(true)
            placeholder(R.drawable.camera_logo)
            transformations(CircleCropTransformation())
        }
    }

    override fun getItemCount(): Int {
        return chatUsers.size
    }

    fun updateData(newChatUsers: List<ChatUser>) {
        chatUsers = newChatUsers
        notifyDataSetChanged()
    }
}
