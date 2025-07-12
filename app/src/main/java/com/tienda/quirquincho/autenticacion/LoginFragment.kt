// ========================================
// autenticacion/LoginFragment.kt - ACTUALIZADO CON API REAL
// ========================================
package com.tienda.quirquincho.autenticacion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tienda.quirquincho.R
import com.tienda.quirquincho.data.almacenamiento.TokenManager
import com.tienda.quirquincho.data.modelos.SolicitudLogin
import com.tienda.quirquincho.data.red.RetrofitCliente
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginFragment : Fragment() {

    // Variables para los elementos de la UI
    private lateinit var etNombreUsuario: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var tvRecuperarPassword: TextView

    // Manager para el token JWT
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar token manager
        tokenManager = TokenManager(requireContext())

        // Inicializar vistas
        inicializarVistas(view)

        // Configurar listeners
        configurarListeners()

        // Verificar si ya está logueado
        verificarSesionExistente()
    }

    /**
     * Inicializa todas las vistas del fragment usando findViewById
     */
    private fun inicializarVistas(view: View) {
        etNombreUsuario = view.findViewById(R.id.et_nombre_usuario)
        etContrasena = view.findViewById(R.id.et_contrasena)
        btnIniciarSesion = view.findViewById(R.id.btn_iniciar_sesion)
        tvRecuperarPassword = view.findViewById(R.id.tv_recuperar_password)
    }

    /**
     * Configura los listeners de los elementos de la UI
     */
    private fun configurarListeners() {
        // Botón Iniciar Sesión
        btnIniciarSesion.setOnClickListener {
            iniciarSesionConApi()
        }

        // Link Recuperar Contraseña
        tvRecuperarPassword.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.funcionalidad_desarrollo),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Verifica si el usuario ya tiene una sesión activa
     */
    private fun verificarSesionExistente() {
        lifecycleScope.launch {
            tokenManager.estaLogueado().collect { estaLogueado ->
                if (estaLogueado) {
                    // Usuario ya está logueado, navegar directamente
                    navegarAPrincipal()
                }
            }
        }
    }

    /**
     * Inicia sesión usando la API real
     */
    private fun iniciarSesionConApi() {
        // Obtener datos de los campos
        val usuario = etNombreUsuario.text.toString().trim()
        val contrasena = etContrasena.text.toString().trim()

        // Validar campos básicos
        if (!validarCampos(usuario, contrasena)) {
            return
        }

        // Mostrar estado de carga
        cambiarEstadoCarga(true)

        // Realizar llamada a la API
        lifecycleScope.launch {
            try {
                // Crear solicitud de login
                val solicitudLogin = SolicitudLogin(
                    login = usuario,
                    contrasena = contrasena
                )

                // Llamar a la API
                val respuesta = RetrofitCliente.apiServicio.iniciarSesion(solicitudLogin)

                // Procesar respuesta
                if (respuesta.isSuccessful) {
                    val cuerpoRespuesta = respuesta.body()
                    if (cuerpoRespuesta != null && cuerpoRespuesta.esExitosa()) {
                        // Login exitoso
                        manejarLoginExitoso(cuerpoRespuesta)
                    } else {
                        // Error en la respuesta
                        manejarErrorApi(cuerpoRespuesta)
                    }
                } else {
                    // Error HTTP
                    manejarErrorHttp(respuesta.code())
                }

            } catch (e: IOException) {
                // Error de red
                manejarErrorRed(e)
            } catch (e: HttpException) {
                // Error HTTP
                manejarErrorHttp(e.code())
            } catch (e: Exception) {
                // Error desconocido
                manejarErrorDesconocido(e)
            } finally {
                // Ocultar estado de carga
                cambiarEstadoCarga(false)
            }
        }
    }

    /**
     * Maneja el login exitoso
     */
    private suspend fun manejarLoginExitoso(respuesta: com.tienda.quirquincho.data.modelos.RespuestaApi<com.tienda.quirquincho.data.modelos.DatosLogin>) {
        val datosLogin = respuesta.datos
        if (datosLogin != null) {
            // Guardar datos de sesión
            tokenManager.guardarDatosSesion(
                token = datosLogin.token,
                usuarioId = datosLogin.usuario.id.toString(),
                nombreUsuario = datosLogin.usuario.nombreUsuario,
                email = datosLogin.usuario.email,
                nombreCompleto = datosLogin.usuario.nombreCompleto,
                expiraEn = datosLogin.expiraEn
            )

            // Mostrar mensaje de éxito
            mostrarMensaje("¡Bienvenido ${datosLogin.usuario.nombreCompleto}!")

            // Navegar a pantalla principal
            navegarAPrincipal()
        }
    }

    /**
     * Maneja errores de la API
     */
    private fun manejarErrorApi(respuesta: com.tienda.quirquincho.data.modelos.RespuestaApi<com.tienda.quirquincho.data.modelos.DatosLogin>?) {
        when {
            respuesta == null -> {
                mostrarMensaje("Error desconocido del servidor")
            }
            respuesta.tieneErrores() -> {
                // Errores de validación
                manejarErroresValidacion(respuesta)
            }
            else -> {
                // Mensaje de error general
                mostrarMensaje(respuesta.mensaje)
            }
        }
    }

    /**
     * Maneja errores de validación específicos
     */
    private fun manejarErroresValidacion(respuesta: com.tienda.quirquincho.data.modelos.RespuestaApi<com.tienda.quirquincho.data.modelos.DatosLogin>) {
        // Limpiar errores previos
        etNombreUsuario.error = null
        etContrasena.error = null

        respuesta.errores?.let { errores ->
            // Mostrar errores en campos específicos
            errores["login"]?.let { error ->
                etNombreUsuario.error = error
            }
            errores["password"]?.let { error ->
                etContrasena.error = error
            }

            // Si no hay errores específicos, mostrar el primero
            if (errores.isNotEmpty() && etNombreUsuario.error == null && etContrasena.error == null) {
                mostrarMensaje(errores.values.first())
            }
        }

        // También mostrar el mensaje general si existe
        if (respuesta.mensaje.isNotEmpty()) {
            mostrarMensaje(respuesta.mensaje)
        }
    }

    /**
     * Maneja errores HTTP
     */
    private fun manejarErrorHttp(codigoError: Int) {
        val mensaje = when (codigoError) {
            401 -> "Credenciales incorrectas"
            404 -> "Servicio no encontrado"
            500 -> "Error interno del servidor"
            503 -> "Servicio no disponible"
            else -> "Error de conexión (Código: $codigoError)"
        }
        mostrarMensaje(mensaje)
    }

    /**
     * Maneja errores de red
     */
    private fun manejarErrorRed(error: IOException) {
        val mensaje = when {
            error.message?.contains("timeout") == true -> "Tiempo de espera agotado"
            error.message?.contains("network") == true -> "Sin conexión a internet"
            else -> "Error de conexión. Verifica tu internet"
        }
        mostrarMensaje(mensaje)
    }

    /**
     * Maneja errores desconocidos
     */
    private fun manejarErrorDesconocido(error: Exception) {
        mostrarMensaje("Error inesperado: ${error.message}")
        error.printStackTrace() // Log para debugging
    }

    /**
     * Valida que los campos obligatorios estén llenos
     */
    private fun validarCampos(usuario: String, contrasena: String): Boolean {
        var esValido = true

        // Limpiar errores previos
        etNombreUsuario.error = null
        etContrasena.error = null

        // Validar campo usuario
        if (usuario.isEmpty()) {
            etNombreUsuario.error = "Ingresa tu nombre de usuario"
            esValido = false
        }

        // Validar campo contraseña
        if (contrasena.isEmpty()) {
            etContrasena.error = "Ingresa tu contraseña"
            esValido = false
        }

        return esValido
    }

    /**
     * Cambia el estado visual de carga
     */
    private fun cambiarEstadoCarga(cargando: Boolean) {
        btnIniciarSesion.isEnabled = !cargando
        btnIniciarSesion.text = if (cargando) "Iniciando sesión..." else "Iniciar sesión"

        // Opcional: Cambiar cursor de campos
        etNombreUsuario.isEnabled = !cargando
        etContrasena.isEnabled = !cargando
    }

    /**
     * Muestra un mensaje Toast al usuario
     */
    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    /**
     * Navega a la pantalla principal
     */
    private fun navegarAPrincipal() {
        try {
            findNavController().navigate(R.id.action_login_to_lista_productos)
        } catch (e: Exception) {
            // Handle navigation error
            mostrarMensaje("Error al navegar. Intenta nuevamente.")
        }
    }
}