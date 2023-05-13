package com.mwangi.real

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SplashActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        // Delay for 2 seconds
        Handler().postDelayed({
            checkUserAuthStatus()
        }, 2000) // 2000 milliseconds = 2 seconds
    }

    private fun checkUserAuthStatus() {
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid
            mDatabase.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // User is already logged in, take them to MainActivity
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // User is not logged in, take them to GetStartedActivity
                        val intent = Intent(this@SplashActivity, GetStartedActivity::class.java)
                        startActivity(intent)
                    }
                    finish() // prevent the user from coming back to the SplashActivity after going to the next activity
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors here
                    finish()
                }
            })
        } else {
            // User is not logged in, take them to GetStartedActivity
            val intent = Intent(this, GetStartedActivity::class.java)
            startActivity(intent)
            finish() // prevent the user from coming back to the SplashActivity after going to the next activity
        }
    }
}
