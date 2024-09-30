package com.example.projetokotlin.view.telaAdm

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityAdmBinding
import com.example.projetokotlin.databinding.ActivityTelaNavegacaoBinding
import com.example.projetokotlin.view.cliente.infoCliente.CriarCliente.listarCliente.ListarCliente
import com.example.projetokotlin.view.formlogin.FormLogin
import com.google.firebase.auth.FirebaseAuth

class Adm : AppCompatActivity() {
    private lateinit var binding: ActivityAdmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //desloga do sistema
        binding.btnDeslogar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val voltarTelaLogin = Intent(this, FormLogin::class.java)
            startActivity(voltarTelaLogin)
            finish()
        }
        //chama a tela de listarCliente
        binding.btnListarCliente.setOnClickListener{
            val voltarTelaLogin = Intent(this, ListarCliente::class.java)
            startActivity(voltarTelaLogin)
            finish()
        }
    }
}