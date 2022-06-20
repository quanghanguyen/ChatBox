package com.example.boxchat.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ChatMessageModel(
    @SerializedName("messageText")
    val messageText: String,
    @SerializedName("messageUser")
    val messageUser: String,
    @SerializedName("messageTime")
    val messageTime: String
        )