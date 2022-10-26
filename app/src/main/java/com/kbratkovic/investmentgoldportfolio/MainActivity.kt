package com.kbratkovic.investmentgoldportfolio

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.kbratkovic.investmentgoldportfolio.ui.gallery.GalleryFragment
import com.kbratkovic.investmentgoldportfolio.ui.portfolio.PortfolioFragment
import com.kbratkovic.investmentgoldportfolio.ui.settings.SettingsFragment
import com.kbratkovic.investmentgoldportfolio.ui.addNewItem.AddNewItemFragment

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navigationView: NavigationView

    private lateinit var toolbar: Toolbar
    private lateinit var fab: FloatingActionButton
    private var materialSwitch: MaterialSwitch? = null

    private val portfolioFragment = PortfolioFragment()
    private val settingsFragment = SettingsFragment()
    private val galleryFragment = GalleryFragment()
    private val addNewItemFragment = AddNewItemFragment()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, portfolioFragment)
            addToBackStack(null)
            commit()
        }



        initializeViews()
        setSupportActionBar(toolbar)
        drawerNavigationToggle()
        drawerNavigationItemSelectedListener()
        bottomNavigationItemSelectedListener()
        manageAddNewItemFab()
        manageDarkModeSwitch()

        toolbar.title = getString(R.string.menu_portfolio)

    } // onCreate End


    private fun initializeViews() {
        toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        fab = findViewById(R.id.fab)

        val menuItem = navigationView.menu.findItem(R.id.switch_theme)
        materialSwitch = menuItem.actionView?.findViewById(R.id.switch_dark_theme)
    }


    private fun drawerNavigationToggle() {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }


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
                        replace(R.id.fragmentContainer, portfolioFragment)
                        addToBackStack(null)
                        commit()

                        setToolbarTitle(portfolioFragment)
                    }

//                    Snackbar.make(bottomNavigation, "Portfolio", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).setAnchorView(fab) .show()
                    true
                }
                R.id.prices -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainer, portfolioFragment)
                        addToBackStack(null)
                        commit()
                    }

                    setToolbarTitle(portfolioFragment)


//                    Snackbar.make(bottomNavigation, "Prices", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).setAnchorView(fab) .show()

                    true
                }
                else -> false
            }
        })
    }


    private fun manageAddNewItemFab() {
        fab.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer, addNewItemFragment)
                addToBackStack(null)
                commit()
            }

            setToolbarTitle(addNewItemFragment)

//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).setAnchorView(fab) .show()
        }
    }


    private fun manageDarkModeSwitch() {
        materialSwitch?.setOnCheckedChangeListener{ _, b ->
            if (b) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }


    private fun setToolbarTitle(fragment: Fragment) {
        when(fragment) {
            portfolioFragment -> toolbar.title = getString(R.string.menu_portfolio)
            settingsFragment -> toolbar.title = getString(R.string.menu_settings)
            addNewItemFragment -> toolbar.title = getString(R.string.menu_add_new_item)
            else -> toolbar.title = getString(R.string.app_name)
        }
    }


}