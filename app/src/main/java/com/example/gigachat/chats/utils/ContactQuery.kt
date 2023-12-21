package com.example.gigachat.chats.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.gowtham.letschat.models.UserProfile
import timber.log.Timber

interface QueryListener{
    fun onCompleted(queriedList: ArrayList<UserProfile>)
    fun onStart(position: Int,contactBatch: ArrayList<String>)
}

class ContactsQuery{

    companion object{
        var queriedList=ArrayList<UserProfile>()
        var currentQueryCount=0
        var totalQueryCount=0
    }

    private val usersCollection = FirebaseFirestore.getInstance().collection("Users")

    fun makeQuery(position: Int,listOfMobile: ArrayList<String>,listener: QueryListener) {
        try {
            listener.onStart(position,listOfMobile)
            usersCollection.whereIn("Phone", listOfMobile).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val contact = document.toObject(UserProfile::class.java)
                        queriedList.add(contact)
                    }
                    currentQueryCount += 1
                    if(currentQueryCount == totalQueryCount)
                        listener.onCompleted(queriedList)
                }
                .addOnFailureListener { exception ->
                    Timber.wtf("Error: ${exception.message}")
                    currentQueryCount += 1
                    if(currentQueryCount == totalQueryCount)
                        listener.onCompleted(queriedList)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}