package com.example.projetokotlin.view.cliente

data class Ordem(
    val Cliente:String?=null,
    val descricao:String?=null,
    val porteSistema:String?=null,
    val valor:String?=null,
    val status:String?=null,
    val comentario:String?=null,
    val numeroStars:Float?=null,
    val id:String?=null
)