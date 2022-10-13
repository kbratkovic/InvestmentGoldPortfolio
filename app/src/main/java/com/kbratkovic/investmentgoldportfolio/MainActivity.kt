package com.kbratkovic.investmentgoldportfolio

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.navigation.NavigationBarView
import com.kbratkovic.investmentgoldportfolio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        bottomNavigation = binding.appBarMain.bottomNavigation

        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).setAnchorView(binding.appBarMain.fab) .show()
        }

        bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            when(item.itemId) {
                R.id.portfolio -> {
                    Snackbar.make(binding.appBarMain.bottomNavigation, "Portfolio", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).setAnchorView(binding.appBarMain.fab) .show()
                    true
                }
                R.id.prices -> {
                    Snackbar.make(binding.appBarMain.bottomNavigation, "Prices", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).setAnchorView(binding.appBarMain.fab) .show()
                    true
                }
                else -> false
            }
        })

//        NavigationView navigationView = binding.navView; // your navigation drawer id
//        MenuItem menuItem = navigationView.getMenu().findItem(R.id.app_bar_switch); // first insialize MenuItem
//        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchButton = (Switch) menuItem.getActionView().findViewById(R.id.darkModeSwitch);
//        // if you are using Switch in your @layout/switch_item then use Switch or use SwitchCompact
//        switchButton.setOnCheckedChangeListener((compoundButton, b) -> {
//            if (b){
//                Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(this, "False", Toast.LENGTH_SHORT).show();
//            }
//        });

        val menuItem = navView.menu.findItem(R.id.switch_theme)
        val switchDarkTheme = menuItem.actionView?.findViewById<MaterialSwitch>(R.id.switch_dark_theme)

        switchDarkTheme?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        })

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.switch_theme -> {
                    if (switchDarkTheme != null) {
                        switchDarkTheme.isChecked = !switchDarkTheme.isChecked
                    }
                    true
                }
                else -> false
            }
        }

    } // onCreate End


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



}