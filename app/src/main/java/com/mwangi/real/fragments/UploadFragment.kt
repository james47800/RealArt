package com.mwangi.real.fragments

import android.Manifest
import android.app.ProgressDialog
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mwangi.real.R
import com.mwangi.real.models.Arts

class UploadFragment : Fragment() {

    private lateinit var imageViewDisplay: ImageView
    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001
    private var imageUri: Uri? = null
    private lateinit var storageRef: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_display, container, false)
        imageViewDisplay = view.findViewById(R.id.imageViewDisplay)
        storageRef = FirebaseStorage.getInstance().reference.child("images")

        // Request storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_CODE
                )
            } else {
                pickImageFromGallery()
            }
        } else {
            pickImageFromGallery()
        }

        view.findViewById<ImageView>(R.id.imageFind).setOnClickListener {
            pickImageFromGallery()
        }

        view.findViewById<ImageView>(R.id.imagePush).setOnClickListener {
            if (imageUri != null) {
                uploadImageToFirebase(imageUri!!)
            } else {
                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun pickImageFromGallery() {
        // Intent to pick image from gallery
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        // Get the current user's ID
        var id = System.currentTimeMillis().toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Get the text from the EditText
        val username = view?.findViewById<EditText>(R.id.describe_upload)?.text.toString()
        if (username.length < 15) {
            Toast.makeText(requireContext(), "Description should be at least 15 characters", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("UploadFragment", "Username: $username")

        // Create a progress dialog to show upload progress
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Uploading")
        progressDialog.setMessage("Uploading your art...")
        progressDialog.show()

        // Upload image to Firebase Storage
        val userImageRef = storageRef.child("$userId/${id}.jpg")
        userImageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL of the uploaded image
                userImageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Save the download URL to Firebase Realtime Database
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Image uploaded successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    val artData = Arts(uri.toString(), username, userId!!, "")
                    val artsReference = FirebaseDatabase.getInstance().getReference().child("Arts")
                    val newArtReference = artsReference.push()
                    newArtReference.setValue(artData)

                    // Save the download URL to the "images" folder
                    val imagesRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
                    imagesRef.putFile(imageUri)
                        .addOnSuccessListener { taskSnapshot ->
                            imagesRef.downloadUrl.addOnSuccessListener { uri ->
                                // Save the download URL to Firebase Realtime Database
                                // You can use the "uri" variable to save the download URL to the database
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle failures
                        }
                }
            }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Failed to upload image: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnProgressListener { taskSnapshot ->
                // Update the progress dialog with the current upload progress
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                progressDialog.setMessage("Uploading ${progress}%")
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    pickImageFromGallery()
                } else {
                    // Permission denied
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        // Check if image was successfully picked
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            // Set the selected image to imageViewDisplay
            imageUri = data?.data
            imageViewDisplay.setImageURI(imageUri)
        }
    }
}

