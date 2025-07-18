package com.tienda.quirquincho.productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tienda.quirquincho.R
import com.tienda.quirquincho.productos.modelos.Producto
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class AdaptadorProductos(
    private val listaProductos: List<Producto>,
    private val alHacerClickEnProducto: (Producto) -> Unit,
    private val alHacerClickEnComprar: (Producto) -> Unit
) : RecyclerView.Adapter<AdaptadorProductos.ViewHolderProducto>() {

    /**
     * ViewHolder que contiene las vistas de cada item del producto
     */
    class ViewHolderProducto(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivImagenProducto: ImageView = itemView.findViewById(R.id.iv_imagen_producto)
        val tvCategoria: TextView = itemView.findViewById(R.id.tv_categoria)
        val tvNombreProducto: TextView = itemView.findViewById(R.id.tv_nombre_producto)
        val tvPrecio: TextView = itemView.findViewById(R.id.tv_precio)
        val btnComprar: Button = itemView.findViewById(R.id.btn_comprar)
    }

    /**
     * Crea una nueva vista para un item del producto
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderProducto {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ViewHolderProducto(vista)
    }

    /**
     * Vincula los datos del producto con las vistas del ViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolderProducto, position: Int) {
        val producto = listaProductos[position]

        // Asignar datos a las vistas
        holder.tvNombreProducto.text = producto.nombre
        holder.tvCategoria.text = "Categoría: ${producto.categoria}"
        holder.tvPrecio.text = "Precio: ${String.format("%.2f", producto.precio)} Bs"

        // Cargar imagen real usando Glide
        if (!producto.imagenUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(producto.imagenUrl)
                .placeholder(R.drawable.ic_placeholder_product) // Imagen mientras carga
                .error(R.drawable.ic_launcher_background) // Imagen si falla la carga
                .transition(DrawableTransitionOptions.withCrossFade()) // Animación suave
                .centerCrop()
                .into(holder.ivImagenProducto)
        } else {
            // Si no hay URL, mostrar placeholder
            holder.ivImagenProducto.setImageResource(R.drawable.ic_placeholder_product)
        }

        // Configurar click en todo el item
        holder.itemView.setOnClickListener {
            alHacerClickEnProducto(producto)
        }

        // Configurar click en botón comprar
        holder.btnComprar.setOnClickListener {
            alHacerClickEnComprar(producto)
        }
    }

    /**
     * Retorna el número total de productos en la lista
     */
    override fun getItemCount(): Int = listaProductos.size

    /**
     * Notifica al adaptador que los datos han cambiado
     */
    fun notificarCambios() {
        notifyDataSetChanged()
    }
}
