package com.example.projetokotlin.view.cliente

import android.graphics.drawable.AnimatedImageDrawable

data class Cliente(
    val Nome:String?=null,
    val Email:String?=null,
    val Telefone:String?=null,
    val status:String?=null,
    val primeiroAcesso:Boolean?=null,
    val id:String?=null
)
