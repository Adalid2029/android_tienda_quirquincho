package com.tienda.quirquincho.data.red

import com.tienda.quirquincho.data.modelos.DatosLogin
import com.tienda.quirquincho.data.modelos.RespuestaApi
import com.tienda.quirquincho.data.modelos.SolicitudLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Interfaz que define todos los endpoints de la API
 */
interface ApiServicio {

    // === AUTENTICACIÓN ===

    @POST("auth/login")
    suspend fun iniciarSesion(
        @Body solicitud: SolicitudLogin
    ): Response<RespuestaApi<DatosLogin>>

    @POST("auth/logout")
    suspend fun cerrarSesion(
        @Header("Authorization") token: String
    ): Response<RespuestaApi<Any>>

    @GET("auth/perfil")
    suspend fun obtenerPerfil(
        @Header("Authorization") token: String
    ): Response<RespuestaApi<Any>>

    // === PRODUCTOS ===

    @GET("productos")
    suspend fun obtenerProductos(): Response<RespuestaApi<Any>>

    @POST("productos")
    suspend fun crearProducto(
        @Header("Authorization") token: String,
        @Body producto: Any
    ): Response<RespuestaApi<Any>>

    // === CARRITO ===

    @GET("carrito")
    suspend fun obtenerCarrito(
        @Header("Authorization") token: String
    ): Response<RespuestaApi<Any>>

    @POST("carrito/agregar")
    suspend fun agregarAlCarrito(
        @Header("Authorization") token: String,
        @Body item: Any
    ): Response<RespuestaApi<Any>>

    // Más endpoints se agregarán según necesidad...
}