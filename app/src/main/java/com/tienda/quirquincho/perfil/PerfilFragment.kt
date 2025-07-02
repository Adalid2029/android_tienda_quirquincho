package com.tienda.quirquincho.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tienda.quirquincho.R

class PerfilFragment : Fragment() {

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
        tvMensaje.text = "👤 Mi Perfil\n\nUsuario: admin\nCorreo: admin@tiendaquirquincho.com\n\nPronto tendrás más opciones aquí"
    }
}