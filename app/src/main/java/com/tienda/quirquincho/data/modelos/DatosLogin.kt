package com.tienda.quirquincho.data.modelos

import com.google.gson.annotations.SerializedName

/**
 * Modelo que contiene los datos de respuesta de login exitoso
 */
data class DatosLogin(
    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val usuario: Usuario,

    @SerializedName("expires_at")
    val expiraEn: String
)
