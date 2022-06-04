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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.time.Instant


/**
 * A simple [Fragment] subclass.
 * Use the [Register.newInstance] factory method to
 * create an instance of this fragment.
 */
class Register : Fragment() {
    private var _binding: FragmentRegisterBinding? = null

    private lateinit var mAuth: FirebaseAuth
    private val binding get() = _binding!!
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .build()

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
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val mediaType = "application/json".toMediaType()
            val bodyReq = "{\n" +
                    "      \"dataSource\": \"Cluster1\",\n" +
                    "      \"database\": \"FishApp\",\n" +
                    "      \"collection\": \"users\",\n"

            val unixTime: String = Instant.now().getEpochSecond().toString()

            val request = Request.Builder()
                .url("https://data.mongodb-api.com/app/data-grmrc/endpoint/data/v1/action/insertOne")
                .header("Content-Type", "application/json")
                .header(
                    "api-key",
                    "VgP7ObwftcYymjetp5C5LVyqBgbWDSrETQ4njGISc7WyLi4tqG2WiOB7QLTaWpiD"
                )
                .post(bodyReq.toRequestBody(mediaType))
                .build()

            println(binding.editTextTextEmailAddress.text)
            val username = binding.editTextTextUsername.text.toString()
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()

            if (TextUtils.isEmpty(username)) {
                Toast.makeText(requireActivity(), "Username cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            } else if (TextUtils.isEmpty(email)) {
                Toast.makeText(requireActivity(), "Email cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(requireActivity(), "Password cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // insert record into mongodb
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("dataSource", "Cluster1")
                    jsonObject.put("database", "FishApp")
                    jsonObject.put("collection", "users")

                    val documentObject = JSONObject()
                    documentObject.put("username", username)
                    val dateObject = JSONObject()
                    val numberLongObject = JSONObject()

                    numberLongObject.put("\$numberLong", unixTime.toString())
                    dateObject.put("\$date", numberLongObject)
                    documentObject.put("createTime", dateObject)
                    jsonObject.put("document", documentObject)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

//            Log.d("RequestBody", jsonObject.toString())
//
//            val requestBody = jsonObject.toString().toRequestBody(JSON)
//
//            val request = Request.Builder()
//                .url("https://data.mongodb-api.com/app/data-grmrc/endpoint/data/v1/action/insertOne")
//                .header("Content-Type", "application/json")
//                .header("api-key", "VgP7ObwftcYymjetp5C5LVyqBgbWDSrETQ4njGISc7WyLi4tqG2WiOB7QLTaWpiD")
//                .post(bodyReq.toRequestBody(MediaType)
//                .build()

//            val request: Request = Request.Builder()
//                .url("https://data.mongodb-api.com/app/data-grmrc/endpoint/data/v1/action/insertOne")
//                .method("POST", requestBody)
//                .addHeader(
//                    "api-key",
//                    "VgP7ObwftcYymjetp5C5LVyqBgbWDSrETQ4njGISc7WyLi4tqG2WiOB7QLTaWpiD"
//                )
//                .addHeader("Content-Type", "application/json")
//                .build()

//

                val buffer = Buffer()
                request.body?.writeTo(buffer)
                Log.d(">>>>", buffer.readUtf8().toString())

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")

                            for ((name, value) in response.headers) {
                                Log.d(name, value)
                            }

                            Log.d(response.body!!.string(), "<<<")
                        }
                    }
                })

                // 執行Call連線到網址
//            call.enqueue(object : Callback {
//                @Throws(IOException::class)
//                override fun onResponse(call: Call?, response: Response) {
//                    // 連線成功
//                    val result = response.body()!!.string()
//                    Log.d("OkHttp result", result)
//                }
//
//                override fun onFailure(call: Call?, e: IOException?) {
//                    // 連線失敗
//                    Log.e("OkHttp error", e.toString())
//                }
//            })

                // create user in firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
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
}
