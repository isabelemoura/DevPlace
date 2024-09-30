package com.example.projetokotlin.view.gerarOrdem

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityGerarNovaOrdemBinding
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.example.projetokotlin.view.EditarExcluirOrdem.EditarExcluirOrdem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class GerarNovaOrdem : AppCompatActivity() {

    private lateinit var binding: ActivityGerarNovaOrdemBinding
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGerarNovaOrdemBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnVoltar.setOnClickListener{
            telaInicial()
        }

        binding.btnGravarDB.setOnClickListener{
            val descricao = binding.txtdescricaoServico.text.toString()
            val porteSistema = binding.txtporteSistema.text.toString()
            val valor = binding.txtvalor.text.toString()
            if(descricao == "" || porteSistema == "" || valor == ""){
                mensagem("Preencha todos os campos", false)
            }else{
                val user = Firebase.auth.currentUser

                user?.let {
                    if(user.email != "devplacemobile@gmail.com" ){
                        gerarOrdem()
                    }else{
                        mensagem("Empresas não podem gerar ordens de serviço", false)
                    }

                }
            }
        }
    }
    private fun navegarTelaEmpresa(){
        val intent = Intent(this,EditarExcluirOrdem::class.java)
        startActivity(intent)
        finish()
    }

    //cadastro de ordem de servico
    private fun gerarOrdem(){
        val descricao = binding.txtdescricaoServico.text.toString()
        val porteSistema = binding.txtporteSistema.text.toString()
        val valor = binding.txtvalor.text.toString()
        val email = Firebase.auth.currentUser
        val id = randomCode()

        email?.let{
            val ordemMap = hashMapOf(
                "Cliente" to it.email.toString(),
                "descricao" to descricao.toString(),
                "porteSistema" to porteSistema.toString(),
                "valor" to "R$"+valor.toString(),
                "status" to "Aguardando analise",
                "comentario" to "",
                "numeroStars" to 0,
                "id" to id
            )

            //atraves do metodo .set, cria uma nova ordem conforme o hashMapOf
            db.collection("Servico").document(id.toString())
                .set(ordemMap).addOnSuccessListener {
                    mensagem("Servico cadastrado com sucesso!", true)
                    limparCampos()
                }.addOnFailureListener{
                    mensagem("Não foi possivel cadastrar este serviço, verifique os campos e tente novamente", true)
                }
        }
    }

    private fun limparCampos(){
        binding.txtdescricaoServico.setText("")
        binding.txtporteSistema.setText("")
        binding.txtvalor.setText("")
    }
    @SuppressLint("SuspiciousIndentation")
    private fun mensagem(msg:String, sucesso:Boolean){
        val builder = AlertDialog.Builder(this)
            if(sucesso == true){
                builder.setTitle("Alerta!")
                .setMessage(msg)
                    .setPositiveButton("OK"){
                            dialog, whitch -> telaInicial()
                    }
            }else{
                builder.setTitle("Alerta!")
                    .setMessage(msg)
                limparCampos()
            }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun telaInicial(){
        val voltarTelaLogin = Intent(this, telaNavegacao:: class.java)
        startActivity(voltarTelaLogin)
        finish()
    }

    private fun randomCode():String{
        val r5 = Random.nextInt(0,2147483647)

        val numero = "$r5"
        return numero
    }
}