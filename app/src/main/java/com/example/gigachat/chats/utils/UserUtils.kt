package com.example.gigachat.chats.utils

import com.gowtham.letschat.db.data.ChatUser
import com.gowtham.letschat.models.Contact
import com.gowtham.letschat.models.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

object UserUtils {

    fun queryContacts() {
        val listOfContacts= mutableListOf<String>()
        listOfContacts.add("9500552211")
        listOfContacts.add("9500552212")
        listOfContacts.add("9500552213")
        listOfContacts.add("9500552214")
        listOfContacts.add("9500552215")
        listOfContacts.add("9500552216")
        listOfContacts.add("9500552217")
        listOfContacts.add("9500552218")
        listOfContacts.add("9500552219")
        listOfContacts.add("9500552210")
        listOfContacts.add("9500552221")

        val subLists: List<List<String>> = listOfContacts.chunked(size = 10)
        ContactsQuery.totalQueryCount=subLists.size
        val contactQuery= ContactsQuery()
        for(index in subLists.indices)
            contactQuery.makeQuery(index,ArrayList(subLists[index]), onQueryListener)
    }

    private val onQueryListener=object : QueryListener {
        override fun onCompleted(queriedList: ArrayList<UserProfile>) {
            try {
                Timber.v("onQueryCompleted ${queriedList.size}")
                val localDeviceContacts= fetchLocalDeviceContacts()
                val finalList = ArrayList<ChatUser>()
                CoroutineScope(Dispatchers.IO).launch {
                    for(doc in queriedList){
                        val savedNumber=localDeviceContacts.first { it.mobile == doc.Phone!!.number}
                        val chatUser=getChatUser(doc,savedNumber.name)
                        finalList.add(chatUser)
                    }
                    setDefaultValues()

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onStart(position: Int, contactBatch: ArrayList<String>) {
            Timber.v("onQueryStart pos: $position inputs: ${contactBatch.size}")
        }
    }

    fun getChatUser(
        doc: UserProfile,
        savedName: String): ChatUser {
        return ChatUser(id = doc.uId.toString(),localName = savedName,user = doc,locallySaved = true)
    }

    fun fetchLocalDeviceContacts(): List<Contact> {
        val dummyContacts=ArrayList<Contact>()
        dummyContacts.add(Contact("Arthur","9500112233"))
        dummyContacts.add(Contact("Arthur1","9500112232"))
        dummyContacts.add(Contact("Arthur2","9500112235"))
        dummyContacts.add(Contact("Arthur3","9500112236"))
        dummyContacts.add(Contact("Arthur4","9500112237"))
        dummyContacts.add(Contact("Arthur5","9500112238"))
        return dummyContacts
    }


    private fun setDefaultValues() {
        ContactsQuery.totalQueryCount =0
        ContactsQuery.currentQueryCount =0
        ContactsQuery.queriedList.clear()
    }



}