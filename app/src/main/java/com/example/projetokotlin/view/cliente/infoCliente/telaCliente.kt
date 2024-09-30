package com.example.projetokotlin.view.cliente.infoCliente

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityServicosClienteBinding
import com.example.projetokotlin.databinding.ActivityTelaClienteBinding
import com.example.projetokotlin.view.cliente.Cliente
import com.example.projetokotlin.view.formlogin.FormLogin
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class telaCliente : AppCompatActivity() {
    private lateinit var binding: ActivityTelaClienteBinding
    private val db = FirebaseFirestore.getInstance()
    var idCliente:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaClienteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        getId()
        carregarEmail()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //fazer a lógica para as demais funcionalidades

        binding.btnEditar.setOnClickListener{
            if(binding.editNome.text.toString() == "" || binding.editEmail.text.toString() == "" || binding.editTelefone.text.toString() == ""){
                mensagem("Preencha todos os campos", "AVISO")
            }else{
                if(idCliente != null){
                    db.collection("Cliente").document(idCliente)
                        .update(
                            mapOf(
                                "Nome" to binding.editNome.text.toString(),
                                "Email" to binding.editEmail.text.toString(),
                                "Telefone" to binding.editTelefone.text.toString()
                            )
                        ).addOnSuccessListener {
                            msgDelete("Cliente ${binding.editNome.text.toString()} editado com sucesso!",false)
                        }.addOnFailureListener{
                            mensagem("Não foi possivel editar o cliente","AVISO")
                        }
                }
            }
        }

        binding.btnVoltar.setOnClickListener{
            gotTotelaNavegacao()
        }

        binding.btnExcluir.setOnClickListener{
            deletarCliente()
        }
    }

    private fun deletarCliente(){
        if(idCliente != null){
            db.collection("Cliente").document(idCliente)
                .delete().addOnSuccessListener {
                    val user = Firebase.auth.currentUser!!
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                msgDelete("O Cliente ${binding.editNome.text.toString()} foi removido",true)

                            }
                        }
                }
                .addOnFailureListener{
                    msgDelete("Não foi possivel remover o cliente ${binding.editNome.text.toString()} ${it.message}",true)
                }
        }else{
            Log.w("TAG", "ID $idCliente: ")
        }
    }

    private fun msgDelete(msg: String,tipoMsg:Boolean) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("AVISO")
            .setMessage(msg)
            .setPositiveButton("Ok") { dialog, whitch ->
                //se verdadeiro, pode ser erro ou o cliente foi deletado
                if(tipoMsg){
                    telaLogin()
                }else{
                    //se falço o cliente foi editado, e será direcionado para tela de navegação
                    gotTotelaNavegacao()
                }

            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }



    private fun telaLogin(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, FormLogin::class.java)
        startActivity(intent)
        finish()
    }

    private fun gotTotelaNavegacao(){
        val intent = Intent(this, telaNavegacao::class.java)
        startActivity(intent)
        finish()
    }

    private fun mensagem(msg: String, titulo: String) {
        val builder = AlertDialog.Builder(this)
            builder.setTitle(titulo)
                .setMessage(msg)
                .setPositiveButton("Ok") { dialog, whitch ->
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
                                binding.editNome.setText(cliente.Nome)
                                binding.editTelefone.setText(cliente.Telefone)
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
    private fun carregarEmail(){
        val email = Firebase.auth.currentUser
        email?.let {
            binding.editEmail.setText(email.email)
        }
    }
}