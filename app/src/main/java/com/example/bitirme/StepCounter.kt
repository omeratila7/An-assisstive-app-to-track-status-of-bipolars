package com.example.bitirme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_step_counter.*

class StepCounter : AppCompatActivity() , SensorEventListener{

    private var sensorManager: SensorManager?=null

    private var running = false;
    private var totalSteps = 0f;
    private var previousTotalSteps = 0f;


    override fun onCreate(savedInstanceState: Bundle?) {

        //loadData()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_counter)

        loadData()
        resetSteps()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

    }

    override fun onResume() {
        super.onResume()
        running = true;
        val stepSensor : Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        
        if (stepSensor == null){
            Toast.makeText(this, "no sensor detected", Toast.LENGTH_SHORT).show()
        }else{
            val x = sensorManager?.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_UI)
            Toast.makeText(this, x.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(running){
            totalSteps = event!!.values[0]
            val currentSteps : Int = totalSteps.toInt() - previousTotalSteps.toInt()
            steps_taken.text = ("$currentSteps")
        }
    }

    private fun resetSteps(){
        steps_taken.setOnClickListener{Toast.makeText(this,"long tap to reset steps", Toast.LENGTH_SHORT).show()}

        steps_taken.setOnLongClickListener{
            previousTotalSteps = totalSteps
            steps_taken.text = 0.toString()
            saveData()

            true
        }
    }

    private fun saveData(){
        val sharedPreference = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putFloat("key1",previousTotalSteps)
        editor.apply()
    }

    private fun loadData(){
        val sharedPreference = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreference.getFloat("key1",0f)
        previousTotalSteps = savedNumber
    }
}