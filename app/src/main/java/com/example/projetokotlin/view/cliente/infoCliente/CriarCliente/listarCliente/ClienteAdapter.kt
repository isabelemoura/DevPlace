package com.example.projetokotlin.view.cliente.infoCliente.CriarCliente.listarCliente

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView

import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.projetokotlin.R
import com.example.projetokotlin.view.avaliacao.avaliacaoServico
import com.example.projetokotlin.view.cliente.Ordem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

import com.google.firebase.firestore.FirebaseFirestore

class ClienteAdapter(private val ListaCliente:ArrayList<ClienteModel>, private val context: Context):RecyclerView.Adapter<ClienteAdapter.MyViewHolder>(){
    private var db = FirebaseFirestore.getInstance()

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var Email:TextView
        var Nome: TextView
        var Telefone: TextView
        var Status: TextView
        var id:String

        var btnAtivar:Button
        var btnDesativar:Button

        init {
            Email = itemView.findViewById(R.id.Email)
            Nome = itemView.findViewById(R.id.Nome)
            Telefone = itemView.findViewById(R.id.Telefone)
            Status = itemView.findViewById(R.id.Status)
            btnAtivar = itemView.findViewById(R.id.btn_ativar)
            btnDesativar = itemView.findViewById(R.id.btn_desativar)
            id=""

            btnAtivar.setOnClickListener{
                db.collection("Cliente").document(id)
                    .update("status", "Ativo")
                    .addOnSuccessListener {
                        mensagem("Cliente ativado com sucesso!", "ALERTA", context)
                        Status.setText("Ativo")
                    }
                    .addOnFailureListener{
                        mensagem("Não foi possivel ativar este cliente", "ALERTA",context)

                    }
            }

            btnDesativar.setOnClickListener{
                db.collection("Cliente").document(id)
                    .update("status", "desativar")
                    .addOnSuccessListener {
                        mensagem("Cliente desativado!", "ALERTA", context)
                        Status.setText("Desativado")
                    }
                    .addOnFailureListener{
                        mensagem("Não foi possivel desativar este cliente", "ALERTA",context)
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_cliente_item, parent,false)
        return  MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return ListaCliente.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cliente = ListaCliente[position]

        holder.Email.text = cliente.Email
        holder.Nome.text = cliente.Nome
        holder.Telefone.text = cliente.Telefone
        holder.Status.text = cliente.status
        holder.id = cliente.id.toString()

        // Exibe ou oculta os botões com base no status do cliente
        if (cliente.status == "Ativo") {
            holder.btnAtivar.visibility = View.GONE
            holder.btnDesativar.visibility = View.VISIBLE
        } else {
            holder.btnAtivar.visibility = View.VISIBLE
            holder.btnDesativar.visibility = View.GONE
        }
    }


    private fun mensagem(msg:String, titulo:String,context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(titulo)
            .setMessage(msg)
            .setPositiveButton("OK"){
                    dialog, whitch ->
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

    }
}