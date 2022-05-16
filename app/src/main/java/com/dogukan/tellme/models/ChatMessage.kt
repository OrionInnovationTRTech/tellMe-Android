package com.dogukan.tellme.models

import java.util.*

class ChatMessage(val id : String, val text :String, val fromID : String, var ToID : String, var TimeStamp:String, var isSeen : Boolean,var type : String,var isTyping : String) {
    constructor() : this("","","","","",false,"TEXT" ,"no")
}