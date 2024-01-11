package com.iqbaldriod.sensor_flashlight

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null

    private val proximitySensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Not needed for proximity sensor
        }

        override fun onSensorChanged(event: SensorEvent) {
            val distance = event.values[0]
            val flashlightIcon = findViewById<ImageView>(R.id.flashlightIcon)

            if (distance < proximitySensor!!.maximumRange) {
                // Hand is close to the screen
                turnOnFlashlight()
                flashlightIcon.setImageResource(R.drawable.torch_on)
            } else {
                // Hand is away from the screen
                turnOffFlashlight()
                flashlightIcon.setImageResource(R.drawable.torch_off)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (proximitySensor == null) {
            // Proximity sensor not available
            // Handle accordingly
        }

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraIds = cameraManager.cameraIdList
            cameraId = cameraIds[0] // Use the first camera
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun turnOnFlashlight() {
        try {
            // Check if the camera ID is not null before attempting to set torch mode
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId!!, true)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun turnOffFlashlight() {
        try {
            // Check if the camera ID is not null before attempting to set torch mode
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId!!, false)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            proximitySensorListener,
            proximitySensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(proximitySensorListener)
        turnOffFlashlight() // Ensure flashlight is off when the app is paused
    }
}
