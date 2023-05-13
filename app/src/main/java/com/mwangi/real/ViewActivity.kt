package com.mwangi.real
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
class ViewActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ViewActivity"
    }

    private lateinit var imageContainer: LinearLayout
    private lateinit var storageReference: StorageReference
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        // Get a reference to the LinearLayout that will contain the images
        imageContainer = findViewById(R.id.imageContainer)

        // Get a reference to the Firebase Storage instance and the folder containing the user's images
        currentUser = FirebaseAuth.getInstance().currentUser!!
        storageReference = FirebaseStorage.getInstance().getReference("images/${currentUser.uid}/")

        // Show a progress dialog while retrieving the images from Firebase Storage
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Fetching art")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // List all the images in the folder and load them into the LinearLayout using Glide
        storageReference.listAll().addOnSuccessListener { listResult ->
            for (imageRef in listResult.items) {
                val imageView = ImageView(this)
                imageView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                // Load the image into the ImageView using Glide
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this).load(uri).into(imageView)
                }.addOnFailureListener { exception ->
                    // Handle any errors
                    Log.e(TAG, "Failed to fetch image from Firebase Storage: ${exception.message}")
                }

                // Add the ImageView to the LinearLayout
                imageContainer.addView(imageView)

                // Set a long click listener on the ImageView to delete the image when long-pressed
                imageView.setOnLongClickListener {
                    deleteImage(imageRef)
                    // Return true to indicate that the event has been consumed
                    true
                }
            }
            // Dismiss the progress dialog once the images are loaded
            progressDialog.dismiss()
        }.addOnFailureListener { exception ->
            progressDialog.dismiss()
            // Handle any errors
            Log.e(TAG, "Failed to list images in Firebase Storage: ${exception.message}")
        }
    }

    private fun deleteImage(imageRef: StorageReference) {
        // Show a progress dialog while deleting the image
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Deleting Image")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Delete the image from Firebase Storage
        imageRef.delete().addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(this, "Image deleted successfully", Toast.LENGTH_SHORT).show()

            // Refresh the activity to display the updated list of images
            finish()
            startActivity(intent)
        }.addOnFailureListener { exception ->
            progressDialog.dismiss()
            // Handle any errors
            Log.e(TAG, "Failed to delete image from Firebase Storage: ${exception.message}")
            Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show()
        }
    }
}
