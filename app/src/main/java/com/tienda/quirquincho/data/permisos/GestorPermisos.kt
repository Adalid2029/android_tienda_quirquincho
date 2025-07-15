package com.tienda.quirquincho.data.permisos

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class GestorPermisos(private val activity: Activity) {

    companion object {
        const val CODIGO_PERMISO_ALMACENAMIENTO = 100
        const val CODIGO_PERMISO_CAMARA = 101
    }

    fun verificarYSolicitarPermisoAlmacenamiento(callback: (Boolean) -> Unit) {
        val permiso = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                Manifest.permission.READ_MEDIA_IMAGES
            }
            else -> {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }

        if (ContextCompat.checkSelfPermission(activity, permiso) == PackageManager.PERMISSION_GRANTED) {
            callback(true)
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permiso),
                CODIGO_PERMISO_ALMACENAMIENTO
            )
        }
    }

    fun verificarYSolicitarPermisoCamara(callback: (Boolean) -> Unit) {
        val permiso = Manifest.permission.CAMERA

        if (ContextCompat.checkSelfPermission(activity, permiso) == PackageManager.PERMISSION_GRANTED) {
            callback(true)
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(permiso),
                CODIGO_PERMISO_CAMARA
            )
        }
    }
}
