package com.example.projetokotlin.view.EditarExcluirOrdem

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
import com.example.projetokotlin.databinding.ActivityTelaEmpresaServicoBinding
import com.example.projetokotlin.view.listaServico.ListaServico
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class EditarExcluirOrdem : AppCompatActivity() {
    private lateinit var binding: ActivityTelaEmpresaServicoBinding
    private val db = FirebaseFirestore.getInstance()
    var status:String = ""
    var id:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTelaEmpresaServicoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recuperarDados()
        val email = Firebase.auth.currentUser
        email?.let {
                db.collection("Servico")
                    .whereEqualTo(email.email.toString(), email.email.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents != null) {
                            for (document in documents) {
                                binding.editDescricao.setText(document.getString("descricao"))
                                binding.editPorte.setText(document.getString("tipoProjeto"))
                                binding.editValor.setText(document.getString("valorApagar"))
                            }
                        } else {
                            binding.editDescricao.setText("")
                            binding.editPorte.setText("")
                            binding.editValor.setText("")
                        }
                    }
                    .addOnFailureListener { exception ->
                        msgEdit("Erro inesperado!")
                        FirebaseAuth.getInstance().signOut()
                        val voltarTelaLogin = Intent(this, telaNavegacao::class.java)
                        startActivity(voltarTelaLogin)
                        finish()
                    }

            //sair da sessão
            binding.btnDeslogar.setOnClickListener{
                val voltarTelaLogin = Intent(this, ListaServico:: class.java)
                startActivity(voltarTelaLogin)
                finish()
                limparInput()
            }

            //editar item
            binding.btnEditar.setOnClickListener{
                caixaDeMensagem("Realmente deseja editar esse registro?", "editarRegistro")
            }

            //excluir item
            binding.btnExcluir.setOnClickListener{
                caixaDeMensagem("Realmente deseja excluir este registro?","deletarRegistro")
            }

            binding.btnCancelar.setOnClickListener{
                caixaDeMensagem("Realmente deseja cancelar este registro?","cancelarRegistro")
            }
        }
    }

    //
    private fun msgEdit(msg: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ALERTA!")
            .setMessage(msg)
            .setPositiveButton("OK"){
                    dialog, whitch -> val voltarTelaLogin = Intent(this, telaNavegacao:: class.java)
                startActivity(voltarTelaLogin)
                finish()
            }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    //através da descricao da ordem, deleta o documento
    private fun deletarRegistro(){
        db.collection("Servico").document(id)
            .delete().addOnCompleteListener {
                msgEdit("Registro deletado com sucesso!")
            }
    }

    private fun editarRegistro(){
        db.collection("Servico").document(id)
            .update(
                "descricao",binding.editDescricao.text.toString(),
                "porteSistema",binding.editPorte.text.toString(),
                "valor",binding.editValor.text.toString()
            )
            .addOnSuccessListener{
                msgEdit("Registro editado com sucesso!")
            }
            .addOnFailureListener{
                val exe = it.message
                msgEdit("Não foi possivel editar este registro! $exe")
            }
    }

    private fun cancelarRegistro(){
        if(status != "Aberto"){
            db.collection("Servico").document(id)
                .update("status", "Cancelado")
                .addOnSuccessListener {
                    msgEdit("Serviço Cancelado!")
                }
                .addOnFailureListener{
                    msgEdit("Não foi possivel rejeitar esse servico, tente mais tarde")
                }
        }
    }

    //Recebe as informações da tela de0 listar ordens
    private fun recuperarDados(){
        val descricao = intent.getStringExtra("descricao")
        val valor = intent.getStringExtra("valor")
        val porte = intent.getStringExtra("porte")
        val status = intent.getStringExtra("status")
        val id = intent.getStringExtra("id")
        this.status = status.toString()
        this.id = id.toString()
        //função para colocar dados nos inputs
        setaInput(descricao.toString(),valor.toString(),porte.toString())
    }

    //coloca nos inputs as informações vindas da tela de listar ordens
    private fun setaInput(descricao:String, valor:String, porte:String){
        binding.editDescricao.setText(descricao)
        binding.editPorte.setText(porte)
        binding.editValor.setText(valor)
    }
    private fun limparInput(){
        binding.editDescricao.setText("")
        binding.editPorte.setText("")
        binding.editValor.setText("")
    }

    //Mostra uma mensagem na tela
    private fun caixaDeMensagem(msg:String,funcao:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta de alteração")
            .setMessage(msg)
            .setPositiveButton("Sim") { dialog, whitch ->
                if (funcao == "editarRegistro") {
                    editarRegistro()
                }
                if(funcao == "deletarRegistro"){
                    deletarRegistro()
                }
                if(funcao == "cancelarRegistro"){
                    cancelarRegistro()
                }
            }
            .setNegativeButton("Não") { dialog, whitch ->
                dialog.dismiss()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}