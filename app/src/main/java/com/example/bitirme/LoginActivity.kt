package com.example.bitirme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.AuthResult
import  com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_To_project.setOnClickListener {
            if (email.text.isNotEmpty() && password_text.text.isNotEmpty()) {
                progressBarGoster()
                // Firebase kütüphanesinden email ve password parametrelerine tanımladığımız edit textleri Stringe çeviriyoruz.
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    email.text.toString(),
                    password.text.toString()
                )
                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        // Kullanıcı başarılı giriş yaptığında bildirim oluşturuyoruz.
                        override fun onComplete(p0: Task<AuthResult>) {
                            if (p0.isSuccessful) {
                                progressBarGoster()
                                Toast.makeText(this@LoginActivity,"Başarılı Giriş: "+FirebaseAuth.getInstance().currentUser?.email, Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginActivity,MainActivity::class.java)
                                startActivity(intent)
                                finish()
                                //@@@ FirebaseAuth.getInstance().signOut()
                            } else {
                                // Kullanıcı hatalı giriş yaptığında bildirim oluşturuyoruz.
                                progressBarGoster()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Hatalı Giriş: " + p0.exception?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
            } else {
                // Kullanıcı alanı boş bıraktığında bildirim oluşturuyoruz.
                Toast.makeText(
                    this@LoginActivity,
                    "Boş alanları doldurunuz",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }

    private fun progressBarGoster(){
        progressBar2.visibility = View.VISIBLE
    }
    private fun progressBarGizle(){
        progressBar2.visibility = View.INVISIBLE
    }

}