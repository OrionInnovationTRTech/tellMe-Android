package com.dogukan.tellme.service
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import com.dogukan.tellme.R
import com.dogukan.tellme.constants.AppConstants
import com.dogukan.tellme.util.AppUtil
import com.dogukan.tellme.view.MainActivity
import com.dogukan.tellme.viewmodel.ChatViewModel
import com.dogukan.tellme.viewmodel.LatestMessagesViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import kotlin.collections.HashMap


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MessagingService : FirebaseMessagingService() {

    // generate the notification
    // attach the notification created with the custom layout
    // show the notification
    private val appUtil = AppUtil()
    private var  viewModel : ChatViewModel = ChatViewModel()

    override fun onMessageReceived(remoteMssage: RemoteMessage) {

        if(remoteMssage.data.isNotEmpty()){

          val map : Map<String,String> = remoteMssage.data
            val title = map["title"]
            val message = map["message"]
            val hisID = map["hisId"]
            val hisImage = map["hisImage"]
            if (Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
                createOreoNotification(title!!,message!!,hisID!!,hisImage!!)
            }else{
                createNormalNotification(title!!,message!!,hisID!!,hisImage!!)
            }
        }
    }


    override fun onNewToken(token: String) {

        super.onNewToken(token)
        uptadeToken(token)
    }
    private fun uptadeToken(token : String){
        val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUID()!!)
        val map : MutableMap<String,Any> = HashMap()
        map["token"] = token
        databaseRef.updateChildren(map)


    }

    private fun createNormalNotification(
        title: String,
        message: String,
        hisID : String,
        hisImage : String,
    ){
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder : NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, AppConstants.CHANNEL_ID)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentText(message)
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources,R.color.orange,null))
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setSound(uri)

        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("hisId",hisID)
        intent.putExtra("hisImage",hisImage)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)
        val manager =getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65 ),builder.build())



    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(
        title: String,
        message: String,
        hisID : String,
        hisImage : String,
    ){
        val channel = NotificationChannel(AppConstants.CHANNEL_ID,"Message",NotificationManager.IMPORTANCE_HIGH)

        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager =getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("hisId","hisID")
        intent.putExtra("hisImage",hisImage)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(this,AppConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(100,notification)



    }


}