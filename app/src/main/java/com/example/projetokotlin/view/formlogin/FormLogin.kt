package com.example.projetokotlin.view.formlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityFormLoginBinding
import com.example.projetokotlin.view.avaliacao.avaliacaoServico
import com.example.projetokotlin.view.cliente.Cliente
import com.example.projetokotlin.view.cliente.infoCliente.CriarCliente.CriarCliente
import com.example.projetokotlin.view.cliente.infoCliente.telaCliente
import com.example.projetokotlin.view.formcadastro.FormCadastro
import com.example.projetokotlin.view.inicioEmpresa.novaSenha.NovaSenhaEmpresa
import com.example.projetokotlin.view.inicioEmpresa.telaInicialEmpresa
import com.example.projetokotlin.view.listaServico.MyAdapter
import com.example.projetokotlin.view.listaServico.Ordem
import com.example.projetokotlin.view.navegacao.telaNavegacao

import com.example.projetokotlin.view.telaAdm.Adm
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.text.Normalizer.Form

class FormLogin : AppCompatActivity() {

    private lateinit var binding: ActivityFormLoginBinding
    private val auth = FirebaseAuth.getInstance()
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btbEntrar.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                mensagem("Preencha todos os campos!!!", "Aviso")
            } else {
                auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { autentic ->
                    if (autentic.isSuccessful) {
                        if (email == "kubicapedro14@gmail.com") {
                            telaAdm()
                        } else {
                            if (email == "devplacemobile@gmail.com") {
                                navegarInicialEmpresa()
                            } else {
                                navegarTelainicial()
                            }
                        }

                    } else {
                        mensagem("E-mail ou senha incorretos!", "Aviso")
                        binding.editEmail.setText("")
                        binding.editSenha.setText("")
                    }
                }
                    .addOnFailureListener {
                        Log.d(
                            "TAG",
                            "Não foi possivel realizar fazer login, tente novamente mais tarde!"
                        )
                    }
            }
        }

        binding.telaCadastrar.setOnClickListener { view ->
            val intent = Intent(this, FormCadastro::class.java)
            startActivity(intent)
        }

        binding.btnRecuperarSenha.setOnClickListener { task ->
            redefinirSenha()
        }
    }

    //redefine a senha do usuario se existir
    private fun redefinirSenha() {
        val intent = Intent(this, NovaSenhaEmpresa::class.java)
        startActivity(intent)
        finish()
    }

    private fun telaAdm(){
        val intent = Intent(this, Adm()::class.java)
        startActivity(intent)
        finish()
    }

    private fun navegarTelainicial() {
        db.collection("Cliente")
            .whereEqualTo("Email", binding.editEmail.text.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val cliente: Cliente? = document.toObject(Cliente::class.java)
                    if (cliente != null) {
                        if(cliente.primeiroAcesso == true){
                            val intent = Intent(this, CriarCliente()::class.java)
                            intent.putExtra("id", cliente.id)
                            startActivity(intent)
                            finish()
                        }else{

                            if(cliente.status != "Ativo"){
                                mensagem("Este cliente está com sua conta desativada, entre em contato com o ADM para mais informações","ALERTA")
                            }else{
                                if(cliente.id != null){
                                    val intent = Intent(this, telaNavegacao()::class.java)
                                    intent.putExtra("id", cliente.id)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    Log.w("TAG", "Erro: cliente id null")
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }

    private fun navegarInicialEmpresa() {
        val intent = Intent(this, telaInicialEmpresa()::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        val usuarioAtual = FirebaseAuth.getInstance().currentUser
        if (usuarioAtual != null) {
            navegarTelainicial()
        }
    }

    private fun mensagem(msg: String, titulo: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
            .setMessage(msg)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}
