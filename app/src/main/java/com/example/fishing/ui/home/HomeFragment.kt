package com.example.fishing.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.fishing.databinding.FragmentHomeBinding
import com.example.fishing.ml.MobilenetV110224Quant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Home Page- fish classification
 */
class HomeFragment : Fragment() {

    private lateinit var select_image_button: Button
    private lateinit var make_prediction: Button
    private lateinit var img_view: ImageView
    private lateinit var text_view: TextView
    private lateinit var bitmap: Bitmap
    private lateinit var camerabtn: Button
    private var cameraPermissionGranted: Boolean = false

    private var _binding: FragmentHomeBinding? = null


    private val binding get() = _binding!!

    /**
     *
     * Request camera permission for using camera of the device.
     * The result of the permission request is handled by a callback, onRequestPermissionsResult
     */
    private fun checkAndGetPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            )
            == PackageManager
                .PERMISSION_GRANTED
        ) {
            // When permission is granted
            cameraPermissionGranted = true
        } else {
            // When permission is not granted
            // Call method
            requestPermissions(
                arrayOf(
                    Manifest.permission
                        .CAMERA
                ), 101
            );
        }
    }

    /**
     *This interface is the contract for receiving the results for permission requests.
     * If passed, camera permission granted. Otherwise, permission denied.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireActivity(), "Camera permission granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireActivity(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * In this case, Home fragment was instantiated.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        return binding.root
    }

    /**
     * Called when the fragment's activity has been created and this fragment's view hierarchy instantiated.
     * This part mainly handle the fish classification.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        select_image_button = binding.buttonSelect
        make_prediction = binding.buttonPredict
        img_view = binding.imageView
        text_view = binding.textViewPredict
        camerabtn = binding.buttonCamera

        /**
         * handling permissions
         */
        checkAndGetPermissions()

        var data = ""
        val assetManager = requireContext().resources.assets
        var inputStream: InputStream? = null
        lateinit var labels: List<String>

        /**
         * Assign label.txt to input stream
         */
        try {
            inputStream = assetManager.open("label.txt")
            val buf = StringBuilder()
            val `in` = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var str: String?
            while (`in`.readLine().also { str = it } != null) {
                buf.append(str)
            }
            `in`.close()
            data = buf.toString()
            Log.d("data!!!!!!", data)
            labels = data.split(":")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        /**
         * Handling select image from device
         */
        select_image_button.setOnClickListener(View.OnClickListener {
            Log.d("mssg", "button pressed")
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent, 250)
        })

        /**
         * Handling Fish classification
         * Resize the imported image to 224 * 224 for better watch
         */
        make_prediction.setOnClickListener(View.OnClickListener {
            val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val model = MobilenetV110224Quant.newInstance(requireActivity())

            val tbuffer = TensorImage.fromBitmap(resized)
            val byteBuffer = tbuffer.buffer

            /**
             * Creates inputs for reference.
             */
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
            inputFeature0.loadBuffer(byteBuffer)

            /**
             * Runs model inference and gets result.
             */
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val max = getMax(outputFeature0.floatArray)

            Log.d("labels", labels.toString())
            Log.d("max", max.toString())
            text_view.text = labels[max]

            /**
             * Releases model resources if no longer used.
             */

            model.close()
        })

        /**
         * Handling taking image
         */
        camerabtn.setOnClickListener(View.OnClickListener {
            val camera: Intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camera, 200)
        }
        )
    }

    /**
     * Show the result after the onActivityCreated
     * Show the fish classification result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 250) {
            img_view.setImageURI(data?.data)

            val uri: Uri? = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            bitmap = data?.extras?.get("data") as Bitmap
            img_view.setImageBitmap(bitmap)
        }

    }

    /**
     * Allows the fragment to clean up resources associated with its View.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Get the Max value
     */
    fun getMax(arr: FloatArray): Int {
        var ind = 0
        var min = 0.0f

        for (i in 0..1000) {
            if (arr[i] > min) {
                ind = i
                min = arr[i]
            }
        }
        return ind
    }
}



