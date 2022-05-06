package com.dogukan.tellme.repository

import com.dogukan.tellme.models.ChatMessage

interface ChatRepositoryI {
    fun showListOfMessage(messageList : ArrayList<ChatMessage>)
    fun checkActiveState(activeState : String)
    fun sendMessage()
}