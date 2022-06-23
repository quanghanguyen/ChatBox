package com.example.boxchat.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.boxchat.R
import com.example.boxchat.chatmessage.ChatBoxActivity
import com.example.boxchat.databinding.ActivityHomeBinding
import com.example.boxchat.databinding.ActivityMainBinding
import com.example.boxchat.firebaseconnection.AuthConnection.auth

class HomeActivity : AppCompatActivity() {

    private lateinit var homeBinding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)

        initEvents()
    }

    private fun initEvents() {
        goChat()
    }

    private fun goChat() {
        homeBinding.openChat.setOnClickListener {
            startActivity(Intent(this, ChatBoxActivity::class.java))
        }
    }
}