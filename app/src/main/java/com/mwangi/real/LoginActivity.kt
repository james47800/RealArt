package com.mwangi.real
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var mLogInName: EditText
    private lateinit var mLoginPassword: EditText
    private lateinit var mBtnLoginFinish: Button
    private lateinit var progressDialog: ProgressDialog

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()

        mLogInName = findViewById(R.id.login_name_or_email)
        mLoginPassword = findViewById(R.id.loginPassword)
        mBtnLoginFinish = findViewById(R.id.loginfinish)

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Real Art is getting things ready...")

        mBtnLoginFinish.setOnClickListener {
            if (!isNetworkConnected()) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val nameOrEmail = mLogInName.text.toString()
            val password = mLoginPassword.text.toString()

            if (nameOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                progressDialog.show()
                val isValidEmail = nameOrEmail.length >= 8 // Check if email is at least 8 characters long
                val signInTask = if (isValidEmail) {
                    // Sign in with email
                    mAuth.signInWithEmailAndPassword(nameOrEmail, password)
                } else {
                    // Sign in with username
                    mAuth.signInWithEmailAndPassword("$nameOrEmail@example.com", password)
                }

                signInTask.addOnCompleteListener { task ->
                    progressDialog.dismiss()
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        if (user != null) {
                            // Retrieve user data from Realtime Database
                            val uid = user.uid
                            val userRef = mDatabase.getReference("users/$uid")
                            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val userData = snapshot.getValue(User::class.java)
                                    if (userData != null) {
                                        // Save user data in a singleton class
                                        UserSession.userData = userData

                                        // Start the HomeActivity after login is successful
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this@LoginActivity, "User data not found", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@LoginActivity, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                                }
                            })
                        } else {
                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Wrong password or email", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

}

// Data class for user data
data class User(
    val name: String = "",
    val email: String = "",
    val imageUrl: String = ""
)

// Singleton class for user session data
object UserSession {
    var userData: User? = null
}
