package com.example.reinforcement

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
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

        // Configurar los destinos de nivel superior (no muestran botón de retroceso)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_daily_goals,
                R.id.navigation_schedule,
                R.id.navigation_todo,
                R.id.navigation_cigarettes,
                R.id.navigation_points
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Escuchar cambios en el destino de navegación para actualizar datos
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Si navegamos a la sección de puntos, forzar la recarga
            if (destination.id == R.id.navigation_points) {
                val pointsFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
                    ?.childFragmentManager?.fragments?.firstOrNull { it is PointsFragment } as? PointsFragment

                pointsFragment?.refreshData()
            }
        }
    }
}