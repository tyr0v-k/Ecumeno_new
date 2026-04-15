package com.uvpv521.calendar.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.uvpv521.calendar.R
import com.uvpv521.calendar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var previousNavItemId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)

        AppBarConfiguration(
            setOf(
                R.id.navigation_calendar,
                R.id.bible_graph,
                R.id.prayers_graph,
                R.id.navigation_rosary,
                R.id.navigation_settings
            )
        )

        navView.setupWithNavController(navController)

        navView.setOnNavigationItemSelectedListener { item ->
            if (previousNavItemId == item.itemId) {
                // Находим текущий граф и его стартовый фрагмент через NavDestination
                val currentDest = navController.currentDestination
                val parentGraph = currentDest?.parent as? NavGraph

                parentGraph?.let { graph ->
                    // startDestination доступен через свойство graph.startDestinationId
                    if (currentDest.id != graph.startDestinationId) {
                        getSharedPreferences("app_settings", Context.MODE_PRIVATE).edit().putInt("last_book", -1).apply()
                        getSharedPreferences("app_settings", Context.MODE_PRIVATE).edit().putInt("last_chapter", -1).apply()
                        navController.popBackStack(graph.startDestinationId, inclusive = false)
                    }
                }
                return@setOnNavigationItemSelectedListener true
            }

            previousNavItemId = item.itemId
            NavigationUI.onNavDestinationSelected(item, navController)
            true
        }
    }
}