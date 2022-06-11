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

/**
 * Weather Page- Weather App
 */
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

    /**
     * Called to have the fragment instantiate its user interface view.
     * In this case, weather fragment was instantiated.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called when the fragment's activity has been created and this fragment's view hierarchy instantiated.
     * This part mainly handle the get weather data and display it
     */
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

        /**
         * when search button was clicked, get weather data and display it
         */
        searchButton.setOnClickListener {
            val city = editText.text.toString()

            /**
             * contect to openweathermap.org
             */
            val url =
                "http://api.openweathermap.org/data/2.5/weather?q=$city&appid=462f445106adc1d21494341838c10019&units=metric"

            /**
             * Get the data from openweathermap.org and display it in correct position
             */
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        /**
                         * Get temperature
                         */
                        val jsonObject = JSONObject(response)
                        val `object` = jsonObject.getJSONObject("main")
                        val temp = `object`.getDouble("temp")
                        temptv.text = "Temperature\n$temp°C"

                        /**
                         * Get country
                         */
                        val countrycode = jsonObject.getJSONObject("sys")
                        val count = countrycode.getString("country")
                        country.text = "$count  :"

                        /**
                         * Get city
                         */
                        val city = jsonObject.getString("name")
                        city_nam.text = city

                        /**
                         * Get icon
                         */
                        val jsonArray = jsonObject.getJSONArray("weather")
                        val obj = jsonArray.getJSONObject(0)
                        val icon = obj.getString("icon")
                        Picasso.get().load("http://openweathermap.org/img/wn/$icon@2x.png")
                            .into(imageView)

                        /**
                         * Get date & time
                         * The formal is 'HH:mm, HHH dd yyyy
                         */
                        val calendar = Calendar.getInstance()
                        val std = SimpleDateFormat("HH:mm a \nE, MMM dd yyyy")
                        val date = std.format(calendar.time)
                        time.text = date

                        /**
                         * Get latitude
                         */
                        val object_latitude = jsonObject.getJSONObject("coord")
                        val lat_find = object_latitude.getDouble("lat")
                        latitude.text = "$lat_find°  N"

                        /**
                         * Get longitude
                         */
                        val object_longitude = jsonObject.getJSONObject("coord")
                        val long_find = object_longitude.getDouble("lon")
                        longitude.text = "$long_find°  E"

                        /**
                         * Get humidity
                         */
                        val object_humidity = jsonObject.getJSONObject("main")
                        val humidity_find = object_humidity.getInt("humidity")
                        humidity.text = "$humidity_find  %"

                        /**
                         * Get sunrise time and covert the time format
                         */
                        val object_sunrise = jsonObject.getJSONObject("sys")
                        val sunrise_find = object_sunrise.getString("sunrise")
                        val sunrise_time = Date(sunrise_find.toLong() * 1000)
                        val sunrise_txt =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sunrise_time)
                        sunrise.text = "$sunrise_txt UTC+8"

                        /**
                         * Get sunset time and covert the time format
                         */
                        val object_sunset = jsonObject.getJSONObject("sys")
                        val sunset_find = object_sunset.getString("sunset")
                        val sunset_time = Date(sunset_find.toLong() * 1000)
                        val sunset_txt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sunset_time)
                        sunset.text = "$sunset_txt UTC+8"

                        /**
                         * Get pressure
                         */
                        val object_pressure = jsonObject.getJSONObject("main")
                        val pressure_find = object_pressure.getString("pressure")
                        pressure.text = "$pressure_find  hPa"

                        /**
                         * Get wind speed
                         */
                        val object_wind = jsonObject.getJSONObject("wind")
                        val wind_find = object_wind.getString("speed")
                        wind.text = "$wind_find  km/h"

                        /**
                         * Get min temperature
                         */
                        val object_min = jsonObject.getJSONObject("main")
                        val mintemp = object_min.getDouble("temp_min")
                        min_temp.text = "Min Temp\n$mintemp °C"

                        /**
                         * Get max temperature
                         */
                        val object_max = jsonObject.getJSONObject("main")
                        val maxtemp = object_max.getDouble("temp_max")
                        max_temp.text = "Max Temp\n$maxtemp °C"

                        /**
                         * Get feels
                         */
                        val object_feel = jsonObject.getJSONObject("main")
                        val feels_find = object_feel.getDouble("feels_like")
                        feels.text = "$feels_find °C"
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            ) { error ->
                Toast.makeText(requireActivity(), error.localizedMessage, Toast.LENGTH_SHORT).show()
            }

            /**
             * Display the data on the screen
             */
            val requestQueue = Volley.newRequestQueue(requireActivity())
            requestQueue.add(stringRequest)
        }
    }

    fun getDoubleTime(): Long {
        return Date().time * 2
    }
}

