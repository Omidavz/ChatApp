package com.omidavz.chatapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.omidavz.chatapp.MessageActivity
import com.omidavz.chatapp.R
import com.omidavz.chatapp.databinding.UserItemBinding
import com.omidavz.chatapp.model.Users

class UserAdapter(
    var context: Context,var mUsers: List<Users>,var  isChat: Boolean) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    lateinit var binding: UserItemBinding;



    class UserViewHolder(var binding:UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(users: Users){
            binding.users = users
        }


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        binding = UserItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val users: Users = mUsers[position]


        if (users.imageUrl.equals("default")) {
            binding.profilePictureItemIV.setImageResource(R.mipmap.ic_launcher)
        } else {
            // Adding Glide Library
            Glide.with(context)
                .load(users.imageUrl)
                .into(holder.binding.profilePictureItemIV)
        }

        // Status Check

        // Status Check
        if (isChat) {
            if (users.status.equals("online")) {
                binding.statusImageOnline.setVisibility(View.VISIBLE)
                binding.statusImageOffline.setVisibility(View.GONE)
            } else {
                binding.statusImageOffline.setVisibility(View.VISIBLE)
                binding.statusImageOnline.setVisibility(View.GONE)
            }
        } else {
            binding.statusImageOffline.setVisibility(View.GONE)
            binding.statusImageOnline.setVisibility(View.GONE)
        }
        binding.profilePictureItemIV.setOnClickListener() {
            val i = Intent(context, MessageActivity::class.java)
            i.putExtra("userid", users.id)
            context.startActivity(i)
        }

        holder.bind(users)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }
    }