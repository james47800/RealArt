package com.mwangi.real

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent


class GetStartedActivity : AppCompatActivity() {
    lateinit var btnSignUp:Button
    lateinit var btnSignIn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)
        btnSignIn=findViewById(R.id.mBtnSignIn)
        btnSignUp=findViewById(R.id.mBtnSignUp)
        // Set click listener for btnSignUp
        btnSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for btnSignIn
        btnSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}