package com.example.fishing

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fishing.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executor


/**`
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class Login : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    /**
     * Assign firebaseAuth to mAuth
     */
    private lateinit var mAuth: FirebaseAuth

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo


    /**
     * Called when the activity will start interacting with the user.
     * Handing fingerprint authentication.
     */
    override fun onResume() {
        super.onResume()
        val biometricStatusTextView = binding.biometricStatus
        val biometricManager = BiometricManager.from(requireActivity())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                biometricStatusTextView.text = "App can authenticate using biometrics."
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                biometricStatusTextView.text = "No biometric features available on this device."
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                biometricStatusTextView.text = "Biometric features are currently unavailable."
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                // Prompts the user to create credentials that your app accepts.
                biometricStatusTextView.text = "Biometric features are not enrolled."
        }
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    /**
     * Called when the fragment's activity has been created and this fragment's view hierarchy instantiated.
     * In this case, it will handing Biometric authentication
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        executor = ContextCompat.getMainExecutor(requireContext())

        biometricPrompt = BiometricPrompt(
            requireActivity(), executor,
            object : BiometricPrompt.AuthenticationCallback() {
                /**
                 * Handing fingerprint authentication error
                 * And display Authentication error message
                 */
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        requireContext(),
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                /**
                 * Handing fingerprint authentication succeeded
                 * And display Authentication succeeded message
                 */
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    startActivity(Intent(requireActivity(), Home::class.java))
                    Toast.makeText(
                        requireContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                /**
                 * Handing fingerprint authentication succeeded
                 * And display Authentication failed message
                 */
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        requireContext(), "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        val biometricLoginButton = binding.biometricLogin
        biometricLoginButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mAuth = FirebaseAuth.getInstance();

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Handing user & password login
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * When longin button was clicked
         */
        binding.loginBtn.setOnClickListener {

            // connect to firebase
            val email: String = binding.editTextTextEmailAddress.text.toString()
            val password: String = binding.editTextTextPassword.text.toString()

            /**
             * Email and Password should be empty
             */
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(requireActivity(), "Email cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(requireActivity(), "Password cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    /**
                     * Handling login succeeded
                     */
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireActivity(),
                            "User logged in successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(requireActivity(), Home::class.java))
                    } else {
                        /**
                         * Login error
                         */
                        Toast.makeText(
                            requireActivity(),
                            "Log in Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        /**
         * Navigate to a destination
         */
        binding.tvRegisterHere.setOnClickListener { view ->
            findNavController().navigate(R.id.action_LoginFragment_to_RegisterFragment)
        }
    }

    /**
     * Allows the fragment to clean up resources associated with its View.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}