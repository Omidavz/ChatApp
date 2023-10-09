package com.omidavz.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.omidavz.chatapp.adapters.MessageAdapter
import com.omidavz.chatapp.databinding.ActivityMessageBinding
import com.omidavz.chatapp.model.Chat
import com.omidavz.chatapp.model.Users

class MessageActivity : AppCompatActivity() {
    lateinit var binding : ActivityMessageBinding

    lateinit var user: FirebaseUser
    private lateinit var reference: DatabaseReference

    lateinit var messageAdapter: MessageAdapter
    lateinit var mchat: MutableList<Chat>

    private lateinit var seenListener: ValueEventListener
    lateinit var userid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_message)



        binding.apply {
            messageRV.setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(this@MessageActivity)
            linearLayoutManager.stackFromEnd = true

            messageRV.layoutManager = linearLayoutManager


        }
        val intent :Intent = intent
        userid = intent.getStringExtra("userid").toString()

        user = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: Users? = dataSnapshot.getValue(Users::class.java)
                binding.usernameMessageTV.setText(user?.username)
                if (user?.imageUrl.equals("default")) {
                    binding.profileMessageIV.setImageResource(R.mipmap.ic_launcher)
                } else {
                    if (this@MessageActivity != null) {
                        Glide.with(this@MessageActivity)
                            .load(user?.imageUrl)
                            .into(binding.profileMessageIV)
                    }
                }
                readMessages(this@MessageActivity.user.uid, userid, user?.imageUrl!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


        binding.sendMessageBtn.setOnClickListener(){
            val msg: String = binding.textMessageET.text.toString()
            if (msg != "") {
                sendMessage(user.uid, userid, msg)
            } else {
                Toast.makeText(
                    this@MessageActivity,
                    "Please Enter Text to Send",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.textMessageET.setText("")
        }

        seenMessage(userid)



    }

    private fun seenMessage(userid: String) {
        reference = FirebaseDatabase.getInstance().getReference("Chats")

        seenListener = reference.addValueEventListener(
        object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat?.receiver.equals(user.getUid()) && chat?.sender
                            .equals(userid)
                    ) {
                        val hashMap = java.util.HashMap<String, Any>()
                        hashMap["isseen"] = true
                        snapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private fun sendMessage(sender: String, receiver: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference

        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message
        hashMap["isseen"] = false


        reference.child("Chats").push().setValue(hashMap)


        // Adding User to chat fragment: Latest Chats with contacts


        // Adding User to chat fragment: Latest Chats with contacts
        val chatRef = FirebaseDatabase.getInstance()
            .getReference("ChatList")
            .child(user.uid)
            .child(userid)


        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }

    private fun readMessages(myid: String, userid: String, imageurl: String) {

        mchat = arrayListOf()

        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mchat.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat?.receiver.equals(myid) && chat?.sender.equals(userid) ||
                        chat?.receiver.equals(userid) && chat?.sender.equals(myid)
                    ) {
                        mchat.add(chat!!)
                    }
                    messageAdapter = MessageAdapter(this@MessageActivity, mchat, imageurl)
                    binding.messageRV.adapter = messageAdapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private fun checkStatus(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(user.uid)
        val hashMap = java.util.HashMap<String, Any>()
        hashMap["status"] = status
        reference.updateChildren(hashMap)
    }


    override fun onResume() {
        super.onResume()
        checkStatus("online")
    }

    override fun onPause() {
        super.onPause()
        reference.removeEventListener(seenListener)
        checkStatus("Offline")
    }


}