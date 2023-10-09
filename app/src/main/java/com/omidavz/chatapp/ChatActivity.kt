package com.omidavz.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.omidavz.chatapp.adapters.ViewPagerAdapter
import com.omidavz.chatapp.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    lateinit var binding : ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat)


        val adapter = ViewPagerAdapter(this)


        binding.apply {
            viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when(position){
                    0 -> tab.text = "Chat"
                    1 -> tab.text = "User"
                    2 -> tab.text = "Profile"
                }
            }.attach()
        }
    }

    // Adding Logout Functionality
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut()
            startActivity(
                Intent(
                    this@ChatActivity,
                    MainActivity::class.java
                )
            )
            return true
        }
        return false
    }
}