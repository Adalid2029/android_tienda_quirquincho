package com.tienda.quirquincho.utilidades

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import com.tienda.quirquincho.R

class SelectorFuenteImagen(
    private val fragment: Fragment,
    private val alSeleccionarFuente: (TipoFuente) -> Unit
) {

    enum class TipoFuente {
        GALERIA,
        CAMARA
    }

    fun mostrarOpcionesSeleccion() {
        val opciones = arrayOf(
            "📷 Tomar foto con cámara",
            "🖼️ Seleccionar de galería"
        )

        AlertDialog.Builder(fragment.requireContext())
            .setTitle("Seleccionar imagen")
            .setItems(opciones) { dialog, which ->
                when (which) {
                    0 -> alSeleccionarFuente(TipoFuente.CAMARA)
                    1 -> alSeleccionarFuente(TipoFuente.GALERIA)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}