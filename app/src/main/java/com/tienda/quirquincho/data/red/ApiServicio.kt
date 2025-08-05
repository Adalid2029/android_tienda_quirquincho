package com.tienda.quirquincho.data.red

import com.tienda.quirquincho.data.modelos.DatosLogin
import com.tienda.quirquincho.data.modelos.Registro
import com.tienda.quirquincho.data.modelos.RespuestaApi
import com.tienda.quirquincho.data.modelos.RespuestaCrearProducto
import com.tienda.quirquincho.data.modelos.RespuestaProductos
import com.tienda.quirquincho.data.modelos.SolicitudCrearProducto
import com.tienda.quirquincho.data.modelos.SolicitudLogin
import com.tienda.quirquincho.data.modelos.Usuario
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Path

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
    suspend fun obtenerProductos(
        @Query("pagina") pagina: Int? = 1,
        @Query("limite") limite: Int? = 10,
        @Query("busqueda") busqueda: String? = null,
    ): Response<RespuestaProductos>

    @POST("productos")
    suspend fun crearProducto(
        @Header("Authorization") token: String,
        @Body producto: SolicitudCrearProducto
    ): Response<RespuestaCrearProducto>

    @Multipart
    @POST("productos/{id}/imagen")
    suspend fun subirImagenProducto(
        @Path("id") idProducto: String,
        @Header("Authorization") token: String,
        @Part imagen: MultipartBody.Part
    ): Response<RespuestaApi<Any>>

    @POST("auth/registro")
    suspend fun registrarUsuario(
        @Body datosRegistro : Registro
    ):Response<RespuestaApi<Usuario>>
}