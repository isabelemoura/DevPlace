package com.example.projetokotlin.view.cliente.infoCliente.CriarCliente.listarCliente

data class ClienteModel(
    val Email:String?=null,
    val Nome:String?=null,
    val Telefone:String?=null,
    val id:String?=null,
    val primeiroAcesso:Boolean?=false,
    val status:String?=null,
)
