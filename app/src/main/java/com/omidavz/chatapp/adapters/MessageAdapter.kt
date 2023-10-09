package com.omidavz.chatapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.omidavz.chatapp.R
import com.omidavz.chatapp.databinding.ChatItemLeftBinding
import com.omidavz.chatapp.databinding.ChatItemRightBinding
import com.omidavz.chatapp.model.Chat

class MessageAdapter(var context: Context,var mChats : List<Chat>,var imageUrl : String)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var bindingLeft : ChatItemLeftBinding
    private lateinit var bindingRight : ChatItemRightBinding

    private val MSG_TYPE_LEFT = 0
    private val MSG_TYPE_RIGHT = 1

    private lateinit var fUser: FirebaseUser


    class MessageViewHolder1( bindingLeft : ChatItemLeftBinding  ) :RecyclerView.ViewHolder(bindingLeft.root){


    }


    class MessageViewHolder2( bindingRight : ChatItemRightBinding ):RecyclerView.ViewHolder(bindingRight.root){


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType ==  MSG_TYPE_LEFT) {
            bindingLeft = ChatItemLeftBinding.inflate(LayoutInflater.from(context),parent,false)
            MessageViewHolder1(bindingLeft)
        } else {
            bindingRight = ChatItemRightBinding.inflate(LayoutInflater.from(context),parent,false)
            MessageViewHolder2(bindingRight)
        }

    }

    override fun getItemCount(): Int {
        return mChats.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val chat = mChats[position]

        when(holder.itemViewType){

            MSG_TYPE_LEFT ->{

                bindingLeft.showMessage.text = chat.message
                if (imageUrl == "default") {
                    bindingLeft.profileImage.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(context)
                        .load(imageUrl)
                        .into(bindingLeft.profileImage)
                }

                if (position == mChats.size - 1) {
                    if (chat.isseen) {
                        bindingLeft.txtSeenStatus.text = "Seen"
                    } else {
                        bindingLeft.txtSeenStatus.text = "sent"
                    }
                } else {
                    bindingLeft.txtSeenStatus.visibility = View.GONE
                }

            }

            MSG_TYPE_RIGHT -> {
                bindingRight.showMessage.text = chat.message

                if (imageUrl == "default") {
                    bindingRight.profileImage.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(context)
                        .load(imageUrl)
                        .into(bindingRight.profileImage)
                }
                if (position == mChats.size - 1) {
                    if (chat.isseen) {
                        bindingRight.txtSeenStatus.text = "Seen"
                    } else {
                        bindingRight.txtSeenStatus.text = "sent"
                    }
                } else {
                    bindingRight.txtSeenStatus.visibility = View.GONE
                }

            }




        }
    }
    override fun getItemViewType(position: Int): Int {
        fUser = FirebaseAuth.getInstance().currentUser!!
        return if (mChats[position].sender.equals(fUser.uid)) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }


}