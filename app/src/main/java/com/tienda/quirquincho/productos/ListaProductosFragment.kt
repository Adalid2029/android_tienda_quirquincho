package com.tienda.quirquincho.productos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tienda.quirquincho.R
import com.tienda.quirquincho.productos.modelos.Producto

class ListaProductosFragment : Fragment() {

    // Variables para los elementos de la UI
    private lateinit var ivCarrito: ImageView
    private lateinit var etBuscarProductos: EditText
    private lateinit var btnAnadirNuevoProducto: Button
    private lateinit var rvProductos: RecyclerView

    // Adaptador y lista de productos
    private lateinit var adaptadorProductos: AdaptadorProductos
    private var listaProductos = mutableListOf<Producto>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lista_productos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        inicializarVistas(view)

        // Configurar RecyclerView
        configurarRecyclerView()

        // Configurar listeners
        configurarListeners()

        // Cargar productos de ejemplo
        cargarProductosEjemplo()
    }

    /**
     * Inicializa todas las vistas del fragment
     */
    private fun inicializarVistas(view: View) {
        ivCarrito = view.findViewById(R.id.iv_carrito)
        etBuscarProductos = view.findViewById(R.id.et_buscar_productos)
        btnAnadirNuevoProducto = view.findViewById(R.id.btn_anadir_nuevo_producto)
        rvProductos = view.findViewById(R.id.rv_productos)
    }

    /**
     * Configura el RecyclerView con su adaptador y layout manager
     */
    private fun configurarRecyclerView() {
        // Crear el adaptador pasando la lista de productos y los listeners
        adaptadorProductos = AdaptadorProductos(
            listaProductos = listaProductos,
            alHacerClickEnProducto = { producto ->
                // TODO: Navegar a detalles del producto
                Toast.makeText(requireContext(), "Producto: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            },
            alHacerClickEnComprar = { producto ->
                // TODO: Agregar al carrito
                Toast.makeText(requireContext(), "Agregado al carrito: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            }
        )

        // Configurar el RecyclerView
        rvProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adaptadorProductos
        }
    }

    /**
     * Configura los listeners de los elementos de la UI
     */
    private fun configurarListeners() {
        // Botón carrito
        ivCarrito.setOnClickListener {
            // TODO: Navegar al carrito
            Toast.makeText(requireContext(), "Carrito de compras", Toast.LENGTH_SHORT).show()
        }

        // Botón añadir nuevo producto
        btnAnadirNuevoProducto.setOnClickListener {
            // TODO: Navegar a agregar producto
            Toast.makeText(requireContext(), "Añadir nuevo producto", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_lista_productos_to_anadir_producto)
        }

        // TODO: Configurar búsqueda en tiempo real
        // etBuscarProductos.addTextChangedListener { ... }
    }

    /**
     * Carga productos de ejemplo para mostrar en la lista
     */
    private fun cargarProductosEjemplo() {
        val productosEjemplo = listOf(
            Producto(
                id = 1,
                nombre = "Yerba Mate Tradicional",
                categoria = "Abarrotes",
                precio = 4.99,
                imagenUrl = "", // Por ahora vacío
                descripcion = "Yerba mate tradicional de excelente calidad"
            ),
            Producto(
                id = 2,
                nombre = "Alfajores de Maicena",
                categoria = "Abarrotes",
                precio = 2.50,
                imagenUrl = "",
                descripcion = "Deliciosos alfajores artesanales"
            ),
            Producto(
                id = 3,
                nombre = "Dulce de Leche Clásico",
                categoria = "Bebidas",
                precio = 3.75,
                imagenUrl = "",
                descripcion = "Dulce de leche cremoso y natural"
            ),
            Producto(
                id = 4,
                nombre = "Galletitas de Agua",
                categoria = "Limpieza del hogar",
                precio = 1.25,
                imagenUrl = "",
                descripcion = "Galletitas crocantes ideales para el mate"
            ),
            Producto(
                id = 5,
                nombre = "Vinagre de Vino Tinto",
                categoria = "Higiene personal",
                precio = 2.99,
                imagenUrl = "",
                descripcion = "Vinagre de vino tinto para ensaladas"
            )
        )

        // Limpiar lista actual y agregar productos de ejemplo
        listaProductos.clear()
        listaProductos.addAll(productosEjemplo)

        // Notificar al adaptador que los datos cambiaron
        adaptadorProductos.notificarCambios()
    }
}
