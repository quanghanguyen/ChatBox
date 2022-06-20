package com.example.boxchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.boxchat.chatmessage.ChatBoxAdapter
import com.example.boxchat.chatmessage.ChatBoxViewModel
import com.example.boxchat.databinding.ActivityMainBinding
import com.example.boxchat.firebaseconnection.AuthConnection.authUser
import com.example.boxchat.model.ChatMessageModel
import com.firebase.ui.auth.AuthUI
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding : ActivityMainBinding
    private val chatBoxViewModel : ChatBoxViewModel by viewModels()
    private lateinit var messageAdapter : ChatBoxAdapter
    private var messageList = ArrayList<ChatMessageModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        initEvents()
        initObserve()
        initListObserve()
        chatBoxViewModel.handleLoadMessage()

        // --------------------------------
//        mainBinding.fab.setOnClickListener {
//            DatabaseConnection.databaseReference.getReference()
//                .push()
//                .setValue(authUser?.displayName?.let { it1 ->
//                    ChatMessageModel(mainBinding.input.text.toString(),
//                        it1, Calendar.getInstance().time.toString()
//                    )
//                })
//            mainBinding.input.setText("")
//        }
    }

    private fun initListObserve() {
        chatBoxViewModel.loadMessage.observe(this) { loadMessageResult ->
            when (loadMessageResult) {
                is ChatBoxViewModel.LoadMessageResult.LoadResultOk -> {
                    messageAdapter.addNewMessage(loadMessageResult.messageList)
                }
                is ChatBoxViewModel.LoadMessageResult.LoadResultError -> {
                    Toast.makeText(this, loadMessageResult.messageError, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initObserve() {
        chatBoxViewModel.sendMessage.observe(this) { sendMessageResult ->
            when (sendMessageResult) {
                is ChatBoxViewModel.SendMessageResult.SendResultOk -> {
                    Toast.makeText(this, sendMessageResult.successMessage, Toast.LENGTH_SHORT).show()
                }
                is ChatBoxViewModel.SendMessageResult.SendResultError -> {
                    Toast.makeText(this, sendMessageResult.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initEvents() {
        checkUser()
        initList()
        sendMessage()
    }

    private fun initList() {
        mainBinding.listOfMessages.apply {
            layoutManager = LinearLayoutManager(context)
            messageAdapter = ChatBoxAdapter(arrayListOf())
            adapter = messageAdapter
        }
    }

    private fun sendMessage() {
        mainBinding.fab.setOnClickListener {
            if (mainBinding.input.text.isNullOrEmpty()) {
                Toast.makeText(this, "Enter Message", Toast.LENGTH_SHORT).show()
            } else {
                val messageText = mainBinding.input.text.toString()
                val messageUser = authUser?.displayName
                val messageTime = Calendar.getInstance().time.toString()

                messageUser?.let { it1 ->
                    chatBoxViewModel.handleSendMessage(messageText,
                        it1, messageTime)
                }
            }
            mainBinding.input.setText("")
        }
    }

    private fun checkUser() {
        if (authUser == null) {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(), 0
            )
        } else {
            Toast.makeText(this, "Welcome ${authUser.displayName}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0)
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_SHORT).show()
//                displayChatMessages()
            } else {
                Toast.makeText(this, "We couldn't sign you in. Please try again later", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

//    private fun displayChatMessages() {
//
//        val query = DatabaseConnection.databaseReference.getReference()
//
//        val options = FirebaseRecyclerOptions.Builder<ChatMessageModel>()
//            .setQuery(query, ChatMessageModel::class.java)
//            .setLifecycleOwner(this)
//            .build()
//
//        val adapter = object : FirebaseRecyclerAdapter<ChatMessageModel, ChatMessageModelHolder>(options) {
//            override fun onCreateViewHolder(
//                parent: ViewGroup,
//                viewType: Int
//            ): ChatMessageModelHolder {
//                val messageItems = MessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                val viewHolder = ChatMessageModelHolder(messageItems)
//                return viewHolder
//            }
//
//            override fun onBindViewHolder(
//                holder: ChatMessageModelHolder,
//                position: Int,
//                model: ChatMessageModel
//            ) {
//                holder.bind(model)
//            }
//
//        }
//
////        val listOfMessage = findViewById<ListView>(R.id.list_of_messages)
////        val adapter = object : FirebaseListAdapter<ChatMessageModel>(FirebaseListOptions<>) {
////            override fun populateView(v: View, model: ChatMessageModel, position: Int) {
////                val messageText = v.findViewById<TextView>(R.id.message_text)
////                val messageUser = v.findViewById<TextView>(R.id.message_user)
////                val messageTime = v.findViewById<TextView>(R.id.message_time)
////
////                messageText.text = model.messageText
////                messageUser.text = model.messageUser
////                messageTime.text = model.messageTime
////            }
////        }
////        listOfMessage.adapter = adapter
//    }
}

//class ChatMessageModelHolder(private val messageBinding: MessageBinding, var chatMessage : ChatMessageModel? = null)
//    : RecyclerView.ViewHolder(messageBinding.root) {
//
//        fun bind(chatMessage: ChatMessageModel?) {
//            with(messageBinding) {
//                messageText.text = chatMessage?.messageText
//                messageUser.text = chatMessage?.messageUser
//                messageTime.text = chatMessage?.messageTime
//            }
//        }
//}
