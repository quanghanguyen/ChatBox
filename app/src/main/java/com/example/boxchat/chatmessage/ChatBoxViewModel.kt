package com.example.boxchat.chatmessage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.boxchat.firebaseconnection.DatabaseConnection
import com.example.boxchat.model.ChatMessageModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatBoxViewModel : ViewModel() {

    val sendMessage = MutableLiveData<SendMessageResult>()
    val loadMessage = MutableLiveData<LoadMessageResult>()

    sealed class SendMessageResult {
        class SendResultOk(val successMessage : String) : SendMessageResult()
        class SendResultError(val errorMessage: String) : SendMessageResult()
    }

    sealed class LoadMessageResult {
        class LoadResultOk(val messageList : ArrayList<ChatMessageModel>) : LoadMessageResult()
        class LoadResultError(val messageError : String) : LoadMessageResult()
    }

    fun handleSendMessage(messageText : String, messageUser : String, messageTime: String) {
        val messageResult = ChatMessageModel(messageText, messageUser, messageTime)

        DatabaseConnection.databaseReference.getReference("Message").push().setValue(messageResult).addOnCompleteListener {
            if (it.isSuccessful) {
                sendMessage.postValue(SendMessageResult.SendResultOk(""))
            } else {
                sendMessage.postValue(SendMessageResult.SendResultError("Fail"))
            }
        }
    }

    fun handleLoadMessage() {
        DatabaseConnection.databaseReference.getReference("Message").addValueEventListener(object :
        ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listMessage = ArrayList<ChatMessageModel>()
                    for (requestSnapshot in snapshot.children) {
                        requestSnapshot.getValue(ChatMessageModel::class.java)?.let {
                            listMessage.add(it)
                        }
                    }
                    loadMessage.postValue(LoadMessageResult.LoadResultOk(listMessage))
                }
            }
            override fun onCancelled(error: DatabaseError) {
                loadMessage.postValue(LoadMessageResult.LoadResultError("Failed to Message"))
            }
        })
    }
}