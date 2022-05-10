package com.dogukan.tellme.view
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.dogukan.tellme.R
import com.dogukan.tellme.adapter.ChatLogRVAdapter
import com.dogukan.tellme.constants.AppConstants
import com.dogukan.tellme.databinding.FragmentChatLogBinding
import com.dogukan.tellme.models.ChatMessage
import com.dogukan.tellme.util.Addition
import com.dogukan.tellme.util.AppUtil
import com.dogukan.tellme.viewmodel.ChatViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.pixFragment
import io.ak1.pix.models.*
import okhttp3.Response
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


class ChatLogFragment : Fragment() {
    private lateinit var binding : FragmentChatLogBinding
    private lateinit var adapter : ChatLogRVAdapter
    private var chatMessageList = ArrayList<ChatMessage>()
    private lateinit var ToID : String
    private lateinit var myID : String
    private lateinit var myName : String
    private lateinit var hisImage : String
    private lateinit var ActiveState : String
    private  var AppUtil = AppUtil()
    private  var addition = Addition()


    private val viewModel : ChatViewModel by viewModels()
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //(activity as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentChatLogBinding.inflate(inflater)
            // ... rest of body of onCreateView() ...

        val activity = activity as? MainActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        (activity as AppCompatActivity).supportActionBar?.hide()

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
        chatMessageList.clear()
        adapter = ChatLogRVAdapter(chatMessageList)
        arguments?.let {
            ToID = ChatLogFragmentArgs.fromBundle(it).toID
            hisImage = ChatLogFragmentArgs.fromBundle(it).imageURL
            myName = ChatLogFragmentArgs.fromBundle(it).username
            ToID.let { it1 -> viewModel.getMessageFirebase(it1) }
            binding.sendmassageBtn.setOnClickListener {
                if (binding.sendmassegeTV.text.toString() == ""){
                    return@setOnClickListener
                }
                viewModel.performSendMessage(binding.sendmassegeTV.text.toString(), ToID,binding)

                getToken(binding.sendmassegeTV.text.toString())
            }
            viewModel.getActiveStateFirebase(ToID)
            viewModel.getActiveState()
        }
        myID = AppUtil.getUID()!!
        viewModel.getAllMessage()
        viewModel.checkIsSeen(ToID)
        binding.recyclerView2.adapter = adapter

        observeLiveData()
    }

    private fun observeLiveData(){
        viewModel.message.observe(viewLifecycleOwner, Observer {
            binding.sendmassageBtn.isEnabled = binding.sendmassegeTV.text != null
            binding.recyclerView2.visibility = View.VISIBLE
            adapter.ChatMessageUpdate(it)

            binding.recyclerView2.scrollToPosition(chatMessageList.count()-1)

        })
        viewModel.activeState.observe(viewLifecycleOwner, Observer {
            viewModel.getActiveStateFirebase(ToID)
            viewModel.getActiveState()
            if (it){
                binding.include.activeState.text = "online"

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


            if (it){
                Log.d("IsSeen",it.toString())

            }
            else{
                Log.d("IsSeen",it.toString())
            }

        })
    }

    private fun getToken(message : String){
        val databaseref = FirebaseDatabase.getInstance().getReference("users").child(ToID)
        databaseref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val token = snapshot.child("token").value.toString()
                val to =JSONObject()
                val data = JSONObject()

                data.put("hisId", ToID)
                data.put("hisImage", hisImage)
                data.put("title", myName)
                data.put("message", message)
                Log.d("Responce", "$myID $myName $message $ToID $hisImage")
                to.put("to",token)
                to.put("data",data)
                //Log.d("Responce", "$token $data")
                sendNotification(to)

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
    private fun sendNotification(to : JSONObject){
        val request : JsonObjectRequest  = object : JsonObjectRequest(Method.POST
            ,AppConstants.NOTIFICATION_URL
            ,to
            ,com.android.volley.Response.Listener {response : JSONObject ->
                Log.d("Responce" , "SendNotification : $response")

        },com.android.volley.Response.ErrorListener {
                Log.d("Responce" , "SendNotification : $it")
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val map : MutableMap<String,String> = HashMap()
                map["Authorization"] = "key=" + AppConstants.SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }

        }
        val requestQueue = Volley.newRequestQueue(context)
        request.retryPolicy = DefaultRetryPolicy(
            3000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)


    }
    private fun pickImage(){
        val options = Options().apply{
            ratio = Ratio.RATIO_AUTO                                    //Image/video capture ratio
            count = 1                                                   //Number of images to restrict selection count
            spanCount = 4                                               //Number for columns in grid
            path = "Pix/Camera"                                         //Custom Path For media Storage
            isFrontFacing = false                                       //Front Facing camera on start
            videoOptions.videoDurationLimitInSeconds = 10                            //Duration for video recording
            mode = Mode.All                                             //Option to select only pictures or videos or both
            flash = Flash.Auto                                          //Option to select flash type
            preSelectedUrls = ArrayList<Uri>()                          //Pre selected Image Urls
        }
        pixFragment(options){
            when (it.status) {
                PixEventCallback.Status.SUCCESS ->{

                } //use results as it.data
             PixEventCallback.Status.BACK_PRESSED ->{

             } // back pressed called
            }
        }

    }


}