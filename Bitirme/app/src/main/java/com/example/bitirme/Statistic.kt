package com.example.bitirme

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_statistic.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class Statistic : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser
    lateinit var drawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        drawerLayout = findViewById(R.id.drawer_layout)
        readData()

    }
    public fun ClickMenu(view: View) {
        MainActivity.SampleClass.OpenDrawer(drawerLayout)
    }
    public fun ClickLogo(view: View){
        MainActivity.SampleClass.closeDrawer(drawerLayout)
    }
    public fun ClickHome(view: View) {
        recreate()
    }
    public fun ClickDashboard(view: View) {
        val intent = Intent(this,AlarmActivity::class.java)
        startActivity(intent)
        finish()
    }
    public fun ClickQuiz(view: View) {
        val intent = Intent(this,QuestionScreen::class.java)
        startActivity(intent)
        finish()
    }
    public fun ClickLogout(view: View) {
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(this,Register::class.java)
        startActivity(intent)
        finish()
    }
    private fun setBarChartData(arr: ArrayList<String>) {

    }

    private fun readData() {
        var database = FirebaseDatabase.getInstance().reference.child(user!!.uid)
        val UsageData = ArrayList<Entry>()
        val callLogData = ArrayList<Entry>()
        val stepData = ArrayList<Entry>()
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val xvalues = ArrayList<String>()

                for (ctr in 0..6) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DATE,ctr-6)
                    val date :Date =cal.getTime()
                    val myFormatObj = SimpleDateFormat("dd-MM-yy")
                    val formattedDate : String = myFormatObj.format(date)
                    var time:Float =0f

                    for (i in snapshot.child(formattedDate).child("Sensors").child("Usages").children) {
                        val str = i.getValue().toString()
                        val x = str.substring(str.lastIndexOf(" ") + 1).toLong()
                        time += TimeUnit.MILLISECONDS.toMinutes(x)
                    }
                    UsageData.add(Entry(time.toFloat(), ctr))

                    time =0f
                    for (i in snapshot.child(formattedDate).child("Sensors").child("callLog").children) {
                        val str = i.getValue().toString()
                        val x = str.substring(str.lastIndexOf(" ") + 1).toLong()
                        time += TimeUnit.MILLISECONDS.toMinutes(x)
                    }
                    callLogData.add(Entry(time.toFloat(), ctr))

                    var step = 0
                    for (i in snapshot.child(formattedDate).child("Sensors").child("steps").children) {
                        val str = i.getValue().toString()
                        step = str.substring(str.lastIndexOf(" ") + 1).toInt()

                    }
                    stepData.add(Entry(step.toFloat(), ctr))

                    xvalues.add(formattedDate)
                }

                val lineDataSet = LineDataSet(UsageData, "Usages")
                val data = LineData(xvalues, lineDataSet)

                lineChart.data = data
                lineChart.notifyDataSetChanged()
                lineChart.invalidate()

                val lineDataSet1 = LineDataSet(callLogData, "Call logs")
                val data1 = LineData(xvalues, lineDataSet1)

                lineChart1.data = data1
                lineChart1.notifyDataSetChanged()
                lineChart1.invalidate()

                val lineDataSet2 = LineDataSet(stepData, "steps")
                val data2 = LineData(xvalues, lineDataSet2)

                lineChart2.data = data2
                lineChart2.notifyDataSetChanged()
                lineChart2.invalidate()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun createValueEventListener(child: String) {

    }


}






