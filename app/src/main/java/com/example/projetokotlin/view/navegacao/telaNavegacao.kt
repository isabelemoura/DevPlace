package com.example.projetokotlin.view.navegacao

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityTelaNavegacaoBinding
import com.example.projetokotlin.view.cliente.Cliente
import com.example.projetokotlin.view.cliente.OrdensDoCliente
import com.example.projetokotlin.view.cliente.infoCliente.CriarCliente.CriarCliente
import com.example.projetokotlin.view.cliente.infoCliente.CriarCliente.listarCliente.ListarCliente
import com.example.projetokotlin.view.cliente.infoCliente.telaCliente
import com.example.projetokotlin.view.formlogin.FormLogin
import com.example.projetokotlin.view.listaServico.ListaServico
import com.example.projetokotlin.view.gerarOrdem.GerarNovaOrdem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class telaNavegacao : AppCompatActivity() {

        private lateinit var binding: ActivityTelaNavegacaoBinding
        private val db = FirebaseFirestore.getInstance()
        var idCliente:String = ""
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityTelaNavegacaoBinding.inflate(layoutInflater)
            enableEdgeToEdge()
            getId()
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

            binding.btbCadastrarServico.setOnClickListener {
                val email = Firebase.auth.currentUser
                email?.let {
                    if (email.email == "devplacemobile@gmail.com") {
                        caixaDeMensagem("O usuario empresa não está autorizado a criar Ordens de serviço.")
                    }else{
                        val intent = Intent(this, GerarNovaOrdem::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
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

            binding.btnGerenciarCliente.setOnClickListener{
                val voltarTelaLogin = Intent(this, telaCliente::class.java)
                if(idCliente==null){
                    Log.w("TAG", "ID cliente está nulo")
                }else{
                    intent.putExtra("id",idCliente)
                    startActivity(voltarTelaLogin)
                    finish()
                }
            }
        }
    private fun caixaDeMensagem(msg:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta!")
            .setMessage(msg)
            .setPositiveButton("Ok"){dialog, whitch ->
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun getId() {
        val email = Firebase.auth.currentUser
        email?.let {
            db.collection("Cliente")
                .whereEqualTo("Email", email.email.toString())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val cliente: Cliente? = document.toObject(Cliente::class.java)
                        if (cliente != null) {
                                if (cliente.id != null) {
                                    this.idCliente = cliente.id
                                    Log.w("TAG", "ID = $idCliente")
                                } else {
                                    Log.w("TAG", "Erro: cliente id null")
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ", exception)
                }
        }
    }
}