package ru.khozyainov.splashun.presentation

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import ru.khozyainov.splashun.R
import ru.khozyainov.splashun.databinding.ActivityMainBinding
import ru.khozyainov.splashun.utils.setStatusBarTextColor

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setStatusBarTextColor()
        setBottomNavigation()
    }

    private fun setBottomNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment

        binding.bottomNavigation.apply {
            //Во фрагменте использовать setupWithNavController(findNavController())
            setupWithNavController(navHostFragment.navController)
            labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_UNLABELED

            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_selected),
                    intArrayOf(-android.R.attr.state_active)
                ),
                intArrayOf(
                    ContextCompat.getColor(this@MainActivity, R.color.black),
                    ContextCompat.getColor(this@MainActivity, R.color.gray)
                )
            )

            itemTextColor = colorStateList
            itemIconTintList = colorStateList
        }
    }
}