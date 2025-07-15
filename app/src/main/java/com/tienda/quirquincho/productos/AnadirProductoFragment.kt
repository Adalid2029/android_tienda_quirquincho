package com.tienda.quirquincho.productos

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import com.tienda.quirquincho.R
import com.tienda.quirquincho.data.permisos.GestorPermisos
import com.tienda.quirquincho.utilidades.SelectorImagen
import com.tienda.quirquincho.data.red.RetrofitCliente
import com.tienda.quirquincho.data.modelos.SolicitudCrearProducto
import com.tienda.quirquincho.data.almacenamiento.TokenManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AnadirProductoFragment : Fragment() {

    // Variables de UI
    private lateinit var ivImagenProducto: ImageView
    private lateinit var btnSeleccionarProducto: Button
    private lateinit var etNombreProducto: EditText
    private lateinit var etPrecioProducto: EditText
    private lateinit var etCantidadProducto: EditText
    private lateinit var etDescripcionProducto: EditText
    private lateinit var btnAnadirProducto: Button

    // Chips de categorías
    private lateinit var chipAbarrotes: Chip
    private lateinit var chipLacteos: Chip
    private lateinit var chipBebidas: Chip
    // ... otros chips

    // Gestores
    private lateinit var gestorPermisos: GestorPermisos
    private lateinit var selectorImagen: SelectorImagen
    private lateinit var tokenManager: TokenManager

    // Variables de estado
    private var imagenSeleccionada: Uri? = null

    private var tokenActual: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_anadir_producto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializarGestores()
        lifecycleScope.launch {
            tokenManager.obtenerToken().collect { token ->
                tokenActual = token
            }
        }
        inicializarVistas(view)
        configurarListeners()
    }

    private fun inicializarGestores() {
        gestorPermisos = GestorPermisos(requireActivity())
        tokenManager = TokenManager(requireContext())
        selectorImagen = SelectorImagen(this) { uri ->
            imagenSeleccionada = uri
            ivImagenProducto.setImageURI(uri)
        }
    }

    private fun inicializarVistas(view: View) {
        ivImagenProducto = view.findViewById(R.id.iv_imagen_producto)
        btnSeleccionarProducto = view.findViewById(R.id.btn_seleccionar_producto)
        etNombreProducto = view.findViewById(R.id.et_nombre_producto)
        etPrecioProducto = view.findViewById(R.id.et_precio_producto)
        etCantidadProducto = view.findViewById(R.id.et_cantidad_producto)
        etDescripcionProducto = view.findViewById(R.id.et_descripcion_producto)
        btnAnadirProducto = view.findViewById(R.id.btn_anadir_producto)

        // Inicializar chips
        chipAbarrotes = view.findViewById(R.id.chip_abarrotes)
        chipLacteos = view.findViewById(R.id.chip_lacteos)
        chipBebidas = view.findViewById(R.id.chip_bebidas)
        // ... otros chips
    }

    private fun configurarListeners() {
        android.util.Log.d("AnadirProducto", "Configurando listeners")

        btnSeleccionarProducto.setOnClickListener {
            android.util.Log.d("AnadirProducto", "Click en seleccionar imagen")
            gestorPermisos.verificarYSolicitarPermisoAlmacenamiento { tienePermiso ->
                android.util.Log.d("AnadirProducto", "Resultado permiso: $tienePermiso")
                if (tienePermiso) {
                    selectorImagen.abrirGaleria()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Permiso denegado para acceder a imágenes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnAnadirProducto.setOnClickListener {
            android.util.Log.d("AnadirProducto", "Click en añadir producto")
            if (validarCampos()) {
                android.util.Log.d("AnadirProducto", "Campos válidos, creando producto...")
                crearProducto()
            } else {
                android.util.Log.d("AnadirProducto", "Campos inválidos")
            }
        }
    }


    private fun validarCampos(): Boolean {
        val nombre = etNombreProducto.text.toString().trim()
        val precio = etPrecioProducto.text.toString().trim()
        val cantidad = etCantidadProducto.text.toString().trim()
        val descripcion = etDescripcionProducto.text.toString().trim()

        // Limpiar errores previos
        etNombreProducto.error = null
        etPrecioProducto.error = null
        etCantidadProducto.error = null
        etDescripcionProducto.error = null

        var esValido = true

        when {
            nombre.isEmpty() -> {
                etNombreProducto.error = "El nombre es obligatorio"
                esValido = false
            }

            nombre.length < 3 -> {
                etNombreProducto.error = "El nombre debe tener al menos 3 caracteres"
                esValido = false
            }
        }

        when {
            precio.isEmpty() -> {
                etPrecioProducto.error = "El precio es obligatorio"
                esValido = false
            }

            precio.toDoubleOrNull() == null -> {
                etPrecioProducto.error = "El precio debe ser un número válido"
                esValido = false
            }

            precio.toDouble() <= 0 -> {
                etPrecioProducto.error = "El precio debe ser mayor a 0"
                esValido = false
            }
        }

        when {
            cantidad.isEmpty() -> {
                etCantidadProducto.error = "La cantidad es obligatoria"
                esValido = false
            }

            cantidad.toIntOrNull() == null -> {
                etCantidadProducto.error = "La cantidad debe ser un número válido"
                esValido = false
            }

            cantidad.toInt() <= 0 -> {
                etCantidadProducto.error = "La cantidad debe ser mayor a 0"
                esValido = false
            }
        }

        when {
            descripcion.isEmpty() -> {
                etDescripcionProducto.error = "La descripción es obligatoria"
                esValido = false
            }

            descripcion.length < 10 -> {
                etDescripcionProducto.error =
                    "La descripción debe tener al menos 10 caracteres (tienes ${descripcion.length})"
                esValido = false
            }
        }

        return esValido
    }

    /**
     * Cambia el estado visual de carga
     */
    private fun cambiarEstadoCarga(cargando: Boolean) {
        btnAnadirProducto.isEnabled = !cargando
        btnAnadirProducto.text = if (cargando) "Creando producto..." else "Añadir"

        // Deshabilitar campos durante la carga
        etNombreProducto.isEnabled = !cargando
        etPrecioProducto.isEnabled = !cargando
        etCantidadProducto.isEnabled = !cargando
        etDescripcionProducto.isEnabled = !cargando
        btnSeleccionarProducto.isEnabled = !cargando
    }

    /**
     * Maneja errores específicos del servidor
     */
    private fun manejarErroresServidor(respuesta: com.tienda.quirquincho.data.modelos.RespuestaCrearProducto) {
        android.util.Log.d("AnadirProducto", "Manejando errores del servidor: ${respuesta.errores}")

        // Limpiar errores previos
        etNombreProducto.error = null
        etPrecioProducto.error = null
        etCantidadProducto.error = null
        etDescripcionProducto.error = null

        respuesta.errores?.let { errores ->
            errores["nombre"]?.let { error ->
                etNombreProducto.error = error
            }
            errores["precio"]?.let { error ->
                etPrecioProducto.error = error
            }
            errores["cantidad_stock"]?.let { error ->
                etCantidadProducto.error = error
            }
            errores["descripcion"]?.let { error ->
                etDescripcionProducto.error = error
            }
            errores["categoria_id"]?.let { error ->
                Toast.makeText(requireContext(), "Error en categoría: $error", Toast.LENGTH_LONG)
                    .show()
            }

            // Si hay errores que no están mapeados a campos específicos
            val erroresNoMapeados = errores.filterKeys {
                it !in listOf("nombre", "precio", "cantidad_stock", "descripcion", "categoria_id")
            }
            if (erroresNoMapeados.isNotEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Errores: ${erroresNoMapeados.values.joinToString(", ")}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Mostrar mensaje general
        if (respuesta.mensaje.isNotEmpty()) {
            Toast.makeText(requireContext(), respuesta.mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    private fun crearProducto() {
        android.util.Log.d("AnadirProducto", "Iniciando creación de producto")

        // Activar estado de carga
        cambiarEstadoCarga(true)

        lifecycleScope.launch {
            try {
                android.util.Log.d("AnadirProducto", "Obteniendo token...")

                val token = tokenManager.obtenerTokenSincrono()
                android.util.Log.d("AnadirProducto", "Token obtenido: ${token?.take(20)}...")

                if (token == null) {
                    android.util.Log.e("AnadirProducto", "Token es null")
                    Toast.makeText(requireContext(), "No hay sesión activa", Toast.LENGTH_SHORT)
                        .show()
                    return@launch
                }

                val solicitud = SolicitudCrearProducto(
                    nombre = etNombreProducto.text.toString().trim(),
                    descripcion = etDescripcionProducto.text.toString().trim(),
                    precio = etPrecioProducto.text.toString().toDouble(),
                    cantidad_stock = etCantidadProducto.text.toString().toInt(),
                    categoria_id = obtenerCategoriaSeleccionada()
                )

                android.util.Log.d("AnadirProducto", "Solicitud creada: $solicitud")
                android.util.Log.d("AnadirProducto", "Enviando request a API...")

                val respuesta = RetrofitCliente.apiServicio.crearProducto(token, solicitud)

                android.util.Log.d("AnadirProducto", "Respuesta recibida: ${respuesta.code()}")

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val productoCreado = respuesta.body()!!

                    if (productoCreado.estado == "exito") {
                        android.util.Log.d(
                            "AnadirProducto",
                            "Producto creado exitosamente: ${productoCreado.datos?.id}"
                        )

                        Toast.makeText(requireContext(), productoCreado.mensaje, Toast.LENGTH_SHORT)
                            .show()

                        // Si hay imagen, subirla
                        if (imagenSeleccionada != null && productoCreado.datos != null) {
                            android.util.Log.d("AnadirProducto", "Subiendo imagen...")
                            subirImagen(productoCreado.datos.id, token)
                        } else {
                            android.util.Log.d("AnadirProducto", "Sin imagen, regresando...")
                            regresarAListaProductos()
                        }
                    } else {
                        // Error en la respuesta exitosa (estado = "error")
                        android.util.Log.e(
                            "AnadirProducto",
                            "Error en respuesta exitosa: ${productoCreado.mensaje}"
                        )
                        manejarErroresServidor(productoCreado)
                    }
                } else {
                    // Error HTTP o respuesta nula
                    android.util.Log.e("AnadirProducto", "Error HTTP: ${respuesta.code()}")

                    try {
                        val errorBody = respuesta.errorBody()?.string()
                        android.util.Log.e("AnadirProducto", "Error body: $errorBody")

                        // Intentar parsear el error como RespuestaCrearProducto
                        if (errorBody != null) {
                            val gson = com.google.gson.Gson()
                            val errorResponse = gson.fromJson(
                                errorBody,
                                com.tienda.quirquincho.data.modelos.RespuestaCrearProducto::class.java
                            )
                            manejarErroresServidor(errorResponse)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error desconocido del servidor",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("AnadirProducto", "Error al parsear error: ${e.message}")
                        Toast.makeText(
                            requireContext(),
                            "Error al crear producto (${respuesta.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AnadirProducto", "Exception: ${e.message}", e)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                // Desactivar estado de carga
                cambiarEstadoCarga(false)
            }
        }
    }

    private suspend fun subirImagen(idProducto: String, token: String) {
        try {
            // Cambiar texto del botón durante subida de imagen
            btnAnadirProducto.text = "Subiendo imagen..."

            val inputStream = requireContext().contentResolver.openInputStream(imagenSeleccionada!!)
            val file =
                File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagenPart = MultipartBody.Part.createFormData("imagen", file.name, requestFile)

            val respuesta =
                RetrofitCliente.apiServicio.subirImagenProducto(idProducto, token, imagenPart)

            if (respuesta.isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    "Producto e imagen guardados exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Producto guardado, error al subir imagen",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Limpiar archivo temporal
            file.delete()
            regresarAListaProductos()

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error al subir imagen: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            regresarAListaProductos()
        }
    }

    private fun obtenerCategoriaSeleccionada(): Int {
        return when {
            chipAbarrotes.isChecked -> 1
            chipLacteos.isChecked -> 2
            chipBebidas.isChecked -> 3
            // ... otros casos
            else -> 1 // Por defecto Abarrotes
        }
    }

    private fun regresarAListaProductos() {
        findNavController().navigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            GestorPermisos.CODIGO_PERMISO_ALMACENAMIENTO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectorImagen.abrirGaleria()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Permiso denegado para acceder a imágenes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}