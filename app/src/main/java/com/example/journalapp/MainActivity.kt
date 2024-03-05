package com.example.journalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.journalapp.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        auth = Firebase.auth

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnCreate.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            signInWithEmail()
        }
    }

    private fun signInWithEmail() {
        val email = binding.email.text.toString()
        val pass = binding.password.text.toString()
        auth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user  = auth.currentUser
                    goToJournalList()
                } else {
                    Toast.makeText(this, "Authenticated failure", Toast.LENGTH_SHORT).show()
                }
                            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            goToJournalList()
        }
    }

    private fun goToJournalList() {
        startActivity(Intent(this, JournalList::class.java))
    }
}