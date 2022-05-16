package com.dogukan.tellme.view
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dogukan.tellme.R
import com.dogukan.tellme.ReViewSendImageFragment
import com.dogukan.tellme.adapter.ChatLogRVAdapter
import com.dogukan.tellme.constants.AppConstants
import com.dogukan.tellme.databinding.FragmentChatLogBinding
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.models.NotificationData
import com.dogukan.tellme.models.PushNotification
import com.dogukan.tellme.service.RetrofitObject
import com.dogukan.tellme.util.Addition
import com.dogukan.tellme.util.AppUtil
import com.dogukan.tellme.viewmodel.ChatViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import io.ak1.pix.models.*
import kotlinx.android.synthetic.main.fragment_chat_log.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.text.FieldPosition
import java.util.*
import kotlin.collections.ArrayList


class ChatLogFragment : Fragment() , ChatLogRVAdapter.RecyclerDetails {
    private lateinit var binding : FragmentChatLogBinding
    private var  mainActivityView : MainActivity = MainActivity()
    private lateinit var adapter : ChatLogRVAdapter
    private var chatMessageList = ArrayList<ChatMessage>()
    private lateinit var ToID : String
    private var position : Int = 0
    private lateinit var myID : String
    private lateinit var myName : String
    private lateinit var hisImage : String
    private lateinit var ActiveState : String
    private lateinit var status : String
    private lateinit var email : String
    private lateinit var token : String
    private  var AppUtil = AppUtil()
    private  var addition = Addition()
    var selectedPhotoUri : Uri?=null
    val topic = "/topics/genelduyurular"


    private val viewModel : ChatViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChatLogBinding.inflate(inflater)
        val activity = activity as? MainActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.hide()
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       // (activity as AppCompatActivity).supportActionBar?.hide()
        super.onViewCreated(view, savedInstanceState)
        init()
        val activity = activity as? MainActivity
        activity?.supportActionBar?.title = myName
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                arguments?.let {
                    val usernamee = ChatLogFragmentArgs.fromBundle(it).username
                    val toIDD = ChatLogFragmentArgs.fromBundle(it).toID
                    val imageUrl = ChatLogFragmentArgs.fromBundle(it).imageURL
                    val position = ChatLogFragmentArgs.fromBundle(it).position
                    val action = ChatLogFragmentDirections.actionChatLogFragmentToLatestMessagesFragment2(position,toIDD,usernamee,imageUrl)
                    this.view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init(){
        binding.recyclerView2.layoutManager = LinearLayoutManager(context)


        binding.recyclerView2.addOnLayoutChangeListener(View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom && binding.recyclerView2.adapter?.itemCount !=0 ) {
                binding.recyclerView2.postDelayed(Runnable {
                    binding.recyclerView2.smoothScrollToPosition(binding.recyclerView2.adapter?.itemCount!! - 1) }, 10)
                }
        })
        binding.sendmassegeTV.doOnTextChanged { text, _, _, count ->
            if (text.toString().trim().isEmpty() && count==0){
                viewModel.setIsTyping("no")
            }else{
                viewModel.setIsTyping("yes")

            }
        }
        chatMessageList.clear()

