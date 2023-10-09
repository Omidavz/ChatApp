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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.omidavz.chatapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    lateinit var binding : ActivitySignUpBinding
    lateinit var auth : FirebaseAuth
    lateinit var myRef : DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)

        auth = Firebase.auth


        binding.apply {
            registerBtn.setOnClickListener(){
                val username : String = usernameET.text.toString()
                val password : String = passwordET.text.toString()
                val email : String = emailET.text.toString()

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Please Fill All Fields",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    registerNow(username, email, password)
                }

            }

        }





    }

    private fun registerNow(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email , password)
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    val user : FirebaseUser = auth.currentUser!!
                    val userId : String = user.uid
                    myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userId)

                    // HashMap

                    // HashMaps
                    val hashMap = HashMap<String, String>()
                    hashMap["id"] = userId
                    hashMap["username"] = username
                    hashMap["imageUrl"] = "default"
                    hashMap["status"] = "offline"


                    // Opening the Chat Activity after success Registration
                    myRef.setValue(hashMap).addOnCompleteListener(this){task->
                        if (task.isSuccessful){
                            val i = Intent(this@SignUpActivity, ChatActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                            finish()
                        }


                    }


                }else{
                    Toast.makeText(this,"Invalid Email or Password",Toast.LENGTH_SHORT).show()
                }


            }


    }
}