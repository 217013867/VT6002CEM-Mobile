package com.example.vt6002cem

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vt6002cem.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Register.newInstance] factory method to
 * create an instance of this fragment.
 */
class Register : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentRegisterBinding? = null

    private lateinit var mAuth: FirebaseAuth
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();

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
        println(binding.editTextTextEmailAddress.text)
        val email = binding.editTextTextEmailAddress.text.toString()
        val password = binding.editTextTextPassword.text.toString()

        if (TextUtils.isEmpty(email)) {
//            etRegEmail.setError("Email cannot be empty")
//            etRegEmail.requestFocus()
        } else if (TextUtils.isEmpty(password)) {
//            etRegPassword.setError("Password cannot be empty")
//            etRegPassword.requestFocus()
        } else {
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