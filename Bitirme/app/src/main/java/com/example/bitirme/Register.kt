package com.example.bitirme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.email
import kotlinx.android.synthetic.main.activity_register.password
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register
        if(FirebaseAuth.getInstance().currentUser!=null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        register.setOnClickListener{
            if(email.text.isNotEmpty() && password.text.isNotEmpty() ){

                yeniUyeKayit(email.text.toString(), password.text.toString())

            }else{
                Toast.makeText(this,"email or password can not empty", Toast.LENGTH_SHORT).show()
            }
        }
        login.setOnClickListener {
            val intent = Intent (this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun yeniUyeKayit(mail: String, sifre: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail,sifre)
            .addOnCompleteListener(object: OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if(p0.isSuccessful){
                        Toast.makeText(this@Register,"Üye kaydedildi:"+ FirebaseAuth.getInstance().currentUser?.email, Toast.LENGTH_SHORT).show()
                        mailGonder()
                        FirebaseAuth.getInstance().signOut()
                    }else{
                        Toast.makeText(this@Register,"Üye kaydedilirken sorun oluştu:"+p0.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }
    private fun mailGonder(){
        var kullanici= FirebaseAuth.getInstance().currentUser
        if (kullanici != null){
            kullanici.sendEmailVerification()
                .addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {
                        if(p0.isSuccessful){
                            Toast.makeText(this@Register,"Mailinizi kontrol edin, mailinizi onaylayın", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@Register,"Mail gönderilirken sorun oluştu "+p0.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }
}