package com.example.bitirme

import android.Manifest
import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.app.usage.UsageStatsManager.INTERVAL_DAILY
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Process
import android.provider.CallLog
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var running = false;
    private var totalSteps = 0f;
    private var previousTotalSteps = 0f;

    var fbAuth = FirebaseAuth.getInstance()
    private val requestReadLog = 2;
    var database = FirebaseDatabase.getInstance().getReference()
    lateinit var drawerLayout: DrawerLayout
    lateinit var HomeS: TextView
    lateinit var HomeQ: TextView
    lateinit var HomeA: TextView
    val user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawer_layout)
        HomeS = findViewById(R.id.HomeStatistic)
        HomeQ = findViewById(R.id.HomeQuiz)
        HomeA = findViewById(R.id.HomeAlarm)
        val now = Calendar.getInstance()
        val date: Date = now.getTime()
        val myFormatObj = SimpleDateFormat("dd-MM-yy")
        val formattedDate: String = myFormatObj.format(date)

        loadDataSteps()
        resetSteps()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ),
                requestReadLog
            )
        } else {
            loadData()
        }

        if (checkUsageStatsPermission())
            ShowUsageState()

        if (database.child(user!!.uid).child(formattedDate).child("Question") == null) {
            HomeQ.text =
                " You have not fill quiz yet. Please, pay attention for efficient evaluation. Go to Quiz from Menu!"
        }
        if (database.child(user!!.uid).child(formattedDate).child("Question") != null) {
            HomeQ.text = " You Filled the Quiz today. Thanks!"
        }
        HomeA.text = "Go to Alarm Page from Menu to add alarm for your mediciane. "
        HomeS.text =
            "You can see the statistic your information. Go to Statistic Page to see Callog, Movement and Social Media information"

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN


        var dt = Date()
        dt.hours = 23
        dt.minutes = 59
        dt.seconds = 0

        println(dt)
        val tasknew: TimerTask = object : TimerTask() {
            override fun run() {
                val currentSteps: Int = totalSteps.toInt() - previousTotalSteps.toInt()
                val list = ArrayList<String>()
                list.add("step count " + currentSteps)
                setDatabase("steps", list)

            }
        }
        val timer: Timer = Timer()
        timer.schedule(tasknew, dt)
    }

    public fun ClickMenu(view: View) {
        SampleClass.OpenDrawer(drawerLayout)
    }

    public fun ClickLogo(view: View) {
        SampleClass.closeDrawer(drawerLayout)
    }

    public fun ClickHome(view: View) {
        val intent = Intent(this, Statistic::class.java)
        startActivity(intent)
        finish()
    }

    public fun ClickDashboard(view: View) {
        val intent = Intent(this, AlarmActivity::class.java)
        startActivity(intent)
        finish()
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

    class SampleClass {
        companion object {
            @JvmStatic
            fun OpenDrawer(drawerLayout: DrawerLayout) {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            fun closeDrawer(drawerLayout: DrawerLayout) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }

        }
    }

    override fun onPause() {
        super.onPause()
        SampleClass.closeDrawer(drawerLayout)
    }

    override fun finish() {
        super.finish()
        SampleClass.closeDrawer(drawerLayout)
    }


    private fun loadData() {
        val list = getCallsData(this)
        val calls = ArrayList<String>()
        for (l in list) {
            calls.add(l.toString())
        }


        setDatabase("callLog", calls)
        //    Toast.makeText(this, list.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun getCallsData(context: Context): ArrayList<String> {
        val callLogs = ArrayList<String>()
        val contentUri = CallLog.Calls.CONTENT_URI

        try {
            val today = Date()
            val cursor = context.contentResolver.query(contentUri, null, null, null, null);
            if (cursor != null) {
                val number = cursor.getColumnIndex(CallLog.Calls.NUMBER)
                val time = cursor.getColumnIndex(CallLog.Calls.DURATION)
                val date = cursor.getColumnIndex(CallLog.Calls.DATE)
                val type = cursor.getColumnIndex(CallLog.Calls.TYPE)
                val a = Date();

                if (cursor.moveToLast()) {
                    do {
                        val callType = when (cursor.getInt(type)) {
                            CallLog.Calls.INCOMING_TYPE -> "Incoming"
                            CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                            CallLog.Calls.MISSED_TYPE -> "Missed"
                            CallLog.Calls.REJECTED_TYPE -> "Rejected"
                            else -> "NotDefined"
                        }
                        val phoneNumber = cursor.getString(number)
                        val callDate = cursor.getString(date)
                        val callDayTime = Date(callDate.toLong())
                        if (callDayTime.day != today.day)
                            break
                        val callDuration =
                            TimeUnit.SECONDS.toMillis(cursor.getString(time).toLong()).toString()
                        //  callLogs.add(CallLogs(callDuration, callDayTime, callType, phoneNumber))
                        callLogs.add("type " + callType + " number " + phoneNumber + " duration " + callDuration)
                    } while (cursor.moveToPrevious())
                }
                cursor.close()
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "User Denied Permission", Toast.LENGTH_SHORT).show()
        }
        return callLogs;
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == requestReadLog) loadData()
    }

    public fun setDatabase(str: String, arr: ArrayList<String>) {
        var currentIndex: Int = 0
        val now = Calendar.getInstance()
        val date: Date = now.getTime()
        val myFormatObj = SimpleDateFormat("dd-MM-yy")
        val formattedDate: String = myFormatObj.format(date)
        val user = FirebaseAuth.getInstance().currentUser?.uid
        Log.e("user::", user.toString())
        val myRef = database.child(user.toString()).child(formattedDate).child("Sensors").child(str)
        for (ans in arr.listIterator()) {
            myRef.child(currentIndex.toString()).setValue(arr[currentIndex])
            currentIndex++
        }
    }

    private fun ShowUsageState() {
        var usageStatsManager: UsageStatsManager =
            getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        var call: Calendar = Calendar.getInstance()
        call.set(Calendar.HOUR, 0)
        call.set(Calendar.MINUTE, 0)
        call.set(Calendar.SECOND, 0)

        var queryUsageStats: List<UsageStats> = usageStatsManager.queryUsageStats(
            INTERVAL_DAILY,
            call.timeInMillis,
            currentTimeMillis()
        )
        var state_data = ArrayList<String>()


        for (i in 0..queryUsageStats.size - 1) {
            if (queryUsageStats.get(i).packageName == "com.instagram.android") {
                state_data.add("total time in foreground instagram " + queryUsageStats.get(i).totalTimeInForeground)
            } else if (queryUsageStats.get(i).packageName == "com.google.android.youtube") {
                state_data.add("total time in foreground youtube " + queryUsageStats.get(i).totalTimeInForeground)
            } else if (queryUsageStats.get(i).packageName == "com.facebook.android") {
                state_data.add("total time in foreground facebook " + queryUsageStats.get(i).totalTimeInForeground)
            } else if (queryUsageStats.get(i).packageName == "com.twitter.android") {
                state_data.add("total time in foreground twitter " + queryUsageStats.get(i).totalTimeInForeground)
            } else if (queryUsageStats.get(i).packageName == "com.whatsapp") {
                state_data.add("total time in foreground whatsapp " + queryUsageStats.get(i).totalTimeInForeground)
            }
        }
        setDatabase("Usages", state_data);
    }

    private fun checkUsageStatsPermission(): Boolean {
        var appOpsManager: AppOpsManager? = null
        var mode: Int = 0
        appOpsManager = getSystemService(Context.APP_OPS_SERVICE)!! as AppOpsManager
        mode = appOpsManager.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        println(mode)
        return mode == MODE_ALLOWED

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            totalSteps = event!!.values[0]
            val currentSteps: Int = totalSteps.toInt() - previousTotalSteps.toInt()
            steps.text = ("$currentSteps")
        }
    }

    override fun onResume() {
        super.onResume()
        running = true;
        val stepSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(this, "no sensor detected", Toast.LENGTH_SHORT).show()
        } else {
            val x = sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)

        }
    }

    private fun resetSteps() {
        previousTotalSteps = totalSteps
        saveDataSteps()
        true
    }

    private fun saveDataSteps() {
        val sharedPreference = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.apply()
    }

    private fun loadDataSteps() {
        val sharedPreference = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreference.getFloat("key1", 0f)
        previousTotalSteps = savedNumber
    }

}