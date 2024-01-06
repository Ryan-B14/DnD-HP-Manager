package com.example.dndhpsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: DndViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        viewModel = ViewModelProvider(this)[DndViewModel::class.java]
        viewModel.setNavController(navHostFragment)

        viewModel.currentFrag = 1
        setBottomNav()
    }

    private fun setBottomNav() {
        findViewById<Button>(R.id.btn_calculator).setOnClickListener {
            viewModel.navController?.navigate(R.id.action_global_go_calc)
        }
        findViewById<Button>(R.id.btn_rules).setOnClickListener {
            viewModel.navController?.navigate(R.id.action_global_go_chp_fragment)
        }
        findViewById<Button>(R.id.btn_hp_management).setOnClickListener {
            viewModel.navController?.navigate(R.id.action_global_go_hpm)
        }
    }
}