package com.example.vt6002cem.ui.home

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
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.example.vt6002cem.R
import com.example.vt6002cem.databinding.FragmentHomeBinding
import com.example.vt6002cem.ml.MobilenetV110224Quant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class HomeFragment : Fragment() {

    private lateinit var select_image_button: Button
    private lateinit var make_prediction: Button
    private lateinit var img_view: ImageView
    private lateinit var text_view: TextView
    private lateinit var bitmap: Bitmap
    private lateinit var camerabtn: Button

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    fun checkAndGetPermissions() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
        } else {
            Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireActivity(), "Camera permission granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireActivity(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        imgview = binding.imageView
//
//        val fileName = "label.txt"
//
//        val inputString = application.assets.open(fileName).bufferedReader().use { it.readText() }
//
//        var townList = inputString.split("\n")
//
//        var tv: TextView = findViewById(R.id.textView_predict)
//
//
//        var select: Button = findViewById(R.id.button_select)
//
//        select.setOnClickListener(View.OnClickListener {
//
//            var intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//
//            startActivityForResult(intent, 100)
//
//
//        })
//        var predict: Button = binding.buttonPredict
//        predict.setOnClickListener(View.OnClickListener {
//            var resized: Bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
//            val model = MobilenetV110224Quant.newInstance(this)
//
//// Creates inputs for reference.
//            val inputFeature0 =
//                TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
//
//            var tbuffer = TensorImage.fromBitmap(resized)
//            var byteBuffer = tbuffer.buffer
//            inputFeature0.loadBuffer(byteBuffer)
//
//// Runs model inference and gets result.
//            val outputs = model.process(inputFeature0)
//            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
//
//            var max = getMax(outputFeature0.floatArray)
//
//            tv.setText(townList[max].toString())
//
//// Releases model resources if no longer used.
//            model.close()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        content.addView(aboutPage);

        select_image_button = binding.buttonSelect
        make_prediction = binding.buttonPredict
        img_view = binding.imageView
        text_view = binding.textViewPredict
        camerabtn = binding.buttonCamera

        // handling permissions
        checkAndGetPermissions()

        val labels =
            application.assets.open("labels.txt").bufferedReader().use { it.readText() }.split("\n")

        select_image_button.setOnClickListener(View.OnClickListener {
            Log.d("mssg", "button pressed")
            var intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent, 250)
        })

        make_prediction.setOnClickListener(View.OnClickListener {
            var resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val model = MobilenetV110224Quant.newInstance(this)

            var tbuffer = TensorImage.fromBitmap(resized)
            var byteBuffer = tbuffer.buffer

// Creates inputs for reference.
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
            inputFeature0.loadBuffer(byteBuffer)

// Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            var max = getMax(outputFeature0.floatArray)

            text_view.setText(labels[max])

// Releases model resources if no longer used.
            model.close()
        })

        camerabtn.setOnClickListener(View.OnClickListener {
            var camera: Intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camera, 200)
        })
    }
}


override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 250) {
        img_view.setImageURI(data?.data)

        var uri: Uri? = data?.data
        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
    } else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
        bitmap = data?.extras?.get("data") as Bitmap
        img_view.setImageBitmap(bitmap)
    }

}

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

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


