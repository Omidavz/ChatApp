package com.omidavz.chatapp.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.omidavz.chatapp.R
import com.omidavz.chatapp.databinding.FragmentProfileBinding
import com.omidavz.chatapp.model.Users

class ProfileFragment : Fragment() {

    lateinit var binding : FragmentProfileBinding
    private lateinit var reference: DatabaseReference
    private lateinit var fuser: FirebaseUser

    // Profile Image
    private lateinit var storageReference: StorageReference
    private val IMAGE_REQUEST = 1
    private lateinit var imageUri: Uri

    private var uploadTask: StorageTask<*>? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false)

        binding.apply {

        }

        // Profile Image reference in storage


        // Profile Image reference in storage
        storageReference = FirebaseStorage.getInstance().getReference("uploads")

        fuser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("MyUsers")
            .child(fuser.uid)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: Users? = dataSnapshot.getValue(Users::class.java)
                binding.usernamer.setText(user?.username)
                if (user?.imageUrl.equals("default")) {
                    binding.profileImage2.setImageResource(R.mipmap.ic_launcher)
                } else {
                    if (activity != null) {
                        Glide.with(context!!).load(user?.imageUrl).into(binding.profileImage2)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        binding.profileImage2.setOnClickListener(View.OnClickListener { selectImage() })

        return binding.root
    }

    private fun selectImage() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(i,IMAGE_REQUEST)

    }

    private fun getFileExtention(uri: Uri): String? {
        val contentResolver = context?.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver?.getType(uri))
    }

    private fun uploadMyImage() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Uploading")
        progressDialog.show()
        if (imageUri != null) {
            val fileReference = storageReference.child(
                System.currentTimeMillis()
                    .toString() + "." + getFileExtention(imageUri)
            )
            uploadTask = fileReference.putFile(imageUri)
            (uploadTask as UploadTask).continueWithTask(Continuation<UploadTask.TaskSnapshot?, Task<Uri?>?> { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                fileReference.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val mUri = downloadUri.toString()
                    reference =
                        FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.uid)
                    val map = HashMap<String, Any>()
                    map["imageUrl"] = mUri
                    reference.updateChildren(map)
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(context, "Failed!!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        } else {
            Toast.makeText(context, "No Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            if (uploadTask != null && uploadTask!!.isInProgress) {
                Toast.makeText(context, "Upload in progress..", Toast.LENGTH_SHORT).show()
            } else {
                uploadMyImage()
            }
        }
    }




}