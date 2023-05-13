package com.mwangi.real.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mwangi.real.AccountSettings

import com.mwangi.real.R
import com.mwangi.real.ViewActivity

class AccountFragment : Fragment() {

    private lateinit var profileEmailTextView: TextView
    private lateinit var profileUsername : TextView
    private lateinit var profileImageDisplay: ImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var accountSettingsIcon:ImageView
    private lateinit var storageReference: StorageReference

    companion object {
        private const val TAG = "AccountFragment"
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountSettingsIcon = view.findViewById(R.id.accountSettingsIcon)
        accountSettingsIcon.setOnClickListener {
            val intent = Intent(activity, AccountSettings::class.java)
            startActivity(intent)
        }

        profileEmailTextView = view.findViewById(R.id.Profileemail)
        profileImageDisplay = view.findViewById(R.id.imageViewDisProf)
        profileUsername=view.findViewById(R.id.ProfileUsername)

        // Get Firebase Authentication instance
        firebaseAuth = FirebaseAuth.getInstance()

        // Get Firebase Realtime Database instance and reference to the user's node
        val userId = firebaseAuth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(userId.toString())

        // Get Firebase Storage instance and reference to the user's profile picture
        storageReference = FirebaseStorage.getInstance().reference.child("profile_pictures/${userId}")

        // Read user's name and profile picture URL from Firebase Realtime Database and set them to the UI
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java)
                profileEmailTextView.text = name
                val email = snapshot.child("email").getValue(String::class.java)
                profileUsername.text=email

                val profilePictureUrl = snapshot.child("profile_picture_url").getValue(String::class.java)
                if (!profilePictureUrl.isNullOrEmpty()) {
                    // Load the profile picture into the ImageView using Glide
                    Glide.with(this@AccountFragment).load(profilePictureUrl).into(profileImageDisplay)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read user's data from database: ${error.message}")
            }
        })

        // Open gallery to select an image when profile picture is clicked
        profileImageDisplay.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            val imageRef = storageReference.child(imageUri?.lastPathSegment ?: "defaultFileName")


            // Upload the selected image to Firebase Storage
            val uploadTask = imageUri?.let { imageRef.putFile(it) }
            uploadTask?.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update the user's profile picture URL in the Firebase Realtime Database
                    val downloadUrl = task.result.toString()
                    databaseReference.child("profile_picture_url").setValue(downloadUrl)
                }
            }
        }

    }
}
