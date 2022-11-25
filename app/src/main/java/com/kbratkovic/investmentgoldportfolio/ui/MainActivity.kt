package com.kbratkovic.investmentgoldportfolio.ui


import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.navigation.NavigationView
import com.kbratkovic.investmentgoldportfolio.BuildConfig
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.ViewModelProviderFactory
import com.kbratkovic.investmentgoldportfolio.database.AppDatabase
import com.kbratkovic.investmentgoldportfolio.repository.Repository
import com.kbratkovic.investmentgoldportfolio.ui.fragments.*
import timber.log.Timber
import timber.log.Timber.*


class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mBottomNavigation: BottomNavigationView
    private lateinit var mNavigationView: NavigationView

    private lateinit var mToolbar: Toolbar
    private var materialSwitch: MaterialSwitch? = null

    private val mPortfolioFragment = PortfolioFragment()
    private val mSettingsFragment = SettingsFragment()
    private val mGalleryFragment = GalleryFragment()
    private val mAddNewItemFragment = AddNewItemFragment()
    private val mApiPricesFragment = ApiPricesFragment()

    private lateinit var mMainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeTimberLogging()
        initializeViewModel()
        startOnDataChangeListener()
        initializeViews()
        setDrawerNavigationToggle()
        drawerNavigationItemSelectedListener()
//        bottomNavigationItemSelectedListener()
        handleDarkModeSwitch()
        setBottomNavigation()

        mToolbar.title = getString(R.string.menu_portfolio)
    } // onCreate End


    private fun startOnDataChangeListener() {
        mMainViewModel.setOnDataChangeListener(object: MainViewModel.OnDataChangeListener {
            override fun onDataChanged(message: String?) {
                if (message.equals(getString(R.string.network_error))) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun initializeTimberLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }


    private fun setBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        setupWithNavController(mBottomNavigation, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.portfolio) {
                setToolbarTitle(mPortfolioFragment)
            }
            if(destination.id == R.id.add_new_item) {
                setToolbarTitle(mAddNewItemFragment)
            }
            if(destination.id == R.id.prices) {
                setToolbarTitle(mApiPricesFragment)
            }
        }
    }


    private fun initializeViewModel() {
        val repository = Repository(AppDatabase.getDatabase(this))
        val viewModelProviderFactory = ViewModelProviderFactory(repository, application)
        mMainViewModel = ViewModelProvider(this, viewModelProviderFactory)[MainViewModel::class.java]
    }


    private fun initializeViews() {
        mToolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        mNavigationView = findViewById(R.id.drawer_navigation_view)
        mBottomNavigation = findViewById(R.id.bottom_navigation)
//        fab = findViewById(R.id.fab)

        val menuItem = mNavigationView.menu.findItem(R.id.switch_theme)
        materialSwitch = menuItem.actionView?.findViewById(R.id.switch_dark_theme)

        setSupportActionBar(mToolbar)
    }


    private fun setDrawerNavigationToggle() {
        val toggle = ActionBarDrawerToggle(
            this, mDrawerLayout, mToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        mDrawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }


    private fun drawerNavigationItemSelectedListener() {
        mNavigationView.setNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.nav_settings -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container, mSettingsFragment)
                        addToBackStack(null)
                        commit()
                        setToolbarTitle(mSettingsFragment)
                    }
                    closeDrawerLayout()
                    true
                }

                R.id.nav_gallery -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragment_container, mGalleryFragment)
                        addToBackStack(null)
                        commit()
                        setToolbarTitle(mGalleryFragment)
                    }
                    closeDrawerLayout()
                    true
                }
                else -> false
            }
        }
    } // drawerNavigationItemSelectedListener


    private fun bottomNavigationItemSelectedListener() {
        mBottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {

                R.id.portfolio -> {
                    setToolbarTitle(mPortfolioFragment)
//                    supportFragmentManager.beginTransaction().apply {
//                        replace(R.id.fragment_container, mPortfolioFragment)
//                        addToBackStack(null)
//                        commit()
//                    }
//                    setToolbarTitle(mPortfolioFragment)
                    true
                }

                R.id.add_new_item -> {
                    setToolbarTitle(mAddNewItemFragment)

//                    supportFragmentManager.beginTransaction().apply {
//                        replace(R.id.fragment_container, mAddNewItemFragment)
//                        addToBackStack(null)
//                        commit()
//                    }
//                    setToolbarTitle(mAddNewItemFragment)
                    true
                }

                R.id.prices -> {
                    setToolbarTitle(mApiPricesFragment)

//                    supportFragmentManager.beginTransaction().apply {
//                        replace(R.id.fragment_container, mApiPricesFragment)
//                        addToBackStack(null)
//                        commit()
//                    }
//                    setToolbarTitle(mApiPricesFragment)
                    true
                }
                else -> false
            }
        }
    } // bottomNavigationItemSelectedListener


    private fun handleDarkModeSwitch() {
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
            mPortfolioFragment -> mToolbar.title = getString(R.string.menu_portfolio)
            mSettingsFragment -> mToolbar.title = getString(R.string.menu_settings)
            mAddNewItemFragment -> mToolbar.title = getString(R.string.menu_add_new_item)
            mGalleryFragment -> mToolbar.title = getString(R.string.menu_gallery)
            mApiPricesFragment -> mToolbar.title = getString(R.string.menu_api_prices)
            else -> mToolbar.title = getString(R.string.app_name)
        }
    }


    private fun closeDrawerLayout() {
        mDrawerLayout.closeDrawer(GravityCompat.START)
    }



}