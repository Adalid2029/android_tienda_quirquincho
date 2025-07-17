package com.tienda.quirquincho.utilidades

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SelectorImagen(
    private val fragment: Fragment,
    private val alSeleccionarImagen: (Uri?) -> Unit
) {

    companion object {
        const val CODIGO_SELECCIONAR_IMAGEN = 1000
        const val CODIGO_TOMAR_FOTO = 1001
    }

    // URI temporal para foto de cámara
    private var uriTemporalFoto: Uri? = null

    // Launcher para galería
    private val selectorGaleriaLauncher: ActivityResultLauncher<Intent> =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val uri = result.data?.data
                alSeleccionarImagen(uri)
            }
        }

    // Launcher para cámara
    private val selectorCamaraLauncher: ActivityResultLauncher<Intent> =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                // La imagen se guardó en uriTemporalFoto
                alSeleccionarImagen(uriTemporalFoto)
            }
        }

    /**
     * Abre la galería para seleccionar imagen
     */
    fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        selectorGaleriaLauncher.launch(intent)
    }

    /**
     * Abre la cámara para tomar foto
     */
    fun abrirCamara() {
        try {
            // Crear archivo temporal para la foto
            val archivoFoto = crearArchivoTemporal()

            // Crear URI usando FileProvider
            uriTemporalFoto = FileProvider.getUriForFile(
                fragment.requireContext(),
                "${fragment.requireContext().packageName}.fileprovider",
                archivoFoto
            )

            // Intent para capturar imagen
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, uriTemporalFoto)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            selectorCamaraLauncher.launch(intent)

        } catch (e: Exception) {
            android.util.Log.e("SelectorImagen", "Error al abrir cámara: ${e.message}")
            alSeleccionarImagen(null)
        }
    }

    /**
     * Crea un archivo temporal para guardar la foto
     */
    private fun crearArchivoTemporal(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val nombreArchivo = "JPEG_${timeStamp}_"
        val directorioAlmacenamiento: File = fragment.requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            nombreArchivo,
            ".jpg",
            directorioAlmacenamiento
        )
    }
}