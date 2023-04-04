package com.example.smartchatgpt

class Message(val message: String, val sendBy: String) {

    companion object {
        const val SEND_BY_ME = "Me"
        const val SEND_BY_BOT = "SmartChatGPT"
    }
}