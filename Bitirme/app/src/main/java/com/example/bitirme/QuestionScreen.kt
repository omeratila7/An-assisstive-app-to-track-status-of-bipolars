package com.example.bitirme

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_question_screen.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class QuestionScreen : AppCompatActivity() {
    private var mCurrentPosition: Int = 1
    private var mQuestionsList: ArrayList<Questions>? = null
    private val answers = ArrayList<Int>()
    var database = FirebaseDatabase.getInstance().getReference()
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_screen)
        drawerLayout = findViewById(R.id.drawer_layout)
        mQuestionsList = Constants.getQuestions()
        setQuestions()
        button.setOnClickListener {

            if (mCurrentPosition <= mQuestionsList!!.size - 1) {
                if (Answer.text.toString() != "") {
                    val answer = Answer.text.toString().toInt()
                    if (answer < mQuestionsList!!.get(mCurrentPosition - 1).lowerLimit || answer > mQuestionsList!!.get(
                            mCurrentPosition - 1
                        ).upperLimit
                    ) {
                        Toast.makeText(this, "Please enter a valid value", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        answers.add(Answer.text.toString().toInt())
                        mCurrentPosition++
                        Answer.text.clear()
                        if (mCurrentPosition <= mQuestionsList!!.size)
                            setQuestions()
                    }
                } else
                    Toast.makeText(this, " enter a value", Toast.LENGTH_SHORT).show()
            } else {
                answers.add(Answer.text.toString().toInt())
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                setDatabase()
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setQuestions() {
        val question = mQuestionsList!!.get(mCurrentPosition - 1)

        if (mCurrentPosition == mQuestionsList!!.size)
            button.text = "Finish"
        else
            button.text = "Next"

        Question.text = question.question

    }
    public fun ClickMenu(view: View) {
        MainActivity.SampleClass.OpenDrawer(drawerLayout)
    }
    public fun ClickLogo(view: View){
        MainActivity.SampleClass.closeDrawer(drawerLayout)
    }
    public fun ClickHome(view: View) {
        val intent = Intent(this,Statistic::class.java)
        startActivity(intent)
        finish()
    }
    public fun ClickDashboard(view: View) {
        val intent = Intent(this,AlarmActivity::class.java)
        startActivity(intent)
        finish()
    }
    public fun ClickQuiz(view: View) {
       recreate()
    }
    public fun ClickLogout(view: View) {
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(this,Register::class.java)
        startActivity(intent)
        finish()
    }
    private fun setDatabase() {
        var currentIndex: Int = 0
        val now = Calendar.getInstance()
        val date: Date = now.getTime()
        val myFormatObj = SimpleDateFormat("dd-MM-yy")
        val today_date: String = myFormatObj.format(date)
        val user = FirebaseAuth.getInstance().currentUser?.uid
        Log.e("user::", user.toString())
        val myRef = database.child(user.toString()).child(today_date).child("Questions")
        for (ans in answers.listIterator()) {
            myRef.child(currentIndex.toString()).setValue(answers[currentIndex])
            currentIndex++
        }

    }


}