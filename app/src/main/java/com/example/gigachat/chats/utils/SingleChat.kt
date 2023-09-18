//package com.example.gigachat.chats.utils
//
//import android.util.Log
//import androidx.fragment.app.Fragment
//import com.google.firebase.firestore.CollectionReference
//import com.google.firebase.firestore.FieldValue
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.SetOptions
//import com.gowtham.letschat.db.data.ChatUser
//import com.gowtham.letschat.db.data.Message
//import com.gowtham.letschat.db.data.TextMessage
//import timber.log.Timber
//import java.sql.Timestamp
//import java.text.DateFormat
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//import java.util.TimeZone
//
//class FSingleChat : Fragment(), MessageSenderCallback {
//    fun sendMessage() {
//        val message = createMessage("my first message")
////        msgDao.insertMessage(message)
//        val db = FirebaseFirestore.getInstance()
//        val messageCollection = db.collection("Messages")
//        val messageSender = MessageSender(
//            messageCollection,
//            currentChatUser,
//           this)
//        messageSender.send(
//            fromUser = myUserId,
//            toUser = currentChatUser.id,
//            message = message
//        )
//    }
//
//    fun createMessage(msg: String, from : String, to : String): Message {
//        return Message(
//            generateId(),
//            createdAt = dateToUTC(),
//            from = from,
//            to = to,
//            chatUsers = arrayListOf(from, to),
//            textMessage = TextMessage(msg)
//        )
//    }
//
//
//    fun generateId(length: Int = 20): String {
//        val alphaNumeric = ('a'..'z') + ('A'..'Z') + ('0'..'9')
//        return alphaNumeric.shuffled().take(length).joinToString("")  //ex: bwUIoWNCSQvPZh8xaFuz
//    }
//
//    fun dateToUTC(date: Date = Date()): String {
//        // will be converted back to local time zone before showing in recycler view
//        val formatterUTC: DateFormat =
//            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
//        formatterUTC.timeZone = TimeZone.getTimeZone("UTC")
//        return formatterUTC.format(date)
//    }
//
//    override fun onSuccess(message: Message) {
////        LogMessage.v("Message sender Sucesss ${message.id}")
////        messageDao.insertMessage(message)
//        //push notification code goes here
//    }
//
//    override fun onFailed(message: Message) {
////        LogMessage.v("Message sender Failed ${it.message}")
////        messageDao.insertMessage(message)
//    }
//
//}
//
//interface MessageSenderCallback {
//    fun onSuccess(message: Message)
//    fun onFailed(message: Message)
//}
//
//class MessageSender(
//    private val msgCollection: CollectionReference,
//    private val chatUser: ChatUser,
//    private val listener: MessageSenderCallback
//) {
//
//    fun send(fromUser: String, toUser: String, message: Message) {
//        val docId = chatUser.documentId
//        if (!docId.isNullOrEmpty()) {
//            Timber.v("Case 1 ${chatUser.documentId}")
//            send(docId, message)
//        } else {
//            //so we don't create multiple nodes for same chat
//            msgCollection.document("${fromUser}_${toUser}").get()
//                .addOnSuccessListener { documentSnapshot ->
//                    if (documentSnapshot.exists()) {
//                        //this node exists send your message
//                        Timber.v("Case 2")
//                        send("${fromUser}_${toUser}", message)
//                    } else {
//                        //senderId_receiverId node doesn't exist check receiverId_senderId
//                        msgCollection.document("${toUser}_${fromUser}").get()
//                            .addOnSuccessListener { documentSnapshot2 ->
//                                if (documentSnapshot2.exists()) {
//                                    Timber.v("Case 3")
//                                    send("${toUser}_${fromUser}", message)
//                                } else {
//                                    //no previous chat history(senderId_receiverId & receiverId_senderId both don't exist)
//                                    //so we create document senderId_receiverId then messages array then add messageMap to messages
//                                    //this node exists send your message
//                                    //add ids of chat members
//                                    Timber.v("Case 4")
//                                    msgCollection.document("${fromUser}_${toUser}")
//                                        .set(
//                                            mapOf(
//                                                "chat_members" to FieldValue.arrayUnion(
//                                                    fromUser,
//                                                    toUser
//                                                )
//                                            ),
//                                            SetOptions.merge()
//                                        ).addOnSuccessListener {
////                                            Log.v("chat member update successfully")
//                                            send("${fromUser}_${toUser}", message)
//                                        }.addOnFailureListener {
////                                            LogMessage.v("chat member update failed ${it.message}")
//                                        }
//                                }
//                            }
//                    }
//                }
//        }
//    }
//
//    private fun send(doc: String, message: Message) {
//        try {
//            chatUser.documentId = doc
////            dbRepo.insertUser(chatUser)
//            message.status = 1  //changing message status to sent
//            message.chatUsers = arrayListOf(message.from, message.to)
//            val messageCopy = message.copy(message).apply {
//                chatUserId =
//                    null //chatUserId field is being used only for relation query,changing to null will ignore this field
//                createdAt =
//                    0 // will get this message in snapshot listener with server time replaced
//            }
//
//            msgCollection.document(doc).collection("messages").document(message.id).set(
//                messageCopy,
//                SetOptions.merge()
//            ).addOnSuccessListener {
//                listener.onSuccess(message)
//            }.addOnFailureListener {
//                message.status = 4
//                listener.onFailed(message)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//}