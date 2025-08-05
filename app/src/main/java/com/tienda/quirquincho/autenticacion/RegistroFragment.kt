package com.tienda.quirquincho.autenticacion

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.tienda.quirquincho.R
import com.tienda.quirquincho.data.modelos.Registro
import com.tienda.quirquincho.data.red.RetrofitCliente
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController


class RegistroFragment : Fragment() {
    private lateinit var etNombreUsuario: EditText
    private lateinit var etCorreoElectronico: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etNombreCompleto: EditText
    private lateinit var etNumeroCelular: EditText
    private lateinit var btnRegistrarse: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_registro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        etNombreUsuario = view.findViewById(R.id.et_nombre_usuario)
        etCorreoElectronico = view.findViewById(R.id.et_correo_electronico)
        etContrasena = view.findViewById(R.id.et_contrasena)
        etNombreCompleto = view.findViewById(R.id.et_nombre_completo)
        etNumeroCelular = view.findViewById(R.id.et_numero_celular)
        btnRegistrarse = view.findViewById(R.id.btn_registrar)

        btnRegistrarse.setOnClickListener {
            val datos = Registro(
                nombreUsuario = etNombreUsuario.text.toString(),
                email = etCorreoElectronico.text.toString(),
                contrasena = etContrasena.text.toString(),
                nombreCompleto = etNombreCompleto.text.toString(),
                telefono = etNumeroCelular.text.toString()
            )

            lifecycleScope.launch {
                try {
                    val respuesta = RetrofitCliente.apiServicio.registrarUsuario(datos)
                    if (respuesta.isSuccessful) {
                        val body = respuesta.body()
                        if (body != null && body.esExitosa()) {
                            Toast.makeText(
                                requireContext(),
                                "¡Registro exitoso!",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.loginFragment)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                body?.mensaje ?: "Error desconocido",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error del servidor: ${respuesta.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}
