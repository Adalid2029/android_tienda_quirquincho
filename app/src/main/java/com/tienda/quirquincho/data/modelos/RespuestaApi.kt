package com.tienda.quirquincho.data.modelos

import com.google.gson.annotations.SerializedName

/**
 * Modelo genérico para todas las respuestas de la API
 */
data class RespuestaApi<T>(
    @SerializedName("estado")
    val estado: String,

    @SerializedName("mensaje")
    val mensaje: String,

    @SerializedName("datos")
    val datos: T? = null,

    @SerializedName("errores")
    val errores: Map<String, String>? = null,

    @SerializedName("timestamp")
    val marcaTiempo: String
) {
    /**
     * Verifica si la respuesta fue exitosa
     */
    fun esExitosa(): Boolean = estado == "exito"

    /**
     * Verifica si hay errores en la respuesta
     */
    fun tieneErrores(): Boolean = errores != null && errores.isNotEmpty()

    /**
     * Obtiene el primer error de validación
     */
    fun obtenerPrimerError(): String? {
        return errores?.values?.firstOrNull()
    }

    /**
     * Obtiene errores específicos por campo
     */
    fun obtenerErrorPorCampo(campo: String): String? {
        return errores?.get(campo)
    }
}
