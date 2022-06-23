package com.example.boxchat.chatmessage

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
import com.example.boxchat.firebaseconnection.AuthConnection
import com.example.boxchat.firebaseconnection.AuthConnection.authUser
import com.example.boxchat.home.HomeActivity
import com.example.boxchat.model.ChatMessageModel
import com.firebase.ui.auth.AuthUI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ChatBoxActivity : AppCompatActivity() {

    private lateinit var mainBinding : ActivityMainBinding
    private val chatBoxViewModel : ChatBoxViewModel by viewModels()
    private lateinit var messageAdapter : ChatBoxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        initEvents()
        initObserve()
        initListObserve()
        chatBoxViewModel.handleLoadMessage()
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
        signOut()
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

                val current  = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                val messageTime = current.format(formatter)

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

    private fun signOut() {
        AuthConnection.auth.signOut()
        startActivity(Intent(this, HomeActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0)
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "We couldn't sign you in. Please try again later", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
