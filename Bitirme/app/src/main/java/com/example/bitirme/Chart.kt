package com.example.bitirme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.mikephil.charting.data.BarEntry

class Chart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        var StatisticsApp: ArrayList<BarEntry>? = null
        val bahar=intent.getIntExtra("Usages", 0)
        Log.e("valueeee:: ",bahar.toString())


    }
}