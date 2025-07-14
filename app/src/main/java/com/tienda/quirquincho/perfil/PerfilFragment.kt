package com.tienda.quirquincho.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tienda.quirquincho.R
import com.tienda.quirquincho.data.almacenamiento.TokenManager
import kotlinx.coroutines.launch

class PerfilFragment : Fragment() {
    private lateinit var btnCerrarSesion: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Por ahora solo mostramos un mensaje
        val tvMensaje = view.findViewById<TextView>(R.id.tv_mensaje_perfil)
        tvMensaje.text =
            "👤 Mi Perfil\n\nUsuario: admin\nCorreo: admin@tiendaquirquincho.com\n\nPronto tendrás más opciones aquí"

        btnCerrarSesion = view.findViewById(R.id.btn_cerrar_sesion)
        btnCerrarSesion.setOnClickListener {
            lifecycleScope.launch {
                val tokenManager = TokenManager(requireContext())
                tokenManager.limpiarSesion()
                findNavController().navigate(R.id.loginFragment)
            }
        }

    }
}