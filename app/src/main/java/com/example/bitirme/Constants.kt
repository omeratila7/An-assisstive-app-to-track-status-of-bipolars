package com.example.bitirme

object Constants {

    fun getQuestions(): ArrayList<Questions>{
        val questionList = ArrayList<Questions>()

        val question1=Questions(
            1,
            -3,
            3,
            "Please enter your mood level between -3 and 3"
        )
        questionList.add(question1)
        val question2=Questions(
            2,
            -3,
            3,
            "Please enter your motivation level between -3 and 3"
        )
        questionList.add(question2)
        val question3=Questions(
            3,
            0,
            4,
            "enter attention and concentration level between 0 and 4"
        )
        questionList.add(question3)
        val question4=Questions(
            4,
            0,
            4,
            "enter irritability level between 0 and 4"
        )
        questionList.add(question4)
        val question5=Questions(
            5,
            0,
            4,
            "enter anxiety level between 0 and 4"
        )
        questionList.add(question5)
        val question6=Questions(
            6,
            0,
            4,
            "enter sleep quality level between 0 and 4"
        )
        questionList.add(question6)
        val question7=Questions(
            7,
            0,
            500,
            "enter number of cigarettes you smoke today "
        )
        questionList.add(question7)
        val question8=Questions(
            8,
            0,
            50,
            "enter number of coffees you drink today"
        )
        questionList.add(question8)
        val question9=Questions(
            9,
            0,
            1,
            "have you drunk alcohol today 0 if no 1 if yes"
        )
        questionList.add(question9)
        val question10=Questions(
            10,
            0,
            1,
            "have you consumed any drug today 0 if no 1 if yes"
        )
        questionList.add(question10)
        return questionList
    }





}