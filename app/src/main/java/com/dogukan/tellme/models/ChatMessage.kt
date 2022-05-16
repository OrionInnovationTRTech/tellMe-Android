package com.dogukan.tellme.models

import java.sql.Timestamp
import java.util.*

class ChatMessage(val id : String, val text :String, val fromID : String, var ToID : String, var TimeStamp : Long, var isSeen : Boolean,var type : String,var isTyping : String) {
    constructor() : this("","","","", 1,false,"TEXT" ,"no")
}