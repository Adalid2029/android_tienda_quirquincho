package com.tienda.quirquincho.data.modelos

import com.google.gson.annotations.SerializedName

/**
 * Modelo que representa un usuario en el sistema
 */
data class Usuario(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val nombreUsuario: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("nombre_completo")
    val nombreCompleto: String,

    @SerializedName("telefono")
    val telefono: String?,

    @SerializedName("groups")
    val grupos: List<String>,

    @SerializedName("permissions")
    val permisos: List<String>
)
