package com.example.cabinsync

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cabinsync.databinding.LoginScreenBinding
import com.example.cabinsync.dataclass.userdata
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class login_screen : AppCompatActivity() {
    private val binding:LoginScreenBinding by lazy{
        LoginScreenBinding.inflate(layoutInflater)
    }

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val login=binding.loginbtn
        val guest=binding.guestloginbtn
        val username=binding.usernametxt
        val password=binding.passwordtxt

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("User")

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        if (isUserSignedIn()) {
            val intent = Intent(this, user_home::class.java)
            startActivity(intent)
            finishAffinity()
            return
        }

        if (isGuestSignedIn()) {
            val intent = Intent(this, guest_main::class.java)
            startActivity(intent)
            finishAffinity()
            return
        }

        login.setOnClickListener(){
            val usernamet = username.text.toString()
            val passwordt = password.text.toString()

            if (usernamet.isNotEmpty() && passwordt.isNotEmpty()) {
                signinUser(usernamet, passwordt)
            } else {
                Toast.makeText(this, "Please Enter Your Login Credentials", LENGTH_SHORT)
                    .show()
            }
        }

        guest.setOnClickListener(){
            val intent=Intent(this,guest_register::class.java)
            startActivity(intent)
        }

        binding.frgtbtn.setOnClickListener(){
            val message:String? = "Please Contact Admin"
            showCustomDialogBox(message)
        }
    }

    private fun signinUser(username: String, password: String) {
        databaseReference.orderByChild("email").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val userData = userSnapshot.getValue(userdata::class.java)

                            val pass = userData?.password
                            val email = userData?.email
                            val dept=userData?.department
                            val role=userData?.designation
                            val id=userSnapshot.key

                            if (pass == password && email == username) {
                                val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPrefs.edit()
                                editor.putString("loginuser", email)
                                editor.putString("department", dept)
                                editor.putString("designation", role)
                                editor.putString("LoginId", id)
                                editor.putBoolean("isSignedIn", true)
                                editor.apply()

                                val intent = Intent(this@login_screen, user_home::class.java)
                                startActivity(intent)
                                Toast.makeText(this@login_screen,"Signin Successful", LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@login_screen,"Invalid Login Credentials.",
                                    LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@login_screen,"Database Error: ${error.message}",
                        LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showCustomDialogBox(message: String?) {
        val dialog = Dialog(this@login_screen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val passmessage : TextView =dialog.findViewById(R.id.dialogtext)
        val logoutButton : Button = dialog.findViewById(R.id.logoutbutton)
        val cancelButton : Button = dialog.findViewById(R.id.cancelbutton)

        passmessage.text=message
        cancelButton.visibility= View.GONE
        logoutButton.text="OK"

        logoutButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun isUserSignedIn(): Boolean {
        return sharedPreferences.getBoolean("isSignedIn", false)
    }

    private fun isGuestSignedIn(): Boolean {
        return sharedPreferences.getBoolean("GuestsignedIn", false)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}