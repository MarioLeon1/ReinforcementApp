package com.example.reinforcement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.reinforcement.databinding.ActivityMainBinding
import com.example.reinforcement.ui.points.PointsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Configurar los destinos de nivel superior
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_daily_goals,
                R.id.navigation_schedule,
                R.id.navigation_todo,
                R.id.navigation_cigarettes,
                R.id.navigation_points
            )
        )

        navView.setupWithNavController(navController)

        // Escuchar cambios en el destino de navegación para actualizar datos
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Si navegamos a la sección de puntos, forzar la recarga
            if (destination.id == R.id.navigation_points) {
                refreshPointsFragment()
            }
        }

        // También escuchar clics en el menú de navegación inferior
        navView.setOnItemSelectedListener { menuItem ->
            navController.navigate(menuItem.itemId)

            // Si seleccionamos la pestaña de puntos, forzamos actualización
            if (menuItem.itemId == R.id.navigation_points) {
                // Dar tiempo a que se cargue el fragmento
                binding.root.post {
                    refreshPointsFragment()
                }
            }
            true
        }
    }

    private fun refreshPointsFragment() {
        // Intentar encontrar y actualizar el fragmento de puntos
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        navHostFragment?.childFragmentManager?.fragments?.forEach { fragment ->
            if (fragment is PointsFragment) {
                fragment.refreshData()
            }
        }
    }
}