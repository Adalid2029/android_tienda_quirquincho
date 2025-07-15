package com.tienda.quirquincho.utilidades

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class SelectorImagen(
    private val fragment: Fragment,
    private val alSeleccionarImagen: (Uri?) -> Unit
) {

    companion object {
        const val CODIGO_SELECCIONAR_IMAGEN = 1000
    }

    private val selectorImagenLauncher: ActivityResultLauncher<Intent> =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == androidx.appcompat.app.AppCompatActivity.RESULT_OK) {
                val uri = result.data?.data
                alSeleccionarImagen(uri)
            }
        }

    fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        selectorImagenLauncher.launch(intent)
    }

}