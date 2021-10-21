package com.example.bitirme

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_alarm.*
import java.util.*
import kotlin.collections.ArrayList


class AlarmActivity : AppCompatActivity() {

    private val exampleList = ArrayList<ExampleItem>()

    private val adapter = RecyclerAdapter(exampleList)
    lateinit var alarmService: AlarmService
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        drawerLayout = findViewById(R.id.drawer_layout)
        alarmService = AlarmService(this)
        List.adapter = adapter
        List.layoutManager = LinearLayoutManager(this)
        List.setHasFixedSize(true)

        Alarms.setOnClickListener {

            setAlarm { timeInMillis ->
                alarmService.setRepetitiveAlarm(timeInMillis)
                val newItem = ExampleItem(
                    medName.text.toString()/*ismi*/,
                    DateFormat.format("hh:mm", timeInMillis).toString()//saati
                )
                exampleList.add(0, newItem)
                adapter.notifyItemInserted(0)
            }


        }
    }

    public fun ClickMenu(view: View) {
        MainActivity.SampleClass.OpenDrawer(drawerLayout)
    }

    public fun ClickLogo(view: View) {
        MainActivity.SampleClass.closeDrawer(drawerLayout)
    }

    public fun ClickHome(view: View) {
        val intent = Intent(this, Statistic::class.java)
        startActivity(intent)
        finish()
    }

    public fun ClickDashboard(view: View) {
        recreate()
    }

    public fun ClickQuiz(view: View) {
        val intent = Intent(this, QuestionScreen::class.java)
        startActivity(intent)
        finish()
    }

    public fun ClickLogout(view: View) {
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
        finish()
    }

    private fun setAlarm(callback: (Long) -> Unit) {
        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            TimePickerDialog(
                this@AlarmActivity,
                0,
                { _, hour, minute ->
                    this.set(Calendar.HOUR_OF_DAY, hour)
                    this.set(Calendar.MINUTE, minute)
                    callback(this.timeInMillis)
                },
                this.get(Calendar.HOUR_OF_DAY),
                this.get(Calendar.MINUTE),
                false
            ).show()

        }
    }
}