package com.ecumeno

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.ecumeno.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var previousNavItemId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode((application as EcumenoApp).preferencesRepository.nightMode.value)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)

        if ((application as EcumenoApp).preferencesRepository.confession.value == ""){
            navGraph.setStartDestination(R.id.onboarding)
            binding.navView.visibility = View.GONE
            binding.delimiter.visibility = View.GONE
        } else{
            navGraph.setStartDestination(R.id.navigation_calendar)
        }

        navController.graph = navGraph

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
                val currentDest = navController.currentDestination
                val parentGraph = currentDest?.parent

                if (parentGraph?.id == R.id.bible_graph || parentGraph?.id == R.id.prayers_graph){
                    parentGraph.let { graph ->
                        if (currentDest.id != graph.startDestinationId) {
                            if (parentGraph.id == R.id.bible_graph){
                                (application as EcumenoApp).preferencesRepository.clearReadingProgress()
                            }
                            navController.popBackStack(graph.startDestinationId, inclusive = false)
                        }
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