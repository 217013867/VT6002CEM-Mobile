package com.example.fishing.ui.weather


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.fishing.databinding.FragmentWeatherBinding
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class WeatherFragment : Fragment() {


    private lateinit var editText: EditText
    private lateinit var searchButton: Button
    private lateinit var imageView: ImageView
    private lateinit var temptv: TextView
    private lateinit var time: TextView
    private lateinit var longitude: TextView
    private lateinit var latitude: TextView
    private lateinit var humidity: TextView
    private lateinit var sunrise: TextView
    private lateinit var sunset: TextView
    private lateinit var pressure: TextView
    private lateinit var wind: TextView
    private lateinit var country: TextView
    private lateinit var city_nam: TextView
    private lateinit var max_temp: TextView
    private lateinit var min_temp: TextView
    private lateinit var feels: TextView

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        editText = binding.editTextTextPersonName
        searchButton = binding.searchButton
        imageView = binding.imageView
        temptv = binding.temptv
        time = binding.time
        longitude = binding.longitude
        latitude = binding.latitude
        humidity = binding.humidity
        sunrise = binding.sunrise
        sunset = binding.sunset
        pressure = binding.pressure
        wind = binding.wind
        country = binding.country
        city_nam = binding.cityName
        max_temp = binding.tempMax
        min_temp = binding.minTemp
        feels = binding.feels

        searchButton.setOnClickListener {
            val city = editText.text.toString()
            val url =
                "http://api.openweathermap.org/data/2.5/weather?q=$city&appid=462f445106adc1d21494341838c10019&units=metric"
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        //find temperature
                        val jsonObject = JSONObject(response)
                        val `object` = jsonObject.getJSONObject("main")
                        val temp = `object`.getDouble("temp")
                        temptv.text = "Temperature\n$temp°C"

                        //find country
                        val object8 = jsonObject.getJSONObject("sys")
                        val count = object8.getString("country")
                        country.text = "$count  :"

                        //find city
                        val city = jsonObject.getString("name")
                        city_nam.text = city

                        //find icon
                        val jsonArray = jsonObject.getJSONArray("weather")
                        val obj = jsonArray.getJSONObject(0)
                        val icon = obj.getString("icon")
                        Picasso.get().load("http://openweathermap.org/img/wn/$icon@2x.png")
                            .into(imageView)

                        //find date & time
                        val calendar = Calendar.getInstance()
                        val std = SimpleDateFormat("HH:mm a \nE, MMM dd yyyy")
                        val date = std.format(calendar.time)
                        time.text = date

                        //find latitude
                        val object2 = jsonObject.getJSONObject("coord")
                        val lat_find = object2.getDouble("lat")
                        latitude.text = "$lat_find°  N"

                        //find longitude
                        val object3 = jsonObject.getJSONObject("coord")
                        val long_find = object3.getDouble("lon")
                        longitude.text = "$long_find°  E"

                        //find humidity
                        val object4 = jsonObject.getJSONObject("main")
                        val humidity_find = object4.getInt("humidity")
                        humidity.text = "$humidity_find  %"

                        //find sunrise
                        val object5 = jsonObject.getJSONObject("sys")
                        val sunrise_find = object5.getString("sunrise")
                        val sunrise_time = Date(sunrise_find.toLong() * 1000)
                        val sunrise_txt =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sunrise_time)
                        sunrise.text = "$sunrise_txt UTC+8"

                        //find sunrise
                        val object6 = jsonObject.getJSONObject("sys")
                        val sunset_find = object6.getString("sunset")
                        val sunset_time = Date(sunset_find.toLong() * 1000)
                        val sunset_txt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sunset_time)
                        sunset.text = "$sunset_txt UTC+8"

                        //find pressure
                        val object7 = jsonObject.getJSONObject("main")
                        val pressure_find = object7.getString("pressure")
                        pressure.text = "$pressure_find  hPa"

                        //find wind speed
                        val object9 = jsonObject.getJSONObject("wind")
                        val wind_find = object9.getString("speed")
                        wind.text = "$wind_find  km/h"

                        //find min temperature
                        val object10 = jsonObject.getJSONObject("main")
                        val mintemp = object10.getDouble("temp_min")
                        min_temp.text = "Min Temp\n$mintemp °C"

                        //find max temperature
                        val object12 = jsonObject.getJSONObject("main")
                        val maxtemp = object12.getDouble("temp_max")
                        max_temp.text = "Max Temp\n$maxtemp °C"

                        //find feels
                        val object13 = jsonObject.getJSONObject("main")
                        val feels_find = object13.getDouble("feels_like")
                        feels.text = "$feels_find °C"
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            ) { error ->
                Toast.makeText(requireActivity(), error.localizedMessage, Toast.LENGTH_SHORT).show()
            }

            val requestQueue = Volley.newRequestQueue(requireActivity())
            requestQueue.add(stringRequest)
        }
    }
}

