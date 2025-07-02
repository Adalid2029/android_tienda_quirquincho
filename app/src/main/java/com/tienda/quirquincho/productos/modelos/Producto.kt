package com.tienda.quirquincho.productos.modelos

/**
 * Modelo de datos que representa un producto en la tienda
 */
data class Producto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val imagenUrl: String,
    val descripcion: String,
    val disponible: Boolean = true,
    val cantidad: Int = 0
)