package com.dogukan.tellme.service

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.Users

@Database(entities = [ChatMessage::class], version = 1)
abstract class ChatMessageDatabase : RoomDatabase(){
    abstract fun ChatMessageDAO() : ChatMessageDAO
    //Singleton
    companion object{
        //make it visible to different threads
        @Volatile private var instance : ChatMessageDatabase? = null

        private val lock = Any()

        //if there is instance, use it, if not, re-create it
        operator fun invoke(context: Context) = instance ?: synchronized(lock){
            instance ?: createDatabase(context).also {
                instance = it
            }
        }


        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ChatMessageDatabase::class.java,
            "chatMessageDatabase")
            .build()
    }
}