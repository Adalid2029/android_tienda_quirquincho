package com.tienda.quirquincho.data.modelos

import com.google.gson.annotations.SerializedName

/**
 * Modelo para la solicitud de login que se envía a la API
 */
data class SolicitudLogin(
    @SerializedName("login")
    val login: String,

    @SerializedName("password")
    val contrasena: String
)