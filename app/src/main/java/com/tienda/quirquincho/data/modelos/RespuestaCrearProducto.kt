package com.tienda.quirquincho.data.modelos

class RespuestaCrearProducto(
    val estado: String,
    val mensaje: String,
    val datos: ProductoCreado?,
    val errores: Map<String, String>?,
    val timestamp: String
)
data class ProductoCreado(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: String
)