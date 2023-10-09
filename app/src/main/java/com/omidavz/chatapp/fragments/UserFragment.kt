package com.omidavz.chatapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.omidavz.chatapp.R
import com.omidavz.chatapp.adapters.UserAdapter
import com.omidavz.chatapp.databinding.FragmentUserBinding
import com.omidavz.chatapp.model.Users

class UserFragment() : Fragment() {

    lateinit var binding: FragmentUserBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var mUsers: MutableList<Users>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        mUsers = arrayListOf()

        getUsers()

        return binding.root


    }


    private fun getUsers() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("MyUsers")



        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(Users::class.java)!!
                    if (!user.id.equals(firebaseUser!!.uid)) {
                        mUsers.add(user)
                    }


                    if (mUsers.size>0 && context != null){
                        userAdapter = UserAdapter(context!!, mUsers, false)
                        binding.recyclerView.adapter = userAdapter
                    }



                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })


    }
}