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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.tienda.quirquincho.data.red.RetrofitCliente
import com.tienda.quirquincho.productos.modelos.aProducto
import android.text.Editable
import android.text.TextWatcher
import android.widget.ProgressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class ListaProductosFragment : Fragment() {

    // Variables para los elementos de la UI
    private lateinit var ivCarrito: ImageView
    private lateinit var etBuscarProductos: EditText
    private lateinit var btnAnadirNuevoProducto: Button
    private lateinit var rvProductos: RecyclerView

    // Adaptador y lista de productos
    private lateinit var adaptadorProductos: AdaptadorProductos
    private var listaProductos = mutableListOf<Producto>()

    private var trabajoBusqueda: Job? = null
    private var textoBusquedaActual = ""

    private var paginaActual = 1
    private var totalResultados = 0
    private var estaCargando = false
    private var hayMasPaginas = true

    private lateinit var pbCargandoMas: ProgressBar

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

        cargarProductosDesdeApi()
    }

    /**
     * Inicializa todas las vistas del fragment
     */
    private fun inicializarVistas(view: View) {
        ivCarrito = view.findViewById(R.id.iv_carrito)
        etBuscarProductos = view.findViewById(R.id.et_buscar_productos)
        btnAnadirNuevoProducto = view.findViewById(R.id.btn_anadir_nuevo_producto)
        rvProductos = view.findViewById(R.id.rv_productos)

        pbCargandoMas = view.findViewById(R.id.pb_cargando_mas)
    }

    /**
     * Configura el RecyclerView con su adaptador y layout manager
     */
    private fun configurarRecyclerView() {
        adaptadorProductos = AdaptadorProductos(
            listaProductos = listaProductos,
            alHacerClickEnProducto = { producto ->
                Toast.makeText(requireContext(), "Producto: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            },
            alHacerClickEnComprar = { producto ->
                Toast.makeText(requireContext(), "Agregado al carrito: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            }
        )

        val layoutManager = LinearLayoutManager(requireContext())
        rvProductos.apply {
            this.layoutManager = layoutManager
            adapter = adaptadorProductos

            // Agregar listener para detectar scroll al final
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // Solo verificar si se desplaza hacia abajo
                    if (dy > 0) {
                        val itemsVisibles = layoutManager.childCount
                        val totalItems = layoutManager.itemCount
                        val primerItemVisible = layoutManager.findFirstVisibleItemPosition()

                        // Verificar si está cerca del final (últimos 5 items)
                        if (!estaCargando && hayMasPaginas &&
                            (itemsVisibles + primerItemVisible) >= totalItems - 5) {
                            cargarSiguientePagina()
                        }
                    }
                }
            })
        }
    }

    /**
     * Configura los listeners de los elementos de la UI
     */
    private fun configurarListeners() {
        ivCarrito.setOnClickListener {
            Toast.makeText(requireContext(), "Carrito de compras", Toast.LENGTH_SHORT).show()
        }

        btnAnadirNuevoProducto.setOnClickListener {
            Toast.makeText(requireContext(), "Añadir nuevo producto", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_lista_productos_to_anadir_producto)
        }

        etBuscarProductos.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val textoBusqueda = s.toString().trim()

                // Cancelar búsqueda anterior si existe
                trabajoBusqueda?.cancel()

                // Crear nueva búsqueda con delay para evitar muchas llamadas
                trabajoBusqueda = lifecycleScope.launch {
                    delay(500) // Esperar 500ms después de que el usuario deje de escribir
                    buscarProductos(textoBusqueda)
                }
            }
        })
    }
    private fun buscarProductos(textoBusqueda: String) {
        if (textoBusquedaActual != textoBusqueda) {
            paginaActual = 1
            hayMasPaginas = true
            listaProductos.clear()
        }

        textoBusquedaActual = textoBusqueda

        if (estaCargando) return // Evitar llamadas múltiples
        estaCargando = true

        if (paginaActual > 1) {
            pbCargandoMas.visibility = View.VISIBLE
        }

        lifecycleScope.launch {
            try {
                val respuesta = if (textoBusqueda.isEmpty()) {
                    RetrofitCliente.apiServicio.obtenerProductos(pagina = paginaActual)
                } else {
                    RetrofitCliente.apiServicio.obtenerProductos(pagina = paginaActual, busqueda = textoBusqueda)
                }

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val datos = respuesta.body()!!.datos
                    val productosApi = datos.productos
                    val productos = productosApi.map { it.aProducto() }

                    // Actualizar información de paginación
                    totalResultados = datos.paginacion.total_resultados
                    hayMasPaginas = productos.size == datos.paginacion.limite

                    if (paginaActual == 1) {
                        // Primera página: reemplazar lista
                        listaProductos.clear()
                    }

                    // Agregar nuevos productos
                    listaProductos.addAll(productos)
                    adaptadorProductos.notificarCambios()

                    // Mostrar mensaje si no hay resultados en la primera página
                    if (listaProductos.isEmpty() && textoBusqueda.isNotEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "No se encontraron productos para: $textoBusqueda",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al buscar productos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                estaCargando = false
                pbCargandoMas.visibility = View.GONE
            }
        }
    }
    private fun cargarSiguientePagina() {
        if (!hayMasPaginas || estaCargando) return

        paginaActual++
        buscarProductos(textoBusquedaActual)
    }
    private fun cargarProductosDesdeApi() {
        paginaActual = 1
        hayMasPaginas = true
        buscarProductos("")
    }
}
