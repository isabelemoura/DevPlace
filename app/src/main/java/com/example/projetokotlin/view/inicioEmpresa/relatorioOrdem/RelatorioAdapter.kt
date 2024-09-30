package com.example.projetokotlin.view.inicioEmpresa.relatorioOrdem

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projetokotlin.R
import com.example.projetokotlin.view.listaServico.Ordem

class RelatorioAdapter(private  var servicoLista:ArrayList<Ordem>, private val context: Context):RecyclerView.Adapter<RelatorioAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var cliente: TextView
        var descricao: TextView
        var valor: TextView
        var porte: TextView
        var status: TextView
        var id_item:String

        //inicializa os textView
        init {
            cliente = itemView.findViewById(R.id.editCliente)
            descricao = itemView.findViewById(R.id.editdescricao)
            valor = itemView.findViewById(R.id.editValor)
            porte = itemView.findViewById(R.id.editPorteSys)
            status = itemView.findViewById(R.id.editStatus)
            id_item = ""
        }
    }

    //diz qual card será exibido neste Adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_item_relatorio, parent,false)
        return  MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return servicoLista.size
    }


    //seta cada text view ao seu valor correspondente do banco de dados
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cliente.text = servicoLista[position].Cliente
        holder.descricao.text = servicoLista[position].descricao
        holder.valor.text = servicoLista[position].valor
        holder.status.text = servicoLista[position].status
        holder.porte.text = servicoLista[position].porteSistema
        holder.id_item = servicoLista[position].id.toString()
    }

}