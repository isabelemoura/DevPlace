package com.example.projetokotlin.view.cliente

import android.content.Context
import android.content.Intent
import android.media.Rating
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.projetokotlin.R
import com.example.projetokotlin.view.EditarExcluirOrdem.EditarExcluirOrdem
import com.example.projetokotlin.view.avaliacao.avaliacaoServico
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random


class MyAdapter(private  val OrdemServico:ArrayList<Ordem>, private val context: Context):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private val db = FirebaseFirestore.getInstance()

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var cliente:TextView
        var descricao: TextView
        var comentario: TextView
        var numeroStars: RatingBar
        var btn_avaliar:Button
        var id_item:String
        init {
            cliente = itemView.findViewById(R.id.txtCliente)
            descricao = itemView.findViewById(R.id.editdescricao)
            comentario = itemView.findViewById(R.id.editComentario)
            numeroStars = itemView.findViewById(R.id.ratingbar)
            btn_avaliar = itemView.findViewById(R.id.btn_avaliar)
            id_item = ""
            btn_avaliar.setOnClickListener{

                val email = Firebase.auth.currentUser
                email?.let {
                    if(email.email.toString() != "devplacemobile@gmail.com"){
                        if(email.email.toString() == cliente.text.toString()){
                            val intent = Intent(context, avaliacaoServico::class.java)
                            intent.putExtra("descricao", descricao.text.toString())
                            intent.putExtra("id",id_item)
                            context.startActivity(intent)
                        }else{
                            mensagem("Você não pode avaliar uma ordem que não te pertence!")
                        }
                    }else{
                        mensagem("A empresa não pode avaliar os serviços prestados aos seus usuarios!")

                    }
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lista_avaliacoes_cliente, parent,false)
        return  MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return OrdemServico.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cliente.text = OrdemServico[position].Cliente
        holder.descricao.text = OrdemServico[position].descricao
        holder.comentario.text = OrdemServico[position].comentario
        holder.numeroStars.rating = OrdemServico[position].numeroStars?.toFloat()!!
        holder.id_item = OrdemServico[position].id.toString()
    }

    private fun mensagem(msg:String){

        val builder = AlertDialog.Builder(context)
            builder.setTitle("Alerta!")
                .setMessage(msg)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}