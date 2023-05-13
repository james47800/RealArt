package com.mwangi.real

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.mwangi.real.fragments.AccountFragment


class UpdatingUserInfo : AppCompatActivity() {
    private lateinit var myUploadButtonAll: Button
    private lateinit var myUpdateUsername: EditText
    private lateinit var user: FirebaseUser // declare user here
    private val TAG = "UpdatingUserInfoActivity"
    private var newName: String? = null // declare newName here
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updating_user_info)

        myUploadButtonAll = findViewById(R.id.UpdatingButtonAll)
        myUpdateUsername = findViewById(R.id.updatingUsername)

        // Disable autofill for the EditText fields
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            myUpdateUsername.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO)
        }

        // Get current user
        user = FirebaseAuth.getInstance().currentUser!!
        // Set existing name as hint in the EditText field
        myUpdateUsername.hint = user.displayName

        myUploadButtonAll.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Updating User Information...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            // Get updated name
            newName = myUpdateUsername.text.toString().trim()

            // Update user's name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User name updated.")
                    } else {
                        Log.e(TAG, "Failed to update user name.", task.exception)
                    }
                }

            // Check if name was updated
            if (newName != null) {
                // Update user data in Firebase Realtime Database
                val userId = user.uid
                val ref = FirebaseDatabase.getInstance().getReference("users").child(userId)
                val childUpdates = HashMap<String, Any>()
                childUpdates["name"] = newName!!
                ref.updateChildren(childUpdates)
                    .addOnSuccessListener {
                        Log.d(TAG, "User data updated in Firebase Realtime Database.")
                        progressDialog.dismiss()
                        Toast.makeText(this, "User information updated successfully!", Toast.LENGTH_LONG).show()

                        // Navigate to AccountFragment
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("fragment", "account")
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to update user data in Firebase Realtime Database.", e)
                        progressDialog.dismiss()
                        Toast.makeText(this, "Failed to update user information. Please try again.", Toast.LENGTH_LONG).show()
                    }
            } else {
                progressDialog.dismiss()
                Toast.makeText(this, "No changes made to user information.", Toast.LENGTH_LONG).show()

                // Navigate to AccountFragment
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("fragment", "account")
                startActivity(intent)
            }
        }
    }
}
