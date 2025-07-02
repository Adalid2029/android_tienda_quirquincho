// Define el paquete donde está esta clase, útil para organizar el código
package com.tienda.quirquincho.autenticacion

// Importaciones necesarias para trabajar con vistas, fragmentos y widgets UI en Android
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.tienda.quirquincho.R
import androidx.navigation.fragment.findNavController


// Declaración de la clase LoginFragment, que extiende Fragment
// Fragment es una porción modular de la interfaz que puede insertarse en una actividad
class LoginFragment : Fragment() {

    // Declaración de variables que apuntarán a los elementos UI del fragmento
    // 'lateinit var' significa que se inicializan después (en onViewCreated), no en el constructor
    private lateinit var etNombreUsuario: EditText   // Campo de texto para el nombre de usuario
    private lateinit var etContrasena: EditText      // Campo de texto para la contraseña
    private lateinit var btnIniciarSesion: Button    // Botón para iniciar sesión
    private lateinit var tvRecuperarPassword: TextView // Texto para recuperar contraseña (link)

    // Método que crea la vista del fragmento
    // Se infla el layout XML que define la interfaz para este fragmento
    override fun onCreateView(
        inflater: LayoutInflater,      // Objeto que transforma XML en objetos View
        container: ViewGroup?,         // Contenedor padre donde se insertará la vista
        savedInstanceState: Bundle?    // Estado previo guardado, si existe
    ): View? {
        // Devuelve la vista inflada de fragment_login.xml para ser mostrada
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    // Método que se llama cuando la vista ya fue creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa las vistas para poder interactuar con ellas en código
        inicializarVistas(view)

        // Configura los "listeners" para manejar eventos como clicks
        configurarListeners()
    }

    /**
     * Inicializa todas las vistas del fragment usando findViewById
     * findViewById busca el componente en la vista por su ID definido en XML
     */
    private fun inicializarVistas(view: View) {
        etNombreUsuario = view.findViewById(R.id.et_nombre_usuario)   // EditText para usuario
        etContrasena = view.findViewById(R.id.et_contrasena)          // EditText para contraseña
        btnIniciarSesion = view.findViewById(R.id.btn_iniciar_sesion) // Botón de login
        tvRecuperarPassword = view.findViewById(R.id.tv_recuperar_password) // Texto para recuperación
    }

    /**
     * Configura los listeners de los elementos de la UI
     * Un listener es un manejador de eventos, por ejemplo un click
     */
    private fun configurarListeners() {
        // Cuando se presiona el botón de iniciar sesión, ejecuta iniciarSesion()
        btnIniciarSesion.setOnClickListener {
            iniciarSesion()
        }

        // Cuando se presiona el texto de recuperar contraseña, muestra un mensaje Toast
        tvRecuperarPassword.setOnClickListener {
            Toast.makeText(
                requireContext(), // Contexto actual, necesario para mostrar Toast
                getString(R.string.funcionalidad_desarrollo), // Mensaje en strings.xml
                Toast.LENGTH_SHORT // Duración corta para el Toast
            ).show()
        }
    }

    /**
     * Método que maneja el proceso de inicio de sesión
     * Valida datos, simula verificación y muestra mensajes al usuario
     */
    private fun iniciarSesion() {
        // Obtiene texto de los EditText y elimina espacios al inicio y final (trim)
        val usuario = etNombreUsuario.text.toString().trim()
        val contrasena = etContrasena.text.toString().trim()

        // Valida que los campos no estén vacíos, si falla termina el método
        if (!validarCampos(usuario, contrasena)) {
            return
        }

        // Simula validación de credenciales (usuario y contraseña)
        if (validarCredenciales(usuario, contrasena)) {
            // Login exitoso: muestra un Toast con mensaje largo
            Toast.makeText(
                requireContext(),
                getString(R.string.login_exitoso),
                Toast.LENGTH_LONG
            ).show()

            // TODO: En el futuro, navegar a otra pantalla usando Navigation Component
            findNavController().navigate(R.id.action_login_to_lista_productos)

        } else {
            // Login fallido: muestra un Toast con mensaje corto
            Toast.makeText(
                requireContext(),
                getString(R.string.login_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Valida que los campos obligatorios estén llenos y muestra errores si no
     * Devuelve true si todo está bien, false si hay campos vacíos
     */
    private fun validarCampos(usuario: String, contrasena: String): Boolean {
        var esValido = true

        // Si el usuario está vacío, muestra error en EditText y marca no válido
        if (usuario.isEmpty()) {
            etNombreUsuario.error = getString(R.string.error_usuario_vacio)
            esValido = false
        } else {
            etNombreUsuario.error = null // Limpia error si está correcto
        }

        // Igual para contraseña: si está vacía muestra error
        if (contrasena.isEmpty()) {
            etContrasena.error = getString(R.string.error_contrasena_vacia)
            esValido = false
        } else {
            etContrasena.error = null
        }

        return esValido
    }

    /**
     * Simula la validación de credenciales
     * En producción se usaría llamada a API o base de datos para validar usuario y contraseña
     * Aquí solo verifica que usuario sea "admin" y contraseña "123456"
     */
    private fun validarCredenciales(usuario: String, contrasena: String): Boolean {
        return usuario == "admin" && contrasena == "123456"
    }
}
