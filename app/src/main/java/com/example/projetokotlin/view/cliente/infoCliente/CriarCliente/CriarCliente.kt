package com.example.projetokotlin.view.cliente.infoCliente.CriarCliente

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityCriarClienteBinding
import com.example.projetokotlin.databinding.ActivityTelaClienteBinding
import com.example.projetokotlin.view.cliente.Cliente
import com.example.projetokotlin.view.cliente.OrdensDoCliente
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class CriarCliente : AppCompatActivity() {
    private lateinit var binding: ActivityCriarClienteBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    var id:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriarClienteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setaId()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




        binding.btnSalvar.setOnClickListener{
            try {
                val email = binding.editEmail.text.toString()
                if(email != null){
                    db.collection("Cliente").document(id)
                        .update(
                            mapOf(
                                "Nome" to binding.editNome.text.toString(),
                                "Telefone" to binding.editTelefone.text.toString(),
                                "Email" to binding.editEmail.text.toString(),
                                "primeiroAcesso" to false
                            ),
                        ).addOnSuccessListener {
                            mensagem("Cliente atualizado com sucesso!","AVISO!")
                        }
                        .addOnFailureListener{
                            mensagem("Algum erro inesperado aconteceu!","AVISO!")
                        }
                }
            }catch (exe:FirebaseException){
                mensagem("ERRO: $exe","AVISO!")
            }
        }
    }

    private fun mensagem(msg:String, titulo:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
            .setMessage(msg)
            .setPositiveButton("OK"){
                    dialog, whitch -> navegarTelainicial()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun navegarTelainicial(){
        val intent = Intent(this, telaNavegacao::class.java)
        startActivity(intent)
        finish()
    }

    private fun setaId(){
        val id = intent.getStringExtra("id")
        this.id = id.toString()
    }
}