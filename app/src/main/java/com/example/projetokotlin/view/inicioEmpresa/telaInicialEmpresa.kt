package com.example.projetokotlin.view.inicioEmpresa

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityTelaEmpresaServicoBinding
import com.example.projetokotlin.databinding.ActivityTelaInicialEmpresaBinding
import com.example.projetokotlin.databinding.ActivityTelaNavegacaoBinding
import com.example.projetokotlin.view.cliente.OrdensDoCliente
import com.example.projetokotlin.view.formlogin.FormLogin
import com.example.projetokotlin.view.gerarOrdem.GerarNovaOrdem
import com.example.projetokotlin.view.inicioEmpresa.novaSenha.NovaSenhaEmpresa
import com.example.projetokotlin.view.inicioEmpresa.relatorioOrdem.RelatorioDasOrdens
import com.example.projetokotlin.view.listaServico.ListaServico
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class telaInicialEmpresa : AppCompatActivity() {
    private lateinit var binding: ActivityTelaInicialEmpresaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaInicialEmpresaBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btbServico.setOnClickListener {
            val intent = Intent(this, ListaServico::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnDeslogar.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val voltarTelaLogin = Intent(this, FormLogin::class.java)
            startActivity(voltarTelaLogin)
            finish()
        }

        binding.btbTelaCliente.setOnClickListener{
            val voltarTelaLogin = Intent(this, OrdensDoCliente::class.java)
            startActivity(voltarTelaLogin)
            finish()
        }

        binding.btnRedefinirSenha.setOnClickListener{
            val voltarTelaLogin = Intent(this, NovaSenhaEmpresa::class.java)
            startActivity(voltarTelaLogin)
            finish()
        }

        binding.btnRelatorio.setOnClickListener{
            val voltarTelaLogin = Intent(this, RelatorioDasOrdens::class.java)
            startActivity(voltarTelaLogin)
            finish()
        }
    }
}