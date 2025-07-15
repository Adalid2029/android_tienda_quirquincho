package com.tienda.quirquincho.data.modelos

data class SolicitudCrearProducto(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val cantidad_stock: Int,
    val categoria_id: Int
)