package com.tienda.quirquincho.data.modelos

import com.google.gson.annotations.SerializedName

class Registro(
    @SerializedName("username")
    val nombreUsuario: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val contrasena: String,

    @SerializedName("nombre_completo")
    val nombreCompleto: String,

    @SerializedName("telefono")
    val telefono: String?
)