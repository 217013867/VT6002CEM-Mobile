package com.example.vt6002cem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vt6002cem.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        mAuth = FirebaseAuth.getInstance();

        binding.registerBtn.setOnClickListener { view ->
//            createUser()
        }
    }

//    private fun createUser() {
//        println(binding.editTextTextEmailAddress.text)
//        return;
//    }
}