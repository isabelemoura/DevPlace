package com.example.projetokotlin.view.formcadastro

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityFormCadastroBinding
import com.example.projetokotlin.view.formlogin.FormLogin
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class FormCadastro : AppCompatActivity() {
    private lateinit var binding: ActivityFormCadastroBinding
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormCadastroBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btbCadastrar.setOnClickListener{
            cadastrarUser()
        }
    }

    //essa função realiza o cadastro do novo usuario no firebase auth
    private fun cadastrarUser(){
        val email = binding.editEmail.text.toString()
        val senha = binding.editSenha.text.toString()
        val id = randomCode()
        if (email.isEmpty() || senha.isEmpty()){
          mensagem("Preencha todos os campos", "AVISO!",false)
        }else{
            auth.createUserWithEmailAndPassword(email,senha).addOnCompleteListener{cadastro ->
                if(cadastro.isSuccessful){
                    val cliente = hashMapOf(
                        "Nome" to "",
                        "Email" to email,
                        "Telefone" to "",
                        "id" to id,
                        "status" to "Ativo",
                        "primeiroAcesso" to true
                    )

                    db.collection("Cliente").document(id)
                        .set(cliente)

                    mensagem("Usuario cadastrado com sucesso!", "Aviso",true)
                    binding.editEmail.setText("")
                    binding.editSenha.setText("")
                }
            }.addOnFailureListener{exe ->
                val mensagemErro = when(exe){
                    is FirebaseAuthWeakPasswordException -> "Digite uma senha com 8 caracteres"
                    is FirebaseAuthInvalidCredentialsException -> "Digite um email valido"
                    is FirebaseAuthUserCollisionException -> "Essa conta já foi cadastrada"
                    is FirebaseNetworkException -> "Sem conexao com a internet!"
                    else -> "Erro ao cadastrar usuario";
                }
                mensagem("Erro: $mensagemErro","Alerta!",false)
            }
        }
    }
    private fun navegarTelainicial(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, FormLogin::class.java)
        startActivity(intent)
        finish()
    }

    private fun mensagem(msg:String, titulo:String, resp:Boolean){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
            .setMessage(msg)
            .setPositiveButton("OK"){
                    dialog, whitch ->
                if(resp){
                    navegarTelainicial()
                }
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun randomCode():String{
        val r5 = Random.nextInt(0,2147483647)

        val numero = "$r5"
        return numero
    }

}