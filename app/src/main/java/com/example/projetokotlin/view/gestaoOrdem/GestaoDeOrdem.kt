package com.example.projetokotlin.view.gestaoOrdem

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityGerarNovaOrdemBinding
import com.example.projetokotlin.databinding.ActivityGestaoDeOrdemBinding
import com.example.projetokotlin.view.inicioEmpresa.telaInicialEmpresa
import com.example.projetokotlin.view.listaServico.ListaServico
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class GestaoDeOrdem : AppCompatActivity() {
    private lateinit var binding: ActivityGestaoDeOrdemBinding
    var status = ""
    var id:String = ""
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGestaoDeOrdemBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        recuperarDados()

        binding.btnAceitar.setOnClickListener {
            if (status != "Aberto") {
                db.collection("Servico").document(id)
                    .update("status", "Aberto")
                    .addOnSuccessListener {
                        mensagem("Serviço aceito com sucesso!", true, "AVISO")
                    }
                    .addOnFailureListener {
                        mensagem(
                            "Não foi possivel aceitar esse servico, tente mais tarde",
                            false,
                            "ERRO"
                        )
                    }
            }else{
                mensagem("Não é possivel aceitar essa ordem, pois já foi aceita",false,"AVISO")
            }
        }

        binding.btnDeslogar.setOnClickListener{
            telaInicial()
        }

        binding.btnFinalizar.setOnClickListener{
            if(status == "Aberto"){
                db.collection("Servico").document(id)
                    .update("status", "Finalizado")
                    .addOnSuccessListener {
                        mensagem("Serviço Finalizado!", true,"AVISO")
                    }
                    .addOnFailureListener{
                        mensagem("Não foi possivel Finalizado esse servico, tente mais tarde", false,"ERRO")
                    }
            }else{
                mensagem("Você precisa aceitar a ordem antes de finalizar!", false,"AVISO")
            }
        }

    }

    private fun mensagem(msg:String, sucesso:Boolean,titulo:String){

        val builder = AlertDialog.Builder(this)
        if(sucesso == true){
            builder.setTitle(titulo)
                .setMessage(msg)
                .setPositiveButton("OK"){
                        dialog, whitch -> telaInicial()
                }
        }else{
            builder.setTitle("Alerta!")
                .setMessage(msg)
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun telaInicial(){
        val email = Firebase.auth.currentUser
        email?.let {
            if(email.email == "devplacemobile@gmail.com"){
                val intent = Intent(this, telaInicialEmpresa::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, telaNavegacao::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun recuperarDados() {
        val descricao = intent.getStringExtra("descricao")
        val valor = intent.getStringExtra("valor")
        val porte = intent.getStringExtra("porte")
        val status = intent.getStringExtra("status")
        val id = intent.getStringExtra("id")
        // Função para colocar dados nos inputs
        setaInput(descricao.toString(), valor.toString(), porte.toString(), status.toString(), id.toString())
    }

    //coloca nos inputs as informações vindas da tela de listar ordens
    private fun setaInput(descricao: String, valor: String, porte: String, status: String, id: String) {
        binding.txtdescricaoServico.setText(descricao)  // Altere editDescricao para txtdescricaoServico
        binding.txtporteSistema.setText(porte)                 // Altere editPorte se necessário
        binding.txtvalor.setText(valor)                   // Altere editValor para txtvalor
        this.status = status
        this.id = id
    }
}