        adapter = ChatLogRVAdapter(chatMessageList,this)
        arguments?.let {
            ToID = ChatLogFragmentArgs.fromBundle(it).toID
            hisImage = ChatLogFragmentArgs.fromBundle(it).imageURL
            myName = ChatLogFragmentArgs.fromBundle(it).username
            position = ChatLogFragmentArgs.fromBundle(it).position
            status =ChatLogFragmentArgs.fromBundle(it).status
            ActiveState = ChatLogFragmentArgs.fromBundle(it).activeState
            email = ChatLogFragmentArgs.fromBundle(it).email
            token = ChatLogFragmentArgs.fromBundle(it).token
            ToID.let { it1 -> viewModel.getMessageFirebase(it1) }
            binding.sendmassageBtn.setOnClickListener {
                if (binding.sendmassegeTV.text.toString() == ""){
                    return@setOnClickListener
                }
                viewModel.performSendMessage(binding.sendmassegeTV.text.toString(), ToID,binding)
                val data = NotificationData(myName,binding.sendmassegeTV.text.toString())
                val notification = PushNotification(data,token)
                sendNotificationMessage(notification)

            }
            viewModel.getActiveStateFirebase(ToID)
            viewModel.getActiveState()
            viewModel.getIsTyping(ToID)
            viewModel.getIstypingData()
        }
        myID = AppUtil.getUID()!!
        viewModel.getAllMessage()
        viewModel.checkIsSeenMessage(ToID)
        binding.recyclerView2.adapter = adapter
        include.setOnClickListener {
            val action = ChatLogFragmentDirections.actionChatLogFragmentToFriendDetailFragment(position,ToID,myName,hisImage,status,ActiveState,email)

            Navigation.findNavController(it).navigate(action)
        }

        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {

                selectedPhotoUri = it
                mainActivityView = (activity as MainActivity)

                val action = ChatLogFragmentDirections.actionChatLogFragmentToReViewSendImageFragment(ToID,selectedPhotoUri.toString())

                view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }


            }
        )

        binding.cameraImageView.setOnClickListener {
            getImage.launch("image/*")
        }

        observeLiveData()
    }

    override fun onStop() {
        super.onStop()
        viewModel.setIsTyping("no")
        mainActivityView.activeState("offline")
        /*if (viewModel.message.hasActiveObservers()){
            viewModel.message.removeObservers(viewLifecycleOwner)
        }
        if (viewModel.imageUpload.hasObservers()){
            viewModel.imageUpload.removeObservers(viewLifecycleOwner)
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setIsTyping("no")
        mainActivityView.activeState("offline")
    }

    private fun observeLiveData(){
        viewModel.message.observe(viewLifecycleOwner, Observer {
            binding.sendmassageBtn.isEnabled = binding.sendmassegeTV.text != null
            binding.recyclerView2.visibility = View.VISIBLE

            adapter.ChatMessageUpdate(it)

            binding.recyclerView2.scrollToPosition(chatMessageList.count()-1)

        })
        viewModel.deleteMessage.observe(viewLifecycleOwner, Observer {
            if (it){
                Log.d("DeleteMessageObserver",it.toString())
                //viewModel.getAllMessage()

            }
            else{
                Log.d("DeleteMessageObserver",it.toString())
                //viewModel.getAllMessage()

            }
        })
        viewModel.imageUpload.observe(viewLifecycleOwner, Observer {


        })
        viewModel.activeState.observe(viewLifecycleOwner, Observer {
            viewModel.getActiveStateFirebase(ToID)
            viewModel.getActiveState()

            if (it){
                viewModel.getIstypingData().observe(viewLifecycleOwner, Observer {
                    if (it){
                        binding.include.activeState.text = "typing"
                    }else{
                        binding.include.activeState.text = "online"
                    }
                })

                binding.include.imageActiveState.setImageResource(R.drawable.greencircle)
                addition.picassoUseIt(hisImage,binding.include.profileImage)
                binding.include.username.text = myName

            }
            else{
                binding.include.activeState.text = "offline"
                binding.include.imageActiveState.setImageResource(R.drawable.redcircle)
                addition.picassoUseIt(hisImage,binding.include.profileImage)
                binding.include.username.text = myName
            }
        })
        viewModel.isSeen.observe(viewLifecycleOwner, Observer {

        })
        viewModel.isTyping.observe(viewLifecycleOwner, Observer {
            viewModel.getIsTyping(ToID)
            viewModel.getIstypingData()
        })
    }
    private fun sendNotificationMessage(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val request = RetrofitObject.api.postNotifitication(notification)
            if (request.isSuccessful){
                //Log.d("request",Gson().toJson(request))
            }else{
                //Log.d("request", request.errorBody().toString())
            }
        }catch (e : Exception){

        }
    }

    override fun onClickDeleteImageViewReciever(holder: ChatLogRVAdapter.RecieverViewHolder) {

        viewModel.getAllMessage()
        viewModel.deleteMessage(ToID,"message deleted",chatMessageList,holder.position)
        holder.recievermessage.text = "message deleted"
        holder.recieverTrashMessage.visibility = View.GONE
    }

    override fun onClickDeleteImageViewSender(holder: ChatLogRVAdapter.SenderViewHolder) {
        viewModel.getAllMessage()
        viewModel.deleteMessage(ToID,"message deleted",chatMessageList,holder.position)
        holder.sendermessage.text = "message deleted"
        holder.senderTrashMessage.visibility = View.GONE

    }



    override fun showIsSeenSender(holder: ChatLogRVAdapter.SenderViewHolder,chatMessage : ArrayList<ChatMessage>,p: Int) {
        //viewModel.getIsSeenStatus()
        //viewModel.checkIsSeenMessage(chatMessage[position].ToID)
        /*viewModel.isSeen.observe(viewLifecycleOwner, Observer {
            if (it){
                if(chatMessage.isNotEmpty() && chatMessage.size!=0){
            viewModel.checkIsSeenMessage(chatMessage[position].ToID)
            viewModel.getIsSeenStatus()

            adapter.ChatMessageUpdate(chatMessage)
        }
            }
        })*/
    }

}