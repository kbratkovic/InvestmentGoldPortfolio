package com.kbratkovic.investmentgoldportfolio.ui


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.kbratkovic.investmentgoldportfolio.BuildConfig
import com.kbratkovic.investmentgoldportfolio.R
import com.kbratkovic.investmentgoldportfolio.ViewModelProviderFactory
import com.kbratkovic.investmentgoldportfolio.database.AppDatabase
import com.kbratkovic.investmentgoldportfolio.repository.Repository
import com.kbratkovic.investmentgoldportfolio.ui.fragments.*
import com.kbratkovic.investmentgoldportfolio.util.Constants
import timber.log.Timber
import timber.log.Timber.*


class MainActivity : AppCompatActivity() {

    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mBottomNavigation: BottomNavigationView
    private lateinit var mNavigationView: NavigationView

    private lateinit var mToolbar: Toolbar

    private var mPortfolioFragment = PortfolioFragment()
    private val mSettingsFragment = SettingsFragment()
    private var mAddNewItemFragment = AddNewItemFragment()
    private var mMarketPricesFragment = MarketPricesFragment()

    private lateinit var mMainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeTimberLogging()
        initializeViewModel()
        initializeViews()
//        setDrawerNavigationToggle()
//        drawerNavigationItemSelectedListener()
        setBottomNavigation()
        setSharedPreferences()
    } // onCreate End


    private fun initializeTimberLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }


    private fun setSharedPreferences() {
        val mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = mSharedPreferences.edit()
        editor.putString("currency", Constants.CURRENCY_EUR_CODE)
        editor.putString("weight", Constants.WEIGHT_GRAM_CODE)
        editor.apply()
    }


    private fun setBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        setupWithNavController(mBottomNavigation, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
//            mBottomNavigation.menu.setGroupCheckable(0, true, true)
            if(destination.id == R.id.portfolio) {
                setToolbarTitle(mPortfolioFragment)
            }
            if(destination.id == R.id.add_new_item) {
                setToolbarTitle(mAddNewItemFragment)
            }
            if(destination.id == R.id.prices) {
                setToolbarTitle(mMarketPricesFragment)
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
                    mBottomNavigation.menu.setGroupCheckable(0, false, false)
                    closeDrawerLayout()
                    true
                }
                else -> false
            }
        }
    } // drawerNavigationItemSelectedListener


    // region settings
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id  = item.itemId
        if (id == R.id.menu_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    // endregion


    private fun setToolbarTitle(fragment: Fragment) {
        when(fragment) {
            mPortfolioFragment -> mToolbar.title = getString(R.string.menu_portfolio)
            mAddNewItemFragment -> mToolbar.title = getString(R.string.menu_add_new_item)
            mMarketPricesFragment -> mToolbar.title = getString(R.string.menu_api_prices)
            mSettingsFragment -> mToolbar.title = getString(R.string.menu_settings)
            else -> mToolbar.title = getString(R.string.app_name)
        }
    }


    private fun closeDrawerLayout() {
        mDrawerLayout.closeDrawer(GravityCompat.START)
    }



}