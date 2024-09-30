package com.example.projetokotlin.view.listaServico

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.graphics.component1
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBindings
import com.example.projetokotlin.R
import com.example.projetokotlin.view.EditarExcluirOrdem.EditarExcluirOrdem
import com.example.projetokotlin.view.gestaoOrdem.GestaoDeOrdem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapter(private  var servicoLista:ArrayList<Ordem>, private val context: Context):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private val db = FirebaseFirestore.getInstance()
    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var cliente: TextView
        var descricao: TextView
        var valor: TextView
        var porte: TextView
        var status: TextView
        var btn_edit:Button
        var id_item:String

        init {
            cliente = itemView.findViewById(R.id.editCliente)
            descricao = itemView.findViewById(R.id.editdescricao)
            valor = itemView.findViewById(R.id.editValor)
            porte = itemView.findViewById(R.id.editPorteSys)
            status = itemView.findViewById(R.id.editStatus)
            btn_edit = itemView.findViewById(R.id.btn_editar)
            id_item = ""

            btn_edit.setOnClickListener{
                levarDadosParaTelasGestaoOrdemEditarExcluirOrdem()
            }


            val email = Firebase.auth.currentUser
            email?.let {
                if(email.email == "devplacemobile@gmail.com"){
                    btn_edit.setText("Clique aqui para aceitar/finalizar Ordem!")
                }else{
                    btn_edit.setText("Clique aqui para gerenciar sua ordem!")
                }
            }
        }

        private fun levarDadosParaTelasGestaoOrdemEditarExcluirOrdem(){
            val email = Firebase.auth.currentUser
            email?.let {
                if (email.email == "devplacemobile@gmail.com") {
                    if(status.text.toString() != "Cancelado"){
                        if (status.text.toString() == "Aguardando analise" || status.text.toString() == "Aberto") {
                            val intent = Intent(context, GestaoDeOrdem::class.java)
                            intent.putExtra("descricao", descricao.text.toString())
                            intent.putExtra("valor", valor.text.toString())
                            intent.putExtra("porte", porte.text.toString())
                            intent.putExtra("status",status.text.toString())
                            intent.putExtra("id",id_item)
                            context.startActivity(intent)
                        } else {
                            mensagem("Esse serviço já foi Finalizado!", false)
                        }
                    }else{
                        mensagem("Esse serviço foi cancelado pelo cliente!", false)
                    }
                } else {
                    if(status.text.toString() == "Aberto"){
                        mensagem("Esse serviço já esta sendo executado pela empresa, não pode ser editado ou excluido", false)
                    }else {
                        if (status.text.toString() != "Cancelado") {
                            if (status.text.toString() == "Aguardando analise") {
                                //pega a referencia da atividade ListarServico para ir para a tela de edicao ser ordem
                                val intent = Intent(context, EditarExcluirOrdem::class.java)
                                intent.putExtra("descricao", descricao.text.toString())
                                intent.putExtra("valor", valor.text.toString())
                                intent.putExtra("porte", porte.text.toString())
                                intent.putExtra("status", status.text.toString())
                                intent.putExtra("id", id_item)
                                context.startActivity(intent)
                            }
                        }else {
                            mensagem(
                                "Você não pode editar essa ordem pois, ela já foi finalizada ou Cancelada",
                                false
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_item_cliente, parent,false)
        return  MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return servicoLista.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cliente.text = servicoLista[position].Cliente
        holder.descricao.text = servicoLista[position].descricao
        holder.valor.text = servicoLista[position].valor
        holder.status.text = servicoLista[position].status
        holder.porte.text = servicoLista[position].porteSistema
        holder.id_item = servicoLista[position].id.toString()
    }

    private fun mensagem(msg:String, sucesso:Boolean){
        val builder = AlertDialog.Builder(context)
        if(sucesso == true){
            builder.setTitle("Alerta!")
                .setMessage(msg)
                .setPositiveButton("OK"){
                        dialog, whitch ->
                }
        }else{
            builder.setTitle("Alerta!")
                .setMessage(msg)
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }


}