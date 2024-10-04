package com.example.projetokotlin.view.avaliacao

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityAvaliacaoServicoBinding
import com.example.projetokotlin.view.cliente.OrdensDoCliente
import com.google.firebase.firestore.FirebaseFirestore

class avaliacaoServico : AppCompatActivity() {
    private lateinit var binding: ActivityAvaliacaoServicoBinding
    private val db = FirebaseFirestore.getInstance()
    var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvaliacaoServicoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Configurações iniciais para a tela
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recuperarDados()
        binding.ratingbar.rating = 1.0F

        // Configura o RatingBar
        binding.ratingbar.setOnRatingBarChangeListener { ratingBar, rating, _ ->
            binding.rbStarts.text = rating.toString()
        }

        // Configura o botão de avaliação
        binding.btnAvaliacao.setOnClickListener {
            db.collection("Servico").document(id)
                .update(
                    mapOf(
                        "comentario" to binding.editComentario.text.toString(),
                        "numeroStars" to binding.ratingbar.rating
                    )
                ).addOnSuccessListener {
                    mensagem("Avaliação feita!", "OK")
                }
                .addOnFailureListener {
                    mensagem("Erro: ${it.message}", "Mensagem de ERRO")
                }
        }

        // Configura o listener de rolagem no EditText
        binding.editComentario.setOnTouchListener { v, event ->
            // Permite a rolagem interna sem interferência do NestedScrollView
            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                v.parent.requestDisallowInterceptTouchEvent(true)
            } else {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        // Torna o EditText somente leitura (sem edição)
        binding.editComentario.isFocusable = true
        binding.editComentario.isFocusableInTouchMode = true
        binding.editComentario.isClickable = true
    }

    private fun setaInput(descricao: String, id: String) {
        binding.editdescricao.text = descricao
        this.id = id
    }

    private fun recuperarDados() {
        val descricao = intent.getStringExtra("descricao")
        val id = intent.getStringExtra("id")

        // Função para colocar dados nos inputs
        setaInput(descricao.orEmpty(), id.orEmpty())
    }

    private fun navegarTelainicial() {
        val intent = Intent(this, OrdensDoCliente::class.java)
        startActivity(intent)
        finish()
    }

    private fun mensagem(msg: String, titulo: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
            .setMessage(msg)
            .setPositiveButton("OK") { _, _ ->
                navegarTelainicial()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}
