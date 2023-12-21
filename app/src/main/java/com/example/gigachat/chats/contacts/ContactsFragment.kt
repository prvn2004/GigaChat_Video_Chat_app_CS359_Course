package com.example.gigachat.chats.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.gigachat.R
import com.example.gigachat.chats.utils.UserUtils
import com.example.gigachat.chats.utils.UserUtils.queryContacts
import com.example.gigachat.databinding.FragmentChatsBinding
import com.example.gigachat.databinding.FragmentContactsBinding
import com.gowtham.letschat.db.data.ChatUser
import com.gowtham.letschat.models.UserProfile
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        val view = binding.root

        val user1 = ChatUser("1", "John Doe", UserProfile("1", null, null, "", "John Doe", "Bio 1", "", null, null))
        val user2 = ChatUser("2", "Jane Smith", UserProfile("2", null, null, "", "Jane Smith", "Bio 2", "", null, null))
        val user3 = ChatUser("3", "Alice Johnson", UserProfile("3", null, null, "", "Alice Johnson", "Bio 3", "", null, null))

        val adapter = ContactsUserAdapter(listOf(user1, user2, user3))

        binding.recyclerView.adapter = adapter

//        lifecycleScope.launch {
//            val chatUsersList = queryContacts()
//            adapter.updateData(chatUsersList)
//        }

        return view
    }
}