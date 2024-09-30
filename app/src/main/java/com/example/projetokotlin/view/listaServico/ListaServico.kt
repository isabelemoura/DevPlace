package com.example.projetokotlin.view.listaServico

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetokotlin.R
import com.example.projetokotlin.databinding.ActivityListaServicoBinding
import com.example.projetokotlin.databinding.ActivityListarClienteBinding
import com.example.projetokotlin.view.inicioEmpresa.telaInicialEmpresa
import com.example.projetokotlin.view.listaServico.uitel.LoadingDialog
import com.example.projetokotlin.view.navegacao.telaNavegacao
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.intellij.lang.annotations.Language
import java.util.Locale

class ListaServico : AppCompatActivity() {
    private lateinit var binding: ActivityListaServicoBinding

    private lateinit var  recyclerView: RecyclerView
    private lateinit var TextViewVoltar: TextView
    private lateinit var searchView: SearchView
    private lateinit var servicoList: ArrayList<Ordem>
    private var filtro:String = ""
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaServicoBinding.inflate(layoutInflater)
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


        recyclerView = findViewById(R.id.recycleview)
        recyclerView.layoutManager =  LinearLayoutManager(this)
        servicoList = arrayListOf()

        db = FirebaseFirestore.getInstance()
        buscar()

        TextViewVoltar = findViewById(R.id.voltar)
        TextViewVoltar.setOnClickListener{
            telaInicial()
        }

        binding.btnPesquisar.setOnClickListener{
            this.filtro = binding.inpPesquisa.text.toString()
            servicoList.clear()
            buscar()
        }

        binding.btnLimpar.setOnClickListener{
            this.filtro = ""
            binding.inpPesquisa.setText("")
            servicoList.clear()
            buscar()
        }

    }

    private fun buscarParaEmpresa(){
        db = FirebaseFirestore.getInstance()
        db.collection("Servico")
            .get().addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val servico: Ordem? = data.toObject(Ordem::class.java)
                        if (servico != null &&  (servico.status.toString() == "Aberto" || servico?.status.toString() == "Aguardando analise")) {
                            if(filtro != ""){
                                if(filtro == servico.descricao || filtro == servico.Cliente || filtro == servico.status || filtro == servico.porteSistema || filtro == servico.valor){
                                    servico?.let { it1 -> servicoList.add(it1) }
                                }
                            }else{
                                servico?.let { it1 -> servicoList.add(it1) }
                            }

                        }
                    }
                    recyclerView.adapter = MyAdapter(servicoList, this)
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    private fun buscarParaCliente(){
        val email = Firebase.auth.currentUser
        email?.let {
        db = FirebaseFirestore.getInstance()
        db.collection("Servico")
            .whereEqualTo("Cliente", email.email.toString())
            .get().addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val servico: Ordem? = data.toObject(Ordem::class.java)
                        if (servico != null) {
                            if(filtro != ""){
                                if(filtro == servico.descricao || filtro == servico.Cliente || filtro == servico.status || filtro == servico.porteSistema || filtro == servico.valor){
                                    servicoList.add(servico)
                                }
                            }else{
                                servicoList.add(servico)
                            }
                        }
                    }
                    recyclerView.adapter = MyAdapter(servicoList, this)
                }else{
                    mensagem("No momento não existe registros para exibir!","ALERTA")
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun buscar(){
        val email = Firebase.auth.currentUser
        email?.let {
            //se o email for empresa, então poderá ver todas as ordens
            if(email.email == "devplacemobile@gmail.com"){
                buscarParaEmpresa()
            }else{
                //se o email foi qualquer outro, só verá as suas proprias ordens
                buscarParaCliente()
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
