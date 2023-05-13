package com.mwangi.real

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.app.ProgressDialog

class RegisterActivity : AppCompatActivity() {
    private lateinit var mRegName: EditText
    private lateinit var mRegEmail: EditText
    private lateinit var mRegPassword1: EditText
    private lateinit var mRegPassword2: EditText
    private lateinit var mRegFinish: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Saving data...")
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mDatabase = FirebaseDatabase.getInstance()

        mRegName = findViewById(R.id.regname)
        mRegEmail = findViewById(R.id.regemail)
        mRegPassword1 = findViewById(R.id.regpassword1)
        mRegPassword2 = findViewById(R.id.regpassword2)
        mRegFinish = findViewById(R.id.regbtnfinish)

        mRegFinish.setOnClickListener {
            val name = mRegName.text.toString()
            val email = mRegEmail.text.toString()
            val password1 = mRegPassword1.text.toString()
            val password2 = mRegPassword2.text.toString()

            if (name.isEmpty() || email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (password1 != password2) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if (password1.length < 8) {
                Toast.makeText(
                    this,
                    "Password must be at least 8 characters long",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mProgressDialog.show()
                mAuth.createUserWithEmailAndPassword(email, password1)
                    .addOnCompleteListener { task ->
                        mProgressDialog.dismiss()
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
                            // Store the user's name and email in Firebase Realtime Database
                            val uid = user!!.uid
                            val userData = hashMapOf<String, String>(
                                "name" to name,
                                "email" to email
                            )
                            mDatabase.reference.child("users").child(uid).setValue(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Registration successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Start LoginActivity and close this activity
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Registration failed: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            Toast.makeText(
                                this,
                                "Registration failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}
