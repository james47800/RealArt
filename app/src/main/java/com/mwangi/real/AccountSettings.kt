package com.mwangi.real
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class AccountSettings : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var updaterAccount:LinearLayout
    private lateinit var newAccountWork: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        var updaterAccount=findViewById<LinearLayout>(R.id.updatingAccount)
        var newAccountWork=findViewById<LinearLayout>(R.id.CreatingNewAccount)
        newAccountWork.setOnClickListener{
            val intent = Intent(this, GetStartedActivity::class.java)
            startActivity(intent)
        }
        updaterAccount.setOnClickListener{
            val intent = Intent(this, UpdatingUserInfo::class.java)
            startActivity(intent)
        }
        // Initialize Firebase Authentication instance
        auth = FirebaseAuth.getInstance()

        // Find the view for deleting account
        val deleteLayout = findViewById<LinearLayout>(R.id.DeletingAccount)

        // Set a click listener for the delete layout
        deleteLayout.setOnClickListener {
            // Show an alert dialog to confirm deletion
            showDeleteConfirmationDialog()
        }

    }

    private fun showDeleteConfirmationDialog() {
        // Create an alert dialog to confirm deletion
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your account?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Get the user's password to confirm deletion
            val passwordDialog = PasswordConfirmationDialog(this) { password ->
                // Delete the user's account
                val user = auth.currentUser
                val credential = EmailAuthProvider.getCredential(user?.email ?: "", password)
                user?.reauthenticate(credential)?.addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        user.delete()
                            .addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    // Account deleted successfully
                                    Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show()

                                    // Launch the GetStartedActivity
                                    val intent = Intent(this, GetStartedActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                } else {
                                    // Failed to delete account
                                    Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // Failed to re authenticate user
                        Toast.makeText(this, "Failed to reauthenticate", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            passwordDialog.show()
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }

}

class PasswordConfirmationDialog(context: Context, private val onConfirm: (String) -> Unit) : Dialog(context) {

    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_password_confirmation)

        // Find views
        passwordEditText = findViewById(R.id.passwordEditText)
        val confirmButton = findViewById<Button>(R.id.confirmButton)

        // Set click listener for confirm button
        confirmButton.setOnClickListener {
            // Get the password from the edit text
            val password = passwordEditText.text.toString()

            // Validate the password and fields
            if (password.isNotEmpty()) {
                // Call onConfirm callback with the password
                onConfirm(password)

                // Dismiss the dialog
                dismiss()
            } else {
                // Show an error message indicating empty password field
                Toast.makeText(context, "Please enter a password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
