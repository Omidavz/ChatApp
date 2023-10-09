package com.omidavz.chatapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.omidavz.chatapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        auth = Firebase.auth


        binding.registerLoginBtn.setOnClickListener(){
            val i = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(i)
        }


        binding.loginBtn.setOnClickListener(){
            val email: String = binding.emailLoginET.text.toString().trim()
            val password: String = binding.passwordLoginET.text.toString().trim()

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
                Toast.makeText(this@MainActivity, "Please Fill the Fields ", Toast.LENGTH_SHORT)
                    .show()

            }else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this){task ->
                        if (task.isSuccessful){
                            val i = Intent(this@MainActivity, ChatActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                            finish()

                        }else{
                            Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show()

                        }

                    }
            }
        }





    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        //  Checking for users exist
        if (currentUser != null) {
            val i = Intent(this@MainActivity, ChatActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}