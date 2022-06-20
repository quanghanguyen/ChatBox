package com.example.boxchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.boxchat.databinding.ActivityMainBinding
import com.example.boxchat.databinding.MessageBinding
import com.example.boxchat.firebaseconnection.AuthConnection.authUser
import com.example.boxchat.firebaseconnection.DatabaseConnection
import com.example.boxchat.model.ChatMessageModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)


        if (authUser == null) {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(), 0
            )
        } else {
            Toast.makeText(this, "Welcome ${authUser.displayName}", Toast.LENGTH_SHORT).show()
            displayChatMessages()
        }

        // --------------------------------
        mainBinding.fab.setOnClickListener {
            DatabaseConnection.databaseReference.getReference()
                .push()
                .setValue(authUser?.displayName?.let { it1 ->
                    ChatMessageModel(mainBinding.input.text.toString(),
                        it1, Calendar.getInstance().time.toString()
                    )
                })
            mainBinding.input.setText("")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0)
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_SHORT).show()
                displayChatMessages()
            } else {
                Toast.makeText(this, "We couldn't sign you in. Please try again later", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun displayChatMessages() {

        val query = DatabaseConnection.databaseReference.getReference()

        val options = FirebaseRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel::class.java)
            .setLifecycleOwner(this)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<ChatMessageModel, ChatMessageModelHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ChatMessageModelHolder {
                val messageItems = MessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val viewHolder = ChatMessageModelHolder(messageItems)
                return viewHolder
            }

            override fun onBindViewHolder(
                holder: ChatMessageModelHolder,
                position: Int,
                model: ChatMessageModel
            ) {
                holder.bind(model)
            }

        }

//        val listOfMessage = findViewById<ListView>(R.id.list_of_messages)
//        val adapter = object : FirebaseListAdapter<ChatMessageModel>(FirebaseListOptions<>) {
//            override fun populateView(v: View, model: ChatMessageModel, position: Int) {
//                val messageText = v.findViewById<TextView>(R.id.message_text)
//                val messageUser = v.findViewById<TextView>(R.id.message_user)
//                val messageTime = v.findViewById<TextView>(R.id.message_time)
//
//                messageText.text = model.messageText
//                messageUser.text = model.messageUser
//                messageTime.text = model.messageTime
//            }
//        }
//        listOfMessage.adapter = adapter
    }
}

class ChatMessageModelHolder(private val messageBinding: MessageBinding, var chatMessage : ChatMessageModel? = null)
    : RecyclerView.ViewHolder(messageBinding.root) {

        fun bind(chatMessage: ChatMessageModel?) {
            with(messageBinding) {
                messageText.text = chatMessage?.messageText
                messageUser.text = chatMessage?.messageUser
                messageTime.text = chatMessage?.messageTime
            }
        }

}
