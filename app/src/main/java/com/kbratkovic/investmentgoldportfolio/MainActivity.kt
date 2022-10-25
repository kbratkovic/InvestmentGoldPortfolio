package com.kbratkovic.investmentgoldportfolio

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.kbratkovic.investmentgoldportfolio.ui.gallery.GalleryFragment
import com.kbratkovic.investmentgoldportfolio.ui.home.HomeFragment
import com.kbratkovic.investmentgoldportfolio.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var binding: ActivityMainBinding

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navigationView: NavigationView

    private val homeFragment = HomeFragment()
    private val settingsFragment = SettingsFragment()
    private val galleryFragment = GalleryFragment()
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, homeFragment)
            addToBackStack(null)
            commit()
        }

        //////////////
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        val fab = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab)
        val switchButton  = findViewById<com.google.android.material.materialswitch.MaterialSwitch>(R.id.switch_dark_theme);

        setSupportActionBar(toolbar)
        //////////////

        drawerNavigationItemSelectedListener();
        bottomNavigationItemSelectedListener();

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)


//        val drawerLayout: DrawerLayout = binding.drawerLayout
//        bottomNavigation = binding.appBarMain.bottomNavigation

//        val navView: NavigationView = binding.navView
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_settings, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).setAnchorView(fab) .show()
        }






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

//        val menuItem = navView.menu.findItem(R.id.switch_theme)
//        val switchDarkTheme = menuItem.actionView?.findViewById<MaterialSwitch>(R.id.switch_dark_theme)
//
//        switchDarkTheme?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
//            if (b) {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            } else {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            }
//        })

//        binding.navView.setNavigationItemSelectedListener {
//            when(it.itemId) {
//                R.id.switch_theme -> {
//                    if (switchDarkTheme != null) {
//                        switchDarkTheme.isChecked = !switchDarkTheme.isChecked
//                    }
//                    true
//                }
//                else -> false
//            }
//        }

    } // onCreate End


    private fun drawerNavigationItemSelectedListener() {
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_settings -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainer, settingsFragment)
                        addToBackStack(null)
                        commit()
                    }
                    true
                }
                R.id.nav_gallery -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainer, galleryFragment)
                        addToBackStack(null)
                        commit()
                    }
                    true
                }
                else -> false
            }
        }
    }


    private fun bottomNavigationItemSelectedListener() {
        bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item: MenuItem ->
            when(item.itemId) {
                R.id.portfolio -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainer, settingsFragment)
                        addToBackStack(null)
                        commit()
                    }

//                    Snackbar.make(bottomNavigation, "Portfolio", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).setAnchorView(fab) .show()
                    true
                }
                R.id.prices -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainer, homeFragment)
                        addToBackStack(null)
                        commit()
                    }

//                    Snackbar.make(bottomNavigation, "Prices", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).setAnchorView(fab) .show()
                    true
                }
                else -> false
            }
        })
    }


//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }




}