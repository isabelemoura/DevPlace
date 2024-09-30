package com.example.projetokotlin.view.inicioEmpresa.novaSenha

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityNovaSenhaEmpresaBinding
import com.example.projetokotlin.databinding.ActivityTelaInicialEmpresaBinding
import com.example.projetokotlin.view.formlogin.FormLogin
import com.example.projetokotlin.view.inicioEmpresa.telaInicialEmpresa
import com.example.projetokotlin.view.listaServico.ListaServico
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class NovaSenhaEmpresa : AppCompatActivity() {
    private lateinit var binding: ActivityNovaSenhaEmpresaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaSenhaEmpresaBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val user = Firebase.auth.currentUser
        if(user?.email != null){
            binding.editEmail.setText(user?.email.toString())
        }

        binding.voltar.setOnClickListener{
            val voltarTelaLogin = Intent(this, FormLogin:: class.java)
            startActivity(voltarTelaLogin)
            finish()
        }

        binding.btnEnviar.setOnClickListener{
            val senha1 = binding.editSenha.text.toString()
            val senha2 = binding.editConfirSenha.text.toString()
            val email = binding.editEmail.text.toString()

            //se os campos estiverem vazios, não deixará criar nova senha
            if (senha1.isEmpty() || senha2.isEmpty() || email.isEmpty()) {
                caixaDeMensagem("Preencha todos os campos", false)
            } else {
                if (binding.editEmail.text != null) {
                    if (senha1 == senha2) {
                        Firebase.auth.sendPasswordResetEmail(email).continueWith { task ->
                            if (task.isCanceled) {
                                caixaDeMensagem(
                                    "Não foi possivel enviar o email, tente novamente mais tarde!",
                                    true
                                )
                            }
                            caixaDeMensagem(
                                "Se o email informado for valido, enviaremos um link para redefinir sua senha!",
                                true
                            )
                        }
                    } else {
                        caixaDeMensagem("As senhas não coincidem!", false)
                        binding.editSenha.setText("")
                        binding.editConfirSenha.setText("")
                    }
                }
            }
        }
    }

    private fun caixaDeMensagem(msg:String, resp:Boolean){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta de alteração")
            .setMessage(msg)
            .setPositiveButton("OK"){dialog, whitch ->
                if(resp){
                    val voltarTelaLogin = Intent(this, FormLogin:: class.java)
                    startActivity(voltarTelaLogin)
                    finish()
                }
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}