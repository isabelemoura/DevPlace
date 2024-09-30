package com.example.projetokotlin.view.avaliacao

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityAvaliacaoServicoBinding
import com.example.projetokotlin.databinding.ActivityFormLoginBinding
import com.example.projetokotlin.view.cliente.OrdensDoCliente
import com.example.projetokotlin.view.formlogin.FormLogin
import com.example.projetokotlin.view.listaServico.ListaServico
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class avaliacaoServico : AppCompatActivity() {
    private lateinit var binding: ActivityAvaliacaoServicoBinding
    private val db = FirebaseFirestore.getInstance()
    var id:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvaliacaoServicoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recuperarDados()
        binding.ratingbar.rating = 1.0F;

        binding.ratingbar.setOnRatingBarChangeListener { ratingBar, rating, fromUser -> rating
            binding.rbStarts.setText("" + rating)
        }
        binding.btnAvaliacao.setOnClickListener {
            db.collection("Servico").document(id)
                .update(
                    mapOf(
                        "comentario" to binding.editComentario.text.toString(),
                        "numeroStars" to binding.ratingbar.rating
                    ),
                ).addOnSuccessListener {
                    mensagem("Avaliação feita!", "OK")
                }
                .addOnFailureListener {
                    mensagem("Erro: ${it.message}", "Mensagem de ERRO")
                }
        }
    }

    private fun setaInput(descricao:String,id:String){
        binding.editdescricao.setText(descricao)
        this.id = id.toString()

    }

    private fun recuperarDados(){
        val descricao = intent.getStringExtra("descricao")
        val id = intent.getStringExtra("id")

        //função para colocar dados nos inputs
        setaInput(descricao.toString(),id.toString())
    }


    private fun navegarTelainicial(){
        val intent = Intent(this, OrdensDoCliente::class.java)
        startActivity(intent)
        finish()
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


}