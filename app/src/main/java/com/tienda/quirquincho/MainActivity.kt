// Define el paquete donde está esta clase, ayuda a organizar el código
package com.tienda.quirquincho

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

// Declara la clase MainActivity que extiende AppCompatActivity
// Esta clase es la actividad principal que se ejecuta* al iniciar la app
class MainActivity : AppCompatActivity() {

    // onCreate es el método que se ejecuta cuando la actividad se crea por primera vez
    // Aquí se inicializan vistas, variables y configuraciones
    override fun onCreate(savedInstanceState: Bundle?) {
        // Llama al método de la superclase para mantener el ciclo de vida adecuado
        super.onCreate(savedInstanceState)

        // Establece el layout XML que definirá la interfaz de usuario para esta actividad
        // R.layout.activity_main es el archivo XML en res/layout/activity_main.xml
        setContentView(R.layout.activity_main)

        // supportFragmentManager es el administrador de fragmentos que controla los fragmentos
        // findFragmentById busca un fragmento dentro del layout por su ID
        // R.id.nav_host_fragment es el ID del FragmentContainerView definido en activity_main.xml
        // Se hace un cast explícito a NavHostFragment porque sabemos que ese fragmento es el controlador de navegación
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // navController es el controlador que maneja la navegación entre fragmentos o destinos dentro de la app
        val navController = navHostFragment.navController

        // Configura la ActionBar (barra superior) para que funcione junto con el NavController
        // Esto habilita cosas como el botón "Atrás" o "Up" en la barra de navegación
        setupActionBarWithNavController(navController)



        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setupWithNavController(navController)

        // Controlar visibilidad del Bottom Navigation
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> {
                    // Ocultar bottom navigation en login
                    bottomNavigation.visibility = View.GONE
                    // Opcional: ocultar ActionBar también
                    // supportActionBar?.hide()
                }
                else -> {
                    // Mostrar bottom navigation en otras pantallas
                    bottomNavigation.visibility = View.VISIBLE
                    // Opcional: mostrar ActionBar también
                    // supportActionBar?.show()
                }
            }
        }
    }

    // Método que se llama cuando el usuario presiona el botón "Up" (flecha atrás) en la ActionBar
    override fun onSupportNavigateUp(): Boolean {
        // Igual que en onCreate, obtenemos el NavHostFragment y su NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Intenta hacer la navegación "hacia arriba" usando NavController
        // Si no puede (por ejemplo, en el destino raíz), delega a la implementación de la superclase
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
