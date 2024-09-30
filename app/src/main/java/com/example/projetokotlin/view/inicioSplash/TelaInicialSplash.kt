package com.example.projetokotlin.view.inicioSplash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.projetokotlin.view.formlogin.FormLogin
import com.example.projetokotlin.R

class TelaInicialSplash : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var percentageText: TextView
    private var progressStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.progressBar2)
        percentageText = findViewById(R.id.percentageText)

        // Simulando carregamento
        Handler().postDelayed(object : Runnable {
            override fun run() {
                if (progressStatus < 100) {
                    progressStatus += 10
                    progressBar.progress = progressStatus
                    percentageText.text = "$progressStatus%"
                    Handler().postDelayed(this, 300) // Atualiza a cada 300ms
                } else {
                    // Inicie a atividade FormLogin
                    val intent = Intent(this@TelaInicialSplash, FormLogin::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }, 300)
    }
}
