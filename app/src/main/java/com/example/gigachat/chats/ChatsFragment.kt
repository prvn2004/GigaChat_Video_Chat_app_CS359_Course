package com.example.gigachat.chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.gigachat.R
import com.example.gigachat.chats.contacts.ContactsFragment
import com.example.gigachat.databinding.FragmentChatsBinding

class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.floatingActionButton.setOnClickListener {
            showFragment(ContactsFragment())
        }

        return view
    }

    private fun showFragment(fragment: Fragment) {

        val bundle = Bundle()
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.mainActivityContainer, fragment).addToBackStack(null)
            .commit()
    }

}