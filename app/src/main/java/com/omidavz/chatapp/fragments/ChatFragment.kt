package com.omidavz.chatapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.omidavz.chatapp.R
import com.omidavz.chatapp.adapters.UserAdapter
import com.omidavz.chatapp.databinding.FragmentChatBinding
import com.omidavz.chatapp.model.ChatList
import com.omidavz.chatapp.model.Users


class ChatFragment : Fragment() {

    lateinit var binding : FragmentChatBinding

    // Credentials
    private lateinit var userAdapter: UserAdapter
    private lateinit var mUsers: MutableList<Users>
    private lateinit var userList: ArrayList<ChatList>

    // Firebase
    private lateinit var fUser : FirebaseUser
    private lateinit var reference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chat,container,false)



        binding.apply {
            chatsRV.setHasFixedSize(true)
            chatsRV.layoutManager = LinearLayoutManager(context)


        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fUser = FirebaseAuth.getInstance().currentUser!!
        userList = arrayListOf()
        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(fUser.uid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (userList ).clear()

                // Loop for all users
                for (snapshot in dataSnapshot.children) {
                    val chatList = snapshot.getValue(ChatList::class.java)
                    userList.add(chatList!!)
                }
                chatList()
            }

            override fun onCancelled(error: DatabaseError) {}
        })





    }

    private fun chatList() {
        // Getting all recent chats
        mUsers = arrayListOf<Users>()

        reference = FirebaseDatabase.getInstance().getReference("MyUsers")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                mUsers.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(Users::class.java)
                    for (chatList in userList) {
                        if (user?.id.equals(chatList.id)) {
                            mUsers.add(user!!)
                        }
                    }
                }
                if(mUsers.size>0 && context != null){
                    userAdapter = UserAdapter(context!!, mUsers, true)
                    binding.chatsRV.adapter = userAdapter

                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })


    }


}