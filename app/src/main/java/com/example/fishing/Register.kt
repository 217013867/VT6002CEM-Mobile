package com.example.fishing
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fishing.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.Instant
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [Register.newInstance] factory method to
 * create an instance of this fragment.
 */
class Register : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("MyActivity", "MyClass.getView() — get item number")
        binding.registerBtn.setOnClickListener { view ->
            createUser()
        }

        binding.tvLoginHere.setOnClickListener { view ->
            findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
        }
    }

    private fun createUser() {
        val unixTime: String = Instant.now().getEpochSecond().toString()

        println(binding.editTextTextEmailAddress.text)
        val username = binding.editTextTextUsername.text.toString()
        val email = binding.editTextTextEmailAddress.text.toString()
        val password = binding.editTextTextPassword.text.toString()

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(requireActivity(), "Username cannot be empty", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(requireActivity(), "Email cannot be empty", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(requireActivity(), "Password cannot be empty", Toast.LENGTH_SHORT).show()
        } else {
            // insert record into mongodb

            val user = hashMapOf(
                "username" to username,
                "email" to email,
                "createTime" to unixTime
            )
            // Add a new document with a generated ID
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

            // create user in firebase
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireActivity(),
                        "User registered successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)

                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Registration Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
