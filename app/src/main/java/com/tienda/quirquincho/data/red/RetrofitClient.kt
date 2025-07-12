package com.tienda.quirquincho.data.red

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cliente Retrofit configurado para la API de Tienda Quirquincho
 */
object RetrofitCliente {

    // URL base de tu API
    private const val URL_BASE = "https://api.tienda.hostrend.net/api/v1/"

    // Interceptor para logging de requests (solo en debug)
    private val interceptorLogging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP con configuraciones personalizadas
    private val clienteHttp = OkHttpClient.Builder()
        .addInterceptor(interceptorLogging) // Log de requests/responses
        .addInterceptor { chain ->
            // Interceptor para agregar headers comunes
            val requestOriginal = chain.request()
            val request = requestOriginal.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "TiendaQuirquincho-Android/1.0")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS) // Timeout de conexión
        .readTimeout(30, TimeUnit.SECONDS)    // Timeout de lectura
        .writeTimeout(30, TimeUnit.SECONDS)   // Timeout de escritura
        .build()

    // Instancia de Retrofit configurada
    private val retrofit = Retrofit.Builder()
        .baseUrl(URL_BASE)
        .client(clienteHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Instancia del servicio API
    val apiServicio: ApiServicio = retrofit.create(ApiServicio::class.java)
}