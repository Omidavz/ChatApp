package com.omidavz.chatapp.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.omidavz.chatapp.ChatActivity
import com.omidavz.chatapp.fragments.ChatFragment
import com.omidavz.chatapp.fragments.ProfileFragment
import com.omidavz.chatapp.fragments.UserFragment

class ViewPagerAdapter(chatActivity: ChatActivity):FragmentStateAdapter(chatActivity) {
    private val fragmentsSize = 3

    override fun getItemCount(): Int {
        return this.fragmentsSize
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> {
                return ChatFragment()
            }
            1-> {
                return UserFragment()
            }
            2->{
                return ProfileFragment()
            }



        }
        return ChatFragment()





    }
}