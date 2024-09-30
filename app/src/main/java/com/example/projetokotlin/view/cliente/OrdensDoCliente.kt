package com.example.projetokotlin.view.cliente

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityServicosClienteBinding
import com.example.projetokotlin.databinding.ActivityTelaEmpresaServicoBinding
import com.example.projetokotlin.view.formlogin.FormLogin
import com.example.projetokotlin.view.inicioEmpresa.telaInicialEmpresa
import com.example.projetokotlin.view.listaServico.ListaServico
import com.example.projetokotlin.view.listaServico.uitel.LoadingDialog
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore

class OrdensDoCliente : AppCompatActivity() {
    private lateinit var binding: ActivityServicosClienteBinding
    private lateinit var  recyclerView: RecyclerView
    private lateinit var listaServicoContratado: ArrayList<Ordem>
    private var filtro:String = ""
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicosClienteBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        setContentView(binding.root)
        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed(object:Runnable{
            override  fun run(){
                loading.isDismiss()
            }
        },2000)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnVoltar.setOnClickListener{
            telaInicial()
        }

        recyclerView = findViewById(R.id.recycleview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        listaServicoContratado = arrayListOf()

        pesquisarAvaliacao()

        binding.btnPesquisar.setOnClickListener{
            this.filtro = binding.inpPesquisa.text.toString()
            listaServicoContratado.clear()
            pesquisarAvaliacao()
        }

        binding.btnLimpar.setOnClickListener{
            this.filtro = ""
            binding.inpPesquisa.setText("")
            listaServicoContratado.clear()
            pesquisarAvaliacao()
        }
    }

    private fun pesquisarAvaliacao(){
        db = FirebaseFirestore.getInstance()
        val email = Firebase.auth.currentUser
        email?.let {
            //se o email for empresa, então poderá ver todas as ordens
            db = FirebaseFirestore.getInstance()
            db.collection("Servico")
                .get().addOnSuccessListener {
                    if (!it.isEmpty) {
                        for (data in it.documents) {
                            val servico: Ordem? = data.toObject(Ordem::class.java)
                            if (servico != null) {
                                if(servico.status.toString() == "Finalizado"){
                                    if(filtro != ""){
                                        if(servico.status == filtro || servico.descricao == filtro || servico.Cliente == filtro || servico.comentario == filtro || servico.numeroStars.toString() == filtro){
                                            listaServicoContratado.add(servico)
                                        }
                                    }else{
                                        listaServicoContratado.add(servico)
                                    }
                                }
                            }
                        }
                        recyclerView.adapter = MyAdapter(listaServicoContratado, this)
                    }else{
                        mensagem("No momento, não existe registros para apresentar","ALERTA")
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun mensagem(msg:String, titulo:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(titulo)
            .setMessage(msg)
            .setPositiveButton("OK"){
                    dialog, whitch -> telaInicial()
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
}